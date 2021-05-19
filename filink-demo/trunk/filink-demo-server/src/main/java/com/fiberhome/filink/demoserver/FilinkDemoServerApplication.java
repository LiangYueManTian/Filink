package com.fiberhome.filink.demoserver;

import com.fiberhome.filink.demoserver.stream.DemoStreams;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.ComponentScan;

/**
 * 示例项目启动类
 *
 * @author 姚远
 */
@EnableBinding(DemoStreams.class)
@ComponentScan(basePackages = "com.fiberhome.filink")
@MapperScan(basePackages = {"com.fiberhome.filink.demoserver.dao"})
@SpringBootApplication
public class FilinkDemoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilinkDemoServerApplication.class, args);
    }

}
