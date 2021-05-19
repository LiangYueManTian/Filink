package com.fiberhome.filink.fdevice.constant.device;

/**
 * 设施权限和设施的对应关系
 *
 * @author liyj
 * @date 2019/6/28
 */

public enum DeviceAuthEnum {
    /**
     * 智能标签 数据权限码  001-2
     */
    DEVICE_LABEL_001("001", "001-2"),
    /**
     * 智能标签 数据权限码  060-2
     */
    DEVICE_LABEL_060("060", "060-2"),
    /**
     * 智能标签 数据权限码  090-2
     */
    DEVICE_LABEL_090("090", "090-2");


    private String code;
    private String parentCode;

    DeviceAuthEnum(String parentCode, String code) {
        this.code = code;
        this.parentCode = parentCode;
    }

    public static String getCodeByParent(String code) {
        for (DeviceAuthEnum item : DeviceAuthEnum.values()) {
            if (item.getParentCode().equals(code)) {
                return item.getCode();
            }
        }

        return null;
    }


    public String getParentCode() {
        return parentCode;
    }

    public String getCode() {

        return code;
    }
}
