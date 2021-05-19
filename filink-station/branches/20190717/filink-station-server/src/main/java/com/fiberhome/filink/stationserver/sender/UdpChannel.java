package com.fiberhome.filink.stationserver.sender;

import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.exception.UdpSendException;
import com.fiberhome.filink.stationserver.constant.RedisKey;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

/**
 * udp通道
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class UdpChannel {

    @Resource(name = "stationChannel")
    private Channel channel;

    /**
     * 发送udp数据方法
     *
     * @param equipmentId 主控id
     * @param data        16进制数据
     */
    public void send(String equipmentId, String data) {
        try {
            log.info("udp channel send data : {}", data);
            DeviceMsg deviceMsg = (DeviceMsg) RedisUtils.hGet(RedisKey.DEVICE_KEY, equipmentId);
            if (deviceMsg == null) {
                log.info("deviceMsg of redis is null");
                throw new UdpSendException("deviceMsg of redis is null");
            }
            String ip = deviceMsg.getIp();
            Integer port = deviceMsg.getPort();
            //发送指令
            channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(HexUtil.hexStringToByte(data)),
                    new InetSocketAddress(ip, port))).sync();
        } catch (InterruptedException e) {
            log.error("udp channel send failed: {}",e);
        }
    }
}
