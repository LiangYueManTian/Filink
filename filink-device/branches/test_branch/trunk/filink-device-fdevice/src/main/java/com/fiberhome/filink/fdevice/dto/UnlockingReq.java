package com.fiberhome.filink.fdevice.dto;

import lombok.Data;

/**
 * 开锁次数请求参数
 * @Author: zhaoliang
 * @Date: 2019/6/14 9:53
 * @Description: com.fiberhome.filink.fdevice.dto
 * @version: 1.0
 */
@Data
public class UnlockingReq {

    /**
     * 设施ID
     */
    private String deviceId;

    /**
     * 起始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}
