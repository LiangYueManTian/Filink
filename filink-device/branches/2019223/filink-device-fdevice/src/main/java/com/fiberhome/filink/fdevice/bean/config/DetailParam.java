package com.fiberhome.filink.fdevice.bean.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 设施详情显示板块
 * @author CongcaiYu@wistronits.com
 */
@Data
public class DetailParam implements Serializable {
    /**
     * id
     */
    private String id;
    /**
     * 名称
     */
    private String name;

}
