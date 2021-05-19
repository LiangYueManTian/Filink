package com.fiberhome.filink.systemcommons.utils;

import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemcommons.service.SysParamService;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * <p>
 * 系统语言公共类 测试类
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019/3/20
 */
@RunWith(MockitoJUnitRunner.class)
public class SystemLanguageUtilTest {

    /**
     * 测试对象
     */
    @InjectMocks
    private SystemLanguageUtil systemLanguageUtil;
    /**
     * 自动注入
     */
    @Mock
    private SysParamService sysParamService;

    /**
     * 系统语言
     */
    private String language;

    @Before
    public void setUp() {
        language = "CN";
    }
    /**
     * querySystemLanguage
     */
    @Test
    public void querySystemLanguageTest() {
        when(sysParamService.querySystemLanguage()).thenReturn("");
        String result = systemLanguageUtil.querySystemLanguage();
        Assert.assertEquals("CN", result);
        when(sysParamService.querySystemLanguage()).thenReturn(language);
        result = systemLanguageUtil.querySystemLanguage();
        Assert.assertEquals(language, result);
    }

    /**
     * getI18nString
     */
    @Test
    public void getI18nStringTest() {
        String key = "SYSTEM_LANGUAGE";
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString("CN", key);
                result = language;
            }
        };
        when(sysParamService.querySystemLanguage()).thenReturn(language);
        String result = systemLanguageUtil.getI18nString(key);
        Assert.assertEquals(language, result);
    }

    /**
     * sendChangeLanguage
     */
    @Test
    public void sendChangeLanguageTest() {
        try {
            systemLanguageUtil.sendChangeLanguage(language);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NullPointerException);
        }
    }
}
