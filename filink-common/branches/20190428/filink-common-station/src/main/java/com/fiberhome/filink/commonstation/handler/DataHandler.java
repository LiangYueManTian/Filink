package com.fiberhome.filink.commonstation.handler;

import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import io.netty.buffer.ByteBuf;

/**
 * 数据处理接口
 * @author CongcaiYu
 */
public interface DataHandler {

    /**
     * 数据处理方法
     * @param dataFormat 数据转换参数类
     * @param byteBuf 字节缓冲
     * @return 返回值
     */
    Object handle(DataFormat dataFormat, ByteBuf byteBuf);
}
