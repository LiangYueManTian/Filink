package com.fiberhome.filink.map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <p>
 * 启动类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/7
 */
@SpringBootApplication(scanBasePackages = "com.fiberhome.filink")
@EnableTransactionManagement
public class FilinkMapServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FilinkMapServerApplication.class, args);
    }
}
