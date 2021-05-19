package com.fiberhome.filink.alarmsetserver.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmName;
import org.springframework.web.bind.annotation.PathVariable;

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
     * 查询单个告警设置信息
     *
     * @param array 告警设置ID
     * @return 告警名称信息
     */
    List<AlarmName> selectByIds(String[] array);

    /**
     * 查询所有告警设置
     * @return 所有告警设置
     */
    List<AlarmName> selectAlarmNameAll();

    /**
     * 根据告警编码查询告警名称信息
     *
     * @param alarmCode 告警编码
     * @return 告警名称信息
     */
    AlarmName selectAlarmByCode(@PathVariable("alarmCode")String alarmCode);

    /**
     * 根据告警名称查询当前告警设置信息
     *
     * @param alarmName 告警名称
     * @return 告警设置信息
     */
    AlarmName selectAlarmByName(@PathVariable("alarmName")String alarmName);
}
