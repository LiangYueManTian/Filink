package com.fiberhome.filink.fdevice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/6 15:48
 * @Description: com.fiberhome.filink.fdevice.dto
 * @version: 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceLogTopNumReq {

    /**
     * 区域ID集合
     */
    private List<String> areaIdList;

    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 设施类型集合
     */
    private List<String> deviceTypeList;

    /**
     * 起始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 起始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 统计总数
     */
    private Integer topTotal;
}
