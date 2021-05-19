package com.fiberhome.filink.alarmsetserver.service;


import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRule;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRuleDto;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRuleForArea;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.userapi.bean.User;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-27
 */
public interface AlarmOrderRuleService extends IService<AlarmOrderRule> {

    /**
     * 查询告警转工单列表信息
     *
     * @param queryCondition 条件信息
     * @return 告警转工单列表信息
     */
    Result queryAlarmOrderRuleList(QueryCondition<AlarmOrderRuleDto> queryCondition);

    /**
     * 查询告警转工单信息
     *
     * @param id 告警id
     * @return 告警转工单信息
     */
    Result queryAlarmOrderRule(String id);

    /**
     * 添加告警转工单信息
     *
     * @param alarmOrderRule 告警转工单信息
     * @return 判断结果
     */
    Result addAlarmOrderRule(AlarmOrderRule alarmOrderRule);

    /**
     * 删除告警转工单信息
     *
     * @param array 告警转工单id
     * @return 判断信息
     */
    Result deleteAlarmOrderRule(String[] array);

    /**
     * 修改告警转工单信息
     *
     * @param alarmOrderRule 告警转工单信息
     * @return 判断结果
     */
    Result updateAlarmOrderRule(AlarmOrderRule alarmOrderRule);

    /**
     * 修改告警转工单状态信息
     *
     * @param status  告警转工单状态信息
     * @param idArray 用户id
     * @return 判断结果
     */
    Result updateAlarmOrderRuleStatus(Integer status, String[] idArray);

    /**
     * 查询当前告警转工单规则
     *
     * @param alarmOrderCondition 告警转工单条件
     * @return 告警转工单规则信息
     */
    AlarmOrderRule queryAlarmOrderRuleFeign(List<AlarmOrderCondition> alarmOrderCondition);

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    User getUser();

    /**
     * 获取用户区设施类型信息
     *
     * @param user 用户
     * @return 用户区设施类型信息
     */
    List<String> getDeviceTypes(User user);

    /**
     * 获取用户区域信息
     *
     * @param user 用户
     * @return 用户区域信息
     */
    List<String> getUserAreaIds(User user);


    /**
     * 根据部门id集合查询是否存在当前告警转工单规则
     * @param departmentIds 部门id集合
     * @return 是否存在
     */
    Result queryAlarmOrderRuleByDeptIds(List<String> departmentIds);

    /**
     * 根据区域ID，部门id集合查询是否存在当前告警转工单规则
     * @param alarmOrderRuleForArea 区域ID，部门id集合
     * @return 是否存在
     */
    Result queryAlarmOrderRuleByAreaDeptIds(AlarmOrderRuleForArea alarmOrderRuleForArea);
}
