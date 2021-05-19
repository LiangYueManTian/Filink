package com.fiberhome.filink.commonstation.handler.impl;

import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;

import com.fiberhome.filink.commonstation.utils.HexUtil;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * 基站信息
 *
 * @author CongcaiYu
 */
public class WellBaseInfoHandler implements DataHandler {

    /**
     * 基础信息处理
     *
     * @param dataFormat DataFormat
     * @param byteBuf    ByteBuf
     * @return Object
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        String s = HexUtil.bufToStr(byteBuf);
        String[] split = s.split(",");
        Map<String, String> result = new HashMap<>(64);
        result.put(ParamsKey.IMEI, split[0]);
        result.put(ParamsKey.IMSI, split[1]);
        return result;
    }
}
