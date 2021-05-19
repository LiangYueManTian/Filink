package com.fiberhome.filink.picture.resp;

import com.fiberhome.filink.picture.bean.PicRelationInfo;
import lombok.Data;

/**
 * <p>
 * 设施图片响应
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-03-19
 */
@Data
public class DevicePicResp extends PicRelationInfo {

    /**
     * 设施类型
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
     * 关联区域id
     */
    private String areaId;

    /**
     * 关联区域名字
     */
    private String areaName;

    /**
     * 来源名字
     */
    private String resourceName;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 日期（年月日）
     */
    private String fmtDate;

    /**
     * 创建时间
     */
    private Long cTime;

    /**
     * 修改时间
     */
    private Long uTime;
}
