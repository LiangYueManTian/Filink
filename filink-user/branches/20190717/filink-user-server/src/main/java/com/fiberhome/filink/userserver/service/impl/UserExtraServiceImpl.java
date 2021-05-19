package com.fiberhome.filink.userserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.FiLinkTimeUtils;
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
import com.fiberhome.filink.userserver.consts.UserConst;
import com.fiberhome.filink.userserver.consts.UserI18n;
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
    private LicenseFeign licenseFeign;

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

    private static final int TOKEN_SITE = 1;
    /**
     * 用户登陆用到的常量信息
     */
    private static final String SINGLE_LOGIN_TYPE = "1";

    /**
     * 开启锁定策略的标志
     */
    private static final String START_LOCK_STRATY = "1";

    /**
     * 用户被禁用
     */
    private static final String FORBIDDEN_USER = "0";

    /**
     * 系统服务异常
     */
    private static final int SYSTEM_SERVICE_ERROR = 125000;
    /**
     * ip没有访问权限
     */
    private static final int IP_NOT_HAVE_PERMISSION = 125010;
    /**
     * 用户服务异常
     */
    private static final int USER_SERVICE_ERROR = 125020;
    /**
     * 超过单个用户最大登录用户数
     */
    private static final int MORE_THAN_MAXUSERNUMBER = 125030;

    /**
     * 用户已被禁用
     */
    private static final int USER_HAS_FORBIDDEN = 125050;

    /**
     * licene已经过期
     */
    private static final int LICENE_HAS_EXPIRATION = 125060;

    /**
     * 用户已被锁定
     */
    private static final int USER_HAS_LOCKED = 125070;

    /**
     * license为空
     */
    private static final int LICENSE_IS_NULL = 125080;

    /**
     * 用户超过有效期限
     */
    public static final int USER_EXCEED_VALID_TIME = 125090;

    /**
     * 没有app登录的权限
     */
    public static final int NO_APP_LOGIN_PERMISSION = 125100;

    /**
     * 超过用户最大登录用户数
     */
    private static final int MUILT_USER_THAN_MAXUSERNUMBER = 125160;
    /**
     * license未开始
     */
    private static final int LICENSE_NOT_STARTED = 125170;
    /**
     * 服务名
     */
    private static final String SERVER_NAME = "filink-user-server";

    private static final int SEND_MANY = 10;

    private static final int DEVICETYPE_INDEX = 0;

    /**
     * 有用数据时候的临界值
     */
    private static final int HAS_DATA_NUMBER = 0;

    /**
     * 不存在验证码
     */
    private static final String NOT_EXPIRE_MESSAGE = "";

    /**
     * lince中的过期时间格式
     */
    private static final String TIME_FORMAT = "yyyy-MM-dd";

    /**
     * 只允许错一次
     */
    private static final int ONLY_ONE = 1;

    /**
     * 最大导出条数
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;

    /**
     * 根据部门id获取所属部门人员信息
     *
     * @param deptIdList 部门id集合
     * @return 人员信息
     */
    @Override
    public List<User> queryUserByDeptList(List<String> deptIdList) {

        if (CheckEmptyUtils.collectEmpty(deptIdList)) {
            return userDao.queryUserByDeptList(deptIdList);
        }
        return null;
    }

    /**
     * 验证用户是否能够登陆
     *
     * @param userParameter 登陆参数
     * @return 验证code码
     */
    @Override
    public Integer validateUserLogin(UserParameter userParameter) {

        String userName = userParameter.getUserName();
        String loginIp = userParameter.getLoginIp();
        String loginSource = userParameter.getLoginSourse();

        License currentLicense;
        //获取当前授权license
        try {
            currentLicense = licenseFeign.getCurrentLicense();
        } catch (Exception e) {
            e.printStackTrace();
            return LICENSE_IS_NULL;
        }

        //判断license是否已经过期
        if (currentLicense != null && StringUtils.isNotEmpty(currentLicense.endTime)) {
            try {
                //license过期时间
                // TODO: 2019/7/17  这里需要看LicenseFeign 的当前时间  
                long expireTime = new SimpleDateFormat(TIME_FORMAT).parse(currentLicense.endTime).getTime();
                //当前时间
                long currentTime = FiLinkTimeUtils.getUtcZeroTimeStamp();
                if (currentTime >= expireTime) {
                    return LICENE_HAS_EXPIRATION;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            //license无效或者为空
            return LICENSE_IS_NULL;
        }

        //判断license是否未开始
        if (StringUtils.isNotEmpty(currentLicense.beginTime)) {
            try {
                //license开始时间
                long beginTime = new SimpleDateFormat(TIME_FORMAT).parse(currentLicense.beginTime).getTime();
                //当前时间
                long currentTime = FiLinkTimeUtils.getUtcZeroTimeStamp();
                if (currentTime < beginTime) {
                    return LICENSE_NOT_STARTED;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            //license无效或者为空
            return LICENSE_IS_NULL;
        }

        //获取整个用户的登录数量
        List<String> keyList = queryOnlieUserId();
        int onlineNum = keyList.size();
        //达到所有用户最大登录数
        int maxLoginNum = Integer.parseInt(currentLicense.maxOnlineNum);
        if (onlineNum >= maxLoginNum) {
            return MUILT_USER_THAN_MAXUSERNUMBER;
        }

        if (UserConst.ADMIN_USER_NAME.equals(userName)) {
            return ResultCode.SUCCESS;
        }
        //获取单个登录的用户信息
        User user = userDao.queryUserByName(userName);

        //判断是否有手机登录的权限
        if (!UserConst.PC_WEBSITE.equals(loginSource)) {
            List<Permission> permissionList = user.getRole().getPermissionList();
            Permission appPermission = permissionList.stream().filter(permission ->
                    UserConst.APP_LOGIN_PERMISSION.equals(permission.getId())).findFirst().orElse(null);
            if (appPermission == null) {
                return NO_APP_LOGIN_PERMISSION;
            }
        }
        //达到最大登录数
        Set<String> loginKeys = RedisUtils.
                keys(UserConst.USER_PREFIX + user.getId() + UserConst.REDIS_SPLIT + UserConst.REDIS_WILDCARD);
        Integer maxUsers = user.getMaxUsers();
        if (CheckEmptyUtils.collectEmpty(loginKeys)) {
            //多用户模式下，达到最大登录数量
            if (!UserConst.SINGLE_LOGIN_TIP.equals(user.getLoginType()) && loginKeys.size() >= maxUsers) {
                return MORE_THAN_MAXUSERNUMBER;
            }
        }


        //系统服务异常
        Result result = securityFeign.queryAccountSecurity();
        if (result == null) {
            return SYSTEM_SERVICE_ERROR;
        }

        //校验ip有没有访问权限
        IpAddress ipAddress = new IpAddress();
        ipAddress.setIpAddress(loginIp);
        Result ipResult = securityFeign.hasIpAddress(ipAddress);
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

        //判断用户有没有被锁
        Long unlockTime = user.getUnlockTime();
        long currentTime = FiLinkTimeUtils.getUtcZeroTimeStamp();
        if (null != unlockTime) {
            if (unlockTime > currentTime) {
                return USER_HAS_LOCKED;
            }
        }

        if (FORBIDDEN_USER.equals(user.getUserStatus())) {
            return USER_HAS_FORBIDDEN;
        }

        //用户超过有效期限
        boolean flag = ValidTimeUtils.validTime(user.getCountValidityTime(), user.getCreateTime());
        if (!flag) {
            return USER_EXCEED_VALID_TIME;
        }

        //如果是单用户登录模式
        if (SINGLE_LOGIN_TYPE.equals(user.getLoginType())) {
            //从redis中查询当前用户是否有登录过
            Set<String> keys = RedisUtils.
                    keys(UserConst.USER_PREFIX + user.getId() + UserConst.REDIS_SPLIT + UserConst.REDIS_WILDCARD);
            if (keys != null) {
                keys.forEach(key -> {
                    RedisUtils.remove(key);
                    Map<String, String> idToken = new HashMap<>();
                    String realKey = key.substring(UserConst.USER_PREFIX.length());
                    idToken.put(realKey, user.getId());
                    WebSocketUtils.websocketSendMessage(userStream, UserConst.BE_OFFLINE,
                            UserConst.FORCEOFFLINE_CHANNEL_KEY, idToken, SEND_MANY);
                });
            }
        }
        return ResultCode.SUCCESS;
    }

    /**
     * 导出用户列表信息
     *
     * @param exportDto 导出用于列表的参数
     * @return 导出的结果
     */
    @Override
    public Result exportUserList(ExportDto exportDto) {
        systemLanguageUtil.querySystemLanguage();
        ExportRequestInfo exportRequestInfo;
        try {
            exportRequestInfo = userListExport.insertTask(exportDto, SERVER_NAME, I18nUtils.getSystemString(UserI18n.OPERATE_USER_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(UserConst.EXPORT_NO_DATA, I18nUtils.getSystemString(UserI18n.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return getExportToLargeMsg(fe);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(UserConst.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS,
                    I18nUtils.getSystemString(UserI18n.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(UserConst.FAILED_TO_CREATE_EXPORT_TASK,
                    I18nUtils.getSystemString(UserI18n.FAILED_TO_CREATE_EXPORT_TASK));
        }
        userListExport.exportData(exportRequestInfo);

        //添加导出安全日志
        universalLog(RequestInfoUtils.getUserId(), UserConst.EXPORT_USER_LIST, LogConstants.LOG_TYPE_SECURITY);

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 通用的日志信息方法
     *
     * @param id      用户id
     * @param model   模板id
     * @param logType 日志类型
     */
    private void universalLog(String id, String model, String logType) {
        systemLanguageUtil.querySystemLanguage();
        User user = userDao.selectById(id);
        //获取日志类型
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("id");
        //获得操作对象名称
        addLogBean.setFunctionCode(model);
        //获得操作对象id
        addLogBean.setOptObjId(id);
        addLogBean.setOptObj(user.getUserName());
        addLogBean.setOptUserName(user.getUserName());
        //操作为新增
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_ADD);

        //新增操作日志
        logProcess.addSecurityLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }


    /**
     * 处理用户登录失败的逻辑
     *
     * @param userParameter 用户信息
     * @return
     */
    @Override
    public void dealLoginFail(UserParameter userParameter) {

        log.info("进入了处理登录错误的方法....");
        String userName = userParameter.getUserName();
        log.info("dealLoginFail userParameter {}", userParameter);
        //获取用户最多能登录出错的次数
        Result result = securityFeign.queryAccountSecurity();
        AccountSecurityStrategy straty = JSON.parseObject(JSON.toJSONString(result.getData()), AccountSecurityStrategy.class);

        //将用户的失败次数加1    (用户id + ip + 端口,List<失败的时间戳>)  记录失败的时间戳，次数为安全策略中记录的总次数
        User user = userDao.queryUserByName(userName);
        if (user == null) {
            return;
        }

        //获取用户ip和端口
        String loginIp = userParameter.getLoginIp();
        String key = loginIp + UserConst.REDIS_IP_PORT_SPLIT + user.getId();
        log.info("key = {}", key);
        boolean hasKey = RedisUtils.hasKey(key);
        Integer illegalLoginCount = straty.getIllegalLoginCount();
        if (hasKey) {

            List<Long> timeList = (List) RedisUtils.get(key);
            log.info("timeList {}", timeList);

            List<Long> lastFailTimeList = new ArrayList<>();
            if (timeList.size() < illegalLoginCount) {
                timeList.add(FiLinkTimeUtils.getUtcZeroTimeStamp());
                RedisUtils.set(key, timeList);
            } else {
                lastFailTimeList = timeList.subList(timeList.size() - illegalLoginCount, timeList.size());
                List<Long> lastFailTime = new ArrayList<>();
                lastFailTime.addAll(lastFailTimeList);
                Long firstNumber = lastFailTime.get(UserConst.FIRST_NUMBER_INDEX);
                Long intervalTime = System.currentTimeMillis() - straty.getIntervalTime() * 60 * 1000;

                //连续一段时间错误次数太多
                if (firstNumber >= intervalTime) {
                    //用户禁用，并设置锁定开始时间
                    if (START_LOCK_STRATY.equals(straty.getLockStrategy())) {
                        Integer lockedTime = straty.getLockedTime() * 60 * 1000;
                        Long unLockTime = FiLinkTimeUtils.getUtcZeroTimeStamp() + lockedTime;
                        userDao.updateStatusAndUnlockTime(user.getId(), unLockTime, FORBIDDEN_USER);
                    }
                    RedisUtils.remove(key);
                } else {
                    //如果没有过期
                    lastFailTime.add(FiLinkTimeUtils.getUtcZeroTimeStamp());
                    lastFailTime.remove(UserConst.FIRST_NUMBER_INDEX);
                    RedisUtils.set(key, lastFailTime);
                }
            }
        } else {
            //如果只错一次，允许用户登录错误的次数是否为一次
            if (illegalLoginCount <= ONLY_ONE) {

                //用户禁用，并设置锁定开始时间
                if (START_LOCK_STRATY.equals(straty.getLockStrategy())) {

                    Integer lockedTime = straty.getLockedTime() * 60 * 1000;
                    Long unLockTime = FiLinkTimeUtils.getUtcZeroTimeStamp() + lockedTime;
                    userDao.updateStatusAndUnlockTime(user.getId(), unLockTime, FORBIDDEN_USER);
                }
            } else {
                //如果可以错多次，则添加错误信息到列表中
                List<Long> timeList = new ArrayList<>();
                timeList.add(FiLinkTimeUtils.getUtcZeroTimeStamp());
                RedisUtils.set(key, timeList);
            }
        }
    }

    /**
     * 根据部门和设施类型查询在线用户
     *
     * @param dataPermission 数据权限信息
     * @return 在线用户唯一标识
     */
    @Override
    public List<String> queryDeviceTypeByPermission(DataPermission dataPermission) {

        List<String> userIds = userDao.queryUserByDeptAndDeviceType(dataPermission);
        if (!userIds.contains(UserConst.ADMIN_ID)) {
            userIds.add(UserConst.ADMIN_ID);
        }
        List<String> onlineUserToken = new ArrayList<>();
        userIds.forEach(userId -> {
            Set<String> keys = RedisUtils.
                    keys(UserConst.USER_PREFIX + userId + UserConst.REDIS_SPLIT + UserConst.REDIS_WILDCARD);
            if (CheckEmptyUtils.collectEmpty(keys)) {
                keys.forEach(tokens -> {
                    String token = tokens.split(UserConst.REDIS_SPLIT)[TOKEN_SITE];
                    onlineUserToken.add(token);
                });
            }
        });

        return onlineUserToken;
    }

    /**
     * 发送短信
     *
     * @param phoneNumber 手机号
     */
    @Override
    public Result sendMessage(String phoneNumber) throws ClientException {

        //判断当前账号是否为有效账号
        User user = userDao.queryUserByPhone(phoneNumber);
        if (user == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.PHONE_NOT_EXIST));
        }

        //添加

        AliyunSms aliyunSms = new AliyunSms();
        AliAccessKey aliAccessKey = parameterFeign.queryMessage();
        aliyunSms.setAccessKeyId(aliAccessKey.getAccessKeyId());
        aliyunSms.setAccessKeySecret(aliAccessKey.getAccessKeySecret());

        int sendNumber = (int) ((Math.random() * 9 + 1) * 100000);
        String sendMessage = Integer.toString(sendNumber);

        aliyunSms.setPhone(phoneNumber);
        SmsCode smsCode = new SmsCode(sendMessage);

        aliyunSms.setTemplateParam(JSON.toJSONString(smsCode));
        aliyunSms.setSignName(UserConst.BASIC_FACILITIES);
        aliyunSms.setTemplateCode(UserConst.LOGIN_MESSAGE_MODEL);

        //SendSmsResponse sendSmsResponse = (SendSmsResponse) aliyunSendSms.sendSmsAndEmail(aliyunSms);
        aliyunSendSms.sendSmsAndEmail(aliyunSms);

        //将要发送的信息保存到redis中
        RedisUtils.set(phoneNumber, sendMessage, UserConst.MESSAGE_EXPIRE_TIME);
        return ResultUtils.success(sendMessage);
    }

    /**
     * 根据设施获取用户信息
     *
     * @param dataPermissionList 设施信息
     * @return
     */
    @Override
    public Map<String, List<String>> queryUserByDevice(List<DataPermission> dataPermissionList) {

        Map<String, List<String>> userMap = new HashMap<>();
        //获取所有用户信息，然后判断每个用户的信息
        List<User> userList = userDao.queryAllUserDetailInfo();
        userList.forEach(user -> {
            List<String> deviceList = new ArrayList<>();
            String deptId = user.getDeptId();
            List<String> deviceTypeList = new ArrayList<>();
            user.getRole().getRoleDevicetypeList().forEach(roleDeviceType -> {
                deviceTypeList.add(roleDeviceType.getDeviceTypeId());
            });
            dataPermissionList.forEach(dataPermission -> {
                List<String> deptList = dataPermission.getDeptList();
                List<String> deviceTypes = dataPermission.getDeviceTypes();
                //如果用户的部门和设施类型属于其中的
                int index = deptList.indexOf(deptId);
                int deviceIndex = deviceTypeList.indexOf(deviceTypes.get(DEVICETYPE_INDEX));
                if (index >= HAS_DATA_NUMBER && deviceIndex >= HAS_DATA_NUMBER) {
                    deviceList.add(dataPermission.getDeviceId());
                }
            });

            checkUserByDevice(deviceList, user, userMap);
        });

        return userMap;
    }

    /**
     * @param phoneNumber 手机号
     * @return 验证码
     */
    @Override
    public String getSmsMessage(String phoneNumber) {

        if (RedisUtils.hasKey(phoneNumber)) {
            return (String) RedisUtils.get(phoneNumber);
        }

        return NOT_EXPIRE_MESSAGE;
    }

    /**
     * 根据手机号获取用户信息
     *
     * @param phoneNumber
     * @return
     */
    @Override
    public User queryUserByPhone(String phoneNumber) {

        return userDao.queryUserByPhone(phoneNumber);
    }

    /**
     * 根据用户id获取token列表
     *
     * @param userId 用户id
     * @return token列表
     */
    @Override
    public List<String> queryTokenByUserId(String userId) {

        List<String> tokenList = new ArrayList<>();
        Set<String> userKeys = RedisUtils.
                keys(UserConst.USER_PREFIX + userId + UserConst.REDIS_SPLIT + UserConst.REDIS_WILDCARD);
        if (CheckEmptyUtils.collectEmpty(userKeys)) {
            userKeys.forEach(keyToken -> {
                String token = keyToken.substring(UserConst.USER_PREFIX.length() +
                        userId.length() + UserConst.REDIS_SPLIT.length(), keyToken.length());
                tokenList.add(token);
            });
        }

        return tokenList;
    }

    /**
     * 根据用户id获取登录用户的设备id列表
     *
     * @param idList 用户id列表
     * @return 设备id列表
     */
    @Override
    public List<String> queryPhoneIdByUserIds(List<String> idList) {

        List<String> deviceIdList = new ArrayList<>();
        if (!CheckEmptyUtils.collectEmpty(idList)) {
            return deviceIdList;
        }

        idList.forEach(id -> {
            Set<String> keys = RedisUtils.keys(UserConst.USER_PREFIX + id + UserConst.REDIS_SPLIT);
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
     * 根据用户名获取用户id列表
     *
     * @param userName 用户名
     * @return 用户id列表
     */
    @Override
    public List<String> queryUserIdByName(String userName) {

        //过滤特殊字符
        if (StringUtils.isNotEmpty(userName)) {
            userName = userName.replace(UserConst.BACK_SLASH_TWO, UserConst.BACK_SLASH_FOUR)
                    .replace(UserConst.PER_CENT, UserConst.BACK_SLASH_PER_CENT)
                    .replace(UserConst.UNDER_LINE, UserConst.BACK_SLASH_UNDER_LINE)
                    .replace(UserConst.FORWORD_SLASH, UserConst.BACK_SLASH_FORWARD_SLASH);
        }
        return userDao.queryUserIdByName(userName);
    }

    /**
     * 查询账号数和在线用户数
     *
     * @return
     */
    @Override
    public UserCount queryUserNumber() {

        int userAccountNumber = 0;
        int onlineUserNumber = 0;
        List<User> userList = userDao.queryAllUser();
        userAccountNumber = userList.size();
        Set<String> tokenKey = RedisUtils.keys(UserConst.USER_PREFIX + UserConst.REDIS_WILDCARD);
        if (CheckEmptyUtils.collectEmpty(tokenKey)) {

            onlineUserNumber = tokenKey.size();

            //如果包含有用户在线时间这个key，就需要减去1
            if (RedisUtils.hasKey(UserI18n.USER_EXPIRE_TIME)) {
                onlineUserNumber--;
            }
        }

        return new UserCount(userAccountNumber, onlineUserNumber);
    }

    /**
     * 根据用户id列表获取用户列表信息
     *
     * @param idList 用户id列表
     * @return 用户信息列表
     */
    @Override
    public List<User> queryOnlineUserByIdList(List<String> idList) {

        List<User> userList = new ArrayList<>();
        idList.forEach(id -> {
            Set<String> keys = RedisUtils.keys(UserConst.USER_PREFIX + id + UserConst.REDIS_SPLIT + UserConst.REDIS_WILDCARD);
            if (CheckEmptyUtils.collectEmpty(keys)) {
                keys.forEach(key -> {
                    Object userObject = RedisUtils.get(key);
                    if (userObject instanceof User) {
                        String token = key.substring(key.indexOf(UserConst.REDIS_SPLIT), key.length());
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
     * 修改在线用户的手机设备id和appkey
     *
     * @param user 修改的用户信息
     * @return 修改的结果
     */
    @Override
    public Result modifyUserPhoneIdAndAppKey(User user) {

        String userId = RequestInfoUtils.getUserId();
        String token = RequestInfoUtils.getToken();
        if (RedisUtils.hasKey(UserConst.USER_PREFIX + userId + UserConst.REDIS_SPLIT + token)) {

            Object userObject = RedisUtils.get(UserConst.USER_PREFIX + userId + UserConst.REDIS_SPLIT + token);
            if (userObject != null) {

                if (userObject instanceof User) {
                    long expire = RedisUtils.getExpire(UserConst.USER_PREFIX + userId + UserConst.REDIS_SPLIT + token);
                    User redisUser = (User) userObject;
                    redisUser.setPushId(user.getPushId());
                    redisUser.setAppKey(user.getAppKey());
                    redisUser.setPhoneType(user.getPhoneType());
                    RedisUtils.set(UserConst.USER_PREFIX + userId + UserConst.REDIS_SPLIT + token, redisUser, expire);
                    return ResultUtils.warn(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.MODIFY_USER_LOGIN_DEVICE_SUCCESS));
                }
            }
        }

        return ResultUtils.warn(UserConst.USER_NOT_EXIST, I18nUtils.getSystemString(UserI18n.USER_NOT_EXIST));
    }

    /**
     * 获取所有的用户信息
     *
     * @return 用户信息
     */
    @Override
    public Result queryAllUserInfo() {

        List<User> userList = userDao.queryAllUser();
        return ResultUtils.success(userList);
    }

    /**
     * 根据权限获取用户信息
     *
     * @param queryCondition 查询参数
     * @return 用户信息
     */
    @Override
    public Result queryUserByPermission(QueryCondition<UserParameter> queryCondition) {

        //获取参数信息和分页信息
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
     * 根据部门和设施类型获取人员信息
     *
     * @param dataPermission 部门和设施类型信息
     * @return 人员列表
     */
    @Override
    public Result queryUserInfoByDeptAndDeviceType(DataPermission dataPermission) {

        List<User> users = userDao.queryUserInfoByDeptAndDeviceType(dataPermission);
        return ResultUtils.success(users);
    }

    /**
     * 查找在线用户中可以发送消息的人
     *
     * @param deviceList 设施列表
     * @param user       用户
     * @param userMap    用户和设施列表的对应关系
     */
    private void checkUserByDevice(List<String> deviceList, User user, Map<String, List<String>> userMap) {

        //查询到了该用户所有要能发消息的设施的设施
        if (CheckEmptyUtils.collectEmpty(deviceList)) {
            Set<String> keySet = RedisUtils.keys(UserConst.USER_PREFIX + user.getId() + UserConst.REDIS_SPLIT + UserConst.REDIS_WILDCARD);
            if (CheckEmptyUtils.collectEmpty(keySet)) {
                keySet.forEach(key -> {
                    String[] tokens = key.split(UserConst.REDIS_SPLIT);
                    userMap.put(tokens[TOKEN_SITE], deviceList);
                });
            }
        }
    }

    /**
     * 导出数据超过最大限制返回信息
     *
     * @param fe 异常
     * @return 返回结果
     */
    private Result getExportToLargeMsg(FilinkExportDataTooLargeException fe) {
        fe.printStackTrace();
        String string = I18nUtils.getSystemString(UserI18n.EXPORT_DATA_TOO_LARGE);
        String dataCount = fe.getMessage();
        Object[] params = {dataCount, maxExportDataSize};
        String msg = MessageFormat.format(string, params);
        return ResultUtils.warn(UserConst.EXPORT_DATA_TOO_LARGE, msg);
    }

    /**
     * 所有在线的用户key
     *
     * @return
     */
    public List<String> queryOnlieUserId() {

        List<String> keyList = new ArrayList<>();
        Set<String> keyToken = RedisUtils.keys(UserConst.USER_PREFIX + UserConst.REDIS_WILDCARD);
        if (CheckEmptyUtils.collectEmpty(keyToken)) {
            return new ArrayList<>(keyToken);
        }

        return keyList;
    }
}
