package com.fiberhome.filink.fdevice.bean.config;

import lombok.Data;

import java.util.List;

/**
 * 设施配置实体类
 * @author CongcaiYu
 */
@Data
public class FiLinkDeviceConfig {
    /**
     * 设施类型
     */
    private String deviceType;
    /**
     * 编码
     */
    private String code;
    /**
     * 详情模板集合
     */
    private List<DetailParam> detailParams;
    /**
     * 配置策略集合
     */
    private List<Configuration> configurations;
}
