package com.fiberhome.filink.userserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.userserver.bean.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 权限相关的信息 Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-25
 */
public interface PermissionDao extends BaseMapper<Permission> {

    /**
     * 查询最上层的权限列表
     * @return  权限列表
     */
    List<Permission> queryTopPermission();

    /**
     * 根据id查询下级权限信息
     * @param parentId  权限id
     * @return  权限列表
     */
    List<Permission> queryPermissionByParentId(@Param("parentId") String parentId);

    /**
     * 根据用户id获取权限列表信息
     * @param userIds   用户id列表
     * @return  权限列表信息
     */
    List<Permission> queryPermissionByUserIds(List<String> userIds);

    /**
     * 根据权限id获取权限信息
     * @param idList id列表
     * @return
     */
    List<Permission> queryPermissionByIds(List<String> idList);
}
