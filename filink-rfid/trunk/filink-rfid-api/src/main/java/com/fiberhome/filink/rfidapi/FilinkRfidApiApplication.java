package com.fiberhome.filink.rfidapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.fiberhome.filink")
@ComponentScan(basePackages = "com.fiberhome.filink")
public class FilinkRfidApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkRfidApiApplication.class, args);
    }

}

