package com.fiberhome.filink.alarmsetserver.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmLevel;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>
 *  告警级别设置Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public interface AlarmLevelDao extends BaseMapper<AlarmLevel> {

    /**
     * 查询告警级别
     *
     * @return 告警级别信息
     */
    List<AlarmLevel> selectAlarmLevel();

    /**
     * 查询告警级别颜色信息
     *
     * @param id 告警id
     * @return 告警级别颜色信息
     */
    List<AlarmLevel> queryAlarmLevelColor(@Param("id") String id);

    /**
     *  根据告警级别编码查询告警级别设置信息
     * @param alarmLevelCode  告警级别编码
     * @return
     */
    AlarmLevel queryAlarmLevelSetFeign(@PathVariable("alarmLevelCode") String alarmLevelCode);
}
