package com.fiberhome.filink.rfid.dao.statistics;

import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.DiscPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.FramePortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.JumpFiberPortStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.odnfacilityresources.MeltFiberPortStatisticsReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * ODN设施资源统计 dao
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-05-31
 */
public interface OdnFacilityResourcesStatisticsDao {
    /**
     * 跳纤侧端口统计(本端)
     *
     * @param jumpFiberPortStatisticsReq 统计条件
     * @return List
     */
    List<Map<String, Integer>> jumpFiberPortStatistics(JumpFiberPortStatisticsReq jumpFiberPortStatisticsReq);

    /**
     * 端口总数统计
     *
     * @param meltFiberPortStatisticsReq 统计条件
     * @return List
     */
    Integer portCountStatistics(MeltFiberPortStatisticsReq meltFiberPortStatisticsReq);

    /**
     * 熔纤侧端口统计
     *
     * @param meltFiberPortStatisticsReq 统计条件
     * @return List
     */
    Integer meltFiberPortStatistics(MeltFiberPortStatisticsReq meltFiberPortStatisticsReq);

    /**
     * 盘端口统计
     *
     * @param discPortStatisticsReq 统计条件
     * @return List
     */
    List<Map<String, Integer>> discPortStatistics(DiscPortStatisticsReq discPortStatisticsReq);

    /**
     * 盘端口统计
     *
     * @param framePortStatisticsReq 统计条件
     * @return List
     */
    List<Map<String, Integer>> framePortStatistics(FramePortStatisticsReq framePortStatisticsReq);

    /**
     * 单个设施端口总数统计
     *
     * @param deviceId 设施id
     * @return Integer
     */
    Integer countPortStatistics(@Param("deviceId") String deviceId);

    /**
     * 多个个设施端口总数统计
     *
     * @param list 设施id list
     * @return List
     */
    List<Map<String, Object>> countPortStatisticsList(@Param("list") List list);

}
