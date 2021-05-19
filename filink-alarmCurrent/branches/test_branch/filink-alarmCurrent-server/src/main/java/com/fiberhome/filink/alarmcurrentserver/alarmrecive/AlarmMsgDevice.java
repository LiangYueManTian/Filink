package com.fiberhome.filink.alarmcurrentserver.alarmrecive;

import com.aliyuncs.exceptions.ClientException;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrentInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmMessage;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmMessageSource;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmMsg;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmPictureMsg;
import com.fiberhome.filink.alarmcurrentserver.bean.DoorInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.OrderDeviceInfo;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrent18n;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmDisposeService;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmReceiveService;
import com.fiberhome.filink.alarmcurrentserver.stream.AlarmStreams;
import com.fiberhome.filink.alarmcurrentserver.utils.ListUtil;
import com.fiberhome.filink.alarmsetapi.bean.AlarmForwardRule;
import com.fiberhome.filink.alarmsetapi.bean.AlarmOrderRule;
import com.fiberhome.filink.bean.WebSocketMessage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * kafka消息监听设施上报
 *
 * @author taowei
 */
@Slf4j
@Component
public class AlarmMsgDevice {

    /**
     * 告警接收和数据解析服务类
     */
    @Autowired
    private AlarmReceiveService alarmReceiveService;

    /**
     * 通道定义
     */
    @Autowired
    private AlarmStreams alarmStreams;

    /**
     * 告警处理服务类
     */
    @Autowired
    private AlarmDisposeService alarmDisposeService;

    /**
     * 设施上报告警接收消息监听处理，同时接受多条设施告警信息
     *
     * @param alarmMsgMap 告警信息
     */
    @Async
    public void alarmMsg(Map alarmMsgMap) {
        try {
            if (null != alarmMsgMap.get(AlarmCurrent18n.PARAMS)) {
                List<AlarmMsg> alarmMsgList = saveAlarmInfo(alarmMsgMap, AlarmCurrent18n.PARAMS);
                //遍历告警信息
                List<AlarmMsg> dataList = alarmMsgList.stream().filter(alarmMsg ->
                        alarmMsg != null).collect(Collectors.toList());
                for (AlarmMsg alarmMessage : dataList) {
                    OrderDeviceInfo orderDeviceInfo = new OrderDeviceInfo();
                    List<OrderDeviceInfo> orderDeviceInfoList = new ArrayList<>();
                    Object data = alarmMessage.getData();
                    //解析门信息
                    orderDeviceInfo = getDoorInfo(data, orderDeviceInfo);
                    orderDeviceInfo.setAlarmSource(alarmMessage.getAlarmSource());
                    orderDeviceInfoList.add(orderDeviceInfo);
                    //设施信息
                    AlarmCurrentInfo alarmCurrentInfo = new AlarmCurrentInfo();
                    alarmCurrentInfo.setAlarmCode(alarmMessage.getAlarmCode());
                    alarmCurrentInfo.setAlarmBeginTime(Long.valueOf(alarmMessage.getAlarmBeginTime()));
                    alarmCurrentInfo.setOrderDeviceInfoList(orderDeviceInfoList);
                    alarmCurrentInfo.setIsOrder(false);
                    alarmCurrentInfo.setEquipmentId(alarmMessage.getEquipmentId());
                    alarmCurrentInfo.setData(alarmMessage.getData());
                    if (AppConstant.ONE.equals(alarmMessage.getAlarmStatus())) {
                        //如果是清除告警，修改对应告警信息的告警状态为设备清除
                        alarmDisposeService.autoCleanAlarm(alarmCurrentInfo);
                    } else {
                        //如果是告警，调用 AlarmReceiveService 中告警解析的接口
                        alarmReceiveService.alarmAnalysis(alarmCurrentInfo);
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存设施上报的告警信息到实体类
     *
     * @param alarmMsgMap 告警信息
     * @param param       key
     * @return dataList
     */
    private List<AlarmMsg> saveAlarmInfo(Map alarmMsgMap, String param) {
        List<AlarmMsg> dataList = new ArrayList<>();
        String time = alarmMsgMap.get("time").toString();
        String deviceId = alarmMsgMap.get("deviceId").toString();
        String equipmentId = alarmMsgMap.get("equipmentId").toString();
        List<Map> mapList = (ArrayList<Map>) alarmMsgMap.get(param);
        //存入List集合
        for (Map alarmMapInfo : mapList) {
            if (alarmMapInfo != null) {
                HashMap alarmMap = (HashMap) alarmMapInfo;
                //存入实体
                AlarmMsg alarmMsg = new AlarmMsg();
                alarmMsg.setAlarmStatus(alarmMap.get("alarmFlag").toString());
                alarmMsg.setData(alarmMap.get("data"));
                alarmMsg.setAlarmBeginTime(time);
                alarmMsg.setAlarmCode(alarmMap.get("dataClass").toString());
                alarmMsg.setEquipmentId(equipmentId);
                alarmMsg.setAlarmSource(deviceId);
                dataList.add(alarmMsg);
            }
        }
        return dataList;
    }

    /**
     * 解析门信息
     *
     * @param data            门信息
     * @param orderDeviceInfo 门集合
     * @return orderDeviceInfo
     */
    private OrderDeviceInfo getDoorInfo(Object data, OrderDeviceInfo orderDeviceInfo) {
        List<DoorInfo> doorInfoList = new ArrayList<>();
        if (data instanceof String) {
            //通讯中断告警，和不带门的告警，比如温度超限告警,加入温度等参数信息
            orderDeviceInfo.setDeviceStatus(data.toString());
        } else if (data instanceof List) {
            //有门的告警，加入门的信息
            List<String> dataInfo = (List<String>) data;
            log.info("door list is List+++++++++++++++++++++++");
            for (int j = 0; j < dataInfo.size(); j++) {
                //"data": {11，22，10，00},   // 门状态:   锁状态:  0:无效 1:开 2:关
                String doorStatus = dataInfo.get(j).substring(0, 1);
                DoorInfo doorInfo = new DoorInfo();
                doorInfo.setDoorStatus(doorStatus);
                doorInfo.setDoorNumber(Integer.toString(j + 1));
                doorInfoList.add(doorInfo);
            }
        } else if (data instanceof Map) {
            // 应急开锁是map结构，key 是门编号1,2,3,4,只有一个 ，value 是门状态 0:无效 1:开 2:关
            HashMap<String, String> unlockMap = (HashMap<String, String>) data;
            Set<String> doorKey = unlockMap.keySet();
            for (String key : doorKey) {
                String doorStatus = unlockMap.get(key);
                DoorInfo doorInfo = new DoorInfo();
                doorInfo.setDoorStatus(doorStatus);
                doorInfo.setDoorNumber(key);
                doorInfoList.add(doorInfo);
            }
        }
        orderDeviceInfo.setDoorInfoList(doorInfoList);
        return orderDeviceInfo;
    }

    /**
     * 告警推送，通知，转工单消息监听处理
     *
     * @param alarmInfo 告警信息
     */
    @Async
    public void alarmAdviceReceives(AlarmInfo alarmInfo) throws ClientException {
        if (null != alarmInfo) {
            log.info(alarmInfo.getAlarmCurrent().toString());
            AlarmCurrent alarmCurrent = alarmInfo.getAlarmCurrent();
            //告警灯推送，调用websocket+
            AlarmMessage alarmMessageInfo = alarmInfo.getAlarmMessage();
            AlarmMessageSource alarmMessageSource = new AlarmMessageSource();
            alarmMessageSource.setAlarmMessage(alarmMessageInfo);
            alarmMessageSource.setDeviceId(alarmCurrent.getAlarmSource());
            alarmMessageSource.setAlarmName(alarmCurrent.getAlarmName());
            alarmMessageSource.setAlarmLevel(alarmCurrent.getAlarmFixedLevel());
            alarmMessageSource.setAlarmNearTime(alarmCurrent.getAlarmNearTime());
            WebSocketMessage socketMessage = new WebSocketMessage();
            socketMessage.setChannelKey("alarm");
            //群发,1 单独发送，需要定义socketMessage.getChannelId() 999 发送多人
            // socketMessage.getKeys() 参数为token ，其他数字表示群发
            socketMessage.setMsgType(55);
            socketMessage.setMsg(alarmMessageSource);
            // 发送消息
            Message<WebSocketMessage> message = MessageBuilder.withPayload(socketMessage).build();
            alarmStreams.webSocketoutput().send(message);
            log.info(socketMessage.getMsg().toString());
            // 是否满足告警远程通知
            if (!ListUtil.isEmpty(alarmInfo.getAlarmForwardRuleList())) {
                List<AlarmForwardRule> alarmForwardRule = alarmInfo.getAlarmForwardRuleList();
                // 告警远程通知
                alarmDisposeService.alarmForward(alarmForwardRule, alarmCurrent);
            }
            if (null != alarmInfo.getAlarmOrderRule()) {
                AlarmOrderRule alarmOrderRule = alarmInfo.getAlarmOrderRule();
                // 告警转工单
                alarmDisposeService.alarmCastOrder(alarmOrderRule, alarmCurrent);
            }
        }
    }

    /**
     * 告警图片信息监听处理
     *
     * @param alarmPictureMap 图片信息
     */
    @Async
    public void alarmPictureDevice(Map alarmPictureMap) {
        try {
            //此处只有一张图片
            //alarmCode 告警编码   deviceId 设施ID doorNumber 门编号 suffix 图片后缀  pictureInfo 16进制图片信息
            Object code = alarmPictureMap.get("dataClass");
            Object device = alarmPictureMap.get("deviceId");
            Object number = alarmPictureMap.get("doorNum");
            Object suf = alarmPictureMap.get("fileFormat");
            Object picture = alarmPictureMap.get("picData");
            Object time = alarmPictureMap.get("time");
            AlarmPictureMsg alarmPictureMsg = new AlarmPictureMsg();
            if (null != code) {
                String alarmCode = code.toString();
                alarmPictureMsg.setAlarmCode(alarmCode);
            }
            if (null != device) {
                String deviceId = device.toString();
                alarmPictureMsg.setDeviceId(deviceId);
            }
            if (null != number) {
                String doorNumber = number.toString();
                alarmPictureMsg.setDoorNumber(doorNumber);
            }
            if (null != suf) {
                String suffix = suf.toString();
                alarmPictureMsg.setSuffix(suffix);
            }
            if (null != picture) {
                String pictureInfo = picture.toString();
                alarmPictureMsg.setPictureInfo(pictureInfo);
            }
            if (null != time) {
                String timeInfo = time.toString();
                alarmPictureMsg.setTime(timeInfo);
            }
            //告警图片处理逻辑
            alarmDisposeService.imageDispose(alarmPictureMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
