package com.fiberhome.filink.alarmSet_api.api;

import com.fiberhome.filink.alarmSet_api.fallback.AlarmSetFeigmFallback;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * 告警设置模块feign测试 中转站
 *
 * @author wtao103@fiberhome.com
 * create on 2018/12/16 5:19 PM
 */
@FeignClient(name = "filink-alarmSet-server", fallback = AlarmSetFeigmFallback.class)
public interface AlarmSetFeigm {

}
