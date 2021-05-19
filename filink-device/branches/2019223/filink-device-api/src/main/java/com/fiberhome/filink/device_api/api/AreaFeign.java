package com.fiberhome.filink.device_api.api;

import com.fiberhome.filink.device_api.fallback.AreaFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 区域Feign
 *
 * @author qiqizhu@wistronits.com
 * @Date: 2019/1/25 16:44
 */
@FeignClient(name = "filink-device-server", path = "/areaInfo", fallback = AreaFeignFallback.class)
public interface AreaFeign {
    /**
     * 根据部门id批量删除与区域关系信息
     *
     * @param deptIds 删除id集合
     * @return 返回结果
     */
    @PostMapping("/deleteAreaDeptRelation")
    Boolean deleteAreaDeptRelation(@RequestBody List<String> deptIds);
}
