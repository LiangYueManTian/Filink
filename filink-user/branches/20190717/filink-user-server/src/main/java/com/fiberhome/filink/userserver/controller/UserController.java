package com.fiberhome.filink.userserver.controller;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.userserver.bean.DataPermission;
import com.fiberhome.filink.userserver.bean.OnlineParameter;
import com.fiberhome.filink.userserver.bean.Parameters;
import com.fiberhome.filink.userserver.bean.PasswordDto;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.bean.UserCount;
import com.fiberhome.filink.userserver.bean.UserParameter;
import com.fiberhome.filink.userserver.service.ImportService;
import com.fiberhome.filink.userserver.service.UserExtraService;
import com.fiberhome.filink.userserver.service.UserService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 用户Controller层
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImportService importService;

    @Autowired
    private UserExtraService userExtraService;


    /**
     * 查询单个用户的信息
     *
     * @param userId 用户id
     * @return 查询结果
     */
    @PostMapping("/queryUserInfoById/{userId}")
    public Result queryUserInfoById(@PathVariable("userId") String userId) {

        return userService.queryUserInfoById(userId);
    }


    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 新增结果
     * @throws Exception
     */
    @PostMapping("/insert")
    public Result addUser(@RequestBody(required = false) User user) throws Exception {

        return userService.addUser(user);
    }

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 修改结果
     * @throws Exception
     */
    @PostMapping("/update")
    public Result updateUser(@RequestBody(required = false) User user) throws Exception {

        return userService.updateUser(user);
    }

    /**
     * 批量删除用户
     *
     * @param deleteUser 要删除的用户信息
     * @return 删除结果
     */
    @PostMapping("/deleteByIds")
    public Result deleteUser(@RequestBody Parameters deleteUser) {

        return userService.deleteUser(deleteUser);
    }

    /**
     * 根据用户名，token和ip存储用户登录信息
     *
     * @param userParameter 用户信息
     * @return 用户信息
     */
    @PostMapping("/queryUserByParameter")
    public User queryUserByName(@RequestBody UserParameter userParameter) {

        User user = userService.queryUserByName(userParameter);

        return user;
    }

    /**
     * 根据用户名查询用户密码
     *
     * @param userName 用户名
     * @return 用户密码
     */
    @GetMapping("/queryUserPwd/{userName}")
    public String queryUserPwd(@PathVariable("userName") String userName) {

        UserParameter userParameter = new UserParameter();
        userParameter.setUserName(userName);
        User user = userService.queryUserByName(userParameter);

        return user.getPassword();
    }

    /**
     * 批量更新用户的状态
     *
     * @param userStatus  用户状态
     * @param userIdArray 需要修改状态的id数组
     * @return 更新结果
     */
    @GetMapping("/updateUserStatus")
    public Result updateUserStatus(@RequestParam Integer userStatus, @RequestParam String[] userIdArray) {
        return userService.updateUserStatus(userStatus, userIdArray);
    }

    /**
     * 校验数据的唯一性
     *
     * @param userQueryCondition 需要校验的信息
     * @return 用户信息列表
     */
    @PostMapping("/verifyUserInfo")
    public Result verifyUserInfo(@RequestBody QueryCondition<User> userQueryCondition) {
        List<User> userList = userService.queryUserByField(userQueryCondition);
        return ResultUtils.success(ResultCode.SUCCESS, null, userList);
    }

    /**
     * 校验数据的唯一性
     *
     * @param userQueryCondition 校验邮箱信息
     * @return 用户信息列表
     */
    @PostMapping("/queryEmailIsExist")
    public Result queryEmailIsExist(@RequestBody QueryCondition<User> userQueryCondition) {

        return ResultUtils.success(userService.queryUserByField(userQueryCondition));
    }

    /**
     * 修改用户密码
     *
     * @param pwd 用户输入的密码对象
     * @return 修改密码的结果
     */
    @PostMapping("/modifyPWD")
    public Result modifyPWD(@RequestBody PasswordDto pwd) {
        return userService.modifyPWD(pwd);
    }

    /**
     * 重置用户密码
     *
     * @param pwd 用户输入的密码集
     * @return 返回对象集
     */
    @PostMapping("/resetPWD")
    public Result resetPWD(@RequestBody PasswordDto pwd) {
        return userService.resetPWD(pwd);
    }

    /**
     * 获取在线用户信息
     *
     * @param onlineParameter 在线用户列表参数
     * @return 在线用户信息
     */
    @PostMapping("/getOnLineUser")
    public Result getOnLineUser(@RequestBody QueryCondition<OnlineParameter> onlineParameter) {
        return userService.getOnLineUser(onlineParameter);
    }

    /**
     * 根据根据用户id和token获取当前用户详细信息
     *
     * @return 用户信息
     */
    @GetMapping("/queryCurrentUser/{userId}/{token}")
    public User queryCurrentUser(@PathVariable("userId") String userId, @PathVariable("token") String token) {
        return userService.queryCurrentUser(userId, token);
    }

    /**
     * 将用户强制下线
     *
     * @param userMap 结构为(userid_token,userid);
     * @return 强制下线结果
     */
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    @PostMapping("/forceOffline")
    public Result forceOffline(@RequestBody Map<String, String> userMap) {

        return userService.forceOffline(userMap);
    }

    /**
     * 用户登出
     *
     * @param userId 登出用户的id
     * @param token  登出用户的token
     * @return 用户登出结果
     */
    @GetMapping("/logout/{userId}/{token}")
    public Result logout(@PathVariable("userId") String userId, @PathVariable("token") String token) {
        return userService.logout(userId, token);
    }

    /**
     * 根据字段进行添加排序分页查询
     *
     * @param userParameter 查询条件
     * @return 用户结果集
     */
    @PostMapping("/queryUserByField")
    public Result queryUserByField(@RequestBody QueryCondition<UserParameter> userParameter) {
        Result userList = userService.queryUserByFieldAndCondition(userParameter);
        return userList;
    }

    /**
     * 获取用户默认密码
     *
     * @return 用户默认密码
     */
    @PostMapping("/queryUserDefaultPWD")
    public Result queryUserDefaultPWD() {
        return userService.queryUserDefaultPWD();
    }

    /**
     * 根据部门查询用户信息
     *
     * @param parameters 部门信息
     * @return 用户信息
     */
    @PostMapping("/queryUserByDept")
    public Result queryUserByDept(@RequestBody Parameters parameters) {
        return userService.queryUserByDept(parameters);
    }


    /**
     * 查询单个用户的信息
     *
     * @param userId 用户id
     * @return 查询结果
     */
    @GetMapping("/queryUserById/{userId}")
    public boolean queryUserById(@PathVariable("userId") String userId) {

        return userService.queryUserById(userId);
    }

    /**
     * 更新用户的有效时间
     *
     * @param userId 用户id
     * @param token  用户token
     * @return 更新的结果
     */
    @GetMapping("/updateLoginTime/{userId}/{token}")
    public boolean updateLoginTime(@PathVariable("userId") String userId, @PathVariable("token") String token) {

        return userService.updateLoginTime(userId, token);
    }

    /**
     * 发送短信验证码
     *
     * @param user 用户信息
     * @return 验证码
     * @throws Exception
     */
    @PostMapping("/sendMessage")
    public Result sendMessage(@RequestBody User user) throws Exception {

        return userExtraService.sendMessage(user.getPhoneNumber());

    }

    /**
     * 查询短信验证码
     *
     * @return 验证码
     */
    @GetMapping("/getSmsMessage/{phoneNumber}")
    public String getSmsMessage(@PathVariable("phoneNumber") String phoneNumber) {

        return userExtraService.getSmsMessage(phoneNumber);
    }

    @CrossOrigin
    @PostMapping("/importUserInfo")
    public Result importUserInfo(@RequestBody MultipartFile file) throws IOException, InvalidFormatException {

        return importService.importUserInfo(file);
    }

    /**
     * 根据id集合批量查询用户信息       后台远程调用
     *
     * @param idList 用户id集合
     * @return 用户信息
     */
    @PostMapping("/queryUserByIdList")
    public List<User> queryUserByIdList(@RequestBody List<String> idList) {

        return userService.queryUserByIdList(idList);
    }

    /**
     * 获取多个部门下的人员信息
     *
     * @param deptIdList 部门id集合
     * @return 人员集合
     */
    @PostMapping("/queryUserByDeptList")
    public List<User> queryUserByDeptList(@RequestBody List<String> deptIdList) {

        return userExtraService.queryUserByDeptList(deptIdList);
    }

    /**
     * 验证当前用户是否能够登陆
     *
     * @param userParameter 登录用户信息
     * @return code码
     */
    @PostMapping("/validateUserLogin")
    public Integer validateUserLogin(@RequestBody UserParameter userParameter) {

        return userExtraService.validateUserLogin(userParameter);
    }


    /**
     * 处理用户登录失败的情况
     *
     * @param userParameter 用户信息
     */
    @PostMapping("/dealLoginFail")
    public void dealLoginFail(@RequestBody UserParameter userParameter) {

        userExtraService.dealLoginFail(userParameter);
    }

    /**
     * 导出用户列表信息
     *
     * @param exportDto 导出用户列表的参数实体
     * @return 导出的结果信息
     */
    @PostMapping("/exportUserList")
    public Result exportUserList(@RequestBody ExportDto<UserParameter> exportDto) {

        return userExtraService.exportUserList(exportDto);
    }

    /**
     * 根据id集合批量查询用户信息       后台远程调用
     *
     * @param idList 用户id集合
     * @return 用户信息
     */
    @PostMapping("/queryUserByIds")
    public Result queryUserByIds(@RequestBody List<String> idList) {

        List<User> users = userService.queryUserByIdList(idList);
        return ResultUtils.success(users);
    }

    /**
     * 根据id集合批量查询用户信息       前端调用
     *
     * @param idList 用户id集合
     * @return 用户信息
     */
    @PostMapping("/queryUserDetailByIds")
    public Result queryUserDetailByIds(@RequestBody List<String> idList) {

        List<User> users = userService.queryUserByIdList(idList);
        return ResultUtils.success(users);
    }

    /**
     * 根据部门和设施类型查询人员信息
     *
     * @param dataPermission 数据权限信息
     * @return 在线用户的token列表
     */
    @PostMapping("/queryDeviceTypeByPermission")
    public List<String> queryDeviceTypeByPermission(@RequestBody DataPermission dataPermission) {

        return userExtraService.queryDeviceTypeByPermission(dataPermission);
    }

    /**
     * 根据设施获取用户信息
     *
     * @param dataPermissionList 设施信息列表
     * @return
     */
    @PostMapping("/queryUserByDevice")
    public Map<String, List<String>> queryUserByDevice(@RequestBody List<DataPermission> dataPermissionList) {

        return userExtraService.queryUserByDevice(dataPermissionList);
    }

    /**
     * 根据手机号获取用户信息
     *
     * @param phoneNumber 手机号
     * @return
     */
    @GetMapping("/queryUserByPhone/{phoneNumber}")
    public User queryUserByPhone(@PathVariable("phoneNumber") String phoneNumber) {

        return userExtraService.queryUserByPhone(phoneNumber);
    }

    /**
     * 根据用户id获取所有的token值
     *
     * @param userId 用户id
     * @return token列表
     */
    @GetMapping("/queryTokenByUserId/{userId}")
    public List<String> queryTokenByUserId(@PathVariable("userId") String userId) {

        return userExtraService.queryTokenByUserId(userId);
    }

    /**
     * 根据用户id列表获取登录设备信息
     *
     * @param idList id列表
     * @return 登录设备id列表
     */
    @PostMapping("/queryPhoneIdByUserIds")
    public List<String> queryPhoneIdByUserIds(@RequestBody List<String> idList) {

        return userExtraService.queryPhoneIdByUserIds(idList);
    }

    /**
     * 根据用户名获取用户id信息
     *
     * @param userName 用户名
     * @return 用户id列表
     */
    @GetMapping("/queryUserIdByName/{userName}")
    public List<String> queryUserIdByName(@PathVariable("userName") String userName) {

        return userExtraService.queryUserIdByName(userName);
    }

    /**
     * 获取账号数和在线用户数
     *
     * @return 用户数量实体类
     */
    @PostMapping("/queryUserNumber")
    public UserCount queryUserNumber() {

        return userExtraService.queryUserNumber();
    }

    /**
     * 根据用户id列表，获取所有的在线用户信息, 后台调用
     *
     * @param idList 用户id列表
     * @return 用户列表
     */
    @PostMapping("/queryOnlineUserByIdList")
    public List<User> queryOnlineUserByIdList(@RequestBody List<String> idList) {

        return userExtraService.queryOnlineUserByIdList(idList);
    }

    /**
     * 修改在线用户的手机设备id和appkey值
     *
     * @param user 登录用户需要修改的信息
     * @return 修改的结果
     */
    @PostMapping("/modifyUserPhoneIdAndAppKey")
    public Result modifyUserPhoneIdAndAppKey(@RequestBody User user) {

        return userExtraService.modifyUserPhoneIdAndAppKey(user);
    }

    /**
     * 获取所有的用户信息
     *
     * @return
     */
    @PostMapping("/queryAllUserInfo")
    public Result queryAllUserInfo() {

        return userExtraService.queryAllUserInfo();
    }

    /**
     * 查询带有指定权限的用户信息
     *
     * @return 用户信息列表
     */
    @PostMapping("/queryUserByPermission")
    public Result queryUserByPermission(@RequestBody QueryCondition<UserParameter> userParameter) {

        return userExtraService.queryUserByPermission(userParameter);
    }

    /**
     * 根据部门和设施类型获取人员信息
     *
     * @param dataPermission 部门和设施类型信息
     * @return 人员列表
     */
    @PostMapping("/queryUserInfoByDeptAndDeviceType")
    public Result queryUserInfoByDeptAndDeviceType(@RequestBody DataPermission dataPermission) {

        return userExtraService.queryUserInfoByDeptAndDeviceType(dataPermission);
    }
}
