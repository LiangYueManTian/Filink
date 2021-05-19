package com.fiberhome.filink.demoserver.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * kafka通道绑定
 *
 * @author 姚远
 */
public interface DemoStreams {

    String DEMO_OUTPUT = "demo_output";

    String DEMO_INPUT = "demo_input";

    /**
     * 输出定义
     *
     * @return 判断信息
     */
    @Output(DEMO_OUTPUT)
    MessageChannel demeOutput();

    /**
     * 输入定义
     *
     * @return 判断信息
     */
    @Input(DEMO_INPUT)
    SubscribableChannel demoInput();
}
