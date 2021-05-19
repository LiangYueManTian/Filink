package com.fiberhome.filink.workflowbusinessserver.service.impl.procbase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.alarmcurrentapi.bean.AlarmCurrent;
import com.fiberhome.filink.alarmhistoryapi.api.AlarmHistoryFeign;
import com.fiberhome.filink.alarmhistoryapi.bean.AlarmHistory;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.AreaInfo;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowapi.constant.ProcessConstants;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTask;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskRecord;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.*;
import com.fiberhome.filink.workflowbusinessserver.bean.procclear.ProcClearFailure;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import com.fiberhome.filink.workflowbusinessserver.component.log.AddLogForApp;
import com.fiberhome.filink.workflowbusinessserver.constant.InspectionTaskConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import com.fiberhome.filink.workflowbusinessserver.dao.procbase.ProcBaseDao;
import com.fiberhome.filink.workflowbusinessserver.exception.*;
import com.fiberhome.filink.workflowbusinessserver.exception.procbase.procoperate.*;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontask.UpdateInspectionStatusReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.*;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.procrelated.ProcRelatedDeviceListForDeviceIdsReq;
import com.fiberhome.filink.workflowbusinessserver.req.process.*;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessDetail;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcRelatedDeviceResp;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procclear.Alarm;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procclear.ClearFailureDownLoadDetail;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procinspection.InspectionDownLoadDetail;
import com.fiberhome.filink.workflowbusinessserver.service.alarmprocess.AlarmProcessService;
import com.fiberhome.filink.workflowbusinessserver.service.impl.process.WorkflowServiceImpl;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskRecordService;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcBaseService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcLogService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.procrelated.ProcRelatedService;
import com.fiberhome.filink.workflowbusinessserver.service.procclear.ProcClearFailureService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionRecordService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionService;
import com.fiberhome.filink.workflowbusinessserver.stream.WorkflowBusinessStreams;
import com.fiberhome.filink.workflowbusinessserver.utils.common.CastListUtil;
import com.fiberhome.filink.workflowbusinessserver.utils.common.CastMapUtil;
import com.fiberhome.filink.workflowbusinessserver.utils.common.ProcBaseUtil;
import com.fiberhome.filink.workflowbusinessserver.utils.procbase.ProcBaseResultCode;
import com.fiberhome.filink.workflowbusinessserver.utils.procbase.ProcBaseValidate;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkFlowBusinessMsg;
import com.fiberhome.filink.workflowbusinessserver.vo.procbase.TurnUserListVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPageBean;


/**
 * <p>
 * 流程主表 服务实现类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-02-18
 */
@Service
@Slf4j
public class ProcBaseServiceImpl extends ServiceImpl<ProcBaseDao, ProcBase> implements ProcBaseService {

    @Autowired
    private ProcBaseDao procBaseDao;

    @Autowired
    private ProcInspectionRecordService procInspectionRecordService;

    @Autowired
    private ProcInspectionService procInspectionService;

    @Autowired
    private ProcClearFailureService procClearFailureService;

    @Autowired
    private WorkflowServiceImpl workflowService;

    @Autowired
    private InspectionTaskRecordService inspectionTaskRecordService;

    @Autowired
    private InspectionTaskService inspectionTaskService;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private AlarmCurrentFeign alarmCurrentFeign;

    @Autowired
    private AlarmHistoryFeign alarmHistoryFeign;

    @Autowired
    private AddLogForApp addLogForApp;

    @Autowired
    private AlarmProcessService alarmProcessService;

    @Autowired
    private ProcRelatedService procRelatedService;

    @Autowired
    private ProcLogService procLogService;

    /**
     * 远程调用日志服务
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 注入上下文
     */
    @Autowired
    private ApplicationContext context;

    /**
     * 远程调用部门服务
     */
    @Autowired
    private DepartmentFeign departmentFeign;

    @Autowired
    private WorkflowBusinessStreams workflowBusinessStreams;

    /**
     * app批量下载条数
     */
    @Value("${batchDownloadProcNum}")
    private Integer batchDownloadProcNum;

    /**
     * 表示代理对象，不是目标对象
     */
    private ProcBaseService proxySelf;

    /**
     * 系统语言
     */
    @Autowired
    private SystemLanguageUtil systemLanguage;

    /**
     * 初始化方法
     */
    @PostConstruct
    private void setSelf() {
        //从上下文获取代理对象（如果通过proxySelf=this是不对的，this是目标对象）
        //此种方法不适合于prototype Bean，因为每次getBean返回一个新的Bean
        proxySelf = context.getBean(ProcBaseServiceImpl.class);
    }


    /**
     * 查询工单名称是否存在
     *
     * @param procId 工单id
     * @param title 工单名称
     * @return Boolean
     */
    @Override
    public Boolean queryTitleIsExists(String procId, String title) {
        ProcBase procBase = procBaseDao.queryTitleIsExists(title);
        if (StringUtils.isEmpty(procId)) {
            if (!ObjectUtils.isEmpty(procBase) && !StringUtils.isEmpty(procBase.getProcId())) {
                return true;
            }
        } else {
            if (!ObjectUtils.isEmpty(procBase) && !StringUtils.isEmpty(procBase.getProcId())) {
                //修改校验时由于当前数据已存在，要排除当前数据
                if (!procBase.getProcId().equals(procId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 新增工单
     *
     * @param processInfo 工单汇总类
     * @return Result
     */
    @Override
    public Result addProcBase(ProcessInfo processInfo) {
        //校验参数合法性
        Result result = this.checkProcParams(processInfo);
        if (result.getCode() != 0){
            return result;
        }

        //新增流程信息
        ProcBase procBase = proxySelf.addProcBaseInfo(processInfo);
        processInfo.setProcBase(procBase);

        //发起流程
        this.startProcessInfo(processInfo);

        //获取流程名及流程id，用于保存操作日志
        BeanUtils.copyProperties(processInfo.getProcBase(),processInfo);
        String format = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        log.info("end add proc the procId : {} the end time : {}, the procType : {} "
                ,processInfo.getProcBase().getProcId(), simpleDateFormat.format(System.currentTimeMillis()) , processInfo.getProcBase().getProcType());
        if (ProcBaseConstants.PROC_RESOURCE_TYPE_3.equals(processInfo.getProcBase().getProcResourceType())) {
            log.info("the device : {} saveTime : {}" ,processInfo.getProcBase().getDeviceId(), simpleDateFormat.format(System.currentTimeMillis()));
        }
        if (ProcBaseConstants.PROC_RESOURCE_TYPE_2.equals(processInfo.getProcBase().getProcResourceType())
                && ProcBaseConstants.PROC_INSPECTION.equals(processInfo.getProcBase().getProcType())) {
            //巡检任务新增巡检工单
            log.info("end inspection task add inspection proc the procId : {} the end time : {}, the inspectionTaskId : {} "
                    ,processInfo.getProcBase().getProcId() , simpleDateFormat.format(System.currentTimeMillis()) , processInfo.getProcInspection().getInspectionTaskId());
        }
        return ResultUtils.success(ProcBaseResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.ADD_PROC_SUCCESS));
    }

    /**
     * 新增流程信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/4 15:42
     * @param processInfo 流程主要信息
     * @return 返回工单编号
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProcBase addProcBaseInfo(ProcessInfo processInfo) {
        //设置用户信息
        this.setUserInfo(processInfo,WorkFlowBusinessConstants.OPERATE_TYPE_ADD);
        //获取返回工单id
        BeanUtils.copyProperties(processInfo.getProcBaseReq(),processInfo.getProcBase());
        //保存工单特性信息
        this.saveProcSpecific(processInfo);
        // 保存关联信息
        this.saveProcRelate(processInfo);
        //需要新增巡检记录
        this.addInspectionRecord(processInfo);
        //需要修改巡检任务状态
        //巡检状态巡检中
        String inspectionTaskStatus = InspectionTaskConstants.INSPECTION_TASK_DOING;
        if (!this.updateInspectionTaskStatus(processInfo, inspectionTaskStatus)) {
            throw new AddProcErrorException();
        }

        return processInfo.getProcBase();
    }

    /**
     * 校验重新生成参数
     * @author hedongwei@wistronits.com
     * @date  2019/5/14 21:46
     * @param processInfo 工单新增
     * @return 校验重新生成参数
     */
    public Result checkProcProcessForRegenerate(ProcessInfo processInfo) {
        String beforeProcId = processInfo.getProcBase().getRegenerateId();

        //检查重新生成的条件
        Result checkResult = this.checkRegenerateCondition(beforeProcId);
        if (null != checkResult) {
            return checkResult;
        }

        //校验参数合法性
        Result result = this.checkProcParams(processInfo);
        if (result.getCode() != 0){
            return result;
        }
        return null;
    }

    /**
     * 重新生成工单过程
     * @author hedongwei@wistronits.com
     * @date  2019/5/14 21:44
     * @param processInfo 工单参数
     * @return 重新生成
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProcBase regenerateProcProcess(ProcessInfo processInfo) {
        //原来工单的编号
        String beforeProcId = processInfo.getProcBase().getRegenerateId();
        //原来工单的信息
        ProcBase oldProcBase = procBaseDao.queryProcessByProcId(beforeProcId);

        //新增流程信息,同时获取到重新生成工单信息
        ProcBase addProcBase = proxySelf.addProcBaseInfo(processInfo);
        //重新生成工单编号
        String addProcId = addProcBase.getProcId();

        //修改原工单的字段
        if (!ObjectUtils.isEmpty(oldProcBase)) {
            if (ProcBaseConstants.PROC_INSPECTION.equals(oldProcBase)) {
                ProcInspection beforeProcInspection = new ProcInspection();
                beforeProcInspection.setProcId(beforeProcId);
                beforeProcInspection.setRegenerateId(addProcId);
                procInspectionService.updateProcInspectionInfo(beforeProcInspection);
            } else if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(oldProcBase)) {
                ProcClearFailure beforeProcClearFailure = new ProcClearFailure();
                beforeProcClearFailure.setProcId(beforeProcId);
                beforeProcClearFailure.setRegenerateId(addProcId);
                procClearFailureService.updateClearFailure(beforeProcClearFailure);
            }
        }


        CheckSingleBackReq req = new CheckSingleBackReq();
        req.setProcId(beforeProcId);

        if (!ObjectUtils.isEmpty(oldProcBase)) {
            //退单状态
            String singleBackStatus = ProcBaseConstants.PROC_STATUS_SINGLE_BACK;
            //未确认退单
            String isCheckSingleBack = ProcBaseConstants.NOT_CHECK_SINGLE_BACK;
            //工单状态为退单状态和工单未确认退单时允许退单确认操作
            if (singleBackStatus.equals(oldProcBase.getStatus()) && isCheckSingleBack.equals(oldProcBase.getIsCheckSingleBack())) {
                //退单确认
                this.checkResultProcess(req);
            }
        }
        return addProcBase;
    }

    /**
     * 重新生成工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/4 15:32
     * @param processInfo 重新生成工单
     * @return 重新生成工单信息
     */
    @Override
    public Result regenerateProc(ProcessInfo processInfo) {

        Result result = this.checkProcProcessForRegenerate(processInfo);
        if (null != result) {
            return result;
        }

        ProcBase addProcBase = proxySelf.regenerateProcProcess(processInfo);
        processInfo.setProcBase(addProcBase);
        //发起流程
        this.startProcessInfo(processInfo);
        String format = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        log.info("end add proc the procId : {} the end time : {}, the procType : {} "
                ,processInfo.getProcBase().getProcId(), simpleDateFormat.format(System.currentTimeMillis()) , processInfo.getProcBase().getProcType());

        //新增工单成功
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.ADD_PROC_SUCCESS));
    }

    /**
     * 校验重新生成工单结果
     * @author hedongwei@wistronits.com
     * @date  2019/4/4 17:22
     * @param procId 工单编号
     * @return 校验重新生成工单结果
     */
    public Result checkRegenerateCondition(String procId) {
        if (StringUtils.isEmpty(procId)) {
            return WorkFlowBusinessMsg.paramErrorMsg();
        }

        ProcBase procBase = this.getProcBaseByProcId(procId);
        if (ObjectUtils.isEmpty(procBase)) {
            return WorkFlowBusinessMsg.paramErrorMsg();
        } else {
            //原工单的状态为已退单，并且没有确认退单，并且没有重新生成过工单才可以点击重新生成按钮
            if (!ProcBaseConstants.PROC_STATUS_SINGLE_BACK.equals(procBase.getStatus())) {
                throw new AddProcErrorException();
            }
        }
        return null;
    }

    /**
     * 修改工单
     *
     * @param processInfo 工单汇总类
     * @return Result
     */
    @Override
    public Result updateProcessById(ProcessInfo processInfo) {
        //校验参数合法性
        Result result = this.checkProcParams(processInfo);
        if (result.getCode() != 0){
            return result;
        }
        //设置用户信息
        this.setUserInfo(processInfo,WorkFlowBusinessConstants.OPERATE_TYPE_UPDATE);
        //判断是否缺少工单id
        if (StringUtils.isEmpty(processInfo.getProcBaseReq().getProcId())){
            return ResultUtils.warn(ProcBaseResultCode.PROC_ID_LOSE,I18nUtils.getSystemString(ProcBaseI18n.PROC_ID_LOSE));
        }
        //获取工单基础信息
        ProcBase procBase = procBaseDao.queryProcessByProcId(processInfo.getProcBaseReq().getProcId());
        if (ObjectUtils.isEmpty(procBase) || StringUtils.isEmpty(procBase.getProcId())){
            return ResultUtils.warn(ProcBaseResultCode.PROC_NOT_EXIST,I18nUtils.getSystemString(ProcBaseI18n.PROC_IS_NOT_EXIST));
        }

        //修改工单信息
        proxySelf.updateProcBaseByIdProcess(procBase, processInfo);

        //待指派状态
        String procStatusAssigned = ProcBaseConstants.PROC_STATUS_ASSIGNED;
        String status = procBase.getStatus();
        //是待指派状态的数据，并且有责任单位的数据可以发起流程
        if (procStatusAssigned.equals(status)) {
            //发起流程
            this.startProcessInfo(processInfo);
        }
        String format = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        log.info("end update proc the procId : {} the end time : {}, the procType : {} "
                ,procBase.getProcId(), simpleDateFormat.format(System.currentTimeMillis()) , ProcBaseConstants.PROC_CLEAR_FAILURE);
        //获取流程名及流程id，用于保存操作日志
        BeanUtils.copyProperties(processInfo.getProcBase(),processInfo);
        return ResultUtils.success(ProcBaseResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.UPDATE_PROC_SUCCESS), true);
    }

    /**
     * 根据工单id修改工单数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/15 14:21
     * @param procBase 工单信息
     * @param processInfo 修改工单参数
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateProcBaseByIdProcess(ProcBase procBase, ProcessInfo processInfo) {
        if (ProcBaseConstants.PROC_STATUS_ASSIGNED.equals(procBase.getStatus())){
            //修改时间
            ProcBaseReq procBaseReq = processInfo.getProcBaseReq();
            procBaseReq.setUpdateTime(new Date());
            // 修改基础信息
            procBaseDao.updateProcBaseById(processInfo.getProcBaseReq());

            // 保存关联信息
            this.saveProcRelate(processInfo);
            //保存工单特性信息
            this.saveProcSpecific(processInfo);
        } else {
            ProcBaseReq procBaseReq = processInfo.getProcBaseReq();
            procBaseReq.setUpdateTime(new Date());
            if (ProcBaseConstants.PROC_INSPECTION.equals(procBase.getProcType())) {
                ProcInspection updateProcInspection = new ProcInspection();
                BeanUtils.copyProperties(procBaseReq, updateProcInspection);
                // 修改备注信息
                procInspectionService.updateProcInspectionRemarkById(updateProcInspection);
            } else {
                ProcClearFailure updateProcClear = new ProcClearFailure();
                BeanUtils.copyProperties(procBaseReq, updateProcClear);
                // 修改备注信息
                procClearFailureService.updateProcClearFailureRemarkById(updateProcClear);
            }
        }
    }

    /**
     * 开启流程
     * @author hedongwei@wistronits.com
     * @date  2019/3/19 18:02
     * @param processInfo
     */
    public void startProcessInfo(ProcessInfo processInfo) {
        String deptType = processInfo.getProcBase().getDeptType();
        if (!StringUtils.isEmpty(deptType)) {
            //部门类型是部门时才能开启流程
            if (WorkFlowBusinessConstants.DEPT_TYPE_DEPT.equals(deptType)) {
                //发起流程参数
                StartProcessInfoReq processInfoReq = this.getStartProcessInfoReq(processInfo);
                //发起流程
                Result result = workflowService.startProcess(processInfoReq);
                if (!ObjectUtils.isEmpty(result)) {
                    if (!ResultCode.SUCCESS.equals(result.getCode())) {
                        throw new AddProcErrorException();
                    }
                }
            }
        }
    }

    /**
     * 新增巡检记录
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 23:13
     * @param processInfo 新增巡检记录参数
     * @return 返回新增巡检记录结果
     */
    public void addInspectionRecord(ProcessInfo processInfo) {
        if (!ObjectUtils.isEmpty(processInfo)) {
            if (!ObjectUtils.isEmpty(processInfo.getProcInspection())) {
                if (!ObjectUtils.isEmpty(processInfo.getProcInspection().getInspectionTaskId())) {
                    String inspectionTaskId = processInfo.getProcInspection().getInspectionTaskId();
                    //查询巡检任务信息
                    InspectionTask inspectionTask = inspectionTaskService.selectById(inspectionTaskId);
                    if (ObjectUtils.isEmpty(inspectionTask)) {
                        throw new AddProcErrorException();
                    }
                    InspectionTaskRecord inspectionTaskRecord = new InspectionTaskRecord();
                    BeanUtils.copyProperties(inspectionTask, inspectionTaskRecord);
                    Result result = inspectionTaskRecordService.addInspectionTaskRecord(inspectionTaskRecord);
                    if (!this.checkProcessInfo(result)) {
                        throw new AddProcErrorException();
                    }
                }
            }
        }
    }

    /**
     * 修改巡检任务状态
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 20:39
     * @param processInfo 流程类
     * @param inspectionTaskStatus 巡检任务状态
     * @return 返回修改巡检任务状态的结果
     */
    @Override
    public boolean updateInspectionTaskStatus(ProcessInfo processInfo, String inspectionTaskStatus) {
        if (!ObjectUtils.isEmpty(processInfo)) {
            if (!ObjectUtils.isEmpty(processInfo.getProcInspection())) {
                if (!ObjectUtils.isEmpty(processInfo.getProcInspection().getInspectionTaskId())) {
                    String inspectionTaskId = processInfo.getProcInspection().getInspectionTaskId();
                    UpdateInspectionStatusReq updateInspectionStatusReq = new UpdateInspectionStatusReq();
                    updateInspectionStatusReq.setInspectionTaskId(inspectionTaskId);
                    updateInspectionStatusReq.setInspectionTaskStatus(inspectionTaskStatus);
                    Result result = inspectionTaskService.updateInspectionStatus(updateInspectionStatusReq);
                    if (!this.checkProcessInfo(result)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 获取启动流程的参数
     * @author hedongwei@wistronits.com
     * @date  2019/3/19 16:11
     * @param processInfo 流程信息
     */
    public StartProcessInfoReq getStartProcessInfoReq(ProcessInfo processInfo) {
        StartProcessInfoReq req = new StartProcessInfoReq();
        BeanUtils.copyProperties(processInfo.getProcBase(), req);
        //部门编号(太长暂定使用工单编号)
        req.setDepartmentIds(processInfo.getProcBase().getProcId());
        return req;
    }

    /**
     * 删除工单
     *
     * @param ids 删除工单id列表
     * @param isDeleted 逻辑删除字段
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateProcessIsDeletedByIds(List<String> ids,String isDeleted) {
        String format = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        log.info("begin delete proc, the begin time : {}", simpleDateFormat.format(System.currentTimeMillis()));
        List<ProcBaseReq> procBaseReqList = new ArrayList<>();
        //根据工单编号查询工单信息
        ProcBaseReq procBaseReqParam = ProcBaseReq.getSearchDeleteProcBaseParam(ids, isDeleted);
        List<ProcBase> procBaseList = procBaseDao.queryProcessByProcIds(procBaseReqParam);
        //获取工单map
        Map<String, ProcBase> procBaseMap = CastMapUtil.getProcBaseMap(procBaseList);

        //判断能否被删除，只要有不能被删除的，直接退出
        for (int i = 0;i<ids.size();i++){
            //转换持久类对象
            ProcBaseReq procBaseReq = new ProcBaseReq();
            Result checkProcResult = ProcBaseValidate.checkProcIsAbleDelete(ids, i, procBaseMap, isDeleted);
            if (null != checkProcResult) {
                return checkProcResult;
            }
            ProcBase procBase = procBaseMap.get(ids.get(i));
            BeanUtils.copyProperties(procBase,procBaseReq);
            procBaseReq.setIsDeleted(isDeleted);
            procBaseReq.setUpdateUser(this.getUserId());
            procBaseReq.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
            procBaseReqList.add(procBaseReq);
        }

        //批量删除/恢复信息
        if (!ObjectUtils.isEmpty(procBaseReqList)) {
            procBaseReqParam.setIsDeleted(isDeleted);
            //删除工单信息
            this.deleteProcProcess(procBaseReqParam, procBaseReqList, isDeleted);
        }
        log.info("end delete proc, the end time : {}", simpleDateFormat.format(System.currentTimeMillis()));
        return ResultUtils.success(ProcBaseResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.DELETE_PROC_SUCCESS));
    }

    /**
     * 修改流程状态信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/18 20:56
     * @param procBase 流程编号
     * @return 返回修改的结果
     */
    @Override
    public Result updateProcBaseStatusById(ProcBase procBase) {
        if (StringUtils.isEmpty(procBase.getProcId())) {
            //工单编号不能为空
            return ResultUtils.warn(ProcBaseResultCode.PROC_ID_LOSE,I18nUtils.getSystemString(ProcBaseI18n.PROC_ID_LOSE));
        }
        //获取工单基础信息
        ProcBase procBaseInfo = procBaseDao.queryProcessByProcId(procBase.getProcId());
        if (ObjectUtils.isEmpty(procBaseInfo) || StringUtils.isEmpty(procBaseInfo.getProcId())){
            return ResultUtils.warn(ProcBaseResultCode.PROC_NOT_EXIST,I18nUtils.getSystemString(ProcBaseI18n.PROC_IS_NOT_EXIST));
        }
        if (StringUtils.isEmpty(procBase.getStatus())) {
            //工单状态不能为空
            return ResultUtils.warn(ProcBaseResultCode.PROC_PARAM_ERROR, I18nUtils.getSystemString(ProcBaseI18n.PROC_PARAM_ERROR));
        }

        if (ProcBaseConstants.PROC_INSPECTION.equals(procBaseInfo.getProcType())) {
            //修改巡检工单的状态
            ProcInspection procInspection = new ProcInspection();
            BeanUtils.copyProperties(procBase, procInspection);
            procInspectionService.updateProcInspectionStatusById(procInspection);
        } else if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBaseInfo.getProcType())) {
            //修改销障工单的状态
            ProcClearFailure procClearFailure = new ProcClearFailure();
            BeanUtils.copyProperties(procBase, procClearFailure);
            procClearFailureService.updateProcClearFailureStatusById(procClearFailure);
        }

        return ResultUtils.success(ProcBaseResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.UPDATE_PROC_SUCCESS));
    }

    /**
     * 根据工单id获取工单类型
     *
     * @param id 工单id
     * @return Result
     */
    @Override
    public Result getProcTypeByProcId(String id) {
        if (StringUtils.isEmpty(id)) {
            //工单编号不能为空
            return ResultUtils.warn(ProcBaseResultCode.PROC_ID_LOSE,I18nUtils.getSystemString(ProcBaseI18n.PROC_ID_LOSE));
        }
        //获取工单基础信息
        ProcBase procBaseInfo = procBaseDao.queryProcessByProcId(id);
        if (ObjectUtils.isEmpty(procBaseInfo) || StringUtils.isEmpty(procBaseInfo.getProcId())){
            return ResultUtils.warn(ProcBaseResultCode.PROC_NOT_EXIST,I18nUtils.getSystemString(ProcBaseI18n.PROC_IS_NOT_EXIST));
        }

        Map<String,String> statusMap = new HashMap<>(1);
        //转换页面需要状态值
        //未完成
        if (ProcBaseConstants.PROC_STATUS_ASSIGNED.equals(procBaseInfo.getStatus()) ||
                ProcBaseConstants.PROC_STATUS_PROCESSING.equals(procBaseInfo.getStatus()) ||
                ProcBaseConstants.PROC_STATUS_PENDING.equals(procBaseInfo.getStatus())
        ){
            //销障工单
            if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBaseInfo.getProcType())){
                statusMap.put("status",ProcBaseConstants.PIC_STATUS_CLEAR_UNFINISHED);
                //巡检
            } else {
                statusMap.put("status",ProcBaseConstants.PIC_STATUS_INSPECTION_UNFINISHED);
            }
            //未完成
        } else if (ProcBaseConstants.PROC_STATUS_SINGLE_BACK.equals(procBaseInfo.getStatus()) &&
                ProcBaseConstants.NOT_CHECK_SINGLE_BACK.equals(procBaseInfo.getIsCheckSingleBack()))
        {
            //销障工单
            if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBaseInfo.getProcType())){
                statusMap.put("status",ProcBaseConstants.PIC_STATUS_CLEAR_UNFINISHED);
                //巡检
            } else {
                statusMap.put("status",ProcBaseConstants.PIC_STATUS_INSPECTION_UNFINISHED);
            }
            //历史
        } else if (ProcBaseConstants.PROC_STATUS_COMPLETED.equals(procBaseInfo.getStatus())){
            if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBaseInfo.getProcType())){
                statusMap.put("status",ProcBaseConstants.PIC_STATUS_CLEAR_HIS);
            } else {
                statusMap.put("status",ProcBaseConstants.PIC_STATUS_INSPECTION_HIS);
            }
            //历史
        } else if ((ProcBaseConstants.PROC_STATUS_SINGLE_BACK.equals(procBaseInfo.getStatus()) &&
                ProcBaseConstants.IS_CHECK_SINGLE_BACK.equals(procBaseInfo.getIsCheckSingleBack()))){
            if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBaseInfo.getProcType())){
                statusMap.put("status",ProcBaseConstants.PIC_STATUS_CLEAR_HIS);
            } else {
                statusMap.put("status",ProcBaseConstants.PIC_STATUS_INSPECTION_HIS);
            }
        }
        return ResultUtils.success(statusMap);
    }

    /**
     * 根据id查看工单
     *
     * @param id 工单id
     * @return Result
     */
    @Override
    public Result queryProcessById(String id) {
        ProcessDetail processDetail = new ProcessDetail();

        //缺少工单id
        if (StringUtils.isEmpty(id)){
            return ResultUtils.warn(ProcBaseResultCode.PROC_ID_LOSE, I18nUtils.getSystemString(ProcBaseI18n.PROC_ID_LOSE));
        }
        //获取工单基础信息
        ProcBaseResp procBaseResp = procBaseDao.queryProcessByProcId(id);
        //工单不存在
        if (ObjectUtils.isEmpty(procBaseResp)){
            return ResultUtils.warn(ProcBaseResultCode.PROC_NOT_EXIST, I18nUtils.getSystemString(ProcBaseI18n.PROC_IS_NOT_EXIST));
        }
        //构造页面返回值
        processDetail = this.convertProcInfoToProcDetail(procBaseResp,processDetail);

        return ResultUtils.success(ProcBaseResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.QUERY_PROC_SUCCESS), processDetail);
    }

    /**
     * 查询巡检任务关联巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 17:11
     * @param queryCondition 查询条件
     * @return 返回巡检任务关联巡检工单信息
     */
    @Override
    public Result queryListRelatedProcByInspectionTaskIdPage(QueryCondition<ProcBaseReq> queryCondition) {
        //设置分页beginNum
        Integer beginNum = (queryCondition.getPageCondition().getPageNum() - 1) * queryCondition.getPageCondition().getPageSize();
        queryCondition.getPageCondition().setBeginNum(beginNum);
        // 构造分页条件
        Page page = myBatiesBuildPage(queryCondition);
        // 查询数据总数
        Integer count = procInspectionService.queryCountListRelatedProcByInspectionTaskId(queryCondition);
        // 查询主表数据
        List<ProcBaseResp> procBaseRespList = procInspectionService.queryListRelatedProcByInspectionTaskIdPage(queryCondition);
        // 构造页面需要数据
        List<ProcBaseResp> resultList = this.convertProcInfoToProcResp(procBaseRespList);
        // 构造返回结果
        PageBean pageBean = myBatiesBuildPageBean(page, count, resultList);
        // 返回数据
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * 分页查询工单未完工列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @Override
    public Result queryListProcUnfinishedProcByPage(QueryCondition<ProcBaseReq> queryCondition) {
        //设置分页beginNum
        Integer beginNum = (queryCondition.getPageCondition().getPageNum() - 1) * queryCondition.getPageCondition().getPageSize();
        queryCondition.getPageCondition().setBeginNum(beginNum);
        // 构造分页条件
        Page page = myBatiesBuildPage(queryCondition);

        //获取权限信息
        this.getPermissionsInfo(queryCondition);

        Integer count = 0;

        List<ProcBaseResp> procBaseRespList = new ArrayList<>();
        queryCondition = ProcBaseUtil.addLastDaysToCondition(queryCondition);
        if (ProcBaseConstants.PROC_INSPECTION.equals(queryCondition.getBizCondition().getProcType())) {
            //巡检工单
            // 查询数据总数
            count = procInspectionService.queryCountListProcInspectionUnfinishedProc(queryCondition);

            // 查询主表数据
            procBaseRespList = procInspectionService.queryListProcInspectionUnfinishedProcByPage(queryCondition);
        } else {
            //销障工单
            //查询数据总数
            count = procClearFailureService.queryCountListProcClearUnfinishedProc(queryCondition);

            //查询主表数据
            procBaseRespList = procClearFailureService.queryListProcClearUnfinishedProcByPage(queryCondition);
        }

        // 构造页面需要数据
        List<ProcBaseResp> resultList = this.convertProcInfoToProcResp(procBaseRespList);

        // 构造返回结果
        PageBean pageBean = myBatiesBuildPageBean(page, count, resultList);
        // 返回数据
        return ResultUtils.pageSuccess(pageBean);
    }


    /**
     * 分页查询工单历史列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @Override
    public Result queryListProcHisProcByPage(QueryCondition<ProcBaseReq> queryCondition) {
        //设置分页beginNum
        Integer beginNum = (queryCondition.getPageCondition().getPageNum() - 1) * queryCondition.getPageCondition().getPageSize();
        queryCondition.getPageCondition().setBeginNum(beginNum);
        // 构造分页条件
        Page page = myBatiesBuildPage(queryCondition);

        //获取权限信息
        this.getPermissionsInfo(queryCondition);
        Integer count = 0;
        List<ProcBaseResp> procBaseRespList = new ArrayList<>();
        queryCondition = ProcBaseUtil.addLastDaysToCondition(queryCondition);
        if (ProcBaseConstants.PROC_INSPECTION.equals(queryCondition.getBizCondition().getProcType())) {
            //巡检工单
            // 查询数据总数
            count = procInspectionService.queryCountListProcInspectionHisProc(queryCondition);

            // 查询主表数据
            procBaseRespList = procInspectionService.queryListProcInspectionHisProcByPage(queryCondition);
        } else {
            //销障工单
            //查询数据总数
            count = procClearFailureService.queryCountListProcClearHisProc(queryCondition);

            //查询主表数据
            procBaseRespList = procClearFailureService.queryListProcClearHisProcByPage(queryCondition);
        }

        // 构造页面需要数据
        List<ProcBaseResp> resultList = this.convertProcInfoToProcResp(procBaseRespList);
        // 构造返回结果
        PageBean pageBean = myBatiesBuildPageBean(page, count, resultList);
        // 返回数据
        return ResultUtils.pageSuccess(pageBean);
    }


    /**
     * 工单未完工列表状态总数统计
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @Override
    public Result queryCountListProcUnfinishedProcStatus(QueryCondition<ProcBaseReq> queryCondition){

        //获取权限信息
        this.getPermissionsInfo(queryCondition);

        // 查询数据总数
        Integer count = procBaseDao.queryCountListProcUnfinishedProc(queryCondition);
        return ResultUtils.success(ProcBaseResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.QUERY_PROC_STATUS_COUNT_SUCCESS),count);
    }

    /**
     * 工单列表状态统计
     *
     * @param procBaseReq 工单请求类
     * @return Result
     */
    @Override
    public Result queryCountProcByStatus(ProcBaseReq procBaseReq) {

        //获取状态数
        List<String> statusList = new ArrayList<>();

        //获取权限信息
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        queryCondition.setBizCondition(procBaseReq);
        procBaseReq = (ProcBaseReq)this.getPermissionsInfo(queryCondition).getBizCondition();
        Set<String> permissionProcIds = procBaseDao.queryPermissionsInfo(procBaseReq.getPermissionDeviceTypes(),procBaseReq.getPermissionAreaIds(),procBaseReq.getPermissionDeptIds());
        //当用户有工单数据权限
        if (!ObjectUtils.isEmpty(permissionProcIds)){
            procBaseReq.setPermissionProcIds(permissionProcIds);
            statusList = procBaseDao.queryCountProcByStatus(procBaseReq);
        }
        return ResultUtils.success(ProcBaseResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.QUERY_PROC_STATUS_COUNT_SUCCESS),statusList.size());
    }

    /**
     * 工单列表分组统计
     *
     * @param procBaseReq 工单请求类
     * @return Result
     */
    @Override
    public Result queryCountProcByGroup(ProcBaseReq procBaseReq){

        //获取总数
        List<Map<String,Object>> maps = new ArrayList<>();

//        获取权限信息
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        queryCondition.setBizCondition(procBaseReq);
        procBaseReq = (ProcBaseReq)this.getPermissionsInfo(queryCondition).getBizCondition();
        Set<String> permissionProcIds = procBaseDao.queryPermissionsInfo(procBaseReq.getPermissionDeviceTypes(),procBaseReq.getPermissionAreaIds(),procBaseReq.getPermissionDeptIds());
        //当用户有工单数据权限
        if (!ObjectUtils.isEmpty(permissionProcIds)){
            procBaseReq.setPermissionProcIds(permissionProcIds);
            maps = procBaseDao.queryCountProcByGroup(procBaseReq);
        }
        return ResultUtils.success(ProcBaseResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.QUERY_PROC_STATUS_COUNT_SUCCESS),maps);
    }

    /**
     * 设施类型统计的工单信息
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @Override
    public Result queryListGroupByDeviceType(QueryCondition<ProcBaseReq> queryCondition){

        //获取权限信息
        this.getPermissionsInfo(queryCondition);

        //获取类型统计
        List<Map<String,Object>> maps = procBaseDao.queryListGroupByDeviceType(queryCondition.getBizCondition());
        return ResultUtils.success(ProcBaseResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.QUERY_PROC_STATUS_COUNT_SUCCESS),maps);
    }

    /**
     * 工单历史列表总数统计
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @Override
    public Result queryCountListProcHisProc(QueryCondition<ProcBaseReq> queryCondition){

        //获取权限信息
        this.getPermissionsInfo(queryCondition);

        // 查询数据总数
        Integer count = procBaseDao.queryCountListProcHisProc(queryCondition);
        return ResultUtils.success(ProcBaseResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.QUERY_PROC_STATUS_COUNT_SUCCESS),count);
    }

    /**
     * 查询ids是否有工单信息
     *
     * @since 2019-03-06
     * @param ids id列表
     * @param type 设施或区域
     * @return List<String>
     */
    @Override
    public Result queryProcExitsForIds(List<String> ids,String type) {
        List<String> resultIds = new ArrayList<>();
        ProcBaseReq procBaseReq = new ProcBaseReq();
        //获取所有id列表信息查询
        Set<String> idSet = new HashSet<>();
        for (String id : ids){
            idSet.add(id);
        }
        if (ProcBaseConstants.DEVICE_IDS.equals(type)){
            procBaseReq.setDeviceIds(idSet);
        } else if (ProcBaseConstants.AREA_IDS.equals(type)){
            procBaseReq.setDeviceAreaIds(idSet);
        }
        //获取所有关系表信息并转成map
        List<ProcRelatedDevice> procRelatedDevices = procBaseDao.queryProcRelateDevice(procBaseReq);
        //查询销障工单信息
        List<ProcClearFailure> procClearFailureList = procClearFailureService.queryClearFailureProcRelateDevice(procBaseReq);
        //获取关联设施map
        Map<String,Object> procRelatedDeviceMaps = CastMapUtil.getProcRelatedDeviceMapList(type, procRelatedDevices);
        //获取销障工单关联设施map
        Map<String, ProcClearFailure> procClearDeviceMaps = CastMapUtil.getProcClearRelatedDeviceMapList(type, procClearFailureList);
        //对比id是否有关联工单id
        for (int i = 0;i < ids.size();i++){
            ProcRelatedDevice procRelatedDevice = (ProcRelatedDevice)procRelatedDeviceMaps.get(ids.get(i));
            if (!ObjectUtils.isEmpty(procRelatedDevice) && !StringUtils.isEmpty(procRelatedDevice.getProcRelatedDeviceId())){
                resultIds.add(ids.get(i));
            }
        }

        for (int i = 0;i < ids.size(); i++) {
            ProcClearFailure procClearFailure = procClearDeviceMaps.get(ids.get(i));
            if (!ObjectUtils.isEmpty(procClearFailure) && !StringUtils.isEmpty(procClearFailure.getProcId())) {
                resultIds.add(ids.get(i));
            }
        }

        if (!ObjectUtils.isEmpty(resultIds)) {
            //去除list中重复的数据
            Set<String> resultIdSet = new HashSet<>();
            resultIdSet.addAll(resultIds);
            resultIds = new ArrayList<>();
            resultIds.addAll(resultIdSet);
        }

        return ResultUtils.success(ProcBaseResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.QUERY_PROC_SUCCESS), resultIds);
    }

    /**
     * 下载工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/21 21:36
     * @param req 下载工单信息
     * @return 工单结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateProcByUser(UpdateProcByUserReq req) {
        String procId = req.getProcId();
        Result procIdCheckResult = checkProcId(procId);
        if (null != procIdCheckResult) {
            return procIdCheckResult;
        }

        if (StringUtils.isEmpty(req.getUserId())) {
            //提示参数异常
            return WorkFlowBusinessMsg.paramErrorMsg();
        }

        //查询流程信息
        ProcBase procBase = this.getProcBaseByProcId(procId);
        if (!ProcBaseConstants.PROC_STATUS_PENDING.equals(procBase.getStatus())) {
            throw new DownLoadProcErrorException();
        }

        //当前时间
        Date nowDate = new Date();

        //需要修改工单的内容
        ProcBase paramInfo = this.getUpdateProcBase(nowDate);
        //工单编号
        paramInfo.setProcId(procId);
        //下载用户
        paramInfo.setAssign(req.getUserId());
        //单位类型为人
        paramInfo.setDeptType(WorkFlowBusinessConstants.DEPT_TYPE_PERSON);
        //修改工单信息
        procBaseDao.updateById(paramInfo);

        //完结流程的参数
        CompleteProcessInfoReq completeReq = this.getCompleteProcessInfoReq(procBase);

        //流程操作办结
        completeReq.setOperation(ProcessConstants.PROC_OPERATION_COMPLETE);
        //下载用户
        completeReq.setAssign(req.getUserId());
        //下载工单
        Result result = workflowService.completeProcess(completeReq);

        boolean processResult = this.checkProcessInfo(result);
        if (!processResult) {
            throw new DownLoadProcErrorException();
        }

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.DOWNLOAD_PROC_SUCCESS));
    }

    /**
     * 指派工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 13:56
     * @param req 指派工单参数
     * @return 指派工单结果
     */
    @Override
    public Result assignProc(AssignProcReq req) {

        String format = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        log.info("begin assign proc , the procId : {}, the begin time : {}", req.getProcId(), simpleDateFormat.format(System.currentTimeMillis()));

        //更改流程的责任单位，发起流程
        Result procIdCheckResult = checkProcId(req.getProcId());
        if (null != procIdCheckResult) {
            return procIdCheckResult;
        }

        //关联部门
        if (ObjectUtils.isEmpty(req.getDepartmentList())) {
            //提示参数异常
            return WorkFlowBusinessMsg.paramErrorMsg();
        }

        //查询流程信息
        ProcBase procBase = this.getProcBaseByProcId(req.getProcId());

        //只有流程状态为待指派的状态才能指派工单
        if (!ProcBaseConstants.PROC_STATUS_ASSIGNED.equals(procBase.getStatus())) {
            throw new AssignProcErrorException();
        }

        //指派到工单的具体的单位
        ProcessInfo processInfo = proxySelf.assignProcProcess(req, procBase);

        //启动流程
        StartProcessInfoReq startProcessInfoReq = this.getStartProcessInfoReq(processInfo);
        Result result = workflowService.startProcess(startProcessInfoReq);
        boolean processResult = this.checkProcessInfo(result);
        //指派异常
        if (!processResult) {
            throw new AssignProcErrorException();
        }

        //新增工单指派日志信息
        String addOptType = LogConstants.DATA_OPT_TYPE_UPDATE;
        String functionCode = "";
        if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBase.getProcType())) {
            //销障工单指派日志
            functionCode = LogFunctionCodeConstant.ASSIGN_CLEAR_FAILURE_PROC_FUNCTION_CODE;
        } else if (ProcBaseConstants.PROC_INSPECTION.equals(procBase.getProcType())) {
            //巡检工单指派日志
            functionCode = LogFunctionCodeConstant.ASSIGN_PROC_INSPECTION_FUNCTION_CODE;
        }
        AddLogBean addLogBean = procLogService.getAddProcOperateLogParam(procBase, functionCode, addOptType);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);

        log.info("end assign proc , the procId : {}, the end time : {}", req.getProcId(), simpleDateFormat.format(System.currentTimeMillis()));

        //指派成功
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.PROC_ASSIGN_SUCCESS));
    }

    /**
     * 工单指派修改数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/15 14:32
     * @param req 指派工单的参数
     * @parma procBase 工单参数
     * @return 修改的工单数据
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProcessInfo assignProcProcess(AssignProcReq req, ProcBase procBase) {
        //添加关联部门信息
        ProcessInfo processInfo = new ProcessInfo();
        if (!ObjectUtils.isEmpty(procBase)) {
            //当前时间
            Date nowDate = new Date();

            ProcBase procBaseParam = this.getUpdateProcBase(nowDate);
            procBaseParam.setProcId(req.getProcId());

            //单位类型为部门
            procBaseParam.setDeptType(WorkFlowBusinessConstants.DEPT_TYPE_DEPT);

            if (ProcBaseConstants.PROC_INSPECTION.equals(procBase.getProcType())) {
                //巡检工单
                req.setDepartmentList(this.getAssignDepartmentInfo(req.getDepartmentList(), req.getProcId()));
                //工单关联部门信息
                procBaseParam = this.setAccountabilityDeptInfo(procBaseParam, req.getDepartmentList());
                //修改巡检工单
                ProcInspection updateProcInspectionParam = new ProcInspection();
                BeanUtils.copyProperties(procBaseParam, updateProcInspectionParam);
                procInspectionService.updateProcInspectionInfo(updateProcInspectionParam);

                processInfo.setProcBase(procBaseParam);
                processInfo.setProcRelatedDepartments(req.getDepartmentList());
            } else {
                //销障工单 (单个责任单位信息)
                req.setDepartmentList(this.getAssignDepartmentInfo(req.getDepartmentList(), req.getProcId()));
                //工单关联部门信息
                procBaseParam = this.setAccountabilityDeptInfo(procBaseParam, req.getDepartmentList());
                //修改销障工单信息
                ProcClearFailure updateProcClearFailure = new ProcClearFailure();
                BeanUtils.copyProperties(procBaseParam, updateProcClearFailure);
                procClearFailureService.updateClearFailure(updateProcClearFailure);

                processInfo.setProcBase(procBaseParam);
                processInfo.setProcRelatedDepartments(req.getDepartmentList());
            }
        }

        return processInfo;
    }

    /**
     * 设置关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/16 15:02
     * @param procBaseParam 工单参数
     * @param departmentList 部门集合
     * @return 设置关联部门信息
     */
    public ProcBase setAccountabilityDeptInfo(ProcBase procBaseParam, List<ProcRelatedDepartment> departmentList) {
        String departmentId = "";
        String departmentName = "";

        if (!ObjectUtils.isEmpty(departmentList)) {
            departmentId = departmentList.get(0).getAccountabilityDept();
            List<String> departmentParam = new ArrayList<>();
            departmentParam.add(departmentId);
            departmentName = procRelatedService.getDepartmentName(departmentParam);
        }
        procBaseParam.setAccountabilityDept(departmentId);
        procBaseParam.setAccountabilityDeptName(departmentName);
        return procBaseParam;
    }

    /**
     * 新增告警id及工单id
     *
     * @since 2019-04-18
     * @param procRelatedAlarm 工单关联告警
     *
     * @return Integer 处理结果
     *
     */
    @Override
    public Integer addProcRelatedAlarm(ProcRelatedAlarm procRelatedAlarm) {
        //参数不能为空
        if (StringUtils.isEmpty(procRelatedAlarm.getProcId()) || StringUtils.isEmpty(procRelatedAlarm.getRefAlarmId()) || StringUtils.isEmpty(procRelatedAlarm.getRefAlarmCode())){
            throw new AddProcRelatedAlarmException();
        }
        //统一设置uuid
        procRelatedAlarm.setProcRelatedAlarmId(NineteenUUIDUtils.uuid());
        return procBaseDao.addProcRelatedAlarm(procRelatedAlarm);
    }

    /**
     * 当前登录用户下载工单
     *
     * @param procBaseReq 工单请求
     *
     * @return Result
     */
    @Override
    public Result downloadProcByLoginUserForApp(ProcBaseReq procBaseReq) {
        String format = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        log.info("begin login user download proc, the beginTime : {}" , simpleDateFormat.format(System.currentTimeMillis()));

        String procType;
        //判断是否存在工单类型
        if (!ObjectUtils.isEmpty(procBaseReq)) {
            if (!ObjectUtils.isEmpty(procBaseReq.getProcType())) {
                //工单状态
                procType = procBaseReq.getProcType();
            } else {
                return WorkFlowBusinessMsg.paramErrorMsg();
            }
        } else {
            return WorkFlowBusinessMsg.paramErrorMsg();
        }

        //查询需要下载的工单信息
        List<ProcBaseRespForApp> procBaseRespForApps = this.searchLoginUserDownloadInfoForProcType(procType);
        if (ObjectUtils.isEmpty(procBaseRespForApps)) {
            String notProcCanDownloadMsg = I18nUtils.getSystemString(ProcBaseI18n.NOT_PROC_CAN_DOWNLOAD);
            //没有需要下载的工单信息
            return ResultUtils.warn(ProcBaseResultCode.NOT_PROC_CAN_DOWNLOAD, notProcCanDownloadMsg);
        }
        procBaseRespForApps = this.getDownloadInfoList(procBaseRespForApps, procType, procBaseReq);
        //拆分数据
        List<ProcBaseRespForApp> procClearFailureForApps = new ArrayList<>();
        List<ProcBaseRespForApp> procInspectionForApps = new ArrayList<>();

        //巡检工单idList
        List<String> procInspectionIdList = new ArrayList<>();
        for (ProcBaseRespForApp procBaseRespForApp : procBaseRespForApps){
            if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBaseRespForApp.getProcType())){
                procClearFailureForApps.add(procBaseRespForApp);
            } else {
                procInspectionForApps.add(procBaseRespForApp);
                procInspectionIdList.add(procBaseRespForApp.getProcId());
            }
            //下载人
            procBaseRespForApp.setAssign(this.getUserId());
        }
        //是否删除
        String isDeleted = WorkFlowBusinessConstants.IS_NOT_DELETED;
        if (!ObjectUtils.isEmpty(procInspectionIdList)) {
            //查询关联巡检记录信息
            List<ProcInspectionRecord> inspectionRecordList = procInspectionRecordService.queryInspectionRecord(isDeleted, procInspectionIdList);
            //将没有巡检的设施添加到返回的工单列表中
            procBaseRespForApps = CastListUtil.notInspectionDeviceToProcBaseResp(procBaseRespForApps, inspectionRecordList);
        }
        //下载工单信息
        List<Object> procBaseRespForAppsResult = this.downloadProcessSearch(procClearFailureForApps, procInspectionForApps, procBaseRespForApps);
        log.info("end login user download proc the endTime : {}" , simpleDateFormat.format(System.currentTimeMillis()));
        return ResultUtils.success(procBaseRespForAppsResult);
    }

    /**
     * 获取指派部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/4 14:38
     * @param departmentList 部门列表
     * @return 指派部门信息
     */
    public List<ProcRelatedDepartment> getAssignDepartmentInfo(List<ProcRelatedDepartment> departmentList, String procId) {
        if (!ObjectUtils.isEmpty(departmentList)) {
            for (ProcRelatedDepartment procRelatedDepartment : departmentList) {
                procRelatedDepartment.setProcRelatedDeptId(NineteenUUIDUtils.uuid());
                procRelatedDepartment.setProcId(procId);
                procRelatedDepartment.setCreateUser(this.getUserId());
                procRelatedDepartment.setCreateTime(new Date());
                procRelatedDepartment.setIsDeleted(WorkFlowBusinessConstants.IS_NOT_DELETED);
            }
        }
        return departmentList;
    }

    /**
     * 获得修改的流程实体
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 14:17
     * @param nowDate 当前时间
     * @return 流程实体
     */
    public ProcBase getUpdateProcBase(Date nowDate) {
        ProcBase procBaseParam = new ProcBase();
        procBaseParam.setUpdateUser(this.getUserId());
        procBaseParam.setUpdateTime(nowDate);
        return procBaseParam;
    }

    /**
     * 转派工单结果
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 13:57
     * @param req 转派工单参数
     * @return 转派工单结果
     */
    @Override
    public Result turnProc(TurnProcReq req) {
        String format = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        log.info("begin turn proc , the procId : {}, the begin time : {}", req.getProcId(), simpleDateFormat.format(System.currentTimeMillis()));
        //校验工单编号
        Result procIdCheckResult = checkProcId(req.getProcId());
        if (null != procIdCheckResult) {
            return procIdCheckResult;
        }

        //转派原因必填
        if (!TurnProcReq.checkTurnReason(req)) {
            return WorkFlowBusinessMsg.paramErrorMsg();
        }

        //查询流程信息
        ProcBase procBase = this.getProcBaseByProcId(req.getProcId());

        //只有流程状态为处理中的状态才能转派工单
        if (!ProcBaseConstants.PROC_STATUS_PROCESSING.equals(procBase.getStatus())) {
            throw new TurnProcErrorException();
        }

        //校验转派人
        Result checkUserResult = this.checkTurnUser(req);
        if (null != checkUserResult) {
            return checkUserResult;
        }

        //修改数据库记录
        proxySelf.turnProcProcess(req, procBase);

        //转派人
        TurnInfoReq turnInfoReq = new TurnInfoReq();
        procBase.setAssign(req.getUserId());
        BeanUtils.copyProperties(procBase, turnInfoReq);

        //调用转派工单的流程
        Result result = workflowService.turnProcess(turnInfoReq);
        boolean processResult = this.checkProcessInfo(result);
        if (!processResult) {
            throw new TurnProcErrorException();
        }

        //转派
        String functionCode = "";
        if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBase.getProcType())) {
            //销障工单转派日志
            functionCode = LogFunctionCodeConstant.TURN_CLEAR_FAILURE_FUNCTION_CODE;
        } else if (ProcBaseConstants.PROC_INSPECTION.equals(procBase.getProcType())) {
            //巡检工单转派日志
            functionCode = LogFunctionCodeConstant.TURN_INSPECTION_FUNCTION_CODE;
        }
        //数据操作类型
        String dataType = LogConstants.DATA_OPT_TYPE_UPDATE;
        //记录app操作日志
        AddLogBean addLogBean = procLogService.getAddProcOperateLogParamForApp(procBase, functionCode, dataType);
        //记录操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        log.info("end turn proc , the procId : {}, the end time : {}", req.getProcId(), simpleDateFormat.format(System.currentTimeMillis()));
        //转派成功
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.PROC_TURN_SUCCESS));
    }

    /**
     * 转办工单修改数据库信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/15 14:11
     * @param req 转办工单
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void turnProcProcess(TurnProcReq req, ProcBase procBase) {

        //将转派人更新到主表中，并且更新转派原因
        ProcBase updateBase = this.getUpdateProcBase(new Date());
        updateBase.setProcId(req.getProcId());
        updateBase.setAssign(req.getUserId());
        updateBase.setTurnReason(req.getTurnReason());

        if (!ObjectUtils.isEmpty(procBase)) {
            //修改工单参数
            this.updateProcBase(procBase, updateBase);
        }
    }

    /**
     * 获取可转派用户
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 21:30
     * @param req 转派用户请求参数
     * @return 转派用户信息
     */
    @Override
    public Result getTurnUser(GetTurnUserReq req) {
        //工单编号
        String procId = req.getProcId();
        Result checkProcIdResult = this.checkProcId(procId);
        if (null != checkProcIdResult) {
            return checkProcIdResult;
        }
        //获取可转派用户信息
        List<User> turnUserList = this.getTurnUserProcess(procId);

        //返回可转派用户信息
        List<TurnUserListVo> turnUserListVoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(turnUserList)) {
            TurnUserListVo turnUser = null;
            for (User userOne : turnUserList) {
                turnUser = new TurnUserListVo();
                BeanUtils.copyProperties(userOne, turnUser);
                turnUserListVoList.add(turnUser);
            }
        }

        return ResultUtils.success(turnUserListVoList);
    }

    /**
     * 获取可转派用户过程
     * @author hedongwei@wistronits.com
     * @date  2019/4/10 11:36
     * @param procId 工单编号
     * @return 可转派用户
     */
    @Override
    public List<User> getTurnUserProcess(String procId) {
        System.out.println("进入到获取推送人员方法:>>>>>>>>>>>>>>>");

        //查询工单信息
        ProcBase procBase = this.getProcBaseByProcId(procId);

        //查询关联设施类型
        List<String> deviceTypeList = this.getTurnRelatedDeviceTypeList(procBase);

        //关联部门编号集合
        List<String> departmentList = this.getTurnRelatedDepartmentList(procBase);

        //根据部门编号查询用户信息
        Object userObject = userFeign.queryUserByDeptList(departmentList);
        List<User> userList = JSONArray.parseArray(JSONArray.toJSONString(userObject), User.class);


        //删除的工单信息
        List<User> deleteUserList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(userList)) {
            for (User userOne : userList) {
                if (WorkFlowBusinessConstants.ADMIN_USER_ID.equals(userOne.getId())) {
                    deleteUserList.add(userOne);
                }
            }

            if (!ObjectUtils.isEmpty(deleteUserList)) {
                userList.removeAll(deleteUserList);
            }

            deleteUserList = new ArrayList<>();
            //获取当前责任人
            String assign = procBase.getAssign();
            if (!ObjectUtils.isEmpty(userList) && !ObjectUtils.isEmpty(assign)) {
                for (User userOne : userList) {
                    //需要排除责任人在转派用户列表中
                    if (assign.equals(userOne.getId())) {
                        deleteUserList.add(userOne);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(deleteUserList)) {
                userList.removeAll(deleteUserList);
            }
        }

        //获取可转派用户集合
        List<User> turnUserList = this.getTurnUserList(userList, deviceTypeList, departmentList);

        //admin不是转派人可以新增admin用户为转办人
        if (!WorkFlowBusinessConstants.ADMIN_USER_ID.equals(procBase.getAssign())) {
            List<String> userIdList = new ArrayList<>();
            userIdList.add(WorkFlowBusinessConstants.ADMIN_USER_ID);
            Object adminObject = userFeign.queryUserByIdList(userIdList);
            List<User> adminList = JSONArray.parseArray(JSONArray.toJSONString(adminObject), User.class);
            if (!ObjectUtils.isEmpty(adminList)) {
                turnUserList.addAll(adminList);
            }
        }
        return turnUserList;
    }

    /**
     * 获取可转派用户
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 22:40
     * @param userList 用户集合
     * @param deviceTypeList 设施类型集合
     * @param departmentList 部门集合
     * @return 返回可转派用户
     */
    public List<User> getTurnUserList(List<User> userList, List<String> deviceTypeList, List<String> departmentList) {
        List<User> turnUserList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(userList)) {
            for (User userOne : userList) {
                if (!ObjectUtils.isEmpty(userOne.getRole())) {
                    if (!ObjectUtils.isEmpty(userOne.getRole().getRoleDevicetypeList())) {
                        turnUserList = this.getTurnUserInfo(userOne, deviceTypeList, turnUserList);
                    }
                }
            }
        }
        return turnUserList;
    }

    /**
     * 获取可转派用户
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 22:39
     * @param userOne 单个用户信息
     * @param deviceTypeList 设施类型数组
     * @param turnUserList 转派用户数组
     * @return 获取可转派用户
     */
    public List<User> getTurnUserInfo(User userOne, List<String> deviceTypeList, List<User> turnUserList) {
        int i = 0;
        //获得设施类型编号
        List<RoleDeviceType> deviceTypeRoles = userOne.getRole().getRoleDevicetypeList();
        for (RoleDeviceType deviceTypeRole : deviceTypeRoles) {
            if (deviceTypeList.contains(deviceTypeRole.getDeviceTypeId())) {
                i ++;
            }
        }
        //相等时用户才可以被转办
        if (i == deviceTypeList.size()) {
            turnUserList.add(userOne);
        }
        return turnUserList;
    }

    /**
     * 获取转办关联设施类型集合
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 22:05
     * @param procBase 工单信息
     * @return 设施类型集合
     */
    public List<String> getTurnRelatedDeviceTypeList(ProcBase procBase) {
        //设施类型code
        List<String> deviceTypeList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(procBase)) {
            deviceTypeList.add(procBase.getDeviceType());
        }
        return deviceTypeList;
    }

    /**
     * 获取转办关联部门编号集合
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 22:05
     * @param procBase 流程
     * @return 部门编号集合
     */
    public List<String> getTurnRelatedDepartmentList(ProcBase procBase) {
        List<String> departmentList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(procBase)) {
            //关联部门
            departmentList.add(procBase.getAccountabilityDept());
        }
        return departmentList;
    }

    /**
     * 校验转派流程用户
     * @author hedongwei@wistronits.com
     * @date  2019/3/29 18:14
     * @param req 转派流程参数
     * @return 转派流程用户
     */
    public Result checkTurnUser(TurnProcReq req) {

        //用户编号
        String userId = req.getUserId();

        //流程编号
        String procId = req.getProcId();

        //转派工单参数
        if (ObjectUtils.isEmpty(userId)) {
            //提示参数异常
            return WorkFlowBusinessMsg.paramErrorMsg();
        }

        //转派人是不是自己
        ProcBase procBaseInfo = this.getProcBaseByProcId(procId);
        if (req.getUserId().equals(procBaseInfo.getAssign())) {
            //提示不能指派给自己
            return ResultUtils.warn(ProcBaseResultCode.PROC_ASSIGN_MYSELF, I18nUtils.getSystemString(ProcBaseI18n.PROC_ASSIGN_MYSELF));
        }

        //验证工单是否能够被这个人转办
        Result checkAssignUserResult = this.checkAssignUserInfo(procId, userId);
        if (null != checkAssignUserResult) {
            return checkAssignUserResult;
        }

        return null;
    }

    /**
     * 指派用户是否有权限被指派
     * @author hedongwei@wistronits.com
     * @date  2019/4/10 12:31
     * @param procId 流程编号
     * @param userId 用户编号
     * @return 校验指派用户信息
     */
    public Result checkAssignUserInfo(String procId, String userId) {
        //能够被转办的用户
        List<User> isAbleList = this.getTurnUserProcess(procId);

        boolean isCheckUser = false;
        if (!ObjectUtils.isEmpty(isAbleList)) {
            for (User userOne : isAbleList) {
                if (userId.equals(userOne.getId())) {
                    isCheckUser = true;
                    break;
                }
            }
        }

        if (!isCheckUser) {
            //提示转办用户没有权限
            return ResultUtils.warn(ProcBaseResultCode.ASSIGN_USER_NOT_HAVE_PERMISSION, I18nUtils.getSystemString(ProcBaseI18n.ASSIGN_USER_NOT_HAVE_PERMISSION));
        }
        return null;
    }


    /**
     * 判断接口调用是否成功
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 13:24
     * @param result 接口校验的结果
     * @return 返回接口调用是否成功
     */
    public boolean checkProcessInfo(Result result) {
        if (!ObjectUtils.isEmpty(result)) {
            if (!ResultCode.SUCCESS.equals(result.getCode())) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }


    /**
     * 检验工单编号是否为空，工单是否存在
     * @author hedongwei@wistronits.com
     * @date  2019/3/22 16:22
     * @param procId 流程编号
     */
    @Override
    public Result checkProcId(String procId) {
        //流程编号
        if (StringUtils.isEmpty(procId)) {
            //工单编号不能为空
            return ResultUtils.warn(ProcBaseResultCode.PROC_ID_LOSE,I18nUtils.getSystemString(ProcBaseI18n.PROC_ID_LOSE));
        }

        //查询工单信息
        ProcBase procBase = this.getProcBaseByProcId(procId);

        if (ObjectUtils.isEmpty(procBase) || StringUtils.isEmpty(procBase.getProcId())){
            return ResultUtils.warn(ProcBaseResultCode.PROC_NOT_EXIST,I18nUtils.getSystemString(ProcBaseI18n.PROC_IS_NOT_EXIST));
        }
        return null;
    }

    /**
     * 根据编号查询工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/3 21:13
     * @param procId 流程编号
     * @return 返回工单信息
     */
    @Override
    public ProcBase getProcBaseByProcId(String procId) {
        ProcBase procBase = procBaseDao.queryProcessByProcId(procId);
        return procBase;
    }

    /**
     * 完结流程的参数
     * @author hedongwei@wistronits.com
     * @date  2019/3/22 16:15
     * @param procBase 流程参数
     */
    @Override
    public CompleteProcessInfoReq getCompleteProcessInfoReq(ProcBase procBase) {
        //完结流程的参数
        CompleteProcessInfoReq completeReq = new CompleteProcessInfoReq();
        BeanUtils.copyProperties(procBase, completeReq);
        //部门编号(太长暂定使用工单编号)
        completeReq.setDepartmentIds(procBase.getProcId());
        return completeReq;
    }


    /**
     * 查询工单详情
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 9:50
     * @param procId 查询工单详情
     * @return procId 工单编号
     */
    @Override
    public ProcBase queryProcBaseById(String procId) {
        return procBaseDao.queryProcessByProcId(procId);
    }

    /**
     * 退单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/22 15:59
     * @param req 退单参数
     * @return 退单结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result singleBackProc(SingleBackProcReq req) {
        String procId = req.getProcId();
        String format = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        log.info("begin singleBack proc , the procId : {}, the begin time : {}", req.getProcId(), simpleDateFormat.format(System.currentTimeMillis()));
        Result procIdCheckResult = checkProcId(procId);
        if (null != procIdCheckResult) {
            return procIdCheckResult;
        }

        //是否参数错误
        if (!SingleBackProcReq.checkSingleBackReason(req)) {
            return WorkFlowBusinessMsg.paramErrorMsg();
        }

        ProcBase procBase = this.getProcBaseByProcId(procId);
        //只有流程状态为处理中的状态才能退单
        if (!ProcBaseConstants.PROC_STATUS_PROCESSING.equals(procBase.getStatus())) {
            throw new SingleBackProcErrorException();
        }

        ProcBase updateProcParam = new ProcBase();
        Date nowDate = new Date();
        BeanUtils.copyProperties(req, updateProcParam);
        updateProcParam.setUpdateTime(nowDate);
        updateProcParam.setUpdateUser(this.getUserId());
        updateProcParam.setRealityCompletedTime(nowDate);
        //修改退单信息
        this.updateProcBase(procBase, updateProcParam);

        //调用退单的接口
        SingleBackInfoReq singleBackReq = new SingleBackInfoReq();
        BeanUtils.copyProperties(procBase, singleBackReq);
        Result result = workflowService.singleBackProcess(singleBackReq);
        boolean processResult = this.checkProcessInfo(result);
        if (!processResult) {
            throw new SingleBackProcErrorException();
        }

        if (ProcBaseConstants.PROC_INSPECTION.equals(procBase.getProcType())) {
            //获取巡检工单详情
            //是否是巡检任务生成,是巡检任务生成的需要查询巡检任务的工单是否全部完成
            ProcInspection procInspectionOne = procInspectionService.getProcInspectionByProcId(procId);
            boolean checkInspectionTaskInfo = procInspectionService.checkInspectionTaskStatusComplete(procInspectionOne);

            //全部完成需要修改巡检任务状态为已完成
            if (checkInspectionTaskInfo) {
                if (!procInspectionService.updateInspectionTaskStatus(procInspectionOne)) {
                    throw new SingleBackProcErrorException();
                }
            }
        }

        //退单
        String functionCode = "";
        if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBase.getProcType())) {
            //销障工单退单日志
            functionCode = LogFunctionCodeConstant.SINGLE_BACK_CLEAR_FAILURE_FUNCTION_CODE;
        } else if (ProcBaseConstants.PROC_INSPECTION.equals(procBase.getProcType())) {
            //巡检工单退单日志
            functionCode = LogFunctionCodeConstant.SINGLE_BACK_INSPECTION_FUNCTION_CODE;
        }
        //数据操作类型
        String dataType = LogConstants.DATA_OPT_TYPE_UPDATE;
        //记录app操作日志
        AddLogBean addLogBean = procLogService.getAddProcOperateLogParamForApp(procBase, functionCode, dataType);
        //记录操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        log.info("end singleBack proc , the procId : {}, the end time : {}", req.getProcId(), simpleDateFormat.format(System.currentTimeMillis()));
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.SINGLE_BACK_SUCCESS));
    }

    /**
     * 修改工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/24 18:03
     * @param procBase 工单参数
     * @param updateProcParam
     */
    public void updateProcBase(ProcBase procBase, ProcBase updateProcParam) {
        if (ProcBaseConstants.PROC_INSPECTION.equals(procBase.getProcType())) {
            //巡检工单
            ProcInspection updateProcInspection = new ProcInspection();
            BeanUtils.copyProperties(updateProcParam, updateProcInspection);
            procInspectionService.updateProcInspectionInfo(updateProcInspection);
        } else {
            //销障工单
            ProcClearFailure updateProcClearFailure = new ProcClearFailure();
            BeanUtils.copyProperties(updateProcParam, updateProcClearFailure);
            procClearFailureService.updateClearFailure(updateProcClearFailure);
        }
    }

    /**
     * 撤销单据
     * @author hedongwei@wistronits.com
     * @date  2019/3/22 15:59
     * @param req 撤销单据参数
     * @return 撤销单据结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result revokeProc(RevokeProcReq req) {
        String procId = req.getProcId();
        String format = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        log.info("begin revoke proc, the procId : {} , the begin time : {}", procId ,simpleDateFormat.format(System.currentTimeMillis()));
        Result procIdCheckResult = checkProcId(procId);
        if (null != procIdCheckResult) {
            return procIdCheckResult;
        }

        ProcBase procBase = this.getProcBaseByProcId(procId);
        //只有流程状态为待处理的状态才能撤销工单
        if (!ProcBaseConstants.PROC_STATUS_PENDING.equals(procBase.getStatus())) {
            throw new RevokeProcErrorException();
        }

        //修改工单的修改人
        ProcBase procBaseParam = this.getUpdateProcBase(new Date());
        procBaseParam.setProcId(procId);
        if (ProcBaseConstants.PROC_INSPECTION.equals(procBase.getProcType())) {
            //巡检工单
            ProcInspection updateProcInspection = new ProcInspection();
            BeanUtils.copyProperties(procBaseParam, updateProcInspection);
            updateProcInspection.setAccountabilityDept("");
            updateProcInspection.setAccountabilityDeptName("");
            procInspectionService.updateProcInspectionInfo(updateProcInspection);
        } else {
            //销障工单
            ProcClearFailure updateProcClearFailure = new ProcClearFailure();
            BeanUtils.copyProperties(procBaseParam, updateProcClearFailure);
            updateProcClearFailure.setAccountabilityDept("");
            updateProcClearFailure.setAccountabilityDeptName("");
            procClearFailureService.updateClearFailure(updateProcClearFailure);
        }

        //调用撤销单据的接口
        RevokeInfoReq revokeInfoReq = new RevokeInfoReq();
        BeanUtils.copyProperties(procBase, revokeInfoReq);
        Result result = workflowService.revokeProcess(revokeInfoReq);
        boolean processResult = this.checkProcessInfo(result);
        //是否撤销工单成功
        if (!processResult) {
            throw new RevokeProcErrorException();
        }

        //新增撤销工单日志信息
        String addOptType = LogConstants.DATA_OPT_TYPE_UPDATE;
        String functionCode = "";
        if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBase.getProcType())) {
            //销障工单撤销日志
            functionCode = LogFunctionCodeConstant.REVOKE_CLEAR_FAILURE_PROC_FUNCTION_CODE;
        } else if (ProcBaseConstants.PROC_INSPECTION.equals(procBase.getProcType())) {
            //巡检工单撤销日志
            functionCode = LogFunctionCodeConstant.REVOKE_INSPECTION_PROC_FUNCTION_CODE;
        }
        AddLogBean addLogBean = procLogService.getAddProcOperateLogParam(procBase, functionCode, addOptType);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        log.info("end revoke proc, the procId : {} , the end time : {}", procId ,simpleDateFormat.format(System.currentTimeMillis()));
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.REVOKE_SUCCESS));
    }

    /**
     * 退单确认
     * @author hedongwei@wistronits.com
     * @date  2019/3/22 12:28
     * @param req 退单确认参数
     * @return 退单确认结果
     */
    @Override
    public Result checkSingleBack(CheckSingleBackReq req) {
        String procId = req.getProcId();
        Result checkProcIdResult = checkProcId(procId);
        if (null != checkProcIdResult) {
            return checkProcIdResult;
        }

        //确认退单
        ProcBase procBase = this.checkResultProcess(req);

        //新增退单确认操作日志
        String addOptType = LogConstants.DATA_OPT_TYPE_UPDATE;
        String functionCode = "";
        if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBase.getProcType())) {
            //销障工单退单确认日志
            functionCode = LogFunctionCodeConstant.CHECK_CLEAR_FAILURE_SINGLE_BACK_FUNCTION_CODE;
        } else if (ProcBaseConstants.PROC_INSPECTION.equals(procBase.getProcType())) {
            //巡检工单退单确认日志
            functionCode = LogFunctionCodeConstant.CHECK_INSPECTION_SINGLE_BACK_FUNCTION_CODE;
        }
        AddLogBean addLogBean = procLogService.getAddProcOperateLogParam(procBase, functionCode, addOptType);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);


        String successMsg = I18nUtils.getSystemString(ProcBaseI18n.CHECK_SINGLE_BACK_SUCCESS);
        //提示确认退单成功
        return ResultUtils.success(ResultCode.SUCCESS, successMsg);
    }

    /**
     * 确认退单的过程
     * @author hedongwei@wistronits.com
     * @date  2019/4/4 16:09
     * @param req 确认退单的参数
     * @return 返回确认退单的值
     */
    public ProcBase checkResultProcess(CheckSingleBackReq req) {
        ProcBase procBaseOne = this.getProcBaseByProcId(req.getProcId());
        //只有流程状态为已退单的状态才能确认退单，未确认退单
        if (!ProcBaseConstants.PROC_STATUS_SINGLE_BACK.equals(procBaseOne.getStatus())
                || !ProcBaseConstants.NOT_CHECK_SINGLE_BACK.equals(procBaseOne.getIsCheckSingleBack())) {
            throw new CheckSingleBackProcErrorException();
        }

        ProcBase procBase = this.getUpdateProcBase(new Date());
        BeanUtils.copyProperties(req, procBase);
        //确认退单属性值设置成1
        procBase.setIsCheckSingleBack(ProcBaseConstants.IS_CHECK_SINGLE_BACK);
        //确认退单操作
        this.updateProcBase(procBaseOne, procBase);
        return procBaseOne;
    }

    /**
     * 查询关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 23:27
     * @param req 关联设施参数
     * @return 返回工单关联设施信息
     */
    @Override
    public Result procRelationDeviceList(ProcRelationDeviceListReq req) {
        //工单编号
        String procId = req.getProcId();
        //查询工单信息
        ProcBase procBase = this.getProcBaseByProcId(procId);
        if (!ObjectUtils.isEmpty(procBase)) {
            if (ProcBaseConstants.PROC_INSPECTION.equals(procBase.getProcType())) {
                //查询工单关联设施信息
                ProcBaseReq procParam = new ProcBaseReq();
                procParam.setProcId(procId);
                List<ProcRelatedDevice> deviceList = procBaseDao.queryProcRelateDevice(procParam);
                List<String> deviceIds = new ArrayList<>();
                if (!ObjectUtils.isEmpty(deviceList)) {
                    for (ProcRelatedDevice deviceOne : deviceList) {
                        deviceIds.add(deviceOne.getDeviceId());
                    }
                    String [] deviceArray = new String[deviceIds.size()];
                    deviceIds.toArray(deviceArray);
                    //调用设施服务接口查询设施信息
                    List<DeviceInfoDto> deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceArray);
                    if (!ObjectUtils.isEmpty(deviceInfoDtoList)) {
                        return ResultUtils.success(deviceInfoDtoList);
                    }
                }
            } else {
                String [] deviceArray = new String [1];
                deviceArray[0] = procBase.getDeviceId();
                //调用设施服务接口查询设施信息
                List<DeviceInfoDto> deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceArray);
                if (!ObjectUtils.isEmpty(deviceInfoDtoList)) {
                    return ResultUtils.success(deviceInfoDtoList);
                }
            }
        }
        return ResultUtils.success(new ArrayList<>());
    }


    /**
     * 查询工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/25 22:19
     * @param procBase 筛选条件
     * @return 查询工单信息
     */
    @Override
    public List<ProcBaseInfoBean> queryProcBaseInfoList(ProcBase procBase) {
        return procBaseDao.queryProcBaseInfoList(procBase);
    }

    /**
     * 查询关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/27 12:56
     * @param procIds 流程编号
     * @return 返回关联设施信息
     */
    @Override
    public List<ProcRelatedDevice> queryProcRelateDeviceByProcIds(List<String> procIds) {
        List<ProcRelatedDevice> procRelatedDeviceList = null;
        if (!ObjectUtils.isEmpty(procIds)) {
            //查询关联设施
            procRelatedDeviceList = procBaseDao.queryProcRelateDeviceByProcIds(procIds);

            //查询销障工单关联设施信息
            List<ProcClearFailure> procClearFailureList = procClearFailureService.queryProcRelateDeviceByProcIds(procIds);
            ProcRelatedDevice procRelatedDeviceOne = null;
            if (!ObjectUtils.isEmpty(procClearFailureList)) {
                for (ProcClearFailure procClearFailureOne : procClearFailureList) {
                    procRelatedDeviceOne = new ProcRelatedDevice();
                    BeanUtils.copyProperties(procClearFailureOne, procRelatedDeviceOne);
                    if (!ObjectUtils.isEmpty(procRelatedDeviceList)) {
                        procRelatedDeviceList.add(procRelatedDeviceOne);
                    } else {
                        procRelatedDeviceList = new ArrayList<>();
                        procRelatedDeviceList.add(procRelatedDeviceOne);
                    }
                }
            }
        }
        return procRelatedDeviceList;
    }

    /**
     * 查询关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/27 12:56
     * @param procIds 流程编号
     * @return 返回关联部门信息
     */
    @Override
    public List<ProcRelatedDepartment> queryProcRelateDeptByProcIds(List<String> procIds) {
        List<ProcRelatedDepartment> procRelatedDepartmentList = null;
        if (!ObjectUtils.isEmpty(procIds)) {
            //查询销障工单关联部门信息
            List<ProcClearFailure> procClearFailureList = procClearFailureService.queryClearRelateDeptByProcIds(procIds);
            if (!ObjectUtils.isEmpty(procClearFailureList)) {
                procRelatedDepartmentList = new ArrayList<>();
                ProcRelatedDepartment procRelatedDepartmentOne = null;
                for (ProcClearFailure procClearFailureOne : procClearFailureList) {
                    if (!ObjectUtils.isEmpty(procClearFailureOne.getAccountabilityDept())) {
                        procRelatedDepartmentOne = new ProcRelatedDepartment();
                        BeanUtils.copyProperties(procClearFailureOne, procRelatedDepartmentOne);
                        procRelatedDepartmentList.add(procRelatedDepartmentOne);
                    }
                }
            }

            //查询巡检工单关联部门信息
            List<ProcInspection> procInspectionList = procInspectionService.queryInspectionRelateDeptByProcIds(procIds);
            if (!ObjectUtils.isEmpty(procInspectionList)) {
                ProcRelatedDepartment procRelatedDepartmentOne = null;
                for (ProcInspection procInspectionOne : procInspectionList) {
                    if (!ObjectUtils.isEmpty(procInspectionOne.getAccountabilityDept())) {
                        procRelatedDepartmentOne = new ProcRelatedDepartment();
                        BeanUtils.copyProperties(procInspectionOne, procRelatedDepartmentOne);
                        if (!ObjectUtils.isEmpty(procRelatedDepartmentList)) {
                            procRelatedDepartmentList.add(procRelatedDepartmentOne);
                        } else {
                            procRelatedDepartmentList = new ArrayList<>();
                            procRelatedDepartmentList.add(procRelatedDepartmentOne);
                        }
                    }
                }
            }
        }
        return procRelatedDepartmentList;
    }

    /**
     * 工单回单
     * @author chaofanrong@wistronits.com
     * @date  2019/4/3 17:33
     * @param procBase 流程
     * @return Result
     */
    @Override
    public Integer receiptProc(ProcBase procBase) {
        procBase.setUpdateTime(new Date());
        return procBaseDao.receiptProcBaseById(procBase);
    }

    /**
     * 查询流程是否有没有办结的工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 12:23
     * @param procInspection 巡检工单参数
     * @return 没有办结的工单信息
     */
    @Override
    public List<ProcBase> selectProcBaseListByProcInspection(ProcInspection procInspection) {
        return procBaseDao.selectProcBaseListByProcInspection(procInspection);
    }

    /**
     * app工单下载
     *
     * @param procBaseReq 工单请求
     *
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result downLoadProcForApp(ProcBaseReq procBaseReq) {
        String format = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        log.info("begin download proc, the beginTime : {}" , simpleDateFormat.format(System.currentTimeMillis()));
        //工单编号和工单类型都为空时提示
        if (ObjectUtils.isEmpty(procBaseReq.getProcId()) && ObjectUtils.isEmpty(procBaseReq.getProcType())) {
            String i18nMsg = I18nUtils.getSystemString(ProcBaseI18n.APP_DOWNLOAD_CONDITION_WRONG);
            //提示请根据工单编号和工单类型下载工单
            return ResultUtils.warn(ProcBaseResultCode.APP_DOWNLOAD_CONDITION_WRONG, i18nMsg);
        }
        //工单类型
        String procType = "";
        if (!ObjectUtils.isEmpty(procBaseReq.getProcId())) {
            //查询工单信息
            ProcBase procBase = this.getProcBaseByProcId(procBaseReq.getProcId());
            if (!ObjectUtils.isEmpty(procBase)) {
                procType = procBase.getProcType();
            } else {
                return ResultUtils.success(new ArrayList());
            }
        } else {
            procType = procBaseReq.getProcType();
        }
        //查询需要下载的工单信息
        List<ProcBaseRespForApp> procBaseRespForApps = this.searchDownloadInfoForProcType(procBaseReq, procType);
        if (ObjectUtils.isEmpty(procBaseRespForApps)) {
            String notProcCanDownloadMsg = I18nUtils.getSystemString(ProcBaseI18n.NOT_PROC_CAN_DOWNLOAD);
            //没有需要下载的工单信息
            return ResultUtils.warn(ProcBaseResultCode.NOT_PROC_CAN_DOWNLOAD, notProcCanDownloadMsg);
        }
        procBaseRespForApps = this.getDownloadInfoList(procBaseRespForApps, procType, procBaseReq);
        //拆分数据
        List<ProcBaseRespForApp> procClearFailureForApps = new ArrayList<>();
        List<ProcBaseRespForApp> procInspectionForApps = new ArrayList<>();
        //工单编号集合
        List<String> procIdList = new ArrayList<>();
        //巡检工单idList
        List<String> procInspectionIdList = new ArrayList<>();
        for (ProcBaseRespForApp procBaseRespForApp : procBaseRespForApps){
            if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBaseRespForApp.getProcType())){
                procClearFailureForApps.add(procBaseRespForApp);
            } else {
                procInspectionForApps.add(procBaseRespForApp);
                procInspectionIdList.add(procBaseRespForApp.getProcId());
            }
            //下载人
            procBaseRespForApp.setAssign(this.getUserId());
            procIdList.add(procBaseRespForApp.getProcId());
        }
        //下载工单信息(批量修改工单责任人信息)
        this.updateProcBaseAssignBatch(procIdList, procType);
        //是否删除
        String isDeleted = WorkFlowBusinessConstants.IS_NOT_DELETED;
        if (!ObjectUtils.isEmpty(procInspectionIdList)) {
            //查询关联巡检记录信息
            List<ProcInspectionRecord> inspectionRecordList = procInspectionRecordService.queryInspectionRecord(isDeleted, procInspectionIdList);
            //将没有巡检的设施添加到返回的工单列表中
            procBaseRespForApps = CastListUtil.notInspectionDeviceToProcBaseResp(procBaseRespForApps, inspectionRecordList);
        }
        //下载工单信息
        List<Object> procBaseRespForAppsResult = this.downloadProcess(procClearFailureForApps, procInspectionForApps, procBaseRespForApps);
        log.info("end download proc the endTime : {}" , simpleDateFormat.format(System.currentTimeMillis()));
        return ResultUtils.success(procBaseRespForAppsResult);
    }

    /**
     * 根据工单类型查询当前用户需要的下载信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 16:32
     */
    public List<ProcBaseRespForApp> searchLoginUserDownloadInfoForProcType(String procType) {
        ProcBaseReq procBaseReq = new ProcBaseReq();
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<ProcBaseReq>();
        queryCondition.setBizCondition(procBaseReq);
        procBaseReq = (ProcBaseReq)this.getPermissionsInfo(queryCondition).getBizCondition();
        //将当前用户编号代入到条件中
        procBaseReq.setAssign(this.getUserId());
        //设置工单批量下载个数
        procBaseReq.setBatchDownloadProcNum(batchDownloadProcNum);
        List<ProcBaseRespForApp> procBaseRespForApps;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //巡检工单
            //责任人等于当前用户并且状态为处理中
            procBaseRespForApps = procInspectionService.queryLoginUserInspectionListForApp(procBaseReq);
        } else {
            //销障工单
            //责任人等于当前用户并且状态为处理中,没有责任人状态为待处理此时数据才能下载
            procBaseRespForApps = procClearFailureService.queryLoginUserClearFailureListForApp(procBaseReq);
        }
        return procBaseRespForApps;
    }

    /**
     * 根据工单类型查询下载信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 12:24
     * @param procBaseReq 工单参数
     * @param procType 工单类型
     * @return 工单下载信息
     */
    public List<ProcBaseRespForApp> searchDownloadInfoForProcType(ProcBaseReq procBaseReq, String procType) {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<ProcBaseReq>();
        queryCondition.setBizCondition(procBaseReq);
        procBaseReq = (ProcBaseReq)this.getPermissionsInfo(queryCondition).getBizCondition();
        //设置工单编号筛选条件
        procBaseReq = this.setProcIdsToProcBaseReq(procBaseReq);
        //将当前用户编号代入到条件中
        procBaseReq.setAssign(this.getUserId());
        //设置工单批量下载个数
        procBaseReq.setBatchDownloadProcNum(batchDownloadProcNum);
        List<ProcBaseRespForApp> procBaseRespForApps;
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //巡检工单
            //责任人等于当前用户并且状态为处理中,没有责任人状态为待处理此时数据才能下载
            procBaseRespForApps = procInspectionService.queryInspectionListForApp(procBaseReq);
        } else {
            //销障工单
            //责任人等于当前用户并且状态为处理中,没有责任人状态为待处理此时数据才能下载
            procBaseRespForApps = procClearFailureService.queryClearFailureListForApp(procBaseReq);
        }
        return procBaseRespForApps;
    }

    /**
     * 查询下载工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 12:14
     * @param procClearFailureForApps
     * @param procInspectionForApps
     * @param procBaseRespForApps
     * @return 返回下载工单信息
     */
    public List<Object> downloadProcessSearch(List<ProcBaseRespForApp> procClearFailureForApps, List<ProcBaseRespForApp> procInspectionForApps, List<ProcBaseRespForApp> procBaseRespForApps) {
        //销障数据
        List<ClearFailureDownLoadDetail> clearFailureDownLoadDetails = procClearFailureService.downLoadClearFailureProcForApp(procClearFailureForApps);
        //巡检数据
        List<InspectionDownLoadDetail> inspectionDownLoadDetails = procInspectionService.downLoadInspectionProcForApp(procInspectionForApps);
        //合并数据
        List<Object> procBaseRespForAppsResult = this.unionDownloadDataList(clearFailureDownLoadDetails, inspectionDownLoadDetails);
        return procBaseRespForAppsResult;
    }

    /**
     * 下载工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 12:14
     * @param procClearFailureForApps
     * @param procInspectionForApps
     * @param procBaseRespForApps
     * @return 下载工单信息
     */
    public List<Object> downloadProcess(List<ProcBaseRespForApp> procClearFailureForApps, List<ProcBaseRespForApp> procInspectionForApps, List<ProcBaseRespForApp> procBaseRespForApps) {
        List<Object> procBaseRespForAppsResult = this.downloadProcessSearch(procClearFailureForApps, procInspectionForApps, procBaseRespForApps);
        //批量下载工单信息(使用异步线程处理)
        workflowService.appDownloadProcBatch(procBaseRespForApps);
        //新增操作日志
        procLogService.addOperateLogBatchForApp(procBaseRespForApps);
        return procBaseRespForAppsResult;
    }

    /**
     * 获取工单下载信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/25 14:24
     * @param procBaseRespForApps 工单app返回参数
     * @param procType 工单类型
     * @param procBaseReq 工单参数
     * @return 获取工单下载信息
     */
    public List<ProcBaseRespForApp> getDownloadInfoList(List<ProcBaseRespForApp> procBaseRespForApps, String procType, ProcBaseReq procBaseReq) {
        if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
            //巡检工单
            //设置批量下载工单ids
            Set<String> procIdSet = new HashSet<>();
            for (ProcBaseRespForApp procBaseRespForApp : procBaseRespForApps){
                procIdSet.add(procBaseRespForApp.getProcId());
            }
            procBaseReq.setProcIds(procIdSet);
            //获取关系表信息
            List<ProcRelatedDevice> procRelatedDevices = procBaseDao.queryProcRelateDevice(procBaseReq);
            //将关联信息加入到返回的工单数据中
            procBaseRespForApps = this.setProcRelatedInfoToRespForApp(procRelatedDevices, procBaseRespForApps);
        } else {
            //销障工单
            //将告警信息加入到返回的工单数据中
            procBaseRespForApps = this.setProcAlarmToRespForApp(procBaseRespForApps);
            //将关联信息加入到返回的工单数据中
            procBaseRespForApps = this.setProcClearFailureRelatedInfoToRespForApp(procBaseRespForApps);
        }
        return procBaseRespForApps;
    }

    /**
     * 校验部门信息有无关联工单
     * @author chaofanrong@wistronits.com
     * @date  2019/4/26 11:03
     */
    @Override
    public Map<String,List<String>> queryProcIdListByDeptIds(List<String> deptIds) {
        //销障工单关联部门
        List<ProcClearFailure> procClearFailureList = procClearFailureService.queryClearProcListByDeptIds(deptIds);
        //查询巡检工单集合
        List<ProcInspection> procInspectionList = procInspectionService.queryInspectionProcListByDeptIds(deptIds);
        Map<String,List<String>> resultMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        List<String> procIds = new ArrayList<>();
        //销障工单关联部门
        if (!ObjectUtils.isEmpty(procClearFailureList)) {
            for (ProcClearFailure procClearFailureOne : procClearFailureList){
                if (resultMap.containsKey(procClearFailureOne.getAccountabilityDept())){
                    procIds = resultMap.get(procClearFailureOne.getAccountabilityDept());
                }
                procIds.add(procClearFailureOne.getProcId());
                resultMap.put(procClearFailureOne.getAccountabilityDept(),procIds);
            }
        }

        //巡检工单关联部门
        if (!ObjectUtils.isEmpty(procInspectionList)) {
            for (ProcInspection procInspectionOne : procInspectionList){
                if (resultMap.containsKey(procInspectionOne.getAccountabilityDept())){
                    procIds = resultMap.get(procInspectionOne.getAccountabilityDept());
                }
                procIds.add(procInspectionOne.getProcId());
                resultMap.put(procInspectionOne.getAccountabilityDept(),procIds);
            }
        }
        return resultMap;
    }

    /**
     * 根据用户查询是否存在正在执行的工单
     * @author hedongwei@wistronits.com
     * @date  2019/5/6 11:48
     * @param userIdList 用户编号集合
     * @return 查询是否存在正在执行的工单
     */
    @Override
    public boolean queryIsExistsAssignUser(List<String> userIdList) {
        if (!ObjectUtils.isEmpty(userIdList)) {
            //查询是否存在进行中的巡检工单
            List<ProcInspection> procInspectionList = procInspectionService.queryExistsProcForUserList(userIdList);
            //查询是否存在进行中的销障工单
            List<ProcClearFailure> procClearFailureList = procClearFailureService.queryExistsProcForUserList(userIdList);
            if (!ObjectUtils.isEmpty(procInspectionList) || !ObjectUtils.isEmpty(procClearFailureList)) {
                //有值代表有正在执行的工单
                return true;
            }
        }
        return false;
    }

    /**
     * 获取没有责任人的工单数据
     * @author hedongwei@wistronits.com
     * @date  2019/4/19 18:30
     * @param procBaseRespForApps
     * @return 获取没有责任人的工单数据
     */
    public List<ProcBaseRespForApp> getNoAssignProcBaseResp(List<ProcBaseRespForApp> procBaseRespForApps) {
        List<ProcBaseRespForApp> noAssignProcBaseList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(procBaseRespForApps)) {
            for (ProcBaseRespForApp procBaseRespForAppOne : procBaseRespForApps) {
                if (ObjectUtils.isEmpty(procBaseRespForAppOne.getAssign())) {
                    noAssignProcBaseList.add(procBaseRespForAppOne);
                }
            }
            return noAssignProcBaseList;
        }
        return new ArrayList<>();
    }


    /**
     * 设置工单编号到请求参数类中
     * @author hedongwei@wistronits.com
     * @date  2019/4/15 14:10
     * @param procBaseReq 流程参数
     * @return 设置工单编号到请求参数类中
     */
    public ProcBaseReq setProcIdsToProcBaseReq(ProcBaseReq procBaseReq) {
        Set<String> procIds = procBaseReq.getProcIds();
        if (ObjectUtils.isEmpty(procIds)){
            procIds = new HashSet<>();
        }

        if (!ObjectUtils.isEmpty(procBaseReq.getProcId())) {
            procIds.add(procBaseReq.getProcId());
            procBaseReq.setProcIds(procIds);
        }
        return procBaseReq;
    }

    /**
     * 销障关联信息添加
     * @author hedongwei@wistronits.com
     * @date  2019/6/25 14:06
     * @param procBaseRespForApps 工单app参数信息
     * @return 返回app页面显示数据
     */
    public List<ProcBaseRespForApp> setProcClearFailureRelatedInfoToRespForApp(List<ProcBaseRespForApp> procBaseRespForApps) {
        //获取设施id集合
        Set<String> deviceIdSet = new HashSet<>();
        if (!ObjectUtils.isEmpty(procBaseRespForApps)) {
            for (ProcBaseRespForApp procBaseRespForApp : procBaseRespForApps){
                deviceIdSet.add(procBaseRespForApp.getDeviceId());
            }
            //关联设施信息为空
            if (ObjectUtils.isEmpty(deviceIdSet)){
                return procBaseRespForApps;
            }

            //调用设施服务接口查询设施详细信息
            List<DeviceInfoDto> deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceIdSet.toArray(new String[deviceIdSet.size()]));
            if (ObjectUtils.isEmpty(deviceInfoDtoList)){
                throw new FilinkObtainDeviceInfoException(I18nUtils.getSystemString(ProcBaseI18n.FAILED_TO_OBTAIN_DEVICE_INFORMATION));
            }

            Map<String,DeviceInfoDto> deviceInfoDtoMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
            for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList){
                deviceInfoDtoMap.put(deviceInfoDto.getDeviceId(),deviceInfoDto);
            }

            //将远程获取到的设施信息转成关系表数据
            List<ProcRelatedDeviceResp> procRelatedDeviceRespList = new ArrayList<>();
            for (ProcBaseRespForApp procBaseRespForApp : procBaseRespForApps) {
                ProcRelatedDeviceResp procRelatedDeviceRespTemp = new ProcRelatedDeviceResp();
                if (ObjectUtils.isEmpty(deviceInfoDtoMap.get(procBaseRespForApp.getDeviceId()))) {
                    throw new FilinkObtainDeviceInfoException(I18nUtils.getSystemString(ProcBaseI18n.FAILED_TO_OBTAIN_DEVICE_INFORMATION));
                }
                BeanUtils.copyProperties(deviceInfoDtoMap.get(procBaseRespForApp.getDeviceId()), procRelatedDeviceRespTemp);
                procRelatedDeviceRespTemp.setProcId(procBaseRespForApp.getProcId());
                procRelatedDeviceRespList.add(procRelatedDeviceRespTemp);
            }
            //组装主表及关系表信息
            procBaseRespForApps = ProcBaseRespForApp.getProcBaseRespForAppsListForProcRelatedDeviceRespList(procRelatedDeviceRespList, procBaseRespForApps);
        }
        return procBaseRespForApps;
    }

    /**
     * 设置工单关联信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/15 14:39
     * @param procRelatedDevices
     * @param procBaseRespForApps
     * @return 设置工单关联信息
     */
    public List<ProcBaseRespForApp> setProcRelatedInfoToRespForApp(
                                                                List<ProcRelatedDevice> procRelatedDevices,
                                                                List<ProcBaseRespForApp> procBaseRespForApps) {
        //获取设施id集合
        Set<String> deviceIdSet = new HashSet<>();
        for (ProcRelatedDevice procRelatedDevice : procRelatedDevices){
            deviceIdSet.add(procRelatedDevice.getDeviceId());
        }
        //关联设施信息为空
        if (ObjectUtils.isEmpty(deviceIdSet)){
            return procBaseRespForApps;
        }
        //调用设施服务接口查询设施详细信息
        List<DeviceInfoDto> deviceInfoDtoList = deviceFeign.getDeviceByIds(deviceIdSet.toArray(new String[deviceIdSet.size()]));
        if (ObjectUtils.isEmpty(deviceInfoDtoList)){
            throw new FilinkObtainDeviceInfoException(I18nUtils.getSystemString(ProcBaseI18n.FAILED_TO_OBTAIN_DEVICE_INFORMATION));
        }
        Map<String,DeviceInfoDto> deviceInfoDtoMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        for (DeviceInfoDto deviceInfoDto : deviceInfoDtoList){
            deviceInfoDtoMap.put(deviceInfoDto.getDeviceId(),deviceInfoDto);
        }

        //将远程获取到的设施信息转成关系表数据
        List<ProcRelatedDeviceResp> procRelatedDeviceRespList = new ArrayList<>();
        for (ProcRelatedDevice procRelatedDevice : procRelatedDevices) {
            ProcRelatedDeviceResp procRelatedDeviceRespTemp = new ProcRelatedDeviceResp();
            if (ObjectUtils.isEmpty(deviceInfoDtoMap.get(procRelatedDevice.getDeviceId()))) {
                throw new FilinkObtainDeviceInfoException(I18nUtils.getSystemString(ProcBaseI18n.FAILED_TO_OBTAIN_DEVICE_INFORMATION));
            }
            BeanUtils.copyProperties(deviceInfoDtoMap.get(procRelatedDevice.getDeviceId()), procRelatedDeviceRespTemp);
            procRelatedDeviceRespTemp.setProcRelatedDeviceId(procRelatedDevice.getProcRelatedDeviceId());
            procRelatedDeviceRespTemp.setProcId(procRelatedDevice.getProcId());
            String areaId = "";
            String areaName = "";
            Integer areaLevel = null;
            if (!ObjectUtils.isEmpty(deviceInfoDtoMap.get(procRelatedDevice.getDeviceId()).getAreaInfo())) {
                AreaInfo areaInfo = deviceInfoDtoMap.get(procRelatedDevice.getDeviceId()).getAreaInfo();
                areaId = areaInfo.getAreaId();
                areaName = areaInfo.getAreaName();
                areaLevel = areaInfo.getLevel();
            }
            procRelatedDeviceRespTemp.setDeviceAreaId(areaId);
            procRelatedDeviceRespTemp.setDeviceAreaName(areaName);
            procRelatedDeviceRespTemp.setDeviceAreaLevel(areaLevel);
            procRelatedDeviceRespList.add(procRelatedDeviceRespTemp);
        }
        //组装主表及关系表信息
        procBaseRespForApps = ProcBaseRespForApp.getProcBaseRespForAppsListForProcRelatedDeviceRespList(procRelatedDeviceRespList, procBaseRespForApps);
        return procBaseRespForApps;
    }

    /**
     * app设置工单关联告警信息
     * @author chaofanrong@wistronits.com
     * @date  2019/4/17 09:03
     * @param procBaseRespForApps
     *
     * @return List<ProcBaseRespForApp>
     */
    public List<ProcBaseRespForApp> setProcAlarmToRespForApp(List<ProcBaseRespForApp> procBaseRespForApps) {
        //调用告警服务接口查询告警详细信息
        List<String> alarmIds = new ArrayList<>();
        for (ProcBaseRespForApp procBaseRespForApp : procBaseRespForApps){
            if (StringUtils.isNotEmpty(procBaseRespForApp.getRefAlarm()) && !alarmIds.contains(procBaseRespForApp.getRefAlarm()) ){
                alarmIds.add(procBaseRespForApp.getRefAlarm());
            }
        }
        //关联告警信息为空
        if (ObjectUtils.isEmpty(alarmIds)){
            return procBaseRespForApps;
        }
        //远程获取当前告警信息
        List<AlarmCurrent> alarmCurrentList = alarmCurrentFeign.queryAlarmDoorByIdsFeign(alarmIds);
        List<AlarmHistory> alarmHistoryList = new ArrayList<>();
        List<Alarm> alarmList = new ArrayList<>();
        //组装当前告警数据
        if (!ObjectUtils.isEmpty(alarmCurrentList)){
            for (AlarmCurrent alarmCurrent : alarmCurrentList){
                Alarm alarm = new Alarm();
                BeanUtils.copyProperties(alarmCurrent,alarm);
                alarmList.add(alarm);
            }
            //如果告警信息未全部获取，则需要再查询历史告警
            if (alarmIds.size() > alarmCurrentList.size()){
                //远程获取历史告警信息
                alarmHistoryList = alarmHistoryFeign.queryAlarmHistoryByIdsFeign(alarmIds);
            }
        } else {
            //如果当前告警为空，则需要再查询历史告警
            alarmHistoryList = alarmHistoryFeign.queryAlarmHistoryByIdsFeign(alarmIds);
        }

        //组装历史告警
        if (!ObjectUtils.isEmpty(alarmHistoryList)){
            for (AlarmHistory alarmHistory : alarmHistoryList){
                Alarm alarm = new Alarm();
                BeanUtils.copyProperties(alarmHistory,alarm);
                alarmList.add(alarm);
            }
        }

        //如果对象为空
        if (ObjectUtils.isEmpty(alarmList)){
            throw new FilinkObtainAlarmInfoException(I18nUtils.getSystemString(ProcBaseI18n.FAILED_TO_OBTAIN_ALARM_INFORMATION));
        }

        //组装主表及告警信息
        Map<String,Alarm> alarmHashMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        for (Alarm alarm : alarmList){
            alarmHashMap.put(alarm.getId(),alarm);
        }

        //解析告警信息
        for (ProcBaseRespForApp procBaseRespForApp : procBaseRespForApps){
            //告警数据为空
            if (ObjectUtils.isEmpty(alarmHashMap.get(procBaseRespForApp.getRefAlarm()))){
                throw new FilinkObtainAlarmInfoException(I18nUtils.getSystemString(ProcBaseI18n.FAILED_TO_OBTAIN_ALARM_INFORMATION));
            }
            procBaseRespForApp.setRefAlarmName(alarmHashMap.get(procBaseRespForApp.getRefAlarm()).getAlarmName());
            procBaseRespForApp.setRefAlarmCode(alarmHashMap.get(procBaseRespForApp.getRefAlarm()).getAlarmCode());
            procBaseRespForApp.setRefAlarmTime(alarmHashMap.get(procBaseRespForApp.getRefAlarm()).getAlarmBeginTime());
            procBaseRespForApp.setDoorNo(alarmHashMap.get(procBaseRespForApp.getRefAlarm()).getDoorNumber());
            procBaseRespForApp.setDoorName(alarmHashMap.get(procBaseRespForApp.getRefAlarm()).getDoorName());
        }
        return procBaseRespForApps;
    }

    /**
     * 批量修改工单责任人
     * @author hedongwei@wistronits.com
     * @date  2019/4/14 20:03
     * @param procIdList 工单编号
     */
    public void updateProcBaseAssignBatch(List<String> procIdList, String procType) {
        if (!ObjectUtils.isEmpty(procIdList)) {
            ProcBase procBaseParam = this.getUpdateProcBase(new Date());
            //责任人
            procBaseParam.setAssign(this.getUserId());
            //单位类型为人
            procBaseParam.setDeptType(WorkFlowBusinessConstants.DEPT_TYPE_PERSON);
            if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
                //修改巡检工单信息
                procInspectionService.updateProcInspectionAssignBatch(procBaseParam, procIdList);
            } else {
                //修改销障工单信息
                procClearFailureService.updateProcClearAssignBatch(procBaseParam, procIdList);
            }
        }
    }

    /**
     * 合并数据
     * @author hedongwei@wistronits.com
     * @date  2019/4/14 20:52
     * @param clearFailureDownLoadDetails 销障的详情数据
     * @param inspectionDownLoadDetails 巡检的详情数据
     * @return 返回给app的数据信息
     */
    public List<Object> unionDownloadDataList(List<ClearFailureDownLoadDetail> clearFailureDownLoadDetails, List<InspectionDownLoadDetail> inspectionDownLoadDetails) {
        //合并数据
        List<Object> procBaseRespForAppsResult = new ArrayList<>();

        //销障数据
        if (!ObjectUtils.isEmpty(clearFailureDownLoadDetails)) {
            for (ClearFailureDownLoadDetail clearFailureDownLoadDetail : clearFailureDownLoadDetails){
                procBaseRespForAppsResult.add(clearFailureDownLoadDetail);
            }
        }

        //巡检数据
        if (!ObjectUtils.isEmpty(inspectionDownLoadDetails)) {
            for (InspectionDownLoadDetail inspectionDownLoadDetail : inspectionDownLoadDetails) {
                procBaseRespForAppsResult.add(inspectionDownLoadDetail);
            }
        }
        return procBaseRespForAppsResult;
    }




    /*--------------------------以下为共用方法--------------------------*/

    /*--------------------------工单关联信息start--------------------------*/

    /**
     * 保存工单关联信息
     *
     * @param processInfo 工单信息汇总
     * @return void
     */
    public void saveProcRelate(ProcessInfo processInfo) {
        if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(processInfo.getProcBase().getProcType())) {
            //销障工单(无关联信息)
        } else if (ProcBaseConstants.PROC_INSPECTION.equals(processInfo.getProcBase().getProcType())) {
            //查询巡检工单信息
            ProcInspection procInspectionParam = new ProcInspection();
            procInspectionParam.setProcId(processInfo.getProcBase().getProcId());
            procInspectionParam.setIsDeleted(WorkFlowBusinessConstants.IS_NOT_DELETED);
            ProcInspection procInspection = procInspectionService.selectProcInspectionOne(procInspectionParam);
            //巡检工单
            //删除工单关联设施信息
            procBaseDao.deleteProcRelateDeviceByProcId(processInfo.getProcBase());
            if (!ObjectUtils.isEmpty(processInfo.getProcRelatedDevices())){
                Date createTime = null;
                if (!ObjectUtils.isEmpty(procInspection)) {
                    createTime = procInspection.getCreateTime();
                } else {
                    createTime = processInfo.getProcBase().getCreateTime();
                }

                //统一设置主键id
                for (int i = 0; i < processInfo.getProcRelatedDevices().size() ; i++){
                    processInfo.getProcRelatedDevices().get(i).setProcRelatedDeviceId(NineteenUUIDUtils.uuid());
                }
                procBaseDao.addProcRelateDevice(processInfo);
            }

            //新增工单关联巡检记录信息
            if (!ObjectUtils.isEmpty(processInfo.getProcInspectionRecords())) {
                //流程id
                String procBaseId = processInfo.getProcBase().getProcId();
                ProcInspectionRecord procInspectionRecord = new ProcInspectionRecord();
                procInspectionRecord.setProcId(procBaseId);
                //删除巡检工单巡检记录
                procInspectionRecordService.deleteRecordByProcId(procInspectionRecord);
                //添加关联编号
                for (ProcInspectionRecord recordOne : processInfo.getProcInspectionRecords()) {
                    //设置巡检工单编号
                    recordOne.setProcId(processInfo.getProcBase().getProcId());
                }
                //新增巡检工单巡检记录
                procInspectionRecordService.insertInspectionRecordBatch(processInfo.getProcInspectionRecords());
            }
        }

    }

    /**
     * 获取工单关联信息
     *
     * @param processInfoList 工单汇总类列表
     * @param procIds 工单id集合
     * @param methodType 方法类型（列表或明细）
     * @param methodType 方法类型（列表或明细）
     *
     * @return List<ProcessInfo> 工单汇总类列表
     */
    public List<ProcessInfo> queryProcRelate(List<ProcessInfo> processInfoList, Set<String> procIds, List<Department> departmentList, String methodType) {
        ProcBaseReq procBaseReq = new ProcBaseReq();
        if (!ObjectUtils.isEmpty(procIds)) {
            procBaseReq.setProcIds(procIds);

            //获取工单关联设施信息
            List<ProcRelatedDevice> procRelatedDevices = procBaseDao.queryProcRelateDevice(procBaseReq);

            //获取销障工单关联巡检设施信息
            procRelatedDevices = ProcRelatedDevice.getClearProcRelatedDevice(processInfoList, procRelatedDevices);


            //工单关联部门信息
            List<ProcRelatedDepartment> procRelatedDepartments = new ArrayList<>();

            //获取工单关联部门信息
            procRelatedDepartments = ProcRelatedDepartment.getProcRelatedDepartment(processInfoList, procRelatedDepartments);

            //组装流程主表及关系表数据
            return assemblyProcAndRelatedDataList(processInfoList, procRelatedDevices, procRelatedDepartments, departmentList, methodType);
        } else {
            return processInfoList;
        }
    }

    /**
     * 组装流程主表及关系表数据
     *
     * @param processInfoList 工单汇总类列表
     * @param procRelatedDevices 工单关联设施列表
     * @param procRelatedDepartments 工单关联设部门列表
     * @param departmentList 远程调用部门信息
     * @param methodType 方法类型（列表或明细）
     *
     * @return List<ProcessInfo> 工单汇总类列表
     */
    public List<ProcessInfo> assemblyProcAndRelatedDataList(
                                                List<ProcessInfo> processInfoList,
                                                List<ProcRelatedDevice> procRelatedDevices,
                                                List<ProcRelatedDepartment> procRelatedDepartments,
                                                List<Department> departmentList,
                                                String methodType) {
            //组装主表及设施关系表信息
            Map<String,List<ProcRelatedDevice>> procRelatedDeviceListMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
            List<ProcRelatedDevice> procRelatedDeviceTemps;
            for (ProcRelatedDevice procRelatedDevice : procRelatedDevices){
                if (procRelatedDeviceListMap.containsKey(procRelatedDevice.getProcId())){
                    procRelatedDeviceTemps = procRelatedDeviceListMap.get(procRelatedDevice.getProcId());
                } else {
                    procRelatedDeviceTemps = new ArrayList<>();
                }
                procRelatedDeviceTemps.add(procRelatedDevice);
                procRelatedDeviceListMap.put(procRelatedDevice.getProcId(),procRelatedDeviceTemps);
            }
            //组装主表及部门关系表信息
            Map<String,List<ProcRelatedDepartment>> procRelatedDeptListMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
            List<ProcRelatedDepartment> procRelatedDeptTemps;
            for (ProcRelatedDepartment procRelatedDepartment : procRelatedDepartments){
                if (procRelatedDeptListMap.containsKey(procRelatedDepartment.getProcId())){
                    procRelatedDeptTemps = procRelatedDeptListMap.get(procRelatedDepartment.getProcId());
                } else {
                    procRelatedDeptTemps = new ArrayList<>();
                }
                procRelatedDeptTemps.add(procRelatedDepartment);
                procRelatedDeptListMap.put(procRelatedDepartment.getProcId(),procRelatedDeptTemps);
            }
            //组装主表及关系表数据
            for (ProcessInfo processInfo : processInfoList){
                processInfo.setProcRelatedDevices(procRelatedDeviceListMap.get(processInfo.getProcBaseResp().getProcId()));
                processInfo = ProcBaseUtil.setDeviceInfoList(processInfo);

                processInfo.setProcRelatedDepartments(procRelatedDeptListMap.get(processInfo.getProcBaseResp().getProcId()));
                this.setAccountabilityUnitName(processInfo,departmentList,methodType);
            }
            return processInfoList;
    }



    /**
     * 设置单位名称
     *
     * @param processInfo 工单汇总类
     * @param departmentList 部门列表
     * @param methodType 方法类型（明细或列表）
     *
     * @return boolean 是否设置成功
     */
    public boolean setAccountabilityUnitName(ProcessInfo processInfo, List<Department> departmentList, String methodType) {
        String accountabilityUnitName = "";
        Map<String, String> departmentMap = new HashMap<>(64);
        if (processInfo.getProcRelatedDepartments() == null || processInfo.getProcRelatedDepartments().size() == 0) {
            return true;
        }
        //明细页，根据id获取部门信息
        if (ProcBaseConstants.PROC_METHOD_DETAIL.equals(methodType)){
            //如果没有关联部门，则不去远程调用
            if (ObjectUtils.isEmpty(processInfo.getProcRelatedDepartments()) || processInfo.getProcRelatedDepartments().size() == 0){
                return true;
            }
            List<String> ids = new ArrayList<>();
            for (ProcRelatedDepartment procRelatedDepartment : processInfo.getProcRelatedDepartments()) {
                ids.add(procRelatedDepartment.getAccountabilityDept());
            }
            departmentList = this.getDepartmentListById(ids);
        }
        if (departmentList == null) {
            return true;
        }
        //list转map，以id作为key
        for (Department department:departmentList){
            if (!ObjectUtils.isEmpty(department) && !StringUtils.isEmpty(department.getDeptName())) {
                departmentMap.put(department.getId(),department.getDeptName());
            }
        }
        //拼接部门名称
        for (ProcRelatedDepartment procRelatedDepartment : processInfo.getProcRelatedDepartments()) {
            String deptName = departmentMap.get(procRelatedDepartment.getAccountabilityDept());
            if (!StringUtils.isEmpty(deptName)){
                if (!StringUtils.isEmpty(accountabilityUnitName)) {
                    accountabilityUnitName += ",";
                    accountabilityUnitName += deptName;
                } else {
                    accountabilityUnitName += deptName;
                }
            }
        }
        if (accountabilityUnitName.length() == 0) {
            return true;
        }
        //获取页面返回的设施信息
        processInfo.getProcBaseResp().setAccountabilityDeptName(accountabilityUnitName);
        return true;
    }

    /**
     * 根据id调用部门服务获取部门信息
     *
     * @return List<Department>
     */
    public List<Department> getDepartmentListById(List<String> ids) {
        List<Department> departmentList = departmentFeign.queryDepartmentFeignById(ids);
        if (departmentList == null) {
            throw new FilinkObtainDeptInfoException(I18nUtils.getSystemString(ProcBaseI18n.FAILED_TO_OBTAIN_DEPARTMENT_INFORMATION));
        }
        return departmentList;
    }
    /*--------------------------工单关联信息end--------------------------*/

    /*--------------------------工单特性信息start--------------------------*/
    /**
     * 保存工单特性信息
     *
     * @param processInfo 工单汇总类
     * @return Result
     */
    public void saveProcSpecific(ProcessInfo processInfo) {
        if (!ObjectUtils.isEmpty(processInfo.getProcBase())) {
            if (!StringUtils.isEmpty(processInfo.getProcBase().getProcType())) {
                if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(processInfo.getProcBase().getProcType())) {
                    //传入值不为空时才需要保存
                    ProcClearFailure procClearFailure = new ProcClearFailure();
                    BeanUtils.copyProperties(processInfo.getProcBaseReq(), procClearFailure);
                    //销障工单添加设施和区域的参数
                    procClearFailure = ProcClearFailure.setDeviceToProcClearFailure(processInfo, procClearFailure);

                    //将关联部门信息添加到巡检工单参数中
                    String departmentId = "";
                    String departmentName = "";
                    if (!ObjectUtils.isEmpty(processInfo.getProcRelatedDepartments())) {
                        departmentId = processInfo.getProcRelatedDepartments().get(0).getAccountabilityDept();
                        departmentName = procRelatedService.getRelatedDepartmentName(processInfo);
                    }
                    procClearFailure.setAccountabilityDept(departmentId);
                    procClearFailure.setAccountabilityDeptName(departmentName);

                    //新增销障工单信息
                    procClearFailureService.saveProcClearFailureSpecific(procClearFailure);
                } else if (ProcBaseConstants.PROC_INSPECTION.equals(processInfo.getProcBase().getProcType())) {
                    ProcInspection procInspection = ProcInspection.setProcInspectionParamToProcInspection(processInfo);
                    //将设施信息添加到巡检工单参数中
                    procInspection = ProcInspection.setDeviceToProcInspection(processInfo, procInspection);
                    //将关联部门信息添加到巡检工单参数中
                    String departmentId = "";
                    String departmentName = "";
                    if (!ObjectUtils.isEmpty(processInfo.getProcRelatedDepartments())) {
                        departmentId = processInfo.getProcRelatedDepartments().get(0).getAccountabilityDept();
                        departmentName = procRelatedService.getRelatedDepartmentName(processInfo);
                    }
                    procInspection.setAccountabilityDept(departmentId);
                    procInspection.setAccountabilityDeptName(departmentName);
                    //新增巡检工单信息
                    procInspectionService.saveProcInspectionSpecific(procInspection);
                }
            }
        }
    }

    /**
     * 获取工单特性信息
     *
     * @param processInfoList 工单汇总列表
     * @param procIds 工单ids
     * @param procType 工单type
     *
     * @return Result
     */
    public void queryProcSpecific(List<ProcessInfo> processInfoList, Set<String> procIds, String procType) {
        if (!ObjectUtils.isEmpty(procIds)){
            if (!StringUtils.isEmpty(procType)){
                if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procType)){
                    //销障工单
                    List<ProcClearFailure> procClearFailures = procClearFailureService.queryProcClearFailureSpecific(procIds);

                    //组装主表及特性表信息
                    Map<String,ProcClearFailure> procClearFailureMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
                    for (ProcClearFailure procClearFailure : procClearFailures){
                        procClearFailureMap.put(procClearFailure.getProcId(),procClearFailure);
                    }
                    for (ProcessInfo processInfo : processInfoList){
                        processInfo.setProcClearFailure(procClearFailureMap.get(processInfo.getProcId()));
                    }

                } else if (ProcBaseConstants.PROC_INSPECTION.equals(procType)) {
                    //巡检工单特性信息待完善
                    List<ProcInspection> procInspectionList = procInspectionService.selectInspectionProcByProcIds(procIds);
                    //组装主表及特性表信息
                    Map<String, ProcInspection> procInspectionMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
                    if (!ObjectUtils.isEmpty(procInspectionList)) {
                        for (ProcInspection procInspectionOne : procInspectionList) {
                            procInspectionMap.put(procInspectionOne.getProcId(), procInspectionOne);
                        }
                    }

                    if (!ObjectUtils.isEmpty(processInfoList)) {
                        for (ProcessInfo processInfo : processInfoList){
                            processInfo.setProcInspection(procInspectionMap.get(processInfo.getProcId()));
                            ProcBaseResp resp = ProcBaseResp.procInspectionSetAttrToProcBaseResp(processInfo.getProcInspection(), processInfo);
                            processInfo.setProcBaseResp(resp);
                        }
                    }
                }
            }
        }
    }

    /*--------------------------工单特性信息end--------------------------*/

    /**
     * 校验工单基本参数
     *
     * @param processInfo 工单汇总类
     * @return Result
     */
    public Result checkProcParams(ProcessInfo processInfo){
        Result result = new Result();
        //校验参数合法性
        if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(processInfo.getProcBase().getProcType())){
            //销障工单
            result = procClearFailureService.checkProcParamsForClearFailure(processInfo);
        } else if (ProcBaseConstants.PROC_INSPECTION.equals(processInfo.getProcBase().getProcType())){
            //巡检工单
            result.setCode(ResultCode.SUCCESS);
        }
        return result;
    }

    /**
     * 设置用户信息
     *
     * @param processInfo 工单汇总类
     * @param operateType 操作类型（新增，删除）
     *
     * @return Result
     */
    public ProcessInfo setUserInfo(ProcessInfo processInfo, String operateType){
        String userId = "system";
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(servletRequestAttributes)) {
            userId = RequestInfoUtils.getUserId();
        }
        //销障工单
        if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(processInfo.getProcBaseReq().getProcType())){
            //新增操作
            if (WorkFlowBusinessConstants.OPERATE_TYPE_ADD.equals(operateType)){
                //手工新增
                if (ProcBaseConstants.PROC_RESOURCE_TYPE_1.equals(processInfo.getProcBaseReq().getProcResourceType())){
                    processInfo.getProcBaseReq().setCreateUser(userId);
                }
                processInfo.getProcBaseReq().setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
                //修改操作
            } else if (WorkFlowBusinessConstants.OPERATE_TYPE_UPDATE.equals(operateType)){
                processInfo.getProcBaseReq().setUpdateUser(userId);
                processInfo.getProcBaseReq().setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
            }

            //巡检工单
        } else if (ProcBaseConstants.PROC_INSPECTION.equals(processInfo.getProcBaseReq().getProcType())){

        }
        return processInfo;
    }

    /**
     * 转换列表所需要的工单信息
     *
     * @param procBaseRespList 工单列表
     *
     * @return List<ProcBaseResp> 列表展示信息
     */
    public List<ProcBaseResp> convertProcInfoToProcResp(List<ProcBaseResp> procBaseRespList) {
        List<ProcBaseResp> procBaseRespResultList = new ArrayList<>();

        //远程获取部门信息
        List<Department> departmentList = departmentFeign.queryAllDepartment();

        //远程获取用户信息
        procBaseRespList = queryUserByIdList(procBaseRespList);

        //获取关系信息
        List<ProcessInfo> processInfoList = this.getProcRelationInfoList(procBaseRespList,departmentList,ProcBaseConstants.PROC_METHOD_LIST);

        //转换为返回界面的数据
        for (ProcessInfo processInfo : processInfoList){
            procBaseRespResultList.add(processInfo.getProcBaseResp());
        }

        //国际化枚举值
        procBaseRespResultList = ProcBaseResp.internationalizationEnum(procBaseRespResultList);
        return procBaseRespResultList;
    }



    /**
     * 转换明细所需要的工单信息
     *
     * @param procBaseResp 工单返回信息
     * @param processDetail 工单明细信息
     *
     * @return ProcessDetail 明细界面展示信息
     */
    public ProcessDetail convertProcInfoToProcDetail(ProcBaseResp procBaseResp, ProcessDetail processDetail) {
        //获取关系信息
        ProcessInfo processInfo = this.getProcRelationInfoList(procBaseResp,new ArrayList<>(),ProcBaseConstants.PROC_METHOD_DETAIL).get(0);

        //明细工单关联设施信息
        processDetail.setProcRelatedDevices(processInfo.getProcRelatedDevices());
        //明细工单关联部门信息
        processDetail.setProcRelatedDepartments(processInfo.getProcRelatedDepartments());

        //巡检工单
        if (ProcBaseConstants.PROC_INSPECTION.equals(procBaseResp.getProcType())) {
            //查询巡检工单详情
            ProcInspection procInspection = procInspectionService.getProcInspectionByProcId(procBaseResp.getProcId());

            procBaseResp = ProcBaseResp.getInspectionProcBaseResp(procInspection, procBaseResp);
            //选择全集
            procBaseResp.setIsSelectAll(procInspection.getIsSelectAll());
        }

        //转换为返回界面的数据
        BeanUtils.copyProperties(procBaseResp,processDetail);
        return processDetail;
    }

    /**
     * 获取关系信息
     *
     * @param object 工单返回类或列表
     * @param departmentList 所有部门信息
     * @param methodType 方法类型（明细或列表）
     *
     * @return List<ProcessInfo>
     */
    public List<ProcessInfo> getProcRelationInfoList(Object object, List<Department> departmentList, String methodType) {
        List<ProcessInfo> processInfoList= new ArrayList<>();
        String procType = "";
        //获取待转换值
        List<ProcBaseResp> procBaseRespList = new ArrayList<>();
        ProcBaseResp procBaseResp;
        if (object instanceof List){
            procBaseRespList = (List<ProcBaseResp>)object;
        } else if (object instanceof ProcBaseResp){
            procBaseRespList = new ArrayList<>();
            procBaseResp = (ProcBaseResp)object;
            procBaseRespList.add(procBaseResp);
        }

        //所有工单ids
        Set<String> procIds = new HashSet<>();

        //复制信息
        for (int i = 0; i< procBaseRespList.size();i++){
            ProcessInfo processInfo = new ProcessInfo();

            //巡检进度添加到返回类中
            procBaseResp = ProcBaseResp.setProgressSpeedToResp(procBaseRespList.get(i));

            //转换时间戳
            procBaseResp = ProcBaseResp.setTimeStamp(procBaseResp);

            //复制信息
            ProcBaseReq procBaseReq = new ProcBaseReq();
            ProcBase procBase = new ProcBase();
            processInfo.setProcBaseResp(procBaseResp);
            BeanUtils.copyProperties(procBaseResp,procBaseReq);
            BeanUtils.copyProperties(procBaseResp,procBase);

            //获取所有工单ids
            procIds.add(procBaseResp.getProcId());
            processInfo.setProcBaseReq(procBaseReq);
            processInfo.setProcBase(procBase);

            //获取所有工单类型
            procType = procBaseReq.getProcType();

            processInfoList.add(processInfo);
        }
        //获取列表工单关联信息
        this.queryProcRelate(processInfoList,procIds,departmentList,methodType);
        //获取工单特性信息
        this.queryProcSpecific(processInfoList,procIds,procType);

        return processInfoList;
    }

    /**
     * 获取登录用户id
     *
     * @return String
     */
    public String getUserId(){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(servletRequestAttributes)) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            if (!ObjectUtils.isEmpty(request)) {
                return request.getHeader("userId");
            }
        }
        return "";
    }

    /**
     * 远程获取用户信息
     *
     * @param procBaseRespList 返回数据列表
     *
     * @return List<ProcBaseResp> 返回数据列表
     */
    public List<ProcBaseResp> queryUserByIdList(List<ProcBaseResp> procBaseRespList){
        List<String> idList = new ArrayList<>();
        for (ProcBaseResp procBaseResp : procBaseRespList){
            if(!StringUtils.isEmpty(procBaseResp.getAssign())){
                idList.add(procBaseResp.getAssign());
            }
        }
        if (!ObjectUtils.isEmpty(idList)){
            List<Object> objects = (List<Object>)userFeign.queryUserByIdList(idList);
            List<User> users = new ArrayList<>();
            for (Object object : objects){
                User user = JSONArray.toJavaObject((JSON)JSONArray.toJSON(object),User.class);
                users.add(user);
            }
            Map<String,String> userMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
            for(User user : users){
                userMap.put(user.getId(),user.getUserName());
            }
            for (ProcBaseResp procBaseResp : procBaseRespList){
                procBaseResp.setAssignName(userMap.get(procBaseResp.getAssign()));
            }
        }
        return procBaseRespList;
    }

    /**
     * 根据告警查询存在的工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/19 12:49
     * @param alarmProcList 告警编号数组
     * @return 查询告警工单结果
     */
    @Override
    public Map<String, Object> queryExistsProcForAlarmList(List<String> alarmProcList) {
        if (!ObjectUtils.isEmpty(alarmProcList)) {
            //初始化返回值
            Map<String, Object> alarmMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
            for (String alarmId : alarmProcList) {
                alarmMap.put(alarmId, "0");
            }
            //查询所有告警编号关联的数据信息
            List<ProcBase> procBaseList = procBaseDao.queryExistsProcForAlarmList(alarmProcList);
            alarmMap = CastMapUtil.getIsExistsAlarmMap(procBaseList, alarmMap);
            return alarmMap;
        }
        return null;
    }

    /**
     * 根据告警查询存在的工单信息
     * @author chaofanrong@wistronits.com
     * @date  2019/4/28 23:38
     * @param procBase 工单信息
     * @param operaType 操作类型（新增或重新生成）
     *
     * @return 查询告警工单结果
     */
    @Override
    public Boolean queryExistsProcForAlarm(ProcBase procBase, String operaType) {
        List<String> alarmProcList = new ArrayList<>();
        alarmProcList.add(procBase.getRefAlarm());
        //查询所有告警编号关联的数据信息
        List<ProcClearFailure> procClearList = procClearFailureService.queryExistsProcClearFailureForAlarmList(alarmProcList);
        if (!ObjectUtils.isEmpty(procClearList)) {
            for (ProcClearFailure procClearOne : procClearList) {
                if (StringUtils.isEmpty(procBase.getProcId())) {
                    if (!ObjectUtils.isEmpty(procClearOne) && !StringUtils.isEmpty(procClearOne.getProcId())) {
                        return true;
                    }
                } else {
                    if (!ObjectUtils.isEmpty(procClearOne) && !StringUtils.isEmpty(procClearOne.getProcId())) {
                        //是否为重新生成
                        if (!ProcBaseConstants.OPERATOR_TYPE_REGENERATE.equals(operaType)){
                            //修改校验时由于当前数据已存在，要排除当前数据
                            if (!procClearOne.getProcId().equals(procBase.getProcId())) {
                                return true;
                            }
                        } else {
                            //重新生成时要排除对应原工单
                            if (!procClearOne.getProcId().equals(procBase.getRegenerateId())) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    /*---------------------------------------获取权限公共方法start-----------------------------------------*/
    /**
     * 获取拥有权限信息
     *
     * @param queryCondition    查询对象
     * @return queryCondition    查询对象
     */
    @Override
    public QueryCondition<ProcBaseReq> getPermissionsInfo(QueryCondition<ProcBaseReq> queryCondition) {
        return getPermissionsInfo(queryCondition,RequestInfoUtils.getUserId());
    }

    /**
     * 获取拥有权限信息（导出）
     *
     * @param queryCondition    查询对象
     * @return queryCondition    查询对象
     */
    @Override
    public QueryCondition<ProcBaseReq> getPermissionsInfoForExport(QueryCondition<ProcBaseReq> queryCondition) {
        return getPermissionsInfo(queryCondition,ExportApiUtils.getCurrentUserId());
    }

    /**
     * 获取拥有权限信息
     *
     * @param queryCondition    查询对象
     * @return queryCondition    查询对象
     */
    public QueryCondition<ProcBaseReq> getPermissionsInfo(QueryCondition<ProcBaseReq> queryCondition, String userId){
        //获取权限信息
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        Object userObj = userFeign.queryUserByIdList(userIds);
        //校验是否有值
        if (ObjectUtils.isEmpty(userObj)) {
            throw new FilinkObtainUserInfoException();
        }
        List<User> userInfoList = JSONArray.parseArray(JSONArray.toJSONString(userObj), User.class);
        User user = new User();
        //添加用户map
        if (!ObjectUtils.isEmpty(userInfoList)) {
            user = userInfoList.get(0);
        }

        //admin用户不用校验权限
        String adminUserId = WorkFlowBusinessConstants.ADMIN_USER_ID;
        String loginUserId = userId;
        if (!ObjectUtils.isEmpty(loginUserId)) {
            //当前用户是admin时不用获取所有权限
            if (adminUserId.equals(loginUserId)) {
                return queryCondition;
            }
        }

        //用户管理区域
        List<String> areaIds = user.getDepartment().getAreaIdList();
        //用户管理设施类型
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();
        //用户管理部门
        Department department = user.getDepartment();

        //当用户权限信息中有信息为空，直接返回页面空
        if (ObjectUtils.isEmpty(areaIds) || ObjectUtils.isEmpty(roleDeviceTypes)){
            throw new FilinkUserPermissionException();
        }

        Set<String> deviceTypeSet = new HashSet<>();
        Set<String> areaIdSet = new HashSet<>();
        Set<String> departmentIdSet = new HashSet<>();
        for (RoleDeviceType roleDeviceType : roleDeviceTypes){
            deviceTypeSet.add(roleDeviceType.getDeviceTypeId());
        }
        for (String areaId : areaIds){
            areaIdSet.add(areaId);
        }
        departmentIdSet.add(department.getId());

        queryCondition.getBizCondition().setPermissionDeviceTypes(deviceTypeSet);
        queryCondition.getBizCondition().setPermissionAreaIds(areaIdSet);
        queryCondition.getBizCondition().setPermissionDeptIds(departmentIdSet);

        return queryCondition;
    }

    /**
     * 根据设施编号集合删除工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 18:56
     * @param req 删除工单参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteProcBaseForDeviceList(DeleteProcBaseForDeviceReq req) {
        String format = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        log.info("begin delete proc for deviceIdList , the begin time : {}", simpleDateFormat.format(System.currentTimeMillis()));
        if (ObjectUtils.isEmpty(req.getIsDeleted())) {
            req.setIsDeleted(WorkFlowBusinessConstants.IS_DELETED);
        }
        if (!ObjectUtils.isEmpty(req.getDeviceIdList())) {
            //查询需要删除的设施的信息
            ProcRelatedDeviceListForDeviceIdsReq relatedDeviceReq = new ProcRelatedDeviceListForDeviceIdsReq();
            BeanUtils.copyProperties(req, relatedDeviceReq);
            relatedDeviceReq.setIsDeleted(ProcBaseUtil.getIsDeletedSearchParam(req.getIsDeleted()));
            List<ProcRelatedDevice> procRelatedDeviceList = procBaseDao.selectRelatedDeviceListInfo(relatedDeviceReq);

            //查询需要删除的销障工单的设施信息
            List<ProcClearFailure> procClearList = procClearFailureService.selectClearRelatedDeviceListInfo(relatedDeviceReq);

            //删除销障工单的方法
            this.deleteClearFailureInfo(req, procClearList);

            //删除巡检工单的方法
            this.deleteInspectionInfo(req, procRelatedDeviceList, relatedDeviceReq);
        }
        log.info("end proc for deviceIdList , the end time : {}", simpleDateFormat.format(System.currentTimeMillis()));
        return ResultUtils.success();
    }

    /**
     * 根据设施删除巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/25 19:41
     * @param req 删除工单设施参数
     * @param procRelatedDeviceList 工单关联设施信息集合
     * @param relatedDeviceReq 关联设施参数
     */
    public void deleteInspectionInfo(DeleteProcBaseForDeviceReq req, List<ProcRelatedDevice> procRelatedDeviceList, ProcRelatedDeviceListForDeviceIdsReq relatedDeviceReq) {
        if (!ObjectUtils.isEmpty(procRelatedDeviceList)) {

            //工单设施map集合，以工单编号为键，以关联设施信息为值
            Map<String, List<ProcRelatedDevice>> procDeviceMap = CastMapUtil.getProcRelatedDeviceMap(procRelatedDeviceList);

            //获取被删除的工单所有的关联设施信息
            Map<String, List<ProcRelatedDevice>> procDeviceAllMap = this.getProcRelatedAllDevice(req, procDeviceMap);

            //找到需要删除的工单信息
            List<String> procList = CastListUtil.getAbleDeleteProcIdList(procDeviceAllMap, req);
            //不需要删除的工单信息
            List<String> notDeleteProcList = CastListUtil.getNotDeletedProcId(procDeviceMap, procList);

            //获取查询参数
            ProcBaseReq procBaseReq = ProcBaseReq.getSearchDeleteProcBaseParam(procList, req.getIsDeleted());
            List<ProcBase> procBaseList = null;
            if (!ObjectUtils.isEmpty(procList)) {
                procBaseList = procBaseDao.queryProcessByProcIds(procBaseReq);
            }

            ProcBaseReq procBaseReqNotDelete = ProcBaseReq.getSearchDeleteProcBaseParam(notDeleteProcList, req.getIsDeleted());
            List<ProcInspection> notInspectionNotDelete = null;
            if (!ObjectUtils.isEmpty(notDeleteProcList)) {
                //查询不用全部删除的表的数据
                notInspectionNotDelete = procInspectionService.selectInspectionProcForProcIds(procBaseReqNotDelete);
                //获取巡检总数
                notInspectionNotDelete = CastListUtil.getNotDeleteDeviceCount(procDeviceMap, notInspectionNotDelete);
            }

            Map<String, ProcBase> procBaseMap = CastMapUtil.getProcBaseMap(procBaseList);
            List<ProcBaseReq> procBaseReqList = new ArrayList<>();

            if (!ObjectUtils.isEmpty(procBaseMap)) {
                String isDeleted = req.getIsDeleted();
                procBaseReq.setIsDeleted(isDeleted);
                procBaseReqList = CastListUtil.getDeleteProcBaseReqList(procList, procBaseMap, isDeleted);
                //删除工单信息
                this.deleteProcProcess(procBaseReq, procBaseReqList, isDeleted);
            }

            Map<String, String> procMap = CastMapUtil.getMapForListString(procList);
            //过滤需要的工单信息
            procRelatedDeviceList = CastListUtil.filterAbleProc(procRelatedDeviceList, procMap);

            //删除关联设施信息
            procRelatedService.deleteRelatedDeviceList(procRelatedDeviceList, req.getIsDeleted());

            //查询巡检记录表
            List<ProcInspectionRecord> inspectionRecordList =
                    procInspectionRecordService.selectRelatedDeviceRecordList(relatedDeviceReq, ProcBaseUtil.getIsDeletedSearchParam(req.getIsDeleted()));
            //删除关联巡检设施记录
            procRelatedService.deleteRelatedInspectionDeviceRecord(inspectionRecordList, procMap, req.getIsDeleted());

            //获取巡检完成记录信息
            if (!ObjectUtils.isEmpty(inspectionRecordList) && !ObjectUtils.isEmpty(notInspectionNotDelete)) {
                notInspectionNotDelete = CastListUtil.getNotDeleteCompleteDeviceCount(inspectionRecordList, notInspectionNotDelete);
            }
            //批量更新巡检工单的设施数量
            procInspectionService.updateProcInspectionDeviceCountBatch(notInspectionNotDelete);
        }
    }

    /**
     * 删除销障工单
     * @author hedongwei@wistronits.com
     * @date  2019/6/25 19:30
     * @param req 删除销障工单参数
     * @param procClearList 销障工单关联信息
     * @return 删除销障工单信息
     */
    public void deleteClearFailureInfo(DeleteProcBaseForDeviceReq req, List<ProcClearFailure> procClearList) {
        if (!ObjectUtils.isEmpty(procClearList)) {
            List<ProcBaseReq> deleteProcBaseReqList = new ArrayList<>();
            List<String> procList = new ArrayList<>();
            ProcBaseReq procBaseReq ;
            //直接删除销障工单的数据
            for (ProcClearFailure procClearFailureOne : procClearList) {
                procBaseReq = new ProcBaseReq();
                BeanUtils.copyProperties(procClearFailureOne ,procBaseReq);
                procBaseReq.setIsDeleted(req.getIsDeleted());
                procBaseReq.setUpdateUser(this.getUserId());
                procBaseReq.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
                deleteProcBaseReqList.add(procBaseReq);
            }
            ProcBaseReq procBaseReqParam = ProcBaseReq.getSearchDeleteProcBaseParam(procList, req.getIsDeleted());
            procBaseReqParam.setIsDeleted(req.getIsDeleted());
            //删除销障工单信息
            this.deleteProcProcess(procBaseReqParam, deleteProcBaseReqList, req.getIsDeleted());
        }
    }

    /**
     * 获取被删除工单的全部关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 17:36
     * @param req 删除工单参数
     * @param procDeviceMap 需要删除的工单信息
     * @return 获取被删除工单的全部关联设施信息
     */
    public Map<String, List<ProcRelatedDevice>> getProcRelatedAllDevice(DeleteProcBaseForDeviceReq req , Map<String, List<ProcRelatedDevice>> procDeviceMap) {
        Map<String, List<ProcRelatedDevice>> procDeviceAllMap = new HashMap<>(WorkFlowBusinessConstants.MAP_INIT_VALUE);
        if (!ObjectUtils.isEmpty(procDeviceMap)) {
            Set<String> procIdSet = new HashSet<>();
            for (String procDeviceKey : procDeviceMap.keySet()) {
                if (!ObjectUtils.isEmpty(procDeviceKey)) {
                    procIdSet.add(procDeviceKey);
                }
            }

            if (!ObjectUtils.isEmpty(procIdSet)) {
                ProcBaseReq searchReq = new ProcBaseReq();
                searchReq.setIsDeleted(ProcBaseUtil.getIsDeletedSearchParam(req.getIsDeleted()));
                searchReq.setProcIds(procIdSet);
                List<ProcRelatedDevice> relatedDeviceAllList = procBaseDao.selectRelatedDeviceListForProcIds(searchReq);
                procDeviceAllMap = CastMapUtil.getProcRelatedDeviceMap(relatedDeviceAllList);
            }
        }
        return procDeviceAllMap;
    }

    /**
     * 删除工单
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 14:19
     * @param procBaseReq 工单参数
     * @param procBaseReqList 工单集合
     * @param isDeleted 是否删除
     */
    public void deleteProcProcess(ProcBaseReq procBaseReq, List<ProcBaseReq> procBaseReqList, String isDeleted) {
        if (!ObjectUtils.isEmpty(procBaseReqList)) {
            procBaseReq.setIsDeleted(isDeleted);
            //巡检工单编号集合
            Set<String> inspectionProcIdList = new HashSet<>();
            //销障工单编号集合
            Set<String>  clearProcIdList = new HashSet<>();
            for (ProcBaseReq procBaseReqOne : procBaseReqList) {
                if (ProcBaseConstants.PROC_INSPECTION.equals(procBaseReqOne.getProcType())) {
                    //巡检工单集合
                    inspectionProcIdList.add(procBaseReqOne.getProcId());
                } else if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBaseReqOne.getProcType())) {
                    //销障工单集合
                    clearProcIdList.add(procBaseReqOne.getProcId());
                }
            }

            if (!ObjectUtils.isEmpty(inspectionProcIdList)) {
                procBaseReq.setUpdateTime(new Date());
                procBaseReq.setUpdateUser(this.getUserId());
                procBaseReq.setProcIds(inspectionProcIdList);
                // 删除/恢复工单基础信息
                procInspectionService.updateInspectionIsDeletedByIds(procBaseReq);
            }

            if (!ObjectUtils.isEmpty(clearProcIdList)) {
                procBaseReq.setUpdateTime(new Date());
                procBaseReq.setUpdateUser(this.getUserId());
                procBaseReq.setProcIds(clearProcIdList);
                // 删除/恢复工单基础信息
                procClearFailureService.updateProcClearFailureIsDeletedByIds(procBaseReq);
            }

            // 修改工单关联信息
            procRelatedService.updateProcRelateIsDeletedBatch(procBaseReqList, isDeleted);

            // 删除/恢复工单特性信息
            procRelatedService.updateProcSpecificIsDeletedBatch(procBaseReqList, isDeleted);

            //删除关联工单超时的告警信息
            Set<String> procIdSet = procBaseReq.getProcIds();
            if (!ObjectUtils.isEmpty(procIdSet)) {
                List<String> procIdList = new ArrayList<>();
                procIdList.addAll(procIdSet);
                Result currentResult = alarmCurrentFeign.batchDeleteAlarmsFeign(procIdList);
                if (!ObjectUtils.isEmpty(currentResult)) {
                    //删除告警信息
                    if (ResultCode.SUCCESS.intValue() != currentResult.getCode()) {
                        //删除工单失败
                        throw new DeleteProcErrorException();
                    }
                } else {
                    //删除工单失败
                    throw new DeleteProcErrorException();
                }
            }

            //新增删除工单日志信息
            this.insertDeleteProcInfo(procBaseReqList);
        }
    }

    /**
     * 新增删除日志信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 15:20
     * @param procBaseReqList
     */
    public void insertDeleteProcInfo(List<ProcBaseReq> procBaseReqList) {
        // 保存删除工单操作日志
        List list = new ArrayList();
        systemLanguage.querySystemLanguage();
        if (!ObjectUtils.isEmpty(procBaseReqList)) {
            for (ProcBase procBase : procBaseReqList) {
                AddLogBean addLogBean = new AddLogBean();
                if (ProcBaseConstants.PROC_CLEAR_FAILURE.equals(procBase.getProcType())){
                    //保存销障工单日志
                    String functionCode = LogFunctionCodeConstant.DELETE_PROC_CLEAR_FAILURE;
                    addLogBean = procLogService.getAddProcOperateLogParam(procBase, functionCode, LogConstants.DATA_OPT_TYPE_DELETE);
                    list.add(addLogBean);
                } else if (ProcBaseConstants.PROC_INSPECTION.equals(procBase.getProcType())){
                    //保存巡检工单日志
                    String functionCode = LogFunctionCodeConstant.DELETE_PROC_INSPECTION_FUNCTION_CODE;
                    addLogBean = procLogService.getAddProcOperateLogParam(procBase, functionCode, LogConstants.DATA_OPT_TYPE_DELETE);
                    list.add(addLogBean);
                }
            }
        }
        procLogService.insertDeleteProcLog(list);
    }

    /*---------------------------------------获取权限公共方法end-----------------------------------------*/
}