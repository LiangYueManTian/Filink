package com.fiberhome.filink.filinkoceanconnectserver.expire;

import com.fiberhome.filink.commonstation.constant.Constant;
import com.fiberhome.filink.commonstation.constant.DeviceEventType;
import com.fiberhome.filink.commonstation.constant.DeviceLogNameI18n;
import com.fiberhome.filink.commonstation.constant.DeviceLogType;
import com.fiberhome.filink.commonstation.exception.ResponseException;
import com.fiberhome.filink.commonstation.utils.AlarmUtil;
import com.fiberhome.filink.deviceapi.bean.DeviceLog;
import com.fiberhome.filink.deviceapi.util.DeviceStatus;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.filinkoceanconnectserver.client.AuthClient;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.filinkoceanconnectserver.stream.FiLinkKafkaSender;
import com.fiberhome.filink.filinkoceanconnectserver.utils.CommonUtil;
import com.fiberhome.filink.filinkoceanconnectserver.utils.ProtocolUtil;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 平台token失效处理
 *
 * @author CongcaiYu
 */
@Log4j
@Component
public class TokenExpireHandler extends KeyExpirationEventMessageListener {


    @Autowired
    private AuthClient authClient;

    @Autowired
    private ControlFeign controlFeign;

    @Autowired
    private ProtocolUtil protocolUtil;

    @Autowired
    private FiLinkKafkaSender streamSender;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 构造方法引入Redis监听配置类
     *
     * @param listenerContainer Redis监听配置类
     */
    public TokenExpireHandler(RedisMessageListenerContainer listenerContainer) {
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
        //token过期，刷新token
        String key = message.toString();
        //如果是app的token过期则进行刷新处理
        if (key.contains(RedisKey.APP_TOKEN_PREFIX)) {
            String appId = key.split(Constant.SEPARATOR)[1];
            log.info("the app : " + appId + " token is expire>>>>>>>");
            handleToken(appId);
        }
        //如果离线key值过期
        if (key.contains(RedisKey.OFF_LINE)) {
            log.info("the control : " + key + " is offline>>>>>>>>");
            updateControlStatus(key, DeviceStatus.Offline.getCode());
        }
        //如果失联key值过期
        if (key.contains(RedisKey.OUT_OF_CONCAT)) {
            log.info("the control : " + key + " is out of concat>>>>>>>>");
            updateControlStatus(key, DeviceStatus.Out_Contact.getCode());
        }
        if (key.contains(RedisKey.SLEEP_TIME)) {
            String equipmentId = key.split(Constant.SEPARATOR)[1];
            commonUtil.clearRedisSerialNum(equipmentId);
            //todo 设备休眠
            updateControlActiveStatus(equipmentId, Constant.SLEEP_STATUS);
            sendDeviceLog(equipmentId, DeviceLogType.EVENT, systemLanguageUtil.getI18nString(DeviceLogNameI18n.SLEEP_EVENT), DeviceEventType.SLEEP_EVENT);
        }
    }


    /**
     * 更新主控激活状态
     *
     * @param equipmentId 设备id
     * @param status 设备状态
     */
    private void updateControlActiveStatus(String equipmentId, String status) {
        ControlParam controlParam = new ControlParam();
        controlParam.setHostId(equipmentId);
        controlParam.setActiveStatus(status);
        controlFeign.updateControlParam(controlParam);
        ControlParam controlById = protocolUtil.getControlById(equipmentId);
        controlById.setActiveStatus(status);
        RedisUtils.hSet(RedisKey.CONTROL_INFO, equipmentId, controlById);
    }

    /**
     * 发送设施日志
     *
     * @param equipmentId 主控/设施id
     * @param logType     日志类型
     * @param logName     日志名称
     * @param type        日志类型
     */
    private void sendDeviceLog(String equipmentId, String logType, String logName, String type) {
        ControlParam controlParam = controlFeign.getControlParamById(equipmentId);
        DeviceLog deviceLog = commonUtil.setDeviceLogCommonInfo(controlParam);
        commonUtil.saveDeviceLog(deviceLog, logType, logName, type);
    }


    /**
     * 更新主控状态值
     *
     * @param expiredKey 过期key值
     * @param status     状态
     */
    private void updateControlStatus(String expiredKey, String status) {
        String equipmentId = expiredKey.split(Constant.SEPARATOR)[1];
        //清除redis中流水号
        commonUtil.clearRedisSerialNum(equipmentId);
        //清除指令时间
        RedisUtils.remove(RedisKey.CMD_TIME);
        //查询该设施主控信息
        ControlParam control = controlFeign.getControlParamById(equipmentId);
        if (control == null) {
            throw new ResponseException("control info is null>>>>>>>>>>>>>");
        }
        //如果该设备已失联,则不需要处理
        if (!DeviceStatus.Out_Contact.getCode().equals(control.getDeviceStatus())) {
            ControlParam controlParam = new ControlParam();
            controlParam.setHostId(equipmentId);
            //更新redis主控状态
            control.setDeviceStatus(status);
            RedisUtils.hSet(RedisKey.CONTROL_INFO, equipmentId, control);
            //将状态更新为离线或失联
            controlParam.setDeviceStatus(status);
            //激活状态改为休眠
            controlParam.setActiveStatus(Constant.SLEEP_STATUS);
            controlFeign.updateDeviceStatusById(controlParam);
        }
        String deviceId = control.getDeviceId();
        //生成通讯中断告警
        Map<String, Object> alarmMap = AlarmUtil.getInterruptAlarmMap(deviceId, equipmentId, Constant.ALARM);
        //发送到告警队列
        streamSender.sendAlarm(alarmMap);
    }


    /**
     * 处理token
     *
     * @param appId 应用id
     */
    private void handleToken(String appId) {
        authClient.getToken(appId);
    }
}
