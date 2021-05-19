package com.fiberhome.filink.fdevice.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: zl
 * @Date: 2019/4/19 14:54
 * @Description: com.fiberhome.filink.fdevice.dto
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
