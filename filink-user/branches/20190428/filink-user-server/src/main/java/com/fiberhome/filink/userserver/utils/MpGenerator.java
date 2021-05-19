package com.fiberhome.filink.userserver.utils;

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
        codeGenerator.setAuthor("xgong103@fiberhome.com");
        codeGenerator.setProjectPath("D:/Code/10-Sys Code/filink-user/trunk/filink-user-server/src/main/java/");
        codeGenerator.setDataBaseIp("10.5.43.18");
        codeGenerator.setDataBasePort("3306");
        codeGenerator.setDataBaseName("filink_user");
        codeGenerator.setDataBaseUserName("root");
        codeGenerator.setDataBasePassword("wistronits@123");
        codeGenerator.setTableNames(new String[]{"role_devicetype"});
        codeGenerator.setPackageName("com.fiberhome.filink.user_server");

        CodeGeneratorUtils.generatorCode(codeGenerator);
    }
}
