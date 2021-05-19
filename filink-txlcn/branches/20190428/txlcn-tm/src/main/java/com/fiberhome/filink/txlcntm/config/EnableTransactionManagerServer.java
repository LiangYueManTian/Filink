package com.fiberhome.filink.txlcntm.config;

import com.fiberhome.filink.txlcntm.TMAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:
 * Date: 19-2-15 下午5:25
 *
 * @author ujued
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(value = {TMAutoConfiguration.class})
public @interface EnableTransactionManagerServer {
}
