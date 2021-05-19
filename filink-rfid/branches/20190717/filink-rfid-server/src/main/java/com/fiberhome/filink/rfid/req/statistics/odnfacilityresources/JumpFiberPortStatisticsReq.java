package com.fiberhome.filink.rfid.req.statistics.odnfacilityresources;

import java.util.List;

/**
 * <p>
 * ONT设施资源统计请求   跳纤侧端口统计
 * </p>
 *
 * @author congcongsun2
 * @since 2019-06-10
 */
public class JumpFiberPortStatisticsReq {
    /**
     * 设施对象
     */
    private List<String> facilities;

    public List<String> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }
}
