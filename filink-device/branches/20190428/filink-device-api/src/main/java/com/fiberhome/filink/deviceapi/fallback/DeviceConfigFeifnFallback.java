package com.fiberhome.filink.deviceapi.fallback;

import com.fiberhome.filink.deviceapi.api.DeviceConfigFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>
 * 设施配置 feign 熔断类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/28
 */
@Slf4j
@Component
public class DeviceConfigFeifnFallback implements DeviceConfigFeign {
    private static final String INFO = "设施配置feign调用熔断》》》》》》》》》》";

    /**
     * 获取设施配置字段的校验规则
     *
     * @return <字段 字段检验规则>集合
     */
    @Override
    public Map<String, String> getConfigPatterns() throws Exception{
        log.info(INFO);
        throw new Exception();
    }
}
