package com.fiberhome.filink.userserver.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.userserver.bean.Parameters;
import com.fiberhome.filink.userserver.bean.Role;
import com.fiberhome.filink.userserver.bean.RoleParamter;

import java.util.List;

/**
 * 角色服务类
 *
 * @author xuangong
 * @since 2019-01-03
 */
public interface RoleService extends IService<Role> {

    /**
     * 查询角色列表信息
     *
     * @param roleQueryCondition
     * @return 角色列表信息
     */
    Result queryRoleList(QueryCondition<Role> roleQueryCondition);

    /**
     * 查询单个角色的信息
     *
     * @param roleId 角色id
     * @return 角色信息
     */
    Result queryRoleInfoById(String roleId);

    /**
     * 新增角色
     *
     * @param role 角色信息
     * @return 新增结果
     * @throws Exception
     */
    Result addRole(Role role);

    /**
     * 修改角色
     *
     * @param role 角色信息
     * @return 修改结果
     * @throws Exception
     */
    Result updateRole(Role role);

    /**
     * 删除单个角色
     *
     * @param parameters 角色id
     * @return 删除结果
     * @throws Exception
     */
    Result deleteRole(Parameters parameters);

    /**
     * 通过自定字段信息查询用户
     *
     * @param userQueryCondition 查询条件
     * @return 角色信息列表
     */
    List<Role> queryRoleByField(QueryCondition<Role> userQueryCondition);

    /**
     * 查询所有角色
     *
     * @return 角色信息列表
     */
    Result queryAllRoles();

    /**
     * 根据字段进行条件排序分页查询
     *
     * @param roleParamter 角色信息参数
     * @return 角色信息列表
     */
    Result queryRoleByFieldAndCondition(QueryCondition<RoleParamter> roleParamter);
}
