package com.fiberhome.filink.systemcommonsapi.api;

import com.fiberhome.filink.systemcommonsapi.fallback.SysParamLanguageFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 系统参数系统语言远程调用
 * @author chaofang@wistronits.com
 * @since 2019-06-04
 */
@FeignClient(name = "filink-system-server", path = "/systemParam", fallback = SysParamLanguageFeignFallback.class)
public interface SysParamLanguageFeign {
    /**
     * 查询系统语言
     * @return 系统语言
     */
    @GetMapping("/querySystemLanguage")
    String querySystemLanguage();
}
