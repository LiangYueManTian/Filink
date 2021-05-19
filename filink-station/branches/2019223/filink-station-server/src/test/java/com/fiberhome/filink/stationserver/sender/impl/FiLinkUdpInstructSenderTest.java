package com.fiberhome.filink.stationserver.sender.impl;

import com.fiberhome.filink.protocol.bean.xmlBean.FiLinkProtocolBean;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.entity.protocol.DeviceMsg;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkReqParams;
import com.fiberhome.filink.stationserver.sender.RequestResolver;
import com.fiberhome.filink.stationserver.util.ProtocolUtil;
import com.fiberhome.filink.stationserver.util.lockenum.CmdId;
import com.fiberhome.filink.stationserver.util.lockenum.CmdType;
import com.fiberhome.filink.stationserver.util.lockenum.RedisKey;
import io.netty.channel.Channel;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * FiLinkUdpInstructSender测试类
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class FiLinkUdpInstructSenderTest {

    private DeviceMsg deviceMsg;

    private FiLinkReqParams fiLinkReqParams;

    /**
     * 测试对象
     */
    @Tested
    private FiLinkUdpInstructSender fiLinkUdpInstructSender;

    /**
     * 模拟Channel对象
     */
    @Injectable(value = "myChannel")
    private Channel myChannel;

    /**
     * 模拟requestHandlerMap
     */
    @Injectable
    private Map<String, RequestResolver> requestHandlerMap;

    /**
     * 模拟requestResolver
     */
    @Injectable
    private RequestResolver requestResolver;

    /**
     * 模拟protocolUtil
     */
    @Injectable
    private ProtocolUtil protocolUtil;

    /**
     * 初始化deviceMsg
     */
    @Before
    public void setUp(){
        deviceMsg = new DeviceMsg();
        deviceMsg.setIp("10.5.24.123");
        deviceMsg.setPort(5645);
        fiLinkReqParams = new FiLinkReqParams();
        fiLinkReqParams.setCmdType(CmdType.REQUEST_TYPE);
        fiLinkReqParams.setCmdId(CmdId.UNLOCK);
    }

    /**
     * 发送消息测试方法
     */
    @Test
    public void send() {
        new Expectations(RedisUtils.class){
            {
               RedisUtils.hGet(RedisKey.DEVICE_KEY,anyString);
               result = deviceMsg;
            }
        };
        String data = "FFEF0101F325C8BBD1452A9A007842474D5000000003000022080001000000000064000000000000000500003902000EF05701000200180000000000003906000EF05701000200290000000000003909000EF05701000200220000000000002005000EF05700000200170000000000002006000EF0570000020000000000000000000000000000";
        fiLinkUdpInstructSender.send("13172750",data);
    }

    /**
     * 获取请求帧数据测试方法
     */
    @Test
    public void getReqData() {
        new Expectations(){
            {
                FiLinkProtocolBean fiLinkProtocolBean = new FiLinkProtocolBean();
                protocolUtil.getProtocolBeanBySerialNum(anyString);
                result = fiLinkProtocolBean;
            }
            {
                requestHandlerMap.get(anyString);
                result = requestResolver;
            }
            {
                String data = "FFEF0101F325C8BBD1452A9A007842474D5000000003000022080001000000000064000000000000000500003902000EF05701000200180000000000003906000EF05701000200290000000000003909000EF05701000200220000000000002005000EF05700000200170000000000002006000EF0570000020000000000000000000000000000";
                requestResolver.resolveUdpReq(fiLinkReqParams);
                result = data;
            }
        };
        fiLinkUdpInstructSender.getReqData(fiLinkReqParams);
    }
}