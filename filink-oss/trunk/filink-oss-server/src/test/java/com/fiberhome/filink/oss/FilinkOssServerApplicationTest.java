package com.fiberhome.filink.oss;

import mockit.Expectations;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
/**
 * <p>
 *     启动类测试类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/3/7
 */
@RunWith(JMockit.class)
public class FilinkOssServerApplicationTest {
    /**
     * 测试对象
     */
    @Tested
    private FilinkOssServerApplication application;
    /**
     * main
     */
    @Test
    public void main() {
        String[] args = new String[4];
        new Expectations(SpringApplication.class) {
            {
                SpringApplication.run(any,args);
                result = null;
            }
        };
        application.main(args);
    }
}
