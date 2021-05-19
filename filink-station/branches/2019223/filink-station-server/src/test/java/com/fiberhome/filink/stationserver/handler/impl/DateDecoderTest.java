package com.fiberhome.filink.stationserver.handler.impl;

import com.fiberhome.filink.protocol.bean.xmlBean.format.DataFormat;
import com.fiberhome.filink.protocol.bean.xmlBean.format.DataFormatParam;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * 日期处理测试类
 * @author CongcaiYu
 */
@RunWith(MockitoJUnitRunner.class)
public class DateDecoderTest {

    @InjectMocks
    private DateDecoder dateDecoder;

    @Test
    public void handle() {
        DataFormat test = new DataFormat();
        dateDecoder.handle(test, Unpooled.directBuffer());
        //解析日期
        DataFormat dataFormat = new DataFormat();
        List<DataFormatParam> dataFormatParams = new ArrayList<>();
        //年
        DataFormatParam year = new DataFormatParam();
        year.setId("year");
        year.setLength(2);
        year.setType("int");
        dataFormatParams.add(year);
        //月
        DataFormatParam month = new DataFormatParam();
        month.setId("month");
        month.setLength(1);
        month.setType("int");
        dataFormatParams.add(month);
        //日
        DataFormatParam day = new DataFormatParam();
        day.setId("day");
        day.setLength(1);
        day.setType("int");
        dataFormatParams.add(day);
        //时
        DataFormatParam hour = new DataFormatParam();
        hour.setId("hour");
        hour.setLength(1);
        hour.setType("int");
        dataFormatParams.add(hour);
        //分
        DataFormatParam minute = new DataFormatParam();
        minute.setId("minute");
        minute.setLength(1);
        minute.setType("int");
        dataFormatParams.add(minute);
        //秒
        DataFormatParam second = new DataFormatParam();
        second.setId("second");
        second.setLength(1);
        second.setType("int");
        dataFormatParams.add(second);
        dataFormat.setDataFormatParams(dataFormatParams);
        ByteBuf byteBuf = Unpooled.directBuffer();
        byteBuf.writeShort(2019);
        byteBuf.writeByte(9);
        byteBuf.writeByte(16);
        byteBuf.writeByte(22);
        byteBuf.writeByte(45);
        byteBuf.writeByte(33);
        dateDecoder.handle(dataFormat, byteBuf);
    }
}