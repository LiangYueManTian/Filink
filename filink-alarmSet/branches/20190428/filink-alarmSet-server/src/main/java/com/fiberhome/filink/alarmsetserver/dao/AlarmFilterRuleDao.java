package com.fiberhome.filink.alarmsetserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterRule;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterRuleDto;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-01
 */
public interface AlarmFilterRuleDao extends BaseMapper<AlarmFilterRule> {

    /**
     * 查询告警过滤信息
     *
     * @param alarmFilterRule 告警过滤DTO
     * @return 告警过滤信息
     */
    List<AlarmFilterRule> queryAlarmFilterRuleList(AlarmFilterRuleDto alarmFilterRule);

    /**
     * 过滤告警名称
     *
     * @param id 告警id
     * @return 过滤信息
     */
    List<AlarmFilterRule> queryAlarmFilterRuleName(String id);

    /**
     * 过滤告警名称
     *
     * @return 过滤信息
     */
    List<AlarmFilterRule> queryAlarmFilterRuleNames();

    /**
     * 根据id查询告警过滤信息
     *
     * @param id 过滤id
     * @return 告警过滤信息
     */
    List<AlarmFilterRule> queryAlarmFilterRuleById(@Param("id") String id);

    /**
     * 删除告警过滤信息
     *
     * @param array 告警过滤id
     * @return 判断结果
     */
    Integer deleteAlarmFilterRule(String[] array);

    /**
     * 修改启用状态信息
     *
     * @param status  状态
     * @param idArray 用户id
     * @return 判断结果
     */
    Integer updateAlarmFilterRuleStatus(@Param("status") Integer status, @Param("idArray") String[] idArray);

    /**
     * 修改告警过滤存库信息
     *
     * @param stored  存库
     * @param idArray 用户id
     * @return 判断结果
     */
    Integer updateAlarmFilterRuleStored(@Param("stored") Integer stored, @Param("idArray") String[] idArray);

    /**
     * 查询当前告警信息是否过被过滤
     *
     * @param alarmFilterConditionList 过滤条件
     * @return 过滤信息
     */
    List<AlarmFilterRule> queryAlarmIsIncludedFeign(AlarmFilterCondition alarmFilterConditionList);

    /**
     * 查询告警过滤信息
     *
     * @return 告警过滤信息
     */
    List<AlarmFilterRule> queryAlarmFilterRuleLists();
}
