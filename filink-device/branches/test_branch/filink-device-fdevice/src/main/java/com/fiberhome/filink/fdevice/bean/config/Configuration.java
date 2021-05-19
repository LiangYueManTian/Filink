package com.fiberhome.filink.fdevice.bean.config;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 配置策略项
 * @author CongcaiYu@wistronits.com
 */
@Data
public class Configuration implements Serializable {
    /**
     * 唯一标识
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 配置项集合
     */
    private List<ConfigParam> configParams;
}
