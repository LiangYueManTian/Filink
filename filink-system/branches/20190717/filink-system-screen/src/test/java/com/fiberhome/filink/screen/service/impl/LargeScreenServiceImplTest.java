package com.fiberhome.filink.screen.service.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.screen.bean.LargeScreen;
import com.fiberhome.filink.screen.constant.LargeScreenConstants;
import com.fiberhome.filink.screen.constant.LargeScreenI18n;
import com.fiberhome.filink.screen.constant.LargeScreenResultCode;
import com.fiberhome.filink.screen.dao.LargeScreenDao;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemcommons.utils.SystemLanguageUtil;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(JMockit.class)
public class LargeScreenServiceImplTest {
    /**测试对象 LargeScreenController*/
    @Tested
    private LargeScreenServiceImpl largeScreenService;
    /**Mock LargeScreenDao*/
    @Injectable
    private LargeScreenDao largeScreenDao;
    /**Mock SystemLanguageUtil*/
    @Injectable
    private SystemLanguageUtil systemLanguageUtil;
    /**Mock LogProcess*/
    @Injectable
    private LogProcess logProcess;
    private LargeScreen largeScreen;
    /**
     * 初始化数据
     */
    @Before
    public void setUp() {
        largeScreen = new LargeScreen();
        largeScreen.setLargeScreenId("100011");
        largeScreen.setLargeScreenName("大屏");
        LargeScreenConstants largeScreenConstants = new LargeScreenConstants();
        LargeScreenI18n largeScreenI18n = new LargeScreenI18n();
        Assert.assertNotNull(largeScreenConstants);
        Assert.assertNotNull(largeScreenI18n);
    }
    /**
     * queryLargeScreenAll
     */
    @Test
    public void queryLargeScreenAllTest() {
        List<LargeScreen> largeScreens = new ArrayList<>();
        new Expectations() {
            {
                largeScreenDao.queryLargeScreenAll();
                result = largeScreens;
            }
        };
        Result result = largeScreenService.queryLargeScreenAll();
        Assert.assertEquals(result.getCode(), 0);
    }
    /**
     * queryLargeScreenNameRepeat
     */
    @Test
    public void queryLargeScreenNameRepeatTest() {
        new Expectations() {
            {
                largeScreenDao.queryLargeScreenNameRepeat(largeScreen);
                result = null;
            }
        };
        Result result = largeScreenService.queryLargeScreenNameRepeat(largeScreen);
        Assert.assertEquals(result.getCode(), 0);
        new Expectations(I18nUtils.class) {
            {
                largeScreenDao.queryLargeScreenNameRepeat(largeScreen);
                result = "1011";

                I18nUtils.getSystemString(LargeScreenI18n.LARGE_SCREEN_NAME_REPEAT);
                result = "名称重复";
            }
        };
        result = largeScreenService.queryLargeScreenNameRepeat(largeScreen);
        Assert.assertEquals(result.getCode(), (int) LargeScreenResultCode.LARGE_SCREEN_NAME_REPEAT);
    }
    /**
     * updateLargeScreenNameById
     */
    @Test
    public void updateLargeScreenNameByIdTest() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(LargeScreenI18n.LARGE_SCREEN_NAME_REPEAT);
                result = "名称重复";

                I18nUtils.getSystemString(LargeScreenI18n.LARGE_SCREEN_NAME_UPDATE_FAIL);
                result = "数据库操作异常";

                I18nUtils.getSystemString(LargeScreenI18n.LARGE_SCREEN_NAME_UPDATE_SUCCESS);
                result = "修改成功";

                largeScreenDao.queryLargeScreenNameRepeat(largeScreen);
                result = "1011";
            }
        };
        Result result = largeScreenService.updateLargeScreenNameById(largeScreen);
        Assert.assertEquals(result.getCode(), (int) LargeScreenResultCode.LARGE_SCREEN_NAME_REPEAT);
        new Expectations() {
            {
                largeScreenDao.queryLargeScreenNameRepeat(largeScreen);
                result = null;

                largeScreenDao.updateLargeScreenNameById(largeScreen);
                result = 0;
            }
        };
        result = largeScreenService.updateLargeScreenNameById(largeScreen);
        Assert.assertEquals(result.getCode(), (int) LargeScreenResultCode.LARGE_SCREEN_NAME_UPDATE_FAIL);
        new Expectations() {
            {
                largeScreenDao.updateLargeScreenNameById(largeScreen);
                result = 1;
            }
        };
        result = largeScreenService.updateLargeScreenNameById(largeScreen);
        Assert.assertEquals(result.getCode(), (int) LargeScreenResultCode.SUCCESS);
    }
}
