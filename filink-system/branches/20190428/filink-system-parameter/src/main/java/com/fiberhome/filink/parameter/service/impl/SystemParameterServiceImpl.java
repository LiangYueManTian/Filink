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
 *    ???????????????????????????
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-03-13
 */
@Slf4j
@Service
public class SystemParameterServiceImpl implements SystemParameterService {
    /**
     * ????????????????????????????????????dao
     */
    @Autowired
    private SysParamDao sysParamDao;
    /**
     * ????????????????????????dao
     */
    @Autowired
    private SysLanguageDao sysLanguageDao;
    /**
     * ????????????????????????
     */
    @Autowired
    private LogProcess logProcess;
    /**
     * ????????????????????????????????????
     */
    @Autowired
    private FdfsFeign fdfsFeign;
    /**
     * ????????????
     */
    @Autowired
    private SysParamService sysParamService;
    /**
     * ???????????????????????????
     */
    @Autowired
    private SendSmsAndEmail aliyunSendSms;
    /**
     * ???????????????????????????
     */
    @Autowired
    private SendSmsAndEmail aliyunSendEmail;
    /**
     * ???????????????????????????????????????????????????
     */
    @Autowired
    private AliSendTest aliSendTest;
    /**
     * APP????????????FTP??????
     */
    @Value(value = "${appUpgrade.SoftwareFtpPath}")
    private String appSoftwareUpgradePath;
    /**
     * APP????????????FTP??????
     */
    @Value(value = "${appUpgrade.HardwareFtpPath}")
    private String appHardwareUpgradePath;


    /**
     * ????????????????????????
     *
     * @return ??????
     */
    @Override
    public AliAccessKey queryMessage() {
        //??????Redis Key
        SysParam sysParam = sysParamService.queryParamByType(ParamTypeRedisEnum.MESSAGE.getType());
        return JSON.parseObject(sysParam.getPresentValue(), AliAccessKey.class);
    }

    /**
     * ????????????????????????
     *
     * @return ??????
     */
    @Override
    public AliAccessKey queryMail() {
        //??????Redis Key
        SysParam sysParam = sysParamService.queryParamByType(ParamTypeRedisEnum.MAIL.getType());
        return JSON.parseObject(sysParam.getPresentValue(), AliAccessKey.class);
    }

    /**
     * ????????????????????????
     *
     * @return ??????
     */
    @Override
    public AliAccessKey queryMobilePush() {
        //??????Redis Key
        SysParam sysParam = sysParamService.queryParamByType(ParamTypeRedisEnum.MOBILE_PUSH.getType());
        return JSON.parseObject(sysParam.getPresentValue(), AliAccessKey.class);
    }

    /**
     * ??????ftp????????????
     *
     * @return ??????
     */
    @Override
    public FtpSettings queryFtpSettings() {
        //??????Redis Key
        SysParam sysParam = sysParamService.queryParamByType(ParamTypeRedisEnum.FTP_SETTINGS.getType());
        return JSON.parseObject(sysParam.getPresentValue(), FtpSettings.class);
    }

    /**
     * ??????APP????????????????????????
     *
     * @param appSoftwareVersion APP????????????
     * @return ??????
     */
    @Override
    public Result checkAppSoftwareUpgrade(String appSoftwareVersion) {
        return checkAppUpgrade(appSoftwareVersion, appSoftwareUpgradePath);
    }

    /**
     * ??????APP????????????????????????
     *
     * @param appHardwareVersion APP????????????
     * @return ??????
     */
    @Override
    public Result checkAppHardwareUpgrade(String appHardwareVersion) {
        return checkAppUpgrade(appHardwareVersion, appHardwareUpgradePath);
    }

    /**
     * ??????APP?????????????????????????????????
     * @param appVersion APP?????????????????????
     * @param ftpPath FTP??????
     * @return ??????
     */
    private Result checkAppUpgrade(String appVersion, String ftpPath) {
        //??????FTP??????
        FtpSettings ftpSettings = queryFtpSettings();
        if (!ftpSettings.checkValue()) {
            //????????????
            return ResultUtils.warn(SystemParameterResultCode.SYSTEM_PARAMETER_DATA_ERROR,
                    I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_DATA_ERROR));
        }
        String ftpVersion;
        try {
            //?????????????????????
            ftpVersion = findWareName(ftpSettings, ftpPath);
        } catch (Exception e) {
            //FTP??????
            return ResultUtils.warn(SystemParameterResultCode.FTP_GET_ERROR,
                    I18nUtils.getString(SystemParameterI18n.FTP_GET_ERROR));
        }
        boolean upgrade = StringUtils.isEmpty(ftpVersion) || StringUtils.equals(appVersion, ftpVersion);
        AppUpgradeFtp upgradeFtp = new AppUpgradeFtp();
        upgradeFtp.setUpgrade(!upgrade);
        if (upgrade) {
            //??????????????????FTP??????????????????????????????
            return ResultUtils.success(upgradeFtp);
        }
        int compare = ftpVersion.compareTo(appVersion);
        if (compare > 0) {
            //????????????
            upgradeFtp.setFtpSettings(ftpSettings);
            upgradeFtp.setFilePath(ftpPath + ftpVersion);
            return ResultUtils.success(upgradeFtp);
        } else {
            //??????????????????????????????????????????
            upgradeFtp.setUpgrade(false);
            return ResultUtils.success(upgradeFtp);
        }
    }

    /**
     * ??????FTP?????????????????????
     * @param ftpSettings FTP???????????????
     * @param ftpPath FTP??????
     * @return ?????????????????????
     */
    private String findWareName(FtpSettings ftpSettings, String ftpPath) {
        try {
            FTPClient ftpClient = loginFTP(ftpSettings, ftpSettings.getInnerIpAddress());
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                ftpClient.disconnect();
                log.error("Ftp settings is error");
                throw new Exception();
            }
            // ????????????
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
     * ????????????????????????
     *
     * @return ????????????List
     */
    @Override
    public Result queryLanguageAll() {
        //????????????????????????
        List<SysLanguage> sysLanguages = sysLanguageDao.queryLanguageAll();
        if (ObjectUtils.isEmpty(sysLanguages)) {
            throw new FilinkSystemParameterDataException();
        }
        return ResultUtils.success(sysLanguages);
    }

    /**
     * ????????????????????????
     *
     * @param aliAccessKeyParam ??????????????????????????????????????????
     * @return ??????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateMessage(AliAccessKeyParam aliAccessKeyParam) {
        //??????JSON
        String presentValue = JSON.toJSONString(aliAccessKeyParam.getAliAccessKey());
        //???????????????
        SysParam sysParam = updateSysParam(aliAccessKeyParam.getParamId(), presentValue);
        //????????????
        RedisUtils.set(ParamTypeRedisEnum.MESSAGE.getKey(), sysParam);
        //????????????
        addLog(aliAccessKeyParam.getParamId(), I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_MESSAGE), SystemParameterConstants.MESSAGE_FUNCTION_CODE);
        return ResultUtils.success(SystemParameterResultCode.SUCCESS,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_MESSAGE_UPDATE_SUCCESS));
    }

    /**
     * ????????????????????????
     *
     * @param aliAccessKeyParam ??????????????????????????????????????????
     * @return ??????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateMail(AliAccessKeyParam aliAccessKeyParam) {
        //??????JSON
        String presentValue = JSON.toJSONString(aliAccessKeyParam.getAliAccessKey());
        //???????????????
        SysParam sysParam = updateSysParam(aliAccessKeyParam.getParamId(), presentValue);
        //????????????
        RedisUtils.set(ParamTypeRedisEnum.MAIL.getKey(), sysParam);
        //????????????
        addLog(aliAccessKeyParam.getParamId(), I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_MAIL), SystemParameterConstants.MAIL_FUNCTION_CODE);
        return ResultUtils.success(SystemParameterResultCode.SUCCESS,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_MAIL_UPDATE_SUCCESS));
    }

    /**
     * ????????????????????????
     *
     * @param messageNotificationParam ???????????????????????????????????????
     * @return ??????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateMessageNotification(MessageNotificationParam messageNotificationParam) {
        //??????JSON
        String presentValue = JSON.toJSONString(messageNotificationParam.getMessageNotification());
        //???????????????
        SysParam sysParam = updateSysParam(messageNotificationParam.getParamId(), presentValue);
        //????????????
        RedisUtils.set(ParamTypeRedisEnum.MESSAGE_NOTIFICATION.getKey(), sysParam);
        //????????????
        addLog(messageNotificationParam.getParamId(), I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_MESSAGE_NOTIFICATION), SystemParameterConstants.MESSAGE_NOTIFICATION_FUNCTION_CODE);
        return ResultUtils.success(SystemParameterResultCode.SUCCESS,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_MESSAGE_NOTIFICATION_UPDATE_SUCCESS));
    }

    /**
     * ????????????????????????
     *
     * @param aliAccessKeyParam ????????????????????????????????????????????????
     * @return ??????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateMobilePush(AliAccessKeyParam aliAccessKeyParam) {
        //??????JSON
        String presentValue = JSON.toJSONString(aliAccessKeyParam.getAliAccessKey());
        //???????????????
        SysParam sysParam = updateSysParam(aliAccessKeyParam.getParamId(), presentValue);
        //????????????
        RedisUtils.set(ParamTypeRedisEnum.MOBILE_PUSH.getKey(), sysParam);
        //????????????
        addLog(aliAccessKeyParam.getParamId(), I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_MOBILE_PUSH), SystemParameterConstants.MOBILE_PUSH_FUNCTION_CODE);
        return ResultUtils.success(SystemParameterResultCode.SUCCESS,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_MOBILE_PUSH_UPDATE_SUCCESS));
    }

    /**
     * ??????????????????
     *
     * @param displaySettingsParam ????????????
     * @return ??????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateDisplaySettings(DisplaySettingsParam displaySettingsParam, MultipartFile file) {
        //?????????????????????
        SysParam sysParam = sysParamDao.queryParamById(displaySettingsParam.getParamId());
        //???????????????????????????
        DisplaySettings displaySettings = parseDisplaySettings(sysParam);
        //??????????????????
        String logoPath = displaySettings.getSystemLogo();
        String logoUrl;
        String logoUrlDb = displaySettings.getSystemLogo();
        if  (file != null) {
            //????????????????????????????????????
            checkImageFileSizeAndExtension(file);
            //????????????????????????
            checkImageFileHeightAndWidth(file);
            //????????????
            logoPath = uploadSystemLogo(file);
            logoUrl = logoPath;
        } else {
            logoUrl = null;
            logoUrlDb = null;
        }
        displaySettingsParam.getDisplaySettings().setSystemLogo(logoPath);
        //??????JSON
        String presentValue = JSON.toJSONString(displaySettingsParam.getDisplaySettings());
        //????????????
        sysParam.setPresentValue(presentValue);
        //????????????ID
        sysParam.setUpdateUser(RequestInfoUtils.getUserId());
        //???????????????
        Integer result = sysParamDao.updateParamById(sysParam);
        //????????????
        if (result != 1) {
            deleteLogo(logoUrl);
            throw new FilinkSystemParameterDatabaseException();
        }
        //????????????
        RedisUtils.set(ParamTypeRedisEnum.DISPLAY_SETTINGS.getKey(), sysParam);
        //???????????????
        deleteLogo(logoUrlDb);
        //????????????
        addLog(displaySettingsParam.getParamId(), I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_DISPLAY_SETTINGS), SystemParameterConstants.DISPLAY_FUNCTION_CODE);
        return ResultUtils.success(SystemParameterResultCode.SUCCESS,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_DISPLAY_UPDATE_SUCCESS));
    }

    /**
     * ???????????????????????????
     * @param sysParam ??????????????????
     * @return ?????????????????????
     */
    private DisplaySettings parseDisplaySettings(SysParam sysParam) {
        if (ObjectUtils.isEmpty(sysParam) || sysParam.checkValue()) {
            throw new FilinkSystemParameterDataException();
        }
        DisplaySettings displaySettings = JSON.parseObject(sysParam.getPresentValue(), DisplaySettings.class);
        //??????????????????????????????????????????
        if (ObjectUtils.isEmpty(displaySettings) || displaySettings.checkLogo()) {
            throw new FilinkSystemParameterDataException();
        }
        return displaySettings;
    }

    /**
     * ??????FTP????????????
     *
     * @param ftpSettingsParam FTP?????????????????????????????????
     * @return ??????
     */
    @Override
    public Result updateFtpSettings(FtpSettingsParam ftpSettingsParam) {
        //??????JSON
        String presentValue = JSON.toJSONString(ftpSettingsParam.getFtpSettings());
        //???????????????
        SysParam sysParam = updateSysParam(ftpSettingsParam.getParamId(), presentValue);
        //????????????
        RedisUtils.set(ParamTypeRedisEnum.FTP_SETTINGS.getKey(), sysParam);
        //????????????
        addLog(ftpSettingsParam.getParamId(), I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_FTP), SystemParameterConstants.FTP_FUNCTION_CODE);
        return ResultUtils.success(SystemParameterResultCode.SUCCESS,
                I18nUtils.getString(SystemParameterI18n.SYSTEM_PARAMETER_FTP_UPDATE_SUCCESS));
    }

    /**
     * ?????????????????????
     * @return ????????????
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
     * ??????????????????
     * @param messageSendTest ??????????????????
     * @return ??????
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
     * ??????????????????
     *
     * @param mailSendTest ??????????????????
     * @return ??????
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
     * FTP??????
     *
     * @param ftpSettingsParam FTP??????
     * @return ??????
     */
    @Override
    public Result ftpSettingsTest(FtpSettingsParam ftpSettingsParam) {
        //??????FTP??????
        FtpSettings ftpSettings = ftpSettingsParam.getFtpSettings();
        boolean  positiveCompletion;
        //APP FTP??????
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
        //WEB FTP??????
        try {
            positiveCompletion = checkFtpSettings(ftpSettings, ftpSettings.getInnerIpAddress());
            //??????IP???????????????IP????????????
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
     * FTP??????
     * @param ftpSettings FTP??????
     * @param ipAddress FTP??????
     * @return true ?????? false??????
     * @throws IOException ??????
     */
    private boolean checkFtpSettings(FtpSettings ftpSettings, String ipAddress) throws IOException {
        FTPClient ftpClient = loginFTP(ftpSettings,ipAddress);
        boolean positiveCompletion = FTPReply.isPositiveCompletion(ftpClient.getReplyCode());
        ftpClient.logout();
        ftpClient.disconnect();
        return positiveCompletion;
    }

    /**
     * ??????FTP?????????
     * @param ftpSettings FTP???????????????
     * @param ipAddress FTP??????
     * @return FTPClient
     * @throws IOException ??????
     */
    private FTPClient loginFTP(FtpSettings ftpSettings, String ipAddress) throws IOException {
        FTPClient ftpClient = new FTPClient();
        // ??????FTP?????????
        ftpClient.setConnectTimeout(ftpSettings.getDisconnectTime() * 1000);
        ftpClient.connect(ipAddress, ftpSettings.getPort());
        // ??????FTP?????????
        ftpClient.login(ftpSettings.getUserName(), ftpSettings.getPassword());
        return ftpClient;
    }

    /**
     * ???????????????
     * @param paramId ??????ID
     * @param presentValue  ???????????????
     * @return ?????????????????????????????????
     */
    private SysParam updateSysParam(String paramId, String presentValue) {
        //?????????????????????
        SysParam sysParam = sysParamDao.queryParamById(paramId);
        //?????????????????????
        if (ObjectUtils.isEmpty(sysParam) || sysParam.checkValue()) {
            throw new FilinkSystemParameterDataException();
        }
        //????????????
        sysParam.setPresentValue(presentValue);
        //????????????ID
        sysParam.setUpdateUser(RequestInfoUtils.getUserId());
        //???????????????
        Integer result = sysParamDao.updateParamById(sysParam);
        //????????????
        if (result != 1) {
            throw new FilinkSystemParameterDatabaseException();
        }
        return sysParam;
    }


    /**
     * ????????????LOGO
     * @param file ??????LOGO
     * @return ??????
     */
    private String uploadSystemLogo(MultipartFile file) {
        //?????????oss???????????????
        String url = fdfsFeign.uploadFile(file);
        //????????????????????????
        if(StringUtils.isEmpty(url)) {
            throw new FilinkDisplaySettingsLogoUploadException();
        }
        return url;
    }

    /**
     * ????????????????????????????????????
     * @param file ????????????
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
     * ????????????????????????
     * @param file ????????????
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
     * ??????Logo??????
     * @param url Logo??????
     */
    private void deleteLogo(String url) {
        //????????????????????????
        if (StringUtils.isEmpty(url) || SystemParameterConstants.DEFAULT_SYSTEM_LOGO.equals(url)) {
            return;
        }
        //??????LOGO
        List<String> fileUrls = new ArrayList<>();
        fileUrls.add(url);
        fdfsFeign.deleteFilesPhy(fileUrls);
    }
    /**
     * ????????????
     * @param paramId ??????ID
     * @param systemParameterName ??????????????????
     * @param functionCode XML functionCode
     */
    private void addLog(String paramId, String systemParameterName, String functionCode) {
        //??????????????????
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(SystemParameterConstants.PARAM_ID);
        addLogBean.setDataName(SystemParameterConstants.SYSTEM_PARAMETER_NAME);
        //????????????????????????
        addLogBean.setOptObj(systemParameterName);
        addLogBean.setFunctionCode(functionCode);
        //??????????????????id
        addLogBean.setOptObjId(paramId);
        //???????????????
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
        //??????????????????
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
