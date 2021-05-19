package com.fiberhome.filink.license.bean;

import lombok.Data;

/**
 * 缓存中关于license限制的使用量
 * @author zepenggao@wistronits.com
 * @date 2019/2/21 10:24
 */
@Data
public class LicenseThreshold {
	public String userNum;
	public String onlineNum;
	public String deviceNum;

	public LicenseThreshold(String userNum, String onlineNum, String deviceNum) {
		this.userNum = userNum;
		this.onlineNum = onlineNum;
		this.deviceNum = deviceNum;
	}
}
