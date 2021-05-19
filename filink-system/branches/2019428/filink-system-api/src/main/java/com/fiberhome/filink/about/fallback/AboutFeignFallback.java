package com.fiberhome.filink.about.fallback;

import com.fiberhome.filink.about.api.AboutFeign;
import com.fiberhome.filink.about.bean.About;
import com.fiberhome.filink.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/6
 */
@Slf4j
@Component
public class AboutFeignFallback implements AboutFeign {
    /**
     * 根据id更新关于信息
     *
     * @param about
     * @return
     */
    @Override
    public Result updateAbout(About about) {
        log.info("更新关于feign调用熔断》》》》》》》》》》");
        return null;
    }
}
