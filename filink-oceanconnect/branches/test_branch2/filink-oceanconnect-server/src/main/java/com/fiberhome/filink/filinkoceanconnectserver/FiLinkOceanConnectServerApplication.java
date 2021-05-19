package com.fiberhome.filink.filinkoceanconnectserver;

import com.fiberhome.filink.filinkoceanconnectserver.stream.OceanConnectChannel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 电信平台启动类
 * @author CongcaiYu
 */
@EnableHystrix
@SpringBootApplication()
@ComponentScan(basePackages = {"com.fiberhome.filink"})
@EnableFeignClients(basePackages = "com.fiberhome.filink")
@EnableBinding(OceanConnectChannel.class)
@EnableAsync
public class FiLinkOceanConnectServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FiLinkOceanConnectServerApplication.class, args);
    }

}
