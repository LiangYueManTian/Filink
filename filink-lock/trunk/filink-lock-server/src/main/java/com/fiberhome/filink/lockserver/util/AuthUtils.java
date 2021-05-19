package com.fiberhome.filink.lockserver.util;

import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.deviceapi.bean.DeviceParam;
import com.fiberhome.filink.lockserver.constant.AuthParam;
import com.fiberhome.filink.lockserver.constant.ConstantParam;
import com.fiberhome.filink.lockserver.exception.FiLinkAccessDenyException;
import com.fiberhome.filink.lockserver.exception.FiLinkLockException;
import com.fiberhome.filink.lockserver.exception.FiLinkDeviceIsNullException;
import com.fiberhome.filink.lockserver.exception.FiLinkSystemException;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 数据权限工具类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/25
 */
@Slf4j
@Component
public class AuthUtils {
    /**
     * userFeign
     */
    @Autowired
    private UserFeign userFeign;
    /**
     * userFeign
     */
    @Autowired
    private DeviceFeign deviceFeign;

    /**
     * 获取当前登录用户的用户信息
     *
     * @return
     */
    public User getCurrentUser() {
        //查询当前用户权限信息
        Object userObj = userFeign.queryCurrentUser(RequestInfoUtils.getUserId(), RequestInfoUtils.getToken());
        //校验是否有值
        if (userObj == null) {
            throw new FiLinkSystemException("userFeign queryCurrentUser failed>>>");
        }
        User user = JSON.parseObject(JSON.toJSONString(userObj), User.class);
        //校验用户信息
        if (user == null || user.getDepartment() == null
                || user.getRole() == null) {
            throw new FiLinkSystemException("user is empry>>>");
        }
        //如果没有区域和设施类型，赋空
        if (user.getDepartment().getAreaIdList() == null) {
            user.getDepartment().setAreaIdList(Collections.emptyList());
        }
        if (user.getRole().getRoleDevicetypeList() == null) {
            user.getRole().setRoleDevicetypeList(Collections.emptyList());
        }

        return user;
    }

    /**
     * 判断用户是否具有数据权限
     *
     * @param deviceId 设施id
     */
    public void addAuth(String deviceId) {
        //设施类型
        String deviceType = findDeviceTypeByDeviceId(deviceId);
        addAuth(deviceType, getCurrentUser());
    }

    /**
     * 判断用户是否具有数据权限
     *
     * @param deviceType 设施类型编号
     * @param user       用户
     */
    public void addAuth(String deviceType, User user) {
        if (ConstantParam.ADMIN.equals(RequestInfoUtils.getUserId())) {
            return;
        }
        //用户管理设施类型
        List<String> deviceTypes = getUserDeviceTypes(user);
        AuthParam.checkLockPermission(deviceType, deviceTypes);
    }

    /**
     * 获取用户授权的区域id集合
     *
     * @param user 用户
     * @return 区域id
     */
    public List<String> getUserAreaIds(User user) {
        return user.getDepartment().getAreaIdList();
    }

    /**
     * 获取用户授权的设施类型集合
     *
     * @param user 用户
     * @return 设施类型
     */
    public List<String> getUserDeviceTypes(User user) {
        List<String> deviceTypes = new ArrayList<>();
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();
        for (RoleDeviceType roleDeviceType : roleDeviceTypes) {
            deviceTypes.add(roleDeviceType.getDeviceTypeId());
        }
        return deviceTypes;
    }

    /**
     * 根据设施序列id查询设施类型
     *
     * @param deviceId 设施id
     * @return 设施类型
     */

    public String findDeviceTypeByDeviceId(String deviceId) {
        DeviceInfoDto deviceInfoDto = deviceFeign.getDeviceById(deviceId);
        if (deviceInfoDto == null || deviceInfoDto.getDeviceId() == null) {
            throw new FiLinkDeviceIsNullException("设施为空");
        }
        return deviceInfoDto.getDeviceType();
    }

    /**
     * 获取用户授权的设施id集合
     *
     * @return 设施id集合
     */
    public List<String> getUserDeviceIds() {
        User currentUser = getCurrentUser();
        List<String> userAreaIds = getUserAreaIds(currentUser);
        List<String> userDeviceTypes = getUserDeviceTypes(currentUser);
        DeviceParam deviceParam = new DeviceParam();
        deviceParam.setAreaIds(userAreaIds);
        deviceParam.setDeviceTypes(userDeviceTypes);
        List<DeviceInfoDto> deviceInfoDtoList;
        try {
            deviceInfoDtoList = deviceFeign.queryDeviceDtoByParam(deviceParam);
        } catch (Exception e) {
            throw new FiLinkLockException("device feign execute failed>>>>>");
        }
        if (deviceInfoDtoList == null || deviceInfoDtoList.size() < 1) {
            //没有访问设施的权限
            throw new FiLinkAccessDenyException();
        }

        return deviceInfoDtoList.stream().map(DeviceInfoDto::getDeviceId).collect(Collectors.toList());
    }

    /**
     * 获得用户的设备id
     *
     * @return
     */
    public String getPhoneId() {
        User currentUser = getCurrentUser();
        return currentUser.getPushId();
    }

    /**
     * 获得AppKey
     *
     * @return
     */
    public Long getAppKey() {
        User currentUser = getCurrentUser();
        Long appKey = null;
        try {
            appKey = Long.valueOf(currentUser.getAppKey());
        } catch (Exception e) {
            log.error("获取appkey失败:", e);
        }
        return appKey;
    }

    /**
     * 判断有无该主控的设施权限
     *
     * @param deviceId 设施id
     */
    public void hasControlDeviceAuth(String deviceId) {
        if (ConstantParam.ADMIN.equals(RequestInfoUtils.getUserId())) {
            return;
        }
        if (StringUtils.isEmpty(deviceId)) {
            return;
        }
        List<String> userDeviceIds = getUserDeviceIds();
        if (userDeviceIds != null && !userDeviceIds.contains(deviceId)) {
            //没有访问设施的权限
            throw new FiLinkAccessDenyException();
        }
    }
}
