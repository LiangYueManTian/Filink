package com.fiberhome.filink.rfid.bean.template;

import lombok.Data;

/**
 * 模板关系表
 *
 * @author liyj
 * @date 2019/5/15
 */
@Data
public class TemplateRelation {
    /**
     * 唯一id
     */
    private Integer id;
    /**
     * 箱模板id
     */
    private Integer boxId;
    /**
     * 框模板id
     */
    private Integer frameId;
    /**
     * 盘模板id
     */
    private Integer discId;
}
