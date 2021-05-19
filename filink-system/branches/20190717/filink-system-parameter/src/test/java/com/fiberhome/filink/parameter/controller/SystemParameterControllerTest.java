package com.fiberhome.filink.parameter.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.parameter.bean.*;
import com.fiberhome.filink.parameter.constant.SystemParameterI18n;
import com.fiberhome.filink.parameter.constant.SystemParameterResultCode;
import com.fiberhome.filink.parameter.service.SystemParameterService;
import com.fiberhome.filink.parameter.constant.DisplayTimeEnum;
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
     * queryMail
     */
    @Test
    public void queryMailTest() {
        systemParameterController.queryMail();
    }
    /**
     * queryMobilePush
     */
    @Test
    public void queryMobilePushTest() {
        systemParameterController.queryMobilePush();
    }
    /**
     * queryHomeDeviceLimit
     */
    @Test
    public void queryHomeDeviceLimitTest() {
        systemParameterController.queryHomeDeviceLimit();
    }
    /**
     * queryFtpSettings
     */
    @Test
    public void queryFtpSettingsTest() {
        systemParameterController.queryFtpSettings();
    }
    /**
     * queryMessage
     */
    @Test
    public void queryMessageTest() {
        systemParameterController.queryMessage();
    }
    /**
     * queryLanguageAll
     */
    @Test
    public void queryLanguageAllTest() {
        systemParameterController.queryLanguageAll();
    }
    /**
     * selectDisplaySettingsParamForPageCollection
     */
    @Test
    public void selectDisplaySettingsParamForPageCollectionTest() {
        systemParameterController.selectDisplaySettingsParamForPageCollection();
    }
    /**
     * checkAppSoftwareUpgrade
     */
    @Test
    public void checkAppSoftwareUpgradeTest() {
        AppUpgrade appUpgrade = new AppUpgrade();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "请求参数错误";
            }
        };
        Result result = systemParameterController.checkAppSoftwareUpgrade(appUpgrade);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR);
        appUpgrade.setAppSoftwareVersion("RP8004.004Z.apk");
        result = systemParameterController.checkAppSoftwareUpgrade(appUpgrade);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }
    /**
     * checkAppHardwareUpgrade
     */
    @Test
    public void checkAppHardwareUpgradeTest() {
        AppUpgrade appUpgrade = new AppUpgrade();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "请求参数错误";
            }
        };
        Result result = systemParameterController.checkAppHardwareUpgrade(appUpgrade);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR);
        appUpgrade.setAppHardwareVersion("RP8004.004Z.bin");
        result = systemParameterController.checkAppHardwareUpgrade(appUpgrade);
        Assert.assertEquals(result.getCode(), (int) ResultCode.SUCCESS);
    }


    /**
     * updateMessage
     */
    @Test
    public void updateMessageTest() {
        AliAccessKeyParam aliAccessKeyParam1 = new AliAccessKeyParam();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "请求参数错误";
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
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "请求参数错误";
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
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "请求参数错误";
            }
        };
        Result result = systemParameterController.updateMessageNotification(messageNotificationParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR);
        MessageNotification messageNotification = new MessageNotification();
        messageNotification.setMessageRemind("1");
        messageNotification.setSoundRemind("1");
        messageNotification.setSoundSelected("a.mp3");
        messageNotification.setRetentionTime(2);
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
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "请求参数错误";
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
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "请求参数错误";
            }
        };
        FtpSettingsParam ftpSettingsParam = new FtpSettingsParam();
        ftpSettingsParam.setParamId("2107210");
        Result result = systemParameterController.updateFtpSettings(ftpSettingsParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR);
        FtpSettings ftpSettings = new FtpSettings();
        ftpSettings.setIpAddress("0:0:0:0:0:0:0:0");
        ftpSettings.setInnerIpAddress("0:0:0:0:0:0:0:0");
        ftpSettings.setUserName("root");
        ftpSettings.setPassword("123456");
        ftpSettings.setPort(22);
        ftpSettings.setDisconnectTime(2);
        ftpSettingsParam.setFtpSettings(ftpSettings);
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
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "请求参数错误";
            }
        };
        DisplaySettings displaySettings = new DisplaySettings();
        DisplaySettingsParam displaySettingsParam = new DisplaySettingsParam();
        Result result = systemParameterController.updateDisplaySettings(displaySettings, displaySettingsParam, null);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR);
        displaySettingsParam.setParamId("2107209");
        displaySettings.setScreenDisplay("1");
        displaySettings.setTimeType(DisplayTimeEnum.LOCAL_TIME.getValue());
        displaySettings.setSystemLanguage("CN");
        displaySettings.setScreenScroll("1");
        displaySettings.setScreenScrollTime(10);
        displaySettings.setHomeDeviceLimit(20000);
        result = systemParameterController.updateDisplaySettings(displaySettings, displaySettingsParam, null);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }

    /**
     * sendMessageTest
     */
    @Test
    public void sendMessageTestTest() {
        MessageSendTest messageSendTest = new MessageSendTest();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "请求参数错误";
            }
        };
        Result result = systemParameterController.sendMessageTest(messageSendTest);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR);
        messageSendTest.setAccessKeyId("agduioawuihawd");
        messageSendTest.setAccessKeySecret("hshshsgdjdgasjsiabgshdhbnjhjsk");
        messageSendTest.setPhone("16602779659");
        result = systemParameterController.sendMessageTest(messageSendTest);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }

    /**
     * sendMailTest
     */
    @Test
    public void sendMailTestTest() {
        MailSendTest mailSendTest = new MailSendTest();
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "请求参数错误";
            }
        };
        Result result = systemParameterController.sendMailTest(mailSendTest);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR);
        mailSendTest.setAccessKeyId("akjhdhakjd");
        mailSendTest.setAccessKeySecret("dnhakjwhduwhuiawhy");
        mailSendTest.setToAddress("1352627145@qq.com");
        result = systemParameterController.sendMailTest(mailSendTest);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }

    /**
     * ftpSettingsTest
     */
    @Test
    public void ftpSettingsTestTest() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR);
                result = "请求参数错误";
            }
        };
        FtpSettingsParam ftpSettingsParam = new FtpSettingsParam();
        ftpSettingsParam.setParamId("2107210");
        Result result = systemParameterController.ftpSettingsTest(ftpSettingsParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR);
        FtpSettings ftpSettings = new FtpSettings();
        ftpSettings.setIpAddress("0:0:0:0:0:0:0:0");
        ftpSettings.setInnerIpAddress("0:0:0:0:0:0:0:0");
        ftpSettings.setUserName("root");
        ftpSettings.setPassword("123456");
        ftpSettings.setPort(22);
        ftpSettings.setDisconnectTime(2);
        ftpSettingsParam.setFtpSettings(ftpSettings);
        result = systemParameterController.ftpSettingsTest(ftpSettingsParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }

}
