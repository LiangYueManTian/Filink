package com.fiberhome.filink.rfid.req.template;

/**
 * 端口信息
 *
 * @author chaofanrong
 * @date 2019/7/2
 */
public class PortInfoReqDto {

    /**
     * 本端端口所属设施id
     */
    private String deviceId;

    /**
     * 本端端口所属箱架AB面
     */
    private Integer boxSide;

    /**
     * 本端端口所属框号
     */
    private String frameNo;

    /**
     * 本端端口所属盘AB面
     */
    private Integer discSide;

    /**
     * 本端端口所属盘号
     */
    private String discNo;

    /**
     * 本端端口号
     */
    private String portNo;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getBoxSide() {
        return boxSide;
    }

    public void setBoxSide(Integer boxSide) {
        this.boxSide = boxSide;
    }

    public String getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(String frameNo) {
        this.frameNo = frameNo;
    }

    public Integer getDiscSide() {
        return discSide;
    }

    public void setDiscSide(Integer discSide) {
        this.discSide = discSide;
    }

    public String getDiscNo() {
        return discNo;
    }

    public void setDiscNo(String discNo) {
        this.discNo = discNo;
    }

    public String getPortNo() {
        return portNo;
    }

    public void setPortNo(String portNo) {
        this.portNo = portNo;
    }
}
