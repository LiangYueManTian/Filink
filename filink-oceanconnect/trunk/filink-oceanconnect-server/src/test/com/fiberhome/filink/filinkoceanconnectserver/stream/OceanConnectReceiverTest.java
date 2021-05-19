package com.fiberhome.filink.filinkoceanconnectserver.stream;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.filinklockapi.bean.ControlDel;
import com.fiberhome.filink.filinklockapi.constant.OperateState;
import com.fiberhome.filink.filinkoceanconnectserver.utils.FtpUpgradeUtil;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * OceanConnectReceiver测试类
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class OceanConnectReceiverTest {

    @Tested
    private OceanConnectReceiver oceanConnectReceiver;

    @Injectable
    private FiLinkOceanConnectMsgReceiverAsync msgReceiverAsync;

    @Injectable
    private FiLinkRequestReceiverAsync requestReceiverAsync;

    @Injectable
    private FiLinkResendReceiverAsync resendReceiverAsync;

    @Injectable
    private FtpUpgradeUtil ftpUpgradeUtil;

    @Injectable
    private FiLinkClearRedisControlAsync clearRedisControlAsync;

    @Test
    public void receive() {
        DeviceMsg deviceMsg = new DeviceMsg();
        deviceMsg.setOceanConnectId("4324-324234-sdf-4324");
        deviceMsg.setHexData("FFEF01011EBFDD6E5C118366002842474D50000D00002201000200000000001600000001000000000001000100000000000000000000");
        oceanConnectReceiver.receive(JSONObject.toJSONString(deviceMsg));
    }

    @Test
    public void sendOceanConnect() {
        String fiLinkReqParamsDtoListJson = "fiLinkReqParamsDtoListJson";
        oceanConnectReceiver.sendOceanConnect(fiLinkReqParamsDtoListJson);
    }

    @Test
    public void cmdResendReceiver() {
        oceanConnectReceiver.cmdResendReceiver();
    }

    @Test
    public void upgradeFile() {
        //msg不为空
        oceanConnectReceiver.upgradeFile("msg");
        //msg为空
        oceanConnectReceiver.upgradeFile("");
    }

    @Test
    public void clearRedisControl() {
        //control json为空
        oceanConnectReceiver.clearRedisControl("");
        //control 对象为空
        ControlDel nullControlDel = new ControlDel();
        oceanConnectReceiver.clearRedisControl(JSONObject.toJSONString(nullControlDel));
        //control json不为空
        ControlDel controlDel = new ControlDel();
        controlDel.setOperate(OperateState.UPDATE_STATE);
        List<String> hostIdList = new ArrayList<>();
        hostIdList.add("0101CFED4C0400000000");
        controlDel.setHostIdList(hostIdList);
        oceanConnectReceiver.clearRedisControl(JSONObject.toJSONString(controlDel));
    }
}