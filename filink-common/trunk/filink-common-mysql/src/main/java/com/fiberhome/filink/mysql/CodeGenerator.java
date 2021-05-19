package com.fiberhome.filink.mysql;

import lombok.Data;

/**
 * 代码生成参数封装
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/28 19:21
 */
@Data
public class CodeGenerator {

    /**
     * 代码输出路径
     * 必须要main\\java\\下
     * 如：   E:\wistronits\filink\svn_01\10-Sys Code\filink-user\trunk\filink-user-server\src\main\java\
     * 注意斜杠转义
     */
    private String projectPath;
    /**
     * 作者
     * 如：   yuanyao@wistronits.com
     */
    private String author;
    /**
     * 表名数组
     */
    private String[] tableNames;
    /**
     * 数据库用户名
     */
    private String dataBaseUserName;
    /**
     * 数据库密码
     */
    private String dataBasePassword;
    /**
     * 数据库ip
     */
    private String dataBaseIp;
    /**
     * 数据库端口
     */
    private String dataBasePort;
    /**
     * 数据库名称
     */
    private String dataBaseName;

    /**
     *父级包名 以com开头，如下所示：
     * com.fiberhome.filink.userserver
     */
    private String packageName;

    public CodeGenerator() {
    }

    public CodeGenerator(String projectPath,
                         String author,
                         String[] tableNames,
                         String dataBaseUserName,
                         String dataBasePassword,
                         String dataBaseIp,
                         String dataBasePort,
                         String dataBaseName) {
        this.projectPath = projectPath;
        this.author = author;
        this.tableNames = tableNames;
        this.dataBaseUserName = dataBaseUserName;
        this.dataBasePassword = dataBasePassword;
        this.dataBaseIp = dataBaseIp;
        this.dataBasePort = dataBasePort;
        this.dataBaseName = dataBaseName;
    }



}
