package com.fiberhome.filink.stationserver.stream;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.filinklockapi.bean.ControlDel;
import com.fiberhome.filink.stationserver.business.PerformanceTest;
import com.fiberhome.filink.stationserver.util.FtpUpgradeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * station消费处理
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class StationReceiver {

    @Autowired
    private FiLinkRequestReceiverAsync requestReceiverAsync;

    @Autowired
    private FiLinkResendReceiverAsync resendReceiverAsync;

    @Autowired
    private FiLinkUdpMsgReceiverAsync udpMsgReceiverAsync;

    @Autowired
    private FiLinkClearRedisControlAsync clearRedisControlAsync;

    @Autowired
    private FtpUpgradeUtil ftpUpgradeUtil;

    /**
     * udp请求消费者
     *
     * @param fiLinkReqParamsDtoListJson 请求参数json
     */
    @StreamListener(StationChannel.STATION_REQUEST_INPUT)
    public void sendUdp(String fiLinkReqParamsDtoListJson) {
        log.info("kafka udp request consuming>>>>>>>>>");
        requestReceiverAsync.sendUdp(fiLinkReqParamsDtoListJson);
    }


    /**
     * 指令重发方法
     */
    @StreamListener(StationChannel.STATION_CMD_RESEND)
    public void cmdResendReceiver() {
        log.info("kafka cmd resend consuming>>>>>>>>>");
        resendReceiverAsync.cmdResendReceiver();
    }


    /**
     * 消息监听处理
     *
     * @param deviceMsg DeviceMsg
     */
    @StreamListener(StationChannel.STATION_UDP_INPUT)
    public void receive(DeviceMsg deviceMsg) {
        log.info("kafka self msg consuming>>>>>>>>");
        udpMsgReceiverAsync.receive(deviceMsg);
    }

    /**
     * 消息监听处理
     *
     * @param msg msg
     */
    @StreamListener(StationChannel.UPGRADE_FILE_INPUT)
    public void upgradeFile(String msg) {
        log.info("kafka refresh upgrade file consuming>>>>>>>>");
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
    @StreamListener(StationChannel.STATION_CONTROL_CLEAR)
    public void clearRedisControl(String controlDelJson) {
        log.info("clear redis control info");
        if(StringUtils.isEmpty(controlDelJson)){
            log.error("controlDel is null");
            return;
        }
        //将主控id集合序列化成json
        ControlDel controlDel = JSONObject.parseObject(controlDelJson, ControlDel.class);
        if(controlDel == null
                || controlDel.getHostIdList() == null
                || controlDel.getHostIdList().size() == 0
                || StringUtils.isEmpty(controlDel.getOperate())){
            log.info("control del info is null");
            return;
        }
        clearRedisControlAsync.clearRedisControl(controlDel);
    }
}
