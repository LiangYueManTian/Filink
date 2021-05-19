package com.fiberhome.filink.rfid.bean.facility;

import lombok.Data;

/**
 * Created by Qing on 2019/6/6.
 * 基础信息bean
 */
@Data
public class BaseInfoBean {

    /**
     * 主键
     */
    private String id;
    /**
     * 标签类型(RFID、二维码)
     */
    private Integer labelType;
    /**
     * 箱架标签状态
     */
    private Integer labelState;
    /**
     * 箱架标签ID
     */
    private String boxLabel;
    /**
     * 设施ID
     */
    private String deviceId;
    /**
     * 实际框号
     */
    private Integer frameNo;
    /**
     * 框所属AB面(true是B面)
     */
    private Integer frameDouble;
    /**
     * 备注
     */
    private String memo;
    /**
     * 最后更新时间
     */
    private Long lastUpdateTime;

    /**
     * 模板名称
     */
    private String mouldName;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 别名
     */
    private String alias;

    /**
     * 标签信息
     */
    private String labelInfo;

    /**
     * 软删除标识 0-正常 1-已删除
     */
    private Integer isDelete;
}
