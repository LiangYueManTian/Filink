package com.fiberhome.filink.userserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.bean.WebSocketMessage;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.api.DeviceMapConfigFeign;
import com.fiberhome.filink.deviceapi.bean.AreaDeptInfo;
import com.fiberhome.filink.license.api.LicenseFeign;
import com.fiberhome.filink.license.bean.License;
import com.fiberhome.filink.license.bean.LicenseThresholdFeignBean;
import com.fiberhome.filink.license.enums.OperationTarget;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.loginSysInfo.bean.LoginSysInfo;
import com.fiberhome.filink.mysql.MpQueryHelper;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.security.api.SecurityFeign;
import com.fiberhome.filink.security.bean.AccountSecurityStrategy;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userserver.bean.Department;
import com.fiberhome.filink.userserver.bean.OnlineParameter;
import com.fiberhome.filink.userserver.bean.OnlineUser;
import com.fiberhome.filink.userserver.bean.Parameters;
import com.fiberhome.filink.userserver.bean.PasswordDto;
import com.fiberhome.filink.userserver.bean.Role;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.bean.UserParameter;
import com.fiberhome.filink.userserver.constant.FunctionCodeConstant;
import com.fiberhome.filink.userserver.constant.UserConstant;
import com.fiberhome.filink.userserver.constant.UserResultCode;
import com.fiberhome.filink.userserver.constant.UserI18n;
import com.fiberhome.filink.userserver.dao.OnlineUserDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.exception.FilinkUserException;
import com.fiberhome.filink.userserver.service.UserService;
import com.fiberhome.filink.userserver.service.UserStream;
import com.fiberhome.filink.userserver.utils.CheckEmptyUtils;
import com.fiberhome.filink.userserver.utils.NameUtils;
import com.fiberhome.filink.userserver.utils.PasswordUtils;
import com.fiberhome.filink.userserver.utils.UUIDUtil;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPageBean;

/**
 * ??????Service???
 *
 * @author xuangong
 * @since 2019-01-03
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private DeviceMapConfigFeign deviceMapConfigFeign;

    @Autowired
    private LogProcess logProcess;

    @Autowired
    private UserStream userStream;

    @Autowired
    private OnlineUserDao onlineUserDao;

    @Autowired
    private AreaFeign areaFeign;

    @Autowired
    private SecurityFeign securityFeign;

    @Autowired
    private LicenseFeign licenseFeign;

    @Autowired
    private ProcBaseFeign procBaseFeign;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * ???????????????
     */
    private static final int EMAIN_LENGTH = 50;

    /**
     * ???????????????
     */
    private static final int TIME_LENGTH = 4;

    /**
     * ??????????????????
     */
    private static final String START_USER_STATUS = "1";

    /**
     * ???????????????????????????
     *
     * @param userId ??????id
     * @return ????????????
     */
    @Override
    public Result queryUserInfoById(String userId) {
        User user = userDao.queryUserInfoById(userId);


        if (user == null) {
            return ResultUtils.warn(UserResultCode.USER_HAS_DELETED, I18nUtils.getSystemString(UserI18n.USER_HAS_DELETED));
        }
        return ResultUtils.success(user);
    }

    /**
     * ????????????
     *
     * @param user ????????????
     * @return ????????????
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = UserConstant.ADD_USER, logType = UserConstant.USER_LOG_TYPE,
            functionCode = FunctionCodeConstant.ADD_USER_MODEL_CODE, dataGetColumnName = UserConstant.LOG_USER_NAME,
            dataGetColumnId = UserConstant.LOG_USER_ID)
    @Override
    public Result addUser(User user) {

        systemLanguageUtil.querySystemLanguage();
        if (user.getEmail() != null && user.getEmail().length() > EMAIN_LENGTH) {
            return ResultUtils.warn(UserResultCode.EMAIL_TOO_LONG, I18nUtils.getSystemString(UserI18n.EMAIL_TOO_LONG));
        }

        if (user.getCountValidityTime() != null && user.getCountValidityTime().length() > TIME_LENGTH) {
            return ResultUtils.warn(UserResultCode.VALIDITYTIME_LENGTH_TOO_LONG,
                    I18nUtils.getSystemString(UserI18n.VALIDITYTIME_LENGTH_TOO_LONG));
        }

        //?????????????????????usercode???????????????
        User userByCode = userDao.queryUserByUserCode(user);
        if (userByCode != null) {
            return ResultUtils.warn(UserResultCode.ADD_USER_FAIL, I18nUtils.getSystemString(UserI18n.USER_EXIST));
        }

        //???????????????????????????
        try {
            License license = licenseFeign.getCurrentLicense();
            int maxUserName = Integer.parseInt(license.maxUserNum);
            int userNum = userDao.queryAllUser().size();
            userNum++;
            if (userNum > maxUserName) {
                return ResultUtils.warn(UserResultCode.USER_NUM_OVER_MAX_USER_NUM,
                        I18nUtils.getSystemString(UserI18n.USER_NUM_OVER_MAX_USER_NUM));
            } else {
                LicenseThresholdFeignBean bean = new LicenseThresholdFeignBean();
                bean.setName(OperationTarget.USER.getValue());
                bean.setNum(userNum);
                licenseFeign.synchronousLicenseThreshold(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(UserResultCode.USER_NUM_OVER_MAX_USER_NUM,
                    I18nUtils.getSystemString(UserI18n.USER_NUM_OVER_MAX_USER_NUM));
        }

        //????????????????????????????????????????????????
        user.setUserCode(NameUtils.removeBlank(user.getUserCode()));
        user.setUserName(NameUtils.removeBlank(user.getUserName()));
        user.setUserNickname(NameUtils.removeBlank(user.getUserNickname()));
        user.setUserDesc(NameUtils.removeBlank(user.getUserDesc()));
        user.setEmail(NameUtils.removeBlank(user.getEmail()));
        user.setAddress(NameUtils.removeBlank(user.getAddress()));

        //???????????????id???UUID???
        String userId = UUIDUtil.getInstance().UUID32();
        String createUserId = RequestInfoUtils.getUserId();
        user.setId(userId);
        user.setCreateUser(createUserId);
        user.setCreateTime(System.currentTimeMillis());
        user.setDeleted(UserConstant.USER_DEFAULT_DELETED);
        //???????????????????????????????????????
        user.setPassword(PasswordUtils.passwordEncrypt(UserConstant.DEFAULT_PWD));

        //???????????????????????????
        String lockKey = UserConstant.LOCK_ADD_USER;
        //?????????????????????????????????ms
        int acquireTimeout = UserConstant.ACQUIRE_TIME_OUT;
        //????????????????????????
        int timeout = UserConstant.TIME_OUT;
        //???????????????
        String lockIdentifier = RedisUtils.lockWithTimeout(lockKey, acquireTimeout, timeout);
        if (org.apache.commons.lang.StringUtils.isEmpty(lockIdentifier)) {
            throw new FilinkUserException(I18nUtils.getSystemString(UserI18n.ERROR_GETTING_REDIS_LOCK));
        }

        //??????????????????????????????????????????
        boolean insertFlag = deviceMapConfigFeign.insertConfigBatch(user.getId());
        if (insertFlag) {
            userDao.insert(user);
            //?????????
            RedisUtils.releaseLock(lockKey, lockIdentifier);
            return ResultUtils.success(ResultCode.SUCCESS);
        }
        //?????????
        RedisUtils.releaseLock(lockKey, lockIdentifier);
        return ResultUtils.warn(UserResultCode.ADD_USER_FAIL, I18nUtils.getSystemString(UserI18n.ADD_USER_FAIL));
    }

    /**
     * ????????????
     *
     * @param user ????????????
     * @return ????????????
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = UserConstant.UPDATE_USER, logType = UserConstant.USER_LOG_TYPE,
            functionCode = FunctionCodeConstant.UPDATE_MODEL_CODE, dataGetColumnName = UserConstant.LOG_USER_NAME,
            dataGetColumnId = UserConstant.LOG_USER_ID)
    @Override
    public Result updateUser(User user) {
        systemLanguageUtil.querySystemLanguage();
        if (StringUtils.isEmpty(user.getId())) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.UPDATE_USER_NOT_NULL));
        }

        if (user.getEmail() != null && user.getEmail().length() > EMAIN_LENGTH) {
            return ResultUtils.warn(UserResultCode.EMAIL_TOO_LONG, I18nUtils.getSystemString(UserI18n.EMAIL_TOO_LONG));
        }

        if (user.getCountValidityTime() != null && user.getCountValidityTime().length() > TIME_LENGTH) {
            return ResultUtils.warn(UserResultCode.VALIDITYTIME_LENGTH_TOO_LONG,
                    I18nUtils.getSystemString(UserI18n.VALIDITYTIME_LENGTH_TOO_LONG));
        }

        //????????????????????????
        String updateUserId = RequestInfoUtils.getUserId();
        user.setUpdateUser(updateUserId);
        user.setUpdateTime(System.currentTimeMillis());

        //??????????????????????????????????????????
        User updateUser = userDao.queryUserInfoById(user.getId());
        if (updateUser == null) {
            return ResultUtils.warn(UserResultCode.USER_HAS_DELETED, I18nUtils.getSystemString(UserI18n.USER_HAS_DELETED));
        }

        //?????????????????????admin??????
        if (UserConstant.ADMIN_ID.equals(user.getId()) && !updateUserId.equals(user.getId())) {
            return ResultUtils.warn(UserResultCode.ADMIN_USER_NOT_OTHER_UPDATE, I18nUtils.getSystemString(UserI18n.ADMIN_USER_NOT_OTHER_UPDATE));
        }

        //admin????????????????????????
        if (UserConstant.ADMIN_ID.equals(user.getId()) && !updateUser.getRoleId().equals(user.getRoleId())) {
            return ResultUtils.warn(UserResultCode.ADMIN_USER_NOT_OTHER_UPDATE, I18nUtils.getSystemString(UserI18n.ADMIN_USER_NOT_OTHER_UPDATE));
        }

        //????????????????????????????????????????????????
        user.setUserName(NameUtils.removeBlank(user.getUserName()));
        user.setUserNickname(NameUtils.removeBlank(user.getUserNickname()));
        user.setUserDesc(NameUtils.removeBlank(user.getUserDesc()));
        user.setEmail(NameUtils.removeBlank(user.getEmail()));
        user.setAddress(NameUtils.removeBlank(user.getAddress()));

        userDao.updateById(user);
        //?????????????????????????????????????????????null?????????
        if (user.getCountValidityTime() == null) {
            userDao.updateUserValidityTime(user);
        }
        //??????redis??????????????????User
        User userDetail = userDao.queryUserDetailById(user.getId());
        //????????????id??????????????????
        List<String> idList = new ArrayList<>();
        idList.add(userDetail.getDeptId());
        List<String> areaIds = areaFeign.selectAreaIdsByDeptIds(idList);
        if (CheckEmptyUtils.collectEmpty(areaIds)) {
            userDetail.getDepartment().setAreaIdList(areaIds);
        }
        updateRedisUserInfo(userDetail);
        userDetail.setLastLoginTime(null);
        return ResultUtils.success(userDetail);
    }


    /**
     * ???????????????????????????
     *
     * @param user ??????????????????
     */
    private void updateRedisUserInfo(User user) {
        //???????????????????????????
        if (RedisUtils.hasKey(user.getId() + user.getId())) {
            RedisUtils.set(user.getId() + user.getId(), user);
        } else {
            return;
        }
        //??????????????????????????????
        Set<String> keys = RedisUtils.keys(UserConstant.USER_PREFIX + user.getId() + UserConstant.REDIS_SPLIT + UserConstant.REDIS_WILDCARD);
        if (CheckEmptyUtils.collectEmpty(keys)) {
            keys.forEach(keyToken -> {
                if (RedisUtils.hasKey(keyToken)) {
                    User redisUser = (User) RedisUtils.get(keyToken);
                    user.setLastLoginTime(redisUser.getLastLoginTime());
                    user.setLastLoginIp(redisUser.getLastLoginIp());
                    user.setLoginIp(redisUser.getLoginIp());
                    user.setLoginTime(redisUser.getLoginTime());
                    user.setLoginSourse(redisUser.getLoginSourse());
                    user.setPushId(redisUser.getPushId());
                    user.setAppKey(redisUser.getAppKey());
                    user.setLoginType(redisUser.getLoginType());
                    long expire = RedisUtils.getExpire(keyToken);
                    user.setRole(null);
                    user.setDepartment(null);
                    RedisUtils.set(keyToken, user, expire);
                }
            });

        }
    }

    /**
     * ????????????????????????????????????????????????????????????admin?????????????????????????????????????????????
     *
     * @param deleteUser ??????????????????id??????
     * @return ????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteUser(Parameters deleteUser) {

        if (deleteUser == null || ArrayUtils.isEmpty(deleteUser.getFirstArrayParameter())) {
            return ResultUtils.warn(UserResultCode.DELETE_USER_IS_NOT_NULL,
                    I18nUtils.getSystemString(UserI18n.DELETE_USER_IS_NOT_NULL));
        }

        //????????????????????????
        String currentUserId = RequestInfoUtils.getUserId();
        for (String userId : deleteUser.getFirstArrayParameter()) {
            if (currentUserId.equals(userId)) {
                return ResultUtils.warn(UserResultCode.DOESNT_DELETE_MINE,
                        I18nUtils.getSystemString(UserI18n.DOESNT_DELETE_MINE));
            }
        }

        //??????????????????????????????
        List<User> deleteUserList = userDao.batchQueryUserByIds(deleteUser.getFirstArrayParameter());
        if (deleteUser.getFirstArrayParameter().length != deleteUserList.size()) {
            return ResultUtils.warn(UserResultCode.EXIST_HAS_DELETE_USER,
                    I18nUtils.getSystemString(UserI18n.EXIST_HAS_DELETE_USER));
        }

        //??????????????????????????????
        User getUser = deleteUserList.stream().filter(user -> user.getUserStatus().equals(START_USER_STATUS))
                .findFirst().orElse(null);
        if (getUser != null) {
            return ResultUtils.warn(UserResultCode.USER_IN_USE_MODEL,
                    I18nUtils.getSystemString(UserI18n.USER_IN_USE_MODEL));
        }

        //????????????????????????????????????
        boolean orderFlag = procBaseFeign.queryIsExistsAssignUser(Arrays.asList(deleteUser.getFirstArrayParameter()));
        if (orderFlag) {
            return ResultUtils.warn(UserResultCode.USER_IN_USE_ORDER,
                    I18nUtils.getSystemString(UserI18n.USER_IN_USE_ORDER));
        }

        final boolean[] deleteFlag = {false};
        //?????????????????????????????????????????????????????????????????????
        if (!deleteUser.isFlag()) {
            deleteUserList.forEach(user -> {
                Set<String> keys = RedisUtils.keys(UserConstant.USER_PREFIX + user.getId() + UserConstant.REDIS_SPLIT + UserConstant.REDIS_WILDCARD);
                if (keys != null && keys.size() > 0) {
                    deleteFlag[0] = true;
                }
            });
        }

        //?????????????????????????????????admin??????????????????????????????????????????
        boolean contains = Arrays.asList(deleteUser.getFirstArrayParameter()).contains(UserConstant.ADMIN_ID);
        if (deleteFlag[0] && !deleteUser.isFlag()) {
            return ResultUtils.warn(UserResultCode.ONLINE_USER, I18nUtils.getSystemString(UserI18n.ONLINE_USER));
        } else if (contains) {
            return ResultUtils.warn(UserResultCode.NOT_ALLOW_DELETE_ADMIN,
                    I18nUtils.getSystemString(UserI18n.NOT_ALLOW_DELETE_ADMIN));
        } else {
            addLogByUsers(deleteUserList, FunctionCodeConstant.DELETE_USER_LOG);
            websocketSendMessage(UserConstant.DELETE_USER_WEBSOCKET,
                    UserConstant.DELETE_USER_WEBSOCKET, deleteUser.getFirstArrayParameter());

            //??????????????????
            Integer deleteNum = userDao.deleteUser(deleteUser.getFirstArrayParameter(), UserConstant.USER_DELETED_CODE);
            if (!deleteNum.equals(deleteUser.getFirstArrayParameter().length)) {
                throw new FilinkUserException(I18nUtils.getSystemString(UserI18n.DELETE_USER_FAIL));
            }

            //???????????????????????????????????????License???
            int userNum = userDao.queryAllUser().size();
            LicenseThresholdFeignBean bean = new LicenseThresholdFeignBean();
            bean.setName(OperationTarget.USER.getValue());
            bean.setNum(userNum);
            licenseFeign.synchronousLicenseThreshold(bean);


            //?????????redis?????????????????????
            for (String id : deleteUser.getFirstArrayParameter()) {
                Set<String> keys = RedisUtils.keys(UserConstant.USER_PREFIX + id + UserConstant.REDIS_SPLIT + UserConstant.REDIS_WILDCARD);
                if (CheckEmptyUtils.collectEmpty(keys)) {
                    keys.forEach(token -> {
                        RedisUtils.remove(token);
                    });
                }
                if (RedisUtils.hasKey(id + id)) {
                    RedisUtils.remove(id + id);
                }
            }
            //????????????????????????????????????????????????
            deviceMapConfigFeign.deletedConfigByUserIds(Arrays.asList(deleteUser.getFirstArrayParameter()));

            return ResultUtils.success(ResultCode.SUCCESS);
        }
    }

    /**
     * ????????????????????????????????????????????????????????????redis???
     *
     * @param userParameter ??????????????????
     * @return
     */
    @Override
    public User queryUserByName(UserParameter userParameter) {
        String userName = userParameter.getUserName();
        User getUser = userDao.queryUserByName(userName);
        //?????????token????????????????????????????????????token????????????????????????
        if (userParameter.getToken() != null) {
            //??????????????????????????????????????????
            Long loginTime = getUser.getLoginTime();
            String lastLoginIp = getUser.getLoginIp();
            getUser.setLoginIp(userParameter.getLoginIp());

            if (loginTime != null) {
                getUser.setLastLoginTime(loginTime);
            }
            if (lastLoginIp != null) {
                getUser.setLastLoginIp(lastLoginIp);
            }
            //?????????????????????
            String loginSourse = userParameter.getLoginSourse();
            if (UserConstant.PC_WEBSITE.equals(loginSourse)) {
                getUser.setLoginSourse(UserConstant.PC_WEBSITE);
            } else {
                getUser.setLoginSourse(UserConstant.APP_WEBSITE);
            }

            //??????????????????????????????????????????
            getUser.setLoginTime(System.currentTimeMillis());
            //?????????????????????ip??????????????????????????????
            userDao.updateById(getUser);
            //????????????id??????????????????
            List<String> idList = new ArrayList<>();
            idList.add(getUser.getDeptId());
            List<String> areaIds = areaFeign.selectAreaIdsByDeptIds(idList);
            if (CheckEmptyUtils.collectEmpty(areaIds)) {
                getUser.getDepartment().setAreaIdList(areaIds);
            }
            //????????????????????????
            LoginSysInfo loginSysInfo = UserExtraServiceImpl.LOGIN_SYS_INFO_THREAD_LOCAL.get();
            Result result;
            if (loginSysInfo != null) {
                result = loginSysInfo.getSecurityStrategyResult();
            } else {
                result = securityFeign.queryAccountSecurity();
            }

            AccountSecurityStrategy strategy = JSON.parseObject(JSON.toJSONString(result.getData()), AccountSecurityStrategy.class);
            Integer noOperationTime = strategy.getNoOperationTime();
            RedisUtils.set(UserI18n.USER_EXPIRE_TIME, noOperationTime * 60);
            getUser.setExpireTime(noOperationTime * 60);
            Role role = getUser.getRole();
            Department department = getUser.getDepartment();
            getUser.setDepartment(null);
            getUser.setRole(null);
            RedisUtils.set(UserConstant.USER_PREFIX + getUser.getId() + UserConstant.REDIS_SPLIT
                    + userParameter.getToken(), getUser, noOperationTime * 60);
            getUser.setRole(role);
            getUser.setDepartment(department);
            //?????????????????????????????????redis?????????????????????????????????????????????
            //if (!RedisUtils.hasKey(getUser.getId() + getUser.getId())) {
            RedisUtils.set(getUser.getId() + getUser.getId(), getUser);
            //}
            //????????????????????????????????????
            String loginIp = userParameter.getLoginIp();
            String key = loginIp + UserConstant.REDIS_IP_PORT_SPLIT + getUser.getId();
            if (RedisUtils.hasKey(key)) {
                RedisUtils.remove(key);
            }
        }
        UserExtraServiceImpl.LOGIN_SYS_INFO_THREAD_LOCAL.remove();
        return getUser;
    }


    /**
     * ??????????????????
     *
     * @param userStatus  ???????????????
     * @param userIdArray ?????????id??????
     * @return ??????????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateUserStatus(int userStatus, String[] userIdArray) {

        //???????????????????????????????????????
        if (userIdArray == null || userIdArray.length == 0) {
            return ResultUtils.warn(UserResultCode.SELECT_NEED_OPER_DATA,
                    I18nUtils.getSystemString(UserI18n.SELECT_NEED_OPER_DATA));
        }

        //??????????????????admin??????,???????????????
        boolean bo = Arrays.stream(userIdArray).anyMatch(u -> u.equals(UserConstant.ADMIN_ID));
        if (bo) {
            return ResultUtils.warn(UserResultCode.ADMIN_USER_NOT_FORBIDDEN,
                    I18nUtils.getSystemString(UserI18n.ADMIN_USER_NOT_FORBIDDEN));
        }

        //??????????????????????????????????????????
        List<User> userList = userDao.batchQueryUserByIds(userIdArray);
        if (userIdArray.length != userList.size()) {
            return ResultUtils.warn(UserResultCode.EXIST_HAS_DELETE_USER,
                    I18nUtils.getSystemString(UserI18n.EXIST_HAS_DELETE_USER));
        }

        Integer updateNum = userDao.updateUserStatus(userStatus, userIdArray);
        if (!updateNum.equals(userIdArray.length)) {
            throw new FilinkUserException(I18nUtils.getSystemString(UserI18n.UPDATE_FAIL));
        }
        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param userQueryCondition ????????????
     * @return ??????????????????
     */
    @Override
    public List<User> queryUserByField(QueryCondition<User> userQueryCondition) {

        List<FilterCondition> filterConditions = userQueryCondition.getFilterConditions();
        FilterCondition filterCondition = new FilterCondition();

        //???????????????????????????,?????????????????????????????????
        filterCondition.setFilterField("is_deleted");
        filterCondition.setOperator("eq");
        filterCondition.setFilterValue("0");
        filterConditions.add(filterCondition);
        userQueryCondition.setFilterConditions(filterConditions);

        Page page = myBatiesBuildPage(userQueryCondition);
        EntityWrapper entityWrapper = MpQueryHelper.myBatiesBuildQuery(userQueryCondition);

        List<User> userList = userDao.selectPage(page, entityWrapper);

        return userList;
    }

    /**
     * ??????????????????,??????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param pwd ???????????????????????????
     * @return ?????????????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result modifyPWD(PasswordDto pwd) {

        //?????????????????????
        Result checkResult = checkPWD(pwd);
        if (checkResult != null) {
            return checkResult;
        }
        //?????????????????????????????????
        if (!PasswordUtils.passwordDecord(pwd.getNewPWD()).equals(PasswordUtils.passwordDecord(pwd.getConfirmPWD()))) {
            return ResultUtils.warn(ResultCode.FAIL,
                    I18nUtils.getSystemString(UserI18n.CONFIRM_NEW_PWD_EQUALS));
        }
        User pwdUser = userDao.queryUserById(pwd.getUserId());
        if (!PasswordUtils.passwordDecord(pwd.getOldPWD()).equals(PasswordUtils.passwordDecord(pwdUser.getPassword()))) {
            return ResultUtils.warn(ResultCode.FAIL,
                    I18nUtils.getSystemString(UserI18n.OLDPWD_WRONG));
        }

        universalLog(pwd.getUserId(), FunctionCodeConstant.MODIFY_USER_LOG, LogConstants.LOG_TYPE_SECURITY);

        Integer modifyNum = userDao.modifyPWD(pwd);
        if (modifyNum <= 0) {
            throw new FilinkUserException(I18nUtils.getSystemString(UserI18n.UPDATE_PWD_FAIL));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.UPDATE_PWD_SUCCESS));
    }

    /**
     * ??????????????????????????????
     *
     * @param pwd ?????????????????????
     * @return ???????????????
     */
    public Result checkPWD(PasswordDto pwd) {
        if (pwd == null) {
            return ResultUtils.warn(UserResultCode.NEWPWD_IS_NOT_NULL, I18nUtils.getSystemString(UserI18n.NEWPWD_IS_NOT_NULL));
        }
        if (StringUtils.isEmpty(pwd.getNewPWD())) {
            return ResultUtils.warn(UserResultCode.NEWPWD_IS_NOT_NULL,
                    I18nUtils.getSystemString(UserI18n.NEWPWD_IS_NOT_NULL));
        } else if (StringUtils.isEmpty(pwd.getOldPWD())) {
            return ResultUtils.warn(UserResultCode.OLDPWD_IS_NOT_NULL,
                    I18nUtils.getSystemString(UserI18n.OLDPWD_IS_NOT_NULL));
        } else if (StringUtils.isEmpty(pwd.getConfirmPWD())) {
            return ResultUtils.warn(UserResultCode.CONFIRMPWD_IS_NOT_NULL,
                    I18nUtils.getSystemString(UserI18n.CONFIRMPWD_IS_NOT_NULL));
        } else if (pwd.getOldPWD().equals(pwd.getNewPWD())) {
            return ResultUtils.warn(UserResultCode.NEW_OLD_PASSWORD_EQUALS,
                    I18nUtils.getSystemString(UserI18n.NEW_OLD_PASSWORD_EQUALS));
        }
        return null;
    }

    /**
     * ??????????????????
     *
     * @param pwd ???????????????????????????
     * @return ??????????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result resetPWD(PasswordDto pwd) {

        //???????????????????????????
        User user = userDao.selectById(pwd.getUserId());
        List<User> userList = new ArrayList<>();
        userList.add(user);
        addLogByUsers(userList, FunctionCodeConstant.RESET_PWD_MODEL);
        //????????????????????? ?????????
        pwd.setNewPWD(PasswordUtils.passwordEncrypt(UserConstant.USER_RESET_NEW_PWD));
        //????????????
        Integer modifyNum = userDao.modifyPWD(pwd);

        if (modifyNum <= 0) {
            throw new FilinkUserException(I18nUtils.getSystemString(UserI18n.UPDATE_PWD_FAIL));
        }
        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     * ????????????????????????
     *
     * @param queryCondition ???????????????
     * @return ??????????????????
     */
    @Override
    public Result getOnLineUser(QueryCondition<OnlineParameter> queryCondition) {

        //?????????????????????????????????
        OnlineParameter onlineParameter = queryCondition.getBizCondition();
        if (onlineParameter == null) {
            onlineParameter = new OnlineParameter();
        }
        Page page = myBatiesBuildPage(queryCondition);

        //??????????????????????????????????????????
        List<String> onlineToken = onlineUserDao.queryAllOnlineUser();
        List<OnlineUser> loginList = new ArrayList<>();

        //?????????????????????
        modifyOnlineUser(loginList);

        //?????????????????????????????????
        if (onlineToken != null && onlineToken.size() > 0) {
            onlineUserDao.deleteBatchIds(onlineToken);
        }
        //?????????????????????????????????
        if (loginList.size() > 0) {
            onlineUserDao.batchAddOnlineUser(loginList);
        }

        //????????????????????????????????????
        onlineParameter.setStartNum((queryCondition.getPageCondition().getPageNum() - 1)
                * queryCondition.getPageCondition().getPageSize());
        onlineParameter.setPage(queryCondition.getPageCondition().getPageNum());
        onlineParameter.setPageSize(queryCondition.getPageCondition().getPageSize());

        //????????????????????????????????????????????????????????????????????????????????????
        String userId = RequestInfoUtils.getUserId();
        String token = RequestInfoUtils.getToken();
        if (!UserConstant.ADMIN_ID.equals(userId)) {
            User user = queryCurrentUser(userId, token);
            onlineParameter.setCurrentUserRoleName(user.getRole().getRoleName());
            onlineParameter.setCurrentUserDepartmentName(user.getDepartment().getDeptName());
        }

        List<OnlineUser> onlineUsers = onlineUserDao.queryOnlineUserList(onlineParameter);
        Long number = onlineUserDao.queryOnlineUserNumber(onlineParameter);

        PageBean pageBean = myBatiesBuildPageBean(page, number.intValue(), onlineUsers);
        return ResultUtils.success(pageBean);

    }

    /**
     * ????????????????????????
     *
     * @param loginList ??????????????????
     */
    public void modifyOnlineUser(List<OnlineUser> loginList) {

        Map<String, User> userMap = new HashMap<>();

        //???????????????redis?????????key?????????id?????????token?????????????????????19???,??????keytoekn?????????_?????????
        Set<String> keySet = RedisUtils.keys(UserConstant.USER_PREFIX + UserConstant.REDIS_WILDCARD);
        //??????????????????????????????
        if (CheckEmptyUtils.collectEmpty(keySet)) {
            keySet.forEach(keyToken -> {
                OnlineUser onlineUser = new OnlineUser();
                Object userBasicObject = RedisUtils.get(keyToken);
                Object userObject = null;
                User basicUser = new User();
                if (userBasicObject instanceof User) {
                    basicUser = (User) userBasicObject;
                    userObject = userMap.get(basicUser.getId());
                    if (userObject == null) {
                        userObject = RedisUtils.get(basicUser.getId() + basicUser.getId());
                        userMap.put(basicUser.getId(), (User) userObject);
                    }
                }

                //??????userobject??????????????????????????????????????????????????????????????????
                if (userObject != null && userObject instanceof User) {
                    basicUser = (User) userBasicObject;
                    User loginUser = userMap.get(basicUser.getId());
                    basicUser.setDepartment(loginUser.getDepartment());
                    basicUser.setRole(loginUser.getRole());
                    userToOnlineUser(keyToken, basicUser, onlineUser);
                    loginList.add(onlineUser);
                }
            });
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param userId ??????id
     * @param token  ??????token
     * @return ????????????
     */
    @Override
    public User queryCurrentUser(String userId, String token) {

        //????????????id??????token??????null??????????????????null
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(token)) {
            return null;
        }

        //???????????????????????????????????????????????????
        if (RedisUtils.hasKey(UserConstant.USER_PREFIX + userId + UserConstant.REDIS_SPLIT + token)) {
            User basicUser = (User) RedisUtils.get(UserConstant.USER_PREFIX + userId + UserConstant.REDIS_SPLIT + token);
            if (RedisUtils.hasKey(basicUser.getId() + basicUser.getId())) {
                Object userObject = RedisUtils.get(basicUser.getId() + basicUser.getId());
                if (userObject instanceof User) {
                    User user = (User) userObject;
                    basicUser.setDepartment(user.getDepartment());
                    basicUser.setRole(user.getRole());
                    return basicUser;
                }
            }
        }

        return null;
    }

    /**
     * key???token  value?????????id
     *
     * @param userMap ??????id
     * @return ?????????????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result forceOffline(Map<String, String> userMap) {

        //??????????????????????????????????????????????????????
        String currentUserId = RequestInfoUtils.getUserId();
        String currentToken = RequestInfoUtils.getToken();
        Map<String, String> offlineMap = new HashMap<>();
        Set<String> idSet = userMap.keySet();
        if (idSet.contains(UserConstant.USER_PREFIX + currentUserId + UserConstant.REDIS_SPLIT + currentToken)) {
            return ResultUtils.warn(UserResultCode.DOESNT_FORCE_MINE_OFFLINE,
                    I18nUtils.getSystemString(UserI18n.DOESNT_FORCE_MINE_OFFLINE));
        }
        if (CheckEmptyUtils.collectEmpty(idSet)) {
            idSet.forEach(removeId -> {
                String id = removeId.substring(UserConstant.USER_PREFIX.length());
                offlineMap.put(id, userMap.get(removeId));
            });
        }

        //websocket???????????????????????????
        WebSocketMessage webSocketMessage = new WebSocketMessage();
        webSocketMessage.setChannelId(UserConstant.FORCEOFFLINE_CHANNEL_ID);
        webSocketMessage.setChannelKey(UserConstant.FORCEOFFLINE_CHANNEL_KEY);
        webSocketMessage.setMsg(offlineMap);

        //????????????????????????????????????????????????
        webSocketMessage.setMsgType(10);
        Message<WebSocketMessage> message = MessageBuilder.withPayload(webSocketMessage).build();
        userStream.webSocketoutput().send(message);

        //?????????????????????????????????redis?????????????????????????????????
        List<User> userList = new ArrayList<>();
        if (userMap != null) {
            Set<String> keys = userMap.keySet();
            keys.forEach(token -> {
                User loginUser = (User) RedisUtils.get(token);
                userList.add(loginUser);
                RedisUtils.remove(token);
            });
        }

        addLogByUsers(userList, FunctionCodeConstant.FORCE_USER_OFFLINE_MODEL);

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.USER_HAS_FORCE_OFFLINE));
    }

    /**
     * ????????????
     *
     * @param userId ??????id
     * @param token  ????????????token??????
     * @return ?????????????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result logout(String userId, String token) {

        universalLog(userId, FunctionCodeConstant.USER_LOGOUT_LOG, LogConstants.LOG_TYPE_SECURITY);
        log.info("userId + UserConstant.REDIS_SPLIT + token ={} ", userId + UserConstant.REDIS_SPLIT + token);

        RedisUtils.remove(UserConstant.USER_PREFIX + userId + UserConstant.REDIS_SPLIT + token);
        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     * ????????????????????????
     *
     * @return ??????????????????
     */
    @Override
    public Result queryUserDefaultPWD() {

        String pwd = UserConstant.USER_RESET_NEW_PWD;
        return ResultUtils.success(ResultCode.SUCCESS, null, pwd);
    }

    /**
     * ??????????????????????????????
     *
     * @param queryCondition ??????????????????????????????????????????
     * @return ??????????????????
     */
    @Override
    public Result queryUserByFieldAndCondition(QueryCondition<UserParameter> queryCondition) {

        //?????????????????????????????????
        UserParameter userParameter = queryCondition.getBizCondition();
        if (userParameter == null) {
            userParameter = new UserParameter();
        }
        userParameter.setUserCode(userParameter.alterLikeValue(userParameter.getUserCode()));
        userParameter.setUserName(userParameter.alterLikeValue(userParameter.getUserName()));
        userParameter.setUserNickname(userParameter.alterLikeValue(userParameter.getUserNickname()));
        userParameter.setPhoneNumber(userParameter.alterLikeValue(userParameter.getPhoneNumber()));
        userParameter.setEmail(userParameter.alterLikeValue(userParameter.getEmail()));
        userParameter.setAddress(userParameter.alterLikeValue(userParameter.getAddress()));
        userParameter.setLastLoginIp(userParameter.alterLikeValue(userParameter.getLastLoginIp()));
        userParameter.setUserDesc(userParameter.alterLikeValue(userParameter.getUserDesc()));
        Page page = myBatiesBuildPage(queryCondition);

        userParameter.setStartNum((queryCondition.getPageCondition().getPageNum() - 1)
                * queryCondition.getPageCondition().getPageSize());
        userParameter.setPage(queryCondition.getPageCondition().getPageNum());
        userParameter.setPageSize(queryCondition.getPageCondition().getPageSize());

        List<User> userList = userDao.queryUserByField(userParameter);
        Long userNumber = userDao.queryUserNum(userParameter);

        PageBean pageBean = myBatiesBuildPageBean(page, userNumber.intValue(), userList);
        return ResultUtils.success(pageBean);
    }

    /**
     * ????????????id??????????????????
     *
     * @param parameters ????????????
     * @return ??????????????????
     */
    @Override
    public Result queryUserByDept(Parameters parameters) {

        List<User> users = new ArrayList<>();
        if (ArrayUtils.isNotEmpty(parameters.getFirstArrayParameter())) {
            users = userDao.queryUserByDepts(parameters.getFirstArrayParameter());
        }
        return ResultUtils.success(users);
    }

    /**
     * ???????????????????????????
     *
     * @param userId ??????id
     * @return ??????????????????
     */
    @Override
    public boolean queryUserById(String userId) {

        User user = userDao.queryUserById(userId);
        if (user == null) {
            return false;
        }

        return true;
    }

    /**
     * ??????????????????????????????
     *
     * @param userId ??????id
     * @param token  ???????????????token
     * @return ?????????????????????????????????
     */
    @Override
    public boolean updateLoginTime(String userId, String token) {

        if (RedisUtils.hasKey(UserConstant.USER_PREFIX + userId + UserConstant.REDIS_SPLIT + token)) {
            Integer expireTime = (Integer) RedisUtils.get(UserI18n.USER_EXPIRE_TIME);
            RedisUtils.expire(UserConstant.USER_PREFIX + userId + UserConstant.REDIS_SPLIT + token, expireTime);
            return true;
        }
        return false;
    }

    /**
     * ??????id????????????????????????
     *
     * @param idList ??????id??????
     * @return ????????????
     */
    @Override
    public List<User> queryUserByIdList(List<String> idList) {
        List<User> userList = userDao.queryUserByIdList(idList);
        //???????????????????????????
        List<String> deptList = new ArrayList<>();
        userList.forEach(user -> {
            String deptId = user.getDeptId();
            deptList.add(deptId);
        });

        //????????????????????????????????????????????????????????????
        List<AreaDeptInfo> areaDeptInfoList = areaFeign.selectAreaDeptInfoByDeptIds(deptList);
        if (CheckEmptyUtils.collectEmpty(areaDeptInfoList)) {
            userList.forEach(user -> {
                List<String> areaIdList = new ArrayList<>();
                List<AreaDeptInfo> collect = areaDeptInfoList.stream().filter(
                        area -> area.getDeptId().equals(user.getDeptId())).collect(Collectors.toList());
                if (CheckEmptyUtils.collectEmpty(collect)) {
                    collect.forEach(areaInfo -> {
                        areaIdList.add(areaInfo.getAreaId());
                    });
                }
                user.getDepartment().setAreaIdList(areaIdList);
            });
        }

        return userList;
    }


    /**
     * ???????????????????????????
     *
     * @param userList ????????????
     * @param model    ????????????
     */
    private void addLogByUsers(List<User> userList, String model) {
        systemLanguageUtil.querySystemLanguage();
        List<AddLogBean> addLogBeanList = new ArrayList<AddLogBean>();
        if (userList.size() == 0) {
            return;
        }
        for (User user : userList) {
            if (user == null) {
                continue;
            }
            //??????????????????
            String logType = LogConstants.LOG_TYPE_SECURITY;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("id");
            addLogBean.setDataName("userName");

            //????????????????????????
            addLogBean.setFunctionCode(model);
            //??????????????????id
            addLogBean.setOptObjId(user.getId());
            addLogBean.setOptObj(user.getUserName());
            //???????????????
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            addLogBeanList.add(addLogBean);
        }
        if (0 < addLogBeanList.size()) {
            //??????????????????
            logProcess.addSecurityLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
        }
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
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);

        //??????????????????
        logProcess.addSecurityLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }


    /**
     * ????????????????????????????????????
     *
     * @param token      ???????????????token
     * @param user       ????????????
     * @param onlineUser ??????????????????
     */
    public void userToOnlineUser(String token, User user, OnlineUser onlineUser) {
        onlineUser.setId(token);
        onlineUser.setUserId(user.getId());
        onlineUser.setAddress(user.getAddress());

        //?????????????????????????????????,????????????????????????
        if (user.getDepartment() != null) {
            onlineUser.setDeptName(user.getDepartment().getDeptName());
        }
        onlineUser.setEmail(user.getEmail());
        onlineUser.setLoginIp(user.getLoginIp());
        onlineUser.setLoginSource(user.getLoginSourse());
        onlineUser.setPhoneNumber(user.getPhoneNumber());

        //?????????????????????????????????????????????????????????
        if (user.getRole() != null) {
            onlineUser.setRoleName(user.getRole().getRoleName());
        }
        onlineUser.setLoginTime(user.getLoginTime());
        onlineUser.setUserCode(user.getUserCode());
        onlineUser.setUserName(user.getUserName());
        onlineUser.setUserNickname(user.getUserNickname());
    }

    /**
     * websocket????????????
     *
     * @param channelId  ??????id
     * @param channelKey ??????key
     * @param message    ?????????
     */
    public void websocketSendMessage(String channelId, String channelKey, Object message) {
        WebSocketMessage webSocketMessage = new WebSocketMessage();
        webSocketMessage.setChannelId(channelId);
        webSocketMessage.setChannelKey(channelKey);
        webSocketMessage.setMsg(message);
        webSocketMessage.setMsgType(10);

        Message<WebSocketMessage> sendMessage = MessageBuilder.withPayload(webSocketMessage).build();
        userStream.webSocketoutput().send(sendMessage);
    }

}
