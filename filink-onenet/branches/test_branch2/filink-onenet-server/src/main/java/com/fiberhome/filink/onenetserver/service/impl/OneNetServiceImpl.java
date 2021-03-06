package com.fiberhome.filink.onenetserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.commonstation.constant.CmdId;
import com.fiberhome.filink.commonstation.constant.CmdType;
import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.protocol.FiLinkReqParamsDto;
import com.fiberhome.filink.commonstation.entity.protocol.ProtocolDto;
import com.fiberhome.filink.commonstation.sender.AbstractInstructSender;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.onenetserver.bean.device.*;
import com.fiberhome.filink.onenetserver.bean.protocol.FiLinkOneNetConnectParams;
import com.fiberhome.filink.onenetserver.constant.OneNetHttpConstants;
import com.fiberhome.filink.onenetserver.constant.OneNetI18n;
import com.fiberhome.filink.onenetserver.constant.OneNetResultCode;
import com.fiberhome.filink.onenetserver.constant.RedisKey;
import com.fiberhome.filink.onenetserver.exception.FilinkOneNetException;
import com.fiberhome.filink.onenetserver.operation.CreateDeviceOpe;
import com.fiberhome.filink.onenetserver.operation.DeleteDeviceOpe;
import com.fiberhome.filink.onenetserver.service.OneNetService;
import com.fiberhome.filink.onenetserver.utils.HostUtil;
import com.fiberhome.filink.onenetserver.utils.OneNetToken;
import com.fiberhome.filink.onenetserver.utils.ProductInfoUtil;
import com.fiberhome.filink.onenetserver.utils.ProtocolUtil;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *   oneNet??????Lwm2m??????API???????????????
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-25
 */
@Slf4j
@Service
public class OneNetServiceImpl implements OneNetService {
    /**
     * ????????????
     */
    @Autowired
    private AbstractInstructSender sender;
    @Autowired
    private ProtocolUtil protocolUtil;
    @Autowired
    private ProductInfoUtil productInfoUtil;
    @Autowired
    private HostUtil hostUtil;

    /**
     * ??????????????????
     *
     * @param hostBean ????????????
     * @return ????????????
     */
    @Override
    public boolean updateOneNetHost(HostBean hostBean) {
        try {
            RedisUtils.set(RedisKey.ADDRESS_KEY, hostBean.getHost());
        } catch (Exception e) {
            log.error("set http config to redis failed:", e);
            return false;
        }
        return true;
    }

    /**
     * ????????????
     * @param createDevice ???????????? title???imei???imsi???productId???accessKey???????????????
     * @return ????????????
     */
    @Override
    public Result createDevice(CreateDevice createDevice) {
        try {
            log.info("create device: {}, imei is :{}", createDevice.getTitle() , createDevice.getImei());
            //????????????ID??????HTTP????????????Token
            String token = getToken(createDevice.getProductId());
            CreateDeviceOpe deviceOpe = new CreateDeviceOpe(token);
            //??????oneNet??????
            createDevice.setHost(hostUtil.getOneNetHost());
            //??????oneNet???api
            OneNetResponse operation = deviceOpe.operation(createDevice, createDevice.toJsonObject());
            //???????????????
            if (!(operation.getErrorno() == 0 && OneNetHttpConstants.CREATE_SUCCESS.equals(operation.getError()))) {
                log.info("create device fallback: {}", operation.toString());
                throw new FilinkOneNetException("oneNet response: create device error");
            }
            //???????????????
            CreateResult createResult = JSON.parseObject(operation.getData().toString(), CreateResult.class);
            return ResultUtils.success(OneNetResultCode.SUCCESS,
                    I18nUtils.getSystemString(OneNetI18n.CREATE_SUCCESS), createResult);
        } catch (Exception e) {
            log.error("create device error", e);
        }
        return ResultUtils.warn(OneNetResultCode.CREATE_FAILED,
                I18nUtils.getSystemString(OneNetI18n.CREATE_FAILED));
    }

    /**
     * ????????????
     * @param deleteDevice ?????? deviceId???productId???accessKey??????
     * @return true ?????? false??????
     */
    @Override
    public Result deleteDevice(DeleteDevice deleteDevice) {
        try {
            log.info("delete device, deviceId: {}", deleteDevice.getDeviceId());
            //????????????ID??????HTTP????????????Token
            String token = getToken(deleteDevice.getProductId());
            DeleteDeviceOpe deleteDeviceOpe = new DeleteDeviceOpe(token);
            //??????oneNet??????
            deleteDevice.setHost(hostUtil.getOneNetHost());
            //??????oneNet???api
            OneNetResponse operation = deleteDeviceOpe.operation(deleteDevice, deleteDevice.toJsonObject());
            if (!(operation.getErrorno() == 0 && OneNetHttpConstants.CREATE_SUCCESS.equals(operation.getError()))) {
                log.info("delete device fallback: {}", operation.toString());
                throw new FilinkOneNetException("oneNet response: delete device error");
            }
            return ResultUtils.success(OneNetResultCode.SUCCESS,
                    I18nUtils.getSystemString(OneNetI18n.DELETE_SUCCESS));
        } catch (Exception e) {
            log.error("delete device error", e);
        }
        return ResultUtils.warn(OneNetResultCode.DELETE_FAILED,
                I18nUtils.getSystemString(OneNetI18n.DELETE_FAILED));
    }

    /**
     * ??????HTTP????????????Token
     * @param productId ??????Id
     * @return Token
     * @throws UnsupportedEncodingException ??????
     * @throws NoSuchAlgorithmException ??????
     * @throws InvalidKeyException ??????
     */
    private String getToken(String productId) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        //????????????ID????????????
        String accessKey = productInfoUtil.findAccessKeyByProductId(productId);
        // ????????????ID????????????API
        String resourceName = "products/" + productId;
        //???????????????token????????????
        String expirationTime = String.valueOf(System.currentTimeMillis() + 1800);
        return OneNetToken.assembleToken(OneNetHttpConstants.VERSION, resourceName, expirationTime, accessKey);
    }

    /**
     * ????????????
     * @param fiLinkReqParamsDto imei???productId???accessKey??????
     */
    @Override
    public void write(FiLinkReqParamsDto fiLinkReqParamsDto) {
        //???????????????????????????,??????redis????????????
        if (CmdId.SET_CONFIG.equals(fiLinkReqParamsDto.getCmdId())) {
            updateRedisControl(fiLinkReqParamsDto.getEquipmentId(), fiLinkReqParamsDto.getParams());
        }
        //?????????????????????
        FiLinkOneNetConnectParams fiLinkReqParams = new FiLinkOneNetConnectParams();
        //????????????
        fiLinkReqParams.setCmdType(CmdType.REQUEST_TYPE);
        //??????id
        fiLinkReqParams.setCmdId(fiLinkReqParamsDto.getCmdId());
        //????????????
        fiLinkReqParams.setParams(fiLinkReqParamsDto.getParams());
        //??????id
        fiLinkReqParams.setEquipmentId(fiLinkReqParamsDto.getEquipmentId());
        //????????????
        fiLinkReqParams.setSoftwareVersion(fiLinkReqParamsDto.getSoftwareVersion());
        //????????????
        fiLinkReqParams.setHardwareVersion(fiLinkReqParamsDto.getHardwareVersion());
        //??????id
        fiLinkReqParams.setAppId(fiLinkReqParamsDto.getAppId());
        //token
        fiLinkReqParams.setToken(fiLinkReqParamsDto.getToken());
        //imei
        fiLinkReqParams.setImei(fiLinkReqParamsDto.getImei());
        //??????id
        fiLinkReqParams.setPhoneId(fiLinkReqParamsDto.getPhoneId());
        //appKey
        fiLinkReqParams.setAppKey(fiLinkReqParamsDto.getAppKey());
        sender.sendInstruct(fiLinkReqParams);
    }

    /**
     * ??????redis????????????
     *
     * @param equipmentId ??????id
     * @param params      ??????????????????
     */
    private void updateRedisControl(String equipmentId, Map<String, Object> params) {
        List<Map<String, Object>> paramsList = (List<Map<String, Object>>) params.get(ParamsKey.PARAMS_KEY);
        if (paramsList == null || paramsList.size() == 0) {
            log.error("set config params list is null");
            return;
        }
        Map<String, Object> configMap = new HashMap<>(64);
        for (Map<String, Object> paramMap : paramsList) {
            String dataClass = (String) paramMap.get(ParamsKey.DATA_CLASS);
            Object data = paramMap.get(ParamsKey.DATA);
            configMap.put(dataClass, data);
        }
        ControlParam control = protocolUtil.getControlById(equipmentId);
        control.setConfigValue(JSONObject.toJSONString(configMap));
        RedisUtils.hSet(RedisKey.CONTROL_INFO, equipmentId, control);
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


}
