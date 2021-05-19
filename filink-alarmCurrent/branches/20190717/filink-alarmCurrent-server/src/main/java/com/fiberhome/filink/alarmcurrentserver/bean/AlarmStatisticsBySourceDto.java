package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 设施类型统计实体类
 * </p>
 *
 * @author weikuan@fiberhome.com
 * @since 2019-05-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmStatisticsBySourceDto {

    /**
     * 光交箱 001
     */
    private Integer opticalBok;

    /**
     * 人井 030
     */
    private Integer well;

    /**
     * 配线架 060
     */
    private Integer distributionFrame;

    /**
     * 接头盒 090
     */
    private Integer junctionBox;

    /**
     * 分纤箱 150
     */
    private Integer splittingBox;
}
