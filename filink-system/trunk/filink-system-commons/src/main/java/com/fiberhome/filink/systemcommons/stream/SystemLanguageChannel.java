package com.fiberhome.filink.systemcommons.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * <p>
 *  系统语言输出通道
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-11
 */
public interface SystemLanguageChannel {
    /**
     * 系统语言命令发送
     */
    String SYSTEM_LANGUAGE_OUTPUT = "system_language_output";

    /**
     * websocket发送者
     *
     * @return SubscribableChannel
     */
    @Output(SYSTEM_LANGUAGE_OUTPUT)
    MessageChannel systemLanguageOutput();
}
