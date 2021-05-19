package com.fiberhome.filink.systemlanguage.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

/**
 * <p>
 *  系统语言输入通道
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-11
 */
public interface SystemLanguageChannel {
    /**
     * 系统语言命令发送
     */
    String SYSTEM_LANGUAGE_INPUT = "system_language_input";

    /**
     * websocket发送者
     *
     * @return SYSTEM_LANGUAGE_INPUT
     */
    @Input(SYSTEM_LANGUAGE_INPUT)
    MessageChannel systemLanguageInput();
}
