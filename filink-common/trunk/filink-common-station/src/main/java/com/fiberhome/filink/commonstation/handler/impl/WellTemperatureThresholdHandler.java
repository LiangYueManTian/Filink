package com.fiberhome.filink.commonstation.handler.impl;

import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.utils.WellDataUtil;
import io.netty.buffer.ByteBuf;

import java.util.Map;

/**
 * 温度阈值发送处理
 * Author:qiqizhu@wistronits.com
 * Date:2019/6/3
 */
public class WellTemperatureThresholdHandler implements DataHandler {
    /**
     * 人井温度阈值处理
     * @param dataFormat 数据转换参数类
     * @param byteBuf 字节缓冲
     * @return
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        Map<String, Object> param = (Map<String, Object>) dataFormat.getParam();
        Object highTemperatureObj = param.get(ParamsKey.HIGH_TEMPERATURE);
        Object lowTemperatureObj = param.get(ParamsKey.LOW_TEMPERATURE);
        if (highTemperatureObj == null || lowTemperatureObj == null) {
            byteBuf.writeInt(0xFFFFFFFF);
            return null;
        } else {
            int highTemperature = Integer.parseInt(highTemperatureObj.toString());
            byte[] hBytes = WellDataUtil.intToByte2(highTemperature);
            int lowTemperature = Integer.parseInt(lowTemperatureObj.toString());
            if (0 > lowTemperature) {
                lowTemperature = (-lowTemperature) * 256 + 32768;
            }
            byte[] lBytes = WellDataUtil.intToByte2(lowTemperature);
            byteBuf.writeBytes(lBytes);
            byteBuf.writeBytes(hBytes);
        }
        return null;
    }
}
