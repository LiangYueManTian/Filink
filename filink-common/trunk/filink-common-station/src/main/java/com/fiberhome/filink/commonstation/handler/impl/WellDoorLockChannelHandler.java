package com.fiberhome.filink.commonstation.handler.impl;


import com.fiberhome.filink.commonstation.constant.WellConstant;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import io.netty.buffer.ByteBuf;

import java.util.Map;

import static com.fiberhome.filink.commonstation.utils.WellDataUtil.intToByte1;


/**
 * 门锁解析
 *
 * @author CongcaiYu
 */
public class WellDoorLockChannelHandler implements DataHandler {

    /**
     * 门锁解析
     *
     * @param dataFormat DataFormat
     * @param byteBuf    ByteBuf
     * @return Object
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        Map<String,Object> param=(Map<String, Object>) dataFormat.getParam();
        Object chan = param.get(WellConstant.CHANNEL);
        if(chan == null) {
            chan = 0;
        }
        Integer channel =(Integer)chan;
        byte[] bytes = intToByte1(channel);
        byteBuf.writeByte(bytes[0]);
        return null;
    }
}
