package com.fiberhome.filink.demoserver.stream;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class KafkaConsumer {

    @StreamListener(DemoStreams.DEMO_INPUT)
    private void consumer(Object message) {
        System.out.println("KAFKA消费成功");
        System.out.println(message);
    }
}
