package com.fiberhome.filink.alarmcurrentapi.fallback;


import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.alarmcurrentapi.bean.AlarmCurrent;
import com.fiberhome.filink.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 当前告警熔断
 *
 * @author wtao103@fiberhome.com
 * create on 2018/12/16 5:39 PM
 */
@Slf4j
@Component
public class AlarmFeignFallback implements AlarmCurrentFeign {

    /**
     * 查询设备信息是否存在
     *
     * @param alarmSources 设备id
     * @return 判断结果
     */
    @Override
    public List<String> queryAlarmSourceForFeign(List<String> alarmSources) {
        log.warn("queryAlarmSourceForFeign failed");
        return null;
    }

    /**
     * 查询区域信息是否存在
     *
     * @param areas 区域id
     * @return 判断结果
     */
    @Override
    public List<String> queryAreaForFeign(List<String> areas) {
        log.warn("queryAreaForFeign failed");
        return null;
    }

    /**
     * 查询单位id信息
     *
     * @param alarmIds 告警id
     * @return 单位id信息
     */
    @Override
    public Result queryDepartmentIdFeign(List<String> alarmIds) {
        log.warn("queryDepartmentIdFeign failed");
        return null;
    }

    /**
     * 当前告警转历史告警定时任务
     */
    @Override
    public void alarmCurrentCastAlarmHistoryTaskFeign() {
        log.warn("alarmCurrentCastAlarmHistoryTaskFegin failed");
    }

    /**
     * 查询当前告警信息
     *
     * @param list 告警id
     * @return 判断结果
     */
    @Override
    public List<AlarmCurrent> queryAlarmCurrentByIdsFeign(List<String> list) {
        log.warn("queryAlarmCurrentByIdsFeign failed");
        return null;
    }

    /**
     * 查询告警门信息
     *
     * @param ids 告警id
     * @return 告警门信息
     */
    @Override
    public List<AlarmCurrent> queryAlarmDoorByIdsFeign(List<String> ids) {
        log.warn("queryAlarmDoorByIdsFeign failed");
        return null;
    }

    /**
     * 删除告警成功
     *
     * @param deviceIds 设施id
     * @return 判断结果
     */
    @Override
    public Result batchDeleteAlarmsFeign(List<String> deviceIds) {
        log.warn("batchDeleteAlarmsFeign failed");
        return null;
    }

    /**
     * 根据单位id查询告警信息
     *
     * @param departmentIds 单位id
     * @return 判断结果
     */
    @Override
    public boolean queryAlarmDepartmentFeign(List<String> departmentIds) {
        log.warn("queryAlarmDepartmentFeign failed");
        return false;
    }

    /**
     * 告警设施id查询c当前告警ode信息
     *
     * @param deviceId 设施id
     * @return 当前告警code信息
     */
    @Override
    public List<AlarmCurrent> queryAlarmDeviceIdCode(String deviceId) {
        log.warn("queryAlarmDepartmentFeign failed");
        return null;
    }
}
