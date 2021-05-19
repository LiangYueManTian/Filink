package com.fiberhome.filink.deviceapi.api;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoBase;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.deviceapi.bean.DeviceParam;
import com.fiberhome.filink.deviceapi.bean.UpdateDeviceStatusPda;
import com.fiberhome.filink.deviceapi.fallback.DeviceFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zepenggao@wistronits.com
 * @date 2019/2/15 16:34
 */
@FeignClient(name = "filink-device-server", path = "/deviceInfo", fallback = DeviceFeignFallback.class)
public interface DeviceFeign {

    /**
     * 根据ID查询设施
     *
     * @param id 设施信息
     * @return
     * @throws Exception
     */
    @GetMapping("/feign/findDeviceById/{id}")
    DeviceInfoDto getDeviceById(@PathVariable("id") String id);
    /**
     * 修改首页首次加载阈值（设施数量）
     * @param homeDeviceLimit 首页首次加载阈值
     */
    @PutMapping("/updateHomeDeviceLimit/{homeDeviceLimit}")
    void updateHomeDeviceLimit(@PathVariable("homeDeviceLimit") Integer homeDeviceLimit);
    /**
     * 根据ID数组查询设施
     *
     * @param ids 设施信息
     * @return
     * @throws Exception
     */
    @PostMapping("/feign/getDeviceByIds")
    List<DeviceInfoDto> getDeviceByIds(@RequestBody String[] ids);

    /**
     * 分页查询设施列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     * @throws Exception
     */
    @PostMapping("/feign/deviceListByPage")
    Result deviceListByPage(@RequestBody QueryCondition<DeviceInfoDto> queryCondition);

    /**
     * 查询当前设施数
     *
     * @return
     */
    @GetMapping("/feign/queryCurrentDeviceCount")
    Integer queryCurrentDeviceCount();

    /**
     * 新增设施
     *
     * @param deviceInfoDto 设施dto
     * @return
     * @throws Exception
     */
    @PostMapping("/feign/addDevice")
    Result addDevice(@RequestBody DeviceInfoDto deviceInfoDto) throws Exception;

    /**
     * 更改指定设施的设施状态，部署状态
     *
     * @param deviceInfoDto
     * @return
     * @throws Exception
     */
    @PostMapping("/feign/updateDeviceStatus")
    Result updateDeviceStatus(@RequestBody DeviceInfoDto deviceInfoDto) throws Exception;

    /**
     * 更改指定设施Ids的部署状态
     *
     * @param updateDeviceStatusPda 更新信息参数
     * @return
     * @throws Exception
     */
    @PostMapping("/feign/updateDeviceListStatus")
    Result updateDeviceListStatus(@RequestBody UpdateDeviceStatusPda updateDeviceStatusPda);

    /**
     * 获取默认配置值
     *
     * @param deviceId
     * @return
     */
    @GetMapping("/feign/getDefaultParams/{deviceId}")
    String getDefaultParams(@PathVariable("deviceId") String deviceId);

    /**
     * 根据区域id和设施类型查询设施
     *
     * @param deviceParam
     * @return
     */
    @PostMapping("/feign/queryDeviceDtoByParam")
    List<DeviceInfoDto> queryDeviceDtoByParam(@RequestBody DeviceParam deviceParam);

    /**
     * 根据区域id和设施类型查询设施信息
     *
     * @param deviceParam
     * @return
     */
    @PostMapping("/feign/queryDeviceInfoBaseByParam")
    List<DeviceInfoBase> queryDeviceInfoBaseByParam(@RequestBody DeviceParam deviceParam);
}
