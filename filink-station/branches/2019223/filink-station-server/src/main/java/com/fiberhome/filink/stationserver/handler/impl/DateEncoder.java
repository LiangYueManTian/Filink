package com.fiberhome.filink.stationserver.handler.impl;

import com.fiberhome.filink.protocol.bean.xmlBean.format.DataFormat;
import com.fiberhome.filink.protocol.bean.xmlBean.format.DataFormatParam;
import com.fiberhome.filink.stationserver.handler.DataHandler;
import com.fiberhome.filink.stationserver.util.ByteBufUtil;
import io.netty.buffer.ByteBuf;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日期编码
 *
 * @author CongcaiYu
 */
public class DateEncoder implements DataHandler {

    /**
     * 日期编码
     *
     * @param dataFormat DataFormat
     * @param byteBuf    ByteBuf
     * @return Object
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        Object param = dataFormat.getParam();
        long timeStamp = Long.parseLong(param.toString());
        String dateFormat = "yyyy-MM-dd-HH-mm-ss";
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        String time = format.format(new Date(timeStamp));
        Map<String, String> timeMap = new HashMap<>(64);
        String[] split = time.split("-");
        timeMap.put("year", split[0]);
        timeMap.put("month", split[1]);
        timeMap.put("day", split[2]);
        timeMap.put("hour", split[3]);
        timeMap.put("minute", split[4]);
        timeMap.put("second", split[5]);
        List<DataFormatParam> dataFormatParams = dataFormat.getDataFormatParams();
        for (DataFormatParam formatParam : dataFormatParams) {
            String id = formatParam.getId();
            String timeNum = timeMap.get(id);
            Integer length = formatParam.getLength();
            String type = formatParam.getType();
            ByteBufUtil.setDataBuf(type, timeNum, length, byteBuf);
        }
        return null;
    }
}
