package com.fiberhome.filink.stationserver.receiver;

import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.commonstation.receiver.AbstractUdpInstructReceiver;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.stationserver.business.PerformanceTest;
import com.fiberhome.filink.stationserver.stream.FiLinkKafkaSender;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * udp响应处理
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class FiLinkUdpInstructReceiver extends AbstractUdpInstructReceiver {

    @Autowired
    private FiLinkKafkaSender fiLinkKafkaSender;


    /**
     * 获取设施数据包
     *
     * @param ctx    ChannelHandlerContext
     * @param packet DatagramPacket
     * @return DeviceMsg
     */
    @Override
    protected DeviceMsg getDeviceMsg(ChannelHandlerContext ctx, DatagramPacket packet) {
        ByteBuf content = packet.content();
        InetSocketAddress sender = packet.sender();
        String ip = sender.getAddress().getHostAddress();
        int port = sender.getPort();
        //ip 端口
        //log.info("hostname: {}", ip);
        //log.info("port: {}", port);
        byte[] byteArray = new byte[content.readableBytes()];
        content.readBytes(byteArray);
        String data = HexUtil.bytesToHexString(byteArray);
        log.info("receive msg : {}", data);
        DeviceMsg deviceMsg = new DeviceMsg();
        deviceMsg.setIp(ip);
        deviceMsg.setPort(port);
        deviceMsg.setHexData(data);
        //todo 告警测试
        if (data.contains("00002208")) {
            PerformanceTest.atomicInteger.incrementAndGet();
            //todo 告警转工单测试
            PerformanceTest.setAlarmTime(data.substring(4, 24), "recvAlarmCmdTime", System.currentTimeMillis());
        }
        return deviceMsg;
    }

    /**
     * 发送消息到kafka
     *
     * @param deviceMsg DeviceMsg
     */
    @Override
    protected void sendMsg(DeviceMsg deviceMsg) {
        //发送消息到kafka
        fiLinkKafkaSender.sendUdp(deviceMsg);
    }

}