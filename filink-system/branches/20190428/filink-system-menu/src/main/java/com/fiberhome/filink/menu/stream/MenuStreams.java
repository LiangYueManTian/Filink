package com.fiberhome.filink.menu.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 输出输入通道定义
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/8 18:04
 */
public interface MenuStreams {
    String MENU_WEB_SOCKET_OUTPUT = "menu_websocket_output";

    /**
     * 菜单webSocket输出通道
     * @return 返回结果
     */
    @Output(MENU_WEB_SOCKET_OUTPUT)
    MessageChannel menuWebSocketOutput();


}
