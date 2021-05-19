package com.fiberhome.filink.userserver.dao;

import com.fiberhome.filink.userserver.bean.Role;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.userserver.bean.RoleParamter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *  角色表 Mapper 接口
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */
@Repository
public interface RoleDao extends BaseMapper<Role> {

    /**
     * 批量删除部门信息
     * @param roleIdArray 部门id
     * @return
     */
    Integer deleteRoles(@Param("roleIdArray") String[] roleIdArray);

    /**
     * 查询所有的角色信息
     * @return
     */
    List<Role> queryAllRoles();

    /**
     * 根据字段进行条件排序分页查询
     * @param roleParamter
     * @return
     */
    List<Role> queryRoleByField(RoleParamter roleParamter);

    /**
     * 查询部门数量
     * @param roleParamter
     * @return
     */
    Long queryRoleNumber(RoleParamter roleParamter);

    /**
     * 根据用户名查询角色信息
     * @param roleName 角色名
     * @return
     */
    Role verityRoleByName(@Param("roleName") String roleName);
}
