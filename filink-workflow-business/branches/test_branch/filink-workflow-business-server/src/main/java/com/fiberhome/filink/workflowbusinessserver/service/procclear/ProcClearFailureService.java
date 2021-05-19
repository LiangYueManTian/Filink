package com.fiberhome.filink.workflowbusinessserver.service.procclear;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procclear.ProcClearFailure;
import com.fiberhome.filink.workflowbusinessserver.req.app.procclear.ReceiptClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.procrelated.ProcRelatedDeviceListForDeviceIdsReq;
import com.fiberhome.filink.workflowbusinessserver.req.procclear.InsertClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.req.procclear.UpdateClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procclear.ClearFailureDownLoadDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 销障工单表 服务类
 * </p>
 *
 * @author wh1701002@wistronits.com
 * @since 2019-02-15
 */
public interface ProcClearFailureService extends IService<ProcClearFailure> {


    /**
     * 修改销障工单
     * @author hedongwei@wistronits.com
     * @date  2019/6/21 13:51
     * @param procClearFailure 销障工单参数
     * @return 修改销障工单结果
     */
    int updateClearFailure(ProcClearFailure procClearFailure);

    /**
     * 查询销障工单数据
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 10:55
     * @param procClearFailure 销障工单参数
     * @return 返回销障工单数据
     */
    ProcClearFailure selectClearFailureProcOne(ProcClearFailure procClearFailure);

    /**
     * 新增销障工单
     *
     * @param insertClearFailureReq 新增销障工单请求类
     * @throws Exception
     *
     * @return Result
     */
    Result addClearFailureProc(InsertClearFailureReq insertClearFailureReq) throws Exception;

    /**
     * 校验参数合法性
     *
     * @param processInfo 工单汇总类
     * @return Result
     */
    Result checkProcParamsForClearFailure(ProcessInfo processInfo);

    /**
     * 修改销障工单
     *
     * @param updateClearFailureReq 修改销障工单请求类
     * @throws Exception
     *
     * @return Result
     */
    Result updateClearFailureProcById(UpdateClearFailureReq updateClearFailureReq) throws Exception;

    /**
     * 分页查询销障工单未完工列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    Result queryListClearFailureUnfinishedProcByPage(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 分页查询销障工单历史列表
     *
     * @param queryCondition 查询封装类
     * @return Result
     */
    Result queryListClearFailureHisProcByPage(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 保存销障工单特有信息
     *
     * @param procClearFailure 销障工单信息
     * @return int
     */
    int saveProcClearFailureSpecific(ProcClearFailure procClearFailure);

    /**
     * 删除/恢复销障工单特有信息
     *
     * @param proId 工单id
     * @param isDeleted 逻辑删除字段
     *
     * @return int
     */
    int updateProcClearFailureSpecificIsDeleted(String proId,String isDeleted);


    /**
     * 批量逻辑删除销障工单
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 11:51
     * @param procBaseReq 删除销障工单参数
     * @return 批量删除销障工单
     */
    int updateProcClearFailureSpecificIsDeletedBatch(ProcBaseReq procBaseReq);


    /**
     * 获取销障工单特有信息
     *
     * @param procIds 工单ids
     * @return List<ProcClearFailure>
     */
    List<ProcClearFailure> queryProcClearFailureSpecific(Set<String> procIds);

    /**
     * 销障工单未完工列表状态总数统计
     *
     * @return Result
     */
    Result queryCountListProcUnfinishedProcStatus();

    /**
     * 销障工单列表待指派状态统计
     *
     * @return Result
     */
    Result queryCountClearFailureProcByAssigned();

    /**
     * 销障工单列表待处理状态统计
     *
     * @return Result
     */
    Result queryCountClearFailureProcByPending();

    /**
     * 销障工单列表处理中状态统计
     *
     * @return Result
     */
    Result queryCountClearFailureProcByProcessing();

    /**
     * 销障工单列表今日新增状态统计
     *
     * @return Result
     */
    Result queryCountClearFailureProcByToday();

    /**
     * 故障原因统计的销障工单信息
     *
     * @return Result
     */
    Result queryListClearFailureGroupByErrorReason();

    /**
     * 处理方案统计的销障工单信息
     *
     * @return Result
     */
    Result queryListClearFailureGroupByProcessingScheme();

    /**
     * 设施类型统计的销障工单信息
     *
     * @return Result
     */
    Result queryListClearFailureGroupByDeviceType();

    /**
     * 工单状态统计的销障工单信息
     *
     * @return Result
     */
    Result queryListClearFailureByStatus();

    /**
     * 销障工单历史列表总数统计
     *
     * @return Result
     */
    Result queryCountListProcHisProc();

    /**
     * 销障工单未完工列表导出
     *
     * @param exportDto 销障工单导出请求
     *
     * @return Result
     */
    Result exportClearFailureProcUnfinished(ExportDto exportDto);

    /**
     * 销障工单历史列表导出
     *
     * @param exportDto 销障工单导出请求
     *
     * @return Result
     */
    Result exportClearFailureProcHistory(ExportDto exportDto);

    /**
     * 重新生成销障工单
     * @author chaofanrong@wistronits.com
     * @date  2019/4/16 18:00
     * @param insertClearFailureReq 新增销障工单请求
     * @return 重新生成销障工单结果
     *
     * @throws Exception
     */
    Result regenerateClearFailureProc(InsertClearFailureReq insertClearFailureReq) throws Exception;

    /**
     * 销障工单前五
     *
     * @param deviceId 设施id
     *
     * @return Result
     */
    Result queryClearFailureProcTopFive(String deviceId);

    /**
     * app销障工单回单
     *
     * @param receiptClearFailureReq 销障工单回单请求
     *
     * @return Result
     */
    Result receiptClearFailureProcForApp(ReceiptClearFailureReq receiptClearFailureReq);

    /**
     * app销障工单下载
     *
     * @param procBaseRespForApps 工单下载列表
     *
     * @return List<ClearFailureDownLoadDetail>
     */
    List<ClearFailureDownLoadDetail> downLoadClearFailureProcForApp(List<ProcBaseRespForApp> procBaseRespForApps);


    /**
     * 根据工单编号修改销障工单的状态
     *
     * @param  procClearFailure 销障工单信息
     *
     * @return 销障工单修改的行数
     */
    int updateProcClearFailureStatusById(ProcClearFailure procClearFailure);


    /**
     * 查询存在销障工单的告警信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/19 13:17
     * @param refAlarmList 关联工单编号
     * @return 存在告警的工单信息
     */
    List<ProcClearFailure> queryExistsProcClearFailureForAlarmList(@Param("refAlarmList") List<String> refAlarmList);

    /**
     * 修改销障工单备注
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 11:38
     * @param procClearFailure 销障工单参数
     * @return 返回修改销障工单状态结果
     */
    int updateProcClearFailureRemarkById(ProcClearFailure procClearFailure);

    /**
     * 根据id删除工单基础信息
     *
     * @param procBaseReq 工单基础信息
     * @return int
     */
    int updateProcClearFailureIsDeletedByIds(ProcBaseReq procBaseReq);


    /**
     * 查询销障工单关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/22 17:10
     * @param procBaseReq 工单参数
     * @return 返回销障工单信息
     */
    List<ProcClearFailure> queryClearFailureProcRelateDevice(ProcBaseReq procBaseReq);


    /**
     * app获取销障工单基础信息
     *
     * @param procBaseReq 工单请求
     *
     * @return List<ProcBaseRespForApp>
     */
    List<ProcBaseRespForApp> queryClearFailureListForApp(ProcBaseReq procBaseReq);

    /**
     * 批量修改销障工单的责任人
     * @author hedongwei@wistronits.com
     * @date  2019/4/14 19:48
     * @param procBase 工单主表
     * @param list 工单id集合
     * @return 修改责任人的结果
     */
    int updateProcClearAssignBatch(@Param("procBase") ProcBase procBase , @Param("list") List<String> list);

    /**
     * 根据告警查询存在的工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/19 12:49
     * @param alarmProcList 告警编号数组
     * @return 查询告警工单结果
     */
    Map<String, Object> queryExistsProcClearForAlarmList(List<String> alarmProcList);

    /**
     * 查询销障工单关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 19:58
     * @param req 关联设施参数
     * @return 查询销障工单关联设施信息
     */
    List<ProcClearFailure> selectClearRelatedDeviceListInfo(ProcRelatedDeviceListForDeviceIdsReq req);

    /**
     * 根据用户编号集合查询存在未办理的销障信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/6 12:28
     * @param userIdList 用户编号集合
     * @return 销障工单信息
     */
    List<ProcClearFailure> queryExistsProcForUserList(@Param("userIdList") List<String> userIdList);


    /**
     * 销障工单关联设施集合
     * @author hedongwei@wistronits.com
     * @date  2019/3/26 17:50
     * @param list 销障工单编号
     * @return 返回销障工单关联设施集合
     */
    List<ProcClearFailure> queryProcRelateDeviceByProcIds(@Param("list") List<String> list);


    /**
     * 查询销障工单未完工列表总数
     *
     * @param queryCondition 查询封装类
     * @return int
     */
    int queryCountListProcClearUnfinishedProc(QueryCondition<ProcBaseReq> queryCondition);


    /**
     * 分页查询销障工单未完工列表
     *
     * @param queryCondition 查询封装类
     * @return List<ProcBaseResp>
     */
    List<ProcBaseResp> queryListProcClearUnfinishedProcByPage(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 查询销障工单历史列表总数
     *
     * @param queryCondition 查询封装类
     * @return int
     */
    int queryCountListProcClearHisProc(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 分页查询销障工单历史列表
     *
     * @param queryCondition 查询封装类
     * @return List<ProcBaseResp>
     */
    List<ProcBaseResp> queryListProcClearHisProcByPage(QueryCondition<ProcBaseReq> queryCondition);

}
