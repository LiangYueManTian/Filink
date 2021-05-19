package com.fiberhome.filink.rfid.dao.statistics;

import com.fiberhome.filink.rfid.req.statistics.opticable.CoreStatisticsReq;
import com.fiberhome.filink.rfid.req.statistics.opticable.OpticCableInfoSectionStatisticsReq;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 光缆及纤芯统计 dao
 * </p>
 *
 * @author congcongsun2@wistronits.com
 * @since 2019-05-31
 */
public interface FiberOpticsAndCoreStatisticsDao {

    /**
     * 光纤统计
     *
     * @return List<Map < String, String>>
     */
    List<Map<String, String>> opticalFiberStatistics();

    /**
     * 光缆段统计
     *
     * @param opticCableInfoSectionStatisticsReq 查询条件
     * @return List<Map < String, String>>
     */
    List<Map<String, String>> opticalFiberSection(OpticCableInfoSectionStatisticsReq opticCableInfoSectionStatisticsReq);

}
