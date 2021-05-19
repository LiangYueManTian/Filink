package com.fiberhome.filink.userserver.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.userserver.bean.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 *  用户服务类
 *
 * @author xuangong
 * @since 2019-01-03
 */
public interface UserService extends IService<User> {

    /**
     * 查询单个用户的信息
     * @param userId 用户id
     * @return 用户信息
     */
    Result queryUserInfoById(String userId);

    /**
     * 新增用户
     * @param user 用户信息
     * @return 新增结果
     * @throws Exception
     */
    Result addUser(User user);

    /**
     *  修改用户
     * @param user 用户信息
     * @return 修改结果
     * @throws Exception
     */
    Result updateUser(User user);

    /**
     * 删除多个用户
     * @param deleteUser 用户id数组
     * @return 删除结果
     * @throws Exception
     */
    Result deleteUser(Parameters deleteUser);

    /**
     * 根据用户名查询用户信息
     * @param userName  用户名
     * @param token 用户token
     * @param loginIp   用户登录ip
     * @return  用户信息
     */
    User queryUserByNmae(String userName,String token,String loginIp);

    /**
     * 批量更新用户的状态
     * @param userStatus 用户的状态
     * @param userIdArray   用户的id数组
     * @return 返回更新结果
     */
    Result updateUserStatus(int userStatus,String[] userIdArray);

    /**
     *通过自定字段信息查询用户
     * @param userQueryCondition 查询条件
     * @return Result   用户信息列表
     */
    List<User> queryUserByField(QueryCondition<User> userQueryCondition);

    /**
     * 修改用户密码
     * @param pwd   密码信息
     * @return Result   修改的结果
     */
    Result modifyPWD(@RequestBody PasswordDto pwd);

    /**
     * 重置用户密码
     * @param pwd 用户输入的密码信息
     * @return  重置密码的结果
     */
    Result resetPWD(@RequestBody PasswordDto pwd);

    /**
     * 获取在线用户
     * @param onlineParameter   在线用户信息参数
     * @return 在线用户信息列表
     */
    Result getOnLineUser(QueryCondition<OnlineParameter> onlineParameter);

    /**
     * 查询当前登录用户信息
     * @param userId    用户id
     * @param token     用户token
     * @return      用户信息
     */
    User queryCurrentUser(String userId,String token);

    /**
     * 强制用户下线
     * @param userMap （用户id_token，用户id）
     * @return  强制下线的结果
     */
    Result forceOffline(Map<String,String> userMap);

    /**
     * 用户登出
     * @param userId 用户id
     * @param token 用户唯一token标识
     * @return  用户登出结果
     */
    Result logout(String userId,String token);

    /**
     * 获取用户默认密码
     * @return 用户默认密码
     */
    Result queryUserDefaultPWD();

    /**
     * 条件和排序分页查询
     * @param userParameter     用户查询条件参数
     * @return  用户信息列表
     */
    Result queryUserByFieldAndCondition(QueryCondition<UserParameter> userParameter);

    /**
     * 通过部门查找用户信息
     * @param parameters     部门参数
     * @return      用户信息列表
     */
    Result queryUserByDept(Parameters parameters);

    /**
     * 根据用户id查询用户是否存在
     * @param userId    用户id
     * @return  返回结果集
     */
    boolean queryUserById(String userId);

    /**
     * 根据用户id和token更新用户的登录时间
     * @param userId    用户id
     * @param token     用户token
     * @return  更新结果
     */
    boolean updateLoginTime(String userId, String token);
}
