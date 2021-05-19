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
 * 用户Service层
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
     * 邮箱的长度
     */
    private static final int EMAIN_LENGTH = 50;

    /**
     * 时间的长度
     */
    private static final int TIME_LENGTH = 4;

    /**
     * 用户启用状态
     */
    private static final String START_USER_STATUS = "1";

    /**
     * 查询单个用户的信息
     *
     * @param userId 用户id
     * @return 查询结果
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
     * 新增用户
     *
     * @param user 用户信息
     * @return 新增结果
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

        //查询有没有相同usercode的用户存在
        User userByCode = userDao.queryUserByUserCode(user);
        if (userByCode != null) {
            return ResultUtils.warn(UserResultCode.ADD_USER_FAIL, I18nUtils.getSystemString(UserI18n.USER_EXIST));
        }

        //是否超过最大用户数
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

        //去除输入框中，用户输入的前后空格
        user.setUserCode(NameUtils.removeBlank(user.getUserCode()));
        user.setUserName(NameUtils.removeBlank(user.getUserName()));
        user.setUserNickname(NameUtils.removeBlank(user.getUserNickname()));
        user.setUserDesc(NameUtils.removeBlank(user.getUserDesc()));
        user.setEmail(NameUtils.removeBlank(user.getEmail()));
        user.setAddress(NameUtils.removeBlank(user.getAddress()));

        //获取新用户id的UUID值
        String userId = UUIDUtil.getInstance().UUID32();
        String createUserId = RequestInfoUtils.getUserId();
        user.setId(userId);
        user.setCreateUser(createUserId);
        user.setCreateTime(System.currentTimeMillis());
        user.setDeleted(UserConstant.USER_DEFAULT_DELETED);
        //对密码进行加密后存入数据库
        user.setPassword(PasswordUtils.passwordEncrypt(UserConstant.DEFAULT_PWD));

        //添加用户的时候加锁
        String lockKey = UserConstant.LOCK_ADD_USER;
        //等待获取锁的时间，单位ms
        int acquireTimeout = UserConstant.ACQUIRE_TIME_OUT;
        //拿到锁的超时时间
        int timeout = UserConstant.TIME_OUT;
        //获取时间锁
        String lockIdentifier = RedisUtils.lockWithTimeout(lockKey, acquireTimeout, timeout);
        if (org.apache.commons.lang.StringUtils.isEmpty(lockIdentifier)) {
            throw new FilinkUserException(I18nUtils.getSystemString(UserI18n.ERROR_GETTING_REDIS_LOCK));
        }

        //插入用户首页地图设施配置信息
        boolean insertFlag = deviceMapConfigFeign.insertConfigBatch(user.getId());
        if (insertFlag) {
            userDao.insert(user);
            //释放锁
            RedisUtils.releaseLock(lockKey, lockIdentifier);
            return ResultUtils.success(ResultCode.SUCCESS);
        }
        //释放锁
        RedisUtils.releaseLock(lockKey, lockIdentifier);
        return ResultUtils.warn(UserResultCode.ADD_USER_FAIL, I18nUtils.getSystemString(UserI18n.ADD_USER_FAIL));
    }

    /**
     * 修改用户
     *
     * @param user 用户信息
     * @return 修改结果
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

        //获取当前操作用户
        String updateUserId = RequestInfoUtils.getUserId();
        user.setUpdateUser(updateUserId);
        user.setUpdateTime(System.currentTimeMillis());

        //查询要更新的用户是否真的存在
        User updateUser = userDao.queryUserInfoById(user.getId());
        if (updateUser == null) {
            return ResultUtils.warn(UserResultCode.USER_HAS_DELETED, I18nUtils.getSystemString(UserI18n.USER_HAS_DELETED));
        }

        //其他人不能修改admin账号
        if (UserConstant.ADMIN_ID.equals(user.getId()) && !updateUserId.equals(user.getId())) {
            return ResultUtils.warn(UserResultCode.ADMIN_USER_NOT_OTHER_UPDATE, I18nUtils.getSystemString(UserI18n.ADMIN_USER_NOT_OTHER_UPDATE));
        }

        //admin账号不能修改角色
        if (UserConstant.ADMIN_ID.equals(user.getId()) && !updateUser.getRoleId().equals(user.getRoleId())) {
            return ResultUtils.warn(UserResultCode.ADMIN_USER_NOT_OTHER_UPDATE, I18nUtils.getSystemString(UserI18n.ADMIN_USER_NOT_OTHER_UPDATE));
        }

        //去除输入框中，用户输入的前后空格
        user.setUserName(NameUtils.removeBlank(user.getUserName()));
        user.setUserNickname(NameUtils.removeBlank(user.getUserNickname()));
        user.setUserDesc(NameUtils.removeBlank(user.getUserDesc()));
        user.setEmail(NameUtils.removeBlank(user.getEmail()));
        user.setAddress(NameUtils.removeBlank(user.getAddress()));

        userDao.updateById(user);
        //更新用户的有效期，有效期存在为null的情况
        if (user.getCountValidityTime() == null) {
            userDao.updateUserValidityTime(user);
        }
        //更新redis里的用户信息User
        User userDetail = userDao.queryUserDetailById(user.getId());
        //根据部门id获取区域信息
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
     * 更新用户的详细信息
     *
     * @param user 待更新的信息
     */
    private void updateRedisUserInfo(User user) {
        //更新用户的详细信息
        if (RedisUtils.hasKey(user.getId() + user.getId())) {
            RedisUtils.set(user.getId() + user.getId(), user);
        } else {
            return;
        }
        //更新每个用户简单信息
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
     * 删除多个用户，不能自己删除自己，不能删除admin，如果删除在线用户需要确认操作
     *
     * @param deleteUser 要删除的用户id数组
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteUser(Parameters deleteUser) {

        if (deleteUser == null || ArrayUtils.isEmpty(deleteUser.getFirstArrayParameter())) {
            return ResultUtils.warn(UserResultCode.DELETE_USER_IS_NOT_NULL,
                    I18nUtils.getSystemString(UserI18n.DELETE_USER_IS_NOT_NULL));
        }

        //不能自己删除自己
        String currentUserId = RequestInfoUtils.getUserId();
        for (String userId : deleteUser.getFirstArrayParameter()) {
            if (currentUserId.equals(userId)) {
                return ResultUtils.warn(UserResultCode.DOESNT_DELETE_MINE,
                        I18nUtils.getSystemString(UserI18n.DOESNT_DELETE_MINE));
            }
        }

        //被删除的用户必须存在
        List<User> deleteUserList = userDao.batchQueryUserByIds(deleteUser.getFirstArrayParameter());
        if (deleteUser.getFirstArrayParameter().length != deleteUserList.size()) {
            return ResultUtils.warn(UserResultCode.EXIST_HAS_DELETE_USER,
                    I18nUtils.getSystemString(UserI18n.EXIST_HAS_DELETE_USER));
        }

        //不能删除被启用的账号
        User getUser = deleteUserList.stream().filter(user -> user.getUserStatus().equals(START_USER_STATUS))
                .findFirst().orElse(null);
        if (getUser != null) {
            return ResultUtils.warn(UserResultCode.USER_IN_USE_MODEL,
                    I18nUtils.getSystemString(UserI18n.USER_IN_USE_MODEL));
        }

        //不能删除有工单任务的账户
        boolean orderFlag = procBaseFeign.queryIsExistsAssignUser(Arrays.asList(deleteUser.getFirstArrayParameter()));
        if (orderFlag) {
            return ResultUtils.warn(UserResultCode.USER_IN_USE_ORDER,
                    I18nUtils.getSystemString(UserI18n.USER_IN_USE_ORDER));
        }

        final boolean[] deleteFlag = {false};
        //如果没有状态位删除用户，则判断当前用户是否在线
        if (!deleteUser.isFlag()) {
            deleteUserList.forEach(user -> {
                Set<String> keys = RedisUtils.keys(UserConstant.USER_PREFIX + user.getId() + UserConstant.REDIS_SPLIT + UserConstant.REDIS_WILDCARD);
                if (keys != null && keys.size() > 0) {
                    deleteFlag[0] = true;
                }
            });
        }

        //如果删除自己，不能删除admin，删除在线用户需要二次点确认
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

            //删除用户信息
            Integer deleteNum = userDao.deleteUser(deleteUser.getFirstArrayParameter(), UserConstant.USER_DELETED_CODE);
            if (!deleteNum.equals(deleteUser.getFirstArrayParameter().length)) {
                throw new FilinkUserException(I18nUtils.getSystemString(UserI18n.DELETE_USER_FAIL));
            }

            //删除用户后将用户总数推送到License中
            int userNum = userDao.queryAllUser().size();
            LicenseThresholdFeignBean bean = new LicenseThresholdFeignBean();
            bean.setName(OperationTarget.USER.getValue());
            bean.setNum(userNum);
            licenseFeign.synchronousLicenseThreshold(bean);


            //需要从redis中删除用户信息
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
            //删除用户的时候要一起删除配置信息
            deviceMapConfigFeign.deletedConfigByUserIds(Arrays.asList(deleteUser.getFirstArrayParameter()));

            return ResultUtils.success(ResultCode.SUCCESS);
        }
    }

    /**
     * 根据用户名查询用户信息，将用户信息存储到redis中
     *
     * @param userParameter 用户查询参数
     * @return
     */
    @Override
    public User queryUserByName(UserParameter userParameter) {
        String userName = userParameter.getUserName();
        User getUser = userDao.queryUserByName(userName);
        //如果有token值则为查询用户信息，没有token则是查询用户密码
        if (userParameter.getToken() != null) {
            //将登录时间修改成上次登录时间
            Long loginTime = getUser.getLoginTime();
            String lastLoginIp = getUser.getLoginIp();
            getUser.setLoginIp(userParameter.getLoginIp());

            if (loginTime != null) {
                getUser.setLastLoginTime(loginTime);
            }
            if (lastLoginIp != null) {
                getUser.setLastLoginIp(lastLoginIp);
            }
            //设置登录源信息
            String loginSourse = userParameter.getLoginSourse();
            if (UserConstant.PC_WEBSITE.equals(loginSourse)) {
                getUser.setLoginSourse(UserConstant.PC_WEBSITE);
            } else {
                getUser.setLoginSourse(UserConstant.APP_WEBSITE);
            }

            //设置当前时间为用户的登录时间
            getUser.setLoginTime(System.currentTimeMillis());
            //用户的登录时间ip等信息更新到用户表中
            userDao.updateById(getUser);
            //根据部门id获取区域信息
            List<String> idList = new ArrayList<>();
            idList.add(getUser.getDeptId());
            List<String> areaIds = areaFeign.selectAreaIdsByDeptIds(idList);
            if (CheckEmptyUtils.collectEmpty(areaIds)) {
                getUser.getDepartment().setAreaIdList(areaIds);
            }
            //获取用户的有效期
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
            //将用户的详细信息放入到redis中，避免一次出错以后，无法修正
            //if (!RedisUtils.hasKey(getUser.getId() + getUser.getId())) {
            RedisUtils.set(getUser.getId() + getUser.getId(), getUser);
            //}
            //将用户登录出错的信息清楚
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
     * 更新用户状态
     *
     * @param userStatus  用户的状态
     * @param userIdArray 用户的id数组
     * @return 修改结果状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateUserStatus(int userStatus, String[] userIdArray) {

        //需要更新状态的用户不能为空
        if (userIdArray == null || userIdArray.length == 0) {
            return ResultUtils.warn(UserResultCode.SELECT_NEED_OPER_DATA,
                    I18nUtils.getSystemString(UserI18n.SELECT_NEED_OPER_DATA));
        }

        //更新用户含有admin用户,不能被停用
        boolean bo = Arrays.stream(userIdArray).anyMatch(u -> u.equals(UserConstant.ADMIN_ID));
        if (bo) {
            return ResultUtils.warn(UserResultCode.ADMIN_USER_NOT_FORBIDDEN,
                    I18nUtils.getSystemString(UserI18n.ADMIN_USER_NOT_FORBIDDEN));
        }

        //需要更新状态的用户不能被删除
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
     * 条件查询用户信息，适合单表查询
     *
     * @param userQueryCondition 查询条件
     * @return 用户信息列表
     */
    @Override
    public List<User> queryUserByField(QueryCondition<User> userQueryCondition) {

        List<FilterCondition> filterConditions = userQueryCondition.getFilterConditions();
        FilterCondition filterCondition = new FilterCondition();

        //查询未被删除的用户,需要添加未被删除的条件
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
     * 修改用户密码,其中新密码，旧密码，确认密码不能为空，新密码和旧密码不能相同
     *
     * @param pwd 用户修改的密码信息
     * @return 修改密码的结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result modifyPWD(PasswordDto pwd) {

        //校验输入的密码
        Result checkResult = checkPWD(pwd);
        if (checkResult != null) {
            return checkResult;
        }
        //新密码和确认密码要相同
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
     * 对输入的密码进行判断
     *
     * @param pwd 输入的密码数据
     * @return 判断的结果
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
     * 重置用户密码
     *
     * @param pwd 用户输入的密码信息
     * @return 重置密码结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result resetPWD(PasswordDto pwd) {

        //添加重置密码的日志
        User user = userDao.selectById(pwd.getUserId());
        List<User> userList = new ArrayList<>();
        userList.add(user);
        addLogByUsers(userList, FunctionCodeConstant.RESET_PWD_MODEL);
        //设置默认新密码 并加密
        pwd.setNewPWD(PasswordUtils.passwordEncrypt(UserConstant.USER_RESET_NEW_PWD));
        //重置密码
        Integer modifyNum = userDao.modifyPWD(pwd);

        if (modifyNum <= 0) {
            throw new FilinkUserException(I18nUtils.getSystemString(UserI18n.UPDATE_PWD_FAIL));
        }
        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     * 获取在线用户信息
     *
     * @param queryCondition 查询条件类
     * @return 用户信息列表
     */
    @Override
    public Result getOnLineUser(QueryCondition<OnlineParameter> queryCondition) {

        //获取参数信息和分页信息
        OnlineParameter onlineParameter = queryCondition.getBizCondition();
        if (onlineParameter == null) {
            onlineParameter = new OnlineParameter();
        }
        Page page = myBatiesBuildPage(queryCondition);

        //获取所有没有被删除的用户信息
        List<String> onlineToken = onlineUserDao.queryAllOnlineUser();
        List<OnlineUser> loginList = new ArrayList<>();

        //对所有用户遍历
        modifyOnlineUser(loginList);

        //将已经下线的用户给删除
        if (onlineToken != null && onlineToken.size() > 0) {
            onlineUserDao.deleteBatchIds(onlineToken);
        }
        //添加后面重新上线的用户
        if (loginList.size() > 0) {
            onlineUserDao.batchAddOnlineUser(loginList);
        }

        //根据条件分页查询用户信息
        onlineParameter.setStartNum((queryCondition.getPageCondition().getPageNum() - 1)
                * queryCondition.getPageCondition().getPageSize());
        onlineParameter.setPage(queryCondition.getPageCondition().getPageNum());
        onlineParameter.setPageSize(queryCondition.getPageCondition().getPageSize());

        //添加数据权限，普通用户可以看到自己同部门或者同角色的信息
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
     * 整理在线用户信息
     *
     * @param loginList 在线用户列表
     */
    public void modifyOnlineUser(List<OnlineUser> loginList) {

        Map<String, User> userMap = new HashMap<>();

        //登录用户在redis存储的key为用户id和登录token组成，长度大于19位,并且keytoekn中含有_分隔符
        Set<String> keySet = RedisUtils.keys(UserConstant.USER_PREFIX + UserConstant.REDIS_WILDCARD);
        //后来又登录的用户信息
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

                //如果userobject为用户类型则进行强转，如果不是就跳过该条数据
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
     * 获取当前登录用户信息
     *
     * @param userId 用户id
     * @param token  用户token
     * @return 用户信息
     */
    @Override
    public User queryCurrentUser(String userId, String token) {

        //如果用户id或者token有为null的值，则返回null
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(token)) {
            return null;
        }

        //从缓存中获取当前登录用户的详细信息
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
     * key为token  value为用户id
     *
     * @param userMap 用户id
     * @return 强制下线的结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result forceOffline(Map<String, String> userMap) {

        //不能强制自己下线，自己下线走注销流程
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

        //websocket发送强制下线的消息
        WebSocketMessage webSocketMessage = new WebSocketMessage();
        webSocketMessage.setChannelId(UserConstant.FORCEOFFLINE_CHANNEL_ID);
        webSocketMessage.setChannelKey(UserConstant.FORCEOFFLINE_CHANNEL_KEY);
        webSocketMessage.setMsg(offlineMap);

        //因为存在多用户模式，所有使用群发
        webSocketMessage.setMsgType(10);
        Message<WebSocketMessage> message = MessageBuilder.withPayload(webSocketMessage).build();
        userStream.webSocketoutput().send(message);

        //强制用户下线的时候，从redis中将用户的登录信息清空
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
     * 用户登出
     *
     * @param userId 用户id
     * @param token  用户唯一token标识
     * @return 用户登出的结果
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
     * 查询用户默认密码
     *
     * @return 用户默认密码
     */
    @Override
    public Result queryUserDefaultPWD() {

        String pwd = UserConstant.USER_RESET_NEW_PWD;
        return ResultUtils.success(ResultCode.SUCCESS, null, pwd);
    }

    /**
     * 分页条件查询用户信息
     *
     * @param queryCondition 用户列表进行条件查询的条件类
     * @return 用户信息列表
     */
    @Override
    public Result queryUserByFieldAndCondition(QueryCondition<UserParameter> queryCondition) {

        //获取参数信息和分页信息
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
     * 根据部门id查询用户信息
     *
     * @param parameters 部门参数
     * @return 用户信息列表
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
     * 查询是否有指定用户
     *
     * @param userId 用户id
     * @return 是否拥有用户
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
     * 更新用户登录的有效期
     *
     * @param userId 用户id
     * @param token  登录用户的token
     * @return 更新用户登录时间的结果
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
     * 根据id集合查询用户信息
     *
     * @param idList 用户id集合
     * @return 用户信息
     */
    @Override
    public List<User> queryUserByIdList(List<String> idList) {
        List<User> userList = userDao.queryUserByIdList(idList);
        //获取所有的部门信息
        List<String> deptList = new ArrayList<>();
        userList.forEach(user -> {
            String deptId = user.getDeptId();
            deptList.add(deptId);
        });

        //获取区域信息，并将区域信息放到用户信息中
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
     * 删除用户信息的日志
     *
     * @param userList 用户列表
     * @param model    模板信息
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
            //获取日志类型
            String logType = LogConstants.LOG_TYPE_SECURITY;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("id");
            addLogBean.setDataName("userName");

            //获得操作对象名称
            addLogBean.setFunctionCode(model);
            //获得操作对象id
            addLogBean.setOptObjId(user.getId());
            addLogBean.setOptObj(user.getUserName());
            //操作为新增
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            addLogBeanList.add(addLogBean);
        }
        if (0 < addLogBeanList.size()) {
            //新增操作日志
            logProcess.addSecurityLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
        }
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
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);

        //新增操作日志
        logProcess.addSecurityLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }


    /**
     * 用户信息转为在线用户信息
     *
     * @param token      用户登录的token
     * @param user       用户信息
     * @param onlineUser 在线用户信息
     */
    public void userToOnlineUser(String token, User user, OnlineUser onlineUser) {
        onlineUser.setId(token);
        onlineUser.setUserId(user.getId());
        onlineUser.setAddress(user.getAddress());

        //如果存在部门为空的用户,则不存储部门信息
        if (user.getDepartment() != null) {
            onlineUser.setDeptName(user.getDepartment().getDeptName());
        }
        onlineUser.setEmail(user.getEmail());
        onlineUser.setLoginIp(user.getLoginIp());
        onlineUser.setLoginSource(user.getLoginSourse());
        onlineUser.setPhoneNumber(user.getPhoneNumber());

        //如果存在角色为空的用户，不存储角色信息
        if (user.getRole() != null) {
            onlineUser.setRoleName(user.getRole().getRoleName());
        }
        onlineUser.setLoginTime(user.getLoginTime());
        onlineUser.setUserCode(user.getUserCode());
        onlineUser.setUserName(user.getUserName());
        onlineUser.setUserNickname(user.getUserNickname());
    }

    /**
     * websocket发送消息
     *
     * @param channelId  通道id
     * @param channelKey 通道key
     * @param message    消息体
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
