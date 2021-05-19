package com.fiberhome.filink.mongo.configuration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启mongo多数据源
 * 不能用sit作为后缀，不能修改配置文件名称，只能使用bootstrap开头
 * bootstrap-dev.yml
 * bootstrap-pro.yml
 *
 * @author yuanyao@wistronits.com
 * create on 2019-05-29 21:53
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({MongoMultiDataSourceRegistrar.class})
public @interface EnableMongoMultiDataSource {

}
