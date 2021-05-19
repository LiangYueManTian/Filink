package com.fiberhome.filink.deviceapi.bean;

import com.fiberhome.filink.filinklockapi.bean.Lock;
import lombok.Data;

import java.util.List;

/**
 * @author zepenggao@wistronits.com
 * create on  2019/1/9
 */
@Data
public class DeviceInfoDto {
    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 设施类型Code
     */
    private String deviceType;

    /**
     * 设施类型名称
     */
    private String deviceTypeName;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 设施状态
     */
    private String deviceStatus;

    /**
     * 设施状态名称
     */
    private String deviceStatusName;

    /**
     * 设施编号
     */
    private String deviceCode;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 部署状态码
     */
    private String deployStatus;

    /**
     * 部署状态名称
     */
    private String deployStatusName;

    /**
     * 省
     */
    private String provinceName;

    /**
     * 市
     */
    private String cityName;

    /**
     * 区
     */
    private String districtName;

    /**
     * gps定位
     */
    private String positionGps;

    /**
     * 基础定位
     */
    private String positionBase;

    /**
     * 关联区域id
     */
    private AreaInfo areaInfo;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 关联特性字段表id
     */
    private String specificFieldId;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private long cTime;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private long uTime;

    /**
     * 是否删除
     */
    private String isDeleted;

    /**
     * 序列号
     */
    private String serialNum;

    /**
     * 是否关注
     */
    private String isCollecting;

    /**
     * 门锁集合
     */
    private List<Lock> lockList;

}
