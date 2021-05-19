package com.fiberhome.filink.userserver.component;

import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.security.api.SecurityFeign;
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
public class UnLockUserListen {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SecurityFeign securityFeign;

    private static final String UNLOCK_USER = "UNLOCK_USER";


    /**
     * 监听禁用用户的服务
     *
     * @param code 消息码
     */
    @StreamListener(UpdateUserListenStream.UNLOCK_USER)
    public void unlockUserInfo(String code) {

        log.info("监听到了kafka-unlockUserInfo的信息,code = " + code);
        if (UNLOCK_USER.equals(code)) {

            List<User> users = userDao.queryAllUserDetailInfo();
            //当前时间
            long currentTime = System.currentTimeMillis();
            List<String> userIdList = new ArrayList<>();
            users.forEach(user -> {
                Long unlockTime = user.getUnlockTime();
                if (unlockTime != null) {
                    if (currentTime >= unlockTime) {
                        userIdList.add(user.getId());
                    }
                }
            });

            if (CheckEmptyUtils.collectEmpty(userIdList)) {
                userDao.batchUpdateUnlockTime(userIdList);

                //记录解锁用户的日志
                List<User> userList = userDao.queryUserByIdList(userIdList);
                LogUtils.addTimeTaskLog(userList, UserConst.UNLOCK_USER_MODEL, LogConstants.LOG_TYPE_SYSTEM);
            }
        }
    }

}
