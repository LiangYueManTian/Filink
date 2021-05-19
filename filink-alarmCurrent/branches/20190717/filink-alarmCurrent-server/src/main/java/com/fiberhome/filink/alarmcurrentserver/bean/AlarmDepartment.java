package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * <p>
 * 责任单位实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class AlarmDepartment {

    /**
     * 负责单位id
     */
    @Field("responsible_department_id")
    private String responsibleDepartmentId;

    /**
     * 负责单位
     */
    @Field("responsible_department")
    private String responsibleDepartment;
}
