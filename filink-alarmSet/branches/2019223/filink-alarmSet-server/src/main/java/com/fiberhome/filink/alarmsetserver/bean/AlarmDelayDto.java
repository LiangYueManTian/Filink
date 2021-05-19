package com.fiberhome.filink.alarmsetserver.bean;


import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 *  历史告警设置dto实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class AlarmDelayDto implements Serializable {
    /**
     * 历史告警设置实体类
     */
    private AlarmDelay alarmDelay;

    /**
     * 设置id
     */
    private Integer id = 1;

    /**
     * 设置名称
     */
    private String alarmDelayName = "历史告警";


}
