package com.fiberhome.filink.userserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.license.api.LicenseFeign;
import com.fiberhome.filink.license.bean.License;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.loginSysInfo.api.LoginSysInfoFeign;
import com.fiberhome.filink.loginSysInfo.bean.LoginSysInfo;
import com.fiberhome.filink.loginSysInfo.bean.LoginSysParam;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.AliAccessKey;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.security.api.SecurityFeign;
import com.fiberhome.filink.security.bean.AccountSecurityStrategy;
import com.fiberhome.filink.security.bean.IpAddress;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.smsapi.api.SendSmsAndEmail;
import com.fiberhome.filink.smsapi.bean.AliyunSms;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userserver.bean.DataPermission;
import com.fiberhome.filink.userserver.bean.Permission;
import com.fiberhome.filink.userserver.bean.SmsCode;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.bean.UserCount;
import com.fiberhome.filink.userserver.bean.UserParameter;
import com.fiberhome.filink.userserver.constant.FunctionCodeConstant;
import com.fiberhome.filink.userserver.constant.UserConstant;
import com.fiberhome.filink.userserver.constant.UserResultCode;
import com.fiberhome.filink.userserver.constant.UserI18n;
import com.fiberhome.filink.userserver.dao.RoleDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.service.UserExtraService;
import com.fiberhome.filink.userserver.service.UserStream;
import com.fiberhome.filink.userserver.userexport.UserListExport;
import com.fiberhome.filink.userserver.utils.CheckEmptyUtils;
import com.fiberhome.filink.userserver.utils.ValidTimeUtils;
import com.fiberhome.filink.userserver.utils.WebSocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPageBean;


/**
 * @author xgong
 */
@Slf4j
@Service
public class UserExtraServiceImpl extends ServiceImpl<UserDao, User> implements UserExtraService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SecurityFeign securityFeign;

    @Autowired
    private UserListExport userListExport;

    @Autowired
    private UserStream userStream;
    @Autowired
    private ParameterFeign parameterFeign;

    @Autowired
    private SendSmsAndEmail aliyunSendSms;

    @Autowired
    private LogProcess logProcess;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    @Autowired
    private RoleDao roleDao;
    @Autowired
    private LoginSysInfoFeign loginSysInfoFeign;

    public static ThreadLocal<LoginSysInfo> LOGIN_SYS_INFO_THREAD_LOCAL = new ThreadLocal<>();


    private static final int TOKEN_SITE = 1;
    /**
     * ?????????????????????????????????
     */
    private static final String SINGLE_LOGIN_TYPE = "1";

    /**
     * ???????????????????????????
     */
    private static final String START_LOCK_STRATY = "1";

    /**
     * ???????????????
     */
    private static final String FORBIDDEN_USER = "0";

    /**
     * ??????????????????
     */
    private static final int SYSTEM_SERVICE_ERROR = 125000;
    /**
     * ip??????????????????
     */
    private static final int IP_NOT_HAVE_PERMISSION = 125010;
    /**
     * ??????????????????
     */
    private static final int USER_SERVICE_ERROR = 125020;
    /**
     * ???????????????????????????????????????
     */
    private static final int MORE_THAN_MAXUSERNUMBER = 125030;

    /**
     * ??????????????????
     */
    private static final int USER_HAS_FORBIDDEN = 125050;

    /**
     * licene????????????
     */
    private static final int LICENE_HAS_EXPIRATION = 125060;

    /**
     * ??????????????????
     */
    private static final int USER_HAS_LOCKED = 125070;

    /**
     * license??????
     */
    private static final int LICENSE_IS_NULL = 125080;

    /**
     * ????????????????????????
     */
    public static final int USER_EXCEED_VALID_TIME = 125090;

    /**
     * ??????app???????????????
     */
    public static final int NO_APP_LOGIN_PERMISSION = 125100;

    /**
     * ?????????????????????????????????
     */
    private static final int MUILT_USER_THAN_MAXUSERNUMBER = 125160;
    /**
     * license?????????
     */
    private static final int LICENSE_NOT_STARTED = 125170;
    /**
     * ?????????
     */
    private static final String SERVER_NAME = "filink-user-server";

    private static final int SEND_MANY = 10;

    private static final int DEVICETYPE_INDEX = 0;

    /**
     * ??????????????????????????????
     */
    private static final int HAS_DATA_NUMBER = 0;

    /**
     * ??????????????????
     */
    private static final String NOT_EXPIRE_MESSAGE = "";

    /**
     * lince????????????????????????
     */
    private static final String TIME_FORMAT = "yyyy-MM-dd";

    /**
     * ??????????????????
     */
    private static final int ONLY_ONE = 1;

    /**
     * ??????????????????
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;

    /**
     * ????????????id??????????????????????????????
     *
     * @param deptIdList ??????id??????
     * @return ????????????
     */
    @Override
    public List<User> queryUserByDeptList(List<String> deptIdList) {

        if (CheckEmptyUtils.collectEmpty(deptIdList)) {
            return userDao.queryUserByDeptList(deptIdList);
        }
        return null;
    }

    /**
     * ??????????????????????????????
     *
     * @param userParameter ????????????
     * @return ??????code???
     */
    @Override
    public Integer validateUserLogin(UserParameter userParameter) {

        String userName = userParameter.getUserName();
        String loginIp = userParameter.getLoginIp();
        String loginSource = userParameter.getLoginSourse();
        IpAddress ipAddress = new IpAddress();
        ipAddress.setIpAddress(loginIp);
        License currentLicense;
        LoginSysParam loginSysParam = new LoginSysParam();
        loginSysParam.setIpAddress(ipAddress);
        log.info("User {} login???Call system service query menu, license, security policy and other information", userName);
        LoginSysInfo loginSysInfo = loginSysInfoFeign.queryLoginSysInfo(loginSysParam);
        LOGIN_SYS_INFO_THREAD_LOCAL.set(loginSysInfo);
        //??????????????????license
        try {
            currentLicense = loginSysInfo.getCurrentLicense();
        } catch (Exception e) {
            e.printStackTrace();
            return LICENSE_IS_NULL;
        }

        //??????license??????????????????
        if (currentLicense != null && StringUtils.isNotEmpty(currentLicense.endTime)) {
            try {
                //license????????????
                long expireTime = new SimpleDateFormat(TIME_FORMAT).parse(currentLicense.endTime).getTime();
                //????????????
                long currentTime = System.currentTimeMillis();
                if (currentTime >= expireTime) {
                    return LICENE_HAS_EXPIRATION;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            //license??????????????????
            return LICENSE_IS_NULL;
        }

        //??????license???????????????
        if (StringUtils.isNotEmpty(currentLicense.beginTime)) {
            try {
                //license????????????
                long beginTime = new SimpleDateFormat(TIME_FORMAT).parse(currentLicense.beginTime).getTime();
                //????????????
                long currentTime = System.currentTimeMillis();
                if (currentTime < beginTime) {
                    return LICENSE_NOT_STARTED;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            //license??????????????????
            return LICENSE_IS_NULL;
        }

        //?????????????????????????????????
        List<String> keyList = queryOnlieUserId();
        int onlineNum = keyList.size();
        //?????????????????????????????????
        int maxLoginNum = Integer.parseInt(currentLicense.maxOnlineNum);


        if (UserConstant.ADMIN_USER_NAME.equals(userName)) {
            return ResultCode.SUCCESS;
        }
        if (onlineNum > maxLoginNum) {
            return MUILT_USER_THAN_MAXUSERNUMBER;
        }
        //?????????????????????????????????
        User user = userDao.queryUserByName(userName);

        //????????????????????????????????????
        if (!UserConstant.PC_WEBSITE.equals(loginSource)) {
            List<Permission> permissionList = user.getRole().getPermissionList();
            Permission appPermission = permissionList.stream().filter(permission ->
                    UserConstant.APP_LOGIN_PERMISSION.equals(permission.getId())).findFirst().orElse(null);
            if (appPermission == null) {
                return NO_APP_LOGIN_PERMISSION;
            }
        }
        //?????????????????????
        Set<String> loginKeys = RedisUtils.
                keys(UserConstant.USER_PREFIX + user.getId() + UserConstant.REDIS_SPLIT + UserConstant.REDIS_WILDCARD);
        Integer maxUsers = user.getMaxUsers();
        if (CheckEmptyUtils.collectEmpty(loginKeys)) {
            //?????????????????????????????????????????????
            if (!UserConstant.SINGLE_LOGIN_TIP.equals(user.getLoginType()) && loginKeys.size() >= maxUsers) {
                return MORE_THAN_MAXUSERNUMBER;
            }
        }


        //??????????????????
        Result result = loginSysInfo.getSecurityStrategyResult();
        if (result == null) {
            return SYSTEM_SERVICE_ERROR;
        }

        //??????ip?????????????????????
        Result ipResult = loginSysInfo.getIpResult();
        if (ipResult == null) {
            return SYSTEM_SERVICE_ERROR;
        }
        int code = ipResult.getCode();
        if (code == -1) {
            return IP_NOT_HAVE_PERMISSION;
        }
        if (user == null) {
            return USER_SERVICE_ERROR;
        }

        //???????????????????????????
        Long unlockTime = user.getUnlockTime();
        long currentTime = System.currentTimeMillis();
        if (null != unlockTime) {
            if (unlockTime > currentTime) {
                return USER_HAS_LOCKED;
            }
        }

        if (FORBIDDEN_USER.equals(user.getUserStatus())) {
            return USER_HAS_FORBIDDEN;
        }

        //????????????????????????
        boolean flag = ValidTimeUtils.validTime(user.getCountValidityTime(), user.getCreateTime());
        if (!flag) {
            return USER_EXCEED_VALID_TIME;
        }

        //??????????????????????????????
        if (SINGLE_LOGIN_TYPE.equals(user.getLoginType())) {
            //???redis???????????????????????????????????????
            Set<String> keys = RedisUtils.
                    keys(UserConstant.USER_PREFIX + user.getId() + UserConstant.REDIS_SPLIT + UserConstant.REDIS_WILDCARD);
            if (keys != null) {
                keys.forEach(key -> {
                    RedisUtils.remove(key);
                    Map<String, String> idToken = new HashMap<>();
                    String realKey = key.substring(UserConstant.USER_PREFIX.length());
                    idToken.put(realKey, user.getId());
                    WebSocketUtils.websocketSendMessage(userStream, UserConstant.BE_OFFLINE,
                            UserConstant.FORCEOFFLINE_CHANNEL_KEY, idToken, SEND_MANY);
                });
            }
        }
        return ResultCode.SUCCESS;
    }

    /**
     * ????????????????????????
     *
     * @param exportDto ???????????????????????????
     * @return ???????????????
     */
    @Override
    public Result exportUserList(ExportDto exportDto) {
        systemLanguageUtil.querySystemLanguage();
        ExportRequestInfo exportRequestInfo;
        try {
            exportRequestInfo = userListExport.insertTask(exportDto, SERVER_NAME, I18nUtils.getSystemString(UserI18n.OPERATE_USER_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(UserResultCode.EXPORT_NO_DATA, I18nUtils.getSystemString(UserI18n.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return getExportToLargeMsg(fe);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(UserResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS,
                    I18nUtils.getSystemString(UserI18n.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(UserResultCode.FAILED_TO_CREATE_EXPORT_TASK,
                    I18nUtils.getSystemString(UserI18n.FAILED_TO_CREATE_EXPORT_TASK));
        }
        userListExport.exportData(exportRequestInfo);

        //????????????????????????
        universalLog(RequestInfoUtils.getUserId(), FunctionCodeConstant.EXPORT_USER_LIST, LogConstants.LOG_TYPE_SECURITY);

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * ???????????????????????????
     *
     * @param id      ??????id
     * @param model   ??????id
     * @param logType ????????????
     */
    private void universalLog(String id, String model, String logType) {
        systemLanguageUtil.querySystemLanguage();
        User user = userDao.selectById(id);
        //??????????????????
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("id");
        //????????????????????????
        addLogBean.setFunctionCode(model);
        //??????????????????id
        addLogBean.setOptObjId(id);
        addLogBean.setOptObj(user.getUserName());
        addLogBean.setOptUserName(user.getUserName());
        //???????????????
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_ADD);

        //??????????????????
        logProcess.addSecurityLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }


    /**
     * ?????????????????????????????????
     *
     * @param userParameter ????????????
     * @return
     */
    @Override
    public void dealLoginFail(UserParameter userParameter) {

        log.info("User login:Entered the method of handling login errors....");
        String userName = userParameter.getUserName();
        log.info("dealLoginFail userParameter {}", userParameter);
        //??????????????????????????????????????????
        Result result = securityFeign.queryAccountSecurity();
        AccountSecurityStrategy straty = JSON.parseObject(JSON.toJSONString(result.getData()), AccountSecurityStrategy.class);

        //???????????????????????????1    (??????id + ip + ??????,List<??????????????????>)  ?????????????????????????????????????????????????????????????????????
        User user = userDao.queryUserByName(userName);
        if (user == null) {
            return;
        }

        //????????????ip?????????
        String loginIp = userParameter.getLoginIp();
        String key = loginIp + UserConstant.REDIS_IP_PORT_SPLIT + user.getId();
        boolean hasKey = RedisUtils.hasKey(key);
        Integer illegalLoginCount = straty.getIllegalLoginCount();
        if (hasKey) {

            List<Long> timeList = (List) RedisUtils.get(key);

            List<Long> lastFailTimeList = new ArrayList<>();
            timeList.add(System.currentTimeMillis());
            if (timeList.size() < illegalLoginCount) {
                RedisUtils.set(key, timeList);
            } else {
                lastFailTimeList = timeList.subList(timeList.size() - illegalLoginCount, timeList.size());
                List<Long> lastFailTime = new ArrayList<>();
                lastFailTime.addAll(lastFailTimeList);
                Long firstNumber = lastFailTime.get(UserConstant.FIRST_NUMBER_INDEX);
                Long intervalTime = System.currentTimeMillis() - straty.getIntervalTime() * 60 * 1000;

                //????????????????????????????????????
                if (firstNumber >= intervalTime) {
                    //??????????????????????????????????????????
                    if (START_LOCK_STRATY.equals(straty.getLockStrategy())) {
                        Integer lockedTime = straty.getLockedTime() * 60 * 1000;
                        Long unLockTime = System.currentTimeMillis() + lockedTime;
                        userDao.updateStatusAndUnlockTime(user.getId(), unLockTime, FORBIDDEN_USER);
                    }
                    RedisUtils.remove(key);
                } else {
                    //??????????????????
                    lastFailTime.add(System.currentTimeMillis());
                    lastFailTime.remove(UserConstant.FIRST_NUMBER_INDEX);
                    RedisUtils.set(key, lastFailTime);
                }
            }
        } else {
            //?????????????????????????????????????????????????????????????????????
            if (illegalLoginCount <= ONLY_ONE) {

                //??????????????????????????????????????????
                if (START_LOCK_STRATY.equals(straty.getLockStrategy())) {

                    Integer lockedTime = straty.getLockedTime() * 60 * 1000;
                    Long unLockTime = System.currentTimeMillis() + lockedTime;
                    userDao.updateStatusAndUnlockTime(user.getId(), unLockTime, FORBIDDEN_USER);
                }
            } else {
                //?????????????????????????????????????????????????????????
                List<Long> timeList = new ArrayList<>();
                timeList.add(System.currentTimeMillis());
                RedisUtils.set(key, timeList);
            }
        }
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param dataPermission ??????????????????
     * @return ????????????????????????
     */
    @Override
    public List<String> queryDeviceTypeByPermission(DataPermission dataPermission) {

        List<String> userIds = userDao.queryUserByDeptAndDeviceType(dataPermission);
        if (!userIds.contains(UserConstant.ADMIN_ID)) {
            userIds.add(UserConstant.ADMIN_ID);
        }
        List<String> onlineUserToken = new ArrayList<>();
        userIds.forEach(userId -> {
            Set<String> keys = RedisUtils.
                    keys(UserConstant.USER_PREFIX + userId + UserConstant.REDIS_SPLIT + UserConstant.REDIS_WILDCARD);
            if (CheckEmptyUtils.collectEmpty(keys)) {
                keys.forEach(tokens -> {
                    String token = tokens.split(UserConstant.REDIS_SPLIT)[TOKEN_SITE];
                    onlineUserToken.add(token);
                });
            }
        });

        return onlineUserToken;
    }

    /**
     * ????????????
     *
     * @param phoneNumber ?????????
     */
    @Override
    public Result sendMessage(String phoneNumber) throws ClientException {

        //???????????????????????????????????????
        User user = userDao.queryUserByPhone(phoneNumber);
        if (user == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.PHONE_NOT_EXIST));
        }

        //??????

        AliyunSms aliyunSms = new AliyunSms();
        AliAccessKey aliAccessKey = parameterFeign.queryMessage();
        aliyunSms.setAccessKeyId(aliAccessKey.getAccessKeyId());
        aliyunSms.setAccessKeySecret(aliAccessKey.getAccessKeySecret());

        int sendNumber = (int) ((Math.random() * 9 + 1) * 100000);
        String sendMessage = Integer.toString(sendNumber);

        aliyunSms.setPhone(phoneNumber);
        SmsCode smsCode = new SmsCode(sendMessage);

        aliyunSms.setTemplateParam(JSON.toJSONString(smsCode));
        aliyunSms.setSignName(UserConstant.BASIC_FACILITIES);
        aliyunSms.setTemplateCode(UserConstant.LOGIN_MESSAGE_MODEL);
        log.info("Mobile phone {} login???Send the verification code", phoneNumber);
        AcsResponse acsResponse = aliyunSendSms.sendSmsAndEmail(aliyunSms);
        if (acsResponse instanceof SendSmsResponse) {
            SendSmsResponse sendSmsResponse = (SendSmsResponse) acsResponse;
            log.info(sendSmsResponse.getCode());
            if (!"OK".equals(sendSmsResponse.getCode())) {
                return ResultUtils.success(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.FAILED_SEND_SMS));
            }
        }
        //??????????????????????????????redis???
        RedisUtils.set(phoneNumber, sendMessage, UserConstant.MESSAGE_EXPIRE_TIME);
        return ResultUtils.success(sendMessage);
    }

    /**
     * ??????????????????????????????
     *
     * @param dataPermissionList ????????????
     * @return
     */
    @Override
    public Map<String, List<String>> queryUserByDevice(List<DataPermission> dataPermissionList) {

        Map<String, List<String>> userMap = new HashMap<>();
        //????????????????????????????????????????????????????????????
        List<User> userList = userDao.queryAllUserDetailInfo();
        userList.forEach(user -> {
            List<String> deviceList = new ArrayList<>();
            String deptId = user.getDeptId();
            List<String> deviceTypeList = new ArrayList<>();
            user.getRole().getRoleDevicetypeList().forEach(roleDeviceType -> {
                deviceTypeList.add(roleDeviceType.getDeviceTypeId());
            });
            dataPermissionList.forEach(dataPermission -> {
                if (UserConstant.ADMIN_ID.equals(user.getId())) {
                    deviceList.add(dataPermission.getDeviceId());
                } else {
                    List<String> deptList = dataPermission.getDeptList();
                    List<String> deviceTypes = dataPermission.getDeviceTypes();
                    //???????????????????????????????????????????????????
                    int index = deptList.indexOf(deptId);
                    int deviceIndex = deviceTypeList.indexOf(deviceTypes.get(DEVICETYPE_INDEX));
                    if (index >= HAS_DATA_NUMBER && deviceIndex >= HAS_DATA_NUMBER) {
                        deviceList.add(dataPermission.getDeviceId());
                    }
                }
            });

            checkUserByDevice(deviceList, user, userMap);
        });

        return userMap;
    }

    /**
     * @param phoneNumber ?????????
     * @return ?????????
     */
    @Override
    public String getSmsMessage(String phoneNumber) {

        if (RedisUtils.hasKey(phoneNumber)) {
            return (String) RedisUtils.get(phoneNumber);
        }

        return NOT_EXPIRE_MESSAGE;
    }

    /**
     * ?????????????????????????????????
     *
     * @param phoneNumber
     * @return
     */
    @Override
    public User queryUserByPhone(String phoneNumber) {

        return userDao.queryUserByPhone(phoneNumber);
    }

    /**
     * ????????????id??????token??????
     *
     * @param userId ??????id
     * @return token??????
     */
    @Override
    public List<String> queryTokenByUserId(String userId) {

        List<String> tokenList = new ArrayList<>();
        Set<String> userKeys = RedisUtils.
                keys(UserConstant.USER_PREFIX + userId + UserConstant.REDIS_SPLIT + UserConstant.REDIS_WILDCARD);
        if (CheckEmptyUtils.collectEmpty(userKeys)) {
            userKeys.forEach(keyToken -> {
                String token = keyToken.substring(UserConstant.USER_PREFIX.length() +
                        userId.length() + UserConstant.REDIS_SPLIT.length(), keyToken.length());
                tokenList.add(token);
            });
        }

        return tokenList;
    }

    /**
     * ????????????id???????????????????????????id??????
     *
     * @param idList ??????id??????
     * @return ??????id??????
     */
    @Override
    public List<String> queryPhoneIdByUserIds(List<String> idList) {

        List<String> deviceIdList = new ArrayList<>();
        if (!CheckEmptyUtils.collectEmpty(idList)) {
            return deviceIdList;
        }

        idList.forEach(id -> {
            Set<String> keys = RedisUtils.keys(UserConstant.USER_PREFIX + id + UserConstant.REDIS_SPLIT);
            if (CheckEmptyUtils.collectEmpty(keys)) {
                keys.forEach(key -> {
                    User loginUser = (User) RedisUtils.get(key);
                    if (loginUser.getPushId() != null) {
                        deviceIdList.add(loginUser.getPushId());
                    }
                });
            }
        });

        return deviceIdList;
    }

    /**
     * ???????????????????????????id??????
     *
     * @param userName ?????????
     * @return ??????id??????
     */
    @Override
    public List<String> queryUserIdByName(String userName) {

        //??????????????????
        if (StringUtils.isNotEmpty(userName)) {
            userName = userName.replace(UserConstant.BACK_SLASH_TWO, UserConstant.BACK_SLASH_FOUR)
                    .replace(UserConstant.PER_CENT, UserConstant.BACK_SLASH_PER_CENT)
                    .replace(UserConstant.UNDER_LINE, UserConstant.BACK_SLASH_UNDER_LINE)
                    .replace(UserConstant.FORWORD_SLASH, UserConstant.BACK_SLASH_FORWARD_SLASH);
        }
        return userDao.queryUserIdByName(userName);
    }

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    @Override
    public UserCount queryUserNumber() {

        int userAccountNumber;
        int onlineUserNumber = 0;
        List<User> userList = userDao.queryAllUser();
        userAccountNumber = userList.size();
        Set<String> tokenKey = RedisUtils.keys(UserConstant.USER_PREFIX + UserConstant.REDIS_WILDCARD);
        if (CheckEmptyUtils.collectEmpty(tokenKey)) {

            onlineUserNumber = tokenKey.size();

            //???????????????????????????????????????key??????????????????1
            if (RedisUtils.hasKey(UserI18n.USER_EXPIRE_TIME)) {
                onlineUserNumber--;
            }
        }

        return new UserCount(userAccountNumber, onlineUserNumber);
    }

    /**
     * ????????????id??????????????????????????????
     *
     * @param idList ??????id??????
     * @return ??????????????????
     */
    @Override
    public List<User> queryOnlineUserByIdList(List<String> idList) {

        List<User> userList = new ArrayList<>();
        idList.forEach(id -> {
            Set<String> keys = RedisUtils.keys(UserConstant.USER_PREFIX + id + UserConstant.REDIS_SPLIT + UserConstant.REDIS_WILDCARD);
            if (CheckEmptyUtils.collectEmpty(keys)) {
                keys.forEach(key -> {
                    Object userObject = RedisUtils.get(key);
                    if (userObject instanceof User) {
                        String token = key.substring(key.indexOf(UserConstant.REDIS_SPLIT), key.length());
                        User user = (User) userObject;
                        user.setToken(token);
                        userList.add(user);
                    }
                });
            }
        });

        return userList;
    }

    /**
     * ?????????????????????????????????id???appkey
     *
     * @param user ?????????????????????
     * @return ???????????????
     */
    @Override
    public Result modifyUserPhoneIdAndAppKey(User user) {

        String userId = RequestInfoUtils.getUserId();
        String token = RequestInfoUtils.getToken();
        if (RedisUtils.hasKey(UserConstant.USER_PREFIX + userId + UserConstant.REDIS_SPLIT + token)) {

            Object userObject = RedisUtils.get(UserConstant.USER_PREFIX + userId + UserConstant.REDIS_SPLIT + token);
            if (userObject != null) {

                if (userObject instanceof User) {
                    long expire = RedisUtils.getExpire(UserConstant.USER_PREFIX + userId + UserConstant.REDIS_SPLIT + token);
                    User redisUser = (User) userObject;
                    redisUser.setPushId(user.getPushId());
                    redisUser.setAppKey(user.getAppKey());
                    redisUser.setPhoneType(user.getPhoneType());
                    RedisUtils.set(UserConstant.USER_PREFIX + userId + UserConstant.REDIS_SPLIT + token, redisUser, expire);
                    return ResultUtils.warn(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.MODIFY_USER_LOGIN_DEVICE_SUCCESS));
                }
            }
        }

        return ResultUtils.warn(UserResultCode.USER_NOT_EXIST, I18nUtils.getSystemString(UserI18n.USER_NOT_EXIST));
    }

    /**
     * ???????????????????????????
     *
     * @return ????????????
     */
    @Override
    public Result queryAllUserInfo() {

        List<User> userList = userDao.queryAllUser();
        return ResultUtils.success(userList);
    }

    /**
     * ??????????????????????????????
     *
     * @param queryCondition ????????????
     * @return ????????????
     */
    @Override
    public Result queryUserByPermission(QueryCondition<UserParameter> queryCondition) {

        //?????????????????????????????????
        UserParameter userParameter = queryCondition.getBizCondition();
        if (userParameter == null) {
            userParameter = new UserParameter();
        }

        Page page = myBatiesBuildPage(queryCondition);

        userParameter.setStartNum((queryCondition.getPageCondition().getPageNum() - 1)
                * queryCondition.getPageCondition().getPageSize());
        userParameter.setPage(queryCondition.getPageCondition().getPageNum());
        userParameter.setPageSize(queryCondition.getPageCondition().getPageSize());

        List<String> roleIdList = roleDao.queryRoleByPermission(userParameter.getPermissionId());
        userParameter.setRoleIdList(roleIdList);
        List<User> userList = userDao.queryUserByPermission(userParameter);
        Long userNumber = userDao.queryUserNumberByPermission(userParameter);

        PageBean pageBean = myBatiesBuildPageBean(page, userNumber.intValue(), userList);
        return ResultUtils.success(pageBean);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param dataPermission ???????????????????????????
     * @return ????????????
     */
    @Override
    public Result queryUserInfoByDeptAndDeviceType(DataPermission dataPermission) {

        List<User> users = userDao.queryUserInfoByDeptAndDeviceType(dataPermission);
        return ResultUtils.success(users);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param deviceList ????????????
     * @param user       ??????
     * @param userMap    ????????????????????????????????????
     */
    private void checkUserByDevice(List<String> deviceList, User user, Map<String, List<String>> userMap) {

        //????????????????????????????????????????????????????????????
        if (CheckEmptyUtils.collectEmpty(deviceList)) {
            Set<String> keySet = RedisUtils.keys(UserConstant.USER_PREFIX + user.getId() + UserConstant.REDIS_SPLIT + UserConstant.REDIS_WILDCARD);
            if (CheckEmptyUtils.collectEmpty(keySet)) {
                keySet.forEach(key -> {
                    String[] tokens = key.split(UserConstant.REDIS_SPLIT);
                    userMap.put(tokens[TOKEN_SITE], deviceList);
                });
            }
        }
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param fe ??????
     * @return ????????????
     */
    private Result getExportToLargeMsg(FilinkExportDataTooLargeException fe) {
        fe.printStackTrace();
        String string = I18nUtils.getSystemString(UserI18n.EXPORT_DATA_TOO_LARGE);
        String dataCount = fe.getMessage();
        Object[] params = {dataCount, maxExportDataSize};
        String msg = MessageFormat.format(string, params);
        return ResultUtils.warn(UserResultCode.EXPORT_DATA_TOO_LARGE, msg);
    }

    /**
     * ?????????????????????key
     *
     * @return
     */
    public List<String> queryOnlieUserId() {

        List<String> keyList = new ArrayList<>();
        Set<String> keyToken = RedisUtils.keys(UserConstant.USER_PREFIX + UserConstant.REDIS_WILDCARD);
        if (CheckEmptyUtils.collectEmpty(keyToken)) {
            return new ArrayList<>(keyToken);
        }

        return keyList;
    }
}
