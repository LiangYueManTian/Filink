package com.fiberhome.filink.picture.annotation;

import java.lang.annotation.*;

/**
 * @author chaofanrong@wistronits.com
 * description 任务注解
 * date 2019/3/30 14:43
 */

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AddExportPicTaskAnnotation {
}
