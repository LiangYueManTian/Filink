package com.fiberhome.filink.fdevice.service.device;

import com.fiberhome.filink.bean.Result;

/**
 * 设施配置接口
 * @author CongcaiYu
 */
public interface DeviceConfigService {

    /**
     * 根据设施id获取详情模块code
     * @param deviceType 设施类型
     * @return 模块code
     */
    Result getDetailCode(String deviceType);

    /**
     * 根据设施id获取参数下发配置项
     * @param deviceType 设施类型
     * @return 配置项
     */
    Result getParamsConfig(String deviceType);

    /**
     * 加载配置文件到缓存
     * @return
     */
    void initDeviceConfigToRedis();
}
