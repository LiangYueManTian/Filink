package com.fiberhome.filink.systemlanguage.stream;

import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  系统语言消费者
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-11
 */
@Slf4j
@Component
public class SystemLanguageConsumer {
    /**
     * 系统语言
     */
    @Autowired
    private SystemLanguageUtil languageUtil;
    /**
     * 消息监听处理
     *
     * @param language language
     */
    @StreamListener(SystemLanguageChannel.SYSTEM_LANGUAGE_INPUT)
    public void changeLanguage(String language) throws Exception {
        log.info("change ocean connect's system language consuming>>>>>>>>>>");
        if (StringUtils.isEmpty(language)) {
            return;
        }
        languageUtil.changeSystemLanguageRedis(language);
    }
}
