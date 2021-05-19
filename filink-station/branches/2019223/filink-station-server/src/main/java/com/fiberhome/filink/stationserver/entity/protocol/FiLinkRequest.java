package com.fiberhome.filink.stationserver.entity.protocol;

import java.io.Serializable;
import java.util.Map;

/**
 * 请求实体类
 * @author CongcaiYu
 */
public class FiLinkRequest implements Serializable {
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
     * data体
     */
    private Map<Integer, String> dataBody;

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

    public Map<Integer, String> getDataBody() {
        return dataBody;
    }

    public void setDataBody(Map<Integer, String> dataBody) {
        this.dataBody = dataBody;
    }
}
