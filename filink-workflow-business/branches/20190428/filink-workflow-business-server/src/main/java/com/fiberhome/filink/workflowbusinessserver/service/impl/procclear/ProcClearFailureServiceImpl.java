package com.fiberhome.filink.workflowbusinessserver.service.impl.procclear;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.DevicePicFeign;
import com.fiberhome.filink.deviceapi.bean.DevicePicResp;
import com.fiberhome.filink.exportapi.bean.Export;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowapi.constant.ProcessConstants;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcClearFailure;
import com.fiberhome.filink.workflowbusinessserver.bean.workflowbusiness.WorkflowBusinessI18n;
import com.fiberhome.filink.workflowbusinessserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import com.fiberhome.filink.workflowbusinessserver.dao.procclear.ProcClearFailureDao;
import com.fiberhome.filink.workflowbusinessserver.export.procclear.ClearFailureProcHistoryExport;
import com.fiberhome.filink.workflowbusinessserver.export.procclear.ClearFailureProcUnfinishedExport;
import com.fiberhome.filink.workflowbusinessserver.req.app.procclear.ReceiptClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.req.procclear.InsertClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.req.procclear.UpdateClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.req.process.CompleteProcessInfoReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procclear.ClearFailureDownLoadDetail;
import com.fiberhome.filink.workflowbusinessserver.service.impl.procbase.ProcBaseServiceImpl;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcLogService;
import com.fiberhome.filink.workflowbusinessserver.service.procclear.ProcClearFailureService;
import com.fiberhome.filink.workflowbusinessserver.service.process.WorkflowService;
import com.fiberhome.filink.workflowbusinessserver.utils.procbase.ProcBaseResultCode;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkFlowBusinessMsg;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkflowBusinessResultCode;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <p>
 * ??????????????? ???????????????
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-02-15
 */
@Service
@RefreshScope
public class ProcClearFailureServiceImpl extends ServiceImpl<ProcClearFailureDao, ProcClearFailure> implements ProcClearFailureService {

    @Autowired
    private ProcClearFailureDao procClearFailureDao;

    @Autowired
    private ProcBaseServiceImpl procBaseService;

    /**
     * ??????????????????????????????????????????
     */
    @Autowired
    private ClearFailureProcUnfinishedExport clearFailureProcUnfinishedExport;

    /**
     * ???????????????????????????????????????
     */
    @Autowired
    private ClearFailureProcHistoryExport clearFailureProcHistoryExport;

    /**
     * ????????????????????????feign
     */
    @Autowired
    private DevicePicFeign devicePicFeign;

    /**
     * ??????????????????????????????
     */
    @Autowired
    private WorkflowService workflowService;

    /**
     * ??????????????????
     */
    @Autowired
    private ProcLogService procLogService;

    /**
     * ??????????????????
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * ??????????????????
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;

    /**
     * ??????????????????
     *
     * @param insertClearFailureReq ?????????????????????
     * @return Result
     */
    @Override
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_ADD, logType = LogConstants.LOG_TYPE_OPERATE, functionCode = LogFunctionCodeConstant.INSERT_PROC_CLEAR, dataGetColumnName = "title", dataGetColumnId = "procId")
    public Result addClearFailureProc(InsertClearFailureReq insertClearFailureReq) throws Exception {
        //????????????uuid
        insertClearFailureReq.setProcId(NineteenUUIDUtils.uuid());
        //??????????????????????????????????????????????????????
        ProcessInfo processInfo = this.getProcessInfoByClearFailure(insertClearFailureReq);
        if (!ObjectUtils.isEmpty(processInfo.getProcBase())){
            processInfo = clearFailureProcParamsInit(processInfo,insertClearFailureReq);
            return procBaseService.addProcBase(processInfo);
        } else {
            return ResultUtils.warn(ProcBaseResultCode.FAIL, I18nUtils.getString(ProcBaseI18n.PROC_PARAM_ERROR));
        }
    }

    /**
     * ??????????????????
     *
     * @param updateClearFailureReq ????????????????????????
     * @return Result
     */
    @Override
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_UPDATE, logType = LogConstants.LOG_TYPE_OPERATE, functionCode = LogFunctionCodeConstant.UPDATE_PROC_CLEAR, dataGetColumnName = "title", dataGetColumnId = "procId")
    public Result updateClearFailureProcById(UpdateClearFailureReq updateClearFailureReq) throws Exception {
        //?????????????????????????????????
        ProcessInfo processInfo = this.getProcessInfoByClearFailure(updateClearFailureReq);
        if (!ObjectUtils.isEmpty(processInfo.getProcBase())){
            processInfo = clearFailureProcParamsInit(processInfo,updateClearFailureReq);
            return procBaseService.updateProcessById(processInfo);
        } else {
            return ResultUtils.warn(ProcBaseResultCode.FAIL, I18nUtils.getString(ProcBaseI18n.PROC_PARAM_ERROR));
        }
    }

    /**
     * ???????????????????????????
     *
     * @param processInfo ??????????????????
     * @param object ???????????????????????????
     *
     * @return processInfo ??????????????????
     */
    public ProcessInfo clearFailureProcParamsInit(ProcessInfo processInfo, Object object) throws Exception {
        processInfo.getProcBase().setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        processInfo.getProcBase().setStatus(ProcBaseConstants.PROC_STATUS_ASSIGNED);
        //????????????
        List<ProcRelatedDepartment> deptList = (List<ProcRelatedDepartment>)PropertyUtils.getProperty(object,"accountabilityDeptList");
        ProcBase procBase = ProcBase.setDeptTypeAndStatus(deptList, processInfo.getProcBase());
        processInfo.setProcBase(procBase);
        return processInfo;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param exportDto ??????????????????
     * @return ??????????????????
     */
    @Override
    public Result exportClearFailureProcUnfinished(ExportDto exportDto) {
        Export export = null;
        try {
            export = clearFailureProcUnfinishedExport.insertTask(exportDto, WorkFlowBusinessConstants.SERVER_NAME, I18nUtils.getString(ProcBaseI18n.PROC_CLEAR_FAILURE_UNFINISHED_LIST));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(WorkflowBusinessResultCode.EXPORT_NO_DATA, I18nUtils.getString(WorkflowBusinessI18n.EXPORT_NO_DATA));

        } catch (FilinkExportDataTooLargeException fe) {
            return WorkFlowBusinessMsg.getExportToLargeMsg(fe, maxExportDataSize);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(ProcBaseResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getString(ProcBaseI18n.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(ProcBaseResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getString(ProcBaseI18n.FAILED_TO_CREATE_EXPORT_TASK));
        }
        clearFailureProcUnfinishedExport.exportData(export);

        //????????????
        procLogService.addLogByExport(exportDto, LogFunctionCodeConstant.PROC_CLEAR_FAILURE_UNFINISHED_EXPORT_CODE);

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(ProcBaseI18n.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param exportDto ??????????????????
     * @return ??????????????????
     */
    @Override
    public Result exportClearFailureProcHistory(ExportDto exportDto) {
        Export export = null;
        try {
            export = clearFailureProcHistoryExport.insertTask(exportDto, WorkFlowBusinessConstants.SERVER_NAME, I18nUtils.getString(ProcBaseI18n.PROC_CLEAR_FAILURE_HISTORY_LIST));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(WorkflowBusinessResultCode.EXPORT_NO_DATA, I18nUtils.getString(WorkflowBusinessI18n.EXPORT_NO_DATA));

        } catch (FilinkExportDataTooLargeException fe) {
            return WorkFlowBusinessMsg.getExportToLargeMsg(fe, maxExportDataSize);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(ProcBaseResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getString(ProcBaseI18n.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(ProcBaseResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getString(ProcBaseI18n.FAILED_TO_CREATE_EXPORT_TASK));
        }
        clearFailureProcHistoryExport.exportData(export);

        //????????????
        procLogService.addLogByExport(exportDto, LogFunctionCodeConstant.PROC_CLEAR_FAILURE_HIS_EXPORT_CODE);

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(ProcBaseI18n.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * ???????????????????????????????????????
     *
     * @param queryCondition ???????????????
     * @return Result
     */
    @Override
    public Result queryListClearFailureUnfinishedProcByPage(QueryCondition<ProcBaseReq> queryCondition) {
        //?????????????????????????????????
        this.setProcTypeToClearFailure(queryCondition);
        return procBaseService.queryListProcUnfinishedProcByPage(queryCondition);
    }

    /**
     * ????????????????????????????????????
     *
     * @param queryCondition ???????????????
     * @return Result
     */
    @Override
    public Result queryListClearFailureHisProcByPage(QueryCondition<ProcBaseReq> queryCondition) {
        //?????????????????????????????????
        this.setProcTypeToClearFailure(queryCondition);
        return procBaseService.queryListProcHisProcByPage(queryCondition);
    }

    /**
     * ??????????????????????????????
     *
     * @param procClearFailure ????????????????????????
     * @return int
     */
    @Override
    public int saveProcClearFailureSpecific(ProcClearFailure procClearFailure) {
        String userId = RequestInfoUtils.getUserId();
        //?????????????????????
        if (!StringUtils.isEmpty(procClearFailure.getProcClearFailureId())){
            procClearFailure.setUpdateUser(userId);
            procClearFailure.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
            return procClearFailureDao.updateProcClearFailureSpecificByProcId(procClearFailure);
        } else {
            procClearFailure.setCreateUser(userId);
            procClearFailure.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
            return procClearFailureDao.addProcClearFailureSpecific(procClearFailure);
        }
    }

    /**
     * ??????/??????????????????????????????
     *
     * @param procId ??????id
     * @param isDeleted ??????????????????
     *
     * @return int
     */
    @Override
    public int updateProcClearFailureSpecificIsDeleted(String procId,String isDeleted) {
        return procClearFailureDao.updateProcClearFailureSpecificIsDeleted(procId,isDeleted);
    }

    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 11:51
     * @param procBaseReq ????????????????????????
     * @return ????????????????????????
     */
    @Override
    public int updateProcClearFailureSpecificIsDeletedBatch(ProcBaseReq procBaseReq) {
        return procClearFailureDao.updateProcClearFailureSpecificIsDeletedBatch(procBaseReq);
    }

    /**
     * ??????????????????????????????
     *
     * @param procIds ??????ids
     * @return List<ProcClearFailure>
     */
    @Override
    public List<ProcClearFailure> queryProcClearFailureSpecific(Set<String> procIds){
        return procClearFailureDao.queryProcClearFailureSpecific(procIds);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @return Result
     */
    @Override
    public Result queryCountListProcUnfinishedProcStatus(){
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition();
        //?????????????????????????????????
        this.setProcTypeToClearFailure(queryCondition);
        return procBaseService.queryCountListProcUnfinishedProcStatus(queryCondition);
    }

    /**
     * ???????????????????????????????????????
     *
     * @return Result
     */
    @Override
    public Result queryCountClearFailureProcByAssigned() {
        //???????????????????????????
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procBaseReq.setStatus(ProcBaseConstants.PROC_STATUS_ASSIGNED);
        procBaseReq.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        return procBaseService.queryCountProcByStatus(procBaseReq);
    }

    /**
     * ???????????????????????????????????????
     *
     * @return Result
     */
    @Override
    public Result queryCountClearFailureProcByPending() {
        //???????????????????????????
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procBaseReq.setStatus(ProcBaseConstants.PROC_STATUS_PENDING);
        procBaseReq.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        return procBaseService.queryCountProcByStatus(procBaseReq);
    }

    /**
     * ???????????????????????????????????????
     *
     * @return Result
     */
    @Override
    public Result queryCountClearFailureProcByProcessing() {
        //???????????????????????????
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procBaseReq.setStatus(ProcBaseConstants.PROC_STATUS_PROCESSING);
        procBaseReq.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        return procBaseService.queryCountProcByStatus(procBaseReq);
    }

    /**
     * ??????????????????????????????????????????
     *
     * @return Result
     */
    @Override
    public Result queryCountClearFailureProcByToday(){
        //????????????????????????
        ProcBaseReq procBaseReq = new ProcBaseReq();
        this.getTodayStartAndEndTime(procBaseReq);
        procBaseReq.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        return procBaseService.queryCountProcByStatus(procBaseReq);
    }

    /**
     * ???????????????????????????????????????
     *
     * @return Result
     */
    @Override
    public Result queryListClearFailureGroupByErrorReason(){
        //????????????
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procBaseReq.setGroupField(ProcBaseConstants.PROC_GROUP_ERROR_REASON);
        return procBaseService.queryCountProcByGroup(procBaseReq);
    }

    /**
     * ???????????????????????????????????????
     *
     * @return Result
     */
    @Override
    public Result queryListClearFailureGroupByProcessingScheme(){
        //????????????
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procBaseReq.setGroupField(ProcBaseConstants.PROC_GROUP_PROCESSING_SCHEME);
        return procBaseService.queryCountProcByGroup(procBaseReq);
    }

    /**
     * ????????????????????????
     * @author chaofanrong@wistronits.com
     * @date  2019/4/16 18:19
     * @param insertClearFailureReq ????????????????????????
     * @return ??????????????????????????????
     */
    @Override
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_ADD, logType = LogConstants.LOG_TYPE_OPERATE, functionCode = LogFunctionCodeConstant.REGENERATE_PROC_CLEAR_FAILURE_FUNCTION_CODE, dataGetColumnName = "title", dataGetColumnId = "procId")
    public Result regenerateClearFailureProc(InsertClearFailureReq insertClearFailureReq) throws Exception {
        //????????????uuid
        insertClearFailureReq.setProcId(NineteenUUIDUtils.uuid());
        //??????????????????????????????????????????????????????
        ProcessInfo processInfo = this.getProcessInfoByClearFailure(insertClearFailureReq);
        if (!ObjectUtils.isEmpty(processInfo.getProcBase())){
            processInfo = clearFailureProcParamsInit(processInfo,insertClearFailureReq);

            //?????????????????????????????????
            processInfo.setOperaType(ProcBaseConstants.OPERATOR_TYPE_REGENERATE);

            //???????????????
            ProcBase procBase = procBaseService.selectById(processInfo.getProcBase().getRegenerateId());
            if (!ObjectUtils.isEmpty(procBase)){
                //??????????????????
                if (ProcBaseConstants.IS_CHECK_SINGLE_BACK.equals(procBase.getIsCheckSingleBack())){
                    //??????????????????????????????
                    return procBaseService.addProcBase(processInfo);
                } else {
                    //??????????????????
                    return procBaseService.regenerateProc(processInfo);
                }
            } else {
                return ResultUtils.warn(ProcBaseResultCode.PROC_NOT_EXIST,I18nUtils.getString(ProcBaseI18n.PROC_IS_NOT_EXIST));
            }
        } else {
            return ResultUtils.warn(ProcBaseResultCode.FAIL, I18nUtils.getString(ProcBaseI18n.PROC_PARAM_ERROR));
        }
    }

    /**
     * ??????????????????
     *
     * @param deviceId ??????id
     *
     * @return Result
     */
    @Override
    public Result queryClearFailureProcTopFive(String deviceId) {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();

        SortCondition sortCondition = new SortCondition();
        sortCondition.setSortField("createTime");
        sortCondition.setSortRule("DESC");
        queryCondition.setSortCondition(sortCondition);

        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(5);
        pageCondition.setPageNum(1);
        queryCondition.setPageCondition(pageCondition);

        //?????????????????????
        ProcBaseReq procBaseReq = new ProcBaseReq();
        Set<String> deviceIds = new HashSet<>();
        deviceIds.add(deviceId);
        procBaseReq.setDeviceIds(deviceIds);
        queryCondition.setBizCondition(procBaseReq);

        //?????????????????????????????????
        this.setProcTypeToClearFailure(queryCondition);

        List<ProcBaseResp> procBaseRespList = (List<ProcBaseResp>)procBaseService.queryListProcUnfinishedProcByPage(queryCondition).getData();
        //????????????ids
        List<String> procIds = new ArrayList<>();
        for (ProcBaseResp procBaseResp : procBaseRespList){
            if (!procIds.contains(procBaseResp.getProcId())){
                procIds.add(procBaseResp.getProcId());
            }
        }
        //???????????????????????????
        if (ObjectUtils.isEmpty(procIds)){
            return ResultUtils.success(procBaseRespList);
        }
        //????????????????????????
        List<Object> objects = (List<Object>)devicePicFeign.getPicUrlByProcIds(procIds).getData();
        if (null == objects){
            return ResultUtils.warn(ProcBaseResultCode.FAIL,I18nUtils.getString(ProcBaseI18n.DEVICE_PIC_SERVER_ERROR));
        }
        List<DevicePicResp> devicePicRespList = new ArrayList<>();
        for (Object object : objects){
            DevicePicResp devicePicResp = JSONArray.toJavaObject((JSON)JSONArray.toJSON(object),DevicePicResp.class);
            devicePicRespList.add(devicePicResp);
        }
        //??????????????????????????????
        Map<String,List<DevicePicResp>> devicePicRespListMaps = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        List<DevicePicResp> devicePicRespListTemp;
        for (DevicePicResp devicePicResp : devicePicRespList){
            if (devicePicRespListMaps.containsKey(devicePicResp.getResourceId())){
                devicePicRespListTemp = devicePicRespListMaps.get(devicePicResp.getResourceId());
            } else {
                devicePicRespListTemp = new ArrayList<>();
            }
            devicePicRespListTemp.add(devicePicResp);
            devicePicRespListMaps.put(devicePicResp.getResourceId(),devicePicRespListTemp);
        }
        for (ProcBaseResp procBaseResp : procBaseRespList){
            procBaseResp.setDevicePicRespList(devicePicRespListMaps.get(procBaseResp.getProcId()));
        }

        return ResultUtils.success(procBaseRespList);
    }

    /**
     * app??????????????????
     *
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result receiptClearFailureProcForApp(ReceiptClearFailureReq receiptClearFailureReq) {
        if (ObjectUtils.isEmpty(receiptClearFailureReq)){
            return ResultUtils.warn(ProcBaseResultCode.FAIL,I18nUtils.getString(ProcBaseI18n.PROC_PARAM_ERROR));
        }
        //????????????????????????id
        if (StringUtils.isEmpty(receiptClearFailureReq.getProcId())){
            return ResultUtils.warn(ProcBaseResultCode.PROC_ID_LOSE,I18nUtils.getString(ProcBaseI18n.PROC_ID_LOSE));
        }
        //????????????????????????
        ProcBase procBase = (ProcBase)procBaseService.queryProcessById(receiptClearFailureReq.getProcId()).getData();
        if (ObjectUtils.isEmpty(procBase) || StringUtils.isEmpty(procBase.getProcId())){
            return ResultUtils.warn(ProcBaseResultCode.PROC_NOT_EXIST,I18nUtils.getString(ProcBaseI18n.PROC_IS_NOT_EXIST));
        }
        //????????????
        procBase.setErrorReason(receiptClearFailureReq.getErrorReason());
        procBase.setErrorUserDefinedReason(receiptClearFailureReq.getUserDefinedErrorReason());

        //????????????
        procBase.setProcessingScheme(receiptClearFailureReq.getProcessingScheme());
        procBase.setProcessingUserDefinedScheme(receiptClearFailureReq.getUserDefinedProcessingScheme());

        //???????????????
        procBase.setUpdateUser(RequestInfoUtils.getUserId());

        //??????????????????
        procBase.setRealityCompletedTime(new Date());

        if (1 != procBaseService.receiptProc(procBase)){
            return ResultUtils.warn(ProcBaseResultCode.FAIL, I18nUtils.getString(ProcBaseI18n.PROC_RECEIPT_FAIL));
        }

        //??????????????????
        this.commitProcClearFailure(procBase);

        //??????????????????
        String functionCode = LogFunctionCodeConstant.RETURN_CLEAR_FAILURE_FUNCTION_CODE;
        //??????????????????
        String dataType = LogConstants.DATA_OPT_TYPE_UPDATE;
        //??????app????????????
        AddLogBean addLogBean = procLogService.getAddProcOperateLogParamForApp(procBase, functionCode, dataType);
        //??????????????????
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);

        //??????
        return ResultUtils.success(ProcBaseResultCode.SUCCESS, I18nUtils.getString(ProcBaseI18n.PROC_RECEIPT_SUCCESS));
    }

    /**
     * ??????????????????
     * @author chaofanrong@wistronits.com
     * @date  2019/4/16 14:28
     * @param procBase ????????????
     * @return ????????????????????????
     */
    public Result commitProcClearFailure(ProcBase procBase) {
        procBase.setStatus(ProcBaseConstants.PROC_STATUS_PROCESSING);
        CompleteProcessInfoReq completeProcessReq = new CompleteProcessInfoReq();
        BeanUtils.copyProperties(procBase, completeProcessReq);

        //??????????????????
        completeProcessReq.setOperation(ProcessConstants.PROC_OPERATION_COMPLETE);

        //????????????
        Result result = workflowService.completeProcess(completeProcessReq);
        return result;
    }

    /**
     * app??????????????????
     *
     * @param procBaseRespForApps ??????????????????
     *
     * @return List<ClearFailureDownLoadDetail>
     */
    @Override
    public List<ClearFailureDownLoadDetail> downLoadClearFailureProcForApp(List<ProcBaseRespForApp> procBaseRespForApps){
        List<ClearFailureDownLoadDetail> clearFailureDownLoadDetails = new ArrayList<>();
        for (ProcBaseRespForApp procBaseRespForApp : procBaseRespForApps){
            ClearFailureDownLoadDetail clearFailureDownLoadDetail = new ClearFailureDownLoadDetail();
            //????????????
            BeanUtils.copyProperties(procBaseRespForApp,clearFailureDownLoadDetail);
            //????????????????????????
            if (!ObjectUtils.isEmpty(procBaseRespForApp.getProcRelatedDeviceRespList())){
                BeanUtils.copyProperties(procBaseRespForApp.getProcRelatedDeviceRespList().get(0),clearFailureDownLoadDetail);
                clearFailureDownLoadDetail.setProcId(procBaseRespForApp.getProcId());
            }
            //??????????????????
            if (!ObjectUtils.isEmpty(procBaseRespForApp.getCreateTime())) {
                clearFailureDownLoadDetail.setStartTime(procBaseRespForApp.getCreateTime().getTime());
            }

            //??????????????????
            if (!ObjectUtils.isEmpty(procBaseRespForApp.getExpectedCompletedTime())) {
                clearFailureDownLoadDetail.setEndTime(procBaseRespForApp.getExpectedCompletedTime().getTime());
            }

            //????????????
            clearFailureDownLoadDetail.setAlarmTime(procBaseRespForApp.getRefAlarmTime());

            clearFailureDownLoadDetails.add(clearFailureDownLoadDetail);
        }

        return clearFailureDownLoadDetails;
    }

    /**
     * ???????????????????????????????????????
     *
     * @return Result
     */
    @Override
    public Result queryListClearFailureGroupByDeviceType(){
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition();
        //?????????????????????????????????
        this.setProcTypeToClearFailure(queryCondition);
        return procBaseService.queryListGroupByDeviceType(queryCondition);
    }

    /**
     * ???????????????????????????????????????
     *
     * @return Result
     */
    @Override
    public Result queryListClearFailureByStatus(){
        Map<String,Integer> statusMap = new HashMap<>(64);
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition();
        //????????????
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procBaseReq.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        // ??????????????????????????????
        queryCondition.setBizCondition(procBaseReq);
        statusMap.put(ProcBaseConstants.PROC_HIS_TOTAL_COUNT,(Integer)this.queryCountListProcHisProc().getData());
        //???????????????????????????
        procBaseReq.setStatus(ProcBaseConstants.PROC_STATUS_COMPLETED);
        statusMap.put(ProcBaseConstants.PROC_STATUS_COMPLETED,(Integer)procBaseService.queryCountProcByStatus(procBaseReq).getData());
        //???????????????????????????
        procBaseReq.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
        procBaseReq.setIsCheckSingleBack(ProcBaseConstants.IS_CHECK_SINGLE_BACK);
        statusMap.put(ProcBaseConstants.PROC_STATUS_SINGLE_BACK,(Integer)procBaseService.queryCountProcByStatus(procBaseReq).getData());
        return ResultUtils.success(ProcBaseResultCode.SUCCESS, I18nUtils.getString(ProcBaseI18n.QUERY_PROC_STATUS_COUNT_SUCCESS),statusMap);
    }

    /**
     * ????????????????????????????????????
     *
     * @return Result
     */
    @Override
    public Result queryCountListProcHisProc(){
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition();
        //?????????????????????????????????
        this.setProcTypeToClearFailure(queryCondition);
        return procBaseService.queryCountListProcHisProc(queryCondition);
    }

    /*-----------------------------------------------------------------------?????????????????????-----------------------------------------------------------------------*/
    /**
     * ?????????????????????????????????
     *
     * @param queryCondition ???????????????
     * @return QueryCondition<ProcBaseReq>
     */
    public QueryCondition<ProcBaseReq> setProcTypeToClearFailure(QueryCondition<ProcBaseReq> queryCondition){
        ProcBaseReq procBaseReq = (ProcBaseReq)queryCondition.getBizCondition();
        if (ObjectUtils.isEmpty(procBaseReq)){
            procBaseReq = new ProcBaseReq();
        }
        procBaseReq.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        queryCondition.setBizCondition(procBaseReq);
        SortCondition sortCondition = queryCondition.getSortCondition();
        if (ObjectUtils.isEmpty(sortCondition)){
            sortCondition = new SortCondition();
        }
        queryCondition.setSortCondition(sortCondition);
        return queryCondition;
    }

    /**
     * ?????????????????????????????????
     *
     * @param procBaseReq ???????????????
     * @return ProcBaseReq
     */
    private ProcBaseReq getTodayStartAndEndTime(ProcBaseReq procBaseReq){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //??????????????????
        LocalDateTime nowTime = LocalDateTime.now();
        //??????????????????
        LocalDate nowDate = LocalDate.now();
        //????????????
        LocalDateTime beginTime = LocalDateTime.of(nowDate, LocalTime.MIN);
        //????????????????????????
        String time1= beginTime.format(dtf);
        //???????????????????????????
        LocalDateTime endTime = LocalDateTime.of(nowDate,LocalTime.MAX);
        //????????????????????????
        String time2 =dtf.format(endTime);
        procBaseReq.setTodayStartTime(time1);
        procBaseReq.setTodayEndTime(time2);
        return procBaseReq;
    }

    /**
     * ?????????????????????
     *
     * @param processInfo ???????????????
     * @return Result
     */
    @Override
    public Result checkProcParamsForClearFailure(ProcessInfo processInfo){
        //?????????????????????
        ProcBaseReq procBaseReq = new ProcBaseReq();
        BeanUtils.copyProperties(processInfo.getProcBase(),procBaseReq);
        processInfo.setProcBaseReq(procBaseReq);
        // ??????????????????
        if (this.checkProcParamsLengthForClearFailure(processInfo)) {
            return ResultUtils.warn(ProcBaseResultCode.PROC_PARAM_ERROR, I18nUtils.getString(ProcBaseI18n.PROC_PARAM_ERROR));
        }

        // ?????????????????????????????????
        if (procBaseService.queryExistsProcForAlarm(processInfo.getProcBase(),processInfo.getOperaType())){
            return ResultUtils.warn(ProcBaseResultCode.IS_EXISTS_PROC_FOR_ALARM, I18nUtils.getString(ProcBaseI18n.IS_EXISTS_PROC_FOR_ALARM));
        }
        return ResultUtils.success(ProcBaseResultCode.SUCCESS);
    }

    /**
     * ??????????????????????????????
     *
     * @param processInfo ???????????????
     * @return boolean
     */
    private boolean checkProcParamsLengthForClearFailure(ProcessInfo processInfo) {
        //????????????
        //??????????????????
        if (StringUtils.isEmpty(processInfo.getProcBase().getTitle()) ||
                //??????????????????
                StringUtils.isEmpty(processInfo.getProcBase().getProcType())||
                //????????????????????????
                StringUtils.isEmpty(processInfo.getProcBase().getRefAlarm()) ||

                //?????????????????????????????????
                ObjectUtils.isEmpty(processInfo.getProcRelatedDevices()) ||

                //??????????????????????????????
                ObjectUtils.isEmpty(processInfo.getProcBase().getExpectedCompletedTime())
        ) {
            return true;
        } else {
            //????????????????????????
            String procName = CheckInputString.nameCheck(processInfo.getProcBase().getTitle());
            if (StringUtils.isEmpty(procName)){
                return true;
            }
            processInfo.getProcBase().setTitle(procName);
            processInfo.getProcBaseReq().setTitle(procName);

            //????????????????????????
            if (!StringUtils.isEmpty(processInfo.getProcBase().getRemark())){
                String remark = CheckInputString.markCheck(processInfo.getProcBase().getRemark());
                if (StringUtils.isEmpty(remark)){
                    return true;
                }
                processInfo.getProcBase().setRemark(remark);
                processInfo.getProcBaseReq().setRemark(remark);
            }

        }
        return false;
    }

    /**
     * ?????????????????????????????????
     *
     * @param object ??????????????????????????????
     *
     * @return ProcessInfo
     */
    public ProcessInfo getProcessInfoByClearFailure(Object object) throws Exception {
        //??????????????????????????????????????????????????????
        ProcessInfo processInfo = new ProcessInfo();
        ProcBase procBase = new ProcBase();
        BeanUtils.copyProperties(object,procBase);
        procBase.setExpectedCompletedTime((Date) PropertyUtils.getProperty(object,"ecTime"));

        //??????????????????
        List<ProcRelatedDevice> procRelatedDevices = new ArrayList<>();
        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
        BeanUtils.copyProperties(object,procRelatedDevice);
        procRelatedDevices.add(procRelatedDevice);

        processInfo.setProcBase(procBase);
        processInfo.setProcRelatedDevices(procRelatedDevices);
        processInfo.setProcRelatedDepartments((List<ProcRelatedDepartment>)PropertyUtils.getProperty(object,"accountabilityDeptList"));
        return processInfo;
    }


}
