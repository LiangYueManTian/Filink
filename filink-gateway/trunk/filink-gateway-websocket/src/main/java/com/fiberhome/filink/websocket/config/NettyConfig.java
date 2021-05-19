package com.fiberhome.filink.websocket.config;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 *
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/23 11:29
 */
public class NettyConfig {

    /**
     * 存储每一个客户端进来的channel对象
     */
    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
}
