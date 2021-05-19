package com.fiberhome.filink.stationserver.handler.impl;

import com.fiberhome.filink.protocol.bean.xmlBean.format.DataFormat;
import com.fiberhome.filink.protocol.bean.xmlBean.format.DataFormatParam;
import com.fiberhome.filink.stationserver.handler.DataHandler;
import com.fiberhome.filink.stationserver.util.HexUtil;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 门锁解析
 *
 * @author CongcaiYu
 */
public class  UnlockHandler implements DataHandler {

    /**
     * 门锁解析
     *
     * @param dataFormat DataFormat
     * @param byteBuf    ByteBuf
     * @return Object
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        Map<String, String> result = new HashMap(64);
        List<DataFormatParam> dataFormatParams = dataFormat.getDataFormatParams();
        Map<String, String> resultMap = dataFormat.getResultMap();
        for (DataFormatParam param : dataFormatParams) {
            Integer length = param.getLength();
            ByteBuf buf = byteBuf.readBytes(length);
            int hexInt = HexUtil.bufToInt(buf);
            String msg = resultMap.get(String.valueOf(hexInt));
            result.put(param.getName(), msg);
        }
        return result;
    }
}
