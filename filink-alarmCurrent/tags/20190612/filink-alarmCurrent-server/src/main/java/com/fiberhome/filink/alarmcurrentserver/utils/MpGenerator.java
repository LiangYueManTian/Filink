package com.fiberhome.filink.alarmcurrentserver.utils;

import com.fiberhome.filink.server_common.bean.CodeGenerator;
import com.fiberhome.filink.server_common.utils.CodeGeneratorUtils;
import lombok.extern.slf4j.Slf4j;


/**
 *
 * 代码生成器
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Slf4j
public class MpGenerator {

    public static void main(String[] args) {

        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.setAuthor("wtao103@fiberhome.com");
        codeGenerator.setProjectPath("D:/code/10-Sys Code/filink-alarmCurrent/trunk/filink-alarmCurrent-server/src/main/java/");
        codeGenerator.setDataBaseIp("10.5.43.18");
        codeGenerator.setDataBasePort("3306");
        codeGenerator.setDataBaseName("filink_alarm");
        codeGenerator.setDataBaseUserName("root");
        codeGenerator.setDataBasePassword("wistronits@123");
        codeGenerator.setTableNames(new String[]{"alarm_query_template"});
        codeGenerator.setPackageName("com.fiberhome.filink.alarmcurrent_server");

        CodeGeneratorUtils.generatorCode(codeGenerator);
    }
}
