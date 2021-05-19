package com.fiberhome.filink.workflowbusinessserver.dao.inspectiontask;

import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskRelatedJob;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontaskjob.InspectionTaskRelatedJobReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-04-16
 */
public interface InspectionTaskRelatedJobDao extends BaseMapper<InspectionTaskRelatedJob> {

    /**
     * 查询巡检任务关联任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 13:10
     * @param req 巡检任务关联任务
     * @return 巡检任务关联任务集合
     */
    List<InspectionTaskRelatedJob> selectInspectionTaskRelatedInfo(InspectionTaskRelatedJobReq req);

    /**
     * 批量新增巡检任务关联任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 12:31
     * @param inspectionTaskRelatedJobList
     * @return 新增结果
     */
    int insertInspectionTaskRelatedJobBatch(@Param("list") List<InspectionTaskRelatedJob> inspectionTaskRelatedJobList );

    /**
     * 删除巡检任务关联任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 12:39
     * @param inspectionTaskRelatedJob 巡检任务关联任务参数
     * @return 删除行数
     */
    int deleteInspectionTaskRelatedJobByInspectionTaskId(InspectionTaskRelatedJob inspectionTaskRelatedJob);


    /**
     * 批量删除巡检任务关联任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 12:32
     * @param req 巡检任务关联任务参数
     * @return 删除的条数
     */
    int deleteInspectionTaskRelatedJobBatch(InspectionTaskRelatedJobReq req);

    /**
     * 逻辑删除巡检任务关联任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 12:39
     * @param inspectionTaskRelatedJob 巡检任务关联任务参数
     * @return 删除行数
     */
    int logicDeleteInspectionTaskRelatedJobByInspectionTaskId(InspectionTaskRelatedJob inspectionTaskRelatedJob);

    /**
     * 批量逻辑删除巡检任务关联任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 12:34
     * @param req 巡检任务筛选参数
     * @return 删除行数
     */
    int logicDeleteInspectionTaskRelatedJob(InspectionTaskRelatedJobReq req);

}
