package com.fiberhome.filink.userserver.controller;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.userserver.bean.*;
import com.fiberhome.filink.userserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 *  用户Controller层
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 查询单个用户的信息
     * @param userId 用户id
     * @return 查询结果
     */
    @PostMapping("/queryUserInfoById/{userId}")
    public Result queryUserInfoById(@PathVariable("userId") String userId) {

        return userService.queryUserInfoById(userId);
    }


    /**
     * 新增用户
     * @param user 用户信息
     * @return 新增结果
     * @throws Exception
     */
    @PostMapping("/insert")
    public Result addUser(@RequestBody(required = false)User user) throws Exception{

        return userService.addUser(user);
    }

    /**
     *  更新用户信息
     * @param user 用户信息
     * @return 修改结果
     * @throws Exception
     */
    @PostMapping("/update")
    public Result updateUser(@RequestBody(required = false)User user) throws Exception{

        return userService.updateUser(user);
    }

    /**
     * 批量删除用户
     * @param deleteUser 要删除的用户信息
     * @return 删除结果
     */
    @PostMapping("/deleteByIds")
    public Result deleteUser(@RequestBody Parameters deleteUser){

        return userService.deleteUser(deleteUser);
    }

    /**
     * 根据用户名，token和ip存储用户登录信息
     * @param userName  用户名
     * @param token     用户token
     * @param loginIp   用户登录ip
     * @return  用户信息
     */
    @GetMapping("/queryUser/{userName}/{token}/{loginIp}")
    public User queryUserByNmae(@PathVariable("userName") String userName,
                                @PathVariable("token") String token,@PathVariable("loginIp") String loginIp) {

        User user = userService.queryUserByNmae(userName,token,loginIp);

        return user;
    }

    /**
     * 根据用户名查询用户密码
     * @param userName  用户名
     * @return  用户密码
     */
    @GetMapping("/queryUserPwd/{userName}")
    public String queryUserPwd(@PathVariable("userName") String userName) {

        User user = userService.queryUserByNmae(userName,null,null);

        return user.getPassword();
    }

    /**
     * 批量更新用户的状态
     * @param userStatus  用户状态
     * @param userIdArray 需要修改状态的id数组
     * @return  更新结果
     */
    @GetMapping("/updateUserStatus")
    public Result updateUserStatus(@RequestParam Integer userStatus,@RequestParam String[] userIdArray) {
        return userService.updateUserStatus(userStatus,userIdArray);
    }

    /**
     * 校验数据的唯一性
     * @param userQueryCondition    需要校验的信息
     * @return 用户信息列表
     */
    @PostMapping("/verifyUserInfo")
    public Result verifyUserInfo(@RequestBody QueryCondition<User> userQueryCondition){
        List<User> userList =  userService.queryUserByField(userQueryCondition);
        return ResultUtils.success(ResultCode.SUCCESS,null,userList);
    }

    /**
     * 修改用户密码
     * @param pwd  用户输入的密码对象
     * @return  修改密码的结果
     */
    @PostMapping("/modifyPWD")
    public Result modifyPWD(@RequestBody PasswordDto pwd){
        return userService.modifyPWD(pwd);
    }

    /**
     * 重置用户密码
     * @param pwd 用户输入的密码集
     * @return 返回对象集
     */
    @PostMapping("/resetPWD")
    public Result resetPWD(@RequestBody PasswordDto pwd){
        return userService.resetPWD(pwd);
    }

    /**
     * 获取在线用户信息
     * @param onlineParameter 在线用户列表参数
     * @return 在线用户信息
     */
    @PostMapping("/getOnLineUser")
    public Result getOnLineUser(@RequestBody QueryCondition<OnlineParameter> onlineParameter){
        return userService.getOnLineUser(onlineParameter);
    }

    /**
     * 根据根据用户id和token获取当前用户详细信息
     * @param userId    用户id
     * @param token     用户token
     * @return          用户信息
     */
    @GetMapping("/queryCurrentUser/{userId}/{token}")
    public User queryCurrentUser(@PathVariable("userId") String userId,@PathVariable("token") String token){
        return userService.queryCurrentUser(userId,token);
    }

    /**
     * 将用户强制下线
     * @param userMap  结构为(userid_token,userid);
     * @return  强制下线结果
     */
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    @PostMapping("/forceOffline")
    public Result forceOffline(@RequestBody Map<String,String> userMap){

        return userService.forceOffline(userMap);
    }

    /**
     * 用户登出
     * @param userId  登出用户的id
     * @param token   登出用户的token
     * @return      用户登出结果
     */
    @GetMapping("/logout/{userId}/{token}")
    public Result logout(@PathVariable("userId") String userId,@PathVariable("token") String token){
        return userService.logout(userId,token);
    }

    /**
     * 根据字段进行添加排序分页查询
     * @param userParameter 查询条件
     * @return  用户结果集
     */
    @PostMapping("/queryUserByField")
    public Result queryUserByField(@RequestBody QueryCondition<UserParameter> userParameter){
        Result userList = userService.queryUserByFieldAndCondition(userParameter);
        return userList;
    }

    /**
     * 获取用户默认密码
     * @return  用户默认密码
     */
    @PostMapping("/queryUserDefaultPWD")
    public Result queryUserDefaultPWD(){
        return userService.queryUserDefaultPWD();
    }

    /**
     * 根据部门查询用户信息
     * @param parameters    部门信息
     * @return    用户信息
     */
    @PostMapping("/queryUserByDept")
    public Result queryUserByDept(@RequestBody Parameters parameters){
        return userService.queryUserByDept(parameters);
    }


    /**
     * 查询单个用户的信息
     * @param userId    用户id
     * @return  查询结果
     */
    @GetMapping("/queryUserById/{userId}")
    public boolean queryUserById(@PathVariable("userId") String userId) {

        return userService.queryUserById(userId);
    }

    /**
     * 更新用户的有效时间
     * @param userId    用户id
     * @param token     用户token
     * @return      更新的结果
     */
    @GetMapping("/updateLoginTime/{userId}/{token}")
    public boolean updateLoginTime(@PathVariable("userId") String userId,@PathVariable("token") String token){

        return userService.updateLoginTime(userId,token);
    }
}
