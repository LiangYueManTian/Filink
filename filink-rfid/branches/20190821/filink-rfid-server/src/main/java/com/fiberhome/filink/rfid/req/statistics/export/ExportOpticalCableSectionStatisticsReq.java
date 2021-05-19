package com.fiberhome.filink.rfid.req.statistics.export;

/**
 * 光缆段和纤芯统计导出数据格式
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/20
 */

public class ExportOpticalCableSectionStatisticsReq {
    /**
     * 类别
     */
    private String tableName;
    /**
     * 使用数量
     */
    private String usedCount;
    /**
     * 未用数量
     */
    private String unusedCount;
    /**
     * 总数
     */
    private String totalCount;

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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }
}
