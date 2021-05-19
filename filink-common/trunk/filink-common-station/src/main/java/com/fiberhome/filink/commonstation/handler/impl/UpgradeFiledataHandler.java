package com.fiberhome.filink.commonstation.handler.impl;

import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 升级包文件处理类
 *
 * @author CongcaiYu
 */
@Slf4j
public class UpgradeFiledataHandler implements DataHandler {

    /**
     * 升级包文件数据处理
     *
     * @param dataFormat 数据转换参数类
     * @param byteBuf    字节缓冲
     * @return 处理后数据
     */
    @Override
    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
        Object param = dataFormat.getParam();
        if(StringUtils.isEmpty(param)){
            log.error("upgrade file param is null>>>>>>>>");
        }
        byteBuf.writeBytes(HexUtil.hexStringToByte(param.toString()));
        return null;
    }
}
