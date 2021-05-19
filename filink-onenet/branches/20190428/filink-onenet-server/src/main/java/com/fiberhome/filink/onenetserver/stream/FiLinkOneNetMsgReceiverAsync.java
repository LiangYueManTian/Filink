package com.fiberhome.filink.onenetserver.stream;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.commonstation.business.MsgBusinessHandler;
import com.fiberhome.filink.commonstation.entity.param.AbstractResOutputParams;
import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.commonstation.entity.xmlbean.FiLinkProtocolBean;
import com.fiberhome.filink.commonstation.exception.ResponseException;
import com.fiberhome.filink.commonstation.receiver.ResponseResolver;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.onenetserver.bean.protocol.FiLinkOneNetInputParams;
import com.fiberhome.filink.onenetserver.utils.ProtocolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * 消息监听处理类
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class FiLinkOneNetMsgReceiverAsync {

    @Autowired
    private Map<String, ResponseResolver> responseHandlerMap;

    @Autowired
    private Map<String, MsgBusinessHandler> businessHandlerMap;

    @Autowired
    private ProtocolUtil protocolUtil;

    /**
     * 消息监听处理
     *
     * @param deviceMsg DeviceMsg
     */
    @Async
    public void receive(DeviceMsg deviceMsg) throws Exception {
        String equipmentId = getEquipmentId(deviceMsg.getHexData());
        log.info("control id : " + equipmentId + ">>>>>>>>>>>>>>>>>>>>>>>>>>");
        //根据主控id获取主控信息
        ControlParam control = protocolUtil.getControlById(equipmentId);
        //根据序列id获取协议信息
        FiLinkProtocolBean protocolBean = protocolUtil.getProtocolBeanByControl(control);
        FiLinkOneNetInputParams fiLinkOceanResInputParams = new FiLinkOneNetInputParams();
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
        log.info("resolveMsg: " + JSONObject.toJSONString(resOutputParams));
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
        //截取id部分
        hexData = hexData.substring(4, 24);
        //将16进制数据包转换成byteBuf
        return hexData;
    }

}
