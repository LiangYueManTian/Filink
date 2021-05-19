package com.fiberhome.filink.rfid.req.template;

import lombok.Data;

/**
 * 成端端口信息修改
 *
 * @author liyj
 * @date 2019/7/3
 */
@Data
public class PortCableReqDto {
    /**
     * 端口id
     */
    private String portId;
    /**
     * 成端状态
     */
    private Integer portCableState;
}
