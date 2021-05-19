package com.fiberhome.filink.rfid.bean.template;

import lombok.Data;

/**
 * templateVo
 *
 * @author liyj
 * @date 2019/5/14
 */
@Data
public class TemplateVO {

    /**
     * 主键id
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 行数
     */
    private Integer row;
    /**
     * 列数
     */
    private Integer col;
    /**
     * 摆放状态
     */
    private Integer putState;
    /**
     * 模板类型 TemplateTypeEnum
     */
    private Integer templateType;
    /**
     * 单双面
     */
    private Integer reversible;
    /**
     * 子模板id 用, 分隔开
     */
    private String childTemplateId;

    @Override
    public String toString() {
        return "TemplateVO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", row=" + row +
                ", col=" + col +
                ", putState=" + putState +
                ", templateType=" + templateType +
                ", reversible=" + reversible +
                ", childTemplateId='" + childTemplateId + '\'' +
                '}';
    }
}
