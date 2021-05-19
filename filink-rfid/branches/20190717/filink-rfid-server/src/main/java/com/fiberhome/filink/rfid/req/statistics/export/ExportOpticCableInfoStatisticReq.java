package com.fiberhome.filink.rfid.req.statistics.export;

/**
 * 光缆统计导出数据格式
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/19
 */
public class ExportOpticCableInfoStatisticReq {
    /**
     * 类别
     */
    private String tableName;
    /**
     * 本地接入-主干光缆
     */
    private String trunkCount;
    /**
     * 本地接入-末端光缆
     */
    private String terminalCount;
    /**
     * 一级干线
     */
    private String stairCount;

    /**
     * 二级干线数量
     */
    private String secondaryCount;

    /**
     * 本地中继数量
     */
    private String relayCount;

    /**
     * 本地核心数量
     */
    private String coreCount;
    /**
     * 本地汇聚数量
     */
    private String convergeCount;
    /**
     * 汇接层光缆
     */
    private String tandemCount;
    /**
     * 联络光缆
     */
    private String contactCount;
    /**
     * 局内光缆
     */
    private String internalCount;
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

    public String getTrunkCount() {
        return trunkCount;
    }

    public void setTrunkCount(String trunkCount) {
        this.trunkCount = trunkCount;
    }

    public String getTerminalCount() {
        return terminalCount;
    }

    public void setTerminalCount(String terminalCount) {
        this.terminalCount = terminalCount;
    }

    public String getStairCount() {
        return stairCount;
    }

    public void setStairCount(String stairCount) {
        this.stairCount = stairCount;
    }

    public String getSecondaryCount() {
        return secondaryCount;
    }

    public void setSecondaryCount(String secondaryCount) {
        this.secondaryCount = secondaryCount;
    }

    public String getRelayCount() {
        return relayCount;
    }

    public void setRelayCount(String relayCount) {
        this.relayCount = relayCount;
    }

    public String getCoreCount() {
        return coreCount;
    }

    public void setCoreCount(String coreCount) {
        this.coreCount = coreCount;
    }

    public String getConvergeCount() {
        return convergeCount;
    }

    public void setConvergeCount(String convergeCount) {
        this.convergeCount = convergeCount;
    }

    public String getTandemCount() {
        return tandemCount;
    }

    public void setTandemCount(String tandemCount) {
        this.tandemCount = tandemCount;
    }

    public String getContactCount() {
        return contactCount;
    }

    public void setContactCount(String contactCount) {
        this.contactCount = contactCount;
    }

    public String getInternalCount() {
        return internalCount;
    }

    public void setInternalCount(String internalCount) {
        this.internalCount = internalCount;
    }
}
