package com.fiberhome.filink.alarmsetserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRuleDeviceType;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-29
 */
public interface AlarmOrderRuleDeviceTypeDao extends BaseMapper<AlarmOrderRuleDeviceType> {

    /**
     * 批量新增设施id
     *
     * @param alarmOrderRuleDeviceTypes 设施信息
     * @return 判断结果
     */
    Integer addAlarmOrderDeviceType(List<AlarmOrderRuleDeviceType> alarmOrderRuleDeviceTypes);

    /**
     * 批量删除设施id
     *
     * @param array 设施id
     * @return 判断结果
     */
    Integer batchDeleteAlarmOrderDeviceType(@Param("array")String[] array);

    /**
     * 删除设施id
     *
     * @param id 告警id
     * @return 判断结果
     */
    Integer deleteAlarmOrderDeviceType(String id);

    /**
     *根据id查询设施类型
     *
     * @param id 告警id
     * @return 判断结果
     */
    List<AlarmOrderRuleDeviceType> queryAlarmOrderRuleDeviceTypeById(@Param("id") String id);
}
