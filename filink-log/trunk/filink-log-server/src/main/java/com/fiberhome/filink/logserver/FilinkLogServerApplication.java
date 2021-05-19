package com.fiberhome.filink.logserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;


/**
 * 日志启动类
 * @author hedongwei@wistronits.com
 * @date  2019/3/20 17:18
 */
@EnableFeignClients(basePackages = "com.fiberhome.filink")
@SpringBootApplication
@ComponentScan(basePackages = "com.fiberhome.filink")
@MapperScan(basePackages = "com.fiberhome.filink.logserver.dao")
@EnableHystrix
public class FilinkLogServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilinkLogServerApplication.class, args);
	}

}

