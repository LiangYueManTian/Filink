package com.fiberhome.filink.alarmsetserver.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterRuleSource;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-25
 */
public interface AlarmFilterRuleSourceDao extends BaseMapper<AlarmFilterRuleSource> {

    /**
     * 批量删除关联告警源id
     *
     * @param array 告警id
     * @return 判断结果
     */
    Integer batchDeleteAlarmFilterRuleSource(String[] array);

    /**
     * 删除关联告警源id
     *
     * @param ruleId 告警id
     * @return 判断结果
     */
    Integer deleteAlarmFilterRuleSource(String ruleId);

    /**
     * 批量新增告警源id
     *
     * @param list 告警id
     * @return 判断结果
     */
    Integer addAlarmFilterRuleSource(List<AlarmFilterRuleSource> list);
}
