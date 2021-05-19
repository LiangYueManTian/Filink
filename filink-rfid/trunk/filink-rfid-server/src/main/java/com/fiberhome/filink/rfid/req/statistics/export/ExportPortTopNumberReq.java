package com.fiberhome.filink.rfid.req.statistics.export;

/**
 * 端口使用率TopN导出数据格式
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/21
 */
public class ExportPortTopNumberReq {
    /**
     * 排名
     */
    private String ranking;
    /**
     * 设施名称
     */
    private String deviceName;
    /**
     * 所属地区
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

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccountabilityUnitName() {
        return accountabilityUnitName;
    }

    public void setAccountabilityUnitName(String accountabilityUnitName) {
        this.accountabilityUnitName = accountabilityUnitName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
