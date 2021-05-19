package com.fiberhome.filink.demoserver.utils;


import com.fiberhome.filink.mysql.CodeGeneratorUtils;

/**
 * 代码生成类
 * 根据数据库生成对应的代码，因为包结构不好处理，所以指定放在外面，再拷贝到项目
 *
 * @author 姚远
 */
public class CodeGenerator {

    public static void main(String[] args) {
        com.fiberhome.filink.mysql.CodeGenerator codeGenerator = new com.fiberhome.filink.mysql.CodeGenerator();
        codeGenerator.setAuthor("姚远");
//        codeGenerator.setProjectPath("D:/Code/10-Sys Code/filink-user/trunk/filink-user-server/src/main/java/");
        codeGenerator.setProjectPath("C:\\Users\\lx\\Desktop\\code");
        codeGenerator.setDataBaseIp("10.5.24.224");
        codeGenerator.setDataBasePort("3306");
        codeGenerator.setDataBaseName("filink_user");
        codeGenerator.setDataBaseUserName("root");
        codeGenerator.setDataBasePassword("wistronits@123");
        codeGenerator.setTableNames(new String[]{"user"});
        codeGenerator.setPackageName("com.fiberhome.filink.demo");

        CodeGeneratorUtils.generatorCode(codeGenerator);
    }
}
