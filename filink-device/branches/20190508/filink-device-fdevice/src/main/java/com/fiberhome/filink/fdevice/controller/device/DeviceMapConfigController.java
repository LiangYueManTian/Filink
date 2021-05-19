package com.fiberhome.filink.fdevice.controller.device;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.fdevice.bean.device.DeviceMapConfig;
import com.fiberhome.filink.fdevice.service.device.DeviceMapConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * <p>
 *  首页地图和设施类型配置前端控制器
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-01-19
 */
@RestController
@RequestMapping("/deviceMapConfig")
public class DeviceMapConfigController {

    /**
     * 自动注入地图设施类型配置service对象
     */
    @Autowired
    private DeviceMapConfigService deviceMapConfigService;

    /**
     *通过用户ID查询用户所有地图设施类型配置信息
     *
     *
     * @return 查询结果
     */
    @GetMapping("/queryDeviceConfigAll")
    public Result queryMapDeviceConfigAll() {
        return deviceMapConfigService.queryDeviceMapConfig();
    }

    /**
     *修改用户地图设施类型配置的设施类型启用状态
     *
     * @param deviceMapConfigs 地图设施类型配置信息list
     * @return 查询结果
     */
    @PostMapping("/updateDeviceTypeStatusAll")
    public Result updateDeviceTypeStatusAll(@RequestBody List<DeviceMapConfig> deviceMapConfigs){
        return deviceMapConfigService.bathUpdateDeviceConfig(deviceMapConfigs);
    }

    /**
     *修改用户地图设施类型配置的设施图标尺寸
     *
     * @param deviceMapConfig 地图设施类型配置信息
     * @return 查询结果
     */
    @PostMapping("/updateDeviceIconSize")
    public Result updateDeviceIconSize(@RequestBody DeviceMapConfig deviceMapConfig){
        return deviceMapConfigService.updateDeviceIconSize(deviceMapConfig);
    }

    /**
     * 批量插入用户首页地图设施配置信息
     *
     * @param userId 用户Id
     * @return 插入条数
     */
    @PostMapping("/insertConfig/{userId}")
    public boolean insertConfigBatch(@PathVariable(value = "userId") String userId){
        return deviceMapConfigService.insertConfigBatch(userId);
    }

    /**
     * 批量插入多个用户首页地图设施配置信息
     *
     * @param userId 用户Id
     * @return 插入条数
     */
    @PostMapping("/insertConfigBatch")
    public boolean insertConfigBatchUsers(@RequestBody List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return false;
        }
        return deviceMapConfigService.insertConfigBatchUsers(userIds);
    }

    /**
     * 删除用户所有配置信息
     *
     * @param userIds 用户Id List
     * @return 成功
     */
    @DeleteMapping("/deletedConfig")
    public boolean deletedConfigByUserIds(@RequestBody List<String> userIds){
        return deviceMapConfigService.deletedConfigByUserIds(userIds);
    }

}
