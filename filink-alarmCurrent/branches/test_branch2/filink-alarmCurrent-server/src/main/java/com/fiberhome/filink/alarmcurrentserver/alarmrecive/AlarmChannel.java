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
     * 增量统计
     */
    String INCREMENTAL_STATISTICSLISTEN = "incremental_input";

    /**
     * 增量统计(周)
     */
    String INCREMENTAL_WEEK_STATISTICSLISTEN = "incremental_week_input";

    /**
     * 增量统计(月)
     */
    String INCREMENTAL_MONTH_STATISTICSLISTEN = "incremental_month_input";

    String INCREMENTAL_INFO_OUT = "filink_alarm_incremental";

    String INCREMENTAL_EXPIRE = "incremental_expire_input";

    String INCREMENTAL_EXPIRE_WEEK = "incremental_expire_week_input";

    String INCREMENTAL_EXPIRE_MONTH = "incremental_expire_month_input";

    String ALARM_CLEAN_STATUS = "alarm_clean_status_input";

    String WORK_FLOW_INPUT = "work_flow_input";

    String WORK_FLOW_OUTPUT = "work_flow_output";


    /**
     * 转工单消费者
     *
     * @return SubscribableChannel
     */
    @Input(WORK_FLOW_INPUT)
    SubscribableChannel workFlowInput();

    /**
     * 转工单发送者
     *
     * @return MessageChannel
     */
    @Output(WORK_FLOW_OUTPUT)
    MessageChannel workFlowOutput();

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


    /**
     * 消息中间件监听通道
     *
     * @return 返回结果
     */
    @Input(INCREMENTAL_STATISTICSLISTEN)
    SubscribableChannel incrementalInput();

    /**
     * 消息中间件监听通道
     *
     * @return 返回结果
     */
    @Input(INCREMENTAL_WEEK_STATISTICSLISTEN)
    SubscribableChannel incrementalWeekInput();

    /**
     * 消息中间件监听通道
     *
     * @return 返回结果
     */
    @Input(INCREMENTAL_MONTH_STATISTICSLISTEN)
    SubscribableChannel incrementalMonthInput();

    /**
     * 设施增量日
     *
     * @return 返回结果
     */
    @Input(INCREMENTAL_EXPIRE)
    MessageChannel alarmSourceIncrementalInput();

    /**
     * 设施增量周
     *
     * @return 返回结果
     */
    @Input(INCREMENTAL_EXPIRE_WEEK)
    MessageChannel alarmSourceIncrementalWeekInput();

    /**
     * 设施增量月
     *
     * @return 返回结果
     */
    @Input(INCREMENTAL_EXPIRE_MONTH)
    MessageChannel alarmSourceIncrementalMonthInput();

    /**
     * 当前转历史
     * @return
     */
    @Input(ALARM_CLEAN_STATUS)
    MessageChannel alarmCleanStatusInput();
}
