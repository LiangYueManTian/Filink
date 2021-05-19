package com.fiberhome.filink.system_commons.controller;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.system_commons.bean.SysParamI18n;
import com.fiberhome.filink.system_commons.bean.SysParamResultCode;
import com.fiberhome.filink.system_commons.service.SysParamService;
import com.fiberhome.filink.system_commons.utils.ParamTypeRedisEnum;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 系统服务统一参数前端控制器 测试类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/20
 */
@RunWith(JMockit.class)
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
     * 测试根据类型查询相应参数信息
     */
    @Test
    public void queryParamTest() {
        String paramType = "12";
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(SysParamI18n.SYSTEM_PARAM_ERROR);
                result = "请求参数错误";
            }
        };
        Result result = sysParamController.queryParam(paramType);
        Assert.assertEquals(result.getCode(), (int) SysParamResultCode.SYSTEM_PARAM_ERROR);
        paramType = "0";
        result = sysParamController.queryParam(paramType);
        Assert.assertEquals(result.getCode(), (int) SysParamResultCode.SUCCESS);
    }

}
