package com.fiberhome.filink.workflowbusinessserver.resp.procinspection;

import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import lombok.Data;

/**
 * 巡检工单返回数据
 * @author hedongwei@wistronits.com
 * @date 2019/4/26 16:45
 */
@Data
public class ProcInspectionResp extends ProcInspection {

    /**
     * 巡检工单未完成数量
     */
    private Integer inspectionProcessCount;

    /**
     * 巡检工单未完成百分比
     */
    private double inspectionProcessPercent;

    /**
     * 巡检工单已完成百分比
     */
    private double inspectionCompletedPercent;

    /**
     * 巡检工单设施正常个数
     */
    private Integer normalInspectionCount;

    /**
     * 巡检工单设施正常百分比
     */
    private double normalInspectionPercent;

    /**
     * 巡检工单设施异常个数
     */
    private Integer errorInspectionCount;

    /**
     * 巡检工单设施异常百分比
     */
    private double errorInspectionPercent;
}
