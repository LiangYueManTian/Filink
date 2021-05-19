package com.fiberhome.filink.alarmsetserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRuleLevel;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-04-02
 */
public interface AlarmForwardRuleLevelDao extends BaseMapper<AlarmForwardRuleLevel> {

    /**
     * 批量删除告警远程通知信息
     *
     * @param array 告警id
     * @return 判断结果
     */
    Integer batchDeleteAlarmLevel(String[] array);

    /**
     * 删除告警远程通知信息
     * @param id
     * @return
     */
    Integer deleteAlarmLevel(String id);

    /**
     * 添加告警级别信息
     *
     * @param alarmLevels 告警级别信息
     * @return 判断结果
     */
    Integer addAlarmLevel(List<AlarmForwardRuleLevel> alarmLevels);

    /**
     *根据id查询告警级别
     *
     * @param id 告警id
     * @return 判断结果
     */
    List<AlarmForwardRuleLevel> queryAlarmFilterRuleAlarmLevelById(@Param("id") String id);
}
