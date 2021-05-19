package com.fiberhome.filink.stationserver.handler.impl;

import com.fiberhome.filink.protocol.bean.xmlBean.format.DataFormat;
import com.fiberhome.filink.protocol.bean.xmlBean.format.DataFormatParam;
import com.fiberhome.filink.stationserver.handler.impl.DateEncoder;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * DateEncoder测试类
 * @author CongcaiYu
 */
@RunWith(MockitoJUnitRunner.class)
public class DateEncoderTest {

    @InjectMocks
    private DateEncoder dateEncoder;

    @Test
    public void handle() {
        DataFormat dataFormat = new DataFormat();
        dataFormat.setParam(System.currentTimeMillis());
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
        dateEncoder.handle(dataFormat, Unpooled.directBuffer());
    }
}