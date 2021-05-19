package com.fiberhome.filink.commonstation.handler.impl;


import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.commonstation.constant.CmdType;
import com.fiberhome.filink.commonstation.constant.ParamsKey;
import io.netty.buffer.ByteBuf;

import java.util.Map;

/**
 * 设施id处理类
 * @author CongcaiYu
 */
public class EquipmentIdHandler implements DataHandler {
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        String deviceId = "";
        Map<String,Object> headerParam = (Map<String, Object>) dataFormat.getParam();
        //获取请求类型
        Integer cmdType = (Integer) headerParam.get(ParamsKey.CMD_TYPE);
        //请求帧
        if(CmdType.REQUEST_TYPE == cmdType){
            //获取设施id
            String equipmentId = headerParam.get(ParamsKey.HEADER_DATA).toString();
            byteBuf.writeBytes(HexUtil.hexStringToByte(equipmentId));
        }else {
            //响应帧
            deviceId = HexUtil.bufToHexStr(byteBuf);
        }
        return deviceId;
    }

}
