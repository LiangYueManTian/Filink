package com.fiberhome.filink.menuapi.fallback;

import com.fiberhome.filink.menuapi.api.MenuFeign;
import com.fiberhome.filink.menuapi.bean.MenuTemplateAndMenuInfoTree;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/16 1:21 PM
 */
@Slf4j
@Component
public class MenuFeignFallback implements MenuFeign {
    private static final String INFO ="Menu service feign call blown》》》》》》》》》》";
    @Override
    public MenuTemplateAndMenuInfoTree getShowMenuTemplate() {
        log.error(INFO);
        return null;
    }
}
