package com.fiberhome.filink.alarmcurrentserver.component;

import com.fiberhome.filink.alarmhistoryapi.api.AlarmHistoryFeign;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@RunWith(JMockit.class)
public class RedisListenerForAlarmCurrentTest {

    @Tested
    private RedisListenerForAlarmCurrent redisListenerForAlarmCurrent;

    /**
     * mongodb方法
     */
    @Injectable
    private MongoTemplate mongoTemplate;
    @Injectable
    private RedisMessageListenerContainer listenerContainer;
    /**
     * 历史告警api
     */
    @Injectable
    private AlarmHistoryFeign alarmHistoryFeigm;

    @Test
    public void onMessage() throws Exception {
        byte[] bytes = new byte[]{1};
        Message message = new Message() {
            @Override
            public byte[] getBody() {
                return new byte[0];
            }

            @Override
            public byte[] getChannel() {
                return new byte[0];
            }
        };
        redisListenerForAlarmCurrent.onMessage(message, bytes);
    }

}