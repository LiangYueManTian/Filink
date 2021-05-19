package com.fiberhome.filink.userapi.api;

import com.fiberhome.filink.userapi.bean.Permission;
import com.fiberhome.filink.userapi.fallback.PermissionFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 权限feign
 * @author  xuangong
 */
@FeignClient(name = "filink-user-server", fallback = PermissionFeignFallback.class)
public interface PermissionFeign {

    /***
     * 根据用户id列表查询权限信息   后台调用
     * @param userIds   用户id列表
     * @return     权限列表
     */
    @PostMapping("/permission/queryPermissionByUserIds")
    List<Permission> queryPermissionByUserIds(@RequestBody List<String> userIds);
}
