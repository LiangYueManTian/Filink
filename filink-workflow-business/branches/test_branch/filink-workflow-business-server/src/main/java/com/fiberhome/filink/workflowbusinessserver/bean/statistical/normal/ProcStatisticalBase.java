package com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal;

import lombok.Data;

/**
 * @author hedongwei@wistronits.com
 * description
 * @date 2019/5/28 19:40
 */
@Data
public class ProcStatisticalBase {

    /**
     * 区域编号
     */
    private String areaId;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 工单数量
     */
    private Integer orderCount;

}
