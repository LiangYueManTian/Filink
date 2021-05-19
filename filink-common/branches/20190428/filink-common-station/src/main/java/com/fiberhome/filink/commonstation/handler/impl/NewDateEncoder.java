package com.fiberhome.filink.commonstation.handler.impl;

import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import io.netty.buffer.ByteBuf;

/**
 * 日期编码
 *
 * @author CongcaiYu
 */
public class NewDateEncoder implements DataHandler {

    /**
     * 日期编码
     *
     * @param dataFormat DataFormat
     * @param byteBuf    ByteBuf
     * @return Object
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        //获取时间戳
        Object param = dataFormat.getParam();
        long timeStamp = Long.parseLong(param.toString());
        return null;
    }
}
