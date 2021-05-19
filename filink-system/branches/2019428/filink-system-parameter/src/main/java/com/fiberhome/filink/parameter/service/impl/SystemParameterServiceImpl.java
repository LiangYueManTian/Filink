package com.fiberhome.filink.parameter.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.utils.LogConstants;
import com.fiberhome.filink.oss_api.api.FdfsFeign;
import com.fiberhome.filink.parameter.bean.*;
import com.fiberhome.filink.parameter.dao.SysLanguageDao;
import com.fiberhome.filink.parameter.dto.SysParameterDto;
import com.fiberhome.filink.parameter.exception.*;
import com.fiberhome.filink.parameter.service.SystemParameterService;
import com.fiberhome.filink.parameter.utils.ImageExtensionEnum;
import com.fiberhome.filink.parameter.utils.ImageSizeEnum;
import com.fiberhome.filink.parameter.utils.SystemParameterConstants;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.sms_api.api.SendSmsAndEmail;
import com.fiberhome.filink.sms_api.bean.AliyunEmail;
import com.fiberhome.filink.sms_api.bean.AliyunSms;
import com.fiberhome.filink.sms_api.bean.template.AlarmWarning;
import com.fiberhome.filink.system_commons.bean.SysParam;
import com.fiberhome.filink.system_commons.dao.SysParamDao;
import com.fiberhome.filink.system_commons.service.SysParamService;
import com.fiberhome.filink.system_commons.utils.ParamTypeRedisEnum;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *    系统参数服务实现类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-03-13
 */
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
        //校验参数
        checkMessageNotification(messageNotificationParam.getMessageNotification());
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
        //校验参数
        checkDisplaySettings(displaySettingsParam.getDisplaySettings());
        //查询数据库数据
        SysParam sysParam = sysParamDao.queryParamById(displaySettingsParam.getParamId());
        if (ObjectUtils.isEmpty(sysParam) || sysParam.checkValue()) {
            throw new FilinkSystemParameterDataException();
        }
        DisplaySettings displaySettings = JSON.parseObject(sysParam.getPresentValue(), DisplaySettings.class);
        //校验数据库数据是否是有效数据
        if (ObjectUtils.isEmpty(displaySettings) || displaySettings.checkLogo()) {
            throw new FilinkSystemParameterDataException();
        }
        String logoPath = displaySettings.getSystemLogo();
        String logoUrl;
        String logoUrlDb = displaySettings.getSystemLogo();
        if  (file != null) {
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
        SysParameterDto sysParameterDto = new SysParameterDto();
        sysParameterDto.setDisplaySettings(displaySettings);
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
        AlarmWarning alarmWarning = new AlarmWarning();
        alarmWarning.setDevname(aliSendTest.getDevName());
        alarmWarning.setDoorno(aliSendTest.getDoorNo());
        alarmWarning.setAlarmdes(aliSendTest.getAlarmDes());
        message.setAccessKeyId(messageSendTest.getAccessKeyId());
        message.setAccessKeySecret(messageSendTest.getAccessKeySecret());
        message.setPhone(messageSendTest.getPhone());
        message.setSignName(aliSendTest.getSignName());
        message.setTemplateCode(aliSendTest.getTemplateCode());
        message.setTemplateParam(JSON.toJSONString(alarmWarning));
        try {
            SendSmsResponse smsResponse = (SendSmsResponse)aliyunSendSms.sendSmsAndEmail(message);
            if (!SystemParameterConstants.SMS_RESPONSE_OK.equalsIgnoreCase(smsResponse.getCode())) {
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
        aliyunEmail.setFromAlias(aliSendTest.getFromAlias());
        aliyunEmail.setSubject(aliSendTest.getSubject());
        aliyunEmail.setTagName(aliSendTest.getTagName());
        aliyunEmail.setHtmlBody(aliSendTest.getHtmlBody());
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
        if (file.getSize() > ImageSizeEnum.SIZE.getValue()) {
            throw new FilinkDisplaySettingsLogoSizeException();
        }
        if (!ImageExtensionEnum.containExtension(FilenameUtils.getExtension(file.getOriginalFilename()))) {
            throw new FilinkDisplaySettingsLogoFormatException();
        }
        BufferedImage sourceImage;
        try {
            sourceImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new FilinkDisplaySettingsLogoFormatException();
        }
        if(sourceImage.getWidth() != ImageSizeEnum.WIDTH.getValue() && sourceImage.getHeight() != ImageSizeEnum.HEIGHT.getValue()){
            throw new FilinkDisplaySettingsLogoSizeException();
        }
        //上传至oss文件服务器
        String url = fdfsFeign.uploadFile(file);
        //校验是否上传成功
        if(StringUtils.isEmpty(url)) {
            throw new FilinkDisplaySettingsLogoUploadException();
        }
        return url;
    }

    /**
     * 删除Logo路径
     * @param url Logo路径
     */
    private void deleteLogo(String url) {
        //校验防止路径丢失
        if (StringUtils.isEmpty(url)) {
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

    /**
     * 校验消息通知配置参数是否启用，启用则校验对应参数，禁用则置空对应参数
     * @param messageNotification 消息通知配置
     */
    private void checkMessageNotification(MessageNotification messageNotification) {
        //消息提醒是否启用
        if (SystemParameterConstants.ONE_TYPE.equals(messageNotification.getMessageRemind())) {
            //校验消息保留时间
            if (messageNotification.checkRetentionTime() || messageNotification.checkSoundRemind()) {
                throw new FilinkSystemParameterParamException();
            }
        } else {
            //消息置空
            messageNotification.setRetentionTime(null);
            messageNotification.setSoundRemind(SystemParameterConstants.ZERO_TYPE);
        }
        //声音提醒是否启用
        if (SystemParameterConstants.ONE_TYPE.equals(messageNotification.getSoundRemind())) {
            //校验选择提醒音
            if (messageNotification.checkSoundSelected()) {
                throw new FilinkSystemParameterParamException();
            }
        } else {
            //选择提醒音置空
            messageNotification.setSoundSelected(null);
        }
    }

    /**
     * 校验大屏显示参数是否启用，启用则校验对应参数，禁用则置空对应参数
     * @param displaySettings 显示参数
     */
    private void checkDisplaySettings(DisplaySettings displaySettings) {
        //消息提醒是否启用
        if (SystemParameterConstants.ONE_TYPE.equals(displaySettings.getScreenDisplay())) {
            //校验消息保留时间
            if (displaySettings.checkEnableScreen()) {
                throw new FilinkSystemParameterParamException();
            }
        } else {
            //消息置空
            displaySettings.setScreenScroll(SystemParameterConstants.ZERO_TYPE);
        }
        //声音提醒是否启用
        if (SystemParameterConstants.ONE_TYPE.equals(displaySettings.getScreenScroll())) {
            //校验选择提醒音
            if (displaySettings.checkScreenScrollTime()) {
                throw new FilinkSystemParameterParamException();
            }
        } else {
            //选择提醒音置空
            displaySettings.setScreenScrollTime(null);
        }
    }

}
