package com.fiberhome.filink.lockserver.service;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.lockserver.bean.Control;
import com.fiberhome.filink.lockserver.bean.ControlParam;
import com.fiberhome.filink.lockserver.bean.ControlReq;
import com.fiberhome.filink.lockserver.bean.Lock;
import com.fiberhome.filink.lockserver.bean.OpenLockBean;
import com.fiberhome.filink.lockserver.bean.QrCodeBean;

import java.util.List;

/**
 * 电子锁接口
 *
 * @author CongcaiYu
 */
public interface LockService {


    /**
     * 根据主控id查询电子锁信息
     *
     * @param controlId 主控id
     * @return 电子锁集合
     */
    List<Lock> queryLockByControlId(String controlId);

    /**
     * 根据设施id查询电子锁信息
     *
     * @param deviceId 设施id
     * @return 电子锁集合
     */
    List<Lock> queryLockByDeviceIdToCall(String deviceId);

    /**
     * 根据设施id查询电子锁信息(不需要权限)
     *
     * @param deviceId 设施id
     * @return 电子锁集合
     */
    List<Lock> queryLockByDeviceId(String deviceId);


    /**
     * 根据二维码查询电子锁及主控信息
     *
     * @param qrCodeBean 二维码实体
     * @return 电子锁及主控信息
     */
    Result queryLockAndControlByQrCode(QrCodeBean qrCodeBean);

    /**
     * 根据设施id查询电子锁锁具信息
     *
     * @param controlReq 参数对象
     * @return 电子锁锁具信息
     */
    Result queryLockAndControlByDeviceId(ControlReq controlReq);

    /**
     * 根据二维码开锁
     *
     * @param qrCodeBean 二维码实体
     * @return 开锁结果
     */
    Result openLockByQrCode(QrCodeBean qrCodeBean);

    /**
     * 更新电子锁状态
     *
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
     * 开锁  for pda
     *
     * @param openLockBean 开锁参数
     * @return 操作结果
     */
    Result openLockForPda(OpenLockBean openLockBean);

    /**
     * 根据设施id查询门锁状态
     *
     * @param lockBean 锁参数
     * @return 门锁状态
     */
    Result queryLockStatusByDeviceId(OpenLockBean lockBean);

    /**
     * 分页查询电子锁信息
     *
     * @param pageCondition 分页查询条件
     * @return 查询结果
     */
    Result lockListByPage(PageCondition pageCondition);

    /**
     * 分页查询电子锁
     *
     * @param deviceList 设施id集合
     * @return 电子锁列表
     */
    List<Lock> lockListByDeviceIds(List<String> deviceList);

    /**
     * 批量更新电子锁状态
     *
     * @param locks 电子锁集合
     * @return 操作结果
     */
    Result batchUpdateLockStatus(List<Lock> locks);

    /**
     * 电子锁配置数据上传(新增/更新)
     *
     * @param control 主控实体
     * @return 操作结果
     */
    Result operateControlAndLock(Control control);

    /**
     * 根据设施id和门编号查询电子锁信息
     *
     * @param deviceId 设施id
     * @param doorNum  门编号
     * @return 电子锁信息
     */
    Lock queryLockByDeviceIdAndDoorNum(String deviceId, String doorNum);

    /**
     * 根据二维码查询电子锁信息
     *
     * @param qrCode 二维码
     * @return 锁信息
     */
    Lock queryLockByQrCode(String qrCode);

    /**
     * 删除平台注册记录
     *
     * @param controlParam 主控参数
     */
    void deleteFromPlatForm(ControlParam controlParam);

    /**
     * 删除授权
     *
     * @param deviceId    设施id
     * @param doorNumList 门编号集合
     */
    void deleteAuth(String deviceId, List<String> doorNumList);

}
