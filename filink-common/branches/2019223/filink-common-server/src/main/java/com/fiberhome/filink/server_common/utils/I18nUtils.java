package com.fiberhome.filink.server_common.utils;

import com.fiberhome.filink.server_common.configuration.LanguageConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 国际化工具类
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/3 10:42
 */
@Slf4j
public class I18nUtils {

    private static final String REOURCES_PATH = "i18n/resource";
    private static final String CN = "cn";
    private static final String US = "us";

    public static Locale getLocal() {
        String environment = SpringUtils.getBean(LanguageConfig.class).getEnvironment();
        if (StringUtils.equals(environment.toLowerCase(), CN)) {
            return Locale.CHINA;
        }else if (StringUtils.equals(environment.toLowerCase(), US)){
            return Locale.US;
        }else {
            return null;
        }
    }

    /**
     * 获取配置文件中指定key和指定环境中的数据
     * 如果不传语言环境 则读取默认配置中的数据
     * @param key key
     * @return 值
     */
    public static String getString(String key) {
        Locale locale = getLocal();
        if (locale == null) {
            return ResourceBundle.getBundle(REOURCES_PATH).getString(key);
        }
       return ResourceBundle.getBundle(REOURCES_PATH, locale).getString(key);
    }

    public static Integer getInteger(String key) {
        Locale locale = getLocal();
        if (locale == null) {
            String string = ResourceBundle.getBundle(REOURCES_PATH).getString(key);
            try {
                int i = Integer.parseInt(string);
                return i;
            } catch (Exception e) {
                log.info("获取配置Sting转int失败");
            }
            return null;
        }else {
            String string = ResourceBundle.getBundle(REOURCES_PATH, locale).getString(key);
            try {
                int i = Integer.parseInt(string);
                return i;
            } catch (Exception e) {
                log.info("获取配置Sting转int失败");
            }
            return null;
        }
    }

}