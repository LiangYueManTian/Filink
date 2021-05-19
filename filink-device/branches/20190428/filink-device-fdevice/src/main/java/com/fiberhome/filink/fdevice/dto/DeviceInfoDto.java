package com.fiberhome.filink.fdevice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.constant.device.DeployStatus;
import com.fiberhome.filink.fdevice.constant.device.DeviceStatus;
import com.fiberhome.filink.fdevice.constant.device.DeviceType;
import com.fiberhome.filink.filinklockapi.bean.Lock;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 设施数据传输类
 *
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
     * 设施名称
     */
    private String deviceName;

    /**
     * 设施状态
     */
    private String deviceStatus;

    /**
     * 设施编号
     */
    private String deviceCode;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 部署状态
     */
    private String deployStatus;

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
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private long uTime;

    /**
     * 更新时间
     */
    private Timestamp updateTime;

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

    /**
     * 区域名称
     */
    private String areaName;

    @JsonIgnore
    public String getTranslationCTime() {
        return ExportApiUtils.getZoneTime(cTime);
    }

    @JsonIgnore
    public String getTranslationUTime() {
        return ExportApiUtils.getZoneTime(uTime);
    }

    @JsonIgnore
    public String getTranslationDeviceType() {
        return I18nUtils.getString(DeviceType.getDesc(deviceType));
    }

    @JsonIgnore
    public String getTranslationDeviceStatus() {
        return I18nUtils.getString(DeviceStatus.getDesc(deviceStatus));
    }

    @JsonIgnore
    public String getTranslationDeployStatus() {
        return I18nUtils.getString(DeployStatus.getMsg(deployStatus));
    }
}
