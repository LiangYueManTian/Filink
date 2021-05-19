package com.fiberhome.filink.device_api.api;

import com.fiberhome.filink.device_api.bean.DeviceInfoDto;
import com.fiberhome.filink.device_api.fallback.DeviceFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 设施Feign
 * @author zepenggao@wistronits.com
 * @date 2019/2/15 16:34
 */
@FeignClient(name = "filink-device-server", fallback = DeviceFeignFallback.class)
public interface DeviceFeign {
	/**
	 * 根据序列号查询
	 * @param id 设施信息
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/deviceInfo/findDeviceBySeriaNumber/{id}")
	DeviceInfoDto findDeviceBySeriaNumber(@PathVariable("id") String id) throws Exception;

	/**
	 * 查询设施信息
	 * @param id 设施id
	 * @return DeviceInfoDto
	 * @throws Exception
	 */
	@GetMapping("/deviceInfo/feign/findDeviceById/{id}")
	DeviceInfoDto getDeviceById(@PathVariable("id") String id) throws Exception;
}
