package com.fiberhome.filink.rfid.bean.facility;

import lombok.Data;

/**
 * Created by Qing on 2019/6/6.
 * 端口信息bean
 */
@Data
public class PortInfoBean extends BaseInfoBean {
    /**
     * 实际盘号
     */
    private Integer boardNo;
    /**
     * 实际端口号
     */
    private Integer portNo;
    /**
     * 端口所属AB面
     */
    private Integer portDouble;
    /**
     * 端口标签ID
     */
    private String portLabel;
    /**
     * 端口状态(占用、异常、空闲、预占用(待跳接、预留)、虚占(无业务))
     */
    private Integer portState;

    /**
     * 适配器类型(FC/SC)
     */
    private Integer adapterType;
}
