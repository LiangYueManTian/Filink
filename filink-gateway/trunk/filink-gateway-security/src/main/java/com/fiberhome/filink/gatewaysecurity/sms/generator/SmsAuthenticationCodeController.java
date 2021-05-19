package com.fiberhome.filink.gatewaysecurity.sms.generator;

import com.fiberhome.filink.gatewaysecurity.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.social.connect.web.HttpSessionSessionStrategy;
//import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 短信登录开发：验证码生成器
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/30 8:53 PM
 */
@Slf4j
@RestController
public class SmsAuthenticationCodeController {


    /**
     * 先用session存放  后续考虑存放redis  或者把session放在redis中
     */

    @Autowired
    private RedisRepository redisRepository;


    @GetMapping("/code/sms")
    public void createSmsAuthenticationCode(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException {

        ServletWebRequest servletWebRequest = new ServletWebRequest(request);
        // 手机号
        String mobile = ServletRequestUtils.getRequiredStringParameter(servletWebRequest.getRequest(), "mobile");


        ValidateCode smsCode = SmsAuthenticationCodeGenerator.generator(new ServletWebRequest(request));
        // TODO: 2019/1/2 存入缓存
        redisRepository.save(new ServletWebRequest(request),smsCode,"SMS_CODE");
        // 获取手机号
        //发
        // 发送短信验证码
        log.info(mobile+"::::"+smsCode.getCode());
    }
}
