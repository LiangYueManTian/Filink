package com.fiberhome.filink.fdevice.dto;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.SortCondition;
import lombok.Data;

import java.util.List;

/**
 * PDA查询附近设施传输类
 * @Author: zl
 * @Date: 2019/4/13 15:52
 * @Description: com.fiberhome.filink.fdevice.dto
 * @version: 1.0
 */
@Data
public class DeviceReqPda {
    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 半径（米）
     */
    private String radius;

    /**
     * 区域ID
     */
    private List<String> areaId;

    /**
     * 设施类型
     */
    private List<String> deviceType;

    /**
     * 设施状态
     */
    private List<String> deviceStatus;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 是否收藏
     */
    private String isCollecting;

    /**
     * 分页条件
     */
    private PageCondition pageCondition;

    /**
     * 排序条件
     */
    private SortCondition sortCondition;
}
