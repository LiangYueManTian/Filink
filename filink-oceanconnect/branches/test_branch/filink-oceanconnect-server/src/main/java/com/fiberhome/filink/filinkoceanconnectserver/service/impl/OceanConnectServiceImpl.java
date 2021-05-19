package com.fiberhome.filink.filinkoceanconnectserver.service.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.commonstation.constant.CmdType;
import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import com.fiberhome.filink.commonstation.entity.protocol.FiLinkReqParamsDto;
import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.commonstation.entity.xmlbean.FiLinkProtocolBean;
import com.fiberhome.filink.commonstation.exception.RequestException;
import com.fiberhome.filink.commonstation.sender.AbstractInstructSender;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.commonstation.utils.JsonUtil;
import com.fiberhome.filink.filinkoceanconnectserver.business.PerformanceTest;
import com.fiberhome.filink.filinkoceanconnectserver.client.DeviceClient;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.filinkoceanconnectserver.entity.https.HttpsConfig;
import com.fiberhome.filink.filinkoceanconnectserver.entity.platform.*;
import com.fiberhome.filink.filinkoceanconnectserver.entity.profile.Platform;
import com.fiberhome.filink.filinkoceanconnectserver.entity.profile.ServiceBean;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkReqOceanConnectParams;
import com.fiberhome.filink.filinkoceanconnectserver.exception.OceanException;
import com.fiberhome.filink.filinkoceanconnectserver.service.OceanConnectService;
import com.fiberhome.filink.filinkoceanconnectserver.stream.FiLinkKafkaSender;
import com.fiberhome.filink.filinkoceanconnectserver.utils.*;
import com.fiberhome.filink.platformappapi.bean.PlatformAppInfo;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.log4j.Log4j;
import org.apache.commons.net.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 中转服务实现类
 *
 * @author CongcaiYu
 */
@Log4j
@Service
public class OceanConnectServiceImpl implements OceanConnectService {

    @Autowired
    private Map<String, AbstractInstructSender> instructSenderMap;

    @Autowired
    private ProtocolUtil protocolUtil;

    @Autowired
    private AppInfoUtil appInfoUtil;

    @Autowired
    private DeviceClient deviceClient;

    @Autowired
    private FiLinkKafkaSender kafkaSender;

    @Autowired
    private ProfileUtil profileUtil;


    /**
     * 成功状态码
     */
    private final int OK = 200;

    /**
     * 发送指令
     *
     * @param fiLinkReqParamsDto FiLinkReqParamsDto
     */
    @Override
    public void sendCmd(FiLinkReqParamsDto fiLinkReqParamsDto) {
        //构造请求帧参数
        FiLinkReqOceanConnectParams fiLinkReqOceanConnectParams = new FiLinkReqOceanConnectParams();
        //请求类型
        fiLinkReqOceanConnectParams.setCmdType(CmdType.REQUEST_TYPE);
        //指令id
        fiLinkReqOceanConnectParams.setCmdId(fiLinkReqParamsDto.getCmdId());
        //请求参数
        fiLinkReqOceanConnectParams.setParams(fiLinkReqParamsDto.getParams());
        //主控id
        fiLinkReqOceanConnectParams.setEquipmentId(fiLinkReqParamsDto.getEquipmentId());
        //软件版本
        fiLinkReqOceanConnectParams.setSoftwareVersion(fiLinkReqParamsDto.getSoftwareVersion());
        //硬件版本
        fiLinkReqOceanConnectParams.setHardwareVersion(fiLinkReqParamsDto.getHardwareVersion());
        //产品id
        fiLinkReqOceanConnectParams.setAppId(fiLinkReqParamsDto.getAppId());
        //token
        fiLinkReqOceanConnectParams.setToken(fiLinkReqParamsDto.getToken());
        //平台id
        fiLinkReqOceanConnectParams.setOceanConnectId(fiLinkReqParamsDto.getPlateFormId());
        //手机id
        fiLinkReqOceanConnectParams.setPhoneId(fiLinkReqParamsDto.getPhoneId());
        //appKey
        fiLinkReqOceanConnectParams.setAppKey(fiLinkReqParamsDto.getAppKey());
        //获取协议信息
        FiLinkProtocolBean protocolBean = protocolUtil.getProtocolBean(fiLinkReqOceanConnectParams);
        fiLinkReqOceanConnectParams.setProtocolBean(protocolBean);
        //获取发送指令类
        String instructSenderName = protocolBean.getInstructSenderName();
        AbstractInstructSender instructSender = instructSenderMap.get(instructSenderName);
        if (instructSender == null) {
            throw new RequestException("no such instructSender : " + instructSenderName);
        }
        instructSender.sendInstruct(fiLinkReqOceanConnectParams);
    }

    /**
     * 新增协议
     *
     * @param protocolDto 协议传输对象
     * @return 操作结果
     */
    @Override
    public boolean addProtocol(ProtocolDto protocolDto) {
        //解析文件保存到redis
        protocolUtil.setProtocolToRedis(protocolDto.getFileHexData());
        return true;
    }

    /**
     * 更新协议
     *
     * @param protocolDto 协议传输对象
     * @return 操作结果
     */
    @Override
    public boolean updateProtocol(ProtocolDto protocolDto) {
        //先清除老版本协议信息
        protocolUtil.deleteProtocolToRedis(protocolDto);
        //将新协议文件解析存入redis
        protocolUtil.setProtocolToRedis(protocolDto.getFileHexData());
        return true;
    }

    /**
     * 删除协议
     *
     * @param protocolDtoList 协议传输对象
     * @return 操作结果
     */
    @Override
    public boolean deleteProtocol(List<ProtocolDto> protocolDtoList) {
        //删除协议信息
        for (ProtocolDto protocolDto : protocolDtoList) {
            protocolUtil.deleteProtocolToRedis(protocolDto);
        }
        return true;
    }


    /**
     * 注册设施
     *
     * @param oceanDevice 设施实体
     */
    @Override
    public Result registry(OceanDevice oceanDevice) {
        //根据appId查询profile厂商信息
        PlatformAppInfo appInfo = appInfoUtil.getAppInfo(oceanDevice.getAppId());
        if (appInfo == null) {
            log.error("app info is null>>>>");
            return ResultUtils.warn(OceanConnectResultCode.REGISTRY_FAILED,
                    I18nUtils.getSystemString(OceanConnectI18n.REGISTRY_FAILED));
        }
        //设置厂商信息
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setManufacturerId(appInfo.getManufacturerId());
        deviceInfo.setModel(appInfo.getModel());
        deviceInfo.setDeviceType(appInfo.getDeviceType());
        deviceInfo.setProtocolType(appInfo.getProtocolType());
        deviceInfo.setManufacturerName(appInfo.getManufacturerName());
        oceanDevice.setDeviceInfo(deviceInfo);
        //设置超时时间
        oceanDevice.setTimeout("0");
        StreamClosedHttpResponse response = deviceClient.registryDevice(oceanDevice);
        //响应为空或者状态不为200则注册失败
        if (response == null || OK != response.getStatusLine().getStatusCode()) {
            return ResultUtils.warn(OceanConnectResultCode.REGISTRY_FAILED,
                    I18nUtils.getSystemString(OceanConnectI18n.REGISTRY_FAILED));
        } else {
            //响应成功,解析响应消息
            RegistryResult registryResult = JsonUtil.jsonString2SimpleObj(response.getContent(), RegistryResult.class);
            return ResultUtils.success(OceanConnectResultCode.SUCCESS,
                    I18nUtils.getSystemString(OceanConnectI18n.REGISTRY_SUCCESS), registryResult);
        }
    }


    /**
     * 接收订阅消息
     *
     * @param receiveBean 接收实体
     */
    @Override
    public void receiveMsg(ReceiveBean receiveBean) {
        try {
            String data = getData(receiveBean);
            //todo 告警转工单测试
            if(data.contains("00002208")){
                PerformanceTest.setAlarmTime(data.substring(4, 24), "recvAlarmCmdTime", System.currentTimeMillis());
            }
            DeviceMsg deviceMsg = new DeviceMsg();
            log.info("receive plateForm msg : " + data);
            deviceMsg.setHexData(data);
            deviceMsg.setOceanConnectId(receiveBean.getDeviceId());
            kafkaSender.sendMsg(deviceMsg);
        } catch (Exception e) {
            log.error("receive data base64 decode error>>>>");
        }
    }


    private String getData(ReceiveBean receiveBean) {
        //获取平台的service
        DeviceServiceData service = receiveBean.getService();
        String serviceId = service.getServiceId();
        Object dataObj = service.getData();
        Map dataMap = (Map) dataObj;
        //获取配置profile对象
        Platform platform = profileUtil.getProfileConfig();
        ServiceBean serviceBean = platform.getUploadMap().get(serviceId);
        String data = dataMap.get(serviceBean.getData()).toString();
        if(StringUtils.isEmpty(data)){
            log.error("data is null>>>>>>>>>>>>");
            throw new OceanException("data is null");
        }
        //判断是否需要进行base64解密
        if(serviceBean.isBase64()){
            //将平台消息进行base64解密
            byte[] dataBytes = Base64.decodeBase64(data);
            data = HexUtil.bytesToHexString(dataBytes);
        }
        return data;
    }


    /**
     * 删除设施
     *
     * @param oceanDevice 设施实体
     * @return 返回结果
     */
    @Override
    public Result deleteDevice(OceanDevice oceanDevice) {
        StreamClosedHttpResponse response = deviceClient.deleteDeviceById(oceanDevice);
        //响应为空或者状态不为200则注册失败
        if (response == null || OK != response.getStatusLine().getStatusCode()) {
            return ResultUtils.warn(OceanConnectResultCode.DELETE_FAILED,
                    I18nUtils.getSystemString(OceanConnectI18n.DELETE_FAILED));
        } else {
            return ResultUtils.success(OceanConnectResultCode.SUCCESS,
                    I18nUtils.getSystemString(OceanConnectI18n.DELETE_SUCCESS));
        }
    }


    /**
     * 更新平台地址
     *
     * @param httpsConfig https配置对象
     * @return 操作结果
     */
    @Override
    public boolean updateHttpsConfig(HttpsConfig httpsConfig) {
        try {
            String address = httpsConfig.getAddress();
            RedisUtils.set(RedisKey.ADDRESS_KEY, address);
        } catch (Exception e) {
            log.error("set https config to redis failed>>>>>>>");
            return false;
        }
        return true;
    }

}
