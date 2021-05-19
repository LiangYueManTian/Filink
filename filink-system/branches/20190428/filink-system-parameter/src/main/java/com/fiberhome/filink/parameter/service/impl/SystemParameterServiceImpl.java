package com.fiberhome.filink.parameter.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.ossapi.api.FdfsFeign;
import com.fiberhome.filink.parameter.bean.*;
import com.fiberhome.filink.parameter.constant.*;
import com.fiberhome.filink.parameter.dao.SysLanguageDao;
import com.fiberhome.filink.parameter.dto.SysParameterDto;
import com.fiberhome.filink.parameter.exception.*;
import com.fiberhome.filink.parameter.service.SystemParameterService;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.smsapi.api.SendSmsAndEmail;
import com.fiberhome.filink.smsapi.bean.AliyunEmail;
import com.fiberhome.filink.smsapi.bean.AliyunSms;
import com.fiberhome.filink.systemcommons.bean.SysParam;
import com.fiberhome.filink.systemcommons.constant.ParamTypeRedisEnum;
import com.fiberhome.filink.systemcommons.dao.SysParamDao;
import com.fiberhome.filink.systemcommons.service.SysParamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *    系统参数服务实现类
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-03-13
 */
@Slf4j
@Service
public class SystemParameterServiceImpl implements SystemParameterService {
    /**
     * 自动注入系统服务统一参数dao
     */
    @Autowired
    private SysParamDao sysParamDao;
    /**
     * 自动注入系统语言dao
     */
    @Autowired
    private SysLanguageDao sysLanguageDao;
    /**
     * 自动注入日志服务
     */
    @Autowired
    private LogProcess logProcess;
    /**
     * 自动注入文件服务远程调用
     */
    @Autowired
    private FdfsFeign fdfsFeign;
    /**
     * 注入服务
     */
    @Autowired
    private SysParamService sysParamService;
    /**
     * 阿里云发送短信注入
     */
    @Autowired
    private SendSmsAndEmail aliyunSendSms;
    /**
     * 阿里云发送短信注入
     */
    @Autowired
    private SendSmsAndEmail aliyunSendEmail;
    /**
     * 阿里云邮箱短信等测试信息实体类注入
     */
    @Autowired
    private AliSendTest aliSendTest;
    /**
     * APP软件升级FTP目录
     */
    @Value(value = "${appUpgrade.SoftwareFtpPath}")
    private String appSoftwareUpgradePath;
    /**
     * APP硬件升级FTP目录
     */
    @Value(value = "${appUpgrade.HardwareFtpPath}")
    private String appHardwareUpgradePath;


    /**
     * 查询短信服务配置
     *
     * @return 结果
     */
    @Override
    public AliAccessKey queryMessage() {
        //获取Redis Key
        SysParam sysParam = sysParamService.queryParamByType(ParamTypeRedisEnum.MESSAGE.getType());
        return JSON.parseObject(sysParam.getPresentValue(), AliAccessKey.class);
    }

    /**
     * 查询邮箱服务配置
     *
     * @return 结果
     */
    @Override
    public AliAccessKey queryMail() {
        //获取Redis Key
        SysParam sysParam = sysParamService.queryParamByType(ParamTypeRedisEnum.MAIL.getType());
        return JSON.parseObject(sysParam.getPresentValue(), AliAccessKey.class);
    }

    /**
     * 查询推送服务配置
     *
     * @return 结果
     */
    @Override
    public AliAccessKey queryMobilePush() {
        //获取Redis Key
        SysParam sysParam = sysParamService.queryParamByType(ParamTypeRedisEnum.MOBILE_PUSH.getType());
        return JSON.parseObject(sysParam.getPresentValue(), AliAccessKey.class);
    }

    /**
     * 查询ftp服务配置
     *
     * @return 结果
     */
    @Override
    public FtpSettings queryFtpSettings() {
        //获取Redis Key
        SysParam sysParam = sysParamService.queryParamByType(ParamTypeRedisEnum.FTP_SETTINGS.getType());
        return JSON.parseObject(sysParam.getPresentValue(), FtpSettings.class);
    }

    /**
     * 检测APP软件是否需要升级
     *
     * @param appSoftwareVersion APP软件版本
     * @return 结果
     */
    @Override
    public Result checkAppSoftwareUpgrade(String appSoftwareVersion) {
        return checkAppUpgrade(appSoftwareVersion, appSoftwareUpgradePath);
    }

    /**
     * 检测APP硬件是否需要升级
     *
     * @param appHardwareVersion APP硬件版本
     * @return 结果
     */
    @Override
    public Result checkAppHardwareUpgrade(String appHardwareVersion) {
        return checkAppUpgrade(appHardwareVersion, appHardwareUpgradePath);
    }

    /**
     * 检测APP软件或硬件是否需要升级
     * @param appVersion APP软件或硬件版本
     * @param ftpPath FTP目录
     * @return 结果
     */
    private Result checkAppUpgrade(String appVersion, String ftpPath) {
        //获取FTP设置
        FtpSettings ftpSettings = queryFtpSettings();
        if (!ftpSettings.checkValue()) {
            //数据异常
            return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_DATA_ERROR,
                    I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_DATA_ERROR));
        }
        String ftpVersion;
        try {
            //查询最新包名称
            ftpVersion = findWareName(ftpSettings, ftpPath);
        } catch (Exception e) {
            //FTP异常
            return ResultUtils.warn(SystemParameterResultCode.FTP_GET_ERROR,
                    I18nUtils.getString(SystemParameterI18n.FTP_GET_ERROR));
        }
        boolean upgrade = StringUtils.isEmpty(ftpVersion) || StringUtils.equals(appVersion, ftpVersion);
        AppUpgradeFtp upgradeFtp = new AppUpgradeFtp();
        upgradeFtp.setUpgrade(!upgrade);
        if (upgrade) {
            //名称一致，或FTP没有升级包不需要升级
            return ResultUtils.success(upgradeFtp);
        }
        int compare = ftpVersion.compareTo(appVersion);
        if (compare > 0) {
            //需要升级
            upgradeFtp.setFtpSettings(ftpSettings);
            upgradeFtp.setFilePath(ftpPath + ftpVersion);
            return ResultUtils.success(upgradeFtp);
        } else {
            //版本大于升级包版本不需要升级
            upgradeFtp.setUpgrade(false);
            return ResultUtils.success(upgradeFtp);
        }
    }

    /**
     * 获取FTP最新升级包名称
     * @param ftpSettings FTP服务器信息
     * @param ftpPath FTP目录
     * @return 最新升级包名称
     */
    private String findWareName(FtpSettings ftpSettings, String ftpPath) {
        try {
            FTPClient ftpClient = loginFTP(ftpSettings, ftpSettings.getInnerIpAddress());
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                ftpClient.disconnect();
                log.error("Ftp settings is error");
                throw new Exception();
            }
            // 中文支持
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.changeWorkingDirectory(ftpPath);
            String[] ftpFileNames = ftpClient.listNames();
            ftpClient.logout();
            ftpClient.disconnect();
            if (ftpFileNames == null || ftpFileNames.length == 0) {
                log.warn("Ftp files is null");
                return null;
            } else {
                Arrays.sort(ftpFileNames);
                return ftpFileNames[ftpFileNames.length -1];
            }
        } catch (Exception e) {
            log.info("ftp is error***");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 查询所有系统语言
     *
     * @return 系统语言List
     */
    @Override
    public Result queryLanguageAll() {
        //查询所有系统语言
        List<SysLanguage> sysLanguages = sysLanguageDao.queryLanguageAll();
        if (ObjectUtils.isEmpty(sysLanguages)) {
            throw new FilinkSystemParameterDataException();
        }
        return ResultUtils.success(sysLanguages);
    }

    /**
     * 修改短信服务配置
     *
     * @param aliAccessKeyParam 阿里云访问密钥修改接收实体类
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateMessage(AliAccessKeyParam aliAccessKeyParam) {
        //转成JSON
        String presentValue = JSON.toJSONString(aliAccessKeyParam.getAliAccessKey());
        //修改数据库
        SysParam sysParam = updateSysParam(aliAccessKeyParam.getParamId(), presentValue);
        //存入缓存
        RedisUtils.set(ParamTypeRedisEnum.MESSAGE.getKey(), sysParam);
        //记录日志
        addLog(aliAccessKeyParam.getParamId(), I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_MESSAGE), SystemParameterConstants.MESSAGE_FUNCTION_CODE);
        return ResultUtils.success(SystemParameterResultCode.SUCCESS,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_MESSAGE_UPDATE_SUCCESS));
    }

    /**
     * 修改邮箱服务配置
     *
     * @param aliAccessKeyParam 阿里云访问密钥修改接收实体类
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateMail(AliAccessKeyParam aliAccessKeyParam) {
        //转成JSON
        String presentValue = JSON.toJSONString(aliAccessKeyParam.getAliAccessKey());
        //修改数据库
        SysParam sysParam = updateSysParam(aliAccessKeyParam.getParamId(), presentValue);
        //存入缓存
        RedisUtils.set(ParamTypeRedisEnum.MAIL.getKey(), sysParam);
        //记录日志
        addLog(aliAccessKeyParam.getParamId(), I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_MAIL), SystemParameterConstants.MAIL_FUNCTION_CODE);
        return ResultUtils.success(SystemParameterResultCode.SUCCESS,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_MAIL_UPDATE_SUCCESS));
    }

    /**
     * 修改消息通知配置
     *
     * @param messageNotificationParam 消息通知配置修改接收实体类
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateMessageNotification(MessageNotificationParam messageNotificationParam) {
        //转成JSON
        String presentValue = JSON.toJSONString(messageNotificationParam.getMessageNotification());
        //修改数据库
        SysParam sysParam = updateSysParam(messageNotificationParam.getParamId(), presentValue);
        //存入缓存
        RedisUtils.set(ParamTypeRedisEnum.MESSAGE_NOTIFICATION.getKey(), sysParam);
        //记录日志
        addLog(messageNotificationParam.getParamId(), I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_MESSAGE_NOTIFICATION), SystemParameterConstants.MESSAGE_NOTIFICATION_FUNCTION_CODE);
        return ResultUtils.success(SystemParameterResultCode.SUCCESS,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_MESSAGE_NOTIFICATION_UPDATE_SUCCESS));
    }

    /**
     * 修改推送服务配置
     *
     * @param aliAccessKeyParam 阿里云移动推送配置修改接收实体类
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateMobilePush(AliAccessKeyParam aliAccessKeyParam) {
        //转成JSON
        String presentValue = JSON.toJSONString(aliAccessKeyParam.getAliAccessKey());
        //修改数据库
        SysParam sysParam = updateSysParam(aliAccessKeyParam.getParamId(), presentValue);
        //存入缓存
        RedisUtils.set(ParamTypeRedisEnum.MOBILE_PUSH.getKey(), sysParam);
        //记录日志
        addLog(aliAccessKeyParam.getParamId(), I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_MOBILE_PUSH), SystemParameterConstants.MOBILE_PUSH_FUNCTION_CODE);
        return ResultUtils.success(SystemParameterResultCode.SUCCESS,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_MOBILE_PUSH_UPDATE_SUCCESS));
    }

    /**
     * 修改显示设置
     *
     * @param displaySettingsParam 显示设置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateDisplaySettings(DisplaySettingsParam displaySettingsParam, MultipartFile file) {
        //查询数据库数据
        SysParam sysParam = sysParamDao.queryParamById(displaySettingsParam.getParamId());
        //获取显示设置当前值
        DisplaySettings displaySettings = parseDisplaySettings(sysParam);
        //系统图标路径
        String logoPath = displaySettings.getSystemLogo();
        String logoUrl;
        String logoUrlDb = displaySettings.getSystemLogo();
        if  (file != null) {
            //校验系统图标大小和后缀名
            checkImageFileSizeAndExtension(file);
            //校验系统图标尺寸
            checkImageFileHeightAndWidth(file);
            //上传文件
            logoPath = uploadSystemLogo(file);
            logoUrl = logoPath;
        } else {
            logoUrl = null;
            logoUrlDb = null;
        }
        displaySettingsParam.getDisplaySettings().setSystemLogo(logoPath);
        //转成JSON
        String presentValue = JSON.toJSONString(displaySettingsParam.getDisplaySettings());
        //封装数据
        sysParam.setPresentValue(presentValue);
        //获取用户ID
        sysParam.setUpdateUser(RequestInfoUtils.getUserId());
        //更新数据库
        Integer result = sysParamDao.updateParamById(sysParam);
        //判断结果
        if (result != 1) {
            deleteLogo(logoUrl);
            throw new FilinkSystemParameterDatabaseException();
        }
        //存入缓存
        RedisUtils.set(ParamTypeRedisEnum.DISPLAY_SETTINGS.getKey(), sysParam);
        //删除原图片
        deleteLogo(logoUrlDb);
        //记录日志
        addLog(displaySettingsParam.getParamId(), I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_DISPLAY_SETTINGS), SystemParameterConstants.DISPLAY_FUNCTION_CODE);
        return ResultUtils.success(SystemParameterResultCode.SUCCESS,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_DISPLAY_UPDATE_SUCCESS));
    }

    /**
     * 获取显示设置当前值
     * @param sysParam 显示设置参数
     * @return 显示设置当前值
     */
    private DisplaySettings parseDisplaySettings(SysParam sysParam) {
        if (ObjectUtils.isEmpty(sysParam) || sysParam.checkValue()) {
            throw new FilinkSystemParameterDataException();
        }
        DisplaySettings displaySettings = JSON.parseObject(sysParam.getPresentValue(), DisplaySettings.class);
        //校验数据库数据是否是有效数据
        if (ObjectUtils.isEmpty(displaySettings) || displaySettings.checkLogo()) {
            throw new FilinkSystemParameterDataException();
        }
        return displaySettings;
    }

    /**
     * 修改FTP服务设置
     *
     * @param ftpSettingsParam FTP服务设置修改接收实体类
     * @return 结果
     */
    @Override
    public Result updateFtpSettings(FtpSettingsParam ftpSettingsParam) {
        //转成JSON
        String presentValue = JSON.toJSONString(ftpSettingsParam.getFtpSettings());
        //修改数据库
        SysParam sysParam = updateSysParam(ftpSettingsParam.getParamId(), presentValue);
        //存入缓存
        RedisUtils.set(ParamTypeRedisEnum.FTP_SETTINGS.getKey(), sysParam);
        //记录日志
        addLog(ftpSettingsParam.getParamId(), I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_FTP), SystemParameterConstants.FTP_FUNCTION_CODE);
        return ResultUtils.success(SystemParameterResultCode.SUCCESS,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_FTP_UPDATE_SUCCESS));
    }

    /**
     * 查询初始化设置
     * @return 查询结果
     */
    @Override
    public Result selectDisplaySettingsParamForPageCollection() {
        SysParam sysParam = sysParamService.queryParamByType(ParamTypeRedisEnum.DISPLAY_SETTINGS.getType());
        DisplaySettings displaySettings = JSON.parseObject(sysParam.getPresentValue(), DisplaySettings.class);
        sysParam = sysParamService.queryParamByType(ParamTypeRedisEnum.MESSAGE_NOTIFICATION.getType());
        MessageNotification messageNotification = JSON.parseObject(sysParam.getPresentValue(), MessageNotification.class);
        SysParameterDto sysParameterDto = new SysParameterDto(displaySettings, messageNotification);
        return ResultUtils.success(sysParameterDto);
    }

    /**
     * 短信发送测试
     * @param messageSendTest 短信配置信息
     * @return 结果
     */
    @Override
    public Result sendMessageTest(MessageSendTest messageSendTest) {
        AliyunSms message = new AliyunSms();
        message.setAccessKeyId(messageSendTest.getAccessKeyId());
        message.setAccessKeySecret(messageSendTest.getAccessKeySecret());
        message.setPhone(messageSendTest.getPhone());
        message.setSignName(aliSendTest.getSignName());
        message.setTemplateCode(aliSendTest.getTemplateCode());
        try {
            SendSmsResponse smsResponse = (SendSmsResponse)aliyunSendSms.sendSmsAndEmail(message);
            if (!SystemParameterConstants.SMS_RESPONSE_OK.equalsIgnoreCase(smsResponse.getCode())) {
                log.info("Code:>>" + smsResponse.getCode() + "Message:>>" + smsResponse.getMessage());
                return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_TEST_FAIL,
                        I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_TEST_FAIL));
            }
        } catch (ClientException e) {
            e.printStackTrace();
            return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_TEST_FAIL,
                    I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_TEST_FAIL));
        }
        return ResultUtils.success(SystemParameterResultCode.SUCCESS,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_SEND_MESSAGE_SUCCESS));
    }

    /**
     * 邮箱发送测试
     *
     * @param mailSendTest 邮箱配置信息
     * @return 结果
     */
    @Override
    public Result sendMailTest(MailSendTest mailSendTest) {
        AliyunEmail aliyunEmail = new AliyunEmail();
        aliyunEmail.setAccessKeyId(mailSendTest.getAccessKeyId());
        aliyunEmail.setAccessKeySecret(mailSendTest.getAccessKeySecret());
        aliyunEmail.setToAddress(mailSendTest.getToAddress());
        aliyunEmail.setAccountName(aliSendTest.getAccountName());
        aliyunEmail.setFromAlias(I18nUtils.getString(SystemParameterI18n.FROM_ALIAS));
        aliyunEmail.setSubject(I18nUtils.getString(SystemParameterI18n.SUBJECT));
        aliyunEmail.setTagName(aliSendTest.getTagName());
        aliyunEmail.setHtmlBody(I18nUtils.getString(SystemParameterI18n.HTML_BODY));
        try {
            aliyunSendEmail.sendSmsAndEmail(aliyunEmail);
        } catch (ClientException e) {
            return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_TEST_FAIL,
                    I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_TEST_FAIL));
        }
        return ResultUtils.success(SystemParameterResultCode.SUCCESS,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_SEND_MAIL_SUCCESS));
    }

    /**
     * FTP测试
     *
     * @param ftpSettingsParam FTP参数
     * @return 结果
     */
    @Override
    public Result ftpSettingsTest(FtpSettingsParam ftpSettingsParam) {
        //获取FTP参数
        FtpSettings ftpSettings = ftpSettingsParam.getFtpSettings();
        boolean  positiveCompletion;
        //APP FTP校验
        try {
            positiveCompletion = checkFtpSettings(ftpSettings, ftpSettings.getIpAddress());
            if (!positiveCompletion) {
                return ResultUtils.warn(SystemParameterResultCode.FTP_LOGIN_ERROR,
                        I18nUtils.getString(SystemParameterI18n.FTP_LOGIN_ERROR));
            }

        } catch (Exception e) {
            log.info("app ftp ip or  port is error***");
            return ResultUtils.warn(SystemParameterResultCode.APP_FTP_HOST_PORT_ERROR,
                    I18nUtils.getString(SystemParameterI18n.APP_FTP_HOST_PORT_ERROR));
        }
        //WEB FTP校验
        try {
            positiveCompletion = checkFtpSettings(ftpSettings, ftpSettings.getInnerIpAddress());
            //外网IP校验与内网IP校验结果
            if (!positiveCompletion) {
                return ResultUtils.warn(SystemParameterResultCode.FTP_LOGIN_ERROR,
                        I18nUtils.getString(SystemParameterI18n.FTP_LOGIN_ERROR));
            }
        } catch (Exception e) {
            log.info("web ftp ip or  port is error***");
            return ResultUtils.warn(SystemParameterResultCode.WEB_FTP_HOST_PORT_ERROR,
                    I18nUtils.getString(SystemParameterI18n.WEB_FTP_HOST_PORT_ERROR));
        }
        return ResultUtils.success(SystemParameterResultCode.SUCCESS,
                I18nUtils.getString(SystemParameterI18n.FTP_TEST_SUCCESS));
    }

    /**
     * FTP测试
     * @param ftpSettings FTP参数
     * @param ipAddress FTP地址
     * @return true 成功 false失败
     * @throws IOException 异常
     */
    private boolean checkFtpSettings(FtpSettings ftpSettings, String ipAddress) throws IOException {
        FTPClient ftpClient = loginFTP(ftpSettings,ipAddress);
        boolean positiveCompletion = FTPReply.isPositiveCompletion(ftpClient.getReplyCode());
        ftpClient.logout();
        ftpClient.disconnect();
        return positiveCompletion;
    }

    /**
     * 登陆FTP服务器
     * @param ftpSettings FTP服务器信息
     * @param ipAddress FTP地址
     * @return FTPClient
     * @throws IOException 异常
     */
    private FTPClient loginFTP(FtpSettings ftpSettings, String ipAddress) throws IOException {
        FTPClient ftpClient = new FTPClient();
        // 连接FTP服务器
        ftpClient.setConnectTimeout(ftpSettings.getDisconnectTime() * 1000);
        ftpClient.connect(ipAddress, ftpSettings.getPort());
        // 登陆FTP服务器
        ftpClient.login(ftpSettings.getUserName(), ftpSettings.getPassword());
        return ftpClient;
    }

    /**
     * 更新数据库
     * @param paramId 参数ID
     * @param presentValue  当前参数值
     * @return 系统参数（当前和默认）
     */
    private SysParam updateSysParam(String paramId, String presentValue) {
        //查询数据库数据
        SysParam sysParam = sysParamDao.queryParamById(paramId);
        //校验是否有数据
        if (ObjectUtils.isEmpty(sysParam) || sysParam.checkValue()) {
            throw new FilinkSystemParameterDataException();
        }
        //封装数据
        sysParam.setPresentValue(presentValue);
        //获取用户ID
        sysParam.setUpdateUser(RequestInfoUtils.getUserId());
        //更新数据库
        Integer result = sysParamDao.updateParamById(sysParam);
        //判断结果
        if (result != 1) {
            throw new FilinkSystemParameterDatabaseException();
        }
        return sysParam;
    }


    /**
     * 上传系统LOGO
     * @param file 系统LOGO
     * @return 路径
     */
    private String uploadSystemLogo(MultipartFile file) {
        //上传至oss文件服务器
        String url = fdfsFeign.uploadFile(file);
        //校验是否上传成功
        if(StringUtils.isEmpty(url)) {
            throw new FilinkDisplaySettingsLogoUploadException();
        }
        return url;
    }

    /**
     * 校验系统图标大小和后缀名
     * @param file 系统图标
     */
    private void checkImageFileSizeAndExtension(MultipartFile file) {
        if (file.getSize() > ImageSizeEnum.SIZE.getValue()) {
            throw new FilinkDisplaySettingsLogoSizeException();
        }
        if (!ImageExtensionEnum.containExtension(FilenameUtils.getExtension(file.getOriginalFilename()))) {
            throw new FilinkDisplaySettingsLogoFormatException();
        }
    }

    /**
     * 校验系统图标尺寸
     * @param file 系统图标
     */
    private void checkImageFileHeightAndWidth(MultipartFile file) {
        BufferedImage sourceImage;
        try {
            sourceImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new FilinkDisplaySettingsLogoFormatException();
        }
        boolean imageSize = sourceImage.getWidth() != ImageSizeEnum.WIDTH.getValue() || sourceImage.getHeight() != ImageSizeEnum.HEIGHT.getValue();
        if(imageSize){
            throw new FilinkDisplaySettingsLogoSizeException();
        }
    }

    /**
     * 删除Logo路径
     * @param url Logo路径
     */
    private void deleteLogo(String url) {
        //校验防止路径丢失
        if (StringUtils.isEmpty(url) || SystemParameterConstants.DEFAULT_SYSTEM_LOGO.equals(url)) {
            return;
        }
        //删除LOGO
        List<String> fileUrls = new ArrayList<>();
        fileUrls.add(url);
        fdfsFeign.deleteFilesPhy(fileUrls);
    }
    /**
     * 新增日志
     * @param paramId 参数ID
     * @param systemParameterName 系统参数名称
     * @param functionCode XML functionCode
     */
    private void addLog(String paramId, String systemParameterName, String functionCode) {
        //获取日志类型
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(SystemParameterConstants.PARAM_ID);
        addLogBean.setDataName(SystemParameterConstants.SYSTEM_PARAMETER_NAME);
        //获得操作对象名称
        addLogBean.setOptObj(systemParameterName);
        addLogBean.setFunctionCode(functionCode);
        //获得操作对象id
        addLogBean.setOptObjId(paramId);
        //操作为新增
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
