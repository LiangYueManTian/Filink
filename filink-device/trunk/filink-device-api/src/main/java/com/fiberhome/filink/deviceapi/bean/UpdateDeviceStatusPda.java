package com.fiberhome.filink.deviceapi.bean;

import lombok.Data;

import java.util.List;

/**
 * @Author: zl
 * @Date: 2019/4/20 14:14
 * @Description: com.fiberhome.filink.deviceapi.bean
 * @version: 1.0
 */
@Data
public class UpdateDeviceStatusPda {
    /**
     * 设施ID集合
     */
    private List<String> deviceIdList;

    /**
     * 部署状态
     */
    private String deployStatus;
}