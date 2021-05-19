package com.fiberhome.filink.workflowbusinessserver.controller.inspectiontask;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTask;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskI18n;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontask.*;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionService;
import com.fiberhome.filink.workflowbusinessserver.constant.InspectionTaskConstants;
import com.fiberhome.filink.workflowbusinessserver.utils.inspectiontask.InspectionTaskMsg;
import com.fiberhome.filink.workflowbusinessserver.utils.inspectiontask.InspectionTaskResultCode;
import com.fiberhome.filink.workflowbusinessserver.utils.procbase.ProcBaseResultCode;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import com.fiberhome.filink.workflowbusinessserver.utils.workflowbusiness.WorkFlowBusinessMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 巡检任务表 前端控制器
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-26
 */
@RestController
@RequestMapping("/inspectionTask")
@Slf4j
public class InspectionTaskController {

    @Autowired
    private InspectionTaskService inspectionTaskService;

    @Autowired
    private ProcInspectionService procInspectionService;

    /**
     * 新增巡检任务
     * @author hedongwei@wistronits.com
     * @date 2019/2/26 14:27
     * @param req 新增巡检任务参数
     */
    @PostMapping("/insertInspectionTask")
    public Result insertInspectionTask(@RequestBody InsertInspectionTaskReq req) {
        Result result = inspectionTaskService.insertInspectionTask(req);
        //新增巡检工单
        if (0 == result.getCode()) {
            procInspectionService.manualAddInspectionProc(req.getInspectionTaskId());
        }
        return result;
    }


    /**
     * 修改巡检任务
     *
     * @param req 修改巡检任务参数
     * @author hedongwei@wistronits.com
     * @date 2019/2/26 14:27
     */
    @PutMapping("/updateInspectionTask")
    public Result updateInspectionTask(@RequestBody UpdateInspectionTaskReq req) {
        Result result = inspectionTaskService.updateInspectionTask(req);
        //新增巡检工单
        if (0 == result.getCode()) {
            procInspectionService.manualAddInspectionProc(req.getInspectionTaskId());
        }
        return result;
    }

    /**
     * 修改巡检任务状态
     *
     * @param req 修改巡检任务状态
     * @author hedongwei@wistronits.com
     * @date 2019/3/15 19:16
     */
    @PutMapping("/updateInspectionStatus")
    public Result updateInspectionStatus(@RequestBody UpdateInspectionStatusReq req) {
        //修改巡检任务状态
        return inspectionTaskService.updateInspectionStatus(req);
    }


    /**
     * 根据设施集合删除巡检任务
     *
     * @param req 删除巡检任务参数
     * @return 删除巡检任务
     * @author hedongwei@wistronits.com
     * @date 2019/4/26 10:10
     */
    @PostMapping("/deleteInspectionTaskForDeviceList")
    public Result deleteInspectionTaskForDeviceList(@RequestBody DeleteInspectionTaskForDeviceReq req) {
        //删除巡检任务
        return inspectionTaskService.deleteInspectionTaskForDeviceList(req);
    }

    /**
     * 删除巡检任务
     *
     * @param req 删除巡检任务参数
     * @author hedongwei@wistronits.com
     * @date 2019/3/4 14:12
     */
    @PostMapping("/deleteInspectionTaskByIds")
    public Result deleteInspectionTaskByIds(@RequestBody DeleteInspectionTaskByIdsReq req) {
        if (StringUtils.isEmpty(req.getIsDeleted())) {
            req.setIsDeleted(WorkFlowBusinessConstants.IS_DELETED);
        }
        return inspectionTaskService.deleteInspectionTaskByIds(req);
    }

    /**
     * 巡检任务关联设施
     *
     * @return 巡检任务关联设施
     * @author hedongwei@wistronits.com
     * @date 2019/3/28 23:23
     */
    @PostMapping("/inspectionTaskRelationDeviceList")
    public Result inspectionTaskRelationDeviceList(@RequestBody InspectionTaskRelationDeviceListReq req) {
        //巡检任务关联设施信息
        return inspectionTaskService.inspectionTaskRelationDeviceList(req);
    }


    /**
     * 批量开启巡检任务
     *
     * @param req 批量开启巡检任务参数
     * @author hedongwei@wistronits.com
     * @date 2019/3/6 16:43
     */
    @PostMapping("/openInspectionTaskBatch")
    public Result openInspectionTaskBatch(@RequestBody OpenInspectionTaskBatchReq req) {
        //巡检任务编号集合
        List<String> openInspectionTaskIds = req.getInspectionTaskIds();
        //检查巡检任务参数
        String retCheckFlag = this.checkInspectionTaskList(openInspectionTaskIds, InspectionTaskConstants.IS_OPEN);
        if (InspectionTaskI18n.PARAM_ERROR.equals(retCheckFlag)) {
            //参数异常提示
            return WorkFlowBusinessMsg.paramErrorMsg();
        } else if (InspectionTaskI18n.EXISTS_OPEN_DATA.equals(retCheckFlag)) {
            //存在开启的巡检任务
            return InspectionTaskMsg.existsOpenDataMsg();
        }
        //开启巡检任务
        return inspectionTaskService.openInspectionTaskBatch(req);
    }

    /**
     * 批量关闭巡检任务
     *
     * @param req 批量关闭巡检任务参数
     * @author hedongwei@wistronits.com
     * @date 2019/3/6 16:43
     */
    @PostMapping("/closeInspectionTaskBatch")
    public Result closeInspectionTaskBatch(@RequestBody CloseInspectionTaskBatchReq req) {
        //巡检任务编号集合
        List<String> openInspectionTaskIds = req.getInspectionTaskIds();
        //检查巡检任务参数
        String retCheckFlag = this.checkInspectionTaskList(openInspectionTaskIds, InspectionTaskConstants.IS_CLOSE);
        if (InspectionTaskI18n.PARAM_ERROR.equals(retCheckFlag)) {
            //参数异常提示
            return WorkFlowBusinessMsg.paramErrorMsg();
        } else if (InspectionTaskI18n.EXISTS_CLOSE_DATA.equals(retCheckFlag)) {
            //存在关闭的巡检任务提示
            return InspectionTaskMsg.existsCloseDataMsg();
        }
        //关闭巡检任务
        return inspectionTaskService.closeInspectionTaskBatch(req);
    }

    /**
     * 检查巡检任务集合是否满足条件
     *
     * @param inspectionTaskIds 巡检任务编号
     * @return boolean 返回巡检任务编号
     * @author hedongwei@wistronits.com
     * @date 2019/3/6 17:14
     */
    public String checkInspectionTaskList(List<String> inspectionTaskIds, String operator) {
        List<InspectionTask> inspectionTasks = inspectionTaskService.selectInspectionTaskForInspectionTaskIds(inspectionTaskIds);
        //批量开启巡检任务参数
        if (null != inspectionTasks && 0 < inspectionTasks.size()) {
            for (InspectionTask inspectionTask : inspectionTasks) {
                if (null != operator && !"".equals(operator)) {
                    //操作不等于启用状态
                    if (operator.equals(inspectionTask.getIsOpen())) {
                        if (InspectionTaskConstants.IS_OPEN.equals(operator)) {
                            //已经开启
                            return InspectionTaskI18n.EXISTS_OPEN_DATA;
                        } else if (InspectionTaskConstants.IS_CLOSE.equals(operator)) {
                            //已经关闭
                            return InspectionTaskI18n.EXISTS_CLOSE_DATA;
                        }
                    }
                } else {
                    return InspectionTaskI18n.PARAM_ERROR;
                }
            }
        } else {
            return InspectionTaskI18n.PARAM_ERROR;
        }
        return "";
    }


    /**
     * 查询巡检任务
     *
     * @param queryCondition 查询巡检任务
     * @return 返回巡检任务列表
     * @author hedongwei@wistronits.com
     * @date 2019/3/4 14:18
     */
    @PostMapping("/queryListInspectionTaskByPage")
    public Result queryListInspectionTaskByPage(@RequestBody QueryCondition<QueryListInspectionTaskByPageReq> queryCondition) {
        //查询巡检任务信息
        return inspectionTaskService.queryListInspectionTaskByPage(queryCondition);
    }


    /**
     * 导出巡检任务数据
     *
     * @param exportDto 导出dto
     * @return Result 导出数据
     * @author hedongwei@wistronits.com
     * @date 2019/4/11 20:33
     */
    @PostMapping("/exportInspectionTask")
    public Result exportInspectionTask(@RequestBody ExportDto<QueryListInspectionTaskByPageReq> exportDto) {
        //导出巡检任务列表
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(ProcBaseResultCode.PROC_PARAM_ERROR, I18nUtils.getSystemString(ProcBaseI18n.PROC_PARAM_ERROR));
        }
        //导出dto
        return inspectionTaskService.exportInspectionTask(exportDto);
    }


    /**
     * 查询巡检任务详情
     *
     * @param id 巡检任务编号
     * @author hedongwei@wistronits.com
     * @date 2019/3/6 10:48
     */
    @GetMapping("getInspectionTaskById/{id}")
    public Result getInspectionTaskById(@PathVariable("id") String id) {
        //判断巡检任务编号是否为空
        if (null != this.checkInspectionTaskId(id)) {
            return this.checkInspectionTaskId(id);
        }
        //查询巡检任务详情
        return inspectionTaskService.getInspectionTaskById(id);
    }

    /**
     * 查询巡检任务详情
     *
     * @param id 巡检任务编号
     * @author hedongwei@wistronits.com
     * @date 2019/3/6 10:48
     */
    @GetMapping("getInspectionTaskDetail/{id}")
    public Result getInspectionTaskDetail(@PathVariable("id") String id) {
        //判断巡检任务编号是否为空
        if (null != this.checkInspectionTaskId(id)) {
            return this.checkInspectionTaskId(id);
        }
        //巡检任务详情
        return inspectionTaskService.getInspectionTaskDetail(id);
    }

    /**
     * 检查巡检任务编号
     *
     * @param id 巡检任务编号
     * @return Result 返回提示信息
     * @author hedongwei@wistronits.com
     * @date 2019/3/10 21:43
     */
    public Result checkInspectionTaskId(String id) {
        //判断巡检任务编号是否为空
        if (null == id || StringUtils.isEmpty(id)) {
            //参数异常i18N的名称
            String paramError = I18nUtils.getSystemString(InspectionTaskI18n.PARAM_ERROR);
            //参数异常的返回code
            Integer paramErrorResultCode = InspectionTaskResultCode.PARAM_ERROR;
            //不满足参数条件返回提示信息
            return ResultUtils.warn(paramErrorResultCode, paramError);
        }
        return null;
    }


    /**
     * 校验部门信息有无关联巡检任务
     *
     * @param deptIds 部门ids
     * @author hedongwei@wistronits.com
     * @date 2019/4/26 11:03
     */
    @PostMapping("/queryInspectionTaskListByDeptIds")
    public Object queryInspectionTaskListByDeptIds(@RequestBody List<String> deptIds) {
        Map<String, List<String>> deptList = null;
        if (!ObjectUtils.isEmpty(deptIds)) {
            deptList = inspectionTaskService.queryInspectionTaskListByDeptIds(deptIds);
        }
        //校验部门信息
        return deptList;
    }


    /**
     * 校验巡检任务名称是否重复
     *
     * @param inspectionTask 巡检任务id和巡检任务名称
     * @return Result
     */
    @PostMapping("/queryInspectionTaskIsExists")
    public Result queryInspectionTaskIsExists(@RequestBody InspectionTask inspectionTask) {
        if (StringUtils.isEmpty(inspectionTask.getInspectionTaskName())) {
            return ResultUtils.warn(InspectionTaskResultCode.PARAM_ERROR, I18nUtils.getSystemString(InspectionTaskI18n.PARAM_ERROR));
        }
        //新增及修改校验
        boolean isExist = inspectionTaskService.returnInspectionTaskNameResult(inspectionTask);
        if (!isExist) {
            //提示巡检任务名称重复
            return InspectionTaskMsg.getInspectionNameIsRepectMsg();
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(InspectionTaskI18n.INSPECTION_NAME_IS_AVAILABLE));
    }


}
