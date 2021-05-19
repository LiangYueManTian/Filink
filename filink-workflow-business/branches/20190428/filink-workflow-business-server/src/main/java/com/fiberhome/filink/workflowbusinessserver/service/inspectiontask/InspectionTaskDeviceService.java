package com.fiberhome.filink.workflowbusinessserver.service.inspectiontask;

import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDevice;
import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontask.taskrelated.InspectionTaskDeviceReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 巡检任务设施表 服务类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-26
 */
public interface InspectionTaskDeviceService extends IService<InspectionTaskDevice> {

    /**
     * 删除巡检任务关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/2/27 11:35
     * @param inspectionTaskDevice 删除巡检任务关联设施信息
     * @return 返回删除结果
     */
    int deleteInspectionTaskDevice(InspectionTaskDevice inspectionTaskDevice);


    /**
     * 批量删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/2 23:59
     * @param inspectionTaskIds 巡检任务编号集合
     * @param isDelete 是否删除
     * @return int 返回巡检任务删除的结果
     */
    int deleteInspectionTaskDeviceBatch(List<String> inspectionTaskIds, String isDelete);

    /**
     *  批量逻辑删除设施
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 19:18
     * @param req 批量逻辑删除设施参数
     * @return 批量删除设施修改行数
     */
    int logicDeleteTaskDeviceBatch(InspectionTaskDeviceReq req);

    /**
     * 批量新增巡检任务关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/2/27 11:35
     * @param inspectionTaskDeviceList 批量新增设施参数信息
     * @param inspectionTaskId 巡检任务编号
     * @return 返回新增结果
     */
    int insertInspectionTaskDeviceBatch(@Param("list")List<InspectionTaskDevice> inspectionTaskDeviceList, String inspectionTaskId);

    /**
     * 查询巡检任务关联设施
     * @author hedongwei@wistronits.com
     * @date  2019/3/2 23:59
     * @param inspectionTaskId 巡检任务编号
     * @return List<InspectionTaskDevice> 返回巡检任务关联设施的结果
     */
    List<InspectionTaskDevice> queryInspectionTaskDeviceByTaskId(@Param("inspectionTaskId") String inspectionTaskId);

    /**
     * 根据巡检编号集合查询巡检任务设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 13:46
     * @param req 查询巡检任务关联设施参数
     * @return 巡检任务设施信息
     */
    List<InspectionTaskDevice> queryInspectionTaskDeviceForDeviceIdList(InspectionTaskDeviceReq req);
}
