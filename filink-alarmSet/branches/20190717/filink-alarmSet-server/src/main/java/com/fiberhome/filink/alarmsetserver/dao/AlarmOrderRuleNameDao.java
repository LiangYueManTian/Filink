package com.fiberhome.filink.alarmsetserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRuleName;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-29
 */
public interface AlarmOrderRuleNameDao extends BaseMapper<AlarmOrderRuleName> {

    /**
     * 批量新增告警名称
     *
     * @param alarmOrderRuleNames 告警名称信息
     * @return 判断结果
     */
    Integer addAlarmOrderRuleName(List<AlarmOrderRuleName> alarmOrderRuleNames);

    /**
     * 批量删除告警名称
     *
     * @param array 告警名称id
     * @return 判断结果
     */
    Integer batchDeleteAlarmOrderRuleName(@Param("array") String[] array);

    /**
     * 删除告警名称信息
     *
     * @param id 告警id
     * @return 判断结果
     */
    Integer deleteAlarmOrderRuleName(String id);

    /**
     *根据id查询告警名称
     *
     * @param id 告警id
     * @return 判断结果
     */
    List<AlarmOrderRuleName> queryAlarmOrderRuleNameById(@Param("id") String id);
}
