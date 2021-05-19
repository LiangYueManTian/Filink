package com.fiberhome.filink.rfidapi.fallback;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.rfidapi.api.TemplateServiceFeign;
import com.fiberhome.filink.rfidapi.bean.TemplateReqDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 模板服务告警
 *
 * @author liyj
 * @date 2019/6/5
 */
@Slf4j
@Component
public class TemplateServiceFeignFallback implements TemplateServiceFeign {


    /**
     * 保存模板和设施的信息
     *
     * @param reqDto reqDto
     * @return Result
     */
    @Override
    public Result saveDeviceAndTempRelation(TemplateReqDto reqDto) {
        log.info("not find templateDto");
        return null;
    }
}
