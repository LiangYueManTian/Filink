package com.fiberhome.filink.device;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * main
 * @author zepenggao@wistronits.com
 * create on  2019
 */
@ComponentScan(basePackages = "com.fiberhome.filink")
@MapperScan(basePackages = {"com.fiberhome.filink.*.dao"})
@SpringBootApplication
@EnableFeignClients(basePackages = "com.fiberhome.filink")
public class FilinkDeviceServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilinkDeviceServerApplication.class, args);
	}

}

