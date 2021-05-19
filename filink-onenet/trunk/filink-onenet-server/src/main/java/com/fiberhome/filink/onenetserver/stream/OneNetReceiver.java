package com.fiberhome.filink.onenetserver.stream;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.filinklockapi.bean.ControlDel;
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
    private FiLinkClearRedisControlAsync clearRedisControl;

    @Autowired
    private FtpUpgradeUtil ftpUpgradeUtil;

    /**
     * 清除redis主控信息
     *
     * @param controlDelJson 删除主控信息
     */
    @StreamListener(OneNetChannel.ONE_NET_CONTROL_CLEAR)
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
        clearRedisControl.clearRedisControl(controlDel);
    }

    /**
     * 消息监听处理
     *
     * @param deviceMsg DeviceMsg
     */
    @StreamListener(OneNetChannel.ONE_NET_INPUT)
    public void receive(DeviceMsg deviceMsg) throws Exception {
        msgReceiverAsync.receive(deviceMsg);
    }


    /**
     * oceanConnect请求接收
     *
     * @param fiLinkReqParamsDtoListJson 请求参数json
     */
    @StreamListener(OneNetChannel.ONE_NET_REQUEST_INPUT)
    public void sendOneNet(String fiLinkReqParamsDtoListJson) {
        requestReceiverAsync.sendOneNet(fiLinkReqParamsDtoListJson);
    }


    /**
     * 指令重发方法
     */
    @StreamListener(OneNetChannel.ONE_NET_WRITE_RESEND)
    public void cmdResendReceiver() {
        resendReceiverAsync.cmdResendReceiver();
    }


    /**
     * 消息监听处理
     *
     * @param msg msg
     */
    @StreamListener(OneNetChannel.UPGRADE_FILE_INPUT)
    public void upgradeFile(String msg) {
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        ftpUpgradeUtil.refreshUpgradeFile();
    }
}
