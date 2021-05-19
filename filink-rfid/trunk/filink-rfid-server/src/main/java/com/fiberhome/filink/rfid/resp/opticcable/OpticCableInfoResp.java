package com.fiberhome.filink.rfid.resp.opticcable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableInfo;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.opticcable.OpticCableConstant;
import com.fiberhome.filink.rfid.enums.OpticCableLevelEnum;
import com.fiberhome.filink.server_common.utils.I18nUtils;

/**
 * <p>
 * 光缆信息表返回类
 * </p>
 *
 * @author chaofanrong
 * @since 2019-05-20
 */
public class OpticCableInfoResp extends OpticCableInfo {

    /**
     * 自定义光缆级别(用于列表导出)
     *
     * @return String 光缆级别
     */
    @JsonIgnore
    public String getTranslationOpticCableLevel(){
        return OpticCableLevelEnum.getValue(this.getOpticCableLevel());
    }

    /**
     * 自定义布线类型(用于列表导出)
     *
     * @return String 布线类型
     */
    @JsonIgnore
    public String getTranslationWiringType(){
        String exportLocales = ExportApiUtils.getExportLocales();
        if (OpticCableConstant.WIRING_TYPE_DIMINISHING.equals(this.getWiringType())){
            return I18nUtils.getString(exportLocales, RfIdI18nConstant.WIRING_TYPE_DIMINISHING);
        } else if (OpticCableConstant.WIRING_TYPE_NOT_DIMINISH.equals(this.getWiringType())){
            return I18nUtils.getString(exportLocales,RfIdI18nConstant.WIRING_TYPE_NOT_DIMINISH);
        } else {
            return this.getWiringType();
        }
    }

    /**
     * 自定义拓扑结构(用于列表导出)
     *
     * @return String 拓扑结构
     */
    @JsonIgnore
    public String getTranslationTopology(){
        String exportLocales = ExportApiUtils.getExportLocales();
        if (OpticCableConstant.TOPOLOGY_CIRCULARITY.equals(this.getTopology())){
            return I18nUtils.getString(exportLocales,RfIdI18nConstant.TOPOLOGY_CIRCULARITY);
        } else if (OpticCableConstant.TOPOLOGY_NON_CIRCULARITY.equals(this.getTopology())){
            return I18nUtils.getString(exportLocales,RfIdI18nConstant.TOPOLOGY_NON_CIRCULARITY);
        } else {
            return this.getTopology();
        }
    }

}
