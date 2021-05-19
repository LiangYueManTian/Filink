package com.fiberhome.filink.scheduleserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = "com.fiberhome.filink")
@EnableFeignClients(basePackages = {"com.fiberhome.filink"})
@EnableAsync
@EnableHystrix
public class FilinkScheduleServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkScheduleServerApplication.class, args);
    }

}

