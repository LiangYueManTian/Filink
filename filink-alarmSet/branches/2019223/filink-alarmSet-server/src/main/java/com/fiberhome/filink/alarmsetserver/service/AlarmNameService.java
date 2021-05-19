package com.fiberhome.filink.alarmsetserver.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.alarmsetserver.bean.AlarmName;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;

/**
 * <p>
 *  当前告警服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public interface AlarmNameService extends IService<AlarmName> {

    /**
     * 查询告警名称信息列表
     *
     * @param queryCondition 封装条件
     * @return 告警名称信息
     */
    Result queryAlarmNameList(QueryCondition queryCondition);

    /**
     * 查询单个告警设置信息
     *
     * @param alarmId 告警ID
     * @return 告警设置信息
     */
    Result queryAlarmCurrentSetById(String alarmId);

    /**
     * 修改告警设置信息
     *
     * @param alarmName 告警信息
     * @return 判断结果
     */
    Result updateAlarmCurrentSet(AlarmName alarmName);


}
