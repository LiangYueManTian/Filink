package com.fiberhome.filink.picture.req;

import com.fiberhome.filink.picture.bean.PicRelationInfo;
import lombok.Data;

import java.util.Set;

/**
 * <p>
 * 设施图片请求
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-03-19
 */
@Data
public class DevicePicReq extends PicRelationInfo {

    /**
     * 图片张数
     */
    private String picNum;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 图片ids
     */
    private Set<String> devicePicIds;

    /**
     * 图片来源
     */
    private String resource;

    /**
     * 设施ids（用于权限控制）
     */
    private Set<String> deviceIds;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 设施编号
     */
    private String deviceCode;

    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 设施类型
     */
    private Set<String> deviceTypes;

    /**
     * 设施区域ids
     */
    private Set<String> areaIds;

    /**
     * 设施区域id
     */
    private String areaId;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 批量下载任务id
     */
    private String taskId;

    /**
     * 权限过滤设施类型
     */
    private Set<String> permissionDeviceTypes;

    /**
     * 权限过滤区域id
     */
    private Set<String> permissionAreaIds;

}
