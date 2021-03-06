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
 * ?????????????????????
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
     * ???????????????
     */
    private final int OK = 200;

    /**
     * ????????????
     *
     * @param fiLinkReqParamsDto FiLinkReqParamsDto
     */
    @Override
    public void sendCmd(FiLinkReqParamsDto fiLinkReqParamsDto) {
        //?????????????????????
        FiLinkReqOceanConnectParams fiLinkReqOceanConnectParams = new FiLinkReqOceanConnectParams();
        //????????????
        fiLinkReqOceanConnectParams.setCmdType(CmdType.REQUEST_TYPE);
        //??????id
        fiLinkReqOceanConnectParams.setCmdId(fiLinkReqParamsDto.getCmdId());
        //????????????
        fiLinkReqOceanConnectParams.setParams(fiLinkReqParamsDto.getParams());
        //??????id
        fiLinkReqOceanConnectParams.setEquipmentId(fiLinkReqParamsDto.getEquipmentId());
        //????????????
        fiLinkReqOceanConnectParams.setSoftwareVersion(fiLinkReqParamsDto.getSoftwareVersion());
        //????????????
        fiLinkReqOceanConnectParams.setHardwareVersion(fiLinkReqParamsDto.getHardwareVersion());
        //??????id
        fiLinkReqOceanConnectParams.setAppId(fiLinkReqParamsDto.getAppId());
        //token
        fiLinkReqOceanConnectParams.setToken(fiLinkReqParamsDto.getToken());
        //??????id
        fiLinkReqOceanConnectParams.setOceanConnectId(fiLinkReqParamsDto.getPlateFormId());
        //??????id
        fiLinkReqOceanConnectParams.setPhoneId(fiLinkReqParamsDto.getPhoneId());
        //appKey
        fiLinkReqOceanConnectParams.setAppKey(fiLinkReqParamsDto.getAppKey());
        //??????????????????
        FiLinkProtocolBean protocolBean = protocolUtil.getProtocolBean(fiLinkReqOceanConnectParams);
        fiLinkReqOceanConnectParams.setProtocolBean(protocolBean);
        //?????????????????????
        String instructSenderName = protocolBean.getInstructSenderName();
        AbstractInstructSender instructSender = instructSenderMap.get(instructSenderName);
        if (instructSender == null) {
            throw new RequestException("no such instructSender : " + instructSenderName);
        }
        instructSender.sendInstruct(fiLinkReqOceanConnectParams);
    }

    /**
     * ????????????
     *
     * @param protocolDto ??????????????????
     * @return ????????????
     */
    @Override
    public boolean addProtocol(ProtocolDto protocolDto) {
        //?????????????????????redis
        protocolUtil.setProtocolToRedis(protocolDto.getFileHexData());
        return true;
    }

    /**
     * ????????????
     *
     * @param protocolDto ??????????????????
     * @return ????????????
     */
    @Override
    public boolean updateProtocol(ProtocolDto protocolDto) {
        //??????????????????????????????
        protocolUtil.deleteProtocolToRedis(protocolDto);
        //??????????????????????????????redis
        protocolUtil.setProtocolToRedis(protocolDto.getFileHexData());
        return true;
    }

    /**
     * ????????????
     *
     * @param protocolDtoList ??????????????????
     * @return ????????????
     */
    @Override
    public boolean deleteProtocol(List<ProtocolDto> protocolDtoList) {
        //??????????????????
        for (ProtocolDto protocolDto : protocolDtoList) {
            protocolUtil.deleteProtocolToRedis(protocolDto);
        }
        return true;
    }


    /**
     * ????????????
     *
     * @param oceanDevice ????????????
     */
    @Override
    public Result registry(OceanDevice oceanDevice) {
        //??????appId??????profile????????????
        PlatformAppInfo appInfo = appInfoUtil.getAppInfo(oceanDevice.getAppId());
        if (appInfo == null) {
            log.error("app info is null>>>>");
            return ResultUtils.warn(OceanConnectResultCode.REGISTRY_FAILED,
                    I18nUtils.getSystemString(OceanConnectI18n.REGISTRY_FAILED));
        }
        //??????????????????
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setManufacturerId(appInfo.getManufacturerId());
        deviceInfo.setModel(appInfo.getModel());
        deviceInfo.setDeviceType(appInfo.getDeviceType());
        deviceInfo.setProtocolType(appInfo.getProtocolType());
        deviceInfo.setManufacturerName(appInfo.getManufacturerName());
        oceanDevice.setDeviceInfo(deviceInfo);
        //??????????????????
        oceanDevice.setTimeout("0");
        StreamClosedHttpResponse response = deviceClient.registryDevice(oceanDevice);
        //??????????????????????????????200???????????????
        if (response == null || OK != response.getStatusLine().getStatusCode()) {
            return ResultUtils.warn(OceanConnectResultCode.REGISTRY_FAILED,
                    I18nUtils.getSystemString(OceanConnectI18n.REGISTRY_FAILED));
        } else {
            //????????????,??????????????????
            RegistryResult registryResult = JsonUtil.jsonString2SimpleObj(response.getContent(), RegistryResult.class);
            return ResultUtils.success(OceanConnectResultCode.SUCCESS,
                    I18nUtils.getSystemString(OceanConnectI18n.REGISTRY_SUCCESS), registryResult);
        }
    }


    /**
     * ??????????????????
     *
     * @param receiveBean ????????????
     */
    @Override
    public void receiveMsg(ReceiveBean receiveBean) {
        try {
            String data = getData(receiveBean);
            //todo ?????????????????????
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
        //???????????????service
        DeviceServiceData service = receiveBean.getService();
        String serviceId = service.getServiceId();
        Object dataObj = service.getData();
        Map dataMap = (Map) dataObj;
        //????????????profile??????
        Platform platform = profileUtil.getProfileConfig();
        ServiceBean serviceBean = platform.getUploadMap().get(serviceId);
        String data = dataMap.get(serviceBean.getData()).toString();
        if(StringUtils.isEmpty(data)){
            log.error("data is null>>>>>>>>>>>>");
            throw new OceanException("data is null");
        }
        //????????????????????????base64??????
        if(serviceBean.isBase64()){
            //?????????????????????base64??????
            byte[] dataBytes = Base64.decodeBase64(data);
            data = HexUtil.bytesToHexString(dataBytes);
        }
        return data;
    }


    /**
     * ????????????
     *
     * @param oceanDevice ????????????
     * @return ????????????
     */
    @Override
    public Result deleteDevice(OceanDevice oceanDevice) {
        StreamClosedHttpResponse response = deviceClient.deleteDeviceById(oceanDevice);
        //??????????????????????????????200???????????????
        if (response == null || OK != response.getStatusLine().getStatusCode()) {
            return ResultUtils.warn(OceanConnectResultCode.DELETE_FAILED,
                    I18nUtils.getSystemString(OceanConnectI18n.DELETE_FAILED));
        } else {
            return ResultUtils.success(OceanConnectResultCode.SUCCESS,
                    I18nUtils.getSystemString(OceanConnectI18n.DELETE_SUCCESS));
        }
    }


    /**
     * ??????????????????
     *
     * @param httpsConfig https????????????
     * @return ????????????
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
