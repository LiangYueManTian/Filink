package com.fiberhome.filink.rfidapi.api;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfidapi.constant.AppConstant;
import com.fiberhome.filink.rfidapi.fallback.DeleteDeviceFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * 对外暴露的api
 *
 * @author chaofanrong
 * @date 2019/6/17
 */
@FeignClient(name = AppConstant.RFID_SERVICE_NAME, fallback = DeleteDeviceFeignFallback.class)
public interface DeleteDeviceFeign {

    /**
     * 校验设施能否被删除
     *
     * @param deviceIds 设施id列表
     * @return Boolean 结果
     */
    @PostMapping("/deleteDevice/checkDevice")
    Boolean checkDevice(@RequestBody List<String> deviceIds);

    /**
     * 回收标签--删除设施
     *
     * @param deviceIds 设施id
     * @return Result Result结果
     */
    @PostMapping("/facility/recoverLabelWithDeviceId")
    Result recoverLabelWithDeviceId(@RequestBody List<String> deviceIds);

}
