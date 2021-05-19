package com.fiberhome.filink.userserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.AliYunPushConstant;
import com.fiberhome.filink.bean.AliYunPushMsgBean;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.filinklockapi.bean.Lock;
import com.fiberhome.filink.filinklockapi.feign.LockFeign;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.parameter.api.ParameterFeign;
import com.fiberhome.filink.parameter.bean.AliAccessKey;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.smsapi.bean.AliyunPush;
import com.fiberhome.filink.smsapi.bean.Android;
import com.fiberhome.filink.smsapi.bean.Ios;
import com.fiberhome.filink.smsapi.send.aliyun.AliyunMobilePush;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userserver.bean.AuthDevice;
import com.fiberhome.filink.userserver.bean.RoleDeviceType;
import com.fiberhome.filink.userserver.bean.TempAuth;
import com.fiberhome.filink.userserver.bean.TempAuthParameter;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.constant.FunctionCodeConstant;
import com.fiberhome.filink.userserver.constant.UserConstant;
import com.fiberhome.filink.userserver.constant.UserResultCode;
import com.fiberhome.filink.userserver.constant.UserI18n;
import com.fiberhome.filink.userserver.dao.AuthDeviceDao;
import com.fiberhome.filink.userserver.dao.RoleDao;
import com.fiberhome.filink.userserver.dao.TempauthDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.exception.FilinkTempauthException;
import com.fiberhome.filink.userserver.service.TempAuthService;
import com.fiberhome.filink.userserver.service.UserService;
import com.fiberhome.filink.userserver.service.UserStream;
import com.fiberhome.filink.userserver.utils.CheckEmptyUtils;
import com.fiberhome.filink.userserver.utils.UUIDUtil;
import com.fiberhome.filink.userserver.utils.WebSocketUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPageBean;


/**
 * <p>
 * 临时授权表 服务实现类
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-03-25
 */
@Service
public class TempAuthServiceImpl extends ServiceImpl<TempauthDao, TempAuth> implements TempAuthService {

    @Autowired
    private TempauthDao tempauthDao;

    @Autowired
    private LockFeign lockFeign;

    @Autowired
    private AuthDeviceDao authDeviceDao;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserStream userStream;

    @Autowired
    private AreaFeign areaFeign;

    @Autowired
    private UserService userService;

    @Autowired
    private ParameterFeign parameterFeign;

    @Autowired
    private AliyunMobilePush aliyunMobilePush;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 条件查询临时授权列表
     *
     * @param queryCondition 临时授权参数实体类
     * @return 临时授权列表
     */
    @Override
    public Result queryTempAuthByCondition(QueryCondition<TempAuthParameter> queryCondition) {

        String userId = RequestInfoUtils.getUserId();
        String token = RequestInfoUtils.getToken();

        //获取参数信息和分页信息
        TempAuthParameter tempAuthParameter = queryCondition.getBizCondition();
        if (!UserConstant.ADMIN_ID.equals(userId)) {
            User user = userService.queryCurrentUser(userId, token);
            List<String> deptList = new ArrayList<>();
            deptList.add(user.getDeptId());
            List<String> areaIds = areaFeign.selectAreaIdsByDeptIds(deptList);
            List<RoleDeviceType> roleDevicetypeList = user.getRole().getRoleDevicetypeList();
            if (CheckEmptyUtils.collectEmpty(roleDevicetypeList)) {
                List<String> roleDeviceIdList = roleDevicetypeList.stream().collect(ArrayList::new, (list, roleDevice) -> {
                    list.add(roleDevice.getDeviceTypeId());
                }, List::addAll);
                tempAuthParameter.setRoleDeviceIdList(roleDeviceIdList);

                if (!CheckEmptyUtils.collectEmpty(roleDeviceIdList) || !CheckEmptyUtils.collectEmpty(areaIds)) {
                    Page page = myBatiesBuildPage(queryCondition);
                    List<TempAuth> nullTempAuths = new ArrayList<>();
                    PageBean pageBean = myBatiesBuildPageBean(page, nullTempAuths.size(), nullTempAuths);
                    return ResultUtils.success(pageBean);
                }
            }
            tempAuthParameter.setAreaIdList(areaIds);
        }
        Page page = myBatiesBuildPage(queryCondition);

        tempAuthParameter.setStartNum((queryCondition.getPageCondition().getPageNum() - 1)
                * queryCondition.getPageCondition().getPageSize());
        tempAuthParameter.setPage(queryCondition.getPageCondition().getPageNum());
        tempAuthParameter.setPageSize(queryCondition.getPageCondition().getPageSize());

        //查询总页数和数据内容
        List<TempAuth> tempAuthList = tempauthDao.queryTempAuthByCondition(tempAuthParameter);
        Long tempAuthNumber = tempauthDao.queryTempAuthNumberByCondition(tempAuthParameter);

        PageBean pageBean = myBatiesBuildPageBean(page, tempAuthNumber.intValue(), tempAuthList);
        return ResultUtils.success(pageBean);
    }

    /**
     * 单个审核临时授权信息
     *
     * @param tempauth 临时授权信息
     * @return 审核的结果
     */
    @Override
    public Result audingTempAuthById(TempAuth tempauth) {

        //审核的用户不能为空
        if (tempauth == null) {
            return ResultUtils.warn(UserResultCode.TEMPAUTH_INFO_NOT_NULL,
                    I18nUtils.getSystemString(UserI18n.TEMPAUTH_INFO_NOT_NULL));
        }

        //被审核的临时授权信息没被删除
        TempAuth audintTempauth = tempauthDao.queryTempAuthById(tempauth.getId());
        if (audintTempauth == null) {
            return ResultUtils.warn(UserResultCode.TEMPAUTH_INFO_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.TEMPAUTH_INFO_HAS_DELETED));
        }

        //将当前时间设置为审核时间
        tempauth.setAuditingTime(System.currentTimeMillis());
        //当前用户为审核人，为授权人
        String currentUserId = RequestInfoUtils.getUserId();
        tempauth.setAuthUserId(currentUserId);

        Integer integer = tempauthDao.modifyTempAuthStatus(tempauth);
        if (integer != 1) {
            throw new FilinkTempauthException(I18nUtils.getSystemString(UserI18n.TEMPAUTH_OPER_ERROR));
        }

        List<String> userIdList = new ArrayList<>();
        userIdList.add(tempauth.getUserId());

        AliYunPushMsgBean aliYunPushMsgBean = new AliYunPushMsgBean();
        String auditResult = I18nUtils.getSystemString(UserConstant.AUDIT_RESULT) + (tempauth.getAuthStatus() == 1
                ? I18nUtils.getSystemString(UserConstant.AUDIT_RESULT_NO_PASS) : I18nUtils.getSystemString(UserConstant.AUDIT_RESULT_PASS));
        aliYunPushMsgBean.setType(AliYunPushConstant.AUDIT_TEMP_AUTH);
        aliYunPushMsgBean.setData(auditResult);
        //给用户推送手机信息
        sendAliYunMessageToUser(
                userIdList, I18nUtils.getSystemString(UserI18n.AUDIT_INFORMATION), aliYunPushMsgBean);

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.AUDITING_TEMPAUTH_SUCCESS));
    }

    /**
     * 批量审核临时授权信息
     *
     * @param idList     待审核临时授权信息id
     * @param tempAuth   临时授权信息
     * @param userIdList 推送消息的用户
     * @return 审核结果
     */
    @Override
    public Result batchAudingTempAuthByIds(String[] idList, TempAuth tempAuth, List<String> userIdList) {

        //审核的用户不能为空
        if (idList == null) {
            return ResultUtils.warn(UserResultCode.TEMPAUTH_INFO_NOT_NULL,
                    I18nUtils.getSystemString(UserI18n.TEMPAUTH_INFO_NOT_NULL));
        }

        //被审核的临时授权信息没被删除
        List<TempAuth> tempAuths = tempauthDao.batchQueryTempAuthByIds(idList);
        if (tempAuths == null || tempAuths.size() != idList.length) {
            return ResultUtils.warn(UserResultCode.TEMPAUTH_INFO_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.TEMPAUTH_INFO_HAS_DELETED));
        }

        //将当前时间设置为审核时间
        long currentTime = System.currentTimeMillis();
        String currentUserId = RequestInfoUtils.getUserId();

        tempAuth.setAuditingTime(currentTime);
        tempAuth.setAuthUserId(currentUserId);

        Integer integer = tempauthDao.batchModifyTempAuthStatus(idList, tempAuth);
        if (integer != idList.length) {
            throw new FilinkTempauthException(I18nUtils.getSystemString(UserI18n.TEMPAUTH_OPER_ERROR));
        }

        AliYunPushMsgBean aliYunPushMsgBean = new AliYunPushMsgBean();
        String auditResult = I18nUtils.getSystemString(UserConstant.AUDIT_RESULT) + (tempAuth.getAuthStatus() == 1
                ? I18nUtils.getSystemString(UserConstant.AUDIT_RESULT_NO_PASS) : I18nUtils.getSystemString(UserConstant.AUDIT_RESULT_PASS));
        aliYunPushMsgBean.setData(auditResult);
        aliYunPushMsgBean.setType(AliYunPushConstant.AUDIT_TEMP_AUTH);
        //给用户推送手机信息
        sendAliYunMessageToUser(
                userIdList, I18nUtils.getSystemString(UserI18n.AUDIT_INFORMATION), aliYunPushMsgBean);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.AUDITING_TEMPAUTH_SUCCESS));
    }

    /**
     * 删除单个临时授权信息
     *
     * @param id 临时授权id
     * @return 删除的个数
     */
    @Override
    public Result deleteTempAuthById(String id) {

        //如果传入的id为空
        if (id == null) {
            return ResultUtils.warn(UserResultCode.TEMPAUTH_INFO_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.TEMPAUTH_INFO_HAS_DELETED));
        }

        //查询当前临时授权信息有没有被删除
        TempAuth deleteTempAuth = tempauthDao.queryTempAuthById(id);
        if (deleteTempAuth == null) {
            return ResultUtils.warn(UserResultCode.TEMPAUTH_INFO_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.TEMPAUTH_INFO_HAS_DELETED));
        }
        Long authExpirationTime = deleteTempAuth.getAuthExpirationTime();

        //被删除的授权信息必须被禁用
        Integer authStatus = deleteTempAuth.getAuthStatus();
        if (UserConstant.TEMP_AUTH_START_STATUS.equals(authStatus) && authExpirationTime > System.currentTimeMillis()) {
            return ResultUtils.warn(UserResultCode.TEMP_AUTH_IS_ENABLED_STATE,
                    I18nUtils.getSystemString(UserI18n.TEMP_AUTH_IS_ENABLED_STATE));
        }

        Integer integer = tempauthDao.deleteTempAuthById(id);
        if (integer != 1) {
            throw new FilinkTempauthException(I18nUtils.getSystemString(UserI18n.TEMPAUTH_OPER_ERROR));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.DELETE_TEMPAUTH_SUCCESS));
    }

    /**
     * 批量删除临时授权信息
     *
     * @param ids 临时授权id数组
     * @return 删除的结果
     */
    @Override
    public Result batchDeleteTempAuth(String[] ids) {

        //传入的id数组为空
        if (ids == null) {
            return ResultUtils.warn(UserResultCode.TEMPAUTH_INFO_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.TEMPAUTH_INFO_HAS_DELETED));
        }

        //查询当前临时授权信息有没有被删除
        List<TempAuth> tempAuthList = tempauthDao.batchQueryTempAuthByIdArray(ids);
        if (tempAuthList == null || tempAuthList.size() != ids.length) {
            return ResultUtils.warn(UserResultCode.TEMPAUTH_INFO_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.TEMPAUTH_INFO_HAS_DELETED));
        }

        //判断当前待删除的授权信息是否为有效信息
        TempAuth tempauth = tempAuthList.stream().filter(auth ->
                UserConstant.TEMP_AUTH_START_STATUS.equals(auth.getAuthStatus()) && auth.getAuthExpirationTime() > System.currentTimeMillis()
        ).findFirst().orElse(null);

        //如果存在未过期的数据，则不能删除未过期的授权信息
        if (tempauth != null) {
            return ResultUtils.warn(UserResultCode.TEMP_AUTH_IS_ENABLED_STATE,
                    I18nUtils.getSystemString(UserI18n.TEMP_AUTH_IS_ENABLED_STATE));
        }

        Integer integer = tempauthDao.batchDeleteUnifyAuth(ids);
        if (integer != ids.length) {
            throw new FilinkTempauthException(I18nUtils.getSystemString(UserI18n.TEMPAUTH_OPER_ERROR));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.DELETE_TEMPAUTH_SUCCESS));
    }

    /**
     * 添加临时授权信息
     *
     * @param tempauth 临时授权信息
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_ADD, logType = LogConstants.LOG_TYPE_SECURITY,
            functionCode = FunctionCodeConstant.ADD_TEMP_AUTH_FUNCTION_CODE, dataGetColumnName = "name", dataGetColumnId = "id")
    public Result addTempAuth(TempAuth tempauth) {
        systemLanguageUtil.querySystemLanguage();
        String deviceId = null;
        Long currentTime = System.currentTimeMillis();
        Long effectiveTime = tempauth.getAuthEffectiveTime();
        Long expireTime = tempauth.getAuthExpirationTime();

        //校验临时授权信息是都有填写
        if (tempauth == null || tempauth.getName() == null) {
            return ResultUtils.warn(UserResultCode.WRITE_FULL_UNIFYAUTH_INFO,
                    I18nUtils.getSystemString(UserI18n.WRITE_FULL_UNIFYAUTH_INFO));
        }

        //生效和失效时间必须同时写或者同时不写
        if (effectiveTime != null && expireTime == null) {
            return ResultUtils.warn(UserResultCode.PERFECT_TIME_INFORMATION,
                    I18nUtils.getSystemString(UserI18n.PERFECT_TIME_INFORMATION));
        }
        //失效时间必须大于当前时间
        if (expireTime != null && expireTime < currentTime) {
            return ResultUtils.warn(UserResultCode.EXPIRETIME_MUST_GT_CURRENTTIME,
                    I18nUtils.getSystemString(UserI18n.EXPIRETIME_MUST_GT_CURRENTTIME));
        }

        //获取当前登录用户信息
        String currentUserId = RequestInfoUtils.getUserId();

        //设置授权用户
        tempauth.setId(UUIDUtil.getInstance().UUID32());
        tempauth.setUserId(currentUserId);
        tempauth.setCreateUser(currentUserId);
        tempauth.setCreateTime(currentTime);
        tempauth.setApplyTime(currentTime);
        tempauth.setIsDeleted(UserConstant.TEMPAUTH_DEFAULT_DELETED);
        //如果生效时间不为null并且申请时间大于生效时间
        if (effectiveTime != null && currentTime > effectiveTime) {
            tempauth.setAuthEffectiveTime(currentTime);
        }
        //添加统一授权信息
        Integer addTempAuthNumber = tempauthDao.insert(tempauth);

        //临时授权需要的设施信息，一种为设施id和门id，一种为二维码
        List<AuthDevice> authDeviceList = tempauth.getAuthDeviceList();
        List<AuthDevice> authDevices = new ArrayList<>();
        Integer addNum = 0;
        if (CheckEmptyUtils.collectEmpty(authDeviceList)) {
            authDeviceList.forEach(authDevice -> {
                if (StringUtils.isNotEmpty(authDevice.getQrCode())) {
                    Lock lock = lockFeign.queryLockByQrCode(authDevice.getQrCode());
                    if (lock != null) {
                        authDevice.setDeviceId(lock.getDeviceId());
                        authDevice.setDoorId(lock.getDoorNum());
                    }
                }
                authDevice.setId(UUIDUtil.getInstance().UUID32());
                authDevice.setAuthId(tempauth.getId());
                authDevice.setCreateUser(currentUserId);

                //根据设施id获取设施的信息
                DeviceInfoDto deviceInfo = deviceFeign.getDeviceById(authDevice.getDeviceId());
                authDevice.setDeviceType(deviceInfo.getDeviceType());
                authDevice.setAreaId(deviceInfo.getAreaInfo().getAreaId());
                authDevice.setCreateTime(Instant.now().toEpochMilli());

                authDevices.add(authDevice);
            });
            deviceId = authDevices.get(UserConstant.FIRST_NUMBER_INDEX).getDeviceId();
            addNum = authDeviceDao.batchAuthDevice(authDevices);
        }
        if (addTempAuthNumber != UserConstant.ADD_ONE_INFO || addNum != authDevices.size()) {
            throw new FilinkTempauthException(I18nUtils.getSystemString(UserI18n.TEMPAUTH_OPER_ERROR));
        }

        //给能够查看指定的设施，并且有审核临时授权的权限
        if (StringUtils.isNotEmpty(deviceId)) {
            sendMessageToUser(deviceId);
        }

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.ADD_TEMP_AUTH_SUCCESS));
    }

    /**
     * 给审核人员推送消息
     */
    private void sendMessageToUser(String deviceId) {

        DeviceInfoDto deviceInfoDto = deviceFeign.getDeviceById(deviceId);
        String deviceType = deviceInfoDto.getDeviceType();
        Set<String> deptSet = deviceInfoDto.getAreaInfo().getAccountabilityUnit();

        //角色id列表
        List<String> roleIdList = roleDao.
                queryRoleByPermissionAndDeviceType(UserConstant.TEMP_AUTH_AUD_PERMISSION, deviceType);
        //部门列表
        List<String> deptIdList = new ArrayList<>(deptSet);
        //获取所有符合要求的人员信息
        List<String> userIdList = new ArrayList<>();
        if (CheckEmptyUtils.collectEmpty(roleIdList) && CheckEmptyUtils.collectEmpty(deptIdList)) {
            List<User> userList = userDao.queryUserByRoleAndDepartment(roleIdList, deptIdList);
            if (CheckEmptyUtils.collectEmpty(userList)) {
                userIdList = userList.stream().collect(ArrayList::new, (list, user) -> {
                    list.add(user.getId());
                }, List::addAll);
            }
        }
        //在推送的人员中添加admin用户
        if (!userIdList.contains(UserConstant.ADMIN_ID)) {
            userIdList.add(UserConstant.ADMIN_ID);
        }
        WebSocketUtils.websocketSendMessage(userStream, UserConstant.AUDIT_TEMP_AUTH,
                UserConstant.TEMP_AUTH_CHANNEL_KEY, userIdList, UserConstant.SEND_MANY);
    }

    /**
     * 根据id查询临时授权信息
     *
     * @param id 临时授权id
     * @return 临时授权信息
     */
    @Override
    public Result queryTempAuthById(String id) {

        TempAuth tempauth = tempauthDao.queryTempAuthById(id);
        return ResultUtils.success(tempauth);
    }

    private void sendAliYunMessageToUser(List<String> userIdList, String title, AliYunPushMsgBean aliYunPushMsgBean) {
        userIdList.forEach(userId -> {
            Set<String> keyToken = RedisUtils.keys(UserConstant.USER_PREFIX + userId + UserConstant.REDIS_WILDCARD);
            if (CheckEmptyUtils.collectEmpty(keyToken)) {

                Map<String, String> androidKeyPushId = new HashMap<>();
                Map<String, String> iosKeyPushId = new HashMap<>();

                keyToken.forEach(key -> {
                    User user = (User) RedisUtils.get(key);
                    if (StringUtils.isNotEmpty(user.getAppKey())) {
                        String appKey = user.getAppKey();
                        String androidPushId = androidKeyPushId.get(appKey);
                        String iosPushId = iosKeyPushId.get(appKey);

                        if (UserConstant.ANDROID_TYPE.equals(user.getPhoneType())) {
                            if (StringUtils.isNotEmpty(androidPushId)) {
                                androidPushId += UserConstant.PUSH_SPLIT + user.getPushId();
                            } else {
                                androidPushId = user.getPushId();
                            }
                            androidKeyPushId.put(appKey, androidPushId);
                        } else {
                            if (StringUtils.isNotEmpty(iosPushId)) {
                                iosPushId += UserConstant.PUSH_SPLIT + user.getPushId();
                            } else {
                                iosPushId = user.getPushId();
                            }
                            iosKeyPushId.put(appKey, iosPushId);
                        }
                    }
                });
                //发送android通知
                if (androidKeyPushId.size() > 0) {
                    androidKeyPushId.keySet().forEach(key -> {
                        AliyunPush aliyunPush = sendAliYunMessage(UserConstant.ANDROID_TYPE,
                                key, androidKeyPushId.get(key), title, JSON.toJSONString(aliYunPushMsgBean));
                        try {
                            aliyunMobilePush.sendSmsAndEmail(aliyunPush);
                        } catch (ClientException e) {
                            e.printStackTrace();
                        }
                    });
                }
                //发送ios通知
                if (iosKeyPushId.size() > 0) {
                    iosKeyPushId.keySet().forEach(key -> {
                        AliyunPush aliyunPush = sendAliYunMessage(UserConstant.IOS_TYPE,
                                key, iosKeyPushId.get(key), title, aliYunPushMsgBean.getData().toString());
                        try {
                            aliyunMobilePush.sendSmsAndEmail(aliyunPush);
                        } catch (ClientException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    /**
     * 返回阿里云推送信息
     *
     * @param appKey 手机登录的appkey
     * @param pushId 手机设备id
     * @param title  推送消息头
     * @param body   推送消息体
     * @return
     */
    private AliyunPush sendAliYunMessage(Integer phoneType, String appKey, String pushId, String title, String body) {
        AliyunPush aliyunPush = new AliyunPush();
        //获取accessKey 和 accessKeySecret
        AliAccessKey aliAccessKey = parameterFeign.queryMobilePush();
        if (ObjectUtils.isEmpty(aliAccessKey)) {
            return null;
        }
        aliyunPush.setAccessKeyId(aliAccessKey.getAccessKeyId());
        aliyunPush.setAccessKeySecret(aliAccessKey.getAccessKeySecret());
        //根据设施类型推送
        aliyunPush.setTarget(UserConstant.PUSH_TARGET);

        //设置安卓参数
        Android android = new Android();
        aliyunPush.setAndroid(android);

        //设置ios参数
        Ios ios = new Ios();
        ios.setSubtitle("");

        Map<String, Object> iosMap = new HashMap<>(UserConstant.MAP_INIT_VALUE);
        iosMap.put(UserConstant.PUSH_EXTRAS, body);
        ios.setExtParameters(JSON.toJSONString(iosMap));
        aliyunPush.setIos(ios);


        //appKey
        aliyunPush.setAppKey(Long.parseLong(appKey));
        //标题
        aliyunPush.setTitle(title);
        //推送的消息信息
        aliyunPush.setBody(body);
        //推送的编号
        aliyunPush.setIds(pushId);

        if (UserConstant.ANDROID_TYPE.equals(phoneType)) {
            aliyunPush.setPushType(UserConstant.PUSH_TYPE);
        }
        return aliyunPush;
    }
}
