package com.fiberhome.filink.stationserver.expire;

import com.fiberhome.filink.commonstation.constant.CmdType;
import com.fiberhome.filink.commonstation.constant.Constant;
import com.fiberhome.filink.commonstation.exception.ResponseException;
import com.fiberhome.filink.commonstation.utils.AlarmUtil;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.deviceapi.util.DeviceStatus;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.constant.RedisKey;
import com.fiberhome.filink.stationserver.stream.FiLinkKafkaSender;
import lombok.extern.log4j.Log4j;
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
@Log4j
@Component
public class DeviceStatusHandler extends KeyExpirationEventMessageListener {

    @Autowired
    private ControlFeign controlFeign;

    @Autowired
    private DeviceFeign deviceFeign;

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
            log.info("the control : " + expiredKey + " is offline>>>>>>>>");
            updateControlStatus(expiredKey, DeviceStatus.Offline.getCode());
        }
        //如果失联key值过期
        if (expiredKey.contains(RedisKey.OUT_OF_CONCAT)) {
            log.info("the control : " + expiredKey + " is out of concat>>>>>>>>");
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
        clearRedisSerialNum(equipmentId);
        //清除指令时间
        RedisUtils.remove(RedisKey.CMD_TIME);
        //将状态更新为离线
        ControlParam controlParam = new ControlParam();
        controlParam.setHostId(equipmentId);
        controlParam.setDeviceStatus(status);
        controlFeign.updateDeviceStatusById(controlParam);
        //查询该设施主控信息
        ControlParam control = controlFeign.getControlParamById(equipmentId);
        if (control == null) {
            throw new ResponseException("control info is null>>>>>>>>>>>>>");
        }
        String deviceId = control.getDeviceId();
        //根据主控id查询设施信息
        DeviceInfoDto deviceInfo = deviceFeign.getDeviceById(deviceId);
        if (deviceInfo == null) {
            throw new ResponseException("device info is null>>>>>>>>>>>>");
        }
        //生成通讯中断告警
        Map<String, Object> alarmMap = AlarmUtil.getInterruptAlarmMap(deviceId, equipmentId, Constant.ALARM);
        //发送到告警队列
        streamSender.sendAlarm(alarmMap);
    }

    /**
     * 清除redis中流水号
     *
     * @param equipmentId 设施id
     */
    private void clearRedisSerialNum(String equipmentId) {
        log.info("redis expire clear redis serialNumber : " + equipmentId);
        //请求帧流水号
        String reqKey = equipmentId + Constant.SEPARATOR + CmdType.REQUEST_TYPE;
        //响应帧流水号
        String resKey = equipmentId + Constant.SEPARATOR + CmdType.RESPONSE_TYPE;
        RedisUtils.hRemove(RedisKey.DEVICE_SERIAL_NUMBER, reqKey);
        RedisUtils.hRemove(RedisKey.DEVICE_SERIAL_NUMBER, resKey);
    }

}
