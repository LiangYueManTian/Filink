package com.fiberhome.filink.menuapi.fallback;

import com.fiberhome.filink.menuapi.api.MenuFeign;
import com.fiberhome.filink.menuapi.bean.MenuTemplateAndMenuInfoTree;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/16 1:21 PM
 */
@Slf4j
@Component
public class MenuFeignFallback implements MenuFeign {

    @Override
    public MenuTemplateAndMenuInfoTree getShowMenuTemplate() {
        log.info("菜单服务feign调用熔断》》》》》》》》》》");
        return null;
    }
}
