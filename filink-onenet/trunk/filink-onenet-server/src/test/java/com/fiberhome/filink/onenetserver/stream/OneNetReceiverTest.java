package com.fiberhome.filink.onenetserver.stream;

import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.onenetserver.utils.FtpUpgradeUtil;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class OneNetReceiverTest {

    /**测试对象 OneNetReceiver*/
    @Tested
    private OneNetReceiver oneNetReceiver;
    /**Mock FiLinkOneNetMsgReceiverAsync*/
    @Injectable
    private FiLinkOneNetMsgReceiverAsync msgReceiverAsync;
    /**Mock FiLinkOneNetRequestReceiverAsync*/
    @Injectable
    private FiLinkOneNetRequestReceiverAsync requestReceiverAsync;
    /**Mock FiLinkOneNetResendReceiverAsync*/
    @Injectable
    private FiLinkOneNetResendReceiverAsync resendReceiverAsync;
    /**Mock FiLinkClearRedisControlAsync*/
    @Injectable
    private FiLinkClearRedisControlAsync clearRedisControl;
    /**Mock FtpUpgradeUtil*/
    @Injectable
    private FtpUpgradeUtil ftpUpgradeUtil;

    /**
     * receive
     */
    @Test
    public void receiveTest() throws Exception {
        DeviceMsg deviceMsg = new DeviceMsg();
        oneNetReceiver.receive(deviceMsg);
    }

    /**
     * sendOneNet
     */
    @Test
    public void sendOneNetTest() {
        String fiLinkReqParamsDtoListJson = "[]";
        oneNetReceiver.sendOneNet(fiLinkReqParamsDtoListJson);
    }

    /**
     * cmdResendReceiver
     */
    @Test
    public void cmdResendReceiverTest() {
        oneNetReceiver.cmdResendReceiver();
    }

    /**
     * upgradeFile
     */
    @Test
    public void upgradeFileTest() {
        String msg = "";
        oneNetReceiver.upgradeFile(msg);
        msg = "askdj";
        oneNetReceiver.upgradeFile(msg);
    }
}
