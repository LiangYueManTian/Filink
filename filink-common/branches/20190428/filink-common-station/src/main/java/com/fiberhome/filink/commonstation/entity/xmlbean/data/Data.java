package com.fiberhome.filink.commonstation.entity.xmlbean.data;

import java.io.Serializable;

/**
 * data实体类
 * @author CongcaiYu
 */
@lombok.Data
public class Data implements Serializable {
    /**
     * 指令id
     */
    private String cmdId;
    /**
     * data体请求
     */
    private DataRequest dataRequest;
    /**
     * data体响应
     */
    private DataResponse dataResponse;

}
