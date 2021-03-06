package com.fiberhome.filink.userserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.AliYunPushConstant;
import com.fiberhome.filink.bean.AliYunPushMsgBean;
import com.fiberhome.filink.bean.FiLinkTimeUtils;
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
import com.fiberhome.filink.userserver.consts.UserConst;
import com.fiberhome.filink.userserver.consts.UserI18n;
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
 * ??????????????? ???????????????
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
     * ??????????????????????????????
     *
     * @param queryCondition ???????????????????????????
     * @return ??????????????????
     */
    @Override
    public Result queryTempAuthByCondition(QueryCondition<TempAuthParameter> queryCondition) {

        String userId = RequestInfoUtils.getUserId();
        String token = RequestInfoUtils.getToken();

        //?????????????????????????????????
        TempAuthParameter tempAuthParameter = queryCondition.getBizCondition();
        if (!UserConst.ADMIN_ID.equals(userId)) {
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

        //??????????????????????????????
        List<TempAuth> tempAuthList = tempauthDao.queryTempAuthByCondition(tempAuthParameter);
        Long tempAuthNumber = tempauthDao.queryTempAuthNumberByCondition(tempAuthParameter);

        PageBean pageBean = myBatiesBuildPageBean(page, tempAuthNumber.intValue(), tempAuthList);
        return ResultUtils.success(pageBean);
    }

    /**
     * ??????????????????????????????
     *
     * @param tempauth ??????????????????
     * @return ???????????????
     */
    @Override
    public Result audingTempAuthById(TempAuth tempauth) {

        //???????????????????????????
        if (tempauth == null) {
            return ResultUtils.warn(UserConst.TEMPAUTH_INFO_NOT_NULL,
                    I18nUtils.getSystemString(UserI18n.TEMPAUTH_INFO_NOT_NULL));
        }

        //??????????????????????????????????????????
        TempAuth audintTempauth = tempauthDao.queryTempAuthById(tempauth.getId());
        if (audintTempauth == null) {
            return ResultUtils.warn(UserConst.TEMPAUTH_INFO_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.TEMPAUTH_INFO_HAS_DELETED));
        }

        //????????????????????????????????????
        tempauth.setAuditingTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        //???????????????????????????????????????
        String currentUserId = RequestInfoUtils.getUserId();
        tempauth.setAuthUserId(currentUserId);

        Integer integer = tempauthDao.modifyTempAuthStatus(tempauth);
        if (integer != 1) {
            throw new FilinkTempauthException(I18nUtils.getSystemString(UserI18n.TEMPAUTH_OPER_ERROR));
        }

        List<String> userIdList = new ArrayList<>();
        userIdList.add(tempauth.getUserId());

        AliYunPushMsgBean aliYunPushMsgBean = new AliYunPushMsgBean();
        String auditResult = I18nUtils.getSystemString(UserConst.AUDIT_RESULT) + (tempauth.getAuthStatus() == 1
                ? I18nUtils.getSystemString(UserConst.AUDIT_RESULT_NO_PASS) : I18nUtils.getSystemString(UserConst.AUDIT_RESULT_PASS));
        aliYunPushMsgBean.setType(AliYunPushConstant.AUDIT_TEMP_AUTH);
        aliYunPushMsgBean.setData(auditResult);
        //???????????????????????????
        sendAliYunMessageToUser(
                userIdList, I18nUtils.getSystemString(UserI18n.AUDIT_INFORMATION), aliYunPushMsgBean);

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.AUDITING_TEMPAUTH_SUCCESS));
    }

    /**
     * ??????????????????????????????
     *
     * @param idList     ???????????????????????????id
     * @param tempAuth   ??????????????????
     * @param userIdList ?????????????????????
     * @return ????????????
     */
    @Override
    public Result batchAudingTempAuthByIds(String[] idList, TempAuth tempAuth, List<String> userIdList) {

        //???????????????????????????
        if (idList == null) {
            return ResultUtils.warn(UserConst.TEMPAUTH_INFO_NOT_NULL,
                    I18nUtils.getSystemString(UserI18n.TEMPAUTH_INFO_NOT_NULL));
        }

        //??????????????????????????????????????????
        List<TempAuth> tempAuths = tempauthDao.batchQueryTempAuthByIds(idList);
        if (tempAuths == null || tempAuths.size() != idList.length) {
            return ResultUtils.warn(UserConst.TEMPAUTH_INFO_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.TEMPAUTH_INFO_HAS_DELETED));
        }

        //????????????????????????????????????
        long currentTime = FiLinkTimeUtils.getUtcZeroTimeStamp();
        String currentUserId = RequestInfoUtils.getUserId();

        tempAuth.setAuditingTime(currentTime);
        tempAuth.setAuthUserId(currentUserId);

        Integer integer = tempauthDao.batchModifyTempAuthStatus(idList, tempAuth);
        if (integer != idList.length) {
            throw new FilinkTempauthException(I18nUtils.getSystemString(UserI18n.TEMPAUTH_OPER_ERROR));
        }

        AliYunPushMsgBean aliYunPushMsgBean = new AliYunPushMsgBean();
        String auditResult = I18nUtils.getSystemString(UserConst.AUDIT_RESULT) + (tempAuth.getAuthStatus() == 1
                ? I18nUtils.getSystemString(UserConst.AUDIT_RESULT_NO_PASS) : I18nUtils.getSystemString(UserConst.AUDIT_RESULT_PASS));
        aliYunPushMsgBean.setData(auditResult);
        aliYunPushMsgBean.setType(AliYunPushConstant.AUDIT_TEMP_AUTH);
        //???????????????????????????
        sendAliYunMessageToUser(
                userIdList, I18nUtils.getSystemString(UserI18n.AUDIT_INFORMATION), aliYunPushMsgBean);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.AUDITING_TEMPAUTH_SUCCESS));
    }

    /**
     * ??????????????????????????????
     *
     * @param id ????????????id
     * @return ???????????????
     */
    @Override
    public Result deleteTempAuthById(String id) {

        //???????????????id??????
        if (id == null) {
            return ResultUtils.warn(UserConst.TEMPAUTH_INFO_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.TEMPAUTH_INFO_HAS_DELETED));
        }

        //????????????????????????????????????????????????
        TempAuth deleteTempAuth = tempauthDao.queryTempAuthById(id);
        if (deleteTempAuth == null) {
            return ResultUtils.warn(UserConst.TEMPAUTH_INFO_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.TEMPAUTH_INFO_HAS_DELETED));
        }

        //???????????????????????????????????????
        Integer authStatus = deleteTempAuth.getAuthStatus();
        if (UserConst.TEMP_AUTH_START_STATUS.equals(authStatus)) {
            return ResultUtils.warn(UserConst.TEMP_AUTH_IS_ENABLED_STATE,
                    I18nUtils.getSystemString(UserI18n.TEMP_AUTH_IS_ENABLED_STATE));
        }

        Integer integer = tempauthDao.deleteTempAuthById(id);
        if (integer != 1) {
            throw new FilinkTempauthException(I18nUtils.getSystemString(UserI18n.TEMPAUTH_OPER_ERROR));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.DELETE_TEMPAUTH_SUCCESS));
    }

    /**
     * ??????????????????????????????
     *
     * @param ids ????????????id??????
     * @return ???????????????
     */
    @Override
    public Result batchDeleteTempAuth(String[] ids) {

        //?????????id????????????
        if (ids == null) {
            return ResultUtils.warn(UserConst.TEMPAUTH_INFO_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.TEMPAUTH_INFO_HAS_DELETED));
        }

        //????????????????????????????????????????????????
        List<TempAuth> tempAuthList = tempauthDao.batchQueryTempAuthByIdArray(ids);
        if (tempAuthList == null || tempAuthList.size() != ids.length) {
            return ResultUtils.warn(UserConst.TEMPAUTH_INFO_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.TEMPAUTH_INFO_HAS_DELETED));
        }

        //?????????????????????????????????????????????????????????
        TempAuth tempauth = tempAuthList.stream().filter(auth ->
                UserConst.TEMP_AUTH_START_STATUS.equals(auth.getAuthStatus())
        ).findFirst().orElse(null);

        //????????????????????????????????????????????????????????????????????????
        if (tempauth != null) {
            return ResultUtils.warn(UserConst.TEMP_AUTH_IS_ENABLED_STATE,
                    I18nUtils.getSystemString(UserI18n.TEMP_AUTH_IS_ENABLED_STATE));
        }

        Integer integer = tempauthDao.batchDeleteUnifyAuth(ids);
        if (integer != ids.length) {
            throw new FilinkTempauthException(I18nUtils.getSystemString(UserI18n.TEMPAUTH_OPER_ERROR));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.DELETE_TEMPAUTH_SUCCESS));
    }

    /**
     * ????????????????????????
     *
     * @param tempauth ??????????????????
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = "add", logType = LogConstants.LOG_TYPE_SECURITY,
            functionCode = "1506101", dataGetColumnName = "name", dataGetColumnId = "id")
    public Result addTempAuth(TempAuth tempauth) {
        systemLanguageUtil.querySystemLanguage();
        String deviceId = null;
        Long currentTime = FiLinkTimeUtils.getUtcZeroTimeStamp();
        Long effectiveTime = tempauth.getAuthEffectiveTime();
        Long expireTime = tempauth.getAuthExpirationTime();

        //???????????????????????????????????????
        if (tempauth == null || tempauth.getName() == null) {
            return ResultUtils.warn(UserConst.WRITE_FULL_UNIFYAUTH_INFO,
                    I18nUtils.getSystemString(UserI18n.WRITE_FULL_UNIFYAUTH_INFO));
        }

        //??????????????????????????????????????????????????????
        if (effectiveTime != null && expireTime == null) {
            return ResultUtils.warn(UserConst.PERFECT_TIME_INFORMATION,
                    I18nUtils.getSystemString(UserI18n.PERFECT_TIME_INFORMATION));
        }
        //????????????????????????????????????
        if (expireTime != null && expireTime < currentTime) {
            return ResultUtils.warn(UserConst.EXPIRETIME_MUST_GT_CURRENTTIME,
                    I18nUtils.getSystemString(UserI18n.EXPIRETIME_MUST_GT_CURRENTTIME));
        }

        //??????????????????????????????
        String currentUserId = RequestInfoUtils.getUserId();

        //??????????????????
        tempauth.setId(UUIDUtil.getInstance().UUID32());
        tempauth.setUserId(currentUserId);
        tempauth.setCreateUser(currentUserId);
        tempauth.setCreateTime(currentTime);
        tempauth.setApplyTime(currentTime);
        tempauth.setIsDeleted(UserConst.TEMPAUTH_DEFAULT_DELETED);
        //????????????????????????null????????????????????????????????????
        if (effectiveTime != null && currentTime > effectiveTime) {
            tempauth.setAuthEffectiveTime(currentTime);
        }
        //????????????????????????
        Integer addTempAuthNumber = tempauthDao.insert(tempauth);

        //???????????????????????????????????????????????????id??????id?????????????????????
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

                //????????????id?????????????????????
                DeviceInfoDto deviceInfo = deviceFeign.getDeviceById(authDevice.getDeviceId());
                authDevice.setDeviceType(deviceInfo.getDeviceType());
                authDevice.setAreaId(deviceInfo.getAreaInfo().getAreaId());
                authDevice.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());

                authDevices.add(authDevice);
            });
            deviceId = authDevices.get(UserConst.FIRST_NUMBER_INDEX).getDeviceId();
            addNum = authDeviceDao.batchAuthDevice(authDevices);
        }
        if (addTempAuthNumber != UserConst.ADD_ONE_INFO || addNum != authDevices.size()) {
            throw new FilinkTempauthException(I18nUtils.getSystemString(UserI18n.TEMPAUTH_OPER_ERROR));
        }

        //?????????????????????????????????????????????????????????????????????
        if (StringUtils.isNotEmpty(deviceId)) {
            sendMessageToUser(deviceId);
        }

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.ADD_TEMP_AUTH_SUCCESS));
    }


    /**
     * ???????????????????????????
     */
    private void sendMessageToUser(String deviceId) {

        DeviceInfoDto deviceInfoDto = deviceFeign.getDeviceById(deviceId);
        String deviceType = deviceInfoDto.getDeviceType();
        Set<String> deptSet = deviceInfoDto.getAreaInfo().getAccountabilityUnit();

        //??????id??????
        List<String> roleIdList = roleDao.
                queryRoleByPermissionAndDeviceType(UserConst.TEMP_AUTH_AUD_PERMISSION, deviceType);
        //????????????
        List<String> deptIdList = new ArrayList<>(deptSet);
        //???????????????????????????????????????
        List<String> userIdList = new ArrayList<>();
        if (CheckEmptyUtils.collectEmpty(roleIdList) && CheckEmptyUtils.collectEmpty(deptIdList)) {
            List<User> userList = userDao.queryUserByRoleAndDepartment(roleIdList, deptIdList);
            if (CheckEmptyUtils.collectEmpty(userList)) {
                userIdList = userList.stream().collect(ArrayList::new, (list, user) -> {
                    list.add(user.getId());
                }, List::addAll);
            }
        }
        //???????????????????????????admin??????
        if (!userIdList.contains(UserConst.ADMIN_ID)) {
            userIdList.add(UserConst.ADMIN_ID);
        }
        WebSocketUtils.websocketSendMessage(userStream, UserConst.AUDIT_TEMP_AUTH,
                UserConst.TEMP_AUTH_CHANNEL_KEY, userIdList, UserConst.SEND_MANY);
    }

    /**
     * ??????id????????????????????????
     *
     * @param id ????????????id
     * @return ??????????????????
     */
    @Override
    public Result queryTempAuthById(String id) {

        TempAuth tempauth = tempauthDao.queryTempAuthById(id);
        return ResultUtils.success(tempauth);
    }

    private void sendAliYunMessageToUser(List<String> userIdList, String title, AliYunPushMsgBean aliYunPushMsgBean) {
        userIdList.forEach(userId -> {
            Set<String> keyToken = RedisUtils.keys(UserConst.USER_PREFIX + userId + UserConst.REDIS_WILDCARD);
            if (CheckEmptyUtils.collectEmpty(keyToken)) {

                Map<String, String> androidKeyPushId = new HashMap<>();
                Map<String, String> iosKeyPushId = new HashMap<>();

                keyToken.forEach(key -> {
                    User user = (User) RedisUtils.get(key);
                    if (StringUtils.isNotEmpty(user.getAppKey())) {
                        String appKey = user.getAppKey();
                        String androidPushId = androidKeyPushId.get(appKey);
                        String iosPushId = iosKeyPushId.get(appKey);

                        if (UserConst.ANDROID_TYPE.equals(user.getPhoneType())) {
                            if (StringUtils.isNotEmpty(androidPushId)) {
                                androidPushId += UserConst.PUSH_SPLIT + user.getPushId();
                            } else {
                                androidPushId = user.getPushId();
                            }
                            androidKeyPushId.put(appKey, androidPushId);
                        } else {
                            if (StringUtils.isNotEmpty(iosPushId)) {
                                iosPushId += UserConst.PUSH_SPLIT + user.getPushId();
                            } else {
                                iosPushId = user.getPushId();
                            }
                            iosKeyPushId.put(appKey, iosPushId);
                        }
                    }
                });
                //??????android??????
                if (androidKeyPushId.size() > 0) {
                    androidKeyPushId.keySet().forEach(key -> {
                        AliyunPush aliyunPush = sendAliYunMessage(UserConst.ANDROID_TYPE,
                                key, androidKeyPushId.get(key), title, JSON.toJSONString(aliYunPushMsgBean));
                        try {
                            aliyunMobilePush.sendSmsAndEmail(aliyunPush);
                        } catch (ClientException e) {
                            e.printStackTrace();
                        }
                    });
                }
                //??????ios??????
                if (iosKeyPushId.size() > 0) {
                    iosKeyPushId.keySet().forEach(key -> {
                        AliyunPush aliyunPush = sendAliYunMessage(UserConst.IOS_TYPE,
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
     * ???????????????????????????
     *
     * @param appKey ???????????????appkey
     * @param pushId ????????????id
     * @param title  ???????????????
     * @param body   ???????????????
     * @return
     */
    private AliyunPush sendAliYunMessage(Integer phoneType, String appKey, String pushId, String title, String body) {
        AliyunPush aliyunPush = new AliyunPush();
        //??????accessKey ??? accessKeySecret
        AliAccessKey aliAccessKey = parameterFeign.queryMobilePush();
        if (ObjectUtils.isEmpty(aliAccessKey)) {
            return null;
        }
        aliyunPush.setAccessKeyId(aliAccessKey.getAccessKeyId());
        aliyunPush.setAccessKeySecret(aliAccessKey.getAccessKeySecret());
        //????????????????????????
        aliyunPush.setTarget(UserConst.PUSH_TARGET);

        //??????????????????
        Android android = new Android();
        aliyunPush.setAndroid(android);

        //??????ios??????
        Ios ios = new Ios();
        ios.setSubtitle("");

        Map<String, Object> iosMap = new HashMap<>(UserConst.MAP_INIT_VALUE);
        iosMap.put(UserConst.PUSH_EXTRAS, body);
        ios.setExtParameters(JSON.toJSONString(iosMap));
        aliyunPush.setIos(ios);


        //appKey
        aliyunPush.setAppKey(Long.parseLong(appKey));
        //??????
        aliyunPush.setTitle(title);
        //?????????????????????
        aliyunPush.setBody(body);
        //???????????????
        aliyunPush.setIds(pushId);

        if (UserConst.ANDROID_TYPE.equals(phoneType)) {
            aliyunPush.setPushType(UserConst.PUSH_TYPE);
        }
        return aliyunPush;
    }
}
