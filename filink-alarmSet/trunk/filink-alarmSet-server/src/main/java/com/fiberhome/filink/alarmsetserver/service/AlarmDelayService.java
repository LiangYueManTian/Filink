package com.fiberhome.filink.alarmsetserver.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.alarmsetserver.bean.AlarmDelay;
import com.fiberhome.filink.bean.Result;

/**
 * <p>
 *  历史告警服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public interface AlarmDelayService extends IService<AlarmDelay> {

    /**
     * 查询历史设置信息
     *
     * @return 历史设置信息
     */
    Result selectAlarmDelay();

    /**
     * 修改延时入库时间
     *
     * @param alarmDelay 延时入库信息
     * @return 判断结果
     */
    Result updateAlarmDelay(AlarmDelay alarmDelay);

    /**
     * 定时任务查询历史告警设置时间
     *
     * @return 历史告警设置时间
     */
    Integer selectAlarmDelayTime();
}
