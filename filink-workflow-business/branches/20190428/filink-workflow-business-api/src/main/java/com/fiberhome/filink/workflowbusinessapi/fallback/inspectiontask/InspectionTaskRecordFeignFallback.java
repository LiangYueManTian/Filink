package com.fiberhome.filink.workflowbusinessapi.fallback.inspectiontask;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.workflowbusinessapi.api.inspectiontask.InspectionTaskRecordFeign;
import com.fiberhome.filink.workflowbusinessapi.bean.inspectiontask.InspectionTaskRecord;
import com.fiberhome.filink.workflowbusinessapi.utils.inspectiontask.InspectionTaskResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author hedongwei@wistronits.com
 * ferign 熔断
 * 13:45 2019/1/19
 */
@Slf4j
@Component
public class InspectionTaskRecordFeignFallback implements InspectionTaskRecordFeign {


    private static final String INFO = "Inspection task record call fallback";

    /**
     * 新增巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/10 22:19
     * @param taskRecord 巡检任务
     * @return 新增巡检任务结果
     */
    @Override
    public Result addInspectionTaskRecord(InspectionTaskRecord taskRecord) {
        //新增巡检任务熔断
        String addInspectionTaskRecordError = "Add inspection task record error";
        log.info(INFO);
        return ResultUtils.warn(InspectionTaskResultCode.ADD_TASK_RECORD_ERROR, addInspectionTaskRecordError);
    }
}
