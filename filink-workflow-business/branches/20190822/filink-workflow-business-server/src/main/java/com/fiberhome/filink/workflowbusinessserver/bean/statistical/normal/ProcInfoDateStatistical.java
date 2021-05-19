package com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 工单信息时间统计
 * @author hedongwei@wistronits.com
 * @date 2019/6/5 21:02
 */
@Data
@Document(collection = "proc_info_date_statistical")
public class ProcInfoDateStatistical {

    /**
     * 区域编号
     */
    private String areaId;

    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 关联责任单位
     */
    private String accountabilityDept;

    /**
     * 工单类型
     */
    private String procType;

    /**
     * 当前日期
     */
    private Long nowDate;

    /**
     * 工单数量
     */
    private Integer orderCount;
}
