package com.fiberhome.filink.rfid;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * <p>
 * 服务启动类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-06-21
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.fiberhome.filink")
@MapperScan(basePackages = {"com.fiberhome.filink.rfid.dao"})
@EnableFeignClients(basePackages = {"com.fiberhome.filink"})
@EnableHystrix
@EnableAsync
public class FilinkRfidServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkRfidServerApplication.class, args);
    }

}

