package com.fiberhome.filink.alarmcurrentserver.component;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmhistoryapi.api.AlarmHistoryFeign;
import com.fiberhome.filink.alarmhistoryapi.bean.AlarmHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
     * 历史告警api
     */
    @Autowired
    private AlarmHistoryFeign alarmHistoryFeigm;

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
        if (expiredKey.startsWith(AppConstant.ALARM_MONGODB_PRE)) {
            // 获取id
            String mongodbId = expiredKey.substring(AppConstant.ALARM_MONGODB_PRE.length());
            log.info("当前告警id" + mongodbId);
            Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(mongodbId));
            AlarmCurrent alarmCurrent = mongoTemplate.findOne(query, AlarmCurrent.class);
            // 当前告警转历史告警
            AlarmHistory alarmHistory = new AlarmHistory();
            // 当前告警信息值复制给历史告警
            BeanUtils.copyProperties(alarmCurrent, alarmHistory);
            // 添加到历史告警中
            alarmHistoryFeigm.insertAlarmHistoryFeign(alarmHistory);
            // 删除已清除的数据
            mongoTemplate.remove(alarmCurrent);
        }

    }
}
