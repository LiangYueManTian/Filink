package com.fiberhome.filink.gateway_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;

/**
 * 启动类 网关服务
 *
 * @author yaoyuan
 */
@EnableZuulProxy
@ComponentScan(basePackages = {"com.fiberhome.filink"})
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class} )
@EnableFeignClients(basePackages = "com.fiberhome.filink")
public class FilinkGatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkGatewayServerApplication.class, args);
    }

}

