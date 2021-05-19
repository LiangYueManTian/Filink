package com.fiberhome.filink.license.enums;

/**
 * @Author: zl
 * @Date: 2019/4/2 17:17
 * @Description: com.fiberhome.filink.license.enums
 * @version: 1.0
 */
public enum OperationTarget {
    /**
     * 用户数
     */
    USER("user"),

    /**
     * 在线用户数
     */
    ONLINE("online"),

    /**
     * 设施数
     */
    DEVICE("device");

    private String value;

    OperationTarget(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
