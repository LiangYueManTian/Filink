package com.fiberhome.filink.userserver.utils;

import com.fiberhome.filink.bean.WebSocketMessage;
import com.fiberhome.filink.userserver.service.UserStream;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @author xgong
 */
public class WebSocketUtils {

    /**
     * websocket发送消息
     * @param channelId 通道id
     * @param channelKey 通道key
     * @param message 消息体
     */
    public static void websocketSendMessage(UserStream userStream,String channelId,
                                     String channelKey, Object message,int sendType){
        WebSocketMessage webSocketMessage = new WebSocketMessage();
        webSocketMessage.setChannelKey(channelKey);
        webSocketMessage.setChannelId(channelId);
        webSocketMessage.setMsg(message);
        webSocketMessage.setMsgType(sendType);

        Message<WebSocketMessage> sendMessage = MessageBuilder.withPayload(webSocketMessage).build();
        userStream.webSocketoutput().send(sendMessage);
    }
}
