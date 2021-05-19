package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * <p>
 * 设施统计实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-21
 */
@Data
@Document(collection = "alarm_source_incremental")
public class AlarmSourceIncremental {

    @Id
    private String id;

    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 时间
     */
    private String groupTime;

    /**
     * 数量
     */
    private Long count;

    /**
     * 类型
     */
    private String type;
}
