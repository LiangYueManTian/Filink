package com.fiberhome.filink.rfid.bean.facility;

import lombok.Data;

/**
 * Created by Qing on 2019/6/20.
 * 设施业务信息上传实体（新增设施时使用）
 */
@Data
public class FacilityBusUploadDto extends FacilityUploadDto {
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
     * 设施id (保证都在这个设施里)
     */
    private String deviceId;
    /**
     * 设施类型
     */
    private String deviceType;
    /**
     * 箱模板id
     */
    private String boxTemplateId;
    /**
     * boxName
     */
    private String boxName;
    /**
     * 设施名称
     */
    private String deviceName;
}
