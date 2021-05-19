package com.fiberhome.filink.deviceapi.bean;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * 端口使用TopN统计 请求
 * </p>
 *
 * @author congcongsun2
 * @since 2019-06-14
 */
@Data
public class TopNoPortStatisticsReq {
    /**
     * 区域id
     */
    private List<String> areaIds;
    /**
     * 设备类型
     */
    private List<String> deviceTypes;
    /**
     * 统计端口占用前n设施
     */
    private Integer topN;
}
