package com.fiberhome.filink.dump.fallback;

import com.fiberhome.filink.dump.api.DumpFeign;
import com.fiberhome.filink.dump.bean.DumpBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  转储策略实现类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/6/3
 */
@Slf4j
@Component
public class DumpFallback implements DumpFeign {
    /**
     * 查询转储策略
     *
     * @param type 类型
     * @return 转储策略
     */
    @Override
    public DumpBean queryDump(String type) {
        log.error("获取转储策略失败>>>>>");
        return null;
    }
}
