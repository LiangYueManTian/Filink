package com.fiberhome.filink.logapi.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author hedongwei@wistronits.com
 * @Description 文件常量类
 * @date 2019/1/17 19:28
 */
@Component
public class FileConstants {

    /**
     * 日志文件存放路径
     */
    @Value("${readLogFilePath}")
    private String logFilePath;

    public static FileConstants fileConstants;

    public String logPath;

    /**
     * @Author hedongwei@wistronits.com
     * @Description 初始化数据
     * @Date 10:15 2019/1/22
     * @Param []
     */
    @PostConstruct
    public void init() {
        fileConstants = this;
        fileConstants.logPath = this.logFilePath;
    }
}
