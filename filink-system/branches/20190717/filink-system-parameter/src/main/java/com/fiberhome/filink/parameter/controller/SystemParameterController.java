package com.fiberhome.filink.parameter.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.parameter.bean.*;
import com.fiberhome.filink.parameter.constant.SystemParameterI18n;
import com.fiberhome.filink.parameter.constant.SystemParameterResultCode;
import com.fiberhome.filink.parameter.service.SystemParameterService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *    系统参数前端控制器
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-13
 */
@RestController
@RequestMapping("/systemParameter")
public class SystemParameterController {

    /**
     * 自动注入service
     */
    @Autowired
    private SystemParameterService systemParameterService;

    /**
     * 查询短信服务配置
     * @return 结果
     */
    @GetMapping("/queryMessage")
    public AliAccessKey queryMessage() {
        return systemParameterService.queryMessage();
    }
    /**
     * 查询邮箱服务配置
     * @return 结果
     */
    @GetMapping("/queryMail")
    public AliAccessKey queryMail() {
        return systemParameterService.queryMail();
    }
    /**
     * 查询推送服务配置
     * @return 结果
     */
    @GetMapping("/queryMobilePush")
    public AliAccessKey queryMobilePush() {
        return systemParameterService.queryMobilePush();
    }
    /**
     * 查询ftp服务配置
     * @return 结果
     */
    @GetMapping("/queryFtpSettings")
    public FtpSettings queryFtpSettings() {
        return systemParameterService.queryFtpSettings();
    }
    /**
     * 查询显示设置首页首次加载阈值
     * @return 结果
     */
    @GetMapping("/queryHomeDeviceLimit")
    public Integer queryHomeDeviceLimit() {
        return systemParameterService.queryHomeDeviceLimit();
    }
    /**
     * 检测APP软件是否需要升级
     * @param appUpgrade APP软件版本
     * @return 结果
     */
    @PostMapping("/checkAppSoftwareUpgrade")
    public Result checkAppSoftwareUpgrade(@RequestBody AppUpgrade appUpgrade) {
        //参数校验
        if (appUpgrade == null || StringUtils.isEmpty(appUpgrade.getAppSoftwareVersion())) {
            return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR,
                    I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR));
        }
        return systemParameterService.checkAppSoftwareUpgrade(appUpgrade.getAppSoftwareVersion());
    }
    /**
     * 检测APP硬件是否需要升级
     * @param appUpgrade APP硬件版本
     * @return 结果
     */
    @PostMapping("/checkAppHardwareUpgrade")
    public Result checkAppHardwareUpgrade(@RequestBody AppUpgrade appUpgrade) {
        //参数校验
        if (appUpgrade == null || StringUtils.isEmpty(appUpgrade.getAppHardwareVersion())) {
            return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR,
                    I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR));
        }
        return systemParameterService.checkAppHardwareUpgrade(appUpgrade.getAppHardwareVersion());
    }
    /**
     * 查询所有系统语言
     * @return 系统语言List
     */
    @GetMapping("/queryLanguageAll")
    public Result queryLanguageAll() {
        return systemParameterService.queryLanguageAll();
    }

    /**
     * 修改短信服务配置
     * @param aliAccessKeyParam 阿里云访问密钥修改接收实体类
     * @return 结果
     */
    @PostMapping("/updateMessage")
    public Result updateMessage(@RequestBody AliAccessKeyParam aliAccessKeyParam) {
        //参数校验
        if (ObjectUtils.isEmpty(aliAccessKeyParam) || aliAccessKeyParam.check()) {
            return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR,
                    I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR));
        }
        return systemParameterService.updateMessage(aliAccessKeyParam);
    }
    /**
     * 修改邮箱服务配置
     * @param aliAccessKeyParam 阿里云访问密钥修改接收实体类
     * @return 结果
     */
    @PostMapping("/updateMail")
    public Result updateMail(@RequestBody AliAccessKeyParam aliAccessKeyParam) {
        //参数校验
        if (ObjectUtils.isEmpty(aliAccessKeyParam) || aliAccessKeyParam.check()) {
            return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR,
                    I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR));
        }
        return systemParameterService.updateMail(aliAccessKeyParam);
    }
    /**
     * 修改消息通知配置
     * @param messageNotificationParam 消息通知配置修改接收实体类
     * @return 结果
     */
    @PostMapping("/updateMessageNotification")
    public Result updateMessageNotification(@RequestBody MessageNotificationParam messageNotificationParam) {
        //参数校验
        if (ObjectUtils.isEmpty(messageNotificationParam) || messageNotificationParam.check()) {
            return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR,
                    I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR));
        }
        return systemParameterService.updateMessageNotification(messageNotificationParam);
    }
    /**
     * 修改推送服务配置
     * @param aliAccessKeyParam 阿里云移动推送配置修改接收实体类
     * @return 结果
     */
    @PostMapping("/updateMobilePush")
    public Result updateMobilePush(@RequestBody AliAccessKeyParam aliAccessKeyParam) {
        //参数校验
        if (ObjectUtils.isEmpty(aliAccessKeyParam) || aliAccessKeyParam.check()) {
            return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR,
                    I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR));
        }
        return systemParameterService.updateMobilePush(aliAccessKeyParam);
    }

    /**
     * FTP服务设置
     * @param ftpSettingsParam FTP服务设置修改接收实体类
     * @return 结果
     */
    @PostMapping("/updateFtpSettings")
    public Result updateFtpSettings(@RequestBody FtpSettingsParam ftpSettingsParam) {
        //参数校验
        if (ObjectUtils.isEmpty(ftpSettingsParam) || ftpSettingsParam.check()) {
            return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR,
                    I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR));
        }
        return systemParameterService.updateFtpSettings(ftpSettingsParam);
    }

    /**
     * 修改显示设置
     *
     * @param displaySettingsParam 显示设置
     * @return 结果
     */
    @PostMapping("/updateDisplaySettings")
    public Result updateDisplaySettings(DisplaySettings displaySettings, DisplaySettingsParam displaySettingsParam, @RequestBody MultipartFile file) {
        //参数校验
        if (ObjectUtils.isEmpty(displaySettingsParam) || ObjectUtils.isEmpty(displaySettings)
                || displaySettingsParam.check() || displaySettings.checkDisplay()) {
            return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR,
                    I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR));
        }
        displaySettingsParam.setDisplaySettings(displaySettings);
        return systemParameterService.updateDisplaySettings(displaySettingsParam, file);
    }

    /**
     * 查询初始化设置
     * @return 查询结果
     */
    @GetMapping("/selectDisplaySettingsParamForPageCollection")
    public Result selectDisplaySettingsParamForPageCollection(){
          return systemParameterService.selectDisplaySettingsParamForPageCollection();
    }

    /**
     * 短信发送测试
     * @param messageSendTest 短信配置信息
     * @return 结果
     */
    @PostMapping("/sendMessageTest")
    public Result sendMessageTest(@RequestBody MessageSendTest messageSendTest) {
        //参数校验
        if (ObjectUtils.isEmpty(messageSendTest) || messageSendTest.check()) {
            return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR,
                    I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR));
        }
        return systemParameterService.sendMessageTest(messageSendTest);
    }

    /**
     * 邮箱发送测试
     * @param mailSendTest 邮箱配置信息
     * @return 结果
     */
    @PostMapping("/sendMailTest")
    public Result sendMailTest(@RequestBody MailSendTest mailSendTest) {
        //参数校验
        if (ObjectUtils.isEmpty(mailSendTest) || mailSendTest.check()) {
            return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR,
                    I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR));
        }
        return systemParameterService.sendMailTest(mailSendTest);
    }

    /**
     * FTP测试
     *
     * @param ftpSettingsParam FTP参数
     * @return 结果
     */
    @PostMapping("/ftpSettingsTest")
    public Result ftpSettingsTest(@RequestBody FtpSettingsParam ftpSettingsParam) {
        //参数校验
        if (ObjectUtils.isEmpty(ftpSettingsParam) || ftpSettingsParam.check()) {
            return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_PARAM_ERROR,
                    I18nUtils.getSystemString(SystemParameterI18n.SYSTEM_PARAMETER_PARAM_ERROR));
        }
        return systemParameterService.ftpSettingsTest(ftpSettingsParam);
    }
}
