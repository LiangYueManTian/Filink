package com.fiberhome.filink.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * <p>
 *     FastDFS文件服务器
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/1/26
 */
@ComponentScan(basePackages = "com.fiberhome.filink")
@SpringBootApplication
public class FilinkOssServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FilinkOssServerApplication.class, args);
    }

}

