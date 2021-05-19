package com.fiberhome.filink.monitor;

import de.codecentric.boot.admin.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@EnableAdminServer
@EnableTurbine
@EnableHystrixDashboard
@SpringCloudApplication
public class FilinkMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkMonitorApplication.class, args);
    }

}
