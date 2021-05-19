package com.fiberhome.filink.rfidapi.fallback;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfidapi.api.DeleteDeviceFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 *  删除设施服务
 *
 * @author chaofanrong
 * @date 2019/6/17
 */
@Slf4j
@Component
public class DeleteDeviceFeignFallback implements DeleteDeviceFeign {

    @Override
    public Boolean checkDevice(List<String> deviceIds) {
        // TODO: 2019/6/17 未来根据需求修改
        log.info("----------->调用rfid服务熔断");
        return null;
    }

    @Override
    public Result recoverLabelWithDeviceId(List<String> deviceIds) {
        log.info("----------->调用rfid服务熔断");
        return null;
    }
}
