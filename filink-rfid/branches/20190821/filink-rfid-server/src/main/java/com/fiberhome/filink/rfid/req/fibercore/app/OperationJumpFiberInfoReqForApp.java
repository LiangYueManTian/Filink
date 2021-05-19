package com.fiberhome.filink.rfid.req.fibercore.app;

import com.fiberhome.filink.rfid.bean.fibercore.JumpFiberInfo;

/**
 * <p>
 * app处理跳接信息请求
 * </p>
 *
 * @author chaofanrong
 * @since 2019-06-03
 */
public class OperationJumpFiberInfoReqForApp extends JumpFiberInfo {

    /**
     * 智能标签类型
     */
    private String markType;

    /**
     * 智能标签状态（0：正常；1、异常）
     */
    private String rfidStatus;

    /**
     * 智能标签类型
     */
    private String oppositeMarkType;

    /**
     * 对端智能标签状态（0：正常；1、异常）
     */
    private String oppositeRfidStatus;

    public String getMarkType() {
        return markType;
    }

    public void setMarkType(String markType) {
        this.markType = markType;
    }

    public String getRfidStatus() {
        return rfidStatus;
    }

    public void setRfidStatus(String rfidStatus) {
        this.rfidStatus = rfidStatus;
    }

    public String getOppositeMarkType() {
        return oppositeMarkType;
    }

    public void setOppositeMarkType(String oppositeMarkType) {
        this.oppositeMarkType = oppositeMarkType;
    }

    public String getOppositeRfidStatus() {
        return oppositeRfidStatus;
    }

    public void setOppositeRfidStatus(String oppositeRfidStatus) {
        this.oppositeRfidStatus = oppositeRfidStatus;
    }
}
