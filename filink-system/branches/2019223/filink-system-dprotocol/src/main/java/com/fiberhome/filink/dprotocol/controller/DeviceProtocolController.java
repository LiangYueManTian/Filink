package com.fiberhome.filink.dprotocol.controller;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocol;
import com.fiberhome.filink.dprotocol.bean.DeviceProtocolI18n;
import com.fiberhome.filink.dprotocol.bean.ProtocolVersionBean;
import com.fiberhome.filink.dprotocol.bean.xmlBean.FiLinkProtocolBean;
import com.fiberhome.filink.dprotocol.exception.FilinkDeviceProtocolParamException;
import com.fiberhome.filink.dprotocol.service.DeviceProtocolService;
import com.fiberhome.filink.dprotocol.utils.DeviceProtocolResultCode;
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
     * 锁
     */
    private static final String LOCK = "lock";

    /**
     * 新增设施协议
     *
     * @param protocolName 设施协议名称
     * @param file         设施协议文件
     * @return 结果
     */
    @PostMapping("/addDeviceProtocol")
    public Result addDeviceProtocol(@RequestParam String protocolName, @RequestBody MultipartFile file){
        if (StringUtils.isEmpty(protocolName) || file == null) {
            return ResultUtils.warn(DeviceProtocolResultCode.DEVICE_PROTOCOL_PARAM_ERROR,
                    I18nUtils.getString(DeviceProtocolI18n.DEVICE_PROTOCOL_PARAM_ERROR));
        }
        synchronized (LOCK) {
            return deviceProtocolService.addDeviceProtocol(protocolName, file);
        }
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
     *
     * @return 查询结果
     */
    @GetMapping("/queryDeviceProtocolList")
    public Result queryDeviceProtocolList() {
        return deviceProtocolService.queryDeviceProtocolList();
    }

    /**
     * 查询缓存设施协议文件信息
     * @param protocolVersionBean 设施协议文件信息缓存
     * @return 设施协议文件信息
     */
    @PostMapping("/queryProtocolXmlBean")
    public FiLinkProtocolBean queryProtocolXmlBean(@RequestBody ProtocolVersionBean protocolVersionBean){
        if (ObjectUtils.isEmpty(protocolVersionBean) || protocolVersionBean.check()) {
            throw new FilinkDeviceProtocolParamException();
        }
        return deviceProtocolService.queryProtocolXmlBean(protocolVersionBean);
    }
}
