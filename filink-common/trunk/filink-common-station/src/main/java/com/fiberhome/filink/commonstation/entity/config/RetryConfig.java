package com.fiberhome.filink.commonstation.entity.config;

import lombok.Data;

/**
 * 重试配置
 * @author CongcaiYu
 */
@Data
public class RetryConfig {

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 重试时间
     */
    private Integer retryCycle;
}
