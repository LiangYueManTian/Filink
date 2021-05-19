package com.fiberhome.filink.user_api.api;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.user_api.bean.PasswordDto;
import com.fiberhome.filink.user_api.bean.User;
import com.fiberhome.filink.user_api.fallback.DepartmentFeignFallback;
import com.fiberhome.filink.user_api.fallback.UserFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * user模块feign测试  中转站
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/16 12:18 PM
 */
@FeignClient(name = "filink-user-server",fallback = UserFeignFallback.class)
public interface UserFeign {

    /**
     * 根据用户名查询用户名token查询用户信息
     * @param userName  用户名
     * @param token     用户token
     * @param loginIp   登录ip
     * @return          用户信息
     */
    @GetMapping("/user/queryUser/{userName}/{token}/{loginIp}")
    Object queryUserByNmae(@PathVariable("userName") String userName,@PathVariable("token") String token,@PathVariable("loginIp") String loginIp);

    /**
     * 根据用户名查询用户密码
     * @param userName  用户名
     * @return          用户密码
     */
    @GetMapping("/user/queryUserPwd/{userName}")
    String queryUserPwd(@PathVariable("userName") String userName);

    /**
     * 修改用户密码
     * @param pwd   密码
     * @return      修改密码的结果
     */
    @PostMapping("/user/modifyPWD")
    Result modifyPWD(@RequestBody PasswordDto pwd);

    /**
     * 查询当前用户信息
     * @param userId    用户id
     * @param token     用户token
     * @return          用户信息
     */
    @GetMapping("/user/queryCurrentUser/{userId}/{token}")
    Object queryCurrentUser(@PathVariable("userId") String userId,@PathVariable("token") String token);

    /**
     * 查询是否有指定用户
     * @param userId    用户id
     * @return          是否存在用户
     */
    @GetMapping("/user/queryUserById/{userId}")
    public boolean queryUserById(@PathVariable("userId") String userId);

    /**
     * 更新登录用户的时间
     * @param userId    用户id
     * @param token     用户token
     * @return          更新结果
     */
    @GetMapping("/user/updateLoginTime/{userId}/{token}")
    public boolean updateLoginTime(@PathVariable("userId") String userId,@PathVariable("token") String token);

    /**
     * 根据用户id获取用户信息
     * @param userId    用户id
     * @return  用户信息
     */
    @PostMapping("/user/queryUserInfoById/{userId}")
    public Result queryUserInfoById(@PathVariable("userId") String userId);
}
