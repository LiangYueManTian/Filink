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
     * 流程业务消息输出通道
     * @return 返回结果
     */
    @Output(ALARM_OUTPUT)
    MessageChannel alarmOutput();

}
