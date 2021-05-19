package com.fiberhome.filink.commonstation.entity.xmlbean;

import com.fiberhome.filink.commonstation.entity.xmlbean.data.Data;
import com.fiberhome.filink.commonstation.entity.xmlbean.data.DataParamsChild;
import com.fiberhome.filink.commonstation.entity.xmlbean.header.HeaderParam;

import java.io.Serializable;
import java.util.List;
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
     * 请求帧处理类名称
     */
    private String requestResolverName;
    /**
     * 响应帧处理类名称
     */
    private String responseResolverName;
    /**
     * 业务处理类名称
     */
    private String businessHandlerName;
    /**
     * 公共头
     */
    private List<HeaderParam> commonHeader;
    /**
     * 请求头配置信息
     */
    private List<HeaderParam> requestHeader;
    /**
     * 响应头配置信息
     */
    private List<HeaderParam> responseHeader;
    /**
     * data体map
     */
    private Map<String, Data> dataMap;
    /**
     * 错误集
     */
    private List<DataParamsChild> errorSet;

}
