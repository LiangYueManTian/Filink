package com.fiberhome.filink.fdevice.constant.device;

/**
 * 设施权限和设施的对应关系
 *
 * @author liyj
 * @date 2019/6/28
 */

public enum DeviceLockAuthEnum {
    /**
     * 电子锁 数据权限码 001-1
     */
    DEVICE_LOCK_001("001", "001-1"),
    /**
     * 电子锁 数据权限码 030-1
     */
    DEVICE_LOCK_030("030", "030-1"),
    /**
     * 电子锁 数据权限码 210-1
     */
    DEVICE_LOCK_210("210", "210-1");

    private String code;
    private String parentCode;

    DeviceLockAuthEnum(String parentCode, String code) {
        this.code = code;
        this.parentCode = parentCode;
    }

    public static String getCodeByParent(String code) {
        for (DeviceLockAuthEnum item : DeviceLockAuthEnum.values()) {
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
