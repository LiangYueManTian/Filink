package com.fiberhome.filink.alarmcurrentserver.alarmrecive;


import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrentInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmInfo;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmReceiveService;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


/**
 * kafka消息监听接收处理类
 *
 * @author taowei
 */
@Slf4j
@Component
public class AlarmMsgReceiver {

    /**
     * 告警接收和数据解析服务类
     */
    @Autowired
    private AlarmReceiveService alarmReceiveService;

    @Autowired
    private AlarmMsgDevice alarmMsgDevice;

    /**
     * 设施上报告警接收消息监听处理，同时接受多条设施告警信息
     *
     * @param alarmMsgMap 告警信息
     * @throws Exception
     */
    @StreamListener(AlarmChannel.ALARM_INPUT)
    public void deviceAlarmReceive(Map alarmMsgMap) throws Exception {
        alarmMsgDevice.alarmMsg(alarmMsgMap);
    }

    /**
     * 告警推送，通知，转工单消息监听处理
     *
     * @param alarmInfo 告警信息
     */
    @StreamListener(AlarmChannel.ALARM_INPUT_END)
    public void alarmAdviceReceive(AlarmInfo alarmInfo) throws Exception {
        alarmMsgDevice.alarmAdviceReceives(alarmInfo);
    }

    /**
     * 工单超时转告警消息监听处理,同时接受多条工单告警信息，不需要排序
     *
     * @param alarmCurrentInfoListJson 告警信息
     */
    @Async
    @StreamListener(AlarmChannel.ORDER_ALARM_INPUT)
    public void orderCastAlarmReceive(String alarmCurrentInfoListJson) throws Exception {
        try {
            if (null != alarmCurrentInfoListJson) {
                List<AlarmCurrentInfo> alarmCurrentInfoList = JSONObject.parseArray(alarmCurrentInfoListJson, AlarmCurrentInfo.class);
                log.info(alarmCurrentInfoList.get(0).toString());
                alarmReceiveService.orderCastAlarm(alarmCurrentInfoList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 告警图片信息监听处理
     *
     * @param alarmPictureMap 图片信息
     *                        todo 余从偲调试
     */
    @StreamListener(AlarmChannel.ALARM_PIC_INPUT)
    public void alarmPictureReceive(Map alarmPictureMap) throws Exception {
        alarmMsgDevice.alarmPictureDevice(alarmPictureMap);
    }

}
