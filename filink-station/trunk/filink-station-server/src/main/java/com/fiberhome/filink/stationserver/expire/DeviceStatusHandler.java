package com.fiberhome.filink.stationserver.expire;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.commonstation.constant.Constant;
import com.fiberhome.filink.commonstation.utils.AlarmUtil;
import com.fiberhome.filink.deviceapi.util.DeviceStatus;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.constant.RedisKey;
import com.fiberhome.filink.stationserver.stream.FiLinkKafkaSender;
import com.fiberhome.filink.stationserver.util.StationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 设施状态判断处理
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class DeviceStatusHandler extends KeyExpirationEventMessageListener {

    @Autowired
    private ControlFeign controlFeign;

    @Autowired
    private FiLinkKafkaSender streamSender;


    /**
     * 构造方法引入Redis监听配置类
     *
     * @param listenerContainer Redis监听配置类
     */
    public DeviceStatusHandler(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 针对redis数据失效事件，进行数据处理
     *
     * @param message 监听信息
     * @param pattern pattern matching the channel (if specified) - can be null
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        //判断是否为null
        if (message == null) {
            return;
        }
        //如果离线key值过期
        String expiredKey = message.toString();
        if (expiredKey.contains(RedisKey.OFF_LINE)) {
            log.info("[offLine] : the control {} is offline", expiredKey);
            updateControlStatus(expiredKey, DeviceStatus.Offline.getCode());
        }
        //如果失联key值过期
        if (expiredKey.contains(RedisKey.OUT_OF_CONCAT)) {
            log.info("[outOfConcat] : the control {} is out of concat", expiredKey);
            updateControlStatus(expiredKey, DeviceStatus.Out_Contact.getCode());
        }
    }


    /**
     * 更新主控状态值
     *
     * @param expiredKey 过期key值
     * @param status     状态
     */
    private void updateControlStatus(String expiredKey, String status) {
        String equipmentId = expiredKey.split(Constant.SEPARATOR)[1];
        StationUtil.clearRedisSerialNum(equipmentId);
        //查询该设施主控信息
        ControlParam control = controlFeign.getControlParamById(equipmentId);
        String deviceStatus = control.getDeviceStatus();
        //如果该设备已失联,则不需要处理
        if (!DeviceStatus.Out_Contact.getCode().equals(deviceStatus)) {
            ControlParam controlParam = new ControlParam();
            controlParam.setHostId(equipmentId);
            //将状态更新为离线或失联
            controlParam.setDeviceStatus(status);
            //激活状态改为休眠
            controlParam.setActiveStatus(Constant.SLEEP_STATUS);
            Result result = controlFeign.updateDeviceStatusById(controlParam);
            if (result != null && result.getCode() == 0) {
                //更新redis主控状态
                control.setDeviceStatus(status);
                control.setActiveStatus(Constant.SLEEP_STATUS);
                RedisUtils.hSet(RedisKey.CONTROL_INFO, equipmentId, control);
            }
        }
        String deviceId = control.getDeviceId();
        //生成通讯中断告警
        Map<String, Object> alarmMap = AlarmUtil.getInterruptAlarmMap(deviceId, equipmentId, Constant.ALARM);
        //发送到告警队列
        streamSender.sendAlarm(alarmMap);
    }

}
