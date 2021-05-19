package com.fiberhome.filink.workflowbusinessserver.service.procinspection;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTask;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.CompleteInspectionProcReq;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.ProcInspectionReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procinspection.InspectionDownLoadDetail;
import com.fiberhome.filink.workflowbusinessserver.vo.procinspection.ProcInspectionVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 巡检工单表 服务类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-03-11
 */
public interface ProcInspectionService extends IService<ProcInspection> {

    /**
     * 修改巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 11:14
     * @param procInspection 工单巡检信息
     * @return 查询巡检工单信息
     */
    int updateProcInspectionInfo(ProcInspection procInspection);

    /**
     * 查询工单巡检信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 11:14
     * @param procInspection 工单巡检信息
     * @return 查询工单巡检信息
     */
    ProcInspection selectProcInspectionOne(ProcInspection procInspection);

    /**
     * 根据流程编号查询巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 19:40
     * @param procId 流程编号
     * @return 巡检工单信息
     */
    ProcInspection selectInspectionProcByProcId(@Param("procId") String procId);

    /**
     * 批量查询巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/16 14:20
     * @param procIds 流程编号集合
     * @return 巡检工单集合
     */
    List<ProcInspection> selectInspectionProcByProcIds(@Param("procIds") Set<String> procIds);

    /**
     * 查询巡检工单未完工列表
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 15:33
     * @param queryCondition 查询条件
     * @return 返回巡检工单未完工列表
     */
    Result queryListInspectionProcessByPage(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 获得页面返回的值
     * @author hedongwei@wistronits.com
     * @date  2019/3/18 10:34
     * @param result 返回页面的值
     * @return 页面返回的值
     */
    Result getInspectionVo(Result result);

    /**
     * 获取巡检工单数据
     * @author hedongwei@wistronits.com
     * @date  2019/4/12 12:31
     * @param procBaseRespList 工单返回类
     * @return 返回页面显示巡检工单数据
     */
    List<ProcInspectionVo> getInspectionVoInfo(List<ProcBaseResp> procBaseRespList);

    /**
     * 新增巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param addInspectionProcReq 新增巡检工单请求
     * @param operate 操作
     * @param isAlarmViewCall 是否是告警前台调用
     * @return 新增巡检工单结果
     */
    Result addInspectionProc(ProcInspectionReq addInspectionProcReq, String operate, String isAlarmViewCall);

    /**
     * 重新生成巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param addInspectionProcReq 重新生成巡检工单参数
     * @param operate 操作
     * @return 重新生成巡检工单结果
     */
    Result regenerateInspectionProc(ProcInspectionReq addInspectionProcReq, String operate);

    /**
     * 修改巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param req 修改巡检工单请求
     * @param operate 操作
     * @return 修改巡检工单结果
     */
    Result updateInspectionProc(ProcInspectionReq req, String operate);

    /**
     * 保存巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/11 16:56
     * @param procInspectionReq 保存巡检工单请求
     * @param operate 操作
     * @param isAlarmViewCall 是否是告警前台调用
     * @return 保存巡检工单结果addInspectionProc
     */
    Result saveProcInspection(ProcInspectionReq procInspectionReq, String operate, String isAlarmViewCall);

    /**
     * 根据流程编号删除巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 19:40
     * @param isDeleted 是否删除
     * @param procIds 流程编号
     * @return 删除巡检工单结果
     */
    int logicDeleteProcInspection(String isDeleted, List<String> procIds);

    /**
     * 保存巡检工单特有信息
     *
     * @param procInspection 巡检工单信息
     * @return int
     */
    int saveProcInspectionSpecific(ProcInspection procInspection);

    /**
     * 查询巡检任务完工记录
     *
     * @param queryCondition 查询对象
     * @return int
     */
    Result queryListInspectionCompleteRecordByPage(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 查询巡检任务关联工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 20:22
     * @param req 巡检任务关联工单参数
     * @return 巡检工单结果
     */
    Result queryListInspectionTaskRelationProcByPage(QueryCondition<ProcBaseReq> req);

    /**
     * 查询巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 20:17
     * @param queryCondition 查询对象
     * @return 巡检工单集合
     */
    List<ProcInspection> queryProcInspectionInfo(QueryCondition queryCondition);

    /**
     * 查询巡检工单个数
     * @author hedongwei@wistronits.com
     * @date  2019/3/14 20:17
     * @param queryCondition 查询对象
     * @return 巡检工单个数
     */
    int queryProcInspectionInfoCount(QueryCondition queryCondition);

    /**
     * 巡检工单详情
     * @author hedongwei@wistronits.com
     * @date  2019/3/20 16:27
     * @param procId 巡检工单编号
     * @return 返回巡检工单详情结果
     */
    Result getInspectionProcById(String procId);

    /**
     * 完成巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/25 9:25
     * @param completeReq 完结巡检工单参数
     * @return 返回完成巡检工单
     */
    Result completeInspectionProc(CompleteInspectionProcReq completeReq);

    /**
     * 根据工单编号查询巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 12:51
     * @param procId 工单编号
     * @return 获取巡检工单信息
     */
    ProcInspection getProcInspectionByProcId(String procId);

    /**
     * 根据工单编号查询巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 19:47
     * @param req 工单参数
     * @return 巡检工单记录信息
     */
    List<ProcInspection> selectInspectionProcForProcIds(ProcBaseReq req);

    /**
     * 修改巡检记录为巡检完成
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 11:40
     * @param procInspectionOne 巡检记录
     * @return 修改巡检记录为巡检完成
     */
    boolean checkInspectionTaskStatusComplete(ProcInspection procInspectionOne);


    /**
     * 修改巡检记录为巡检完成
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 11:40
     * @param procInspectionOne 巡检记录
     * @return 修改巡检记录为巡检完成
     */
    boolean updateInspectionTaskStatus(ProcInspection procInspectionOne);


    /**
     * 设置工单类型为巡检工单
     *
     * @param queryCondition 查询封装类
     * @return QueryCondition<ProcBaseReq>
     */
    QueryCondition<ProcBaseReq> setProcTypeToInspection(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * app巡检工单下载
     *
     * @param procBaseRespForApps 工单下载列表
     *
     * @return List<InspectionDownLoadDetail>
     */
    List<InspectionDownLoadDetail> downLoadInspectionProcForApp(List<ProcBaseRespForApp> procBaseRespForApps);

    /**
     * 巡检任务手动调用新增巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 9:00
     * @param inspectionTaskId 巡检任务编号
     */
    void manualAddInspectionProc(String inspectionTaskId);

    /**
     * 巡检任务新增巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/16 19:53
     * @param inspectionTask 新增巡检工单参数
     * @return 返回任务生成巡检任务
     */
    void jobAddInspectionProc(InspectionTask inspectionTask);


    /**
     *  巡检工单未完工列表导出
     *
     * @param exportDto 巡检工单导出请求
     *
     * @return Result
     */
    Result exportListInspectionProcess(ExportDto exportDto);

    /**
     * 查询巡检工单前五条
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 19:33
     * @param deviceId 设施编号
     * @return 巡检工单前五条
     */
    Result queryProcInspectionTopFive(String deviceId);

    /**
     *  巡检工单已完成列表导出
     *
     * @param exportDto 巡检工单导出请求
     *
     * @return Result
     */
    Result exportListInspectionComplete(ExportDto exportDto);


    /**
     * 批量修改巡检设施数量
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 22:55
     * @param list 巡检工单集合
     * @return 修改结果
     */
    void updateProcInspectionDeviceCountBatch(@Param("list") List<ProcInspection> list);


    /**
     * 根据巡检工单编号查询巡检工单完成数量
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 16:50
     * @param procInspection 巡检工单
     * @return 查询巡检工单完成数量
     */
    Result queryProcInspectionByProcInspectionId(ProcInspection procInspection);


    /**
     * 修改巡检工单状态
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 11:38
     * @param procInspection 工单状态
     * @return 返回修改巡检工单状态结果
     */
    int updateProcInspectionStatusById(ProcInspection procInspection);

    /**
     * 修改巡检工单备注
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 11:38
     * @param procInspection 工单状态
     * @return 返回修改巡检工单状态结果
     */
    int updateProcInspectionRemarkById(ProcInspection procInspection);

    /**
     * 根据id删除工单基础信息
     *
     * @param procBaseReq 工单基础信息
     * @return int
     */
    int updateInspectionIsDeletedByIds(ProcBaseReq procBaseReq);

    /**
     * app获取当前用户巡检工单基础信息
     *
     * @param procBaseReq 工单请求
     *
     * @return List<ProcBaseRespForApp>
     */
    List<ProcBaseRespForApp> queryLoginUserInspectionListForApp(ProcBaseReq procBaseReq);

    /**
     * app获取巡检工单基础信息
     *
     * @param procBaseReq 工单请求
     *
     * @return List<ProcBaseRespForApp>
     */
    List<ProcBaseRespForApp> queryInspectionListForApp(ProcBaseReq procBaseReq);


    /**
     * 批量修改巡检工单的责任人
     * @author hedongwei@wistronits.com
     * @date  2019/4/14 19:48
     * @param procBase 工单主表
     * @param list 工单id集合
     * @return 修改责任人的结果
     */
    int updateProcInspectionAssignBatch(@Param("procBase") ProcBase procBase , @Param("list") List<String> list);

    /**
     * 查询巡检关联部门信息
     * @author chaofanrong@wistronits.com
     * @date  2019/4/26 11:39
     * @param deptIds 部门ids
     * @return 工单关联部门信息
     */
    List<ProcInspection> queryInspectionProcListByDeptIds(@Param("deptIds") List<String> deptIds);


    /**
     * 根据区域编号和部门编号查询巡检关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/8/28 11:39
     * @param deptIds 部门ids
     * @param areaId 区域编号
     * @return 工单关联部门信息
     */
    List<ProcInspection> queryInspectionProcListByDeptIdsAndAreaId(@Param("deptIds") List<String> deptIds, @Param("areaId") String areaId);

    /**
     * 根据用户编号集合查询存在未办理的巡检信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/6 12:28
     * @param userIdList 用户编号集合
     * @return 巡检工单信息
     */
    List<ProcInspection> queryExistsProcForUserList(@Param("userIdList") List<String> userIdList);


    /**
     * 巡检工单关联设施名称
     * @author hedongwei@wistronits.com
     * @date  2019/3/26 17:50
     * @param list 工单编号
     * @return 返回巡检关联部门集合
     */
    List<ProcInspection> queryInspectionRelateDeptByProcIds(@Param("list") List<String> list);


    /**
     * 查询巡检工单未完工列表总数
     *
     * @param queryCondition 查询封装类
     * @return int
     */
    int queryCountListProcInspectionUnfinishedProc(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 分页查询巡检工单未完工列表
     *
     * @param queryCondition 查询封装类
     * @return List<ProcBaseResp>
     */
    List<ProcBaseResp> queryListProcInspectionUnfinishedProcByPage(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 查询巡检工单历史列表总数
     *
     * @param queryCondition 查询封装类
     * @return int
     */
    int queryCountListProcInspectionHisProc(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 分页查询巡检工单历史列表
     *
     * @param queryCondition 查询封装类
     * @return List<ProcBaseResp>
     */
    List<ProcBaseResp> queryListProcInspectionHisProcByPage(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 查询巡检任务是否有没有办结的巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 12:23
     * @param procInspection 巡检工单参数
     * @return 没有办结的工单信息
     */
    List<ProcInspection> selectNotInspectionProcInspection(ProcInspection procInspection);

    /**
     * 巡检任务关联巡检工单个数
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 17:09
     * @param queryCondition 查询条件
     * @return 巡检任务关联巡检工单个数
     */
    int queryCountListRelatedProcByInspectionTaskId(QueryCondition<ProcBaseReq> queryCondition);

    /**
     * 查询巡检任务关联巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 17:07
     * @param queryCondition 查询条件
     * @return 巡检任务关联巡检工单信息
     */
    List<ProcBaseResp> queryListRelatedProcByInspectionTaskIdPage(QueryCondition<ProcBaseReq> queryCondition);
}
