package com.fiberhome.filink.stationserver.sender;

import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.constant.RedisKey;
import io.netty.channel.Channel;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * UdpChannel测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class UdpChannelTest {

    @Tested
    private UdpChannel udpChannel;

    @Injectable
    private Channel stationChannel;

    private String equipmentId;

    private String data;

    @Before
    public void setUp() {
        equipmentId = "0101FFF04C0400000005";
        data = "FFEF01019AC16BD40424A50A002F42474D50D3B100002205000100000000001D000000005D0FD3F8007F7F7F03621A3E59FF1B00000000000000000000";

    }

    @Test
    public void send() {
        //deviceMsg为空
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.DEVICE_KEY, equipmentId);
                result = null;
            }
        };
        try {
            udpChannel.send(equipmentId, data);
        } catch (Exception e) {
        }

        //deviceMsg不为空
        DeviceMsg deviceMsg = new DeviceMsg();
        deviceMsg.setIp("10.5.24.12");
        deviceMsg.setPort(5683);
        deviceMsg.setEquipmentId(equipmentId);
        deviceMsg.setHexData(data);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGet(RedisKey.DEVICE_KEY, equipmentId);
                result = deviceMsg;
            }
        };
        udpChannel.send(equipmentId, data);
    }
}