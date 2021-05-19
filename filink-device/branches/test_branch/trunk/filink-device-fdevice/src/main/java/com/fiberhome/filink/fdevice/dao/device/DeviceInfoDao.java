package com.fiberhome.filink.fdevice.dao.device;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.SortCondition;
import com.fiberhome.filink.deviceapi.bean.DevicePicReq;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zepenggao@wistronits.com
 * @since 2019-01-07
 */
public interface DeviceInfoDao extends BaseMapper<DeviceInfo> {

    /**
     * 根据设施名称查询设施
     * @param deviceName 设施名
     * @return DeviceInfo
     */
    DeviceInfo selectByName(String deviceName);

    /**
     * 新增设施
     *
     * @param deviceInfo 设施信息
     * @return int
     */
    int insertDevice(DeviceInfo deviceInfo);

    /**
     * 分页模糊查询设备
     *
     * @param page    Page
     * @param wrapper Wrapper
     * @return List<DeviceInfo>
     */
    List<DeviceInfo> selectDevice(Page page, @Param("ew") Wrapper<DeviceInfo> wrapper);

    /**
     * 根据区域id查询设施
     *
     * @param areaId 查询id
     * @return 查询结果
     */
    List<DeviceInfo> queryDeviceByAreaId(String areaId);

    /**
     * 根据区域ids查询设施
     * @param areaIds
     * @return
     */
    List<DeviceInfo> queryDeviceByAreaIds(List<String> areaIds);

    /**
     * 查询没有关联区域的设施
     * @param
     * @return 查询结果
     */
    List<DeviceInfo> queryDeviceAreaIdIsNull();

    /**
     * 设置该区域关联设施为null
     *
     * @param areaId 区域ID
     * @return
     */
    Integer setAreaDeviceIsNull(@Param("areaId") String areaId);

    /**
     * 关联设施
     * @param areaId 区域id list 关联设施id
     * @param list
     * @return 关联结果
     */
    Integer setAreaDevice(@Param("areaId") String areaId, @Param("list") List<String> list);

    /**
     * 根据设施id查询设施信息
     *
     * @param id 设施id
     * @return DeviceInfo
     */
    DeviceInfo selectDeviceById(String id);

    /**
     * 批量查询
     *
     * @param deviceIds 设施id
     * @return
     */
    List<DeviceInfo> selectByIds(List<String> deviceIds);

    /**
     * 根据设施编号查询设施
     *
     * @param deviceCode 设施编号
     * @return List<DeviceInfo>
     */
    List<DeviceInfo> checkDeviceCode(String deviceCode);

    /**
     * 首页查询用户所有设施基本信息
     *
     * @return 查询结果
     */
    List<HomeDeviceInfoDto> queryDeviceAreaList();

    /**
     * 首页查询单个设施基本信息
     *
     * @param deviceId 设施ID
     * @return 查询结果
     */
    HomeDeviceInfoDto queryDeviceAreaById(String deviceId);
    /**
     * 首页查询设施信息根据设施ID List
     * @param deviceIds 设施ID List
     * @return 查询结果
     */
    List<HomeDeviceInfoDto> queryDeviceAreaByIds(List<String> deviceIds);

    /**
     * 首页查询设施信息根据区域ID-
     * @param areaId 区域ID-
     * @return  查询结果
     */
    List<HomeDeviceInfoDto> queryDeviceAreaByAreaId(String areaId);

    /**
     * 删除设施
     *
     * @param ids 设施ID数组
     */
    void deleteDeviceByIds(String[] ids);

    /**
     * 查询设施
     *
     * @param ids 设施ID数组
     * @return 设施列表
     */
    List<DeviceInfo> selectDeviceByIds(String[] ids);

    /**
     * 根据序列号查询设施
     *
     * @param seriaNum 序列号
     * @return
     */
//    DeviceInfo selectDeviceBySerialNumber(String seriaNum);

    /**
     * 分页查询设施列表
     *
     * @param page          分页条件
     * @param map           过滤条件
     * @param sortCondition 排序条件
     * @return
     */
//    List<DeviceInfoDto> selectDeviceByPage(@Param("page") PageCondition page, @Param("map") Map map, @Param("sort") SortCondition sortCondition);

    /**
     * 分页查询设施列表
     * @param pageCondition
     * @param filterConditionList
     * @param sortCondition
     * @return
     */
    List<DeviceInfoDto> selectDevicePage(@Param("page")PageCondition pageCondition,
                                         @Param("filterList")List<FilterCondition> filterConditionList,
                                         @Param("sort")SortCondition sortCondition);

//    Integer selectDeviceCount(@Param("map") Map map);

    /**
     * 根据过滤条件查询设施数量
     * @param filterConditionList 查询条件
     * @return 设施数量
     */
    Integer selectDeviceCount(@Param("filterList")List<FilterCondition> filterConditionList);

    /**
     * 查询基础设施信息
     * @param deviceReq
     * @return
     */
    List<DeviceInfo> selectDeviceByBean(DeviceReq deviceReq);

    /**
     * 根据经纬度，半径查询范围内的设施
     * @param deviceReqPda 查询条件
     * @param userId
     * @return 设施集合
     */
    List<DeviceInfoForPda> queryNearbyDeviceList(@Param("req")DeviceReqPda deviceReqPda, @Param("userId")String userId);

    /**
     * 查询范围内的设施
     * @param deviceReqPda
     * @param userId
     * @return
     */
    Integer queryNearbyDeviceCount(@Param("req")DeviceReqPda deviceReqPda, @Param("userId")String userId);

    /**
     * 更新指定设施的部署状态
     * @param updateDeviceStatusPda
     * @return
     */
    Integer updateDeviceListStatus(UpdateDeviceStatusPda updateDeviceStatusPda);

    /**
     * 查询指定设施的对应图片信息
     * @param devicePicReq
     * @return
     */
    List<DevicePicDto> queryPicInfoByDeviceIds(DevicePicReq devicePicReq);

    /**
     * 根据区域ID集合查询设施类型集合
     *
     * @param areaIds 区域ID集合
     * @return 设施类型集合
     */
    List<String> queryDeviceTypesByAreaIds(List<String> areaIds);

    /**
     * 根据区域id和设施类型查询设施信息
     * @param deviceParam
     * @return
     */
    List<DeviceInfoBase> queryDeviceInfoBaseByParam(DeviceParam deviceParam);
}
