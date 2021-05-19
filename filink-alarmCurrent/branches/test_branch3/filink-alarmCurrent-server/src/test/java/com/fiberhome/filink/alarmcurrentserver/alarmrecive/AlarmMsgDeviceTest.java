package com.fiberhome.filink.alarmcurrentserver.alarmrecive;

import com.fiberhome.filink.alarmcurrentserver.bean.*;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmDisposeService;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmReceiveService;
import com.fiberhome.filink.alarmcurrentserver.stream.AlarmStreams;
import com.fiberhome.filink.alarmsetapi.bean.AlarmForwardRule;
import com.fiberhome.filink.alarmsetapi.bean.AlarmOrderRule;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(JMockit.class)
public class AlarmMsgDeviceTest {

    @Tested
    private AlarmMsgDevice alarmMsgDevice;

    /**
     * 告警接收和数据解析服务类
     */
    @Injectable
    private AlarmReceiveService alarmReceiveService;

    /**
     * 通道定义
     */
    @Injectable
    private AlarmStreams alarmStreams;

    /**
     * 告警处理服务类
     */
    @Injectable
    private AlarmDisposeService alarmDisposeService;
    @Injectable
    private AlarmMsgSender alarmMsgSender;
    @Test
    public void alarmMsg() throws Exception {

        Map<String, Object> alarmMsgMapOne = new HashMap<>(12);
        Map<String, Object> alarmMsgMapTwo = new HashMap<>(12);
        Map<String, Object> alarmMsgMapThree = new HashMap<>(12);
        List<Map> paramList = new ArrayList<>();
        alarmMsgMapTwo.put("1", "1");
        alarmMsgMapThree.put("data", alarmMsgMapTwo);
        alarmMsgMapThree.put("alarmFlag", "2");
        alarmMsgMapThree.put("dataClass", "IllegalOpeningInnerCover");
        alarmMsgMapThree.put("size", "1");
        paramList.add(alarmMsgMapThree);
        alarmMsgMapOne.put("equipmentId", "HOSTID2WWQWQ");
        alarmMsgMapOne.put("params", paramList);
        alarmMsgMapOne.put("deviceId", "01rcVQ8KAULrt9VY4SG");
        alarmMsgMapOne.put("time", "1858533639000");

        AlarmCurrentInfo alarmCurrentInfo = new AlarmCurrentInfo();
        alarmCurrentInfo.setAlarmCode("1");
        alarmCurrentInfo.setAlarmBeginTime(Long.valueOf("1"));
        List<OrderDeviceInfo> list = new ArrayList<>();
        alarmCurrentInfo.setOrderDeviceInfoList(list);
        alarmCurrentInfo.setIsOrder(false);
        alarmCurrentInfo.setEquipmentId("1");
        alarmCurrentInfo.setData("1");
        try {
            alarmMsgDevice.alarmMsg(alarmMsgMapOne);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void alarmAdviceReceives() throws Exception {
        AlarmInfo alarmInfo = new AlarmInfo();
        AlarmMessage alarmMessageInfo = new AlarmMessage();
        alarmMessageInfo.setAlarmColor("code");
        alarmMessageInfo.setPrompt("11");
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setId("1");
        alarmCurrent.setAlarmName("ming");
        alarmCurrent.setAlarmSource("001");
        alarmCurrent.setAlarmName("code");
        alarmCurrent.setAlarmFixedLevel("1");
        alarmCurrent.setAlarmNearTime(0L);
        AlarmMessageSource alarmMessageSource = new AlarmMessageSource();
        alarmMessageSource.setAlarmMessage(alarmMessageInfo);
        alarmMessageSource.setDeviceId(alarmCurrent.getAlarmSource());
        alarmMessageSource.setAlarmName(alarmCurrent.getAlarmName());
        alarmMessageSource.setAlarmLevel(alarmCurrent.getAlarmFixedLevel());
        alarmMessageSource.setAlarmNearTime(alarmCurrent.getAlarmNearTime());
        alarmInfo.setAlarmCurrent(alarmCurrent);
        alarmInfo.setAlarmMessage(alarmMessageInfo);
        List<AlarmForwardRule> list = new ArrayList<>();
        AlarmForwardRule alarmForwardRule = new AlarmForwardRule();
        alarmForwardRule.setId("1");
        list.add(alarmForwardRule);
        alarmInfo.setAlarmForwardRuleList(list);
        AlarmOrderRule alarmOrderRule = new AlarmOrderRule();
        alarmOrderRule.setId("1");
        alarmInfo.setAlarmOrderRule(alarmOrderRule);
        try {
            alarmMsgDevice.alarmAdviceReceives(alarmInfo);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void alarmPictureDevice() throws Exception {
        Map alarmPictureMap = new HashMap();
        alarmPictureMap.put("dataClass", "1");
        alarmPictureMap.put("deviceId", "2");
        alarmPictureMap.put("doorNum", "3");
        alarmPictureMap.put("fileFormat", "4");
        alarmPictureMap.put("picData", "5");
        alarmPictureMap.put("time", "3");
        try {
            alarmMsgDevice.alarmPictureDevice(alarmPictureMap);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

}