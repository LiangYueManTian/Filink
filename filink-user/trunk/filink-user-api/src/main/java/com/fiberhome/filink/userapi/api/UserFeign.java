package com.fiberhome.filink.userapi.api;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.userapi.bean.*;
import com.fiberhome.filink.userapi.fallback.UserFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * user模块feign
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/16 12:18 PM
 */
@FeignClient(name = "filink-user-server", fallback = UserFeignFallback.class)
public interface UserFeign {

    /**
     * 根据用户名查询用户信息
     *
     * @param userParameter 用户信息参数
     * @return 用户信息
     */
    @PostMapping("/user/queryUserByParameter")
    User queryUserByName(@RequestBody UserParameter userParameter);

    /**
     * 根据用户名查询用户密码
     *
     * @param userName 用户名
     * @return 用户密码
     */
    @GetMapping("/user/queryUserPwd/{userName}")
    String queryUserPwd(@PathVariable("userName") String userName);

    /**
     * 修改用户密码
     *
     * @param pwd 密码
     * @return 修改密码的结果
     */
    @PostMapping("/user/modifyPWD")
    Result modifyPWD(@RequestBody PasswordDto pwd);

    /**
     * 根据根据用户id和token获取当前用户详细信息
     *
     * @param userId 用户id
     * @param token  用户token
     * @return
     */
    @GetMapping("/user/queryCurrentUser/{userId}/{token}")
    Object queryCurrentUser(@PathVariable("userId") String userId, @PathVariable("token") String token);

    /**
     * 查询是否有指定用户
     *
     * @param userId 用户id
     * @return 是否存在用户
     */
    @GetMapping("/user/queryUserById/{userId}")
    boolean queryUserById(@PathVariable("userId") String userId);

    /**
     * 更新登录用户的时间
     *
     * @param userId 用户id
     * @param token  用户token
     * @return 更新结果
     */
    @GetMapping("/user/updateLoginTime/{userId}/{token}")
    boolean updateLoginTime(@PathVariable("userId") String userId, @PathVariable("token") String token);

    /**
     * 根据用户id获取用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    @PostMapping("/user/queryUserInfoById/{userId}")
    Result queryUserInfoById(@PathVariable("userId") String userId);

    /**
     * 根据id集合批量查询用户信息
     *
     * @param idList 用户id集合
     * @return 用户信息
     */
    @PostMapping("/user/queryUserByIdList")
    Object queryUserByIdList(@RequestBody List<String> idList);

    /**
     * 获取多个部门下的人员信息
     *
     * @param deptIdList 部门id集合
     * @return 人员集合
     */
    @PostMapping("/user/queryUserByDeptList")
    Object queryUserByDeptList(@RequestBody List<String> deptIdList);

    /**
     * 根据部门和设施类型查询人员信息
     *
     * @param dataPermission 数据权限信息
     * @return 在线用户的token列表
     */
    @PostMapping("/user/queryDeviceTypeByPermission")
    List<String> queryDeviceTypeByPermission(@RequestBody DataPermission dataPermission);

    /**
     * 根据设施获取用户信息
     *
     * @param dataPermissionList 设施信息列表
     * @return
     */
    @PostMapping("/user/queryUserByDevice")
    Map<String, List<String>> queryUserByDevice(@RequestBody List<DataPermission> dataPermissionList);

    /**
     * 查询短信验证码
     *
     * @param phoneNumber 手机号
     * @return 验证码信息
     */
    @GetMapping("/user/getSmsMessage/{phoneNumber}")
    String getSmsMessage(@PathVariable("phoneNumber") String phoneNumber);

    /**
     * 根据手机号获取用户信息
     *
     * @param phoneNumber 手机号
     * @return
     */
    @GetMapping("/user/queryUserByPhone/{phoneNumber}")
    Object queryUserByPhone(@PathVariable("phoneNumber") String phoneNumber);

    /**
     * 根据用户id获取所有的token值
     *
     * @param userId 用户id
     * @return token列表
     */
    @GetMapping("/user/queryTokenByUserId/{userId}")
    List<String> queryTokenByUserId(@PathVariable("userId") String userId);

    /**
     * 验证当前用户是否能够登陆
     *
     * @param userParameter 登录用户信息
     * @return code码
     */
    @PostMapping("/user/validateUserLogin")
    Integer validateUserLogin(@RequestBody UserParameter userParameter);

    /**
     * 处理用户登录失败的情况
     *
     * @param userParameter 用户信息
     */
    @PostMapping("/user/dealLoginFail")
    void dealLoginFail(@RequestBody UserParameter userParameter);

    /**
     * 根据用户id列表获取登录设备信息
     *
     * @param idList id列表
     * @return 登录设备id列表
     */
    @PostMapping("/user/queryPhoneIdByUserIds")
    List<String> queryPhoneIdByUserIds(@RequestBody List<String> idList);

    /**
     * 根据用户名获取用户id信息
     *
     * @param userName 用户名
     * @return 用户id列表
     */
    @GetMapping("/user/queryUserIdByName/{userName}")
    List<String> queryUserIdByName(@PathVariable("userName") String userName);

    /**
     * 获取账号数和在线用户数
     *
     * @return 用户数量实体类
     */
    @PostMapping("/user/queryUserNumber")
    UserCount queryUserNumber();

    /**
     * 根据用户id列表，获取所有的在线用户信息, 后台调用
     *
     * @param idList 用户id列表
     * @return 用户列表
     */
    @PostMapping("/user/queryOnlineUserByIdList")
    Object queryOnlineUserByIdList(@RequestBody List<String> idList);

    /**
     * 查询登陆用户信息
     *
     * @param userLoginInfoParam 传入参数
     * @return 查询结果
     */
    @PostMapping("/userLoginController/queryUserLoginInfo")
    LoginInfoBean queryUserLoginInfo(@RequestBody UserLoginInfoParam userLoginInfoParam);
}
