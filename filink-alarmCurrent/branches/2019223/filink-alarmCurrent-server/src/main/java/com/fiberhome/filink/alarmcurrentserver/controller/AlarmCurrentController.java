package com.fiberhome.filink.alarmcurrentserver.controller;


import com.fiberhome.filink.alarmcurrentserver.bean.Alarm18n;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmMessage;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentService;
import com.fiberhome.filink.alarmcurrentserver.stream.AlarmStreams;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.bean.WebSocketMessage;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  当前告警前端控制器
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@RestController
@RequestMapping("/alarmCurrent")
public class AlarmCurrentController {

    @Autowired
    private AlarmCurrentService alarmCurrentService;

    @Autowired
    private AlarmStreams alarmStreams;

    /**
     * 查询当前告警列表信息
     *
     * @return 当前告警列表信息
     */
    @PostMapping("/queryAlarmCurrentList")
    public Result queryAlarmCurrentList(@RequestBody QueryCondition<AlarmCurrent> queryCondition) {
        return alarmCurrentService.queryAlarmCurrentList(queryCondition);
    }

    /**
     * 设施id查询告警信息
     *
     * @param deviceId 设施id
     * @return 判断结果
     */
    @PostMapping("/queryAlarmDeviceId/{deviceId}")
    public Result queryAlarmDeviceId(@PathVariable String deviceId) {
        return alarmCurrentService.queryAlarmDeviceId(deviceId);
    }

    /**
     * 查询单个当前告警的详细信息
     *
     * @param alarmId 告警id
     * @return 查询结果
     */
    @PostMapping("/queryAlarmCurrentInfoById/{alarmId}")
    public Result queryAlarmCurrentInfoById(@PathVariable String alarmId) {
        if (alarmId == null || StringUtils.isEmpty(alarmId)) {
            return ResultUtils.success(ResultCode.FAIL, I18nUtils.getString(Alarm18n.ALARM_ID_NULL));
        }
        return alarmCurrentService.queryAlarmCurrentInfoById(alarmId);
    }

    /**
     * 批量设置当前告警的告警确认状态
     *
     * @param alarmCurrents 当前告警信息
     * @return 判断结果
     */
    @PostMapping("/updateAlarmConfirmStatus")
    public Result updateAlarmConfirmStatus(@RequestBody List<AlarmCurrent> alarmCurrents) {
        if (alarmCurrents == null || alarmCurrents.size() == 0) {
            return ResultUtils.success(I18nUtils.getString(Alarm18n.ALARM_USER_NULL));
        }
        return alarmCurrentService.updateAlarmConfirmStatus(alarmCurrents);
    }

    /**
     * 批量设置当前告警的告警清除状态
     *
     * @param alarmCurrents 当前告警信息
     * @return 判断结果
     */
    @PostMapping("/updateAlarmCleanStatus")
    public Result updateAlarmCleanStatus(@RequestBody List<AlarmCurrent> alarmCurrents) {
        if (alarmCurrents == null || alarmCurrents.size() == 0) {
            return ResultUtils.success(I18nUtils.getString(Alarm18n.ALARM_USER_NULL));
        }
        return alarmCurrentService.updateAlarmCleanStatus(alarmCurrents);
    }

    /**
     * 查询各级别告警总数
     *
     * @return 级别告警信息
     */
    @PostMapping("/queryEveryAlarmCount")
    public Result queryEveryAlarmCount() {
        return alarmCurrentService.queryEveryAlarmCount();
    }

    /**
     * 查询设备信息是否存在
     *
     * @param alarmSources 设备Id
     * @return 判断结果
     */
    @PostMapping("/queryAlarmSource")
    public List<String> queryAlarmSource(@RequestBody List<String> alarmSources) {
        if (alarmSources == null || alarmSources.size() == 0) {
            return null;
        }
        return alarmCurrentService.queryAlarmSource(alarmSources);
    }

    /**
     * 查询区域信息是否存在
     *
     * @param areaIds 区域id
     * @return 判断结果
     */
    @PostMapping("/queryArea")
    public List<String> queryArea(@RequestBody List<String> areaIds) {
        if (areaIds == null || areaIds.size() == 0) {
            return null;
        }
        return alarmCurrentService.queryArea(areaIds);
    }

    /**
     * websocket发送消息
     *
     * @return 发送信息
     */
    @GetMapping("/websocket")
    public Result websocket() {
        AlarmMessage alarmMessage = new AlarmMessage();
        alarmMessage.setAlarmLevel("111");
        alarmMessage.setAlarmCode("111");
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
