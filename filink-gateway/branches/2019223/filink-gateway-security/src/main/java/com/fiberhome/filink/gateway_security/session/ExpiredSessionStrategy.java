package com.fiberhome.filink.gateway_security.session;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
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
     *
     * @param sessionInformationExpiredEvent
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent sessionInformationExpiredEvent)
            throws IOException, ServletException {
        // TODO: 2019/1/1
        log.info("并发登录，你被踢掉了");
        sessionInformationExpiredEvent.getResponse().setContentType("application/json;charset=UTF-8");
        sessionInformationExpiredEvent.getResponse().getWriter().write(JSONObject.toJSONString(ResultUtils.warn(
                ResultCode.FAIL,"并发登录，你被踢掉了")));
    }
}
