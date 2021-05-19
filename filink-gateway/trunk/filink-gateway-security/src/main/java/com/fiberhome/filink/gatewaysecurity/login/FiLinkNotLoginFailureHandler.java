package com.fiberhome.filink.gatewaysecurity.login;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.gatewaysecurity.constant.I18Const;
import com.fiberhome.filink.gatewaysecurity.constant.LoginConstant;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
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
@Component(value = "fiLinkNotLoginFailureHandler")
public class FiLinkNotLoginFailureHandler extends ExceptionMappingAuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        AuthenticationException e) throws IOException, ServletException {

        // 通过response写入响应
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setContentType(LoginConstant.RESPONSE_HEAD_TYPE);

        httpServletResponse.getWriter().write(JSONObject.toJSONString(ResultUtils.success(
                ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.USER_NOT_LOGIN)))));

    }
}
