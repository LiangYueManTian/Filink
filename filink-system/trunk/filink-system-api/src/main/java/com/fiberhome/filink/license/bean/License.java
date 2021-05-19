package com.fiberhome.filink.license.bean;

/**
 * License
 *
 * @Author: zl
 * @Date: 2019/4/10 15:59
 * @Description: com.fiberhome.filink.license.bean
 * @version: 1.0
 */
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

    public License() {
    }

    public License(String maxUserNum, String maxOnlineNum, String maxDeviceNum) {
        this.maxUserNum = maxUserNum;
        this.maxOnlineNum = maxOnlineNum;
        this.maxDeviceNum = maxDeviceNum;
    }
}
