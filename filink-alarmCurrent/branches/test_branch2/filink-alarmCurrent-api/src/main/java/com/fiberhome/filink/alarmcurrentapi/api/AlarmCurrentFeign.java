package com.fiberhome.filink.alarmcurrentapi.api;

import com.fiberhome.filink.alarmcurrentapi.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentapi.fallback.AlarmFeignFallback;
import com.fiberhome.filink.bean.Result;
import java.util.List;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 当前告警模块feign测试 中转站
 *
 * @author wtao103@fiberhome.com
 * create on 2018/12/16 5:19 PM
 */
@FeignClient(name = "filink-alarmcurrent-server", fallback = AlarmFeignFallback.class)
public interface AlarmCurrentFeign {

    /**
     * 查询设备信息是否存在
     *
     * @param alarmSources 设备id
     * @return 判断结果
     */
    @PostMapping("/alarmCurrent/queryAlarmSourceForFeign")
    List<String> queryAlarmSourceForFeign(@RequestBody List<String> alarmSources);

    /**
     * 查询区域信息是否存在
     *
     * @param areas 区域id
     * @return 判断结果
     */
    @PostMapping("/alarmCurrent/queryAreaForFeign")
    List<String> queryAreaForFeign(@RequestBody List<String> areas);

    /**
     * 查询单位id信息
     *
     * @param alarmIds 告警id
     * @return 单位id信息
     */
    @PostMapping("/alarmCurrent/queryDepartmentIdFeign")
    Result queryDepartmentIdFeign(@RequestBody List<String> alarmIds);

    /**
     * 当前告警转历史告警定时任务
     */
    @PostMapping("/alarmReceive/alarmCurrentCastAlarmHistoryTaskFeign")
    void alarmCurrentCastAlarmHistoryTaskFeign();

    /**
     * 查询当前告警信息
     *
     * @param list 告警id
     * @return 判断结果
     */
    @PostMapping("/alarmCurrent/queryAlarmCurrentByIdsFeign")
    List<AlarmCurrent> queryAlarmCurrentByIdsFeign(@RequestBody List<String> list);

    /**
     * 查询告警门信息
     *
     * @param ids 告警id
     * @return 告警门信息
     */
    @PostMapping("/alarmCurrent/queryAlarmDoorByIdsFeign")
    List<AlarmCurrent> queryAlarmDoorByIdsFeign(@RequestBody List<String> ids);

    /**
     * 删除告警成功
     *
     * @param deviceIds 设施id
     * @return 判断结果
     */
    @PostMapping("/alarmCurrent/batchDeleteAlarmsFeign")
    Result batchDeleteAlarmsFeign(@RequestBody List<String> deviceIds);

    /**
     * 根据单位id查询告警信息
     *
     * @param departmentIds 单位id
     * @return 判断结果
     */
    @PostMapping("/alarmCurrent/queryAlarmDepartmentFeign")
    boolean queryAlarmDepartmentFeign(@RequestBody List<String> departmentIds);

    /**
     * 告警设施id查询c当前告警ode信息
     *
     * @param deviceId 设施id
     * @return 当前告警code信息
     */
    @PostMapping("/alarmCurrent/queryAlarmDeviceIdCode/{deviceId}")
    List<AlarmCurrent> queryAlarmDeviceIdCode(@PathVariable("deviceId") String deviceId);
}
