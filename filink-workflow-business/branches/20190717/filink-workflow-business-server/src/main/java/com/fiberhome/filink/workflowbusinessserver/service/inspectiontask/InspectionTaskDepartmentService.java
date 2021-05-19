package com.fiberhome.filink.workflowbusinessserver.service.inspectiontask;

import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDepartment;
import com.baomidou.mybatisplus.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 巡检任务关联单位表 服务类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-27
 */
public interface InspectionTaskDepartmentService extends IService<InspectionTaskDepartment> {

    /**
     * 查询巡检任务关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/5 13:37
     * @param inspectionTaskIds 巡检任务编号集合
     * @return List<InspectionTaskDepartment> 巡检任务关联部门信息
     */
    List<InspectionTaskDepartment> queryInspectionTaskDepartmentsByTaskIds(@Param("list")List<String> inspectionTaskIds);

    /**
     * 查询巡检任务关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/5 13:37
     * @param inspectionTaskId 巡检任务编号
     * @return List<InspectionTaskDepartment> 巡检任务关联部门信息
     */
    List<InspectionTaskDepartment> queryInspectionTaskDepartmentsByTaskId(@Param("insepctionTaskId")String inspectionTaskId);

    /**
     * 删除巡检任务关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/2/27 15:44
     * @param inspectionTaskDepartment 巡检任务关联部门参数
     * @return int 返回删除关联部门更改的行数
     */
    int deleteInspectionTaskDepartment(InspectionTaskDepartment inspectionTaskDepartment);

    /**
     * 新增巡检任务关联单位信息
     * @author hedongwei@wistronits.com
     * @date  2019/2/27 15:44
     * @param inspectionTaskDepartmentList 巡检任务部门参数
     * @param inspectionTaskId 巡检任务编号
     * @return int 返回新增关联部门更改的行数
     */
    int insertInspectionTaskDepartmentBatch(@Param("list")List<InspectionTaskDepartment> inspectionTaskDepartmentList, String inspectionTaskId);

    /**
     * 批量删除巡检任务关联单位
     * @author hedongwei@wistronits.com
     * @date  2019/3/2 23:36
     * @param inspectionTaskIds 巡检任务编号
     * @param isDeleted 是否删除关联单位
     * @return int 批量删除巡检任务关联单位
     */
    int deleteInspectionTaskDepartmentBatch(List<String> inspectionTaskIds, String isDeleted);

    /**
     * 根据部门编号查询巡检部门数据
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 20:14
     * @param deptIds 部门编号
     * @return 部门编号查询巡检部门数据
     */
    List<InspectionTaskDepartment> queryTaskListByDeptIds(List<String> deptIds);

}
