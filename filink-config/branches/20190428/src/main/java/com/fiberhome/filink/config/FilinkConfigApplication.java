package com.fiberhome.filink.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
//@EnableDiscoveryClient
@EnableConfigServer
public class FilinkConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkConfigApplication.class, args);
    }

}

