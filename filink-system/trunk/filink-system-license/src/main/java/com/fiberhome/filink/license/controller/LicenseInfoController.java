package com.fiberhome.filink.license.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.license.bean.License;
import com.fiberhome.filink.license.bean.LicenseFeignBean;
import com.fiberhome.filink.license.bean.LicenseThresholdFeignBean;
import com.fiberhome.filink.license.exception.FilinkLicenseException;
import com.fiberhome.filink.license.service.LicenseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gzp
 * @since 2019-02-19
 */
@RestController
@RequestMapping("/licenseInfo")
public class LicenseInfoController {

	@Autowired
	private LicenseInfoService licenseInfoService;

	/**
	 * 上传license文件
	 *
	 * @param file license文件
	 * @return Result
	 * @throws Exception 异常
	 */
	@PostMapping("/uploadLicense")
	public Result uploadLicense(MultipartFile file) throws Exception {
		return licenseInfoService.uploadLicense(file);
	}

	/**
	 * 上传license文件（admin）
	 *
	 * @param file license文件
	 * @return Result
	 * @throws Exception 异常
	 */
	@PostMapping("/uploadLicenseForAdmin")
	public Result uploadLicenseForAdmin(MultipartFile file) throws Exception {
		return licenseInfoService.uploadLicense(file);
	}

	/**
	 * 获取license详情
	 * 
	 * @return Result
	 * @throws Exception 异常
	 */
	@GetMapping("/getLicenseDetail")
	public Result getLicenseDetail() throws Exception {
		return licenseInfoService.getLicenseDetail();
	}

	/**
	 * 从Redis和数据库中获取当前License信息
	 * @return 当前License信息，不包含LicenseThreshold
	 */
	@GetMapping("/feign/getCurrentLicense")
	public License getCurrentLicense() {
		return licenseInfoService.getCurrentLicense();
	}

	/**
	 * 校验过期时间
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/validateLicenseTime")
	public Result validateLicenseTime() {
		Map<String, Boolean> map = new HashMap<>(64);
		boolean b = licenseInfoService.validateLicenseTime();
		map.put("licenseStatus", b);
		return ResultUtils.success(map);
	}

	/**
	 * 增删操作Redis的license活跃值
	 * 
	 * @param licenseFeignBean LicenseFeignBean
	 * @return boolean
	 * @throws Exception 异常
	 */
	@PostMapping("/updateRedisLicenseThreshold")
	public boolean updateRedisLicenseThreshold(@RequestBody LicenseFeignBean licenseFeignBean) throws Exception {
		return licenseInfoService.updateRedisLicenseThreshold(licenseFeignBean);
	}

	/**
	 * 同步LicenseThreshold
	 *
	 * @param licenseThresholdFeignBean LicenseThresholdFeignBean
	 * @return boolean
	 */
	@PostMapping("/synchronousLicenseThreshold")
	public boolean synchronousLicenseThreshold(@RequestBody LicenseThresholdFeignBean licenseThresholdFeignBean) {
		return licenseInfoService.synchronousLicenseThreshold(licenseThresholdFeignBean);
	}
}
