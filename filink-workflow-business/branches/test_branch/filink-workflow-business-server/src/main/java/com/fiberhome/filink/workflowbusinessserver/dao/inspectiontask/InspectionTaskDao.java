package com.fiberhome.filink.workflowbusinessserver.dao.inspectiontask;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTask;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontask.QueryListInspectionTaskByPageReq;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 巡检任务表 Mapper 接口
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-26
 */
public interface InspectionTaskDao extends BaseMapper<InspectionTask> {

    /**
     * 巡检任务查询巡检任务名称是否重复
     * @author hedongwei@wistronits.com
     * @date  2019/2/26 18:10
     * @param inspectionTask 巡检任务参数
     * @return Integer 查询重复的数据个数
     */
    int queryInspectionTaskNameIsExists(InspectionTask inspectionTask);

    /**
     * 根据巡检任务编号查询巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/4 10:11
     * @param list 查询巡检任务参数
     * @return List<Inspection> 巡检任务信息
     */
    List<InspectionTask> selectInspectionTaskForInspectionTaskIds(@Param("list") List<String> list);

    /**
     * 批量删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/2 23:04
     * @param list 删除巡检任务的参数
     * @param updateUser 修改用户
     * @param updateTime 修改时间
     * @param isDeleted 是否删除
     * @return int 删除巡检任务的个数
     */
    int deleteInspectionTaskBatch(@Param("list") List<String> list, @Param("updateUser") String updateUser,
                             @Param("updateTime")Date updateTime, @Param("isDeleted") String isDeleted);

    /**
     * 批量开启或关闭巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/2 23:04
     * @param list 删除巡检任务的参数
     * @param updateTask 修改任务
     * @return int 删除巡检任务的个数
     */
    int updateInspectionTaskOpenAndCloseBatch(@Param("list") List<String> list, @Param("updateTask") InspectionTask updateTask);

    /**
     * 批量修改巡检任务信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 19:44
     * @param inspectionTaskList 巡检任务集合
     * @return 修改行数
     */
    int updateInspectionTaskInfoBatch(@Param("list") List<InspectionTask> inspectionTaskList);

    /**
     * 查询巡检任务编号
     * @author hedongwei@wistronits.com
     * @date  2019/3/7 23:40
     * @param queryCondition 查询条件
     * @return List<String> 巡检任务编号
     */
    List<String> queryListInspectionTaskByPage(QueryCondition<QueryListInspectionTaskByPageReq> queryCondition);

    /**
     * 查询巡检任务个数
     * @author hedongwei@wistronits.com
     * @date  2019/3/7 23:42
     * @param queryCondition 查询条件
     * @return int 巡检任务个数
     */
    int queryListInspectionTaskCount(QueryCondition<QueryListInspectionTaskByPageReq> queryCondition);
}
