package com.fiberhome.filink.fdevice.dto;

import lombok.Data;

/**
 * <p>
 * 端口使用TopN统计 返回
 * </p>
 *
 * @author congcongsun2
 * @since 2019-06-14
 */
@Data
public class TopNPortStatisticsResp {
    /**
     * 设施id
     */
    private String deviceId;
    /**
     * 端口使用率
     */
    private Double utilizationRate;
}
