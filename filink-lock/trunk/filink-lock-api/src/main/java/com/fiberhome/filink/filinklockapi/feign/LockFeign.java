package com.fiberhome.filink.filinklockapi.feign;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.filinklockapi.bean.Lock;
import com.fiberhome.filink.filinklockapi.hystrix.LockHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 电子锁feign
 *
 * @author CongcaiYu
 */
@FeignClient(name = "filink-lock-server", path = "/lock", fallback = LockHystrix.class)
public interface LockFeign {

    /**
     * 批量更新电子锁状态
     *
     * @param lock 电子锁信息
     * @return 操作结果
     */
    @PutMapping("/batchUpdate")
    Result updateLockStatus(@RequestBody List<Lock> lock);

    /**
     * 查询电子锁列表
     *
     * @param deviceIds 设施id集合
     * @return 电子锁列表
     */
    @PostMapping("/feign/device/lockList")
    List<Lock> lockListByDeviceIds(@RequestBody List<String> deviceIds);


    /**
     * 查询电子锁设施ID
     * @return 设施ID
     */
    @GetMapping("/feign/device/deviceIdListOfLock")
    List<String> queryDeviceIdListOfLock();

    /**
     * 根据设施id和门编号查询电子锁信息
     *
     * @param lock 参数
     * @return 电子锁信息
     */
    @PostMapping("/feign/queryLockByDeviceIdAndDoorNum")
    Lock queryLockByDeviceIdAndDoorNum(@RequestBody Lock lock);

    /**
     * 根据二维码查询电子锁信息
     *
     * @param qrCode 二维码
     * @return 锁信息
     */
    @GetMapping("/feign/queryLockByQrCode/{qrCode}")
    Lock queryLockByQrCode(@PathVariable("qrCode") String qrCode);
}
