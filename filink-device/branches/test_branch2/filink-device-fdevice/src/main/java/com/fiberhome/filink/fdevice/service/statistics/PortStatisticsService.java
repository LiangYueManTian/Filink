package com.fiberhome.filink.fdevice.service.statistics;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.bean.DevicePortUtilizationRate;
import com.fiberhome.filink.deviceapi.bean.TopNoPortStatisticsReq;

import java.util.List;


/**
 * <p>
 * 设施端口使用率信息表 Service
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-06-14
 */

public interface PortStatisticsService {
    /**
     * 新增
     *
     * @param list 设施端口使用率信息
     * @return Integer
     */
    Integer addPortStatistics(List<DevicePortUtilizationRate> list);

    /**
     * 新增
     *
     * @param deviceId 设施id
     * @return Integer
     */
    Result queryPortStatistics(String deviceId);

    /**
     * 查询TopN
     *
     * @param topNoPortStatisticsReq 查询条件
     * @return Result
     */
    Result queryPortTopNo(TopNoPortStatisticsReq topNoPortStatisticsReq);
}
