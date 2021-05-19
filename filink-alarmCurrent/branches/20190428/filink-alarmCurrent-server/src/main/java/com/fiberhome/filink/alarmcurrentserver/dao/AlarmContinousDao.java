package com.fiberhome.filink.alarmcurrentserver.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmContinous;

/**
 * <p>
 * 告警持续时间Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-15
 */
public interface AlarmContinousDao extends BaseMapper<AlarmContinous> {

    /**
     * 新增告警持续时间
     *
     * @param list 告警名称信息
     * @return 判断结果
     */
    Integer addAlarmContinous(AlarmContinous alarmContinous);


    /**
     * 修改告警持续时间
     *
     * @param list 告警名称信息
     * @return 判断结果
     */
    Integer batchUpdateAlarmContinous(AlarmContinous alarmContinous);


    /**
     * 删除关联告警持续时间
     *
     * @param array 告警id
     * @return 判断结果
     */
    Integer batchDeleteAlarmContinous(String[] array);

    /**
     * 查询持续时间信息
     *
     * @param id 告警id
     * @return 告警模板信息
     */
    AlarmContinous queryAlarmContinousById(String id);
}
