package com.fiberhome.filink.workflowbusinessserver.bean.statistical.export;

import lombok.Data;

/**
 * 处理方案导出类
 * @author hedongwei@wistronits.com
 * @date 2019/6/20 15:16
 */
@Data
public class ProcProcessingSchemeStatisticalExportBean {

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 处理方案其他数量
     */
    private String otherCount;

    /**
     * 处理方案报修数量
     */
    private String repairCount;

    /**
     * 处理方案现场销障数量
     */
    private String liveCount;

}
