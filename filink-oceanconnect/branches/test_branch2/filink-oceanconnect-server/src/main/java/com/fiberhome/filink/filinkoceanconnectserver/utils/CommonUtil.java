package com.fiberhome.filink.filinkoceanconnectserver.utils;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.WebSocketMessage;
import com.fiberhome.filink.commonstation.constant.CmdType;
import com.fiberhome.filink.commonstation.constant.Constant;
import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.config.UnlockPushBean;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.entity.xmlbean.header.HeaderParam;
import com.fiberhome.filink.commonstation.exception.FiLinkBusinessException;
import com.fiberhome.filink.commonstation.exception.ResponseException;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.utils.AliYunPushUtil;
import com.fiberhome.filink.deviceapi.api.DeviceLogFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceLog;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.filinkoceanconnectserver.controller.PerformanceTest;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkReqOceanConnectParams;
import com.fiberhome.filink.filinkoceanconnectserver.stream.FiLinkKafkaSender;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.AliAccessKey;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公共调用方法
 *
 * @Author: qiqizhu@wistronits.com
 * Date:2019/6/5
 */
@Slf4j
@Component
public class CommonUtil {
    @Autowired
    private DeviceLogFeign deviceLogFeign;
    @Autowired
    private ControlFeign controlFeign;
    @Autowired
    private ParameterFeign parameterFeign;
    @Autowired
    private AliYunPushUtil pushUtil;
    @Autowired
    private FiLinkKafkaSender streamSender;

    /**
     * 保存设施日志
     *
     * @param deviceLog 设施日志
     * @param logType   设施日志类型
     * @param logName   设施日志名称
     * @param type      事件类型
     */
    public void saveDeviceLog(DeviceLog deviceLog, String logType, String logName, String type) {
        deviceLog.setLogName(logName);
        deviceLog.setLogType(logType);
        deviceLog.setType(type);
        deviceLogFeign.saveDeviceLog(deviceLog);
    }

    /**
     * 设置设施日志公共信息
     *
     * @param control       控制器信息
     * @return 设施日志对象
     */
    public DeviceLog setDeviceLogCommonInfo(ControlParam control) {
        //设施日志
        DeviceLog deviceLog = new DeviceLog();
        //获取主控信息
        if (control == null) {
            throw new FiLinkBusinessException("control is null>>>>>>>");
        }
        //设置设施日志信息
        deviceLog.setDeviceId(control.getDeviceId());
        deviceLog.setNodeObject(control.getHostName());
        deviceLog.setCurrentTime(System.currentTimeMillis());
        return deviceLog;
    }


    /**
     * 刷新缓存时间
     * 人井专用
     */
    public void refreshRedisTime(String equipmentId, ControlParam control) {
        refreshHeatBeatTime(equipmentId, control);
        String sleepTime = RedisKey.SLEEP_TIME + Constant.SEPARATOR + equipmentId;
        RedisUtils.set(sleepTime, equipmentId, 20);
    }

    /**
     * 更新主控心跳时间
     *
     * @param equipmentId 主控id
     * @param control     主控对象信息
     */
    public void refreshHeatBeatTime(String equipmentId, ControlParam control) {
        //获取该主控重启时间和心跳周期
        String configValue = control.getConfigValue();
        if (configValue == null) {
            throw new ResponseException("the control : " + equipmentId + "config value is null>>>>>>>>>>>>>");
        }
        Map<String, Object> configMap = JSONObject.parseObject(configValue, Map.class);
        Object heartBeatCycleObj = configMap.get(ParamsKey.HEART_BEAT_CYCLE);
        if (heartBeatCycleObj == null) {
            throw new ResponseException("the control : " + equipmentId + "heartbeaat cycle is null>>>>>>>>>>>>>");
        }
        //心跳周期
        long hearBeatCycle = Integer.parseInt(heartBeatCycleObj.toString()) * 60;
        //拼接key值
        String offLineKey = RedisKey.OFF_LINE + Constant.SEPARATOR + equipmentId;
        //设置离线过期时间
        RedisUtils.set(offLineKey, equipmentId, hearBeatCycle);
        //设置失联过期时间
        String outOfConcatKey = RedisKey.OUT_OF_CONCAT + Constant.SEPARATOR + equipmentId;
        RedisUtils.set(outOfConcatKey, equipmentId, 2 * hearBeatCycle);
    }


    /**
     * 过滤相同流水号指令
     *
     * @param serialNumber 流水号
     * @return 是否包含该流水号
     */
    public boolean filterRepeatCmd(String key, Integer serialNumber) {
        //从redis中获取该设施流水号
        List<Integer> serialNumList = (List<Integer>) RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, key);
        if (serialNumList == null || serialNumList.size() == 0) {
            serialNumList = new ArrayList<>();
            serialNumList.add(serialNumber);
            RedisUtils.hSet(RedisKey.DEVICE_SERIAL_NUMBER, key, serialNumList);
            return false;
        } else {
            //判断是否包含该流水号,若包含则不进行处理
            if (serialNumList.contains(serialNumber)) {
                return true;
            } else {
                serialNumList.add(serialNumber);
                RedisUtils.hSet(RedisKey.DEVICE_SERIAL_NUMBER, key, serialNumList);
                return false;
            }
        }

    }

    /**
     * 发送指令
     *
     * @param control 主控信息
     * @param cmdId   命令id
     * @param params  参数信息
     */
    public FiLinkReqOceanConnectParams getResponseParams(ControlParam control, String cmdId, Map<String, Object> params, Integer serialNumber) {
        FiLinkReqOceanConnectParams fiLinkReqOceanConnectParams = new FiLinkReqOceanConnectParams();
        //设置指令类型
        fiLinkReqOceanConnectParams.setCmdType(CmdType.RESPONSE_TYPE);
        //设置指令id
        fiLinkReqOceanConnectParams.setCmdId(cmdId);
        //设置设施id
        fiLinkReqOceanConnectParams.setEquipmentId(control.getHostId());
        //设置参数信息
        fiLinkReqOceanConnectParams.setParams(params);
        //设置流水号
        fiLinkReqOceanConnectParams.setSerialNumber(serialNumber);
        //设置软件版本
        fiLinkReqOceanConnectParams.setSoftwareVersion(control.getSoftwareVersion());
        //设置硬件版本
        fiLinkReqOceanConnectParams.setHardwareVersion(control.getHardwareVersion());
        //设置appId
        fiLinkReqOceanConnectParams.setAppId(control.getProductId());
        //设置平台id
        fiLinkReqOceanConnectParams.setOceanConnectId(control.getPlatformId());
        return fiLinkReqOceanConnectParams;
    }

    /**
     * 开锁消息处理
     *
     * @param equipmentId 设备/主控id
     * @param socketMsg 消息
     * @param key 缓存key
     */
    public void unLockMessagePush(String equipmentId, String socketMsg, String key, Result result) {
        UnlockPushBean pushBean = (UnlockPushBean) RedisUtils.hGet(RedisKey.UNLOCK_PUSH, key);
        if (pushBean == null) {
            log.error("unlock push bean is null>>>>>>>>> : " + equipmentId);
            return;
        }
        //获取手机id
        if (!StringUtils.isEmpty(pushBean.getPhoneId())) {
            //app推送
            pushBean.setMsg(socketMsg);
            pushAppMsg(pushBean);
            //清除token信息
            RedisUtils.hRemove(RedisKey.UNLOCK_PUSH, key);
            //todo 开锁测试
            PerformanceTest.setLockTime(equipmentId, "pushMsgTime", System.currentTimeMillis());
            return;
        }
        //获取该指令对应的用户token
        if (StringUtils.isEmpty(pushBean.getToken())) {
            log.error("the token is null : " + equipmentId);
            return;
        }
        //推送开锁成功消息
        WebSocketMessage socketMessage = new WebSocketMessage();
        socketMessage.setChannelKey(Constant.UNLOCK_RESULT);
        socketMessage.setChannelId(pushBean.getToken());
        socketMessage.setMsgType(0);
        socketMessage.setMsg(result);
        streamSender.sendWebSocket(socketMessage);
        //todo 开锁测试
        PerformanceTest.setLockTime(equipmentId, "pushMsgTime", System.currentTimeMillis());
        //清除token信息
        RedisUtils.hRemove(RedisKey.UNLOCK_PUSH, key);
    }

    /**
     * 推送app消息
     *
     * @param pushBean 推送实体
     */
    private void pushAppMsg(UnlockPushBean pushBean) {
        //查询appkey和密钥
        AliAccessKey aliAccessKey = (AliAccessKey) RedisUtils.get(RedisKey.ALI_PUSH_KEY);
        //若redis为空,则查询系统服务
        if (aliAccessKey == null) {
            aliAccessKey = parameterFeign.queryMobilePush();
            if (aliAccessKey == null) {
                log.error("system aliAccessKey is null>>>>");
                return;
            }
            RedisUtils.set(RedisKey.ALI_PUSH_KEY, aliAccessKey);
        }
        pushBean.setAccessKeyId(aliAccessKey.getAccessKeyId());
        pushBean.setAccessKeySecret(aliAccessKey.getAccessKeySecret());
        pushBean.setTitle(I18nUtils.getSystemString(OceanConnectI18n.UNLOCK_TITLE));
        //app推送
        pushUtil.pushMsg(pushBean);
    }

    /**
     * 清除redis中流水号
     *
     * @param equipmentId 设施id
     */
    public void clearRedisSerialNum(String equipmentId) {
        log.info("redis expire clear redis serialNumber : " + equipmentId);
        //请求帧流水号
        String reqKey = equipmentId + Constant.SEPARATOR + CmdType.REQUEST_TYPE;
        //响应帧流水号
        String resKey = equipmentId + Constant.SEPARATOR + CmdType.RESPONSE_TYPE;
        RedisUtils.hRemove(RedisKey.DEVICE_SERIAL_NUMBER, reqKey);
        RedisUtils.hRemove(RedisKey.DEVICE_SERIAL_NUMBER, resKey);
        RedisUtils.hRemove(RedisKey.DEVICE_SERIAL_NUMBER, equipmentId);
    }

    /**
     * 响应帧数据特殊处理
     *
     * @param headerParam 响应头参数信息
     * @param dataBuf     缓冲流
     * @return 处理后数据
     */
    public String executeHeader(HeaderParam headerParam, ByteBuf dataBuf) {
        //判断有无处理类
        DataFormat dataFormat = headerParam.getDataFormat();
        if (dataFormat != null) {
            Map<String, Object> headerParamMap = new HashMap<>(64);
            headerParamMap.put(ParamsKey.CMD_TYPE, CmdType.RESPONSE_TYPE);
            dataFormat.setParam(headerParamMap);
            return execute(dataFormat, dataBuf).toString();
        } else {
            throw new ResponseException("data type is null: " + headerParam.getId());
        }
    }

    /**
     * 执行handler
     *
     * @param dataFormat 数据处理类
     * @param byteBuf    字节缓冲流
     * @return 处理后数据
     */
    public Object execute(DataFormat dataFormat, ByteBuf byteBuf) {
        try {
            Class<?> dataFormatClazz = Class.forName(dataFormat.getClassName());
            DataHandler handler = (DataHandler) dataFormatClazz.newInstance();
            return handler.handle(dataFormat, byteBuf);
        } catch (Exception e) {
            throw new ResponseException("data format execute failed: " + dataFormat.getId());
        }
    }

    /**
     * 生成流水号
     *
     * @param equipmentId 主控id
     * @return 流水号
     */
    public synchronized Integer getSerialNum(String equipmentId) {
        try {
            //从redis中获取该设施流水号
            Integer serialNum = (Integer) RedisUtils.hGet(RedisKey.SERIAL_NUM, equipmentId);
            //如果redis为空,赋予初始值
            if (serialNum == null) {
                serialNum = 1;
            } else {
                //如果流水号达到最大值,则从1开始
                if (serialNum == Short.MAX_VALUE - Short.MIN_VALUE) {
                    serialNum = 1;
                } else {
                    serialNum += 1;
                }
            }
            //将序列号存入redis
            RedisUtils.hSet(RedisKey.SERIAL_NUM, equipmentId, serialNum);
            return serialNum;
        } catch (Exception e) {
            log.error("get serial number from redis failed>>>>>>");
        }
        return 1;
    }
}
