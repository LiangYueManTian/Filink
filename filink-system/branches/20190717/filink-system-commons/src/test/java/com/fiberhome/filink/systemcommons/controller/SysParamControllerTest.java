package com.fiberhome.filink.systemcommons.controller;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemcommons.constant.SysParamI18n;
import com.fiberhome.filink.systemcommons.constant.SysParamResultCode;
import com.fiberhome.filink.systemcommons.service.SysParamService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * <p>
 * 系统服务统一参数前端控制器 测试类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/20
 */
@RunWith(MockitoJUnitRunner.class)
public class SysParamControllerTest {
    /**
     * 测试对象
     */
    @Tested
    private SysParamController sysParamController;
    /**
     * 自动注入Service
     */
    @Injectable
    private SysParamService sysParamService;
    /**
     * queryParam
     */
    @Test
    public void queryParamTest() {
        String paramType = "120";
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(SysParamI18n.SYSTEM_PARAM_ERROR);
                result = "parameter error";
            }
        };
        Result result = sysParamController.queryParam(paramType);
        Assert.assertEquals(result.getCode(), (int) SysParamResultCode.SYSTEM_PARAM_ERROR);
        paramType = "0";
        result = sysParamController.queryParam(paramType);
        Assert.assertEquals(result.getCode(), (int) SysParamResultCode.SUCCESS);
    }
    /**
     * querySystemLanguage
     */
    @Test
    public void querySystemLanguageTest() {
        sysParamController.querySystemLanguage();
    }

}
