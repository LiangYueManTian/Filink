package com.fiberhome.filink.fdevice.controller.config;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.fdevice.service.device.DeviceConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设施配置控制器
 * @author CongcaiYu
 */
@RestController
@RequestMapping("/deviceConfig")
public class DeviceConfigController {

    @Autowired
    private DeviceConfigService deviceConfigService;

    /**
     * 根据设施id获取详情模块code
     * @param deviceType 设施类型
     * @return 模块code
     */
    @GetMapping("/getDetailCode/{deviceType}")
    public Result getDetailCode(@PathVariable String deviceType){
        return deviceConfigService.getDetailCode(deviceType);
    }

    /**
     * 根据设施id获取参数下发配置项
     * @param deviceType 设施类型
     * @return 配置项信息
     */
    @GetMapping("/getPramsConfig/{deviceType}")
    public Result getParamsConfig(@PathVariable String deviceType){
        return deviceConfigService.getParamsConfig(deviceType);
    }

}
