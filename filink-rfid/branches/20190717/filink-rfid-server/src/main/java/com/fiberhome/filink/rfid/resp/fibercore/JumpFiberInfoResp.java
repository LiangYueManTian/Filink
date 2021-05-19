package com.fiberhome.filink.rfid.resp.fibercore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.rfid.bean.fibercore.JumpFiberInfo;
import com.fiberhome.filink.rfid.constant.CommonConstant;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.fibercore.JumpCoreConstant;
import com.fiberhome.filink.rfid.req.fibercore.QueryJumpFiberInfoReq;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 跳接信息返回类
 * </p>
 *
 * @author chaofanrong
 * @since 2019-06-12
 */
public class JumpFiberInfoResp extends JumpFiberInfo {

    /**
    * 设施名字
    */
    private String deviceName;

    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 设施类型名字
     */
    private String deviceTypeName;

    /**
     * 对端设施名字
     */
    private String oppositeDeviceName;

    /**
     * 对端设施类型
     */
    private String oppositeDeviceType;

    /**
     * 对端设施类型名字
     */
    private String oppositeDeviceTypeName;

    /**
     * 对端端口状态
     */
    private String oppositePortStatus;

    /**
     * 对端端口标签类型
     */
    private String oppositePortMarkType;

    /**
     * 对端端口标签状态
     */
    private String oppositePortRfidStatus;

    /**
     * 对端端口智能标签id
     */
    private String oppositePortRfidCode;

    /**
     * 对端端口标签备注
     */
    private String oppositePortRemark;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getOppositeDeviceName() {
        return oppositeDeviceName;
    }

    public void setOppositeDeviceName(String oppositeDeviceName) {
        this.oppositeDeviceName = oppositeDeviceName;
    }

    public String getOppositeDeviceType() {
        return oppositeDeviceType;
    }

    public void setOppositeDeviceType(String oppositeDeviceType) {
        this.oppositeDeviceType = oppositeDeviceType;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public String getOppositeDeviceTypeName() {
        return oppositeDeviceTypeName;
    }

    public void setOppositeDeviceTypeName(String oppositeDeviceTypeName) {
        this.oppositeDeviceTypeName = oppositeDeviceTypeName;
    }

    public String getOppositePortStatus() {
        return oppositePortStatus;
    }

    public void setOppositePortStatus(String oppositePortStatus) {
        this.oppositePortStatus = oppositePortStatus;
    }

    public String getOppositePortMarkType() {
        return oppositePortMarkType;
    }

    public void setOppositePortMarkType(String oppositePortMarkType) {
        this.oppositePortMarkType = oppositePortMarkType;
    }

    public String getOppositePortRfidStatus() {
        return oppositePortRfidStatus;
    }

    public void setOppositePortRfidStatus(String oppositePortRfidStatus) {
        this.oppositePortRfidStatus = oppositePortRfidStatus;
    }

    public String getOppositePortRemark() {
        return oppositePortRemark;
    }

    public void setOppositePortRemark(String oppositePortRemark) {
        this.oppositePortRemark = oppositePortRemark;
    }

    public String getOppositePortRfidCode() {
        return oppositePortRfidCode;
    }

    public void setOppositePortRfidCode(String oppositePortRfidCode) {
        this.oppositePortRfidCode = oppositePortRfidCode;
    }

    /**
     * 设置设施类型名称（用于导出）
     *
     * @return String 设施类型名字
     */
    @JsonIgnore
    public String getTranslationOppositeDeviceType(){
        String exportLocales = ExportApiUtils.getExportLocales();
        if (CommonConstant.DEVICE_TYPE_001.equals(this.getOppositeDeviceType())){
            //光交箱
            return I18nUtils.getString(exportLocales, RfIdI18nConstant.DEVICE_TYPE_001);
        } else if (CommonConstant.DEVICE_TYPE_030.equals(this.getOppositeDeviceType())){
            //人井
            return I18nUtils.getString(exportLocales, RfIdI18nConstant.DEVICE_TYPE_060);
        } else if (CommonConstant.DEVICE_TYPE_060.equals(this.getOppositeDeviceType())){
            //配线架
            return I18nUtils.getString(exportLocales, RfIdI18nConstant.DEVICE_TYPE_060);
        } else if (CommonConstant.DEVICE_TYPE_090.equals(this.getOppositeDeviceType())){
            //接头盒
            return I18nUtils.getString(exportLocales, RfIdI18nConstant.DEVICE_TYPE_090);
        } else if (CommonConstant.DEVICE_TYPE_150.equals(this.getOppositeDeviceType())){
            //分纤箱
            return I18nUtils.getString(exportLocales, RfIdI18nConstant.DEVICE_TYPE_150);
        } else {
            return this.getOppositeDeviceType();
        }
    }

    /**
     * 设置对端端口状态（用于导出）
     *
     * @return String 端口状态
     */
    @JsonIgnore
    public String getTranslationOppositePortStatus() {
        String exportLocales = ExportApiUtils.getExportLocales();
        if (CommonConstant.PORT_STATUS_PRE_OCCUPY.equals(this.getOppositePortStatus())) {
            //预占用
            return I18nUtils.getString(exportLocales, RfIdI18nConstant.PORT_STATUS_PRE_OCCUPY);
        } else if (CommonConstant.PORT_STATUS_OCCUPY.equals(this.getOppositePortStatus())) {
            //占用
            return I18nUtils.getString(exportLocales, RfIdI18nConstant.PORT_STATUS_OCCUPY);
        } else if (CommonConstant.PORT_STATUS_FREE.equals(this.getOppositePortStatus())) {
            //空闲
            return I18nUtils.getString(exportLocales, RfIdI18nConstant.PORT_STATUS_FREE);
        } else if (CommonConstant.PORT_STATUS_EXCEPTION.equals(this.getOppositePortStatus())) {
            //异常
            return I18nUtils.getString(exportLocales, RfIdI18nConstant.PORT_STATUS_EXCEPTION);
        } else if (CommonConstant.PORT_STATUS_VIRTUAL_OCCUPY.equals(this.getOppositePortStatus())) {
            //虚占
            return I18nUtils.getString(exportLocales, RfIdI18nConstant.PORT_STATUS_VIRTUAL_OCCUPY);
        } else {
            return this.getOppositePortStatus();
        }
    }

    /**
     * 设置对端端口适配器类型（用于导出）
     *
     * @return String 适配器类型
     */
    @JsonIgnore
    public String getTranslationOppositeAdapterType() {
        if (CommonConstant.ADAPTER_TYPE_FC.equals(this.getAdapterType())) {
            return JumpCoreConstant.ADAPTER_TYPE_FC;
        } else if (CommonConstant.ADAPTER_TYPE_SC.equals(this.getAdapterType())) {
            return JumpCoreConstant.ADAPTER_TYPE_SC;
        } else {
            return this.getAdapterType().toString();
        }
    }

    /**
     * 设置对端端口标签类型（用于导出）
     *
     * @return String 标签类型
     */
    @JsonIgnore
    public String getTranslationOppositePortRfidType() {
        String exportLocales = ExportApiUtils.getExportLocales();
        if (CommonConstant.RFID_TYPE_QR_CODE.equals(this.getOppositePortMarkType())) {
            return I18nUtils.getString(exportLocales, RfIdI18nConstant.RFID_TYPE_QR_CODE);
        } else if (CommonConstant.RFID_TYPE_RFID.equals(this.getOppositePortMarkType())) {
            return I18nUtils.getString(exportLocales, RfIdI18nConstant.RFID_TYPE_RFID);
        } else {
            return this.getOppositePortMarkType();
        }
    }

    /**
     * 设置对端端口标签状态（用于导出）
     *
     * @return String 标签状态
     */
    @JsonIgnore
    public String getTranslationOppositePortRfidStatus() {
        String exportLocales = ExportApiUtils.getExportLocales();
        if (CommonConstant.RFID_STATUS_NORMAL.equals(this.getOppositePortRfidStatus())) {
            return I18nUtils.getString(exportLocales, RfIdI18nConstant.RFID_STATUS_NORMAL);
        } else if (CommonConstant.RFID_STATUS_ABNORMAL.equals(this.getOppositePortRfidStatus())) {
            return I18nUtils.getString(exportLocales, RfIdI18nConstant.RFID_STATUS_ABNORMAL);
        } else {
            return this.getOppositePortRfidStatus();
        }
    }

    /*-------------------------------------设置设施类型公共方法start-------------------------------------*/
    /**
     * 设置设施类型名称
     *
     * @param deviceType 设施类型
     *
     * @return String 设施类型名字
     */
    public static String getDeviceTypeName(String deviceType){
        //设施类型名字
        if (CommonConstant.DEVICE_TYPE_001.equals(deviceType)){
            //光交箱
            return I18nUtils.getSystemString(RfIdI18nConstant.DEVICE_TYPE_001);
        } else if (CommonConstant.DEVICE_TYPE_030.equals(deviceType)){
            //人井
            return I18nUtils.getSystemString(RfIdI18nConstant.DEVICE_TYPE_030);
        } else if (CommonConstant.DEVICE_TYPE_060.equals(deviceType)){
            //配线架
            return I18nUtils.getSystemString(RfIdI18nConstant.DEVICE_TYPE_060);
        } else if (CommonConstant.DEVICE_TYPE_090.equals(deviceType)){
            //接头盒
            return I18nUtils.getSystemString(RfIdI18nConstant.DEVICE_TYPE_090);
        } else if (CommonConstant.DEVICE_TYPE_150.equals(deviceType)){
            //分纤箱
            return I18nUtils.getSystemString(RfIdI18nConstant.DEVICE_TYPE_150);
        } else {
            return deviceType;
        }
    }
    /*-------------------------------------设置设施类型公共方法end-------------------------------------*/

    /*-------------------------------------组装本端及对端start-------------------------------------*/
    /**
     * 组装本端及对端
     *
     * @param jumpFiberInfoRespList 跳接信息列表
     *
     * @return jumpFiberInfoRespList 跳接信息列表
     */
    public static List<JumpFiberInfoResp> assemblyJumpFiberInfoThisAndOpposite(List<JumpFiberInfoResp> jumpFiberInfoRespList, QueryJumpFiberInfoReq queryJumpFiberInfoReq) {
        //组装本端及对端信息
        List<JumpFiberInfoResp> jumpFiberInfoRespListTemp = new ArrayList<>();
        for (JumpFiberInfoResp jumpFiberInfoResp : jumpFiberInfoRespList){
            JumpFiberInfoResp jumpFiberInfoRespTemp = new JumpFiberInfoResp();
            if (!(queryJumpFiberInfoReq.getDeviceId() + "-"
                    + queryJumpFiberInfoReq.getBoxSide() + "-"
                    + queryJumpFiberInfoReq.getFrameNo() + "-"
                    + queryJumpFiberInfoReq.getDiscSide() + "-"
                    + queryJumpFiberInfoReq.getDiscNo() +"-"
                    + queryJumpFiberInfoReq.getPortNo()).equals(
                    (jumpFiberInfoResp.getDeviceId() + "-"
                            + jumpFiberInfoResp.getBoxSide() + "-"
                            + jumpFiberInfoResp.getFrameNo() + "-"
                            + jumpFiberInfoResp.getDiscSide() + "-"
                            + jumpFiberInfoResp.getDiscNo() +"-"
                            + jumpFiberInfoResp.getPortNo())
            )
            ){
                //本端和对端互换
                BeanUtils.copyProperties(jumpFiberInfoResp,jumpFiberInfoRespTemp);
                jumpFiberInfoRespTemp.setRfidCode(jumpFiberInfoResp.getOppositeRfidCode());
                jumpFiberInfoRespTemp.setDeviceId(jumpFiberInfoResp.getOppositeDeviceId());
                jumpFiberInfoRespTemp.setDeviceType(jumpFiberInfoResp.getOppositeDeviceType());
                jumpFiberInfoRespTemp.setBoxSide(jumpFiberInfoResp.getOppositeBoxSide());
                jumpFiberInfoRespTemp.setFrameNo(jumpFiberInfoResp.getOppositeFrameNo());
                jumpFiberInfoRespTemp.setDiscSide(jumpFiberInfoResp.getOppositeDiscSide());
                jumpFiberInfoRespTemp.setDiscNo(jumpFiberInfoResp.getOppositeDiscNo());
                jumpFiberInfoRespTemp.setPortNo(jumpFiberInfoResp.getOppositePortNo());
                jumpFiberInfoRespTemp.setRemark(jumpFiberInfoResp.getOppositeRemark());

                jumpFiberInfoRespTemp.setOppositeRfidCode(jumpFiberInfoResp.getRfidCode());
                jumpFiberInfoRespTemp.setOppositeDeviceId(jumpFiberInfoResp.getDeviceId());
                jumpFiberInfoRespTemp.setOppositeDeviceType(jumpFiberInfoResp.getDeviceType());
                jumpFiberInfoRespTemp.setOppositeBoxSide(jumpFiberInfoResp.getBoxSide());
                jumpFiberInfoRespTemp.setOppositeFrameNo(jumpFiberInfoResp.getFrameNo());
                jumpFiberInfoRespTemp.setOppositeDiscSide(jumpFiberInfoResp.getDiscSide());
                jumpFiberInfoRespTemp.setOppositeDiscNo(jumpFiberInfoResp.getDiscNo());
                jumpFiberInfoRespTemp.setOppositePortNo(jumpFiberInfoResp.getPortNo());
                jumpFiberInfoRespTemp.setOppositeRemark(jumpFiberInfoResp.getRemark());

                jumpFiberInfoRespListTemp.add(jumpFiberInfoRespTemp);
            } else {
                jumpFiberInfoRespListTemp.add(jumpFiberInfoResp);
            }
        }
        return jumpFiberInfoRespListTemp;
    }
}


