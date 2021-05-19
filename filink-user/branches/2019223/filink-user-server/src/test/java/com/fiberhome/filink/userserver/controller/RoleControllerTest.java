package com.fiberhome.filink.userserver.controller;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.userserver.bean.Parameters;
import com.fiberhome.filink.userserver.bean.Role;
import com.fiberhome.filink.userserver.bean.RoleParamter;
import com.fiberhome.filink.userserver.service.RoleService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class RoleControllerTest {

    @Tested
    private RoleController roleController;

    @Injectable
    private RoleService roleService;


    @Test
    public void queryRoleList() throws Exception {

        QueryCondition<Role> roleQueryCondition = new QueryCondition<>();
        new Expectations(){
            {
                roleService.queryRoleList(roleQueryCondition);
                result = ResultUtils.success();
            }
        };
        Result result = roleController.queryRoleList(roleQueryCondition);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryAllRoles() throws Exception {
        new Expectations(){
            {
                roleService.queryAllRoles();
                result = ResultUtils.success();
            }
        };
        Result result = roleController.queryAllRoles();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryRoleInfoById() throws Exception {

        new Expectations(){
            {
                roleService.queryRoleInfoById("123");
                result = ResultUtils.success();
            }
        };
        Result result = roleController.queryRoleInfoById("123");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

    }

    @Test
    public void addRole() throws Exception {
        Role role = new Role();
        role.setRoleName("123");
        role.setId("123");

        new Expectations(){
            {
                roleService.addRole(role);
                result = ResultUtils.success();
            }
        };
        Result result = roleController.addRole(role);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void updateRole() throws Exception {

        Role role = new Role();
        role.setId("123");

        new Expectations(){
            {
                roleService.updateRole(role);
                result = ResultUtils.success();
            }
        };
        Result result = roleController.updateRole(role);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void deleteRole() throws Exception {

        Parameters parameters = new Parameters();

        new Expectations(){
            {
                roleService.deleteRole(parameters);
                result = ResultUtils.success();
            }
        };
        Result result = roleController.deleteRole(parameters);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void verifyRoleInfo() throws Exception {

        QueryCondition<Role> roleQueryCondition = new QueryCondition<>();

        new Expectations(){
            {
                roleService.queryRoleByField(roleQueryCondition);
                result = ResultUtils.success();
            }
        };
        Result result = roleController.verifyRoleInfo(roleQueryCondition);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryRoleByField() throws Exception{
        QueryCondition<RoleParamter> roleParamterQueryCondition = new QueryCondition<>();
        RoleParamter roleParamter = new RoleParamter();
        roleParamterQueryCondition.setBizCondition(roleParamter);

        new Expectations(){
            {
                roleService.queryRoleByFieldAndCondition(roleParamterQueryCondition);
                result = ResultUtils.success();
            }
        };
        Result result = roleController.queryRoleByField(roleParamterQueryCondition);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

}