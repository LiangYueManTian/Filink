package com.fiberhome.filink.userserver.controller;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.userserver.bean.Parameters;
import com.fiberhome.filink.userserver.bean.PasswordDto;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.bean.UserParameter;
import com.fiberhome.filink.userserver.service.UserService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(JMockit.class)
public class UserControllerTest {

    @Tested
    private UserController userController;

    @Injectable
    private UserService userService;

    @Injectable
    private StringRedisTemplate stringRedisTemplate;

    private User user;

    private List<User> list = new ArrayList<>();

    @Test
    public void queryUserInfoById() throws Exception {
        new Expectations(){
            {
                userService.queryUserInfoById("123");
                result = ResultUtils.success();
            }
        };
        userController.queryUserInfoById("123");
    }

    @Test
    public void addUser() throws Exception {
        User user = new User();
        new Expectations(){
            {
                userService.addUser(user);
                result = ResultUtils.success();
            }
        };
        Result result = userController.addUser(user);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void updateUser() throws Exception {
        User user = new User();
        new Expectations(){
            {
                userService.updateUser(user);
                result = ResultUtils.success();
            }
        };
        Result result = userController.updateUser(user);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void deleteUser() throws Exception {
        Parameters parameters = new Parameters();
        new Expectations(){
            {
                userService.deleteUser(parameters);
                result = ResultUtils.success();
            }
        };
        Result result = userController.deleteUser(parameters);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUserByNmae() throws Exception {
        new Expectations(){
            {
                User user = userService.queryUserByNmae("123","456","123");
            }
        };
        userController.queryUserByNmae("123", "456", "123");
    }

    @Test
    public void queryUserPwd() throws Exception {
        new Expectations(){
            {
                User user = userService.queryUserByNmae("123",null,null);
                result = user.getPassword();
            }
        };
        String pwd = userController.queryUserPwd("123");
    }

    @Test
    public void updateUserStatus() throws Exception {
        int userStatus = 1;
        String[] userIds = {"123","456"};
        new Expectations(){
            {
                Result result1 = userService.updateUserStatus(userStatus, userIds);
                result = ResultUtils.success();
            }
        };
        Result result1 = userController.updateUserStatus(userStatus, userIds);
        Assert.assertTrue(result1.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void verifyUserInfo() throws Exception {
        new Expectations(){
            {
                userService.queryUserByField((QueryCondition<User>) any);
                result = ResultUtils.success();
            }
        };
        Result result = userController.verifyUserInfo(new QueryCondition<>());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void modifyPWD() throws Exception {
        PasswordDto passwordDto = new PasswordDto();
        new Expectations(){
            {
                userService.modifyPWD(passwordDto);
                this.result = ResultUtils.success();
            }
        };
        Result result = userController.modifyPWD(passwordDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void resetPWD() throws Exception {
        PasswordDto passwordDto = new PasswordDto();
        new Expectations(){
            {
                userService.resetPWD(passwordDto);
                this.result = ResultUtils.success();
            }
        };
        Result result = userController.resetPWD(passwordDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void getOnLineUser() throws Exception {
        new Expectations(){
            {
                userService.getOnLineUser(null);
                result = ResultUtils.success();
            }
        };
        Result onLineUser = userController.getOnLineUser(null);
        Assert.assertTrue(onLineUser.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryCurrentUser() throws Exception {
        new Expectations(){
            {
                User user = userService.queryCurrentUser("123", "456");
                result = user;
            }
        };
        userController.queryCurrentUser("123","456");
    }

    @Test
    public void forceOffline() throws Exception {
        Map<String, String> idMap = new HashMap<>();
        idMap.put("123","456");
        new Expectations(){
            {
                userService.forceOffline(idMap);
                result = ResultUtils.success();
            }
        };
        Result result = userController.forceOffline(idMap);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void logout() throws Exception {

        new Expectations(){
            {
                userService.logout("123","456");
                result = ResultUtils.success();
            }
        };
        userController.logout("123","456");
    }

    @Test
    public void queryUserByField() throws  Exception{

        QueryCondition<UserParameter> userParameterQueryCondition = new QueryCondition<>();

        new Expectations(){
            {
                userService.queryUserByFieldAndCondition(userParameterQueryCondition);
                result = ResultUtils.success();
            }
        };
        userController.queryUserByField(userParameterQueryCondition);
    }

    @Test
    public void queryUserDefaultPWD() throws  Exception{

        new Expectations(){
            {
                userService.queryUserDefaultPWD();
                result = ResultUtils.success();
            }
        };
        userController.queryUserDefaultPWD();
    }

    @Test
    public void queryUserByDept(){
        Parameters parameters = new Parameters();
        new Expectations(){
            {
                userService.queryUserByDept(parameters);
                result = ResultUtils.success();
            }
        };
        userController.queryUserByDept(parameters);
    }

    @Test
    public void updateLoginTime() {
        String userId = "1";
        String token = "123";
        new Expectations(){
            {
                userService.updateLoginTime(userId,token);
                result = true;
            }
        };

        userController.updateLoginTime(userId,token);
    }

    @Test
    public void queryUserById() {

        String userId = "1";
        new Expectations(){
            {
                userService.queryUserById(userId);;
                result = true;
            }
        };

        userController.queryUserById(userId);
    }
}
