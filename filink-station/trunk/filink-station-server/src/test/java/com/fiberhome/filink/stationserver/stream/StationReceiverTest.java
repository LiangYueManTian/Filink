package com.fiberhome.filink.stationserver.stream;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.filinklockapi.bean.ControlDel;
import com.fiberhome.filink.filinklockapi.constant.OperateState;
import com.fiberhome.filink.stationserver.util.FtpUpgradeUtil;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * stationReceiver测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class StationReceiverTest {

    @Tested
    private StationReceiver stationReceiver;

    @Injectable
    private FiLinkRequestReceiverAsync requestReceiverAsync;

    @Injectable
    private FiLinkResendReceiverAsync resendReceiverAsync;

    @Injectable
    private FiLinkUdpMsgReceiverAsync udpMsgReceiverAsync;

    @Injectable
    private FiLinkClearRedisControlAsync clearRedisControlAsync;

    @Injectable
    private FtpUpgradeUtil ftpUpgradeUtil;

    @Test
    public void sendUdp() {
        String fiLinkReqParamsDtoListJson = "fiLinkReqParamsDtoListJson";
        stationReceiver.sendUdp(fiLinkReqParamsDtoListJson);
    }

    @Test
    public void cmdResendReceiver() {
        stationReceiver.cmdResendReceiver();
    }

    @Test
    public void receive() {
        DeviceMsg deviceMsg = new DeviceMsg();
        deviceMsg.setHexData("FFEF01011EBFDD6E5C118366002842474D50000D00002201000200000000001600000001000000000001000100000000000000000000");
        stationReceiver.receive(deviceMsg);
    }

    @Test
    public void upgradeFile() {
        //msg不为空
        stationReceiver.upgradeFile("msg");
        //msg为空
        stationReceiver.upgradeFile("");
    }

    @Test
    public void clearRedisControl() {
        //control json为空
        stationReceiver.clearRedisControl("");
        //control 对象为空
        ControlDel nullControlDel = new ControlDel();
        stationReceiver.clearRedisControl(JSONObject.toJSONString(nullControlDel));
        //control json不为空
        ControlDel controlDel = new ControlDel();
        controlDel.setOperate(OperateState.UPDATE_STATE);
        List<String> hostIdList = new ArrayList<>();
        hostIdList.add("0101CFED4C0400000000");
        controlDel.setHostIdList(hostIdList);
        stationReceiver.clearRedisControl(JSONObject.toJSONString(controlDel));
    }
}