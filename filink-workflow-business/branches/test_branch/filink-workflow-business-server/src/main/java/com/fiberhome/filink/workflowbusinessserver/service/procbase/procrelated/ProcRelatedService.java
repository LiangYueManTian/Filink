package com.fiberhome.filink.workflowbusinessserver.service.procbase.procrelated;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;

import java.util.List;
import java.util.Map;

/**
 * 工单关联service
 * @author hedongwei@wistronits.com
 * @date 2019/4/24 11:25
 */
public interface ProcRelatedService {

    /**
     * 获取工单关联部门名称
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 9:09
     * @param processInfo 工单信息
     * @return 关联工单关联部门名称
     */
    String getRelatedDepartmentName(ProcessInfo processInfo);

    /**
     * 获取部门名称
     * @author hedongwei@wistronits.com
     * @date  2019/6/24 14:24
     * @param departmentIdList 部门编号集合
     * @return 获取部门名称
     */
    String getDepartmentName(List<String> departmentIdList);

    /**
     * 修改工单关联信息isDeleted状态
     *
     * @param procBaseReqList 工单基础类
     * @param isDeleted 是否删除
     * @return void
     */
    void updateProcRelateIsDeletedBatch(List<ProcBaseReq> procBaseReqList, String isDeleted);


    /**
     * 删除/恢复工单特性信息
     *
     * @param procBaseReqList 工单基础类
     * @param isDeleted 是否删除
     * @return void
     */
    void updateProcSpecificIsDeletedBatch(List<ProcBaseReq> procBaseReqList, String isDeleted);

    /**
     * 删除关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 14:27
     * @param procRelatedDeviceList 关联设施集合
     * @param isDeleted 是否删除
     */
    void deleteRelatedDeviceList(List<ProcRelatedDevice> procRelatedDeviceList, String isDeleted);


    /**
     * 删除关联巡检设施记录
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 14:32
     * @param inspectionRecordList 巡检记录集合
     * @param procMap 工单map
     * @param isDeleted 是否删除
     */
    void deleteRelatedInspectionDeviceRecord(List<ProcInspectionRecord> inspectionRecordList, Map<String, String> procMap, String isDeleted);
}
