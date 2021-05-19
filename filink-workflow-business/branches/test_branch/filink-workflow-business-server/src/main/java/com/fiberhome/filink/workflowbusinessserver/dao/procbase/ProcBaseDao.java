package com.fiberhome.filink.workflowbusinessserver.dao.procbase;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.*;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.procrelated.LogicDeleteRelatedDeviceBatch;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.procrelated.ProcRelatedDeviceListForDeviceIdsReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 流程主表 Mapper 接口
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-02-18
 */
public interface ProcBaseDao extends BaseMapper<ProcBase> {

    /**
     * 工单关联设施集合
     * @author hedongwei@wistronits.com
     * @date  2019/3/26 17:50
     * @param list 工单编号
     * @return 返回关联设施集合
     */
    List<ProcRelatedDevice> queryProcRelateDeviceByProcIds(@Param("list") List<String> list);

    /**
     * 工单关联设施名称
     * @author hedongwei@wistronits.com
     * @date  2019/3/26 17:50
     * @param list 工单编号
     * @return 返回关联部门集合
     */
    List<ProcRelatedDepartment> queryProcRelateDeptByProcIds(@Param("list") List<String> list);

    /**
     * 查询工单名称是否存在
     *
     * @param title 工单名
     * @return ProcBase
     */
    ProcBase queryTitleIsExists(@Param("title") String title);

    /**
     * 新增工单基础信息
     *
     * @param procBaseReq 工单请求信息
     * @return int
     */
    int addProcBase(ProcBaseReq procBaseReq);

    /**
     * 删除工单关联设施信息
     *
     * @param procBase 工单基础信息
     * @return int
     */
    int deleteProcRelateDeviceByProcId(ProcBase procBase);

    /**
     * 修改工单关联设施信息isdeleted状态
     *
     * @param procBase 工单基础信息
     * @return int
     */
    int updateProcRelateDeviceIsDeletedByProcId(ProcBase procBase);

    /**
     * 批量修改工单关联设施信息是否删除状态
     *
     * @param procBaseReq 工单基础信息
     * @return int
     */
    int updateProcRelateDeviceIsDeletedByProcIds(ProcBaseReq procBaseReq);

    /**
     * 删除工单关联部门信息
     *
     * @param procBase 工单基础信息
     * @return int
     */
    int deleteProcRelateDeptByProcId(ProcBase procBase);

    /**
     * 修改工单关联部门信息isdeleted状态
     *
     * @param procBase 工单基础信息
     * @return int
     */
    int updateProcRelateDeptIsDeletedByProcId(ProcBase procBase);

    /**
     * 修改工单关联部门信息是否删除状态
     *
     * @param procBaseReq 工单基础信息
     * @return int
     */
    int updateProcRelateDeptIsDeletedByProcIds(ProcBaseReq procBaseReq);

    /**
     * 根据编号修改工单状态
     *
     * @param procBase 工单基础信息
     * @return int
     */
    int updateProcBaseStatusById(ProcBase procBase);

    /**
     * 保存工单关联设施信息
     *
     * @param processInfo 工单信息汇总
     * @return int
     */
    int addProcRelateDevice(ProcessInfo processInfo);

    /**
     * 保存工单关联部门信息
     *
     * @param processInfo 工单信息汇总
     * @return int
     */
    int addProcRelateDept(ProcessInfo processInfo);

    /**
     * 根据工单id获取工单信息
     *
     * @param procId 工单id
     * @return ProcBaseResp
     */
    ProcBaseResp queryProcessByProcId(String procId);

    /**
     * 根据多个工单编号查询工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 10:17
     * @param req 根据多个工单编号查询工单信息
     * @return 查询工单信息
     */
    List<ProcBase> queryProcessByProcIds(ProcBaseReq req);

    /**
     * 获取工单关联设施信息
     *
     * @param procBaseReq 工单请求信息
     * @return List<ProcRelatedDevice>
     */
    List<ProcRelatedDevice> queryProcRelateDevice(ProcBaseReq procBaseReq);

    /**
     * 获取工单关联部门信息
     *
     * @param procBaseReq 工单请求信息
     * @return List<ProcRelatedDepartment>
     */
    List<ProcRelatedDepartment> queryProcRelateDept(ProcBaseReq procBaseReq);

    /**
     * 根据id修改工单基础信息
     *
     * @param procBase 工单基础信息
     * @return int
     */
    int updateProcBaseById(ProcBase procBase);

    /**
     * 根据id修改工单备注信息
     *
     * @param procBase 工单基础信息
     * @return int
     */
    int updateProcBaseRemarkById(ProcBase procBase);

    /**
     * 查询关联工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 17:07
     * @param queryCondition 查询条件
     * @return 关联工单信息
     */
    List<ProcBaseResp> queryListRelatedProcByInspectionTaskIdPage(QueryCondition<ProcBaseReq> queryCondition);


    /**
     * 关联工单个数
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 17:09
     * @param queryCondition 查询条件
     * @return 关联工单个数
     */
    int queryCountListRelatedProcByInspectionTaskId(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 分页查询工单未完工列表
     *
     * @param queryCondition 查询封装类
     * @return List<ProcBaseResp>
     */
    List<ProcBaseResp> queryListProcUnfinishedProcByPage(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 查询工单未完工列表总数
     *
     * @param queryCondition 查询封装类
     * @return int
     */
    int queryCountListProcUnfinishedProc(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 分页查询工单历史列表
     *
     * @param queryCondition 查询封装类
     * @return List<ProcBaseResp>
     */
    List<ProcBaseResp> queryListProcHisProcByPage(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 查询工单历史列表总数
     *
     * @param queryCondition 查询封装类
     * @return int
     */
    int queryCountListProcHisProc(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 根据id删除工单基础信息
     *
     * @param procBaseReq 工单基础信息
     * @return int
     */
    int updateProcBaseIsDeletedByIds(ProcBaseReq procBaseReq);

    /**
     * 根据流程编号删除关联部门
     * @author hedongwei@wistronits.com
     * @date  2019/4/8 17:04
     * @param procRelatedDepartment 关联部门参数
     * @return int 删除关联部门的个数
     */
    int deleteProcRelatedDepartmentByProcId(ProcRelatedDepartment procRelatedDepartment);

    /**
     * 工单未完工列表状态统计
     *
     * @param procBaseReq 工单请求信息
     * @return List<String>
     */
    List<String> queryCountProcByStatus(ProcBaseReq procBaseReq);

    /**
     * 设施类型统计的工单信息类型总数
     *
     * @param procBaseReq 工单请求信息
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> queryListGroupByDeviceType(ProcBaseReq procBaseReq);

    /**
     * 工单历史列表统计
     *
     * @param procBaseReq 工单请求信息
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> queryCountProcByGroup(ProcBaseReq procBaseReq);

    /**
     * 查询工单集合
     * @author hedongwei@wistronits.com
     * @date  2019/3/25 22:19
     * @param procBase 筛选条件
     * @return 查询工单集合
     */
    List<ProcBaseInfoBean> queryProcBaseInfoList(ProcBase procBase);

    /**
     * 查询流程是否有没有办结的工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 12:23
     * @param procInspection 巡检工单参数
     * @return 没有办结的工单信息
     */
    List<ProcBase> selectProcBaseListByProcInspection(ProcInspection procInspection);

    /**
     * 新增告警id及工单id
     *
     * @since 2019-04-18
     * @param procRelatedAlarm 工单关联告警
     *
     * @return Result
     *
     */
    Integer addProcRelatedAlarm(ProcRelatedAlarm procRelatedAlarm);

    /**
     * app获取工单基础信息
     *
     * @param procBaseReq 工单请求
     *
     * @return List<ProcBaseRespForApp>
     */
    List<ProcBaseRespForApp> queryProcBaseListForApp(ProcBaseReq procBaseReq);

    /**
     * 批量修改工单的责任人
     * @author hedongwei@wistronits.com
     * @date  2019/4/14 19:48
     * @param procBase 工单主表
     * @param list 工单id集合
     * @return 修改责任人的结果
     */
    int updateProcBaseAssignBatch( @Param("procBase") ProcBase procBase , @Param("list") List<String> list);


    /**
     * 查询存在工单的告警信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/19 13:17
     * @param refAlarmList 关联工单编号
     * @return 存在告警的工单信息
     */
    List<ProcBase> queryExistsProcForAlarmList(@Param("refAlarmList") List<String> refAlarmList);

    /**
     * 根据用户编号集合查询存在未办理的单据信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/6 12:28
     * @param userIdList 用户编号集合
     * @return 单据信息
     */
    List<ProcBase> queryExistsProcForUserList(@Param("userIdList") List<String> userIdList);


    /**
     * 查询包含权限的工单
     * @author chaofanrong@wistronits.com
     * @date  2019/4/19 16:49
     * @param permissionDeviceTypes 权限设施types
     * @param permissionAreaIds 权限区域ids
     * @param permissionDeptIds 权限部门ids
     *
     * @return 包含权限的工单ids
     */
    Set<String> queryPermissionsInfo(@Param("permissionDeviceTypes") Set<String> permissionDeviceTypes, @Param("permissionAreaIds") Set<String> permissionAreaIds,@Param("permissionDeptIds") Set<String> permissionDeptIds);

    /**
     * 修改工单创建告警的信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/20 14:45
     * @param procIds 流程编号
     * @param updateTime 修改时间
     * @return 返回修改信息的条数
     */
    int updateCreateAlarmInfo(@Param("procIds") List<String> procIds, @Param("updateTime")Date updateTime);


    /**
     * 查询关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 19:58
     * @param req 关联设施参数
     * @return 查询关联设施信息
     */
    List<ProcRelatedDevice> selectRelatedDeviceListInfo(ProcRelatedDeviceListForDeviceIdsReq req);


    /**
     * 根据工单编号集合查询关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 17:18
     * @param req 工单参数
     * @return 返回查询关联设施信息结果
     */
    List<ProcRelatedDevice> selectRelatedDeviceListForProcIds(ProcBaseReq req);


    /**
     * 逻辑删除关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 22:10
     * @param req 逻辑删除参数
     * @return 逻辑删除关联设施
     */
    int logicDeleteRelatedDevice(LogicDeleteRelatedDeviceBatch req);

    /**
     * 逻辑删除关联部门
     * @author chaofanrong@wistronits.com
     * @date  2019/4/26 11:39
     * @param deptIds 部门ids
     * @return 工单关联部门信息
     */
    List<ProcRelatedDepartment> queryProcListByDeptIds(@Param("deptIds") List<String> deptIds);

    /**
     * 回单修改工单主表信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/10 15:01
     * @param procBase 修改工单参数
     * @return 修改行数
     */
    int receiptProcBaseById(ProcBase procBase);

    /**
     * 批量新增关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/14 21:08
     * @param procRelatedDeviceList 关联设施信息
     */
    void addProcRelateDeviceToBatch(@Param("list") List<ProcRelatedDevice> procRelatedDeviceList);

    /**
     * 批量新增关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/14 21:08
     * @param procRelatedDepartmentList 关联部门信息
     */
    void addProcRelateDeptToBatch(@Param("list") List<ProcRelatedDepartment> procRelatedDepartmentList);

    /**
     * 查询所有工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/15 18:21
     * @param procBase 工单信息
     * @return 查询工单信息
     */
    List<ProcBase> selectAllProcBase(ProcBase procBase);

}
