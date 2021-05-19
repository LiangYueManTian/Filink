package com.fiberhome.filink.gateway_security.session;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * session失效处理
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/1 4:48 PM
 */
@Slf4j
@RestController
@RequestMapping
public class SessionController {

    @GetMapping("/session/invalid")
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public Result sessionInvalid() {
        log.info("用户session失效");
        return ResultUtils.warn(ResultCode.FAIL, "session失效");

    }
}
