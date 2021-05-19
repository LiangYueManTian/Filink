package com.fiberhome.filink.commonstation.handler.impl;


import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.constant.WellConstant;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.utils.WellByteBufUtil;
import com.fiberhome.filink.commonstation.utils.WellDataUtil;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * 开锁
 *
 * @author CongcaiYu
 */
public class WellUnlockHandler implements DataHandler {

    /**
     *
     * @param dataFormat DataFormat
     * @param byteBuf    ByteBuf
     * @return Object
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        Map<String, Map> result = new HashMap(64);
        Map<Integer, String> doorMap = new HashMap(64);
        Map<Integer, String> lockMap = new HashMap(64);
        char[] chars = WellDataUtil.byteToBit(byteBuf.readByte()).toCharArray();
        Map<String, String> resultMap = dataFormat.getResultMap();
        for (int i = 0; i < 4; i++) {
            lockMap.put(4-i, resultMap.get(String.valueOf(chars[i])));
        }
        for (int i = 4; i < 8; i++) {
            doorMap.put(8-i, resultMap.get(String.valueOf(chars[i])));
        }
        result.put(WellConstant.DOOR_MAP, doorMap);
        result.put(WellConstant.LOCK_MAP, lockMap);
        return result;
    }
}
