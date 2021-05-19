package com.fiberhome.filink.alarmcurrentserver.alarmrecive;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 告警输出输入通道定义
 *
 * @author taowei
 */

public interface AlarmChannel {

    /**
     * 告警接收
     */
    String ALARM_INPUT = "alarm_input";

    String ALARM_OUTPUT = "alarm_output";


    /**
     * 告警图片
     */
    String ALARM_PIC_INPUT = "alarm_pic_input";

    String ALARM_PIC_OUTPUT = "alarm_pic_output";

    /**
     * 告警远程通知和转工单和推送
     */
    String ALARM_INPUT_END = "alarm_input_end";

    String ALARM_OUTPUT_END = "alarm_output_end";

    /**
     * 工单转告警
     */
    String ORDER_ALARM_INPUT = "order_alarm_input";

    String ORDER_ALARM_OUTPUT = "order_alarm_output";

    /**
     * 告警接收输入流
     *
     * @return SubscribableChannel
     */
    @Input(ALARM_INPUT)
    SubscribableChannel input();

    /**
     * 告警接收输出流
     *
     * @return MessageChannel
     */
    @Output(ALARM_OUTPUT)
    MessageChannel output();

    /**
     * 告警图片输入流
     *
     * @return SubscribableChannel
     */
    @Input(ALARM_PIC_INPUT)
    SubscribableChannel picInput();

    /**
     * 告警图片输出流
     *
     * @return MessageChannel
     */
    @Output(ALARM_PIC_OUTPUT)
    MessageChannel picOutput();


    /**
     * 告警远程通知和转工单和推送输入流
     *
     * @return SubscribableChannel
     */
    @Input(ALARM_INPUT_END)
    SubscribableChannel adviceInput();

    /**
     * 告警远程通知和转工单和推送输出流
     *
     * @return MessageChannel
     */
    @Output(ALARM_OUTPUT_END)
    MessageChannel adviceOutput();


    /**
     * 工单转告警输入流
     *
     * @return SubscribableChannel
     */
    @Input(ORDER_ALARM_INPUT)
    SubscribableChannel orderInput();

    /**
     * 工单转告警输出流
     *
     * @return MessageChannel
     */
    @Output(ORDER_ALARM_OUTPUT)
    MessageChannel orderOutput();

}
