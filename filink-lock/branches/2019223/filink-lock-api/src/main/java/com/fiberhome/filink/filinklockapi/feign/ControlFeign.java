package com.fiberhome.filink.filinklockapi.feign;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.filinklockapi.bean.Control;
import com.fiberhome.filink.filinklockapi.hystrix.ControlHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 电子锁feign
 * @author CongcaiYu
 */
@FeignClient(name = "filink-lock-server",fallback = ControlHystrix.class)
public interface ControlFeign {

    /**
     * 根据设施id修改主控信息
     * @param control 主控信息
     * @return 操作结果
     */
    @PutMapping("/control")
    Result updateControlParamsByDeviceId(@RequestBody Control control);

    /**
     * 根据设施id查询主控信息
     * @param deviceId 设施id
     * @return 主控信息
     */
    @GetMapping("/control/feign/{deviceId}")
    Control getControlParams(@PathVariable("deviceId") String deviceId);

}
