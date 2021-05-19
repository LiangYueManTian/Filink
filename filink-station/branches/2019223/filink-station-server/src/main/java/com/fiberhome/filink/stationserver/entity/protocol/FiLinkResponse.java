package com.fiberhome.filink.stationserver.entity.protocol;

import java.io.Serializable;
import java.util.Map;

/**
 * 响应实体类
 * @author CongcaiYu
 */
public class FiLinkResponse implements Serializable {
    /**
     * 帧头
     */
    private Integer frameHead;
    /**
     * 指令长度
     */
    private Integer cmdLen;
    /**
     * bgmp
     */
    private Bgmp bgmp;
    /**
     * 响应码
     */
    private Integer cmdOk;

    private Map<String, Object> dataBody;

    public Map<String, Object> getDataBody() {
        return dataBody;
    }

    public void setDataBody(Map<String, Object> dataBody) {
        this.dataBody = dataBody;
    }

    public Integer getFrameHead() {
        return frameHead;
    }

    public void setFrameHead(Integer frameHead) {
        this.frameHead = frameHead;
    }

    public Integer getCmdLen() {
        return cmdLen;
    }

    public void setCmdLen(Integer cmdLen) {
        this.cmdLen = cmdLen;
    }

    public Bgmp getBgmp() {
        return bgmp;
    }

    public void setBgmp(Bgmp bgmp) {
        this.bgmp = bgmp;
    }

    public Integer getCmdOk() {
        return cmdOk;
    }

    public void setCmdOk(Integer cmdOk) {
        this.cmdOk = cmdOk;
    }

}
