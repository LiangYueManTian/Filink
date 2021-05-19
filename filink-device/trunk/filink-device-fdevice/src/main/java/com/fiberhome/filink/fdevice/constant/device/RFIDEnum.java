package com.fiberhome.filink.fdevice.constant.device;

import com.fiberhome.filink.server_common.utils.I18nUtils;

/**
 * rfid 中英文国际化
 *
 * @author liyj
 * @date 2019/9/12
 */
public enum RFIDEnum {

    /**
     * 熔纤
     */
    RFID_CORE_KEY("RFID_CORE_KEY", DeviceI18n.RFID_CORE_KEY),
    /**
     * 成端
     */
    RFID_PORT_CABLE_KEY("RFID_PORT_CABLE_KEY", DeviceI18n.RFID_PORT_CABLE_KEY),

    /**
     * 跳接
     */
    RFID_JUMP_KEY("RFID_JUMP_KEY", DeviceI18n.RFID_JUMP_KEY),

    /**
     * 光缆段
     */
    RFID_OPTIC_KEY("RFID_OPTIC_KEY", DeviceI18n.RFID_OPTIC_KEY);

    private String key;
    private String resourceName;

    RFIDEnum(String key, String resourceName) {
        this.key = key;
        this.resourceName = resourceName;
    }

    /**
     * 获取中文名称
     *
     * @param key key
     * @return 中文名称
     */
    public static String getNameByLanguage(String key) {
        for (RFIDEnum rfidEnum : RFIDEnum.values()) {
            if (key.equals(rfidEnum.getKey())) {
                return I18nUtils.getSystemString(rfidEnum.getResourceName());
            }
        }
        return null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
}