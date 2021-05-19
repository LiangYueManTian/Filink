package com.fiberhome.filink.workflowbusinessserver.service.impl.procinspection;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.AreaInfoForeignDto;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.exportapi.bean.Export;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.workflowapi.constant.ProcessConstants;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTask;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDetailBean;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.AddInspectionTaskBean;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import com.fiberhome.filink.workflowbusinessserver.bean.workflowbusiness.WorkflowBusinessI18n;
import com.fiberhome.filink.workflowbusinessserver.constant.*;
import com.fiberhome.filink.workflowbusinessserver.dao.procinspection.ProcInspectionDao;
import com.fiberhome.filink.workflowbusinessserver.exception.FilinkWorkflowBusinessDataException;
import com.fiberhome.filink.workflowbusinessserver.exception.procinspection.InspectionDeviceException;
import com.fiberhome.filink.workflowbusinessserver.export.procinspection.InspectionProcCompleteExport;
import com.fiberhome.filink.workflowbusinessserver.export.procinspection.InspectionProcProcessExport;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.req.process.CompleteProcessInfoReq;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.CompleteInspectionProcReq;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.ProcInspectionRecordReq;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.ProcInspectionReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessDetail;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcRelatedDeviceResp;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procinspection.InspectionDownLoadDetail;
import com.fiberhome.filink.workflowbusinessserver.resp.procinspection.ProcInspectionResp;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcBaseService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcLogService;
import com.fiberhome.filink.workflowbusinessserver.service.process.WorkflowService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionRecordService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionService;
import com.fiberhome.filink.workflowbusinessserver.utils.common.*;
import com.fiberhome.filink.workflowbusinessserver.utils.procbase.ProcBaseResultCode;
import com.fiberhome.filink.workflowbusinessserver.utils.procinspection.ProcInspectionResultCode;
import com.fiberhome.filink.workflowbusinessserver.utils.request.RequestHeaderUtils;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkFlowBusinessMsg;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkflowBusinessResultCode;
import com.fiberhome.filink.workflowbusinessserver.vo.procinspection.GetInspectionProcByIdVo;
import com.fiberhome.filink.workflowbusinessserver.vo.procinspection.GetInspectionTaskRelatedProcVo;
import com.fiberhome.filink.workflowbusinessserver.vo.procinspection.ProcInspectionRecordVo;
import com.fiberhome.filink.workflowbusinessserver.vo.procinspection.ProcInspectionVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 巡检工单表 服务实现类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-03-11
 */
@Service
@RefreshScope
public class ProcInspectionServiceImpl extends ServiceImpl<ProcInspectionDao, ProcInspection> implements ProcInspectionService {

    @Autowired
    private ProcBaseService procBaseService;

    @Autowired
    private LogProcess logProcess;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private ProcInspectionDao procInspectionDao;

    @Autowired
    private InspectionTaskService inspectionTaskService;

    @Autowired
    private ProcInspectionRecordService procInspectionRecordService;

    @Autowired
    private InspectionProcProcessExport inspectionProcProcessExport;

    @Autowired
    private InspectionProcCompleteExport inspectionProcCompleteExport;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private DepartmentFeign departmentFeign;

    @Autowired
    private AreaFeign areaFeign;

    @Autowired
    private ProcLogService procLogService;

    /**
     * 最大导出条数
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;

    /**
     * 根据流程编号查询巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 19:40
     * @param procId 流程编号
     * @return 巡检工单信息
     */
    @Override
    public ProcInspection selectInspectionProcByProcId(String procId) {
        return procInspectionDao.selectInspectionProcByProcId(procId);
    }


    /**
     * 批量查询巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/16 14:20
     * @param procIds 流程编号集合
     * @return 巡检工单集合
     */
    @Override
    public List<ProcInspection> selectInspectionProcByProcIds(Set<String> procIds) {
        return procInspectionDao.selectInspectionProcByProcIds(procIds);
    }

    /**
     * 查询巡检工单未完工列表
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 15:33
     * @param queryCondition 查询条件
     * @return 返回巡检工单未完工列表
     */
    @Override
    public Result queryListInspectionProcessByPage(QueryCondition<ProcBaseReq> queryCondition) {
        //校验方法参数
        if (null != ValidateUtils.checkQueryConditionParam(queryCondition)) {
            return ValidateUtils.checkQueryConditionParam(queryCondition);
        }

        //获取查询条件过滤之后的条件
        queryCondition = ValidateUtils.filterQueryCondition(queryCondition);

        //设施工单类型为巡检工单
        queryCondition = this.setProcTypeToInspection(queryCondition);
        Result result = procBaseService.queryListProcUnfinishedProcByPage(queryCondition);
        result = this.getInspectionVo(result);
        return result;
    }

    /**
     * 获得页面返回的值
     * @author hedongwei@wistronits.com
     * @date  2019/3/18 10:34
     * @param result 返回页面的值
     * @return 页面返回的值
     */
    @Override
    public Result getInspectionVo(Result result) {
        //转换成前端展示的数据
        List<ProcBaseResp> procBaseRespList = (List<ProcBaseResp>) result.getData();
        result.setData(this.getInspectionVoInfo(procBaseRespList));
        return result;
    }

    /**
     * 获取巡检工单数据
     * @author hedongwei@wistronits.com
     * @date  2019/4/12 12:31
     * @param procBaseRespList 工单返回类
     * @return 返回页面显示巡检工单数据
     */
    @Override
    public List<ProcInspectionVo> getInspectionVoInfo(List<ProcBaseResp> procBaseRespList) {
        List<ProcInspectionVo> procInspectionVoList = new ArrayList<>();
        if (null != procBaseRespList && 0 < procBaseRespList.size()) {
            ProcInspectionVo procInspectionVo;
            for (ProcBaseResp procBaseResp : procBaseRespList) {
                procInspectionVo = new ProcInspectionVo();
                BeanUtils.copyProperties(procBaseResp, procInspectionVo);
                procInspectionVoList.add(procInspectionVo);
            }
            return procInspectionVoList;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 获取巡检任务关联工单页面显示
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 17:30
     * @param result 查询的结果
     * @return 获取巡检任务关联工单页面显示数据
     */
    public Result getInspectionTaskRelatedProcVo(Result result) {
        //转换成前端展示的数据
        List<ProcBaseResp> procBaseRespList = (List<ProcBaseResp>) result.getData();
        List<GetInspectionTaskRelatedProcVo> relatedProcVoList = new ArrayList<>();
        if (null != procBaseRespList && 0 < procBaseRespList.size()) {
            GetInspectionTaskRelatedProcVo inspectionTaskRelatedProcVo;
            for (ProcBaseResp procBaseResp : procBaseRespList) {
                inspectionTaskRelatedProcVo = new GetInspectionTaskRelatedProcVo();
                BeanUtils.copyProperties(procBaseResp, inspectionTaskRelatedProcVo);
                relatedProcVoList.add(inspectionTaskRelatedProcVo);
            }
            result.setData(relatedProcVoList);
        } else {
            result.setData(new ArrayList<GetInspectionTaskRelatedProcVo>());
        }
        return result;
    }



    /**
     * 新增巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param req 新增巡检工单参数
     * @param operate 操作
     * @param isAlarmViewCall 是否是告警前台调用
     * @return 返回新增巡检工单结果
     */
    @Override
    public Result addInspectionProc(ProcInspectionReq req, String operate, String isAlarmViewCall) {
        //新增巡检工单
        Result addResult = this.addInspectionProcess(req, operate, ProcBaseConstants.IS_NOT_REGENERATE, isAlarmViewCall);
        if (null != addResult) {
            return addResult;
        }

        //新增巡检工单成功提示信息
        String addInspectionSuccess = I18nUtils.getString(ProcInspectionI18n.ADD_PROC_INSPECTION_SUCCESS);
        return ResultUtils.success(ResultCode.SUCCESS, addInspectionSuccess);
    }



    /**
     * 重新生成巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param req 新增巡检工单参数
     * @param operate 操作
     * @return 返回重新生成巡检工单结果
     */
    @Override
    public Result regenerateInspectionProc(ProcInspectionReq req, String operate) {
        String isAlarmViewCall = ProcInspectionConstants.NOT_IS_ALARM_VIEW_CALL;
        //新增巡检工单信息
        Result addResult = this.addInspectionProcess(req, operate, ProcBaseConstants.IS_REGENERATE, isAlarmViewCall);
        if (null != addResult) {
            return addResult;
        }

        //新增巡检工单成功提示信息
        String addInspectionSuccess = I18nUtils.getString(ProcInspectionI18n.ADD_PROC_INSPECTION_SUCCESS);
        return ResultUtils.success(ResultCode.SUCCESS, addInspectionSuccess);
    }

    /**
     * 新增巡检工单过程
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param req 新增巡检工单参数
     * @param operate 操作
     * @param isAlarmViewCall 是否是告警前台调用
     * @return 返回新增巡检工单结果
     */
    public Result addInspectionProcess(ProcInspectionReq req, String operate, boolean isRegenerate, String isAlarmViewCall) {
        //校验参数是否正确
        if (!ProcInspectionReq.validateProcInspectionReq(req, ProcInspectionConstants.OPERATE_ADD)) {
            return WorkFlowBusinessMsg.paramErrorMsg();
        }

        //当前时间
        Date nowDate = new Date();
        //工单编号
        String procId = req.getProcId();

        //工单编号
        procId = NineteenUUIDUtils.uuid();
        req.setProcId(procId);

        //获得新增巡检工单的参数
        ProcessInfo processInfo = this.saveInspectionProcParamToProcessInfo(req, nowDate, operate);

        //调用新增工单的方法
        Result addProcBaseResult = null;
        //是否重新生成工单
        if (isRegenerate) {
            addProcBaseResult  = procBaseService.regenerateProc(processInfo);
        } else {
            addProcBaseResult  = procBaseService.addProcBase(processInfo);
        }

        //成功的结果
        Integer successResultCode = ResultCode.SUCCESS;

        if (ObjectUtils.isEmpty(addProcBaseResult) || !successResultCode.equals(addProcBaseResult.getCode())) {
            return ResultUtils.success(ProcBaseResultCode.PROC_ADD_ERROR, I18nUtils.getString(ProcBaseI18n.ADD_PROC_FAIL));
        }

        List<ProcBase> procBaseList = new ArrayList<>();
        procBaseList.add(processInfo.getProcBase());

        if (isRegenerate) {
            //重新生成工单日志
            this.regenerateProcInspectionLog(req, procBaseList, isAlarmViewCall);
        } else {
            //新增日志
            this.addProcInspectionLog(req, procBaseList, isAlarmViewCall);
        }
        return null;
    }

    /**
     * 修改巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param req 修改巡检工单请求
     * @param operate 操作
     * @return 返回修改巡检工单结果
     */
    @Override
    public Result updateInspectionProc(ProcInspectionReq req, String operate) {
        //巡检工单编号
        String procId = req.getProcId();
        //当前时间
        Date nowDate = new Date();
        //查询工单
        ProcBase procBase = procBaseService.queryProcBaseById(procId);
        if (null == procBase) {
            //巡检工单不存在
            return ResultUtils.warn(ProcBaseResultCode.PROC_NOT_EXIST, I18nUtils.getString(ProcBaseI18n.PROC_IS_NOT_EXIST));
        }

        //校验参数是否正确
        if (!ProcInspectionReq.validateProcInspectionReq(req, ProcInspectionConstants.OPERATE_UPDATE)) {
            return WorkFlowBusinessMsg.paramErrorMsg();
        }



        //获得新增巡检工单的参数
        ProcessInfo processInfo = this.saveInspectionProcParamToProcessInfo(req, nowDate, operate);

        //调用修改工单的方法
        Result addProcBaseResult = procBaseService.updateProcessById(processInfo);
        //成功的结果
        Integer successResultCode = ResultCode.SUCCESS;
        //新增日志
        List<ProcBase> procBaseList = new ArrayList<>();
        procBaseList.add(processInfo.getProcBase());
        this.updateProcInspectionLog(req, procBaseList);

        //修改巡检工单成功提示信息
        String updateInspectionSuccess = I18nUtils.getString(ProcBaseI18n.UPDATE_PROC_SUCCESS);
        return ResultUtils.success(ResultCode.SUCCESS, updateInspectionSuccess);
    }

    /**
     * 保存巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param req 保存巡检工单请求
     * @param operate 操作
     * @param isAlarmViewCall 是否是告警前台调用
     * @return 保存巡检工单结果
     */
    @Override
    public Result saveProcInspection(ProcInspectionReq req, String operate, String isAlarmViewCall) {
        if (ProcInspectionConstants.OPERATE_UPDATE.equals(operate)) {
            //工单编号
            String procId = req.getProcId();
            if (StringUtils.isEmpty(procId)) {
                //提示缺少工单编号
                return ResultUtils.warn(ProcBaseResultCode.PROC_ID_LOSE, I18nUtils.getString(ProcBaseI18n.PROC_ID_LOSE));
            }
            //修改巡检工单
            return this.updateInspectionProc(req, operate);
        } else {
            //新增巡检工单
            return this.addInspectionProc(req, operate, isAlarmViewCall);
        }
    }

    /**
     * 根据流程编号删除巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 19:40
     * @param isDeleted 是否删除
     * @param procIds 流程编号
     * @return 删除巡检工单结果
     */
    @Override
    public int logicDeleteProcInspection(String isDeleted, List<String> procIds) {
        //删除巡检工单
        return procInspectionDao.logicDeleteProcInspection(isDeleted, procIds);
    }



    /**
     * 保存巡检工单特有信息
     *
     * @param procInspection 巡检工单信息
     * @return int
     */
    @Override
    public int saveProcInspectionSpecific(ProcInspection procInspection) {
        //区分新增和修改
        if (!StringUtils.isEmpty(procInspection.getProcInspectionId())) {
            return procInspectionDao.updateById(procInspection);
        } else {
            procInspection.setProcInspectionId(NineteenUUIDUtils.uuid());
            return procInspectionDao.insert(procInspection);
        }
    }

    /**
     * 查询巡检任务完工记录
     *
     * @param queryCondition 查询对象
     * @return int
     */
    @Override
    public Result queryListInspectionCompleteRecordByPage(QueryCondition<ProcBaseReq> queryCondition) {

        //校验方法参数
        if (null != ValidateUtils.checkQueryConditionParam(queryCondition)) {
            return ValidateUtils.checkQueryConditionParam(queryCondition);
        }

        //设置工单类型为巡检工单
        this.setProcTypeToInspection(queryCondition);
        Result result = procBaseService.queryListProcHisProcByPage(queryCondition);
        //获得页面返回的数据
        result = this.getInspectionVo(result);
        return result;
    }

    /**
     * 查询巡检任务关联工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 20:22
     * @param queryCondition 巡检任务关联工单参数
     */
    @Override
    public Result queryListInspectionTaskRelationProcByPage(QueryCondition<ProcBaseReq> queryCondition) {

        //校验方法参数
        if (null != ValidateUtils.checkQueryConditionParam(queryCondition)) {
            return ValidateUtils.checkQueryConditionParam(queryCondition);
        }

        //获取查询条件过滤之后的条件
        queryCondition = ValidateUtils.filterQueryCondition(queryCondition);

        //设施工单类型为巡检工单
        this.setProcTypeToInspection(queryCondition);
        Result result = procBaseService.queryListRelatedProcByInspectionTaskIdPage(queryCondition);
        //获得页面返回的数据
        result = this.getInspectionTaskRelatedProcVo(result);
        return result;
    }

    /**
     * 查询巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 20:17
     * @param queryCondition 查询对象
     * @return 巡检工单集合
     */
    @Override
    public List<ProcInspection> queryProcInspectionInfo(QueryCondition queryCondition) {
        return procInspectionDao.queryProcInspectionInfo(queryCondition);
    }

    /**
     * 查询巡检工单个数
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 20:17
     * @param queryCondition 查询对象
     * @return 巡检工单个数
     */
    @Override
    public int queryProcInspectionInfoCount(QueryCondition queryCondition) {
        return procInspectionDao.queryProcInspectionInfoCount(queryCondition);
    }

    /**
     * 查询巡检工单详情
     * @author hedongwei@wistronits.com
     * @date  2019/3/20 16:32
     * @param procId 流程编号
     * @return 返回巡检工单详情
     */
    @Override
    public Result getInspectionProcById(String procId) {
        Result result = procBaseService.queryProcessById(procId);
        //查询成功
        if (ResultCode.SUCCESS.equals(result)) {
            ProcessDetail processDetail = (ProcessDetail)result.getData();
            //转换成页面显示的内容
            GetInspectionProcByIdVo procByIdVo = new GetInspectionProcByIdVo();
            BeanUtils.copyProperties(processDetail, procByIdVo);
            result.setData(procByIdVo);
        }
        return result;
    }

    /**
     * 办理巡检工单参数
     * @author hedongwei@wistronits.com
     * @date  2019/3/25 9:25
     * @param completeReq 完结巡检工单参数
     * @return 返回办理巡检工单结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result completeInspectionProc(CompleteInspectionProcReq completeReq) {
        //校验参数
        Result checkResult = this.checkCompleteInspectionProc(completeReq);
        if (null != checkResult) {
            return checkResult;
        }

        ProcBase procBase = procBaseService.getProcBaseByProcId(completeReq.getProcId());

        //获取巡检工单详情
        ProcInspection procInspectionOne = this.getProcInspectionByProcId(completeReq.getProcId());
        boolean checkInspectionTaskInfo = false;
        if (!StringUtils.isEmpty(procInspectionOne.getInspectionTaskId())) {
            checkInspectionTaskInfo = this.checkInspectionTaskStatusComplete(procInspectionOne);
        }


        //获取设施参数集合
        List<ProcInspectionRecordReq> recordReqList = completeReq.getProcInspectionRecords();
        List<ProcInspectionRecord> recordList = new ArrayList<>();
        //有巡检结果的数据
        List<ProcInspectionRecord> completeInspectionList = new ArrayList<>();
        ProcInspectionRecord procInspectionRecord = null;
        Date nowDate = new Date();
        if (!ObjectUtils.isEmpty(recordReqList)) {
            for (ProcInspectionRecordReq recordReqOne : recordReqList) {
                procInspectionRecord = new ProcInspectionRecord();
                BeanUtils.copyProperties(recordReqOne, procInspectionRecord);
                //工单编号
                procInspectionRecord.setProcId(completeReq.getProcId());
                //修改用户
                procInspectionRecord.setUpdateUser(completeReq.getUserId());
                //修改时间
                procInspectionRecord.setUpdateTime(nowDate);
                //巡检时间
                procInspectionRecord.setInspectionTime(nowDate);

                if (!ObjectUtils.isEmpty(procInspectionRecord.getResult())) {
                    completeInspectionList.add(recordReqOne);
                }
                recordList.add(procInspectionRecord);
            }

            //修改已完成巡检设施数量
            this.updateInspectionCompleteCount(procInspectionOne, completeInspectionList);

            //批量修改设施参数信息
            int resultNum = procInspectionRecordService.updateInspectionRecordBatch(recordList);
            //数据库连接异常
            if (0 > resultNum) {
                throw new InspectionDeviceException();
            }
            //修改工单信息
            this.updateProcBaseForCompleteInspection(completeReq, nowDate);
        }


        //提交流程
        this.completeProcInspectionDeviceRecord(completeReq);

        //全部完成需要修改巡检任务状态
        if (checkInspectionTaskInfo && WorkFlowBusinessConstants.COMPLETE_TYPE_COMMIT.equals(completeReq.getOperate())) {
            if (!this.updateInspectionTaskStatus(procInspectionOne)) {
                throw new InspectionDeviceException();
            }
        }

        //新增操作日志
        String functionCode = LogFunctionCodeConstant.RETURN_INSPECTION_FUNCTION_CODE;
        //数据操作类型
        String dataType = LogConstants.DATA_OPT_TYPE_UPDATE;
        //记录app操作日志
        AddLogBean addLogBean = procLogService.getAddProcOperateLogParamForApp(procBase, functionCode, dataType);
        //记录操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);

        //巡检设施成功
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(ProcInspectionI18n.INSPECTION_DEVICE_SUCCESS));
    }

    /**
     * 校验完成巡检任务的参数
     * @author hedongwei@wistronits.com
     * @date  2019/5/8 16:22
     * @param completeReq 完成参数
     * @return 提示信息
     */
    public Result checkCompleteInspectionProc(CompleteInspectionProcReq completeReq) {
        //校验参数结果
        Result checkResult = CompleteInspectionProcReq.checkCompleteInspectionProc(completeReq);
        if (null != checkResult) {
            //返回校验结果
            return checkResult;
        }
        //校验工单编号
        Result procIdCheckResult = procBaseService.checkProcId(completeReq.getProcId());
        if (null != procIdCheckResult) {
            return procIdCheckResult;
        }

        //查询工单信息
        ProcBase procBase = procBaseService.getProcBaseByProcId(completeReq.getProcId());
        if (!ObjectUtils.isEmpty(procBase)) {
            String status = procBase.getStatus();
            if (!ProcBaseConstants.PROC_STATUS_PROCESSING.equals(status)) {
                //不是进行中状态的工单不能巡检设施
                throw new InspectionDeviceException();
            }
        } else {
            //不是进行中状态的工单不能巡检设施
            throw new InspectionDeviceException();
        }

        //是否巡检完全部设施
        if (WorkFlowBusinessConstants.COMPLETE_TYPE_COMMIT.equals(completeReq.getOperate())) {
            Result inspectionDeviceAll = this.isInspectionDeviceAll(completeReq);
            if (null != inspectionDeviceAll) {
                return inspectionDeviceAll;
            }
        }
        return null;
    }

    /**
     * app巡检修改工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/7 19:23
     * @param completeReq 巡检参数
     * @param nowDate 当前时间
     */
    public void updateProcBaseForCompleteInspection(CompleteInspectionProcReq completeReq, Date nowDate) {
        if (WorkFlowBusinessConstants.COMPLETE_TYPE_COMMIT.equals(completeReq.getOperate())) {
            ProcBase procBase = new ProcBase();
            procBase.setProcId(completeReq.getProcId());
            procBase.setUpdateUser(completeReq.getUserId());
            procBase.setUpdateTime(nowDate);
            procBase.setRealityCompletedTime(nowDate);
            //修改工单实际完成时间
            if (!procBaseService.updateById(procBase)) {
                throw new InspectionDeviceException();
            }
        }
    }

    /**
     * 修改巡检完成数量
     * @author hedongwei@wistronits.com
     * @date  2019/4/25 9:59
     * @param procInspectionOne 巡检信息
     * @param completeInspectionList   完成巡检集合
     */
    public void updateInspectionCompleteCount(ProcInspection procInspectionOne, List<ProcInspectionRecord> completeInspectionList) {
        if (!ObjectUtils.isEmpty(procInspectionOne)) {
            int completeInspectionCount = this.getCompleteInspectionCount(procInspectionOne, completeInspectionList);
            ProcInspection updateProcInspection = new ProcInspection();
            updateProcInspection.setProcInspectionId(procInspectionOne.getProcInspectionId());
            int deviceCompleteCount = procInspectionOne.getInspectionCompletedCount() + completeInspectionCount;
            updateProcInspection.setInspectionCompletedCount(deviceCompleteCount);
            procInspectionDao.updateById(updateProcInspection);
        }
    }


    /**
     * 获取完成的巡检数量
     * @author hedongwei@wistronits.com
     * @date  2019/4/25 9:54
     * @param procInspectionOne 巡检工单详情
     * @param completeInspectionList 完成巡检信息
     * @return 获取完成的巡检数量
     */
    public int getCompleteInspectionCount(ProcInspection procInspectionOne, List<ProcInspectionRecord> completeInspectionList) {
        //比较是否删除
        //查询已经巡检的巡检设施信息
        List<ProcInspectionRecord> isCompleteRecord = procInspectionRecordService.queryIsInspectionDeviceList(procInspectionOne.getProcId());
        Map<String, List<ProcInspectionRecord>> isCompleteRecordMap = CastMapUtil.getProcInspectionRecord(isCompleteRecord);
        int completeInspectionCount = 0;
        if (!ObjectUtils.isEmpty(isCompleteRecordMap) && !ObjectUtils.isEmpty(completeInspectionList)) {
            for (ProcInspectionRecord procInspectionRecordOne : completeInspectionList) {
                if (isCompleteRecordMap.containsKey(procInspectionOne.getProcId())) {
                    List<ProcInspectionRecord> procInspectionRecordList = isCompleteRecordMap.get(procInspectionOne.getProcId());
                    if (!ObjectUtils.isEmpty(procInspectionRecordList)) {
                        boolean flag = true;
                        for (ProcInspectionRecord procRecordOne : procInspectionRecordList) {
                            if (procInspectionRecordOne.getDeviceId().equals(procRecordOne.getDeviceId())) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            completeInspectionCount ++;
                        }
                    }
                }
            }
        } else if (!ObjectUtils.isEmpty(isCompleteRecordMap)) {
            completeInspectionCount = isCompleteRecord.size();
        } else if (!ObjectUtils.isEmpty(completeInspectionList)) {
            completeInspectionCount = completeInspectionList.size();
        }
        return completeInspectionCount;
    }

    /**
     *
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 19:45
     * @param req
     */
    public Result isInspectionDeviceAll(CompleteInspectionProcReq req) {
        //校验是否所有设施全部巡检完毕
        List<ProcInspectionRecord> notInspection = procInspectionRecordService.queryNotInspectionDeviceList(req.getProcId());
        List<ProcInspectionRecordReq> inspectionRecordList = req.getProcInspectionRecords();
        if (!ObjectUtils.isEmpty(notInspection) && !ObjectUtils.isEmpty(inspectionRecordList)) {
            int i = 0;
            for (ProcInspectionRecord notInspectionOne : notInspection) {
                for (ProcInspectionRecordReq procInspectionRecordReq : inspectionRecordList) {
                    if (notInspectionOne.getDeviceId().equals(procInspectionRecordReq.getDeviceId())) {
                        i ++;
                    }
                }
            }
            if (i != notInspection.size()) {
                //没有巡检完所有设施
                return ResultUtils.warn(ProcInspectionResultCode.INSPECTION_DEVICE_NOT_COMPLETE, I18nUtils.getString(ProcInspectionI18n.INSPECTION_DEVICE_NOT_COMPLETE));
            }
        } else if (!ObjectUtils.isEmpty(notInspection)){
            //没有巡检完所有设施
            return ResultUtils.warn(ProcInspectionResultCode.INSPECTION_DEVICE_NOT_COMPLETE, I18nUtils.getString(ProcInspectionI18n.INSPECTION_DEVICE_NOT_COMPLETE));
        }
        return null;
    }

    /**
     * 根据工单编号查询巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 12:51
     * @param procId 工单编号
     * @return 获取巡检工单信息
     */
    @Override
    public ProcInspection getProcInspectionByProcId(String procId) {
        //查询工单信息
        ProcInspection inspectionParam = new ProcInspection();
        inspectionParam.setProcId(procId);
        inspectionParam.setIsDeleted(WorkFlowBusinessConstants.IS_NOT_DELETED);
        ProcInspection procInspectionOne = procInspectionDao.selectOne(inspectionParam);
        return procInspectionOne;
    }

    /**
     * 根据工单编号查询巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 19:47
     * @param req 工单参数
     * @return 巡检工单记录信息
     */
    @Override
    public List<ProcInspection> selectInspectionProcForProcIds(ProcBaseReq req) {
        return procInspectionDao.selectInspectionProcForProcIds(req);
    }


    /**
     * 修改巡检记录为巡检完成
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 11:40
     * @param procInspectionOne 巡检记录
     * @return 修改巡检记录为巡检完成
     */
    @Override
    public boolean checkInspectionTaskStatusComplete(ProcInspection procInspectionOne) {

        //流程编号
        String procId = procInspectionOne.getProcId();

        //查询巡检任务
        List<ProcBase> procBaseList = procBaseService.selectProcBaseListByProcInspection(procInspectionOne);
        if (!ObjectUtils.isEmpty(procBaseList)) {
            if (1 == procBaseList.size()) {
                ProcBase procBaseOne = procBaseList.get(0);
                if (!procId.equals(procBaseOne.getProcId())) {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * 修改巡检记录为巡检完成
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 11:40
     * @param procInspectionOne 巡检记录
     * @return 修改巡检记录为巡检完成
     */
    @Override
    public boolean updateInspectionTaskStatus(ProcInspection procInspectionOne) {
        //巡检状态巡检完成,当最后一个巡检
        String inspectionTaskStatus = InspectionTaskConstants.INSPECTION_TASK_COMPLETED;
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setProcInspection(procInspectionOne);
        if (!procBaseService.updateInspectionTaskStatus(processInfo, inspectionTaskStatus)) {
            return false;
        }
        return true;
    }





    /**
     * 巡检设施流程提交
     * @author hedongwei@wistronits.com
     * @date  2019/4/2 14:03
     * @param completeReq 办结参数
     */
    public void completeProcInspectionDeviceRecord(CompleteInspectionProcReq completeReq) {
        //提交操作的时候
        if (WorkFlowBusinessConstants.COMPLETE_TYPE_COMMIT.equals(completeReq.getOperate())) {
            //修改工单的实际完成时间
            ProcBase procBaseParam = new ProcBase();
            procBaseParam.setRealityCompletedTime(new Date());
            procBaseParam.setProcId(completeReq.getProcId());
            procBaseParam.setUpdateTime(new Date());
            procBaseParam.setUpdateUser(completeReq.getUserId());
            procBaseService.updateById(procBaseParam);

            Integer successResultCode = ResultCode.SUCCESS;
            Result result = this.commitProcInspection(completeReq);
            if (ObjectUtils.isEmpty(result) || !successResultCode.equals(result.getCode())) {
                throw new InspectionDeviceException();
            }
        }
    }

    /**
     * 提交巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 13:11
     * @param completeReq 提交参数
     * @return 提交巡检工单结果
     */
    public Result commitProcInspection(CompleteInspectionProcReq completeReq) {
        ProcBase procBase = new ProcBase();
        procBase.setProcId(completeReq.getProcId());
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
     * 新增巡检工单参数转换成processInfo
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 13:54
     * @param req 新增巡检工单过程参数
     * @param nowDate 当前时间
     * @param operate 操作
     * @return 流程信息
     */
    public ProcessInfo saveInspectionProcParamToProcessInfo(ProcInspectionReq req, Date nowDate, String operate) {
        //新增操作
        String operateAdd = ProcInspectionConstants.OPERATE_ADD;
        //用户编号
        String userId = "";
        //巡检工单参数
        ProcInspection procInspection = null;
        //流程参数
        ProcBase procBase = null;
        if (operateAdd.equals(operate)) {
            //获取巡检结束时间
            Date inspectionEndTime = this.getInspectionEndTime(req, nowDate);
            //流程主表
            procBase = this.getInsertProcBase(req, inspectionEndTime, nowDate);
            //登录用户编号
            userId = procBase.getCreateUser();
            //新增巡检工单
            procInspection = ProcInspection.getInsertProcInspection(req, inspectionEndTime, nowDate, userId);
        } else {
            //流程主表
            procBase = this.getUpdateProcBase(req, nowDate);
            //修改巡检工单
            procInspection = ProcInspection.getUpdateProcInspection(req, nowDate, userId);
            //查询巡检工单信息
            ProcInspection inspectionOne = procInspectionDao.selectInspectionProcByProcId(procBase.getProcId());
            if (!ObjectUtils.isEmpty(inspectionOne)) {
                procInspection.setProcInspectionId(inspectionOne.getProcInspectionId());
            }
            //登录用户编号
            userId = procBase.getUpdateUser();
        }

        ProcBaseReq procBaseReq = new ProcBaseReq();
        if (null != procBase) {
            BeanUtils.copyProperties(procBase, procBaseReq);
        }

        //获取关联设施
        List<ProcRelatedDevice> insertDeviceList = this.getInspectionDeviceList(req, nowDate, userId);
        //获取关联单位
        List<ProcRelatedDepartment> insertDepartmentList = this.getInsertDepartmentList(req, nowDate, userId);
        //获取巡检记录信息
        List<ProcInspectionRecord> inspectionRecordList = this.getProcInspectionRecordList(req, insertDeviceList);

        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setProcBase(procBase);
        processInfo.setProcBaseReq(procBaseReq);
        processInfo.setProcInspection(procInspection);
        processInfo.setProcInspectionRecords(inspectionRecordList);
        processInfo.setProcRelatedDepartments(insertDepartmentList);
        processInfo.setProcRelatedDevices(insertDeviceList);
        return processInfo;
    }


    /**
     * 新增巡检工单日志
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 12:46
     * @param req 新增巡检工单参数
     * @param procBaseList 工单集合
     * @param isAlarmViewCall 告警前台调用
     */
    public void addProcInspectionLog(ProcInspectionReq req, List<ProcBase> procBaseList, String isAlarmViewCall) {
        //新增巡检工单
        String dataOptType = LogConstants.DATA_OPT_TYPE_ADD;
        //功能项
        String functionCode = LogFunctionCodeConstant.ADD_PROC_INSPECTION_FUNCTION_CODE;
        //日志类型
        String logType = "";
        if (ProcInspectionConstants.IS_ALARM_VIEW_CALL.equals(isAlarmViewCall)) {
            logType = LogConstants.LOG_TYPE_OPERATE;
        } else if (ProcBaseConstants.PROC_RESOURCE_TYPE_2.equals(req.getProcResourceType())) {
            logType = LogConstants.LOG_TYPE_SYSTEM;
        } else if (ProcBaseConstants.PROC_RESOURCE_TYPE_1.equals(req.getProcResourceType())) {
            logType = LogConstants.LOG_TYPE_OPERATE;
        }
        //新增日志
        this.addInspectionProcLogBatch(procBaseList, logType, dataOptType, functionCode);
    }

    /**
     * 重新生成巡检工单日志
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 12:46
     * @param req 重新生成巡检工单参数
     * @param procBaseList 工单集合
     * @param isAlarmViewCall 告警前台调用
     */
    public void regenerateProcInspectionLog(ProcInspectionReq req, List<ProcBase> procBaseList, String isAlarmViewCall) {
        //重新生成巡检工单
        String dataOptType = LogConstants.DATA_OPT_TYPE_ADD;
        //功能项
        String functionCode = LogFunctionCodeConstant.REGENERATE_PROC_INSPECTION_FUNCTION_CODE;
        //日志类型
        String logType = "";
        if (ProcInspectionConstants.IS_ALARM_VIEW_CALL.equals(isAlarmViewCall)) {
            logType = LogConstants.LOG_TYPE_OPERATE;
        } else if (ProcBaseConstants.PROC_RESOURCE_TYPE_2.equals(req.getProcResourceType())) {
            logType = LogConstants.LOG_TYPE_SYSTEM;
        } else if (ProcBaseConstants.PROC_RESOURCE_TYPE_1.equals(req.getProcResourceType())) {
            logType = LogConstants.LOG_TYPE_OPERATE;
        }
        //新增日志
        this.addInspectionProcLogBatch(procBaseList, logType, dataOptType, functionCode);
    }

    /**
     * 修改巡检工单日志
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 12:46
     * @param req 修改巡检工单参数
     * @param procBaseList 工单集合
     */
    public void updateProcInspectionLog(ProcInspectionReq req, List<ProcBase> procBaseList) {
        //新增巡检工单
        String dataOptType = LogConstants.DATA_OPT_TYPE_UPDATE;
        //功能项
        String functionCode = LogFunctionCodeConstant.UPDATE_PROC_INSPECTION_FUNCTION_CODE;
        //日志类型
        String logType = LogConstants.LOG_TYPE_OPERATE;
        //新增日志
        this.addInspectionProcLogBatch(procBaseList, logType, dataOptType, functionCode);
    }


    /**
     * 新增巡检工单日志
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 14:12
     * @param procBaseList 工单日志
     * @param logType 日志类型
     * @param dataOptType 数据类型
     * @param functionCode 功能项
     */
    public void addInspectionProcLogBatch(List<ProcBase> procBaseList, String logType, String dataOptType, String functionCode) {
        List<AddLogBean> addLogBeanList = new ArrayList<>();
        if (null != procBaseList && 0 < procBaseList.size()) {
            for (ProcBase procBase : procBaseList) {
                AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
                addLogBean.setDataId(ProcInspectionConstants.PROC_INSPECTION_ID_ATTR_NAME);
                addLogBean.setDataName(ProcInspectionConstants.PROC_INSPECTION_NAME_ATTR_NAME);
                //获得操作对象名称
                addLogBean.setOptObj(procBase.getTitle());
                addLogBean.setFunctionCode(functionCode);
                //获得操作对象id
                addLogBean.setOptObjId(procBase.getProcId());
                addLogBean.setDataOptType(dataOptType);
                addLogBeanList.add(addLogBean);
            }

            if (LogConstants.LOG_TYPE_OPERATE.equals(logType)) {
                logProcess.addOperateLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
            } else if (LogConstants.LOG_TYPE_SYSTEM.equals(logType)) {
                logProcess.addSystemLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
            }
        }
    }

    /**
     * 获取设施集合
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 11:22
     * @param req 新增巡检任务参数类
     * @param deviceList 设施集合
     */
    public List<ProcInspectionRecord> getProcInspectionRecordList(ProcInspectionReq req, List<ProcRelatedDevice> deviceList) {
        //流程巡检集合
        List<ProcInspectionRecord> procInspectionList = new ArrayList<>();
        //来源类型
        String resourceType = req.getProcResourceType();
        //巡检任务编号
        String inspectionTaskId = "";
        //巡检任务名称
        String inspectionTaskName = "";
        if (ProcBaseConstants.PROC_RESOURCE_TYPE_2.equals(resourceType)) {
            //巡检任务编号
            inspectionTaskId = req.getInspectionTaskId();
            //巡检任务名称
            inspectionTaskName = req.getInspectionTaskName();
        }

        //设施集合
        if (null != deviceList && 0 < deviceList.size()) {
            //巡检记录表
            for (ProcRelatedDevice relatedDevice : deviceList) {
                ProcInspectionRecord procInspectionRecord = new ProcInspectionRecord();
                BeanUtils.copyProperties(relatedDevice, procInspectionRecord);
                if (!StringUtils.isEmpty(inspectionTaskId) && !StringUtils.isEmpty(inspectionTaskName)) {
                    procInspectionRecord.setInspectionTaskId(inspectionTaskId);
                    procInspectionRecord.setInspectionTaskName(inspectionTaskName);
                }
                procInspectionRecord.setProcInspectionRecordId(NineteenUUIDUtils.uuid());
                procInspectionList.add(procInspectionRecord);
            }
        }
        return procInspectionList;
    }

    /**
     * 获取新增的关联
     * @author hedongwei@wistronits.com
     * @date  2019/3/12 17:41
     * @param req 巡检新增参数
     * @param nowDate 当前时间
     * @param userId 用户编号
     * @return 关联单位
     */
    public List<ProcRelatedDepartment> getInsertDepartmentList(ProcInspectionReq req, Date nowDate, String userId) {
        if (null != req.getDeptList() && 0 < req.getDeptList().size()) {
            for (ProcRelatedDepartment deptOne : req.getDeptList()) {
                //关联工单
                deptOne.setProcId(req.getProcId());
                //创建时间
                deptOne.setCreateTime(nowDate);
                //创建用户
                deptOne.setCreateUser(userId);
            }
        }
        //返回单位
        return req.getDeptList();
    }

    /**
     * 获取新增的关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/3/12 17:41
     * @param req 巡检新增参数
     * @param nowDate 当前时间
     * @param userId 用户编号
     * @return 关联设施
     */
    public List<ProcRelatedDevice> getInspectionDeviceList(ProcInspectionReq req, Date nowDate, String userId) {
        if (null != req.getDeviceList() && 0 < req.getDeviceList().size()) {

            for (ProcRelatedDevice deviceOne : req.getDeviceList()) {
                //新增操作时
                //关联工单
                deviceOne.setProcId(req.getProcId());
                //创建时间
                deviceOne.setCreateTime(nowDate);
                //创建用户
                deviceOne.setCreateUser(userId);
            }
        }
        //返回设施
        return req.getDeviceList();
    }

    /**
     * 获取巡检结束时间
     * @author hedongwei@wistronits.com
     * @date  2019/3/12 17:19
     * @param req 巡检流程参数
     * @param nowDate 当前时间
     * @return 巡检结束时间
     */
    public Date getInspectionEndTime(ProcInspectionReq req, Date nowDate) {
        //计划完成时间
        //巡检任务生成的巡检工单
        Date inspectionEndTime = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        if (ProcBaseConstants.PROC_RESOURCE_TYPE_2.equals(req.getProcResourceType())) {
            inspectionEndTime = new Date(req.getInspectionEndDate());
        } else if (ProcBaseConstants.PROC_RESOURCE_TYPE_3.equals(req.getProcResourceType())) {
            //周期
            String period = req.getAlarmCompleteTime();
            int periodInt = Integer.parseInt(period);
            //算出计划完成时间 = 创建时间 + 周期
            calendar.add(Calendar.DATE, periodInt);
            inspectionEndTime = calendar.getTime();
        } else if (!ObjectUtils.isEmpty(req.getInspectionEndDate())){
            inspectionEndTime = new Date(req.getInspectionEndDate());
        }
        //返回巡检结束时间
        return inspectionEndTime;
    }

    /**
     * 获取新增流程主表信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/12 17:24
     * @param req 新增巡检任务参数
     * @param inspectionEndTime 期望完工时间
     * @param nowDate 当前时间
     * @return 新增流程主表信息
     */
    public ProcBase getInsertProcBase(ProcInspectionReq req, Date inspectionEndTime, Date nowDate) {
        ProcBase procBase = new ProcBase();
        BeanUtils.copyProperties(req, procBase);
        //计划完成时间
        procBase.setExpectedCompletedTime(inspectionEndTime);
        //创建时间
        procBase.setCreateTime(nowDate);
        //工单类型
        procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
        //工单来源为1
        if (ProcBaseConstants.PROC_RESOURCE_TYPE_1.equals(req.getProcResourceType())) {
            //新增用户信息
            procBase.setCreateUser(RequestHeaderUtils.getHeadParam(RequestHeaderConstants.PARAM_USER_ID));
        } else {
            procBase.setCreateUser(req.getUserId());
        }
        //工单状态为待指派
        procBase.setStatus(ProcBaseConstants.PROC_STATUS_ASSIGNED);

        List<ProcRelatedDepartment> deptList = req.getDeptList();
        procBase = ProcBase.setDeptTypeAndStatus(deptList, procBase);

        return procBase;
    }

    /**
     * 获取修改流程主表信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/12 17:24
     * @param req 修改巡检任务参数
     * @param nowDate 当前时间
     * @return 修改流程主表信息
     */
    public ProcBase getUpdateProcBase(ProcInspectionReq req, Date nowDate) {
        ProcBase procBase = new ProcBase();
        BeanUtils.copyProperties(req, procBase);
        //工单类型
        procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
        //预计完成时间
        if (!ObjectUtils.isEmpty(req.getInspectionEndDate())) {
            Long endDate = req.getInspectionEndDate();
            procBase.setExpectedCompletedTime(new Date(endDate));
        }

        //工单状态为待指派
        procBase.setStatus(ProcBaseConstants.PROC_STATUS_ASSIGNED);

        //单位类型
        List<ProcRelatedDepartment> deptList = req.getDeptList();
        procBase = ProcBase.setDeptTypeAndStatus(deptList, procBase);

        //修改时间
        procBase.setUpdateTime(nowDate);
        //修改人
        procBase.setUpdateUser(RequestHeaderUtils.getHeadParam(RequestHeaderConstants.PARAM_USER_ID));
        return procBase;
    }



    /**
     * 设置工单类型为巡检工单
     *
     * @param queryCondition 查询封装类
     * @return QueryCondition<ProcBaseReq>
     */
    @Override
    public QueryCondition<ProcBaseReq> setProcTypeToInspection(QueryCondition<ProcBaseReq> queryCondition){
        ProcBaseReq procBaseReq = (ProcBaseReq)queryCondition.getBizCondition();
        if (ObjectUtils.isEmpty(procBaseReq)){
            procBaseReq = new ProcBaseReq();
        }
        procBaseReq.setProcType(ProcBaseConstants.PROC_INSPECTION);
        queryCondition.setBizCondition(procBaseReq);
        return queryCondition;
    }

    /**
     * app巡检工单下载
     *
     * @param procBaseRespForApps 工单下载列表
     *
     * @return List<InspectionDownLoadDetail>
     */
    @Override
    public List<InspectionDownLoadDetail> downLoadInspectionProcForApp(List<ProcBaseRespForApp> procBaseRespForApps) {
        List<InspectionDownLoadDetail> inspectionDownLoadDetails = new ArrayList<>();
        for (ProcBaseRespForApp procBaseRespForApp : procBaseRespForApps){
            InspectionDownLoadDetail inspectionDownLoadDetail = new InspectionDownLoadDetail();

            if (!ObjectUtils.isEmpty(procBaseRespForApp.getProcNotInspectionDeviceList())
                    && !ObjectUtils.isEmpty(procBaseRespForApp.getProcRelatedDeviceRespList())) {
                //工单返回类的设施信息
                List<ProcRelatedDeviceResp> deviceRespList = procBaseRespForApp.getProcRelatedDeviceRespList();
                //没有巡检的设施信息
                List<ProcInspectionRecord> notRecordList = procBaseRespForApp.getProcNotInspectionDeviceList();
                Map<String, ProcInspectionRecord> recordMap = CastMapUtil.getProcInspectionRecordMapForProcId(notRecordList);
                List<ProcRelatedDeviceResp> deleteRelatedDeviceList = new ArrayList<>();
                for (ProcRelatedDeviceResp deviceOne : deviceRespList) {
                    //是否存在未巡检的数据， 存在未巡检的数据需要把已巡检的数据删除
                    if (!recordMap.containsKey(deviceOne.getDeviceId())) {
                        deleteRelatedDeviceList.add(deviceOne);
                    }
                }

                if (!ObjectUtils.isEmpty(deleteRelatedDeviceList)) {
                    deviceRespList.removeAll(deleteRelatedDeviceList);
                    procBaseRespForApp.setProcRelatedDeviceRespList(deviceRespList);
                }
            }


            //工单内容
            BeanUtils.copyProperties(procBaseRespForApp, inspectionDownLoadDetail);

            //工单开始时间
            if (!ObjectUtils.isEmpty(procBaseRespForApp.getCreateTime())) {
                inspectionDownLoadDetail.setStartTime(procBaseRespForApp.getCreateTime().getTime());
            }

            //工单结束时间
            if (!ObjectUtils.isEmpty(procBaseRespForApp.getExpectedCompletedTime())) {
                inspectionDownLoadDetail.setEndTime(procBaseRespForApp.getExpectedCompletedTime().getTime());
            }

            inspectionDownLoadDetails.add(inspectionDownLoadDetail);
        }

        return inspectionDownLoadDetails;
    }

    /**
     * 巡检任务手动调用新增巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 9:00
     * @param inspectionTaskId 巡检任务编号
     */
    @Override
    @Async
    public void manualAddInspectionProc(String inspectionTaskId) {
        InspectionTask inspectionTaskParam = new InspectionTask();
        inspectionTaskParam.setInspectionTaskId(inspectionTaskId);
        InspectionTask inspectionTaskOne = inspectionTaskService.getInspectionTaskOne(inspectionTaskParam);
        if (!ObjectUtils.isEmpty(inspectionTaskOne) && !ObjectUtils.isEmpty(inspectionTaskOne.getTaskStartTime())) {
            Long taskStartTime = inspectionTaskOne.getTaskStartTime().getTime();
            Long nowTime = System.currentTimeMillis();
            //现在时间大于开始时间
            if (nowTime >= taskStartTime) {
                InspectionTask inspectionTask = new InspectionTask();
                inspectionTask.setInspectionTaskId(inspectionTaskId);
                this.jobAddInspectionProc(inspectionTask);
            }
        }

    }

    /**
     * 巡检任务新增巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/16 19:53
     * @param inspectionTask 新增巡检工单参数
     */
    @Override
    public void jobAddInspectionProc(InspectionTask inspectionTask) {
        Date nowDate = this.getDateFormat(new Date());
        Long nowDateTime = nowDate.getTime();

        if (!ObjectUtils.isEmpty(inspectionTask.getInspectionTaskId())) {
            String inspectionTaskId = inspectionTask.getInspectionTaskId();
            //根据巡检任务编号查询巡检任务信息
            InspectionTaskDetailBean taskDetail = inspectionTaskService.getInspectionDetailInfo(inspectionTaskId);
            if (!ObjectUtils.isEmpty(taskDetail)) {

                //是否开启巡检任务
                String isOpen = taskDetail.getIsOpen();
                if (InspectionTaskConstants.IS_OPEN.equals(isOpen)) {
                    //巡检开始时间
                    Date taskStart = taskDetail.getTaskStartTime();
                    //巡检结束时间
                    Date taskEnd = taskDetail.getTaskEndTime();
                    //周期
                    int taskPeriod = taskDetail.getTaskPeriod();

                    if (!ObjectUtils.isEmpty(taskStart) && !ObjectUtils.isEmpty(taskPeriod)) {
                        Date taskStartDate = this.getDateFormat(taskStart);
                        //任务开始时间
                        Long taskStartTime = taskStartDate.getTime();


                        Date taskEndDate = null;
                        Long taskEndTime = null;
                        if (!ObjectUtils.isEmpty(taskEnd)) {
                            taskEndDate = this.getDateFormat(taskEnd);
                            //任务结束时间
                            taskEndTime = taskEndDate.getTime();
                        }

                        //相差月份
                        int diffMonth = DateUtilInfos.getMonthNum(taskStartDate, nowDate);

                        //判断周期是否整除
                        if (diffMonth >= 0 && diffMonth % taskPeriod == 0 ) {
                            //新增巡检工单
                            this.inspectionTaskInsertProcInspection(taskStartTime, taskEndTime, nowDateTime, taskDetail);
                            //时间范围大于结束时间， 巡检任务状态变成禁用
                            this.inspectionTaskStatusIsClose(taskEndTime, nowDateTime, inspectionTaskId);
                        }
                    }
                }
            }
        }
    }

    /**
     * 巡检任务生成巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/4/22 9:55
     * @param taskStartTime 巡检开始时间
     * @param taskEndTime 巡检任务结束时间
     * @param nowDateTime 当前时间
     * @param taskDetail 巡检任务信息
     */
    public void inspectionTaskInsertProcInspection(Long taskStartTime ,Long taskEndTime, Long nowDateTime, InspectionTaskDetailBean taskDetail) {
        //判断时间范围是否大于等于开始时间， 小于等于结束时间，或者是一直开启
        if (nowDateTime >= taskStartTime) {
            if (!ObjectUtils.isEmpty(taskEndTime) && nowDateTime <= taskEndTime) {
                //新增巡检工单
                this.addInspectionProcForJob(taskDetail);
            }
            if (ObjectUtils.isEmpty(taskEndTime)) {
                //新增巡检工单
                this.addInspectionProcForJob(taskDetail);
            }
        }
    }

    /**
     * 巡检状态变成禁用
     * @author hedongwei@wistronits.com
     * @date  2019/4/22 9:50
     * @param taskEndTime 任务完成时间
     * @param nowDateTime 当前时间
     * @param inspectionTaskId 巡检任务编号
     */
    public void inspectionTaskStatusIsClose(Long taskEndTime, Long nowDateTime, String inspectionTaskId) {
        //时间范围大于结束时间， 巡检任务状态变成禁用
        if (!ObjectUtils.isEmpty(taskEndTime)) {
            if (taskEndTime < nowDateTime) {
                InspectionTask inspectionTaskParam = new InspectionTask();
                inspectionTaskParam.setInspectionTaskId(inspectionTaskId);
                //巡检任务状态变成禁用
                inspectionTaskParam.setIsOpen(InspectionTaskConstants.IS_CLOSE);
                inspectionTaskService.updateById(inspectionTaskParam);
            }
        }
    }

    /**
     * 转换时间格式为年月日
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 0:20
     * @param date 时间
     * @return 转换时间格式为年月日
     */
    public Date getDateFormat(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = simpleDateFormat.format(date);
        Date retDate = null;
        try {
            retDate = simpleDateFormat.parse(dateString);
        } catch (Exception ex){
            throw new FilinkWorkflowBusinessDataException();
        }
        return retDate;
    }

    /**
     * 定时任务新增巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/16 23:28
     * @param detailBean 详情类
     */
    public Result addInspectionProcForJob(InspectionTaskDetailBean detailBean) {
        Result result;
        //巡检区域编号
        String areaId = detailBean.getInspectionAreaId();
        //巡检区域名称
        String areaName = this.getInspectionAreaName(areaId);

        //获得巡检任务关联设施
        List<InspectionTaskDevice> deviceList = detailBean.getDeviceList();
        //巡检任务关联设施编号
        List<String> deviceIds = this.getDeviceIds(deviceList);
        //查询关联设施信息，包含设施的所有信息
        List<DeviceInfoDto> deviceInfoDtoList = this.getDeviceInfoDtoList(deviceIds);

        //设施对象map
        Map<String, DeviceInfoDto> deviceInfoDtoMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (null != deviceInfoDtoList && 0 < deviceInfoDtoList.size()) {
            for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList) {
                String deviceId = deviceInfoDto.getDeviceId();
                deviceInfoDtoMap.put(deviceId, deviceInfoDto);
            }
        }

        //获得巡检任务关联单位
        List<InspectionTaskDepartment> deptList = detailBean.getDeptList();

        //生成巡检工单 生成一条巡检工单
        AddInspectionTaskBean addInspectionTaskBean = new AddInspectionTaskBean();
        addInspectionTaskBean.setDetailBean(detailBean);
        addInspectionTaskBean.setDeptList(deptList);
        addInspectionTaskBean.setDeviceInfoDtoMap(deviceInfoDtoMap);
        addInspectionTaskBean.setDeviceList(deviceList);
        //新增巡检任务结果
        result = this.generateInspectionProc(addInspectionTaskBean, areaName);
        return result;
    }

    /**
     * 新增巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 20:16
     * @param addInspectionTaskBean 新增巡检任务信息
     * @param areaName 区域名称
     * @return 返回新增巡检任务结果
     */
    public Result generateInspectionProc(AddInspectionTaskBean addInspectionTaskBean, String areaName) {
        //巡检任务详情信息
        InspectionTaskDetailBean detailBean = addInspectionTaskBean.getDetailBean();
        //巡检任务关联设施信息
        List<InspectionTaskDevice> deviceList = addInspectionTaskBean.getDeviceList();
        //设施对象map
        Map<String, DeviceInfoDto> deviceInfoDtoMap = addInspectionTaskBean.getDeviceInfoDtoMap();
        //部门集合
        List<InspectionTaskDepartment> deptList = addInspectionTaskBean.getDeptList();

        ProcInspectionReq inspectionReq = new ProcInspectionReq();
        detailBean.setDeptList(null);
        detailBean.setDeviceList(null);
        BeanUtils.copyProperties(detailBean, inspectionReq);

        List<ProcRelatedDevice> relatedDeviceList = new ArrayList<>();
        //设施类型map
        Map<String, List<ProcRelatedDevice>> deviceTypeMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        //关联设施
        if (null != deviceList && 0 < deviceList.size()) {
            ProcRelatedDevice relatedDevice;
            for (InspectionTaskDevice deviceOne : deviceList) {
                relatedDevice = new ProcRelatedDevice();
                BeanUtils.copyProperties(deviceOne, relatedDevice);
                String deviceOneId = deviceOne.getDeviceId();
                if (!ObjectUtils.isEmpty(deviceInfoDtoMap.get(deviceOneId))) {
                    String deviceType = deviceInfoDtoMap.get(deviceOneId).getDeviceType();
                    String deviceName = deviceInfoDtoMap.get(deviceOneId).getDeviceName();
                    relatedDevice.setDeviceName(deviceName);
                    relatedDevice.setDeviceType(deviceType);
                    relatedDevice.setDeviceAreaName(areaName);
                    List<ProcRelatedDevice> procRelatedDevices = new ArrayList<>();
                    if (deviceTypeMap.containsKey(deviceType)) {
                        procRelatedDevices = deviceTypeMap.get(deviceType);
                    }
                    procRelatedDevices.add(relatedDevice);
                    deviceTypeMap.put(deviceType, procRelatedDevices);
                }
                relatedDeviceList.add(relatedDevice);
            }
        }

        //关联部门
        List<ProcRelatedDepartment> departmentList = this.getProcRelatedDepartmentList(deptList);
        //工单标题
        inspectionReq.setTitle(detailBean.getInspectionTaskName());
        //区域名称
        inspectionReq.setInspectionAreaName(areaName);
        //巡检任务生成
        inspectionReq.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_2);

        Date nowDate = new Date();

        //巡检开始时间
        inspectionReq.setInspectionStartDate(nowDate.getTime());

        //巡检结束时间等于巡检开始时间加上新增天数
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        calendar.add(Calendar.DATE, detailBean.getProcPlanDate());

        //巡检结束时间
        inspectionReq.setInspectionEndDate(calendar.getTime().getTime());
        //关联设施
        inspectionReq.setDeviceList(relatedDeviceList);
        //关联部门
        inspectionReq.setDeptList(departmentList);
        //批量新增巡检工单
        this.createProcInspectionBatchForJob(deviceTypeMap, inspectionReq);
        return ResultUtils.success();
    }

    /**
     * 获取关联单位
     * @author hedongwei@wistronits.com
     * @date  2019/5/14 9:17
     * @param deptList 责任单位集合
     */
    public List<ProcRelatedDepartment> getProcRelatedDepartmentList(List<InspectionTaskDepartment> deptList) {
        List<ProcRelatedDepartment> departmentList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(deptList)) {
            ProcRelatedDepartment relatedDept;
            for (InspectionTaskDepartment deptOne : deptList) {
                relatedDept = new ProcRelatedDepartment();
                BeanUtils.copyProperties(deptOne, relatedDept);
                departmentList.add(relatedDept);
            }
        }
        return departmentList;
    }

    /**
     * 批量创建巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 0:04
     * @param deviceTypeMap 设施map
     * @param inspectionReq 巡检参数
     */
    public void createProcInspectionBatchForJob(Map<String, List<ProcRelatedDevice>> deviceTypeMap, ProcInspectionReq inspectionReq) {
        //工单名称前缀
        String top = SequenceConstants.PROC_INSPECTION_CREATE_FOR_TASK_TOP;
        //工单名称后缀
        String end = SequenceConstants.PROC_INSPECTION_CREATE_FOR_TASK_END;
        if (!ObjectUtils.isEmpty(deviceTypeMap)) {
            for (String deviceTypeOne : deviceTypeMap.keySet()) {
                inspectionReq.setDeviceList(deviceTypeMap.get(deviceTypeOne));
                Calendar calendar = Calendar.getInstance();
                //根据规则生成工单名称
                String title = SequenceUtil.getSequenceInfo(top, end, calendar.getTime());
                inspectionReq.setTitle(title);
                //生成巡检工单
                Result result = this.saveProcInspection(inspectionReq, ProcInspectionConstants.OPERATE_ADD, ProcInspectionConstants.NOT_IS_ALARM_VIEW_CALL);
            }
        }
    }

    /**
     * 获取区域名称
     * @author hedongwei@wistronits.com
     * @date  2019/3/31 9:46
     * @param areaId 区域编号
     * @return 区域名称
     */
    private String getInspectionAreaName(String areaId) {
        String areaName = "";
        List<String> areaIds = new ArrayList<>();
        areaIds.add(areaId);

        //查询巡检任务关联区域信息
        List<AreaInfoForeignDto> areaInfoList = areaFeign.selectAreaInfoByIds(areaIds);

        //巡检区域名称
        if (null != areaInfoList && 0 < areaInfoList.size()) {
            areaName = areaInfoList.get(0).getAreaName();
        }
        return areaName;
    }

    /**
     * 获得巡检任务关联设施编号集合
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 19:30
     * @param deviceList 设施集合
     * @return 获得巡检任务关联设施编号集合
     */
    private List<String> getDeviceIds(List<InspectionTaskDevice> deviceList) {
        List<String> deviceIds = new ArrayList<>();
        if (null != deviceList && 0 < deviceList.size()) {
            for (InspectionTaskDevice deviceOne : deviceList) {
                //设施编号
                deviceIds.add(deviceOne.getDeviceId());
            }
        }
        return deviceIds;
    }

    /**
     * 查询设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 19:35
     * @param deviceIds 设施编号数组
     * @return 获得设施信息
     */
    public List<DeviceInfoDto> getDeviceInfoDtoList(List<String> deviceIds) {
        List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(deviceIds)) {
            String [] deviceListsArray = new String [deviceIds.size()];
            deviceIds.toArray(deviceListsArray);
            //查询巡检任务关联设施根据设施编号
            deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceListsArray);
        }
        return deviceInfoDtoList;
    }

    /**
     * 获取部门编号集合
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 19:39
     * @param deptList 巡检任务关联部门集合
     * @return 获取部门编号集合
     */
    public List<String> getDeptIds(List<InspectionTaskDepartment> deptList) {
        List<String> deptIds = new ArrayList<>();
        if (null != deptList && 0 < deptList.size()) {
            for (InspectionTaskDepartment deptOne : deptList) {
                deptIds.add(deptOne.getInspectionTaskDeptId());
            }
        }
        return deptIds;
    }

    /**
     * 获取部门map
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 19:51
     * @param departmentList 部门集合
     * @return 获取部门map
     */
    public Map<String, String> getDepartmentMap(List<Department> departmentList) {
        Map<String ,String> departmentMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (null != departmentList && 0 < departmentList.size()) {
            for (Department departmentOne : departmentList) {
                departmentMap.put(departmentOne.getId(), departmentOne.getDeptName());
            }
        }
        return departmentMap;
    }

    /**
     *  巡检工单未完工列表导出
     *
     * @param exportDto 巡检工单导出请求
     *
     * @return Result
     */
    @Override
    public Result exportListInspectionProcess(ExportDto exportDto) {
        Export export = null;
        try {
            export = inspectionProcProcessExport.insertTask(exportDto, WorkFlowBusinessConstants.SERVER_NAME, I18nUtils.getString(ProcBaseI18n.PROC_INSPECTION_PROCESS_LIST));
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
        inspectionProcProcessExport.exportData(export);

        //新增日志
        procLogService.addLogByExport(exportDto, LogFunctionCodeConstant.PROC_INSPECTION_PROCESS_EXPORT_CODE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(ProcBaseI18n.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 查询巡检工单前五条
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 19:33
     * @return 巡检工单前五条
     */
    @Override
    public Result queryProcInspectionTopFive(String deviceId) {
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

        //设置工单类型为巡检工单
        this.setProcTypeToInspection(queryCondition);

        List<ProcBaseResp> procBaseRespList = (List<ProcBaseResp>)procBaseService.queryListProcUnfinishedProcByPage(queryCondition).getData();
        //获取工单ids
        List<String> procIds = new ArrayList<>();
        if (!ObjectUtils.isEmpty(procBaseRespList)) {
            //获得工单编号集合
            procIds = CastListUtil.getProcIdsForProcBaseRespList(procIds , procBaseRespList);

            //获取巡检记录信息
            List<ProcInspectionRecordVo> recordVoList = procInspectionRecordService.queryInspectionRecordByProcIds(procIds);
            if (!ObjectUtils.isEmpty(recordVoList)) {
                //巡检记录
                Map<String, List<ProcInspectionRecordVo>> recordVoMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
                for (ProcInspectionRecordVo recordOne : recordVoList) {
                    if (!ObjectUtils.isEmpty(recordOne.getProcId())) {
                        String procId = recordOne.getProcId();
                        List<ProcInspectionRecordVo> recordVoMapOne = new ArrayList<>();
                        if (recordVoMap.containsKey(procId)) {
                            recordVoMapOne = recordVoMap.get(procId);
                        }
                        recordVoMapOne.add(recordOne);
                        //将巡检记录根据工单编号过滤
                        recordVoMap.put(procId, recordVoMapOne);
                    }
                }

                for (ProcBaseResp procBaseResp : procBaseRespList) {
                    procBaseResp.setProcInspectionRecordVoList(recordVoMap.get(procBaseResp.getProcId()));
                }
            }
        }
        return ResultUtils.success(procBaseRespList);
    }



    /**
     *  巡检工单完工列表导出
     *
     * @param exportDto 巡检工单导出请求
     *
     * @return Result
     */
    @Override
    public Result exportListInspectionComplete(ExportDto exportDto) {
        Export export = null;
        try {
            export = inspectionProcCompleteExport.insertTask(exportDto, WorkFlowBusinessConstants.SERVER_NAME, I18nUtils.getString(ProcBaseI18n.PROC_INSPECTION_COMPLETE_LIST));
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
        inspectionProcCompleteExport.exportData(export);
        //新增日志
        procLogService.addLogByExport(exportDto, LogFunctionCodeConstant.PROC_INSPECTION_COMPLETE_EXPORT_CODE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(ProcBaseI18n.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 批量修改巡检设施数量
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 22:55
     * @param list 巡检工单集合
     * @return 修改结果
     */
    @Override
    public void updateProcInspectionDeviceCountBatch(List<ProcInspection> list) {
        if (!ObjectUtils.isEmpty(list)) {
            //批量修改巡检工单设施数量
            int row = procInspectionDao.updateProcInspectionDeviceCountBatch(list);
            if (0 > row) {
                throw new FilinkWorkflowBusinessDataException();
            }
        }
    }

    /**
     * 根据巡检工单编号查询巡检工单完成数量
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 16:50
     * @param procInspection 巡检任务
     * @return 查询巡检工单完成数量
     */
    @Override
    public Result queryProcInspectionByProcInspectionId(ProcInspection procInspection) {
        ProcInspectionResp resp = procInspectionDao.queryProcInspectionByProcInspectionId(procInspection);
        if (!ObjectUtils.isEmpty(resp)) {
            int deviceAllCount = resp.getInspectionDeviceCount();
            int completedCount = resp.getInspectionCompletedCount();
            if (!ObjectUtils.isEmpty(deviceAllCount) && !ObjectUtils.isEmpty(completedCount)) {
                int processCount = deviceAllCount - completedCount;
                //获得未完成巡检设施数量
                resp.setInspectionProcessCount(processCount);
            }
        }
        return ResultUtils.success(resp);
    }
}
