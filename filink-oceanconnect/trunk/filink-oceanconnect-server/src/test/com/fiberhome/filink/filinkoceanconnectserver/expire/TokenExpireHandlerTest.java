package com.fiberhome.filink.filinkoceanconnectserver.expire;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
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
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * 离线,失联处理测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class TokenExpireHandlerTest {

    @Tested
    private TokenExpireHandler tokenExpireHandler;

    @Injectable
    private AuthClient authClient;

    @Injectable
    private ControlFeign controlFeign;

    @Injectable
    private ProtocolUtil protocolUtil;

    @Injectable
    private FiLinkKafkaSender streamSender;

    @Injectable
    private CommonUtil commonUtil;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    @Injectable
    private RedisMessageListenerContainer redisMessageListenerContainer;

    private byte[] byteArray = new byte[1024];

    private ControlParam controlParam;

    private Result feignResult;

    @Before
    public void setUp() {
        new Expectations() {
            {
                new TokenExpireHandler(redisMessageListenerContainer);
                result = tokenExpireHandler;
            }
        };
    }

    @Test
    public void onMessage() {
        //message为空
        tokenExpireHandler.onMessage(null, byteArray);

        //appId token失效
        String expireTokenKey = RedisKey.APP_TOKEN_PREFIX + "-342341213213";
        DefaultMessage expireToken = new DefaultMessage(new byte[1024], expireTokenKey.getBytes());
        tokenExpireHandler.onMessage(expireToken, byteArray);

        //离线
        String offLineKey = RedisKey.OFF_LINE + "-010195CDD73784BFC85C";
        controlParam = new ControlParam();
        controlParam.setHostId("010195CDD73784BFC85C");
        controlParam.setDeviceStatus(DeviceStatus.Offline.getCode());
        DefaultMessage offLine = new DefaultMessage(new byte[1024], offLineKey.getBytes());
        new Expectations(commonUtil) {
            {
                commonUtil.clearRedisSerialNum(anyString);
            }
        };
        feignResult = ResultUtils.success();
        new Expectations() {
            {
                controlFeign.getControlParamById(anyString);
                result = controlParam;
            }

            {
                controlFeign.updateDeviceStatusById((ControlParam) any);
                result = feignResult;
            }
        };
        new Expectations(RedisUtils.class) {
            {
                controlFeign.getControlParamById(anyString);
                result = controlParam;
            }

            {
                RedisUtils.hSet(RedisKey.CONTROL_INFO, anyString, controlParam);
            }
        };
        tokenExpireHandler.onMessage(offLine, byteArray);

        //失联
        String outOfConcatKey = RedisKey.OUT_OF_CONCAT + "-010195CDD73784BFC85C";
        controlParam = new ControlParam();
        controlParam.setHostId("010195CDD73784BFC85C");
        controlParam.setDeviceStatus(DeviceStatus.Out_Contact.getCode());
        DefaultMessage outOfConcat = new DefaultMessage(new byte[1024], outOfConcatKey.getBytes());
        new Expectations(commonUtil) {
            {
                commonUtil.clearRedisSerialNum(anyString);
            }
        };
        feignResult = ResultUtils.success();
        new Expectations() {
            {
                controlFeign.getControlParamById(anyString);
                result = controlParam;
            }
        };
        new Expectations(RedisUtils.class) {
            {
                controlFeign.getControlParamById(anyString);
                result = controlParam;
            }
        };
        tokenExpireHandler.onMessage(outOfConcat, byteArray);

        //休眠
        String sleepKey = RedisKey.SLEEP_TIME + "-010195CDD73784BFC85C";
        controlParam = new ControlParam();
        controlParam.setHostId("010195CDD73784BFC85C");
        controlParam.setDeviceStatus(DeviceStatus.Out_Contact.getCode());
        DefaultMessage sleep = new DefaultMessage(new byte[1024], sleepKey.getBytes());
        new Expectations(commonUtil) {
            {
                commonUtil.clearRedisSerialNum(anyString);
            }
        };
        new Expectations(){
            {
                controlFeign.updateControlParam((ControlParam) any);
                result = feignResult;
            }
            {
                protocolUtil.getControlById(anyString);
                result = controlParam;
            }
        };
        new Expectations(ResultUtils.class){
            {
                RedisUtils.hSet(RedisKey.CONTROL_INFO, anyString, any);
            }
        };
        tokenExpireHandler.onMessage(sleep, byteArray);
    }
}