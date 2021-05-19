package com.fiberhome.filink.userserver;
/**
 * <p>
 * 用户服务启动类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */

import com.fiberhome.filink.userserver.stream.UpdateUserListenStream;
import com.fiberhome.filink.userserver.stream.UpdateUserStream;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author xgong
 */

@SpringCloudApplication
@EnableHystrixDashboard
@EnableFeignClients(basePackages = "com.fiberhome.filink")
@ComponentScan(basePackages = "com.fiberhome.filink")
@MapperScan(basePackages = {"com.fiberhome.filink.userserver.dao"})
@EnableBinding({UpdateUserListenStream.class, UpdateUserStream.class})
@EnableHystrix
public class FilinkUserServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkUserServerApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

