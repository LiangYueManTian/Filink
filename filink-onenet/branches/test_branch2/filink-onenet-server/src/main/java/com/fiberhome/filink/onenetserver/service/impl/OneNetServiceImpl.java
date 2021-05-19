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
 *   oneNet平台Lwm2m协议API服务实现层
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-25
 */
@Slf4j
@Service
public class OneNetServiceImpl implements OneNetService {
    /**
     * 自动注入
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
     * 更新平台域名
     *
     * @param hostBean 平台域名
     * @return 操作结果
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
     * 新增设备
     * @param createDevice 设备信息 title，imei，imsi，productId，accessKey为必填信息
     * @return 设备信息
     */
    @Override
    public Result createDevice(CreateDevice createDevice) {
        try {
            log.info("create device: {}, imei is :{}", createDevice.getTitle() , createDevice.getImei());
            //根据产品ID获取HTTP安全鉴权Token
            String token = getToken(createDevice.getProductId());
            CreateDeviceOpe deviceOpe = new CreateDeviceOpe(token);
            //获取oneNet域名
            createDevice.setHost(hostUtil.getOneNetHost());
            //调用oneNet的api
            OneNetResponse operation = deviceOpe.operation(createDevice, createDevice.toJsonObject());
            //校验返回码
            if (!(operation.getErrorno() == 0 && OneNetHttpConstants.CREATE_SUCCESS.equals(operation.getError()))) {
                log.info("create device fallback: {}", operation.toString());
                throw new FilinkOneNetException("oneNet response: create device error");
            }
            //解析返回值
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
     * 删除设备
     * @param deleteDevice 设备 deviceId，productId，accessKey必填
     * @return true 成功 false失败
     */
    @Override
    public Result deleteDevice(DeleteDevice deleteDevice) {
        try {
            log.info("delete device, deviceId: {}", deleteDevice.getDeviceId());
            //根据产品ID获取HTTP安全鉴权Token
            String token = getToken(deleteDevice.getProductId());
            DeleteDeviceOpe deleteDeviceOpe = new DeleteDeviceOpe(token);
            //获取oneNet域名
            deleteDevice.setHost(hostUtil.getOneNetHost());
            //调用oneNet的api
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
     * 获取HTTP安全鉴权Token
     * @param productId 产品Id
     * @return Token
     * @throws UnsupportedEncodingException 异常
     * @throws NoSuchAlgorithmException 异常
     * @throws InvalidKeyException 异常
     */
    private String getToken(String productId) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        //根据产品ID获取秘钥
        String accessKey = productInfoUtil.findAccessKeyByProductId(productId);
        // 通过产品ID访问产品API
        String resourceName = "products/" + productId;
        //用户自定义token过期时间
        String expirationTime = String.valueOf(System.currentTimeMillis() + 1800);
        return OneNetToken.assembleToken(OneNetHttpConstants.VERSION, resourceName, expirationTime, accessKey);
    }

    /**
     * 下发命令
     * @param fiLinkReqParamsDto imei，productId，accessKey必填
     */
    @Override
    public void write(FiLinkReqParamsDto fiLinkReqParamsDto) {
        //如果是配置下发指令,更新redis主控信息
        if (CmdId.SET_CONFIG.equals(fiLinkReqParamsDto.getCmdId())) {
            updateRedisControl(fiLinkReqParamsDto.getEquipmentId(), fiLinkReqParamsDto.getParams());
        }
        //构造请求帧参数
        FiLinkOneNetConnectParams fiLinkReqParams = new FiLinkOneNetConnectParams();
        //请求类型
        fiLinkReqParams.setCmdType(CmdType.REQUEST_TYPE);
        //指令id
        fiLinkReqParams.setCmdId(fiLinkReqParamsDto.getCmdId());
        //请求参数
        fiLinkReqParams.setParams(fiLinkReqParamsDto.getParams());
        //主控id
        fiLinkReqParams.setEquipmentId(fiLinkReqParamsDto.getEquipmentId());
        //软件版本
        fiLinkReqParams.setSoftwareVersion(fiLinkReqParamsDto.getSoftwareVersion());
        //硬件版本
        fiLinkReqParams.setHardwareVersion(fiLinkReqParamsDto.getHardwareVersion());
        //产品id
        fiLinkReqParams.setAppId(fiLinkReqParamsDto.getAppId());
        //token
        fiLinkReqParams.setToken(fiLinkReqParamsDto.getToken());
        //imei
        fiLinkReqParams.setImei(fiLinkReqParamsDto.getImei());
        //手机id
        fiLinkReqParams.setPhoneId(fiLinkReqParamsDto.getPhoneId());
        //appKey
        fiLinkReqParams.setAppKey(fiLinkReqParamsDto.getAppKey());
        sender.sendInstruct(fiLinkReqParams);
    }

    /**
     * 更新redis主控信息
     *
     * @param equipmentId 主控id
     * @param params      配置参数信息
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


}
