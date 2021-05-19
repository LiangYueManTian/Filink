package com.fiberhome.filink.workflowbusinessserver.service.procclear;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcClearFailure;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.req.procclear.InsertClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.req.app.procclear.ReceiptClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.req.procclear.UpdateClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procclear.ClearFailureDownLoadDetail;

import java.util.List;
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
}
