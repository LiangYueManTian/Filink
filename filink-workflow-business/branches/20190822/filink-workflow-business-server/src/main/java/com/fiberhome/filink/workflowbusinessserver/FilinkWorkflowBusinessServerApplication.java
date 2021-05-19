package com.fiberhome.filink.workflowbusinessserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;


/**
 * <p>
 * 服务启动类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-03-06
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.fiberhome.filink")
@MapperScan(basePackages = {"com.fiberhome.filink.workflowbusinessserver.dao"})
@EnableFeignClients(basePackages = {"com.fiberhome.filink"})
@EnableHystrix
public class FilinkWorkflowBusinessServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkWorkflowBusinessServerApplication.class, args);
    }

}

