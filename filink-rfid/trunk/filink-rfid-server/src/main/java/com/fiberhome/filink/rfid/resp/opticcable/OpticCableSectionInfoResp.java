package com.fiberhome.filink.rfid.resp.opticcable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableSectionInfo;
import com.fiberhome.filink.rfid.constant.CommonConstant;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.opticcable.OpticCableSectionConstant;
import com.fiberhome.filink.server_common.utils.I18nUtils;

/**
 * <p>
 * 光缆段信息表返回类
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
public class OpticCableSectionInfoResp extends OpticCableSectionInfo {
    /**
     * 光缆名称
     */
    private String opticCableName;

    /**
     * 所属区域名称
     */
    private String areaName;
    /**
     * 起始节点设备
     */
    private String startNodeName;
    /**
     * 终止节点设备
     */
    private String terminationNodeName;

    public String getOpticCableName() {
        return opticCableName;
    }
    public void setOpticCableName(String opticCableName) {
        this.opticCableName = opticCableName;
    }

    public String getAreaName() {
        return areaName;
    }
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getStartNodeName() {
        return startNodeName;
    }
    public void setStartNodeName(String startNodeName) {
        this.startNodeName = startNodeName;
    }

    public String getTerminationNodeName() {
        return terminationNodeName;
    }
    public void setTerminationNodeName(String terminationNodeName) {
        this.terminationNodeName = terminationNodeName;
    }


    /**
     * 自定义状态(用于列表导出)
     *
     * @return String 状态
     */
    @JsonIgnore
    public String getTranslationStatus(){
        String exportLocales = ExportApiUtils.getExportLocales();
        if (OpticCableSectionConstant.STATUS_USE.equals(this.getStatus())){
            return I18nUtils.getString(exportLocales, RfIdI18nConstant.STATUS_USE);
        } else if (OpticCableSectionConstant.STATUS_UNUSED.equals(this.getStatus())){
            return I18nUtils.getString(exportLocales,RfIdI18nConstant.STATUS_UNUSED);
        } else {
            return this.getStatus();
        }
    }

    /**
     * 自定义开始节点设施类型(用于列表导出)
     *
     * @return String 设施类型
     */
    @JsonIgnore
    public String getTranslationStartNodeDeviceType(){
        String exportLocales = ExportApiUtils.getExportLocales();
        if (CommonConstant.DEVICE_TYPE_001.equals(this.getStartNodeDeviceType())){
            //光交箱
            return I18nUtils.getString(exportLocales,RfIdI18nConstant.DEVICE_TYPE_001);
        } else if (CommonConstant.DEVICE_TYPE_030.equals(this.getStartNodeDeviceType())){
            //人井
            return I18nUtils.getString(exportLocales,RfIdI18nConstant.DEVICE_TYPE_030);
        } else if (CommonConstant.DEVICE_TYPE_060.equals(this.getStartNodeDeviceType())){
            //配线架
            return I18nUtils.getString(exportLocales,RfIdI18nConstant.DEVICE_TYPE_060);
        } else if (CommonConstant.DEVICE_TYPE_090.equals(this.getStartNodeDeviceType())){
            //接头盒
            return I18nUtils.getString(exportLocales,RfIdI18nConstant.DEVICE_TYPE_090);
        } else if (CommonConstant.DEVICE_TYPE_150.equals(this.getStartNodeDeviceType())){
            //分纤箱
            return I18nUtils.getString(exportLocales,RfIdI18nConstant.DEVICE_TYPE_150);
        } else {
            return this.getStartNodeDeviceType();
        }
    }

    /**
     * 自定义终止节点设施类型(用于列表导出)
     *
     * @return String 设施类型
     */
    @JsonIgnore
    public String getTranslationTerminationNodeDeviceType(){
        String exportLocales = ExportApiUtils.getExportLocales();
        if (CommonConstant.DEVICE_TYPE_001.equals(this.getTerminationNodeDeviceType())){
            //光交箱
            return I18nUtils.getString(exportLocales,RfIdI18nConstant.DEVICE_TYPE_001);
        } else if (CommonConstant.DEVICE_TYPE_030.equals(this.getTerminationNodeDeviceType())){
            //人井
            return I18nUtils.getString(exportLocales,RfIdI18nConstant.DEVICE_TYPE_030);
        } else if (CommonConstant.DEVICE_TYPE_060.equals(this.getTerminationNodeDeviceType())){
            //配线架
            return I18nUtils.getString(exportLocales,RfIdI18nConstant.DEVICE_TYPE_060);
        } else if (CommonConstant.DEVICE_TYPE_090.equals(this.getTerminationNodeDeviceType())){
            //接头盒
            return I18nUtils.getString(exportLocales,RfIdI18nConstant.DEVICE_TYPE_090);
        } else if (CommonConstant.DEVICE_TYPE_150.equals(this.getTerminationNodeDeviceType())){
            //分纤箱
            return I18nUtils.getString(exportLocales,RfIdI18nConstant.DEVICE_TYPE_150);
        } else {
            return this.getTerminationNodeDeviceType();
        }
    }
}

