package com.fiberhome.filink.map.api;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.map.bean.BaiduArea;
import com.fiberhome.filink.map.fallback.MapFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 地区对外提供的API
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/7
 */
@FeignClient(name = "filink-map-server", path = "/map", fallback = MapFeignFallback.class)
public interface MapFeign {
    /**
     * 根据名称查询地区
     *
     * @param area 参数bean
     * @return  地区
     */
    @PostMapping("getArea")
    Result queryAreaByName(@RequestBody BaiduArea area);

    /**
     * 根据名称查询其子地区
     *
     * @param area 参数bean
     * @return 子地区集合
     */
    @GetMapping("getSonArea")
    Result querySonAreaByName(@RequestBody BaiduArea area);

    /**
     * 根据id更新地区
     *
     * @param area
     * @return  更新结果
     */
    @PostMapping("/updateArea")
    Result updateAreaById(@RequestBody BaiduArea area);
}
