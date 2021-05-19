package com.fiberhome.filink.stationserver.stream;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.protocol.bean.xmlBean.AbstractProtocolBean;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.entity.param.AbstractResOutputParams;
import com.fiberhome.filink.stationserver.entity.protocol.DeviceMsg;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkResInputParams;
import com.fiberhome.filink.stationserver.receiver.MsgBusinessHandler;
import com.fiberhome.filink.stationserver.receiver.ResponseResolver;
import com.fiberhome.filink.stationserver.util.ProtocolUtil;
import com.fiberhome.filink.stationserver.util.lockenum.RedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 消息监听处理类
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class FiLinkUdpMsgReceiver {

    @Autowired
    private Map<String, ResponseResolver> responseHandlerMap;

    @Autowired
    private MsgBusinessHandler businessHandler;

    @Autowired
    private ProtocolUtil protocolUtil;

    /**
     * 消息监听处理
     *
     * @param deviceMsg DeviceMsg
     */
    @StreamListener(StationChannel.STATION_UDP_INPUT)
    public void receive(DeviceMsg deviceMsg) throws Exception {
        //todo 获取序列id
        String serialNum = "13172750";
        deviceMsg.setDeviceId(serialNum);
        //根据序列id获取协议信息
        AbstractProtocolBean protocolBean = protocolUtil.getProtocolBeanBySerialNum(serialNum);
        String version = "fiLinkUdpResponseResolver";
        FiLinkResInputParams fiLinkResInputParams = new FiLinkResInputParams();
        //构造响应帧输入参数
        fiLinkResInputParams.setDataSource(deviceMsg.getHexData());
        fiLinkResInputParams.setProtocolBean(protocolBean);
        ResponseResolver responseResolver = responseHandlerMap.get(version);
        //解析响应帧
        AbstractResOutputParams abstractResOutputParams = responseResolver.resolveRes(fiLinkResInputParams);
        log.info("resolveMsg: " + JSONObject.toJSONString(abstractResOutputParams));
        String deviceId = abstractResOutputParams.getDeviceId();
        deviceMsg.setDeviceId(deviceId);
        //将设施的ip port保存到redis
        if(StringUtils.isEmpty(deviceId)){
            log.info("deviceId is null >>>>>>>>>>>>>");
        }else {
            RedisUtils.hSet(RedisKey.DEVICE_KEY,deviceId,deviceMsg);
        }
        businessHandler.handleMsg(abstractResOutputParams);
    }

}
