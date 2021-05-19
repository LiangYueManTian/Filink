package com.fiberhome.filink.userserver.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author xgong
 */
public interface UpdateUserListenStream {

    /**
     * 消息中间件监听通道
     */
    String UPDATE_USER_INPUT = "updateInPut";

    String USER_FORBIDDEN_INPUT = "userForbiddenInput";

    String UNLOCK_USER = "unlock_user";

    String EXPIRE_USER = "user_expire";

    /**
     * 消息中间件监听通道
     *
     * @return
     */
    @Input(UPDATE_USER_INPUT)
    SubscribableChannel updateUserInput();

    /**
     * 监听远程定时任务
     *
     * @return
     */
    @Input(USER_FORBIDDEN_INPUT)
    SubscribableChannel userForbiddenInput();

    /**
     * 监听是否能解锁用户
     *
     * @return
     */
    @Input(UNLOCK_USER)
    SubscribableChannel unlockUser();

    /**
     * 监听账号的有效期
     *
     * @return
     */
    @Input(EXPIRE_USER)
    SubscribableChannel userExpire();
}
