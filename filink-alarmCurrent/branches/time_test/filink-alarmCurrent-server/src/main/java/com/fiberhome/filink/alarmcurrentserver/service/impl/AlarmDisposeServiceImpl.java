package com.fiberhome.filink.alarmcurrentserver.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmcurrentserver.bean.*;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrent18n;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmReceiveDao;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmDisposeService;
import com.fiberhome.filink.alarmcurrentserver.utils.HexUtil;
import com.fiberhome.filink.alarmcurrentserver.utils.ImageExtensionEnum;
import com.fiberhome.filink.alarmcurrentserver.utils.ListUtil;
import com.fiberhome.filink.alarmsetapi.api.AlarmSetFeign;
import com.fiberhome.filink.alarmsetapi.bean.AlarmForwardRule;
import com.fiberhome.filink.alarmsetapi.bean.AlarmOrderRule;
import com.fiberhome.filink.deviceapi.api.DevicePicFeign;
import com.fiberhome.filink.deviceapi.bean.BatchUploadPicReq;
import com.fiberhome.filink.deviceapi.bean.PicRelationInfo;
import com.fiberhome.filink.deviceapi.util.DeviceStatus;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.ossapi.bean.ImageUploadBean;
import com.fiberhome.filink.ossapi.bean.ImageUrl;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.AliAccessKey;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.smsapi.api.SendSmsAndEmail;
import com.fiberhome.filink.smsapi.bean.AliyunEmail;
import com.fiberhome.filink.smsapi.bean.AliyunSms;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessapi.api.procclear.ProcClearFeign;
import com.fiberhome.filink.workflowbusinessapi.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessapi.req.procclear.InsertClearFailureReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * <p>
 * 告警处理服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 14:07 2019/2/27 0027
 */
@Service
@Slf4j
public class AlarmDisposeServiceImpl extends ServiceImpl<AlarmReceiveDao, AlarmReceive> implements AlarmDisposeService {

    /**
     * 远程调用设施图片服务
     */
    @Autowired
    private DevicePicFeign devicePicFeign;

    /**
     * 远程调用oss服务
     */
    @Autowired
    private FdfsFeign fdfsFeign;

    /**
     * 图片来源（告警）
     */
    public static final String PIC_RESOURCE_1 = "1";

    /**
     * mongodb实现类
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 用户服务接口s
     */
    @Autowired
    private UserFeign userFeign;

    /**
     * 告警设置服务接口类
     */
    @Autowired
    private AlarmSetFeign alarmSetFeign;

    /**
     * 告警转工单接口类
     */
    @Autowired
    private ProcClearFeign procClearFeign;

    /**
     * 发送短信
     */
    @Autowired
    private SendSmsAndEmail aliyunSendSms;

    /**
     * 发送邮件
     */
    @Autowired
    private SendSmsAndEmail aliyunSendEmail;

    /**
     * 短信发送
     */
    @Autowired
    private ParameterFeign parameterFeign;
    /**
     * 设施主控
     */
    @Autowired
    private ControlFeign controlFeign;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 告警远程通知
     *
     * @param alarmForwardRules 告警远程通知规则
     * @param alarmCurrent      当前告警信息
     */
    @Override
    public void alarmForward(List<AlarmForwardRule> alarmForwardRules, AlarmCurrent alarmCurrent) throws ClientException {
        for (AlarmForwardRule alarmForwardRule : alarmForwardRules) {
            int pushType = alarmForwardRule.getPushType();
            // 获取通知人id
            Set<String> alarmForwardRuleUserSet = alarmForwardRule.getAlarmForwardRuleUserList();
            // 转换为list
            List<String> alarmForwardRuleUserList = new ArrayList<>(alarmForwardRuleUserSet);
            // 调用用户服务的接口，查询用户信息
            List<Object> userList = (List<Object>) userFeign.queryUserByIdList(alarmForwardRuleUserList);
            if (ObjectUtils.isEmpty(userList)) {
                return;
            }
            for (Object object : userList) {
                User user = JSONArray.toJavaObject((JSON) JSONArray.toJSON(object), User.class);
                // 判断发送
                if (pushType == 1) {
                    //发送短信
                    sendSms(user, alarmCurrent);
                } else {
                    //发送邮件
                    sendEmail(user, alarmCurrent);
                }
            }
        }
    }

    /**
     * 告警远程通知发送邮件
     *
     * @param user 用户信息
     * @throws ClientException 客户端异常
     */
    private void sendEmail(User user, AlarmCurrent alarmCurrent) throws ClientException {
        AlarmNotices alarmNotice = new AlarmNotices();
        if (StringUtils.isEmpty(alarmCurrent.getExtraMsg())) {
            alarmNotice.setAlarmdes("");
        } else {
            alarmNotice.setAlarmdes(alarmCurrent.getExtraMsg());
        }
        alarmNotice.setAlarmname(alarmCurrent.getAlarmName());
        alarmNotice.setAlarmtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
                format(new Date(alarmCurrent.getAlarmBeginTime() + 8 * 60 * 60 * 1000)));
        alarmNotice.setDevicename(alarmCurrent.getAlarmObject().toString());
        alarmNotice.setDevicetype(deviceTypeEnum(alarmCurrent.getAlarmSourceTypeId()));
        alarmNotice.setRegion(alarmCurrent.getAreaName());
        //TODO 暂时没有完全确定邮件模板，此处暂时中文硬编码
        String msg = systemLanguageUtil.getI18nString(AlarmCurrent18n.EMAIL_NAME_TITLE) + "<br>"
                + "&nbsp&nbsp&nbsp&nbsp" + systemLanguageUtil.getI18nString(AlarmCurrent18n.EMAIL_NAME_CONTENT)
                + "<br>" + systemLanguageUtil.getI18nString(AlarmCurrent18n.EMAIL_ALARM_NAME) + alarmNotice.getAlarmname() + ""
                + "<br>" + systemLanguageUtil.getI18nString(AlarmCurrent18n.EMAIL_DEVICE_NAME) + alarmNotice.getDevicename()
                + "<br>" + systemLanguageUtil.getI18nString(AlarmCurrent18n.EMAIL_DEVICE_TYPE) + alarmNotice.getDevicetype()
                + "<br>" + systemLanguageUtil.getI18nString(AlarmCurrent18n.EMAIL_NAME_REGION) + alarmNotice.getRegion()
                + "<br>" + systemLanguageUtil.getI18nString(AlarmCurrent18n.EMAIL_ALARM_TIME) + alarmNotice.getAlarmtime()
                + "<br>" + systemLanguageUtil.getI18nString(AlarmCurrent18n.EMAIL_ALARM_DES) + alarmNotice.getAlarmdes()
                + "<br>" + systemLanguageUtil.getI18nString(AlarmCurrent18n.EMAIL_NAME_TIPS)
                + "<br><br><br><br><br>" + systemLanguageUtil.getI18nString(AlarmCurrent18n.EMAIL_NAME_THINK);
        AliyunEmail aliyunEmail = new AliyunEmail();
        aliyunEmail.setAccountName("filink@fi-link.net");
        aliyunEmail.setFromAlias(systemLanguageUtil.getI18nString(AlarmCurrent18n.INFRASTRUCTURE));
        aliyunEmail.setHtmlBody(msg);
        aliyunEmail.setSubject(systemLanguageUtil.getI18nString(AlarmCurrent18n.DEVICE_TI));
        //可以同时发给多个人，用逗号隔开
        aliyunEmail.setToAddress(user.getEmail());
        aliyunEmail.setTagName("test");
        //区域信息，如果是除杭州region外的其它region（如新加坡、澳洲Region），
        // 需要将下面的"cn-hangzhou"替换为"ap-southeast-1"、或"ap-southeast-2"。
        if (!AppConstant.AREA.equals(alarmCurrent.getAreaName())) {
            aliyunEmail.setArea("ap-southeast-1");
        }
        // 邮件服务
        AliAccessKey aliAccessKey = parameterFeign.queryMail();
        if (aliAccessKey == null) {
            return;
        }
        aliyunEmail.setAccessKeyId(aliAccessKey.getAccessKeyId());
        aliyunEmail.setAccessKeySecret(aliAccessKey.getAccessKeySecret());
        aliyunSendEmail.sendSmsAndEmail(aliyunEmail);
    }

    /**
     * 告警图片处理逻辑
     *
     * @param alarmPictureMsg 告警图片信息
     */
    @Override
    public void imageDispose(AlarmPictureMsg alarmPictureMsg) {
        //根据告警编码，设施ID，未清除，门编号在当前告警表或者告警过滤表中找出对应的告警ID，撬门，撬锁告警才会拍照，一定包含门信息
        BatchUploadPicReq batchUploadPicReq = new BatchUploadPicReq();
        Query query = new Query(Criteria.where(AppConstant.ALARM_SOURCE).is(alarmPictureMsg.getDeviceId())
                .and(AppConstant.ALARM_CODE).is(alarmPictureMsg.getAlarmCode())
                .and(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                .and(AppConstant.DOOR_NUMBER).is(alarmPictureMsg.getDoorNumber()));
        //当前告警表
        List<AlarmCurrent> alarmCurrents = mongoTemplate.find(query, AlarmCurrent.class);
        if (ListUtil.isListNotExpty(alarmCurrents)) {
            AlarmCurrent alarmCurrent = alarmCurrents.get(0);
            batchUploadPicReq.setDeviceId(alarmCurrent.getAlarmSource());
            batchUploadPicReq.setResourceId(alarmCurrent.getId());
            batchUploadPicReq.setAlarmName(alarmCurrent.getAlarmName());
        }
        //告警过滤表
        List<AlarmFilter> alarmFilterList = mongoTemplate.find(query, AlarmFilter.class);
        if (ListUtil.isListNotExpty(alarmFilterList)) {
            AlarmFilter alarmFilter = alarmFilterList.get(0);
            batchUploadPicReq.setDeviceId(alarmFilter.getAlarmSource());
            batchUploadPicReq.setResourceId(alarmFilter.getId());
            batchUploadPicReq.setAlarmName(alarmFilter.getAlarmName());
        }
        batchUploadPicReq.setResource(AppConstant.ONE);
        batchUploadPicReq.setType(alarmPictureMsg.getSuffix());
        batchUploadPicReq.setCreateUser("System");
        batchUploadPicReq.setHexString(alarmPictureMsg.getPictureInfo());
        String time = alarmPictureMsg.getTime();
        //告警ID 和 图片信息传给设施服务
        List<BatchUploadPicReq> picList = new ArrayList<>();
        picList.add(batchUploadPicReq);
        try {
            this.uploadImagesForAlarm(picList, time);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 自动清除告警
     *
     * @param alarmCurrentInfo 告警信息
     */
    @Override
    public void autoCleanAlarm(AlarmCurrentInfo alarmCurrentInfo) {
        // 判断是否存在要看有没有门,没有门信息的是工单超时告警，不用清除。一条告警信息只对应一个设施（工单超时除外）
        OrderDeviceInfo orderDeviceInfo = alarmCurrentInfo.getOrderDeviceInfoList().get(0);
        if (ObjectUtils.isEmpty(orderDeviceInfo)) {
            log.warn("无该设施：{}");
            return;
        }
        List<DoorInfo> doorInfoList = orderDeviceInfo.getDoorInfoList();
        Query query = null;
        if (ListUtil.isEmpty(doorInfoList)) {
            //设施告警，没有门，根据设施ID，告警编码判断当前告警是否重复
            query = new Query(Criteria.where(AppConstant.ALARM_SOURCE).is(orderDeviceInfo.getAlarmSource())
                    .and(AppConstant.ALARM_CODE).is(alarmCurrentInfo.getAlarmCode())
                    .and(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE));
            cleanAlarm(query, alarmCurrentInfo, orderDeviceInfo);
        } else {
            //遍历所有有告警的门，只管有告警的信息
            for (DoorInfo doorInfo : doorInfoList) {
                String alarmCode = alarmCurrentInfo.getAlarmCode();
                String doorStatus = doorInfo.getDoorStatus();
                // 获取第一个值
                String doorStatusOne = doorStatus.substring(0, doorStatus.length() - 1);
                // 获取第二个值
                String doorStatusTwo = doorStatus.substring(1, doorStatus.length());
                // 门的信息 为1过滤
                if (alarmCode.equals(AppConstant.NOTCLOSED) || alarmCode.equals(AppConstant.VIOLENCECLOSE)) {
                    if (doorStatusOne.equals(AppConstant.ONE)) {
                        continue;
                    }
                }
                // 锁的信息 为1过滤
                if (alarmCode.equals(AppConstant.UNLOCK)) {
                    if (doorStatusTwo.equals(AppConstant.ONE)) {
                        continue;
                    }
                }
                if (!AppConstant.LINGS.equals(doorInfo.getDoorStatus())) {
                    //设施告警，根据设施ID，告警编码，门编号判断当前告警是否重复
                    query = new Query(Criteria.where(AppConstant.ALARM_SOURCE).is(orderDeviceInfo.getAlarmSource())
                            .and(AppConstant.ALARM_CODE).is(alarmCurrentInfo.getAlarmCode())
                            .and(AppConstant.DOOR_NUMBER).is(doorInfo.getDoorNumber())
                            .and(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE));
                    cleanAlarm(query, alarmCurrentInfo, orderDeviceInfo);
                }
            }
        }
    }

    /**
     * 告警转工单
     *
     * @param alarmOrderRule 告警转工单规则
     * @param alarmCurrent   当前告警信息
     */
    @Override
    public void alarmCastOrder(AlarmOrderRule alarmOrderRule, AlarmCurrent alarmCurrent) {
        int orderType = alarmOrderRule.getOrderType();
        if (orderType == LogFunctionCodeConstant.ALARM_STATUS_TWO) {
            //消障工单，目前的需求只会产生消障工单
            InsertClearFailureReq insertClearFailureReq = new InsertClearFailureReq();
            //添加责任单位，多个
            List<ProcRelatedDepartment> procList = new ArrayList<>();
            ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
            procRelatedDepartment.setAccountabilityDept(alarmOrderRule.getDepartmentId());
            procList.add(procRelatedDepartment);
            insertClearFailureReq.setAccountabilityDeptList(procList);
            //添加其他
            insertClearFailureReq.setDeviceAreaId(alarmCurrent.getAreaId());
            insertClearFailureReq.setDeviceAreaName(alarmCurrent.getAreaName());
            insertClearFailureReq.setDeviceId(alarmCurrent.getAlarmSource());
            insertClearFailureReq.setDeviceName(alarmCurrent.getAlarmObject());
            insertClearFailureReq.setDeviceType(alarmCurrent.getAlarmSourceTypeId());
            // 计算期望完成时间的日期
            Long eTime = alarmCurrent.getAlarmBeginTime() + Long.valueOf(alarmOrderRule.getCompletionTime()) * 24 * 60 * 60 * 1000;
            Date date = new Date(eTime);
            insertClearFailureReq.setProcResourceType("3");
            insertClearFailureReq.setEcTime(date);
            insertClearFailureReq.setRefAlarm(alarmCurrent.getId());
            insertClearFailureReq.setRefAlarmCode(alarmCurrent.getAlarmCode());
            insertClearFailureReq.setRefAlarmName(alarmCurrent.getAlarmName());
            insertClearFailureReq.setRemark(alarmCurrent.getRemark());
            String title = createTitle();
            insertClearFailureReq.setTitle(title);
            procClearFeign.addClearFailureProc(insertClearFailureReq);
        }
    }

    /**
     * 告警远程通知发送短信
     *
     * @param user         用户信息
     * @param alarmCurrent 当前告警信息
     * @throws ClientException 客户端异常
     */
    private void sendSms(User user, AlarmCurrent alarmCurrent) throws ClientException {
        AlarmNotices alarmNotice = new AlarmNotices();

        if (StringUtils.isEmpty(alarmCurrent.getExtraMsg())) {
            alarmNotice.setAlarmdes("");
        } else {
            alarmNotice.setAlarmdes(alarmCurrent.getExtraMsg());
        }
        alarmNotice.setAlarmname(alarmCurrent.getAlarmName());
        alarmNotice.setAlarmtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
                format(new Date(alarmCurrent.getAlarmBeginTime() + 8 * 60 * 60 * 1000)));
        alarmNotice.setDevicename(alarmCurrent.getAlarmObject().toString());
        alarmNotice.setDevicetype(deviceTypeEnum(alarmCurrent.getAlarmSourceTypeId()));
        alarmNotice.setRegion(alarmCurrent.getAreaName());

        AliyunSms aliyunSms = new AliyunSms();
        aliyunSms.setTemplateParam(JSONArray.toJSON(alarmNotice).toString());
        aliyunSms.setPhone(user.getPhoneNumber());
        aliyunSms.setSignName(AppConstant.ALARM_SMS_NAME);
        aliyunSms.setTemplateCode(AppConstant.ALARM_SMS_BT);
        AliAccessKey aliAccessKey = parameterFeign.queryMessage();
        if (aliAccessKey == null) {
            return;
        }
        aliyunSms.setAccessKeyId(aliAccessKey.getAccessKeyId());
        aliyunSms.setAccessKeySecret(aliAccessKey.getAccessKeySecret());
        // 区域信息，如果是除杭州region外的其它region（如新加坡、澳洲Region），
        // 需要将下面的"cn-hangzhou"替换为"ap-southeast-1"、或"ap-southeast-2"。
        if (!AppConstant.AREA.equals(alarmCurrent.getAreaName())) {
            aliyunSms.setArea("ap-southeast-1");
        }
        //发送短信
        aliyunSendSms.sendSmsAndEmail(aliyunSms);
    }

    /**
     * 告警清除
     *
     * @param query            查询条件
     * @param alarmCurrentInfo 告警信息
     * @param orderDeviceInfo  设施信息
     */
    private void cleanAlarm(Query query, AlarmCurrentInfo alarmCurrentInfo, OrderDeviceInfo orderDeviceInfo) {
        // 获取当前时间
        long currentTime = System.currentTimeMillis();
        List<AlarmCurrent> alarmCurrents = mongoTemplate.find(query, AlarmCurrent.class);
        //一个设施ID只可能对应一条告警信息，存在才需要清除
        for (AlarmCurrent alarmCurrent : alarmCurrents) {
            Long alarmSystemNearTime = alarmCurrent.getAlarmSystemNearTime();
            //告警清除时间必须晚于当前告警表的最近发生时间
            if (null != alarmSystemNearTime && alarmSystemNearTime < alarmCurrentInfo.getAlarmBeginTime()) {
                // 修改清除状态
                Update update = new Update().set(AppConstant.ALARM_CLEAN_STATUS, 2)
                        .set(AppConstant.ALARM_CLEAN_ID, AppConstant.SYSTEM).set(AppConstant.ALARM_CLEAN_NAME, AppConstant.SYSTEM)
                        .set(AppConstant.ALARM_CLEAN_TIME, currentTime);
                mongoTemplate.updateFirst(query, update, AlarmCurrent.class);
                // 当前告警转历史告警
                Query queryTwo = new Query(Criteria.where(AppConstant.ALARM_ID).is(alarmCurrent.getId()));
                AlarmCurrent alarmCurrentOne = mongoTemplate.findOne(queryTwo, AlarmCurrent.class);
                Integer setTime = alarmSetFeign.queryAlarmHistorySetFeign();
                long time = setTime * 60 * 60;
                RedisUtils.set(AppConstant.ALARM_MONGODB_PRE + alarmCurrentOne.getId(), alarmCurrentOne.getId(), time);
                // 等级主控id查询告警是否存在
                Query query1 = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(3)
                        .and(AppConstant.CONTROLID).is(alarmCurrentInfo.getEquipmentId()));
                AlarmCurrent alarmCurrentList = mongoTemplate.findOne(query1, AlarmCurrent.class);
                // 不存在更新主控id状态
                if (ObjectUtils.isEmpty(alarmCurrentList)) {
                    selectEquipmentId(alarmCurrentInfo.getEquipmentId());
                } else {
                    updateControl(alarmCurrentInfo);
                }
                log.info("清除告警code：{}", alarmCurrentInfo.getAlarmCode());
            }
        }
    }

    /**
     * 通讯中断清除更改主控状态
     * @param alarmCurrentInfo 主控状态
     */
    private void updateControl(AlarmCurrentInfo alarmCurrentInfo) {
        if (alarmCurrentInfo.getAlarmCode().equals(AppConstant.COMMUNICATION_INTERRUPT)) {
            ControlParam controlParam = new ControlParam();
            //更新设施状态
            controlParam.setHostId(alarmCurrentInfo.getEquipmentId());
            controlParam.setDeviceStatus(DeviceStatus.Alarm.getCode());
            controlFeign.updateDeviceStatusById(controlParam);
        }
    }


    /**
     * 修改主控状态
     *
     * @param equipmentId 主控id
     */
    public void selectEquipmentId(String equipmentId) {
        ControlParam controlParam = controlFeign.getControlParamById(equipmentId);
        String deviceStatus = controlParam.getDeviceStatus();
        if (DeviceStatus.Alarm.getCode().equals(deviceStatus)) {
            //更新设施状态
            controlParam.setDeviceStatus(DeviceStatus.Normal.getCode());
            controlFeign.updateDeviceStatusById(controlParam);
        }
    }

    /**
     * 按规则生成工单名称
     *
     * @return title
     */
    private String createTitle() {
        // XZ + 5位随机数 + 190201 + A   生成Title
        int number = new Random().nextInt(100000);
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = sdf.format(d);
        // 生成工单名称
        return "XZ" + number + dateNowStr.substring(2, dateNowStr.length()) + "A";
    }

    /**
     * 批量上传告警图片列表
     *
     * @param batchUploadPicReqs
     * @return Result
     */
    public Boolean uploadImagesForAlarm(List<BatchUploadPicReq> batchUploadPicReqs, String time) throws Exception {
        return this.uploadImages(batchUploadPicReqs, time);
    }

    /**
     * 批量上传图片列表
     *
     * @param batchUploadPicReqs
     * @return Boolean
     */
    public Boolean uploadImages(List<BatchUploadPicReq> batchUploadPicReqs, String time) throws Exception {
        //必填参数校验
        if (this.checkProcParamsForBatchUpload(batchUploadPicReqs)) {
            return false;
        }
        //上传图片文件信息
        Map<String, ImageUploadBean> picMap = new HashMap<>(64);
        //返回图片url
        Map<String, ImageUrl> picsUrlMap = new HashMap<>(64);
        //保存图片数据信息
        List<PicRelationInfo> picRelationInfos = new ArrayList<>();
        //解析图片信息
        if (!this.getFileInfo(batchUploadPicReqs, picMap, picRelationInfos)) {
            return false;
        }
        //批量上传图片
        if (!this.uploadFileImage(picMap, picsUrlMap, picRelationInfos)) {
            return false;
        }

        for (PicRelationInfo picRelationInfo : picRelationInfos) {
            picRelationInfo.setCreateTime(new Date(Long.parseLong(time)));
        }

        //远程调用图片服务批量保存图片记录
        devicePicFeign.saveImagesDataForFeign(picRelationInfos);

        return true;
    }

    /*---------------------------------------解析图片start------------------------------------------*/

    /**
     * 获取文件信息方法
     *
     * @param batchUploadPicReqs 批量上传图片请求
     * @param picMap             上传图片信息
     * @param picRelationInfos   保存图片信息
     * @return Boolean
     */
    private Boolean getFileInfo(List<BatchUploadPicReq> batchUploadPicReqs, Map<String, ImageUploadBean> picMap,
                                List<PicRelationInfo> picRelationInfos) {
        //获取图片信息
        for (int i = 0; i < batchUploadPicReqs.size(); i++) {
            //校验图片类型
            if (!ImageExtensionEnum.containExtension(batchUploadPicReqs.get(i).getType())) {
                return false;
            }
            PicRelationInfo picRelationInfo = new PicRelationInfo();
            ImageUploadBean imageUploadBean = new ImageUploadBean();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String type = batchUploadPicReqs.get(i).getType();
            byte[] bytes = HexUtil.hexStringToBytes(batchUploadPicReqs.get(i).getHexString());
            int size = HexUtil.getLenFromByte(bytes);
            BeanUtils.copyProperties(batchUploadPicReqs.get(i), picRelationInfo);

            picRelationInfo.setPicSize(Long.toString(size));
            picRelationInfo.setPicName(uuid);
            picRelationInfo.setType("." + type);
            picRelationInfo.setDevicePicId(uuid);
            picRelationInfos.add(picRelationInfo);

            imageUploadBean.setFileExtension(type);
            imageUploadBean.setFileHexData(batchUploadPicReqs.get(i).getHexString());

            picMap.put(uuid, imageUploadBean);
        }
        return true;
    }
    /*---------------------------------------解析图片end------------------------------------------*/

    /**
     * 批量上传图片文件
     *
     * @param picMap           上传图片文件
     * @param picsUrlMap       返回图片url
     * @param picRelationInfos 保存图片记录
     * @return Boolean
     */
    protected Boolean uploadFileImage(Map<String, ImageUploadBean> picMap, Map<String, ImageUrl> picsUrlMap,
                                      List<PicRelationInfo> picRelationInfos) {
        picsUrlMap = fdfsFeign.uploadFileImage(picMap);
        if (ObjectUtils.isEmpty(picsUrlMap)) {
            return false;
        }

        //获取图片url
        for (int i = 0; i < picRelationInfos.size(); i++) {
            picRelationInfos.get(i).setPicUrlBase(picsUrlMap.get(picRelationInfos.get(i).getDevicePicId()).getOriginalUrl());
            picRelationInfos.get(i).setPicUrlThumbnail(picsUrlMap.get(picRelationInfos.get(i).getDevicePicId()).getThumbUrl());
        }
        return true;
    }

    /**
     * 校验图片基本参数
     *
     * @param batchUploadPicReqs 批量上传请求
     * @return Boolean
     */
    private Boolean checkProcParamsForBatchUpload(List<BatchUploadPicReq> batchUploadPicReqs) {
        //必填校验
        if (!ObjectUtils.isEmpty(batchUploadPicReqs)) {
            for (BatchUploadPicReq batchUploadPicReq : batchUploadPicReqs) {
                //关联设施不能为空
                if (StringUtils.isEmpty(batchUploadPicReq.getDeviceId())) {
                    return true;
                }
                //图片来源不能为空
                if (StringUtils.isEmpty(batchUploadPicReq.getResource())) {
                    return true;
                }
                //图片不能为空
                if (StringUtils.isEmpty(batchUploadPicReq.getHexString())) {
                    return true;
                }
                //关联告警名称不能为空
                if (PIC_RESOURCE_1.equals(batchUploadPicReq.getResource())) {
                    if (StringUtils.isEmpty(batchUploadPicReq.getAlarmName())) {
                        return true;
                    }
                }
                //当feign调用时创建人不能为空
                if (StringUtils.isEmpty(batchUploadPicReq.getCreateUser())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 设施类型
     *
     * @param code 条件信息
     * @return 设施类型信息
     */
    public String deviceTypeEnum(String code) {
        String name = "";
        switch (code) {
            case "001":
                name = systemLanguageUtil.getI18nString(AppConstant.OPTICAL_BOX);
                break;
            case "030":
                name = systemLanguageUtil.getI18nString(AppConstant.WELL);
                break;
            case "060":
                name = systemLanguageUtil.getI18nString(AppConstant.DISTRIBUTION_FRAME);
                break;
            case "090":
                name = systemLanguageUtil.getI18nString(AppConstant.JUNCTION_BOX);
                break;
            case "150":
                name = systemLanguageUtil.getI18nString(AppConstant.SPLITTING_BOX);
                break;
            default:
        }
        return name;
    }

}
