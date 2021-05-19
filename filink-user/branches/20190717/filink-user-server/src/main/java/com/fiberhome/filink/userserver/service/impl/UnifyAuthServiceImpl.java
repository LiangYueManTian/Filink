package com.fiberhome.filink.userserver.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.FiLinkTimeUtils;
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
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userserver.bean.AuthDevice;
import com.fiberhome.filink.userserver.bean.AuthInfo;
import com.fiberhome.filink.userserver.bean.DeviceInfo;
import com.fiberhome.filink.userserver.bean.TempAuth;
import com.fiberhome.filink.userserver.bean.UnifyAuth;
import com.fiberhome.filink.userserver.bean.UnifyAuthParameter;
import com.fiberhome.filink.userserver.bean.UserAuthInfo;
import com.fiberhome.filink.userserver.consts.UserConst;
import com.fiberhome.filink.userserver.consts.UserI18n;
import com.fiberhome.filink.userserver.dao.AuthDeviceDao;
import com.fiberhome.filink.userserver.dao.TempauthDao;
import com.fiberhome.filink.userserver.dao.UnifyauthDao;
import com.fiberhome.filink.userserver.exception.FilinkTempauthException;
import com.fiberhome.filink.userserver.exception.FilinkUnifyAuthException;
import com.fiberhome.filink.userserver.service.UnifyAuthService;
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

import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPageBean;


/**
 * <p>
 * 统一授权 服务实现类
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-03-25
 */
@Service
public class UnifyAuthServiceImpl extends ServiceImpl<UnifyauthDao, UnifyAuth> implements UnifyAuthService {

    @Autowired
    private UnifyauthDao unifyauthDao;

    @Autowired
    private TempauthDao tempauthDao;

    @Autowired
    private AuthDeviceDao authDeviceDao;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    @Autowired
    private DeviceFeign deviceFeign;

    /**
     * 添加统一授权信息
     *
     * @param unifyauth 统一授权实体信息
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = "add", logType = LogConstants.LOG_TYPE_SECURITY,
            functionCode = "1505101", dataGetColumnName = "name", dataGetColumnId = "id")
    public Result addUnifyAuth(UnifyAuth unifyauth) {
        systemLanguageUtil.querySystemLanguage();
        Long currentTime = FiLinkTimeUtils.getUtcZeroTimeStamp();
        Long effectiveTime = unifyauth.getAuthEffectiveTime();
        Long expireTime = unifyauth.getAuthExpirationTime();

        //校验统一授权信息是都有填写
        if (unifyauth == null || unifyauth.getName() == null) {
            return ResultUtils.warn(UserConst.WRITE_FULL_UNIFYAUTH_INFO,
                    I18nUtils.getSystemString(UserI18n.WRITE_FULL_UNIFYAUTH_INFO));
        }

        //生效和失效时间必须同时写或者同时不写
        if ((effectiveTime == null && expireTime != null) || (effectiveTime != null && expireTime == null)) {
            return ResultUtils.warn(UserConst.PERFECT_TIME_INFORMATION,
                    I18nUtils.getSystemString(UserI18n.PERFECT_TIME_INFORMATION));
        }

        //失效时间必须大于当前时间
        if (expireTime != null && expireTime < currentTime) {
            return ResultUtils.warn(UserConst.EXPIRETIME_MUST_GT_CURRENTTIME,
                    I18nUtils.getSystemString(UserI18n.EXPIRETIME_MUST_GT_CURRENTTIME));
        }

        //获取当前登录用户信息
        String currentUserId = RequestInfoUtils.getUserId();

        //设置授权用户
        unifyauth.setId(UUIDUtil.getInstance().UUID32());
        unifyauth.setAuthUserId(currentUserId);
        unifyauth.setCreateUser(currentUserId);
        unifyauth.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        unifyauth.setIsDeleted(UserConst.UNIFYAUTH_DEFAULT_DELETED);

        //去除统一授权中的前后空格
        unifyauth.setName(NameUtils.removeBlank(unifyauth.getName()));
        unifyauth.setRemark(NameUtils.removeBlank(unifyauth.getRemark()));
        //添加统一授权信息
        Integer insert = unifyauthDao.insert(unifyauth);
        //添加授权的设施信息
        List<AuthDevice> authDevices = new ArrayList<>();
        List<AuthDevice> authDeviceList = unifyauth.getAuthDeviceList();
        Integer addNum = 0;
        if (CheckEmptyUtils.collectEmpty(authDeviceList)) {
            authDeviceList.forEach(authDevice -> {
                authDevice.setId(UUIDUtil.getInstance().UUID32());
                authDevice.setAuthId(unifyauth.getId());
                authDevice.setCreateUser(currentUserId);
                authDevice.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                authDevices.add(authDevice);
            });
            //批量添加设施和门信息
            addNum = authDeviceDao.batchAuthDevice(authDevices);
        }
        if (insert != UserConst.ADD_ONE_INFO || addNum != authDevices.size()) {
            throw new FilinkUnifyAuthException(I18nUtils.getSystemString(UserI18n.UNIFYAUTH_OPER_ERROR));
        }

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.ADD_UNIFYAUTH_SUCCESS));
    }

    /**
     * 调价查询统一授权信息
     *
     * @param unifyAuthCondition 统一授权条件信息
     * @return 统一授权列表信息
     */
    @Override
    public Result queryUnifyAuthByCondition(QueryCondition<UnifyAuthParameter> unifyAuthCondition) {

        //获取参数信息和分页信息
        UnifyAuthParameter unifyAuthParameter = unifyAuthCondition.getBizCondition();
        Page page = myBatiesBuildPage(unifyAuthCondition);

        unifyAuthParameter.setStartNum((unifyAuthCondition.getPageCondition().getPageNum() - 1)
                * unifyAuthCondition.getPageCondition().getPageSize());
        unifyAuthParameter.setPage(unifyAuthCondition.getPageCondition().getPageNum());
        unifyAuthParameter.setPageSize(unifyAuthCondition.getPageCondition().getPageSize());
        if (!UserConst.ADMIN_ID.equals(RequestInfoUtils.getUserId())) {
            unifyAuthParameter.setCurrentUserId(RequestInfoUtils.getUserId());
        }

        //查询总页数和数据内容
        List<UnifyAuth> unifyAuthList = unifyauthDao.queryUnifyAuthByCondition(unifyAuthParameter);
        Long unifyAuthNumber = unifyauthDao.queryUnifyAuthNumberByCondition(unifyAuthParameter);

        PageBean pageBean = myBatiesBuildPageBean(page, unifyAuthNumber.intValue(), unifyAuthList);
        return ResultUtils.success(pageBean);
    }

    /**
     * 更新统一授权信息
     *
     * @param unifyauth 授权信息实体类
     * @return 更新的结果
     */
    @Override
    public Result modifyUnifyAuth(UnifyAuth unifyauth) {

        Long currentTime =FiLinkTimeUtils.getUtcZeroTimeStamp();
        Long effectiveTime = unifyauth.getAuthEffectiveTime();
        Long expireTime = unifyauth.getAuthExpirationTime();
        String userId = RequestInfoUtils.getUserId();

        //校验统一授权信息是都有填写
        if (unifyauth == null || unifyauth.getName() == null) {
            return ResultUtils.warn(UserConst.WRITE_FULL_UNIFYAUTH_INFO,
                    I18nUtils.getSystemString(UserI18n.WRITE_FULL_UNIFYAUTH_INFO));
        }

        //生效和失效时间必须同时写或者同时不写
        if ((effectiveTime == null && expireTime != null) || (effectiveTime != null && expireTime == null)) {
            return ResultUtils.warn(UserConst.PERFECT_TIME_INFORMATION,
                    I18nUtils.getSystemString(UserI18n.PERFECT_TIME_INFORMATION));
        }

        //失效时间必须大于当前时间
        if (expireTime != null && expireTime < currentTime) {
            return ResultUtils.warn(UserConst.EXPIRETIME_MUST_GT_CURRENTTIME,
                    I18nUtils.getSystemString(UserI18n.EXPIRETIME_MUST_GT_CURRENTTIME));
        }


        //查询当前统一授权信息有没有被删除
        UnifyAuth modiyUnifyAuth = unifyauthDao.queryUnifyAuthById(unifyauth.getId());
        if (modiyUnifyAuth == null) {
            return ResultUtils.warn(UserConst.UNIFYAUTH_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.UNIFYAUTH_HAS_DELETED));
        }

        //去除统一授权信息的前后空格
        unifyauth.setName(NameUtils.removeBlank(unifyauth.getName()));
        unifyauth.setRemark(NameUtils.removeBlank(unifyauth.getRemark()));
        //设置统一授权的更新时间
        unifyauth.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        Integer integer = unifyauthDao.modifyUnifyAuth(unifyauth);
        if (integer != 1) {
            throw new FilinkUnifyAuthException(I18nUtils.getSystemString(UserI18n.UNIFYAUTH_OPER_ERROR));
        }

        //删除统一授权中的设施信息
        authDeviceDao.batchDeleteByAuthId(unifyauth.getId());
        //添加设施信息
        List<AuthDevice> authDevices = new ArrayList<>();
        List<AuthDevice> authDeviceList = unifyauth.getAuthDeviceList();
        Integer addNum = 0;
        if (CheckEmptyUtils.collectEmpty(authDeviceList)) {
            authDeviceList.forEach(authDevice -> {
                authDevice.setId(UUIDUtil.getInstance().UUID32());
                authDevice.setAuthId(unifyauth.getId());
                authDevice.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                authDevice.setCreateUser(userId);
                authDevices.add(authDevice);
            });

            //批量添加设施和门信息
            addNum = authDeviceDao.batchAuthDevice(authDevices);
            if (addNum != authDevices.size()) {
                throw new FilinkUnifyAuthException(I18nUtils.getSystemString(UserI18n.UNIFYAUTH_OPER_ERROR));
            }
        }

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.MODIFY_UNIFYAUTH_SUCCESS));
    }

    /**
     * 根据id查询统一授权的的信息
     *
     * @param id 统一授权的id
     * @return 统一授权信息
     */
    @Override
    public Result queryUnifyAuthById(String id) {

        UnifyAuth unifyauth = unifyauthDao.queryUnifyAuthById(id);
        if (unifyauth == null) {
            return ResultUtils.warn(UserConst.DATA_IS_NULL);
        }
        return ResultUtils.success(unifyauth);
    }

    /**
     * 删除指定的统一授权信息
     *
     * @param id 待删除逇统一授权id
     * @return 删除的结果
     */
    @Override
    public Result deleteUnifyAuthById(String id) {

        //如果传入的id为空
        if (id == null) {
            return ResultUtils.warn(UserConst.UNIFYAUTH_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.UNIFYAUTH_HAS_DELETED));
        }

        //查询当前统一授权信息有没有被删除
        UnifyAuth modiyUnifyAuth = unifyauthDao.queryUnifyAuthById(id);
        if (modiyUnifyAuth == null) {
            return ResultUtils.warn(UserConst.UNIFYAUTH_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.UNIFYAUTH_HAS_DELETED));
        }

        //被删除的授权信息必须被禁用
        Integer authStatus = modiyUnifyAuth.getAuthStatus();
        if (UserConst.UNIFY_AUTH_START_STATUS.equals(authStatus)) {
            return ResultUtils.warn(UserConst.UNIFY_AUTH_IS_ENABLED_STATE,
                    I18nUtils.getSystemString(UserI18n.UNIFY_AUTH_IS_ENABLED_STATE));
        }

        Integer integer = unifyauthDao.deleteUnifyAuthById(id);
        if (integer != UserConst.ADD_ONE_INFO) {
            throw new FilinkUnifyAuthException(I18nUtils.getSystemString(UserI18n.UNIFYAUTH_OPER_ERROR));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.DELETE_UNIFYAUTH_SUCCESS));
    }

    /**
     * 【批量删除统一授权信息
     *
     * @param ids 待删除的统一授权id
     * @return 删除的结果
     */
    @Override
    public Result batchDeleteUnifyAuth(String[] ids) {

        //传入的id数组为空
        if (ids == null) {
            return ResultUtils.warn(UserConst.UNIFYAUTH_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.UNIFYAUTH_HAS_DELETED));
        }

        //查询当前统一授权信息有没有被删除
        List<UnifyAuth> unifyAuthList = unifyauthDao.batchQueryUnifyAuthByIds(ids);
        if (unifyAuthList == null || unifyAuthList.size() != ids.length) {
            return ResultUtils.warn(UserConst.UNIFYAUTH_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.UNIFYAUTH_HAS_DELETED));
        }

        //判断当前待被删除授权信息有没有生效的
        UnifyAuth firstUnifyAuth = unifyAuthList.stream().filter(auth ->
                UserConst.UNIFY_AUTH_START_STATUS.equals(auth.getAuthStatus())
        ).findFirst().orElse(null);

        //如果存在未生效的信息
        if (firstUnifyAuth != null) {
            return ResultUtils.warn(UserConst.UNIFY_AUTH_IS_ENABLED_STATE,
                    I18nUtils.getSystemString(UserI18n.UNIFY_AUTH_IS_ENABLED_STATE));
        }

        Integer integer = unifyauthDao.batchDeleteUnifyAuth(ids);
        if (integer != ids.length) {
            throw new FilinkUnifyAuthException(I18nUtils.getSystemString(UserI18n.UNIFYAUTH_OPER_ERROR));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.DELETE_UNIFYAUTH_SUCCESS));
    }

    /**
     * 批量修改统一授权的状态
     *
     * @param unifyAuthParameter 修改参数类
     * @return
     */
    @Override
    public Result batchModifyUnifyAuthStatus(UnifyAuthParameter unifyAuthParameter) {

        if (unifyAuthParameter == null) {
            return ResultUtils.warn(UserConst.UNIFYAUTH_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.UNIFYAUTH_HAS_DELETED));
        }
        String[] ids = unifyAuthParameter.getIdArray();
        //查询当前统一授权信息有没有被删除
        List<UnifyAuth> unifyAuthList = unifyauthDao.batchQueryUnifyAuthByIds(ids);
        if (unifyAuthList == null || unifyAuthList.size() != ids.length) {
            return ResultUtils.warn(UserConst.UNIFYAUTH_HAS_DELETED,
                    I18nUtils.getSystemString(UserI18n.UNIFYAUTH_HAS_DELETED));
        }

        Integer integer = unifyauthDao.batchModifyUnifyAuthStatus(ids, unifyAuthParameter.getAuthStatus());
        if (integer != ids.length) {
            throw new FilinkUnifyAuthException(I18nUtils.getSystemString(UserI18n.UNIFYAUTH_OPER_ERROR));
        }

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.MODIFY_UNIFYAUTHSTATUS_SUCCESS));
    }

    /**
     * 获取当前登录人的授权信息
     *
     * @return
     */
    @Override
    public Result queryUserAuthInfoById() {

        String userId = RequestInfoUtils.getUserId();
        List<UnifyAuth> unifyAuthList = unifyauthDao.queryUnifyAuthByUserId(userId);
        List<TempAuth> tempAuthList = tempauthDao.queryTempAuthByUserId(userId);
        //根据设施id获取设施的信息
        List<String> deviceIdList = new ArrayList<>();
        //获取统一授权中的所有设施id
        if (CheckEmptyUtils.collectEmpty(unifyAuthList)) {
            unifyAuthList.forEach(unifyAuth -> {
                List<AuthDevice> authDeviceList = unifyAuth.getAuthDeviceList();
                deviceToList(authDeviceList, deviceIdList);
            });
        }

        //获取临时授权中的所有设施id
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

                //设置统一授权中的所有设施名称
                if (CheckEmptyUtils.collectEmpty(unifyAuthList)) {
                    unifyAuthList.forEach(unifyAuth -> {
                        List<AuthDevice> authDeviceList = unifyAuth.getAuthDeviceList();
                        setDeviceName(authDeviceList,deviceMap);
                    });
                }

                //设置临时授权中的所有设施名称
                if (CheckEmptyUtils.collectEmpty(tempAuthList)) {
                    tempAuthList.forEach(tempAuth -> {
                        List<AuthDevice> authDeviceList = tempAuth.getAuthDeviceList();
                        setDeviceName(authDeviceList,deviceMap);
                    });
                }
            }
        }

        return ResultUtils.success(new AuthInfo(unifyAuthList, tempAuthList));
    }

    /**
     * 在授权设施列表中设施对应的设施名称
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
     * 将授权设施中的设施id添加到list列表中
     *
     * @param authDeviceList 授权设施列表信息
     * @param deviceIdList   设施id列表
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
     * 根据用户id和设施以及门锁id查询授权信息
     *
     * @param userAUthInfo 用户，设施，门锁信息
     * @return
     */
    @Override
    public AuthInfo queryAuthInfoByUserIdAndDeviceAndDoor(UserAuthInfo userAUthInfo) {

        List<UnifyAuth> unifyauths = unifyauthDao.queryAuthInfoByUserIdAndDeviceAndDoor(userAUthInfo);
        List<TempAuth> tempauths = tempauthDao.queryAuthInfoByUserIdAndDeviceAndDoor(userAUthInfo);

        return new AuthInfo(unifyauths, tempauths);
    }

    /**
     * 根据设施信息删除授权信息
     *
     * @param deviceInfo 设施信息
     * @return 删除的数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteAuthByDevice(DeviceInfo deviceInfo) {

        List<UnifyAuth> unifyauths = unifyauthDao.queryUnifyAuthByDevice(deviceInfo);
        List<TempAuth> tempauths = tempauthDao.queryTempAuthByDevice(deviceInfo);
        List<String> unifyAuthIdList = new ArrayList<>();
        List<String> tempAuthIdList = new ArrayList<>();
        List<String> authDeviceIdList = new ArrayList<>();
        Integer allDeleteNum = 0;
        //删除统一授权
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
                throw new FilinkUnifyAuthException(I18nUtils.getSystemString(UserI18n.UNIFYAUTH_OPER_ERROR));
            }
            allDeleteNum += deleteNum;
        }


        //删除临时授权
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
                throw new FilinkTempauthException(I18nUtils.getSystemString(UserI18n.TEMPAUTH_OPER_ERROR));
            }
            allDeleteNum += deleteTempAuthNum;
        }


        //删除中间表信息
        if (authDeviceIdList.size() > 0) {

            Integer authDeviceNum = authDeviceDao.batchDeleteAuthDevice(authDeviceIdList);
            if (authDeviceNum != authDeviceIdList.size()) {
                throw new FilinkTempauthException(I18nUtils.getSystemString(UserI18n.TEMPAUTH_OPER_ERROR));
            }
            allDeleteNum += authDeviceNum;
        }

        return allDeleteNum;
    }

    /**
     * 根据统一授权名称获取统一授权信息
     *
     * @param unifyauth 统一授权信息
     * @return 统一授权列表信息
     */
    @Override
    public Result queryAuthByName(UnifyAuth unifyauth) {

        String name = unifyauth.getName();
        List<UnifyAuth> unifyauths = unifyauthDao.queryAuthByName(name);
        return ResultUtils.success(unifyauths);
    }
}
