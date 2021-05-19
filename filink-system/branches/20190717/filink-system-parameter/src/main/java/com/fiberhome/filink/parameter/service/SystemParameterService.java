package com.fiberhome.filink.parameter.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.parameter.bean.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *    系统参数服务类
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-03-13
 */
public interface SystemParameterService {
    /**
     * 查询短信服务配置
     * @return 结果
     */
    AliAccessKey queryMessage();
    /**
     * 查询邮箱服务配置
     * @return 结果
     */
    AliAccessKey queryMail();
    /**
     * 查询推送服务配置
     * @return 结果
     */
    AliAccessKey queryMobilePush();
    /**
     * 查询ftp服务配置
     * @return 结果
     */
    FtpSettings queryFtpSettings();
    /**
     * 查询显示设置首页首次加载阈值
     * @return 结果
     */
    Integer queryHomeDeviceLimit();
    /**
     * 检测APP软件是否需要升级
     * @param appSoftwareVersion APP软件版本
     * @return 结果
     */
    Result checkAppSoftwareUpgrade(String appSoftwareVersion);
    /**
     * 检测APP硬件是否需要升级
     * @param appHardwareVersion APP硬件版本
     * @return 结果
     */
    Result checkAppHardwareUpgrade(String appHardwareVersion);
    /**
     * 查询所有系统语言
     * @return 系统语言List
     */
    Result queryLanguageAll();
    /**
     * 修改短信服务配置
     * @param aliAccessKeyParam 阿里云访问密钥修改接收实体类
     * @return 结果
     */
    Result updateMessage(AliAccessKeyParam aliAccessKeyParam);
    /**
     * 修改邮箱服务配置
     * @param aliAccessKeyParam 阿里云访问密钥修改接收实体类
     * @return 结果
     */
    Result updateMail(AliAccessKeyParam aliAccessKeyParam);
    /**
     * 修改消息通知配置
     * @param messageNotificationParam 消息通知配置修改接收实体类
     * @return 结果
     */
    Result updateMessageNotification(MessageNotificationParam messageNotificationParam);
    /**
     * 修改推送服务配置
     * @param aliAccessKeyParam 阿里云移动推送配置修改接收实体类
     * @return 结果
     */
    Result updateMobilePush(AliAccessKeyParam aliAccessKeyParam);
    /**
     * 修改显示设置
     *
     * @param displaySettingsParam 显示设置
     * @param file LOGO
     * @return 结果
     */
    Result updateDisplaySettings(DisplaySettingsParam displaySettingsParam, MultipartFile file);
    /**
     * 修改FTP服务设置
     * @param ftpSettingsParam FTP服务设置修改接收实体类
     * @return 结果
     */
    Result updateFtpSettings(FtpSettingsParam ftpSettingsParam);

    /**
     * 查询初始化设置
     * @return 查询结果
     */
    Result selectDisplaySettingsParamForPageCollection();

    /**
     * 短信发送测试
     * @param messageSendTest 短信配置信息
     * @return 结果
     */
    Result sendMessageTest(MessageSendTest messageSendTest);

    /**
     * 邮箱发送测试
     * @param mailSendTest 邮箱配置信息
     * @return 结果
     */
    Result sendMailTest(MailSendTest mailSendTest);

    /**
     * FTP测试
     *
     * @param ftpSettingsParam FTP参数
     * @return 结果
     */
    Result ftpSettingsTest(FtpSettingsParam ftpSettingsParam);
}
