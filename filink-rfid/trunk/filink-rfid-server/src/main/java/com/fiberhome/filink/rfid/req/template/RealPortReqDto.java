package com.fiberhome.filink.rfid.req.template;

import lombok.Data;

/**
 * 实景图 端口
 *
 * @author liyj
 * @date 2019/6/21
 */
@Data
public class RealPortReqDto {
    /**
     * 盘id
     */
    private String discId;
    /**
     * 状态 0 不在位 1  在位
     */
    private Integer state;
    /**
     * 端口id
     */
    private String portId;
    /**
     * 绑定状态
     */
    private Integer busState;
    /**
     * 绑定业务信息
     */
    private String busBindingState;
    /**
     * 端口状态 PortStateEnum
     */
    private Integer portState;
}
