package com.fiberhome.filink.fdevice.dao.device;

import com.fiberhome.filink.fdevice.bean.device.DeviceMapConfig;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.fdevice.dto.DeviceMapConfigDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  首页地图和设施类型配置Mapper 接口
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-01-19
 */
public interface DeviceMapConfigDao extends BaseMapper<DeviceMapConfig> {

    /**
     *通过用户ID查询用户设施类型配置信息
     *
     * @param userId 用户Id
     * @return 查询结果
     */
    DeviceMapConfigDto queryDeviceMapConfig(String userId);
    /**
     *通过用户ID查询用户设施类型配置信息
     *
     * @param userId 用户Id
     * @return 查询结果
     */
    List<DeviceMapConfig> queryDeviceConfig(String userId);
    /**
     * 修改用户地图设施类型配置的设施类型启用状态
     * @param deviceMapConfigs 设施类型配置list
     * @param userId 用户Id
     * @return 更新条数
     */
    Integer bathUpdateDeviceConfig(@Param("configlist")List<DeviceMapConfig> deviceMapConfigs, @Param("userId")String userId);

    /**
     *修改用户地图设施类型配置的设施图标尺寸
     *
     * @param deviceMapConfig 地图设施类型配置信息
     * @return 更新条数
     */
    Integer updateDeviceIconSize(DeviceMapConfig deviceMapConfig);

    /**
     * 批量插入首页地图设施配置信息
     *
     * @param deviceMapConfigs 设施类型配置list
     * @return 插入条数
     */
    Integer insertConfigBatch(List<DeviceMapConfig> deviceMapConfigs);

    /**
     * 删除用户所有配置信息
     *
     * @param userIds 用户Id List
     * @return 条数
     */
    Integer deletedConfigByUserIds(List<String> userIds);
}
