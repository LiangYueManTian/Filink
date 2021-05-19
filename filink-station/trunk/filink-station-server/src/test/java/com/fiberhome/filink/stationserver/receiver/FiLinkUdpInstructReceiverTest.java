package com.fiberhome.filink.stationserver.receiver;

import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.stationserver.stream.FiLinkKafkaSender;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.InetSocketAddress;

/**
 * 指令接收测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class FiLinkUdpInstructReceiverTest {

    @Tested
    private FiLinkUdpInstructReceiver fiLinkUdpInstructReceiver;

    @Injectable
    private FiLinkKafkaSender fiLinkKafkaSender;

    @Test
    public void getDeviceMsg() {
        byte[] data = new byte[1024];
        InetSocketAddress socketAddress = new InetSocketAddress("10.5.24.12", 5683);
        DatagramPacket datagramPacket = new DatagramPacket(Unpooled.copiedBuffer(data), socketAddress);
        new Expectations(datagramPacket){
            {
                datagramPacket.sender();
                result = socketAddress;
            }
        };
        fiLinkUdpInstructReceiver.getDeviceMsg(null,datagramPacket);
    }

    @Test
    public void sendMsg() {
        DeviceMsg deviceMsg = new DeviceMsg();
        deviceMsg.setIp("10.5.24.12");
        deviceMsg.setPort(5683);
        deviceMsg.setEquipmentId("0101FFF04C0400000005");
        deviceMsg.setHexData("FFEF01019AC16BD40424A50A002F42474D50D3B100002205000100000000001D000000005D0FD3F8007F7F7F03621A3E59FF1B00000000000000000000");
        fiLinkUdpInstructReceiver.sendMsg(deviceMsg);
    }
}