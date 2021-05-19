package com.fiberhome.filink.menuapi.api;


import com.fiberhome.filink.menuapi.bean.MenuTemplateAndMenuInfoTree;
import com.fiberhome.filink.menuapi.fallback.MenuFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;


/**
 * @author qiqizhu@wistronits.com
 * @Date: 2019/1/25 16:44
 */
@FeignClient(name = "filink-system-server", path = "/menu",fallback = MenuFeignFallback.class)
public interface MenuFeign {
    /**
     * 根据部门id批量删除与区域关系信息
     * @return 查询结果
     */
    @PostMapping("/getShowMenuTemplate")
    MenuTemplateAndMenuInfoTree getShowMenuTemplate();
}
