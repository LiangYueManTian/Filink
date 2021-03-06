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
 * ????????????????????????
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
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 9:09
     * @param processInfo ????????????
     * @return ??????????????????????????????
     */
    @Override
    public String getRelatedDepartmentName(ProcessInfo processInfo) {
        String departmentName = "";
        if (!ObjectUtils.isEmpty(processInfo.getProcRelatedDepartments())) {
            //?????????????????????????????????????????????
            List<String> departmentIdList = new ArrayList<>();
            for (ProcRelatedDepartment  department : processInfo.getProcRelatedDepartments()) {
                departmentIdList.add(department.getAccountabilityDept());
            }
            departmentName = this.getDepartmentName(departmentIdList);
        }
        return departmentName;
    }

    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/6/24 14:24
     * @param departmentIdList ??????????????????
     * @return ??????????????????
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
     * ????????????????????????isDeleted??????
     *
     * @param procBaseReqList ???????????????
     * @return void
     */
    @Override
    public void updateProcRelateIsDeletedBatch(List<ProcBaseReq> procBaseReqList, String isDeleted) {
        List<String> procIds = new ArrayList<>();
        ProcBaseReq procBaseReqInfo = new ProcBaseReq();
        //????????????????????????
        Set<String> inspectionProcIdList = new HashSet<>();
        //????????????????????????
        Set<String>  clearProcIdList = new HashSet<>();
        if (!ObjectUtils.isEmpty(procBaseReqList)) {
            for (ProcBaseReq procBaseReqOne : procBaseReqList) {
                if (!ObjectUtils.isEmpty(procBaseReqOne)){
                    if (!ObjectUtils.isEmpty(procBaseReqOne.getProcId())) {
                        if (ProcBaseConstants.PROC_INSPECTION.equals(procBaseReqOne.getProcType())) {
                            //??????????????????
                            inspectionProcIdList.add(procBaseReqOne.getProcId());
                        } else if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBaseReqOne.getProcType())) {
                            //??????????????????
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
            //????????????????????????
            this.deleteProcRelatedByProcBaseReq(inspectionProcIdList, clearProcIdList, procBaseReqParam, isDeleted, isDeletedSearch);
        }
    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/6/22 16:39
     * @param inspectionProcIdList ????????????????????????
     * @param clearProcIdList ????????????????????????
     * @param procBaseReqParam  ????????????
     * @param isDeleted ????????????
     * @param isDeletedSearch ??????????????????
     */
    public void deleteProcRelatedByProcBaseReq(Set<String> inspectionProcIdList, Set<String>  clearProcIdList,
                                               ProcBaseReq procBaseReqParam, String isDeleted, String isDeletedSearch) {
        if (!ObjectUtils.isEmpty(inspectionProcIdList)) {
            ProcBaseReq procBaseReqInspectionParam = new ProcBaseReq();
            BeanUtils.copyProperties(procBaseReqParam, procBaseReqInspectionParam);
            procBaseReqInspectionParam.setProcIds(inspectionProcIdList);

            List<String> inspectionList = new ArrayList<>();
            inspectionList.addAll(inspectionProcIdList);

            //??????????????????????????????
            List<ProcRelatedDevice> deviceCounts = procBaseDao.queryProcRelateDevice(procBaseReqInspectionParam);

            //????????????????????????
            List<ProcInspectionRecord> recordList = procInspectionRecordService.queryInspectionRecord(isDeletedSearch, inspectionList);

            procBaseReqInspectionParam.setIsDeleted(isDeleted);
            // ??????????????????????????????
            int deviceCount = procBaseDao.updateProcRelateDeviceIsDeletedByProcIds(procBaseReqInspectionParam);
            //???????????????????????????
            if (deviceCount != deviceCounts.size()){
                new FilinkProcBaseException(I18nUtils.getSystemString(ProcBaseI18n.DELETE_PROC_RELATED_DEVICE_FAIL));
            }

            //??????????????????????????????
            int recordCount = procInspectionRecordService.logicDeleteRecordByProcIds(isDeleted, inspectionList);
            //???????????????????????????
            if (recordCount != recordList.size()) {
                //????????????????????????????????????
                new FilinkProcBaseException(I18nUtils.getSystemString(ProcBaseI18n.DELETE_PROC_RELATED_RECORD_FAIL));
            }

        }

        if (!ObjectUtils.isEmpty(clearProcIdList)) {
            ProcBaseReq procBaseReqProcClearParam = new ProcBaseReq();
            BeanUtils.copyProperties(procBaseReqParam, procBaseReqProcClearParam);
            procBaseReqProcClearParam.setProcIds(clearProcIdList);

            //??????????????????????????????
            List<ProcRelatedDepartment> unitCounts = procBaseDao.queryProcRelateDept(procBaseReqProcClearParam);
            procBaseReqProcClearParam.setIsDeleted(isDeleted);

            // ??????????????????????????????
            int unitCount = procBaseDao.updateProcRelateDeptIsDeletedByProcIds(procBaseReqProcClearParam);
            //???????????????????????????
            if (unitCount != unitCounts.size()){
                new FilinkProcBaseException(I18nUtils.getSystemString(ProcBaseI18n.DELETE_PROC_RELATED_UNIT_FAIL));
            }
        }
    }

    /**
     * ??????/????????????????????????
     *
     * @param procBaseReqList ???????????????
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
                            //??????????????????
                            deleteInspectionProcIds.add(procBaseReq.getProcId());
                        }
                    }
                }
            }
        }

        //??????????????????
        if (!ObjectUtils.isEmpty(deleteProcClearFailure)) {
            ProcBaseReq procBaseReq = new ProcBaseReq();
            Set<String> procIdSet = new HashSet<>();
            procIdSet.addAll(deleteProcClearFailure);
            procBaseReq.setProcIds(procIdSet);
            procBaseReq.setIsDeleted(isDeleted);
            //????????????
            procClearFailureService.updateProcClearFailureSpecificIsDeletedBatch(procBaseReq);
        }

        //??????????????????
        if (!ObjectUtils.isEmpty(deleteInspectionProcIds)) {
            //??????????????????
            procInspectionService.logicDeleteProcInspection(isDeleted, deleteInspectionProcIds);

        }
    }


    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 14:27
     * @param procRelatedDeviceList ??????????????????
     * @param isDeleted ????????????
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
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 14:32
     * @param inspectionRecordList ??????????????????
     * @param procMap ??????map
     * @param isDeleted ????????????
     */
    @Override
    public void deleteRelatedInspectionDeviceRecord(List<ProcInspectionRecord> inspectionRecordList, Map<String, String> procMap, String isDeleted) {
        if (!ObjectUtils.isEmpty(inspectionRecordList)) {
            //???????????????????????????
            inspectionRecordList = CastListUtil.filterAbleProcToRecord(inspectionRecordList, procMap);
            if (!ObjectUtils.isEmpty(inspectionRecordList)) {
                LogicDeleteRelatedDeviceRecordBatch recordBatchDelete = new LogicDeleteRelatedDeviceRecordBatch();
                List<String> recordIdList = CastListUtil.getInspectionRecordIdList(inspectionRecordList);
                recordBatchDelete.setRelatedDeviceList(recordIdList);
                recordBatchDelete.setIsDeleted(isDeleted);
                //?????????????????????
                int row = procInspectionRecordService.logicDeleteRelatedDeviceRecord(recordBatchDelete);
                if (row <= 0) {
                    throw new FilinkWorkflowBusinessDataException();
                }
            }
        }
    }
}
