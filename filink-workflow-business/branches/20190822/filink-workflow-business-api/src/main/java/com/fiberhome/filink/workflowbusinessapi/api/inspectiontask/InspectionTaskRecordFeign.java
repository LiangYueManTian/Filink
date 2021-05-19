package com.fiberhome.filink.workflowbusinessapi.api.inspectiontask;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.workflowbusinessapi.bean.inspectiontask.InspectionTaskRecord;
import com.fiberhome.filink.workflowbusinessapi.fallback.inspectiontask.InspectionTaskRecordFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author hedongwei@wistronits.com
 * InspectionTaskRecordFerign接口
 * 13:41 2019/1/19
 */
@FeignClient(name = "filink-workflow-business-server", path = "/inspectionTaskRecord",fallback = InspectionTaskRecordFeignFallback.class)
public interface InspectionTaskRecordFeign {

    /**
     * 新增巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/10 22:19
     * @param taskRecord 巡检任务
     * @return 新增巡检任务结果
     */
    @PostMapping("/addInspectionTaskRecord")
    Result addInspectionTaskRecord(InspectionTaskRecord taskRecord);
}
