package com.fiberhome.filink.rfid.req.statistics.odnfacilityresources;

import java.util.List;

/**
 * <p>
 * ONT设施资源统计请求  盘端口统计
 * </p>
 *
 * @author congcongsun2
 * @since 2019-06-10
 */
public class DiscPortStatisticsReq {

    /**
     * 设施id
     */
    private String deviceId;
    /**
     * 盘id
     */
    private List<String> plateIds;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<String> getPlateIds() {
        return plateIds;
    }

    public void setPlateIds(List<String> plateIds) {
        this.plateIds = plateIds;
    }
}
