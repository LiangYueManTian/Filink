package com.fiberhome.filink.rfid.resp.statistics;

/**
 * <p>
 * 光缆级别统计返回类
 * </p>
 *
 * @author congcongsun2
 * @since 2019-05-30
 */
public class OpticCableInfoStatisticsResp {

    /**
     * 本地接入-主干光缆
     */
    private int trunkCount;
    /**
     * 本地接入-末端光缆
     */
    private int terminalCount;
    /**
     * 一级干线
     */
    private int stairCount;

    /**
     * 二级干线数量
     */
    private int secondaryCount;

    /**
     * 本地中继数量
     */
    private int relayCount;

    /**
     * 本地核心数量
     */
    private int coreCount;
    /**
     * 本地汇聚数量
     */
    private int convergeCount;
    /**
     * 汇接层光缆
     */
    private int tandemCount;
    /**
     * 联络光缆
     */
    private int contactCount;
    /**
     * 局内光缆
     */
    private int internalCount;
    /**
     * 光缆总数
     */
    private int totalCount;

    public int getStairCount() {
        return stairCount;
    }

    public void setStairCount(int stairCount) {
        this.stairCount = stairCount;
    }

    public int getSecondaryCount() {
        return secondaryCount;
    }

    public void setSecondaryCount(int secondaryCount) {
        this.secondaryCount = secondaryCount;
    }

    public int getTrunkCount() {
        return trunkCount;
    }

    public void setTrunkCount(int trunkCount) {
        this.trunkCount = trunkCount;
    }

    public int getRelayCount() {
        return relayCount;
    }

    public void setRelayCount(int relayCount) {
        this.relayCount = relayCount;
    }

    public int getTerminalCount() {
        return terminalCount;
    }

    public void setTerminalCount(int terminalCount) {
        this.terminalCount = terminalCount;
    }

    public int getCoreCount() {
        return coreCount;
    }

    public void setCoreCount(int coreCount) {
        this.coreCount = coreCount;
    }

    public int getConvergeCount() {
        return convergeCount;
    }

    public void setConvergeCount(int convergeCount) {
        this.convergeCount = convergeCount;
    }

    public int getTandemCount() {
        return tandemCount;
    }

    public void setTandemCount(int tandemCount) {
        this.tandemCount = tandemCount;
    }

    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }

    public int getInternalCount() {
        return internalCount;
    }

    public void setInternalCount(int internalCount) {
        this.internalCount = internalCount;
    }

    public int getTotalCount() {
        return trunkCount + terminalCount + stairCount + secondaryCount + relayCount + coreCount
                + convergeCount + tandemCount + contactCount + internalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
