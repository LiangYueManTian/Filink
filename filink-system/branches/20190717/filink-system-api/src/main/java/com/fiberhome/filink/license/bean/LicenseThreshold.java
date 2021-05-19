package com.fiberhome.filink.license.bean;

/**
 * 缓存中关于license限制的使用量
 *
 * @Author: zl
 * @Date: 2019/4/10 16:00
 * @Description: com.fiberhome.filink.license.bean
 * @version: 1.0
 */
public class LicenseThreshold {
    public String userNum;
    public String onlineNum;
    public String deviceNum;

    public LicenseThreshold(String userNum, String onlineNum, String deviceNum) {
        this.userNum = userNum;
        this.onlineNum = onlineNum;
        this.deviceNum = deviceNum;
    }

    public LicenseThreshold() {
    }
}
