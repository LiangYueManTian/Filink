package com.fiberhome.filink.alarmcurrentserver.service;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.userapi.bean.User;
import java.util.List;

/**
 * <p>
 * 当前告警服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public interface AlarmCurrentService {

    /**
     * 查询当前告警信息列表
     *
     * @param queryCondition 查询条件封装
     * @return 当前告警信息
     */
    PageBean queryAlarmCurrentList(QueryCondition<AlarmCurrent> queryCondition, User user, boolean needsAuth);

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    User getUser();

    /**
     * 查询当前告警数量
     * @param queryCondition
     * @param user
     * @return
     */
    Integer getCount(QueryCondition queryCondition, User user);

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    User getExportUser();

    /**
     * 查询当前告警信息列表
     *
     * @param queryCondition 查询条件封装
     * @return 当前告警信息
     */
    Result queryAlarmCurrentPage(QueryCondition<AlarmCurrent> queryCondition);

    /**
     * 查询当前告警信息
     *
     * @param list 告警id
     * @return 当前告警信息
     */
    List<AlarmCurrent> queryAlarmCurrentByIdsFeign(List<String> list);

    /**
     * 查询单个当前告警信息
     *
     * @param alarmId 当前告警ID
     * @return 当前告警信息
     */
    Result queryAlarmCurrentInfoById(String alarmId);

    /**
     * 批量修改当前告警备注信息
     *
     * @param alarmCurrents 当前告警信息
     * @return 判断结果
     */
    Result batchUpdateAlarmRemark(List<AlarmCurrent> alarmCurrents);

    /**
     * 批量设置当前告警的告警确认状态
     *
     * @param alarmCurrents 当前告警用户信息
     * @return 判断结果
     */
    Result batchUpdateAlarmConfirmStatus(List<AlarmCurrent> alarmCurrents);

    /**
     * 批量设置当前告警的告警清除状态
     *
     * @param alarmCurrents 当前告警用户信息
     * @return 判断结果
     */
    Result batchUpdateAlarmCleanStatus(List<AlarmCurrent> alarmCurrents);

    /**
     * 查询各级别告警总数
     *
     * @param alarmLevel 级别告警信息
     * @return 级别信息
     */
    Result queryEveryAlarmCount(String alarmLevel);

    /**
     * 设施id查询告警信息
     *
     * @param deviceId 设施id
     * @return 判断结果
     */
    Result queryAlarmDeviceId(String deviceId);

    /**
     * 告警设施id查询c当前告警ode信息
     *
     * @param deviceId 设施id
     * @return 当前告警code信息
     */
    List<AlarmCurrent> queryAlarmDeviceIdCode(String deviceId);

    /**
     * 查询设备信息是否存在
     *
     * @param alarmSources 设备id
     * @return 判断结果
     */
    List<String> queryAlarmSourceForFeign(List<String> alarmSources);

    /**
     * 查询区域信息是否存在
     *
     * @param areaIds 区域ID
     * @return 判断结果
     */
    List<String> queryAreaForFeign(List<String> areaIds);

    /**
     * 查询单位id信息
     *
     * @param alarmIds 告警id
     * @return 单位id信息
     */
    Result queryDepartmentId(List<String> alarmIds);

    /**
     * 查询告警类型
     *
     * @param id 告警id
     * @return 判断结果
     */
    Result queryIsStatus(String id);

    /**
     * 查询告警门信息
     *
     * @param ids 告警id
     * @return 告警门信息
     */
    List<AlarmCurrent> queryAlarmDoor(List<String> ids);

    /**
     * 删除告警信息
     *
     * @param deviceIds 设施id
     * @return 判断结果
     */
    Result deleteAlarms(List<String> deviceIds);

    /**
     * 根据单位id查询告警信息
     *
     * @param departmentIds 单位id
     * @return 判断结果
     */
    boolean queryAlarmDepartmentFeign(List<String> departmentIds);
}
