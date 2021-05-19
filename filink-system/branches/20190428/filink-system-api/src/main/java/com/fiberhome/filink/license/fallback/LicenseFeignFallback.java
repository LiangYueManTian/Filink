package com.fiberhome.filink.license.fallback;

import com.fiberhome.filink.license.api.LicenseFeign;
import com.fiberhome.filink.license.bean.License;
import com.fiberhome.filink.license.bean.LicenseFeignBean;
import com.fiberhome.filink.license.bean.LicenseThresholdFeignBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zepenggao@wistronits.com
 * @date 2019/2/21 11:14
 */
@Slf4j
@Component
public class LicenseFeignFallback implements LicenseFeign {

	private static final String THE_REQUEST_TIMEOUT = "请求超时";

	@Override
	public License getCurrentLicense() {
		log.info("feign调用熔断》》》》》》》》》》");
		throw new RuntimeException(THE_REQUEST_TIMEOUT);
	}

	@Override
	public boolean validateLicenseTime() throws Exception {
		log.info("feign调用熔断》》》》》》》》》》");
		throw new RuntimeException(THE_REQUEST_TIMEOUT);
	}

	@Override
	public boolean updateRedisLicenseThreshold(LicenseFeignBean licenseFeignBean) {
		log.info("feign调用熔断》》》》》》》》》》");
		throw new RuntimeException(THE_REQUEST_TIMEOUT);
	}

	@Override
	public boolean synchronousLicenseThreshold(LicenseThresholdFeignBean licenseThresholdFeignBean) {
		log.info("feign调用熔断》》》》》》》》》》");
		throw new RuntimeException(THE_REQUEST_TIMEOUT);
	}
}
