package com.fiberhome.filink.alarmcurrentserver.stream;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.WebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * Stream监听demo
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Slf4j
@Component
public class ConsumerDemo {

    /**
     * log监听
     * @param message 返回值
     */
    @StreamListener(AlarmStreams.WEBSOCKET_OUTPUT)
    public void consumerDmeo(WebSocketMessage message) {
        log.info("Received" + JSONObject.toJSONString(message));
    }
}
