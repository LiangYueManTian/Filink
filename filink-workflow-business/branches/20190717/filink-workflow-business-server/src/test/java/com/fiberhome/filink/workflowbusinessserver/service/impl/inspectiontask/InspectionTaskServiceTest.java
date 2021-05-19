package com.fiberhome.filink.workflowbusinessserver.service.impl.inspectiontask;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.scheduleapi.api.InspectionTaskFeign;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTask;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskRelatedJob;
import com.fiberhome.filink.workflowbusinessserver.constant.InspectionTaskConstants;
import com.fiberhome.filink.workflowbusinessserver.dao.inspectiontask.InspectionTaskDao;
import com.fiberhome.filink.workflowbusinessserver.dao.inspectiontask.InspectionTaskRelatedJobDao;
import com.fiberhome.filink.workflowbusinessserver.export.inspectiontask.InspectionTaskListExport;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontask.*;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontask.taskrelated.InspectionTaskDeviceReq;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontaskjob.InspectionTaskRelatedJobReq;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskDepartmentService;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskDeviceService;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontaskjob.InspectionTaskJobService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcBaseService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcLogService;
import com.fiberhome.filink.workflowbusinessserver.utils.common.CastListUtil;
import com.fiberhome.filink.workflowbusinessserver.vo.inspectiontask.QueryListInspectionTaskByPageVo;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.*;


/**
 * 巡检任务逻辑层测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/8 20:42
 */
@RunWith(JMockit.class)
public class InspectionTaskServiceTest {

    /**
     * 测试对象巡检任务逻辑层
     */
    @Tested
    private InspectionTaskServiceImpl inspectionTaskService;



    /**
     * 测试对象巡检任务持久层
     */
    @Injectable
    private InspectionTaskDao inspectionTaskDao;

    /**
     * 测试对象巡检任务关联设施逻辑层
     */
    @Injectable
    private InspectionTaskDeviceService inspectionTaskDeviceService;

    /**
     * 测试对象巡检任务关联部门逻辑层
     */
    @Injectable
    private InspectionTaskDepartmentService inspectionTaskDepartmentService;

    /**
     * 部门feign
     */
    @Injectable
    private DepartmentFeign departmentFeign;

    /**
     * 区域feign
     */
    @Injectable
    private AreaFeign areaFeign;

    /**
     * 设施feign
     */
    @Injectable
    private DeviceFeign deviceFeign;

    /**
     * 日志处理类
     */
    @Injectable
    private LogProcess logProcess;

    /**
     * 巡检任务feign
     */
    @Injectable
    private InspectionTaskFeign inspectionTaskFeign;

    /**
     * 巡检任务列表导出
     */
    @Injectable
    private InspectionTaskListExport inspectionTaskListExport;

    /**
     * 工单逻辑层
     */
    @Injectable
    private ProcBaseService procBaseService;

    /**
     * 工单日志逻辑层
     */
    @Injectable
    private ProcLogService procLogService;

    /**
     * 巡检任务关联定时任务逻辑层
     */
    @Injectable
    private InspectionTaskRelatedJobDao inspectionTaskRelatedJobDao;

    /**
     * 巡检任务定时任务逻辑层
     */
    @Injectable
    private InspectionTaskJobService inspectionTaskJobService;

    /**
     * 最大导出条数
     */
    @Injectable
    private Integer maxExportDataSize;

    /**
     * 系统语言
     */
    @Injectable
    private SystemLanguageUtil systemLanguage;


    /**
     * 新增巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/8 21:13
     */
    @Test
    public void insertInspectionTask() {
        Integer maxSize = 10000;
        ReflectionTestUtils.setField(inspectionTaskService, "maxExportDataSize", maxSize);


        InsertInspectionTaskReq req = new InsertInspectionTaskReq();
        req.setInspectionTaskName("2222");
        inspectionTaskService.insertInspectionTask(req);



        InsertInspectionTaskReq reqInfo = new InsertInspectionTaskReq();
        reqInfo.setInspectionTaskName("新增测试巡检任务");
        Integer taskPeriod = 2;
        reqInfo.setTaskPeriod(taskPeriod);
        Integer procPlanDate = 2;
        reqInfo.setProcPlanDate(procPlanDate);
        reqInfo.setTaskStartDate(new Date().getTime());
        reqInfo.setTaskEndDate(new Date().getTime() + 1L);
        String inspectionAreaId = "1";
        reqInfo.setInspectionAreaId(inspectionAreaId);
        String isOpen = "1";
        reqInfo.setIsOpen(isOpen);
        String isSelectAll = "0";
        reqInfo.setIsSelectAll(isSelectAll);
        List<InspectionTaskDepartment> inspectionTaskDepartments = new ArrayList<>();
        InspectionTaskDepartment inspectionTaskDepartment = new InspectionTaskDepartment();
        inspectionTaskDepartment.setAccountabilityDept("1");
        inspectionTaskDepartments.add(inspectionTaskDepartment);
        reqInfo.setDepartmentList(inspectionTaskDepartments);
        List<InspectionTaskDevice> inspectionTaskDeviceList  = new ArrayList<>();
        InspectionTaskDevice inspectionTaskDevice = new InspectionTaskDevice();
        inspectionTaskDevice.setDeviceId("22222");
        inspectionTaskDevice.setDeviceAreaId("2222");
        inspectionTaskDeviceList.add(inspectionTaskDevice);
        reqInfo.setDeviceList(inspectionTaskDeviceList);
        inspectionTaskService.insertInspectionTask(reqInfo);


        new Expectations() {
            {
                inspectionTaskDao.insert((InspectionTask) any);
                result = 1;
            }
        };
        try {
            inspectionTaskService.insertInspectionTask(reqInfo);
        } catch (Exception e) {

        }
    }


    /**
     * 修改巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/8 21:13
     */
    @Test
    public void updateInspectionTask() {

        UpdateInspectionTaskReq reqInfo = new UpdateInspectionTaskReq();
        reqInfo.setInspectionTaskName("新增测试巡检任务");
        reqInfo.setInspectionTaskId("22222222");
        Integer taskPeriod = 2;
        reqInfo.setTaskPeriod(taskPeriod);
        Integer procPlanDate = 2;
        reqInfo.setProcPlanDate(procPlanDate);
        reqInfo.setTaskStartDate(new Date().getTime());
        reqInfo.setTaskEndDate(new Date().getTime() + 1L);
        String inspectionAreaId = "1";
        reqInfo.setInspectionAreaId(inspectionAreaId);
        String isOpen = "1";
        reqInfo.setIsOpen(isOpen);
        String isSelectAll = "0";
        reqInfo.setIsSelectAll(isSelectAll);
        List<InspectionTaskDepartment> inspectionTaskDepartments = new ArrayList<>();
        InspectionTaskDepartment inspectionTaskDepartment = new InspectionTaskDepartment();
        inspectionTaskDepartment.setAccountabilityDept("1");
        inspectionTaskDepartments.add(inspectionTaskDepartment);
        reqInfo.setDepartmentList(inspectionTaskDepartments);
        List<InspectionTaskDevice> inspectionTaskDeviceList  = new ArrayList<>();
        InspectionTaskDevice inspectionTaskDevice = new InspectionTaskDevice();
        inspectionTaskDevice.setDeviceId("22222");
        inspectionTaskDevice.setDeviceAreaId("2222");
        inspectionTaskDeviceList.add(inspectionTaskDevice);
        reqInfo.setDeviceList(inspectionTaskDeviceList);

        Integer maxSize = 10000;
        ReflectionTestUtils.setField(inspectionTaskService, "maxExportDataSize", maxSize);


        UpdateInspectionTaskReq req = new UpdateInspectionTaskReq();
        req.setInspectionTaskName("2222");
        req.setInspectionTaskId("1111111");
        inspectionTaskService.updateInspectionTask(req);



        try {
            inspectionTaskService.updateInspectionTask(reqInfo);
        } catch (Exception e) {

        }


        new Expectations() {
            {
                inspectionTaskDao.queryInspectionTaskNameIsExists((InspectionTask) any);
                result = 1;
            }
        };


        try {
            inspectionTaskService.updateInspectionTask(reqInfo);
        } catch (Exception e) {

        }


        new Expectations() {
            {
                inspectionTaskDao.queryInspectionTaskNameIsExists((InspectionTask) any);
                result = 0;

                inspectionTaskDeviceService.deleteInspectionTaskDevice((InspectionTaskDevice) any);
                result = 1;

                inspectionTaskDepartmentService.deleteInspectionTaskDepartment((InspectionTaskDepartment) any);
                result = 1;

                inspectionTaskDeviceService.insertInspectionTaskDeviceBatch((List<InspectionTaskDevice>) any, anyString);
                result = 1;

                inspectionTaskDepartmentService.insertInspectionTaskDepartmentBatch((List<InspectionTaskDepartment>) any, anyString);
                result = 1;


                inspectionTaskRelatedJobDao.insertInspectionTaskRelatedJobBatch((List<InspectionTaskRelatedJob>) any);
                result = 1;
            }
        };
        try {
            inspectionTaskService.updateInspectionTask(reqInfo);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                inspectionTaskDao.queryInspectionTaskNameIsExists((InspectionTask) any);
                result = 0;

                InspectionTask inspectionTask = inspectionTaskDao.selectById(reqInfo.getInspectionTaskId());
                result = null;
            }
        };


        try {
            inspectionTaskService.updateInspectionTask(reqInfo);
        } catch (Exception e) {

        }
    }

    /**
     * 修改巡检任务状态
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 12:55
     */
    @Test
    public void updateInspectionStatus() {
        UpdateInspectionStatusReq req = null;
        inspectionTaskService.updateInspectionStatus(req);

        UpdateInspectionStatusReq reqInfo = new UpdateInspectionStatusReq();
        reqInfo.setInspectionTaskId("1");
        inspectionTaskService.updateInspectionStatus(reqInfo);


        UpdateInspectionStatusReq reqParam = new UpdateInspectionStatusReq();
        reqParam.setInspectionTaskId("1");
        reqParam.setInspectionTaskStatus("1");
        inspectionTaskService.updateInspectionStatus(reqParam);
    }


    /**
     * 删除巡检任务信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 12:55
     */
    @Test
    public void deleteInspectionTaskByIds() {
        DeleteInspectionTaskByIdsReq req = null;
        inspectionTaskService.deleteInspectionTaskByIds(req);

        DeleteInspectionTaskByIdsReq reqInfo = new DeleteInspectionTaskByIdsReq();
        reqInfo.setIsDeleted("1");
        List<String> inspectionTaskIds = new ArrayList<>();
        inspectionTaskIds.add("1");
        reqInfo.setInspectionTaskIds(inspectionTaskIds);

        new Expectations() {
            {
                inspectionTaskDao.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                result = null;
            }
        };

        try {
            inspectionTaskService.deleteInspectionTaskByIds(reqInfo);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                inspectionTaskDao.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                result = inspectionTaskIds;


                inspectionTaskDao.deleteInspectionTaskBatch((List<String>) any,anyString,
                        (Date) any, anyString);
                result = 1;


                inspectionTaskDeviceService.deleteInspectionTaskDeviceBatch((List<String>) any,anyString);
                result = 1;

                inspectionTaskDepartmentService.deleteInspectionTaskDepartmentBatch((List<String>) any,anyString);
                result = 1;

                inspectionTaskDao.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                InspectionTask task = new InspectionTask();
                task.setInspectionTaskId("1");
                List<InspectionTask> inspectionTaskList = new ArrayList<>();
                inspectionTaskList.add(task);
                result = inspectionTaskList;
            }
        };

        try {
            inspectionTaskService.deleteInspectionTaskByIds(reqInfo);
        } catch (Exception e) {

        }
    }

    /**
     * 删除巡检任务过程
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 13:35
     */
    @Test
    public void deleteInspectionTaskProcess() {
        new Expectations() {
            {

                inspectionTaskDao.deleteInspectionTaskBatch((List<String>) any,anyString,
                        (Date) any, anyString);
                result = 1;


                inspectionTaskDeviceService.deleteInspectionTaskDeviceBatch((List<String>) any,anyString);
                result = 1;

                inspectionTaskDepartmentService.deleteInspectionTaskDepartmentBatch((List<String>) any,anyString);
                result = 1;

                inspectionTaskDao.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                InspectionTask task = new InspectionTask();
                task.setInspectionTaskId("1");
                List<InspectionTask> inspectionTaskList = new ArrayList<>();
                inspectionTaskList.add(task);
                result = inspectionTaskList;
            }
        };

        this.deleteProcessInfo();

        new Expectations() {
            {

                inspectionTaskDao.deleteInspectionTaskBatch((List<String>) any,anyString,
                        (Date) any, anyString);
                result = -1;
            }
        };

        this.deleteProcessInfo();

        new Expectations() {
            {

                inspectionTaskDao.deleteInspectionTaskBatch((List<String>) any,anyString,
                        (Date) any, anyString);
                result = 1;


                inspectionTaskDeviceService.deleteInspectionTaskDeviceBatch((List<String>) any,anyString);
                result = -1;
            }
        };
        this.deleteProcessInfo();


        new Expectations() {
            {

                inspectionTaskDao.deleteInspectionTaskBatch((List<String>) any,anyString,
                        (Date) any, anyString);
                result = 1;

                inspectionTaskDeviceService.deleteInspectionTaskDeviceBatch((List<String>) any,anyString);
                result = 1;

                inspectionTaskDepartmentService.deleteInspectionTaskDepartmentBatch((List<String>) any,anyString);
                result = -1;
            }
        };

        this.deleteProcessInfo();


    }

    /**
     * 删除信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 13:44
     */
    public void deleteProcessInfo() {
        try {
            DeleteInspectionTaskByIdsReq req = new DeleteInspectionTaskByIdsReq();
            List<String> inspectionTaskIds = new ArrayList<>();
            inspectionTaskIds.add("1");
            req.setInspectionTaskIds(inspectionTaskIds);
            inspectionTaskService.deleteInspectionTaskProcess(req);
        } catch (Exception e) {

        }
    }

    /**
     * 巡检任务关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 13:44
     */
    @Test
    public void inspectionTaskRelationDeviceList() {
        InspectionTaskRelationDeviceListReq req = new InspectionTaskRelationDeviceListReq();
        inspectionTaskService.inspectionTaskRelationDeviceList(req);

        req.setInspectionTaskId("1");
        new Expectations() {
            {
                inspectionTaskDeviceService.queryInspectionTaskDeviceByTaskId(anyString);
                result = null;
            }
        };
        inspectionTaskService.inspectionTaskRelationDeviceList(req);
    }


    /**
     * 删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 13:44
     */
    @Test
    public void deleteInspectionTaskLogBatch() {
        List<String> inspectionTaskIds = new ArrayList<>();
        inspectionTaskIds.add("1");
        this.searchInspectionTask();
        inspectionTaskService.deleteInspectionTaskLogBatch(inspectionTaskIds);
    }

    /**
     * 开启或关闭巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 13:44
     */
    @Test
    public void openOrCloseInspectionTaskLogBatch() {
        List<String> inspectionTaskIds = new ArrayList<>();
        inspectionTaskIds.add("1");
        String functionCode = "1";
        this.searchInspectionTask();
        inspectionTaskService.openOrCloseInspectionTaskLogBatch(inspectionTaskIds, functionCode);
    }

    /**
     * 查询巡检任务信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 14:00
     */
    public void searchInspectionTask() {
        new Expectations() {
            {
                inspectionTaskDao.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                List<InspectionTask> inspectionTaskList = new ArrayList<>();
                InspectionTask inspectionTask = new InspectionTask();
                inspectionTask.setInspectionTaskId("1");
                inspectionTaskList.add(inspectionTask);
                result = inspectionTaskList;
            }
        };
    }


    /**
     * 巡检任务查询巡检任务名称是否重复
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 13:44
     */
    @Test
    public void queryInspectionTaskNameIsExists() {
        InspectionTask inspectionTask = new InspectionTask();
        inspectionTaskService.queryInspectionTaskNameIsExists(inspectionTask);
    }

    /**
     * 得到巡检任务名称结果
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 13:44
     */
    @Test
    public void returnInspectionTaskNameResult() {
        InspectionTask inspectionTask = new InspectionTask();
        inspectionTaskService.returnInspectionTaskNameResult(inspectionTask);

        inspectionTask.setInspectionTaskName("test");
        inspectionTask.setInspectionTaskId("taskId");
        new Expectations() {
            {
                inspectionTaskDao.queryInspectionTaskNameIsExists((InspectionTask) any);
                result = 1;
            }
        };
        inspectionTaskService.returnInspectionTaskNameResult(inspectionTask);


        new Expectations() {
            {
                inspectionTaskDao.queryInspectionTaskNameIsExists((InspectionTask) any);
                result = -1;
            }
        };
        inspectionTaskService.returnInspectionTaskNameResult(inspectionTask);
    }


    /**
     * 批量逻辑删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 13:44
     */
    @Test
    public void deleteInspectionTaskBatch() {
        List<String> inspectionIds = new ArrayList<>();
        String isDeleted = "0";
        inspectionTaskService.deleteInspectionTaskBatch(inspectionIds, isDeleted);


        inspectionIds.add("1");


        new Expectations() {
            {
                inspectionTaskDao.deleteInspectionTaskBatch((List<String>) any, anyString,(Date) any, anyString);
                result = 1;
            }
        };
        inspectionTaskService.deleteInspectionTaskBatch(inspectionIds, isDeleted);

        new Expectations() {
            {
                inspectionTaskDao.deleteInspectionTaskBatch((List<String>) any, anyString,(Date) any, anyString);
                result = -1;
            }
        };
        inspectionTaskService.deleteInspectionTaskBatch(inspectionIds, isDeleted);

    }


    /**
     * 根据巡检任务编号查询巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 13:44
     */
    @Test
    public void selectIsDeleteForInspectionTaskIds() {
        List<String> inspectionIds = new ArrayList<>();
        inspectionTaskService.selectIsDeleteForInspectionTaskIds(inspectionIds);

        inspectionIds.add("1");
        inspectionIds.add("2");

        this.searchInspectionTask();
        inspectionTaskService.selectIsDeleteForInspectionTaskIds(inspectionIds);

        inspectionIds = new ArrayList<>();
        inspectionIds.add("1");
        this.searchInspectionTask();
        inspectionTaskService.selectIsDeleteForInspectionTaskIds(inspectionIds);

        new Expectations() {
            {
                inspectionTaskDao.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                result = null;
            }
        };
        inspectionTaskService.selectIsDeleteForInspectionTaskIds(inspectionIds);

    }


    /**
     * 查询巡检任务列表
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 13:44
     */
    @Test
    public void queryListInspectionTaskByPage() {
        QueryCondition<QueryListInspectionTaskByPageReq> queryCondition = new QueryCondition<>();
        inspectionTaskService.queryListInspectionTaskByPage(queryCondition);


        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(20);
        queryCondition.setPageCondition(pageCondition);
        queryCondition.setSortCondition(new SortCondition());
        queryCondition.setFilterConditions(new ArrayList<>());
        queryCondition.setBizCondition(new QueryListInspectionTaskByPageReq());


        new Expectations() {
            {
                inspectionTaskDao.queryListInspectionTaskByPage((QueryCondition<QueryListInspectionTaskByPageReq>) any);
                List<String> inspectionTaskIdList = new ArrayList<>();
                inspectionTaskIdList.add("1");
                result = inspectionTaskIdList;

                inspectionTaskDao.selectInspectionTaskForInspectionTaskIds((List<String>) any);
                List<InspectionTask> inspectionTaskList = new ArrayList<>();
                InspectionTask inspectionTask = new InspectionTask();
                inspectionTask.setInspectionTaskId("1");
                inspectionTask.setTaskStartTime(new Date());
                inspectionTask.setTaskEndTime(new Date());
                inspectionTaskList.add(inspectionTask);
                result = inspectionTaskList;
            }
        };
        inspectionTaskService.queryListInspectionTaskByPage(queryCondition);
    }

    /**
     * 返回巡检部门编号集合
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 15:34
     */
    @Test
    public void getInspectionTaskDepartmentIds() {
        List<InspectionTaskDepartment> inspectionTaskDepartments = new ArrayList<>();
        InspectionTaskDepartment department = new InspectionTaskDepartment();
        department.setInspectionTaskId("1");
        department.setAccountabilityDept("1");
        inspectionTaskDepartments.add(department);
        inspectionTaskService.getInspectionTaskDepartmentIds(inspectionTaskDepartments);
    }


    /**
     * 查询巡检任务详情
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 15:39
     */
    @Test
    public void getInspectionTaskById() {
        new Expectations() {
            {
                inspectionTaskDao.selectOne((InspectionTask) any);
                InspectionTask inspectionTask = new InspectionTask();
                inspectionTask.setInspectionAreaId("1");
                inspectionTask.setInspectionTaskId("1");
                inspectionTask.setIsOpen("1");
                inspectionTask.setInspectionTaskName("1");
                result = inspectionTask;


                inspectionTaskDepartmentService.queryInspectionTaskDepartmentsByTaskId(anyString);
                List<InspectionTaskDepartment> departmentList = new ArrayList<>();
                InspectionTaskDepartment inspectionTaskDepartment = new InspectionTaskDepartment();
                inspectionTaskDepartment.setAccountabilityDept("1");
                inspectionTaskDepartment.setInspectionTaskId("1");
                departmentList.add(inspectionTaskDepartment);
                departmentList.add(inspectionTaskDepartment);
                result = departmentList;

                departmentFeign.queryDepartmentFeignById((List<String>) any);
                List<Department> departmentInfoList = new ArrayList<>();
                Department departmentOne = new Department();
                departmentOne.setId("1");
                departmentOne.setDeptName("name");
                departmentInfoList.add(departmentOne);
                result = departmentInfoList;


                inspectionTaskDeviceService.queryInspectionTaskDeviceByTaskId(anyString);
                List<InspectionTaskDevice> deviceTaskList = new ArrayList<>();
                InspectionTaskDevice taskDeviceOne = new InspectionTaskDevice();
                taskDeviceOne.setDeviceId("1");
                taskDeviceOne.setInspectionTaskId("1");
                taskDeviceOne.setDeviceAreaId("1");
                deviceTaskList.add(taskDeviceOne);
                deviceTaskList.add(taskDeviceOne);
                result = deviceTaskList;


                deviceFeign.getDeviceByIds((String [])any);
                List<DeviceInfoDto> deviceList = new ArrayList<>();
                DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
                deviceInfoDto.setDeviceId("1");
                deviceInfoDto.setDeviceName("name");
                deviceInfoDto.setDeviceType("010");
                deviceInfoDto.setDeviceTypeName("typeName");
                deviceList.add(deviceInfoDto);
                result = deviceList;
            }
        };
        String inspectionTaskId = "1";
        inspectionTaskService.getInspectionTaskById(inspectionTaskId);
    }


    /**
     * 查询巡检任务信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 15:39
     */
    @Test
    public void getInspectionTaskOne() {
        InspectionTask inspectionTask = new InspectionTask();
        inspectionTaskService.getInspectionTaskOne(inspectionTask);
    }


    /**
     * 根据巡检任务编号查询巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 15:39
     */
    @Test
    public void selectInspectionTaskForInspectionTaskIds() {
        List<String> taskIds = new ArrayList<>();
        inspectionTaskService.selectInspectionTaskForInspectionTaskIds(taskIds);

        List<String> taskIdInfo = new ArrayList<>();
        taskIdInfo.add("1");
        inspectionTaskService.selectInspectionTaskForInspectionTaskIds(taskIdInfo);
    }

    /**
     * 批量开启巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 15:39
     */
    @Test
    public void openInspectionTaskBatch() {
        OpenInspectionTaskBatchReq req = new OpenInspectionTaskBatchReq();
        List<String> inspectionTaskIdList = new ArrayList<>();
        inspectionTaskIdList.add("1");
        inspectionTaskIdList.add("2");
        req.setInspectionTaskIds(inspectionTaskIdList);

        new Expectations() {
            {
                inspectionTaskDao.updateInspectionTaskOpenAndCloseBatch((List<String>) any, (InspectionTask) any);
                result = 2;
            }
        };
        inspectionTaskService.openInspectionTaskBatch(req);


        new Expectations() {
            {
                inspectionTaskDao.updateInspectionTaskOpenAndCloseBatch((List<String>) any, (InspectionTask) any);
                result = 1;
            }
        };
        try {
            inspectionTaskService.openInspectionTaskBatch(req);
        } catch (Exception e) {

        }
    }


    /**
     * 批量开启巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 15:39
     */
    @Test
    public void closeInspectionTaskBatch() {
        CloseInspectionTaskBatchReq req = new CloseInspectionTaskBatchReq();
        List<String> inspectionTaskIdList = new ArrayList<>();
        inspectionTaskIdList.add("1");
        inspectionTaskIdList.add("2");
        req.setInspectionTaskIds(inspectionTaskIdList);

        new Expectations() {
            {
                inspectionTaskDao.updateInspectionTaskOpenAndCloseBatch((List<String>) any, (InspectionTask) any);
                result = 2;
            }
        };
        inspectionTaskService.closeInspectionTaskBatch(req);


        new Expectations() {
            {
                inspectionTaskDao.updateInspectionTaskOpenAndCloseBatch((List<String>) any, (InspectionTask) any);
                result = 1;
            }
        };
        try {
            inspectionTaskService.closeInspectionTaskBatch(req);
        } catch (Exception e) {

        }
    }

    /**
     * 巡检任务详情
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 15:39
     */
    @Test
    public void getInspectionTaskDetail() {
        String id = "1";
        inspectionTaskService.getInspectionTaskDetail(id);
    }

    /**
     * 导出巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 15:39
     */
    @Test
    public void exportInspectionTask() {
        ExportDto exportDto = new ExportDto();
        inspectionTaskService.exportInspectionTask(exportDto);

        new Expectations() {
            {
                inspectionTaskListExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportNoDataException();
            }
        };
        try {
            inspectionTaskService.exportInspectionTask(exportDto);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                inspectionTaskListExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportDataTooLargeException("");
            }
        };
        try {
            inspectionTaskService.exportInspectionTask(exportDto);
        } catch (Exception e) {

        }


        new Expectations() {
            {
                inspectionTaskListExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportTaskNumTooBigException();
            }
        };
        try {
            inspectionTaskService.exportInspectionTask(exportDto);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                inspectionTaskListExport.insertTask((ExportDto) any, anyString, anyString);
                result = new Exception();
            }
        };
        try {
            inspectionTaskService.exportInspectionTask(exportDto);
        } catch (Exception e) {

        }

    }



    /**
     * 根据设施集合删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 15:39
     */
    @Test
    public void deleteInspectionTaskForDeviceList() {
        DeleteInspectionTaskForDeviceReq req = new DeleteInspectionTaskForDeviceReq();
        inspectionTaskService.deleteInspectionTaskForDeviceList(req);


        new Expectations(CastListUtil.class) {
            {
                inspectionTaskDeviceService.queryInspectionTaskDeviceForDeviceIdList((InspectionTaskDeviceReq) any);
                List<InspectionTaskDevice> inspectionTaskDeviceList = new ArrayList<>();
                InspectionTaskDevice inspectionTaskDevice = new InspectionTaskDevice();
                inspectionTaskDevice.setDeviceAreaId("1");
                inspectionTaskDevice.setDeviceId("1");
                inspectionTaskDevice.setInspectionTaskId("1");
                inspectionTaskDeviceList.add(inspectionTaskDevice);
                result = inspectionTaskDeviceList;

                CastListUtil.getNotDeleteTaskDeviceCount((Map<String, List<InspectionTaskDevice>>) any,(List<InspectionTask>) any);
                List<InspectionTask> notDeleteInspectionTaskList = new ArrayList<>();
                InspectionTask inspectionTask = new InspectionTask();
                inspectionTask.setInspectionTaskId("1");
                notDeleteInspectionTaskList.add(inspectionTask);
                result = notDeleteInspectionTaskList;

                inspectionTaskDeviceService.logicDeleteTaskDeviceBatch((InspectionTaskDeviceReq) any);
                result = 1;
            }
        };
        try {
            inspectionTaskService.deleteInspectionTaskForDeviceList(req);
        } catch (Exception e) {

        }


        new Expectations(CastListUtil.class) {
            {
                inspectionTaskDeviceService.queryInspectionTaskDeviceForDeviceIdList((InspectionTaskDeviceReq) any);
                List<InspectionTaskDevice> inspectionTaskDeviceList = new ArrayList<>();
                InspectionTaskDevice inspectionTaskDevice = new InspectionTaskDevice();
                inspectionTaskDevice.setDeviceAreaId("1");
                inspectionTaskDevice.setDeviceId("1");
                inspectionTaskDevice.setInspectionTaskId("1");
                inspectionTaskDeviceList.add(inspectionTaskDevice);
                result = inspectionTaskDeviceList;

                CastListUtil.getAbleDeleteInspectionTaskIdList((Map<String, List<InspectionTaskDevice>>) any, req);
                List<String> ableDeleteTaskIdList = new ArrayList<>();
                ableDeleteTaskIdList.add("1");
                result = ableDeleteTaskIdList;
            }
        };
        try {
            inspectionTaskService.deleteInspectionTaskForDeviceList(req);
        } catch (Exception e) {

        }
    }

    /**
     * 校验部门信息有无关联巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 15:39
     */
    @Test
    public void queryInspectionTaskListByDeptIds() {
        new Expectations() {
            {
                inspectionTaskDepartmentService.queryTaskListByDeptIds((List<String>) any);
                List<InspectionTaskDepartment> departmentList = new ArrayList<>();
                InspectionTaskDepartment inspectionTaskDepartment = new InspectionTaskDepartment();
                inspectionTaskDepartment.setInspectionTaskId("1");
                inspectionTaskDepartment.setAccountabilityDept("1");
                departmentList.add(inspectionTaskDepartment);
                departmentList.add(inspectionTaskDepartment);
                result = departmentList;
            }
        };

        List<String> deptIds = new ArrayList<>();
        inspectionTaskService.queryInspectionTaskListByDeptIds(deptIds);
    }

    /**
     * 修改删除设施总数
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 19:50
     */
    @Test
    public void updateDeleteDeviceCount() {
        List<InspectionTask> notDeleteInspectionTaskList = new ArrayList<>();
        InspectionTask inspectionTask = new InspectionTask();
        inspectionTask.setInspectionTaskId("1");
        notDeleteInspectionTaskList.add(inspectionTask);

        new Expectations() {
            {
                inspectionTaskDao.updateInspectionTaskInfoBatch((List<InspectionTask>) any);
                result = -1;
            }
        };

        try {
            inspectionTaskService.updateDeleteDeviceCount(notDeleteInspectionTaskList);
        } catch (Exception e) {

        }
    }

    /**
     * 巡检任务管理部门数据加入到巡检任务列表中
     * @author hedongwei@wistronits.com
     * @date  2019/3/7 10:22
     */
    @Test
    public void areaInfoInspectionTask() {
        List<QueryListInspectionTaskByPageVo>  inspectionTaskVoList = new ArrayList<>();
        QueryListInspectionTaskByPageVo pageVo = new QueryListInspectionTaskByPageVo();
        pageVo.setInspectionAreaId("1");
        inspectionTaskVoList.add(pageVo);

        Map<String, String> areaMap = new HashMap<>();
        areaMap.put("1", "1");
        inspectionTaskService.areaInfoInspectionTask(inspectionTaskVoList, areaMap);
    }

    /**
     * 获取巡检任务关联巡检部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 20:47
     */
    @Test
    public void getTaskListDeptName() {
        List<InspectionTaskDepartment> inspectionTaskDepartments = new ArrayList<>();
        InspectionTaskDepartment inspectionTaskDepartment = new InspectionTaskDepartment();
        inspectionTaskDepartment.setInspectionTaskId("1");
        inspectionTaskDepartment.setAccountabilityDept("1");
        inspectionTaskDepartments.add(inspectionTaskDepartment);
        inspectionTaskDepartments.add(inspectionTaskDepartment);
        Map<String, String> departmentMap = new HashMap<>();
        departmentMap.put("1", "1");
        inspectionTaskService.getTaskListDeptName(inspectionTaskDepartments, departmentMap);
    }

    /**
     * 巡检任务周期是否经过2月份
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 14:38
     */
    @Test
    public void isAfterFebruary() {
        Long startDate = 1567861620000L;
        Date startTime = new Date(startDate);
        Long addStartTime = 2678400000L;
        Long endDate = startTime.getTime() + addStartTime;
        Date endTime = new Date(endDate);
        int taskPeriod = 1;
        inspectionTaskService.isAfterFebruary(startTime, endTime, taskPeriod);

        Date endTimeInfo = null;
        inspectionTaskService.isAfterFebruary(startTime, endTimeInfo, taskPeriod);
    }

    /**
     * 新增巡检定时任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 10:54
     */
    @Test
    public void addInspectionTaskJob() {
        InspectionTask inspectionTask = new InspectionTask();
        inspectionTask.setInspectionTaskName("1");
        String taskType = InspectionTaskConstants.INSPECTION_TASK_JOB_TYPE_2;
        inspectionTaskService.addInspectionTaskJob(inspectionTask, taskType);
    }

    /**
     * 创建修改巡检任务主表参数
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 10:54
     */
    @Test
    public void createUpdateInspectionTaskParam() {
        UpdateInspectionTaskReq req = new UpdateInspectionTaskReq();
        inspectionTaskService.createUpdateInspectionTaskParam(req);
    }

    /**
     * 修改巡检任务创建定时任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 10:54
     */
    @Test
    public void updateInspectionTaskCreateJob() {
        new Expectations() {
            {
                inspectionTaskRelatedJobDao.selectInspectionTaskRelatedInfo((InspectionTaskRelatedJobReq) any);
                List<InspectionTaskRelatedJob> relatedJobs = new ArrayList<>();
                InspectionTaskRelatedJob job = new InspectionTaskRelatedJob();
                job.setInspectionTaskJobName("1");
                relatedJobs.add(job);
                result = relatedJobs;
            }
        };
        String inspectionTaskId = "1";
        UpdateInspectionTaskReq req = new UpdateInspectionTaskReq();
        req.setTaskStartDate(11111L);
        req.setTaskEndDate(22222L);
        req.setTaskPeriod(2);
        try {
            inspectionTaskService.updateInspectionTaskCreateJob(req, inspectionTaskId);
        } catch (Exception e) {

        }



        new Expectations() {
            {
                inspectionTaskRelatedJobDao.selectInspectionTaskRelatedInfo((InspectionTaskRelatedJobReq) any);
                List<InspectionTaskRelatedJob> relatedJobs = new ArrayList<>();
                InspectionTaskRelatedJob job = new InspectionTaskRelatedJob();
                job.setInspectionTaskJobName("1");
                relatedJobs.add(job);
                result = relatedJobs;

                Result result = inspectionTaskFeign.deleteTaskJobList((List<String>) any);
                result = null;

            }
        };
        String inspectionTaskIdInfo = "1";
        UpdateInspectionTaskReq reqInfo = new UpdateInspectionTaskReq();
        try {
            inspectionTaskService.updateInspectionTaskCreateJob(reqInfo, inspectionTaskId);
        } catch (Exception e) {

        }
    }


    /**
     * 新增巡检任务参数
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 21:44
     */
    @Test
    public void insertInspectionTaskParam() {
        InsertInspectionTaskReq req = new InsertInspectionTaskReq();
        inspectionTaskService.insertInspectionTaskParam(req);
    }

    /**
     * 校验返回参数
     * @author hedongwei@wistronits.com
     * @date  2019/7/9 21:50
     */
    @Test
    public void checkResultInfo() {
        String msg = "";
        Integer resultCode = -1;
        Result result = ResultUtils.warn(resultCode, msg);
        inspectionTaskService.checkResultInfo(result);

        inspectionTaskService.checkResultInfo(null);
    }



}
