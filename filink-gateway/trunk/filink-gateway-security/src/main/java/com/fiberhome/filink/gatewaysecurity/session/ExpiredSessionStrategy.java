package com.fiberhome.filink.gatewaysecurity.session;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.gatewaysecurity.constant.I18Const;
import com.fiberhome.filink.gatewaysecurity.constant.LoginConstant;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 并发登录处理
 *
 * @author yuanyao@wistronits.com
 * create on 2018/11/27 8:40 PM
 */
@Slf4j
public class ExpiredSessionStrategy implements SessionInformationExpiredStrategy {


    /**
     * @param sessionInformationExpiredEvent
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent sessionInformationExpiredEvent)
            throws IOException, ServletException {

        sessionInformationExpiredEvent.getResponse().setContentType(LoginConstant.RESPONSE_HEAD_TYPE);
        sessionInformationExpiredEvent.getResponse().getWriter().write(JSONObject.toJSONString(ResultUtils.warn(
                ResultCode.FAIL, I18nUtils.getSystemString(I18Const.LOGIN_ANOTHER_LOCATION))));
    }
}
