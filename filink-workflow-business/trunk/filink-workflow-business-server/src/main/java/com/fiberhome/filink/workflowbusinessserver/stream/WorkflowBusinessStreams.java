package com.fiberhome.filink.workflowbusinessserver.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 输出输入通道定义
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/8 18:04
 */
public interface WorkflowBusinessStreams {
    String WORKFLOW_BUSINESS_INPUT = "workflow_business_input";

    String ALARM_OUTPUT = "alarm_output";

    String PROC_INSPECTION_INPUT = "proc_inspection_input";

    String NOW_DAY_PROC_INPUT = "now_day_proc_input";

    String WEEK_PROC_INPUT = "week_proc_input";

    String MONTH_PROC_INPUT = "month_proc_input";

    String YEAR_PROC_INPUT = "year_proc_input";

    String RECEIPT_PROC_OUT = "receipt_proc_out";


    /**
     * 任务中心消费类
     * @return 返回结果
     */
    @Input(WORKFLOW_BUSINESS_INPUT)
    SubscribableChannel workflowBusinessInput();

    /**
     * 巡检工单消费类
     * @return 返回结果
     */
    @Input(PROC_INSPECTION_INPUT)
    SubscribableChannel procInspectionInput();

    /**
     * 统计工单日增量数据消费类
     * @return  返回结果
     */
    @Input(NOW_DAY_PROC_INPUT)
    SubscribableChannel nowDayProcInput();

    /**
     * 统计工单周增量数据消费类
     * @return  返回结果
     */
    @Input(WEEK_PROC_INPUT)
    SubscribableChannel weekProcInput();

    /**
     * 统计工单月增量数据消费类
     * @return  返回结果
     */
    @Input(MONTH_PROC_INPUT)
    SubscribableChannel monthProcInput();

    /**
     * 统计工单年增量数据消费类
     * @return  返回结果
     */
    @Input(YEAR_PROC_INPUT)
    SubscribableChannel yearProcInput();

    /**
     * 流程业务消息输出通道
     * @return 返回结果
     */
    @Output(ALARM_OUTPUT)
    MessageChannel alarmOutput();

    /**
     * 工单回单推送入口
     * @return 返回结果
     */
    @Output(RECEIPT_PROC_OUT)
    MessageChannel receiptProcOut();

}
