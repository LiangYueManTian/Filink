package com.fiberhome.filink.alarmcurrentserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fiberhome.filink.alarmcurrentserver.bean.*;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmsetapi.api.AlarmSetFeign;
import com.fiberhome.filink.alarmsetapi.bean.AlarmForwardRule;
import com.fiberhome.filink.alarmsetapi.bean.AlarmOrderRule;
import com.fiberhome.filink.deviceapi.api.DevicePicFeign;
import com.fiberhome.filink.deviceapi.bean.BatchUploadPicReq;
import com.fiberhome.filink.deviceapi.bean.PicRelationInfo;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.ossapi.bean.ImageUploadBean;
import com.fiberhome.filink.ossapi.bean.ImageUrl;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.smsapi.api.SendSmsAndEmail;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessapi.api.procclear.ProcClearFeign;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

@RunWith(JMockit.class)
public class AlarmDisposeServiceImplTest {

    @Tested
    private AlarmDisposeServiceImpl alarmDisposeService;

    /**
     * 远程调用设施图片服务
     */
    @Injectable
    private DevicePicFeign devicePicFeign;

    /**
     * 远程调用oss服务
     */
    @Injectable
    private FdfsFeign fdfsFeign;

    /**
     * 图片来源（告警）
     */
    public static final String PIC_RESOURCE_1 = "1";

    /**
     * mongodb实现类
     */
    @Injectable
    private MongoTemplate mongoTemplate;

    /**
     * 用户服务接口s
     */
    @Injectable
    private UserFeign userFeign;

    /**
     * 告警设置服务接口类
     */
    @Injectable
    private AlarmSetFeign alarmSetFeign;

    /**
     * 告警转工单接口类
     */
    @Injectable
    private ProcClearFeign procClearFeign;

    /**
     * 发送短信
     */
    @Injectable
    private SendSmsAndEmail aliyunSendSms;

    /**
     * 发送邮件
     */
    @Injectable
    private SendSmsAndEmail aliyunSendEmail;

    /**
     * 短信发送
     */
    @Injectable
    private ParameterFeign parameterFeign;
    /**
     * 设施主控
     */
    @Injectable
    private ControlFeign controlFeign;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    @Test
    public void alarmForward() throws Exception {
        List<AlarmForwardRule> alarmForwardRules = new ArrayList<>();
        AlarmForwardRule alarmForwardRule = new AlarmForwardRule();
        alarmForwardRule.setId("1");
        alarmForwardRule.setPushType(1);
        Set<String> strings = new HashSet<>();
        strings.add("1");
        alarmForwardRule.setAlarmForwardRuleUserList(strings);
        alarmForwardRules.add(alarmForwardRule);
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setId("1");
        alarmCurrent.setAlarmBeginTime(0L);
        alarmCurrent.setAlarmObject("1");
        alarmCurrent.setAlarmSourceTypeId("001");
        alarmCurrent.setAreaName("code");
        List<Object> objects = new ArrayList<>();
        Object object = new Object();
        User user = (JSONArray.toJavaObject((JSON) JSONArray.toJSON(object), User.class));
        user.setId("1");
        user.setUserName("admin");
        objects.add(user);

        new Expectations() {
            {
                userFeign.queryUserByIdList((List<String>) any);
                result = objects;
            }
        };
        alarmDisposeService.alarmForward(alarmForwardRules, alarmCurrent);
        List<AlarmForwardRule> alarmForwardRuleList = new ArrayList<>();
        AlarmForwardRule forwardRule = new AlarmForwardRule();
        forwardRule.setId("2");
        forwardRule.setPushType(2);
        Set<String> stringSet = new HashSet<>();
        stringSet.add("2");
        forwardRule.setAlarmForwardRuleUserList(stringSet);
        alarmForwardRuleList.add(forwardRule);
        alarmDisposeService.alarmForward(alarmForwardRuleList, alarmCurrent);
    }

    @Test
    public void imageDispose() throws Exception {
        AlarmPictureMsg alarmPictureMsg = new AlarmPictureMsg();
        alarmPictureMsg.setTime("1");
        alarmPictureMsg.setAlarmCode("code");
        alarmPictureMsg.setDeviceId("1");
        alarmPictureMsg.setDoorNumber("dome");
        alarmPictureMsg.setPictureInfo("dd");
        alarmPictureMsg.setSuffix(".suf");
        List<AlarmCurrent> alarmCurrents = new ArrayList<>();
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setId("1");
        alarmCurrents.add(alarmCurrent);
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = alarmCurrents;
            }
        };
        List<AlarmFilter> alarmFilterList = new ArrayList<>();
        AlarmFilter alarmFilter = new AlarmFilter();
        alarmCurrent.setId("1");
        alarmFilterList.add(alarmFilter);
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmFilter.class);
                result = alarmFilterList;
            }
        };
        alarmDisposeService.imageDispose(alarmPictureMsg);
    }

    @Test
    public void autoCleanAlarm() throws Exception {
        AlarmCurrentInfo alarmCurrentInfo = new AlarmCurrentInfo();
        alarmCurrentInfo.setAlarmBeginTime(10L);
        List<OrderDeviceInfo> orderDeviceInfos = new ArrayList<>();
        OrderDeviceInfo orderDeviceInfo = new OrderDeviceInfo();
        orderDeviceInfo.setAreaId("1");
        orderDeviceInfos.add(orderDeviceInfo);
        alarmCurrentInfo.setOrderDeviceInfoList(orderDeviceInfos);
        List<AlarmCurrent> alarmCurrentList = new ArrayList<>();
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setAlarmSystemNearTime(1L);
        alarmCurrent.setAlarmBeginTime(2L);
        alarmCurrent.setId("1");
        alarmCurrentList.add(alarmCurrent);
        new MockUp<RedisUtils>() {
            @Mock
            void set(String key, Object value, long time) {
            }
        };
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = alarmCurrentList;
            }
        };
        alarmCurrentInfo.setAlarmCode(AppConstant.COMMUNICATION_INTERRUPT);
        alarmDisposeService.autoCleanAlarm(alarmCurrentInfo);
        AlarmCurrentInfo currentInfo = new AlarmCurrentInfo();
        currentInfo.setAlarmBeginTime(10L);
        currentInfo.setAlarmCode("code");
        List<OrderDeviceInfo> list = new ArrayList<>();
        OrderDeviceInfo deviceInfo = new OrderDeviceInfo();
        List<DoorInfo> doorInfoList = new ArrayList<>();
        DoorInfo doorInfo = new DoorInfo();
        doorInfo.setDoorStatus("22");
        doorInfo.setDoorName("1");
        doorInfoList.add(doorInfo);
        deviceInfo.setDoorInfoList(doorInfoList);
        list.add(deviceInfo);
        currentInfo.setOrderDeviceInfoList(list);

        alarmDisposeService.autoCleanAlarm(currentInfo);
    }

    @Test
    public void alarmCastOrder() throws Exception {
        AlarmOrderRule alarmOrderRule = new AlarmOrderRule();
        alarmOrderRule.setOrderType(2);
        alarmOrderRule.setCompletionTime(10);
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setResponsibleDepartmentId("1");
        alarmCurrent.setAreaId("1");
        alarmCurrent.setAreaName("qu");
        alarmCurrent.setAlarmSource("11");
        alarmCurrent.setAlarmObject("1");
        alarmCurrent.setAlarmSourceTypeId("001");
        alarmCurrent.setAlarmBeginTime(0L);
        alarmCurrent.setId("1");
        alarmCurrent.setAlarmCode("code");
        alarmCurrent.setAreaName("code");
        alarmCurrent.setRemark("1");

        alarmDisposeService.alarmCastOrder(alarmOrderRule, alarmCurrent);
    }

    @Test
    public void selectEquipmentId() throws Exception {
        ControlParam controlParam = new ControlParam();
        controlParam.setDeviceStatus("3");
        new Expectations() {
            {
                controlFeign.getControlParamById(anyString);
                result = controlParam;
            }
        };
        alarmDisposeService.selectEquipmentId("1");
    }

    @Test
    public void uploadFileImage() throws Exception {
        Map<String, ImageUploadBean> map = new HashMap<>();
        ImageUploadBean imageUploadBean = new ImageUploadBean();
        imageUploadBean.setFileExtension("1");
        imageUploadBean.setFileHexData("1");
        map.put("1", imageUploadBean);
        Map<String, ImageUrl> map1 = new HashMap<>();
        ImageUrl imageUrl = new ImageUrl();
        imageUrl.setThumbUrl("1");
        imageUrl.setOriginalUrl("1");
        map1.put("1", imageUrl);
        List<PicRelationInfo> picRelationInfos = new ArrayList<>();
        PicRelationInfo picRelationInfo = new PicRelationInfo();
        picRelationInfo.setDevicePicId("1");

        picRelationInfo.setPicUrlBase("1");
        picRelationInfo.setPicUrlThumbnail("1");
        picRelationInfos.add(picRelationInfo);
        new Expectations() {
            {
                fdfsFeign.uploadFileImage((Map<String, ImageUploadBean>) any);
                result = map1;
            }
        };
        alarmDisposeService.uploadFileImage(map, map1, picRelationInfos);
    }

    @Test
    public void uploadImages() throws Exception {
        List<BatchUploadPicReq> batchUploadPicReqs = new ArrayList<>();
        BatchUploadPicReq batchUploadPicReq = new BatchUploadPicReq();
        batchUploadPicReq.setDeviceId("1");
        batchUploadPicReq.setResource("1");
        batchUploadPicReq.setHexString("1");
        batchUploadPicReq.setResource("1");
        batchUploadPicReq.setAlarmName("1");
        batchUploadPicReq.setCreateUser("admin");
        batchUploadPicReq.setType("JPEG");
        batchUploadPicReq.setResourceId("1");
        batchUploadPicReqs.add(batchUploadPicReq);
        String time = "101010";
        Map<String, ImageUploadBean> map = new HashMap<>();
        Map<String, ImageUrl> map1 = new HashMap<>();
        ImageUrl imageUrl = new ImageUrl();
        imageUrl.setThumbUrl("1");
        imageUrl.setOriginalUrl("1");
        map1.put("1", imageUrl);
        new Expectations() {
            {
                fdfsFeign.uploadFileImage((Map<String, ImageUploadBean>) any);
                result = null;
            }
        };
        alarmDisposeService.uploadImages(batchUploadPicReqs, time);
    }

    @Test
    public void deviceTypeEnum() throws Exception {
        String codeInfo = "001";
        String codeInfoOne = "030";
        String codeInfoTwo = "060";
        String codeInfoThree = "090";
        String codeInfoFilter = "150";
        alarmDisposeService.deviceTypeEnum(codeInfo);
        alarmDisposeService.deviceTypeEnum(codeInfoOne);
        alarmDisposeService.deviceTypeEnum(codeInfoTwo);
        alarmDisposeService.deviceTypeEnum(codeInfoThree);
        alarmDisposeService.deviceTypeEnum(codeInfoFilter);
    }

}