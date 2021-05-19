package com.fiberhome.filink.license.bean;

import lombok.Data;

/**
 * License
 * @author zepenggao@wistronits.com
 * @date 2019/2/18 17:03
 */
@Data
public class License {
	public String tryRemark;
	public String beginTime;
	public String endTime;
	public String maxUserNum;
	public String maxOnlineNum;
	public String maxDeviceNum;
	public LicenseThreshold licenseThreshold;

	public License(String tryRemark, String beginTime, String endTime, String maxUserNum, String maxOnlineNum,
			String maxDeviceNum) {
		this.tryRemark = tryRemark;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.maxUserNum = maxUserNum;
		this.maxOnlineNum = maxOnlineNum;
		this.maxDeviceNum = maxDeviceNum;
	}
	public License(){}

	public License(String maxUserNum, String maxOnlineNum, String maxDeviceNum) {
		this.maxUserNum = maxUserNum;
		this.maxOnlineNum = maxOnlineNum;
		this.maxDeviceNum = maxDeviceNum;
	}
}
