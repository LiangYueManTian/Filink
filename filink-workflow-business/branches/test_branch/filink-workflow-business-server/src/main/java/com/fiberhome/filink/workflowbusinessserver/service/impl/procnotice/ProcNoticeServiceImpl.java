package com.fiberhome.filink.workflowbusinessserver.service.impl.procnotice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fiberhome.filink.bean.AliYunPushConstant;
import com.fiberhome.filink.bean.AliYunPushMsgBean;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.AliAccessKey;
import com.fiberhome.filink.smsapi.bean.AliyunPush;
import com.fiberhome.filink.smsapi.bean.Android;
import com.fiberhome.filink.smsapi.bean.Ios;
import com.fiberhome.filink.smsapi.send.aliyun.AliyunMobilePush;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.NoticeProcInfo;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.NoticeDownloadUserReq;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcBaseService;
import com.fiberhome.filink.workflowbusinessserver.service.procnotice.ProcNoticeService;
import com.fiberhome.filink.workflowbusinessserver.utils.common.CastListUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * 工单推送
 * @author hedongwei@wistronits.com
 * @date 2019/4/20 21:23
 */
@Slf4j
@Service
public class ProcNoticeServiceImpl implements ProcNoticeService {

    @Autowired
    private ProcBaseService procBaseService;

    @Autowired
    private UserFeign userFeign;

    @Resource
    private AliyunMobilePush aliyunMobilePush;

    @Resource
    private ParameterFeign parameterFeign;

    /**
     * 每次发送消息个数
     */
    @Value("${pushSendNumber}")
    private Integer pushSendNumber;

    /**
     * 系统语言环境
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;


    /**
     * 获取需要推送的用户信息
     *
     * @param noticeDownloadUserReq 提醒下载用户参数
     * @return 推送的用户信息
     * @author hedongwei@wistronits.com
     * @date 2019/4/20 18:48
     */
    public List<User> getNoticeUserList(NoticeDownloadUserReq noticeDownloadUserReq) {
        List<User> userList = new ArrayList<>();
        if (ObjectUtils.isEmpty(noticeDownloadUserReq.getAssign())) {
            //根据工单编号找到能够找到需要推送的app用户信息
            userList = procBaseService.getTurnUserProcess(noticeDownloadUserReq.getProcId());
        } else {
            List<String> userIds = new ArrayList<>();
            userIds.add(noticeDownloadUserReq.getAssign());
            Object userInfoObject = userFeign.queryUserByIdList(userIds);
            if (!ObjectUtils.isEmpty(userInfoObject)) {
                userList = JSONArray.parseArray(JSONArray.toJSONString(userInfoObject), User.class);
            }
        }
        return userList;
    }

    /**
     * 获取默认阿里云手机推送参数
     * @author hedongwei@wistronits.com
     * @date  2019/5/13 20:34
     * @return 阿里云手机推送参数
     */
    public AliyunPush getDefaultAliYunMobilePush() {
        AliyunPush aliyunPush = new AliyunPush();
        //获取accessKey 和 accessKeySecret
        AliAccessKey aliAccessKey = parameterFeign.queryMobilePush();
        if (ObjectUtils.isEmpty(aliAccessKey)) {
            return null;
        }
        aliyunPush.setAccessKeyId(aliAccessKey.getAccessKeyId());
        aliyunPush.setAccessKeySecret(aliAccessKey.getAccessKeySecret());
        //根据设施类型推送
        aliyunPush.setTarget("DEVICE");

        //设置安卓参数
        Android android = this.getAndroidParam();
        aliyunPush.setAndroid(android);

        //设置ios参数
        Ios ios = this.getIosParam();
        aliyunPush.setIos(ios);
        return aliyunPush;
    }


    /**
     * 获取阿里云推送参数
     * @author hedongwei@wistronits.com
     * @date  2019/4/20 18:00
     * @param ids 推送的用户编号
     * @param title 标题
     * @param body 数据信息
     * @return 获取阿里云推送参数
     */
    public AliyunPush getAliYunPushParam(String ids, String title, String body, Long appKey) {

        AliyunPush aliyunPush = getDefaultAliYunMobilePush();

        if (null == aliyunPush) {
            return null;
        }

        //appKey
        aliyunPush.setAppKey(appKey);
        //标题
        aliyunPush.setTitle(title);
        //推送的消息信息
        aliyunPush.setBody(body);
        //推送的编号
        aliyunPush.setIds(ids);

        return aliyunPush;
    }

    /**
     * 发送消息
     *
     * @author hedongwei@wistronits.com
     * @date 2019/4/25 11:10
     */
    @Override
    public void noticeInfo() {
        ProcBase procBase = new ProcBase();
        procBase.setProcId("0t5PX4WBRrvyUvQ0rQX");
        procBase.setTitle("XJ90764190514A");
        procBase.setProcType("inspection");
        NoticeProcInfo noticeProcInfo = NoticeProcInfo.castProcBaseToNoticeProcInfo(procBase);
        AliYunPushMsgBean aliYunPushMsgBean = this.generateAliYunPushMsgBean(noticeProcInfo);


        //安卓是 MESSAGE
        String ids = "";
        AliyunPush aliyunPush = this.getAliYunPushParam(ids.toString(), systemLanguageUtil.getI18nString(ProcBaseI18n.PROC_DOWNLOAD_NOTICE_TITLE), JSON.toJSONString(aliYunPushMsgBean), 25926001L);
        aliyunPush.setPushType("MESSAGE");
        aliyunPush.setTarget("DEVICE");
        Android android = new Android();
        aliyunPush.setAndroid(android);

        Ios ios = new Ios();
        aliyunPush.setIos(ios);


        try {
            aliyunMobilePush.sendSmsAndEmail(aliyunPush);
        } catch (Exception e) {
            log.error("下载工单消息提醒失败,appkey : 25926001" , e);
        }
    }

    /**
     * 生成阿里云推送消息数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/14 10:06
     * @param data 数据
     * @return 生成阿里云推送消息数据
     */
    public AliYunPushMsgBean generateAliYunPushMsgBean(Object data) {
        AliYunPushMsgBean aliYunPushMsgBean = new AliYunPushMsgBean();
        aliYunPushMsgBean.setData(data);
        aliYunPushMsgBean.setType(AliYunPushConstant.WORK_ORDER_TYPE);
        return aliYunPushMsgBean;
    }

    /**
     * 获取消息推送安卓的参数
     *
     * @author hedongwei@wistronits.com
     * @date 2019/4/25 13:01
     */
    public Android getAndroidParam() {
        Android android = new Android();
        return android;
    }

    /**
     * 获取消息推送ios的参数
     *
     * @author hedongwei@wistronits.com
     * @date 2019/4/25 13:01
     */
    public Ios getIosParam() {
        Ios ios = new Ios();
        ios.setSubtitle("");
        return ios;
    }

    /**
     * 工单编号
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 9:37
     * @param noticeDownloadUserReq 提醒下载人员参数
     * @return 通知下载人员
     */
    @Override
    public Result noticeDownloadUser(NoticeDownloadUserReq noticeDownloadUserReq) {
        log.debug("进入到下载人员");
        String procId = noticeDownloadUserReq.getProcId();
        Result result = procBaseService.checkProcId(procId);
        if (null != result) {
            return result;
        }

        //获取提醒用户集合
        List<User> userList = this.getNoticeUserList(noticeDownloadUserReq);

        //获取用户信息
        List<String> userIdList = CastListUtil.getNoticeUserId(userList);
        userList = this.getNoticeUserList(userIdList);

        //获取当前工单信息
        ProcBase procBase = procBaseService.getProcBaseByProcId(procId);

        //需要发送给每个用户的参数
        NoticeProcInfo noticeProcInfo = NoticeProcInfo.castProcBaseToNoticeProcInfo(procBase);
        if (ObjectUtils.isEmpty(noticeProcInfo)) {
            return null;
        }
        AliYunPushMsgBean aliYunPushMsgBean = this.generateAliYunPushMsgBean(noticeProcInfo);

        System.out.println("需要推送的用户个数为>>>>>>>>>>>>>>>" + userList.size());
        if (!ObjectUtils.isEmpty(userList)) {

            //appKey map 用以appKey为键，以设施编号集合为值
            Map<String, List<List<String>>> appKeyMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);

            //推送类型map
            Map<String, String> pushTypeMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);

            //当前组装的每个appKey的数据
            Map<String, List<String>> appDeviceMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
            for (int i = 0 ; i < userList.size(); i++) {
                int count = i + 1;
                //此处获取appKey值
                if (!ObjectUtils.isEmpty(userList.get(i))) {
                    if (!ObjectUtils.isEmpty(userList.get(i).getPushId()) && !ObjectUtils.isEmpty(userList.get(i).getAppKey())
                            && !ObjectUtils.isEmpty(userList.get(i).getPhoneType())) {
                        String appKey = userList.get(i).getAppKey();
                        //新增需要推送的参数
                        List<String> appDeviceList = this.getAppDeviceList(appDeviceMap, userList.get(i));
                        appDeviceMap.put(appKey, appDeviceList);
                        String pushType = this.getPushTypeByPhoneType(userList.get(i).getPhoneType());
                        pushTypeMap.put(appKey, pushType);

                        //每个appKey需要推送的数据
                        if (appDeviceList.size() == pushSendNumber) {
                            //获取每个appKey需要推送的数据
                            appKeyMap = this.equalSetNoticeNumberProcess(appKeyMap, appKey, appDeviceList);
                            appDeviceMap.put(appKey, null);
                        }
                    }
                }
                //最后一次把所有的appKey没满足指定条数的数据发送
                appKeyMap = this.getAppKeyMap(appDeviceMap, count, userList, appKeyMap);
            }
            //推送信息
            this.sendSmsAndEmailBatch(appKeyMap, aliYunPushMsgBean, pushTypeMap);
        }
        return ResultUtils.success();
    }

    /**
     * 获取推送的类型
     * @author hedongwei@wistronits.com
     * @date  2019/5/14 13:59
     * @param phoneType 手机类型
     * @return 手机类型
     */
    public String getPushTypeByPhoneType(Integer phoneType) {
        //推送的类型
        String pushType = "";
        if (WorkFlowBusinessConstants.PHONE_TYPE_ANDROID.equals(phoneType)) {
            pushType = "MESSAGE";
        } else if (WorkFlowBusinessConstants.PHONE_TYPE_IOS.equals(phoneType)) {
            pushType = "NOTICE";
        }
        return pushType;
    }


    /**
     * 当发送消息的数量等于设置的发送数量时 获取设置的appMap
     * @author hedongwei@wistronits.com
     * @date  2019/5/13 23:01
     * @param appKeyMap appKeyMap
     * @param appKey appKey
     * @param appDeviceList app设施集合
     * @return 获取设置的appMap
     */
    public Map<String, List<List<String>>> equalSetNoticeNumberProcess(Map<String, List<List<String>>> appKeyMap , String appKey, List<String> appDeviceList) {
        List<List<String>> appKeyOneList = new ArrayList<>();
        //获取每个appKey需要推送的数据
        if (!ObjectUtils.isEmpty(appKeyMap) && !ObjectUtils.isEmpty(appKeyMap.get(appKey))) {
            appKeyOneList = appKeyMap.get(appKey);
        }
        //将每次满足发送条件的数据加到每个appKey需要推送的消息中
        appKeyOneList.add(appDeviceList);
        appKeyMap.put(appKey, appKeyOneList);
        return appKeyMap;
    }

    /**
     * 获取app设施map
     * @author hedongwei@wistronits.com
     * @date  2019/5/13 22:34
     * @param appDeviceMap app设施map
     * @param user 用户信息
     * @return app设施map
     */
    public List<String> getAppDeviceList(Map<String, List<String>> appDeviceMap, User user) {
        String appKey = user.getAppKey();
        //每一个需要提醒的数据信息
        List<String> appDeviceList = new ArrayList<>();
        Set<String> appDeviceSet = new HashSet<>();
        if (!ObjectUtils.isEmpty(appDeviceMap) && !ObjectUtils.isEmpty(appDeviceMap.get(appKey))) {
            appDeviceList = appDeviceMap.get(appKey);
            appDeviceSet.addAll(appDeviceList);
            appDeviceList = new ArrayList<>();

        }
        appDeviceSet.add(user.getPushId());
        appDeviceList.addAll(appDeviceSet);
        return appDeviceList;
    }

    /**
     * 获取appKeyMap
     * @author hedongwei@wistronits.com
     * @date  2019/5/13 22:50
     * @param appDeviceMap app设施map
     * @param count 当前循环次数
     * @param userList 用户集合
     * @param appKeyMap appKeyMap
     * @return 获取appKeyMap
     */
    public Map<String, List<List<String>>> getAppKeyMap(Map<String, List<String>> appDeviceMap, int count, List<User> userList, Map<String, List<List<String>>> appKeyMap) {
        if (count == userList.size()) {
            if (!ObjectUtils.isEmpty(appDeviceMap)) {
                for (String appKeyOne : appDeviceMap.keySet()) {
                    List<List<String>> appKeyOneList = new ArrayList<>();
                    appKeyOneList.add(appDeviceMap.get(appKeyOne));
                    appKeyMap.put(appKeyOne, appKeyOneList);
                }
            }
        }
        return appKeyMap;
    }


    /**
     * 重置aliYunPush
     * @author hedongwei@wistronits.com
     * @date  2019/5/13 20:55
     * @param aliYunPush 重置参数
     */
    public AliyunPush resetAliYunPush(AliyunPush aliYunPush) {
        aliYunPush.setAppKey(null);
        aliYunPush.setIds(null);
        aliYunPush.setPushType(null);
        Ios ios = this.getIosParam();
        aliYunPush.setIos(ios);
        return aliYunPush;
    }


    /**
     * 批量发送推送消息
     * @author hedongwei@wistronits.com
     * @date  2019/5/13 20:28
     * @param appKeyMap 推送参数
     * @param aliYunPushMsgBean  阿里云发送消息
     * @param pushTypeMap 发送类型map
     */
    public void sendSmsAndEmailBatch(Map<String, List<List<String>>> appKeyMap, AliYunPushMsgBean aliYunPushMsgBean, Map<String, String> pushTypeMap) {
        log.debug("发送推送消息>>>>>>>>>>>>>>>>>>>>>>>>>>>> appKeyMap集合的长度是:" + appKeyMap.size());
        log.debug("发送推送消息>>>>>>>>>>>>>>>>>>>>>>>>>>>> pushTypeMap集合的长度是:" + appKeyMap.size());
        //推送消息
        if (!ObjectUtils.isEmpty(appKeyMap)) {
            AliyunPush aliyunPush = getDefaultAliYunMobilePush();
            for (String key : appKeyMap.keySet()) {
                String pushType = "";
                if (!ObjectUtils.isEmpty(pushTypeMap) && !ObjectUtils.isEmpty(pushTypeMap.get(key))) {
                    pushType = pushTypeMap.get(key);
                }
                if (!ObjectUtils.isEmpty(aliyunPush) && !ObjectUtils.isEmpty(pushType)) {
                    log.debug("进入到推送消息>>>>>>>>>>>>>" + key);
                    //推送消息
                    this.sendSmsAndEmail(appKeyMap.get(key), Long.valueOf(key), aliYunPushMsgBean, aliyunPush, pushType);
                }
            }
        }
    }

    /**
     * 获取提醒用户集合
     * @author hedongwei@wistronits.com
     * @date  2019/5/5 22:15
     * @param userIdList 用户编号集合
     * @return 获取提醒用户集合
     */
    public List<User> getNoticeUserList(List<String> userIdList) {
        List<User> userList = new ArrayList<>();
        Object userInfoObject = userFeign.queryOnlineUserByIdList(userIdList);
        log.debug("查询提醒的在线用户集合>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>: " + userIdList.size());
        if (!ObjectUtils.isEmpty(userInfoObject)) {
            userList = JSONArray.parseArray(JSONArray.toJSONString(userInfoObject), User.class);
            log.debug("查询到的在线用户集合的长度为>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>: " + userList.size());
        }
        return userList;
    }


    /**
     * 推送信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/5 22:59
     * @param appNoticeList app提醒设施编号集合
     * @param appNoticeList app设备提醒信息
     * @param appKey app唯一标识
     * @param aliYunPush 阿里云push
     * @param pushType 发送类型
     */
    public void sendSmsAndEmail(List<List<String>> appNoticeList, Long appKey,AliYunPushMsgBean aliYunPushMsgBean, AliyunPush aliYunPush, String pushType) {
        if (!ObjectUtils.isEmpty(appNoticeList) && !ObjectUtils.isEmpty(appKey) && !ObjectUtils.isEmpty(aliYunPushMsgBean)) {
            for (List<String> ids : appNoticeList) {
                String idsString = this.getIdsString(ids);
                if (ObjectUtils.isEmpty(idsString)) {
                    continue;
                }
                aliYunPush.setIds(idsString);
                aliYunPush.setTitle(systemLanguageUtil.getI18nString(ProcBaseI18n.PROC_DOWNLOAD_NOTICE_TITLE));
                if ("NOTICE".equals(pushType)) {
                    //消息提醒为notice时,需要传入ios的内容的特殊处理
                    aliYunPush.setBody(systemLanguageUtil.getI18nString(ProcBaseI18n.PROC_DOWNLOAD_NOTICE_TITLE));
                    Ios ios = aliYunPush.getIos();
                    //ios角标设置为0
                    ios.setBadge(0);
                    Map<String ,Object> iosMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
                    iosMap.put("Extras", aliYunPushMsgBean);
                    ios.setExtParameters(JSON.toJSONString(iosMap));
                    aliYunPush.setIos(ios);
                } else {
                    aliYunPush.setBody(JSON.toJSONString(aliYunPushMsgBean));
                }
                aliYunPush.setAppKey(appKey);
                aliYunPush.setPushType(pushType);
                //需要发送推送消息
                try {
                    aliyunMobilePush.sendSmsAndEmail(aliYunPush);
                } catch (Exception e) {
                    log.error("download proc msg notice error, appKey is " + appKey, e);
                }
                //重置阿里云推送参数
                aliYunPush = this.resetAliYunPush(aliYunPush);
            }
        }
    }

    /**
     * 获取推送的设备id
     * @author hedongwei@wistronits.com
     * @date  2019/5/21 23:31
     * @param ids 设备id集合
     * @return 推送设施id
     */
    public String getIdsString(List<String> ids) {
        String idsString = "";
        if (!ObjectUtils.isEmpty(ids)) {
            for (String idOne : ids) {
                if (!ObjectUtils.isEmpty(idsString)) {
                    idsString += "," ;
                }
                idsString += idOne;
            }
        }
        return idsString;
    }


    /**
     * 测试方法
     * @author hedongwei@wistronits.com
     * @date  2019/5/21 23:36
     * @param args 参数
     */
    public static void main(String [] args) {
        ProcBase procBase = new ProcBase();
        procBase.setProcId("0t5PX4WBRrvyUvQ0rQX");
        procBase.setTitle("XJ90764190514A");
        procBase.setProcType("inspection");
        NoticeProcInfo noticeProcInfo = NoticeProcInfo.castProcBaseToNoticeProcInfo(procBase);
        AliYunPushMsgBean aliYunPushMsgBean = new AliYunPushMsgBean();
        aliYunPushMsgBean.setData(noticeProcInfo);
        aliYunPushMsgBean.setType(AliYunPushConstant.WORK_ORDER_TYPE);

        Map<String ,Object> iosMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        iosMap.put("Extras", aliYunPushMsgBean);
        System.out.println(JSON.toJSONString(iosMap));
    }
}
