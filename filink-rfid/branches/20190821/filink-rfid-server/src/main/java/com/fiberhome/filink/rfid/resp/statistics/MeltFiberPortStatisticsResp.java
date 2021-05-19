package com.fiberhome.filink.rfid.resp.statistics;
/**
 * <p>
 * 熔纤侧端口统计返回类
 * </p>
 *
 * @author congcongsun2
 * @since 2019-05-30
 */
public class MeltFiberPortStatisticsResp {
    /**
     * 使用数量
     */
    private int usedPortCount;
    /**
     * 未用数量
     */
    private int unusedPortCount;
    /**
     * 总数量
     */
    private int totalCount;

    public int getUsedPortCount() {
        return usedPortCount;
    }

    public void setUsedPortCount(int usedPortCount) {
        this.usedPortCount = usedPortCount;
    }

    public int getUnusedPortCount() {
        return unusedPortCount;
    }

    public void setUnusedPortCount(int unusedPortCount) {
        this.unusedPortCount = unusedPortCount;
    }

    public int getTotalCount() {
        return unusedPortCount + usedPortCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
