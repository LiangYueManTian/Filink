package com.fiberhome.filink.filinklockapi.feign;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.filinklockapi.bean.Lock;
import com.fiberhome.filink.filinklockapi.hystrix.LockHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 电子锁feign
 * @author CongcaiYu
 */
@FeignClient(name = "filink-lock-server",fallback = LockHystrix.class)
public interface LockFeign {

    /**
     * 批量更新电子锁状态
     * @param lock 电子锁信息
     * @return 操作结果
     */
    @PutMapping("/lock/batchUpdate")
    Result updateLockStatus(@RequestBody List<Lock> lock);
}
