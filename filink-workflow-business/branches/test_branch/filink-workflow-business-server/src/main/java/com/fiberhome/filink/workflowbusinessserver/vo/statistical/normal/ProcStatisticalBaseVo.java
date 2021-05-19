package com.fiberhome.filink.workflowbusinessserver.vo.statistical.normal;

import lombok.Data;

/**
 * 工单报表返回类父类
 * @author hedongwei@wistronits.com
 * @date 2019/5/29 14:58
 */
@Data
public class ProcStatisticalBaseVo {

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
