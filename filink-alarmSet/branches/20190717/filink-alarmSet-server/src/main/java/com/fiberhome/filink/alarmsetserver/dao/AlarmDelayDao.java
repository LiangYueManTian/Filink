package com.fiberhome.filink.alarmsetserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmDelay;

/**
 * <p>
 * 告警设置延长时间Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */

public interface AlarmDelayDao extends BaseMapper<AlarmDelay> {

    /**
     * 查询历史设置信息
     *
     * @return 历史设置信息
     */
    AlarmDelay selectDelay();

    /**
     * 修改延时入库时间
     *
     * @param alarmDelay 延时入库信息
     * @return 判断结果
     */
    Integer updateDelay(AlarmDelay alarmDelay);
}
