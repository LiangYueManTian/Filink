package com.fiberhome.filink.commonstation.handler.impl;


import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.utils.WellByteBufUtil;
import com.fiberhome.filink.commonstation.utils.WellDataUtil;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * 门锁解析
 *
 * @author CongcaiYu
 */
public class WellOtherInfoHandler implements DataHandler {

    /**
     * 门锁解析
     *
     * @param dataFormat DataFormat
     * @param byteBuf    ByteBuf
     * @return Object
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        byte b = byteBuf.readByte();
        char[] chars = WellDataUtil.byteToBit(b).toCharArray();
        Map<String, String> resultMap = dataFormat.getResultMap();
        return resultMap.get(String.valueOf(chars[0]));
    }
}
