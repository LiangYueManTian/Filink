package com.fiberhome.filink.fdevice.service.device;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.fdevice.bean.device.DeviceMapConfig;
import com.baomidou.mybatisplus.service.IService;


import java.util.List;

/**
 * <p>
 * 首页地图和设施类型配置服务类
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-01-19
 */
public interface DeviceMapConfigService extends IService<DeviceMapConfig> {
    /**
     * 通过用户ID查询用户设施类型配置信息
     *
     * @return 查询结果
     */
    Result queryDeviceMapConfig();

    /**
     * 修改用户地图设施类型配置的设施类型启用状态
     *
     * @param deviceMapConfigs 地图设施类型配置信息list
     * @return 查询结果
     */
    Result bathUpdateDeviceConfig(List<DeviceMapConfig> deviceMapConfigs);

    /**
     * 修改用户地图设施类型配置的设施图标尺寸
     *
     * @param deviceMapConfig 地图设施类型配置信息
     * @return 查询结果
     */
    Result updateDeviceIconSize(DeviceMapConfig deviceMapConfig);

    /**
     * 批量插入用户首页地图设施配置信息
     *
     * @param userId 用户Id
     * @return 插入条数
     */
    boolean insertConfigBatch(String userId);

    /**
     * 删除用户所有配置信息
     *
     * @param userIds 用户Id List
     * @return 更新条数
     */
    boolean deletedConfigByUserIds(List<String> userIds);
}
