package com.fiberhome.filink.alarmcurrentserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.alarmcurrentserver.alarmrecive.AlarmMsgSender;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrentInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.DoorInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.OrderDeviceInfo;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmsetapi.api.AlarmSetFeign;
import com.fiberhome.filink.alarmsetapi.bean.AlarmFilterCondition;
import com.fiberhome.filink.alarmsetapi.bean.AlarmFilterRule;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.AreaInfo;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@RunWith(JMockit.class)
public class AlarmReceiveServiceImplTest {

    /**
     * 工单超时告警编码,跟数据库一致
     */
    private static final String orderOutOfTime = "100000";

    @Tested
    private AlarmReceiveServiceImpl alarmReceiveService;

    /**
     * mongodb实现类
     */
    @Injectable
    private MongoTemplate mongoTemplate;

    /**
     * 告警设置服务接口类
     */
    @Injectable
    private AlarmSetFeign alarmSetFeign;

    /**
     * 设施服务接口类
     */
    @Injectable
    private DeviceFeign deviceFeign;

    /**
     * 消息推送
     */
    @Injectable
    private AlarmMsgSender alarmMsgSender;

    @Injectable
    private AreaFeign areaFeign;

    /**
     * lock
     */
    @Injectable
    private ControlFeign controlFeign;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 工单转告警解析
     *
     * @throws Exception
     */
    @Test
    public void orderCastAlarm() throws Exception {
        List<AlarmCurrentInfo> alarmCurrentInfos = new ArrayList<>();
        AlarmCurrentInfo alarmCurrentInfo = new AlarmCurrentInfo();
        alarmCurrentInfo.setOrderId("1");
        alarmCurrentInfo.setEquipmentId("1000");
        alarmCurrentInfo.setAlarmCode("code");
        alarmCurrentInfo.setOrderName("ming");
        alarmCurrentInfo.setAlarmBeginTime(0L);
        alarmCurrentInfo.setAlarmStatus("3");
        alarmCurrentInfo.setIsOrder(false);
        alarmCurrentInfos.add(alarmCurrentInfo);
        List<OrderDeviceInfo> orderDeviceInfoList = new ArrayList<>();
        OrderDeviceInfo orderDeviceInfo = new OrderDeviceInfo();
        orderDeviceInfo.setAreaId("1");
        orderDeviceInfo.setAddress("diz");
        orderDeviceInfoList.add(orderDeviceInfo);
        alarmCurrentInfo.setOrderDeviceInfoList(orderDeviceInfoList);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("provinceName", "1");
        jsonObject.put("cityName", "1");
        jsonObject.put("districtName", "2");
        jsonObject.put("address", "2");
        new Expectations() {
            {
                areaFeign.queryAreaById(anyString);
                result = ResultUtils.success(jsonObject);
            }
        };
        alarmReceiveService.orderCastAlarm(alarmCurrentInfos);
        new Expectations() {
            {
                alarmSetFeign.queryAlarmIsIncludedFeign((List<AlarmFilterCondition>) any);
                result = null;
            }
        };
        alarmReceiveService.orderCastAlarm(alarmCurrentInfos);
    }

    /**
     * 设施上报告警
     *
     * @throws Exception
     */
    @Test
    public void alarmAnalysis() throws Exception {
        AlarmCurrentInfo alarmCurrentInfo = new AlarmCurrentInfo();
        alarmCurrentInfo.setOrderId("1");
        alarmCurrentInfo.setEquipmentId("1000");
        alarmCurrentInfo.setAlarmCode("code");
        alarmCurrentInfo.setOrderName("ming");
        alarmCurrentInfo.setAlarmBeginTime(0L);
        alarmCurrentInfo.setAlarmStatus("3");
        alarmCurrentInfo.setIsOrder(true);
        List<OrderDeviceInfo> orderDeviceInfoList = new ArrayList<>();
        OrderDeviceInfo orderDeviceInfo = new OrderDeviceInfo();
        orderDeviceInfo.setAreaId("1");
        orderDeviceInfo.setAddress("diz");
        List<DoorInfo> doorInfos = new ArrayList<>();
        DoorInfo doorInfo = new DoorInfo();
        doorInfo.setDoorName("qqw");
        doorInfo.setDoorNumber("11");
        doorInfo.setDoorStatus("qqq");
        doorInfos.add(doorInfo);
        orderDeviceInfo.setDoorInfoList(doorInfos);
        StringBuffer deptIds = new StringBuffer("1,2");
        orderDeviceInfo.setResponsibleDepartmentIds(deptIds);
        orderDeviceInfoList.add(orderDeviceInfo);
        alarmCurrentInfo.setOrderDeviceInfoList(orderDeviceInfoList);

        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        Set<String> strings = new HashSet<>();
        strings.add("2");
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setAccountabilityUnit(strings);
        StringBuilder accountabilityUnitNames = new StringBuilder("1,2");
        areaInfo.setAccountabilityUnitName(accountabilityUnitNames);
        deviceInfoDto.setAreaInfo(areaInfo);
        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
            }
        };
        alarmReceiveService.alarmAnalysis(alarmCurrentInfo);
        AlarmFilterRule alarmFilterRule = new AlarmFilterRule();
        alarmFilterRule.setIsDeleted(null);
        new Expectations() {
            {
                alarmSetFeign.queryAlarmIsIncludedFeign((List<AlarmFilterCondition>) any);
                result = null;
            }
        };
        alarmReceiveService.alarmAnalysis(alarmCurrentInfo);
    }

    /**
     * 设施上报告警解析
     */
    @Test
    public void alarmAnalysisTest() {
        AlarmCurrentInfo alarmCurrentInfoOne = new AlarmCurrentInfo();
        alarmCurrentInfoOne.setOrderId("1");
        alarmCurrentInfoOne.setEquipmentId("1000");
        alarmCurrentInfoOne.setAlarmCode("code");
        alarmCurrentInfoOne.setOrderName("ming");
        alarmCurrentInfoOne.setAlarmBeginTime(0L);
        alarmCurrentInfoOne.setAlarmStatus("3");
        alarmCurrentInfoOne.setIsOrder(true);
        List<OrderDeviceInfo> orderDeviceInfoArrayList = new ArrayList<>();
        OrderDeviceInfo deviceInfo = new OrderDeviceInfo();
        List<DoorInfo> doorInfoList = new ArrayList<>();
        deviceInfo.setDoorInfoList(doorInfoList);
        orderDeviceInfoArrayList.add(deviceInfo);
        alarmCurrentInfoOne.setOrderDeviceInfoList(orderDeviceInfoArrayList);
        alarmReceiveService.alarmAnalysis(alarmCurrentInfoOne);
        AlarmFilterRule alarmFilterRule = new AlarmFilterRule();
        alarmFilterRule.setId("1");
        new Expectations() {
            {
                alarmSetFeign.queryAlarmIsIncludedFeign((List<AlarmFilterCondition>) any);
                result = null;
            }
        };
        alarmReceiveService.alarmAnalysis(alarmCurrentInfoOne);
        List<AlarmCurrent> alarmCurrentList = new ArrayList<>();
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setId("1");
        alarmCurrent.setAlarmName("code");
        alarmCurrent.setAlarmBeginTime(0L);
        alarmCurrent.setAlarmNearTime(9L);
        alarmCurrent.setAlarmSystemNearTime(0L);
        alarmCurrent.setControlId("111");
        alarmCurrent.setAlarmHappenCount(1);
        alarmCurrentList.add(alarmCurrent);
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = alarmCurrentList;
            }
        };
        new Expectations() {
            {
                mongoTemplate.updateFirst((Query) any, (Update) any, AlarmCurrent.class);
            }
        };
        alarmReceiveService.alarmAnalysis(alarmCurrentInfoOne);
    }

    @Test
    public void selectCodes() {
        String codeInfo = AppConstant.PRYDOOR;
        String codeInfoOne = AppConstant.NOTCLOSED;
        String codeInfoTwo = AppConstant.VIOLENCECLOSE;
        String codeInfoThree = AppConstant.PRYLOCK;
        String codeInfoTo = AppConstant.UNLOCK;
        String codeInfoFour = AppConstant.EMERGENCYLOCK;
        String codeInfoSix = AppConstant.ILLEGAL_OPEN_LOCK;
        alarmReceiveService.selectCodes(codeInfo, new String());
        alarmReceiveService.selectCodes(codeInfoOne, new String());
        alarmReceiveService.selectCodes(codeInfoTwo, new String());
        alarmReceiveService.selectCodes(codeInfoThree, new String());
        alarmReceiveService.selectCodes(codeInfoTo, new String());
        alarmReceiveService.selectCodes(codeInfoFour, new String());
        alarmReceiveService.selectCodes(codeInfoSix, new String());
    }

    @Test
    public void selectCodeInfo() {
        String codeInfo = AppConstant.HUMIDITY;
        String codeInfoOne = AppConstant.HIGHTEMPERATURE;
        String codeInfoTwo = AppConstant.ELECTRICITY;
        String codeInfoThree = AppConstant.LEAN;
        alarmReceiveService.selectCodeInfo(codeInfo, new String(), new AlarmCurrentInfo());
        alarmReceiveService.selectCodeInfo(codeInfoOne, new String(), new AlarmCurrentInfo());
        alarmReceiveService.selectCodeInfo(codeInfoTwo, new String(), new AlarmCurrentInfo());
        alarmReceiveService.selectCodeInfo(codeInfoThree, new String(), new AlarmCurrentInfo());
    }

}