package com.fiberhome.filink.workflowbusinessserver.service.procbase;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.*;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.*;
import com.fiberhome.filink.workflowbusinessserver.req.process.CompleteProcessInfoReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 流程主表 服务类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-02-18
 */
public interface ProcBaseService extends IService<ProcBase> {

    /**
     * 查询工单名称是否存在
     *
     * @param procId 工单id
     * @param title 工单名称
     * @return Boolean
     */
    Boolean queryTitleIsExists(String procId, String title);

    /**
     * 新增工单
     *
     * @param processInfo 工单信息汇总类
     * @return Result
     */
    Result addProcBase(ProcessInfo processInfo);

    /**
     * 新增工单
     *
     * @param processInfo 工单汇总类
     * @return Result
     */
    ProcBase addProcBaseInfo(ProcessInfo processInfo);

    /**
     * 重新生成
     * @author hedongwei@wistronits.com
     * @date  2019/5/22 23:43
     * @param processInfo
     * @return 重新生成工单
     */
    ProcBase regenerateProcProcess(ProcessInfo processInfo);


    /**
     * 重新生成工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/4 15:32
     * @param processInfo 重新生成工单
     * @return 重新生成工单信息
     */
    Result regenerateProc(ProcessInfo processInfo);

    /**
     * 修改工单
     *
     * @param processInfo 工单信息汇总类
     * @return Result
     */
    Result updateProcessById(ProcessInfo processInfo);

    /**
     * 根据工单id修改工单数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/15 14:21
     * @param procBase 工单信息
     * @param processInfo 修改工单参数
     */
    void updateProcBaseByIdProcess(ProcBase procBase, ProcessInfo processInfo);

    /**
     * 修改巡检任务状态
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 20:39
     * @param processInfo 流程类
     * @param inspectionTaskStatus 巡检任务状态
     * @return 返回修改巡检任务状态的结果
     */
    boolean updateInspectionTaskStatus(ProcessInfo processInfo, String inspectionTaskStatus);

    /**
     * 删除/恢复工单
     *
     * @param ids 删除id列表
     * @param isDeleted 逻辑删除字段
     *
     * @return Result
     */
    Result updateProcessIsDeletedByIds(List<String> ids, String isDeleted);

    /**
     * 修改流程状态信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/18 20:56
     * @param procBase 流程编号
     * @return 返回修改的结果
     */
    Result updateProcBaseStatusById(ProcBase procBase);

    /**
     * 根据id查看工单
     *
     * @param id 工单id
     * @return Result
     */
    Result queryProcessById(String id);

    /**
     * 根据工单id获取工单类型
     *
     * @param id 工单id
     * @return Result
     */
    Result getProcTypeByProcId(String id);

    /**
     * 查询巡检任务关联工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 17:11
     * @param queryCondition 查询条件
     * @return 返回巡检任务关联工单信息
     */
    Result queryListRelatedProcByInspectionTaskIdPage(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 分页查询工单未完工列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    Result queryListProcUnfinishedProcByPage(QueryCondition<ProcBaseReq> queryCondition);


    /**
     * 分页查询工单历史列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    Result queryListProcHisProcByPage(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 销障工单未完工列表状态总数统计
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    Result queryCountListProcUnfinishedProcStatus(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 工单列表状态统计
     *
     * @param procBaseReq 工单请求类
     * @return Result
     */
    Result queryCountProcByStatus(ProcBaseReq procBaseReq);

    /**
     * 设施类型统计的销障工单信息
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    Result queryListGroupByDeviceType(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 销障工单历史列表总数统计
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    Result queryCountListProcHisProc(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 工单列表分组统计
     *
     * @param procBaseReq 工单请求类
     * @return Result
     */
    Result queryCountProcByGroup(ProcBaseReq procBaseReq);

    /**
     * 根据ids查询是否有工单信息
     *
     * @since 2019-02-27
     * @param ids id列表
     * @param type 设施或区域id
     * @return Result
     *
     */
    Result queryProcExitsForIds(List<String> ids, String type);

    /**
     * 下载工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/21 21:36
     * @param req 下载工单信息
     * @return 工单结果
     */
    Result updateProcByUser(UpdateProcByUserReq req);


    /**
     * 指派工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 13:56
     * @param req 指派工单参数
     * @return 指派工单结果
     */
    Result assignProc(AssignProcReq req);


    /**
     * 工单指派修改数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/15 14:32
     * @param req 指派工单的参数
     * @param procBase 流程
     * @return 修改的工单数据
     */
    ProcessInfo assignProcProcess(AssignProcReq req, ProcBase procBase);

    /**
     * 转派工单结果
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 13:57
     * @param req 转派工单参数
     * @return 转派工单结果
     */
    Result turnProc(TurnProcReq req);


    /**
     * 转办工单修改数据库信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/15 14:11
     * @param req 转办工单
     * @param procBase 工单信息
     */
    void turnProcProcess(TurnProcReq req, ProcBase procBase);

    /**
     * 获取转派用户
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 21:30
     * @param req 转派用户请求参数
     * @return 转派用户信息
     */
    Result getTurnUser(GetTurnUserReq req);

    /**
     * 获取可转派用户过程
     * @author hedongwei@wistronits.com
     * @date  2019/4/10 11:36
     * @param procId 工单编号
     * @return 可转派用户
     */
    List<User> getTurnUserProcess(String procId);


    /**
     * 查询工单详情
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 9:50
     * @param procId 查询工单详情
     * @return procId 工单编号
     */
    ProcBase queryProcBaseById(String procId);

    /**
     * 退单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/22 15:59
     * @param req 退单参数
     * @return 退单结果
     */
    Result singleBackProc(SingleBackProcReq req);

    /**
     * 撤销单据
     * @author hedongwei@wistronits.com
     * @date  2019/3/22 15:59
     * @param req 撤销单据参数
     * @return 撤销单据结果
     */
    Result revokeProc(RevokeProcReq req);


    /**
     * 退单确认
     * @author hedongwei@wistronits.com
     * @date  2019/3/22 12:28
     * @param req 退单确认参数
     * @return 退单确认结果
     */
    Result checkSingleBack(CheckSingleBackReq req);

    /**
     * 查询关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 23:27
     * @param req 关联设施参数
     * @return 返回工单关联设施信息
     */
    Result procRelationDeviceList(ProcRelationDeviceListReq req);

    /**
     * 查询工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/25 22:19
     * @param procBase 筛选条件
     * @return 查询工单信息
     */
    List<ProcBaseInfoBean> queryProcBaseInfoList(ProcBase procBase);

    /**
     * 查询关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/27 12:56
     * @param procIds 流程编号
     * @return 返回关联设施信息
     */
    List<ProcRelatedDevice> queryProcRelateDeviceByProcIds(List<String> procIds);

    /**
     * 查询关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/27 12:56
     * @param procIds 流程编号
     * @return 返回关联部门信息
     */
    List<ProcRelatedDepartment> queryProcRelateDeptByProcIds(List<String> procIds);

    /**
     * 检验工单编号是否为空，工单是否存在
     * @author hedongwei@wistronits.com
     * @date  2019/3/22 16:22
     * @param procId 流程编号
     * @return 返回校验结果
     */
    Result checkProcId(String procId);

    /**
     * 根据编号查询工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/3 21:13
     * @param procId 流程编号
     * @return 返回工单信息
     */
    ProcBase getProcBaseByProcId(String procId);

    /**
     * 完结流程的参数
     * @author hedongwei@wistronits.com
     * @date  2019/3/22 16:15
     * @param procBase 流程参数
     * @return 获取完结流程的参数
     */
    CompleteProcessInfoReq getCompleteProcessInfoReq(ProcBase procBase);


    /**
     * 工单回单
     * @author chaofanrong@wistronits.com
     * @date  2019/4/3 17:33
     * @param procBase 流程
     * @return Integer
     */
    Integer receiptProc(ProcBase procBase);


    /**
     * 查询流程是否有没有办结的工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 12:23
     * @param procInspection 巡检工单参数
     * @return 没有办结的工单信息
     */
    List<ProcBase> selectProcBaseListByProcInspection(ProcInspection procInspection);

    /**
     * 更新告警id及工单id
     *
     * @since 2019-04-18
     * @param procRelatedAlarm 工单关联告警
     * @return Integer 处理结果
     *
     */
    Integer addProcRelatedAlarm(ProcRelatedAlarm procRelatedAlarm);

    /**
     * 当前登录用户下载工单
     *
     * @param procBaseReq 工单请求
     *
     * @return Result
     */
    Result downloadProcByLoginUserForApp(ProcBaseReq procBaseReq);

    /**
     * app工单下载
     *
     * @param procBaseReq 工单请求
     *
     * @return Result
     */
    Result downLoadProcForApp(ProcBaseReq procBaseReq);

    /**
     * 根据告警查询存在的工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/19 12:49
     * @param alarmProcList 告警编号数组
     * @return 查询告警工单结果
     */
    Map<String, Object> queryExistsProcForAlarmList(List<String> alarmProcList);

    /**
     * 根据告警查询存在的工单信息
     * @author chaofanrong@wistronits.com
     * @date  2019/4/28 23:38
     * @param procBase 工单信息
     * @param operaType 操作类型（新增或重新生成）
     *
     * @return 查询告警工单结果
     */
    Boolean queryExistsProcForAlarm(ProcBase procBase, String operaType);


    /**
     * 获取拥有权限信息（查询）
     *
     * @param queryCondition    查询对象
     * @return queryCondition    查询对象
     */
    QueryCondition<ProcBaseReq> getPermissionsInfo(QueryCondition<ProcBaseReq> queryCondition);


    /**
     * 删除工单数据
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 18:52
     * @param req 删除工单参数
     * @return 删除工单数据
     */
    Result deleteProcBaseForDeviceList(DeleteProcBaseForDeviceReq req);

    /**
     * 获取拥有权限信息（导出）
     *
     * @param queryCondition    查询对象
     * @return queryCondition    查询对象
     */
    QueryCondition<ProcBaseReq> getPermissionsInfoForExport(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 校验部门信息有无关联工单
     * @param deptIds 部门ids
     * @author chaofanrong@wistronits.com
     * @date  2019/4/26 11:03
     * @return 返回部门信息
     */
    Map<String,List<String>> queryProcIdListByDeptIds(List<String> deptIds);

    /**
     * 根据部门编号查询工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 17:08
     * @param deptIds 部门编号集合
     * @return 是否存在工单
     */
    Result existsProcForDeptIds(List<String> deptIds);

    /**
     * 查询是否存在正在办理工单的用户
     * @author hedongwei@wistronits.com
     * @date  2019/5/6 10:39
     * @param userIdList 用户编号集合
     * @return 是否存在办理工单的用户
     */
    boolean queryIsExistsAssignUser(List<String> userIdList);

    /**
     * 校验当前用户工单是否有权限
     * @author hedongwei@wistronits.com
     * @date  2019/8/7 14:21
     * @param deviceType 设施类型
     * @param areaId 区域编号
     * @param userId 用户编号
     * @return 校验当前用户工单是否有权限
     */
    boolean validateProcIsPermission(String deviceType, String areaId, String userId);

}
