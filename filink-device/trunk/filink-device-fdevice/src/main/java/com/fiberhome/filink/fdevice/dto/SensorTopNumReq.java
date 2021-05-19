package com.fiberhome.filink.fdevice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/3 16:28
 * @Description: com.fiberhome.filink.lockserver.bean
 * @version: 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SensorTopNumReq {
    /**
     * 传感值类型
     */
    private String sensorType;

    /**
     * 区域ID集合
     */
    private List<String> areaIdList;

    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 统计数量
     */
    private Integer topTotal;

    /**
     * 最大值/最小值
     */
    private boolean isTop;
}
