package com.fiberhome.filink.userserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.userserver.bean.RolePermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-25
 */
public interface RolePermissionDao extends BaseMapper<RolePermission> {

    /**
     * 批量删除角色权限中间表
     *
     * @param roleId 角色id
     * @return 删除的数量
     */
    Integer batchDeleteByRoleId(@Param("roleId") String roleId);

    /**
     * 批量添加角色权限中间数据
     *
     * @param rolePermissionList 角色权限对应关系
     * @return 添加的数量
     */
    Integer batchAddRolePermission(List<RolePermission> rolePermissionList);
}
