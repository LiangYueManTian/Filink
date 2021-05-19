package com.fiberhome.filink.alarmcurrentserver;

import com.fiberhome.filink.alarmcurrentserver.alarmrecive.AlarmChannel;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * <p>
 *  当前告警服务启动类
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
@MapperScan(basePackages = {"com.fiberhome.filink.alarmcurrentserver.dao"})
@EnableBinding({AlarmChannel.class})
@EnableAsync
public class FilinkAlarmCurrentServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkAlarmCurrentServerApplication.class, args);
    }

}

