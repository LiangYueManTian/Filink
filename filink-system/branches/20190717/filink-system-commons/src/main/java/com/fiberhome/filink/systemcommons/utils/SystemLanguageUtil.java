package com.fiberhome.filink.systemcommons.utils;


import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemcommons.service.SysParamService;
import com.fiberhome.filink.systemcommons.stream.SystemLanguageChannel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
/**
 * <p>
 *  系统语言公共类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-11
 */
@Component
public class SystemLanguageUtil {

    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private SystemLanguageChannel systemLanguageChannel;
    /**
     * 系统语言
     */
    private static String systemLanguage = "CN";
    /**
     * 系统语言
     */
    private static final String DEFAULT_SYSTEM_LANGUAGE = "CN";
    /**
     * 查询系统语言
     *
     * @return 系统语言
     */
    public String querySystemLanguage() {
        //查询系统语言
        String language = sysParamService.querySystemLanguage();
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
        String language = sysParamService.querySystemLanguage();
        return I18nUtils.getString(language, key);
    }

    /**
     * 发送更改系统语言命令
     * @param language 系统语言
     */
    public void sendChangeLanguage(String language) {
        Message<String> message = MessageBuilder.withPayload(language).build();
        systemLanguageChannel.systemLanguageOutput().send(message);
    }
}
