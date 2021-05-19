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
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * 新增工单数据测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/31 20:48
 */
@RunWith(JMockit.class)
public class AddProcDataTest {

    /**
     * 测试对象新增工单数据
     */
    @Tested
    private AddProcData addProcData;

    /**
     * 工单父类逻辑层
     */
    @Injectable
    private ProcBaseDao procBaseDao;

    /**
     * 巡检工单逻辑层
     */
    @Injectable
    private ProcInspectionService procInspectionService;

    /**
     * 工单巡检记录逻辑层
     */
    @Injectable
    private ProcInspectionRecordService procInspectionRecordService;

    /**
     * 销障工单逻辑层
     */
    @Injectable
    private ProcClearFailureService procClearFailureService;


    /**
     * 新增关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 20:36
     */
    @Test
    public void addProcRelatedDevice() {
        List<ProcRelatedDevice> addDeviceInfo = new ArrayList<>();
        addProcData.addProcRelatedDevice(addDeviceInfo);
    }

    /**
     * 新增关联部门
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 20:36
     */
    @Test
    public void addRelatedDept(){
        List<ProcRelatedDepartment> addDepartment = new ArrayList<>();
        addProcData.addRelatedDept(addDepartment);
    }

    /**
     * 新增巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 20:36
     */
    @Test
    public void addProcInspection(){
        List<ProcInspection> procInspectionList = new ArrayList<>();
        //批量新增巡检工单信息
        addProcData.addProcInspection(procInspectionList);
    }

    /**
     * 新增巡检工单记录信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 20:38
     */
    @Test
    public void addInspectionRecord(){
        List<ProcInspectionRecord> addInspectionRecordInfo = new ArrayList<>();
        addProcData.addInspectionRecord(addInspectionRecordInfo);
    }


    /**
     * 新增销障工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 20:38
     */
    @Test
    public void addProcClearFailure() {
        List<ProcClearFailure> addProcClearFailure = new ArrayList<>();
        addProcData.addProcClearFailure(addProcClearFailure);
    }
}
