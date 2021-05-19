package com.fiberhome.filink.rfid.req.statistics.export;

/**
 * 熔纤侧端口统计导出数据格式
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/20
 */
public class ExportMeltFiberPortStatisticsReq {
    /**
     * 成端
     */
    private String usedPortCount;
    /**
     * 未成端
     */
    private String unusedPortCount;
    /**
     * 类别
     */
    private String tableName;
    /**
     * 总数
     */
    private String totalCount;

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String getUsedPortCount() {
        return usedPortCount;
    }

    public void setUsedPortCount(String usedPortCount) {
        this.usedPortCount = usedPortCount;
    }

    public String getUnusedPortCount() {
        return unusedPortCount;
    }

    public void setUnusedPortCount(String unusedPortCount) {
        this.unusedPortCount = unusedPortCount;
    }
}
