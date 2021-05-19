package com.fiberhome.filink.deviceapi.fallback;

import com.fiberhome.filink.deviceapi.api.DeviceFeignTest;
import com.fiberhome.filink.deviceapi.bean.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * hello  world
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/16 1:21 PM
 */
@Slf4j
@Component
public class DeviceFeignTestFallback implements DeviceFeignTest {


    @Override
    public Device feignTest() {
        log.info("feign调用熔断》》》》》》》》》》");
        throw new RuntimeException("feign调用熔断》》》》》》》》》》");
//        return null;
    }
}
