package com.fiberhome.filink.workflowbusinessserver.bean.statistical.overview;

import lombok.Data;

/**
 * 工单故障原因统计
 * @author hedongwei@wistronits.com
 * @date 2019/5/28 19:46
 */
@Data
public class ProcErrorReasonOverviewStatistical extends ProcStatisticalOverviewBase {

    /**
     * 故障原因
     */
    private String errorReason;
}
