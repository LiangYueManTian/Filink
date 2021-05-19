package com.fiberhome.filink.stationserver.stream;

import com.fiberhome.filink.stationserver.entity.protocol.DeviceMsg;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkResOutputParams;
import com.fiberhome.filink.stationserver.receiver.MsgBusinessHandler;
import com.fiberhome.filink.stationserver.receiver.ResponseResolver;
import com.fiberhome.filink.stationserver.util.ProtocolUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * FiLinkUdpMsgReceiver测试类
 * @author CongcaiYu
 */
@RunWith(MockitoJUnitRunner.class)
public class FiLinkUdpMsgReceiverTest {

    /**
     * 测试对象FiLinkUdpMsgReceiver
     */
    @InjectMocks
    private FiLinkUdpMsgReceiver fiLinkUdpMsgReceiver;

    /**
     * 模拟Map<String, ResponseResolver>
     */
    @Mock
    private Map<String, ResponseResolver> responseHandlerMap;

    /**
     * 模拟ResponseResolver
     */
    @Mock
    private ResponseResolver responseResolver;

    /**
     * 模拟MsgBusinessHandler
     */
    @Mock
    private MsgBusinessHandler businessHandler;

    /**
     * 模拟ProtocolUtil
     */
    @Mock
    private ProtocolUtil protocolUtil;

    /**
     * 监听消息测试方法
     */
    @Test
    public void receive() throws Exception{
        //构造deviceMsg对象
        DeviceMsg deviceMsg = new DeviceMsg();
        deviceMsg.setIp("10.5.24.112");
        deviceMsg.setPort(8432);
        deviceMsg.setHexData(Mockito.anyString());
        //模拟responseHandlerMap.get(version)
        Mockito.when(responseHandlerMap.get("fiLinkUdpResponseResolver")).thenReturn(responseResolver);
        FiLinkResOutputParams fiLinkResOutputParams = new FiLinkResOutputParams();
        Mockito.when(responseResolver.resolveRes(Mockito.any())).thenReturn(fiLinkResOutputParams);
        fiLinkUdpMsgReceiver.receive(deviceMsg);

        //deviceId为空
        fiLinkResOutputParams.setDeviceId("13172750");
        Mockito.when(responseResolver.resolveRes(Mockito.any())).thenReturn(fiLinkResOutputParams);
        fiLinkUdpMsgReceiver.receive(deviceMsg);
    }
}