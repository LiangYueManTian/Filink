package com.fiberhome.filink.workflowbusinessapi.api.procbase;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.workflowbusinessapi.bean.procbase.*;
import com.fiberhome.filink.workflowbusinessapi.fallback.procbase.ProcBaseFeignFallback;
import com.fiberhome.filink.workflowbusinessapi.req.procbase.DeleteProcBaseForDeviceReq;
import com.fiberhome.filink.workflowbusinessapi.req.procbase.NoticeDownloadUserReq;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author hedongwei@wistronits.com
 * ProcBaseFeign接口
 * 13:41 2019/1/19
 */
@FeignClient(name = "filink-workflow-business-server" , path = "/procBase",fallback = ProcBaseFeignFallback.class)
public interface ProcBaseFeign {

    /**
     * 根据工单编号修改工单的状态
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param procBase 修改工单的类型
     * @return 修改工单的状态
     */
    @PutMapping("/updateProcBaseStatusById")
    Result updateProcBaseStatusById(@RequestBody ProcBase procBase);

    /**
     * 删除工单
     *
     * @param procBases 工单信息列表
     * @return Result
     */
    @PutMapping("/deleteProc")
    Result deleteProc(@RequestBody List<ProcBase> procBases);

    /**
     * 恢复工单
     *
     * @param procBases 工单信息列表
     * @return Result
     */
    @PutMapping("/recoverProc")
    Result recoverProc(@RequestBody List<ProcBase> procBases);

    /**
     * 查询流程信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/25 22:32
     * @param procBase 筛选条件
     * @return 查询流程信息
     */
    @PostMapping("/queryProcBaseInfoList")
    @ResponseBody List<ProcBaseInfoBean> queryProcBaseInfoList(@RequestBody ProcBase procBase);

    /**
     * 查询关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/27 12:56
     * @param procIds 流程编号
     * @return 返回关联设施信息
     */
    @PostMapping("/queryProcRelateDeviceByProcIds")
    List<ProcRelatedDevice> queryProcRelateDeviceByProcIds(@RequestBody List<String> procIds);

    /**
     * 查询关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/27 12:56
     * @param procIds 流程编号
     * @return 返回关联部门信息
     */
    @PostMapping("/queryProcRelateDeptByProcIds")
    List<ProcRelatedDepartment> queryProcRelateDeptByProcIds(@RequestBody List<String> procIds);

    /**
     * 查询设施ids是否有工单信息
     *
     * @since 2019-02-27
     * @param deviceIds 设施id列表
     * @return Result
     *
     */
    @PostMapping("/queryProcExitsForDeviceIds")
    Result queryProcExitsForDeviceIds(@RequestBody List<String> deviceIds);

    /**
     * 查询区域ids是否有工单信息
     *
     * @since 2019-02-27
     * @param areaIds 区域id列表
     * @return Result
     *
     */
    @PostMapping("/queryProcExitsForAreaIds")
    Result queryProcExitsForAreaIds(@RequestBody List<String> areaIds);


    /**
     * 通知下载人员
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 9:37
     * @param noticeDownloadUserReq 提醒下载用户参数
     * @return 通知下载人员
     */
    @PostMapping("/noticeDownloadUser")
    Result noticeDownloadUser(@RequestBody NoticeDownloadUserReq noticeDownloadUserReq);

    /**
     *  更新告警id及工单id
     *
     * @since 2019-04-18
     * @param procRelatedAlarm 工单关联告警
     *
     * @return Result
     *
     */
    @PostMapping("/addProcRelatedAlarm")
    Integer addProcRelatedAlarm(@RequestBody ProcRelatedAlarm procRelatedAlarm);

    /**
     * 根据告警查询存在的工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/19 12:49
     * @param alarmProcList 告警编号数组
     * @return 工单结果
     */
    @PostMapping("/queryExistsProcForAlarmList")
    Map<String, Object> queryExistsProcForAlarmList(@RequestBody List<String> alarmProcList);



    /**
     * 删除工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 17:45
     * @param req 设施编号集合
     * @return 删除工单信息
     */
    @PostMapping("/deleteProcBaseForDeviceList")
    Result deleteProcBaseForDeviceList(@RequestBody DeleteProcBaseForDeviceReq req);

    /**
     * 校验部门信息有无关联工单
     * @author chaofanrong@wistronits.com
     * @param deptIds 部门ids
     *
     * @date  2019/4/26 11:52
     *
     * @return 部门关联工单id
     */
    @PostMapping("/queryProcIdListByDeptIds")
    Object queryProcIdListByDeptIds(@RequestBody List<String> deptIds);


    /**
     * 查询是否存在正在办理工单的用户
     * @author hedongwei@wistronits.com
     * @date  2019/5/6 10:39
     * @param userIdList 用户编号集合
     * @return 是否存在办理工单的用户
     */
    @PostMapping("/queryIsExistsAssignUser")
    boolean queryIsExistsAssignUser(@RequestBody List<String> userIdList);
}
