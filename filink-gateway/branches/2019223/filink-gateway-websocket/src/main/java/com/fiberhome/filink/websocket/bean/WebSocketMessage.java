package com.fiberhome.filink.websocket.bean;

import lombok.Data;

/**
 * webSocket 消息封装
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-26 14:05
 */
@Data
public class WebSocketMessage {

    /**
     * 此处使用自定义id ， 由前端生成，可以使用用户id
     */
    private String channelId;

    /**
     * 群发还定向通知  除了0 其他都是群发
     */
    private Integer msgType;

    /**
     * 消息类型，根据具体功能填写，前端根据此key处理具体业务逻辑
     */
    private String channelKey;

    /**
     * 消息
     */
    private Object msg;
}
