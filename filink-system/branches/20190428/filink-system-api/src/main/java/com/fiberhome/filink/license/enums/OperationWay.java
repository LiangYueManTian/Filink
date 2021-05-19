package com.fiberhome.filink.license.enums;

/**
 * @Author: zl
 * @Date: 2019/4/2 16:09
 * @Description: com.fiberhome.filink.license.enums
 * @version: 1.0
 */
public enum OperationWay {
    /**
     * 用户数
     */
    ADD("add"),

    /**
     * 在线用户数
     */
    DELETE("delete");

    private String value;

    OperationWay(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
