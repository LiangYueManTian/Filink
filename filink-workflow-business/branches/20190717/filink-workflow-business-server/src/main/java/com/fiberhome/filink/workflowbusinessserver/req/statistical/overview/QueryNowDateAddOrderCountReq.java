package com.fiberhome.filink.workflowbusinessserver.req.statistical.overview;

import lombok.Data;

/**
 * 查询当天新增工单数
 * @author hedongwei@wistronits.com
 * @date 2019/5/30 13:55
 */
@Data
public class QueryNowDateAddOrderCountReq extends QueryListProcOverviewBaseReq {

    /**
     * 当前日期时间
     */
    private Long nowDateTime;

}
