package com.fiberhome.filink.alarmsetserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRuleDeviceType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-26
 */
public interface AlarmForwardRuleDeviceTypeDao extends BaseMapper<AlarmForwardRuleDeviceType> {

    /**
     * 批量删除设施类型id
     *
     * @param array 告警id
     * @return 判断结果
     */
    Integer batchDeleteAlarmDeviceType(String[] array);

    /**
     * 批量新增设施类型id
     *
     * @param alarmForwardRuleDeviceTypeList 设施类型信息
     * @return 判断结果
     */
    Integer addAlarmForwardRuleDeviceType(List<AlarmForwardRuleDeviceType> alarmForwardRuleDeviceTypeList);

    /**
     * 删除设施类型id
     *
     * @param id 告警id
     * @return 判断结果
     */
    Integer deleteAlarmDeviceType(String id);

    /**
     *根据id查询设施类型
     *
     * @param id 告警id
     * @return 判断结果
     */
    List<AlarmForwardRuleDeviceType> queryAlarmForwardRuleDeviceTypeById(@Param("id") String id);
}
