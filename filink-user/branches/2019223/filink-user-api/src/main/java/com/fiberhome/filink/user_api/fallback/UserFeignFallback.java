package com.fiberhome.filink.user_api.fallback;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.user_api.api.UserFeign;
import com.fiberhome.filink.user_api.bean.PasswordDto;
import com.fiberhome.filink.user_api.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * feign熔断
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/16 12:19 PM
 */
@Slf4j
@Component
public class UserFeignFallback implements UserFeign {

    /**
     * 根据用户名查询用户信息熔断
     * @param userName  用户名
     * @param token     用户token
     * @param loginIp   登录ip
     * @return          判断结果
     */
    @Override
    public User queryUserByNmae(String userName, String token,String loginIp) {
        log.info("queryUserByNmae feign调用熔断》》》》》》》》》》");
        return null;
    }

    /**
     * 根据用户code查询用户密码熔断
     * @param userName  用户名
     * @return          判断结果
     */
    @Override
    public String queryUserPwd(String userName) {
        log.info("queryUserPwd feign调用熔断》》》》》》》》》》");
        return null;
    }

    /**
     * 修改用户密码熔断处理
     * @param pwd   密码
     * @return      处理结果
     */
    @Override
    public Result modifyPWD(PasswordDto pwd) {
        log.info("modifyPWD feign调用熔断》》》》》》》》》》");
        return null;
    }

    /**
     * 获取当前登录用于熔断处理
     * @param userId    用户id
     * @param token     用户token
     * @return          熔断处理结果
     */
    @Override
    public User queryCurrentUser(String userId, String token) {
        log.info("queryCurrentUser feign调用熔断》》》》》》》》》》");
        return null;
    }

    /**
     * 根据用户id查询用户结果熔断处理
     * @param userId    用户id
     * @return          处理结果
     */
    @Override
    public boolean queryUserById(String userId) {
        log.info("queryUserById feign调用熔断》》》》》》》》》》");
        return false;
    }

    /**
     * 更新用户有效期时间熔断处理
     * @param userId    用户id
     * @param token     用户token
     * @return          熔断处理
     */
    @Override
    public boolean updateLoginTime(String userId, String token) {
        log.info("updateLoginTime feign调用熔断》》》》》》》》》》");
        return false;
    }

    @Override
    public Result queryUserInfoById(String userId) {
        log.info("queryUserInfoById feign调用熔断》》》》》》》》》》");
        return null;
    }
}
