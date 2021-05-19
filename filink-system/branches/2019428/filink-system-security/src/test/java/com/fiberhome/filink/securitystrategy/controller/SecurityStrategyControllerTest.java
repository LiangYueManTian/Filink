package com.fiberhome.filink.securitystrategy.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.securitystrategy.bean.AccountParam;
import com.fiberhome.filink.securitystrategy.bean.AccountSecurityStrategy;
import com.fiberhome.filink.securitystrategy.bean.PasswordParam;
import com.fiberhome.filink.securitystrategy.bean.PasswordSecurityStrategy;
import com.fiberhome.filink.securitystrategy.service.SecurityStrategyService;
import com.fiberhome.filink.securitystrategy.utils.SecurityStrategyResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class SecurityStrategyControllerTest {
    /**测试对象 SecurityStrategyController*/
    @Tested
    private SecurityStrategyController securityStrategyController;
    /**Mock SecurityStrategyService*/
    @Injectable
    private SecurityStrategyService securityStrategyService;

    /**
     * updatePasswordStrategy
     */
    @Test
    public void updatePasswordStrategyTest() {
        PasswordParam passwordParam = new PasswordParam();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString);
                result = null;
            }
        };
        Result result = securityStrategyController.updatePasswordStrategy(passwordParam);
        Assert.assertEquals(result.getCode(), (int) SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR);
        PasswordSecurityStrategy password = new PasswordSecurityStrategy();
        password.setMinLength(10);
        password.setContainLower("1");
        password.setContainNumber("1");
        password.setContainUpper("1");
        password.setContainSpecialCharacter("1");
        passwordParam.setPasswordSecurityStrategy(password);
        passwordParam.setParamId("1001");
        result = securityStrategyController.updatePasswordStrategy(passwordParam);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }

    /**
     *updateAccountStrategy
     */
    @Test
    public void updateAccountStrategyTest() {
        AccountParam accountParam = new AccountParam();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString);
                result = null;
            }
        };
        Result result = securityStrategyController.updateAccountStrategy(accountParam);
        Assert.assertEquals(result.getCode(), (int) SecurityStrategyResultCode.SECURITY_STRATEGY_PARAM_ERROR);
        AccountSecurityStrategy account = new AccountSecurityStrategy();
        account.setMinLength(11);
        account.setIllegalLoginCount(11);
        account.setIntervalTime(11);
        account.setForbidStrategy("1");
        account.setLockStrategy("1");
        account.setNoOperationTime(11);
        accountParam.setAccountSecurityStrategy(account);
        accountParam.setParamId("100001");
        result = securityStrategyController.updateAccountStrategy(accountParam);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }
}
