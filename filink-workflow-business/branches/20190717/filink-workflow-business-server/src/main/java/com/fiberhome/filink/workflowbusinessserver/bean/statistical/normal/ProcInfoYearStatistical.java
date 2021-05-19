package com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 工单年统计表
 * @author hedongwei@wistronits.com
 * @date 2019/6/17 10:04
 */
@Data
@Document(collection = "proc_info_year_statistical")
public class ProcInfoYearStatistical {

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
     * 统计日期
     */
    private Long nowDate;

    /**
     * 年份
     */
    private String year;

    /**
     * 工单数量
     */
    private Integer orderCount;
}
