package com.fiberhome.filink.device_api.fallback;

import com.fiberhome.filink.device_api.api.AreaFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 区域模块调用异常处理
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/16 1:21 PM
 */
@Slf4j
@Component
public class AreaFeignFallback implements AreaFeign {

    /**
     * 根据部门id批量删除与区域关系信息
     * @param deptIds 删除id集合
     * @return 返回结果
     */
    @Override
    public Boolean deleteAreaDeptRelation(List<String> deptIds) {
        log.info("区域服务feign调用熔断》》》》》》》》》》");
        return null;
    }
}
