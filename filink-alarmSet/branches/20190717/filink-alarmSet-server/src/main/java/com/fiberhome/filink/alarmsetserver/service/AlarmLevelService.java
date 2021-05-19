package com.fiberhome.filink.alarmsetserver.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.alarmsetserver.bean.AlarmLevel;
import com.fiberhome.filink.bean.Result;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>
 * 告警级别服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public interface AlarmLevelService extends IService<AlarmLevel> {

    /**
     * 查询全量告警级别信息
     *
     * @return 告警级别信息
     */
    Result queryAlarmLevelList();

    /**
     * 修改告警级别
     *
     * @param alarmLevel 告警级别
     * @return 修改结果
     */
    Result updateAlarmLevel(AlarmLevel alarmLevel);

    /**
     * 查询单个告警级别信息
     *
     * @param alarmId 告警级别
     * @return 告警级别信息
     */
    Result queryAlarmLevelById(String alarmId);

    /**
     * 查询告警级别
     *
     * @return 告警级别信息
     */
    Result queryAlarmLevel();

    /**
     * 根据告警级别编码查询告警级别设置信息
     *
     * @param alarmLevelCode 告警级别编码
     * @return 告警级别
     */
    AlarmLevel queryAlarmLevelSetFeign(@PathVariable("alarmLevelCode") String alarmLevelCode);
}
