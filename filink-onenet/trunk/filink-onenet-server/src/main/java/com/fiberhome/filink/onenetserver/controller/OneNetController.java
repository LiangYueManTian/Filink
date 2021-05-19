package com.fiberhome.filink.onenetserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.onenetserver.bean.device.CreateDevice;
import com.fiberhome.filink.onenetserver.bean.device.DeleteDevice;
import com.fiberhome.filink.onenetserver.bean.device.HostBean;
import com.fiberhome.filink.onenetserver.constant.OneNetI18n;
import com.fiberhome.filink.onenetserver.constant.OneNetResultCode;
import com.fiberhome.filink.onenetserver.service.OneNetService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *   oneNet平台Lwm2m协议API控制层
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-25
 */
@RestController
@RequestMapping("/oneNet")
@Slf4j
public class OneNetController {
    /**
     * 自动注入OneNetService
     */
    @Autowired
    private OneNetService oneNetService;

    /**
     * 更新平台域名
     *
     * @param hostBean 平台域名
     * @return 操作结果
     */
    @PostMapping("/updateOneNetHost")
    public boolean updateOneNetHost(@RequestBody HostBean hostBean) {
        if (hostBean == null || StringUtils.isEmpty(hostBean.getHost())) {
            log.warn("[update http config]: http config is null");
            return false;
        }
        return oneNetService.updateOneNetHost(hostBean);
    }
    /**
     * 新增设备
     * @param createDevice 设备信息 title，imei，imsi，productId，accessKey为必填信息
     * @return 设备信息
     */
    @PostMapping("/createDevice")
    public Result createDevice(@RequestBody CreateDevice createDevice) {
        if (ObjectUtils.isEmpty(createDevice) || createDevice.check()) {
            log.warn("[addControl]: CreateDevice is null");
            return ResultUtils.warn(OneNetResultCode.PARAMETER_ERROR,
                    I18nUtils.getSystemString(OneNetI18n.PARAMETER_ERROR));
        }
        return oneNetService.createDevice(createDevice);
    }
    /**
     * 删除设备
     * @param deleteDevice 设备 deviceId，productId，accessKey必填
     * @return true 成功 false失败
     */
    @PostMapping("/deleteDevice")
    public Result deleteDevice(@RequestBody DeleteDevice deleteDevice) {
        if (ObjectUtils.isEmpty(deleteDevice) || deleteDevice.check()) {
            log.warn("[deleteControl]： DeleteDevice is null");
            return ResultUtils.warn(OneNetResultCode.PARAMETER_ERROR,
                    I18nUtils.getSystemString(OneNetI18n.PARAMETER_ERROR));
        }
        return oneNetService.deleteDevice(deleteDevice);
    }
    /**
     * 新增协议
     *
     * @param protocolDto 协议传输类
     * @return 操作结果
     */
    @PostMapping("/addProtocol")
    public boolean addProtocol(@RequestBody ProtocolDto protocolDto) {
        //参数校验
        if (protocolDto == null || StringUtils.isEmpty(protocolDto.getFileHexData())) {
            log.warn("addProtocol file is null");
            return false;
        }
        return oneNetService.addProtocol(protocolDto);
    }

    /**
     * 修改协议
     *
     * @param protocolDto 协议传输类
     * @return 操作结果
     */
    @PostMapping("/updateProtocol")
    public boolean updateProtocol(@RequestBody ProtocolDto protocolDto) {
        //参数校验
        if (StringUtils.isEmpty(protocolDto.getFileHexData())
                || StringUtils.isEmpty(protocolDto.getHardwareVersion())
                || StringUtils.isEmpty(protocolDto.getSoftwareVersion())) {
            log.warn("updateProtocol file or softwareVersion or hardwareVersion is null");
            return false;
        }
        return oneNetService.updateProtocol(protocolDto);
    }

    /**
     * 删除协议
     *
     * @param protocolDtoList 协议传输类
     * @return 操作结果
     */
    @PostMapping("/deleteProtocol")
    public boolean deleteProtocol(@RequestBody List<ProtocolDto> protocolDtoList) {
        //参数校验
        if (protocolDtoList == null || protocolDtoList.size() == 0) {
            log.warn("deleteProtocol list is null>>>>>>>");
            return false;
        }
        for (ProtocolDto protocolDto : protocolDtoList) {
            if (protocolDto == null
                    || StringUtils.isEmpty(protocolDto.getSoftwareVersion())
                    || StringUtils.isEmpty(protocolDto.getHardwareVersion())) {
                log.warn("softwareVersion or hardwareVersion is null");
                return false;
            }
        }
        return oneNetService.deleteProtocol(protocolDtoList);
    }
}
