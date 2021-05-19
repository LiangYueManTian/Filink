package com.fiberhome.filink.workflowbusinessserver.controller.procbase;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.*;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.*;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcBaseService;
import com.fiberhome.filink.workflowbusinessserver.service.procclear.ProcClearFailureService;
import com.fiberhome.filink.workflowbusinessserver.service.procnotice.ProcNoticeService;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.utils.procbase.ProcBaseResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 流程主表 前端控制器
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-02-18
 */
@RestController
@RequestMapping("/procBase")
public class ProcBaseController {

    @Autowired
    private ProcBaseService procBaseService;

    @Autowired
    private ProcNoticeService procNoticeService;

    @Autowired
    private ProcClearFailureService procClearFailureService;

    @GetMapping("/getWorkTime")
    public Object getWorkTime(){
        return PerformaceTest.workTimeMap;
    }

    @GetMapping("/resetWorkTime")
    public String resetWorkTime(){
        PerformaceTest.workTimeMap = new HashMap<>();
        return "reset success";
    }

    /**
     * 校验工单名称是否重复
     *
     * @param procBase 工单id和工单名称
     * @return Result
     */
    @PostMapping("/queryTitleIsExists")
    public Result queryTitleIsExists(@RequestBody ProcBase procBase) {
        if (StringUtils.isEmpty(procBase.getTitle())) {
            return ResultUtils.warn(ProcBaseResultCode.PROC_PARAM_ERROR, I18nUtils.getSystemString(ProcBaseI18n.PROC_NAME_NULL));
        }
        //新增及修改校验
        boolean isExist = procBaseService.queryTitleIsExists(procBase.getProcId(), procBase.getTitle());
        if (isExist) {
            return ResultUtils.warn(ProcBaseResultCode.PROC_PARAM_ERROR, I18nUtils.getSystemString(ProcBaseI18n.PROC_NAME_SAME));
        }
        return ResultUtils.success(ProcBaseResultCode.SUCCESS, I18nUtils.getSystemString(ProcBaseI18n.PROC_NAME_AVAILABLE));
    }

    /**
     * 删除工单
     *
     * @param ids 工单id列表
     * @return Result
     */
    @PostMapping("/deleteProc")
    public Result deleteProc(@RequestBody List<String> ids){
        return procBaseService.updateProcessIsDeletedByIds(ids,ProcBaseConstants.IS_DELETED_1);
    }

    /**
     * 恢复工单
     *
     * @param ids 工单id列表
     * @return Result
     */
    @PostMapping("/recoverProc")
    public Result recoverProc(@RequestBody List<String> ids){
        return procBaseService.updateProcessIsDeletedByIds(ids,ProcBaseConstants.IS_DELETED_0);
    }

    /**
     * 修改流程状态信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/18 20:56
     * @param procBase 流程编号
     * @return 返回修改的结果
     */
    @PutMapping("/updateProcBaseStatusById")
    public Result updateProcBaseStatusById(@RequestBody ProcBase procBase) {
        return procBaseService.updateProcBaseStatusById(procBase);
    }

    /**
     * 查看工单
     *
     * @param id 工单id
     * @return Result
     */
    @GetMapping("/getProcessByProcId/{id}")
    public Result getProcessByProcId(@PathVariable String id){
        return procBaseService.queryProcessById(id);
    }

    /**
     * 根据工单id获取工单类型
     *
     * @param id 工单id
     * @return Result
     */
    @GetMapping("/getProcTypeByProcId/{id}")
    public Result getProcTypeByProcId(@PathVariable String id){
        return procBaseService.getProcTypeByProcId(id);
    }


    /**
     * 查询设施ids是否有工单信息
     *
     * @since 2019-02-27
     * @param deviceIds 设施id列表
     * @return Result
     *
     */
    @PostMapping("/queryProcExitsForDeviceIds")
    public Result queryProcExitsForDeviceIds(@RequestBody List<String> deviceIds){
        return procBaseService.queryProcExitsForIds(deviceIds, ProcBaseConstants.DEVICE_IDS);
    }

    /**
     * 查询区域ids是否有工单信息
     *
     * @since 2019-02-27
     * @param areaIds 区域id列表
     * @return Result
     *
     */
    @PostMapping("/queryProcExitsForAreaIds")
    public Result queryProcExitsForAreaIds(@RequestBody List<String> areaIds){
        return procBaseService.queryProcExitsForIds(areaIds, ProcBaseConstants.AREA_IDS);
    }


    /**
     * 下载工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/21 21:27
     * @param req 下载工单参数
     * @return 下载工单结果
     */
    @PutMapping("/updateProcByUserForApp")
    public Result updateProcByUserForApp(@RequestBody UpdateProcByUserReq req) {
        //下载工单
        return procBaseService.updateProcByUser(req);
    }

    /**
     * 通知下载人员
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 9:37
     * @param noticeDownloadUserReq 提醒下载用户参数
     * @return 通知下载人员
     */
    @PostMapping("noticeDownloadUser")
    public Result noticeDownloadUser(@RequestBody  NoticeDownloadUserReq noticeDownloadUserReq) {
        //通知下载人员
        return procNoticeService.noticeDownloadUser(noticeDownloadUserReq);
    }



    /**
     * 工单指派
     * @author hedongwei@wistronits.com
     * @date  2019/3/27 20:35
     * @param req 指派的参数
     * @return 指派工单结果
     */
    @PostMapping("/assignProc")
    public Result assignProc(@RequestBody AssignProcReq req) {
        //指派工单
        return procBaseService.assignProc(req);
    }

    /**
     * 转派工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 23:21
     * @param req 转派流程参数
     * @return 转派工单
     */
    @PostMapping("/turnProcForApp")
    public Result turnProcForApp(@RequestBody TurnProcReq req) {
        //转派工单
        return procBaseService.turnProc(req);
    }

    /**
     * 退单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/22 15:59
     * @param req 退单参数
     * @return 退单结果
     */
    @PostMapping("/singleBackProcForApp")
    public Result singleBackProcForApp(@RequestBody SingleBackProcReq req) {
        //退单
        return procBaseService.singleBackProc(req);
    }

    /**
     * 撤销单据
     * @author hedongwei@wistronits.com
     * @date  2019/3/22 15:59
     * @param req 撤销单据参数
     * @return 撤销单据结果
     */
    @PostMapping("/revokeProc")
    public Result revokeProc(@RequestBody RevokeProcReq req) {
        //撤销单据
        return procBaseService.revokeProc(req);
    }

    /**
     * 退单确认
     * @author hedongwei@wistronits.com
     * @date  2019/3/22 12:28
     * @param req 退单确认参数
     * @return 退单确认结果
     */
    @PostMapping("/checkSingleBack")
    public Result checkSingleBack(@RequestBody CheckSingleBackReq req) {
        //退单确认
        return procBaseService.checkSingleBack(req);
    }

    /**
     * 获取转派用户
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 21:30
     * @param req 转派用户请求参数
     * @return 转派用户信息
     */
    @PostMapping("/getTurnUserForApp")
    public Result getTurnUserForApp(@RequestBody GetTurnUserReq req) {
        //获取转派人员
        return procBaseService.getTurnUser(req);
    }


    /**
     * 工单关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 23:23
     */
    @PostMapping("/procRelationDeviceList")
    public Result procRelationDeviceList(@RequestBody ProcRelationDeviceListReq req) {
        //工单关联设施信息
        return procBaseService.procRelationDeviceList(req);
    }

    /**
     * 查询工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/25 22:32
     * @param procBase 筛选条件
     * @return 查询工单信息
     */
    @PostMapping("/queryProcBaseInfoList")
    public List<ProcBaseInfoBean> queryProcBaseInfoList(@RequestBody ProcBase procBase) {
        //查询流程信息
        return procBaseService.queryProcBaseInfoList(procBase);
    }

    /**
     * 查询关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/27 12:56
     * @param procIds 流程编号
     * @return 返回关联设施信息
     */
    @PostMapping("/queryProcRelateDeviceByProcIds")
    public List<ProcRelatedDevice> queryProcRelateDeviceByProcIds(@RequestBody List<String> procIds) {
        //查询关联设施信息
        return procBaseService.queryProcRelateDeviceByProcIds(procIds);
    }

    /**
     * 查询关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/27 12:56
     * @param procIds 流程编号
     * @return 返回关联部门信息
     */
    @PostMapping("/queryProcRelateDeptByProcIds")
    public List<ProcRelatedDepartment> queryProcRelateDeptByProcIds(@RequestBody List<String> procIds) {
        return procBaseService.queryProcRelateDeptByProcIds(procIds);
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
    @PostMapping("/addProcRelatedAlarm")
    public Integer addProcRelatedAlarm(@RequestBody ProcRelatedAlarm procRelatedAlarm){
        return procBaseService.addProcRelatedAlarm(procRelatedAlarm);
    }


    /**
     * app工单下载
     *
     * @param procBaseReq 工单请求
     *
     * @return Result
     */
    @PostMapping("/downLoadProcForApp")
    public Result downLoadProcForApp(@RequestBody ProcBaseReq procBaseReq) {
        return procBaseService.downLoadProcForApp(procBaseReq);
    }


    /**
     * 根据告警查询存在的工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/19 12:49
     * @param alarmProcList 告警编号数组
     * @return 工单结果
     */
    @PostMapping("/queryExistsProcForAlarmList")
    public Map<String, Object> queryExistsProcForAlarmList(@RequestBody List<String> alarmProcList) {
        if (!StringUtils.isEmpty(alarmProcList)) {
            return procClearFailureService.queryExistsProcClearForAlarmList(alarmProcList);
        } else {
            return null;
        }
    }

    /**
     * 删除工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 17:45
     * @param req 设施编号集合
     * @return 删除工单信息
     */
    @PostMapping("/deleteProcBaseForDeviceList")
    public Result deleteProcBaseForDeviceList(@RequestBody DeleteProcBaseForDeviceReq req) {
        //调用删除工单的方法
        return procBaseService.deleteProcBaseForDeviceList(req);
    }




    /**
     * 校验部门信息有无关联工单
     * @author chaofanrong@wistronits.com
     * @param deptIds 部门ids
     *
     * @date  2019/4/26 11:03
     */
    @PostMapping("/queryProcIdListByDeptIds")
    public Object queryProcIdListByDeptIds(@RequestBody List<String> deptIds) {
        return procBaseService.queryProcIdListByDeptIds(deptIds);
    }

    /**
     * 查询是否存在正在办理工单的用户
     * @author hedongwei@wistronits.com
     * @date  2019/5/6 10:39
     * @param userIdList 用户编号集合
     * @return 是否存在办理工单的用户
     */
    @PostMapping("/queryIsExistsAssignUser")
    public boolean queryIsExistsAssignUser(@RequestBody List<String> userIdList) {
        return procBaseService.queryIsExistsAssignUser(userIdList);
    }


}
