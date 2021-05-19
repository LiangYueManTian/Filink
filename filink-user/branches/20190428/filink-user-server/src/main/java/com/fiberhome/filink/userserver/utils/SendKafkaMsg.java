package com.fiberhome.filink.userserver.utils;

import com.fiberhome.filink.userserver.stream.UpdateUserStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

/**
 * 发送kafka消息方法
 *
 * @author xgong
 */
@Slf4j
public class SendKafkaMsg {

    public static void sendMessage(UpdateUserStream updateUserStream, String taskId) {
        Message msg = MessageBuilder.withPayload(taskId).build();
        log.info("发送kafka消息");
        updateUserStream.updateOutput().send(msg);
    }
}
