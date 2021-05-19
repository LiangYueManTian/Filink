package com.fiberhome.filink.lockserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 电子锁启动类
 * @author CongcaiYu
 */
@ComponentScan(basePackages = "com.fiberhome.filink")
@MapperScan(basePackages = "com.fiberhome.filink.lockserver.dao")
@SpringBootApplication
@EnableFeignClients(basePackages = "com.fiberhome.filink")
public class FilinkLockServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkLockServerApplication.class, args);
    }

}

