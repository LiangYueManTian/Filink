package com.fiberhome.filink.commonstation.handler.impl;

import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * 应急开锁处理类
 *
 * @author CongcaiYu
 */
public class EmergencyLockHandler implements DataHandler {


    /**
     * 应急开锁data处理
     *
     * @param dataFormat 数据转换参数类
     * @param byteBuf    字节缓冲
     * @return 处理后结果
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        Map<String, String> result = new HashMap<>(64);
        Map<String, String> doorMap = dataFormat.getResultMap();
        //门编号
        String doorNum = String.valueOf(byteBuf.readByte());
        //门状态
        String doorState = String.valueOf(byteBuf.readByte());
        result.put(doorNum, doorMap.get(doorState));
        return result;
    }
}
