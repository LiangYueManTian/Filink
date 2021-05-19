package com.fiberhome.filink.deviceapi.fallback;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.api.DeviceCollectingFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceCollecting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 我的关注 feign熔断类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/18
 */
@Slf4j
@Component
public class DeviceCollectingFeignFallback implements DeviceCollectingFeign {
    private static final String INFO = "我的关注feign调用熔断》》》》》》》》》》";

    @Override
    public Result delDeviceCollectingByDeviceId(DeviceCollecting deviceCollecting) throws Exception {
        log.info(INFO);
        throw new Exception();
    }
}
