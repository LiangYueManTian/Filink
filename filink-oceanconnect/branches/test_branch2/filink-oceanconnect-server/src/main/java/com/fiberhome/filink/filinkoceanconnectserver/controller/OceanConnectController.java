package com.fiberhome.filink.filinkoceanconnectserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.filinkoceanconnectserver.entity.https.HttpsConfig;
import com.fiberhome.filink.filinkoceanconnectserver.entity.platform.DeviceServiceData;
import com.fiberhome.filink.filinkoceanconnectserver.entity.platform.OceanDevice;
import com.fiberhome.filink.filinkoceanconnectserver.entity.platform.ReceiveBean;
import com.fiberhome.filink.filinkoceanconnectserver.service.OceanConnectService;
import com.fiberhome.filink.filinkoceanconnectserver.utils.OceanConnectI18n;
import com.fiberhome.filink.filinkoceanconnectserver.utils.OceanConnectResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * oceanConnect控制器
 *
 * @author CongcaiYu
 */
@Slf4j
@RestController
@RequestMapping("/oceanConnect")
public class OceanConnectController {


    @Autowired
    private OceanConnectService oceanConnectService;

    @GetMapping("/resetRecvCount")
    public Object resetRecvCount(){
        PerformanceTest.atomicInteger = new AtomicInteger(0);
        return "reset success";
    }

    @GetMapping("/getRecvCount")
    public Object getRecvCount(){
        return PerformanceTest.atomicInteger.get();
    }

    /**
     * todo 告警数量
     * @return
     */
    @GetMapping("/getAlarmCount")
    public Object getAlarmCount(){
        return "alarm : "+PerformanceTest.alarmCountMap+" , count : "+PerformanceTest.alarmCountMap.size();
    }

    /**
     * todo 告警数量
     * @return
     */
    @GetMapping("/resetAlarmCount")
    public Object resetAlarmCount(){
        PerformanceTest.alarmCountMap = new HashMap<>();
        return "success";
    }

    /**
     * todo 告警测试代码
     * @return
     */
    @GetMapping("/getAlarmTime")
    public Object getAlarmTime(){
        return PerformanceTest.alarmTimeMap;
    }

    /**
     * todo 告警测试代码
     * @return
     */
    @GetMapping("/resetAlarmTime")
    public String resetAlarmTime(){
        PerformanceTest.alarmTimeMap = new HashMap<>();
        return "reset success";
    }

    /**
     * todo 升级测试代码
     * @return
     * @throws Exception
     */
    @GetMapping("/getUpgradeTime")
    public String getUpgradeTime() throws Exception{
        return new ObjectMapper().writeValueAsString(PerformanceTest.upgradeTimeMap);
    }

    /**
     * todo 图片测试代码
     * @return
     * @throws Exception
     */
    @GetMapping("/getPicTime")
    public String getPicTime() throws Exception{
        return new ObjectMapper().writeValueAsString(PerformanceTest.picTimeMap);
    }

    /**
     * todo 图片测试代码
     * @return
     * @throws Exception
     */
    @GetMapping("/getLockTime")
    public String getLockTime() throws Exception{
        return new ObjectMapper().writeValueAsString(PerformanceTest.lockTimeMap);
    }

    /**
     * todo 升级测试
     * @return
     */
    @GetMapping("/resetUpgradeTime")
    public String resetUpgrade(){
        PerformanceTest.upgradeTimeMap = new HashMap<>(64);
        return "reset success";
    }

    /**
     * todo 图片测试
     * @return
     */
    @GetMapping("/resetPicTime")
    public String resetPic(){
        PerformanceTest.picTimeMap = new HashMap<>(64);
        return "reset success";
    }

    /**
     * todo 开锁测试
     * @return
     */
    @GetMapping("/resetLockTime")
    public String resetLock(){
        PerformanceTest.lockTimeMap = new HashMap<>(64);
        return "reset success";
    }


    /**
     * 注册设备
     *
     * @param oceanDevice 设施实体
     * @return 操作结果
     */
    @PostMapping("/registry")
    public Result registryDevice(@RequestBody OceanDevice oceanDevice) {
        log.info("registry device>>>>>>>>>");
        if (oceanDevice == null || StringUtils.isEmpty(oceanDevice.getAppId())) {
            return ResultUtils.warn(OceanConnectResultCode.PARAMETER_ERROR,
                    I18nUtils.getSystemString(OceanConnectI18n.PARAMETER_ERROR));
        }
        return oceanConnectService.registry(oceanDevice);
    }


    /**
     * 删除设施
     *
     * @param oceanDevice 设施信息
     * @return 操作结果
     */
    @PostMapping("/delete")
    public Result deleteDevice(@RequestBody OceanDevice oceanDevice) {
        log.info("delete device>>>>>>>>>>");
        if (oceanDevice == null
                || StringUtils.isEmpty(oceanDevice.getDeviceId())
                || StringUtils.isEmpty(oceanDevice.getAppId())) {
            return ResultUtils.warn(OceanConnectResultCode.PARAMETER_ERROR,
                    I18nUtils.getSystemString(OceanConnectI18n.PARAMETER_ERROR));
        }
        return oceanConnectService.deleteDevice(oceanDevice);
    }


    /**
     * 接收oceanConnect消息通知
     *
     * @param receiveBean 消息实体
     */
    @PostMapping("/receive")
    public void receiveOceanMsg(@RequestBody ReceiveBean receiveBean) {
        //参数校验
        if (receiveBean == null || StringUtils.isEmpty(receiveBean.getDeviceId())
                || StringUtils.isEmpty(receiveBean.getNotifyType())
                || StringUtils.isEmpty(receiveBean.getGatewayId())
                || StringUtils.isEmpty(receiveBean.getService())) {
            log.error("receiveBean is null>>>>>>>>>>>");
            return;
        }
        DeviceServiceData service = receiveBean.getService();
        if (StringUtils.isEmpty(service.getServiceId())
                || StringUtils.isEmpty(service.getServiceType())
                || StringUtils.isEmpty(service.getData())
                || StringUtils.isEmpty(service.getEventTime())) {
            log.error("device service data is null>>>>>>>>>>>");
            return;
        }
        log.info("receive message from oceanConnect: " + JSONObject.toJSONString(receiveBean));
        oceanConnectService.receiveMsg(receiveBean);
    }

    /**
     * 新增协议
     *
     * @param protocolDto 协议传输类
     * @return 操作结果
     */
    @PostMapping("/addProtocol")
    public boolean addProtocol(@RequestBody ProtocolDto protocolDto) {
        log.info("add ocean connect protocol>>>>>>>>>>>>>>>.");
        //参数校验
        if (protocolDto == null || StringUtils.isEmpty(protocolDto.getFileHexData())) {
            log.info("file is null>>>>>>>");
            return false;
        }
        return oceanConnectService.addProtocol(protocolDto);
    }

    /**
     * 修改协议
     *
     * @param protocolDto 协议传输类
     * @return 操作结果
     */
    @PostMapping("/updateProtocol")
    public boolean updateProtocol(@RequestBody ProtocolDto protocolDto) {
        log.info("upadate ocean connect protocol>>>>>>>>>>");
        //参数校验
        if (StringUtils.isEmpty(protocolDto.getFileHexData())
                || StringUtils.isEmpty(protocolDto.getHardwareVersion())
                || StringUtils.isEmpty(protocolDto.getSoftwareVersion())) {
            log.info("file or softwareVersion or hardwareVersion is null>>>>>>");
            return false;
        }
        return oceanConnectService.updateProtocol(protocolDto);
    }

    /**
     * 删除协议
     *
     * @param protocolDtoList 协议传输类
     * @return 操作结果
     */
    @PostMapping("/deleteProtocol")
    public boolean deleteProtocol(@RequestBody List<ProtocolDto> protocolDtoList) {
        log.info("delete ocean connect protocol>>>>>>>>>>");
        //参数校验
        if (protocolDtoList == null || protocolDtoList.size() == 0) {
            log.info("protocol list is null>>>>>>>");
            return false;
        }
        for (ProtocolDto protocolDto : protocolDtoList) {
            if (protocolDto == null
                    || StringUtils.isEmpty(protocolDto.getSoftwareVersion())
                    || StringUtils.isEmpty(protocolDto.getHardwareVersion())) {
                log.info("softwareVersion or hardwareVersion is null>>>>>>>>>");
                return false;
            }
        }
        return oceanConnectService.deleteProtocol(protocolDtoList);
    }


    /**
     * 更新平台地址
     *
     * @param httpsConfig https配置对象
     * @return 操作结果
     */
    @PostMapping("/updateHttpsConfig")
    public boolean updateHttpsConfig(@RequestBody HttpsConfig httpsConfig) {
        log.info("update https config>>>>>>>>");
        if(httpsConfig == null
                || StringUtils.isEmpty(httpsConfig.getAddress())){
            log.error("https config is null>>>>>>>");
            return false;
        }
        return oceanConnectService.updateHttpsConfig(httpsConfig);
    }

}
