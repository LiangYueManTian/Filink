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
     * @param queryCondition 条件
     * @return 告警名称信息
     */
    Result queryAlarmNameList(QueryCondition queryCondition);

    /**
     * 查询告警名称信息列表
     *
     * @param queryCondition 条件
     * @return 告警名称信息
     */
    Result queryAlarmNamePage(QueryCondition queryCondition);

    /**
     * 根据id查询告警设置信息
     *
     * @param array 告警ID
     * @return 告警设置信息
     */
    Result queryAlarmCurrentSetById(String[] array);

    /**
     * 根据告警编码查询告警名称信息
     *
     * @param alarmCode 告警编码
     * @return 告警名称信息
     */
    AlarmName queryCurrentAlarmSetFeign(String alarmCode);

    /**
     * 根据告警名称查询当前告警设置信息
     *
     * @param alarmName 告警名称
     * @return 告警设置信息
     */
    AlarmName queryCurrentAlarmSetByNameFeign(String alarmName);
    /**
     * 修改告警设置信息
     *
     * @param alarmName 告警信息
     * @return 判断结果
     */
    Result updateAlarmCurrentSet(AlarmName alarmName);

}
