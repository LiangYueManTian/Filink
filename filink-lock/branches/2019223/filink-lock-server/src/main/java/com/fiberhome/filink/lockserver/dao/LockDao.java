package com.fiberhome.filink.lockserver.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.lockserver.bean.Lock;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 电子锁Dao
 * @author CongcaiYu
 */
public interface LockDao extends BaseMapper<Lock> {

    /**
     * 根据设施id查询电子锁信息
     * @param deviceId 设施id
     * @return 电子锁集合
     */
    List<Lock> queryLockByDeviceId(String deviceId);

    /**
     * 保存电子锁信息
     * @param lock 电子锁信息
     */
    void saveLockInfo(Lock lock);

    /**
     * 更新电子锁状态
     * @param lock 电子锁信息
     */
    void updateLockStatus(Lock lock);

    /**
     * 根据设施id和锁具编号查询电子锁信息
     *
     * @param deviceId 设施id
     * @param slotNum 锁具编号
     * @return 电子锁信息
     */
    Lock queryLockByDeviceIdAndSlotNum(@Param(value = "deviceId") String deviceId,
                                       @Param(value = "slotNum") String slotNum);

    /**
     * 根据设施id和锁具编号查询电子锁信息
     *
     * @param deviceId 设施id
     * @param slotNumList 锁具编号集合
     * @return 电子锁信息集合
     */
    List<Lock> queryLockByDeviceIdAndSlotNumList(@Param(value = "deviceId") String deviceId,
                                           @Param(value = "slotNumList") List<String> slotNumList);
}
