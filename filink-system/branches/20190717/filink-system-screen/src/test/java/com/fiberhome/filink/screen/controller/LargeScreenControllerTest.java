package com.fiberhome.filink.screen.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.screen.bean.LargeScreen;
import com.fiberhome.filink.screen.constant.LargeScreenI18n;
import com.fiberhome.filink.screen.constant.LargeScreenResultCode;
import com.fiberhome.filink.screen.service.LargeScreenService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class LargeScreenControllerTest {
    /**测试对象 LargeScreenController*/
    @Tested
    private LargeScreenController largeScreenController;
    /**Mock LargeScreenService*/
    @Injectable
    private LargeScreenService largeScreenService;
    private LargeScreen largeScreen;
    /**
     * 初始化数据
     */
    @Before
    public void setUp() {
        largeScreen = new LargeScreen();
        largeScreen.setLargeScreenId("100011");
        largeScreen.setLargeScreenName("大屏");
        largeScreen.setLargeScreenId(largeScreen.getLargeScreenId());
        largeScreen.setLargeScreenName(largeScreen.getLargeScreenName());
    }
    /**
     * queryLargeScreenAll
     */
    @Test
    public void queryLargeScreenAllTest() {
        largeScreenController.queryLargeScreenAll();
    }
    /**
     * queryLargeScreenNameRepeat
     */
    @Test
    public void queryLargeScreenNameRepeatTest() {
        LargeScreen largeScreen1 = new LargeScreen();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(LargeScreenI18n.LARGE_SCREEN_PARAM_ERROR);
                result = "参数错误";
            }
        };
        Result result = largeScreenController.queryLargeScreenNameRepeat(largeScreen1);
        Assert.assertEquals(result.getCode(), (int) LargeScreenResultCode.LARGE_SCREEN_PARAM_ERROR);
        result = largeScreenController.queryLargeScreenNameRepeat(largeScreen);
        Assert.assertEquals(result.getCode(), (int) LargeScreenResultCode.SUCCESS);
    }
    /**
     * updateLargeScreenNameById
     */
    @Test
    public void updateLargeScreenNameByIdTest() {
        LargeScreen largeScreen1 = new LargeScreen();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(LargeScreenI18n.LARGE_SCREEN_PARAM_ERROR);
                result = "参数错误";
            }
        };
        Result result = largeScreenController.updateLargeScreenNameById(largeScreen1);
        Assert.assertEquals(result.getCode(), (int) LargeScreenResultCode.LARGE_SCREEN_PARAM_ERROR);
        result = largeScreenController.updateLargeScreenNameById(largeScreen);
        Assert.assertEquals(result.getCode(), (int) LargeScreenResultCode.SUCCESS);
    }
}
