package com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal;

import lombok.Data;

/**
 * 工单状态统计
 * @author hedongwei@wistronits.com
 * @date 2019/5/28 19:24
 */
@Data
public class ProcStatusStatistical extends ProcStatisticalBase {

    /**
     * 工单统计状态
     */
    private String status;
}
