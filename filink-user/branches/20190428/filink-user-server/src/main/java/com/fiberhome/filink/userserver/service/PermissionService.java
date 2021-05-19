package com.fiberhome.filink.userserver.service;


import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.userserver.bean.Permission;

import java.util.List;

/**
 * <p>
 * 权限相关的信息 服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-25
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 获取最顶层的权限列表
     * @return  权限列表
     */
    Result queryTopPermission();

    /**
     * 根据id查询下级权限信息
     * @param parentId  权限id
     * @return  权限列表
     */
    Result queryPermissionByParentId(String parentId);

    /**
     * 根据用户id查询权限列表
     * @param userIds   用户id列表
     * @return  权限列表信息
     */
    List<Permission> queryPermissionByUserIds(List<String> userIds);
}
