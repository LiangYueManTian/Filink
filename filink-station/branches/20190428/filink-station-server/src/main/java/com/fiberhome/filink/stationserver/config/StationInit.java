package com.fiberhome.filink.stationserver.config;

import com.fiberhome.filink.commonstation.entity.config.RetryConfig;
import com.fiberhome.filink.commonstation.receiver.AbstractUdpInstructReceiver;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 系统初始化
 *
 * @author CongcaiYu
 */
@Component
public class StationInit {

    @Value("${constant.port}")
    private Integer port;

    @Value("${constant.retryCount}")
    private Integer retryCount;

    @Value("${constant.retryCycle}")
    private Integer retryCycle;

    @Autowired
    private AbstractUdpInstructReceiver serverHandler;


    /**
     * 初始化udpServer
     *
     * @return Channel
     */
    @Bean("myChannel")
    public Channel initUdpServer() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(serverHandler);

            Channel channel = bootstrap.bind(port).sync().channel();
            return channel;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化重试配置
     *
     * @return 重试配置
     */
    @Bean("stationRetryConfig")
    public RetryConfig initRetryConfig() {
        RetryConfig retryConfig = new RetryConfig();
        retryConfig.setRetryCount(retryCount);
        retryConfig.setRetryCycle(retryCycle);
        return retryConfig;
    }

}
