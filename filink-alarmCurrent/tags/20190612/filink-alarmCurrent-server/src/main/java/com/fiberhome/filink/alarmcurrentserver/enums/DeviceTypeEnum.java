package com.fiberhome.filink.alarmcurrentserver.enums;


import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.server_common.utils.I18nUtils;

/**
 * 设施类型泛型，（以后要考虑动态扩容的问题）
 *
 * @author taowei@wistronits.com
 * @Date 2019/1/7
 */
public enum DeviceTypeEnum {

    /**
     * 光交箱
     */
    Optical_Box(I18nUtils.getString(AppConstant.OPTICAL_BOX), "001"),

    /**
     * 人井
     */
    Well(I18nUtils.getString(AppConstant.WELL), "030"),

    /**
     * 配线架
     */
    Distribution_Frame(I18nUtils.getString(AppConstant.DISTRIBUTION_FRAME), "060"),

    /**
     * 接头盒
     */
    Junction_Box(I18nUtils.getString(AppConstant.JUNCTION_BOX), "090"),

    /**
     * 分纤箱
     */
    Splitting_Box(I18nUtils.getString(AppConstant.SPLITTING_BOX), "150");

    private String desc;
    private String code;

    DeviceTypeEnum(String desc, String code) {
        this.desc = desc;
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 根据 code 获取 对应的名称
     *
     * @param value code
     * @return 名称
     */
    public static String getDesc(String value) {
        for (DeviceTypeEnum dt : DeviceTypeEnum.values()) {
            if (value.equals(dt.code)) {
                return dt.desc;
            }
        }
        return null;
    }
}
