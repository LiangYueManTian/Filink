package com.fiberhome.filink.commonstation.handler.impl;

import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.commonstation.utils.WellDataUtil;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * Author:qiqizhu@wistronits.com
 * Date:2019/6/12
 */
public class WellDataTemperatureThresholdHandler implements DataHandler {
    /**
     * 上报温度阈值处理
     * @param dataFormat 数据转换参数类
     * @param byteBuf 字节缓冲
     * @return
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        Map<String, Object> result = new HashMap(64);
        byte[] hBytes = new byte[2];
        byte[] lBytes = new byte[2];
        byteBuf.readBytes(lBytes);
        byteBuf.readBytes(hBytes);
        int low = WellDataUtil.bytes2ToInt(lBytes, 0);
        int high = WellDataUtil.bytes2ToInt(hBytes, 0);
        if (low > 32768) {
            low = -(low - 32768) / 256;
        }
        result.put(ParamsKey.LOW_TEMPERATURE, low);
        result.put(ParamsKey.HIGH_TEMPERATURE, high);
        return result;
    }
}
