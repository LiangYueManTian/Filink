package com.fiberhome.filink.stationserver.stream;

import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.config.RetryConfig;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.constant.RedisKey;
import com.fiberhome.filink.stationserver.sender.UdpChannel;
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
    private UdpChannel udpChannel;

    @Resource(name = "stationRetryConfig")
    private RetryConfig retryConfig;

    /**
     * 指令重发方法
     */
    @Async
    public void cmdResendReceiver() {
        try {
            resendHandler();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("cmd rensend handle failed>>>>>>>>>>");
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
        //重试次数
        Integer retryCountConfig = retryConfig.getRetryCount();
        for (Map.Entry<Object, Object> cmdEntry : cmdMap.entrySet()) {
            Map<String, Object> dataMap = (Map<String, Object>) cmdEntry.getValue();
            //获取主控id
            String equipmentId = (String) dataMap.get(ParamsKey.EQUIPMENT_ID);
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
                //达到最大重试次数，从redis中删除
                RedisUtils.hRemove(RedisKey.CMD_RESEND_BUFFER, cmdEntry.getKey());
                String unlockKey = equipmentId + RedisKey.UNLOCK_CMD_RESEND_BUFFER;
                RedisUtils.hRemove(unlockKey, cmdEntry.getKey());
                //删除推送消息unlockPushBean
                RedisUtils.hRemove(RedisKey.UNLOCK_PUSH, cmdEntry.getKey());
            } else {
                //发送指令
                udpChannel.send(equipmentId, hexData);
                //发送次数加一
                log.info("cmd resend : " + hexData);
                dataMap.put(ParamsKey.RETRY_COUNT, ++retryCount);
                RedisUtils.hSet(RedisKey.CMD_RESEND_BUFFER, cmdEntry.getKey().toString(), dataMap);
            }
        }
    }

}
