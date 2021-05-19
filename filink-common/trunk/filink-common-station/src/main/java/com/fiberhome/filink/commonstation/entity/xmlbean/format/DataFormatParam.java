package com.fiberhome.filink.commonstation.entity.xmlbean.format;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据转换参数
 * @author CongcaiYu
 */
@Data
public class DataFormatParam implements Serializable {
    /**
     * id
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 类型
     */
    private String type;
    /**
     * 长度
     */
    private Integer length;

}
