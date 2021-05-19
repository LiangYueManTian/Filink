package com.fiberhome.filink.workflowbusinessserver.consume;

import com.fiberhome.filink.workflowbusinessserver.service.alarmprocess.AlarmProcessService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionService;
import com.fiberhome.filink.workflowbusinessserver.stream.WorkflowBusinessStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 流程业务消费类
 * @author hedongwei@wistronits.com
 * @date 2019/4/4 18:26
 */
@Component
public class WorkflowBusinessConsume {

    @Autowired
    private AlarmProcessService alarmProcessService;

    @Autowired
    private ProcInspectionService procInspectionService;

    @StreamListener(WorkflowBusinessStreams.WORKFLOW_BUSINESS_INPUT)
    public void workflowBusinessConsumer(String msg) {
        if (!StringUtils.isEmpty(msg)) {
            //调用工单生成超时告警的方法
            alarmProcessService.procTimeOutCreateAlarm();
        }
    }


    @StreamListener(WorkflowBusinessStreams.PROC_INSPECTION_INPUT)
    public void procInspectionConsumer(String msg) {
        if (!StringUtils.isEmpty(msg)) {
            System.out.println("调用巡检工单方法成功0000000");
        }
    }
}
