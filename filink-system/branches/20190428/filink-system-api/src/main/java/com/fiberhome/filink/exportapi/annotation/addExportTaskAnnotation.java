package com.fiberhome.filink.exportapi.annotation;

import java.lang.annotation.*;

/**
 * @author qiqizhu@wistronits.com
 * description 任务注解
 * date 2019/1/21 20:11
 */

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface addExportTaskAnnotation {
}
