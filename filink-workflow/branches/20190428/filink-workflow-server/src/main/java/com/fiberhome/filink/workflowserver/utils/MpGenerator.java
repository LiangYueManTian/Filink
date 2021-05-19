package com.fiberhome.filink.workflowserver.utils;

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
        codeGenerator.setAuthor("hedongwei@wistronits.com");
        codeGenerator.setProjectPath("D:\\FILinkProject\\Filink_Project\\10-Sys Code\\filink-workflow\\trunk\\filink-workflow-server\\src\\main\\java\\com\\fiberhome\\filink\\workflowserver");
        codeGenerator.setDataBaseIp("10.5.43.12");
        codeGenerator.setDataBasePort("3306");
        codeGenerator.setDataBaseName("filink_workflow");
        codeGenerator.setDataBaseUserName("root");
        codeGenerator.setDataBasePassword("root");
        codeGenerator.setTableNames(new String[]{"act_process_task_config"});

        CodeGeneratorUtils.generatorCode(codeGenerator);
    }
}
