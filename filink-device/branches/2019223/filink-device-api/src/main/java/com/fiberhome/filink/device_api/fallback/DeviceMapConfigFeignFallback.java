package com.fiberhome.filink.device_api.fallback;

import com.fiberhome.filink.device_api.api.DeviceMapConfigFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 *  首页地图和设施类型配置
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-02-12
 */
@Slf4j
@Component
public class DeviceMapConfigFeignFallback implements DeviceMapConfigFeign {
    /**
     * 批量插入用户首页地图设施配置信息
     *
     * @param userId 用户Id
     * @return 插入条数
     */
    @Override
    public boolean insertConfigBatch(String userId) {
        log.info("feign调用熔断》》》》》》》》》》");
        return false;
    }

    /**
     * 删除用户所有配置信息
     *
     * @param userIds 用户Id List
     * @return 成功
     */
    @Override
    public boolean deletedConfigByUserIds(List<String> userIds) {
        log.info("feign调用熔断》》》》》》》》》》");
        return false;
    }
}
