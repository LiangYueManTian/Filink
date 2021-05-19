package com.fiberhome.filink.userapi.api;

import com.fiberhome.filink.userapi.bean.DeviceInfo;
import com.fiberhome.filink.userapi.bean.UserAuthInfo;
import com.fiberhome.filink.userapi.fallback.AuthFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 授权feign
 *
 * @author xgong
 */
@FeignClient(name = "filink-user-server", fallback = AuthFeignFallback.class, path = "/unifyauth")
public interface AuthFeign {

    /**
     * 根据用户信息，门锁id和设施类型来查询授权信息
     *
     * @param userAUthInfo 用户设施门锁信息
     * @return
     */
    @PostMapping("/queryAuthInfoByUserIdAndDeviceAndDoor")
    boolean queryAuthInfoByUserIdAndDeviceAndDoor(@RequestBody UserAuthInfo userAUthInfo);

    /**
     * 根据设施信息删除授权信息
     *
     * @param deviceInfo 设施信息
     * @return 删除的数量
     */
    @PostMapping("/deleteAuthByDevice")
    Integer deleteAuthByDevice(@RequestBody DeviceInfo deviceInfo);
}
