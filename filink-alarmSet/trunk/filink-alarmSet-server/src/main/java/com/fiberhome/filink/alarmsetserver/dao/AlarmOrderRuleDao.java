package com.fiberhome.filink.alarmsetserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRule;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRuleDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-27
 */
public interface AlarmOrderRuleDao extends BaseMapper<AlarmOrderRule> {

    /**
     * 查询告警转工单列表信息
     *
     * @param alarmOrderRuleDto 告警转工单DTO
     * @return 告警转工单列表信息
     */
    List<AlarmOrderRule> queryAlarmOrderRuleList(AlarmOrderRuleDto alarmOrderRuleDto);

    /**
     * 查询告警转工单信息
     *
     * @param id 告警id
     * @return 告警转工单信息
     */
    List<AlarmOrderRule> queryAlarmOrderRuleById(@Param("id") String id);

    /**
     * 删除告警转工单信息
     *
     * @param array 告警id
     * @return 判断结果
     */
    Integer deleteAlarmOrderRule(@Param("array") String[] array);

    /**
     * 修改告警转工单状态信息
     *
     * @param status  告警转工单状态信息
     * @param idArray 用户id
     * @return 判断结果
     */
    Integer updateAlarmOrderRuleStatus(@Param("status") Integer status, @Param("idArray") String[] idArray);

    /**
     * 查询当前告警转工单规则
     *
     * @param alarmOrderCondition 告警转工单条件
     * @return 告警转工单规则信息
     */
    List<AlarmOrderRule> queryAlarmOrderRuleFeign(AlarmOrderCondition alarmOrderCondition);

    /**
     * 根据部门id集合查询当前告警转工单规则
     * @param departmentIds 部门id集合
     * @return 当前告警转工单规则id
     */
    List<String> queryAlarmOrderRuleByDeptIds(List<String> departmentIds);

    /**
     * 根据区域ID，部门id集合查询是否存在当前告警转工单规则
     *
     * @param areaId 区域ID
     * @param departmentIds 部门id集合
     * @return 当前告警转工单规则id
     */
    List<String> queryAlarmOrderRuleByAreaDeptIds(@Param("areaId") String areaId, @Param("departmentIds") List<String> departmentIds);
}
