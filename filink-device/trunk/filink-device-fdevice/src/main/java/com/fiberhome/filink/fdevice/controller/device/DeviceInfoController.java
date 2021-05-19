package com.fiberhome.filink.fdevice.controller.device;


import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.constant.device.DeviceI18n;
import com.fiberhome.filink.fdevice.constant.device.DeviceResultCode;
import com.fiberhome.filink.fdevice.dto.*;
import com.fiberhome.filink.fdevice.exception.FiLinkDeviceException;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 设施管理控制器
 * </p>
 *
 * @author zepenggao@wistronits.comz
 * @since 2019-01-07
 */
@RestController
@RequestMapping("/deviceInfo")
public class DeviceInfoController {


    @Autowired
    private DeviceInfoService deviceInfoService;

    /**
     * 新增设施
     *
     * @param deviceInfo DeviceInfo
     * @return Result
     */
    @PostMapping("addDevice")
    public Result addDevice(@RequestBody DeviceInfo deviceInfo) {
        return deviceInfoService.addDevice(deviceInfo);
    }


    /**
     * PDA新增设施
     *
     * @param deviceInfo DeviceInfo
     * @return Result
     */
    @PostMapping("addDeviceForPda")
    public Result addDeviceForPda(@RequestBody DeviceInfo deviceInfo) {
        return deviceInfoService.addDeviceForPda(deviceInfo);
    }

    /**
     * 新增设施Feign
     *
     * @param deviceInfoDto DeviceInfoDto
     * @return Result
     */
    @PostMapping("/feign/addDevice")
    public Result addDevice(@RequestBody DeviceInfoDto deviceInfoDto) {
        DeviceInfo deviceInfo = new DeviceInfo();
        BeanUtils.copyProperties(deviceInfoDto, deviceInfo);
        return deviceInfoService.addDevice(deviceInfo);
    }

    /**
     * 校验设施名称是否重复
     *
     * @param deviceInfo 设施id和设施名称
     * @return Result
     */
    @PostMapping("/queryDeviceNameIsExist")
    public Result checkDeviceName(@RequestBody DeviceInfo deviceInfo) {
        if (ObjectUtils.isEmpty(deviceInfo)) {
            return ResultUtils.success(DeviceResultCode.DEVICE_PARAM_ERROR, I18nUtils.getSystemString(DeviceI18n.DEVICE_NAME_NULL), false);
        }
        boolean isExist = deviceInfoService.checkDeviceName(deviceInfo.getDeviceId(), deviceInfo.getDeviceName());
        if (isExist) {
            return ResultUtils.success(DeviceResultCode.DEVICE_NAME_SAME, I18nUtils.getSystemString(DeviceI18n.DEVICE_NAME_SAME), false);
        }
        return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getSystemString(DeviceI18n.DEVICE_NAME_AVAILABLE), true);
    }

    /**
     * 修改设施信息
     *
     * @param deviceInfo 设施基本信息
     * @return Result
     */
    @PutMapping("updateDeviceById")
    public Result updateDevice(@RequestBody DeviceInfo deviceInfo) {
        return deviceInfoService.updateDevice(deviceInfo);
    }

    /**
     * 修改PDA设施信息
     *
     * @param deviceInfo 设施基本信息
     * @return Result
     */
    @PutMapping("updateDeviceForPda")
    public Result updateDeviceForPda(@RequestBody DeviceInfo deviceInfo) {
        return deviceInfoService.updateDeviceForPda(deviceInfo);
    }

    /**
     * 分页查询设施列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @PostMapping("deviceListByPage")
    public Result listDevice(@RequestBody QueryCondition<DeviceInfo> queryCondition) {
        return deviceInfoService.listDevice(queryCondition);
    }

    /**
     * 分页查询设施列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @PostMapping("/deviceListOfLockByPage")
    public Result deviceListOfLockByPage(@RequestBody QueryCondition<DeviceInfo> queryCondition) {
        return deviceInfoService.listDeviceForSelectionLock(queryCondition);
    }

    /**
     * 分页查询设施列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @PostMapping("/feign/deviceListByPage")
    public Result deviceListByPage(@RequestBody QueryCondition<DeviceInfoDto> queryCondition) {
        if (queryCondition.getFilterConditions() == null) {
            queryCondition.setFilterConditions(new ArrayList<>());
        }
        QueryCondition<DeviceInfo> queryConditionNew = new QueryCondition<>();
        BeanUtils.copyProperties(queryCondition, queryConditionNew);
        DeviceInfo deviceInfo = new DeviceInfo();
        BeanUtils.copyProperties(queryCondition.getBizCondition(), deviceInfo);
        queryConditionNew.setBizCondition(deviceInfo);
        return deviceInfoService.listDevice(queryConditionNew);
    }

    /**
     * 查询当前设施数
     *
     * @return
     */
    @GetMapping("/feign/queryCurrentDeviceCount")
    public Integer queryCurrentDeviceCount() {
        return deviceInfoService.queryCurrentDeviceCount();
    }

    /**
     * 根据区域id查询绑定的设施（公共选择器）
     *
     * @param areaId
     * @return
     */
    @GetMapping("/queryDeviceDtoByAreaIdForPageSelection/{areaId}")
    public Result queryDeviceDtoByAreaIdForPageSelection(@PathVariable String areaId) {
        List<DeviceInfoDto> deviceInfoDtoList = deviceInfoService.queryDeviceDtoByAreaId(areaId);
        return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getSystemString(DeviceI18n.QUERY_DEVICE_SUCCESS), deviceInfoDtoList);
    }

    /**
     * 根据区域ids查询绑定的设施（公共选择器）
     *
     * @param areaIds
     * @return
     */
    @PostMapping("/queryDeviceDtoByAreaIdsForPageSelection")
    public Result queryDeviceDtoByAreaIdsForPageSelection(@RequestBody List<String> areaIds) {
        List<DeviceInfoDto> deviceInfoDtoList = deviceInfoService.queryDeviceDtoByAreaIds(areaIds);
        return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getSystemString(DeviceI18n.QUERY_DEVICE_SUCCESS), deviceInfoDtoList);
    }

    /**
     * 根据区域id和设施类型查询设施
     *
     * @param deviceParam
     * @return
     */
    @PostMapping("/queryDeviceDtoForPageSelection")
    public Result queryDeviceDtoForPageSelection(@RequestBody DeviceParam deviceParam) {
        return deviceInfoService.queryDeviceDtoForPageSelection(deviceParam);
    }

    /**
     * 根据区域id和设施类型查询设施
     *
     * @param deviceParam
     * @return
     */
    @PostMapping("/feign/queryDeviceDtoByParam")
    public List<DeviceInfoDto> queryDeviceDtoByParam(@RequestBody DeviceParam deviceParam) {
        return (List<DeviceInfoDto>) deviceInfoService.queryDeviceDtoForPageSelection(deviceParam).getData();
    }

    /**
     * 获取设施详情
     *
     * @param deviceReq 参数请求对象
     * @return Result 查询结果
     * @throws Exception 异常
     */
    @PostMapping("/queryDevice")
    public Result queryDeviceByBean(@RequestBody DeviceReq deviceReq) throws Exception {
        //检验参数
        if (StringUtils.isEmpty(deviceReq)) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.PARAMETER_ERROR));
        }
        //设施id和设施名称至少存在一个
        if (ObjectUtils.isEmpty(deviceReq.getDeviceId()) && ObjectUtils.isEmpty(deviceReq.getDeviceName())) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.PARAMETER_ERROR));
        }

        return deviceInfoService.queryDeviceByBean(deviceReq);
    }

    /**
     * 根据设施id获取参数下发配置项
     *
     * @param deviceId 设施id
     * @return 配置项信息
     */
    @GetMapping("/feign/getDefaultParams/{deviceId}")
    public String getDefaultParams(@PathVariable String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.PARAMETER_ERROR));
        }
        return JSON.toJSONString(deviceInfoService.getDefaultParamsByDeviceType(deviceId));
    }

    /**
     * 获取设施详情
     *
     * @param id 设施id
     * @return Result
     * @throws Exception 异常
     */
    @GetMapping("/findDeviceById/{id}")
    public Result getDeviceById(@PathVariable String id) {
        return deviceInfoService.getDeviceById(id, RequestInfoUtils.getUserId());
    }

    /**
     * 获取设施详情
     *
     * @param id 设施id
     * @return Result
     * @throws Exception 异常
     */
    @GetMapping("/feign/findDeviceById/{id}")
    public DeviceInfoDto feignGetDeviceById(@PathVariable String id) {
        Result result = deviceInfoService.getDeviceById(id, null);
        return (DeviceInfoDto) result.getData();
    }

    /**
     * 根据id查询设施是否存在
     *
     * @param id id
     * @return 查询结果
     */
    @GetMapping("/queryDeviceIsExistById/{id}")
    public Result queryDeviceIsExistById(@PathVariable String id) {
        Result result = deviceInfoService.queryDeviceIsExistById(id);
        return result;
    }

    /**
     * 获取设施详情
     *
     * @param ids 设施id
     * @return Result
     * @throws Exception 异常
     */
    @PostMapping("/feign/getDeviceByIds")
    public List<DeviceInfoDto> feignGetDeviceByIds(@RequestBody String[] ids) {
        List<DeviceInfoDto> dtoList = null;
        try {
            dtoList = deviceInfoService.getDeviceByIds(ids);
        } catch (Exception e) {
            //异常情况下直接返回null
            e.printStackTrace();
        }
        return dtoList;
    }

    /**
     * 获取设施详情（前端）
     *
     * @param ids 设施id
     * @return Result
     * @throws Exception 异常
     */
    @PostMapping("/getDeviceByIds")
    public Result getDeviceByIds(@RequestBody String[] ids) {
        return deviceInfoService.getDeviceResultByIds(ids);
    }

    /**
     * 首页查询用户所有设施基本信息(经纬度)
     *
     * @return 设施基本信息
     */
    @GetMapping("/queryDeviceAreaList")
    public Result queryDeviceAreaList() {
        return deviceInfoService.queryDeviceAreaList();
    }

    /**
     * 首页根据设施Id查询设施基本信息
     *
     * @param deviceId 设施Id
     * @return 设施基本信息
     */
    @GetMapping("/queryHomeDeviceById/{deviceId}")
    public Result queryHomeDeviceById(@PathVariable String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.PARAMETER_ERROR));
        }
        return deviceInfoService.queryHomeDeviceById(deviceId);
    }

    /**
     * 首页根据设施IdList查询设施基本信息
     *
     * @param deviceIds 设施IdList
     * @return 设施基本信息
     */
    @PostMapping("/queryHomeDeviceByIds")
    public Result queryHomeDeviceByIds(@RequestBody List<String> deviceIds) {
        if (CollectionUtils.isEmpty(deviceIds)) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.PARAMETER_ERROR));
        }
        return deviceInfoService.queryHomeDeviceByIds(deviceIds);
    }

    /**
     * 修改首页首次加载阈值（设施数量）
     *
     * @param homeDeviceLimit 首页首次加载阈值
     */
    @PutMapping("/updateHomeDeviceLimit/{homeDeviceLimit}")
    public void updateHomeDeviceLimit(@PathVariable("homeDeviceLimit") Integer homeDeviceLimit) {
        if (homeDeviceLimit == null) {
            return;
        }
        deviceInfoService.updateHomeDeviceLimit(homeDeviceLimit);
    }

    /**
     * 首页查询用户所有设施基本信息
     *
     * @return 设施基本信息
     */
    @GetMapping("/queryHomeDeviceArea")
    public Result queryHomeDeviceArea() {
        return deviceInfoService.queryHomeDeviceArea();
    }

    /**
     * 首页刷新用户所有设施信息
     *
     * @return 首页设施信息
     */
    @GetMapping("/refreshHomeDeviceArea")
    public Result refreshHomeDeviceArea() {
        return deviceInfoService.refreshHomeDeviceArea();
    }

    /**
     * 首页刷新用户指定设施信息（大数据）
     *
     * @param areaIdList 首页刷新的区域
     * @return 首页设施信息
     */
    @PostMapping("/refreshHomeDeviceAreaHuge")
    public Result refreshHomeDeviceAreaHuge(@RequestBody List<String> areaIdList) {
        if (CollectionUtils.isEmpty(areaIdList)) {
            throw new FiLinkDeviceException(I18nUtils.getSystemString(DeviceI18n.PARAMETER_ERROR));
        }
        return deviceInfoService.refreshHomeDeviceAreaHuge(areaIdList);
    }

    /**
     * 删除设施
     *
     * @param ids 设施ids
     * @return Result
     */
    @PostMapping("/deleteDeviceByIds")
    public Result deleteDeviceByIds(@RequestBody String[] ids) {
        return deviceInfoService.deleteDeviceByIds(ids);
    }

    /**
     * 设施是否可以修改
     *
     * @param deviceId 设施id
     * @return Result
     */
    @GetMapping("/deviceCanChangeDetail/{deviceId}")
    public Result deviceCanChangeDetail(@PathVariable String deviceId) {
        if (deviceInfoService.deviceCanChangeDetail(deviceId)) {
            return ResultUtils.success();
        }
        return ResultUtils.warn(DeviceResultCode.DEVICE_CANNOT_UPDATE, I18nUtils.getSystemString(DeviceI18n.DEVICE_CANNOT_UPDATE));
    }

    /**
     * 导出设施列表
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @PostMapping("/exportDeviceList")
    public Result exportDeviceList(@RequestBody ExportDto exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(DeviceResultCode.EXPORT_PARAM_NULL, I18nUtils.getSystemString(DeviceI18n.EXPORT_PARAM_NULL));
        }
        return deviceInfoService.exportDeviceList(exportDto);
    }

    /**
     * 请求附近设施列表
     *
     * @param deviceReqPda 请求参数
     * @return 返回结果
     */
    @PostMapping("/queryNearbyDeviceListForPda")
    public Result queryNearbyDeviceListForPda(@RequestBody DeviceReqPda deviceReqPda) {
        return deviceInfoService.queryNearbyDeviceListForPda(deviceReqPda);
    }

    /**
     * 更改指定设施的设施状态，部署状态
     *
     * @param deviceInfoDto 更新信息参数
     * @return
     * @throws Exception
     */
    @PostMapping("/feign/updateDeviceStatus")
    public Result updateDeviceStatus(@RequestBody DeviceInfoDto deviceInfoDto) throws Exception {
        return deviceInfoService.updateDeviceStatus(deviceInfoDto);
    }

    /**
     * 更改指定设施Ids的部署状态
     *
     * @param updateDeviceStatusPda 更新信息参数
     * @return
     * @throws Exception
     */
    @PostMapping("/feign/updateDeviceListStatus")
    public Result updateDeviceListStatus(@RequestBody UpdateDeviceStatusPda updateDeviceStatusPda) {
        return deviceInfoService.updateDeviceListStatus(updateDeviceStatusPda);
    }

    /**
     * 根据区域ID集合查询设施类型集合
     *
     * @param areaIds 区域ID集合
     * @return 设施类型集合返回值
     */
    @PostMapping("/queryDeviceTypesByAreaIds")
    public Result queryDeviceTypesByAreaIds(@RequestBody List<String> areaIds) {
        List<String> deviceTypeList = deviceInfoService.queryDeviceTypesByAreaIds(areaIds);
        return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getSystemString(DeviceI18n.QUERY_DEVICE_SUCCESS), deviceTypeList);
    }

    /**
     * 根据区域id和设施类型查询设施信息
     *
     * @param deviceParam
     * @return
     */
    @PostMapping("/feign/queryDeviceInfoBaseByParam")
    public List<DeviceInfoBase> queryDeviceInfoBaseByParam(@RequestBody DeviceParam deviceParam) {
        return deviceInfoService.queryDeviceInfoBaseByParam(deviceParam);
    }

    /**
     * 根据设施id获取设施名称
     */
    @GetMapping("/feign/queryDeviceNameById/{deviceId}")
    public String queryDeviceNameById(@PathVariable("deviceId") String deviceId) {
        return deviceInfoService.queryDeviceNameById(deviceId);
    }

    /**
     * 根据设施id获取设施名称
     */
    @GetMapping("/feign/queryDeviceTypeById/{deviceId}")
    public String queryDeviceTypeById(@PathVariable("deviceId") String deviceId) {
        return deviceInfoService.queryDeviceTypeById(deviceId);
    }

}
