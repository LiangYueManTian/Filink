package com.fiberhome.filink.filinkoceanconnectserver.stream;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.commonstation.business.MsgBusinessHandler;
import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.commonstation.entity.xmlbean.FiLinkProtocolBean;
import com.fiberhome.filink.commonstation.receiver.ResponseResolver;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkOceanResInputParams;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkOceanResOutputParams;
import com.fiberhome.filink.filinkoceanconnectserver.receiver.FiLinkOceanConnectResponseResolver;
import com.fiberhome.filink.filinkoceanconnectserver.utils.ProtocolUtil;
import com.fiberhome.filink.redis.RedisUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

/**
 * FiLinkOceanConnectMsgReceiverAsync测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class FiLinkOceanConnectMsgReceiverAsyncTest {

    private DeviceMsg deviceMsg;

    private FiLinkProtocolBean protocolBean;

    private FiLinkOceanResOutputParams outputParams;

    @Tested
    private FiLinkOceanConnectMsgReceiverAsync fiLinkOceanConnectMsgReceiverAsync;

    @Injectable
    private ProtocolUtil protocolUtil;

    @Injectable
    private Map<String, ResponseResolver> responseHandlerMap;

    @Injectable
    public Map<String, MsgBusinessHandler> businessHandlerMap;

    @Injectable
    private FiLinkOceanConnectResponseResolver responseResolver;

    @Injectable
    private MsgBusinessHandler businessHandler;

    @Mocked
    private RedisUtils redisUtils;

    @Before
    public void setup() {
        deviceMsg = new DeviceMsg();
        deviceMsg.setEquipmentId("01019AC16BD40424A50A");
        deviceMsg.setIp("10.5.1.112");
        deviceMsg.setPort(2341);
        deviceMsg.setHexData("FFEF01019AC16BD40424A50A003142474D50303500002208000100000000001F000000005D107D4900000001000039030000047F7F7F000000000000000000");
        protocolBean = new FiLinkProtocolBean();
        protocolBean.setResponseResolverName("udpResponseResolver");
        protocolBean.setBusinessHandlerName("udpBusinessHandler");
        outputParams = new FiLinkOceanResOutputParams();
        outputParams.setEquipmentId("01019AC16BD40424A50A");
    }

    @Test
    public void receive() {
        new Expectations() {
            {
                protocolUtil.getProtocolBeanByControl((ControlParam) any);
                result = protocolBean;
            }

            {
                responseHandlerMap.get(anyString);
                result = responseResolver;
            }

            {
                responseResolver.resolveRes((FiLinkOceanResInputParams) any);
                result = outputParams;
            }

        };
        fiLinkOceanConnectMsgReceiverAsync.receive(JSONObject.toJSONString(deviceMsg));

        //responseResolverName为空
        protocolBean.setResponseResolverName(null);
        new Expectations() {
            {
                protocolUtil.getProtocolBeanByControl((ControlParam) any);
                result = protocolBean;
            }

            {
                responseHandlerMap.get(anyString);
                result = null;
            }
        };
        try {
            fiLinkOceanConnectMsgReceiverAsync.receive(JSONObject.toJSONString(deviceMsg));
        } catch (Exception e) {

        }

        //deviceId为空
        outputParams.setEquipmentId(null);
        protocolBean.setResponseResolverName("udpResponseResolver");
        new Expectations() {
            {
                protocolUtil.getProtocolBeanByControl((ControlParam) any);
                result = protocolBean;
            }

            {
                responseHandlerMap.get(anyString);
                result = null;
            }

            {
                responseResolver.resolveRes((FiLinkOceanResInputParams) any);
                result = outputParams;
            }

            {
                responseHandlerMap.get(anyString);
                result = responseResolver;
            }
        };
        try {
            fiLinkOceanConnectMsgReceiverAsync.receive(JSONObject.toJSONString(deviceMsg));
        } catch (Exception e) {

        }

        //businessHandler为空
        protocolBean.setResponseResolverName("udpResponseResolver");
        new Expectations() {
            {
                protocolUtil.getProtocolBeanByControl((ControlParam) any);
                result = protocolBean;
            }

            {
                responseHandlerMap.get(anyString);
                result = null;
            }

            {
                responseResolver.resolveRes((FiLinkOceanResInputParams) any);
                result = outputParams;
            }

            {
                responseHandlerMap.get(anyString);
                result = responseResolver;
            }

            {
                businessHandlerMap.get(anyString);
                result = null;
            }
        };
        try {
            fiLinkOceanConnectMsgReceiverAsync.receive(JSONObject.toJSONString(deviceMsg));
        } catch (Exception e) {

        }
    }
}