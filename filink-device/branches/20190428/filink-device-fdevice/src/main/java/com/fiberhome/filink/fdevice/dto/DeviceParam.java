package com.fiberhome.filink.fdevice.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: zl
 * @Date: 2019/4/20 20:04
 * @Description: com.fiberhome.filink.fdevice.dto
 * @version: 1.0
 */
@Data
public class DeviceParam {
    /**
     * 区域ID集合
     */
    private List<String> areaIds;

    /**
     * 设施类型集合
     */
    private List<String> deviceTypes;
}
