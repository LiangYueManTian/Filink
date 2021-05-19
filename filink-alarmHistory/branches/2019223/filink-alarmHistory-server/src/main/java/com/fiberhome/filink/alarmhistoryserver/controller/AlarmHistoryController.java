package com.fiberhome.filink.alarmhistoryserver.controller;


import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory;
import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory18n;
import com.fiberhome.filink.alarmhistoryserver.service.AlarmHistoryService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  历史告警前端控制器
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@RestController
@CrossOrigin
@RequestMapping("/alarmHistory")
public class AlarmHistoryController {


    @Autowired
    private AlarmHistoryService alarmHistoryService;

    /**
     * 查询历史告警列表信息
     *
     * @param queryCondition 条件
     * @return 历史告警列表信息
     */
    @PostMapping("/queryAlarmHistoryList")
    public Result queryAlarmHistoryList(@RequestBody QueryCondition<AlarmHistory> queryCondition) {
        return alarmHistoryService.queryAlarmHistoryList(queryCondition);
    }

    /**
     * 查询单个历史告警的信息
     *
     * @param alarmId 历史告警id
     * @return 查询结果
     */
    @PostMapping("/queryAlarmHistoryInfoById/{alarmId}")
    public Result queryAlarmHistoryInfoById(@PathVariable("alarmId") String alarmId) {
        if (alarmId == null || StringUtils.isEmpty(alarmId)) {
            return ResultUtils.success(ResultCode.FAIL,
                    I18nUtils.getString(AlarmHistory18n.ALARM_ID_NULL));
        }
        return alarmHistoryService.queryAlarmHistoryInfoById(alarmId);
    }

    /**
     * 添加历史告警信息
     *
     * @param alarmHistory 历史告警信息
     * @return 判断信息
     */
    @PostMapping("/insertAlarmHistory")
    public Result insertAlarmHistory(@RequestBody AlarmHistory alarmHistory) {
        if (alarmHistory == null) {
            return ResultUtils.success(null);
        }
        return alarmHistoryService.insertAlarmHistory(alarmHistory);
    }
}
