package com.fiberhome.filink.fdevice.dao.statistics;

import com.fiberhome.filink.fdevice.dto.DeviceNumDto;
import com.fiberhome.filink.fdevice.dto.DeviceParam;

import java.util.List;

/**
 * @Author: zhaoliang
 * @Date: 2019/5/23 22:27
 * @Description: com.fiberhome.filink.fdevice.dao.statistics
 * @version: 1.0
 */
public interface StatisticsDao {

    /**
     * 查询设施数量
     * @param deviceParam
     * @return
     */
    List<DeviceNumDto> queryDeviceCount(DeviceParam deviceParam);

    /**
     * 根据区域ID和设施类型统计设施状态数量
     * @param deviceParam
     * @return
     */
    List<DeviceNumDto> queryDeviceStatusCount(DeviceParam deviceParam);

    /**
     * 根据区域ID和设施类型统计设施部署状态数量
     * @param deviceParam
     * @return
     */
    List<DeviceNumDto> queryDeployStatusCount(DeviceParam deviceParam);

    /**
     * 查询区域内的设施数量
     * @param areaIds
     * @return
     */
    List<DeviceNumDto> queryDeviceTypeCount(List<String> areaIds);

    /**
     * 查询当前用户设施状态数量
     * @param deviceParam
     * @return
     */
    List<DeviceNumDto> queryUserDeviceStatusCount(DeviceParam deviceParam);

    /**
     * 查询当前用户设施数量和设施状态数量
     * @param deviceParam
     * @return
     */
    List<DeviceNumDto> queryUserDeviceAndStatusCount(DeviceParam deviceParam);
}
