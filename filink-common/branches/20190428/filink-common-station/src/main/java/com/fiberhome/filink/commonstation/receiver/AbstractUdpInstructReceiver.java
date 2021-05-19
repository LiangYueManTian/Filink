package com.fiberhome.filink.commonstation.receiver;

import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;


/**
 * udp接收
 * @author CongcaiYu
 */
public abstract class AbstractUdpInstructReceiver extends SimpleChannelInboundHandler<DatagramPacket> {

    /**
     * udp监听处理
     *
     * @param ctx ChannelHandlerContext
     * @param packet DatagramPacket
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
        DeviceMsg deviceMsg = this.getDeviceMsg(ctx, packet);
        this.sendMsg(deviceMsg);
    }

    /**
     * 获取deviceMsg
     *
     * @param ctx ChannelHandlerContext
     * @param packet DatagramPacket
     * @return DeviceMsg
     */
    abstract protected DeviceMsg getDeviceMsg(ChannelHandlerContext ctx, DatagramPacket packet);

    /**
     * 发送消息
     *
     * @param deviceMsg DeviceMsg
     */
    abstract protected void sendMsg(DeviceMsg deviceMsg);

}
