package com.fiberhome.filink.stationserver.stream;

import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.config.RetryConfig;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.constant.RedisKey;
import com.fiberhome.filink.stationserver.sender.UdpChannel;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * 指令重发消费者测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class FiLinkResendReceiverAsyncTest {

    @Tested
    private FiLinkResendReceiverAsync fiLinkResendReceiverAsync;

    @Injectable
    private UdpChannel udpChannel;

    @Injectable
    private RetryConfig stationRetryConfig;

    private String equipmentId;

    private String hexData;

    @Before
    public void setUp() {
        stationRetryConfig = new RetryConfig();
        stationRetryConfig.setRetryCount(2);
        stationRetryConfig.setRetryCycle(30);
        equipmentId = "0101CFED4C0400000000";
        hexData = "FFEF01011EBFDD6E5C118366002842474D50000D00002201000200000000001600000001000000000001000100000000000000000000";
    }

    @Test
    public void cmdResendReceiver() {
        //cmdMap为空
        Map<String, Object> nullCmdMap = new HashMap<>(64);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGetMap(RedisKey.CMD_RESEND_BUFFER);
                result = nullCmdMap;
            }
        };
        fiLinkResendReceiverAsync.cmdResendReceiver();

        //cmdMap不为空,达到最大次数
        Map<String, Object> notNullCmdOneCountResendMap = new HashMap<>(64);
        Map<String, Object> notNullCmdOneCountMap = new HashMap<>(64);
        notNullCmdOneCountMap.put(ParamsKey.EQUIPMENT_ID, equipmentId);
        notNullCmdOneCountMap.put(ParamsKey.HEX_DATA, hexData);
        notNullCmdOneCountMap.put(ParamsKey.RETRY_COUNT, 2);
        notNullCmdOneCountMap.put(ParamsKey.TIME, 1563874900812l);
        String key = equipmentId+"-255";
        notNullCmdOneCountResendMap.put(key, notNullCmdOneCountMap);
        String unlockKey = equipmentId + RedisKey.UNLOCK_CMD_RESEND_BUFFER;
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGetMap(RedisKey.CMD_RESEND_BUFFER);
                result = notNullCmdOneCountResendMap;
            }
            {
                RedisUtils.hRemove(RedisKey.CMD_RESEND_BUFFER, key);
            }
            {
                RedisUtils.hRemove(unlockKey, key);
            }
            {
                RedisUtils.hRemove(RedisKey.UNLOCK_PUSH, key);
            }
        };
        fiLinkResendReceiverAsync.cmdResendReceiver();


        //cmdMap不为空,未达到最大次数
        Map<String, Object> notNullCmdTwoCountResendMap = new HashMap<>(64);
        Map<String, Object> notNullCmdTwoCountMap = new HashMap<>(64);
        notNullCmdTwoCountMap.put(ParamsKey.EQUIPMENT_ID, equipmentId);
        notNullCmdTwoCountMap.put(ParamsKey.HEX_DATA, hexData);
        notNullCmdTwoCountMap.put(ParamsKey.RETRY_COUNT, 1);
        notNullCmdTwoCountMap.put(ParamsKey.TIME, 1563874900812l);
        notNullCmdTwoCountResendMap.put(key,notNullCmdTwoCountMap);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGetMap(RedisKey.CMD_RESEND_BUFFER);
                result = notNullCmdTwoCountResendMap;
            }
        };
        fiLinkResendReceiverAsync.cmdResendReceiver();
    }
}