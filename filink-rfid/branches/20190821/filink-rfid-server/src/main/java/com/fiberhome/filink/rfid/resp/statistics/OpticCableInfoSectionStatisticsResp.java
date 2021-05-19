package com.fiberhome.filink.rfid.resp.statistics;

/**
 * <p>
 * 光缆段和纤芯统计返回类
 * </p>
 *
 * @author congcongsun2
 * @since 2019-05-30
 */
public class OpticCableInfoSectionStatisticsResp {
    /**
     * 使用数量
     */
    private int used;
    /**
     * 未用数量
     */
    private int unused;
    /**
     * 总数量
     */
    private int totalCount;

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public int getUnused() {
        return unused;
    }

    public void setUnused(int unused) {
        this.unused = unused;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
