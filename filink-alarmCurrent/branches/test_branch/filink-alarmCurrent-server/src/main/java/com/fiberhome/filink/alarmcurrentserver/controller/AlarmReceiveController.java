package com.fiberhome.filink.alarmcurrentserver.controller;


import com.fiberhome.filink.alarmcurrentserver.alarmrecive.AlarmMsgSender;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrentInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmMessage;
import com.fiberhome.filink.alarmcurrentserver.bean.OrderDeviceInfo;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmDisposeService;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmReceiveService;
import com.fiberhome.filink.alarmcurrentserver.stream.AlarmStreams;
import com.fiberhome.filink.alarmsetapi.api.AlarmSetFeign;
import com.fiberhome.filink.bean.WebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 当前告警接收前端控制器
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@RestController
@RequestMapping("/alarmReceive")
@Slf4j
public class AlarmReceiveController {

    @Autowired
    private AlarmMsgSender alarmMsgSender;

    @Autowired
    private AlarmReceiveService alarmReceiveService;

    @Autowired
    private AlarmStreams alarmStreams;

    @Autowired
    private AlarmDisposeService alarmDisposeService;

    @Autowired
    private AlarmSetFeign alarmSetFeign;

    /**
     * 告警接收和解析 发送 kafka 消息
     * todo 测试方法 余从偲
     */
    @PostMapping("/sendAlarmCurrentInfo")
    public void sendAlarmCurrentInfo() {
        Map<String, Object> alarmMsgMapOne = new HashMap<>(12);
        Map<String, Object> alarmMsgMapTwo = new HashMap<>(12);
        Map<String, Object> alarmMsgMapThree = new HashMap<>(12);
        Map<String, String> alarmMsgMapFour = new HashMap<>(12);
        Map<String, Object> alarmMsgMapFive = new HashMap<>(12);
        List<String> dataList = new ArrayList<>();
        List<Map> paramList = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {

        //告警状态 alarmStatus    1 恢复告警   2 告警   alarmFlag
        //告警编码 alarmCode   dataClass
        //设备上报告警时间 alarmBeginTime   time
        //设施id  alarmSourseId  deviceId

        //第二条信息,无门的设施告警
//            alarmMsgMapThree.put("data", "88");
//            alarmMsgMapThree.put("alarmFlag", "1");
//            alarmMsgMapThree.put("dataClass", "lean");
//            paramList.add(alarmMsgMapThree);

        //第三条信息,应急开锁告警
//        alarmMsgMapFour.put("1","2");
//        alarmMsgMapFive.put("data", alarmMsgMapFour);
//        alarmMsgMapFive.put("alarmFlag", "2");
//        alarmMsgMapFive.put("dataClass", "unLock");
//        paramList.add(alarmMsgMapFive);

        // 门状态:   锁状态:  0:无效 1:开 2:关
        //第一条信息，有门的设施告警
//        dataList.add("01");
//        dataList.add("22");
//        dataList.add("10");
//        dataList.add("00");
//        alarmMsgMapTwo.put("data", dataList);
//        alarmMsgMapTwo.put("alarmFlag", "1");
//        alarmMsgMapTwo.put("dataClass", "unLock");
//        paramList.add(alarmMsgMapTwo);
//
//            alarmMsgMapOne.put("params", paramList);
//            alarmMsgMapOne.put("deviceId", "mmDmYE9FrF7aMaL9G6G");
//            alarmMsgMapOne.put("time", "1558481451");
//        }
//        for (int i = 0; i < 300; i++) {

        alarmSetFeign.queryCurrentAlarmSetFeign("11");
        alarmMsgMapTwo.put("1", "12");
        alarmMsgMapTwo.put("2", "00");
        alarmMsgMapTwo.put("3", "00");
        alarmMsgMapTwo.put("4", "00");
        alarmMsgMapThree.put("data", alarmMsgMapTwo);

        alarmMsgMapThree.put("alarmFlag", "2");
        alarmMsgMapThree.put("dataClass", "communicationInterrupt");
        alarmMsgMapThree.put("size", "2");

        alarmMsgMapThree.put("alarmFlag", "2");
        alarmMsgMapThree.put("dataClass", "violenceClose");
        alarmMsgMapThree.put("size", "1");

        paramList.add(alarmMsgMapThree);
        alarmMsgMapOne.put("equipmentId", "0101F3A7102A374757AF");
        alarmMsgMapOne.put("params", paramList);

        alarmMsgMapOne.put("deviceId", "xvkVcIw2HhVqELgoihL");
        alarmMsgMapOne.put("time", "1558171506");

        alarmMsgMapOne.put("deviceId", "DGNv90vborA2ZqwI6OV");
        alarmMsgMapOne.put("time", "1558533639000");
            /*alarmMsgMapTwo.put("1", "1");
            alarmMsgMapThree.put("data", alarmMsgMapTwo);
            alarmMsgMapThree.put("alarmFlag", "2");
            alarmMsgMapThree.put("dataClass", "notClosed");
            alarmMsgMapThree.put("size", "1");
            paramList.add(alarmMsgMapThree);
            alarmMsgMapOne.put("equipmentId", "HOSTID2WWQWQ");
            alarmMsgMapOne.put("params", paramList);
            alarmMsgMapOne.put("deviceId", "0At8fvtc1kWZQNkhSaC");
            alarmMsgMapOne.put("time", "1858533639000");*/


//        }
        alarmMsgSender.send(alarmMsgMapOne);
        log.info("--------发送了告警信息到kafka----：" + alarmMsgMapOne.get("time"));
    }

    /**
     * 工单转告警发送 kafka 消息
     * todo 测试方法 魏鹤东
     */
    @PostMapping("/orderCastAlarm")
    public void orderCastAlarm() {
        List<AlarmCurrentInfo> paramList = new ArrayList<>();
        List<OrderDeviceInfo> deviceList = new ArrayList<>();
        AlarmCurrentInfo alarmCurrentInfo = new AlarmCurrentInfo();
        OrderDeviceInfo orderDeviceInfo = new OrderDeviceInfo();
        //添加设施信息
        orderDeviceInfo.setAreaId("1");
        orderDeviceInfo.setAreaName("2");
        orderDeviceInfo.setAddress("3");
        orderDeviceInfo.setResponsibleDepartmentIds(new StringBuffer(""));
        orderDeviceInfo.setResponsibleDepartmentNames(new StringBuffer(""));
        deviceList.add(orderDeviceInfo);
        //添加告警信息
        alarmCurrentInfo.setOrderDeviceInfoList(deviceList);
        alarmCurrentInfo.setAlarmBeginTime(1551481451000L);
        alarmCurrentInfo.setAlarmStatus("1");
        alarmCurrentInfo.setIsOrder(true);
        alarmCurrentInfo.setOrderId("111");
        alarmCurrentInfo.setOrderName("这是个巡检工单");
        //添加到集合
        paramList.add(alarmCurrentInfo);
        alarmMsgSender.orderCastAlarm(paramList);
        log.info("--------发送了工单转告警信息到kafka----：" + paramList.get(0).toString());
    }

    /**
     * 告警图片发送 kafka 消息
     * todo 测试方法 余从偲
     */
    @PostMapping("/alarmPictureReceive")
    public void alarmPictureReceive() {
        Map<String, Object> alarmPictureMap = new HashMap<>(12);
        //alarmCode 告警编码   deviceId 设施ID doorNumber 门编号 suffix 图片后缀  pictureInfo 16进制图片信息
        alarmPictureMap.put("alarmCode", "unLock");
        alarmPictureMap.put("deviceId", "099a741730f011e992ed519d1fc57e29");
        alarmPictureMap.put("doorNumber", "3");
        alarmPictureMap.put("suffix", "JPG");
        alarmPictureMap.put("pictureInfo", "099a741730f011e992ed519d1fc57e29");
        alarmMsgSender.sendPicture(alarmPictureMap);
        log.info("key：" + alarmPictureMap.get("alarmCode"));
    }

    /**
     * websocket
     * todo 测试方法
     *
     * @return 判断结果
     */
    @PostMapping("/websocket/{levelId}")
    public void websocket(@PathVariable String levelId) {
        AlarmMessage alarmMessage = new AlarmMessage();
        alarmMessage.setAlarmLevel("222");
        alarmMessage.setAlarmLevelCode(levelId);
        alarmMessage.setAlarmColor("222");
        alarmMessage.setIsPrompt("1");
        alarmMessage.setPlayCount(5);
        alarmMessage.setFilter("1");
        alarmMessage.setPrompt("a.mp3");
        //告警灯推送，调用websocket
        WebSocketMessage socketMessage = new WebSocketMessage();
        socketMessage.setChannelKey("alarm");
        socketMessage.setMsgType(55);
        socketMessage.setMsg(alarmMessage);
        // 发送消息
        Message<WebSocketMessage> message = MessageBuilder.withPayload(socketMessage).build();
        alarmStreams.webSocketoutput().send(message);
    }

}
