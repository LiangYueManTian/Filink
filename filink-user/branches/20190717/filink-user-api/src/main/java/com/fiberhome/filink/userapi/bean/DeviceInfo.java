package com.fiberhome.filink.userapi.bean;

import lombok.Data;

import java.util.List;

/**
 * 设施的信息
 * @author xuangong
 */
@Data
public class DeviceInfo {

    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 设施对应的门id
     */
    private List<String> doorId;
}
