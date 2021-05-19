package com.fiberhome.filink.workflowbusinessserver.req.inspectiontask;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.workflowbusinessserver.utils.common.OperateUtil;
import com.fiberhome.filink.workflowbusinessserver.utils.common.ProcBaseUtil;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Set;

/**
 * 查询巡检任务
 * @author hedongwei@wistronits.comn
 * @date 2019/3/7 16:40
 */
@Data
public class QueryListInspectionTaskByPageReq {


    /**
     * 巡检任务编号集合
     */
    private List<String> inspectionTaskIds;

    /**
     * 巡检任务名称
     */
    private String inspectionTaskName;

    /**
     * 巡检任务状态
     */
    private String inspectionTaskStatus;

    /**
     * 巡检任务状态集合
     */
    private List<String> inspectionTaskStatusList;

    /**
     * 巡检任务类型
     */
    private String inspectionTaskType;

    /**
     * 巡检任务类型集合
     */
    private List<String> inspectionTaskTypes;

    /**
     * 巡检周期
     */
    private Integer taskPeriod;

    /**
     * 巡检周期操作
     */
    private String taskPeriodOperate;

    /**
     * 巡检工单期望用时
     */
    private Integer procPlanDate;

    /**
     * 巡检工单期望用时操作
     */
    private String procPlanDateOperate;

    /**
     * 巡检任务开始时间
     */
    private Long taskStartTime;

    /**
     * 巡检任务开始时间集合
     */
    private List<Long> startTimes;

    /**
     * 巡检任务结束时间
     */
    private Long taskEndTime;

    /**
     * 巡检任务结束时间集合
     */
    private List<Long> endTimes;

    /**
     * 区域编号集合
     */
    private List<String> inspectionAreaIds;

    /**
     * 单位编号集合
     */
    private List<String> deptIds;

    /**
     * 巡检设施总数
     */
    private Integer inspectionDeviceCount;

    /**
     * 巡检设施总数操作符
     */
    private String inspectionDeviceCountOperate;

    /**
     * 权限过滤设施类型
     */
    private Set<String> permissionDeviceTypes;

    /**
     * 权限过滤区域id
     */
    private Set<String> permissionAreaIds;

    /**
     * 权限过滤部门id
     */
    private Set<String> permissionDeptIds;

    /**
     * 是否启用巡检任务 0 禁用 1 启用
     */
    private List<String> isOpens;

    /**
     * 是否启用巡检任务 0 禁用 1 启用
     */
    private String isOpen;

    public List<Long> getStartTimes() {
        return startTimes;
    }

    public void setStartTimes(List<Long> startTimes) {
        this.startTimes = startTimes;
    }

    public List<Long> getEndTimes() {
        return endTimes;
    }

    public void setEndTimes(List<Long> endTimes) {
        this.endTimes = endTimes;
    }

    /**
     * 获取巡检周期的操作符
     * @author hedongwei@wistronits.com
     * @date  2019/5/16 11:44
     * @return 获取巡检周期的操作符
     */
    @JsonIgnore
    public String getTaskPeriodOperateValue() {
        return OperateUtil.getOperateValue(this.taskPeriodOperate);
    }


    /**
     * 获取巡检期望完工时间的操作符
     * @author hedongwei@wistronits.com
     * @date  2019/5/16 11:43
     * @return 巡检期望完工时间的操作符
     */
    @JsonIgnore
    public String getProcPlanDateOperateValue() {
        return OperateUtil.getOperateValue(this.procPlanDateOperate);
    }



    /**
     * 获取巡检设施个数的操作符
     * @author hedongwei@wistronits.com
     * @date  2019/5/16 11:43
     * @return 巡检设施个数的操作符
     */
    @JsonIgnore
    public String getInspectionDeviceCountOperateValue() {
        return OperateUtil.getOperateValue(this.inspectionDeviceCountOperate);
    }


    /**
     * 返回转义后的工单查询条件
     * @author hedongwei@wistronits.com
     * @date  2019/8/12 17:06
     * @param req 工单参数
     * @return 返回工单查询条件
     */
    public static QueryCondition<QueryListInspectionTaskByPageReq> getParseTaskReq(QueryCondition<QueryListInspectionTaskByPageReq> req) {
        if (!ObjectUtils.isEmpty(req.getBizCondition())) {
            QueryListInspectionTaskByPageReq pageReq = req.getBizCondition();
            String parseInspectionTaskName = ProcBaseUtil.alterLikeValue(pageReq.getInspectionTaskName());
            pageReq.setInspectionTaskName(parseInspectionTaskName);
            req.setBizCondition(pageReq);
        }
        return req;
    }
}
