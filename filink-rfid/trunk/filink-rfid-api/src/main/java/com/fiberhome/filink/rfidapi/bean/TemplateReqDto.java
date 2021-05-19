package com.fiberhome.filink.rfidapi.bean;

import lombok.Data;

/**
 * 保存模板
 *
 * @author liyj
 * @date 2019/5/22
 */
@Data
public class TemplateReqDto {
    /**
     * 关系id
     */
    private String relationId;
    /**
     * 箱模板走向
     */
    private Integer boxTrend;
    /**
     * 箱模板编号规则
     */
    private Integer boxCodeRule;

    /**
     * 框模板走向
     */
    private Integer frameTrend;
    /**
     * 框模板编号规则
     */
    private Integer frameCodeRule;

    /**
     * 盘模板走向
     */
    private Integer discTrend;
    /**
     * 盘模板编号规则
     */
    private Integer discCodeRule;
    /**
     * 设施id
     */
    private String deviceId;
    /**
     * 箱模板id
     */
    private String boxTemplateId;
    /**
     * 模板名称
     */
    private String boxName;
    /**
     * 设施名称
     */
    private String deviceName;
    /**
     * 设施类型
     */
    private String deviceType;
}
