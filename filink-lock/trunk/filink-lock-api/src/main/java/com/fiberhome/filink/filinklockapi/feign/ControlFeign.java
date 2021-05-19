package com.fiberhome.filink.filinklockapi.feign;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.bean.RemoveAlarm;
import com.fiberhome.filink.filinklockapi.hystrix.ControlHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * 电子锁feign
 *
 * @author CongcaiYu
 */
@FeignClient(name = "filink-lock-server", path = "/control", fallback = ControlHystrix.class)
public interface ControlFeign {

    /**
     * 判断服务是否畅通
     *
     * @return true:畅通
     */
    @PostMapping("/feign/ping")
    boolean ping();

    /**
     * 根据设施id查询主控信息
     *
     * @param deviceId 设施id
     * @return 主控信息
     */
    @GetMapping("/feign/{deviceId}")
    List<ControlParam> getControlParams(@PathVariable("deviceId") String deviceId);


    /**
     * 根据主控id获取设施id
     *
     * @param controlId 主控id
     * @return 设施id
     * @throws Exception
     */
    @GetMapping("/feign/device/{controlId}")
    String getDeviceIdByControlId(@PathVariable("controlId") String controlId) throws Exception;

    /**
     * 根据id查询主控信息
     *
     * @param controlId
     * @return 主控信息
     */
    @GetMapping("/feign/info/{controlId}")
    ControlParam getControlParamById(@PathVariable("controlId") String controlId);


    /**
     * 根据主控id更新主控的部署状态
     *
     * @param controlParam 主控信息
     * @return 结果
     */
    @PostMapping("/feign/updateDeployStatusById")
    Result updateDeployStatusById(@RequestBody ControlParam controlParam);

    /**
     * 根据主控id更新主控的设施状态
     *
     * @param controlParam 主控信息
     * @return 查询结果
     */
    @PostMapping("/feign/updateDeviceStatusById")
    Result updateDeviceStatusById(@RequestBody ControlParam controlParam);

    /**
     * 根据主控id更新主控的设施状态及部署状态
     *
     * @param controlParam 主控信息
     * @return 查询结果
     */
    @PostMapping("/feign/updateControlStatusById")
    Result updateControlStatusById(@RequestBody ControlParam controlParam);

    /**
     * 根据设施id更新主控的部署状态
     *
     * @param controlParam 主控信息
     * @return 结果
     */
    @PostMapping("/feign/deployStatus/update")
    Result updateDeployStatusByDeviceId(@RequestBody ControlParam controlParam);

    /**
     * 更新主控信息
     *
     * @param controlParam 主控信息
     * @return 结果
     */
    @PostMapping("/feign/updateControlParam")
    Result updateControlParam(@RequestBody ControlParam controlParam);

    /**
     * 根据设施id删除主控
     *
     * @param deviceIdList 设施id集合
     * @return 操作结果
     */
    @PostMapping("/deleteControlByDeviceIds")
    Result deleteControlByDeviceIds(@RequestBody List<String> deviceIdList);


    /**
     * 根据主控id及告警类型清除告警显示状态
     *
     * @param removeAlarmList 告警清除集合
     * @return 操作结果
     */
    @PostMapping("/feign/removeAlarm")
    Result removeAlarm(@RequestBody List<RemoveAlarm> removeAlarmList);
}

