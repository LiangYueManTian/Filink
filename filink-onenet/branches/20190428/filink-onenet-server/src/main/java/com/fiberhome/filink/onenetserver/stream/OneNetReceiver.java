package com.fiberhome.filink.onenetserver.stream;

import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.onenetserver.utils.FtpUpgradeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * oceanConnect消费者
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class OneNetReceiver {

    @Autowired
    private FiLinkOneNetMsgReceiverAsync msgReceiverAsync;

    @Autowired
    private FiLinkOneNetRequestReceiverAsync requestReceiverAsync;

    @Autowired
    private FiLinkOneNetResendReceiverAsync resendReceiverAsync;
    @Autowired
    private FtpUpgradeUtil ftpUpgradeUtil;
    /**
     * 消息监听处理
     *
     * @param deviceMsgObj DeviceMsg
     */
    @StreamListener(OneNetChannel.ONE_NET_INPUT)
    public void receive(DeviceMsg deviceMsg) throws Exception {
        log.info("kafka self msg consuming>>>>>>>>>>");
        msgReceiverAsync.receive(deviceMsg);
    }


    /**
     * oceanConnect请求接收
     *
     * @param fiLinkReqParamsDtoListJson 请求参数json
     */
    @StreamListener(OneNetChannel.ONE_NET_REQUEST_INPUT)
    public void sendOceanConnect(String fiLinkReqParamsDtoListJson) {
        log.info("kafka request consuming>>>>>>>>");
        requestReceiverAsync.sendOceanConnect(fiLinkReqParamsDtoListJson);
    }


    /**
     * 指令重发方法
     */
    @StreamListener(OneNetChannel.ONE_NET_WRITE_RESEND)
    public void cmdResendReceiver() {
        log.info("kafka cmd resend consuming>>>>>>>>>>");
        resendReceiverAsync.cmdResendReceiver();
    }

    /**
     * 消息监听处理
     *
     * @param msg msg
     */
    @StreamListener(OneNetChannel.UPGRADE_FILE_INPUT)
    public void upgradeFile(String msg) {
        log.info("kafka refresh upgrade file consuming>>>>>>>>");
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        ftpUpgradeUtil.refreshUpgradeFile();
    }
}
