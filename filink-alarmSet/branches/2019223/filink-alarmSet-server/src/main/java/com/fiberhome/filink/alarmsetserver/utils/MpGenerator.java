package com.fiberhome.filink.alarmsetserver.utils;

import com.fiberhome.filink.server_common.bean.CodeGenerator;
import com.fiberhome.filink.server_common.utils.CodeGeneratorUtils;
import lombok.extern.slf4j.Slf4j;


/**
 *
 * 代码生成器
 *
 * @author yuanyao@wistronits.com
 * create on 2018/7/19 下午4:06
 */
@Slf4j
public class MpGenerator {

    public static void main(String[] args) {

        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.setAuthor("wtao103@fiberhome.com");
        codeGenerator.setProjectPath("D:/code/10-Sys Code/filink-alarmSet/trunk/filink-alarmSet-server/src/main/java/");
        codeGenerator.setDataBaseIp("10.5.43.18");
        codeGenerator.setDataBasePort("3306");
        codeGenerator.setDataBaseName("filink_alarm");
        codeGenerator.setDataBaseUserName("root");
        codeGenerator.setDataBasePassword("wistronits@123");
        codeGenerator.setTableNames(new String[]{"alarm_level","alarm_name"});
        codeGenerator.setPackageName("com.fiberhome.filink.alarmset_server");

        CodeGeneratorUtils.generatorCode(codeGenerator);
    }
}
