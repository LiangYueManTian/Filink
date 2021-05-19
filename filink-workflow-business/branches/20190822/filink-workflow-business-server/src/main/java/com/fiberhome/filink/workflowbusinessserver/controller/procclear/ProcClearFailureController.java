package com.fiberhome.filink.workflowbusinessserver.controller.procclear;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBaseI18n;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkflowBusinessResultCode;
import com.fiberhome.filink.workflowbusinessserver.req.app.procclear.ReceiptClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.req.procclear.InsertClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.req.procclear.UpdateClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.service.procclear.ProcClearFailureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 销障工单表 前端控制器
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-02-15
 */
@RestController
@RequestMapping("/procClearFailure")
public class ProcClearFailureController {

    @Autowired
    private ProcClearFailureService procClearFailureService;

    /**
     * 新增销障工单
     *
     * @param insertClearFailureReq 新增销障工单请求类
     * @return Result
     */
    @PostMapping("/addClearFailureProc")
    public Result addClearFailureProc(@RequestBody InsertClearFailureReq insertClearFailureReq) throws Exception {
        return procClearFailureService.addClearFailureProc(insertClearFailureReq);
    }

    /**
     * 修改销障工单
     *
     * @param updateClearFailureReq 修改销障工单请求类
     * @return Result
     */
    @PostMapping("/updateClearFailureProcById")
    public Result updateClearFailureProc(@RequestBody UpdateClearFailureReq updateClearFailureReq) throws Exception{
        return procClearFailureService.updateClearFailureProcById(updateClearFailureReq);
    }

    /**
     * 分页查询销障工单未完工列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @PostMapping("/queryListClearFailureProcessByPage")
    public Result queryListClearFailureProcessByPage(@RequestBody QueryCondition<ProcBaseReq> queryCondition) {
        return procClearFailureService.queryListClearFailureUnfinishedProcByPage(queryCondition);
    }

    /**
     * 分页查询销障工单历史列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    @PostMapping("/queryListHistoryClearFailureByPage")
    public Result queryListHistoryClearFailureByPage(@RequestBody QueryCondition<ProcBaseReq> queryCondition) {
        return procClearFailureService.queryListClearFailureHisProcByPage(queryCondition);
    }

    /**
     * 销障工单列表待指派状态统计
     *
     * @return Result
     */
    @PostMapping("/queryCountClearFailureProcByAssigned")
    public Result queryCountClearFailureProcByAssigned() {
        return procClearFailureService.queryCountClearFailureProcByAssigned();
    }

    /**
     * 销障工单列表待处理状态统计
     *
     * @return Result
     */
    @PostMapping("/queryCountClearFailureProcByPending")
    public Result queryCountClearFailureProcByPending() {
        return procClearFailureService.queryCountClearFailureProcByPending();
    }

    /**
     * 销障工单列表处理中状态统计
     *
     * @return Result
     */
    @PostMapping("/queryCountClearFailureProcByProcessing")
    public Result queryCountClearFailureProcByProcessing() {
        return procClearFailureService.queryCountClearFailureProcByProcessing();
    }

    /**
     * 销障工单列表今日新增统计
     *
     * @return Result
     */
    @PostMapping("/queryCountClearFailureProcByToday")
    public Result queryCountClearFailureProcByToday() {
        return procClearFailureService.queryCountClearFailureProcByToday();
    }

    /**
     * 销障工单未完工列表状态总数统计
     *
     * @return Result
     */
    @PostMapping("/queryCountListProcUnfinishedProcStatus")
    public Result queryCountListProcUnfinishedProcStatus() {
        return procClearFailureService.queryCountListProcUnfinishedProcStatus();
    }

    /**
     * 故障原因统计的销障工单信息
     *
     * @return Result
     */
    @PostMapping("/queryListClearFailureGroupByErrorReason")
    public Result queryListClearFailureGroupByErrorReason() {
        return procClearFailureService.queryListClearFailureGroupByErrorReason();
    }

    /**
     * 处理方案统计的销障工单信息
     *
     * @return Result
     */
    @PostMapping("/queryListClearFailureGroupByProcessingScheme")
    public Result queryListClearFailureGroupByProcessingScheme() {
        return procClearFailureService.queryListClearFailureGroupByProcessingScheme();
    }

    /**
     * 设施类型统计的销障工单信息
     *
     * @return Result
     */
    @PostMapping("/queryListClearFailureGroupByDeviceType")
    public Result queryListClearFailureGroupByDeviceType() {
        return procClearFailureService.queryListClearFailureGroupByDeviceType();
    }

    /**
     * 工单状态统计的销障工单信息
     *
     * @return Result
     */
    @PostMapping("/queryListClearFailureByStatus")
    public Result queryListClearFailureByStatus() {
        return procClearFailureService.queryListClearFailureByStatus();
    }

    /**
     * 销障工单历史列表总数统计
     *
     * @return Result
     */
    @PostMapping("/queryCountListProcHisProc")
    public Result queryCountListProcHisProc() {
        return procClearFailureService.queryCountListProcHisProc();
    }

    /**
     * 销障工单未完工列表导出
     *
     * @param exportDto 销障工单未完工列表导出请求
     *
     * @return Result
     */
    @PostMapping("/exportClearFailureProcUnfinished")
    public Result exportClearFailureProcUnfinished(@RequestBody ExportDto<ProcBaseReq> exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(WorkflowBusinessResultCode.PROC_PARAM_ERROR, I18nUtils.getSystemString(ProcBaseI18n.PROC_PARAM_ERROR));
        }
        return procClearFailureService.exportClearFailureProcUnfinished(exportDto);
    }

    /**
     * 销障工单历史列表导出
     *
     * @param exportDto 销障工单历史列表导出请求
     *
     * @return Result
     */
    @PostMapping("/exportClearFailureProcHistory")
    public Result exportClearFailureProcHistory(@RequestBody ExportDto<ProcBaseReq> exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(WorkflowBusinessResultCode.PROC_PARAM_ERROR, I18nUtils.getSystemString(ProcBaseI18n.PROC_PARAM_ERROR));
        }
        return procClearFailureService.exportClearFailureProcHistory(exportDto);
    }

    /**
     * 销障工单前五
     *
     * @return Result
     */
    @GetMapping("/queryClearFailureProcTopFive/{deviceId}")
    public Result queryClearFailureProcTopFive(@PathVariable("deviceId") String deviceId) {
        return procClearFailureService.queryClearFailureProcTopFive(deviceId);
    }

    /**
     * 重新生成销障工单
     * @author chaofanrong@wistronits.com
     * @date  2019/4/16 18:00
     * @param insertClearFailureReq 新增销障工单请求
     * @return 重新生成销障工单结果
     */
    @PostMapping("/regenerateClearFailureProc")
    public Result regenerateClearFailureProc(@RequestBody InsertClearFailureReq insertClearFailureReq) throws Exception {
        //重新生成销障工单
        return procClearFailureService.regenerateClearFailureProc(insertClearFailureReq);
    }

    /**
     * app销障工单回单
     *
     * @param receiptClearFailureReq 销障工单回单请求
     * @return Result
     */
    @PostMapping("/receiptClearFailureProcForApp")
    public Result receiptClearFailureProcForApp(@RequestBody ReceiptClearFailureReq receiptClearFailureReq) {
        return procClearFailureService.receiptClearFailureProcForApp(receiptClearFailureReq);
    }

}
