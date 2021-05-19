package com.fiberhome.filink.alarmsetserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRuleArea;
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
public interface AlarmOrderRuleAreaDao extends BaseMapper<AlarmOrderRuleArea> {

    /**
     * 批量添加区域id
     *
     * @param alarmOrderRuleAreas 区域信息
     * @return 判断结果
     */
    Integer addAlarmOrderRuleArea(List<AlarmOrderRuleArea> alarmOrderRuleAreas);

    /**
     * 批量删除区域id
     *
     * @param array 区域信息id
     * @return 判断结果
     */
    Integer batchDeleteAlarmOrderRuleArea(@Param("array") String[] array);

    /**
     * 删除区域id
     *
     * @param id 告警id
     * @return 判断结果
     */
    Integer deleteAlarmOrderRuleArea(String id);
}
