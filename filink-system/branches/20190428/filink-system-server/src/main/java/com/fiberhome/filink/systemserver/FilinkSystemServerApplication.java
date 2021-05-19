package com.fiberhome.filink.systemserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.*;
/**
 * <p>
 *    系统服务启动类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-02-18
 */
@EnableHystrix
@ComponentScan(basePackages = "com.fiberhome.filink")
@MapperScan(basePackages = {"com.fiberhome.filink.*.dao"})
@SpringBootApplication(scanBasePackages = "com.fiberhome.filink")
@EnableFeignClients(basePackages = "com.fiberhome.filink")
public class FilinkSystemServerApplication {
    public static void main(String [] args) {
        SpringApplication.run(FilinkSystemServerApplication.class, args);
    }
}

