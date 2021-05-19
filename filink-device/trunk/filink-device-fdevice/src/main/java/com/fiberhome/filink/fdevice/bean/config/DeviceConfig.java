package com.fiberhome.filink.fdevice.bean.config;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 设施配置
 *
 * @author CongcaiYu
 */
@Data
public class DeviceConfig {

    /**
     * 语言
     */
    private String language;

    /**
     * 配置集合
     */
    private Map<String, FiLinkDeviceConfig> configMap;
}
