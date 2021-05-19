package com.fiberhome.filink.onenetserver.stream;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.onenetserver.utils.FtpUpgradeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import java.util.List;

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
    private FiLinkClearRedisControlAsync clearRedisControl;

    @Autowired
    private FtpUpgradeUtil ftpUpgradeUtil;

    /**
     * 清除redis主控信息
     *
     * @param equipmentIdListJson 主控id集合
     */
    @StreamListener(OneNetChannel.ONE_NET_CONTROL_CLEAR)
    public void clearRedisControl(String equipmentIdListJson) {
        log.info("clear redis control info");
        if(StringUtils.isEmpty(equipmentIdListJson)){
            log.error("equipment id list is null");
            return;
        }
        //将主控id集合序列化成json
        List<String> equipmentIdList = JSONObject.parseArray(equipmentIdListJson, String.class);
        clearRedisControl.clearRedisControl(equipmentIdList);
    }

    /**
     * 消息监听处理
     *
     * @param deviceMsg DeviceMsg
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
    public void sendOneNet(String fiLinkReqParamsDtoListJson) {
        log.info("kafka request consuming>>>>>>>>");
        requestReceiverAsync.sendOneNet(fiLinkReqParamsDtoListJson);
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
