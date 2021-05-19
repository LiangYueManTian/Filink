package com.fiberhome.filink.userserver.component;

import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.constant.UserConstant;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * @author xgong
 */
@Component
@Slf4j
public class RedisListenerForUser extends KeyExpirationEventMessageListener {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    private static final String TIMEOUT_MODEL = "1504103";

    /**
     * @param listenerContainer must not be {@literal null}.
     */
    public RedisListenerForUser(RedisMessageListenerContainer listenerContainer) {
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

        //message.toString()可以获取失效的key
        String expiredKey = message.toString();
        if (!expiredKey.contains(UserConstant.REDIS_SPLIT)) {
            return;
        }
        String userId = expiredKey.substring(UserConstant.USER_PREFIX.length(), expiredKey.indexOf(UserConstant.REDIS_SPLIT));
        if (StringUtils.isNotEmpty(userId)) {
            log.info("Expired user id:{}", userId);

            //添加用户失效退出日志
            User userInfo = userDao.queryUserInfoById(userId);
            if (userInfo == null) {
                return;
            }
            systemLanguageUtil.querySystemLanguage();
            LogUtils.addLog(userInfo, TIMEOUT_MODEL, LogConstants.LOG_TYPE_SECURITY);
        }
    }
}
