package com.fiberhome.filink.commonstation.handler.impl;


import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.constant.WellConstant;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.utils.WellDataUtil;
import io.netty.buffer.ByteBuf;

import java.util.Map;

/**
 * 设备地址处理
 * @Author: qiqizhu@wistronits.com
 * Date:2019/5/14
 */
public class WellServerCommunicationAddressHandler implements DataHandler {
    /**
     * 设备地址处理
     * @param dataFormat 数据转换参数类
     * @param byteBuf 字节缓冲
     * @return
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        Map<String, Object> param = (Map<String, Object>) dataFormat.getParam();
        Object hostIp = param.get(WellConstant.HOST_IP);
        if (hostIp != null) {
            String serverAddress = hostIp.toString();
            byte[] bytes = WellDataUtil.Ip2byte(serverAddress);
            byteBuf.writeBytes(bytes);
        } else {
            byteBuf.writeInt(0xFFFFFFFF);
        }
        return null;
    }
}
