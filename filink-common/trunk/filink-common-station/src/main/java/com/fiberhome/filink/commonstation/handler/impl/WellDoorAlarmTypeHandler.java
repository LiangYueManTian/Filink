package com.fiberhome.filink.commonstation.handler.impl;


import com.fiberhome.filink.commonstation.constant.WellConstant;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.utils.WellByteBufUtil;
import com.fiberhome.filink.commonstation.utils.WellDataUtil;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * 人井门告警
 *
 * @author CongcaiYu
 */
public class WellDoorAlarmTypeHandler implements DataHandler {
    /**
     * 门锁告警解析
     *
     * @param dataFormat DataFormat
     * @param byteBuf    ByteBuf
     * @return Object
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        //转换成bit之后的string
        String s = WellDataUtil.byteToBit(byteBuf.readByte());
        //截取A门的状态信息
        int aCode = Integer.parseInt(s.substring(6, 8), 2);
        Map<String, String> resultMap = dataFormat.getResultMap();
        Map<String,String> result = new HashMap<>(64);
        result.put(WellConstant.DOOR_LOCK_A,resultMap.get(String.valueOf(aCode)));
        return result;
    }
}
