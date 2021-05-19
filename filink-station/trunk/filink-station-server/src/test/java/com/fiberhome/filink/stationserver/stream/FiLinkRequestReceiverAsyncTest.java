package com.fiberhome.filink.stationserver.stream;

import com.fiberhome.filink.stationserver.service.StationService;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * FiLinkRequestReceiverAsync测试类
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class FiLinkRequestReceiverAsyncTest {

    @Tested
    private FiLinkRequestReceiverAsync requestReceiverAsync;

    @Injectable
    private StationService stationService;

    @Test
    public void sendUdp() {
        //解析失败
        String failReqJson = "123";
        try {
            requestReceiverAsync.sendUdp(failReqJson);
        } catch (Exception e) {
        }
        //list为空
        String nullListReqJson = "[]";
        try {
            requestReceiverAsync.sendUdp(nullListReqJson);
        } catch (Exception e) {
        }
        //fiLinkReqParamsDto对象为空
        String nullParamDtoReqJson = "[{\"equipmentId\":\"\"}]";
        requestReceiverAsync.sendUdp(nullParamDtoReqJson);
        //正常场景
        String reqJson = "[{\"equipmentId\":\"0101FFF04C0400000005\",\"softwareVersion\":\"RP9003.004A.bin\",\"hardwareVersion\":\"NRF52840_elock\",\"cmdId\":\"0x2201\",\"params\":{\"params\":[{\"slotNum\":\"1\",\"operate\":\"0\"}]},\"appId\":null,\"imei\":\"172136272151297\",\"plateFormId\":null,\"token\":\"265f68ca-dce0-4184-9070-8a4ee0743f24\",\"phoneId\":null,\"plateForm\":\"3\",\"appKey\":null}]";
        requestReceiverAsync.sendUdp(reqJson);
    }
}