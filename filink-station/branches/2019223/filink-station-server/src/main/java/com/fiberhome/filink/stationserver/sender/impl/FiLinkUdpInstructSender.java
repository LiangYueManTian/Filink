package com.fiberhome.filink.stationserver.sender.impl;

import com.fiberhome.filink.protocol.bean.xmlBean.AbstractProtocolBean;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.entity.param.AbstractReqParams;
import com.fiberhome.filink.stationserver.entity.protocol.DeviceMsg;
import com.fiberhome.filink.stationserver.exception.UdpSendException;
import com.fiberhome.filink.stationserver.sender.AbstractInstructSender;
import com.fiberhome.filink.stationserver.sender.RequestResolver;
import com.fiberhome.filink.stationserver.util.HexUtil;
import com.fiberhome.filink.stationserver.util.ProtocolUtil;
import com.fiberhome.filink.stationserver.util.lockenum.RedisKey;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * filink udp指令下发
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class FiLinkUdpInstructSender extends AbstractInstructSender {

    @Autowired
    private Map<String, RequestResolver> requestHandlerMap;

    @Autowired
    private ProtocolUtil protocolUtil;

    @Resource(name = "myChannel")
    private Channel channel;

    /**
     * 发送指令
     *
     * @param deviceId 设施id
     * @param data     16进制命令帧
     */
    @Override
    protected void send(String deviceId, String data) {
        try {
            DeviceMsg deviceMsg = (DeviceMsg) RedisUtils.hGet(RedisKey.DEVICE_KEY,deviceId);
            if (deviceMsg == null) {
                log.info("deviceMsg of redis is null>>>>>>>>");
                throw new UdpSendException("deviceMsg of redis is null");
            }
            String ip = deviceMsg.getIp();
            Integer port = deviceMsg.getPort();
            //发送指令
            channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(HexUtil.hexStringToByte(data)),
                    new InetSocketAddress(ip, port))).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成请求帧
     *
     * @param abstractReqParams 请求参数
     * @return 16进制请求帧
     */
    @Override
    protected String getReqData(AbstractReqParams abstractReqParams) {
        String serialNum = abstractReqParams.getDeviceId();
        AbstractProtocolBean protocolBean = protocolUtil.getProtocolBeanBySerialNum(serialNum);
        abstractReqParams.setProtocolBean(protocolBean);
        //todo 从xml中获取处理类名称
        String version = "fiLinkUdpRequestHandler";
        RequestResolver requestResolver = requestHandlerMap.get(version);
        return requestResolver.resolveUdpReq(abstractReqParams);
    }

}
