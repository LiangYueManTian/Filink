package com.fiberhome.filink.parameter.bean;

import com.fiberhome.filink.parameter.dto.SysParameterDto;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class BeanTest {
    /**
     * AliSendTest
     */
    @Test
    public void AliSendTest() {
        AliSendTest aliSendTest = new AliSendTest();
        String signName = "基础设施";
        String templateCode = "SMS_153885498";
        String accountName = "filink@fi-link.net";
        String tagName = "test";
        aliSendTest.setSignName(signName);
        aliSendTest.setTemplateCode(templateCode);
        aliSendTest.setAccountName(accountName);
        aliSendTest.setTagName(tagName);
        Assert.assertEquals(aliSendTest.getSignName(),signName);
        Assert.assertEquals(aliSendTest.getTemplateCode(), templateCode);
        Assert.assertEquals(aliSendTest.getAccountName(), accountName);
        Assert.assertEquals(aliSendTest.getTagName(), tagName);
    }

    /**
     * AppUpgradeFtp
     */
    @Test
    public void AppUpgradeFtp() {
        FtpSettings ftpSettings = new FtpSettings();
        String filePath = "/yy_link/ftp/a.zip";
        AppUpgradeFtp appUpgradeFtp = new AppUpgradeFtp();
        appUpgradeFtp.setFtpSettings(ftpSettings);
        appUpgradeFtp.setFilePath(filePath);
        Assert.assertEquals(appUpgradeFtp.getFtpSettings(), ftpSettings);
        Assert.assertEquals(appUpgradeFtp.getFilePath(), filePath);
    }

    /**
     * DisplaySettings
     */
    @Test
    public void DisplaySettings() {
        DisplaySettings displaySettings = new DisplaySettings();
        Assert.assertTrue(displaySettings.checkDisplay());
        displaySettings.setScreenDisplay("1");
        displaySettings.setScreenScroll("2");
        displaySettings.setSystemLanguage("CN");
        displaySettings.setTimeType("Time");
        Assert.assertTrue(displaySettings.checkDisplay());
        displaySettings.setScreenScroll("1");
        Assert.assertTrue(displaySettings.checkDisplay());
        displaySettings.setScreenScrollTime(20);
        displaySettings.setHomeDeviceLimit(20000);
        Assert.assertTrue(displaySettings.checkDisplay());
    }

    /**
     * FtpSettings
     */
    @Test
    public void FtpSettings() {
        FtpSettings ftpSettings = new FtpSettings();
        ftpSettings.setIpAddress("0.0.0.0.0");
        ftpSettings.setInnerIpAddress("0.0.0.0");
        ftpSettings.setUserName("jkshaddddddjkahwdikhukawhdkuahdwukhuawhhh");
        ftpSettings.setPassword("jkshaddddddjkahwdikhukawhdkuahdwukhuawhhh");
        Assert.assertFalse(ftpSettings.checkValue());
        ftpSettings.setUserName("aaa");
        ftpSettings.setPassword("aaa");
        ftpSettings.setPort(2341);
        ftpSettings.setDisconnectTime(2);
        Assert.assertFalse(ftpSettings.checkValue());
    }

    /**
     * MailSendTest
     */
    @Test
    public void MailSendTest() {
        MailSendTest mailSendTest = new MailSendTest();
        String error = "jih-";
        String mail = "1352627145@qq.com";
        mailSendTest.setAccessKeyId(error);
        mailSendTest.setAccessKeySecret(error);
        mailSendTest.setToAddress(mail);
        Assert.assertTrue(mailSendTest.check());
        Assert.assertEquals(mailSendTest.getAccessKeyId(), error);
        Assert.assertEquals(mailSendTest.getAccessKeySecret(), error);
        Assert.assertEquals(mailSendTest.getToAddress(), mail);
    }

    /**
     * MessageNotification
     */
    @Test
    public void MessageNotification() {
        MessageNotification messageNotification = new MessageNotification();
        messageNotification.setMessageRemind("1");
        messageNotification.setSoundRemind("1");
        Assert.assertTrue(messageNotification.checkMessageRemind());
        messageNotification.setSoundSelected("a.mp3");
        Assert.assertTrue(messageNotification.checkMessageRemind());
    }

    /**
     * MessageSendTest
     */
    @Test
    public void MessageSendTest() {
        MessageSendTest messageSendTest = new MessageSendTest();
        String phone = "16602779659";
        String accessKeyId = "sajdiojwio";
        messageSendTest.setPhone(phone);
        messageSendTest.setAccessKeyId(accessKeyId);
        messageSendTest.setAccessKeySecret(accessKeyId);
        Assert.assertEquals(messageSendTest.getPhone(), phone);
        Assert.assertEquals(messageSendTest.getAccessKeyId(), accessKeyId);
        Assert.assertEquals(messageSendTest.getAccessKeySecret(), accessKeyId);
    }

    /**
     * SysLanguage
     */
    @Test
    public void SysLanguage() {
        SysLanguage sysLanguage = new SysLanguage();
        String languageId = "1111";
        String languageType = "CN";
        String languageName = "简体中文";
        sysLanguage.setLanguageId(languageId);
        sysLanguage.setLanguageName(languageName);
        sysLanguage.setLanguageType(languageType);
        Assert.assertEquals(sysLanguage.getLanguageId(), languageId);
        Assert.assertEquals(sysLanguage.getLanguageName(), languageName);
        Assert.assertEquals(sysLanguage.getLanguageType(), languageType);
    }

    /**
     * SysParameterDto
     */
    @Test
    public void SysParameterDto() {
        SysParameterDto sysParameterDto = new SysParameterDto();
        DisplaySettings displaySettings = new DisplaySettings();
        MessageNotification messageNotification = new MessageNotification();
        sysParameterDto.setDisplaySettings(displaySettings);
        sysParameterDto.setMessageNotification(messageNotification);
        Assert.assertEquals(sysParameterDto.getDisplaySettings(), displaySettings);
        Assert.assertEquals(sysParameterDto.getMessageNotification(), messageNotification);
    }
}
