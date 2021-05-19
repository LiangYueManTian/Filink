package com.fiberhome.filink.protocol.bean.xmlBean;



import com.fiberhome.filink.protocol.bean.xmlBean.data.Data;
import com.fiberhome.filink.protocol.bean.xmlBean.header.RequestHeader;
import com.fiberhome.filink.protocol.bean.xmlBean.header.ResponseHeader;

import java.io.Serializable;
import java.util.Map;

/**
 * filink协议实体类
 * @author CongcaiYu
 */
@lombok.Data
public class FiLinkProtocolBean extends AbstractProtocolBean implements Serializable {

    /**
     * 软件版本
     */
    private String softwareVersion;
    /**
     * 硬件版本
     */
    private String hardwareVersion;
    /**
     * 请求头
     */
    private RequestHeader requestHeader;
    /**
     * 响应头
     */
    private ResponseHeader responseHeader;
    /**
     * 消息体
     */
    private Map<String, Data> dataMap;

}
