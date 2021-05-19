package com.fiberhome.filink.license.api;

import com.fiberhome.filink.license.bean.License;
import com.fiberhome.filink.license.bean.LicenseFeignBean;
import com.fiberhome.filink.license.bean.LicenseThresholdFeignBean;
import com.fiberhome.filink.license.fallback.LicenseFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zepenggao@wistronits.com
 * @date 2019/2/21 11:13
 */
@FeignClient(name = "filink-system-server", path = "/licenseInfo", fallback = LicenseFeignFallback.class)
public interface LicenseFeign {

	/**
	 * 从Redis和数据库中获取当前License信息
	 * @return 当前License信息，不包含LicenseThreshold
	 */
	@GetMapping("/feign/getCurrentLicense")
	License getCurrentLicense() ;

	/**
	 * 校验过期时间
	 *
	 * @return boolean
	 * @throws Exception 异常
	 */
	@GetMapping("/validateLicenseTime")
	boolean validateLicenseTime() throws Exception;

	/**
	 * 增删操作Redis的license活跃值
	 *
	 * @param licenseFeignBean LicenseFeignBean
	 * @return boolean
	 * @throws Exception 异常
	 */
	@PostMapping("/updateRedisLicenseThreshold")
	boolean updateRedisLicenseThreshold(@RequestBody LicenseFeignBean licenseFeignBean) throws Exception;

	/**
	 * 同步LicenseThreshold
	 *
	 * @param licenseThresholdFeignBean LicenseThresholdFeignBean
	 * @return boolean
	 */
	@PostMapping("/synchronousLicenseThreshold")
	boolean synchronousLicenseThreshold(@RequestBody LicenseThresholdFeignBean licenseThresholdFeignBean);
}
