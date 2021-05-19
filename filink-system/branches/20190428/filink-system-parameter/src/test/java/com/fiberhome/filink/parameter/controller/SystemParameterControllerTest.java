package com.fiberhome.filink.parameter.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.parameter.bean.*;
import com.fiberhome.filink.parameter.constant.SystemParameterI18n;
import com.fiberhome.filink.parameter.constant.SystemParameterResultCode;
import com.fiberhome.filink.parameter.service.SystemParameterService;
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
public class SystemParameterControllerTest {
    /**测试对象 SystemParameterController*/
    @Tested
    private SystemParameterController systemParameterController;
    /**Mock SystemParameterService*/
    @Injectable
    private SystemParameterService systemParameterService;
    /**测试数据*/
    private AliAccessKeyParam aliAccessKeyParam;

    /**
     * 初始化数据
     */
    @Before
    public void setUp() {
        aliAccessKeyParam = new AliAccessKeyParam();
        aliAccessKeyParam.setParamId("dihfkaihwd");
        AliAccessKey aliAccessKey = new AliAccessKey();
        aliAccessKey.setAccessKeyId("aiohwdihail");
        aliAccessKey.setAccessKeySecret("ajwdijail");
        aliAccessKeyParam.setAliAccessKey(aliAccessKey);
    }

    /**
     * updateMessage
     */
    @Test
    public void updateMessageTest() {
        AliAccessKeyParam aliAccessKeyParam1 = new AliAccessKeyParam();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "parameter error";
            }
        };
        Result result = systemParameterController.updateMessage(aliAccessKeyParam1);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR);
        result = systemParameterController.updateMessage(aliAccessKeyParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }
    /**
     * updateMail
     */
    @Test
    public void updateMailTest() {
        AliAccessKeyParam aliAccessKeyParam1 = new AliAccessKeyParam();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "parameter error";
            }
        };
        Result result = systemParameterController.updateMail(aliAccessKeyParam1);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR);
        result = systemParameterController.updateMail(aliAccessKeyParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }
    /**
     * updateMessageNotification
     */
    @Test
    public void updateMessageNotificationTest() {
        MessageNotificationParam messageNotificationParam = new MessageNotificationParam();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "parameter error";
            }
        };
        Result result = systemParameterController.updateMessageNotification(messageNotificationParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR);
        MessageNotification messageNotification = new MessageNotification();
        messageNotification.setMessageRemind("1");
        messageNotificationParam.setParamId("aojdopjaw");
        messageNotificationParam.setMessageNotification(messageNotification);
        result = systemParameterController.updateMessageNotification(messageNotificationParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }
    /**
     * updateMobilePush
     */
    @Test
    public void updateMobilePushTest() {
        AliAccessKeyParam aliAccessKeyParam1 = new AliAccessKeyParam();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "parameter error";
            }
        };
        Result result = systemParameterController.updateMobilePush(aliAccessKeyParam1);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR);
        result = systemParameterController.updateMobilePush(aliAccessKeyParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }

    /**
     * updateFtpSettings
     */
    @Test
    public void updateFtpSettingsTest() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "parameter error";
            }
        };
        FtpSettingsParam ftpSettingsParam = new FtpSettingsParam();
        ftpSettingsParam.setParamId("2107210");
        FtpSettings ftpSettings = new FtpSettings();
        ftpSettingsParam.setFtpSettings(ftpSettings);
        Result result = systemParameterController.updateFtpSettings(ftpSettingsParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR);
        ftpSettingsParam.getFtpSettings().setIpAddress("10.5.24");
        ftpSettingsParam.getFtpSettings().setUserName("root");
        ftpSettingsParam.getFtpSettings().setPassword("123456");
        ftpSettingsParam.getFtpSettings().setPort(22);
        ftpSettingsParam.getFtpSettings().setDisconnectTime(1024);
        result = systemParameterController.updateFtpSettings(ftpSettingsParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR);
        ftpSettingsParam.getFtpSettings().setDisconnectTime(5);
        result = systemParameterController.updateFtpSettings(ftpSettingsParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR);
        ftpSettingsParam.getFtpSettings().setIpAddress("10.5.24.142");
        result = systemParameterController.updateFtpSettings(ftpSettingsParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }
    /**
     * updateDisplaySettings
     */
    @Test
    public void updateDisplaySettingsTest() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "parameter error";
            }
        };
        DisplaySettings displaySettings = new DisplaySettings();
        DisplaySettingsParam displaySettingsParam = new DisplaySettingsParam();
        displaySettingsParam.setParamId("2107209");
        displaySettings.setScreenDisplay("1");
        displaySettings.setTimeType("a");
        Result result = systemParameterController.updateDisplaySettings(displaySettings, displaySettingsParam, null);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR);
//        displaySettings.setTimeType(DisplayTimeEnum.LOCAL_TIME.getValue());
//        result = systemParameterController.updateDisplaySettings(displaySettings, displaySettingsParam, null);
//        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }

}
