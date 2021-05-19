package com.fiberhome.filink.filinkoceanconnectserver.entity.profile;

import lombok.Data;

/**
 * profile service实体
 *
 * @author CongcaiYu
 */
@Data
public class ServiceBean {

    /**
     * id
     */
    private String id;

    /**
     * 是否base64加密
     */
    private boolean isBase64;

    /**
     * method对象
     */
    private MethodBean method;

    /**
     * 长度
     */
    private String length;

    /**
     * 数据包
     */
    private String data;
}
