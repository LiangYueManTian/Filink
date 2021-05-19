package com.fiberhome.filink.rfid.resp.statistics;

/**
 * <p>
 * ONT设施资源统计请求   跳纤侧端口统计返回类
 * </p>
 *
 * @author congcongsun2
 * @since 2019-05-30
 */
public class JumpFiberPortStatisticsResp {
    /**
     * 占用端口数量
     */
    private int usedCount;
    /**
     * 空闲端口数量
     */
    private int unusedCount;
    /**
     * 异常端口数量
     */
    private int exceptionCount;
    /**
     * 预占端口数量
     */
    private int advanceCount;
    /**
     * 虚占端口数量
     */
    private int virtualCount;
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

    public int getExceptionCount() {
        return exceptionCount;
    }

    public void setExceptionCount(int exceptionCount) {
        this.exceptionCount = exceptionCount;
    }

    public int getAdvanceCount() {
        return advanceCount;
    }

    public void setAdvanceCount(int advanceCount) {
        this.advanceCount = advanceCount;
    }

    public int getVirtualCount() {
        return virtualCount;
    }

    public void setVirtualCount(int virtualCount) {
        this.virtualCount = virtualCount;
    }

    public int getTotalCount() {
        return usedCount + unusedCount + exceptionCount + advanceCount + virtualCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
