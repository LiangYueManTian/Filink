package com.fiberhome.filink.workflowbusinessserver.controller.inspectiontask;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ColumnInfo;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTask;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskI18n;
import com.fiberhome.filink.workflowbusinessserver.constant.InspectionTaskConstants;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontask.*;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡检任务逻辑层测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/4 15:27
 */
@RunWith(JMockit.class)
public class InspectionTaskControllerTest {

    @Tested
    private InspectionTaskController inspectionTaskController;

    @Injectable
    private InspectionTaskService inspectionTaskService;

    @Injectable
    private ProcInspectionService procInspectionService;


    /**
     * 新增巡检任务方法测试
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:33
     */
    @Test
    public void insertInspectionTask() {
        InsertInspectionTaskReq req = new InsertInspectionTaskReq();
        new Expectations() {
            {
                inspectionTaskService.insertInspectionTask((InsertInspectionTaskReq) any);
                result = ResultUtils.success();
            }
        };
        inspectionTaskController.insertInspectionTask(req);
    }


    /**
     * 修改巡检任务方法测试
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:33
     */
    @Test
    public void updateInspectionTask() {
        new Expectations() {
            {
                inspectionTaskService.updateInspectionTask((UpdateInspectionTaskReq) any);
                result = ResultUtils.success();
            }
        };
        UpdateInspectionTaskReq req = new UpdateInspectionTaskReq();
        inspectionTaskController.updateInspectionTask(req);
    }

    /**
     * 修改巡检任务状态方法测试
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:33
     */
    @Test
    public void updateInspectionStatus() {
        UpdateInspectionStatusReq req = new UpdateInspectionStatusReq();
        inspectionTaskController.updateInspectionStatus(req);
    }

    /**
     * 根据设施集合删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:33
     */
    @Test
    public void deleteInspectionTaskForDeviceList() {
        DeleteInspectionTaskForDeviceReq req = new DeleteInspectionTaskForDeviceReq();
        inspectionTaskController.deleteInspectionTaskForDeviceList(req);
    }


    /**
     * 删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:33
     */
    @Test
    public void deleteInspectionTaskByIds() {
        DeleteInspectionTaskByIdsReq req = new DeleteInspectionTaskByIdsReq();
        inspectionTaskController.deleteInspectionTaskByIds(req);
    }

    /**
     * 巡检任务关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:33
     */
    @Test
    public void inspectionTaskRelationDeviceList() {
        InspectionTaskRelationDeviceListReq req = new InspectionTaskRelationDeviceListReq();
        inspectionTaskController.inspectionTaskRelationDeviceList(req);
    }

    /**
     * 批量开启巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:33
     */
    @Test
    public void openInspectionTaskBatch() {
        OpenInspectionTaskBatchReq req = new OpenInspectionTaskBatchReq();
        List<String> inspectionTaskIdList = new ArrayList<>();
        inspectionTaskIdList.add("1");
        req.setInspectionTaskIds(inspectionTaskIdList);
        new Expectations() {
            {
                inspectionTaskService.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                result = null;
            }
        };
        inspectionTaskController.openInspectionTaskBatch(req);

        OpenInspectionTaskBatchReq reqInfo = new OpenInspectionTaskBatchReq();
        new Expectations() {
            {
                inspectionTaskService.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                List<InspectionTask> inspectionTaskList = new ArrayList<>();
                InspectionTask inspectionTask = new InspectionTask();
                inspectionTask.setIsOpen(InspectionTaskConstants.IS_OPEN);
                inspectionTaskList.add(inspectionTask);
                result = inspectionTaskList;
            }
        };
        inspectionTaskController.openInspectionTaskBatch(reqInfo);

        OpenInspectionTaskBatchReq reqInfoParam = new OpenInspectionTaskBatchReq();
        new Expectations() {
            {
                inspectionTaskService.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                List<InspectionTask> inspectionTaskList = new ArrayList<>();
                InspectionTask inspectionTask = new InspectionTask();
                inspectionTask.setIsOpen(InspectionTaskConstants.IS_CLOSE);
                inspectionTaskList.add(inspectionTask);
                result = inspectionTaskList;
            }
        };
        inspectionTaskController.openInspectionTaskBatch(reqInfoParam);
    }

    /**
     * 批量关闭巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:33
     */
    @Test
    public void closeInspectionTaskBatch() {
        CloseInspectionTaskBatchReq req = new CloseInspectionTaskBatchReq();
        List<String> inspectionTaskIdList = new ArrayList<>();
        inspectionTaskIdList.add("1");
        req.setInspectionTaskIds(inspectionTaskIdList);
        new Expectations() {
            {
                inspectionTaskService.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                result = null;
            }
        };
        inspectionTaskController.closeInspectionTaskBatch(req);

        CloseInspectionTaskBatchReq reqInfo = new CloseInspectionTaskBatchReq();
        new Expectations() {
            {
                inspectionTaskService.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                List<InspectionTask> inspectionTaskList = new ArrayList<>();
                InspectionTask inspectionTask = new InspectionTask();
                inspectionTask.setIsOpen(InspectionTaskConstants.IS_CLOSE);
                inspectionTaskList.add(inspectionTask);
                result = inspectionTaskList;
            }
        };
        inspectionTaskController.closeInspectionTaskBatch(reqInfo);

        CloseInspectionTaskBatchReq reqInfoParam = new CloseInspectionTaskBatchReq();
        new Expectations() {
            {
                inspectionTaskService.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                List<InspectionTask> inspectionTaskList = new ArrayList<>();
                InspectionTask inspectionTask = new InspectionTask();
                inspectionTask.setIsOpen(InspectionTaskConstants.IS_OPEN);
                inspectionTaskList.add(inspectionTask);
                result = inspectionTaskList;
            }
        };
        inspectionTaskController.closeInspectionTaskBatch(reqInfo);
    }


    /**
     * 检查巡检任务集合是否满足条件
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:33
     */
    @Test
    public void checkInspectionTaskList() {
        //巡检任务编号集合
        List<String> inspectionTaskIds = new ArrayList<>();

        //操作
        String operator = InspectionTaskI18n.IS_OPEN;
        new Expectations() {
            {
                inspectionTaskService.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                List<InspectionTask> inspectionTaskList = new ArrayList<>();
                InspectionTask inspectionTask = new InspectionTask();
                inspectionTask.setIsOpen(InspectionTaskI18n.IS_OPEN);
                inspectionTaskList.add(inspectionTask);
                result = inspectionTaskList;
            }
        };
        inspectionTaskController.checkInspectionTaskList(inspectionTaskIds, operator);


        //巡检任务编号集合
        List<String> inspectionTaskIdInfo = new ArrayList<>();

        //操作
        String operatorInfo = InspectionTaskI18n.IS_CLOSE;
        new Expectations() {
            {
                inspectionTaskService.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                List<InspectionTask> inspectionTaskList = new ArrayList<>();
                InspectionTask inspectionTask = new InspectionTask();
                inspectionTask.setIsOpen(operatorInfo);
                inspectionTaskList.add(inspectionTask);
                result = inspectionTaskList;
            }
        };
        inspectionTaskController.checkInspectionTaskList(inspectionTaskIdInfo, operatorInfo);

        //巡检任务编号集合
        List<String> inspectionTaskIdListInfo = new ArrayList<>();
        //操作
        String operatorOneInfo = "";
        new Expectations() {
            {
                inspectionTaskService.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                List<InspectionTask> inspectionTaskList = new ArrayList<>();
                InspectionTask inspectionTask = new InspectionTask();
                inspectionTask.setIsOpen(operatorInfo);
                inspectionTaskList.add(inspectionTask);
                result = inspectionTaskList;
            }
        };
        inspectionTaskController.checkInspectionTaskList(inspectionTaskIdListInfo, operatorOneInfo);


        //巡检任务编号集合
        List<String> inspectionTaskIdNullList = new ArrayList<>();
        //操作
        String operatorOneInfoParam = "";
        new Expectations() {
            {
                inspectionTaskService.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                List<InspectionTask> inspectionTaskList = null;
                result = inspectionTaskList;
            }
        };
        inspectionTaskController.checkInspectionTaskList(inspectionTaskIdNullList, operatorOneInfoParam);

    }


    /**
     * 查询巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:33
     */
    @Test
    public void queryListInspectionTaskByPage() {
        QueryCondition<QueryListInspectionTaskByPageReq> queryCondition  = new QueryCondition<>();
        inspectionTaskController.queryListInspectionTaskByPage(queryCondition);
    }

    /**
     * 导出巡检任务数据
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:33
     */
    @Test
    public void exportInspectionTask() {
        ExportDto<QueryListInspectionTaskByPageReq> exportDto = new ExportDto<>();
        new Expectations() {
            {
                exportDto.checkParam();
                result = true;
            }
        };
        inspectionTaskController.exportInspectionTask(exportDto);


        ExportDto<QueryListInspectionTaskByPageReq> exportDtoParam = new ExportDto<>();
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setColumnName("name");
        List<ColumnInfo> info = new ArrayList<>();
        info.add(columnInfo);
        exportDtoParam.setColumnInfoList(info);
        exportDtoParam.setExcelType(0);
        new Expectations() {
            {
                exportDto.checkParam();
                result = false;
            }
        };
        inspectionTaskController.exportInspectionTask(exportDtoParam);
    }


    /**
     * 查询巡检任务详情
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:33
     */
    @Test
    public void getInspectionTaskById() {
        String id = "1";
        new Expectations() {
            {
                inspectionTaskController.checkInspectionTaskId(anyString);
                result = null;
            }
        };
        inspectionTaskController.getInspectionTaskById(id);

        String idInfo = "";
        new Expectations() {
            {
                inspectionTaskController.checkInspectionTaskId(anyString);
                result = ResultUtils.success();
            }
        };
        inspectionTaskController.getInspectionTaskById(idInfo);
    }

    /**
     * 查询巡检任务详情
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:33
     */
    @Test
    public void getInspectionTaskDetail() {
        String id = "1";
        new Expectations() {
            {
                inspectionTaskController.checkInspectionTaskId(anyString);
                result = null;
            }
        };
        inspectionTaskController.getInspectionTaskDetail(id);

        String idInfo = "";
        new Expectations() {
            {
                inspectionTaskController.checkInspectionTaskId(anyString);
                result = ResultUtils.success();
            }
        };
        inspectionTaskController.getInspectionTaskDetail(idInfo);
    }


    /**
     * 校验部门信息有无关联巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:33
     */
    @Test
    public void queryInspectionTaskListByDeptIds() {
        List<String> deptIds = new ArrayList<>();
        deptIds.add("1");
        inspectionTaskController.queryInspectionTaskListByDeptIds(deptIds);

        List<String> deptInfo = new ArrayList<>();
        inspectionTaskController.queryInspectionTaskListByDeptIds(deptInfo);
    }


    /**
     * 校验部门信息有无关联巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/4 15:33
     */
    @Test
    public void queryInspectionTaskIsExists() {
        InspectionTask inspectionTask = new InspectionTask();
        inspectionTaskController.queryInspectionTaskIsExists(inspectionTask);

        InspectionTask inspectionTaskParam = new InspectionTask();
        inspectionTaskParam.setInspectionTaskName("info");
        new Expectations() {
            {
                inspectionTaskService.returnInspectionTaskNameResult((InspectionTask) any);
                boolean flag = true;
                result = flag;
            }
        };
        inspectionTaskController.queryInspectionTaskIsExists(inspectionTaskParam);


        InspectionTask inspectionTaskParamInfo = new InspectionTask();
        inspectionTaskParamInfo.setInspectionTaskName("info");
        new Expectations() {
            {
                inspectionTaskService.returnInspectionTaskNameResult((InspectionTask) any);
                boolean flag = false;
                result = flag;
            }
        };
        inspectionTaskController.queryInspectionTaskIsExists(inspectionTaskParamInfo);
    }


}
