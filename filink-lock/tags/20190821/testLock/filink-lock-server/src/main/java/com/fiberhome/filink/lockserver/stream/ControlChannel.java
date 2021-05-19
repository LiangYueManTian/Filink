package com.fiberhome.filink.lockserver.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * <p>
 *   主控 输入输出通道定义
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/7/2
 */
public interface ControlChannel {
    /**
     * 发送者
     */
    String CONTROL_SENDER = "control_sender";

    /**
     * 生产者
     * @return
     */
    @Output(CONTROL_SENDER)
    MessageChannel controlOutput();

}
