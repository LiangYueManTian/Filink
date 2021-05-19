package com.fiberhome.filink.securitystrategy.controller;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.securitystrategy.bean.AccountParam;
import com.fiberhome.filink.securitystrategy.bean.PasswordParam;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyI18n;
import com.fiberhome.filink.securitystrategy.constant.SecurityStrategyResultCode;
import com.fiberhome.filink.securitystrategy.service.SecurityStrategyService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *    安全策略前端控制器
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-02-20
 */
@RestController
@RequestMapping("/securityStrategy")
public class SecurityStrategyController {

    /**
     * 自动注入服务类
     */
    @Autowired
    private SecurityStrategyService securityStrategyService;

    /**
     * 查询账号安全策略
     * @return 账号安全策略
     */
    @GetMapping("/queryAccountSecurity")
    public Result queryAccountSecurity() {
        return securityStrategyService.queryAccountSecurity();
    }

    /**
     * 查询密码安全策略
     * @return 密码安全策略
     */
    @GetMapping("/queryPasswordSecurity")
    public Result queryPasswordSecurity() {
        return securityStrategyService.queryPasswordSecurity();
    }

    /**
     * 更新密码安全策略
     * @param passwordParam 密码安全策略
     * @return 更新条数
     */
    @PostMapping("/updatePasswordStrategy")
    public Result updatePasswordStrategy(@RequestBody PasswordParam passwordParam) {
        if (ObjectUtils.isEmpty(passwordParam) || passwordParam.check()) {
            return ResultUtils.warn(SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR,
                    I18nUtils.getSystemString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR));
        }
        return securityStrategyService.updatePasswordStrategy(passwordParam);
    }

    /**
     *更新账号安全策略
     * @param accountParam 账号安全策略
     * @return 更新条数
     */
    @PostMapping("/updateAccountStrategy")
    public Result updateAccountStrategy(@RequestBody AccountParam accountParam) {
        if (ObjectUtils.isEmpty(accountParam) || accountParam.check()) {
            return ResultUtils.warn(SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR,
                    I18nUtils.getSystemString(SecurityStrategyI18n.SECURITY_STRATEGY_PARAM_ERROR));
        }
        return securityStrategyService.updateAccountStrategy(accountParam);
    }
}
