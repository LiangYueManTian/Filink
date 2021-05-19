package com.fiberhome.filink.userserver.controller;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userserver.bean.Parameters;
import com.fiberhome.filink.userserver.bean.Role;
import com.fiberhome.filink.userserver.bean.RoleParamter;
import com.fiberhome.filink.userserver.consts.UserI18n;
import com.fiberhome.filink.userserver.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 角色Controller层
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 查询角色列表信息
     *
     * @return 角色列表信息
     */
    @PostMapping("/queryRoleList")
    public Result queryRoleList(@RequestBody QueryCondition<Role> roleQueryCondition) {
        return roleService.queryRoleList(roleQueryCondition);
    }

    /**
     * 查询所有角色的id和名称
     *
     * @return 角色信息
     */
    @PostMapping("/queryAllRoles")
    public Result queryAllRoles() {
        return roleService.queryAllRoles();
    }

    /**
     * 查询单个角色的信息
     *
     * @param roleId 角色id
     * @return 查询结果
     */
    @PostMapping("/queryRoleInfoById/{roleId}")
    public Result queryRoleInfoById(@PathVariable("roleId") String roleId) {
        return roleService.queryRoleInfoById(roleId);
    }

    /**
     * 新增角色
     *
     * @param role 角色信息
     * @return 新增结果
     * @throws Exception
     */
    @PostMapping("/insert")
    public Result addRole(@RequestBody(required = false) Role role) throws Exception {

        return roleService.addRole(role);
    }

    /**
     * 修改角色
     *
     * @param role 角色信息
     * @return 修改结果
     * @throws Exception
     */
    @PostMapping("/update")
    public Result updateRole(@RequestBody(required = false) Role role) throws Exception {
        if (role.getId() == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(UserI18n.ROLE_ID_NOT_NULL));
        }

        return roleService.updateRole(role);
    }

    /**
     * 删除单个角色
     *
     * @param parameters 角色参数信息
     * @return 删除结果
     * @throws Exception
     */
    @PostMapping("/deleteByIds")
    public Result deleteRole(@RequestBody Parameters parameters) throws Exception {
        return roleService.deleteRole(parameters);
    }

    /**
     * 校验角色的信息
     *
     * @param roleQueryCondition 查询角色信息的条件类
     * @return 角色信息列表
     */
    @PostMapping("/verifyRoleInfo")
    public Result verifyRoleInfo(@RequestBody QueryCondition<Role> roleQueryCondition) {
        List<Role> roleList = roleService.queryRoleByField(roleQueryCondition);
        return ResultUtils.success(ResultCode.SUCCESS, null, roleList);
    }

    /**
     * 根据字段进行条件排序分页查询
     *
     * @param roleParamter 查询条件类
     * @return 角色列表
     */
    @PostMapping("/queryRoleByField")
    public Result queryRoleByField(@RequestBody QueryCondition<RoleParamter> roleParamter) {
        Result roleList = roleService.queryRoleByFieldAndCondition(roleParamter);
        return roleList;
    }
}
