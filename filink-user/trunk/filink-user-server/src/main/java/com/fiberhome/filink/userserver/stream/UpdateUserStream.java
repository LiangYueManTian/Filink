package com.fiberhome.filink.userserver.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author xgong
 */
public interface UpdateUserStream {

    String UPDATE_USER_INFO = "update_userInfo";

    /**
     * 任务中心消息输出通道
     *
     * @return 返回结果
     */
    @Output(UPDATE_USER_INFO)
    MessageChannel updateOutput();
}
