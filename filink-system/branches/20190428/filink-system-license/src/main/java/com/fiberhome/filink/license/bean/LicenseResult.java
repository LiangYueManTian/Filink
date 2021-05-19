package com.fiberhome.filink.license.bean;

/**
 * @author zepenggao@wistronits.com
 * @date 2019/2/28 15:13
 */
public class LicenseResult {
	public String controlName;
	public String  controlDesc;
	public String controlData ;
	public String controlValue;

	public LicenseResult() {
	}

	public LicenseResult(String controlName, String controlDesc, String controlData) {
		this.controlName = controlName;
		this.controlDesc = controlDesc;
		this.controlData = controlData;
	}

	public LicenseResult(String controlName, String controlDesc, String controlData, String controlValue) {
		this.controlName = controlName;
		this.controlDesc = controlDesc;
		this.controlData = controlData;
		this.controlValue = controlValue;
	}
}
