package com.fiberhome.filink.websocket;

import com.fiberhome.filink.websocket.bean.WebSocketMessage;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GlobalWebsocketUtil {

    //保存全局的  连接上服务器的客户
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor
            .INSTANCE);

    /**
     * 保存channel和用户的关系
     */
    public static ConcurrentMap<String,Channel> channelConcurrentMap = new ConcurrentHashMap<>();
}
