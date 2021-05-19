package com.fiberhome.filink.workflowbusinessserver.service.impl.procclear;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.DevicePicFeign;
import com.fiberhome.filink.deviceapi.bean.DevicePicResp;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.workflowapi.constant.ProcessConstants;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procclear.ProcClearFailure;
import com.fiberhome.filink.workflowbusinessserver.bean.workflowbusiness.WorkflowBusinessI18n;
import com.fiberhome.filink.workflowbusinessserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import com.fiberhome.filink.workflowbusinessserver.dao.procclear.ProcClearFailureDao;
import com.fiberhome.filink.workflowbusinessserver.export.procclear.ClearFailureProcHistoryExport;
import com.fiberhome.filink.workflowbusinessserver.export.procclear.ClearFailureProcUnfinishedExport;
import com.fiberhome.filink.workflowbusinessserver.req.app.procclear.ReceiptClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.procrelated.ProcRelatedDeviceListForDeviceIdsReq;
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
import com.fiberhome.filink.workflowbusinessserver.utils.common.CastMapUtil;
import com.fiberhome.filink.workflowbusinessserver.utils.procbase.ProcBaseResultCode;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkFlowBusinessMsg;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkflowBusinessResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <p>
 * 销障工单表 服务实现类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-02-15
 */
@Service
@RefreshScope
@Slf4j
public class ProcClearFailureServiceImpl extends ServiceImpl<ProcClearFailureDao, ProcClearFailure> implements ProcClearFailureService {

    @Autowired
    private ProcClearFailureDao procClearFailureDao;

    @Autowired
    private ProcBaseServiceImpl procBaseService;

    /**
     * 注入销障工单未完成列表导出类
     */
    @Autowired
    private ClearFailureProcUnfinishedExport clearFailureProcUnfinishedExport;

    /**
     * 注入销障工单历史列表导出类
     */
    @Autowired
    private ClearFailureProcHistoryExport clearFailureProcHistoryExport;

    /**
     * 注入远程访问图片feign
     */
    @Autowired
    private DevicePicFeign devicePicFeign;

    /**
     * 注入远程访问工单服务
     */
    @Autowired
    private WorkflowService workflowService;

    /**
     * 工单日志服务
     */
    @Autowired
    private ProcLogService procLogService;

    /**
     * 日志处理服务
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 最大导出条数
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;

    /**
     * 系统语言
     */
    @Autowired
    private SystemLanguageUtil systemLanguage;

    /**
     * 修改销障工单
     * @author hedongwei@wistronits.com
     * @date  2019/6/21 13:51
     * @param procClearFailure 销障工单参数
     * @return 修改销障工单结果
     */
    @Override
    public int updateClearFailure(ProcClearFailure procClearFailure) {
        return procClearFailureDao.updateById(procClearFailure);
    }

    /**
     * 查询销障工单数据
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 10:55
     * @param procClearFailure 销障工单参数
     * @return 返回销障工单数据
     */
    @Override
    public ProcClearFailure selectClearFailureProcOne(ProcClearFailure procClearFailure) {
        ProcClearFailure procClearFailureInfo = procClearFailureDao.selectOne(procClearFailure);
        return procClearFailureInfo;
    }



    /**
     * 新增销障工单
     *
     * @param insertClearFailureReq 新增销障工单类
     * @return Result
     */
    @Override
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_ADD, logType = LogConstants.LOG_TYPE_OPERATE, functionCode = LogFunctionCodeConstant.INSERT_PROC_CLEAR, dataGetColumnName = "title", dataGetColumnId = "procId")
    public Result addClearFailureProc(InsertClearFailureReq insertClearFailureReq) throws Exception {
        systemLanguage.querySystemLanguage();
        String format = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        //统一设置uuid
        insertClearFailureReq.setProcId(NineteenUUIDUtils.uuid());
        log.info("start add proc the procId : {} the start time : {}, the procType : {} "
                ,insertClearFailureReq.getProcId(), simpleDateFormat.format(System.currentTimeMillis()) , ProcBaseConstants.PROC_CLEAR_FAILURE);
        if (ProcBaseConstants.PROC_RESOURCE_TYPE_3.equals(insertClearFailureReq.getProcResourceType())) {
            log.info("the device : {} recvTime : {}" ,insertClearFailureReq.getDeviceId(),simpleDateFormat.format(System.currentTimeMillis()));
        }
        //新增及修改销障工单请求转换为工单汇总
        ProcessInfo processInfo = this.getProcessInfoByClearFailure(insertClearFailureReq);
        if (!ObjectUtils.isEmpty(processInfo.getProcBase())){
            processInfo = clearFailureProcParamsInit(processInfo,insertClearFailureReq);
            return procBaseService.addProcBase(processInfo);
        } else {
            return ResultUtils.warn(ProcBaseResultCode.FAIL, I18nUtils.getSystemString(ProcBaseI18n.PROC_PARAM_ERROR));
        }
    }

    /**
     * 修改销障工单
     *
     * @param updateClearFailureReq 更新销障工单请求
     * @return Result
     */
    @Override
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_UPDATE, logType = LogConstants.LOG_TYPE_OPERATE, functionCode = LogFunctionCodeConstant.UPDATE_PROC_CLEAR, dataGetColumnName = "title", dataGetColumnId = "procId")
    public Result updateClearFailureProcById(UpdateClearFailureReq updateClearFailureReq) throws Exception {
        systemLanguage.querySystemLanguage();
        String format = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        log.info("start update proc the procId : {} the start time : {}, the procType : {} "
                ,updateClearFailureReq.getProcId(), simpleDateFormat.format(System.currentTimeMillis()) , ProcBaseConstants.PROC_CLEAR_FAILURE);
        //新增请求转换为工单汇总
        ProcessInfo processInfo = this.getProcessInfoByClearFailure(updateClearFailureReq);
        if (!ObjectUtils.isEmpty(processInfo.getProcBase())){
            processInfo = clearFailureProcParamsInit(processInfo,updateClearFailureReq);
            return procBaseService.updateProcessById(processInfo);
        } else {
            return ResultUtils.warn(ProcBaseResultCode.FAIL, I18nUtils.getSystemString(ProcBaseI18n.PROC_PARAM_ERROR));
        }
    }

    /**
     * 销障工单参数初始化
     *
     * @param processInfo 工单请求汇总
     * @param object 新增或修改销障工单
     *
     * @return processInfo 工单请求汇总
     */
    public ProcessInfo clearFailureProcParamsInit(ProcessInfo processInfo, Object object) throws Exception {
        processInfo.getProcBase().setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        processInfo.getProcBase().setStatus(ProcBaseConstants.PROC_STATUS_ASSIGNED);
        //单位类型
        List<ProcRelatedDepartment> deptList = (List<ProcRelatedDepartment>)PropertyUtils.getProperty(object,"accountabilityDeptList");
        ProcBase procBase = ProcBase.setDeptTypeAndStatus(deptList, processInfo.getProcBase());
        processInfo.setProcBase(procBase);
        return processInfo;
    }

    /**
     * 创建导出销障工单未完工列表任务
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @Override
    public Result exportClearFailureProcUnfinished(ExportDto exportDto) {
        ExportRequestInfo exportRequestInfo = null;
        try {
            exportRequestInfo = clearFailureProcUnfinishedExport.insertTask(exportDto, WorkFlowBusinessConstants.SERVER_NAME, I18nUtils.getSystemString(ProcBaseI18n.PROC_CLEAR_FAILURE_UNFINISHED_LIST));
        } catch (FilinkExportNoDataException fe) {
            log.error("export clear failure unfinished no data exception", fe);
            return ResultUtils.warn(WorkflowBusinessResultCode.EXPORT_NO_DATA, I18nUtils.getSystemString(WorkflowBusinessI18n.EXPORT_NO_DATA));

        } catch (FilinkExportDataTooLargeException fe) {
            return WorkFlowBusinessMsg.getExportToLargeMsg(fe, maxExportDataSize);
        } catch (FilinkExportTaskNumTooBigException fe) {
            log.error("export clear failure unfinished task num to big exception", fe);
            return ResultUtils.warn(ProcBaseResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(ProcBaseI18n.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            log.error("export clear failure unfinished exception", e);
            return ResultUtils.warn(ProcBaseResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(ProcBaseI18n.FAILED_TO_CREATE_EXPORT_TASK));
        }
        clearFailureProcUnfinishedExport.exportData(exportRequestInfo);

        //新增日志
        procLogService.addLogByExport(exportDto, LogFunctionCodeConstant.PROC_CLEAR_FAILURE_UNFINISHED_EXPORT_CODE);

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 创建导出销障工单历史列表任务
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @Override
    public Result exportClearFailureProcHistory(ExportDto exportDto) {
        ExportRequestInfo exportRequestInfo = null;
        try {
            exportRequestInfo = clearFailureProcHistoryExport.insertTask(exportDto, WorkFlowBusinessConstants.SERVER_NAME, I18nUtils.getSystemString(ProcBaseI18n.PROC_CLEAR_FAILURE_HISTORY_LIST));
        } catch (FilinkExportNoDataException fe) {
            log.error("export clear failure history no data exception", fe);
            return ResultUtils.warn(WorkflowBusinessResultCode.EXPORT_NO_DATA, I18nUtils.getSystemString(WorkflowBusinessI18n.EXPORT_NO_DATA));

        } catch (FilinkExportDataTooLargeException fe) {
            return WorkFlowBusinessMsg.getExportToLargeMsg(fe, maxExportDataSize);
        } catch (FilinkExportTaskNumTooBigException fe) {
            log.error("export clear failure history task num to big exception", fe);
            return ResultUtils.warn(ProcBaseResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(ProcBaseI18n.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            log.error("export clear failure history exception", e);
            return ResultUtils.warn(ProcBaseResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(ProcBaseI18n.FAILED_TO_CREATE_EXPORT_TASK));
        }
        clearFailureProcHistoryExport.exportData(exportRequestInfo);

        //新增日志
        procLogService.addLogByExport(exportDto, LogFunctionCodeConstant.PROC_CLEAR_FAILURE_HIS_EXPORT_CODE);

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 分页查询销障工单未完工列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @Override
    public Result queryListClearFailureUnfinishedProcByPage(QueryCondition<ProcBaseReq> queryCondition) {
        //设施工单类型为销障工单
        this.setProcTypeToClearFailure(queryCondition);
        return procBaseService.queryListProcUnfinishedProcByPage(queryCondition);
    }

    /**
     * 分页查询销障工单历史列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @Override
    public Result queryListClearFailureHisProcByPage(QueryCondition<ProcBaseReq> queryCondition) {
        //设施工单类型为销障工单
        this.setProcTypeToClearFailure(queryCondition);
        return procBaseService.queryListProcHisProcByPage(queryCondition);
    }

    /**
     * 保存销障工单特有信息
     *
     * @param procClearFailure 销障工单特有信息
     * @return int
     */
    @Override
    public int saveProcClearFailureSpecific(ProcClearFailure procClearFailure) {
        String userId = RequestInfoUtils.getUserId();
        //工单编号
        String procId = procClearFailure.getProcId();
        //查询销障工单信息
        ProcClearFailure procClearFailureParam = new ProcClearFailure();
        procClearFailureParam.setProcId(procId);
        procClearFailureParam.setIsDeleted(WorkFlowBusinessConstants.IS_NOT_DELETED);
        ProcClearFailure procClearFailureInfo = procClearFailureDao.selectOne(procClearFailureParam);

        //区分新增和修改
        if (!ObjectUtils.isEmpty(procClearFailureInfo)){
            procClearFailure.setUpdateUser(userId);
            procClearFailure.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
            return procClearFailureDao.updateById(procClearFailure);
        } else {
            procClearFailure.setCreateUser(userId);
            procClearFailure.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
            return procClearFailureDao.insert(procClearFailure);
        }
    }

    /**
     * 删除/恢复销障工单特有信息
     *
     * @param procId 工单id
     * @param isDeleted 逻辑删除字段
     *
     * @return int
     */
    @Override
    public int updateProcClearFailureSpecificIsDeleted(String procId,String isDeleted) {
        return procClearFailureDao.updateProcClearFailureSpecificIsDeleted(procId,isDeleted);
    }

    /**
     * 批量逻辑删除销障工单
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 11:51
     * @param procBaseReq 删除销障工单参数
     * @return 批量删除销障工单
     */
    @Override
    public int updateProcClearFailureSpecificIsDeletedBatch(ProcBaseReq procBaseReq) {
        return procClearFailureDao.updateProcClearFailureSpecificIsDeletedBatch(procBaseReq);
    }

    /**
     * 获取销障工单特有信息
     *
     * @param procIds 工单ids
     * @return List<ProcClearFailure>
     */
    @Override
    public List<ProcClearFailure> queryProcClearFailureSpecific(Set<String> procIds){
        return procClearFailureDao.queryProcClearFailureSpecific(procIds);
    }

    /**
     * 销障工单未完工列表状态总数统计
     *
     * @return Result
     */
    @Override
    public Result queryCountListProcUnfinishedProcStatus(){
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition();
        //设施工单类型为销障工单
        this.setProcTypeToClearFailure(queryCondition);
        return procBaseService.queryCountListProcUnfinishedProcStatus(queryCondition);
    }

    /**
     * 销障工单列表待指派状态统计
     *
     * @return Result
     */
    @Override
    public Result queryCountClearFailureProcByAssigned() {
        //销障工单待指派状态
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procBaseReq.setStatus(ProcBaseConstants.PROC_STATUS_ASSIGNED);
        procBaseReq.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        return procBaseService.queryCountProcByStatus(procBaseReq);
    }

    /**
     * 销障工单列表待处理状态统计
     *
     * @return Result
     */
    @Override
    public Result queryCountClearFailureProcByPending() {
        //销障工单待处理状态
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procBaseReq.setStatus(ProcBaseConstants.PROC_STATUS_PENDING);
        procBaseReq.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        return procBaseService.queryCountProcByStatus(procBaseReq);
    }

    /**
     * 销障工单列表处理中状态统计
     *
     * @return Result
     */
    @Override
    public Result queryCountClearFailureProcByProcessing() {
        //销障工单处理中状态
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procBaseReq.setStatus(ProcBaseConstants.PROC_STATUS_PROCESSING);
        procBaseReq.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        return procBaseService.queryCountProcByStatus(procBaseReq);
    }

    /**
     * 销障工单列表今日新增状态统计
     *
     * @return Result
     */
    @Override
    public Result queryCountClearFailureProcByToday(){
        //销障工单今日新增
        ProcBaseReq procBaseReq = new ProcBaseReq();
        this.getTodayStartAndEndTime(procBaseReq);
        procBaseReq.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        return procBaseService.queryCountProcByStatus(procBaseReq);
    }

    /**
     * 故障原因统计的销障工单信息
     *
     * @return Result
     */
    @Override
    public Result queryListClearFailureGroupByErrorReason(){
        //销障工单
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procBaseReq.setGroupField(ProcBaseConstants.PROC_GROUP_ERROR_REASON);
        return procBaseService.queryCountProcByGroup(procBaseReq);
    }

    /**
     * 处理方案统计的销障工单信息
     *
     * @return Result
     */
    @Override
    public Result queryListClearFailureGroupByProcessingScheme(){
        //销障工单
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procBaseReq.setGroupField(ProcBaseConstants.PROC_GROUP_PROCESSING_SCHEME);
        return procBaseService.queryCountProcByGroup(procBaseReq);
    }

    /**
     * 重新生成销障工单
     * @author chaofanrong@wistronits.com
     * @date  2019/4/16 18:19
     * @param insertClearFailureReq 新增销障工单请求
     * @return 重新生成销障工单结果
     */
    @Override
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_ADD, logType = LogConstants.LOG_TYPE_OPERATE, functionCode = LogFunctionCodeConstant.REGENERATE_PROC_CLEAR_FAILURE_FUNCTION_CODE, dataGetColumnName = "title", dataGetColumnId = "procId")
    public Result regenerateClearFailureProc(InsertClearFailureReq insertClearFailureReq) throws Exception {
        systemLanguage.querySystemLanguage();
        //统一设置uuid
        insertClearFailureReq.setProcId(NineteenUUIDUtils.uuid());
        //新增及修改销障工单请求转换为工单汇总
        ProcessInfo processInfo = this.getProcessInfoByClearFailure(insertClearFailureReq);
        if (!ObjectUtils.isEmpty(processInfo.getProcBase())){
            processInfo = clearFailureProcParamsInit(processInfo,insertClearFailureReq);

            //重新生成工单不校验告警
            processInfo.setOperaType(ProcBaseConstants.OPERATOR_TYPE_REGENERATE);

            //获取原工单
            ProcClearFailure procClearFailure = procClearFailureDao.selectById(processInfo.getProcBase().getRegenerateId());
            if (!ObjectUtils.isEmpty(procClearFailure)){
                //如果确认退单
                if (ProcBaseConstants.IS_CHECK_SINGLE_BACK.equals(procClearFailure.getIsCheckSingleBack())){
                    //直接保存重新生成数据
                    return procBaseService.addProcBase(processInfo);
                } else {
                    //重新生成数据
                    return procBaseService.regenerateProc(processInfo);
                }
            } else {
                return ResultUtils.warn(ProcBaseResultCode.PROC_NOT_EXIST,I18nUtils.getSystemString(ProcBaseI18n.PROC_IS_NOT_EXIST));
            }
        } else {
            return ResultUtils.warn(ProcBaseResultCode.FAIL, I18nUtils.getSystemString(ProcBaseI18n.PROC_PARAM_ERROR));
        }
    }

    /**
     * 销障工单前五
     *
     * @param deviceId 设施id
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

        //该设施过滤前五
        ProcBaseReq procBaseReq = new ProcBaseReq();
        Set<String> deviceIds = new HashSet<>();
        deviceIds.add(deviceId);
        procBaseReq.setDeviceIds(deviceIds);
        queryCondition.setBizCondition(procBaseReq);

        //设施工单类型为销障工单
        this.setProcTypeToClearFailure(queryCondition);

        List<ProcBaseResp> procBaseRespList = (List<ProcBaseResp>)procBaseService.queryListProcUnfinishedProcByPage(queryCondition).getData();
        //获取工单ids
        List<String> procIds = new ArrayList<>();
        for (ProcBaseResp procBaseResp : procBaseRespList){
            if (!procIds.contains(procBaseResp.getProcId())){
                procIds.add(procBaseResp.getProcId());
            }
        }
        //如果该设施没有工单
        if (ObjectUtils.isEmpty(procIds)){
            return ResultUtils.success(procBaseRespList);
        }
        //远程获取图片信息
        List<Object> objects = (List<Object>)devicePicFeign.getPicUrlByProcIds(procIds).getData();
        if (null == objects){
            return ResultUtils.warn(ProcBaseResultCode.FAIL,I18nUtils.getSystemString(ProcBaseI18n.DEVICE_PIC_SERVER_ERROR));
        }
        List<DevicePicResp> devicePicRespList = new ArrayList<>();
        for (Object object : objects){
            DevicePicResp devicePicResp = JSONArray.toJavaObject((JSON)JSONArray.toJSON(object),DevicePicResp.class);
            devicePicRespList.add(devicePicResp);
        }
        //组装主表及图片表信息
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
     * app销障工单回单
     *
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result receiptClearFailureProcForApp(ReceiptClearFailureReq receiptClearFailureReq) {
        String format = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        log.info("begin complete clear failure proc , the procId : {}, the begin time : {}", receiptClearFailureReq.getProcId(), simpleDateFormat.format(System.currentTimeMillis()));
        if (ObjectUtils.isEmpty(receiptClearFailureReq)){
            return ResultUtils.warn(ProcBaseResultCode.FAIL,I18nUtils.getSystemString(ProcBaseI18n.PROC_PARAM_ERROR));
        }
//        for (int i = 0;i<receiptClearFailureReqs.size();i++){
            //判断是否缺少工单id
            if (StringUtils.isEmpty(receiptClearFailureReq.getProcId())){
                return ResultUtils.warn(ProcBaseResultCode.PROC_ID_LOSE,I18nUtils.getSystemString(ProcBaseI18n.PROC_ID_LOSE));
            }
            //获取工单基础信息
            ProcBase procBase = (ProcBase)procBaseService.queryProcessById(receiptClearFailureReq.getProcId()).getData();
            if (ObjectUtils.isEmpty(procBase) || StringUtils.isEmpty(procBase.getProcId())){
                return ResultUtils.warn(ProcBaseResultCode.PROC_NOT_EXIST,I18nUtils.getSystemString(ProcBaseI18n.PROC_IS_NOT_EXIST));
            }
            //故障原因
            procBase.setErrorReason(receiptClearFailureReq.getErrorReason());
            procBase.setErrorUserDefinedReason(receiptClearFailureReq.getUserDefinedErrorReason());

            //处理方案
            procBase.setProcessingScheme(receiptClearFailureReq.getProcessingScheme());
            procBase.setProcessingUserDefinedScheme(receiptClearFailureReq.getUserDefinedProcessingScheme());

            //设置更新人
            procBase.setUpdateUser(RequestInfoUtils.getUserId());

            //实际完工时间
            procBase.setRealityCompletedTime(new Date());

            //修改时间
            procBase.setUpdateTime(new Date());

            if (1 != procClearFailureDao.receiptProcClearFailureById(procBase)){
                return ResultUtils.warn(ProcBaseResultCode.FAIL, I18nUtils.getSystemString(ProcBaseI18n.PROC_RECEIPT_FAIL));
            }

            //提交销障工单
            this.commitProcClearFailure(procBase);

            //新增操作日志
            String functionCode = LogFunctionCodeConstant.RETURN_CLEAR_FAILURE_FUNCTION_CODE;
            //数据操作类型
            String dataType = LogConstants.DATA_OPT_TYPE_UPDATE;
            //记录app操作日志
            AddLogBean addLogBean = procLogService.getAddProcOperateLogParamForApp(procBase, functionCode, dataType);
            //记录操作日志
            logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
            log.info("end complete clear failure proc , the procId : {}, the end time : {}", receiptClearFailureReq.getProcId(), simpleDateFormat.format(System.currentTimeMillis()));
            //销障
            return ResultUtils.success(ProcBaseResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.PROC_RECEIPT_SUCCESS));
    }

    /**
     * 提交销障工单
     * @author chaofanrong@wistronits.com
     * @date  2019/4/16 14:28
     * @param procBase 提交参数
     * @return 提交销障工单结果
     */
    public Result commitProcClearFailure(ProcBase procBase) {
        procBase.setStatus(ProcBaseConstants.PROC_STATUS_PROCESSING);
        CompleteProcessInfoReq completeProcessReq = new CompleteProcessInfoReq();
        BeanUtils.copyProperties(procBase, completeProcessReq);

        //流程操作办结
        completeProcessReq.setOperation(ProcessConstants.PROC_OPERATION_COMPLETE);

        //办结工单
        Result result = workflowService.completeProcess(completeProcessReq);
        return result;
    }

    /**
     * app销障工单下载
     *
     * @param procBaseRespForApps 工单下载列表
     *
     * @return List<ClearFailureDownLoadDetail>
     */
    @Override
    public List<ClearFailureDownLoadDetail> downLoadClearFailureProcForApp(List<ProcBaseRespForApp> procBaseRespForApps){
        List<ClearFailureDownLoadDetail> clearFailureDownLoadDetails = new ArrayList<>();
        for (ProcBaseRespForApp procBaseRespForApp : procBaseRespForApps){
            ClearFailureDownLoadDetail clearFailureDownLoadDetail = new ClearFailureDownLoadDetail();
            //工单内容
            BeanUtils.copyProperties(procBaseRespForApp,clearFailureDownLoadDetail);
            //工单关联设施内容
            if (!ObjectUtils.isEmpty(procBaseRespForApp.getProcRelatedDeviceRespList())){
                BeanUtils.copyProperties(procBaseRespForApp.getProcRelatedDeviceRespList().get(0),clearFailureDownLoadDetail);
                clearFailureDownLoadDetail.setProcId(procBaseRespForApp.getProcId());
            }
            //工单开始时间
            if (!ObjectUtils.isEmpty(procBaseRespForApp.getCreateTime())) {
                clearFailureDownLoadDetail.setStartTime(procBaseRespForApp.getCreateTime().getTime());
            }

            //工单结束时间
            if (!ObjectUtils.isEmpty(procBaseRespForApp.getExpectedCompletedTime())) {
                clearFailureDownLoadDetail.setEndTime(procBaseRespForApp.getExpectedCompletedTime().getTime());
            }

            //告警时间
            clearFailureDownLoadDetail.setAlarmTime(procBaseRespForApp.getRefAlarmTime());

            clearFailureDownLoadDetails.add(clearFailureDownLoadDetail);
        }

        return clearFailureDownLoadDetails;
    }

    /**
     * 根据工单编号修改销障工单的状态
     *
     * @param  procClearFailure 销障工单信息
     *
     * @return 销障工单修改的行数
     */
    @Override
    public int updateProcClearFailureStatusById(ProcClearFailure procClearFailure) {
        return procClearFailureDao.updateProcClearFailureStatusById(procClearFailure);
    }

    /**
     * 查询存在销障工单的告警信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/19 13:17
     * @param refAlarmList 关联工单编号
     * @return 存在告警的工单信息
     */
    @Override
    public List<ProcClearFailure> queryExistsProcClearFailureForAlarmList(List<String> refAlarmList) {
        return procClearFailureDao.queryExistsProcClearFailureForAlarmList(refAlarmList);
    }

    /**
     * 修改销障工单备注
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 11:38
     * @param procClearFailure 销障工单参数
     * @return 返回修改销障工单状态结果
     */
    @Override
    public int updateProcClearFailureRemarkById(ProcClearFailure procClearFailure) {
        return procClearFailureDao.updateProcClearFailureRemarkById(procClearFailure);
    }

    /**
     * 根据id删除工单基础信息
     *
     * @param procBaseReq 工单基础信息
     * @return int
     */
    @Override
    public int updateProcClearFailureIsDeletedByIds(ProcBaseReq procBaseReq) {
        return procClearFailureDao.updateProcClearFailureIsDeletedByIds(procBaseReq);
    }

    /**
     * 查询销障工单关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/22 17:10
     * @param procBaseReq 工单参数
     * @return 返回销障工单信息
     */
    @Override
    public List<ProcClearFailure> queryClearFailureProcRelateDevice(ProcBaseReq procBaseReq) {
        return procClearFailureDao.queryClearFailureProcRelateDevice(procBaseReq);
    }

    /**
     * app获取登录用户销障工单基础信息
     *
     * @param procBaseReq 工单请求
     *
     * @return List<ProcBaseRespForApp>
     */
    @Override
    public List<ProcBaseRespForApp> queryLoginUserClearFailureListForApp(ProcBaseReq procBaseReq) {
        return procClearFailureDao.queryLoginUserClearFailureListForApp(procBaseReq);
    }

    /**
     * 查询巡检关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 11:39
     * @param list 部门ids
     * @return 工单关联部门信息
     */
    @Override
    public List<ProcClearFailure> queryClearRelateDeptByProcIds(List<String> list) {
        return procClearFailureDao.queryClearRelateDeptByProcIds(list);
    }

    /**
     * app获取销障工单基础信息
     *
     * @param procBaseReq 工单请求
     *
     * @return List<ProcBaseRespForApp>
     */
    @Override
    public List<ProcBaseRespForApp> queryClearFailureListForApp(ProcBaseReq procBaseReq) {
        return procClearFailureDao.queryClearFailureListForApp(procBaseReq);
    }

    /**
     * 批量修改销障工单的责任人
     * @author hedongwei@wistronits.com
     * @date  2019/4/14 19:48
     * @param procBase 工单主表
     * @param list 工单id集合
     * @return 修改责任人的结果
     */
    @Override
    public int updateProcClearAssignBatch(ProcBase procBase, List<String> list) {
        return procClearFailureDao.updateProcClearAssignBatch(procBase, list);
    }


    /**
     * 根据告警查询存在的工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/19 12:49
     * @param alarmProcList 告警编号数组
     * @return 查询告警工单结果
     */
    @Override
    public Map<String, Object> queryExistsProcClearForAlarmList(List<String> alarmProcList) {
        if (!ObjectUtils.isEmpty(alarmProcList)) {
            //初始化返回值
            Map<String, Object> alarmMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
            for (String alarmId : alarmProcList) {
                alarmMap.put(alarmId, "0");
            }
            //查询所有告警编号关联的数据信息
            List<ProcClearFailure> clearFailureList = procClearFailureDao.queryExistsProcClearForAlarmList(alarmProcList);
            alarmMap = CastMapUtil.getIsExistsClearAlarmMap(clearFailureList, alarmMap);
            return alarmMap;
        }
        return null;
    }

    /**
     * 查询销障工单关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 19:58
     * @param req 关联设施参数
     * @return 查询销障工单关联设施信息
     */
    @Override
    public List<ProcClearFailure> selectClearRelatedDeviceListInfo(ProcRelatedDeviceListForDeviceIdsReq req) {
        return procClearFailureDao.selectClearRelatedDeviceListInfo(req);
    }

    /**
     * 根据用户编号集合查询存在未办理的销障信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/6 12:28
     * @param userIdList 用户编号集合
     * @return 销障工单信息
     */
    @Override
    public List<ProcClearFailure> queryExistsProcForUserList(List<String> userIdList) {
        return procClearFailureDao.queryExistsProcForUserList(userIdList);
    }


    /**
     * 销障工单关联设施集合
     * @author hedongwei@wistronits.com
     * @date  2019/3/26 17:50
     * @param list 销障工单编号
     * @return 返回销障工单关联设施集合
     */
    @Override
    public List<ProcClearFailure> queryProcRelateDeviceByProcIds(List<String> list) {
        return procClearFailureDao.queryProcRelateDeviceByProcIds(list);
    }


    /**
     * 查询巡检关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 11:39
     * @param deptIds 部门ids
     * @return 工单关联部门信息
     */
    @Override
    public List<ProcClearFailure> queryClearProcListByDeptIds(List<String> deptIds) {
        return procClearFailureDao.queryClearProcListByDeptIds(deptIds);
    }


    /**
     * 查询销障工单未完工列表总数
     *
     * @param queryCondition 查询封装类
     * @return int
     */
    @Override
    public int queryCountListProcClearUnfinishedProc(QueryCondition<ProcBaseReq> queryCondition) {
        return procClearFailureDao.queryCountListProcClearUnfinishedProc(queryCondition);
    }

    /**
     * 分页查询销障工单未完工列表
     *
     * @param queryCondition 查询封装类
     * @return List<ProcBaseResp>
     */
    @Override
    public List<ProcBaseResp> queryListProcClearUnfinishedProcByPage(QueryCondition<ProcBaseReq> queryCondition) {
        return procClearFailureDao.queryListProcClearUnfinishedProcByPage(queryCondition);
    }

    /**
     * 查询销障工单历史列表总数
     *
     * @param queryCondition 查询封装类
     * @return int
     */
    @Override
    public int queryCountListProcClearHisProc(QueryCondition<ProcBaseReq> queryCondition) {
        return procClearFailureDao.queryCountListProcClearHisProc(queryCondition);
    }


    /**
     * 分页查询销障工单历史列表
     *
     * @param queryCondition 查询封装类
     * @return List<ProcBaseResp>
     */
    @Override
    public List<ProcBaseResp> queryListProcClearHisProcByPage(QueryCondition<ProcBaseReq> queryCondition) {
        return procClearFailureDao.queryListProcClearHisProcByPage(queryCondition);
    }

    /**
     * 批量新增关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/14 21:08
     * @param procClearFailureList 关联销障工单信息
     */
    @Override
    public int addClearFailureBatch(List<ProcClearFailure> procClearFailureList) {
        return procClearFailureDao.addClearFailureBatch(procClearFailureList);
    }

    /**
     * 设施类型统计的销障工单信息
     *
     * @return Result
     */
    @Override
    public Result queryListClearFailureGroupByDeviceType(){
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition();
        //设施工单类型为销障工单
        this.setProcTypeToClearFailure(queryCondition);
        return procBaseService.queryListGroupByDeviceType(queryCondition);
    }

    /**
     * 工单状态统计的销障工单信息
     *
     * @return Result
     */
    @Override
    public Result queryListClearFailureByStatus(){
        Map<String,Integer> statusMap = new HashMap<>(64);
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition();
        //销障工单
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procBaseReq.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        // 销障工单历史状态总数
        queryCondition.setBizCondition(procBaseReq);
        statusMap.put(ProcBaseConstants.PROC_HIS_TOTAL_COUNT,(Integer)this.queryCountListProcHisProc().getData());
        //销障工单已完成状态
        procBaseReq.setStatus(ProcBaseConstants.PROC_STATUS_COMPLETED);
        statusMap.put(ProcBaseConstants.PROC_STATUS_COMPLETED,(Integer)procBaseService.queryCountProcByStatus(procBaseReq).getData());
        //销障工单已退单状态
        procBaseReq.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
        procBaseReq.setIsCheckSingleBack(ProcBaseConstants.IS_CHECK_SINGLE_BACK);
        statusMap.put(ProcBaseConstants.PROC_STATUS_SINGLE_BACK,(Integer)procBaseService.queryCountProcByStatus(procBaseReq).getData());
        return ResultUtils.success(ProcBaseResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.QUERY_PROC_STATUS_COUNT_SUCCESS),statusMap);
    }

    /**
     * 销障工单历史列表总数统计
     *
     * @return Result
     */
    @Override
    public Result queryCountListProcHisProc(){
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition();
        //设施工单类型为销障工单
        this.setProcTypeToClearFailure(queryCondition);
        return procBaseService.queryCountListProcHisProc(queryCondition);
    }

    /*-----------------------------------------------------------------------以下为公共方法-----------------------------------------------------------------------*/
    /**
     * 设置工单类型为销障工单
     *
     * @param queryCondition 查询封装类
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
     * 获取今天开始及结束时间
     *
     * @param procBaseReq 工单请求类
     * @return ProcBaseReq
     */
    private ProcBaseReq getTodayStartAndEndTime(ProcBaseReq procBaseReq){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //获取当前时间
        LocalDateTime nowTime = LocalDateTime.now();
        //获取当前日期
        LocalDate nowDate = LocalDate.now();
        //设置零点
        LocalDateTime beginTime = LocalDateTime.of(nowDate, LocalTime.MIN);
        //将时间进行格式化
        String time1= beginTime.format(dtf);
        //设置当天的结束时间
        LocalDateTime endTime = LocalDateTime.of(nowDate,LocalTime.MAX);
        //将时间进行格式化
        String time2 =dtf.format(endTime);
        procBaseReq.setTodayStartTime(time1);
        procBaseReq.setTodayEndTime(time2);
        return procBaseReq;
    }

    /**
     * 校验参数合法性
     *
     * @param processInfo 工单汇总类
     * @return Result
     */
    @Override
    public Result checkProcParamsForClearFailure(ProcessInfo processInfo){
        //转换为持久对象
        ProcBaseReq procBaseReq = new ProcBaseReq();
        BeanUtils.copyProperties(processInfo.getProcBase(),procBaseReq);
        processInfo.setProcBaseReq(procBaseReq);
        // 校验必填参数
        if (this.checkProcParamsLengthForClearFailure(processInfo)) {
            return ResultUtils.warn(ProcBaseResultCode.PROC_PARAM_ERROR, I18nUtils.getSystemString(ProcBaseI18n.PROC_PARAM_ERROR));
        }

        // 校验告警存在的工单信息
        if (procBaseService.queryExistsProcForAlarm(processInfo.getProcBase(),processInfo.getOperaType())){
            return ResultUtils.warn(ProcBaseResultCode.IS_EXISTS_PROC_FOR_ALARM, I18nUtils.getSystemString(ProcBaseI18n.IS_EXISTS_PROC_FOR_ALARM));
        }
        return ResultUtils.success(ProcBaseResultCode.SUCCESS);
    }

    /**
     * 校验工单基本参数长度
     *
     * @param processInfo 工单汇总类
     * @return boolean
     */
    private boolean checkProcParamsLengthForClearFailure(ProcessInfo processInfo) {
        //必填校验
        //工单名称为空
        if (StringUtils.isEmpty(processInfo.getProcBase().getTitle()) ||
                //工单类型为空
                StringUtils.isEmpty(processInfo.getProcBase().getProcType())||
                //工单关联告警为空
                StringUtils.isEmpty(processInfo.getProcBase().getRefAlarm()) ||

                //工单关联设施及区域为空
                ObjectUtils.isEmpty(processInfo.getProcRelatedDevices()) ||

                //工单期望完工时间为空
                ObjectUtils.isEmpty(processInfo.getProcBase().getExpectedCompletedTime())
        ) {
            return true;
        } else {
            //工单名称统一校验
            String procName = CheckInputString.nameCheck(processInfo.getProcBase().getTitle());
            if (StringUtils.isEmpty(procName)){
                return true;
            }
            processInfo.getProcBase().setTitle(procName);
            processInfo.getProcBaseReq().setTitle(procName);

            //工单备注统一校验
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
     * 获取今天开始及结束时间
     *
     * @param object 新增及修改工单请求类
     *
     * @return ProcessInfo
     */
    private ProcessInfo getProcessInfoByClearFailure(Object object) throws Exception {
        //新增及修改销障工单请求转换为工单汇总
        ProcessInfo processInfo = new ProcessInfo();
        ProcBase procBase = new ProcBase();
        BeanUtils.copyProperties(object,procBase);
        procBase.setExpectedCompletedTime((Date) PropertyUtils.getProperty(object,"ecTime"));

        //获取设施信息
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
