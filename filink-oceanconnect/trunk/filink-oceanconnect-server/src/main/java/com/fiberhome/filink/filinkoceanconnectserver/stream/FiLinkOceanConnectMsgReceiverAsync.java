package com.fiberhome.filink.filinkoceanconnectserver.stream;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.commonstation.business.MsgBusinessHandler;
import com.fiberhome.filink.commonstation.constant.WellConstant;
import com.fiberhome.filink.commonstation.entity.param.AbstractResOutputParams;
import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.commonstation.entity.xmlbean.FiLinkProtocolBean;
import com.fiberhome.filink.commonstation.exception.ResponseException;
import com.fiberhome.filink.commonstation.receiver.ResponseResolver;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkOceanResInputParams;
import com.fiberhome.filink.filinkoceanconnectserver.utils.ProtocolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
public class FiLinkOceanConnectMsgReceiverAsync {

    @Autowired
    private Map<String, ResponseResolver> responseHandlerMap;

    @Autowired
    private Map<String, MsgBusinessHandler> businessHandlerMap;

    @Autowired
    private ProtocolUtil protocolUtil;

    /**
     * 消息监听处理
     *
     * @param deviceMsgObj DeviceMsg
     */
    @Async
    public void receive(String deviceMsgObj) {
        if(StringUtils.isEmpty(deviceMsgObj)){
            log.info("[receiveMsg-resolve] : device msg is null");
            return;
        }
        DeviceMsg deviceMsg;
        try {
            deviceMsg = JSONObject.parseObject(deviceMsgObj, DeviceMsg.class);
        }catch (Exception e){
            log.error("[receiveMsg-resolve] : parse device msg failed", e);
            return;
        }
        try {
            handleMsg(deviceMsg);
        }catch (Exception e){
            log.error("[receiveMsg-resolve] : handle receive device msg failed", e);
        }
    }

    /**
     * 处理消费者消息
     * @param deviceMsg 设施对象
     * @throws Exception 异常
     */
    private void handleMsg(DeviceMsg deviceMsg) throws Exception{
        String equipmentId = getEquipmentId(deviceMsg.getHexData());
        log.info("[receiveMsg-resolve] : {}", equipmentId);
        //根据主控id获取主控信息
        ControlParam control = protocolUtil.getControlById(equipmentId);
        //根据序列id获取协议信息
        FiLinkProtocolBean protocolBean = protocolUtil.getProtocolBeanByControl(control);
        FiLinkOceanResInputParams fiLinkOceanResInputParams = new FiLinkOceanResInputParams();
        //处理类名称
        String responseResolverName = protocolBean.getResponseResolverName();
        //构造响应帧输入参数
        fiLinkOceanResInputParams.setDataSource(deviceMsg.getHexData());
        fiLinkOceanResInputParams.setProtocolBean(protocolBean);
        ResponseResolver responseResolver = responseHandlerMap.get(responseResolverName);
        //响应帧处理类为空
        if (responseResolver == null) {
            throw new ResponseException("no such responseResolver: " + responseResolverName);
        }
        //解析响应帧
        AbstractResOutputParams resOutputParams = responseResolver.resolveRes(fiLinkOceanResInputParams);
        log.info("[receiveMsg-resolve] : {}", JSONObject.toJSONString(resOutputParams));
        //set主控信息
        resOutputParams.setControlParam(control);
        //业务处理类名称
        String businessHandlerName = protocolBean.getBusinessHandlerName();
        MsgBusinessHandler businessHandler = businessHandlerMap.get(businessHandlerName);
        if (businessHandler == null) {
            throw new ResponseException("no such businessHandler: " + businessHandlerName);
        }
        businessHandler.handleMsg(resOutputParams);
    }


    /**
     * 获取装置id
     *
     * @param hexData 16进制数据
     * @return 装置id
     */
    private String getEquipmentId(String hexData) {
        if(hexData==null){
            return null;
        }
        String id;
        //截取id部分
        if(hexData.startsWith(WellConstant.FE)){
            id=hexData.substring(10,26);
        }else {
            id = hexData.substring(4, 24);
        }
        return id;
    }

}
