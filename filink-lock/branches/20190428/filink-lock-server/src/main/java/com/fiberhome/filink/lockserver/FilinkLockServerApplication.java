package com.fiberhome.filink.lockserver;

import com.fiberhome.filink.lockserver.stream.OceanConnectChannel;
import com.fiberhome.filink.lockserver.stream.OneNetChannel;
import com.fiberhome.filink.lockserver.stream.UdpChannel;
import com.fiberhome.filink.txlcntc.config.EnableDistributedTransaction;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;

/**
 * 电子锁启动类
 *
 * @author CongcaiYu
 */
@ComponentScan(basePackages = "com.fiberhome.filink")
@MapperScan(basePackages = "com.fiberhome.filink.lockserver.dao")
@SpringBootApplication
@EnableFeignClients(basePackages = "com.fiberhome.filink")
@EnableBinding({UdpChannel.class, OneNetChannel.class, OceanConnectChannel.class})
@EnableHystrix
@EnableDistributedTransaction
public class FilinkLockServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkLockServerApplication.class, args);
    }

}

