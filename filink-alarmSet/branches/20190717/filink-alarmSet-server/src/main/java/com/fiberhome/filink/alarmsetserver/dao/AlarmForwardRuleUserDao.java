package com.fiberhome.filink.alarmsetserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRuleUser;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-25
 */
public interface AlarmForwardRuleUserDao extends BaseMapper<AlarmForwardRuleUser> {

    /**
     * 批量删除通知人id
     *
     * @param array 告警id
     * @return 判断结果
     */
    Integer batchDeleteAlarmForwardRuleUser(String[] array);

    /**
     * 批量新增通知人id
     * @param alarmForwardRuleUsers 通知人信息
     * @return 判断结果
     */
    Integer addAlarmForwardRuleUser(List<AlarmForwardRuleUser> alarmForwardRuleUsers);

    /**
     * 删除通知人id
     *
     * @param id 通知人id
     * @return 判断结果
     */
    Integer deleteAlarmForwardRuleUser(String id);
}
