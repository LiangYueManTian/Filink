package com.fiberhome.filink.commonstation.handler.impl;


import com.fiberhome.filink.commonstation.constant.WellConstant;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.utils.WellDataUtil;
import io.netty.buffer.ByteBuf;

import java.util.Map;

/**
 * 门锁解析
 *
 * @author CongcaiYu
 */
public class WellIdHandler implements DataHandler {
    /**
     * 根据id计算idenId
     *
     * @param dataFormat DataFormat
     * @param byteBuf    ByteBuf
     * @return Object
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        Map<String,Object> param = (Map<String, Object>) dataFormat.getParam();
        Object idenId = param.get(WellConstant.IDEN_ID);
        if(idenId == null) {
            idenId = "48191175263631000001";
        }
        byte[] bytes = WellDataUtil.controllerIdToByte((String) idenId);
        param.put(WellConstant.IDEN_ID,bytes);
        byteBuf.writeBytes(bytes);
        return bytes;
    }
}
