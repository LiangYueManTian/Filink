package com.fiberhome.filink.systemlanguage.utils;

import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemcommonsapi.api.SysParamLanguageFeign;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  系统语言公共类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-11
 */
@Data
@Slf4j
@Component
public class SystemLanguageUtil {

    @Autowired
    private SysParamLanguageFeign languageFeign;
    /**
     * 系统语言
     */
    private static String systemLanguage = "CN";
    /**
     * 系统语言
     */
    private static final String DEFAULT_SYSTEM_LANGUAGE = "CN";
    /**
     * 系统语言Redis
     */
    private static final String SYSTEM_LANGUAGE_REDIS = "SYSTEM_LANGUAGE";



    /**
     * 查询系统语言
     *
     * @return 系统语言
     */
    public String querySystemLanguage() {
        //查询系统语言
        String language = querySystem();
        if (StringUtils.isEmpty(language)) {
            //服务系统语言数据出现问题，重置为默认值
            systemLanguage = DEFAULT_SYSTEM_LANGUAGE;
        } else {
            //更改为配置语言
            systemLanguage = language;
        }
        return systemLanguage;
    }

    /**
     * 获取I8n字符串
     * @param key I8n文件Key值
     * @return I8n字符串
     */
    public String getI18nString(String key) {
        //查询系统语言
        String language = querySystem();
        return I18nUtils.getString(language, key);
    }

    /**
     * 放入缓存
     * @param language 系统语言
     */
    public void changeSystemLanguageRedis(String language) {
        //放入缓存
        RedisUtils.set(SYSTEM_LANGUAGE_REDIS, language);
    }

    /**
     * 查询系统语言
     *
     * @return 系统语言
     */
    private String querySystem() {
        //查询Redis缓存
        String language = (String) RedisUtils.get(SYSTEM_LANGUAGE_REDIS);
        if (StringUtils.isEmpty(language)) {
            //缓存不存在查询显示设置参数
            language = languageFeign.querySystemLanguage();
            //数据异常，信息丢失
            if (StringUtils.isEmpty(language)) {
                //服务系统语言数据出现问题，重置为默认值
                log.error("The system server is exception >>>>>>>>");
                return null;
            } else {
                //放入缓存
                changeSystemLanguageRedis(language);
            }
        }
        return language;
    }
}
