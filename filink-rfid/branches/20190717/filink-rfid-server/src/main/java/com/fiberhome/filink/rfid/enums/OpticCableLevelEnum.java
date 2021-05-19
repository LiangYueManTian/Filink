package com.fiberhome.filink.rfid.enums;

import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.opticcable.OpticCableConstant;
import com.fiberhome.filink.server_common.utils.I18nUtils;

/**
 * 光缆级别
 *
 * @author rongchaofan
 * @date 2019/6/5
 */
public enum OpticCableLevelEnum {
    /**
     * 本地接入-主干光缆
     */
    OPTIC_CABLE_LEVEL_TRUNK(OpticCableConstant.OPTIC_CABLE_LEVEL_TRUNK, RfIdI18nConstant.OPTIC_CABLE_LEVEL_TRUNK),
    /**
     * 本地接入-末端光缆
     */
    OPTIC_CABLE_LEVEL_END(OpticCableConstant.OPTIC_CABLE_LEVEL_END,RfIdI18nConstant.OPTIC_CABLE_LEVEL_END),
    /**
     * 一级干线
     */
    OPTIC_CABLE_LEVEL_LEVEL_ONE(OpticCableConstant.OPTIC_CABLE_LEVEL_LEVEL_ONE,RfIdI18nConstant.OPTIC_CABLE_LEVEL_LEVEL_ONE),
    /**
     * 二级干线
     */
    OPTIC_CABLE_LEVEL_LEVEL_LEVEL_TWO(OpticCableConstant.OPTIC_CABLE_LEVEL_LEVEL_LEVEL_TWO,RfIdI18nConstant.OPTIC_CABLE_LEVEL_LEVEL_LEVEL_TWO),
    /**
     * 本地中继
     */
    OPTIC_CABLE_LEVEL_LEVEL_REPEATER(OpticCableConstant.OPTIC_CABLE_LEVEL_LEVEL_REPEATER,RfIdI18nConstant.OPTIC_CABLE_LEVEL_LEVEL_REPEATER),
    /**
     * 本地核心
     */
    OPTIC_CABLE_LEVEL_LEVEL_KERNEL(OpticCableConstant.OPTIC_CABLE_LEVEL_LEVEL_KERNEL,RfIdI18nConstant.OPTIC_CABLE_LEVEL_LEVEL_KERNEL),
    /**
     * 本地汇聚
     */
    OPTIC_CABLE_LEVEL_LEVEL_CONVERGE(OpticCableConstant.OPTIC_CABLE_LEVEL_LEVEL_CONVERGE,RfIdI18nConstant.OPTIC_CABLE_LEVEL_LEVEL_CONVERGE),
    /**
     * 汇接层光缆
     */
    OPTIC_CABLE_LEVEL_LEVEL_JUNCTION(OpticCableConstant.OPTIC_CABLE_LEVEL_LEVEL_JUNCTION,RfIdI18nConstant.OPTIC_CABLE_LEVEL_LEVEL_JUNCTION),
    /**
     * 联络光缆
     */
    OPTIC_CABLE_LEVEL_LEVEL_CONTACT(OpticCableConstant.OPTIC_CABLE_LEVEL_LEVEL_CONTACT,RfIdI18nConstant.OPTIC_CABLE_LEVEL_LEVEL_CONTACT),
    /**
     * 局内光缆
     */
    OPTIC_CABLE_LEVEL_LEVEL_INSIDE(OpticCableConstant.OPTIC_CABLE_LEVEL_LEVEL_INSIDE,RfIdI18nConstant.OPTIC_CABLE_LEVEL_LEVEL_INSIDE);

    private String index;
    private String value;

    OpticCableLevelEnum(String index,String value) {
        this.index = index;
        this.value = value;
    }

    public String getIndex() {
        return index;
    }

    public static String getValue(String index) {
        String value = "";
        for (OpticCableLevelEnum item : OpticCableLevelEnum.values()) {
            if (item.getIndex().equals(index)) {
                String exportLocales = ExportApiUtils.getExportLocales();
                value = I18nUtils.getString(exportLocales, item.value);
            }
        }
        return value;
    }




}
