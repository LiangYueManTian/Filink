package com.fiberhome.filink.workflowbusinessserver.service.impl.inspectiontask;

import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskRecord;
import com.fiberhome.filink.workflowbusinessserver.dao.inspectiontask.InspectionTaskRecordDao;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 巡检任务记录逻辑层测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/8 20:42
 */
@RunWith(JMockit.class)
public class InspectionTaskRecordServiceTest {

    /**
     * 测试对象巡检任务记录逻辑层
     */
    @Tested
    private InspectionTaskRecordServiceImpl inspectionTaskRecordService;

    /**
     * 注入对象巡检任务记录持久层
     */
    @Injectable
    private InspectionTaskRecordDao inspectionTaskRecordDao;


    /**
     * 新增巡检任务记录信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/8 21:05
     */
    @Test
    public void addInspectionTaskRecord() {
        InspectionTaskRecord inspectionTaskRecord = new InspectionTaskRecord();
        inspectionTaskRecordService.addInspectionTaskRecord(inspectionTaskRecord);

        new Expectations() {
            {
                inspectionTaskRecordDao.insert((InspectionTaskRecord) any);
                result = 1;
            }
        };
        InspectionTaskRecord inspectionTaskRecordParamInfo = new InspectionTaskRecord();
        inspectionTaskRecordParamInfo.setInspectionTaskId("1");
        inspectionTaskRecordService.addInspectionTaskRecord(inspectionTaskRecordParamInfo);



        new Expectations() {
            {
                inspectionTaskRecordDao.insert((InspectionTaskRecord) any);
                result = 0;
            }
        };

        InspectionTaskRecord inspectionTaskRecordParam = new InspectionTaskRecord();
        inspectionTaskRecordParam.setInspectionTaskId("1");
        try {
            inspectionTaskRecordService.addInspectionTaskRecord(inspectionTaskRecordParam);
        } catch (Exception e) {

        }

    }
}
