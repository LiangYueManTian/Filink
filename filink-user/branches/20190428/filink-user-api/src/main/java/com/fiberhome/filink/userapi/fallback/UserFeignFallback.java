package com.fiberhome.filink.userapi.fallback;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.DataPermission;
import com.fiberhome.filink.userapi.bean.PasswordDto;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.userapi.bean.UserCount;
import com.fiberhome.filink.userapi.bean.UserParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

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
     * 根据用户名查询用户信息
     * @param userParameter  用户信息参数
     * @return
     */
    @Override
    public User queryUserByName(UserParameter userParameter) {
        log.info("queryUserByName feign调用熔断》》》》》》》》》》");
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
     * @return          熔断处理结果
     */
    @Override
    public Object queryCurrentUser(@PathVariable("userId") String userId, @PathVariable("token") String token) {
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

    /**
     * 获取用户信息熔断处理
     * @param userId    用户id
     * @return  null
     */
    @Override
    public Result queryUserInfoById(String userId) {
        log.info("queryUserInfoById feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public Object queryUserByIdList(List<String> idList) {
        log.info("queryUserByIdList feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public Object queryUserByDeptList(List<String> deptIdList) {
        log.info("queryUserByDeptList feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public List<String> queryDeviceTypeByPermission(DataPermission dataPermission) {
        log.info("queryDeviceTypeByPermission feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public Map queryUserByDevice(List<DataPermission> dataPermissionList) {
        log.info("queryUserByDevice feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public String getSmsMessage(String phoneNumber) {
        log.info("queryUserByDevice feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public Object queryUserByPhone(String phoneNumber) {
        log.info("queryUserByPhone feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public List<String> queryTokenByUserId(String userId) {
        log.info("queryTokenByUserId feign调用熔断》》》》》》》》》》");
        return null;
    }

    /**
     * 验证用户登录熔断
     * @param userParameter  登录用户信息
     * @return
     */
    @Override
    public Integer validateUserLogin(UserParameter userParameter) {
        log.info("validateUserLogin feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public void dealLoginFail(UserParameter userParameter) {
        log.info("validateUserLogin feign调用熔断》》》》》》》》》》");
    }

    @Override
    public List<String> queryPhoneIdByUserIds(List<String> idList) {

        log.info("queryPhoneIdByUserIds feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public List<String> queryUserIdByName(String userName) {
        log.info("queryUserIdByName feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public UserCount queryUserNumber() {
        log.info("queryUserNumber feign调用熔断》》》》》》》》》》");
        return null;
    }

    @Override
    public Object queryOnlineUserByIdList(List<String> idList) {
        log.info("queryOnlineUserByIdList feign调用熔断》》》》》》》》》》");
        return null;
    }
}
