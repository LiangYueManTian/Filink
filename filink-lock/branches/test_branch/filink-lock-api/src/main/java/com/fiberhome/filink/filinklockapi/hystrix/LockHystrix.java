package com.fiberhome.filink.filinklockapi.hystrix;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.filinklockapi.bean.Lock;
import com.fiberhome.filink.filinklockapi.feign.LockFeign;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 更新电子锁状态熔断类
 *
 * @author CongcaiYu
 */
@Log4j
@Component
public class LockHystrix implements LockFeign {

    /**
     * 更新电子锁状态熔断
     *
     * @param locks 电子锁信息
     * @return 结果
     */
    @Override
    public Result updateLockStatus(List<Lock> locks) {
        log.info("update lock status failed>>>>>>>>>");
        return null;
    }

    /**
     * 查询电子锁列表
     *
     * @param deviceIds 设施集合
     * @return 电子锁列表
     */
    @Override
    public List<Lock> lockListByDeviceIds(List<String> deviceIds) {
        log.info("get lockList  failed>>>>>>>>>");
        return null;
    }


    /**
     * 根据设施id和门编号查询电子锁信息
     *
     * @param lock 参数
     * @return 电子锁信息
     */
    @Override
    public Lock queryLockByDeviceIdAndDoorNum(Lock lock) {
        log.info("queryLockByDeviceIdAndDoorNum failed>>>>>>>>>");
        return null;
    }

    /**
     * 根据二维码查询电子锁信息
     *
     * @param qrCode 二维码
     * @return 锁信息
     */
    @Override
    public Lock queryLockByQrCode(String qrCode) {
        log.info("queryLockByQrCode failed>>>>>>>>>");
        return null;
    }
}
