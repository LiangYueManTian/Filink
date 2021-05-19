package com.fiberhome.filink.protocol.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 通信协议 参数类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/2/28
 */
@Data
public class ProtocolParams implements Serializable {
    /**
     * 协议id
     */
    private String paramId;

    /**
     * 协议名称
     */
    private String paramType;

    /**
     * 协议内容
     */
    private ProtocolField protocolField;

}
