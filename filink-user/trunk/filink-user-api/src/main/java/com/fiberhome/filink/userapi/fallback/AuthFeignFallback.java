package com.fiberhome.filink.userapi.fallback;

import com.fiberhome.filink.userapi.api.AuthFeign;
import com.fiberhome.filink.userapi.bean.DeviceInfo;
import com.fiberhome.filink.userapi.bean.UserAuthInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * feign熔断
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/16 12:19 PM
 */
@Slf4j
@Component
public class AuthFeignFallback implements AuthFeign {

    @Override
    public boolean queryAuthInfoByUserIdAndDeviceAndDoor(UserAuthInfo userAUthInfo) {
        log.info("queryAuthInfoByUserIdAndDeviceAndDoor feign调用熔断》》》》》》》》》》");
        return false;
    }

    @Override
    public Integer deleteAuthByDevice(DeviceInfo deviceInfo) {
        log.info("deleteAuthByDevice feign调用熔断》》》》》》》》》》");
        return null;
    }
}
