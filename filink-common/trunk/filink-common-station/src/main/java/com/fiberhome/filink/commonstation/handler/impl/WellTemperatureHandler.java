package com.fiberhome.filink.commonstation.handler.impl;


import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.utils.WellByteBufUtil;
import com.fiberhome.filink.commonstation.utils.WellDataUtil;
import io.netty.buffer.ByteBuf;

/**
 * 温度处理
 *
 * @author CongcaiYu
 */
public class WellTemperatureHandler implements DataHandler {

    /**
     *
     * @param dataFormat DataFormat
     * @param byteBuf    ByteBuf
     * @return Object
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        byte[] bytes = new byte[2];
        byteBuf.getBytes(0, bytes);
        String bits = "";
        int temperatureInfo;
        for (int i = bytes.length - 1; i >= 0; i--) {
            bits = bits +WellDataUtil.byteToBit(bytes[i]);
        }
        if (bits.length() != 16) {
            return false;
        }
        if (bits.charAt(0) == '1') {
            temperatureInfo = 32768 - Integer.parseInt(bits, 2);
        } else {
            temperatureInfo = WellByteBufUtil.bytes2ToInt(byteBuf, 2);
        }
        return (double) temperatureInfo / 256;
    }
}
