package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.Data;
import net.sf.json.JSONObject;

/**
 * <P>
 * 告警次数导出字段
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class DeviceNameExport {

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
     * 责任单位
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
