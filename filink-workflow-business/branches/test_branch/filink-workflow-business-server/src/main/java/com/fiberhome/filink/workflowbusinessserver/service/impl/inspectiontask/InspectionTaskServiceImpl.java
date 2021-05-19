package com.fiberhome.filink.workflowbusinessserver.service.impl.inspectiontask;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.AreaInfoForeignDto;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.scheduleapi.api.InspectionTaskFeign;
import com.fiberhome.filink.scheduleapi.bean.InspectionTaskBizBean;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.*;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.workflowbusiness.WorkflowBusinessI18n;
import com.fiberhome.filink.workflowbusinessserver.constant.InspectionTaskConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.workflowbusinessserver.constant.RequestHeaderConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import com.fiberhome.filink.workflowbusinessserver.dao.inspectiontask.InspectionTaskDao;
import com.fiberhome.filink.workflowbusinessserver.dao.inspectiontask.InspectionTaskRelatedJobDao;
import com.fiberhome.filink.workflowbusinessserver.exception.FilinkWorkflowBusinessDataException;
import com.fiberhome.filink.workflowbusinessserver.export.inspectiontask.InspectionTaskListExport;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontask.*;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontask.taskrelated.InspectionTaskDeviceReq;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontaskjob.InspectionTaskRelatedJobReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskDepartmentService;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskDeviceService;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskService;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontaskjob.InspectionTaskJobService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcBaseService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcLogService;
import com.fiberhome.filink.workflowbusinessserver.utils.common.*;
import com.fiberhome.filink.workflowbusinessserver.utils.inspectiontask.InspectionTaskMsg;
import com.fiberhome.filink.workflowbusinessserver.utils.inspectiontask.InspectionTaskResultCode;
import com.fiberhome.filink.workflowbusinessserver.utils.procbase.ProcBaseResultCode;
import com.fiberhome.filink.workflowbusinessserver.utils.request.RequestHeaderUtils;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkFlowBusinessMsg;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkflowBusinessResultCode;
import com.fiberhome.filink.workflowbusinessserver.vo.inspectiontask.GetInspectionTaskByIdVo;
import com.fiberhome.filink.workflowbusinessserver.vo.inspectiontask.QueryListInspectionTaskByPageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPageBean;


/**
 * <p>
 * 巡检任务表 服务实现类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-26
 */
@Service
@RefreshScope
@Slf4j
public class InspectionTaskServiceImpl extends ServiceImpl<InspectionTaskDao, InspectionTask> implements InspectionTaskService {

    @Autowired
    private InspectionTaskDao inspectionTaskDao;

    @Autowired
    private InspectionTaskDeviceService inspectionTaskDeviceService;

    @Autowired
    private InspectionTaskDepartmentService inspectionTaskDepartmentService;

    @Autowired
    private DepartmentFeign departmentFeign;

    @Autowired
    private AreaFeign areaFeign;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private LogProcess logProcess;

    @Autowired
    private InspectionTaskFeign inspectionTaskFeign;

    @Autowired
    private InspectionTaskListExport inspectionTaskListExport;

    @Autowired
    private ProcBaseService procBaseService;

    @Autowired
    private ProcLogService procLogService;

    @Autowired
    private InspectionTaskRelatedJobDao inspectionTaskRelatedJobDao;

    @Autowired
    private InspectionTaskJobService inspectionTaskJobService;

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
     * 新增巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 14:36
     * @param req 新增巡检任务参数
     * @return result 新增巡检任务结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_ADD, logType = LogConstants.LOG_TYPE_OPERATE, functionCode = LogFunctionCodeConstant.INSERT_TASK_FUNCTION_CODE, dataGetColumnName = "inspectionTaskName", dataGetColumnId = "inspectionTaskId")
    public Result insertInspectionTask(InsertInspectionTaskReq req) {
        systemLanguage.querySystemLanguage();
        //查询新增巡检任务的名称是否重复
        InspectionTask inspectionTask = new InspectionTask();
        BeanUtils.copyProperties(req, inspectionTask);
        if (!this.returnInspectionTaskNameResult(inspectionTask)) {
            //提示巡检任务名称重复
            return InspectionTaskMsg.getInspectionNameIsRepectMsg();
        }

        //巡检任务编号
        String inspectionTaskId = NineteenUUIDUtils.uuid();
        req.setInspectionTaskId(inspectionTaskId);

        //判断请求的新增巡检任务的参数是否满足条件
        if (!InsertInspectionTaskReq.validateInsertInspectionTaskReq(req)) {
            //参数异常
            return WorkFlowBusinessMsg.paramErrorMsg();
        }

        //新增巡检任务
        if (!this.insertInspectionTaskProcess(req)) {
            String inspectionTaskMsg = I18nUtils.getSystemString(InspectionTaskI18n.FAIL_INSERT_INSPECTION_TASK);
            //提示新增巡检任务失败
            return ResultUtils.warn(InspectionTaskResultCode.FAIL_INSERT_INSPECTION_TASK, inspectionTaskMsg);
        }

        String successInsertInspection = I18nUtils.getSystemString(InspectionTaskI18n.SUCCESS_INSERT_INSPECTION_TASK);
        //提示新增巡检任务成功
        return ResultUtils.success(ResultCode.SUCCESS, successInsertInspection);
    }


    /**
     * 修改巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 14:36
     * @param req 修改巡检任务参数
     * @return result 修改巡检任务结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_UPDATE, logType = LogConstants.LOG_TYPE_OPERATE, functionCode = LogFunctionCodeConstant.UPDATE_TASK_FUNCTION_CODE, dataGetColumnName = "inspectionTaskName", dataGetColumnId = "inspectionTaskId")
    public Result updateInspectionTask(UpdateInspectionTaskReq req) {
        systemLanguage.querySystemLanguage();
        //判断请求的修改巡检任务的参数是否满足条件
        if (!UpdateInspectionTaskReq.validateUpdateInspectionTaskReq(req)) {
            //不满足参数条件返回提示信息
            return WorkFlowBusinessMsg.paramErrorMsg();
        }

        //查询修改的名称
        InspectionTask inspectionTask = new InspectionTask();
        BeanUtils.copyProperties(req, inspectionTask);
        if (!this.returnInspectionTaskNameResult(inspectionTask)) {
            //提示巡检任务名称重复
            return InspectionTaskMsg.getInspectionNameIsRepectMsg();
        }

        //开始时间
        if (null != req.getTaskStartDate()) {
            inspectionTask.setTaskStartTime(new Date(req.getTaskStartDate()));
        }

        //结束时间
        if (null != req.getTaskEndDate()) {
            inspectionTask.setTaskEndTime(new Date(req.getTaskEndDate()));
        }

        //查询需要修改的数据是否存在
        InspectionTask inspectionTaskInfo = inspectionTaskDao.selectById(req.getInspectionTaskId());
        if (ObjectUtils.isEmpty(inspectionTaskInfo)) {
            //不存在提示信息
            String notExistMsg = I18nUtils.getSystemString(InspectionTaskI18n.INSPECTION_TASK_NOT_EXIST);
            //提示巡检任务不存在不能修改巡检任务数据
            return ResultUtils.warn(InspectionTaskResultCode.INSPECTION_TASK_NOT_EXISTS, notExistMsg);
        }

        //修改巡检任务
        if (!this.updateInspectionTaskProcess(req)) {
            //提示修改失败
            String updateError = I18nUtils.getSystemString(WorkflowBusinessI18n.UPDATE_FAIL);
            return ResultUtils.warn(WorkflowBusinessResultCode.UPDATE_FAIL, updateError);
        }

        //修改巡检任务的提示信息
        String successUpdateInspection = I18nUtils.getSystemString(WorkflowBusinessI18n.UPDATE_SUCCESS);

        //返回提示信息
        return ResultUtils.success(ResultCode.SUCCESS, successUpdateInspection);
    }


    /**
     * 修改巡检任务状态
     * @author hedongwei@wistronits.com
     * @date  2019/3/15 19:16
     * @param req 修改巡检任务状态
     * @return 修改巡检任务状态结果
     */
    @Override
    public Result updateInspectionStatus(UpdateInspectionStatusReq req) {

        //判断参数是否异常
        if (!ObjectUtils.isEmpty(req)) {
            if (StringUtils.isEmpty(req.getInspectionTaskId()) || StringUtils.isEmpty(req.getInspectionTaskStatus())) {
                //参数异常
                return WorkFlowBusinessMsg.paramErrorMsg();
            }
        } else {
            //参数异常
            return WorkFlowBusinessMsg.paramErrorMsg();
        }

        InspectionTask inspectionTask = new InspectionTask();
        BeanUtils.copyProperties(req, inspectionTask);
        //修改巡检状态
        inspectionTaskDao.updateById(inspectionTask);
        //修改巡检任务的提示信息
        String successUpdateInspection = I18nUtils.getSystemString(WorkflowBusinessI18n.UPDATE_SUCCESS);
        //返回提示信息
        return ResultUtils.success(ResultCode.SUCCESS, successUpdateInspection);
    }

    /**
     * 删除巡检任务信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/1 17:31
     * @param req 删除巡检任务参数
     * @return Result 删除巡检任务结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteInspectionTaskByIds(DeleteInspectionTaskByIdsReq req) {
        //删除巡检任务信息
        if (!DeleteInspectionTaskByIdsReq.validateDeleteInspectionTaskByIdsReq(req)) {
            //不满足参数条件返回提示信息
            return WorkFlowBusinessMsg.paramErrorMsg();
        }

        //被删除的巡检任务编号
        List<String> inspectionTaskIds = req.getInspectionTaskIds();

        //判断巡检任务有没有不满足被删除条件的数据
        Result checkInspectionTaskResult = this.selectIsDeleteForInspectionTaskIds(inspectionTaskIds);
        if (null != checkInspectionTaskResult) {
            //提示信息
            return checkInspectionTaskResult;
        }

        //删除巡检任务过程
        this.deleteInspectionTaskProcess(req);

        //修改巡检任务的提示信息
        String successUpdateInspection = I18nUtils.getSystemString(WorkflowBusinessI18n.DELETE_SUCCESS);
        //返回提示信息
        return ResultUtils.success(ResultCode.SUCCESS, successUpdateInspection);
    }

    /**
     * 删除巡检任务过程
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 18:10
     * @param req 删除巡检任务信息
     */
    public void deleteInspectionTaskProcess(DeleteInspectionTaskByIdsReq req) {

        //被删除的巡检任务编号
        List<String> inspectionTaskIds = req.getInspectionTaskIds();

        //删除巡检任务
        if (!this.deleteInspectionTaskBatch(inspectionTaskIds, req.getIsDeleted())) {
            throw new FilinkWorkflowBusinessDataException();
        }

        //删除关联设施
        int deleteDeviceResult = inspectionTaskDeviceService.deleteInspectionTaskDeviceBatch(inspectionTaskIds, req.getIsDeleted());
        if (0 > deleteDeviceResult) {
            throw new FilinkWorkflowBusinessDataException();
        }

        //删除关联单位
        int deleteDeptResult = inspectionTaskDepartmentService.deleteInspectionTaskDepartmentBatch(inspectionTaskIds, req.getIsDeleted());
        if (0 > deleteDeptResult) {
            throw new FilinkWorkflowBusinessDataException();
        }

        //删除巡检定时任务
        inspectionTaskJobService.deleteInspectionTaskJob(inspectionTaskIds, req.getIsDeleted());

        //删除巡检任务新增日志
        this.deleteInspectionTaskLogBatch(inspectionTaskIds);
    }

    /**
     * 巡检任务关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/3/29 17:07
     * @param req 巡检任务关联设施参数
     * @return 巡检任务关联设施
     */
    @Override
    public Result inspectionTaskRelationDeviceList(InspectionTaskRelationDeviceListReq req) {
        //巡检任务编号
        String inspectionTaskId = req.getInspectionTaskId();
        if (ObjectUtils.isEmpty(inspectionTaskId)) {
            return ResultUtils.success(new ArrayList<>());
        }
        //查询巡检任务关联设施
        List<InspectionTaskDevice> deviceList = inspectionTaskDeviceService.queryInspectionTaskDeviceByTaskId(inspectionTaskId);
        if (ObjectUtils.isEmpty(deviceList)) {
            deviceList = new ArrayList<>();
        }
        return ResultUtils.success(deviceList);
    }

    /**
     * 删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 14:12
     * @param inspectionTaskIds 巡检任务编号
     */
    public void deleteInspectionTaskLogBatch(List<String> inspectionTaskIds) {
        systemLanguage.querySystemLanguage();
        //新增批量删除巡检任务的日志
        List<AddLogBean> addLogBeanList = new ArrayList<>();
        //根据巡检任务编号查询巡检任务信息
        List<InspectionTask> taskList = inspectionTaskDao.selectInspectionTaskForInspectionTaskIds(inspectionTaskIds);
        if (null != taskList && 0 < taskList.size()) {
            for (InspectionTask inspectionTaskOne : taskList) {
                AddLogBean addLogBean = logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE);
                addLogBean.setDataId(InspectionTaskConstants.INSPECTION_TASK_ID_ATTR_NAME);
                addLogBean.setDataName(InspectionTaskConstants.INSPECTION_TASK_NAME_ATTR_NAME);
                //获得操作对象名称
                addLogBean.setOptObj(inspectionTaskOne.getInspectionTaskName());
                addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_TASK_FUNCTION_CODE);
                //获得操作对象id
                addLogBean.setOptObjId(inspectionTaskOne.getInspectionTaskId());
                //操作为删除
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
                addLogBeanList.add(addLogBean);
            }
            logProcess.addOperateLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }

    /**
     * 开启或关闭巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 14:12
     * @param inspectionTaskIds 巡检任务编号
     */
    public void openOrCloseInspectionTaskLogBatch(List<String> inspectionTaskIds, String functionCode) {
        //新增批量开启或关闭巡检任务的日志
        List<AddLogBean> addLogBeanList = new ArrayList<>();
        //根据巡检任务编号查询巡检任务信息
        List<InspectionTask> taskList = inspectionTaskDao.selectInspectionTaskForInspectionTaskIds(inspectionTaskIds);
        systemLanguage.querySystemLanguage();
        if (null != taskList && 0 < taskList.size()) {
            for (InspectionTask inspectionTaskOne : taskList) {
                AddLogBean addLogBean = logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE);
                addLogBean.setDataId(InspectionTaskConstants.INSPECTION_TASK_ID_ATTR_NAME);
                addLogBean.setDataName(InspectionTaskConstants.INSPECTION_TASK_NAME_ATTR_NAME);
                //获得操作对象名称
                addLogBean.setOptObj(inspectionTaskOne.getInspectionTaskName());
                addLogBean.setFunctionCode(functionCode);
                //获得操作对象id
                addLogBean.setOptObjId(inspectionTaskOne.getInspectionTaskId());
                //操作为修改
                addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
                addLogBeanList.add(addLogBean);
            }
            logProcess.addOperateLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }



    /**
     * 巡检任务查询巡检任务名称是否重复
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 18:10
     * @param inspectionTask 巡检任务参数
     * @return Integer 查询重复的数据个数
     */
    @Override
    public int queryInspectionTaskNameIsExists(InspectionTask inspectionTask) {
        int count = inspectionTaskDao.queryInspectionTaskNameIsExists(inspectionTask);
        return count;
    }

    /**
     * 得到巡检任务名称结果
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 18:10
     * @param inspectionTask 巡检任务参数
     * @return boolean 返回巡检任务名称是否重复的结果
     */
    @Override
    public boolean returnInspectionTaskNameResult(InspectionTask inspectionTask) {
        //巡检任务名称为空不查询巡检任务名称是否重复
        if (StringUtils.isEmpty(inspectionTask.getInspectionTaskName())) {
            return true;
        }
        //拼装参数信息
        InspectionTask inspectionTaskParam = new InspectionTask();
        inspectionTaskParam.setIsDeleted(WorkFlowBusinessConstants.IS_NOT_DELETED);
        inspectionTaskParam.setInspectionTaskName(inspectionTask.getInspectionTaskName());
        if (!StringUtils.isEmpty(inspectionTask.getInspectionTaskId())) {
            inspectionTaskParam.setInspectionTaskId(inspectionTask.getInspectionTaskId());
        }
        int taskCount = inspectionTaskDao.queryInspectionTaskNameIsExists(inspectionTaskParam);
        if (taskCount > 0) {
            //巡检名称重复
            return false;
        }
        return true;
    }

    /**
     * 批量逻辑删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/2 22:49
     * @param inspectionIds 巡检任务编号
     * @param isDeleted 是否删除巡检任务
     */
    @Override
    public boolean deleteInspectionTaskBatch(List<String> inspectionIds, String isDeleted) {
        if (0 < inspectionIds.size()) {
            Date nowDate = new Date();
            String userId = RequestHeaderUtils.getHeadParam(RequestHeaderConstants.PARAM_USER_ID);
            //批量删除
            int deleteRowNumber = inspectionTaskDao.deleteInspectionTaskBatch(inspectionIds, userId, nowDate, isDeleted);
            //判断删除的行数是不是满足需要删除的数据数量
            if (0 < deleteRowNumber && deleteRowNumber == inspectionIds.size()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 根据巡检任务编号查询巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/4 10:11
     * @param list 查询巡检任务参数
     * @return boolean 巡检任务信息
     */
    @Override
    public Result selectIsDeleteForInspectionTaskIds(List<String> list) {
        if (!ObjectUtils.isEmpty(list)) {
            //根据巡检任务编号查询巡检任务信息
            List<InspectionTask> taskList = inspectionTaskDao.selectInspectionTaskForInspectionTaskIds(list);
            if (!ObjectUtils.isEmpty(taskList)) {
                if (list.size() != taskList.size()) {
                    return ResultUtils.warn(InspectionTaskResultCode.EXIST_IS_DELETED_TASK_DATA, I18nUtils.getSystemString(InspectionTaskI18n.EXIST_IS_DELETED_TASK_DATA));
                }
                return null;
            } else {
                return WorkFlowBusinessMsg.paramErrorMsg();
            }
        } else {
            return WorkFlowBusinessMsg.paramErrorMsg();
        }
    }

    /**
     * 查询巡检任务列表
     * @author hedongwei@wistronits.com
     * @date  2019/3/4 15:56
     * @param queryCondition 查询条件
     * @return  Result 返回查询巡检任务列表
     */
    @Override
    public Result queryListInspectionTaskByPage(QueryCondition<QueryListInspectionTaskByPageReq> queryCondition) {
        //校验方法参数
        if (null != ValidateUtils.checkQueryConditionParam(queryCondition)) {
            return ValidateUtils.checkQueryConditionParam(queryCondition);
        }

        //获取查询条件过滤之后的条件
        queryCondition = ValidateUtils.filterQueryCondition(queryCondition);

        //获得开始行数
        Integer begin = (queryCondition.getPageCondition().getPageNum() - 1) * queryCondition.getPageCondition().getPageSize();
        queryCondition.getPageCondition().setBeginNum(begin);

        //不是导出操作
        boolean isExport = false;

        //获取查询权限
        queryCondition = this.getInspectionTaskCondition(queryCondition, isExport);

        //获取巡检编号
        List<String> inspectionTaskIds = inspectionTaskDao.queryListInspectionTaskByPage(queryCondition);


        List<InspectionTask> taskList = new ArrayList<>();

        //页面显示的巡检任务
        List<QueryListInspectionTaskByPageVo> inspectionTaskVoList = new ArrayList<>();

        //数据总数
        int count = 0;

        if (!ObjectUtils.isEmpty(inspectionTaskIds)) {
            //查询数据信息
            taskList = inspectionTaskDao.selectInspectionTaskForInspectionTaskIds(inspectionTaskIds);

            //获得显示的数据
            List<InspectionTask> showTaskList = this.getShowInspectionTaskList(taskList, inspectionTaskIds);

            //查询数据总数
            count = inspectionTaskDao.queryListInspectionTaskCount(queryCondition);

            //页面需要显示的巡检任务的数据信息
            inspectionTaskVoList = this.getInspectionTaskInfoList(showTaskList);

        }

        // 构造分页条件
        Page page = myBatiesBuildPage(queryCondition);

        // 构造返回结果
        PageBean pageBean = myBatiesBuildPageBean(page, count, inspectionTaskVoList);

        //返回显示数据
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * 获取巡检任务条件
     * @author hedongwei@wistronits.com
     * @date  2019/4/22 16:03
     * @param queryCondition 查询条件
     * @param isExport 是否导出
     * @return 获取查询巡检任务条件
     */
    @Override
    public QueryCondition<QueryListInspectionTaskByPageReq> getInspectionTaskCondition(QueryCondition queryCondition, boolean isExport) {
        //获取权限信息
        QueryCondition<ProcBaseReq> procBaseReqCondition = new QueryCondition<>();
        procBaseReqCondition.setBizCondition(new ProcBaseReq());
        ProcBaseReq procBaseReq = null;
        if (isExport) {
            procBaseReqCondition = procBaseService.getPermissionsInfoForExport(procBaseReqCondition);
            procBaseReq = procBaseReqCondition.getBizCondition();
        } else {
            procBaseReqCondition = procBaseService.getPermissionsInfo(procBaseReqCondition);
            procBaseReq = procBaseReqCondition.getBizCondition();
        }

        //将权限信息加入到queryCondition中
        QueryListInspectionTaskByPageReq inspectionTaskReq = (QueryListInspectionTaskByPageReq)queryCondition.getBizCondition();
        inspectionTaskReq.setPermissionDeviceTypes(procBaseReq.getPermissionDeviceTypes());
        inspectionTaskReq.setPermissionDeptIds(procBaseReq.getPermissionDeptIds());
        inspectionTaskReq.setPermissionAreaIds(procBaseReq.getPermissionAreaIds());
        queryCondition.setBizCondition(inspectionTaskReq);
        return queryCondition;
    }

    /**
     * 获取有序的巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/13 15:07
     * @param taskList 巡检任务
     * @param inspectionTaskIds 巡检任务编号
     * @return 获取有序的巡检任务信息
     */
    @Override
    public List<InspectionTask> getShowInspectionTaskList(List<InspectionTask> taskList, List<String> inspectionTaskIds) {
        //巡检任务列表map
        Map<String, InspectionTask> inspectionTaskInfo = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(taskList)) {
            for (InspectionTask inspectionTask : taskList) {
                inspectionTaskInfo.put(inspectionTask.getInspectionTaskId(), inspectionTask);
            }
        }

        //将数据转化成有序的内容
        List<InspectionTask> showTaskList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(inspectionTaskIds)) {
            for (String inspectionTaskId : inspectionTaskIds) {
                showTaskList.add(inspectionTaskInfo.get(inspectionTaskId));
            }
        }
        return showTaskList;
    }



    /**
     * 页面需要显示的巡检任务列表信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/7 14:51
     * @param taskList 巡检任务集合
     * @return  页面需要显示的巡检任务信息
     */
    @Override
    public List<QueryListInspectionTaskByPageVo> getInspectionTaskInfoList(List<InspectionTask> taskList) {
        //巡检任务返回集合
        List<QueryListInspectionTaskByPageVo> inspectionTaskVoList = new ArrayList<>();
        //巡检任务编号数组
        List<String> taskIds = new ArrayList<>();
        //区域编号数组
        List<String> areaIds = new ArrayList<>();
        //区域编号set集合
        Set<String> areaSet = new HashSet<>();

        if (null != taskList && 0 < taskList.size()) {
            for (InspectionTask inspectionTaskOne : taskList) {
                //将对象赋给需要查询之后的结果
                QueryListInspectionTaskByPageVo inspectionTaskVo = new QueryListInspectionTaskByPageVo();
                BeanUtils.copyProperties(inspectionTaskOne, inspectionTaskVo);
                if (!ObjectUtils.isEmpty(inspectionTaskVo.getTaskStartTime())) {
                    //开始时间
                    Long startTime = inspectionTaskVo.getTaskStartTime().getTime();
                    inspectionTaskVo.setStartTime(startTime);
                }
                if (!ObjectUtils.isEmpty(inspectionTaskVo.getTaskEndTime())) {
                    //结束时间
                    Long endTime = inspectionTaskVo.getTaskEndTime().getTime();
                    inspectionTaskVo.setEndTime(endTime);
                }
                inspectionTaskVoList.add(inspectionTaskVo);
                //添加巡检任务编号添加到巡检任务编号数组
                taskIds.add(inspectionTaskOne.getInspectionTaskId());
                //将区域编号添加到区域编号数组
                areaSet.add(inspectionTaskOne.getInspectionAreaId());
            }
        }
        areaIds.addAll(areaSet);
        //将区域名称添加到巡检任务列表中
        inspectionTaskVoList = addAreaInfoToTask(inspectionTaskVoList, areaIds);
        //将单位信息添加到巡检任务列表中
        inspectionTaskVoList = addDeptToTask(inspectionTaskVoList, taskIds);
        return inspectionTaskVoList;
    }


    /**
     * 返回拼装区域数据之后的巡检任务列表
     * @author hedongwei@wistronits.com
     * @date  2019/3/7 14:40
     * @param voData 页面显示数据
     * @param areaIds 区域编号
     * @return List<QueryListInspectionTaskByPageVo> 页面数据
     */
    public List<QueryListInspectionTaskByPageVo> addAreaInfoToTask(Object voData, List<String> areaIds) {
        List<QueryListInspectionTaskByPageVo> inspectionTaskVoList = (List<QueryListInspectionTaskByPageVo>)voData;
        if (!ObjectUtils.isEmpty(inspectionTaskVoList)) {
            List<String> inspectionIdsList = new ArrayList<>();
            for (QueryListInspectionTaskByPageVo pageVoList : inspectionTaskVoList) {
                inspectionIdsList.add(pageVoList.getInspectionTaskId());
            }
            //根据区域编号查询区域信息
            List<AreaInfoForeignDto> areaInfoList = areaFeign.selectAreaInfoByIds(areaIds);
            //获取区域map
            Map<String, String> areaMap = CastMapUtil.getAreaMap(areaInfoList);
            //替换返回值中的区域信息
            inspectionTaskVoList = this.areaInfoInspectionTask(inspectionTaskVoList, areaMap);
        }

        return inspectionTaskVoList;
    }

    /**
     * 返回拼装单位数据之后的巡检任务列表
     * @author hedongwei@wistronits.com
     * @date  2019/3/7 14:40
     * @param voData 页面显示数据
     * @param taskIds 巡检任务编号集合
     * @return List<QueryListInspectionTaskByPageVo> 页面数据
     */
    public List<QueryListInspectionTaskByPageVo> addDeptToTask(Object voData, List<String> taskIds) {
        List<QueryListInspectionTaskByPageVo> inspectionTaskVoList = (List<QueryListInspectionTaskByPageVo>)voData;
        //查询责任单位信息
        List<InspectionTaskDepartment> inspectionTaskDepartments = inspectionTaskDepartmentService.queryInspectionTaskDepartmentsByTaskIds(taskIds);
        //获取部门编号数组
        List<String> deptIds = this.getInspectionTaskDepartmentIds(inspectionTaskDepartments);
        //根据部门id数组查询的责任单位信息
        List<Department> departments = departmentFeign.queryDepartmentFeignById(deptIds);
        //责任单位信息
        Map<String, String> departmentMap = CastMapUtil.getDepartmentMap(departments);

        //查询巡检任务关联责任单位信息
        Map<String ,String> inspectionTaskDepartmentMap = this.getTaskListDeptName(inspectionTaskDepartments, departmentMap);
        //将部门添加到数据中
        inspectionTaskVoList = this.deptIntoInspectionTask(inspectionTaskVoList, inspectionTaskDepartmentMap);
        return inspectionTaskVoList;
    }


    /**
     * 返回巡检部门编号集合
     * @author hedongwei@wistronits.com
     * @date  2019/3/7 9:45
     * @param inspectionTaskDepartments 巡检部门编号
     */
    public List<String> getInspectionTaskDepartmentIds(List<InspectionTaskDepartment> inspectionTaskDepartments ) {
        if (null != inspectionTaskDepartments && 0 < inspectionTaskDepartments.size()) {
            //单位编号集合
            List<String> departmentIdList = new ArrayList<>();
            Set<String> departmentIdSet = new HashSet<>();
            for (InspectionTaskDepartment inspectionTaskDepartment : inspectionTaskDepartments) {
                //获取单位编号
                String deptId = inspectionTaskDepartment.getAccountabilityDept();
                departmentIdSet.add(deptId);
            }
            departmentIdList.addAll(departmentIdSet);
            return departmentIdList;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 查询巡检任务详情
     * @author hedongwei@wistronits.com
     * @date  2019/3/5 18:02
     * @param id 巡检任务编号
     * @return Result 返回巡检任务详情的结果
     */
    @Override
    public Result getInspectionTaskById(String id) {
        InspectionTask inspectionParam =  new InspectionTask();
        inspectionParam.setInspectionTaskId(id);
        inspectionParam.setIsDeleted(WorkFlowBusinessConstants.IS_NOT_DELETED);
        //查询巡检任务信息
        InspectionTask inspectionTask = inspectionTaskDao.selectOne(inspectionParam);
        //巡检任务详情返回类
        GetInspectionTaskByIdVo inspectionTaskByIdVo = new GetInspectionTaskByIdVo();
        if (null != inspectionTask) {
            BeanUtils.copyProperties(inspectionTask, inspectionTaskByIdVo);

            //区域编号
            String areaId = inspectionTask.getInspectionAreaId();
            List<String> areaIds = new ArrayList<>();
            areaIds.add(areaId);
            //根据区域编号查询区域信息
            List<AreaInfoForeignDto> areaList = areaFeign.selectAreaInfoByIds(areaIds);
            Map<String, String> areaMap = CastMapUtil.getAreaMap(areaList);

            //查询区域名称
            inspectionTaskByIdVo.setInspectionAreaName(areaMap.get(areaId));

            //查询巡检任务关联单位
            List<InspectionTaskDepartment> departmentList = inspectionTaskDepartmentService.queryInspectionTaskDepartmentsByTaskId(id);
            //单位编号
            List<String> deptIds = new ArrayList<>();
            if (null != departmentList && 0 < departmentList.size()) {
                for (InspectionTaskDepartment deptOne : departmentList) {
                    deptIds.add(deptOne.getAccountabilityDept());
                }
            }

            //根据巡检任务关联单位查询单位的信息
            List<Department> departments = departmentFeign.queryDepartmentFeignById(deptIds);
            //责任单位信息
            Map<String, String> departmentMap = CastMapUtil.getDepartmentMap(departments);

            List<InspectionTaskDepartmentBean> departmentBeans = this.getDeptList(departmentList, departmentMap, id);
            //将关联单位添加到返回类
            inspectionTaskByIdVo.setDeptList(departmentBeans);

            //查询任务关联设施信息
            List<InspectionTaskDevice> deviceTaskList = inspectionTaskDeviceService.queryInspectionTaskDeviceByTaskId(id);
            List<String> deviceIds = new ArrayList<>();
            Set<String> deviceIdSet = new HashSet<>();
            if (null != deviceTaskList && 0 < deviceTaskList.size())  {
                for (InspectionTaskDevice taskDeviceOne : deviceTaskList) {
                    deviceIdSet.add(taskDeviceOne.getDeviceId());
                }
            }
            deviceIds.addAll(deviceIdSet);
            String [] deviceArray = new String[deviceIds.size()];
            deviceIds.toArray(deviceArray);
            //根据设施编号查询设施信息
            List<DeviceInfoDto> deviceList = deviceFeign.getDeviceByIds(deviceArray);
            Map<String, String> deviceMap = CastMapUtil.getDeviceMap(deviceList);
            List<InspectionTaskDeviceBean> deviceBeans = this.getDeviceBeanList(deviceTaskList, id, areaMap, deviceMap);
            //将关联设施添加到返回类
            inspectionTaskByIdVo.setDeviceList(deviceBeans);

            //拼接设施名称
            String deviceName = this.getDeviceName(deviceBeans);
            inspectionTaskByIdVo.setDeviceName(deviceName);

            //拼接责任单位名称
            String accountabilityDeptName = this.getDeptName(departmentBeans);
            inspectionTaskByIdVo.setAccountabilityDeptName(accountabilityDeptName);


            //关联设施总数
            inspectionTaskByIdVo.setInspectionDeviceCount(deviceBeans.size() + "");

        }
        return ResultUtils.success(inspectionTaskByIdVo);
    }


    /**
     * 查询巡检任务信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 9:11
     * @param inspectionTask 巡检任务参数
     * @return 返回巡检任务信息
     */
    @Override
    public InspectionTask getInspectionTaskOne(InspectionTask inspectionTask) {
        return inspectionTaskDao.selectOne(inspectionTask);
    }

    /**
     * 设施名称
     * @author hedongwei@wistronits.com
     * @date  2019/4/18 0:26
     * @param deviceBeans 设施对象
     * @return 拼接设施名称
     */
    public String getDeviceName(List<InspectionTaskDeviceBean> deviceBeans) {
        String deviceName = "";
        if (!ObjectUtils.isEmpty(deviceBeans)) {
            for (InspectionTaskDeviceBean inspectionTaskDeviceBean : deviceBeans) {
                if (StringUtils.isEmpty(deviceName)) {
                    deviceName = inspectionTaskDeviceBean.getDeviceName();
                } else {
                    deviceName += "," + inspectionTaskDeviceBean.getDeviceName();
                }
            }
        }
        return deviceName;
    }

    /**
     * 拼接部门名称
     * @author hedongwei@wistronits.com
     * @date  2019/4/18 0:26
     * @param departmentBeans 设施对象
     * @return 拼接设施名称
     */
    public String getDeptName(List<InspectionTaskDepartmentBean> departmentBeans) {
        String accountabilityDeptName = "";
        if (!ObjectUtils.isEmpty(departmentBeans)) {
            for (InspectionTaskDepartmentBean departmentBean : departmentBeans) {
                if (StringUtils.isEmpty(accountabilityDeptName)) {
                    accountabilityDeptName = departmentBean.getAccountabilityDeptName();
                } else {
                    accountabilityDeptName += "," + departmentBean.getAccountabilityDeptName();
                }
            }
        }
        return accountabilityDeptName;
    }

    /**
     * 根据巡检任务编号查询巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/4 10:11
     * @param list 查询巡检任务参数
     * @return List<Inspection> 巡检任务信息
     */
    @Override
    public List<InspectionTask> selectInspectionTaskForInspectionTaskIds(List<String> list) {
        if (null != list && 0 < list.size()) {
            return inspectionTaskDao.selectInspectionTaskForInspectionTaskIds(list);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 批量开启巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 18:06
     * @param req 开启巡检任务参数
     * @return Result 返回开启巡检任务的结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result openInspectionTaskBatch(OpenInspectionTaskBatchReq req) {
        //开启巡检任务参数集合
        List<String> openTaskList  = req.getInspectionTaskIds();
        //开启巡检任务
        if (!this.inspectionTaskOpenAndCloseProcess(openTaskList, InspectionTaskConstants.IS_OPEN)) {
            WorkFlowBusinessMsg.paramErrorMsg();
        }
        //开启巡检任务的功能编码
        String openTaskFunctionCode = LogFunctionCodeConstant.OPEN_TASK_FUNCTION_CODE;
        //批量新增日志
        this.openOrCloseInspectionTaskLogBatch(openTaskList, openTaskFunctionCode);

        //提示巡检任务开启成功
        String inspectionTaskOpenSuccess = I18nUtils.getSystemString(InspectionTaskI18n.INSPECTION_TASK_OPEN_SUCCESS);
        return ResultUtils.success(ResultCode.SUCCESS, inspectionTaskOpenSuccess);
    }

    /**
     * 批量关闭巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 18:06
     * @param req 关闭巡检任务参数
     * @return Result 返回关闭巡检任务的结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result closeInspectionTaskBatch(CloseInspectionTaskBatchReq req) {
        //关闭巡检任务编号集合
        List<String> closeTaskList  = req.getInspectionTaskIds();
        //关闭巡检任务
        if (!this.inspectionTaskOpenAndCloseProcess(closeTaskList, InspectionTaskConstants.IS_CLOSE)) {
            WorkFlowBusinessMsg.paramErrorMsg();
        }
        //关闭巡检任务的功能编码
        String closeTaskFunctionCode = LogFunctionCodeConstant.CLOSE_TASK_FUNCTION_CODE;
        //批量新增日志
        this.openOrCloseInspectionTaskLogBatch(closeTaskList, closeTaskFunctionCode);
        //提示巡检任务关闭成功
        String inspectionTaskCloseSuccess = I18nUtils.getSystemString(InspectionTaskI18n.INSPECTION_TASK_CLOSE_SUCCESS);
        return ResultUtils.success(ResultCode.SUCCESS, inspectionTaskCloseSuccess);
    }

    /**
     * 巡检任务详情
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 18:06
     * @param id 巡检任务编号
     * @return 返回巡检任务详情的结果
     */
    @Override
    public Result getInspectionTaskDetail(String id) {
        //查询巡检任务详情
        InspectionTaskDetailBean inspectionTaskDetail = this.getInspectionDetailInfo(id);
        return ResultUtils.success(inspectionTaskDetail);
    }

    /**
     * 巡检任务详情
     * @author hedongwei@wistronits.com
     * @date  2019/4/16 20:33
     * @param id 巡检任务编号
     * @return 巡检任务详情
     */
    @Override
    public InspectionTaskDetailBean getInspectionDetailInfo(String id) {
        //查询巡检任务信息
        InspectionTask inspectionTask = inspectionTaskDao.selectById(id);
        //巡检任务详情返回类
        InspectionTaskDetailBean inspectionTaskDetail = new InspectionTaskDetailBean();
        if (null != inspectionTask) {
            BeanUtils.copyProperties(inspectionTask, inspectionTaskDetail);

            //查询巡检任务关联单位
            List<InspectionTaskDepartment> departmentList = inspectionTaskDepartmentService.queryInspectionTaskDepartmentsByTaskId(id);

            //查询设施关联设施信息
            List<InspectionTaskDevice> deviceTaskList = inspectionTaskDeviceService.queryInspectionTaskDeviceByTaskId(id);

            //巡检任务关联部门
            inspectionTaskDetail.setDeptList(departmentList);
            //巡检任务关联设施
            inspectionTaskDetail.setDeviceList(deviceTaskList);
        }
        return inspectionTaskDetail;
    }

    /**
     *  导出巡检任务
     *
     * @param exportDto 巡检工单导出请求
     *
     * @return Result
     */
    @Override
    public Result exportInspectionTask(ExportDto exportDto) {
        ExportRequestInfo exportRequestInfo = null;
        try {
            exportRequestInfo = inspectionTaskListExport.insertTask(exportDto, WorkFlowBusinessConstants.SERVER_NAME, I18nUtils.getSystemString(InspectionTaskI18n.INSPECTION_TASK_LIST));
        } catch (FilinkExportNoDataException fe) {
            log.error("export inspection task list no data exception", fe);
            return ResultUtils.warn(WorkflowBusinessResultCode.EXPORT_NO_DATA, I18nUtils.getSystemString(WorkflowBusinessI18n.EXPORT_NO_DATA));

        } catch (FilinkExportDataTooLargeException fe) {
            return WorkFlowBusinessMsg.getExportToLargeMsg(fe, maxExportDataSize);
        } catch (FilinkExportTaskNumTooBigException fe) {
            log.error("export inspection task task num to big exception", fe);
            return ResultUtils.warn(ProcBaseResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(ProcBaseI18n.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            log.error("export inspection task exception", e);
            return ResultUtils.warn(ProcBaseResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(ProcBaseI18n.FAILED_TO_CREATE_EXPORT_TASK));
        }
        inspectionTaskListExport.exportData(exportRequestInfo);
        //新增日志
        procLogService.addLogByExport(exportDto, LogFunctionCodeConstant.INSPECTION_TASK_EXPORT_CODE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 根据设施集合删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 11:34
     * @param req 删除巡检任务参数
     * @return 返回删除巡检任务结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteInspectionTaskForDeviceList(DeleteInspectionTaskForDeviceReq req) {
        //默认是删除操作
        if (ObjectUtils.isEmpty(req.getIsDeleted())) {
            req.setIsDeleted(WorkFlowBusinessConstants.IS_DELETED);
        }

        InspectionTaskDeviceReq paramInfo = new InspectionTaskDeviceReq();
        paramInfo.setDeviceIdList(req.getDeviceIdList());
        paramInfo.setIsDeleted(ProcBaseUtil.getIsDeletedSearchParam(req.getIsDeleted()));
        //查询巡检任务关联设施信息
        List<InspectionTaskDevice> inspectionTaskDeviceList = inspectionTaskDeviceService.queryInspectionTaskDeviceForDeviceIdList(paramInfo);
        if (!ObjectUtils.isEmpty(inspectionTaskDeviceList)) {
            //查询需要删除的巡检任务信息
            Map<String, List<InspectionTaskDevice>> inspectionTaskDeviceMap = CastMapUtil.getInspectionDeviceMap(inspectionTaskDeviceList);

            //查询所有删除的巡检任务关联的设施信息
            Map<String, List<InspectionTaskDevice>> inspectionTaskDeviceAllMap = this.getInspectionTaskAllDeviceMap(req, inspectionTaskDeviceMap);

            //找到需要删除的巡检任务编号
            List<String> ableDeleteTaskIdList = CastListUtil.getAbleDeleteInspectionTaskIdList(inspectionTaskDeviceAllMap, req);

            //删除巡检任务信息
            if (!ObjectUtils.isEmpty(ableDeleteTaskIdList)) {
                DeleteInspectionTaskByIdsReq taskReq = new DeleteInspectionTaskByIdsReq();
                taskReq.setIsDeleted(req.getIsDeleted());
                taskReq.setInspectionTaskIds(ableDeleteTaskIdList);
                //删除巡检任务信息
                this.deleteInspectionTaskProcess(taskReq);
            }

            //过滤删除的巡检任务信息
            List<String> notDeleteTaskList = CastListUtil.getNotDeletedTaskId(inspectionTaskDeviceMap, ableDeleteTaskIdList);


            //查询不需要删除的信息
            List<InspectionTask> notDeleteInspectionTaskList = null;
            if (!ObjectUtils.isEmpty(notDeleteTaskList)) {
                //查询不用删除的关联设施信息
                notDeleteInspectionTaskList = inspectionTaskDao.selectInspectionTaskForInspectionTaskIds(notDeleteTaskList);
                //获取设施总数
                notDeleteInspectionTaskList = CastListUtil.getNotDeleteTaskDeviceCount(inspectionTaskDeviceMap, notDeleteInspectionTaskList);
            }



            Map<String, String> ableDeleteTaskIdMap = CastMapUtil.getMapForListString(ableDeleteTaskIdList);
            //需要删除的关联设施信息
            List<InspectionTaskDevice> filterAbleTask = CastListUtil.filterAbleTask(inspectionTaskDeviceList, ableDeleteTaskIdMap);

            //批量删除关联设施信息
            if (!ObjectUtils.isEmpty(filterAbleTask)) {
                this.logicDeleteDeviceProcess(filterAbleTask, req);
            }

            //批量修改设施总数信息
            if (!ObjectUtils.isEmpty(notDeleteInspectionTaskList)) {
                this.updateDeleteDeviceCount(notDeleteInspectionTaskList);
            }

        }
        //修改巡检任务的提示信息
        String successUpdateInspection = I18nUtils.getSystemString(WorkflowBusinessI18n.DELETE_SUCCESS);
        //返回提示信息
        return ResultUtils.success(ResultCode.SUCCESS, successUpdateInspection);
    }


    /**
     * 校验部门信息有无关联巡检任务
     * @param deptIds 部门ids
     * @author hedongwei@wistronits.com
     *
     * @date  2019/4/26 11:03
     * @return 有无关联巡检任务
     */
    @Override
    public Map<String, List<String>> queryInspectionTaskListByDeptIds(List<String> deptIds) {
        List<InspectionTaskDepartment> departmentList = inspectionTaskDepartmentService.queryTaskListByDeptIds(deptIds);
        Map<String,List<String>> resultMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        List<String> inspectionIds;
        for (InspectionTaskDepartment department : departmentList){
            if (resultMap.containsKey(department.getAccountabilityDept())){
                inspectionIds = resultMap.get(department.getAccountabilityDept());
            } else {
                inspectionIds = new ArrayList<>();
            }
            inspectionIds.add(department.getInspectionTaskId());
            resultMap.put(department.getAccountabilityDept(),inspectionIds);
        }
        return resultMap;
    }

    /**
     * 修改删除设施总数
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 19:50
     * @param notDeleteInspectionTaskList 修改删除设施总数
     */
    public void updateDeleteDeviceCount(List<InspectionTask> notDeleteInspectionTaskList) {
        if (!ObjectUtils.isEmpty(notDeleteInspectionTaskList)) {
            int row = inspectionTaskDao.updateInspectionTaskInfoBatch(notDeleteInspectionTaskList);
            if (row <= 0) {
                throw new FilinkWorkflowBusinessDataException();
            }
        }
    }

    /**
     * 逻辑删除关联设施表
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 19:37
     * @param filterAbleTask 需要删除的设施信息
     * @param req 删除巡检任务参数
     */
    public void logicDeleteDeviceProcess(List<InspectionTaskDevice> filterAbleTask, DeleteInspectionTaskForDeviceReq req) {
        List<String> inspectionTaskDeviceIdList = CastListUtil.getTaskDeviceIdList(filterAbleTask);
        if (!ObjectUtils.isEmpty(inspectionTaskDeviceIdList)) {
            InspectionTaskDeviceReq logicDeleteDeviceReq = new InspectionTaskDeviceReq();
            logicDeleteDeviceReq.setInspectionTaskDeviceIdList(inspectionTaskDeviceIdList);
            logicDeleteDeviceReq.setIsDeleted(req.getIsDeleted());
            int row = inspectionTaskDeviceService.logicDeleteTaskDeviceBatch(logicDeleteDeviceReq);
            if (row <= 0) {
                throw new FilinkWorkflowBusinessDataException();
            }
        }

    }


    /**
     * 获取删除的巡检任务全部的设施map
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 17:53
     * @param req 删除巡检任务的参数
     * @param inspectionTaskDeviceMap 巡检任务设施map
     * @return 获取删除的巡检任务全部的设施map
     */
    public Map<String, List<InspectionTaskDevice>> getInspectionTaskAllDeviceMap(DeleteInspectionTaskForDeviceReq req , Map<String, List<InspectionTaskDevice>> inspectionTaskDeviceMap) {
        Map<String, List<InspectionTaskDevice>> deviceAllMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(inspectionTaskDeviceMap)) {
            List<String> inspectionTaskIdList = new ArrayList<>();
            for (String inspectionTaskIdKey : inspectionTaskDeviceMap.keySet()) {
                if (!ObjectUtils.isEmpty(inspectionTaskIdKey)) {
                    inspectionTaskIdList.add(inspectionTaskIdKey);
                }
            }

            if (!ObjectUtils.isEmpty(inspectionTaskIdList)) {
                //查询所有删除的巡检任务关联设施信息
                InspectionTaskDeviceReq paramInfo = new InspectionTaskDeviceReq();
                paramInfo.setInspectionTaskIdList(inspectionTaskIdList);
                paramInfo.setIsDeleted(ProcBaseUtil.getIsDeletedSearchParam(req.getIsDeleted()));
                List<InspectionTaskDevice> relatedDeviceAllList = inspectionTaskDeviceService.queryInspectionTaskDeviceForDeviceIdList(paramInfo);
                deviceAllMap = CastMapUtil.getInspectionDeviceMap(relatedDeviceAllList);
            }
        }
        return deviceAllMap;
    }

    /**
     * 关闭或开启巡检任务过程
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 20:27
     * @param taskList 任务编号
     * @param isOpenValue 是否开启栏位的值
     */
    public boolean inspectionTaskOpenAndCloseProcess(List<String> taskList, String isOpenValue) {
        if (null != taskList && 0 < taskList.size()) {
            InspectionTask inspectionTask = new InspectionTask();
            //修改时间
            inspectionTask.setUpdateTime(new Date());
            //修改用户
            inspectionTask.setUpdateUser(RequestHeaderUtils.getHeadParam(RequestHeaderConstants.PARAM_USER_ID));
            //关闭
            inspectionTask.setIsOpen(isOpenValue);
            //批量修改巡检任务关闭
            int updateRow = inspectionTaskDao.updateInspectionTaskOpenAndCloseBatch(taskList, inspectionTask);
            //更改的行数等于批量关闭巡检任务的行数
            if (taskList.size() != updateRow) {
                throw new FilinkWorkflowBusinessDataException();
            }
        }
        return true;
    }






    /**
     * 巡检任务关联设施List
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 10:41
     * @param deviceList 设施信息
     * @param id 巡检任务编号
     * @param areaMap 全部区域map
     * @param deviceMap 全部设施map
     * @return List<InspectionTaskDeviceBean> 巡检任务关联设施List
     */
    public List<InspectionTaskDeviceBean> getDeviceBeanList(List<InspectionTaskDevice> deviceList, String id, Map<String, String> areaMap, Map<String, String> deviceMap) {
        //获取全部设施信息
        List<InspectionTaskDeviceBean> deviceBeanList = new ArrayList<>();
        if (null != deviceList && 0 < deviceList.size()) {
            for (InspectionTaskDevice deviceOne : deviceList) {
                InspectionTaskDeviceBean deviceBean = new InspectionTaskDeviceBean();
                BeanUtils.copyProperties(deviceOne, deviceBean);
                //获取设施id
                String deviceId = deviceOne.getDeviceId();
                //获取设施name
                String deviceName = "";
                if (null != deviceMap.get(deviceId)) {
                    deviceName = deviceMap.get(deviceId);
                }
                deviceBean.setDeviceName(deviceName);

                //获取区域id
                String areaId = deviceOne.getDeviceAreaId();
                //获取区域名称
                String areaName = "";
                if (null != deviceMap.get(areaId)) {
                    areaName = deviceMap.get(areaId);
                }
                deviceBean.setDeviceAreaName(areaName);
                deviceBeanList.add(deviceBean);
            }
        }
        return deviceBeanList;
    }



    /**
     * 巡检任务单位信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/5 23:18
     * @param departmentList 单位集合
     * @param departmentMap 单位参数
     * @param id 巡检任务编号
     * @return List<InspectionTaskDepartmentBean> 巡检任务单位信息
     */
    public List<InspectionTaskDepartmentBean> getDeptList(List<InspectionTaskDepartment> departmentList , Map<String, String> departmentMap, String id) {
        //巡检任务编号
        List<InspectionTaskDepartmentBean> departmentBeans = new ArrayList<>();
        if (null != departmentList && 0 < departmentList.size()) {
            for (InspectionTaskDepartment deptOne : departmentList) {
                InspectionTaskDepartmentBean departmentBean = new InspectionTaskDepartmentBean();
                BeanUtils.copyProperties(deptOne, departmentBean);
                //责任单位编号
                String accountabilityUnit = deptOne.getAccountabilityDept();
                //将责任单位名称添加到返回值中
                String accountabilityUnitName = "";
                if (null != departmentMap.get(accountabilityUnit)) {
                    accountabilityUnitName = departmentMap.get(accountabilityUnit);
                }
                departmentBean.setAccountabilityDeptName(accountabilityUnitName);
                departmentBeans.add(departmentBean);
            }
        }
        return departmentBeans;
    }

    /**
     * 巡检任务管理部门数据加入到巡检任务列表中
     * @author hedongwei@wistronits.com
     * @date  2019/3/5 17:05
     * @param inspectionTaskVoList 巡检页面显示集合
     * @param inspectionTaskDepartmentMap 巡检任务关联部门map
     * @return List<QueryListInspectionTaskByPageVo> 页面显示巡检任务列表
     */
    public List<QueryListInspectionTaskByPageVo> deptIntoInspectionTask(List<QueryListInspectionTaskByPageVo>  inspectionTaskVoList, Map<String, String> inspectionTaskDepartmentMap) {
        if (null != inspectionTaskVoList && 0 < inspectionTaskVoList.size()) {
            for (QueryListInspectionTaskByPageVo taskVoInfo : inspectionTaskVoList) {
                if (!StringUtils.isEmpty(taskVoInfo.getInspectionTaskId())) {
                    String inspectionTaskId = taskVoInfo.getInspectionTaskId();
                    String deptName = "";
                    if (null != inspectionTaskDepartmentMap.get(inspectionTaskId)) {
                        deptName = inspectionTaskDepartmentMap.get(inspectionTaskId);
                    }
                    taskVoInfo.setAccountabilityDeptName(deptName);
                }
            }
        }
        return inspectionTaskVoList;
    }

    /**
     * 巡检任务管理部门数据加入到巡检任务列表中
     * @author hedongwei@wistronits.com
     * @date  2019/3/7 10:22
     * @param inspectionTaskVoList 巡检任务列表显示数据
     * @param areaMap 区域map
     * @return List<QueryListInspectionTaskByPageVo> 页面显示巡检任务列表
     */
    public List<QueryListInspectionTaskByPageVo> areaInfoInspectionTask(List<QueryListInspectionTaskByPageVo>  inspectionTaskVoList, Map<String, String> areaMap) {
        if (null != inspectionTaskVoList && 0 < inspectionTaskVoList.size() && null != areaMap) {
            for (QueryListInspectionTaskByPageVo inspectionTaskVo : inspectionTaskVoList) {
                //巡检区域编号
                String inspectionAreaId = inspectionTaskVo.getInspectionAreaId();
                if (null != areaMap.get(inspectionAreaId)) {
                    //将区域名称赋给前台显示
                    inspectionTaskVo.setInspectionAreaName(areaMap.get(inspectionAreaId));
                }
            }
        }
        return inspectionTaskVoList;
    }

    /**
     * 获取巡检任务关联巡检部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/5 16:53
     * @param inspectionTaskDepartments 巡检任务关联巡检部门
     * @param departmentMap 所有部门信息
     */
    public Map<String, String> getTaskListDeptName(List<InspectionTaskDepartment> inspectionTaskDepartments,
                                                    Map<String, String> departmentMap ) {
        //查询巡检任务关联责任单位信息
        Map<String ,String> inspectionTaskDepartmentMap = new HashMap<String, String>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (null != inspectionTaskDepartments && 0 < inspectionTaskDepartments.size()) {
            for (InspectionTaskDepartment taskInfo: inspectionTaskDepartments) {
                String relatedTaskId = taskInfo.getInspectionTaskId();
                String deptId = taskInfo.getAccountabilityDept();
                if (null == inspectionTaskDepartmentMap.get(relatedTaskId) || "".equals(inspectionTaskDepartmentMap.get(relatedTaskId))) {
                    //获取单位名称
                    inspectionTaskDepartmentMap.put(relatedTaskId, departmentMap.get(deptId));
                } else {
                    //原来部门信息
                    String oldDepartmentInfo = inspectionTaskDepartmentMap.get(relatedTaskId);
                    //需要在原来的部门信息中增加后续的部门信息
                    String addDepartmentInfo = oldDepartmentInfo + "," + departmentMap.get(deptId);
                    inspectionTaskDepartmentMap.put(relatedTaskId, addDepartmentInfo);
                }
            }
        }
        return inspectionTaskDepartmentMap;
    }





    /**
     * 新增巡检任务过程
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 14:32
     * @param req 新增巡检任务
     * @param req 巡检任务
     * @return boolean 新增巡检任务参数
     */
    public boolean insertInspectionTaskProcess(InsertInspectionTaskReq req) {
        //新增巡检任务
        InspectionTask inspectionTask = this.insertInspectionTaskParam(req);
        int insertInspectionResult = inspectionTaskDao.insert(inspectionTask);
        //新增巡检任务成功后新增关联信息表
        if (0 != insertInspectionResult) {
            InspectionTaskRelated inspectionTaskRelated = new InspectionTaskRelated();
            BeanUtils.copyProperties(req, inspectionTaskRelated);
            //新增巡检任务关联信息
            this.insertInspectionTaskRelated(inspectionTaskRelated);
            //新增巡检任务定时任务
            this.addInspectionTaskJobInfo(req, inspectionTask);
        }
        return true;
    }

    /**
     * 新增巡检任务定时任务信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 12:24
     * @param req 新增巡检任务参数
     * @param inspectionTask 巡检任务类
     */
    public void addInspectionTaskJobInfo(InsertInspectionTaskReq req, InspectionTask inspectionTask) {
        if (!ObjectUtils.isEmpty(req.getTaskStartDate())) {

            boolean notAddFebruaryTaskJob = true;

            Date startTime = new Date(req.getTaskStartDate());

            Calendar calendar = Calendar.getInstance();

            calendar.setTime(startTime);
            //年
            int year = calendar.get(Calendar.YEAR);
            //月
            int month = calendar.get(Calendar.MONTH) + 1;
            //日
            int day = calendar.get(Calendar.DATE);
            //最后一天
            int lastDay = this.getLastDayOfMonth(year, month);
            //是每个月的最后一天
            if (day == lastDay) {
                notAddFebruaryTaskJob = false;
            }

            //巡检任务关联记录
            List<InspectionTaskRelatedJob> inspectionTaskRelatedJobList = new ArrayList<>();

            String jobName = this.getJobName(inspectionTask, InspectionTaskConstants.INSPECTION_TASK_JOB_TYPE_1);

            String jobNameFebruary = this.getJobName(inspectionTask, InspectionTaskConstants.INSPECTION_TASK_JOB_TYPE_2);

            //创建巡检定时任务
            if (!this.addInspectionTaskJob(inspectionTask, InspectionTaskConstants.INSPECTION_TASK_JOB_TYPE_1)) {
                throw new FilinkWorkflowBusinessDataException();
            } else {
                InspectionTaskRelatedJob inspectionTaskRelatedJob = new InspectionTaskRelatedJob();
                inspectionTaskRelatedJob.setInspectionTaskJobName(jobName);
                inspectionTaskRelatedJob.setInspectionTaskId(inspectionTask.getInspectionTaskId());
                inspectionTaskRelatedJobList.add(inspectionTaskRelatedJob);
            }

            int dayNum29 = 29;
            int dayNum30 = 30;

            //日期包括29号或者30号 并且不是每月的最后一天，并且在周期中包含2月份
            if ((dayNum29 == day || dayNum30 == day)) {
                Date taskEndTime = null;
                if (!StringUtils.isEmpty(req.getTaskEndDate())) {
                    taskEndTime = new Date(req.getTaskEndDate());
                }
                //是否包含2月份
                boolean isAfterFebruary = this.isAfterFebruary(startTime, taskEndTime, req.getTaskPeriod());
                if (isAfterFebruary && notAddFebruaryTaskJob) {
                    notAddFebruaryTaskJob = true;
                } else {
                    notAddFebruaryTaskJob = false;
                }
            } else {
                notAddFebruaryTaskJob = false;
            }

            if (notAddFebruaryTaskJob) {
                //新增巡检任务二月份的定时任务
                if (!this.addInspectionTaskJob(inspectionTask, InspectionTaskConstants.INSPECTION_TASK_JOB_TYPE_2)) {
                    throw new FilinkWorkflowBusinessDataException();
                }
                InspectionTaskRelatedJob inspectionTaskRelatedJob = new InspectionTaskRelatedJob();
                inspectionTaskRelatedJob.setInspectionTaskJobName(jobNameFebruary);
                inspectionTaskRelatedJob.setInspectionTaskId(inspectionTask.getInspectionTaskId());
                inspectionTaskRelatedJobList.add(inspectionTaskRelatedJob);
            }

            //新增巡检任务关联表记录信息
            int addRow = this.insertInspectionTaskRelatedJobInfo(inspectionTaskRelatedJobList);
            if (addRow <= 0) {
                throw new FilinkWorkflowBusinessDataException();
            }

        }
    }


    /**
     * 巡检任务周期是否经过2月份
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 14:38
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param taskPeriod 周期
     * @return 巡检任务周期是否经过2月份
     */
    public boolean isAfterFebruary(Date startTime, Date endTime, int taskPeriod) {
        if (ObjectUtils.isEmpty(endTime)) {
            return true;
        }
        if (!ObjectUtils.isEmpty(startTime) && !ObjectUtils.isEmpty(endTime) && !ObjectUtils.isEmpty(taskPeriod)) {
            Calendar startCalendar = Calendar.getInstance();

            startCalendar.setTime(startTime);
            //年
            int startYear = startCalendar.get(Calendar.YEAR);
            //月
            int startMonth = startCalendar.get(Calendar.MONTH) + 1;

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endTime);

            //年
            int endYear = endCalendar.get(Calendar.YEAR);

            //月
            int endMonth = endCalendar.get(Calendar.MONTH) + 1;

            //计算年份
            int calculateYear = startYear;

            //计算月份
            int calculateMonth = startMonth;

            //相差月份
            int diffMonth = DateUtilInfos.getMonthNum(startTime, endTime);


            float percentFloat =  (float)calculateMonth/(float)taskPeriod;
            //小数点后两位
            Integer scale = 2;
            //到小数点后两位
            double percentDouble = new BigDecimal(percentFloat).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
            //周期循环次数
            int cycleCount = (int) Math.floor(percentDouble);

            if (cycleCount > 0) {
                for (int i = 0; i < cycleCount; i++) {
                    calculateMonth  = calculateMonth + taskPeriod;
                    if (12 < calculateMonth) {
                        calculateMonth = calculateMonth - 12;
                        calculateYear = calculateYear + 1;
                    }
                    if (2 == calculateMonth) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 新增巡检工单关联任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 12:58
     * @param inspectionTaskRelatedJobList 巡检工单关联任务参数
     * @return 巡检工单关联任务新增行数
     */
    public int insertInspectionTaskRelatedJobInfo(List<InspectionTaskRelatedJob> inspectionTaskRelatedJobList) {
        if (!ObjectUtils.isEmpty(inspectionTaskRelatedJobList)) {
            Date nowDate = new Date();
            for (InspectionTaskRelatedJob inspectionTaskRelatedJob : inspectionTaskRelatedJobList) {
                //用户编号
                inspectionTaskRelatedJob.setCreateUser(ProcBaseUtil.getUserId());
                //创建时间
                inspectionTaskRelatedJob.setCreateTime(nowDate);
                //巡检任务编号
                inspectionTaskRelatedJob.setInspectionTaskRelatedId(NineteenUUIDUtils.uuid());
            }
            return inspectionTaskRelatedJobDao.insertInspectionTaskRelatedJobBatch(inspectionTaskRelatedJobList);
        }
        return 0;
    }


    /**
     * 获取指定年月的最后一天
     * @param year
     * @param month
     * @return 获取指定年月的最后一天
     */
    public int getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month-1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DATE);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //最后一天的日期
        int day = cal.get(Calendar.DATE);
        return day;
    }

    /**
     * 返回新增巡检任务主表信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 15:49
     * @param req 新增巡检任务参数
     * @return 新增巡检任务主表信息
     */
    public InspectionTask insertInspectionTaskParam(InsertInspectionTaskReq req) {
        InspectionTask inspectionTask = new InspectionTask();
        BeanUtils.copyProperties(req, inspectionTask);
        Date nowDate = new Date();
        //创建时间
        inspectionTask.setCreateTime(nowDate);
        //新增用户信息
        inspectionTask.setCreateUser(RequestHeaderUtils.getHeadParam(RequestHeaderConstants.PARAM_USER_ID));
        //巡检状态 未巡检
        inspectionTask.setInspectionTaskStatus(InspectionTaskConstants.INSPECTION_TASK_WAIT);
        //巡检类型 例行巡检
        inspectionTask.setInspectionTaskType(InspectionTaskConstants.TASK_TYPE_ROUTINE_INSPECTION);
        //巡检开始时间
        if (!ObjectUtils.isEmpty(req.getTaskStartDate())) {
            inspectionTask.setTaskStartTime(new Date(req.getTaskStartDate()));
        }
        if (!ObjectUtils.isEmpty(req.getTaskEndDate())) {
            //巡检结束时间
            inspectionTask.setTaskEndTime(new Date(req.getTaskEndDate()));
        }
        //添加巡检设施数量
        if (null != req.getDeviceList()) {
            inspectionTask.setInspectionDeviceCount(req.getDeviceList().size());
        } else {
            Integer deviceCount = 0;
            inspectionTask.setInspectionDeviceCount(deviceCount);
        }
        return inspectionTask;
    }

    /**
     * 新增巡检任务关联信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 15:56
     * @param inspectionTaskRelated 巡检任务关联信息
     */
    public void insertInspectionTaskRelated(InspectionTaskRelated inspectionTaskRelated) {
        //新增关联设施
        List<InspectionTaskDevice> insertDeviceList = inspectionTaskRelated.getDeviceList();
        if (!ObjectUtils.isEmpty(insertDeviceList)) {
            if (!this.insertDeviceListBatch(insertDeviceList, inspectionTaskRelated.getInspectionTaskId())) {
                throw new FilinkWorkflowBusinessDataException();
            }
        }

        //新增关联单位
        List<InspectionTaskDepartment> departmentList = inspectionTaskRelated.getDepartmentList();
        if (!ObjectUtils.isEmpty(departmentList)) {
            if (!this.insertDepartmentListBatch(departmentList, inspectionTaskRelated.getInspectionTaskId())) {
                throw new FilinkWorkflowBusinessDataException();
            }
        }
    }

    /**
     * 新增巡检定时任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 10:54
     * @param inspectionTask 巡检任务参数
     * @return 新增巡检定时任务结果
     */
    public boolean addInspectionTaskJob(InspectionTask inspectionTask, String taskType) {
        String jobName = this.getJobName(inspectionTask, taskType);
        InspectionTaskBizBean taskBiz = this.generateInspectionTaskBizBean(inspectionTask, jobName);
        Result result = null;
        if (InspectionTaskConstants.INSPECTION_TASK_JOB_TYPE_1.equals(taskType)) {
            //新增巡检任务的方法
            result = inspectionTaskFeign.addTaskJob(taskBiz);
        } else if (InspectionTaskConstants.INSPECTION_TASK_JOB_TYPE_2.equals(taskType)) {
            //新增二月份最后一天巡检任务的方法
            result = inspectionTaskFeign.addFebruaryTaskJob(taskBiz);
        }

        boolean resultInfo = checkResultInfo(result);
        return resultInfo;
    }

    /**
     * 获取任务名称
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 11:55
     * @param inspectionTask 巡检任务参数
     * @param taskType 任务类型
     * @return 任务名称
     */
    public String getJobName(InspectionTask inspectionTask, String taskType) {
        String jobName = "";
        if (InspectionTaskConstants.INSPECTION_TASK_JOB_TYPE_1.equals(taskType)) {
            jobName = inspectionTask.getInspectionTaskName() + InspectionTaskConstants.INSPECTION_TASK_JOB_TYPE_1;
        } else if (InspectionTaskConstants.INSPECTION_TASK_JOB_TYPE_2.equals(taskType)) {
            jobName = inspectionTask.getInspectionTaskName() + InspectionTaskConstants.INSPECTION_TASK_JOB_TYPE_2;
        }
        return jobName;
    }

    /**
     * 生成巡检任务定时任务参数
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 11:45
     * @param inspectionTask 巡检任务
     * @param jobTaskName 任务开始名称
     * @return 生成巡检任务定时任务结果
     */
    public InspectionTaskBizBean generateInspectionTaskBizBean(InspectionTask inspectionTask, String jobTaskName) {
        InspectionTaskBizBean taskBiz = new InspectionTaskBizBean();
        taskBiz.setInspectionTaskId(inspectionTask.getInspectionTaskId());
        taskBiz.setJobGroup("inspectiontask");
        taskBiz.setTaskPeriod(inspectionTask.getTaskPeriod());
        taskBiz.setTaskStartTime(inspectionTask.getTaskStartTime());
        taskBiz.setJobTaskName(jobTaskName);
        return taskBiz;
    }

    /**
     * 检验返回结果是否正确
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 12:49
     * @param result 返回结果
     * @return 返回结果是否正确
     */
    public boolean checkResultInfo(Result result) {
        if (!ObjectUtils.isEmpty(result)) {
            if (!ResultCode.SUCCESS.equals(result.getCode())) {
                return false;
            }
        } else {
            return true;
        }
        return true;
    }

    /**
     * 修改巡检任务过程
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 14:32
     * @param req 修改巡检任务
     * @return boolean 修改巡检任务参数
     */
    public boolean updateInspectionTaskProcess(UpdateInspectionTaskReq req) {

        //获取修改巡检任务参数
        InspectionTask inspectionTask = this.createUpdateInspectionTaskParam(req);

        //巡检任务编号
        String inspectionTaskId = req.getInspectionTaskId();

        //修改巡检任务主表信息
        inspectionTaskDao.updateById(inspectionTask);

        //删除关联设施
        if (!this.deleteInspectionTaskDevice(inspectionTask)) {
            throw new FilinkWorkflowBusinessDataException();
        }

        //删除关联单位
        if (!this.deleteInspectionTaskDepartment(inspectionTask)) {
            throw new FilinkWorkflowBusinessDataException();
        }

        InspectionTaskRelated inspectionTaskRelated = new InspectionTaskRelated();
        BeanUtils.copyProperties(req, inspectionTaskRelated);

        //新增关联信息
        this.insertInspectionTaskRelated(inspectionTaskRelated);

        //创建巡检任务
        this.updateInspectionTaskCreateJob(req, inspectionTaskId);
        return true;
    }

    /**
     * 创建修改巡检任务主表参数
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 16:07
     * @param req 修改巡检任务参数
     * @return 修改巡检任务主表参数
     */
    public InspectionTask createUpdateInspectionTaskParam(UpdateInspectionTaskReq req) {
        InspectionTask inspectionTask = new InspectionTask();
        BeanUtils.copyProperties(req, inspectionTask);
        inspectionTask.setInspectionTaskId(req.getInspectionTaskId());
        Date nowDate = new Date();
        //更新时间
        inspectionTask.setUpdateTime(nowDate);
        //修改用户信息
        inspectionTask.setUpdateUser(RequestHeaderUtils.getHeadParam(RequestHeaderConstants.PARAM_USER_ID));

        //巡检开始时间
        if (!ObjectUtils.isEmpty(req.getTaskStartDate())) {
            inspectionTask.setTaskStartTime(new Date(req.getTaskStartDate()));
        }

        if (!ObjectUtils.isEmpty(req.getTaskEndDate())) {
            //巡检结束时间
            inspectionTask.setTaskEndTime(new Date(req.getTaskEndDate()));
        }

        //添加巡检设施数量
        if (null != req.getDeviceList()) {
            inspectionTask.setInspectionDeviceCount(req.getDeviceList().size());
        } else {
            Integer deviceCount = 0;
            inspectionTask.setInspectionDeviceCount(deviceCount);
        }
        return inspectionTask;
    }

    /**
     * 修改巡检任务创建定时任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 12:40
     * @param req 修改巡检任务参数
     * @param inspectionTaskId 巡检任务编号
     */
    public void updateInspectionTaskCreateJob(UpdateInspectionTaskReq req, String inspectionTaskId) {
        //查询定时任务是否存在
        InspectionTaskRelatedJobReq relatedJobParam = new InspectionTaskRelatedJobReq();
        relatedJobParam.setInspectionTaskId(req.getInspectionTaskId());
        relatedJobParam.setIsDeleted(WorkFlowBusinessConstants.IS_NOT_DELETED);
        List<InspectionTaskRelatedJob> relatedJobs = inspectionTaskRelatedJobDao.selectInspectionTaskRelatedInfo(relatedJobParam);
        if (!ObjectUtils.isEmpty(relatedJobs)) {

            List<String> inspectionTaskList = new ArrayList<>();
            for (InspectionTaskRelatedJob relatedJobOne : relatedJobs) {
                inspectionTaskList.add(relatedJobOne.getInspectionTaskJobName());
            }

            //删除原来的巡检定时任务
            Result result = inspectionTaskFeign.deleteTaskJobList(inspectionTaskList);
            if (!checkResultInfo(result)) {
                throw new FilinkWorkflowBusinessDataException();
            }

            //删除巡检任务关联任务记录
            InspectionTaskRelatedJob taskJob = new InspectionTaskRelatedJob();
            taskJob.setInspectionTaskId(req.getInspectionTaskId());
            taskJob.setIsDeleted(WorkFlowBusinessConstants.IS_DELETED);
            inspectionTaskRelatedJobDao.logicDeleteInspectionTaskRelatedJobByInspectionTaskId(taskJob);
        }


        InspectionTask inspectionTaskInfo = new InspectionTask();
        BeanUtils.copyProperties(req, inspectionTaskInfo);
        if (!ObjectUtils.isEmpty(req.getTaskStartDate()) && !ObjectUtils.isEmpty(req.getTaskEndDate()) && !ObjectUtils.isEmpty(req.getTaskPeriod())) {
            inspectionTaskInfo.setTaskStartTime(new Date(req.getTaskStartDate()));
            inspectionTaskInfo.setTaskEndTime(new Date(req.getTaskStartDate()));
            InsertInspectionTaskReq insertReq = new InsertInspectionTaskReq();
            BeanUtils.copyProperties(req, insertReq);
            //创建巡检定时任务
            this.addInspectionTaskJobInfo(insertReq, inspectionTaskInfo);
        }
    }

    /**
     * 批量新增设施集合
     * @author hedongwei@wistronits.com
     * @date  2019/2/28 18:16
     * @param insertDeviceList 新增设施集合
     * @param inspectionTaskId 新增巡检任务编号
     * @return boolean 新增设施结果
     */
    public boolean insertDeviceListBatch(List<InspectionTaskDevice> insertDeviceList, String inspectionTaskId) {
        //批量新增设施集合结果
        boolean resultInfo = true;
        //批量新增关联设施
        int deviceBatchResult = inspectionTaskDeviceService.insertInspectionTaskDeviceBatch(insertDeviceList, inspectionTaskId);
        //查询新增结果是否为参数的对应结果，不一致的话就返回false
        if (null != insertDeviceList && deviceBatchResult == insertDeviceList.size()) {
            resultInfo = true;
        } else {
            resultInfo = false;
        }
        return resultInfo;
    }

    /**
     * 批量新增巡检任务关联单位
     * @author hedongwei@wistronits.com
     * @date  2019/2/28 18:16
     * @param insertDepartmentList 新增单位集合
     * @param inspectionTaskId 新增巡检任务编号
     * @return boolean 新增单位结果
     */
    public boolean insertDepartmentListBatch(List<InspectionTaskDepartment> insertDepartmentList, String inspectionTaskId) {
        //批量新增单位集合结果
        boolean resultInsertDepartment = true;
        //批量新增巡检任务关联设施
        int departmentBatchResult = inspectionTaskDepartmentService.insertInspectionTaskDepartmentBatch(insertDepartmentList, inspectionTaskId);
        if (null != insertDepartmentList && departmentBatchResult == insertDepartmentList.size()) {
            resultInsertDepartment = true;
        } else {
            resultInsertDepartment = false;
        }
        return resultInsertDepartment;
    }

    /**
     * 删除巡检任务关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/2/28 18:22
     * @param inspectionTask 删除巡检任务关联设施参数
     */
    public boolean deleteInspectionTaskDevice(InspectionTask inspectionTask) {
        //删除设施结果
        boolean deleteDeviceResult = true;
        InspectionTaskDevice inspectionTaskDevice = new InspectionTaskDevice();
        //获取巡检任务编号
        inspectionTaskDevice.setInspectionTaskId(inspectionTask.getInspectionTaskId());
        //根据巡检任务编号删除关联的设施协议信息
        int deleteInspectionTaskRow = inspectionTaskDeviceService.deleteInspectionTaskDevice(inspectionTaskDevice);
        if (deleteInspectionTaskRow <= 0) {
            deleteDeviceResult = false;
        }
        return deleteDeviceResult;
    }

    /**
     * 删除巡检任务关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/2/28 18:22
     * @param inspectionTask 删除巡检任务关联设施参数
     */
    public boolean deleteInspectionTaskDepartment(InspectionTask inspectionTask) {
        //删除设施结果
        boolean deleteDepartmentResult = true;
        InspectionTaskDepartment inspectionTaskDepartment = new InspectionTaskDepartment();
        //获取巡检任务编号
        inspectionTaskDepartment.setInspectionTaskId(inspectionTask.getInspectionTaskId());
        //根据巡检任务编号删除关联的设施协议信息
        int deleteInspectionTaskRow = inspectionTaskDepartmentService.deleteInspectionTaskDepartment(inspectionTaskDepartment);
        if (deleteInspectionTaskRow <= 0) {
            deleteDepartmentResult = false;
        }
        return deleteDepartmentResult;
    }




}
