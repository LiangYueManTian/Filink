package com.fiberhome.filink.onenetserver.expire;

import com.fiberhome.filink.commonstation.exception.ResponseException;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.onenetserver.stream.FiLinkKafkaSender;
import com.fiberhome.filink.redis.RedisUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@RunWith(JMockit.class)
public class DeviceStatusHandlerTest {

    /**测试对象 DeviceStatusHandler*/
    @Tested
    private DeviceStatusHandler deviceStatusHandler = new DeviceStatusHandler(new RedisMessageListenerContainer());
    /**Mock ControlFeign*/
    @Injectable
    private ControlFeign controlFeign;
    /**Mock FiLinkKafkaSender*/
    @Injectable
    private FiLinkKafkaSender streamSender;

    /**
     * onMessage
     */
    @Test
    public void onMessageTest() {
        deviceStatusHandler.onMessage(null, null);
        Message message = new Message() {
            @Override
            public byte[] getBody() {
                return new byte[0];
            }

            @Override
            public byte[] getChannel() {
                return new byte[0];
            }

            public String toString() {
                return "oneNetOffLine-01010093C20400000000";
            }
        };
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hRemove(anyString, anyString);

                RedisUtils.hSet(anyString, anyString, any);

                controlFeign.getControlParamById(anyString);
                result = null;
            }
        };
        try {
            deviceStatusHandler.onMessage(message, null);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), ResponseException.class);
        }
        ControlParam control = new ControlParam();
        control.setDeviceId("545454");
        control.setDeviceStatus("4");
        new Expectations(RedisUtils.class) {
            {
                controlFeign.getControlParamById(anyString);
                result = control;
            }
        };
        deviceStatusHandler.onMessage(message, null);
        Message messageTmp = new Message() {
            @Override
            public byte[] getBody() {
                return new byte[0];
            }

            @Override
            public byte[] getChannel() {
                return new byte[0];
            }

            public String toString() {
                return "oneNetOutOfConcat-0101D094C20400000000";
            }
        };
        deviceStatusHandler.onMessage(messageTmp, null);
    }

}
