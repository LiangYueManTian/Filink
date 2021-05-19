package com.fiberhome.filink.workflowbusinessserver.controller.inspectiontask;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskRecord;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 巡检任务记录表 前端控制器
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-26
 */
@RestController
@RequestMapping("/inspectionTaskRecord")
public class InspectionTaskRecordController {

    @Autowired
    private InspectionTaskRecordService inspectionTaskRecordService;

    /**
     * 新增巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/10 22:19
     * @param taskRecord 巡检任务
     * @return 新增巡检任务结果
     */
    @PostMapping("/addInspectionTaskRecord")
    public Result addInspectionTaskRecord(InspectionTaskRecord taskRecord) {
        //新增巡检记录信息
        return inspectionTaskRecordService.addInspectionTaskRecord(taskRecord);
    }
}
