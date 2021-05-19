package com.fiberhome.filink.fdevice.controller.config;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.service.device.DeviceConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 设施配置控制器
 *
 * @author CongcaiYu
 */
@RestController
@RequestMapping("/deviceConfig")
public class DeviceConfigController {

    @Autowired
    private DeviceConfigService deviceConfigService;

    /**
     * 根据设施id获取详情模块code
     * <p>
     * aram deviceInfo 设施信息
     *
     * @return 模块code
     */
    @PostMapping("/getDetailCode")
    public Result getDetailCode(@RequestBody DeviceInfo deviceInfo) {
        return deviceConfigService.getDetailCode(deviceInfo);
    }

    /**
     * 根据设施id获取参数下发配置项
     *
     * @param deviceType 设施类型
     * @return 配置项信息
     */
    @GetMapping("/getPramsConfig/{deviceType}")
    public Result getParamsConfig(@PathVariable String deviceType) {
        return deviceConfigService.getParamsConfig(deviceType);
    }

    /**
     * 获取设施配置字段的校验规则
     *
     * @return <字段 字段检验规则>集合
     */
    @GetMapping("/getConfigPatterns")
    public Map<String, String> getConfigPatterns() {
        return deviceConfigService.getConfigPatterns();
    }
}
