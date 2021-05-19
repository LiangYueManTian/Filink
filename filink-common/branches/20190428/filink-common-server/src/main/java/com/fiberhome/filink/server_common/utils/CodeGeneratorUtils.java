package com.fiberhome.filink.server_common.utils;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.fiberhome.filink.server_common.bean.CodeGenerator;

/**
 * 代码生成工具类
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/28 19:03
 */
public class CodeGeneratorUtils {

    /**
     * 根据表名生成代码
     * 自己转义xml文件到mapper目录
     */
    public static void generatorCode(CodeGenerator properties) {
        AutoGenerator autoGenerator = new AutoGenerator();

        // 选择模板引擎
        autoGenerator.setTemplateEngine(new FreemarkerTemplateEngine());

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setAuthor(properties.getAuthor());
        gc.setOutputDir(properties.getProjectPath());
        gc.setFileOverride(true); // 是否覆盖同名文件
        gc.setActiveRecord(true);
        gc.setEnableCache(false); // 二级缓存
        gc.setBaseResultMap(true);
        gc.setBaseColumnList(false);

//        /* 自定义文件命名， */
        gc.setMapperName("%sDao");
        gc.setXmlName("%sMapper");
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setControllerName("%sController");

        autoGenerator.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setTypeConvert(new MySqlTypeConvert() {

            // 自定义数据库表字段类型转换【可选】
            @Override
            public DbColumnType processTypeConvert(String fieldType) {
                System.out.println("转换类型：" + fieldType);
                // 注意！！processTypeConvert 存在默认类型转换，如果不是你要的效果请自定义返回、非如下直接返回。
                return super.processTypeConvert(fieldType);
            }

        });

        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername(properties.getDataBaseUserName());
        dsc.setPassword(properties.getDataBasePassword());
        dsc.setUrl("jdbc:mysql://" + properties.getDataBaseIp() + ":" + properties.getDataBasePort() + "/" + properties.getDataBaseName() + "?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false");
        autoGenerator.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 需要生成的表
        strategy.setInclude(properties.getTableNames());
        autoGenerator.setStrategy(strategy);

        // 包配置
        PackageConfig pc = new PackageConfig();
        // 不同项目需要修改parent
        pc.setParent(properties.getPackageName());
        pc.setMapper("dao");
        pc.setController("controller");
        pc.setServiceImpl("service.impl");
        pc.setEntity("bean");

        autoGenerator.setPackageInfo(pc);

        // 执行生成
        autoGenerator.execute();
    }
}
