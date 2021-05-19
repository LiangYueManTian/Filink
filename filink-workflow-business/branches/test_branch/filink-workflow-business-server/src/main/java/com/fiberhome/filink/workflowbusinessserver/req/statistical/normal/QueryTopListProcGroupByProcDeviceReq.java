package com.fiberhome.filink.workflowbusinessserver.req.statistical.normal;

import lombok.Data;

/**
 * 查询统计
 * @author hedongwei@wistronits.com
 * @date 2019/5/28 19:09
 */
@Data
public class QueryTopListProcGroupByProcDeviceReq extends QueryListProcBaseReq {

    /**
     * 前多少位数量
     */
    private Integer topCount;
}
