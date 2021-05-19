package com.fiberhome.filink.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;

@EnableZuulProxy
@ComponentScan(basePackages = {"com.fiberhome.filink"})
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class} )
@EnableFeignClients(basePackages = "com.fiberhome.filink")
@EnableHystrix
public class FilinkGatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkGatewayServerApplication.class, args);
    }
}

