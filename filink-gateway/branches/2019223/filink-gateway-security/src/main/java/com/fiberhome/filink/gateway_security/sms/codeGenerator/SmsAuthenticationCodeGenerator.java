package com.fiberhome.filink.gateway_security.sms.codeGenerator;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * 短信验证码生成器
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/30 8:58 PM
 */
public class SmsAuthenticationCodeGenerator {


    /**
     * 生产短信验证码
     * @param request
     * @return
     */
    public static ValidateCode generator(ServletWebRequest request) {
        // TODO: 2018/12/30 生产随机码  长度后续改为可配置
        String code = RandomStringUtils.randomNumeric(6);
        // TODO: 2018/12/30 过期时间后续改成可配置
        return new ValidateCode(code,60);
    }
}
