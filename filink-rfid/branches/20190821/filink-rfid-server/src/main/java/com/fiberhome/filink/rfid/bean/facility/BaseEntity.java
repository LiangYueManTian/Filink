package com.fiberhome.filink.rfid.bean.facility;

import lombok.Data;

/**
 * Created by Qing on 2019/6/6.
 * 基础实体
 */
@Data
public class BaseEntity {

    /**
     * id
     */
    private String id;
    /**
     * 模板号
     */
    private Integer mouldNo;

    /**
     * 实际号
     */
    private Integer realNo;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 是否有业务配置 0/1 (可修改在位状态/ 不可修改在位状态)
     */
    private Integer busState;
}
