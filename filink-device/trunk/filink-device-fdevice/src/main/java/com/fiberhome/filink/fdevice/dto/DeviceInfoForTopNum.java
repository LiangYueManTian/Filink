package com.fiberhome.filink.fdevice.dto;

import lombok.Data;
import net.sf.json.JSONObject;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/25 9:46
 * @Description: com.fiberhome.filink.fdevice.dto
 * @version: 1.0
 */
@Data
public class DeviceInfoForTopNum {

    /**
     * 排名
     */
    private Integer ranking;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 责任单位名称
     */
    private String accountabilityUnitName;

    /**
     * 部署状态
     */
    private String status;

    public void jsonToBean(JSONObject json) {
        deviceName = json.getString("deviceName");
        areaName = json.getString("areaName");
        address = json.getString("address");
        accountabilityUnitName = json.getString("accountabilityUnitName");
        status = json.getString("status");
        ranking = json.getInt("ranking");
    }
}
