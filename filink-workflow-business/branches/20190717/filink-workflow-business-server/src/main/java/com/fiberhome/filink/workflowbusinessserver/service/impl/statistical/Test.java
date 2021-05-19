package com.fiberhome.filink.workflowbusinessserver.service.impl.statistical;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procclear.ProcClearFailure;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import com.fiberhome.filink.workflowbusinessserver.dao.procbase.ProcBaseDao;
import com.fiberhome.filink.workflowbusinessserver.service.procclear.ProcClearFailureService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionRecordService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Author:qiqizhu@wistronits.com
 * Date:2019/6/15
 */
@Component
public class Test {

    @Autowired
    private ProcBaseDao procBaseDao;
    @Autowired
    private ProcInspectionService procInspectionService;
    @Autowired
    private ProcInspectionRecordService procInspectionRecordService;
    @Autowired
    private ProcClearFailureService procClearFailureService;

    public void test1(List<ProcRelatedDevice> addDeviceInfo2){
        procBaseDao.addProcRelateDeviceToBatch(addDeviceInfo2);
    }

    public void test2(List<ProcRelatedDepartment> addDepartment){
        procBaseDao.addProcRelateDeptToBatch(addDepartment);
    }

    public void test3(List<ProcInspection> procInspectionList2){
        //批量新增巡检工单信息
        procInspectionService.insertBatch(procInspectionList2);
    }

    public void test4(List<ProcInspectionRecord> addInspectionRecordInfo2){
        procInspectionRecordService.insertInspectionRecordBatch(addInspectionRecordInfo2);
    }

    public void test5(List<ProcClearFailure> addProcClearFailure) {
        procClearFailureService.insertBatch(addProcClearFailure);
    }
}
