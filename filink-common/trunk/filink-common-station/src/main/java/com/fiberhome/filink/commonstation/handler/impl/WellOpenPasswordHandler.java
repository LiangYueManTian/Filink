package com.fiberhome.filink.commonstation.handler.impl;


import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.commonstation.utils.WellDataUtil;
import io.netty.buffer.ByteBuf;

import java.util.Map;

/**
 * 门锁密码计算
 *
 * @author CongcaiYu
 */
public class WellOpenPasswordHandler implements DataHandler {

    /**
     * 门锁解析
     *
     * @param dataFormat DataFormat
     * @param byteBuf    ByteBuf
     * @return Object
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        Map<String,Object> param = (Map<String, Object>) dataFormat.getParam();
        byte[] bytes = (byte[]) param.get("idenId");
        String equipmentId = (String) param.get(ParamsKey.EQUIPMENT_ID);
        byte[] cidBytes = HexUtil.hexStringToByte(equipmentId);
        String equipmentIdStr = WellDataUtil.byteToControllerId(cidBytes);
        byte[]  equipmentIdBytes = WellDataUtil.controllerIdToByte(equipmentIdStr);
        byte[] psdBytes = WellDataUtil.getOpenDoorPsd(bytes,equipmentIdBytes );
        byteBuf.writeBytes(psdBytes);
        return null;
    }
}
