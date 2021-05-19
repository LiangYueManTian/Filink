package com.fiberhome.filink.systemcommonsapi.fallback;

import com.fiberhome.filink.systemcommonsapi.api.SysParamLanguageFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 系统参数系统语言远程调用熔断
 * @author chaofang@wistronits.com
 * @since 2019-06-04
 */
@Slf4j
@Component
public class SysParamLanguageFeignFallback implements SysParamLanguageFeign {

    /**
     * 查询系统语言
     *
     * @return 系统语言
     */
    @Override
    public String querySystemLanguage() {
        log.error("query System language fallback>>>>>>>>>>>>>>>>>");
        return null;
    }
}
