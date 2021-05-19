package com.fiberhome.filink.demoapi.api;

import com.fiberhome.filink.demoapi.fallback.DemoApiFallback;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * @author yuanyao@wistronits.com
 * create on 2019-09-12 10:02
 */
@FeignClient(name = "filink-alarmcurrent-server", fallback = DemoApiFallback.class)
public interface DemoApi {
}
