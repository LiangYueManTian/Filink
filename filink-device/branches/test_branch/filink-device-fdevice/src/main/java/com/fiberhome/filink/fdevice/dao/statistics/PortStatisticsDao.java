package com.fiberhome.filink.fdevice.dao.statistics;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.deviceapi.bean.DevicePortUtilizationRate;
import com.fiberhome.filink.deviceapi.bean.TopNoPortStatisticsReq;
import com.fiberhome.filink.fdevice.dto.TopNPortStatisticsResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 设施端口使用率信息表 DAO
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-06-14
 */
public interface PortStatisticsDao extends BaseMapper<DevicePortUtilizationRate> {

    /**
     * 新增
     *
     * @param list 设施端口使用率信息
     * @return Integer
     */
    Integer addPortStatistics(@Param("list") List<DevicePortUtilizationRate> list);

    /**
     * 删除
     *
     * @param list 设施端口使用率信息
     * @return Integer
     */
    Integer deletePortStatistics(@Param("list") List<DevicePortUtilizationRate> list);

    /**
     * 查询
     *
     * @param deviceId 设施端口使用率信息
     * @return Double
     */
    Double queryPortStatisticsByDeviceId(@Param("deviceId") String deviceId);

    /**
     * 查找端口使用率topN
     *
     * @param topNoPortStatisticsReq 查询条件
     * @return TopNPortStatisticsResp
     */
    List<TopNPortStatisticsResp> queryPortStatistics(TopNoPortStatisticsReq topNoPortStatisticsReq);
}
