package com.fiberhome.filink.alarmcurrentserver.bean;

import com.fiberhome.filink.alarmsetapi.bean.AlarmFilterRule;
import com.fiberhome.filink.alarmsetapi.bean.AlarmName;
import java.util.List;
import lombok.Data;

/**
 * <p>
 * 告警符合过滤信息
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class AlarmFilterNewInfo {

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
     * 告警过滤信息
     */
    private List<AlarmFilter> alarmFilterNewInfoList;

    /**
     * 告警过滤规则实体类
     */
    private List<AlarmFilterRule> alarmFilterRule;
}
