package com.fiberhome.filink.fdevice.bean.device;

import lombok.Data;

/**
 * @author zepenggao@wistronits.com
 * create on  2019/1/8
 */
@Data
public class DeviceQueryBean {

    //
    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 设施编号
     */
    private String deviceCode;

    /**
     * 所属区域
     */
    private String areaId;

    /**
     * 省Id
     */
    private String provinceId;

    /**
     * 市Id
     */
    private String cityId;

    /**
     * 区Id
     */
    private String districtId;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 部署状态
     */
    private String deployStatus;
}
