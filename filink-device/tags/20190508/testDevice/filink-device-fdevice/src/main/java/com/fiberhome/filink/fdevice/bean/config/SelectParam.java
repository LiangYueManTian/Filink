package com.fiberhome.filink.fdevice.bean.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 配置策略下拉框参数
 * @author CongcaiYu@wistrionits.com
 */
@Data
public class SelectParam implements Serializable {
    /**
     * id
     */
    private String id;
    /**
     * 名称
     */
    private String name;
}
