//package com.fiberhome.filink.parameter.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.fiberhome.filink.bean.RequestInfoUtils;
//import com.fiberhome.filink.bean.Result;
//import com.fiberhome.filink.logapi.log.LogProcess;
//import com.fiberhome.filink.ossapi.api.FdfsFeign;
//import com.fiberhome.filink.parameter.bean.*;
//import com.fiberhome.filink.parameter.constant.SystemParameterI18n;
//import com.fiberhome.filink.parameter.constant.SystemParameterResultCode;
//import com.fiberhome.filink.parameter.exception.*;
//import com.fiberhome.filink.parameter.constant.DisplayTimeEnum;
//import com.fiberhome.filink.parameter.constant.SystemParameterConstants;
//import com.fiberhome.filink.redis.RedisUtils;
//import com.fiberhome.filink.server_common.utils.I18nUtils;
//import com.fiberhome.filink.systemcommons.bean.SysParam;
//import com.fiberhome.filink.systemcommons.dao.SysParamDao;
//import com.fiberhome.filink.systemcommons.constant.ParamTypeRedisEnum;
//import mockit.Expectations;
//import mockit.Injectable;
//import mockit.Tested;
//import mockit.integration.junit4.JMockit;
//import org.apache.http.entity.ContentType;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.io.InputStream;
//
//@RunWith(JMockit.class)
//public class SystemParameterServiceImplTest {
//    /**测试对象 SystemParameterServiceImpl*/
//    @Tested
//    private SystemParameterServiceImpl systemParameterService;
//    /**Mock SysParamDao*/
//    @Injectable
//    private SysParamDao sysParamDao;
//    /**Mock FdfsFeign*/
//    @Injectable
//    private FdfsFeign fdfsFeign;
//    /**Mock LogProcess*/
//    @Injectable
//    private LogProcess logProcess;
//    /**测试数据 AliAccessKeyParam*/
//    private AliAccessKeyParam aliAccessKeyParam;
//    /**测试数据 SysParam*/
//    private SysParam sysParam;
//    /**
//     * 初始化数据
//     */
//    @Before
//    public void setUp() {
//        SystemParameterI18n systemParameterI18n = new SystemParameterI18n();
//        SystemParameterConstants systemParameterConstants = new SystemParameterConstants();
//        aliAccessKeyParam = new AliAccessKeyParam();
//        aliAccessKeyParam.setParamId("1");
//        AliAccessKey aliAccessKey = new AliAccessKey();
//        aliAccessKey.setAccessKeyId(systemParameterI18n.toString());
//        aliAccessKey.setAccessKeySecret(systemParameterConstants.toString());
//        aliAccessKeyParam.setAliAccessKey(aliAccessKey);
//        String presentValue = JSON.toJSONString(aliAccessKeyParam.getAliAccessKey());
//        sysParam = new SysParam();
//        sysParam.setParamId("1");
//        sysParam.setParamType("1");
//        sysParam.setPresentValue(presentValue);
//        FtpSettings ftpSettings = new FtpSettings();
//        ftpSettings.setIpAddress(aliAccessKey.getAccessKeyId());
//        ftpSettings.setPassword(ftpSettings.getIpAddress());
//        ftpSettings.setUserName(ftpSettings.getPassword());
//        ftpSettings.setIpAddress(ftpSettings.getUserName());
//        ftpSettings.setPort(1);
//        ftpSettings.setDisconnectTime(ftpSettings.getPort());
//        ftpSettings.setPort(ftpSettings.getDisconnectTime());
//        presentValue = JSON.toJSONString(ftpSettings);
//        sysParam.setDefaultValue(presentValue);
//    }
//
//    /**
//     * updateMessage
//     */
//    @Test
//    public void updateMessageTest() {
//        new Expectations() {
//            {
//                sysParamDao.queryParamById(anyString);
//                result = null;
//            }
//        };
//        try {
//            systemParameterService.updateMessage(aliAccessKeyParam);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FilinkSystemParameterDataException.class);
//        }
//        new Expectations(RequestInfoUtils.class) {
//            {
//                RequestInfoUtils.getUserId();
//                result = "ajkdowkowa";
//                sysParamDao.queryParamById(anyString);
//                result = sysParam;
//            }
//            {
//                sysParamDao.updateParamById((SysParam) any);
//                result = 0;
//            }
//        };
//        try {
//            systemParameterService.updateMessage(aliAccessKeyParam);
//        } catch (Exception e) {
//            Assert.assertTrue(e.getClass() == FilinkSystemParameterDatabaseException.class);
//        }
//        new Expectations(I18nUtils.class, RedisUtils.class) {
//            {
//                I18nUtils.getString(anyString);
//                result = "短信服务设置";
//                RedisUtils.set(ParamTypeRedisEnum.MESSAGE.getKey(), (SysParam) any);
//                result = true;
//                sysParamDao.updateParamById((SysParam) any);
//                result = 1;
//            }
//        };
//        Result result = systemParameterService.updateMessage(aliAccessKeyParam);
//        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
//    }
//    /**
//     * updateMail
//     */
//    @Test
//    public void updateMailTest() {
//        new Expectations(RequestInfoUtils.class, I18nUtils.class, RedisUtils.class) {
//            {
//                RequestInfoUtils.getUserId();
//                result = "ajkdowkowa";
//                I18nUtils.getString(anyString);
//                result = "邮箱服务设置";
//                RedisUtils.set(ParamTypeRedisEnum.MAIL.getKey(), (SysParam) any);
//                result = true;
//                sysParamDao.updateParamById((SysParam) any);
//                result = 1;
//                sysParamDao.queryParamById(anyString);
//                result = sysParam;
//            }
//        };
//        Result result = systemParameterService.updateMail(aliAccessKeyParam);
//        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
//    }
//    /**
//     * updateMessageNotification
//     */
//    @Test
//    public void updateMessageNotificationTest() {
//        MessageNotificationParam messageNotificationParam = new MessageNotificationParam();
//        MessageNotification messageNotification = new MessageNotification();
//        messageNotification.setMessageRemind("1");
//        messageNotificationParam.setParamId(SystemParameterI18n.SYSTEM_PARAMETER_MESSAGE_NOTIFICATION);
//        messageNotificationParam.setMessageNotification(messageNotification);
//        try {
//            systemParameterService.updateMessageNotification(messageNotificationParam);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkSystemParameterParamException.class);
//        }
//        messageNotificationParam.getMessageNotification().setRetentionTime(1);
//        messageNotificationParam.getMessageNotification().setSoundRemind("1");
//        try {
//            systemParameterService.updateMessageNotification(messageNotificationParam);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkSystemParameterParamException.class);
//        }
//        messageNotificationParam.getMessageNotification().setMessageRemind("0");
//        new Expectations(RequestInfoUtils.class, I18nUtils.class, RedisUtils.class) {
//            {
//                RequestInfoUtils.getUserId();
//                result = "ajkdowkowa";
//                I18nUtils.getString(anyString);
//                result = "消息通知设置";
//                RedisUtils.set(ParamTypeRedisEnum.MESSAGE_NOTIFICATION.getKey(), (SysParam) any);
//                result = true;
//                sysParamDao.updateParamById((SysParam) any);
//                result = 1;
//                sysParamDao.queryParamById(anyString);
//                result = sysParam;
//            }
//        };
//        Result result = systemParameterService.updateMessageNotification(messageNotificationParam);
//        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
//    }
//    /**
//     * updateMobilePush
//     */
//    @Test
//    public void updateMobilePushTest() {
//        new Expectations(RequestInfoUtils.class, I18nUtils.class, RedisUtils.class) {
//            {
//                RequestInfoUtils.getUserId();
//                result = "ajkdowkowa";
//                I18nUtils.getString(anyString);
//                result = "推送服务设置";
//                RedisUtils.set(ParamTypeRedisEnum.MOBILE_PUSH.getKey(), (SysParam) any);
//                result = true;
//                sysParamDao.updateParamById((SysParam) any);
//                result = 1;
//                sysParamDao.queryParamById(anyString);
//                result = sysParam;
//            }
//        };
//        Result result = systemParameterService.updateMobilePush(aliAccessKeyParam);
//        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
//    }
//
//    /**
//     * updateFtpSettings
//     */
//    @Test
//    public void updateFtpSettingsTest() {
//        FtpSettingsParam ftpSettingsParam = new FtpSettingsParam();
//        ftpSettingsParam.setParamId("2107210");
//        new Expectations(RequestInfoUtils.class, I18nUtils.class, RedisUtils.class) {
//            {
//                RequestInfoUtils.getUserId();
//                result = "1";
//                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_FTP);
//                result = "FTP服务设置";
//                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_FTP_UPDATE_SUCCESS);
//                result = "FTP服务设置修改成功";
//                RedisUtils.set(ParamTypeRedisEnum.FTP_SETTINGS.getKey(), (SysParam) any);
//                result = true;
//                sysParamDao.updateParamById((SysParam) any);
//                result = 1;
//                sysParamDao.queryParamById(anyString);
//                result = sysParam;
//            }
//        };
//        Result result = systemParameterService.updateFtpSettings(ftpSettingsParam);
//        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
//    }
//
//    /**
//     * updateDisplaySettings
//     */
//    @Test
//    public void updateDisplaySettingsTest() throws IOException {
//        DisplaySettings displaySettings = new DisplaySettings();
//        DisplaySettingsParam displaySettingsParam = new DisplaySettingsParam();
//        displaySettingsParam.setParamId("2107209");
//        displaySettings.setScreenDisplay("1");
//        displaySettingsParam.setDisplaySettings(displaySettings);
//        try {
//            systemParameterService.updateDisplaySettings(displaySettingsParam, null);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkSystemParameterParamException.class);
//        }
//        displaySettingsParam.getDisplaySettings().setScreenScroll(displaySettings.getScreenDisplay());
//        try {
//            systemParameterService.updateDisplaySettings(displaySettingsParam, null);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkSystemParameterParamException.class);
//        }
//        SysParam sysParam = new SysParam();
//        sysParam.setParamId(displaySettingsParam.getParamId());
//        displaySettings.setScreenDisplay("0");
//        displaySettings.setScreenScroll("0");
//        displaySettings.setSystemLogo("http://10.5.24.142:80/group1/M00/00/00/auihsdiouhwiouahdiouhaoihw.mapper");
//        displaySettings.setTimeType(DisplayTimeEnum.LOCAL_TIME.getValue());
//        sysParam.setPresentValue("{}");
//        sysParam.setDefaultValue(sysParam.getPresentValue());
//        sysParam.setParamType(ParamTypeRedisEnum.FTP_SETTINGS.getType());
//        new Expectations() {
//            {
//                sysParamDao.queryParamById(sysParam.getParamId());
//                result = null;
//            }
//        };
//        try {
//            systemParameterService.updateDisplaySettings(displaySettingsParam, null);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkSystemParameterDataException.class);
//        }
//        new Expectations() {
//            {
//                sysParamDao.queryParamById(sysParam.getParamId());
//                result = sysParam;
//            }
//        };
//        try {
//            systemParameterService.updateDisplaySettings(displaySettingsParam, null);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkSystemParameterDataException.class);
//        }
//        sysParam.setPresentValue(JSON.toJSONString(displaySettings));
//        sysParam.setDefaultValue(sysParam.getPresentValue());
//        new Expectations(RequestInfoUtils.class) {
//            {
//                sysParamDao.queryParamById(sysParam.getParamId());
//                result = sysParam;
//
//                RequestInfoUtils.getUserId();
//                result = "1";
//
//                sysParamDao.updateParamById((SysParam) any);
//                result = 0;
//            }
//        };
//        try {
//            systemParameterService.updateDisplaySettings(displaySettingsParam, null);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkSystemParameterDatabaseException.class);
//        }
//        new Expectations(I18nUtils.class, RedisUtils.class) {
//            {
//                sysParamDao.updateParamById((SysParam) any);
//                result = 1;
//
//                RedisUtils.set(anyString, any);
//                result = true;
//
//                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_DISPLAY_SETTINGS);
//                result = "显示设置";
//
//                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_DISPLAY_UPDATE_SUCCESS);
//                result = "显示设置修改成功";
//            }
//        };
//        Result result = systemParameterService.updateDisplaySettings(displaySettingsParam, null);
//        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
//        byte[] bytes = new byte[3145730];
//        MultipartFile file = new MockMultipartFile(
//                "logo.png", //文件名
//                "logo.png", //originalName 相当于上传文件在客户机上的文件名
//                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
//                bytes//文件流
//        );
//        try {
//            systemParameterService.updateDisplaySettings(displaySettingsParam, file);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkDisplaySettingsLogoSizeException.class);
//        }
//        bytes = new byte[400];
//        file = new MockMultipartFile(
//                "logo.mapper", //文件名
//                "logo.mapper", //originalName 相当于上传文件在客户机上的文件名
//                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
//                bytes//文件流
//        );
//        try {
//            systemParameterService.updateDisplaySettings(displaySettingsParam, file);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkDisplaySettingsLogoFormatException.class);
//        }
//        file = new MockMultipartFile(
//                "logo.png", //文件名
//                "logo.png", //originalName 相当于上传文件在客户机上的文件名
//                ContentType.APPLICATION_OCTET_STREAM.toString(), //文件类型
//                bytes//文件流
//        );
//        new Expectations(ImageIO.class) {
//            {
//                ImageIO.read((InputStream) any);
//                result = new IOException();
//            }
//        };
//        try {
//            systemParameterService.updateDisplaySettings(displaySettingsParam, file);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkDisplaySettingsLogoFormatException.class);
//        }
//        BufferedImage sourceImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
//        new Expectations(ImageIO.class) {
//            {
//                ImageIO.read((InputStream) any);
//                result = sourceImage;
//            }
//        };
//        try {
//            systemParameterService.updateDisplaySettings(displaySettingsParam, file);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkDisplaySettingsLogoSizeException.class);
//        }
//        BufferedImage sourceImage1 = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
//        new Expectations(ImageIO.class) {
//            {
//                ImageIO.read((InputStream) any);
//                result = sourceImage1;
//
//                fdfsFeign.uploadFile((MultipartFile) any);
//                result = null;
//            }
//        };
//        try {
//            systemParameterService.updateDisplaySettings(displaySettingsParam, file);
//        } catch (Exception e) {
//            Assert.assertSame(e.getClass(), FilinkDisplaySettingsLogoUploadException.class);
//        }
//        new Expectations() {
//            {
//                fdfsFeign.uploadFile((MultipartFile) any);
//                result = "http://10.5.24.142:80/group1/M00/00/00/jafkljdliwjaildjakj.png";
//            }
//        };
//        result = systemParameterService.updateDisplaySettings(displaySettingsParam, file);
//        Assert.assertEquals(result.getCode(), (int) SystemParameterResultCode.SUCCESS);
//    }
//}
