package com.fiberhome.filink.lockserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.lockserver.bean.ControlParam;
import com.fiberhome.filink.lockserver.bean.ControlParamForControl;
import com.fiberhome.filink.lockserver.bean.ControlReq;
import com.fiberhome.filink.lockserver.bean.SetConfigBean;
import com.fiberhome.filink.lockserver.bean.SetDeployStatusBean;
import com.fiberhome.filink.lockserver.constant.control.ControlI18n;
import com.fiberhome.filink.lockserver.constant.control.ControlResultCode;
import com.fiberhome.filink.lockserver.exception.FiLinkControlException;
import com.fiberhome.filink.lockserver.service.ControlService;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 主控控制器
 *
 * @author CongcaiYu
 */
@RestController
@RequestMapping("/control")
public class ControlController {

    @Autowired
    private ControlService controlService;


    /**
     * 根据主控id查询电子锁主控信息
     *
     * @param controlReq 主控信息请求对象
     * @return 查询结果
     */
    @PostMapping("/info/controlId")
    public Result queryControlByControlId(@RequestBody ControlReq controlReq) {
        //参数校验
        controlReq.checkControlIdIsNull();
        return controlService.queryControlByControlId(controlReq);
    }

    /**
     * 根据设施id查询主控信息
     *
     * @param deviceId 设施id
     * @return 主控信息
     */
    @GetMapping("/{deviceId}")
    public Result getControlParams(@PathVariable String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            throw new FiLinkControlException("deviceId is empty>>>>>>");
        }
        List<ControlParam> controlParamInfoList = controlService.getControlInfoByDeviceIdToCall(deviceId);
        return ResultUtils.success(controlParamInfoList);
    }

    /**
     * 根据设施id查询主控信息(feign)
     *
     * @param deviceId 设施id
     * @return 主控信息
     */
    @GetMapping("/feign/{deviceId}")
    public List<ControlParam> getControlFeign(@PathVariable String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            throw new FiLinkControlException("deviceId is empty>>>>>>");
        }
        return controlService.getControlInfoByDeviceIdToCall(deviceId);
    }

    /**
     * 根据设施id查询pda需要的主控信息(feign)
     *
     * @param deviceId 设施id
     * @return 主控信息 for pda
     */
    @GetMapping("/feign/pda/{deviceId}")
    public List<ControlParamForControl> getControlFeignForPda(@PathVariable String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            throw new FiLinkControlException("deviceId is empty>>>>>>");
        }

        return controlService.getControlParamForControlByDeviceId(deviceId);
    }


    /**
     * 根据主控id获取设施id
     *
     * @param controlId 主控id
     * @return 设施id
     */
    @GetMapping("/feign/device/{controlId}")
    public String getDeviceIdByControlId(@PathVariable String controlId) {
        if (StringUtils.isEmpty(controlId)) {
            throw new FiLinkControlException("controlId is empty>>>>>>>>>>>>>");
        }
        return controlService.getDeviceIdByControlId(controlId);
    }

    /**
     * 根据id查询主控信息
     *
     * @param controlId
     * @return 主控信息
     */
    @GetMapping("/feign/info/{controlId}")
    public ControlParam getControlParamById(@PathVariable String controlId) {
        ControlParam controlParam = new ControlParam();
        if (StringUtils.isEmpty(controlId)) {
            throw new FiLinkControlException("controlId is empty>>>>>>");
        }
        //查询，找不到主控信息，返回空，不报错
        try {
            controlParam = controlService.getControlParamById(controlId);
        } catch (Exception e) {

        }
        return controlParam;
    }

    /**
     * 根据主控id更新主控的部署状态
     *
     * @param controlParam 主控信息
     */
    @PostMapping("/feign/updateDeployStatusById")
    public Result updateDeployStatusById(@RequestBody ControlParam controlParam) {
        controlParam.checkParamsForUpdateDeployStatus();
        controlService.updateDeployStatusById(controlParam);
        return ResultUtils.success();
    }

    /**
     * 根据主控id更新主控的设施状态
     *
     * @param controlParam 主控信息
     */
    @PostMapping("/feign/updateDeviceStatusById")
    public Result updateDeviceStatusById(@RequestBody ControlParam controlParam) {
        controlParam.checkParamsForUpdateDeviceStatus();
        controlService.updateDeviceStatusById(controlParam);
        return ResultUtils.success();
    }

    /**
     * 根据设施id更新主控的部署状态
     *
     * @param setDeployStatusBean 设置部署状态实体
     */
    @PostMapping("/deployStatus/update")
    public Result updateDeployStatusByDeviceId(@RequestBody SetDeployStatusBean setDeployStatusBean) {
        //检查参数
        setDeployStatusBean.checkDepoyStatusBean();
        controlService.updateDeployStatusByDeviceId(setDeployStatusBean.getDeviceIds(), setDeployStatusBean.getDeployStatus());
        return ResultUtils.success(ControlResultCode.SUCCESS, I18nUtils.getString(ControlI18n.SET_DEPLOY_STATUS_SUCCESS));
    }

    /**
     * 根据设施id删除主控
     *
     * @param deviceIdList 设施id集合
     */
    @PostMapping("/deleteControlByDeviceIds")
    public Result deleteControlByDeviceIds(@RequestBody List<String> deviceIdList) {
        if (deviceIdList == null || deviceIdList.size() == 0) {
            throw new FiLinkControlException("deviceId is null>>>>>>>>>");
        }
        controlService.deleteControlByDeviceIds(deviceIdList);
        return ResultUtils.success();
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
        setConfigBean.checkParams();
        return controlService.setConfig(setConfigBean);
    }

    /**
     * 更新主控信息
     *
     * @param controlParam 主控信息
     * @return 结果
     */
    @PostMapping("/feign/updateControlParam")
    public Result updateControlParam(@RequestBody ControlParam controlParam) {
        controlParam.checkUpdateParams();
        controlService.updateControlParam(controlParam, LogConstants.OPT_TYPE_WEB);
        return ResultUtils.success();
    }

    /**
     * 根据主控id删除电子锁主控
     *
     * @param controlReq
     * @return 操作结果
     */
    @PostMapping("/delete")
    public Result deleteLockAndControlByControlId(@RequestBody ControlReq controlReq) {
        //参数校验
        controlReq.checkControlIdIsNull();
        controlService.deleteLockAndControlByControlId(controlReq);
        return ResultUtils.success();
    }
}
