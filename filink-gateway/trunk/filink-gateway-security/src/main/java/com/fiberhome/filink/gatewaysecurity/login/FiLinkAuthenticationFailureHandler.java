package com.fiberhome.filink.gatewaysecurity.login;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.gatewaysecurity.constant.I18Const;
import com.fiberhome.filink.gatewaysecurity.constant.LoginConstant;
import com.fiberhome.filink.gatewaysecurity.constant.LoginResultCode;
import com.fiberhome.filink.gatewaysecurity.utils.IpUtil;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.userapi.bean.UserParameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

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

    @Autowired
    private UserFeign userFeign;

    private static final String USER_NAME = "username";

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType(LoginConstant.RESPONSE_HEAD_TYPE);
        String username = httpServletRequest.getParameter(USER_NAME);
        log.info("User {} login：Login failed", username);
        String phoneNumber = httpServletRequest.getParameter(LoginConstant.LOGIN_BY_PHONE);
        if (StringUtils.isNotEmpty(phoneNumber)) {
            httpServletResponse.getWriter().write(JSONObject.toJSONString(ResultUtils.success(
                    ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.VERIFICATION_CODE_ERROR)))));
            return;
        }

        //用户登录出错，处理出错的情况信息
        UserParameter userParameter = new UserParameter();
        String loginIp = IpUtil.getIpAdrress(httpServletRequest);
        int port = IpUtil.getPort(httpServletRequest);
        userParameter.setLoginIp(loginIp);
        userParameter.setPort(port);
        userParameter.setUserName(username);
        Object userObject = userFeign.queryUserByName(userParameter);
        User getUser = JSONArray.toJavaObject((JSON) JSONArray.toJSON(userObject), User.class);
        Integer failFlag = loginFailInfo(getUser);

        switch (failFlag) {
            case LoginResultCode.USER_HAS_LOCKED:
                httpServletResponse.getWriter().write(JSONObject.toJSONString(ResultUtils.success(
                        ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.USER_HAS_LOCKED)))));
                break;
            case LoginResultCode.USER_HAS_FORBIDDEN:
                httpServletResponse.getWriter().write(JSONObject.toJSONString(ResultUtils.success(
                        ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.USER_HAS_FORBIDDEN)))));
                break;
            default:
                if (StringUtils.isNotEmpty(username)) {
                    userFeign.dealLoginFail(userParameter);
                }
                httpServletResponse.getWriter().write(JSONObject.toJSONString(ResultUtils.success(
                        ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(I18Const.LOGIN_PASSWORD_WRONG)))));
                break;
        }
    }

    /**
     * 判断用户出错的情况
     *
     * @return
     */
    private Integer loginFailInfo(User getUser) {

        if (getUser == null) {
            return ResultCode.SUCCESS;
        }

        Long unlockTime = getUser.getUnlockTime();
        //如果被锁定
        if (!ObjectUtils.isEmpty(unlockTime)) {
            long currentTime = System.currentTimeMillis();
            if (unlockTime >= currentTime) {
                return LoginResultCode.USER_HAS_LOCKED;
            }
        } else if (LoginConstant.USER_STOP_STATUS.equals(getUser.getUserStatus())) {
            return LoginResultCode.USER_HAS_FORBIDDEN;
        }

        return ResultCode.SUCCESS;
    }
}

