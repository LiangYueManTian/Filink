package com.fiberhome.filink.workflowbusinessserver.service.inspectiontask;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTask;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDetailBean;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontask.*;
import com.fiberhome.filink.workflowbusinessserver.vo.inspectiontask.QueryListInspectionTaskByPageVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 巡检任务表 服务类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-26
 */
public interface InspectionTaskService extends IService<InspectionTask> {

    /**
     * 新增巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 14:32
     * @param req 新增巡检任务
     * @return Result 新增巡检任务结果
     */
    Result insertInspectionTask(InsertInspectionTaskReq req);

    /**
     * 修改巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 14:32
     * @param req 修改巡检任务
     * @return Result 修改巡检任务结果
     */
    Result updateInspectionTask(UpdateInspectionTaskReq req);

    /**
     * 修改巡检任务状态
     * @author hedongwei@wistronits.com
     * @date  2019/3/15 19:16
     * @param req 修改巡检任务状态
     * @return 修改巡检任务状态结果
     */
    Result updateInspectionStatus(UpdateInspectionStatusReq req);


    /**
     * 删除巡检任务信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/1 17:31
     * @param req 删除巡检任务参数
     * @return Result 删除巡检任务结果
     */
    Result deleteInspectionTaskByIds(DeleteInspectionTaskByIdsReq req);

    /**
     * 巡检任务关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/3/29 17:07
     * @param req 巡检任务关联设施参数
     * @return 巡检任务关联设施
     */
    Result inspectionTaskRelationDeviceList(InspectionTaskRelationDeviceListReq req);


    /**
     * 巡检任务查询巡检任务名称是否重复
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 18:10
     * @param inspectionTask 巡检任务参数
     * @return Integer 查询重复的数据个数
     */
    int queryInspectionTaskNameIsExists(InspectionTask inspectionTask);

    /**
     * 巡检任务查询巡检任务名称是否重复
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 18:10
     * @param inspectionTask 巡检任务参数
     * @return boolean 返回巡检任务名称重复的结果
     */
    boolean returnInspectionTaskNameResult(InspectionTask inspectionTask);

    /**
     * 批量删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/2 23:08
     * @param list 批量逻辑删除巡检信息
     * @param isDeleted 是否删除
     * @return  boolean 删除结果
     */
    boolean deleteInspectionTaskBatch(List<String> list, String isDeleted);


    /**
     * 根据巡检任务编号查询巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/4 10:11
     * @param list 查询巡检任务参数
     * @return boolean 巡检任务信息
     */
    Result selectIsDeleteForInspectionTaskIds(List<String> list);

    /**
     * 查询巡检任务列表
     * @author hedongwei@wistronits.com
     * @date  2019/3/4 15:56
     * @param queryCondition 查询条件
     * @return  Result 返回查询巡检任务列表
     */
    Result queryListInspectionTaskByPage(QueryCondition<QueryListInspectionTaskByPageReq> queryCondition);

    /**
     * 获取巡检任务条件
     * @author hedongwei@wistronits.com
     * @date  2019/4/22 16:03
     * @param queryCondition 查询条件
     * @param isExport 是否是导出
     * @return 获取查询巡检任务条件
     */
    QueryCondition<QueryListInspectionTaskByPageReq> getInspectionTaskCondition(QueryCondition queryCondition, boolean isExport);

    /**
     * 获取有序的巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/13 15:07
     * @param taskList 巡检任务
     * @param inspectionTaskIds 巡检任务编号
     * @return 获取有序的巡检任务信息
     */
    List<InspectionTask> getShowInspectionTaskList(List<InspectionTask> taskList, List<String> inspectionTaskIds);

    /**
     * 页面需要显示的巡检任务列表信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/7 14:51
     * @param taskList 巡检任务集合
     * @return  页面需要显示的巡检任务信息
     */
    List<QueryListInspectionTaskByPageVo> getInspectionTaskInfoList(List<InspectionTask> taskList);

    /**
     * 查询巡检任务详情
     * @author hedongwei@wistronits.com
     * @date  2019/3/5 18:02
     * @param id 巡检任务编号
     * @return Result 返回巡检任务详情的结果
     */
    Result getInspectionTaskById(String id);

    /**
     * 查询巡检任务信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 9:11
     * @param inspectionTask 巡检任务参数
     * @return 返回巡检任务信息
     */
    InspectionTask getInspectionTaskOne(InspectionTask inspectionTask);


    /**
     * 根据巡检任务编号查询巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/4 10:11
     * @param list 查询巡检任务参数
     * @return List<Inspection> 巡检任务信息
     */
    List<InspectionTask> selectInspectionTaskForInspectionTaskIds(List<String> list);

    /**
     * 批量开启巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 18:06
     * @param req 开启巡检任务参数
     * @return 返回开启巡检任务结果
     */
    Result openInspectionTaskBatch(OpenInspectionTaskBatchReq req);

    /**
     * 批量关闭巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 18:06
     * @param req 关闭巡检任务参数
     * @return 返回关闭巡检任务结果
     */
    Result closeInspectionTaskBatch(CloseInspectionTaskBatchReq req);


    /**
     * 巡检任务详情
     * @author hedongwei@wistronits.com
     * @date  2019/3/6 18:06
     * @param id 巡检任务编号
     * @return 返回巡检任务详情的结果
     */
    Result getInspectionTaskDetail(String id);

    /**
     * 巡检任务详情
     * @author hedongwei@wistronits.com
     * @date  2019/4/16 20:33
     * @param id 巡检任务编号
     * @return 巡检任务详情
     */
    InspectionTaskDetailBean getInspectionDetailInfo(String id);


    /**
     *  导出巡检任务
     *
     * @param exportDto 巡检工单导出请求
     *
     * @return Result
     */
    Result exportInspectionTask(ExportDto exportDto);


    /**
     * 根据设施集合删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 11:34
     * @param req 删除巡检任务参数
     * @return 返回删除巡检任务结果
     */
    Result deleteInspectionTaskForDeviceList(DeleteInspectionTaskForDeviceReq req);

    /**
     * 校验部门信息有无关联巡检任务
     * @param deptIds 部门ids
     * @author hedongwei@wistronits.com
     *
     * @date  2019/4/26 11:03
     * @return 有无关联巡检任务
     */
    Map<String,List<String>> queryInspectionTaskListByDeptIds(List<String> deptIds);
}
