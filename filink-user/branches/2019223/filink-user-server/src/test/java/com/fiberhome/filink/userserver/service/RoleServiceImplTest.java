package com.fiberhome.filink.userserver.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.MpQueryHelper;
import com.fiberhome.filink.userserver.bean.Parameters;
import com.fiberhome.filink.userserver.bean.Role;
import com.fiberhome.filink.userserver.bean.RoleParamter;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.dao.RoleDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.service.impl.RoleServiceImpl;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(JMockit.class)
public class RoleServiceImplTest {

    @Tested
    private RoleServiceImpl roleService;

    @Injectable
    private LogProcess logProcess;

    @Injectable
    private RoleDao roleDao;

    @Injectable
    UserDao userDao;

    @Test
    public void queryRoleList() throws Exception {


        QueryCondition<Role> roleQueryCondition = new QueryCondition<>();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(0);
        pageCondition.setPageSize(5);
        roleQueryCondition.setPageCondition(pageCondition);
        Page page = MpQueryHelper.MyBatiesBuildPage(roleQueryCondition);

//        EntityWrapper entityWrapper = new EntityWrapper<>();
//        List<Role> roleList = new ArrayList<>();
//        Role role = new Role();
//        role.setId("123");
//        roleList.add(role);

/*        new Expectations(){
            {
                roleDao.selectPage(page,entityWrapper);
                result = roleList;
            }
        };

        new Expectations(){
            {
                roleDao.selectCount(entityWrapper);
                result = 1;
            }
        };*/

        roleService.queryRoleList(roleQueryCondition);
    }

    @Test
    public void queryRoleInfoByIdTest(){

        String roleId = "1";
        Role role = new Role();
        role.setId("1");
        role.setRoleName("123");

        new Expectations(){
            {
                roleDao.selectById(roleId);
                result = role;
            }
        };

        roleService.queryRoleInfoById(roleId);
    }

    @Test
    public void addRole() throws Exception {

        Role role = new Role();
        role.setRoleName("123");

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };

        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "ADD_ROLE_SUCCESS";
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result result = new Result();
                result.setCode(4);
                result.setMsg("success");
                return result;
            }
        };

        new Expectations(){
            {
                roleDao.verityRoleByName("123");
                result = null;
            }
        };


        new Expectations(){
            {
                roleDao.insert(role);
                result = 1;
            }
        };
        roleService.addRole(role);
    }

    @Test
    public void updateRole() throws Exception {

        Role role = new Role();
        role.setId("123");
        role.setRoleName("123");

        Role role1 = new Role();
        role1.setId("123");
        role1.setRoleName("456");

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };

        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "UPDATE_ROLE_SUCCESS";
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result result = new Result();
                result.setCode(2);
                result.setMsg("success");
                return result;
            }
        };


        new Expectations(){
            {
                roleDao.selectById(role.getId());
                result = role;
            }
        };

        new Expectations(){
            {
                roleDao.updateById(role1);
                result = 1;
            }
        };
        roleService.updateRole(role1);
    }

    @Test
    public void deleteRole() throws Exception {
        Parameters parameters = new Parameters();
        String[] ids = {"123"};
        parameters.setFirstArrayParamter(ids);
        User user = new User();
        user.setId("123");
        List<User> users = new ArrayList<>();
        List<Role> roles = new ArrayList<>();
        users.add(user);

        Role role = new Role();
        role.setId("123");
        role.setDefaultRole(0);
        roles.add(role);

        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "DELETE_ROLE_SUCCESS";
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result result = new Result();
                result.setCode(3);
                result.setMsg("success");
                return result;
            }
        };


        new Expectations(){
            {
                userDao.queryUserByRoles(parameters.getFirstArrayParamter());
                result = null;
            }
        };

        new Expectations(){
            {
                roleDao.selectBatchIds(Arrays.asList(parameters.getFirstArrayParamter()));
                result = roles;
            }
        };

        new Expectations(){
            {
                roleDao.deleteRoles(parameters.getFirstArrayParamter());
                result = 1;
            }
        };

        roleService.deleteRole(parameters);
    }

    @Test
    public void queryRoleByField() throws Exception {

        QueryCondition<Role> roleQueryCondition = new QueryCondition<>();
        List<FilterCondition> filterConditions = new ArrayList<>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("id");
        filterCondition.setOperator("eq");
        filterCondition.setFilterValue("1");
        filterConditions.add(filterCondition);

        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(5);
        roleQueryCondition.setFilterConditions(filterConditions);
        roleQueryCondition.setPageCondition(pageCondition);

        roleService.queryRoleByField(roleQueryCondition);
    }

    @Test
    public void queryAllRoles() throws Exception {

        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setId("123");
        role.setRoleName("456");
        roles.add(role);

        new Expectations(){
            {
                roleDao.queryAllRoles();
                result = roles;
            }
        };
        roleService.queryAllRoles();
    }

    @Test
    public void queryRoleByFieldAndCondition() throws Exception {

        QueryCondition<RoleParamter> roleParamterQueryCondition = new QueryCondition<>();
        RoleParamter roleParamter = new RoleParamter();
        roleParamterQueryCondition.setBizCondition(roleParamter);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(5);
        roleParamterQueryCondition.setPageCondition(pageCondition);

        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setId("123");
        role.setRoleName("456");
        roles.add(role);

        new Expectations(){
            {
                roleDao.queryRoleByField(roleParamter);
                result = roles;
            }
        };

        new Expectations(){
            {
                roleDao.queryRoleNumber(roleParamter);
                result = 1;
            }
        };

        roleService.queryRoleByFieldAndCondition(roleParamterQueryCondition);
    }

}