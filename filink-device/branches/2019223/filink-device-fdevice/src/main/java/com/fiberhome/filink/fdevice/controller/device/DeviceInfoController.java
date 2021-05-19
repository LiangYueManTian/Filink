package com.fiberhome.filink.fdevice.controller.device;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.fdevice.bean.DeviceI18n;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.dto.DeviceInfoDto;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.utils.DeviceResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;


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
     * 校验设施名称是否重复
     *
     * @param deviceInfo 设施id和设施名称
     * @return Result
     */
    @PostMapping("/queryDeviceNameIsExsit")
    public Result checkDeviceName(@RequestBody DeviceInfo deviceInfo) {
        if (ObjectUtils.isEmpty(deviceInfo)) {
            return ResultUtils.success(DeviceResultCode.DEVICE_PARAM_ERROT, I18nUtils.getString(DeviceI18n.DEVICE_NAME_NULL), false);
        }
        boolean isExist = deviceInfoService.checkDeviceName(deviceInfo.getDeviceId(), deviceInfo.getDeviceName());
        if (isExist) {
            return ResultUtils.success(DeviceResultCode.DEVICE_NAME_SAME, I18nUtils.getString(DeviceI18n.DEVICE_NAME_SAME), false);
        }
        return ResultUtils.success(DeviceResultCode.SUCCESS, I18nUtils.getString(DeviceI18n.DEVICE_NAME_AVAILABLE), true);
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
     * 分页查询设施列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @PostMapping("deviceListByPage")
    public Result listDevice(@RequestBody QueryCondition<DeviceInfo> queryCondition) throws Exception {
        return deviceInfoService.listDevice(queryCondition);
    }

    /**
     * 获取设施详情
     *
     * @param id 设施id
     * @return Result
     * @throws Exception 异常
     */
    @GetMapping("/findDeviceById/{id}")
    public Result getDeviceById(@PathVariable String id) throws Exception {
        return deviceInfoService.getDeviceById(id);
    }

    /**
     * 获取设施详情
     *
     * @param id 设施id
     * @return Result
     * @throws Exception 异常
     */
    @GetMapping("/feign/findDeviceById/{id}")
    public DeviceInfoDto feignGetDeviceById(@PathVariable String id) throws Exception {
        Result result = deviceInfoService.getDeviceById(id);
        return (DeviceInfoDto) result.getData();
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
     * 根据序列号查询设施
     *
     * @param id 设施序列号
     * @return Result
     * @throws Exception 异常
     */
    @GetMapping("/findDeviceBySeriaNumber/{id}")
    public DeviceInfoDto findDeviceBySeriaNumber(@PathVariable String id) throws Exception {
        return deviceInfoService.findDeviceBySeriaNumber(id);
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
        return ResultUtils.warn(DeviceResultCode.DEVICE_CANNOT_UPDATE, I18nUtils.getString(DeviceI18n.DEVICE_CANNOT_UPDATE));
    }


}
