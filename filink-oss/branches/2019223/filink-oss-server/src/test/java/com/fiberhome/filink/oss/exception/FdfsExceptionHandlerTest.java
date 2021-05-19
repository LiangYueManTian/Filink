package com.fiberhome.filink.oss.exception;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.oss.bean.FdfsI18n;
import com.fiberhome.filink.oss.utils.FdfsResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>
 *     异常捕获类测试类
 * </p>
 * @author chaofang@wistronits.com
 * @since 2019/3/8
 */
@RunWith(JMockit.class)
public class FdfsExceptionHandlerTest {
    /**
     * 测试对象
     */
    @Tested
    private FdfsExceptionHandler fdfsExceptionHandler;

    @Test
    public void handlerFdfsParamException() {
        FdfsI18n fdfsI18n = new FdfsI18n();
        String exception = fdfsI18n.OSS_PARAM_ERROR;
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(exception);
                result = "请求参数错误";
            }
        };
        Result result = fdfsExceptionHandler.handlerFdfsParamException(new FilinkFdfsParamException());
        Assert.assertTrue(result.getCode() == FdfsResultCode.OSS_PARAM_ERROR);
    }
}
