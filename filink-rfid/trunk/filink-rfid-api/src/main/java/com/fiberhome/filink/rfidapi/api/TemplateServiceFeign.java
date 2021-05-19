package com.fiberhome.filink.rfidapi.api;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfidapi.bean.TemplateReqDto;
import com.fiberhome.filink.rfidapi.constant.AppConstant;
import com.fiberhome.filink.rfidapi.fallback.TemplateServiceFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * 对外暴露的api
 *
 * @author liyj
 * @date 2019/6/5
 */
@FeignClient(name = AppConstant.RFID_SERVICE_NAME, fallback = TemplateServiceFeignFallback.class)
public interface TemplateServiceFeign {

    /**
     * 保存模板和设施的信息
     *
     * @param reqDto 模板相关的设施信息
     * @return Result Result结果
     */
    @PostMapping("/template/saveDeviceAndTempRelation")
    Result saveDeviceAndTempRelation(@RequestBody TemplateReqDto reqDto);

    /**
     * 验证设施是否配置了业务信息
     *
     * @param deviceId 设施id
     * @return true/false 配置/没有配置
     */
    @GetMapping("/facility/isExistBus/{deviceId}")
    Boolean isExistBus(@PathVariable("deviceId") String deviceId);
}
