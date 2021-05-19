package com.fiberhome.filink.rfid.req.template;

import com.fiberhome.filink.rfid.bean.template.TemplateVO;
import lombok.Data;

/**
 * 保存模板
 *
 * @author liyj
 * @date 2019/5/22
 */
@Data
public class TemplateReqDto extends TemplateVO {
    /**
     * 关系id 即是箱模板id
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
     * 设施id (保证都在这个设施里)
     */
    private String deviceId;
    /**
     * 设施名称
     */
    private String deviceName;
    /**
     * 设施类型
     */
    private String deviceType;
    /**
     * 箱模板id
     */
    private String boxTemplateId;
    /**
     * 箱模板名称
     */
    private String boxName;
}
