package com.fiberhome.filink.stationserver;

import com.fiberhome.filink.stationserver.stream.StationChannel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * station启动类
 *
 * @author CongcaiYu
 */
@EnableHystrix
@SpringBootApplication()
@ComponentScan(basePackages = {"com.fiberhome.filink"})
@EnableBinding(StationChannel.class)
@EnableFeignClients(basePackages = "com.fiberhome.filink")
@EnableAsync
public class FiLinkStationServerApplication {


    public static void main(String[] args) {
        SpringApplication.run(FiLinkStationServerApplication.class, args);
    }

}

