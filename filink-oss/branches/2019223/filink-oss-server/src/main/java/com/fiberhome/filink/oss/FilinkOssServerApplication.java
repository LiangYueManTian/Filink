package com.fiberhome.filink.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * OSS文件系统服务启动类类
 *
 * @author chaofang@wistronits.com
 * @since 2019/1/22
 */
@ComponentScan(basePackages = "com.fiberhome.filink")
@EnableFeignClients(basePackages = "com.fiberhome.filink")
@SpringBootApplication
public class FilinkOssServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkOssServerApplication.class, args);
    }

}

