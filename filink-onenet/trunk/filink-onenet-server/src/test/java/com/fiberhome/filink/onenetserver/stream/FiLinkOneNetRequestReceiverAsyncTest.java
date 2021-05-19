package com.fiberhome.filink.onenetserver.stream;

import com.fiberhome.filink.onenetserver.service.OneNetService;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class FiLinkOneNetRequestReceiverAsyncTest {
    /**测试对象 FiLinkOneNetRequestReceiverAsync*/
    @Tested
    private FiLinkOneNetRequestReceiverAsync requestReceiver;
    /**Mock OneNetService*/
    @Injectable
    private OneNetService oneNetService;

    /**
     * sendOneNet
     */
    @Test
    public void sendOneNetTest() {
        String fiLinkReqParamsDtoListJso = "ss";
        requestReceiver.sendOneNet(fiLinkReqParamsDtoListJso);
        fiLinkReqParamsDtoListJso = "[]";
        requestReceiver.sendOneNet(fiLinkReqParamsDtoListJso);
        fiLinkReqParamsDtoListJso = "[{\"cmdId\":\"0x2201\",\"equipmentId\":\"2445466\"},{}]";
        requestReceiver.sendOneNet(fiLinkReqParamsDtoListJso);
    }
}
