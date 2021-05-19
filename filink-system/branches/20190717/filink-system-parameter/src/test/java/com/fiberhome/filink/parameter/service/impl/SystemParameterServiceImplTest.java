package com.fiberhome.filink.parameter.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.parameter.bean.*;
import com.fiberhome.filink.parameter.constant.DisplayTimeEnum;
import com.fiberhome.filink.parameter.constant.SystemParameterConstants;
import com.fiberhome.filink.parameter.constant.SystemParameterI18n;
import com.fiberhome.filink.parameter.constant.SystemParameterResultCode;
import com.fiberhome.filink.parameter.dao.SysLanguageDao;
import com.fiberhome.filink.parameter.exception.*;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.smsapi.api.SendSmsAndEmail;
import com.fiberhome.filink.smsapi.bean.AliyunEmail;
import com.fiberhome.filink.smsapi.bean.AliyunSms;
import com.fiberhome.filink.systemcommons.bean.SysParam;
import com.fiberhome.filink.systemcommons.constant.ParamTypeRedisEnum;
import com.fiberhome.filink.systemcommons.dao.SysParamDao;
import com.fiberhome.filink.systemcommons.service.SysParamService;
import com.fiberhome.filink.systemcommons.utils.SystemLanguageUtil;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RunWith(JMockit.class)
public class SystemParameterServiceImplTest {
    /**测试对象 SystemParameterServiceImpl*/
    @Tested
    private SystemParameterServiceImpl systemParameterService;
    /**Mock SysParamDao*/
    @Injectable
    private SysParamDao sysParamDao;
    /**Mock SysLanguageDao*/
    @Injectable
    private SysLanguageDao sysLanguageDao;
    /**Mock LogProcess*/
    @Injectable
    private LogProcess logProcess;
    /**Mock SystemLanguageUtil*/
    @Injectable
    private SystemLanguageUtil languageUtil;
    /**Mock FdfsFeign*/
    @Injectable
    private FdfsFeign fdfsFeign;
    /**Mock DeviceFeign*/
    @Injectable
    private DeviceFeign deviceFeign;
    /**Mock SysParamService*/
    @Injectable
    private SysParamService sysParamService;
    /**Mock aliyunSendSms*/
    @Injectable
    private SendSmsAndEmail aliyunSendSms;
    /**Mock aliyunSendEmail*/
    @Injectable
    private SendSmsAndEmail aliyunSendEmail;
    /**Mock AliSendTest*/
    @Injectable
    private AliSendTest aliSendTest;
    /**测试数据 AliAccessKeyParam*/
    private AliAccessKeyParam aliAccessKeyParam;
    /**测试数据 SysParam*/
    private SysParam sysParam;
    /**
     * 初始化数据
     */
    @Before
    public void setUp() {
        SystemParameterI18n systemParameterI18n = new SystemParameterI18n();
        SystemParameterConstants systemParameterConstants = new SystemParameterConstants();
        aliAccessKeyParam = new AliAccessKeyParam();
        aliAccessKeyParam.setParamId("1");
        AliAccessKey aliAccessKey = new AliAccessKey();
        aliAccessKey.setAccessKeyId(systemParameterI18n.toString());
        aliAccessKey.setAccessKeyId("test");
        aliAccessKey.setAccessKeySecret(systemParameterConstants.toString());
        aliAccessKeyParam.setAliAccessKey(aliAccessKey);
        String presentValue = JSON.toJSONString(aliAccessKeyParam.getAliAccessKey());
        sysParam = new SysParam();
        sysParam.setParamId("1");
        sysParam.setParamType("1");
        sysParam.setPresentValue(presentValue);
        FtpSettings ftpSettings = new FtpSettings();
        ftpSettings.setIpAddress(aliAccessKey.getAccessKeyId());
        ftpSettings.setPassword(ftpSettings.getIpAddress());
        ftpSettings.setUserName(ftpSettings.getPassword());
        ftpSettings.setIpAddress(ftpSettings.getUserName());
        ftpSettings.setPort(1);
        ftpSettings.setDisconnectTime(ftpSettings.getPort());
        ftpSettings.setPort(ftpSettings.getDisconnectTime());
        presentValue = JSON.toJSONString(ftpSettings);
        sysParam.setDefaultValue(presentValue);
        aliSendTest = new AliSendTest();
        aliSendTest.setSignName( "基础设施");
        aliSendTest.setTemplateCode("SMS_153885498");
        aliSendTest.setAccountName("filink@fi-link.net");
        aliSendTest.setTagName("test");
    }

    /**
     * queryMessage
     */
    @Test
    public void queryMessageTest() {
        new Expectations() {
            {
                sysParamService.queryParamByType(ParamTypeRedisEnum.MESSAGE.getType());
                result = sysParam;
            }
        };
        AliAccessKey result = systemParameterService.queryMessage();
        Assert.assertEquals(result.getAccessKeyId(), "test");
    }

    /**
     * queryMail
     */
    @Test
    public void queryMailTest() {
        new Expectations() {
            {
                sysParamService.queryParamByType(ParamTypeRedisEnum.MAIL.getType());
                result = sysParam;
            }
        };
        AliAccessKey result = systemParameterService.queryMail();
        Assert.assertEquals(result.getAccessKeyId(), "test");
    }
    /**
     * queryMobilePush
     */
    @Test
    public void queryMobilePushTest() {
        new Expectations() {
            {
                sysParamService.queryParamByType(ParamTypeRedisEnum.MOBILE_PUSH.getType());
                result = sysParam;
            }
        };
        AliAccessKey result = systemParameterService.queryMobilePush();
        Assert.assertEquals(result.getAccessKeyId(), "test");
    }
    /**
     * queryHomeDeviceLimit
     */
    @Test
    public void queryHomeDeviceLimitTest() {
        SysParam sysParam = new SysParam();
        DisplaySettings displaySettings = new DisplaySettings();
        new Expectations() {
            {
                sysParamService.queryParamByType(ParamTypeRedisEnum.DISPLAY_SETTINGS.getType());
                result = sysParam;
            }
        };
        Integer result = systemParameterService.queryHomeDeviceLimit();
        Assert.assertNull(result);
        Integer homeDeviceLimit = 20000;
        displaySettings.setHomeDeviceLimit(homeDeviceLimit);
        sysParam.setPresentValue(JSONObject.toJSONString(displaySettings));
        result = systemParameterService.queryHomeDeviceLimit();
        Assert.assertEquals(result, homeDeviceLimit);
    }
    /**
     * queryFtpSettings
     */
    @Test
    public void queryFtpSettingsTest() {
        SysParam sysParam = new SysParam();
        FtpSettings ftpSettings = new FtpSettings();
        String test = "0.0.0.0";
        ftpSettings.setIpAddress(test);
        sysParam.setPresentValue(JSONObject.toJSONString(ftpSettings));
        new Expectations() {
            {
                sysParamService.queryParamByType(ParamTypeRedisEnum.FTP_SETTINGS.getType());
                result = sysParam;
            }
        };
        FtpSettings result = systemParameterService.queryFtpSettings();
        Assert.assertEquals(result.getIpAddress(), test);
    }
    /**
     * queryLanguageAll
     */
    @Test
    public void queryLanguageAllTest() {
        List<SysLanguage> sysLanguages = new ArrayList<>();
        new Expectations() {
            {
                sysLanguageDao.queryLanguageAll();
                result = sysLanguages;
            }
        };
        try {
            systemParameterService.queryLanguageAll();
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkSystemParameterDataException.class);
        }
        SysLanguage sysLanguage = new SysLanguage();
        sysLanguages.add(sysLanguage);
        sysLanguages.add(sysLanguage);
        Result result = systemParameterService.queryLanguageAll();
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }
    /**
     * selectDisplaySettingsParamForPageCollection
     */
    @Test
    public void selectDisplaySettingsParamForPageCollectionTest() {SysParam sysParam = new SysParam();
        DisplaySettings displaySettings = new DisplaySettings();
        new Expectations() {
            {
                sysParamService.queryParamByType(ParamTypeRedisEnum.DISPLAY_SETTINGS.getType());
                result = sysParam;
                sysParamService.queryParamByType(ParamTypeRedisEnum.MESSAGE_NOTIFICATION.getType());
                result = sysParam;
            }
        };
        Result result = systemParameterService.selectDisplaySettingsParamForPageCollection();
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }

    /**
     * checkAppSoftwareUpgrade
     */
    @Test
    public void checkAppSoftwareUpgradeTest() {
        ReflectionTestUtils.setField(systemParameterService, "appSoftwareUpgradePath", "/test/noFile/");
        String appSoftwareVersion = "RP8004.004B.apk";
        SysParam sysParam = new SysParam();
        FtpSettings ftpSettings = new FtpSettings();
        ftpSettings.setInnerIpAddress("39.98.72.132");
        sysParam.setPresentValue(JSONObject.toJSONString(ftpSettings));
        new Expectations(I18nUtils.class) {
            {
                sysParamService.queryParamByType(ParamTypeRedisEnum.FTP_SETTINGS.getType());
                result = sysParam;

                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_DATA_ERROR);
                result = "请求参数错误";
            }
        };
        Result result = systemParameterService.checkAppSoftwareUpgrade(appSoftwareVersion);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_DATA_ERROR);
        ftpSettings.setIpAddress(ftpSettings.getInnerIpAddress());
        ftpSettings.setPort(2341);
        ftpSettings.setUserName("filink");
        ftpSettings.setPassword("sss");
        ftpSettings.setDisconnectTime(2);
        sysParam.setPresentValue(JSONObject.toJSONString(ftpSettings));
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(SystemParameterI18n.FTP_GET_ERROR);
                result = "FTP服务器异常或FTP服务设置错误";
            }
        };
        result = systemParameterService.checkAppSoftwareUpgrade(appSoftwareVersion);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.FTP_GET_ERROR);
        ftpSettings.setPassword("filink");
        sysParam.setPresentValue(JSONObject.toJSONString(ftpSettings));
        result = systemParameterService.checkAppSoftwareUpgrade(appSoftwareVersion);
        AppUpgradeFtp upgradeFtp = (AppUpgradeFtp)result.getData();
        Assert.assertFalse(upgradeFtp.isUpgrade());
        ReflectionTestUtils.setField(systemParameterService, "appSoftwareUpgradePath", "/test/softFiles/");
        result = systemParameterService.checkAppSoftwareUpgrade(appSoftwareVersion);
        upgradeFtp = (AppUpgradeFtp)result.getData();
        Assert.assertTrue(upgradeFtp.isUpgrade());
    }

    /**
     * checkAppHardwareUpgrade
     */
    @Test
    public void checkAppHardwareUpgradeTest() {
        ReflectionTestUtils.setField(systemParameterService, "appHardwareUpgradePath", "/test/hardFiles/");
        String appHardwareVersion = "RP9003.005B.bin";
        SysParam sysParam = new SysParam();
        FtpSettings ftpSettings = new FtpSettings();
        ftpSettings.setInnerIpAddress("39.98.72.132");
        ftpSettings.setIpAddress(ftpSettings.getInnerIpAddress());
        ftpSettings.setPort(2341);
        ftpSettings.setUserName("filink");
        ftpSettings.setPassword("filink");
        ftpSettings.setDisconnectTime(2);
        sysParam.setPresentValue(JSONObject.toJSONString(ftpSettings));
        new Expectations() {
            {
                sysParamService.queryParamByType(ParamTypeRedisEnum.FTP_SETTINGS.getType());
                result = sysParam;
            }
        };
        Result result = systemParameterService.checkAppHardwareUpgrade(appHardwareVersion);
        AppUpgradeFtp upgradeFtp = (AppUpgradeFtp)result.getData();
        Assert.assertFalse(upgradeFtp.isUpgrade());
    }
    /**
     * updateMessage
     */
    @Test
    public void updateMessageTest() {
        new Expectations() {
            {
                sysParamDao.queryParamById(anyString);
                result = null;
            }
        };
        try {
            systemParameterService.updateMessage(aliAccessKeyParam);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkSystemParameterDataException.class);
        }
        new Expectations(RequestInfoUtils.class) {
            {
                sysParamDao.queryParamById(anyString);
                result = sysParam;
                RequestInfoUtils.getUserId();
                result = "ajkdowkowa";
                sysParamDao.updateParamById((SysParam) any);
                result = 0;
            }
        };
        try {
            systemParameterService.updateMessage(aliAccessKeyParam);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkSystemParameterDatabaseException.class);
        }
        new Expectations(I18nUtils.class, RedisUtils.class) {
            {
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_MESSAGE_UPDATE_SUCCESS);
                result = "修改短信服务设置成功";
                sysParamDao.updateParamById((SysParam) any);
                result = 1;
                languageUtil.getI18nString(SystemParameterI18n.SYSTEM_PARAMETER_MESSAGE);
                result = "短信服务设置";
                RedisUtils.set(ParamTypeRedisEnum.MESSAGE.getKey(), (SysParam) any);
            }
        };
        Result result = systemParameterService.updateMessage(aliAccessKeyParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }
    /**
     * updateMail
     */
    @Test
    public void updateMailTest() {
        new Expectations(RequestInfoUtils.class, I18nUtils.class, RedisUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "ajkdowkowa";
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_MAIL_UPDATE_SUCCESS);
                result = "修改邮箱服务设置成功";
                languageUtil.getI18nString(SystemParameterI18n.SYSTEM_PARAMETER_MAIL);
                result = "邮箱服务设置";
                RedisUtils.set(ParamTypeRedisEnum.MAIL.getKey(), (SysParam) any);
                sysParamDao.updateParamById((SysParam) any);
                result = 1;
                sysParamDao.queryParamById(anyString);
                result = sysParam;
            }
        };
        Result result = systemParameterService.updateMail(aliAccessKeyParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }
    /**
     * updateMessageNotification
     */
    @Test
    public void updateMessageNotificationTest() {
        MessageNotificationParam messageNotificationParam = new MessageNotificationParam();
        MessageNotification messageNotification = new MessageNotification();
        messageNotification.setMessageRemind("1");
        messageNotificationParam.setParamId(SystemParameterI18n.SYSTEM_PARAMETER_MESSAGE_NOTIFICATION);
        messageNotificationParam.setMessageNotification(messageNotification);
        messageNotificationParam.getMessageNotification().setRetentionTime(1);
        messageNotificationParam.getMessageNotification().setSoundRemind("1");
        messageNotificationParam.getMessageNotification().setMessageRemind("0");
        new Expectations(RequestInfoUtils.class, I18nUtils.class, RedisUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "ajkdowkowa";
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_MESSAGE_NOTIFICATION_UPDATE_SUCCESS);
                result = "修改消息通知设置成功";
                languageUtil.getI18nString(SystemParameterI18n.SYSTEM_PARAMETER_MESSAGE_NOTIFICATION);
                result = "消息通知设置";
                RedisUtils.set(ParamTypeRedisEnum.MESSAGE_NOTIFICATION.getKey(), (SysParam) any);
                sysParamDao.updateParamById((SysParam) any);
                result = 1;
                sysParamDao.queryParamById(anyString);
                result = sysParam;
            }
        };
        Result result = systemParameterService.updateMessageNotification(messageNotificationParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }
    /**
     * updateMobilePush
     */
    @Test
    public void updateMobilePushTest() {
        new Expectations(RequestInfoUtils.class, I18nUtils.class, RedisUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "ajkdowkowa";
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_MOBILE_PUSH_UPDATE_SUCCESS);
                result = "修改推送服务设置成功";
                languageUtil.getI18nString(SystemParameterI18n.SYSTEM_PARAMETER_MOBILE_PUSH);
                result = "推送服务设置";
                RedisUtils.set(ParamTypeRedisEnum.MOBILE_PUSH.getKey(), (SysParam) any);
                sysParamDao.updateParamById((SysParam) any);
                result = 1;
                sysParamDao.queryParamById(anyString);
                result = sysParam;
            }
        };
        Result result = systemParameterService.updateMobilePush(aliAccessKeyParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }

    /**
     * updateFtpSettings
     */
    @Test
    public void updateFtpSettingsTest() {
        FtpSettingsParam ftpSettingsParam = new FtpSettingsParam();
        ftpSettingsParam.setParamId("2107210");
        new Expectations(RequestInfoUtils.class, I18nUtils.class, RedisUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "1";
                languageUtil.getI18nString(SystemParameterI18n.SYSTEM_PARAMETER_FTP);
                result = "FTP服务设置";
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_FTP_UPDATE_SUCCESS);
                result = "FTP服务设置修改成功";
                RedisUtils.set(ParamTypeRedisEnum.FTP_SETTINGS.getKey(), (SysParam) any);
                sysParamDao.updateParamById((SysParam) any);
                result = 1;
                sysParamDao.queryParamById(anyString);
                result = sysParam;
            }
        };
        Result result = systemParameterService.updateFtpSettings(ftpSettingsParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }

    /**
     * updateDisplaySettings
     */
    @Test
    public void updateDisplaySettingsTest() throws IOException {
        DisplaySettings displaySettings = new DisplaySettings();
        DisplaySettingsParam displaySettingsParam = new DisplaySettingsParam();
        displaySettingsParam.setParamId("2107209");
        SysParam sysParam = new SysParam();
        sysParam.setParamId(displaySettingsParam.getParamId());
        new Expectations() {
            {
                sysParamDao.queryParamById(displaySettingsParam.getParamId());
                result = null;
            }
        };
        try {
            systemParameterService.updateDisplaySettings(displaySettingsParam, null);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkSystemParameterDataException.class);
        }
        displaySettings.setScreenDisplay("0");
        displaySettings.setScreenScroll("0");
        displaySettings.setSystemLogo("http://10.5.24.142:80/group1/M00/00/00/auihsdiouhwiouahdiouhaoihw.mapper");
        displaySettings.setTimeType(DisplayTimeEnum.LOCAL_TIME.getValue());
        sysParam.setPresentValue("{}");
        sysParam.setDefaultValue(sysParam.getPresentValue());
        sysParam.setParamType(ParamTypeRedisEnum.FTP_SETTINGS.getType());
        displaySettingsParam.setDisplaySettings(displaySettings);
        new Expectations() {
            {
                sysParamDao.queryParamById(displaySettingsParam.getParamId());
                result = sysParam;
            }
        };
        try {
            systemParameterService.updateDisplaySettings(displaySettingsParam, null);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkSystemParameterDataException.class);
        }
        sysParam.setPresentValue(JSON.toJSONString(displaySettings));
        sysParam.setDefaultValue(sysParam.getPresentValue());
        new Expectations(RequestInfoUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "1";

                sysParamDao.updateParamById((SysParam) any);
                result = 0;
            }
        };
        try {
            systemParameterService.updateDisplaySettings(displaySettingsParam, null);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkSystemParameterDatabaseException.class);
        }
        displaySettings.setSystemLogo("local");
        displaySettingsParam.setDisplaySettings(displaySettings);
        new Expectations(I18nUtils.class, RedisUtils.class) {
            {
                sysParamDao.updateParamById((SysParam) any);
                result = 1;

                RedisUtils.set(anyString, any);

                languageUtil.getI18nString(SystemParameterI18n.SYSTEM_PARAMETER_DISPLAY_SETTINGS);
                result = "显示设置";

                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_DISPLAY_UPDATE_SUCCESS);
                result = "显示设置修改成功";
            }
        };
        Result result = systemParameterService.updateDisplaySettings(displaySettingsParam, null);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
        MultipartFile file = new MockMultipartFile(
                "logo.png", //文件名
                "logo.png", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new byte[3145730]//文件流
        );
        try {
            systemParameterService.updateDisplaySettings(displaySettingsParam, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDisplaySettingsLogoSizeException.class);
        }
        file = new MockMultipartFile(
                "logo.mapper", //文件名
                "logo.mapper", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new byte[500]//文件流
        );
        try {
            systemParameterService.updateDisplaySettings(displaySettingsParam, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDisplaySettingsLogoFormatException.class);
        }
        file = new MockMultipartFile(
                "logo.png", //文件名
                "logo.png", //originalName 相当于上传文件在客户机上的文件名
                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
                new byte[500]//文件流
        );
        new Expectations(ImageIO.class) {
            {
                ImageIO.read((InputStream) any);
                result = new IOException();
            }
        };
        try {
            systemParameterService.updateDisplaySettings(displaySettingsParam, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDisplaySettingsLogoFormatException.class);
        }
        BufferedImage sourceImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        new Expectations(ImageIO.class) {
            {
                ImageIO.read((InputStream) any);
                result = sourceImage;
            }
        };
        try {
            systemParameterService.updateDisplaySettings(displaySettingsParam, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDisplaySettingsLogoSizeException.class);
        }
        BufferedImage sourceImage1 = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        new Expectations(ImageIO.class) {
            {
                ImageIO.read((InputStream) any);
                result = sourceImage1;

                fdfsFeign.uploadFile((MultipartFile) any);
                result = null;
            }
        };
        try {
            systemParameterService.updateDisplaySettings(displaySettingsParam, file);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkDisplaySettingsLogoUploadException.class);
        }
        new Expectations() {
            {
                fdfsFeign.uploadFile((MultipartFile) any);
                result = "http://10.5.24.142:80/group1/M00/00/00/jafkljdliwjaildjakj.png";
            }
        };
        result = systemParameterService.updateDisplaySettings(displaySettingsParam, file);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }

    /**
     * sendMessageTest
     */
    @Test
    public void sendMessageTestTest() throws ClientException {
        MessageSendTest messageSendTest = new MessageSendTest();
        messageSendTest.setPhone("16602779659");
        messageSendTest.setAccessKeyId("ncdskjhnfjke");
        messageSendTest.setAccessKeySecret("shajkldhwikhjad");
        SendSmsResponse smsResponse = new SendSmsResponse();
        new Expectations(I18nUtils.class) {
            {
                aliyunSendSms.sendSmsAndEmail((AliyunSms) any);
                result = new ClientException("");

                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_TEST_FAIL);
                result = "测试失败";

                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_SEND_MESSAGE_SUCCESS);
                result = "测试成功";
            }
        };
        Result result = systemParameterService.sendMessageTest(messageSendTest);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_TEST_FAIL);
        new Expectations() {
            {
                aliyunSendSms.sendSmsAndEmail((AliyunSms) any);
                result = smsResponse;
            }
        };
        result = systemParameterService.sendMessageTest(messageSendTest);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_TEST_FAIL);
        smsResponse.setCode("ok");
        result = systemParameterService.sendMessageTest(messageSendTest);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }
    /**
     * sendMailTest
     */
    @Test
    public void sendMailTestTest() throws ClientException {
        MailSendTest mailSendTest = new MailSendTest();
        mailSendTest.setAccessKeyId("ncdskjhnfjke");
        mailSendTest.setAccessKeySecret("shajkldhwikhjad");
        mailSendTest.setToAddress("1352627145@qq.com");
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_TEST_FAIL);
                result = "测试失败";

                I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_SEND_MAIL_SUCCESS);
                result = "测试成功";
            }
        };
        Result result = systemParameterService.sendMailTest(mailSendTest);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
        new Expectations(I18nUtils.class) {
            {
                aliyunSendEmail.sendSmsAndEmail((AliyunEmail) any);
                result = new ClientException("");
            }
        };
        result = systemParameterService.sendMailTest(mailSendTest);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SYSTEM_PARAMETER_TEST_FAIL);
    }
    /**
     * ftpSettingsTest
     */
    @Test
    public void ftpSettingsTestTest() {
        FtpSettingsParam ftpSettingsParam = new FtpSettingsParam();
        FtpSettings ftpSettings = new FtpSettings();
        ftpSettings.setIpAddress("39.98.72.132");
        ftpSettings.setInnerIpAddress("39.98.72.132");
        ftpSettings.setUserName("filink");
        ftpSettings.setPassword("filink");
        ftpSettings.setDisconnectTime(2);
        ftpSettings.setPort(2341);
        ftpSettingsParam.setFtpSettings(ftpSettings);
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(SystemParameterI18n.FTP_TEST_SUCCESS);
                result = "测试成功";
            }
        };
        Result result = systemParameterService.ftpSettingsTest(ftpSettingsParam);
        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
    }
}
