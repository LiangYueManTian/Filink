package com.fiberhome.filink.lockserver.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.lockserver.bean.Lock;
import com.fiberhome.filink.lockserver.bean.OpenLockBean;

import java.util.List;

/**
 * 电子锁接口
 * @author CongcaiYu
 */
public interface LockService {

    /**
     * 保存电子锁信息
     * @param lock 电子锁信息
     * @return Result 结果
     */
    Result saveLockInfo(Lock lock);

    /**
     * 根据设施id查询电子锁信息
     * @param deviceId 设施id
     * @return 电子锁集合
     */
    List<Lock> queryLockByDeviceId(String deviceId);

    /**
     * 更新电子锁状态
     * @param lock 电子锁信息
     */
    void updateLockStatus(Lock lock);

    /**
     * 开锁
     *
     * @param openLockBean 开锁参数
     * @return 操作结果
     */
    Result openLock(OpenLockBean openLockBean);

    /**
     * 批量更新电子锁状态
     *
     * @param locks 电子锁集合
     * @return 操作结果
     */
    Result batchUpdateLockStatus(List<Lock> locks);

}
