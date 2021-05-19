package com.fiberhome.filink.workflowserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;

/**
 * 流程server启动类
 * @author hedongwei@wistronits.com
 * @date  2019/3/25 11:08
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.fiberhome.filink")
@MapperScan(basePackages = "com.fiberhome.filink.workflowserver.dao")
@EnableFeignClients(basePackages = "com.fiberhome.filink")
@EnableHystrix
public class FilinkWorkflowServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkWorkflowServerApplication.class, args);
    }

}

