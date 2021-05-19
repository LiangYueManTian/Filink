package com.fiberhome.filink.fdevice.dao.statistics;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.fdevice.bean.devicelog.UnlockingStatistics;
import com.fiberhome.filink.fdevice.dto.DeviceLogTopNum;
import com.fiberhome.filink.fdevice.dto.DeviceLogTopNumReq;
import com.fiberhome.filink.fdevice.dto.UnlockingReq;

import java.util.List;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/12 17:33
 * @Description: com.fiberhome.filink.fdevice.dao.statistics
 * @version: 1.0
 */
public interface UnlockingStatisticsDao extends BaseMapper<UnlockingStatistics> {

    /**
     * 查询数据库最近开锁数据日期
     * @return
     */
    String queryMaxStatisticsDate();

    /**
     * 查询单个设施一段时间内的开锁次数
     * @param unlockingReq
     * @return
     */
    List<UnlockingStatistics> queryUnlockingTimesByDeviceId(UnlockingReq unlockingReq);

    /**
     * 查询开锁次数最多的设施
     * @param deviceLogTopNumReq
     * @return
     */
    List<DeviceLogTopNum> queryUnlockingTopNum(DeviceLogTopNumReq deviceLogTopNumReq);
}
