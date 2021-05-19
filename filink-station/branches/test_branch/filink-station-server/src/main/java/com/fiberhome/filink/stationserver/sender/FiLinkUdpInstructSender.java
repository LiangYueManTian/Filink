package com.fiberhome.filink.stationserver.sender;

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
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.constant.RedisKey;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkReqParams;
import com.fiberhome.filink.stationserver.util.ProtocolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * filink udp指令下发
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class FiLinkUdpInstructSender extends AbstractInstructSender {

    @Autowired
    private Map<String, RequestResolver> requestHandlerMap;

    @Autowired
    private ProtocolUtil protocolUtil;

    @Autowired
    private UdpChannel udpChannel;


    /**
     * 发送指令
     *
     * @param abstractReqParams 请求参数信息
     * @param data              16进制命令帧
     */
    @Override
    public void send(AbstractReqParams abstractReqParams, String data) {
        udpChannel.send(abstractReqParams.getEquipmentId(), data);
    }

    /**
     * 生成请求帧
     *
     * @param abstractReqParams 请求参数
     * @return 16进制请求帧
     */
    @Override
    public String getReqData(AbstractReqParams abstractReqParams) {
        //log.info("get udp request data");
        FiLinkReqParams fiLinkReqParams;
        try {
            fiLinkReqParams = (FiLinkReqParams) abstractReqParams;
        } catch (Exception e) {
            throw new RequestException("filink request params parse exception");
        }
        String equipmentId = fiLinkReqParams.getEquipmentId();
        FiLinkProtocolBean protocolBean = protocolUtil.getProtocolBean(fiLinkReqParams);
        fiLinkReqParams.setProtocolBean(protocolBean);
        //请求帧处理类名称
        String requestResolverName = protocolBean.getRequestResolverName();
        RequestResolver requestResolver = requestHandlerMap.get(requestResolverName);
        //请求帧处理类为空
        if (requestResolver == null) {
            throw new RequestException("no such requestResolver: " + requestResolverName);
        }
        //如果是请求帧,将指令存入缓存以重发

        //生成流水号
        Integer serialNum = fiLinkReqParams.getSerialNumber();
        if(serialNum == null){
            serialNum = getSerialNum(equipmentId);
            fiLinkReqParams.setSerialNumber(serialNum);
        }
        //解析获取请求帧数据包
        String hexData = requestResolver.resolveUdpReq(fiLinkReqParams);
        //请求帧
        String cmdId = fiLinkReqParams.getCmdId();
        if (CmdType.REQUEST_TYPE == fiLinkReqParams.getCmdType()) {
            //构造redis参数
            Map<String, Object> dataMap = new HashMap<>(64);
            //重发次数
            dataMap.put(ParamsKey.RETRY_COUNT, 0);
            //数据
            dataMap.put(ParamsKey.HEX_DATA, hexData);
            dataMap.put(ParamsKey.EQUIPMENT_ID, equipmentId);
            dataMap.put(ParamsKey.SERIAL_NUMBER, serialNum);
            dataMap.put(ParamsKey.TIME, System.currentTimeMillis());
            String key = equipmentId + Constant.SEPARATOR + serialNum;
            //配置策略key不需要跟流水号,用最新的配置策略去进行配置
            if (CmdId.SET_CONFIG.equals(cmdId)) {
                key = equipmentId + Constant.SEPARATOR + cmdId;
                RedisUtils.hSet(RedisKey.CMD_RESEND_BUFFER, key, dataMap);
            } else if (CmdId.UNLOCK.equals(cmdId)) {
                //如果是开锁指令
                UnlockPushBean unlockPushBean = new UnlockPushBean();
                unlockPushBean.setToken(fiLinkReqParams.getToken());
                unlockPushBean.setAppKey(fiLinkReqParams.getAppKey());
                unlockPushBean.setPhoneId(fiLinkReqParams.getPhoneId());
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


    /**
     * 生成流水号
     *
     * @param equipmentId 主控id
     * @return 流水号
     */
    private synchronized Integer getSerialNum(String equipmentId) {
        try {
            //从redis中获取该设施流水号
            Integer serialNum = (Integer) RedisUtils.hGet(RedisKey.SERIAL_NUM, equipmentId);
            //如果redis为空,赋予初始值
            if (serialNum == null) {
                serialNum = 1;
            } else {
                //如果流水号达到最大值,则从1开始
                if (serialNum == Short.MAX_VALUE - Short.MIN_VALUE) {
                    serialNum = 1;
                } else {
                    serialNum += 1;
                }
            }
            //将序列号存入redis
            RedisUtils.hSet(RedisKey.SERIAL_NUM, equipmentId, serialNum);
            return serialNum;
        }catch (Exception e){
            log.error("get serial number from redis failed");
        }
        return 1;
    }

}
