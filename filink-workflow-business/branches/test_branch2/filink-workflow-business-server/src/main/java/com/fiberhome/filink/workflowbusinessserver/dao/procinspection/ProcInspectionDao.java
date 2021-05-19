package com.fiberhome.filink.workflowbusinessserver.dao.procinspection;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;
import com.fiberhome.filink.workflowbusinessserver.resp.procinspection.ProcInspectionResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 巡检工单表 Mapper 接口
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-03-11
 */
public interface ProcInspectionDao extends BaseMapper<ProcInspection> {

    /**
     * 根据流程编号查询巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 19:40
     * @param procId 流程编号
     * @return 巡检工单详情
     */
    ProcInspection selectInspectionProcByProcId(@Param("procId") String procId);

    /**
     * 根据流程编号查询巡检工单集合
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 19:40
     * @param procIds 工单编号集合
     * @return 巡检工单
     */
    List<ProcInspection> selectInspectionProcByProcIds(@Param("procIds") Set<String> procIds);


    /**
     * 根据工单编号查询巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 19:47
     * @param req 工单参数
     * @return 巡检工单记录信息
     */
    List<ProcInspection> selectInspectionProcForProcIds(ProcBaseReq req);

    /**
     * 根据流程编号删除巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/13 19:40
     * @param isDeleted 是否删除
     * @param procIds 流程编号
     * @return 删除巡检工单结果
     */
    int logicDeleteProcInspection(@Param("isDeleted")String isDeleted ,@Param("list")List<String> procIds);

    /**
     * 批量修改巡检设施数量
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 22:55
     * @param list 巡检工单集合
     * @return 修改结果
     */
    int updateProcInspectionDeviceCountBatch(@Param("list") List<ProcInspection> list);

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
     * 根据巡检工单编号查询巡检工单完成数量
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 16:50
     * @param procInspection 巡检任务
     * @return 查询巡检工单完成数量
     */
    ProcInspectionResp queryProcInspectionByProcInspectionId(ProcInspection procInspection);

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
     * 当前登录用户获取app巡检工单信息
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
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 11:39
     * @param deptIds 部门ids
     * @return 工单关联部门信息
     */
    List<ProcInspection> queryInspectionProcListByDeptIds(@Param("deptIds") List<String> deptIds);


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
