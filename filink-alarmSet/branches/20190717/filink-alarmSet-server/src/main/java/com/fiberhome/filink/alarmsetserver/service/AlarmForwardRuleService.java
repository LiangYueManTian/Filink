package com.fiberhome.filink.alarmsetserver.service;


import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRule;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRuleDto;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-25
 */
public interface AlarmForwardRuleService extends IService<AlarmForwardRule> {

    /**
     * 查询告警远程通知列表信息
     *
     * @param queryCondition 告警远程通知dto
     * @return 告警远程通知列表信息
     */
    Result queryAlarmForwardRuleList(QueryCondition<AlarmForwardRuleDto> queryCondition);

    /**
     * 根据id查询远程通知信息
     *
     * @param id 告警id
     * @return 远程通知信息
     */
    Result queryAlarmForwardId(String id);

    /**
     * 新增告警远程通知信息
     *
     * @param alarmForwardRule 告警远程通知信息
     * @return 判断结果
     */
    Result addAlarmForwardRule(AlarmForwardRule alarmForwardRule);

    /**
     * 删除告警远程通知信息
     *
     * @param array 告警远程通知id
     * @return 判断结果
     */
    Result deleteAlarmForwardRule(String[] array);

    /**
     * 修改告警远程通知信息
     *
     * @param alarmForwardRule 告警远程通知信息
     * @return 判断结果
     */
    Result updateAlarmForwardRule(AlarmForwardRule alarmForwardRule);

    /**
     * 修改告警远程通知状态信息
     *
     * @param status 告警远程通知状态信息
     * @param idArray 用户id
     * @return 判断结果
     */
    Result batchUpdateAlarmForwardRuleStatus(Integer status, String[] idArray);

    /**
     * 修改告警远程通知推送类型信息
     *
     * @param pushType 推送类型信息
     * @param idArray 用户id
     * @return 判断结果
     */
    Result batchUpdateAlarmForwardRulePushType(Integer pushType, String[] idArray);

    /**
     * 查询当前告警远程通知规则
     *
     * @param alarmForwardCondition 远程通知条件
     * @return 告警远程通知信息
     */
    List<AlarmForwardRule> queryAlarmForwardRuleFeign(List<AlarmForwardCondition> alarmForwardCondition);
}
