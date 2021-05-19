package com.fiberhome.filink.stationserver.entity.param;

import com.fiberhome.filink.protocol.bean.xmlBean.AbstractProtocolBean;
import lombok.Data;

/**
 * 响应帧输入参数抽象类
 * @author CongcaiYu
 */
@Data
public abstract class AbstractResInputParams {
    /**
     * 16进制数据包
     */
    private String dataSource;
    /**
     * 协议实体
     */
    private AbstractProtocolBean protocolBean;

}
