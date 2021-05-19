package com.fiberhome.filink.userserver.controller;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.userserver.bean.Permission;
import com.fiberhome.filink.userserver.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 权限相关的信息 前端控制器
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-25
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取最顶层的权限列表
     *
     * @return 权限列表
     */
    @PostMapping("/queryTopPermission")
    public Result queryTopPermission() {

        return permissionService.queryTopPermission();
    }

    /**
     * 根据id查询下级权限信息
     *
     * @param parentId 权限id
     * @return 权限列表
     */
    @PostMapping("/queryByParentId/{parentId}")
    public Result queryPermissionByParentId(@PathVariable String parentId) {

        return permissionService.queryPermissionByParentId(parentId);
    }

    /***
     * 根据用户id列表查询权限信息   后台调用
     * @param userIds   用户id列表
     * @return 权限列表
     */
    @PostMapping("/queryPermissionByUserIds")
    public List<Permission> queryPermissionByUserIds(@RequestBody List<String> userIds) {

        return permissionService.queryPermissionByUserIds(userIds);
    }
}
