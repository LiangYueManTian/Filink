package com.fiberhome.filink.workflowbusinessserver.controller.inspectiontask;

import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskRecord;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskRecordService;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 巡检任务记录逻辑层测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/4 15:27
 */
@RunWith(JMockit.class)
public class InspectionTaskRecordControllerTest {

    /**
     * 被测试的对象巡检任务记录控制层
     */
    @Tested
    private InspectionTaskRecordController inspectionTaskRecordController;

    /**
     * 被测试的对象巡检任务记录逻辑层
     */
    @Injectable
    private InspectionTaskRecordService inspectionTaskRecordService;


    /**
     * 新增巡检任务记录方法测试
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:33
     */
    @Test
    public void addInspectionTaskRecord() {
        InspectionTaskRecord record = new InspectionTaskRecord();
        inspectionTaskRecordController.addInspectionTaskRecord(record);
    }


}
