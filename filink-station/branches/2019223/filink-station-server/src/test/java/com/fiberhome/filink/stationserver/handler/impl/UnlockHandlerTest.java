package com.fiberhome.filink.stationserver.handler.impl;

import com.fiberhome.filink.protocol.bean.xmlBean.format.DataFormat;
import com.fiberhome.filink.protocol.bean.xmlBean.format.DataFormatParam;
import com.fiberhome.filink.stationserver.handler.impl.UnlockHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UnlockHandler测试类
 * @author CongcaiYu
 */
@RunWith(MockitoJUnitRunner.class)
public class UnlockHandlerTest {

    @InjectMocks
    private UnlockHandler unlockHandler;

    @Test
    public void handle() {
        //dataFormat对象
        DataFormat dataFormat = new DataFormat();
        List<DataFormatParam> dataFormatParams = new ArrayList<>();
        DataFormatParam four = new DataFormatParam();
        four.setName("4");
        four.setLength(1);
        dataFormatParams.add(four);
        DataFormatParam three = new DataFormatParam();
        three.setName("3");
        three.setLength(1);
        dataFormatParams.add(three);
        DataFormatParam two = new DataFormatParam();
        two.setName("2");
        two.setLength(1);
        dataFormatParams.add(two);
        DataFormatParam one = new DataFormatParam();
        one.setName("1");
        one.setLength(1);
        dataFormatParams.add(one);
        dataFormat.setDataFormatParams(dataFormatParams);
        //resultMap
        Map<String,String> resultMap = new HashMap<>();
        resultMap.put("0","22");
        resultMap.put("1","21");
        resultMap.put("2","11");
        resultMap.put("3","12");
        resultMap.put("123","02");
        resultMap.put("124","01");
        resultMap.put("125","10");
        resultMap.put("126","20");
        resultMap.put("127","00");
        dataFormat.setResultMap(resultMap);
        ByteBuf byteBuf = Unpooled.directBuffer();
        byteBuf.writeByte(0);
        byteBuf.writeByte(1);
        byteBuf.writeByte(2);
        byteBuf.writeByte(3);
        unlockHandler.handle(dataFormat,byteBuf);
    }
}