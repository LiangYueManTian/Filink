package com.fiberhome.filink.gatewaysecurity.login;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.gatewaysecurity.constant.I18Const;
import com.fiberhome.filink.gatewaysecurity.constant.UserConst;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 退出成功处理
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/1 6:54 PM
 */
@Slf4j
public class LoginOutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {

        response.setContentType(UserConst.RESPONSE_HEAD_TYPE);
        response.getWriter().write(JSONObject.toJSONString(
                ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(I18Const.LOG_OUT))));
    }
}
