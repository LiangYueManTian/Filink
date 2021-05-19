package com.fiberhome.filink.workflowbusinessserver.utils;

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
        codeGenerator.setAuthor("chaofanrong@wistronits.com");
        codeGenerator.setProjectPath("D:\\workspace\\ideaWorkspace\\filink\\filink-workflow-business\\trunk\\filink-workflow-business-server\\src\\main\\java\\");
        codeGenerator.setDataBaseIp("10.5.43.18");
        codeGenerator.setDataBasePort("3306");
        codeGenerator.setDataBaseName("filink_workflow_business");
        codeGenerator.setDataBaseUserName("root");
        codeGenerator.setDataBasePassword("wistronits@123");
        codeGenerator.setTableNames(new String[]{"proc_inspection_record"});
        codeGenerator.setPackageName("com.fiberhome.filink.workflowbusinessserver");

        CodeGeneratorUtils.generatorCode(codeGenerator);
    }
}
