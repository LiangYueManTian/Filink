package com.fiberhome.filink.dprotocol.controller;


import com.fiberhome.filink.bean.CheckInputString;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocol;
import com.fiberhome.filink.dprotocol.constant.DeviceProtocolI18n;
import com.fiberhome.filink.dprotocol.constant.DeviceProtocolResultCode;
import com.fiberhome.filink.dprotocol.dto.ProtocolVersionBean;
import com.fiberhome.filink.dprotocol.exception.FilinkDeviceProtocolParamException;
import com.fiberhome.filink.dprotocol.service.DeviceProtocolService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *     设施协议前端控制器
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-01-12
 */
@RestController
@RequestMapping("/deviceProtocol")
public class DeviceProtocolController {

    /**
     * 自动注入设施协议service
     */
    @Autowired
    private DeviceProtocolService deviceProtocolService;

    /**
     * 校验设施协议名称是否重复
     * @param deviceProtocol 设施协议
     * @return 是否重复
     */
    @PostMapping("/checkDeviceProtocolNameRepeat")
    public Result checkDeviceProtocolNameRepeat(@RequestBody DeviceProtocol deviceProtocol) {
        if (ObjectUtils.isEmpty(deviceProtocol) || deviceProtocol.checkName()) {
            return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR,
                    I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR));
        }
        return deviceProtocolService.checkDeviceProtocolNameRepeat(deviceProtocol);
    }
    /**
     * 新增设施协议
     *
     * @param protocolName 设施协议名称
     * @param file         设施协议文件
     * @return 结果
     */
    @PostMapping("/addDeviceProtocol")
    public Result addDeviceProtocol(@RequestParam String protocolName, @RequestBody MultipartFile file){
        protocolName = CheckInputString.nameCheck(protocolName);
        if (StringUtils.isEmpty(protocolName) || file == null) {
            return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR,
                    I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR));
        }
        return deviceProtocolService.addDeviceProtocol(protocolName, file);
    }

    /**
     * 查询上传设施协议文件限制
     * @return 结果
     */
    @GetMapping("/queryFileLimit")
    public Result queryFileLimit() {
        return deviceProtocolService.queryFileLimit();
    }

    /**
     * 修改设施协议
     *
     *@param file    设施协议文件
     * @param deviceProtocol 设施协议
     * @return 结果
     */
    @PostMapping("/updateDeviceProtocol")
    public Result updateDeviceProtocol(DeviceProtocol deviceProtocol, @RequestBody MultipartFile file) {
        if (ObjectUtils.isEmpty(deviceProtocol) || file == null || deviceProtocol.checkIdAndName()) {
            return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR,
                    I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR));
        }
        return deviceProtocolService.updateDeviceProtocol(deviceProtocol, file);
    }

    /**
     * 修改设施协议名称
     *
     * @param deviceProtocol 设施协议
     * @return 结果
     */
    @PostMapping("/updateProtocolName")
    public Result updateProtocolName(@RequestBody DeviceProtocol deviceProtocol) {
        if (ObjectUtils.isEmpty(deviceProtocol) || deviceProtocol.checkIdAndName()) {
            return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR,
                    I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR));
        }
        return deviceProtocolService.updateDeviceProtocol(deviceProtocol);
    }

    /**
     * 删除设施协议
     *
     * @param protocolIds 设施协议ID List
     * @return Integer
     */
    @PostMapping("/deleteDeviceProtocol")
    public Result deleteDeviceProtocol(@RequestBody List<String> protocolIds) {
        if (protocolIds == null || protocolIds.size() == 0) {
            return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR,
                    I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR));
        }
        return deviceProtocolService.deleteDeviceProtocol(protocolIds);
    }

    /**
     * 查询设施协议列表
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    @PostMapping("/queryDeviceProtocolList")
    public Result queryDeviceProtocolList(@RequestBody QueryCondition<DeviceProtocol> queryCondition) {
        if (ObjectUtils.isEmpty(queryCondition) || queryCondition.getFilterConditions() == null
                || queryCondition.getPageCondition() == null || queryCondition.getPageCondition().getPageNum() == null
                || queryCondition.getPageCondition().getPageSize() == null) {
            return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR,
                    I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR));
        }
        return deviceProtocolService.queryDeviceProtocolList(queryCondition);
    }

    /**
     * 根据软硬件版本获取文件16进制字符串
     * @param protocolVersionBean 设施协议
     * @return 文件16进制字符串
     */
    @PostMapping("/queryProtocol")
    public String queryProtocol(@RequestBody ProtocolVersionBean protocolVersionBean) {
        if (ObjectUtils.isEmpty(protocolVersionBean) || StringUtils.isEmpty(protocolVersionBean.getHardwareVersion())
                || StringUtils.isEmpty(protocolVersionBean.getSoftwareVersion())) {
            throw new FilinkDeviceProtocolParamException();
        }
        //转换数据
        DeviceProtocol deviceProtocol = new DeviceProtocol();
        deviceProtocol.setHardwareVersion(protocolVersionBean.getHardwareVersion());
        deviceProtocol.setSoftwareVersion(protocolVersionBean.getSoftwareVersion());
        if (deviceProtocol.checkVersion()) {
            throw new FilinkDeviceProtocolParamException();
        }
        return deviceProtocolService.queryProtocol(deviceProtocol);
    }

}
