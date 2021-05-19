package com.fiberhome.filink.gateway_security.login;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.gateway_security.Const.I18Const;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录失败处理
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/30 4:30 PM
 */
@Slf4j
@Primary
@Component(value = "fiLinkAuthenticationFailureHandler")
public class FiLinkAuthenticationFailureHandler extends ExceptionMappingAuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        AuthenticationException e) throws IOException, ServletException {
        log.info("登录失败");
        log.info("返回json");
        // 通过response写入响应
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        // // TODO: 2018/12/30  登录失败返回结果需要定义
        httpServletResponse.getWriter().write(JSONObject.toJSONString(ResultUtils.success(
                ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(I18Const.LOGIN_PASSWORD_WRONG)))));

    }
}
