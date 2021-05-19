package com.fiberhome.filink.rfid.bean.facility;

import lombok.Data;

/**
 * Created by Qing on 2019/6/6.
 * 设施信息查询bean
 */
@Data
public class FacilityQueryBean {

    /**
     * 标签ID
     */
    private String rfidLabel;

    /**
     * 所属设施ID
     */
    private String deviceId;

    /**
     * 框号
     */
    private Integer frameNo;

    /**
     * 框所属AB面(true是B面)
     */
    private Integer frameDouble;

    /**
     * 盘号
     */
    private Integer boardNo;

    /**
     * 端口号
     */
    private Integer portNo;

    /**
     * 端口所属AB面(true是B面)
     */
    private Integer portDouble;

    /**
     * 查询类型(箱架/盘/端口)
     */
    private Integer queryType;

    /**
     * 设施类型
     * 光交箱 - 001
     * 配线架 - 060
     * 接头盒- 090
     */
    private String deviceType;
}
