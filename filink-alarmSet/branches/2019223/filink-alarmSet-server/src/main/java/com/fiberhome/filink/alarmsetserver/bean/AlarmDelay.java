package com.fiberhome.filink.alarmsetserver.bean;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;


/**
 * <p>
 *  历史告警设置实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
@TableName("alarm_delay")
public class AlarmDelay {

    private static final long serialVersionUID = 1L;

    /**
     * 延时时间
     */
    private Integer delay;

}
