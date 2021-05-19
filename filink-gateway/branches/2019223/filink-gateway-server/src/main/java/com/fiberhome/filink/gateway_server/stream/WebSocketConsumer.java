package com.fiberhome.filink.gateway_server.stream;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.websocket.GlobalWebsocketUtil;
import com.fiberhome.filink.websocket.bean.WebSocketMessage;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentMap;

/**
 * Stream监听demo
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/8 18:44
 */
@Slf4j
@Component
public class WebSocketConsumer {

    /**
     * 定向通知
     * @param message
     */
    @StreamListener(GatewayStreams.WEBSOCKET_INPUT)
    public void senMessage(WebSocketMessage message) {
        ChannelGroup channels = GlobalWebsocketUtil.channels;
        System.out.println(channels);
        Integer msgType = message.getMsgType();
        if (msgType == 0) {
            // 定向通知
            String channelId = message.getChannelId();
            Channel channel = GlobalWebsocketUtil.channelConcurrentMap.get(channelId);
            if (channel != null) {
                log.info("webSocket定向推送消息成功, "+JSONObject.toJSONString(message));
                channel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(message)));
            }else {
                log.info("websocket消息推送失败，通道错误，请检查通道id:{}",channelId);
            }
        } else {
            // 群发
            ConcurrentMap<String, Channel> channelConcurrentMap = GlobalWebsocketUtil.channelConcurrentMap;
            channelConcurrentMap.forEach((key, value) -> {
                if (value != null) {
                    value.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(message)));
                    log.info("webSocket群发推送消息成功, "+JSONObject.toJSONString(message));
                }
            });
        }


    }

}
