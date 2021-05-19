package com.fiberhome.filink.filinkoceanconnectserver.stream;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.filinklockapi.bean.ControlDel;
import com.fiberhome.filink.filinkoceanconnectserver.utils.FtpUpgradeUtil;
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
public class OceanConnectReceiver {

    @Autowired
    private FiLinkOceanConnectMsgReceiverAsync msgReceiverAsync;

    @Autowired
    private FiLinkRequestReceiverAsync requestReceiverAsync;

    @Autowired
    private FiLinkResendReceiverAsync resendReceiverAsync;

    @Autowired
    private FtpUpgradeUtil ftpUpgradeUtil;

    @Autowired
    private FiLinkClearRedisControlAsync clearRedisControlAsync;

    /**
     * 消息监听处理
     *
     * @param deviceMsgObj DeviceMsg
     */
    @StreamListener(OceanConnectChannel.OCEAN_CONNECT_INPUT)
    public void receive(String deviceMsgObj) {
        msgReceiverAsync.receive(deviceMsgObj);
    }


    /**
     * oceanConnect请求接收
     *
     * @param fiLinkReqParamsDtoListJson 请求参数json
     */
    @StreamListener(OceanConnectChannel.OCEAN_REQUEST_INPUT)
    public void sendOceanConnect(String fiLinkReqParamsDtoListJson) {
        requestReceiverAsync.sendOceanConnect(fiLinkReqParamsDtoListJson);
    }


    /**
     * 指令重发方法
     */
    @StreamListener(OceanConnectChannel.OCEAN_CMD_RESEND)
    public void cmdResendReceiver() {
        resendReceiverAsync.cmdResendReceiver();
    }

    /**
     * 消息监听处理
     *
     * @param msg msg
     */
    @StreamListener(OceanConnectChannel.UPGRADE_FILE_INPUT)
    public void upgradeFile(String msg) {
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        ftpUpgradeUtil.refreshUpgradeFile();
    }

    /**
     * 清除redis主控信息
     *
     * @param controlDelJson 删除主控信息
     */
    @StreamListener(OceanConnectChannel.OCEAN_CONTROL_CLEAR)
    public void clearRedisControl(String controlDelJson) {
        log.info("clear redis control info");
        if (StringUtils.isEmpty(controlDelJson)) {
            log.error("controlDel is null");
            return;
        }
        //将主控id集合序列化成json
        ControlDel controlDel = JSONObject.parseObject(controlDelJson, ControlDel.class);
        if (controlDel == null
                || controlDel.getHostIdList() == null
                || controlDel.getHostIdList().size() == 0
                || StringUtils.isEmpty(controlDel.getOperate())) {
            log.info("control del info is null");
            return;
        }
        clearRedisControlAsync.clearRedisControl(controlDel);
    }
}
