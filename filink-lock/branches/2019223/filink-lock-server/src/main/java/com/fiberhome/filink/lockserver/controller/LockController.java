package com.fiberhome.filink.lockserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.lockserver.bean.Lock;
import com.fiberhome.filink.lockserver.bean.OpenLockBean;
import com.fiberhome.filink.lockserver.exception.FiLinkLockException;
import com.fiberhome.filink.lockserver.service.LockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 电子锁控制层
 * @author CongcaiYu
 */
@RestController
@RequestMapping("lock")
public class LockController {

    @Autowired
    private LockService lockService;


    /**
     * 保存电子锁信息
     * @param lock 电子锁信息
     * @return 结果
     */
    @PostMapping
    public Result saveLock(@RequestBody Lock lock){
        return lockService.saveLockInfo(lock);
    }

    /**
     * 批量更新电子锁状态
     * @param locks 电子锁信息
     * @return 操作结果
     */
    @PutMapping("/batchUpdate")
    public Result batchUpdateLockStatus(@RequestBody List<Lock> locks){
        return lockService.batchUpdateLockStatus(locks);
    }


    /**
     * 根据设施id查询电子锁信息
     * @param deviceId 设施id
     * @return 结果
     */
    @GetMapping("/{deviceId}")
    public Result getLockByDeviceId(@PathVariable String deviceId){
        List<Lock> locks = lockService.queryLockByDeviceId(deviceId);
        return ResultUtils.success(locks);
    }

    /**
     * 开锁
     * @param openLockBean 开锁请求参数
     * @return 操作结果
     */
    @PostMapping("/openLock")
    public Result openLock(@RequestBody OpenLockBean openLockBean){
        //参数校验
        if(openLockBean == null
                || StringUtils.isEmpty(openLockBean.getDeviceId())
                || openLockBean.getSlotNumList() == null
                || openLockBean.getSlotNumList().size() == 0){
            throw new FiLinkLockException("deviceId or slotNumList is null>>>>>>>");
        }
        return lockService.openLock(openLockBean);
    }

}
