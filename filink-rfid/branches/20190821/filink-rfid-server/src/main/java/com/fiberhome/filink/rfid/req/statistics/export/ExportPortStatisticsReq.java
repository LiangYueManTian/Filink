package com.fiberhome.filink.rfid.req.statistics.export;

/**
 * 端口状态统计导出数据格式
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/20
 */
public class ExportPortStatisticsReq {
    /**
     * 类别
     */
    private String tableName;
    /**
     * 占用端口数量
     */
    private String usedCount;
    /**
     * 空闲端口数量
     */
    private String unusedCount;
    /**
     * 异常端口数量
     */
    private String exceptionCount;
    /**
     * 预占端口数量
     */
    private String advanceCount;
    /**
     * 虚占端口数量
     */
    private String virtualCount;
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

    public String getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(String usedCount) {
        this.usedCount = usedCount;
    }

    public String getUnusedCount() {
        return unusedCount;
    }

    public void setUnusedCount(String unusedCount) {
        this.unusedCount = unusedCount;
    }

    public String getExceptionCount() {
        return exceptionCount;
    }

    public void setExceptionCount(String exceptionCount) {
        this.exceptionCount = exceptionCount;
    }

    public String getAdvanceCount() {
        return advanceCount;
    }

    public void setAdvanceCount(String advanceCount) {
        this.advanceCount = advanceCount;
    }

    public String getVirtualCount() {
        return virtualCount;
    }

    public void setVirtualCount(String virtualCount) {
        this.virtualCount = virtualCount;
    }
}
