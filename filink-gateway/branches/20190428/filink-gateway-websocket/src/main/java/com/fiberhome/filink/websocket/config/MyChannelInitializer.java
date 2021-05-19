package com.fiberhome.filink.websocket.config;

import com.fiberhome.filink.websocket.handler.MyChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 组装handler
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-25 15:32
 */
public class MyChannelInitializer extends ChannelInitializer {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        // HttpServerCodec：将请求和应答消息解码为HTTP消息
        pipeline.addLast("http-codec", new HttpServerCodec());
        // HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        // WebSocket通信支持
        pipeline.addLast("http-chunked", new ChunkedWriteHandler());
        // TODO: 2019-01-25 心跳检测  后续研究检测时间
        pipeline.addLast(new IdleStateHandler(60,30,60*30, TimeUnit.SECONDS));

        pipeline.addLast("handler", new MyChannelHandler());//自定义处理

    }
}
