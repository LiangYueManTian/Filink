package com.fiberhome.filink.onenetserver.config;

import com.fiberhome.filink.commonstation.entity.config.RetryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 系统初始化
 *
 * @author CongcaiYu
 */
@Component
public class ConfigInit {

    @Value("${constant.retryCount}")
    private Integer retryCount;

    @Value("${constant.retryCycle}")
    private Integer retryCycle;


    /**
     * 初始化重试配置
     *
     * @return 重试配置
     */
    @Bean("oneNetRetryConfig")
    public RetryConfig initRetryConfig() {
        RetryConfig retryConfig = new RetryConfig();
        retryConfig.setRetryCount(retryCount);
        retryConfig.setRetryCycle(retryCycle);
        return retryConfig;
    }

}
