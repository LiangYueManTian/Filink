package com.fiberhome.filink.onenetserver.stream;

import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.config.RetryConfig;
import com.fiberhome.filink.onenetserver.constant.OneNetParamsKey;
import com.fiberhome.filink.onenetserver.constant.RedisKey;
import com.fiberhome.filink.onenetserver.sender.SendUtil;
import com.fiberhome.filink.redis.RedisUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(JMockit.class)
public class FiLinkOneNetResendReceiverAsyncTest {

    /**测试对象 FiLinkOneNetResendReceiverAsync*/
    @Tested
    private FiLinkOneNetResendReceiverAsync resendReceiver;
    /**Mock SendUtil*/
    @Injectable
    private SendUtil sendUtil;
    /**Mock RetryConfig*/
    @Injectable
    private RetryConfig oneNetRetryConfig;

    /**
     * cmdResendReceiver
     */
    @Test
    public void cmdResendReceiverTest() {
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGetMap(RedisKey.CMD_RESEND_BUFFER);
                result = null;
            }
        };
        resendReceiver.cmdResendReceiver();
        Map<Object, Object> cmdMap = new HashMap<>(16);
        cmdMap.put("44545","sss");
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hGetMap(RedisKey.CMD_RESEND_BUFFER);
                result = cmdMap;

                RedisUtils.hRemove(anyString, anyString);

                RedisUtils.hSet(RedisKey.CMD_RESEND_BUFFER, anyString, any);

                oneNetRetryConfig.getRetryCount();
                result = 2;

                oneNetRetryConfig.getRetryCycle();
                result = 600;
            }
        };
        resendReceiver.cmdResendReceiver();
        Map<String, Object> dataMap = new HashMap<>(16);
        dataMap.put(ParamsKey.HEX_DATA, "HDUKAHUKDH");
        dataMap.put(ParamsKey.RETRY_COUNT, 3);
        dataMap.put(ParamsKey.TIME, System.currentTimeMillis());
        Map<String, Object> dataMap1 = new HashMap<>(16);
        dataMap1.put(ParamsKey.HEX_DATA, "HDUKAHUKDH");
        dataMap1.put(ParamsKey.RETRY_COUNT, 3);
        dataMap1.put(ParamsKey.TIME, 1562810000000L);
        Map<String, Object> dataMap2 = new HashMap<>(16);
        dataMap2.put(ParamsKey.HEX_DATA, "HDUKAHUKDH");
        dataMap2.put(ParamsKey.RETRY_COUNT, 1);
        dataMap2.put(ParamsKey.TIME, 1562810000000L);
        dataMap2.put(OneNetParamsKey.IMEI, null);
        Map<String, Object> dataMap3 = new HashMap<>(16);
        dataMap3.put(ParamsKey.HEX_DATA, "HDUKAHUKDH");
        dataMap3.put(ParamsKey.RETRY_COUNT, 1);
        dataMap3.put(ParamsKey.TIME, 1562810000000L);
        dataMap3.put(OneNetParamsKey.IMEI, "5644545445");
        dataMap3.put(OneNetParamsKey.APP_ID, "5644545445");
        cmdMap.put("44545",dataMap);
        cmdMap.put("44545222",dataMap1);
        cmdMap.put("445454532",dataMap2);
        cmdMap.put("44545453244",dataMap3);
        resendReceiver.cmdResendReceiver();
    }
}
