package com.fiberhome.filink.workflowbusinessserver.bean.statistical.export;

import lombok.Data;

/**
 * 工单故障原因导出
 * @author hedongwei@wistronits.com
 * @date 2019/6/20 15:57
 */
@Data
public class ProcErrorReasonStatisticalExportBean {

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 故障原因其他数量
     */
    private String otherCount;

    /**
     * 故障原因人为损坏数量
     */
    private String failureCount;

    /**
     * 故障原因道路施工数量
     */
    private String rodeWorkCount;

    /**
     * 故障原因盗穿数量
     */
    private String stealWearCount;

    /**
     * 故障原因销障数量
     */
    private String clearFailureCount;

}
