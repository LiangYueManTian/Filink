package com.fiberhome.filink.workflowbusinessserver.dao.procclear;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procclear.ProcClearFailure;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.procrelated.ProcRelatedDeviceListForDeviceIdsReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 销障工单表 Mapper 接口
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-02-15
 */
public interface ProcClearFailureDao extends BaseMapper<ProcClearFailure> {

    /**
     * 新增销障工单特有信息
     *
     * @param procClearFailure 销障工单基础信息
     * @return int
     */
    int addProcClearFailureSpecific(ProcClearFailure procClearFailure);

    /**
     * 修改销障工单特有信息
     *
     * @param procClearFailure 销障工单基础信息
     * @return int
     */
    int updateProcClearFailureSpecificByProcId(ProcClearFailure procClearFailure);

    /**
     * 删除/恢复销障工单特有信息
     *
     * @param procId 工单id
     * @param isDeleted 逻辑删除字段
     *
     * @return int
     */
    int updateProcClearFailureSpecificIsDeleted(@Param("procId") String procId,@Param("isDeleted") String isDeleted);


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
    List<ProcClearFailure> queryProcClearFailureSpecific(@Param("procIds") Set<String> procIds);

    /**
     * 根据工单编号修改销障工单的状态
     *
     * @param procClearFailure 销障工单信息
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
     * 查询存在销障工单的告警信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/19 13:17
     * @param refAlarmList 关联销障工单编号
     * @return 存在告警的销障工单信息
     */
    List<ProcClearFailure> queryExistsProcClearForAlarmList(@Param("refAlarmList") List<String> refAlarmList);

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


    /**
     * 销障工单回单修改信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/10 15:01
     * @param procBase 修改工单参数
     * @return 修改行数
     */
    int receiptProcClearFailureById(ProcBase procBase);

}
