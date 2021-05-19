package com.fiberhome.filink.rfid.bean.facility;

import lombok.Data;

/**
 * Created by Qing on 2019/6/6.
 * 基础模板
 */
@Data
public class BaseMould {

    /**
     * 模板ID
     */
    private String id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板行数
     */
    private Integer row;

    /**
     * 模板列数
     */
    private Integer col;

    /**
     * 单双面
     */
    private Integer reversible;

    /**
     * 内部模板名称
     */
    private String subname;

    /**
     * 摆放状态
     */
    private Integer putState;
}
