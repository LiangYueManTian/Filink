package com.fiberhome.filink.lockserver.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.lockserver.bean.Lock;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 电子锁Dao
 *
 * @author CongcaiYu
 */
public interface LockDao extends BaseMapper<Lock> {

    /**
     * 根据主控id查询电子锁信息
     *
     * @param controlId 主控id
     * @return 电子锁集合
     */
    List<Lock> queryLockByControlId(String controlId);

    /**
     * 分页查询电子锁
     *
     * @param deviceList 设施id集合
     * @return 电子锁列表
     */
    List<Lock> lockListByDeviceIds(@Param("deviceList") List deviceList);

    /**
     * 根据二维码查询电子锁信息
     *
     * @param qrcode 二维码
     * @return 电子锁信息
     */
    Lock queryLockByQrcode(String qrcode);

    /**
     * 根据二维码查询电子锁信息
     *
     * @param lockCode 锁芯id
     * @return 电子锁信息
     */
    Lock queryLockByLockCode(String lockCode);

    /**
     * 根据设施id根据二维码查询电子锁信息
     *
     * @param deviceId 设施id
     * @return 电子锁集合
     */
    List<Lock> queryLockByDeviceId(String deviceId);

    /**
     * 根据设施id和门编号查询电子锁信息
     *
     * @param deviceId 设施id
     * @param doorNum  门编号
     * @return 电子锁信息
     */
    Lock queryLockByDeviceIdAndDoorNum(@Param("deviceId") String deviceId, @Param("doorNum") String doorNum);

    /**
     * 更新电子锁状态
     *
     * @param lock 电子锁信息
     */
    void updateLockStatus(Lock lock);

    /**
     * 批量新增电子锁信息
     *
     * @param locks 电子锁信息列表
     */
    void addLocks(List<Lock> locks);

    /**
     * 批量更新电子锁信息
     *
     * @param locks     电子锁信息列表
     * @param controlId 主控id
     */
    void updateLocks(@Param("list") List<Lock> locks, @Param("controlId") String controlId);

    /**
     * 查询门锁的数量
     *
     * @param locks     电子锁信息列表
     * @param controlId 主控id
     * @return 结果
     */
    Integer countLocks(@Param("locks") List<Lock> locks, @Param("controlId") String controlId);

    /**
     * 根据设施id和门编号批量删除电子锁信息
     *
     * @param deviceId    设施id
     * @param doorNumList 门编号列表
     */
    void deleteLockByDeviceIdAndDoorNum(@Param("deviceId") String deviceId, @Param("doorNumList") List<String> doorNumList);
}
