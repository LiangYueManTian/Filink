package com.fiberhome.filink.commonstation.handler.impl;

import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import io.netty.buffer.ByteBuf;

/**
 * 时间解码
 * @author CongcaiYu
 */
public class NewDateDecoder implements DataHandler {

    /**
     * 解析时间
     *
     * @param dataFormat 数据转换参数类
     * @param byteBuf 字节缓冲
     * @return Object
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        int i = HexUtil.bufToInt(byteBuf);

        return i;
    }
}
