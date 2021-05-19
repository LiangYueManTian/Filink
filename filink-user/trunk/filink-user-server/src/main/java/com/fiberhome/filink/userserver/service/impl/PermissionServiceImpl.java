package com.fiberhome.filink.userserver.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.userserver.bean.Permission;
import com.fiberhome.filink.userserver.dao.PermissionDao;
import com.fiberhome.filink.userserver.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 权限相关的信息 服务实现类
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-02-25
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionDao, Permission> implements PermissionService {

    @Autowired
    private PermissionDao permissionDao;

    /**
     * 获取顶层的权限列表
     *
     * @return 权限列表
     */
    @Override
    public Result queryTopPermission() {

        List<Permission> permissions = permissionDao.queryTopPermission();
        return ResultUtils.success(ResultCode.SUCCESS, null, permissions);
    }

    /**
     * 根据id查询下级权限信息
     *
     * @param parentId 权限id
     * @return 权限列表
     */
    @Override
    public Result queryPermissionByParentId(String parentId) {

        List<Permission> permissionList = permissionDao.queryPermissionByParentId(parentId);
        return ResultUtils.success(ResultCode.SUCCESS, null, permissionList);
    }

    /**
     * 根据用户id列表查询权限列表信息
     *
     * @param userIds 用户id列表
     * @return 权限列表信息
     */
    @Override
    public List<Permission> queryPermissionByUserIds(List<String> userIds) {

        return permissionDao.queryPermissionByUserIds(userIds);
    }


}
