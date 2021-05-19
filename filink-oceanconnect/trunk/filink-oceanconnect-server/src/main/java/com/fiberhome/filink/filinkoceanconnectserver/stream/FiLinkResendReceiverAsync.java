package com.fiberhome.filink.filinkoceanconnectserver.stream;

import com.fiberhome.filink.commonstation.constant.Constant;
import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.config.RetryConfig;
import com.fiberhome.filink.filinkoceanconnectserver.constant.OceanParamsKey;
import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkReqOceanConnectParams;
import com.fiberhome.filink.filinkoceanconnectserver.sender.SendUtil;
import com.fiberhome.filink.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 指令重发消费者
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class FiLinkResendReceiverAsync {


    @Autowired
    private SendUtil sendUtil;

    @Resource(name = "oceanRetryConfig")
    private RetryConfig retryConfig;

    /**
     * 指令重发方法
     */
    @Async
    public void cmdResendReceiver() {
        try {
            resendHandler();
        } catch (Exception e) {
            log.error("cmd resend handle failed", e);
        }
    }

    /**
     * 指令重发处理
     */
    private void resendHandler() {
        //从redis中获取需要重发的指令
        Map<Object, Object> cmdMap = RedisUtils.hGetMap(RedisKey.CMD_RESEND_BUFFER);
        if (cmdMap == null || cmdMap.size() == 0) {
            return;
        }
        Integer retryCountConfig = retryConfig.getRetryCount();
        for (Map.Entry<Object, Object> cmdEntry : cmdMap.entrySet()) {
            Map<String, Object> dataMap = (Map<String, Object>) cmdEntry.getValue();
            //获取数据包
            String hexData = (String) dataMap.get(ParamsKey.HEX_DATA);
            //获取重试次数
            Integer retryCount = (Integer) dataMap.get(ParamsKey.RETRY_COUNT);
            //获取该指令存入时间
            Long time = (Long) dataMap.get(ParamsKey.TIME);
            //判断该指令发送时间是否大于轮询时间
            long currentTime = System.currentTimeMillis();
            //重试时间
            Integer retryCycle = retryConfig.getRetryCycle();
            if (currentTime - time < retryCycle * 1000) {
                log.info("the cmd time is not need to resend");
                continue;
            }
            if (retryCount >= retryCountConfig) {
                //清除指令
                removeCmd(cmdEntry);
            } else {
                //发送重发指令
                sendCmd(cmdEntry, dataMap, hexData, retryCount);
            }
        }
    }

    /**
     * 发送重发指令
     *
     * @param cmdEntry   指令键值对
     * @param dataMap    参数信息
     * @param hexData    指令
     * @param retryCount 重试次数
     */
    private void sendCmd(Map.Entry<Object, Object> cmdEntry, Map<String, Object> dataMap, String hexData, Integer retryCount) {
        try {
            //获取平台id
            String oceanConnectId = dataMap.get(OceanParamsKey.DEVICE_ID).toString();
            //获取appId
            String appId = dataMap.get(OceanParamsKey.APP_ID).toString();
            //发送指令
            FiLinkReqOceanConnectParams oceanConnectParams = new FiLinkReqOceanConnectParams();
            oceanConnectParams.setAppId(appId);
            oceanConnectParams.setOceanConnectId(oceanConnectId);
            sendUtil.sendData(oceanConnectParams, hexData);
            log.info("cmd resend success : data:{}", hexData);
        } catch (Exception e) {
            log.error("resend cmd failed", e);
        } finally {
            //次数加一
            dataMap.put(ParamsKey.RETRY_COUNT, ++retryCount);
            RedisUtils.hSet(RedisKey.CMD_RESEND_BUFFER, cmdEntry.getKey().toString(), dataMap);
        }
    }


    /**
     * 移除重发指令
     *
     * @param cmdEntry 指令键值对
     */
    private void removeCmd(Map.Entry<Object, Object> cmdEntry) {
        String equipmentId = (String) cmdEntry.getKey();
        equipmentId = equipmentId.split(Constant.SEPARATOR)[0];
        String unlockKey = equipmentId + RedisKey.UNLOCK_CMD_RESEND_BUFFER;
        //达到最大重试次数，从redis中删除
        RedisUtils.hRemove(RedisKey.CMD_RESEND_BUFFER, cmdEntry.getKey());
        RedisUtils.hRemove(unlockKey, cmdEntry.getKey());
        //删除推送消息unlockPushBean
        RedisUtils.hRemove(RedisKey.UNLOCK_PUSH, cmdEntry.getKey());
    }

}
