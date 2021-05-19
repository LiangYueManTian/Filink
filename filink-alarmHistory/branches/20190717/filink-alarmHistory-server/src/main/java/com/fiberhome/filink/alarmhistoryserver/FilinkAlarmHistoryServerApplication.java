package com.fiberhome.filink.alarmhistoryserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.ComponentScan;

/**
 * <p>
 *  告警服务启动类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */

@EnableHystrix
@SpringCloudApplication
@EnableHystrixDashboard
@EnableFeignClients(basePackages = "com.fiberhome.filink")
@ComponentScan(basePackages = "com.fiberhome.filink")
@MapperScan(basePackages = {"com.fiberhome.filink.alarmhistoryserver.dao"})
public class FilinkAlarmHistoryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkAlarmHistoryServerApplication.class, args);
    }

}

