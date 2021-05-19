package com.fiberhome.filink.commonstation.handler.impl;


import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormatParam;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人井加速度解析
 *
 * @author CongcaiYu
 */
public class WellLeanHandler implements DataHandler {

    /**
     *
     * @param dataFormat DataFormat
     * @param byteBuf    ByteBuf
     * @return Object
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        Map<String, Integer> result = new HashMap(64);
        List<DataFormatParam> dataFormatParams = dataFormat.getDataFormatParams();
        for (DataFormatParam param : dataFormatParams) {
            Integer length = param.getLength();
            ByteBuf buf = byteBuf.readBytes(length);
            int hexInt = HexUtil.bufToInt(buf);
            result.put(param.getName(), hexInt);
            ReferenceCountUtil.release(buf);
        }
        return result;
    }
}
