package com.fiberhome.filink.onenetserver.stream;

import com.fiberhome.filink.commonstation.business.MsgBusinessHandler;
import com.fiberhome.filink.commonstation.entity.param.AbstractResOutputParams;
import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.commonstation.entity.xmlbean.FiLinkProtocolBean;
import com.fiberhome.filink.commonstation.exception.ResponseException;
import com.fiberhome.filink.commonstation.receiver.ResponseResolver;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.onenetserver.bean.protocol.FiLinkOneNetInputParams;
import com.fiberhome.filink.onenetserver.bean.protocol.FiLinkOneNetOutputParams;
import com.fiberhome.filink.onenetserver.business.FiLinkNewMsgBusinessHandler;
import com.fiberhome.filink.onenetserver.receiver.FiLinkOneNetResponseResolver;
import com.fiberhome.filink.onenetserver.utils.ProtocolUtil;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class FiLinkOneNetMsgReceiverAsyncTest {
    /**测试对象 FiLinkOneNetMsgReceiverAsync*/
    @Tested
    private FiLinkOneNetMsgReceiverAsync msgReceiver;
    /**Mock responseHandlerMap*/
    @Injectable
    private Map<String, ResponseResolver> responseHandlerMap;
    /**Mock businessHandlerMap*/
    @Injectable
    private Map<String, MsgBusinessHandler> businessHandlerMap;
    /**Mock ProtocolUtil*/
    @Injectable
    private ProtocolUtil protocolUtil;
    @Injectable
    private FiLinkOneNetResponseResolver responseResolver;
    @Injectable
    private FiLinkNewMsgBusinessHandler businessHandler;
    /**
     * receive
     */
    @Test
    public void receiveTest() throws Exception {
        DeviceMsg deviceMsg = new DeviceMsg();
        deviceMsg.setHexData("FHAOHIDIUHDKHKUQHDKHJAHDKUQHDUKAHKUJ");
        ControlParam control = new ControlParam();
        FiLinkProtocolBean protocolBean = new FiLinkProtocolBean();
        protocolBean.setResponseResolverName("fiLinkResponseResolver");
        protocolBean.setBusinessHandlerName("fiLinkNewBusinessHandler");
        new Expectations() {
            {
                protocolUtil.getControlById(anyString);
                result = control;

                protocolUtil.getProtocolBeanByControl(control);
                result = protocolBean;

                responseHandlerMap.get(anyString);
                result = null;
            }
        };
        try {
            msgReceiver.receive(deviceMsg);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), ResponseException.class);
        }
        AbstractResOutputParams resOutputParams = new FiLinkOneNetOutputParams();
        new Expectations() {
            {
                responseHandlerMap.get(anyString);
                result = responseResolver;

                responseResolver.resolveRes((FiLinkOneNetInputParams) any);
                result = resOutputParams;

                businessHandlerMap.get(anyString);
                result = null;
            }
        };
        try {
            msgReceiver.receive(deviceMsg);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), ResponseException.class);
        }
        new Expectations() {
            {
                businessHandlerMap.get(anyString);
                result = businessHandler;
            }
        };
        msgReceiver.receive(deviceMsg);
    }
}
