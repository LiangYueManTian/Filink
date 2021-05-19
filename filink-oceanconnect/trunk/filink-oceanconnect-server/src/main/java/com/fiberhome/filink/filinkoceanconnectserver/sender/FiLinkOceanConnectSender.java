package com.fiberhome.filink.filinkoceanconnectserver.sender;

import com.fiberhome.filink.commonstation.constant.CmdId;
import com.fiberhome.filink.commonstation.constant.CmdType;
import com.fiberhome.filink.commonstation.constant.Constant;
import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.config.UnlockPushBean;
import com.fiberhome.filink.commonstation.entity.param.AbstractReqParams;
import com.fiberhome.filink.commonstation.entity.xmlbean.FiLinkProtocolBean;
import com.fiberhome.filink.commonstation.exception.RequestException;
import com.fiberhome.filink.commonstation.sender.AbstractInstructSender;
import com.fiberhome.filink.commonstation.sender.RequestResolver;
import com.fiberhome.filink.filinkoceanconnectserver.constant.OceanParamsKey;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkReqOceanConnectParams;
import com.fiberhome.filink.filinkoceanconnectserver.utils.CommonUtil;
import com.fiberhome.filink.filinkoceanconnectserver.utils.ProtocolUtil;
import com.fiberhome.filink.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * oceanConnect发送者
 *
 * @author CongcaiYu
 */
@Slf4j
@Component("fiLinkOceanConnectSender")
public class FiLinkOceanConnectSender extends AbstractInstructSender {

    @Autowired
    private Map<String, RequestResolver> requestHandlerMap;


    @Autowired
    private SendUtil sendUtil;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private ProtocolUtil protocolUtil;


    /**
     * 指令下发
     *
     * @param abstractReqParams 请求参数信息
     * @param data              16进制命令帧
     */
    @Override
    protected void send(AbstractReqParams abstractReqParams, String data) {
        if (abstractReqParams instanceof FiLinkReqOceanConnectParams) {
            FiLinkReqOceanConnectParams oceanConnectParams = (FiLinkReqOceanConnectParams) abstractReqParams;
            sendUtil.sendData(oceanConnectParams, data);
        } else {
            log.error("ocean connect request params convert failed");
        }
    }


    /**
     * 获取请求帧
     *
     * @param abstractReqParams 请求参数
     * @return 请求帧
     */
    @Override
    protected String getReqData(AbstractReqParams abstractReqParams) {
        FiLinkReqOceanConnectParams fiLinkReqOceanConnectParams;
        try {
            fiLinkReqOceanConnectParams = (FiLinkReqOceanConnectParams) abstractReqParams;
        } catch (Exception e) {
            throw new RequestException("filink request params parse exception");
        }
        String equipmentId = fiLinkReqOceanConnectParams.getEquipmentId();
        FiLinkProtocolBean protocolBean = (FiLinkProtocolBean) fiLinkReqOceanConnectParams.getProtocolBean();
        if(protocolBean == null){
            //获取协议信息
            protocolBean = protocolUtil.getProtocolBean(fiLinkReqOceanConnectParams);
            fiLinkReqOceanConnectParams.setProtocolBean(protocolBean);
        }
        //请求帧处理类名称
        String requestResolverName = protocolBean.getRequestResolverName();
        RequestResolver requestResolver = requestHandlerMap.get(requestResolverName);
        //请求帧处理类为空
        if (requestResolver == null) {
            throw new RequestException("no such requestResolver: " + requestResolverName);
        }
        //生成流水号
        Integer serialNum = fiLinkReqOceanConnectParams.getSerialNumber();
        if (serialNum == null) {
            serialNum = commonUtil.getSerialNum(equipmentId);
            fiLinkReqOceanConnectParams.setSerialNumber(serialNum);
        }
        //解析获取请求帧数据包
        String hexData = requestResolver.resolveUdpReq(fiLinkReqOceanConnectParams);
        //请求帧
        String cmdId = abstractReqParams.getCmdId();
        log.info("[sendMsg-resolve] : get request {} of the control {}", hexData, equipmentId);
        if (CmdType.REQUEST_TYPE == fiLinkReqOceanConnectParams.getCmdType()) {
            //构造redis参数
            Map<String, Object> dataMap = new HashMap<>(64);
            //重发次数
            dataMap.put(ParamsKey.RETRY_COUNT, 0);
            //数据
            dataMap.put(ParamsKey.HEX_DATA, hexData);
            dataMap.put(ParamsKey.EQUIPMENT_ID, equipmentId);
            dataMap.put(ParamsKey.SERIAL_NUMBER, serialNum);
            dataMap.put(OceanParamsKey.DEVICE_ID, fiLinkReqOceanConnectParams.getOceanConnectId());
            dataMap.put(OceanParamsKey.APP_ID, fiLinkReqOceanConnectParams.getAppId());
            dataMap.put(ParamsKey.TIME, System.currentTimeMillis());
            String key = equipmentId + Constant.SEPARATOR + serialNum;
            //配置策略key不需要跟流水号,用最新的配置策略去进行配置
            if (CmdId.SET_CONFIG.equals(cmdId)) {
                key = equipmentId + Constant.SEPARATOR + cmdId;
                RedisUtils.hSet(RedisKey.CMD_RESEND_BUFFER, key, dataMap);
            } else if (CmdId.UNLOCK.equals(cmdId)) {
                //如果是开锁指令
                UnlockPushBean unlockPushBean = new UnlockPushBean();
                unlockPushBean.setToken(fiLinkReqOceanConnectParams.getToken());
                unlockPushBean.setAppKey(fiLinkReqOceanConnectParams.getAppKey());
                unlockPushBean.setPhoneId(fiLinkReqOceanConnectParams.getPhoneId());
                //将用户token存入redis
                RedisUtils.hSet(RedisKey.UNLOCK_PUSH, key, unlockPushBean);
                RedisUtils.hSet(RedisKey.CMD_RESEND_BUFFER, key, dataMap);
                //将开锁命令存入缓存，设备响应时重发
                String unlockKey = equipmentId + RedisKey.UNLOCK_CMD_RESEND_BUFFER;
                RedisUtils.hSet(unlockKey, key, dataMap);
            } else if (CmdId.DEPLOY_STATUS.equals(cmdId)) {
                key = equipmentId + Constant.SEPARATOR + cmdId;
                RedisUtils.hSet(RedisKey.CMD_RESEND_BUFFER, key, dataMap);
                //部署状态存入缓存,每次设备激活进行重发
                RedisUtils.hSet(RedisKey.DEPLOY_CMD, equipmentId, dataMap);
            } else {
                RedisUtils.hSet(RedisKey.CMD_RESEND_BUFFER, key, dataMap);
            }
        }
        return hexData;
    }

}
