package com.fiberhome.filink.workflowbusinessserver.service.impl.procbase.procrelated;

import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.procrelated.LogicDeleteRelatedDeviceBatch;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.procrelated.LogicDeleteRelatedDeviceRecordBatch;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.dao.procbase.ProcBaseDao;
import com.fiberhome.filink.workflowbusinessserver.exception.FilinkObtainDeptInfoException;
import com.fiberhome.filink.workflowbusinessserver.exception.FilinkProcBaseException;
import com.fiberhome.filink.workflowbusinessserver.exception.FilinkWorkflowBusinessDataException;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.procrelated.ProcRelatedService;
import com.fiberhome.filink.workflowbusinessserver.service.procclear.ProcClearFailureService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionRecordService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionService;
import com.fiberhome.filink.workflowbusinessserver.utils.common.CastListUtil;
import com.fiberhome.filink.workflowbusinessserver.utils.common.ProcBaseUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * 工单关联逻辑处理
 * @author hedongwei@wistronits.com
 * @date 2019/4/24 11:28
 */
@Service
public class ProcRelatedServiceImpl implements ProcRelatedService {


    @Autowired
    private ProcInspectionService procInspectionService;

    @Autowired
    private ProcClearFailureService procClearFailureService;

    @Autowired
    private ProcBaseDao procBaseDao;

    @Autowired
    private ProcInspectionRecordService procInspectionRecordService;

    @Autowired
    private DepartmentFeign departmentFeign;


    /**
     * 获取工单关联部门名称
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 9:09
     * @param processInfo 工单信息
     * @return 关联工单关联部门名称
     */
    @Override
    public String getRelatedDepartmentName(ProcessInfo processInfo) {
        String departmentName = "";
        if (!ObjectUtils.isEmpty(processInfo.getProcRelatedDepartments())) {
            //取出需要查询的责任单位编号集合
            List<String> departmentIdList = new ArrayList<>();
            for (ProcRelatedDepartment  department : processInfo.getProcRelatedDepartments()) {
                departmentIdList.add(department.getAccountabilityDept());
            }
            departmentName = this.getDepartmentName(departmentIdList);
        }
        return departmentName;
    }

    /**
     * 获取部门名称
     * @author hedongwei@wistronits.com
     * @date  2019/6/24 14:24
     * @param departmentIdList 部门编号集合
     * @return 获取部门名称
     */
    @Override
    public String getDepartmentName(List<String> departmentIdList) {
        String departmentName = "";
        List<Department> departmentList = departmentFeign.queryDepartmentFeignById(departmentIdList);
        if (!ObjectUtils.isEmpty(departmentList)) {
            for (Department department : departmentList) {
                if (!"".equals(departmentName)) {
                    departmentName += ",";
                }
                departmentName += department.getDeptName();
            }
        } else {
            throw new FilinkObtainDeptInfoException(I18nUtils.getSystemString(ProcBaseI18n.FAILED_TO_OBTAIN_DEPARTMENT_INFORMATION));
        }
        return departmentName;
    }

    /**
     * 修改工单关联信息isDeleted状态
     *
     * @param procBaseReqList 工单基础类
     * @return void
     */
    @Override
    public void updateProcRelateIsDeletedBatch(List<ProcBaseReq> procBaseReqList, String isDeleted) {
        List<String> procIds = new ArrayList<>();
        ProcBaseReq procBaseReqInfo = new ProcBaseReq();
        //巡检工单编号集合
        Set<String> inspectionProcIdList = new HashSet<>();
        //销障工单编号集合
        Set<String>  clearProcIdList = new HashSet<>();
        if (!ObjectUtils.isEmpty(procBaseReqList)) {
            for (ProcBaseReq procBaseReqOne : procBaseReqList) {
                if (!ObjectUtils.isEmpty(procBaseReqOne)){
                    if (!ObjectUtils.isEmpty(procBaseReqOne.getProcId())) {
                        if (ProcBaseConstants.PROC_INSPECTION.equals(procBaseReqOne.getProcType())) {
                            //巡检工单集合
                            inspectionProcIdList.add(procBaseReqOne.getProcId());
                        } else if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBaseReqOne.getProcType())) {
                            //销障工单集合
                            clearProcIdList.add(procBaseReqOne.getProcId());
                        }
                        procIds.add(procBaseReqOne.getProcId());
                    }
                    procBaseReqInfo = procBaseReqOne;
                }
            }
        }

        if (!ObjectUtils.isEmpty(procIds)) {
            ProcBaseReq procBaseReqParam = new ProcBaseReq();
            BeanUtils.copyProperties(procBaseReqInfo, procBaseReqParam);
            procBaseReqParam.setProcId(null);
            String isDeletedSearch = ProcBaseUtil.getIsDeletedSearchParam(isDeleted);
            procBaseReqParam.setIsDeleted(isDeletedSearch);
            //删除工单关联信息
            this.deleteProcRelatedByProcBaseReq(inspectionProcIdList, clearProcIdList, procBaseReqParam, isDeleted, isDeletedSearch);
        }
    }

    /**
     * 删除工单关联信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/22 16:39
     * @param inspectionProcIdList 巡检工单编号集合
     * @param clearProcIdList 销障工单编号集合
     * @param procBaseReqParam  工单参数
     * @param isDeleted 是否删除
     * @param isDeletedSearch 查询删除条件
     */
    public void deleteProcRelatedByProcBaseReq(Set<String> inspectionProcIdList, Set<String>  clearProcIdList,
                                               ProcBaseReq procBaseReqParam, String isDeleted, String isDeletedSearch) {
        if (!ObjectUtils.isEmpty(inspectionProcIdList)) {
            ProcBaseReq procBaseReqInspectionParam = new ProcBaseReq();
            BeanUtils.copyProperties(procBaseReqParam, procBaseReqInspectionParam);
            procBaseReqInspectionParam.setProcIds(inspectionProcIdList);

            List<String> inspectionList = new ArrayList<>();
            inspectionList.addAll(inspectionProcIdList);

            //获取工单关联设施信息
            List<ProcRelatedDevice> deviceCounts = procBaseDao.queryProcRelateDevice(procBaseReqInspectionParam);

            //获取巡检记录信息
            List<ProcInspectionRecord> recordList = procInspectionRecordService.queryInspectionRecord(isDeletedSearch, inspectionList);

            procBaseReqInspectionParam.setIsDeleted(isDeleted);
            // 删除工单关联设施信息
            int deviceCount = procBaseDao.updateProcRelateDeviceIsDeletedByProcIds(procBaseReqInspectionParam);
            //如果删除条数不一致
            if (deviceCount != deviceCounts.size()){
                new FilinkProcBaseException(I18nUtils.getSystemString(ProcBaseI18n.DELETE_PROC_RELATED_DEVICE_FAIL));
            }

            //逻辑删除关联工单信息
            int recordCount = procInspectionRecordService.logicDeleteRecordByProcIds(isDeleted, inspectionList);
            //如果删除条数不一致
            if (recordCount != recordList.size()) {
                //提示工单删除巡检记录失败
                new FilinkProcBaseException(I18nUtils.getSystemString(ProcBaseI18n.DELETE_PROC_RELATED_RECORD_FAIL));
            }

        }

        if (!ObjectUtils.isEmpty(clearProcIdList)) {
            ProcBaseReq procBaseReqProcClearParam = new ProcBaseReq();
            BeanUtils.copyProperties(procBaseReqParam, procBaseReqProcClearParam);
            procBaseReqProcClearParam.setProcIds(clearProcIdList);

            //获取工单关联部门信息
            List<ProcRelatedDepartment> unitCounts = procBaseDao.queryProcRelateDept(procBaseReqProcClearParam);
            procBaseReqProcClearParam.setIsDeleted(isDeleted);

            // 删除工单关联部门信息
            int unitCount = procBaseDao.updateProcRelateDeptIsDeletedByProcIds(procBaseReqProcClearParam);
            //如果删除条数不一致
            if (unitCount != unitCounts.size()){
                new FilinkProcBaseException(I18nUtils.getSystemString(ProcBaseI18n.DELETE_PROC_RELATED_UNIT_FAIL));
            }
        }
    }

    /**
     * 删除/恢复工单特性信息
     *
     * @param procBaseReqList 工单基础类
     * @return void
     */
    @Override
    public void updateProcSpecificIsDeletedBatch(List<ProcBaseReq> procBaseReqList, String isDeleted) {
        List<String> deleteInspectionProcIds = new ArrayList<>();
        List<String> deleteProcClearFailure = new ArrayList<>();
        if (!ObjectUtils.isEmpty(procBaseReqList)) {
            for (ProcBaseReq procBaseReq : procBaseReqList) {
                if (!ObjectUtils.isEmpty(procBaseReq)){
                    if (!StringUtils.isEmpty(procBaseReq.getProcType())){
                        if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBaseReq.getProcType())){
                            deleteProcClearFailure.add(procBaseReq.getProcId());
                        } else if (ProcBaseConstants.PROC_INSPECTION.equals(procBaseReq.getProcType())){
                            //删除巡检编号
                            deleteInspectionProcIds.add(procBaseReq.getProcId());
                        }
                    }
                }
            }
        }

        //删除销障工单
        if (!ObjectUtils.isEmpty(deleteProcClearFailure)) {
            ProcBaseReq procBaseReq = new ProcBaseReq();
            Set<String> procIdSet = new HashSet<>();
            procIdSet.addAll(deleteProcClearFailure);
            procBaseReq.setProcIds(procIdSet);
            procBaseReq.setIsDeleted(isDeleted);
            //销障工单
            procClearFailureService.updateProcClearFailureSpecificIsDeletedBatch(procBaseReq);
        }

        //删除巡检工单
        if (!ObjectUtils.isEmpty(deleteInspectionProcIds)) {
            //删除巡检工单
            procInspectionService.logicDeleteProcInspection(isDeleted, deleteInspectionProcIds);

        }
    }


    /**
     * 删除关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 14:27
     * @param procRelatedDeviceList 关联设施集合
     * @param isDeleted 是否删除
     */
    @Override
    public void deleteRelatedDeviceList(List<ProcRelatedDevice> procRelatedDeviceList, String isDeleted) {
        if (!ObjectUtils.isEmpty(procRelatedDeviceList)) {
            LogicDeleteRelatedDeviceBatch relatedDeviceBatch = new LogicDeleteRelatedDeviceBatch();
            List<String> relatedIdList = CastListUtil.getRelatedDeviceIdList(procRelatedDeviceList);
            relatedDeviceBatch.setRelatedDeviceList(relatedIdList);
            relatedDeviceBatch.setIsDeleted(isDeleted);
            int row = procBaseDao.logicDeleteRelatedDevice(relatedDeviceBatch);
            if (row <= 0) {
                throw new FilinkWorkflowBusinessDataException();
            }
        }
    }

    /**
     * 删除关联巡检设施记录
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 14:32
     * @param inspectionRecordList 巡检记录集合
     * @param procMap 工单map
     * @param isDeleted 是否删除
     */
    @Override
    public void deleteRelatedInspectionDeviceRecord(List<ProcInspectionRecord> inspectionRecordList, Map<String, String> procMap, String isDeleted) {
        if (!ObjectUtils.isEmpty(inspectionRecordList)) {
            //过滤需要的工单信息
            inspectionRecordList = CastListUtil.filterAbleProcToRecord(inspectionRecordList, procMap);
            if (!ObjectUtils.isEmpty(inspectionRecordList)) {
                LogicDeleteRelatedDeviceRecordBatch recordBatchDelete = new LogicDeleteRelatedDeviceRecordBatch();
                List<String> recordIdList = CastListUtil.getInspectionRecordIdList(inspectionRecordList);
                recordBatchDelete.setRelatedDeviceList(recordIdList);
                recordBatchDelete.setIsDeleted(isDeleted);
                //逻辑删除记录表
                int row = procInspectionRecordService.logicDeleteRelatedDeviceRecord(recordBatchDelete);
                if (row <= 0) {
                    throw new FilinkWorkflowBusinessDataException();
                }
            }
        }
    }
}
