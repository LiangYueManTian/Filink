package com.fiberhome.filink.workflowbusinessserver.service.impl.inspectiontask;

import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDepartment;
import com.fiberhome.filink.workflowbusinessserver.dao.inspectiontask.InspectionTaskDepartmentDao;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡检任务关联部门测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/6 13:42
 */
@RunWith(JMockit.class)
public class InspectionTaskDepartmentServiceTest {

    /**
     * 被测试的对象巡检任务关联部门
     */
    @Tested
    private InspectionTaskDepartmentServiceImpl inspectionTaskDepartmentService;

    /**
     * 巡检任务关联部门
     */
    @Injectable
    private InspectionTaskDepartmentDao inspectionTaskDepartmentDao;

    /**
     * 查询巡检关联部门信息
     */
    @Test
    public void queryInspectionTaskDepartmentsByTaskIds() {
        List<String> inspectionTaskIds = new ArrayList<>();
        inspectionTaskDepartmentService.queryInspectionTaskDepartmentsByTaskIds(inspectionTaskIds);
    }


    /**
     * 查询巡检任务关联部门信息
     */
    @Test
    public void queryInspectionTaskDepartmentsByTaskId() {
        String inspectionTaskIds = "222";
        inspectionTaskDepartmentService.queryInspectionTaskDepartmentsByTaskId(inspectionTaskIds);
    }


    /**
     * 删除巡检任务关联部门信息
     */
    @Test
    public void deleteInspectionTaskDepartment() {
        InspectionTaskDepartment inspectionTaskDepartment = new InspectionTaskDepartment();
        inspectionTaskDepartment.setInspectionTaskId("1");
        inspectionTaskDepartmentService.deleteInspectionTaskDepartment(inspectionTaskDepartment);

        InspectionTaskDepartment inspectionTaskDepartmentInfo = new InspectionTaskDepartment();
        inspectionTaskDepartmentService.deleteInspectionTaskDepartment(inspectionTaskDepartmentInfo);

    }


    /**
     * 批量新增巡检任务关联单位
     */
    @Test
    public void insertInspectionTaskDepartmentBatch() {
        List<InspectionTaskDepartment> departmentList = new ArrayList<>();
        InspectionTaskDepartment department = new InspectionTaskDepartment();
        department.setInspectionTaskId("1");
        departmentList.add(department);
        String inspectionTaskId = "1";
        inspectionTaskDepartmentService.insertInspectionTaskDepartmentBatch(departmentList, inspectionTaskId);
    }


    /**
     * 批量删除巡检任务关联单位
     */
    @Test
    public void deleteInspectionTaskDepartmentBatch() {
        List<String> inspectionTaskIds = new ArrayList<>();
        String inspectionTaskId = "";
        inspectionTaskIds.add(inspectionTaskId);
        String isDeleted = "0";

        inspectionTaskDepartmentService.deleteInspectionTaskDepartmentBatch(inspectionTaskIds, isDeleted);


        List<String> isDeletedInfoParam = null;
        String isDeletedInfo = "0";
        inspectionTaskDepartmentService.deleteInspectionTaskDepartmentBatch(isDeletedInfoParam, isDeletedInfo);
    }

    public InspectionTaskDepartmentServiceTest() {
        super();
    }

    /**
     * 根据部门集合查询巡检任务信息
     */
    @Test
    public void queryTaskListByDeptIds() {
        List<String> deptIds = new ArrayList<>();
        inspectionTaskDepartmentService.queryTaskListByDeptIds(deptIds);
    }

}
