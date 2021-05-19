package com.fiberhome.filink.dump.api;

import com.fiberhome.filink.dump.bean.DumpBean;
import com.fiberhome.filink.dump.fallback.DumpFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>
 *  转储策略feign
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/6/3
 */
@FeignClient(name = "filink-system-server", path = "/dump", fallback =DumpFallback.class)
public interface DumpFeign {
    /**
     * 查询转储策略
     *
     * @param type 类型
     * @return 转储策略
     */
    @GetMapping("/feign/{type}")
    DumpBean queryDump(@PathVariable("type") String type);
}
