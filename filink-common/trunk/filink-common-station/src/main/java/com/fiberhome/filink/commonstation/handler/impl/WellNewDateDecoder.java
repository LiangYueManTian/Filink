package com.fiberhome.filink.commonstation.handler.impl;


import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.utils.WellDataUtil;
import io.netty.buffer.ByteBuf;

/**
 * 时间解码
 *
 * @author CongcaiYu
 */
public class WellNewDateDecoder implements DataHandler {

    /**
     * 解析时间
     *
     * @param dataFormat 数据转换参数类
     * @param byteBuf    字节缓冲
     * @return Object
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        byte[] timeBytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(timeBytes);
        int i = WellDataUtil.bytes4ToInt(timeBytes, 0);
        return i;
    }
}
