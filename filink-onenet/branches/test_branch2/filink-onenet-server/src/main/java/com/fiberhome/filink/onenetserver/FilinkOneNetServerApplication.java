package com.fiberhome.filink.onenetserver;

import com.fiberhome.filink.onenetserver.stream.OneNetChannel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Async;

/**
 * <p>
 *   oneNet平台服务启动类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@ComponentScan(basePackages = "com.fiberhome.filink")
@EnableFeignClients(basePackages = "com.fiberhome.filink")
@SpringBootApplication
@EnableHystrix
@EnableBinding(OneNetChannel.class)
@Async
public class FilinkOneNetServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkOneNetServerApplication.class, args);
    }


}
