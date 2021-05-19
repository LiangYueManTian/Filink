package com.fiberhome.filink.alarmcurrentserver.component;

import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.service.impl.AlarmCurrentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * @author weikaun
 */
@Component
@Slf4j
public class RedisListenerForAlarmCurrent extends KeyExpirationEventMessageListener {

    /**
     * mongodb方法
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 当前告警service
     */
    @Autowired
    private AlarmCurrentServiceImpl alarmCurrentService;

    /**
     * Creates new {@link MessageListener} for {@code __keyevent@*__:expired} messages.
     *
     * @param listenerContainer must not be {@literal null}.
     */
    public RedisListenerForAlarmCurrent(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 针对redis数据失效事件，进行数据处理
     *
     * @param message 监听信息
     * @param pattern pattern matching the channel (if specified) - can be null
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        //判断是否为null
        if (message == null) {
            return;
        }
        // 获取key值
        String expiredKey = message.toString();
        // 判断值是否相同
        if (expiredKey.startsWith(AppConstant.MONGODB_PRE)) {
            // 获取id
            String mongodbId = expiredKey.substring(AppConstant.MONGODB_PRE.length());
            Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(mongodbId));
            AlarmCurrent alarmCurrent = mongoTemplate.findOne(query, AlarmCurrent.class);
            alarmCurrentService.currentTumHistory(alarmCurrent);
        }

    }
}
