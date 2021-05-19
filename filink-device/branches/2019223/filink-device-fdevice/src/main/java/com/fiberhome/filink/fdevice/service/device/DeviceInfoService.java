package com.fiberhome.filink.fdevice.service.device;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.dto.DeviceInfoDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zepenggao@wistronits.com
 * @since 2019-01-07
 */
public interface DeviceInfoService extends IService<DeviceInfo> {

    /**
     * 新增设施
     *
     * @param deviceInfo DeviceInfo
     * @return Result
     * @throws Exception 异常
     */
    Result addDevice(DeviceInfo deviceInfo);

    /**
     * 校验设施名称是否重复
     *
     * @param deviceId   设施id
     * @param deviceName 设施名称
     * @return boolean
     */
    boolean checkDeviceName(String deviceId, String deviceName);

    /**
     * 修改设施信息
     *
     * @param deviceInfo 设施基本信息
     * @return Result
     * @throws Exception 异常
     */
    Result updateDevice(DeviceInfo deviceInfo);

    /**
     * 分页查询设施列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     * @throws Exception 异常
     */
    Result listDevice(QueryCondition<DeviceInfo> queryCondition) throws Exception;

    /**
     * 根据区域id查询绑定的设施
     *
     * @param id 查询id
     * @return 查询结果
     */
    List<DeviceInfo> queryDeviceByAreaId(String id);
    /**
     * 关联设施
     *
     * @param map 关联设施信息
     * @return 操作结果
     * @throws
     */
    Boolean setAreaDevice(Map<String, List<String>> map);

    /**
     * 查询设施详情
     *
     * @param id 设施id
     * @return Result
     * @throws Exception 异常
     */
    Result getDeviceById(String id) throws Exception;


    /**
     * 首页查询用户所有设施基本信息
     *
     * @return 设施基本信息
     */
    Result queryDeviceAreaList();

    /**
     * 删除设施
     *
     * @param ids 设施ids
     * @return Result
     */
    Result deleteDeviceByIds(String[] ids);

    /**
     * 根据序列号查询设施
     * @param id 设施序列号
     * @return DeviceInfoDto
     * @throws Exception 异常
     */
    DeviceInfoDto findDeviceBySeriaNumber(String id) throws Exception;

    /**
     * 设施是否可以修改
     * @param deviceId 设施序列号
     * @return
     */
    boolean deviceCanChangeDetail(String deviceId);

    /**
     * 刷新指定区域的Gis map 信息
     * @param areaId 区域ID
     */
    void refreshDeviceAreaRedis(String areaId);

}
