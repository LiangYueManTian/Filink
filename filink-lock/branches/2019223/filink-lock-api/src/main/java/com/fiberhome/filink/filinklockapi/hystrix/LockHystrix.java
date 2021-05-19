package com.fiberhome.filink.filinklockapi.hystrix;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.filinklockapi.bean.Lock;
import com.fiberhome.filink.filinklockapi.feign.LockFeign;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 更新电子锁状态熔断类
 * @author CongcaiYu
 */
@Log4j
@Component
public class LockHystrix implements LockFeign {

    /**
     * 更新电子锁状态熔断
     * @param locks 电子锁信息
     * @return 结果
     */
    @Override
    public Result updateLockStatus(List<Lock> locks) {
        log.info("update lock status failed>>>>>>>>>");
        return null;
    }
}
