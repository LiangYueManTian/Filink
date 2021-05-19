package com.fiberhome.filink.alarmCurrent_api.api;

import com.fiberhome.filink.alarmCurrent_api.fallback.AlarmFeignFallback;
import java.util.List;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 当前告警模块feign测试 中转站
 *
 * @author wtao103@fiberhome.com
 * create on 2018/12/16 5:19 PM
 */
@FeignClient(name = "filink-alarmCurrent-server", fallback = AlarmFeignFallback.class)
public interface AlarmCurrentFeign {

    /**
     * 查询设备信息是否存在
     *
     * @param alarmSources 设备id
     * @return 判断结果
     */
    @PostMapping("/alarmCurrent/queryAlarmSource")
    List<String> queryAlarmSource(@RequestBody List<String> alarmSources);

    /**
     * 查询区域信息是否存在
     *
     * @param areas 区域id
     * @return 判断结果
     */
    @PostMapping("/alarmCurrent/queryArea")
    List<String> queryArea(@RequestBody List<String> areas);
}
