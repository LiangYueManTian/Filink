package com.fiberhome.filink.userserver.component;

import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.security.api.SecurityFeign;
import com.fiberhome.filink.security.bean.AccountSecurityStrategy;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.consts.UserConst;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.stream.UpdateUserListenStream;
import com.fiberhome.filink.userserver.utils.CheckEmptyUtils;
import com.fiberhome.filink.userserver.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xgong
 */
@Component
@Slf4j
public class ForbiddenUserListen {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SecurityFeign securityFeign;

    private static final String USER_TASK_INFO = "USER_INFO";

    private static final long DAY_MILLION_SECOND = 1000 * 3600 * 24;

    /**
     * 启用账号禁用策略
     */
    private static final String OPEN_STRATEY = "1";

    /**
     * 关闭账号禁用策略
     */
    private static final String CLOSE_STRATEGY = "0";

    /**
     * 用户禁用
     */
    private static final int USER_STATUS_STOP = 0;

    /**
     * 用户启用的状态值
     */
    private static final String OPEN_USER_STATUS = "1";

    /**
     * 监听禁用用户的服务
     *
     * @param code 消息码
     */
    @StreamListener(UpdateUserListenStream.USER_FORBIDDEN_INPUT)
    public void forbiddenUserListen(String code) {

        log.info("监听到了kafka-forbiddenUserListen的信息,code = " + code);
        if (USER_TASK_INFO.equals(code)) {
            Result result = securityFeign.queryAccountSecurity();
            //获取安全策略信息
            if (result != null && result.getData() != null) {
                AccountSecurityStrategy strategy = JSON.parseObject(JSON.toJSONString(result.getData()), AccountSecurityStrategy.class);
                //如果启用账号锁定策略
                List<String> userIdList = new ArrayList<>();
                if (OPEN_STRATEY.equals(strategy.getForbidStrategy())) {
                    List<User> users = userDao.queryAllUser();
                    users.forEach(user -> {
                        //如果用户为启用状态
                        if (user.getUserStatus().equals(OPEN_USER_STATUS)) {
                            Long loginTime = user.getLoginTime();
                            if (loginTime == null) {
                                loginTime = user.getCreateTime();
                            }
                            long currentTime = System.currentTimeMillis();
                            long noLoginTime = currentTime - loginTime;
                            long day = noLoginTime / DAY_MILLION_SECOND;
                            if (day > strategy.getNoLoginTime()) {
                                userIdList.add(user.getId());
                            }
                        }
                    });
                }

                //如果有长时间未登录的用户，则把用户设置为禁用
                if (CheckEmptyUtils.collectEmpty(userIdList)) {
                    String[] idArray = {};
                    userDao.updateUserStatus(USER_STATUS_STOP, userIdList.toArray(idArray));

                    //记录禁用用户的日志
                    List<User> userList = userDao.queryUserByIdList(userIdList);
                    LogUtils.addTimeTaskLog(userList, UserConst.FORBIDDEN_USER_MODEL, LogConstants.LOG_TYPE_SYSTEM);
                }
            }

            log.info("监听到了kafka-forbiddenUserListen的信息,code = " + code);
        }
    }


}
