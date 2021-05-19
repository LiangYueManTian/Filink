package com.fiberhome.filink.commonstation.handler.impl;


import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import io.netty.buffer.ByteBuf;

/**
 * 文件数据处理类
 * @author CongcaiYu
 */
public class FileDataHandler implements DataHandler {

    /**
     * 文件数据处理类
     *
     * @param dataFormat 数据转换参数类
     * @param byteBuf 字节缓冲
     * @return 处理后数据
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        byte[] byteBufArray = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(byteBufArray);
        return HexUtil.bytesToHexString(byteBufArray);
    }
}
