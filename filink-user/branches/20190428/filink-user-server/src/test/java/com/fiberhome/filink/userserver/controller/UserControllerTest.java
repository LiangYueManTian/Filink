package com.fiberhome.filink.userserver.controller;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.userserver.bean.DataPermission;
import com.fiberhome.filink.userserver.bean.Parameters;
import com.fiberhome.filink.userserver.bean.PasswordDto;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.bean.UserParameter;
import com.fiberhome.filink.userserver.service.ImportService;
import com.fiberhome.filink.userserver.service.UserExtraService;
import com.fiberhome.filink.userserver.service.UserService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
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
    private ImportService importService;

    @Injectable
    private UserExtraService userExtraService;


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

        new MockUp<RedisUtils>(){
            @Mock
            String lockWithTimeout(String lockName, int acquireTimeout, int timeout) {
                return "lockIdentifier";
            }
        };

        new MockUp<RedisUtils>(){
            @Mock
            boolean releaseLock(String lockKey, String value) {
                return true;
            }
        };

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
        UserParameter userParameter = new UserParameter();
        userParameter.setUserName("123");
        userParameter.setToken("456");
        userParameter.setLoginIp("123");
        new Expectations(){
            {
                User user = userService.queryUserByName(userParameter);
            }
        };
        User user = userController.queryUserByName(userParameter);
    }

    @Test
    public void queryUserPwd() throws Exception {
        UserParameter userParameter = new UserParameter();
        userParameter.setUserName("123");

        new Expectations(){
            {
                User user = userService.queryUserByName(userParameter);
                result = user.getPassword();
            }
        };
        userController.queryUserPwd("123");
    }

    @Test
    public void updateUserStatus() throws Exception {
        int userStatus = 1;
        String[] userIds = {"123","456"};
        new Expectations(){
            {
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
    public void queryEmailIsExist() throws Exception {
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
                User user = userService.queryCurrentUser("123","456");
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
        UserParameter userParameter = new UserParameter();
        userParameterQueryCondition.setBizCondition(userParameter);

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

    @Test
    public void sendMessage() throws Exception{
        User user = new User();
        String phoneNumber = "110";
        user.setPhonenumber(phoneNumber);
        new Expectations(){
            {
                userExtraService.sendMessage(phoneNumber);;
                result = ResultUtils.success();
            }
        };

        Result result = userController.sendMessage(user);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void getSmsMessage() {
        String phoneNumber = "110";
        new Expectations(){
            {
                userExtraService.getSmsMessage(phoneNumber);;
                result = "123";
            }
        };

        userController.getSmsMessage(phoneNumber);
    }

    @Test
    public void importUserInfo() throws Exception{
        FileInputStream fileInputStream = new FileInputStream(new File("D:\\1.txt"));
        String fileName = "name";
        MultipartFile file = new MockMultipartFile(fileName,fileInputStream);
        new Expectations(){
            {
                importService.importUserInfo(file);;
                result = ResultUtils.success();
            }
        };

        Result result = userController.importUserInfo(file);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUserByIdList() {

        List<String> userIdList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        userIdList.add("123");
        new Expectations(){
            {
                userService.queryUserByIdList(userIdList);;
                result = userList;
            }
        };

        userController.queryUserByIdList(userIdList);
    }

    @Test
    public void queryUserByDeptList() {

        List<User> userList = new ArrayList<>();
        List<String> deptIdList = new ArrayList<>();
        deptIdList.add("123");
        new Expectations(){
            {
                userExtraService.queryUserByDeptList(deptIdList);;
                result = userList;
            }
        };

        userController.queryUserByDeptList(deptIdList);
    }

    @Test
    public void validateUserLogin() {
        UserParameter userParameter = new UserParameter();

        new Expectations(){
            {
                userExtraService.validateUserLogin(userParameter);;
                result = ResultCode.SUCCESS;
            }
        };

        userController.validateUserLogin(userParameter);
    }

    @Test
    public void dealLoginFail() {
        UserParameter userParameter = new UserParameter();

        new Expectations(){
            {
                userExtraService.dealLoginFail(userParameter);;
            }
        };

        userController.dealLoginFail(userParameter);
    }

    @Test
    public void exportUserList() {
        ExportDto<UserParameter> exportDto = new ExportDto<>();

        new Expectations(){
            {
                userExtraService.exportUserList(exportDto);;
            }
        };

        userController.exportUserList(exportDto);
    }

    @Test
    public void queryUserByIds() {
        List<String> idList = new ArrayList<>();

        new Expectations(){
            {
                userService.queryUserByIdList(idList);;
            }
        };

        Result result = userController.queryUserByIds(idList);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUserDetailByIds() {
        List<String> idList = new ArrayList<>();

        new Expectations(){
            {
                userService.queryUserByIdList(idList);;
            }
        };

        Result result = userController.queryUserByIds(idList);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryDeviceTypeByPermission() {
        DataPermission dataPermission = new DataPermission();

        new Expectations(){
            {
                userExtraService.queryDeviceTypeByPermission(dataPermission);;
            }
        };

        userController.queryDeviceTypeByPermission(dataPermission);
    }

    @Test
    public void queryUserByDevice() {
        List<DataPermission> dataPermissions = new ArrayList<>();

        new Expectations(){
            {
                userExtraService.queryUserByDevice(dataPermissions);;
            }
        };

        userController.queryUserByDevice(dataPermissions);
    }

    @Test
    public void queryUserByPhone() {
        String phoneNumber = "110";

        new Expectations(){
            {
                userExtraService.queryUserByPhone(phoneNumber);;
            }
        };

        userController.queryUserByPhone(phoneNumber);
    }

    @Test
    public void queryTokenByUserId() {
        String userId = "110";

        new Expectations(){
            {
                userExtraService.queryTokenByUserId(userId);;
            }
        };

        userController.queryTokenByUserId(userId);
    }

    @Test
    public void queryPhoneIdByUserIds() {
        List<String> idList = new ArrayList<>();

        new Expectations(){
            {
                userExtraService.queryPhoneIdByUserIds(idList);;
            }
        };

        userController.queryPhoneIdByUserIds(idList);
    }

    @Test
    public void queryUserIdByName() {
        String userName = "admin";

        new Expectations(){
            {
                userExtraService.queryUserIdByName(userName);;
            }
        };

        userController.queryUserIdByName(userName);
    }

    @Test
    public void queryUserNumber() {

        new Expectations(){
            {
                userExtraService.queryUserNumber();;
            }
        };

        userController.queryUserNumber();
    }

    @Test
    public void queryOnlineUserByIdList() {
        List<String> idList = new ArrayList<>();

        new Expectations(){
            {
                userExtraService.queryOnlineUserByIdList(idList);;
            }
        };

        userController.queryOnlineUserByIdList(idList);
    }

    @Test
    public void modifyUserPhoneIdAndAppKey() {

        User user = new User();

        new Expectations(){
            {
                userExtraService.modifyUserPhoneIdAndAppKey(user);;
            }
        };

        userController.modifyUserPhoneIdAndAppKey(user);
    }

    @Test
    public void queryAllUserInfo() {

        new Expectations(){
            {
                userExtraService.queryAllUserInfo();;
            }
        };

        userController.queryAllUserInfo();
    }

    @Test
    public void queryUserByPermission() {

        QueryCondition<UserParameter> queryCondition = new QueryCondition<>();
        new Expectations(){
            {
                userExtraService.queryUserByPermission(queryCondition);;
            }
        };

        userController.queryUserByPermission(queryCondition);
    }

    @Test
    public void queryUserInfoByDeptAndDeviceType() {

        DataPermission dataPermission = new DataPermission();
        new Expectations(){
            {
                userExtraService.queryUserInfoByDeptAndDeviceType(dataPermission);;
            }
        };

        userController.queryUserInfoByDeptAndDeviceType(dataPermission);
    }
}
