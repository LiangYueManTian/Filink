package com.fiberhome.filink.userserver.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userserver.bean.AuthDevice;
import com.fiberhome.filink.userserver.bean.AuthInfo;
import com.fiberhome.filink.userserver.bean.DeviceInfo;
import com.fiberhome.filink.userserver.bean.Tempauth;
import com.fiberhome.filink.userserver.bean.Unifyauth;
import com.fiberhome.filink.userserver.bean.UnifyAuthParameter;
import com.fiberhome.filink.userserver.bean.UserAuthInfo;
import com.fiberhome.filink.userserver.consts.UserConst;
import com.fiberhome.filink.userserver.consts.UserI18n;
import com.fiberhome.filink.userserver.dao.AuthDeviceDao;
import com.fiberhome.filink.userserver.dao.TempauthDao;
import com.fiberhome.filink.userserver.dao.UnifyauthDao;
import com.fiberhome.filink.userserver.exception.FilinkTempauthException;
import com.fiberhome.filink.userserver.exception.FilinkUnifyauthException;
import com.fiberhome.filink.userserver.service.UnifyauthService;
import com.fiberhome.filink.userserver.utils.CheckEmptyUtils;
import com.fiberhome.filink.userserver.utils.NameUtils;
import com.fiberhome.filink.userserver.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fiberhome.filink.server_common.utils.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.server_common.utils.MpQueryHelper.myBatiesBuildPageBean;

/**
 * <p>
 * ???????????? ???????????????
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-03-25
 */
@Service
public class UnifyAuthServiceImpl extends ServiceImpl<UnifyauthDao, Unifyauth> implements UnifyauthService {

    @Autowired
    private UnifyauthDao unifyauthDao;

    @Autowired
    private TempauthDao tempauthDao;

    @Autowired
    private AuthDeviceDao authDeviceDao;

    @Autowired
    private DeviceFeign deviceFeign;

    /**
     * ????????????????????????
     *
     * @param unifyauth ????????????????????????
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = "add", logType = LogConstants.LOG_TYPE_SECURITY,
            functionCode = "1505101", dataGetColumnName = "name", dataGetColumnId = "id")
    public Result addUnifyAuth(Unifyauth unifyauth) {

        Long currentTime = System.currentTimeMillis();
        Long effectiveTime = unifyauth.getAuthEffectiveTime();
        Long expireTime = unifyauth.getAuthExpirationTime();

        //???????????????????????????????????????
        if (unifyauth == null || unifyauth.getName() == null) {
            return ResultUtils.warn(UserConst.WRITE_FULL_UNIFYAUTH_INFO,
                    I18nUtils.getString(UserI18n.WRITE_FULL_UNIFYAUTH_INFO));
        }

        //??????????????????????????????????????????????????????
        if ((effectiveTime == null && expireTime != null) || (effectiveTime != null && expireTime == null)) {
            return ResultUtils.warn(UserConst.PERFECT_TIME_INFORMATION,
                    I18nUtils.getString(UserI18n.PERFECT_TIME_INFORMATION));
        }

        //????????????????????????????????????
        if (expireTime != null && expireTime < currentTime) {
            return ResultUtils.warn(UserConst.EXPIRETIME_MUST_GT_CURRENTTIME,
                    I18nUtils.getString(UserI18n.EXPIRETIME_MUST_GT_CURRENTTIME));
        }

        //??????????????????????????????
        String currentUserId = RequestInfoUtils.getUserId();

        //??????????????????
        unifyauth.setId(UUIDUtil.getInstance().UUID32());
        unifyauth.setAuthUserId(currentUserId);
        unifyauth.setCreateUser(currentUserId);
        unifyauth.setCreateTime(System.currentTimeMillis());
        unifyauth.setIsDeleted(UserConst.UNIFYAUTH_DEFAULT_DELETED);

        //????????????????????????????????????
        unifyauth.setName(NameUtils.removeBlank(unifyauth.getName()));
        unifyauth.setRemark(NameUtils.removeBlank(unifyauth.getRemark()));
        //????????????????????????
        Integer insert = unifyauthDao.insert(unifyauth);
        //???????????????????????????
        List<AuthDevice> authDevices = new ArrayList<>();
        List<AuthDevice> authDeviceList = unifyauth.getAuthDeviceList();
        Integer addNum = 0;
        if (CheckEmptyUtils.collectEmpty(authDeviceList)) {
            authDeviceList.forEach(authDevice -> {
                authDevice.setId(UUIDUtil.getInstance().UUID32());
                authDevice.setAuthId(unifyauth.getId());
                authDevice.setCreateUser(currentUserId);
                authDevice.setCreateTime(System.currentTimeMillis());
                authDevices.add(authDevice);
            });
            //??????????????????????????????
            addNum = authDeviceDao.batchAuthDevice(authDevices);
        }
        if (insert != UserConst.ADD_ONE_INFO || addNum != authDevices.size()) {
            throw new FilinkUnifyauthException(I18nUtils.getString(UserI18n.UNIFYAUTH_OPER_ERROR));
        }

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(UserI18n.ADD_UNIFYAUTH_SUCCESS));
    }

    /**
     * ??????????????????????????????
     *
     * @param unifyAuthCondition ????????????????????????
     * @return ????????????????????????
     */
    @Override
    public Result queryUnifyAuthByCondition(QueryCondition<UnifyAuthParameter> unifyAuthCondition) {

        //?????????????????????????????????
        UnifyAuthParameter unifyAuthParameter = unifyAuthCondition.getBizCondition();
        Page page = myBatiesBuildPage(unifyAuthCondition);

        unifyAuthParameter.setStartNum((unifyAuthCondition.getPageCondition().getPageNum() - 1)
                * unifyAuthCondition.getPageCondition().getPageSize());
        unifyAuthParameter.setPage(unifyAuthCondition.getPageCondition().getPageNum());
        unifyAuthParameter.setPageSize(unifyAuthCondition.getPageCondition().getPageSize());
        if (!UserConst.ADMIN_ID.equals(RequestInfoUtils.getUserId())) {
            unifyAuthParameter.setCurrentUserId(RequestInfoUtils.getUserId());
        }

        //??????????????????????????????
        List<Unifyauth> unifyauthList = unifyauthDao.queryUnifyAuthByCondition(unifyAuthParameter);
        Long unifyAuthNumber = unifyauthDao.queryUnifyAuthNumberByCondition(unifyAuthParameter);

        PageBean pageBean = myBatiesBuildPageBean(page, unifyAuthNumber.intValue(), unifyauthList);
        return ResultUtils.success(pageBean);
    }

    /**
     * ????????????????????????
     *
     * @param unifyauth ?????????????????????
     * @return ???????????????
     */
    @Override
    public Result modifyUnifyAuth(Unifyauth unifyauth) {

        Long currentTime = System.currentTimeMillis();
        Long effectiveTime = unifyauth.getAuthEffectiveTime();
        Long expireTime = unifyauth.getAuthExpirationTime();
        String userId = RequestInfoUtils.getUserId();

        //???????????????????????????????????????
        if (unifyauth == null || unifyauth.getName() == null) {
            return ResultUtils.warn(UserConst.WRITE_FULL_UNIFYAUTH_INFO,
                    I18nUtils.getString(UserI18n.WRITE_FULL_UNIFYAUTH_INFO));
        }

        //??????????????????????????????????????????????????????
        if ((effectiveTime == null && expireTime != null) || (effectiveTime != null && expireTime == null)) {
            return ResultUtils.warn(UserConst.PERFECT_TIME_INFORMATION,
                    I18nUtils.getString(UserI18n.PERFECT_TIME_INFORMATION));
        }

        //????????????????????????????????????
        if (expireTime != null && expireTime < currentTime) {
            return ResultUtils.warn(UserConst.EXPIRETIME_MUST_GT_CURRENTTIME,
                    I18nUtils.getString(UserI18n.EXPIRETIME_MUST_GT_CURRENTTIME));
        }


        //????????????????????????????????????????????????
        Unifyauth modiyUnifyAuth = unifyauthDao.queryUnifyAuthById(unifyauth.getId());
        if (modiyUnifyAuth == null) {
            return ResultUtils.warn(UserConst.UNIFYAUTH_HAS_DELETED,
                    I18nUtils.getString(UserI18n.UNIFYAUTH_HAS_DELETED));
        }

        //???????????????????????????????????????
        unifyauth.setName(NameUtils.removeBlank(unifyauth.getName()));
        unifyauth.setRemark(NameUtils.removeBlank(unifyauth.getRemark()));
        //?????????????????????????????????
        unifyauth.setUpdateTime(System.currentTimeMillis());
        Integer integer = unifyauthDao.modifyUnifyAuth(unifyauth);
        if (integer != 1) {
            throw new FilinkUnifyauthException(I18nUtils.getString(UserI18n.UNIFYAUTH_OPER_ERROR));
        }

        //????????????????????????????????????
        authDeviceDao.batchDeleteByAuthId(unifyauth.getId());
        //??????????????????
        List<AuthDevice> authDevices = new ArrayList<>();
        List<AuthDevice> authDeviceList = unifyauth.getAuthDeviceList();
        Integer addNum = 0;
        if (CheckEmptyUtils.collectEmpty(authDeviceList)) {
            authDeviceList.forEach(authDevice -> {
                authDevice.setId(UUIDUtil.getInstance().UUID32());
                authDevice.setAuthId(unifyauth.getId());
                authDevice.setCreateTime(System.currentTimeMillis());
                authDevice.setCreateUser(userId);
                authDevices.add(authDevice);
            });

            //??????????????????????????????
            addNum = authDeviceDao.batchAuthDevice(authDevices);
            if (addNum != authDevices.size()) {
                throw new FilinkUnifyauthException(I18nUtils.getString(UserI18n.UNIFYAUTH_OPER_ERROR));
            }
        }

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(UserI18n.MODIFY_UNIFYAUTH_SUCCESS));
    }

    /**
     * ??????id??????????????????????????????
     *
     * @param id ???????????????id
     * @return ??????????????????
     */
    @Override
    public Result queryUnifyAuthById(String id) {

        Unifyauth unifyauth = unifyauthDao.queryUnifyAuthById(id);
        if (unifyauth == null) {
            return ResultUtils.warn(UserConst.DATA_IS_NULL);
        }
        return ResultUtils.success(unifyauth);
    }

    /**
     * ?????????????????????????????????
     *
     * @param id ????????????????????????id
     * @return ???????????????
     */
    @Override
    public Result deleteUnifyAuthById(String id) {

        //???????????????id??????
        if (id == null) {
            return ResultUtils.warn(UserConst.UNIFYAUTH_HAS_DELETED,
                    I18nUtils.getString(UserI18n.UNIFYAUTH_HAS_DELETED));
        }

        //????????????????????????????????????????????????
        Unifyauth modiyUnifyAuth = unifyauthDao.queryUnifyAuthById(id);
        if (modiyUnifyAuth == null) {
            return ResultUtils.warn(UserConst.UNIFYAUTH_HAS_DELETED,
                    I18nUtils.getString(UserI18n.UNIFYAUTH_HAS_DELETED));
        }

        //???????????????????????????????????????
        Integer authStatus = modiyUnifyAuth.getAuthStatus();
        if (UserConst.UNIFY_AUTH_START_STATUS.equals(authStatus)) {
            return ResultUtils.warn(UserConst.UNIFY_AUTH_IS_ENABLED_STATE,
                    I18nUtils.getString(UserI18n.UNIFY_AUTH_IS_ENABLED_STATE));
        }

        Integer integer = unifyauthDao.deleteUnifyAuthById(id);
        if (integer != UserConst.ADD_ONE_INFO) {
            throw new FilinkUnifyauthException(I18nUtils.getString(UserI18n.UNIFYAUTH_OPER_ERROR));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(UserI18n.DELETE_UNIFYAUTH_SUCCESS));
    }

    /**
     * ?????????????????????????????????
     *
     * @param ids ????????????????????????id
     * @return ???????????????
     */
    @Override
    public Result batchDeleteUnifyAuth(String[] ids) {

        //?????????id????????????
        if (ids == null) {
            return ResultUtils.warn(UserConst.UNIFYAUTH_HAS_DELETED,
                    I18nUtils.getString(UserI18n.UNIFYAUTH_HAS_DELETED));
        }

        //????????????????????????????????????????????????
        List<Unifyauth> unifyauthList = unifyauthDao.batchQueryUnifyAuthByIds(ids);
        if (unifyauthList == null || unifyauthList.size() != ids.length) {
            return ResultUtils.warn(UserConst.UNIFYAUTH_HAS_DELETED,
                    I18nUtils.getString(UserI18n.UNIFYAUTH_HAS_DELETED));
        }

        //??????????????????????????????????????????????????????
        Unifyauth firstUnifyauth = unifyauthList.stream().filter(auth ->
                UserConst.UNIFY_AUTH_START_STATUS.equals(auth.getAuthStatus())
        ).findFirst().orElse(null);

        //??????????????????????????????
        if (firstUnifyauth != null) {
            return ResultUtils.warn(UserConst.UNIFY_AUTH_IS_ENABLED_STATE,
                    I18nUtils.getString(UserI18n.UNIFY_AUTH_IS_ENABLED_STATE));
        }

        Integer integer = unifyauthDao.batchDeleteUnifyAuth(ids);
        if (integer != ids.length) {
            throw new FilinkUnifyauthException(I18nUtils.getString(UserI18n.UNIFYAUTH_OPER_ERROR));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(UserI18n.DELETE_UNIFYAUTH_SUCCESS));
    }

    /**
     * ?????????????????????????????????
     *
     * @param unifyAuthParameter ???????????????
     * @return
     */
    @Override
    public Result batchModifyUnifyAuthStatus(UnifyAuthParameter unifyAuthParameter) {

        if (unifyAuthParameter == null) {
            return ResultUtils.warn(UserConst.UNIFYAUTH_HAS_DELETED,
                    I18nUtils.getString(UserI18n.UNIFYAUTH_HAS_DELETED));
        }
        String[] ids = unifyAuthParameter.getIdArray();
        //????????????????????????????????????????????????
        List<Unifyauth> unifyauthList = unifyauthDao.batchQueryUnifyAuthByIds(ids);
        if (unifyauthList == null || unifyauthList.size() != ids.length) {
            return ResultUtils.warn(UserConst.UNIFYAUTH_HAS_DELETED,
                    I18nUtils.getString(UserI18n.UNIFYAUTH_HAS_DELETED));
        }

        Integer integer = unifyauthDao.batchModifyUnifyAuthStatus(ids, unifyAuthParameter.getAuthStatus());
        if (integer != ids.length) {
            throw new FilinkUnifyauthException(I18nUtils.getString(UserI18n.UNIFYAUTH_OPER_ERROR));
        }

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(UserI18n.MODIFY_UNIFYAUTHSTATUS_SUCCESS));
    }

    /**
     * ????????????????????????????????????
     *
     * @return
     */
    @Override
    public Result queryUserAuthInfoById() {

        String userId = RequestInfoUtils.getUserId();
        List<Unifyauth> unifyAuthList = unifyauthDao.queryUnifyAuthByUserId(userId);
        List<Tempauth> tempAuthList = tempauthDao.queryTempAuthByUserId(userId);
        //????????????id?????????????????????
        List<String> deviceIdList = new ArrayList<>();
        //????????????????????????????????????id
        if (CheckEmptyUtils.collectEmpty(unifyAuthList)) {
            unifyAuthList.forEach(unifyAuth -> {
                List<AuthDevice> authDeviceList = unifyAuth.getAuthDeviceList();
                deviceToList(authDeviceList, deviceIdList);
            });
        }

        //????????????????????????????????????id
        if (CheckEmptyUtils.collectEmpty(tempAuthList)) {
            tempAuthList.forEach(tempAuth -> {
                List<AuthDevice> authDeviceList = tempAuth.getAuthDeviceList();
                deviceToList(authDeviceList, deviceIdList);
            });
        }

        if (CheckEmptyUtils.collectEmpty(deviceIdList)) {
            List<DeviceInfoDto> deviceList = deviceFeign.
                    getDeviceByIds(deviceIdList.toArray(new String[deviceIdList.size()]));
            if(CheckEmptyUtils.collectEmpty(deviceList)){
                Map<String, String> deviceMap  = new HashMap<>();
                deviceList.forEach(device ->{
                    deviceMap.put(device.getDeviceId(),device.getDeviceName());
                });

                //????????????????????????????????????id
                if (CheckEmptyUtils.collectEmpty(unifyAuthList)) {
                    unifyAuthList.forEach(unifyAuth -> {
                        List<AuthDevice> authDeviceList = unifyAuth.getAuthDeviceList();
                        setDeviceName(authDeviceList,deviceMap);
                    });
                }

                //????????????????????????????????????id
                if (CheckEmptyUtils.collectEmpty(tempAuthList)) {
                    tempAuthList.forEach(tempAuth -> {
                        List<AuthDevice> authDeviceList = tempAuth.getAuthDeviceList();
                        deviceToList(authDeviceList, deviceIdList);
                    });
                }
            }
        }

        return ResultUtils.success(new AuthInfo(unifyAuthList, tempAuthList));
    }

    /**
     * ???????????????????????????????????????????????????
     * @param authDeviceList
     * @param deviceMap
     */
    private void setDeviceName(List<AuthDevice> authDeviceList, Map<String, String> deviceMap) {

        if (CheckEmptyUtils.collectEmpty(authDeviceList)) {
            authDeviceList.forEach(authDevice -> {
                authDevice.setDeviceName(deviceMap.get(authDevice.getDeviceId()));
            });
        }
    }


    /**
     * ???????????????????????????id?????????list?????????
     *
     * @param authDeviceList ????????????????????????
     * @param deviceIdList   ??????id??????
     */
    private void deviceToList(List<AuthDevice> authDeviceList, List<String> deviceIdList) {

        if (CheckEmptyUtils.collectEmpty(authDeviceList)) {
            authDeviceList.forEach(authDevice -> {
                String id = authDevice.getDeviceId();
                deviceIdList.add(id);
            });
        }
    }

    /**
     * ????????????id?????????????????????id??????????????????
     *
     * @param userAUthInfo ??????????????????????????????
     * @return
     */
    @Override
    public AuthInfo queryAuthInfoByUserIdAndDeviceAndDoor(UserAuthInfo userAUthInfo) {

        List<Unifyauth> unifyauths = unifyauthDao.queryAuthInfoByUserIdAndDeviceAndDoor(userAUthInfo);
        List<Tempauth> tempauths = tempauthDao.queryAuthInfoByUserIdAndDeviceAndDoor(userAUthInfo);

        return new AuthInfo(unifyauths, tempauths);
    }

    /**
     * ????????????????????????????????????
     *
     * @param deviceInfo ????????????
     * @return ???????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteAuthByDevice(DeviceInfo deviceInfo) {

        List<Unifyauth> unifyauths = unifyauthDao.queryUnifyAuthByDevice(deviceInfo);
        List<Tempauth> tempauths = tempauthDao.queryTempAuthByDevice(deviceInfo);
        List<String> unifyAuthIdList = new ArrayList<>();
        List<String> tempAuthIdList = new ArrayList<>();
        List<String> authDeviceIdList = new ArrayList<>();
        Integer allDeleteNum = 0;
        //??????????????????
        if (CheckEmptyUtils.collectEmpty(unifyauths)) {
            unifyauths.forEach(unifyauth -> {
                unifyAuthIdList.add(unifyauth.getId());
                List<AuthDevice> authDevices = unifyauth.getAuthDeviceList();
                if (CheckEmptyUtils.collectEmpty(authDevices)) {
                    authDevices.forEach(authDevice -> {
                        authDeviceIdList.add(authDevice.getId());
                    });
                }
            });
            Integer deleteNum = unifyauthDao.batchDeleteUnifyAuth(unifyAuthIdList.toArray(new String[unifyAuthIdList.size()]));
            if (deleteNum != unifyauths.size()) {
                throw new FilinkUnifyauthException(I18nUtils.getString(UserI18n.UNIFYAUTH_OPER_ERROR));
            }
            allDeleteNum += deleteNum;
        }


        //??????????????????
        if (CheckEmptyUtils.collectEmpty(tempauths)) {
            tempauths.forEach(tempauth -> {
                List<AuthDevice> authDevices = tempauth.getAuthDeviceList();
                if (CheckEmptyUtils.collectEmpty(authDevices)) {
                    authDevices.forEach(authDevice -> {
                        authDeviceIdList.add(authDevice.getId());
                    });
                }
                tempAuthIdList.add(tempauth.getId());
            });

            Integer deleteTempAuthNum = tempauthDao.batchDeleteUnifyAuth(tempAuthIdList.toArray(new String[tempAuthIdList.size()]));
            if (deleteTempAuthNum != tempauths.size()) {
                throw new FilinkTempauthException(I18nUtils.getString(UserI18n.TEMPAUTH_OPER_ERROR));
            }
            allDeleteNum += deleteTempAuthNum;
        }


        //?????????????????????
        if (authDeviceIdList.size() > 0) {

            Integer authDeviceNum = authDeviceDao.batchDeleteAuthDevice(authDeviceIdList);
            if (authDeviceNum != authDeviceIdList.size()) {
                throw new FilinkTempauthException(I18nUtils.getString(UserI18n.TEMPAUTH_OPER_ERROR));
            }
            allDeleteNum += authDeviceNum;
        }

        return allDeleteNum;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param unifyauth ??????????????????
     * @return ????????????????????????
     */
    @Override
    public Result queryAuthByName(Unifyauth unifyauth) {

        String name = unifyauth.getName();
        List<Unifyauth> unifyauths = unifyauthDao.queryAuthByName(name);
        return ResultUtils.success(unifyauths);
    }
}
