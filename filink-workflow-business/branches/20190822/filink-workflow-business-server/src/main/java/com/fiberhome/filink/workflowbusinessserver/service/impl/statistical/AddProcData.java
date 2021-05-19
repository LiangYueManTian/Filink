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
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 工单新增性能测试数据类
 * @author  qiqizhu@wistronits.com
 * @date 2019/6/15
 */
@Component
public class AddProcData {

    /**
     * 工单父类逻辑层
     */
    @Autowired
    private ProcBaseDao procBaseDao;
    /**
     * 巡检工单逻辑层
     */
    @Autowired
    private ProcInspectionService procInspectionService;
    /**
     * 工单巡检记录逻辑层
     */
    @Autowired
    private ProcInspectionRecordService procInspectionRecordService;
    /**
     * 销障工单逻辑层
     */
    @Autowired
    private ProcClearFailureService procClearFailureService;

    /**
     * 新增关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 20:36
     * @param addDeviceInfo 关联设施参数
     */
    public void addProcRelatedDevice(List<ProcRelatedDevice> addDeviceInfo){
        procBaseDao.addProcRelateDeviceToBatch(addDeviceInfo);
    }

    /**
     * 新增关联部门
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 20:36
     * @param addDepartment 关联部门参数
     */
    public void addRelatedDept(List<ProcRelatedDepartment> addDepartment){
        procBaseDao.addProcRelateDeptToBatch(addDepartment);
    }

    /**
     * 新增巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 20:36
     * @param procInspectionList 巡检工单参数
     */
    public void addProcInspection(List<ProcInspection> procInspectionList){
        //批量新增巡检工单信息
        procInspectionService.insertBatch(procInspectionList);
    }

    /**
     * 新增巡检工单记录信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 20:38
     * @param addInspectionRecordInfo 巡检工单记录参数
     */
    public void addInspectionRecord(List<ProcInspectionRecord> addInspectionRecordInfo){
        procInspectionRecordService.insertInspectionRecordBatch(addInspectionRecordInfo);
    }


    /**
     * 新增销障工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 20:38
     * @param addProcClearFailure 销障工单记录参数
     */
    public void addProcClearFailure(List<ProcClearFailure> addProcClearFailure) {
        procClearFailureService.insertBatch(addProcClearFailure);
    }
}
