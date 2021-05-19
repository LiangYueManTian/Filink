package com.fiberhome.filink.stationserver.stream;

import com.fiberhome.filink.stationserver.entity.protocol.DeviceMsg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.messaging.MessageChannel;

import static org.junit.Assert.*;

/**
 * FiLinkUdpMsgSender测试类
 * @author CongcaiYu
 */
@RunWith(MockitoJUnitRunner.class)
public class FiLinkUdpMsgSenderTest {

    /**
     * 测试对象FiLinkUdpMsgSender
     */
    @InjectMocks
    private FiLinkUdpMsgSender fiLinkUdpMsgSender;

    /**
     * 模拟stationChannel
     */
    @Mock
    private StationChannel stationChannel;

    @Mock
    private MessageChannel messageChannel;

    /**
     * 发送
     */
    @Test
    public void send() {
        //模拟stationChannel
        Mockito.when(stationChannel.output()).thenReturn(messageChannel);
        fiLinkUdpMsgSender.send(new DeviceMsg());
    }
}