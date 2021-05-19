package com.fiberhome.filink.alarmcurrentserver.bean;

import com.fiberhome.filink.alarmsetapi.bean.AlarmName;
import java.util.List;
import lombok.Data;

/**
 * <p>
 * 告警信息是否重复
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class RepeatedOnNotFilter {

    /**
     * 当前告警信息
     */
    private List<AlarmCurrent> alarmRepeatOnCurrentList;

    /**
     * 设施信息实体类
     */
    private OrderDeviceInfo orderDeviceInfo;

    /**
     * 告警名称
     */
    private AlarmName alarmNameInfo;

    /**
     * 告警信息实体类
     */
    private AlarmCurrentInfo alarmCurrentInfo;

    /**
     * 当前告警信息
     */
    private List<AlarmCurrent> alarmCurrentNewInfoList;
}
