package com.fiberhome.filink.userserver.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.device_api.api.DeviceMapConfigFeign;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.utils.LogConstants;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.MpQueryHelper;
import com.fiberhome.filink.userserver.bean.*;
import com.fiberhome.filink.userserver.consts.UserConst;
import com.fiberhome.filink.userserver.consts.UserI18n;
import com.fiberhome.filink.userserver.dao.OnlineUserDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.exception.FilinkUserException;
import com.fiberhome.filink.userserver.service.UserService;
import com.fiberhome.filink.userserver.service.UserStream;
import com.fiberhome.filink.userserver.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.fiberhome.filink.server_common.utils.MpQueryHelper.MyBatiesBuildPage;
import static com.fiberhome.filink.server_common.utils.MpQueryHelper.MyBatiesBuildPageBean;

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

    private static int EMAIN_LENGTH = 50;

    private static int TIME_LENGTH = 4;


    /**
     * 查询单个用户的信息
     * @param userId 用户id
     * @return 查询结果
     */
    @Override
    public Result queryUserInfoById(String userId) {
        User user = userDao.queryUserInfoById(userId);
        if (user == null) {
            return ResultUtils.warn(ResultCode.FAIL, "查询用户信息失败");
        }
        return ResultUtils.success(user);
    }

    /**
     * 新增用户
     * @param user 用户信息
     * @return 新增结果
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = "add", logType = "2", functionCode = "1501101", dataGetColumnName = "userName", dataGetColumnId = "id")
    @Override
    public Result addUser(User user) {

        if(user.getEmail() != null && user.getEmail().length() > EMAIN_LENGTH){
            return ResultUtils.warn(UserConst.EMAIL_TOO_LONG, I18nUtils.getString(UserI18n.EMAIL_TOO_LONG));
        }

        if(user.getCountValidityTime() != null && user.getCountValidityTime().length() > TIME_LENGTH){
            return ResultUtils.warn(UserConst.VALIDITYTIME_LENGTH_TOO_LONG,
                    I18nUtils.getString(UserI18n.VALIDITYTIME_LENGTH_TOO_LONG));
        }

        //查询有没有相同usercode的用户存在
        User userByCode = userDao.queryUserByUserCode(user);
        if(userByCode != null){
            return ResultUtils.warn(UserConst.ADD_USER_FAIL, I18nUtils.getString(UserI18n.USER_EXIST));
        }
        //获取新用户id的UUID值
        String userId = UUIDUtil.getInstance().UUID32();
        String createUserId = RequestInfoUtils.getUserId();
        user.setId(userId);
        user.setCreateUser(createUserId);
        user.setCreateTime(System.currentTimeMillis());
        user.setDeleted(UserConst.USER_DEFAULT_DELETED);
        user.setPassword(UserConst.DEFAULT_PWD);

        //插入用户首页地图设施配置信息
        boolean insertFlag = deviceMapConfigFeign.insertConfigBatch(user.getId());
        if (insertFlag) {
            int result = userDao.insert(user);
            if (result != 1) {
                throw new FilinkUserException(I18nUtils.getString(UserI18n.ADD_USER_FAIL));
            }
            return ResultUtils.success(ResultCode.SUCCESS);
        }
        return ResultUtils.warn(UserConst.ADD_USER_FAIL, I18nUtils.getString(UserI18n.ADD_USER_FAIL));
    }

    /**
     * 修改用户
     * @param user 用户信息
     * @return 修改结果
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = "update", logType = "2", functionCode = "1501102", dataGetColumnName = "userName", dataGetColumnId = "id")
    @Override
    public Result updateUser(User user) {

        if (StringUtils.isEmpty(user.getId())) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(UserI18n.UPDATE_USER_NOT_NULL));
        }

        if(user.getEmail() != null && user.getEmail().length() > EMAIN_LENGTH){
            return ResultUtils.warn(UserConst.EMAIL_TOO_LONG, I18nUtils.getString(UserI18n.EMAIL_TOO_LONG));
        }

        if(user.getCountValidityTime() != null && user.getCountValidityTime().length() > TIME_LENGTH){
            return ResultUtils.warn(UserConst.VALIDITYTIME_LENGTH_TOO_LONG,
                    I18nUtils.getString(UserI18n.VALIDITYTIME_LENGTH_TOO_LONG));
        }

        //获取当前操作用户
        String updateUserId = RequestInfoUtils.getUserId();
        user.setUpdateUser(updateUserId);
        user.setUpdateTime(System.currentTimeMillis());

        //查询要更新的用户是否真的存在
        User updateUser = userDao.selectById(user.getId());
        if (updateUser == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(UserI18n.USER_NOT_EXIST));
        }

        int result = userDao.updateById(user);
        //更新用户的有效期，有效期存在为null的情况
        int updateTime = userDao.updateUserValidityTime(user);
        if (result != 1 || updateTime != 1) {
            throw new FilinkUserException(I18nUtils.getString(UserI18n.UPDATE_FAIL));
        }
        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     * 删除多个用户，不能自己删除自己，不能删除admin，如果删除在线用户需要确认操作
     * @param deleteUser 要删除的用户id数组
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteUser(Parameters deleteUser) {

        if (deleteUser == null || ArrayUtils.isEmpty(deleteUser.getFirstArrayParamter())) {
            return ResultUtils.warn(UserConst.DELETE_USER_IS_NOT_NULL,
                    I18nUtils.getString(UserI18n.DELETE_USER_IS_NOT_NULL));
        }

        //不能自己删除自己
        String currentUserId = RequestInfoUtils.getUserId();
        for(String userId : deleteUser.getFirstArrayParamter()){
            if(currentUserId.equals(userId)){
                return ResultUtils.warn(UserConst.DOESNT_DELETE_MINE,
                        I18nUtils.getString(UserI18n.DOESNT_DELETE_MINE));
            }
        }

        List<User> deleteUserList = userDao.selectBatchIds(Arrays.asList(deleteUser.getFirstArrayParamter()));
        final boolean[] deleteFlag = {false};
        //如果没有状态位删除用户，则判断当前用户是否在线
        if(!deleteUser.isFlag()) {
            deleteUserList.forEach(user -> {
                Set<String> keys = RedisUtils.keys(user.getId() + UserConst.REDIS_WILDCARD);
                if(keys != null && keys.size() > 0) {
                    deleteFlag[0] = true;
                }
            });
        }

        //如果删除自己，不能删除admin，删除在线用户需要二次点确认
        boolean contains = Arrays.asList(deleteUser.getFirstArrayParamter()).contains(UserConst.ADMIN_ID);
        if (deleteFlag[0] && !deleteUser.isFlag()) {
            return ResultUtils.warn(UserConst.ONLINE_USER, I18nUtils.getString(UserI18n.ONLINE_USER));
        } else if (contains) {
            return ResultUtils.warn(UserConst.NOT_ALLOW_DELETE_ADMIN,
                    I18nUtils.getString(UserI18n.NOT_ALLOW_DELETE_ADMIN));
        } else {
            addLogByUsers(deleteUserList, UserConst.DELETE_USER_LOG);
            websocketSendMessage(UserConst.DELETE_USER_WEBSOCKET,
                    UserConst.DELETE_USER_WEBSOCKET,deleteUser.getFirstArrayParamter());

            //删除用户信息
            Integer deleteNum = userDao.deleteUser(deleteUser.getFirstArrayParamter(), UserConst.USER_DELETED_CODE);
            if (!deleteNum.equals(deleteUser.getFirstArrayParamter().length)) {
                throw new FilinkUserException(I18nUtils.getString(UserI18n.DELETE_USER_FAIL));
            }

            //需要从redis中删除用户信息
            for (String id : deleteUser.getFirstArrayParamter()) {
                Set<String> keys = RedisUtils.keys(id + UserConst.REDIS_WILDCARD);
                if(keys != null && keys.size() > 0){
                    keys.forEach(token ->{
                        RedisUtils.remove(token);
                    });
                }
            }
            //删除用户的时候要一起删除配置信息
            boolean deleteDeviceFlag = deviceMapConfigFeign.
                    deletedConfigByUserIds(Arrays.asList(deleteUser.getFirstArrayParamter()));

            return ResultUtils.success(ResultCode.SUCCESS);
        }
    }

    /**
     * 根据用户名查询用户信息，将用户信息存储到redis中
     * @param userName  用户名
     * @param token     用户token
     * @param loginIp   用户登录ip
     * @return
     */
    @Override
    public User queryUserByNmae(String userName, String token,String loginIp) {
        log.info("登录::::根据用户名查询用户");
        User getUser = userDao.queryUserByNmae(userName);

        //如果有token值则为查询用户信息，没有token则是查询用户密码
        if (token != null) {
            //将登录时间修改成上次登录时间
            log.info("loginIp = " + loginIp);
            Long loginTime = getUser.getLoginTime();
            String lastLoginIp = getUser.getLoginIp();
            loginIp = loginIp.replace("-",".");
            getUser.setLoginIp(loginIp);

            if (loginTime != null) {
                getUser.setLastLoginTime(loginTime);
            }
            if(lastLoginIp != null){
                getUser.setLastLoginIp(lastLoginIp);
            }
            //设置当前时间为用户的登录时间
            getUser.setLoginTime(System.currentTimeMillis());
            getUser.setLoginSourse("1");
            //用户的登录时间ip等信息更新到用户表中
            userDao.updateById(getUser);

            //则添加用户信息到redis中,存储方式(userid_token,userinfo,过期时间)
            log.info("userId_token = " + getUser.getId() + UserConst.REDIS_SPLIT + token);
            RedisUtils.set(getUser.getId() + UserConst.REDIS_SPLIT + token,getUser,UserConst.EXPIRE_TIME);
        }
        return getUser;
    }


    /**
     * 更新用户状态
     * @param userStatus 用户的状态
     * @param userIdArray   用户的id数组
     * @return  修改结果状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateUserStatus(int userStatus, String[] userIdArray) {

        Integer updateNum = userDao.updateUserStatus(userStatus, userIdArray);
        if (!updateNum.equals(userIdArray.length)) {
            throw new FilinkUserException(I18nUtils.getString(UserI18n.UPDATE_FAIL));
        }
        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     * 条件查询用户信息，适合单表查询
     * @param userQueryCondition 查询条件
     * @return  用户信息列表
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

        Page page = MpQueryHelper.MyBatiesBuildPage(userQueryCondition);
        EntityWrapper entityWrapper = MpQueryHelper.MyBatiesBuildQuery(userQueryCondition);

        List<User> userList = userDao.selectPage(page, entityWrapper);

        return userList;
    }

    /**
     * 修改用户密码,其中新密码，旧密码，确认密码不能为空，新密码和旧密码不能相同
     * @param pwd   用户修改的密码信息
     * @return  修改密码的结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result modifyPWD(PasswordDto pwd) {

        //校验输入的密码
        Result checkResult = checkPWD(pwd);
        if(checkResult != null){
            return checkResult;
        }
        User updateUser = userDao.queryUserById(pwd.getUserId());
        if (updateUser == null) {
            return ResultUtils.warn(UserConst.USER_NOT_EXIST,
                    I18nUtils.getString(UserI18n.NEW_OLD_PASSWORD_EQUALS));
        }

        if (!updateUser.getPassword().equals(pwd.getOldPWD())) {
            return ResultUtils.warn(UserConst.OLDPWD_WRONG,
                    I18nUtils.getString(UserI18n.OLDPWD_WRONG));
        }

        universalLog(pwd.getUserId(), UserConst.MODIFY_USER_LOG, LogConstants.LOG_TYPE_SECURITY);

        Integer modifyNum = userDao.modifyPWD(pwd);
        if (modifyNum <= 0) {
            throw new FilinkUserException(I18nUtils.getString(UserI18n.UPDATE_PWD_FAIL));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(UserI18n.UPDATE_PWD_SUCCESS));
    }

    /**
     * 对输入的密码进行判断
     * @param pwd   输入的密码数据
     * @return  判断的结果
     */
    public Result checkPWD(PasswordDto pwd){
        if(pwd == null){
            return ResultUtils.warn(UserConst.NEWPWD_IS_NOT_NULL, I18nUtils.getString(UserI18n.NEWPWD_IS_NOT_NULL));
        }
        if (StringUtils.isEmpty(pwd.getNewPWD())) {
            return ResultUtils.warn(UserConst.NEWPWD_IS_NOT_NULL,
                    I18nUtils.getString(UserI18n.NEWPWD_IS_NOT_NULL));
        } else if (StringUtils.isEmpty(pwd.getOldPWD())) {
            return ResultUtils.warn(UserConst.OLDPWD_IS_NOT_NULL,
                    I18nUtils.getString(UserI18n.OLDPWD_IS_NOT_NULL));
        } else if (StringUtils.isEmpty(pwd.getConfirmPWD())) {
            return ResultUtils.warn(UserConst.CONFIRMPWD_IS_NOT_NULL,
                    I18nUtils.getString(UserI18n.CONFIRMPWD_IS_NOT_NULL));
        } else if (pwd.getOldPWD().equals(pwd.getNewPWD())) {
            return ResultUtils.warn(UserConst.NEW_OLD_PASSWORD_EQUALS,
                    I18nUtils.getString(UserI18n.NEW_OLD_PASSWORD_EQUALS));
        }
        return null;
    }

    /**
     * 重置用户密码
     * @param pwd 用户输入的密码信息
     * @return  重置密码结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result resetPWD(PasswordDto pwd) {

        //添加重置密码的日志
        universalLog(pwd.getUserId(), UserConst.RESET_PWD_MODEL, LogConstants.LOG_TYPE_SECURITY);
        // TODO: 2019/3/6  后期可以查询系统设置的新密码
        //设置默认新密码
        pwd.setNewPWD(UserConst.USER_RESET_NEW_PWD);
        //重置密码
        Integer modifyNum = userDao.modifyPWD(pwd);

        if (modifyNum <= 0) {
            throw new FilinkUserException(I18nUtils.getString(UserI18n.UPDATE_PWD_FAIL));
        }
        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     * 获取在线用户信息
     * @param queryCondition 参数列表
     * @return   用户信息列表
     */
    @Override
    public Result getOnLineUser(QueryCondition<OnlineParameter> queryCondition) {

        //获取参数信息和分页信息
        OnlineParameter onlineParameter = queryCondition.getBizCondition();
        if(onlineParameter == null){
            onlineParameter = new OnlineParameter();
        }
        Page page = MyBatiesBuildPage(queryCondition);

        //获取所有没有被删除的用户信息
        List<User> users = userDao.queryAllUser();
        List<String> onlineToken = onlineUserDao.queryAllOnlineUser();
        List<OnlineUser> loginList = new ArrayList<>();

        //对所有用户遍历
        if(users != null && users.size() > 0){
            users.forEach(user ->{
                //模糊查询缓存，查询哪些用户有登录的信息
                Set<String> keys = RedisUtils.keys(user.getId() + UserConst.REDIS_WILDCARD);
                //对缓存中在线用户和数据库在线用户进行筛选判断
                if(keys != null && keys.size() > 0){
                    keys.forEach(keyToken ->{
                        //登录用户在redis存储的key为用户id和登录token组成，长度大于32位,并且keytoekn中含有_分隔符
                        if(keyToken.length() > UserConst.UUID_LENGTH && keyToken.indexOf(UserConst.REDIS_SPLIT) > 0) {
                            if (onlineToken.contains(keyToken)) {
                                onlineToken.remove(keyToken);
                            } else {
                                //后来又登录的用户信息
                                OnlineUser onlineUser = new OnlineUser();
                                Object userObject = RedisUtils.get(keyToken);

                                //如果userobject为用户类型则进行强转，如果不是就跳过该条数据
                                if(userObject instanceof User){
                                    User loginUser = (User) userObject;

                                    userToOnlineUser(keyToken, loginUser, onlineUser);
                                    loginList.add(onlineUser);
                                }
                            }
                        }
                    });
                }
            });

            //将已经下线的用户给删除
            if(onlineToken != null && onlineToken.size() > 0) {
                onlineUserDao.deleteBatchIds(onlineToken);
            }
            //添加后面重新上线的用户
            if(loginList.size() > 0) {
                onlineUserDao.batchAddOnlineUser(loginList);
            }

            //根据条件分页查询用户信息
            onlineParameter.setStartNum((queryCondition.getPageCondition().getPageNum() - 1) *
                    queryCondition.getPageCondition().getPageSize());
            onlineParameter.setPage(queryCondition.getPageCondition().getPageNum());
            onlineParameter.setPageSize(queryCondition.getPageCondition().getPageSize());

            List<OnlineUser> onlineUsers = onlineUserDao.queryOnlineUserList(onlineParameter);
            Long number = onlineUserDao.queryOnlineUserNumber(onlineParameter);

            PageBean pageBean = MyBatiesBuildPageBean(page, number.intValue(), onlineUsers);
            return ResultUtils.success(pageBean);
        }
        return ResultUtils.success(null);
    }

    /**
     * 获取当前登录用户信息
     * @param userId    用户id
     * @param token     用户token
     * @return  用户信息
     */
    @Override
    public User queryCurrentUser(String userId, String token) {
        //从缓存中获取当前登录用户的详细信息
        if(RedisUtils.hasKey(userId + UserConst.REDIS_SPLIT + token)) {
            return (User) RedisUtils.get(userId + UserConst.REDIS_SPLIT + token);
        }

        return null;
    }

    /**
     * key为token  value为用户id
     * @param userMap 用户id
     * @return  强制下线的结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result forceOffline(Map<String,String> userMap) {

        //不能强制自己下线，自己下线走注销流程
        String currentUserId = RequestInfoUtils.getUserId();
        String currentToken = RequestInfoUtils.getToken();
        Set<String> idSet = userMap.keySet();
        if(idSet.contains(currentUserId + UserConst.REDIS_SPLIT + currentToken)){
            return ResultUtils.warn(UserConst.DOESNT_FORCE_MINE_OFFLINE,
                    I18nUtils.getString(UserI18n.DOESNT_FORCE_MINE_OFFLINE));
        }

        //websocket发送强制下线的消息
        WebSocketMessage webSocketMessage = new WebSocketMessage();
        webSocketMessage.setChannelId(UserConst.FORCEOFFLINE_CHANNEL_ID);
        webSocketMessage.setChannelKey(UserConst.FORCEOFFLINE_CHANNEL_KEY);
        webSocketMessage.setMsg(userMap);

        //因为存在多用户模式，所有使用群发
        webSocketMessage.setMsgType(10);
        Message<WebSocketMessage> message = MessageBuilder.withPayload(webSocketMessage).build();
        userStream.webSocketoutput().send(message);

        //强制用户下线的时候，从redis中将用户的登录信息清空
        List<User> userList = new ArrayList<>();
        if(userMap != null) {
            Set<String> keys = userMap.keySet();
            keys.forEach(token ->{
                User loginUser = (User)RedisUtils.get(token);
                userList.add(loginUser);
                RedisUtils.remove(token);
            });
        }

        addLogByUsers(userList,UserConst.FORCE_USER_OFFLINE_MODEL);

        return ResultUtils.success(ResultCode.SUCCESS,I18nUtils.getString(UserI18n.USER_HAS_FORCE_OFFLINE));
    }

    /**
     * 用户登出
     * @param userId 用户id
     * @param token 用户唯一token标识
     * @return  用户登出的结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result logout(String userId, String token) {

        universalLog(userId, UserConst.USER_LOGOUT_LOG, LogConstants.LOG_TYPE_SECURITY);
        log.info("userId + UserConst.REDIS_SPLIT + token = " + userId + UserConst.REDIS_SPLIT + token);

        RedisUtils.remove(userId + UserConst.REDIS_SPLIT + token);
        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     * 查询用户默认密码
     * @return  用户默认密码
     */
    @Override
    public Result queryUserDefaultPWD() {

        String pwd = UserConst.USER_RESET_NEW_PWD;
        return ResultUtils.success(ResultCode.SUCCESS, null, pwd);
    }

    /**
     * 分页条件查询用户信息
     * @param queryCondition  用户列表进行条件查询的条件类
     * @return  用户信息列表
     */
    @Override
    public Result queryUserByFieldAndCondition(QueryCondition<UserParameter> queryCondition) {

        //获取参数信息和分页信息
        UserParameter userParameter = queryCondition.getBizCondition();
        if(userParameter == null){
            userParameter = new UserParameter();
        }
        Page page = MyBatiesBuildPage(queryCondition);

        userParameter.setStartNum((queryCondition.getPageCondition().getPageNum() - 1) *
                queryCondition.getPageCondition().getPageSize());
        userParameter.setPage(queryCondition.getPageCondition().getPageNum());
        userParameter.setPageSize(queryCondition.getPageCondition().getPageSize());

        List<User> userList = userDao.queryUserByField(userParameter);
        Long userNumber = userDao.queryUserNum(userParameter);

        PageBean pageBean = MyBatiesBuildPageBean(page, userNumber.intValue(), userList);
        return ResultUtils.success(pageBean);
    }

    /**
     * 根据部门id查询用户信息
     * @param parameters   部门参数
     * @return  用户信息列表
     */
    @Override
    public Result queryUserByDept(Parameters parameters) {

        List<User>  users = userDao.queryUserByDepts(parameters.getFirstArrayParamter());
        return ResultUtils.success(users);
    }

    /**
     * 查询是否有指定用户
     * @param userId 用户id
     * @return  是否拥有用户
     */
    @Override
    public boolean queryUserById(String userId) {

        User user = userDao.queryUserById(userId);
        if(user == null){
            return false;
        }

        return true;
    }

    /**
     * 更新用户登录的有效期
     * @param userId 用户id
     * @param token  登录用户的token
     * @return  更新用户登录时间的结果
     */
    @Override
    public boolean updateLoginTime(String userId, String token) {

        if(RedisUtils.hasKey(userId + UserConst.REDIS_SPLIT + token)){
            RedisUtils.expire(userId + UserConst.REDIS_SPLIT + token,UserConst.EXPIRE_TIME);
            return true;
        }

        return false;
    }

    /**
     * 删除用户信息的日志
     * @param userList 用户列表
     * @param model 模板信息
     */
    private void addLogByUsers(List<User> userList, String model) {

        List<AddLogBean> addLogBeanList = new ArrayList<AddLogBean>();
        if (userList.size() == 0) {
            return;
        }
        for (User user : userList) {
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
     * @param id  用户id
     * @param model   模板id
     * @param logType 日志类型
     */
    private void universalLog(String id, String model, String logType) {

        User user = userDao.selectById(id);
        //获取日志类型
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("id");
        //获得操作对象名称
        addLogBean.setFunctionCode(model);
        //获得操作对象id
        addLogBean.setOptObjId(id);

        addLogBean.setOptObj(user.getUserName());
        //操作为新增
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);

        //新增操作日志
        logProcess.addSecurityLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }


    /**
     * 用户信息转为在线用户信息
     * @param token  用户登录的token
     * @param user   用户信息
     * @param onlineUser  在线用户信息
     */
    public void userToOnlineUser(String token, User user, OnlineUser onlineUser) {
        onlineUser.setId(token);
        onlineUser.setUserId(user.getId());
        onlineUser.setAddress(user.getAddress());
        onlineUser.setDeptName(user.getDepartment().getDeptName());
        onlineUser.setEmail(user.getEmail());
        onlineUser.setLoginIp(user.getLoginIp());
        onlineUser.setLoginSource(user.getLoginSourse());
        onlineUser.setPhoneNumber(user.getPhonenumber());
        onlineUser.setRoleName(user.getRole().getRoleName());
        onlineUser.setLoginTime(user.getLoginTime());
        onlineUser.setUserCode(user.getUserCode());
        onlineUser.setUserName(user.getUserName());
        onlineUser.setUserNickname(user.getUserNickname());
    }


    /**
     * websocket发送消息
     * @param channelId 通道id
     * @param channelKey 通道key
     * @param message 消息体
     */
    public void websocketSendMessage(String channelId,String channelKey,Object message){
        WebSocketMessage webSocketMessage = new WebSocketMessage();
        webSocketMessage.setChannelId(channelId);
        webSocketMessage.setChannelKey(channelKey);
        webSocketMessage.setMsg(message);
        webSocketMessage.setMsgType(10);

        Message<WebSocketMessage> sendMessage = MessageBuilder.withPayload(webSocketMessage).build();
        userStream.webSocketoutput().send(sendMessage);
    }
}
