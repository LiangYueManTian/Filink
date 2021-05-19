package com.fiberhome.filink.commonstation.handler.impl;

import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import io.netty.buffer.ByteBuf;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Author:qiqizhu@wistronits.com
 * Date:2019/5/28
 */
public class WellCmdIdHandler implements DataHandler {
    /**
     *命令id处理
     * @param dataFormat 数据转换参数类
     * @param byteBuf 字节缓冲
     * @return
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        Map<String, String> resultMap = dataFormat.getResultMap();
        Map<String, String> param = (Map<String, String>) dataFormat.getParam();
        String data = param.get(ParamsKey.HEADER_DATA);
        String cmdId = resultMap.get(data);
        int dataSourceInt;
        if (StringUtils.isEmpty(cmdId)) {
            dataSourceInt = Integer.decode(data);
        } else {
            dataSourceInt = Integer.decode(cmdId);
        }
        byteBuf.writeShort(dataSourceInt);
        return null;
    }
}
