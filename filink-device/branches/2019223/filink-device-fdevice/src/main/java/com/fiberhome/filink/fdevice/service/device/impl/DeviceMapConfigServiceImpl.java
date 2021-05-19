package com.fiberhome.filink.fdevice.service.device.impl;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.fdevice.bean.device.DeviceMapConfig;
import com.fiberhome.filink.fdevice.bean.device.DeviceMapI18n;
import com.fiberhome.filink.fdevice.dao.device.DeviceMapConfigDao;
import com.fiberhome.filink.fdevice.dto.DeviceMapConfigDto;
import com.fiberhome.filink.fdevice.exception.*;
import com.fiberhome.filink.fdevice.service.device.DeviceMapConfigService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.fdevice.utils.DeviceMapConfigEnum;
import com.fiberhome.filink.fdevice.utils.DeviceType;
import com.fiberhome.filink.server_common.exception.UserNotLoginException;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  首页地图和设施类型配置服务实现类
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-01-19
 */
@Service
public class DeviceMapConfigServiceImpl extends ServiceImpl<DeviceMapConfigDao, DeviceMapConfig> implements DeviceMapConfigService {

    /**
     * 自动注入地图设施类型配置dao对象
     */
    @Autowired
    private DeviceMapConfigDao deviceMapConfigDao;
    /**设施类型配置启用*/
    private static final String DEVICE_TYPE_ENABLE = "1";
    /**设施类型配置禁用*/
    private static final String DEVICE_TYPE_DISABLE = "0";
    /**用户ID请求头*/
    private static final String REQUEST_USER = "userId";
    /**
     * 通过用户ID查询用户设施类型配置信息
     *
     * @return 查询结果
     */
    @Override
    public Result queryDeviceMapConfig() {
        //获取用户信息
        String userId = getUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new UserNotLoginException();
        }
        DeviceMapConfigDto deviceMapConfigDto = deviceMapConfigDao.queryDeviceMapConfig(userId);
        if (ObjectUtils.isEmpty(deviceMapConfigDto)) {
            throw new FilinkDeviceMapMessageException();
        }
        return ResultUtils.success(deviceMapConfigDto);
    }

    /**
     * 修改用户地图设施类型配置的设施类型启用状态
     *
     * @param deviceMapConfigs 地图设施类型配置信息list
     * @return 查询结果
     */
    @Override
    public Result bathUpdateDeviceConfig(List<DeviceMapConfig> deviceMapConfigs) {
        //校验参数
        if (deviceMapConfigs == null || deviceMapConfigs.size() == 0) {
            throw new FilinkDeviceMapParamException();
        }
        for (DeviceMapConfig m : deviceMapConfigs ) {
            if (checkDeviceMapConfig(m) || !checkDeviceConfig(m)) {
                throw new FilinkDeviceMapParamException();
            }
        }
        //获取用户信息
        String userId = getUserId();
        int results = deviceMapConfigDao.bathUpdateDeviceConfig(deviceMapConfigs, userId);
        if (results != deviceMapConfigs.size()) {
            throw new FilinkDeviceMapUpdateException();
        }
        return ResultUtils.success();
    }

    /**
     * 修改用户地图设施类型配置的设施图标尺寸
     *
     * @param deviceMapConfig 地图设施类型配置信息
     * @return 查询结果
     */
    @Override
    public Result updateDeviceIconSize(DeviceMapConfig deviceMapConfig) {
        //校验参数
        if (checkDeviceMapConfig(deviceMapConfig) || StringUtils.isEmpty(deviceMapConfig.getConfigValue())) {
            throw new FilinkDeviceMapParamException();
        }
        //获取用户信息
        String userId = getUserId();
        deviceMapConfig.setUserId(userId);
        int results = deviceMapConfigDao.updateDeviceIconSize(deviceMapConfig);
        if (results != 1) {
            throw new FilinkDeviceMapUpdateException();
        }
        return ResultUtils.success();
    }

    /**
     * 批量插入用户首页地图设施配置信息
     *
     * @param userId 用户Id
     * @return 插入条数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertConfigBatch(String userId) {
        if(StringUtils.isEmpty(userId)) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceMapI18n.CONFIG_USER_PARAM_ERROR));
        }
        List<DeviceMapConfig> deviceMapConfigs = new ArrayList<>();
        //当前用户
        String createUser = getUserId();
        //插入默认设施类型数据
        for (DeviceType deviceType: DeviceType.values()) {
            DeviceMapConfig deviceMapConfig = new DeviceMapConfig();
            deviceMapConfig.setConfigId(UUIDUtil.getInstance().UUID32());
            deviceMapConfig.setConfigType(DeviceMapConfigEnum.DEVICE_CONFIG_TYPE.getValue());
            deviceMapConfig.setUserId(userId);
            deviceMapConfig.setCreateUser(createUser);
            deviceMapConfig.setDeviceType(deviceType.getCode());
            deviceMapConfig.setConfigValue(DeviceMapConfigEnum.DEVICE_CONFIG_VALUE.getValue());
            deviceMapConfigs.add(deviceMapConfig);
        }
        //插入默认地图设施图标尺寸数据
        DeviceMapConfig mapConfig = new DeviceMapConfig();
        mapConfig.setConfigId(UUIDUtil.getInstance().UUID32());
        mapConfig.setConfigType(DeviceMapConfigEnum.MAP_CONFIG_TYPE.getValue());
        mapConfig.setUserId(userId);
        mapConfig.setCreateUser(createUser);
        mapConfig.setDeviceType(DeviceMapConfigEnum.MAP_DEVICE_TYPE.getValue());
        mapConfig.setConfigValue(DeviceMapConfigEnum.MAP_CONFIG_VALUE.getValue());
        deviceMapConfigs.add(mapConfig);
        Integer result = deviceMapConfigDao.insertConfigBatch(deviceMapConfigs);
        if (result != DeviceType.values().length + 1) {
            throw new FilinkDeviceMapException();
        }
        return true;
    }

    /**
     * 删除用户所有配置信息
     *
     * @param userIds 用户Id List
     * @return 成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletedConfigByUserIds(List<String> userIds) {
        //校验用户ID
        if (userIds == null || userIds.size() == 0) {
            throw new FilinkDeviceException(I18nUtils.getString(DeviceMapI18n.CONFIG_USER_PARAM_ERROR));
        }
        deviceMapConfigDao.deletedConfigByUserIds(userIds);
        return true;
    }

    /**
     * 获取用户ID
     * @return 用户ID
     */
    private String getUserId() {
        String userId = null;
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            userId = request.getHeader(REQUEST_USER);
        }
        return userId;
    }


    /**
     * 校验实体是否为空
     *
     * @param deviceMapConfig 首页地图设施配置实体类
     * @return true false
     */
    private boolean checkDeviceMapConfig(DeviceMapConfig deviceMapConfig) {
        return ObjectUtils.isEmpty(deviceMapConfig);
    }

    /**
     * 校验首页设施类型配置参数是否正确
     *
     * @param deviceMapConfig 首页地图设施配置实体类
     * @return true false
     */
    private boolean checkDeviceConfig(DeviceMapConfig deviceMapConfig){
        return DEVICE_TYPE_ENABLE.equals(deviceMapConfig.getConfigValue()) || DEVICE_TYPE_DISABLE.equals(deviceMapConfig.getConfigValue());
    }


}
