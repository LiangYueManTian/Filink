package com.fiberhome.filink.fdevice.service.device;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;

import java.util.Map;

/**
 * 设施配置接口
 *
 * @author CongcaiYu
 */
public interface DeviceConfigService {

    /**
     * 根据设施id获取详情模块code
     *
     * @param deviceInfo 设施id
     * @return 模块code
     */
    Result getDetailCode(DeviceInfo deviceInfo);

    /**
     * 根据设施id获取参数下发配置项
     *
     * @param deviceType 设施类型
     * @return 配置项
     */
    Result getParamsConfig(String deviceType);

    /**
     * 加载配置文件到缓存
     *
     * @return
     */
    void initDeviceConfigToRedis();

    /**
     * 得到配置的校验正则表达式
     *
     * @return 正则表达式
     */
    Map<String, String> getConfigPatterns();

    /**
     * 获取配置默认值
     *
     * @param deviceType 设施类型
     * @return配置默认值
     */
    Map<String, String> getDefaultParams(String deviceType);
}
