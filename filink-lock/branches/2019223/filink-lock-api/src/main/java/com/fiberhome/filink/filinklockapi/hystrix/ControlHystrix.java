package com.fiberhome.filink.filinklockapi.hystrix;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.filinklockapi.bean.Control;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 主控熔断类
 * @author CongcaiYu
 */
@Log4j
@Component
public class ControlHystrix implements ControlFeign {

    /**
     * 更新主控信息熔断
     * @param control 主控信息
     * @return 操作结果
     */
    @Override
    public Result updateControlParamsByDeviceId(Control control) {
        log.info("update control params failed>>>>>>>>>");
        return null;
    }

    /**
     * 根据设施id查询主控信息
     * @param deviceId 设施id
     * @return 主控信息
     */
    @Override
    public Control getControlParams(String deviceId) {
        log.info("query control params failed>>>>>>>>>>>>");
        return null;
    }

}
