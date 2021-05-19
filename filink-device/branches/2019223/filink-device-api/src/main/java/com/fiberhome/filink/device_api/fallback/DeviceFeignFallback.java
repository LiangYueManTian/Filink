package com.fiberhome.filink.device_api.fallback;

import com.fiberhome.filink.device_api.api.DeviceFeign;
import com.fiberhome.filink.device_api.bean.DeviceInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 设施模块调用异常处理
 *
 * @author zepenggao@wistronits.com
 * @date 2019/2/15 16:35
 */
@Slf4j
@Component
public class DeviceFeignFallback implements DeviceFeign {
	/**
	 * 根据序列号查询
	 * @param id 设施信息
	 * @return
	 * @throws Exception
	 */
	@Override
	public DeviceInfoDto findDeviceBySeriaNumber(String id) throws Exception {
		log.warn("》》》》》》》》》》》》feign调用熔断");
		return null;
	}

	/**
	 * 查询设施信息
	 * @param id 设施id
	 * @return
	 * @throws Exception
	 */
	@Override
	public DeviceInfoDto getDeviceById(String id) throws Exception {
		log.warn("》》》》》》》》》》》》feign调用熔断");
		return null;
	}
}
