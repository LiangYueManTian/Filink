package com.fiberhome.filink.lockserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.lockserver.bean.Control;
import com.fiberhome.filink.lockserver.bean.SetConfigBean;
import com.fiberhome.filink.lockserver.exception.FiLinkControlException;
import com.fiberhome.filink.lockserver.service.MasterControlService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 主控控制器
 *
 * @author CongcaiYu
 */
@RestController
@RequestMapping("/control")
@Log4j
public class MasterControlController {

    @Autowired
    private MasterControlService controlService;

    /**
     * 保存主控参数信息
     *
     * @param control 主控信息
     * @return 操作结果
     */
    @PostMapping
    public Result saveControlParams(@RequestBody Control control) {
        // TODO build2
        return controlService.saveControlParams(control);
    }


    /**
     * 根据设施id修改主控信息
     *
     * @param control 主控信息
     * @return 操作结果
     */
    @PutMapping
    public Result updateControlParamsByDeviceId(@RequestBody Control control) {
        // TODO build2完善
        return controlService.updateControlParamsByDeviceId(control);
    }

    /**
     * 根据设施id查询主控信息
     *
     * @param deviceId 设施id
     * @return 主控信息
     */
    @GetMapping("/{deviceId}")
    public Result getControlParams(@PathVariable String deviceId) {
        Control controlInfo = controlService.getControlInfoByDeviceId(deviceId);
        return ResultUtils.success(controlInfo);
    }

    /**
     * 根据设施id查询主控信息(feign)
     *
     * @param deviceId 设施id
     * @return 主控信息
     */
    @GetMapping("/feign/{deviceId}")
    public Control getControlFeign(@PathVariable String deviceId) {
        log.info("feign get control deviceId : " + deviceId + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        return controlService.getControlInfoByDeviceId(deviceId);
    }


    /**
     * 配置设施策略
     *
     * @param setConfigBean 参数
     * @return 操作结果
     */
    @PostMapping("/setConfig")
    public Result setConfig(@RequestBody SetConfigBean setConfigBean) {
        //参数校验
        if (setConfigBean == null
                || setConfigBean.getDeviceIds() == null
                || setConfigBean.getDeviceIds().size() == 0) {
            throw new FiLinkControlException("deviceId is null>>>>>>>>>");
        }
        if (setConfigBean.getConfigParams() == null || setConfigBean.getConfigParams().size() == 0) {
            throw new FiLinkControlException("config value is null>>>>>>>>>>>>>");
        }
        return controlService.setConfig(setConfigBean);
    }

}
