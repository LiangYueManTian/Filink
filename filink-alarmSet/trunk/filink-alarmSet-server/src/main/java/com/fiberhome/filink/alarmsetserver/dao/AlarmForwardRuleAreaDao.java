package com.fiberhome.filink.alarmsetserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRuleArea;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-26
 */
public interface AlarmForwardRuleAreaDao extends BaseMapper<AlarmForwardRuleArea> {

    /**
     * 批量删除区域id信息
     *
     * @param array 告警id
     * @return 判断结果
     */
    Integer batchDeleteAlarmForwardRuleArea(String[] array);

    /**
     * 批量新增区域id信息
     *
     * @param alarmForwardRuleAreaList 告警远程通知信息
     * @return 判断结果
     */
    Integer addAlarmForwardRuleArea(List<AlarmForwardRuleArea> alarmForwardRuleAreaList);

    /**
     * 删除区域id信息
     *
     * @param id 告警id
     * @return 判断结果
     */
    Integer deleteAlarmForwardRuleArea(String id);
}
