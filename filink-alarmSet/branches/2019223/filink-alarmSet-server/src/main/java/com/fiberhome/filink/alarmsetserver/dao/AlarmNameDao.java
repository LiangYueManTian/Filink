package com.fiberhome.filink.alarmsetserver.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmName;

import java.util.List;

/**
 * <p>
 *  当前告警设置Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public interface AlarmNameDao extends BaseMapper<AlarmName> {

    /**
     * 查询告警名称
     * @return 告警名称信息
     */
    List<AlarmName> selectAlarmName();

    /**
     * 查询新增告警设置页面
     *
     * @return 判断结果
     */
    List<AlarmName> selectAlarmNames();

    /**
     * 新增告警设置
     *
     * @param alarmName 告警设置信息
     * @return 判断结果
     */
    Integer updateAlarmName(AlarmName alarmName);

    /**
     * 删除告警设置信息
     *
     * @param alarmId 告警ID数组
     * @return 判断结果
     */
    Integer deleteAlarmName(String alarmId);
}
