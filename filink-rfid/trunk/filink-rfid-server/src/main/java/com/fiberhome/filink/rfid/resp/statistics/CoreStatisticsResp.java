package com.fiberhome.filink.rfid.resp.statistics;

/**
 * 纤芯统计返回
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/28
 */
public class CoreStatisticsResp {
    /**
     * 使用数量
     */
    private int usedCount;
    /**
     * 未用数量
     */
    private int unusedCount;
    /**
     * 总数量
     */
    private int totalCount;

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public int getUnusedCount() {
        return unusedCount;
    }

    public void setUnusedCount(int unusedCount) {
        this.unusedCount = unusedCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
