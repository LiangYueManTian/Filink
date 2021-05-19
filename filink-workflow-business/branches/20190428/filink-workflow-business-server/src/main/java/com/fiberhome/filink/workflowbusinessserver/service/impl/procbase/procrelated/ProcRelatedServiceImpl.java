package com.fiberhome.filink.workflowbusinessserver.service.impl.procbase.procrelated;

import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.procrelated.LogicDeleteRelatedDeviceBatch;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.procrelated.LogicDeleteRelatedDeviceRecordBatch;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import com.fiberhome.filink.workflowbusinessserver.dao.procbase.ProcBaseDao;
import com.fiberhome.filink.workflowbusinessserver.exception.FilinkProcBaseException;
import com.fiberhome.filink.workflowbusinessserver.exception.FilinkWorkflowBusinessDataException;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.procrelated.ProcRelatedService;
import com.fiberhome.filink.workflowbusinessserver.service.procclear.ProcClearFailureService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionRecordService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionService;
import com.fiberhome.filink.workflowbusinessserver.utils.common.CastListUtil;
import com.fiberhome.filink.workflowbusinessserver.utils.common.ProcBaseUtil;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
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
        if (!ObjectUtils.isEmpty(procBaseReqList)) {
            for (ProcBaseReq procBaseReqOne : procBaseReqList) {
                if (!ObjectUtils.isEmpty(procBaseReqOne)){
                    if (!ObjectUtils.isEmpty(procBaseReqOne.getProcId())) {
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
            Set<String> procBaseSet = new HashSet<>();
            procBaseSet.addAll(procIds);
            procBaseReqParam.setProcIds(procBaseSet);

            //获取工单关联设施信息
            List<ProcRelatedDevice> deviceCounts = procBaseDao.queryProcRelateDevice(procBaseReqParam);

            //获取工单关联部门信息
            List<ProcRelatedDepartment> unitCounts = procBaseDao.queryProcRelateDept(procBaseReqParam);

            //获取巡检记录信息
            List<ProcInspectionRecord> recordList = procInspectionRecordService.queryInspectionRecord(isDeletedSearch, procIds);


            procBaseReqParam.setIsDeleted(isDeleted);
            // 删除工单关联设施信息
            int deviceCount = procBaseDao.updateProcRelateDeviceIsDeletedByProcIds(procBaseReqParam);
            //如果删除条数不一致
            if (deviceCount != deviceCounts.size()){
                new FilinkProcBaseException(I18nUtils.getString(ProcBaseI18n.DELETE_PROC_RELATED_DEVICE_FAIL));
            }

            // 删除工单关联部门信息
            int unitCount = procBaseDao.updateProcRelateDeptIsDeletedByProcIds(procBaseReqParam);
            //如果删除条数不一致
            if (unitCount != unitCounts.size()){
                new FilinkProcBaseException(I18nUtils.getString(ProcBaseI18n.DELETE_PROC_RELATED_UNIT_FAIL));
            }

            //逻辑删除关联工单信息
            int recordCount = procInspectionRecordService.logicDeleteRecordByProcIds(isDeleted, procIds);
            //如果删除条数不一致
            if (recordCount != recordList.size()) {
                //提示工单删除巡检记录失败
                new FilinkProcBaseException(I18nUtils.getString(ProcBaseI18n.DELETE_PROC_RELATED_RECORD_FAIL));
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
