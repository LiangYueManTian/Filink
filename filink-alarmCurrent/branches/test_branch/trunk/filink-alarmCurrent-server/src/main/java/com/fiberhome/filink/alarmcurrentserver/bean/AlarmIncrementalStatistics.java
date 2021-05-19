package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * <p>
 * 告警统计分组的实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 14:11 2019/2/27 0027
 */
@Data
@Document(collection = "alarm_incremental_statistics")
public class AlarmIncrementalStatistics {

    /**
     * 分组的类型
     */
    private String groupType;

    /**
     * 分组的地区
     */
    private String groupArea;

    /**
     * 分组的数量
     */
    private Long groupNum;

    /**
     * 时间
     */
    private Long groupTime;

    /**
     * 时间类型
     */
    private String timeType;

}
