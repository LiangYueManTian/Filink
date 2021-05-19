package com.fiberhome.filink.stationserver.stream;

import com.fiberhome.filink.bean.WebSocketMessage;
import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FiLinkKafkaSender测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class FiLinkKafkaSenderTest {

    @Tested
    private FiLinkKafkaSender kafkaSender;

    @Injectable
    private StationChannel stationChannel;

    @Test
    public void sendUdp() {
        DeviceMsg deviceMsg = new DeviceMsg();
        deviceMsg.setHexData("FFEF01011EBFDD6E5C118366002842474D50000D00002201000200000000001600000001000000000001000100000000000000000000");
        deviceMsg.setIp("10.5.24.12");
        deviceMsg.setPort(6756);
        kafkaSender.sendUdp(deviceMsg);
    }

    @Test
    public void sendAlarm() {
        Map<String,Object> alarmMap = new HashMap<>(64);
        Map<String,Object> alarmParam = new HashMap<>(64);
        alarmParam.put(ParamsKey.DATA_CLASS,ParamsKey.ELECTRICITY);
        alarmParam.put(ParamsKey.DATA,"25");
        List<Map<String,Object>> alarmList = new ArrayList<>();
        alarmList.add(alarmParam);
        alarmMap.put(ParamsKey.PARAMS_KEY,alarmList);
        alarmMap.put(ParamsKey.DEVICE_ID,"f305fd80ad2f11e99e600242ac110002");
        alarmMap.put(ParamsKey.TIME,"1563874900812l");
        kafkaSender.sendAlarm(alarmMap);
    }

    @Test
    public void sendWebSocket() {
        WebSocketMessage webSocketMessage = new WebSocketMessage();
        webSocketMessage.setMsgType(1);
        webSocketMessage.setChannelId("1");
        webSocketMessage.setChannelKey("alarm");
        kafkaSender.sendWebSocket(webSocketMessage);
    }

    @Test
    public void sendAlarmPic() {
        Map<String,String> picMap = new HashMap<>(64);
        picMap.put(ParamsKey.PIC_DATA, "fileData");
        picMap.put(ParamsKey.DEVICE_ID, "f305fd80ad2f11e99e600242ac110002");
        picMap.put(ParamsKey.FILE_FORMAT, "jpg");
        picMap.put(ParamsKey.DATA_SIZE, "6");
        picMap.put(ParamsKey.DOOR_NUM, "1");
        picMap.put(ParamsKey.FILE_TYPE, "1");
        picMap.put(ParamsKey.DATA_CLASS, "violence");
        picMap.put(ParamsKey.TIME, "1563874900812l");
        kafkaSender.sendAlarmPic(picMap);
    }
}