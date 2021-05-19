package com.fiberhome.filink.workflowbusinessserver.controller.procinspection;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTask;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.CompleteInspectionByPageReq;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.CompleteInspectionProcReq;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.ProcInspectionReq;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionRecordService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionService;
import com.fiberhome.filink.workflowbusinessserver.utils.procbase.ProcBaseResultCode;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcInspectionConstants;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkFlowBusinessMsg;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 巡检工单表 前端控制器
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-03-11
 */
@RestController
@RequestMapping("/procInspection")
public class ProcInspectionController {

    @Autowired
    private ProcInspectionService procInspectionService;

    @Autowired
    private ProcInspectionRecordService procInspectionRecordService;

    /**
     * 巡检未完工列表
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:46
     * @param queryCondition 查询条件
     * @return 巡检未完工列表结果
     */
    @PostMapping("/queryListInspectionProcessByPage")
    public Result queryListInspectionProcessByPage(@RequestBody QueryCondition<ProcBaseReq> queryCondition) {
        return procInspectionService.queryListInspectionProcessByPage(queryCondition);
    }


    /**
     * 导出未完工巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 17:11
     * @param exportDto
     * @return 导出巡检任务信息
     */
    @PostMapping("/exportListInspectionProcess")
    public Result exportListInspectionProcess(@RequestBody ExportDto<ProcBaseReq> exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(ProcBaseResultCode.PROC_PARAM_ERROR, I18nUtils.getString(ProcBaseI18n.PROC_PARAM_ERROR));
        }
        //导出dto
        return procInspectionService.exportListInspectionProcess(exportDto);
    }

    /**
     * 巡检工单前五
     *
     * @return Result
     */
    @GetMapping("/queryProcInspectionTopFive/{deviceId}")
    public Result queryProcInspectionTopFive(@PathVariable("deviceId") String deviceId) {
        return procInspectionService.queryProcInspectionTopFive(deviceId);
    }

    /**
     * 导出已完成巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 17:11
     * @param exportDto 导出类型
     * @return 导出已完成巡检任务信息
     */
    @PostMapping("/exportListInspectionComplete")
    public Result exportListInspectionComplete(@RequestBody ExportDto<ProcBaseReq> exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(ProcBaseResultCode.PROC_PARAM_ERROR, I18nUtils.getString(ProcBaseI18n.PROC_PARAM_ERROR));
        }
        //导出dto
        return procInspectionService.exportListInspectionComplete(exportDto);
    }

    /**
     * 新增巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param addInspectionProcReq 新增巡检工单参数
     * @return 新增巡检工单结果
     */
    @PostMapping("/addInspectionProc")
    public Result addInspectionProc(@RequestBody ProcInspectionReq addInspectionProcReq) {
        //新增巡检工单
        return procInspectionService.saveProcInspection(addInspectionProcReq, ProcInspectionConstants.OPERATE_ADD, ProcInspectionConstants.NOT_IS_ALARM_VIEW_CALL);
    }

    /**
     * 告警页面调用新增巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param addInspectionProcReq 新增巡检工单参数
     * @return 新增巡检工单结果
     */
    @PostMapping("/addInspectionProcForAlarm")
    public Result addInspectionProcForAlarm(@RequestBody ProcInspectionReq addInspectionProcReq) {
        //新增巡检工单
        return procInspectionService.saveProcInspection(addInspectionProcReq, ProcInspectionConstants.OPERATE_ADD, ProcInspectionConstants.IS_ALARM_VIEW_CALL);
    }

    /**
     * 重新生成巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param addInspectionProcReq 新增巡检工单请求
     * @return 重新生成巡检工单结果
     */
    @PostMapping("/regenerateInspectionProc")
    public Result regenerateInspectionProc(@RequestBody ProcInspectionReq addInspectionProcReq) {
        //重新生成巡检工单
        return procInspectionService.regenerateInspectionProc(addInspectionProcReq, ProcInspectionConstants.OPERATE_ADD);
    }

    /**
     * 修改巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param updateInspectionProcReq 修改巡检工单请求
     * @return 修改巡检工单结果
     */
    @PostMapping("/updateInspectionProc")
    public Result updateInspectionProc(@RequestBody ProcInspectionReq updateInspectionProcReq) {
        //修改巡检工单
        return procInspectionService.saveProcInspection(updateInspectionProcReq, ProcInspectionConstants.OPERATE_UPDATE, ProcInspectionConstants.NOT_IS_ALARM_VIEW_CALL);
    }


    /**
     * 办理巡检工单请求
     * @author hedongwei@wistronits.com
     * @date  2019/3/23 12:27
     * @param completeReq 办理巡检工单请求
     * @return 返回办结巡检工单结果
    */
    @PostMapping("/completeInspectionProcForApp")
    public Result completeInspectionProcForApp(@RequestBody CompleteInspectionProcReq completeReq) {
        //办理巡检工单
        return procInspectionService.completeInspectionProc(completeReq);
    }

    /**
     * 巡检工单详情
     * @author hedongwei@wistronits.com
     * @date  2019/3/20 16:27
     * @param procId 巡检工单编号
     * @return 巡检工单详情信息
     */
    @GetMapping("/getInspectionProcById/{procId}")
    public Result getInspectionProcById(@PathVariable("procId") String procId) {
        //巡检工单详情
        return procInspectionService.getInspectionProcById(procId);
    }



    /**
     * 查询已完工巡检记录
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 16:36
     * @param queryCondition 查询对象
     * @return 查询结果
     */
    @PostMapping("/queryListInspectionCompleteRecordByPage")
    public Result queryListInspectionCompleteRecordByPage(@RequestBody QueryCondition<ProcBaseReq> queryCondition) {
        return procInspectionService.queryListInspectionCompleteRecordByPage(queryCondition);
    }

    /**
     * 查询已完成的巡检信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 16:36
     * @param req 查询已完成的巡检信息
     * @return 查询结果
     */
    @PostMapping("/queryListCompleteInspectionByPage")
    public Result queryListCompleteInspectionByPage(@RequestBody QueryCondition<CompleteInspectionByPageReq> req) {
        CompleteInspectionByPageReq bizReq = req.getBizCondition();
        if (!ObjectUtils.isEmpty(bizReq)) {
            if (StringUtils.isEmpty(bizReq.getProcId())) {
                //工单编号不能为空
                String procIdNullMsg = I18nUtils.getString(ProcBaseI18n.PROC_ID_LOSE);
                return ResultUtils.warn(ProcBaseResultCode.PROC_ID_LOSE, procIdNullMsg);
            } else {
                //查询工单信息
                return procInspectionRecordService.queryInspectionRecordByCondition(req);
            }
        } else {
            //参数错误
            return WorkFlowBusinessMsg.paramErrorMsg();
        }
    }

    /**
     * 获取巡检工单设施数量
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 16:44
     * @param procInspection
     */
    @PostMapping("/queryProcInspectionByProcInspectionId")
    public Result queryProcInspectionByProcInspectionId(@RequestBody ProcInspection procInspection) {
        return procInspectionService.queryProcInspectionByProcInspectionId(procInspection);
    }

    /**
     * 巡检任务关联工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 20:03
     * @param req 查询巡检任务关联工单信息
     */
    @PostMapping("/queryListInspectionTaskRelationProcByPage")
    public Result queryListInspectionTaskRelationProcByPage(@RequestBody QueryCondition<ProcBaseReq> req) {
        ProcBaseReq bizReq = req.getBizCondition();
        if (!ObjectUtils.isEmpty(bizReq)) {
            if (StringUtils.isEmpty(bizReq.getInspectionTaskId())) {
                return WorkFlowBusinessMsg.paramErrorMsg();
            } else {
                //查询巡检任务关联工单信息
                return procInspectionService.queryListInspectionTaskRelationProcByPage(req);
            }
        } else {
            //参数错误
            return WorkFlowBusinessMsg.paramErrorMsg();
        }
    }

    /**
     * 巡检任务新增巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/16 19:53
     * @param inspectionTask 新增巡检工单参数
     * @return 返回任务生成巡检任务
     */
    @PostMapping("/jobAddInspectionProc")
    public Result jobAddInspectionProc(@RequestBody InspectionTask inspectionTask) {
        procInspectionService.jobAddInspectionProc(inspectionTask);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(ProcBaseI18n.ADD_PROC_SUCCESS));
    }
}
