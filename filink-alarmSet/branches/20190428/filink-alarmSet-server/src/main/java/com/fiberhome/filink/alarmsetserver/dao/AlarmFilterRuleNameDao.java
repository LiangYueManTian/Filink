package com.fiberhome.filink.alarmsetserver.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterRuleName;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-25
 */
public interface AlarmFilterRuleNameDao extends BaseMapper<AlarmFilterRuleName> {

    /**
     * 批量删除关联告警名称id
     *
     * @param array 告警id
     * @return 判断结果
     */
    Integer batchDeleteAlarmFilterRuleName(String[] array);

    /**
     * 删除关联告警名称id
     *
     * @param ruleId 告警id
     * @return 判断结果
     */
    Integer deleteAlarmFilterRuleName(String ruleId);

    /**
     * 批量新增告警名称信息
     *
     * @param list 告警名称信息
     * @return 判断结果
     */
    Integer addAlarmFilterRuleName(List<AlarmFilterRuleName> list);

    /**
     *根据id查询告警名称
     *
     * @param id 告警id
     * @return 判断结果
     */
    List<AlarmFilterRuleName> queryAlarmFilterRuleNameById(@Param("id") String id);


}
