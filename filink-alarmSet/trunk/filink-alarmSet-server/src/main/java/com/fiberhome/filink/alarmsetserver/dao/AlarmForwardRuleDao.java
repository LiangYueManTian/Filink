package com.fiberhome.filink.alarmsetserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRule;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRuleDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-25
 */
public interface AlarmForwardRuleDao extends BaseMapper<AlarmForwardRule> {

    /**
     * 查询告警远程通知信息
     *
     * @param alarmForwardRuleDto 告警远程通知dto
     * @return 告警远程通知信息
     */
    List<AlarmForwardRule> queryAlarmForwardRuleList(AlarmForwardRuleDto alarmForwardRuleDto);

    /**
     * 根据id查询远程通知信息
     *
     * @param id 告警id
     * @return 远程通知信息
     */
    List<AlarmForwardRule> queryAlarmForwardId(@Param("id") String id);

    /**
     * 删除告警远程通知信息
     *
     * @param array 告警远程通知信息
     * @return 判断结果
     */
    Integer deleteAlarmForwardRule(String[] array);

    /**
     * 修改告警远程通知状态信息
     *
     * @param status  告警远程通知状态信息
     * @param idArray 用户id
     * @return 判断结果
     */
    Integer updateAlarmForwardRuleStatus(@Param("status") Integer status, @Param("idArray") String[] idArray);

    /**
     * 修改告警远程通知推送类型信息
     *
     * @param pushType 推送类型信息
     * @param idArray  用户id
     * @return 判断结果
     */
    Integer updateAlarmForwardRulePushType(@Param("pushType") Integer pushType, @Param("idArray") String[] idArray);

    /**
     * 查询当前告警远程通知规则
     *
     * @param alarmForwardCondition 远程通知条件
     * @return 告警远程通知信息
     */
    List<AlarmForwardRule> queryAlarmForwardRuleFeign(AlarmForwardCondition alarmForwardCondition);
}
