package com.fiberhome.filink.rfid.bean.facility;

import lombok.Data;

/**
 * Created by Qing on 2019/6/6.
 * 设施实体查询条件
 */
@Data
public class DeviceQueryBean {

    /**
     * 所属设施ID
     */
    private String deviceId;

    /**
     * 框号
     */
    private Integer frameNo;

    /**
     * 框所属A、B 面
     */
    private Integer frameDouble;

    /**
     * 盘号
     */
    private Integer boardNo;
}
