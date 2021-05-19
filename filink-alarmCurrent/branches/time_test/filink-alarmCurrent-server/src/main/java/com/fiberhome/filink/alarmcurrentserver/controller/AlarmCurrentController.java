package com.fiberhome.filink.alarmcurrentserver.controller;


import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmMessage;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmSourceLevelParameter;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentExportService;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentService;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmDisposeService;
import com.fiberhome.filink.alarmcurrentserver.stream.AlarmStreams;
import com.fiberhome.filink.alarmcurrentserver.utils.ListUtil;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.bean.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 当前告警前端控制器
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@RestController
@RequestMapping("/alarmCurrent")
public class AlarmCurrentController {

    /**
     * 当前告警Service
     */
    @Autowired
    private AlarmCurrentService alarmCurrentService;

    /**
     * webSockert
     */
    @Autowired
    private AlarmStreams alarmStreams;

    /**
     * 导出Service
     */
    @Autowired
    private AlarmCurrentExportService alarmExportService;

    /**
     * 告警处理服务类
     */
    @Autowired
    private AlarmDisposeService alarmDisposeService;

    /**
     * 查询当前告警列表信息
     *
     * @param queryCondition 封装条件
     * @return 当前告警列表信息
     */
    @PostMapping("/queryAlarmCurrentList")
    public Result queryAlarmCurrentList(@RequestBody QueryCondition<AlarmCurrent> queryCondition) {
        User user;
        String userId = RequestInfoUtils.getUserId();
        if (AppConstant.ONE.equals(userId)) {
            user = new User();
            user.setId(userId);
        } else {
            user = alarmCurrentService.getUser();
        }
        PageBean pageBean = alarmCurrentService.queryAlarmCurrentList(queryCondition, user, true);
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * 查询当前告警列表信息
     *
     * @param queryCondition 封装条件
     * @return 当前告警列表信息
     */
    @PostMapping("/queryAlarmCurrentPage")
    public Result queryAlarmCurrentPage(@RequestBody QueryCondition<AlarmCurrent> queryCondition) {
        return alarmCurrentService.queryAlarmCurrentPage(queryCondition);
    }

    /**
     * 查询当前告警信息
     *
     * @param list 告警id
     * @return 当前告警信息
     */
    @PostMapping("/queryAlarmCurrentByIdsFeign")
    public List<AlarmCurrent> queryAlarmCurrentByIdsFeign(@RequestBody List<String> list) {
        if (ListUtil.isEmpty(list)) {
            return null;
        }
        return alarmCurrentService.queryAlarmCurrentByIdsFeign(list);
    }

    /**
     * 查询单个当前告警的详细信息
     *
     * @param alarmId 告警id
     * @return 查询结果
     */
    @PostMapping("/queryAlarmCurrentInfoById/{alarmId}")
    public Result queryAlarmCurrentInfoById(@PathVariable String alarmId) {
        if (StringUtils.isEmpty(alarmId)) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.ALARM_ID_NULL));
        }
        return alarmCurrentService.queryAlarmCurrentInfoById(alarmId);
    }

    /**
     * 批量修改当前告警备注信息
     *
     * @param alarmCurrents 当前告警信息
     * @return 判断结果
     */
    @PostMapping("/updateAlarmRemark")
    public Result batchUpdateAlarmRemark(@RequestBody List<AlarmCurrent> alarmCurrents) {
        if (ListUtil.isEmpty(alarmCurrents)) {
            return ResultUtils.warn(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmCurrentService.batchUpdateAlarmRemark(alarmCurrents);
    }

    /**
     * 批量设置当前告警的告警确认状态
     *
     * @param alarmCurrents 当前告警用户信息
     * @return 判断结果
     */
    @PostMapping("/updateAlarmConfirmStatus")
    public Result batchUpdateAlarmConfirmStatus(@RequestBody List<AlarmCurrent> alarmCurrents) {
        if (ListUtil.isEmpty(alarmCurrents)) {
            return ResultUtils.warn(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmCurrentService.batchUpdateAlarmConfirmStatus(alarmCurrents);
    }

    /**
     * 批量设置当前告警的告警清除状态
     *
     * @param alarmCurrents 当前告警用户信息
     * @return 判断结果
     */
    @PostMapping("/updateAlarmCleanStatus")
    public Result batchUpdateAlarmCleanStatus(@RequestBody List<AlarmCurrent> alarmCurrents) {
        if (ListUtil.isEmpty(alarmCurrents)) {
            return ResultUtils.success(I18nUtils.getSystemString(AppConstant.ALARM_USER_NULL));
        }
        return alarmCurrentService.batchUpdateAlarmCleanStatus(alarmCurrents);
    }

    /**
     * 当前转历史
     */
    @PostMapping("/updateAlarmCleanStatusCompensation")
    public void updateAlarmCleanStatusCompensation() {
        alarmExportService.updateAlarmCleanStatusCompensation();
    }


    /**
     * 查询各级别告警总数
     *
     * @param alarmLevel 级别告警信息
     * @return 级别信息
     */
    @PostMapping("/queryEveryAlarmCount")
    public Result queryEveryAlarmCount(@RequestBody String alarmLevel) {
        return alarmCurrentService.queryEveryAlarmCount(alarmLevel);
    }

    /**
     * 查询设备信息是否存在
     *
     * @param alarmSources 设备Id
     * @return 判断结果
     */
    @PostMapping("/queryAlarmSourceForFeign")
    public List<String> queryAlarmSourceForFeign(@RequestBody List<String> alarmSources) {
        if (ListUtil.isEmpty(alarmSources)) {
            return null;
        }
        return alarmCurrentService.queryAlarmSourceForFeign(alarmSources);
    }

    /**
     * 查询区域信息是否存在
     *
     * @param areaIds 区域id
     * @return 判断结果
     */
    @PostMapping("/queryAreaForFeign")
    public List<String> queryAreaForFeign(@RequestBody List<String> areaIds) {
        if (ListUtil.isEmpty(areaIds)) {
            return null;
        }
        return alarmCurrentService.queryAreaForFeign(areaIds);
    }

    /**
     * 查询单位id信息
     *
     * @param alarmIds 告警id
     * @return 单位id信息
     */
    @PostMapping("/queryDepartmentId")
    public Result queryDepartmentId(@RequestBody List<String> alarmIds) {
        if (ListUtil.isEmpty(alarmIds)) {
            return ResultUtils.warn(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmCurrentService.queryDepartmentId(alarmIds);
    }

    /**
     * 设施id查询告警信息
     *
     * @param deviceId 设施id
     * @return 判断结果
     */
    @PostMapping("/queryAlarmDeviceId/{deviceId}")
    public Result queryAlarmDeviceId(@PathVariable String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            return ResultUtils.warn(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmCurrentService.queryAlarmDeviceId(deviceId);
    }

    /**
     * 告警设施id查询c当前告警ode信息
     *
     * @param deviceId 设施id
     * @return 当前告警code信息
     */
    @PostMapping("/queryAlarmDeviceIdCode/{deviceId}")
    public List<AlarmCurrent> queryAlarmDeviceIdCode(@PathVariable("deviceId") String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            return null;
        }
        return alarmCurrentService.queryAlarmDeviceIdCode(deviceId);
    }

    /**
     * 查询告警类型
     *
     * @param id 告警id
     * @return 判断结果
     */
    @GetMapping("/queryIsStatus/{id}")
    public Result queryIsStatus(@PathVariable("id") String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultUtils.warn(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmCurrentService.queryIsStatus(id);
    }

    /**
     * 导出当前告警信息
     *
     * @param exportDto 带出信息
     * @return 判断结果
     */
    @PostMapping("/exportAlarmList")
    public Result exportAlarmList(@RequestBody ExportDto exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmExportService.exportAlarmList(exportDto);
    }

    /**
     * 查询告警门信息
     *
     * @param ids 告警id
     * @return 告警门信息
     */
    @PostMapping("/queryAlarmDoorByIdsFeign")
    public List<AlarmCurrent> queryAlarmDoorByIdsFeign(@RequestBody List<String> ids) {
        if (ListUtil.isEmpty(ids)) {
            return null;
        }
        return alarmCurrentService.queryAlarmDoor(ids);
    }

    /**
     * 删除告警信息
     *
     * @param deviceIds 设施id
     * @return 判断结果
     */
    @PostMapping("/batchDeleteAlarmsFeign")
    public Result batchDeleteAlarmsFeign(@RequestBody List<String> deviceIds) {
        if (ListUtil.isEmpty(deviceIds)) {
            return ResultUtils.warn(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmCurrentService.deleteAlarms(deviceIds);
    }

    /**
     * 根据单位id查询告警信息
     *
     * @param departmentIds 单位id
     * @return 判断结果
     */
    @PostMapping("/queryAlarmDepartmentFeign")
    public boolean queryAlarmDepartmentFeign(@RequestBody List<String> departmentIds) {
        if (ListUtil.isEmpty(departmentIds)) {
            return false;
        }
        return alarmCurrentService.queryAlarmDepartmentFeign(departmentIds);
    }

    /**
     * 告警设施总数信息
     *
     * @param alarmObject 设施类型
     * @return 总数信息
     */
    @PostMapping("/queryAlarmObjectCount")
    public Result queryAlarmObjectCount(@RequestBody String alarmObject) {
        return alarmExportService.queryAlarmObjectCount(alarmObject);
    }

    /**
     * 首页告警设施id更多信息
     *
     * @param alarmSourceLevelParameter 条件信息
     * @return 首页告警设施id更多信息
     */
    @PostMapping("/queryAlarmObjectCountHonePage")
    public Result queryAlarmObjectCountHonePage(@RequestBody AlarmSourceLevelParameter alarmSourceLevelParameter) {
        return alarmExportService.queryAlarmObjectCountHonePage(alarmSourceLevelParameter);
    }

    /**
     * 首页告警级别更多信息
     *
     * @param alarmSourceLevelParameter 条件信息
     * @return 首页告警级别更多信息
     */
    @PostMapping("/queryAlarmDeviceIdHonePage")
    public Result queryAlarmDeviceIdHonePage(@RequestBody AlarmSourceLevelParameter alarmSourceLevelParameter) {
        return alarmExportService.queryAlarmDeviceIdHonePage(alarmSourceLevelParameter);
    }

    /**
     * 首页告警告警id查询设施类型
     *
     * @param alarmSourceLevelParameter 条件信息
     * @return 首页告警设施id更多信息
     */
    @PostMapping("/queryAlarmIdCountHonePage")
    public Result queryAlarmIdCountHonePage(@RequestBody AlarmSourceLevelParameter alarmSourceLevelParameter) {
        return alarmExportService.queryAlarmIdCountHonePage(alarmSourceLevelParameter);
    }

    /**
     * 首页告警告警id查询告警级别
     *
     * @param alarmSourceLevelParameter 条件信息
     * @return 首页告警级别更多信息
     */
    @PostMapping("/queryAlarmIdHonePage")
    public Result queryAlarmIdHonePage(@RequestBody AlarmSourceLevelParameter alarmSourceLevelParameter) {
        return alarmExportService.queryAlarmIdHonePage(alarmSourceLevelParameter);
    }

    /**
     * 告警top导出
     *
     * @param exportDto 条件信息
     * @return 返回结果
     */
    @PostMapping("/exportDeviceTop")
    public Result exportDeviceTop(@RequestBody ExportDto exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmExportService.exportDeviceTop(exportDto);
    }

    /**
     * websocket
     *
     * @return 判断结果
     */
    @GetMapping("/websocket")
    public Result websocket() {
        AlarmMessage alarmMessage = new AlarmMessage();
        alarmMessage.setAlarmLevel("111");
        alarmMessage.setAlarmColor("111");
        alarmMessage.setIsPrompt("1");
        alarmMessage.setPlayCount(1);
        alarmMessage.setFilter("1");
        alarmMessage.setPrompt("a.mp3");
        // 发送消息
        WebSocketMessage socketMessage = new WebSocketMessage();
        socketMessage.setChannelKey("alarm");
        socketMessage.setMsgType(1);
        socketMessage.setMsg(alarmMessage);
        Message<WebSocketMessage> message = MessageBuilder.withPayload(socketMessage).build();
        alarmStreams.webSocketoutput().send(message);
        return ResultUtils.success("发送成功");
    }
}
