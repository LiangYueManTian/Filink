package com.fiberhome.filink.alarmhistoryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 历史告警启动类
 *
 * @author wtao103@fiberhome.com
 * create on 2018/12/16 5:39 PM
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.fiberhome.filink")
@ComponentScan(basePackages = "com.fiberhome.filink")
public class FilinkAlarmHistoryApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkAlarmHistoryApiApplication.class, args);
    }

}

