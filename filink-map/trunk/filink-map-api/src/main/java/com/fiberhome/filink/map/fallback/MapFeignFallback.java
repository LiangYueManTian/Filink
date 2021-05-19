package com.fiberhome.filink.map.fallback;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.map.api.MapFeign;
import com.fiberhome.filink.map.bean.BaiduArea;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  MapFeign熔断类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/7
 */
@Slf4j
@Component
public class MapFeignFallback implements MapFeign {
    /**
     * 根据名称查询地区
     *
     * @param area 参数bean
     * @return 地区
     */
    @Override
    public Result queryAreaByName(BaiduArea area) {
        log.info("根据名称查询地区feign调用熔断》》》》》》》》》》");
        return null;
    }

    /**
     * 根据名称查询其子地区
     *
     * @param area 参数bean
     * @return 地区集合
     */
    @Override
    public Result querySonAreaByName(BaiduArea area) {
        log.info("根据名称查询其子地区feign调用熔断》》》》》》》》》》");
        return null;
    }

    /**
     * 根据id更新地区
     *
     * @param area
     * @return
     */
    @Override
    public Result updateAreaById(BaiduArea area) {
        log.info("根据id更新地区feign调用熔断》》》》》》》》》》");
        return null;
    }
}
