package com.fiberhome.filink.stationserver.expire;

import com.fiberhome.filink.deviceapi.util.DeviceStatus;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.constant.RedisKey;
import com.fiberhome.filink.stationserver.stream.FiLinkKafkaSender;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * 设施状态过期处理测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class DeviceStatusHandlerTest {

    private byte[] byteArray = new byte[1024];

    private ControlParam controlParam;

    @Injectable
    private ControlFeign controlFeign;

    @Injectable
    private FiLinkKafkaSender streamSender;

    @Injectable
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @Mocked
    private RedisUtils redisUtils;

    @Tested
    private DeviceStatusHandler deviceStatusHandler;

    @Before
    public void setUp() {
        new Expectations() {
            {
                new DeviceStatusHandler(redisMessageListenerContainer);
                result = deviceStatusHandler;
            }
        };
    }


    @Test
    public void onMessage() {
        //message为空
        deviceStatusHandler.onMessage(null, byteArray);

        //离线
        String offLineKey = "offLine-010195CDD73784BFC85C";
        controlParam = new ControlParam();
        controlParam.setDeviceStatus(DeviceStatus.Offline.getCode());
        DefaultMessage offLine = new DefaultMessage(new byte[1024], offLineKey.getBytes());
        new Expectations() {
            {
                controlFeign.getControlParamById(anyString);
                result = controlParam;
            }

            {
                RedisUtils.hSet(RedisKey.CONTROL_INFO, anyString, controlParam);
            }
        };
        deviceStatusHandler.onMessage(offLine, byteArray);

        //失联
        String outOfConcatKey = "outOfConcat-010195CDD73784BFC85C";
        controlParam = new ControlParam();
        controlParam.setDeviceStatus(DeviceStatus.Out_Contact.getCode());
        DefaultMessage outOfConcat = new DefaultMessage(new byte[1024], outOfConcatKey.getBytes());
        new Expectations() {
            {
                controlFeign.getControlParamById(anyString);
                result = controlParam;
            }
        };
        deviceStatusHandler.onMessage(outOfConcat, byteArray);
    }
}