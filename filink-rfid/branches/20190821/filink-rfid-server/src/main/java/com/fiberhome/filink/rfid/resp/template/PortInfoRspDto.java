package com.fiberhome.filink.rfid.resp.template;

import lombok.Data;

/**
 * 端口信息返回体
 *
 * @author liyj
 * @date 2019/6/5
 */
@Data
public class PortInfoRspDto {

    /**
     * 端口id
     */
    private String portId;
    /**
     * 只能标签id
     */
    private String rfId;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 适配器类型 AdapterTypeEnum
     */
    private Integer adapterType;
    /**
     * 标签类型
     */
    private Integer labelType;
    /**
     * 光缆段名称
     */
    private String opticCableSectionName;
    /**
     * 纤芯信息
     */
    private String cableCore;
    /**
     * 备注信息
     */
    private String remark;
    /**
     * 端口号 由 箱框盘 拼接
     */
    private String portNum;
}

