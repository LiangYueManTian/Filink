package com.fiberhome.filink.workflowbusinessserver.service.impl.procbase.procrelated;

import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.procrelated.LogicDeleteRelatedDeviceBatch;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.dao.procbase.ProcBaseDao;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import com.fiberhome.filink.workflowbusinessserver.service.procclear.ProcClearFailureService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionRecordService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工单关联逻辑层测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/25 14:01
 */
@RunWith(JMockit.class)
public class ProcRelatedServiceTest {

    /**
     * 测试对象工单关联逻辑层
     */
    @Tested
    private ProcRelatedServiceImpl procRelatedService;


    @Injectable
    private ProcInspectionService procInspectionService;

    @Injectable
    private ProcClearFailureService procClearFailureService;

    @Injectable
    private ProcBaseDao procBaseDao;

    @Injectable
    private ProcInspectionRecordService procInspectionRecordService;

    @Injectable
    private DepartmentFeign departmentFeign;


    /**
     * 获取工单关联部门名称
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 14:24
     */
    @Test
    public void getRelatedDepartmentName() {
        ProcessInfo processInfo = new ProcessInfo();
        List<ProcRelatedDepartment> procRelatedDepartmentList = new ArrayList<>();
        ProcRelatedDepartment department = new ProcRelatedDepartment();
        department.setProcId("1");
        department.setAccountabilityDept("1");
        procRelatedDepartmentList.add(department);
        processInfo.setProcRelatedDepartments(procRelatedDepartmentList);

        new Expectations() {
            {
                departmentFeign.queryDepartmentFeignById((List<String>) any);
                List<Department> departmentList = new ArrayList<>();
                Department department = new Department();
                department.setId("1");
                department.setDeptName("name");
                departmentList.add(department);
                department = new Department();
                department.setId("2");
                department.setDeptName("name");
                departmentList.add(department);
                result = departmentList;
            }
        };
        procRelatedService.getRelatedDepartmentName(processInfo);
    }

    /**
     * 获取部门名称
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 14:49
     */
    @Test
    public void getDepartmentName() {
        List<String> departmentIdList = new ArrayList<>();
        new Expectations() {
            {
                departmentFeign.queryDepartmentFeignById((List<String>) any);
                result = null;
            }
        };

        try {
            procRelatedService.getDepartmentName(departmentIdList);
        } catch (Exception e) {

        }
    }

    /**
     * 批量删除工单关联信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 14:56
     */
    @Test
    public void updateProcRelateIsDeletedBatch() {
        List<ProcBaseReq> procBaseReqList = new ArrayList<>();
        ProcBaseReq req = new ProcBaseReq();
        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        req.setProcId("1");
        procBaseReqList.add(req);
        req = new ProcBaseReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        req.setProcId("2");
        procBaseReqList.add(req);
        String isDeleted = ProcBaseConstants.IS_DELETED_1;
        procRelatedService.updateProcRelateIsDeletedBatch(procBaseReqList, isDeleted);
    }

    /**
     * 批量删除工单特性信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 14:59
     */
    @Test
    public void updateProcSpecificIsDeletedBatch() {
        List<ProcBaseReq> procBaseReqList = new ArrayList<>();
        ProcBaseReq req = new ProcBaseReq();
        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        req.setProcId("1");
        procBaseReqList.add(req);
        req = new ProcBaseReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        req.setProcId("2");
        procBaseReqList.add(req);
        String isDeleted = ProcBaseConstants.IS_DELETED_1;
        procRelatedService.updateProcSpecificIsDeletedBatch(procBaseReqList, isDeleted);
    }


    /**
     * 删除关联设施集合
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 15:04
     */
    @Test
    public void deleteRelatedDeviceList() {
        List<ProcRelatedDevice> procRelatedDeviceList = new ArrayList<>();
        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
        procRelatedDevice.setProcId("1");
        procRelatedDevice.setDeviceId("1");
        procRelatedDeviceList.add(procRelatedDevice);
        String isDeleted = ProcBaseConstants.IS_DELETED_1;

        new Expectations() {
            {
                procBaseDao.logicDeleteRelatedDevice((LogicDeleteRelatedDeviceBatch) any);
                result = 1;
            }
        };
        procRelatedService.deleteRelatedDeviceList(procRelatedDeviceList, isDeleted);

        new Expectations() {
            {
                procBaseDao.logicDeleteRelatedDevice((LogicDeleteRelatedDeviceBatch) any);
                result = 0;
            }
        };
        try {
            procRelatedService.deleteRelatedDeviceList(procRelatedDeviceList, isDeleted);
        } catch (Exception e) {

        }
    }

    /**
     * 删除关联巡检设施记录信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 15:23
     */
    @Test
    public void deleteRelatedInspectionDeviceRecord() {
        List<ProcInspectionRecord> inspectionRecordList = new ArrayList<>();
        ProcInspectionRecord procInspectionRecord = new ProcInspectionRecord();
        procInspectionRecord.setProcId("1");
        procInspectionRecord.setDeviceId("1");
        inspectionRecordList.add(procInspectionRecord);

        Map<String, String> procMap = new HashMap<>();
        procMap.put("2","2");
        String isDeleted = ProcBaseConstants.IS_DELETED_1;
        try {
            procRelatedService.deleteRelatedInspectionDeviceRecord(inspectionRecordList, procMap, isDeleted);
        } catch (Exception e) {

        }
    }

}
