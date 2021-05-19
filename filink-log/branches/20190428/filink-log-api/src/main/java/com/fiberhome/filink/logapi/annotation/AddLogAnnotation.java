package com.fiberhome.filink.logapi.annotation;

import java.lang.annotation.*;

/**
 * @author hedongwei@wistronits.com
 * description 日志注解
 * date 2019/1/21 20:11
 */

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AddLogAnnotation {

    /**
     *  工单的操作类型
     */
    String value() default "";

    /**
     *  日志的类型
     */
    String logType() default "";

    /**
     *  功能编码
     */
    String functionCode() default "";

    /**
     *  数据的名称
     */
    String dataGetColumnName() default "";

    /**
     *  编号的名称
     */
    String dataGetColumnId() default "";

}
