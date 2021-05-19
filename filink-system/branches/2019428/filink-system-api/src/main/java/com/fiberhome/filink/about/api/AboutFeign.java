package com.fiberhome.filink.about.api;

import com.fiberhome.filink.about.bean.About;
import com.fiberhome.filink.about.fallback.AboutFeignFallback;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.menuapi.fallback.MenuFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * <p>
 * 关于对外提供的API
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/6
 */
@FeignClient(name = "filink-system-server", path = "/about", fallback = AboutFeignFallback.class)
public interface AboutFeign {

    /**
     * 根据id更新关于信息
     *
     * @param about
     * @return
     */
    @PostMapping("/update")
    Result updateAbout(About about);

}
