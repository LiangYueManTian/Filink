package com.fiberhome.filink.commonstation.handler.impl;

import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import io.netty.buffer.ByteBuf;

import java.util.Map;

/**
 * 人井升级数据处理
 * Author:qiqizhu@wistronits.com
 * Date:2019/6/3
 */
public class WellUpDataHandler implements DataHandler {
    /**
     * 人井升级数据处理
     * @param dataFormat 数据转换参数类
     * @param byteBuf 字节缓冲
     * @return
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        Map<String, Object> param = (Map<String, Object>) dataFormat.getParam();
        byte[] bytes = (byte[]) param.get(ParamsKey.DATA);
        byteBuf.writeBytes(bytes);
        return null;
    }
}
