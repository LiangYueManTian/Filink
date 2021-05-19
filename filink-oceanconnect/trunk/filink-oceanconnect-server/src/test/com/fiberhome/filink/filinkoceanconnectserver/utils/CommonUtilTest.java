package com.fiberhome.filink.filinkoceanconnectserver.utils;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.bean.WebSocketMessage;
import com.fiberhome.filink.commonstation.constant.CmdId;
import com.fiberhome.filink.commonstation.constant.CmdType;
import com.fiberhome.filink.commonstation.constant.Constant;
import com.fiberhome.filink.commonstation.constant.WebSocketCode;
import com.fiberhome.filink.commonstation.entity.config.UnlockPushBean;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.entity.xmlbean.header.HeaderParam;
import com.fiberhome.filink.commonstation.utils.AliYunPushUtil;
import com.fiberhome.filink.deviceapi.api.DeviceLogFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceLog;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.filinkoceanconnectserver.stream.FiLinkKafkaSender;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.AliAccessKey;
import com.fiberhome.filink.redis.RedisUtils;
import io.netty.buffer.Unpooled;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * commonUtil测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class CommonUtilTest {

    @Tested
    private CommonUtil commonUtil;

    @Injectable
    private DeviceLogFeign deviceLogFeign;

    @Injectable
    private ControlFeign controlFeign;

    @Injectable
    private ParameterFeign parameterFeign;

    @Injectable
    private AliYunPushUtil pushUtil;

    @Injectable
    private FiLinkKafkaSender streamSender;

    @Test
    public void saveDeviceLog() {
        DeviceLog deviceLog = new DeviceLog();
        String logType = "event";
        String deviceLogName = "open lock";
        String type = "1";
        new Expectations() {
            {
                deviceLogFeign.saveDeviceLog(deviceLog);
            }
        };
        commonUtil.saveDeviceLog(deviceLog, logType, deviceLogName, type);
    }

    @Test
    public void setDeviceLogCommonInfo() {
        //control为空
        try {
            commonUtil.setDeviceLogCommonInfo(null);
        } catch (Exception e) {
        }
        //control不为空
        ControlParam controlParam = new ControlParam();
        controlParam.setDeviceId("deviceId");
        controlParam.setHostName("hostName");
        commonUtil.setDeviceLogCommonInfo(controlParam);
    }

    @Test
    public void refreshRedisTime() {
        String equipmentId = "equipmentId";
        ControlParam controlParam = new ControlParam();
        new MockUp<CommonUtil>() {
            @Mock
            public void refreshHeatBeatTime(String equipmentId, ControlParam control) {

            }
        };
        String sleepTime = RedisKey.SLEEP_TIME + Constant.SEPARATOR + equipmentId;
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(sleepTime, equipmentId, 20);
            }
        };
        commonUtil.refreshRedisTime(equipmentId, controlParam);
    }

    @Test
    public void refreshHeatBeatTime() {
        String equipmentId = "equipmentId";
        //configValue为空
        ControlParam controlParam = new ControlParam();
        try {
            commonUtil.refreshHeatBeatTime(equipmentId, controlParam);
        } catch (Exception e) {
        }

        //心跳周期为空
        String nullHeartConfig = "{\"exceptionHeartbeatCycle\":\"1440\",\"lowTemperature\":\"20\",\"lean\":\"10\",\"humidity\":\"80\",\"highTemperature\":\"70\",\"electricity\":\"60\",\"workTime\":\"240\"}";
        controlParam.setConfigValue(nullHeartConfig);
        try {
            commonUtil.refreshHeatBeatTime(equipmentId, controlParam);
        } catch (Exception e) {
        }

        //心跳周期不为空
        String heartConfig = "{\"exceptionHeartbeatCycle\":\"1440\",\"lowTemperature\":\"20\",\"heartbeatCycle\":\"1440\",\"lean\":\"10\",\"humidity\":\"80\",\"highTemperature\":\"70\",\"electricity\":\"60\",\"workTime\":\"240\"}";
        controlParam.setConfigValue(heartConfig);
        String offLineKey = RedisKey.OFF_LINE + Constant.SEPARATOR + equipmentId;
        String outOfConcatKey = RedisKey.OUT_OF_CONCAT + Constant.SEPARATOR + equipmentId;
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(offLineKey, equipmentId, 1440 * 60);
            }

            {
                RedisUtils.set(outOfConcatKey, equipmentId, 1440 * 60 * 2);
            }
        };
        commonUtil.refreshHeatBeatTime(equipmentId, controlParam);
    }

    @Test
    public void filterRepeatCmd() {
        //serialList为空
        String key = "010184976F67250A2DDA-123";
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, key);
                result = null;
            }

            {
                RedisUtils.hSet(RedisKey.DEVICE_SERIAL_NUMBER, key, any);
            }
        };
        commonUtil.filterRepeatCmd(key, 12);

        //serialList不为空,包含该流水号
        List<Integer> list = new ArrayList<>();
        list.add(12);
        new Expectations() {
            {
                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, key);
                result = list;
            }

            {
                RedisUtils.hSet(RedisKey.DEVICE_SERIAL_NUMBER, key, any);
            }
        };
        commonUtil.filterRepeatCmd(key, 12);

        //serialList不为空,不包含该流水号
        list.add(11);
        new Expectations() {
            {
                RedisUtils.hGet(RedisKey.DEVICE_SERIAL_NUMBER, key);
                result = list;
            }

            {
                RedisUtils.hSet(RedisKey.DEVICE_SERIAL_NUMBER, key, any);
            }
        };
        commonUtil.filterRepeatCmd(key, 5);


    }

    @Test
    public void getResponseParams() {
        ControlParam controlParam = new ControlParam();
        Map<String, Object> unlockParam = new HashMap<>(64);
        unlockParam.put("slotNum", "1");
        unlockParam.put("operate", "0");
        Map<String, Object> unlockMap = new HashMap<>(64);
        List<Map<String, Object>> paramList = new ArrayList<>();
        paramList.add(unlockParam);
        unlockMap.put("params", paramList);
        commonUtil.getResponseParams(controlParam, CmdId.UNLOCK, unlockMap, 1);
    }

    @Test
    public void unLockMessagePush() {
        //pushBean为空
        String equipmentId = "010184976F67250A2DDA";
        String key = "010184976F67250A2DDA-12";
        String socketMsg = "开锁成功";
        Result result = ResultUtils.success(WebSocketCode.NLOCKED_RESULT, null, socketMsg);
        UnlockPushBean pushBean = new UnlockPushBean();

        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.UNLOCK_PUSH, key);
                result = null;
            }
        };
        commonUtil.unLockMessagePush(equipmentId, socketMsg, key, result);

        //手机id不为空
        pushBean.setPhoneId("phoneId");
        AliAccessKey aliAccessKey = new AliAccessKey();
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.UNLOCK_PUSH, key);
                result = pushBean;
            }

            {
                RedisUtils.get(RedisKey.ALI_PUSH_KEY);
                result = null;
            }
        };
        new Expectations() {
            {
                parameterFeign.queryMobilePush();
                result = aliAccessKey;
            }
        };
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.set(RedisKey.ALI_PUSH_KEY, aliAccessKey);
            }

            {
                RedisUtils.hRemove(RedisKey.UNLOCK_PUSH, key);
            }
        };
        new Expectations() {
            {
                pushUtil.pushMsg(pushBean);
            }
        };
        commonUtil.unLockMessagePush(equipmentId, socketMsg, key, result);

        //token不为空
        pushBean.setPhoneId(null);
        pushBean.setToken("token");
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.UNLOCK_PUSH, key);
                result = pushBean;
            }

            {
                RedisUtils.hRemove(RedisKey.UNLOCK_PUSH, key);
            }
        };
        new Expectations() {
            {
                streamSender.sendWebSocket((WebSocketMessage) any);
            }
        };
        commonUtil.unLockMessagePush(equipmentId, socketMsg, key, result);
    }

    @Test
    public void clearRedisSerialNum() {
        String equipmentId = "equipmentId";
        String reqKey = equipmentId + Constant.SEPARATOR + CmdType.REQUEST_TYPE;
        //响应帧流水号
        String resKey = equipmentId + Constant.SEPARATOR + CmdType.RESPONSE_TYPE;
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hRemove(RedisKey.DEVICE_SERIAL_NUMBER, reqKey);
            }

            {
                RedisUtils.hRemove(RedisKey.DEVICE_SERIAL_NUMBER, resKey);
            }

            {
                RedisUtils.hRemove(RedisKey.DEVICE_SERIAL_NUMBER, equipmentId);
            }
        };
        commonUtil.clearRedisSerialNum(equipmentId);
    }

    @Test
    public void executeHeader() {
        HeaderParam headerParam = new HeaderParam();
        //headerParam为空
        try {
            commonUtil.executeHeader(headerParam, Unpooled.copiedBuffer("1564137561".getBytes()));
        } catch (Exception e) {
        }

        //headerParam不为空
        DataFormat dataFormat = new DataFormat();
        dataFormat.setClassName("com.fiberhome.filink.commonstation.handler.impl.NewDateDecoder");
        headerParam.setDataFormat(dataFormat);
        try {
            commonUtil.executeHeader(headerParam, Unpooled.copiedBuffer("1564137561".getBytes()));
        } catch (Exception e) {
        }
    }

    @Test
    public void execute() {
    }

    @Test
    public void getSerialNum() {
    }
}