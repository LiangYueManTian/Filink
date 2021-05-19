package com.fiberhome.filink.alarmhistoryserver.controller;


import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory;
import com.fiberhome.filink.alarmhistoryserver.constant.AppConstant;
import com.fiberhome.filink.alarmhistoryserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmhistoryserver.service.AlarmHistoryExportService;
import com.fiberhome.filink.alarmhistoryserver.service.AlarmHistoryService;
import com.fiberhome.filink.alarmhistoryserver.utils.ListUtil;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.bean.User;
import java.util.List;
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
 * 历史告警前端控制器
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@RestController
@CrossOrigin
@RequestMapping("/alarmHistory")
public class AlarmHistoryController {

    /**
     * 历史告警Service
     */
    @Autowired
    private AlarmHistoryService alarmHistoryService;

    /**
     * 历史告警导出Service
     */
    @Autowired
    private AlarmHistoryExportService alarmHistoryExportService;

    /**
     * 查询历史告警列表信息
     *
     * @param queryCondition 封装条件
     * @return 历史告警列表信息
     */
    @PostMapping("/queryAlarmHistoryList")
    public Result queryAlarmHistoryList(@RequestBody QueryCondition<AlarmHistory> queryCondition) {
        User user = alarmHistoryService.getUser();
        PageBean pageBean = alarmHistoryService.queryAlarmHistoryList(queryCondition, user, true);
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * 查询单个历史告警的信息
     *
     * @param alarmId 历史告警id
     * @return 查询结果
     */
    @PostMapping("/queryAlarmHistoryInfoById/{alarmId}")
    public Result queryAlarmHistoryInfoById(@PathVariable("alarmId") String alarmId) {
        if (StringUtils.isEmpty(alarmId)) {
            return ResultUtils.warn(ResultCode.FAIL,
                    I18nUtils.getSystemString(AppConstant.ALARM_ID_NULL));
        }
        return alarmHistoryService.queryAlarmHistoryInfoById(alarmId);
    }

    /**
     * 查询单个历史告警的信息
     *
     * @param alarmId 历史告警id
     * @return 查询结果
     */
    @PostMapping("/queryAlarmHistoryByIdFeign/{alarmId}")
    public List<AlarmHistory> queryAlarmHistoryByIdFeign(@PathVariable("alarmId") String alarmId) {
        if (StringUtils.isEmpty(alarmId)) {
            return null;
        }
        return alarmHistoryService.queryAlarmHistoryById(alarmId);
    }

    /**
     * 根据id查询历史告警信息
     *
     * @param alarmId 告警id
     * @return 历史告警信息
     */
    @PostMapping("/queryAlarmHistoryByIdsFeign")
    public List<AlarmHistory> queryAlarmHistoryByIdsFeign(@RequestBody List<String> alarmId) {
        if (ListUtil.isEmpty(alarmId)) {
            return null;
        }
        return alarmHistoryService.queryAlarmHistoryByIds(alarmId);
    }

    /**
     * 根据设施id查询告警信息
     *
     * @param deviceId 设施id
     * @return 告警信息
     */
    @PostMapping("/queryAlarmHistoryDeviceId/{deviceId}")
    public Result queryAlarmHistoryDeviceId(@PathVariable String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            return ResultUtils.warn(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmHistoryService.queryAlarmHistoryDeviceId(deviceId);
    }

    /**
     * 批量修改历史告警备注信息
     *
     * @param alarmHistories 历史告警信息
     * @return 判断结果
     */
    @PostMapping("/batchUpdateAlarmRemark")
    public Result batchUpdateAlarmRemark(@RequestBody List<AlarmHistory> alarmHistories) {
        if (ListUtil.isEmpty(alarmHistories)) {
            return ResultUtils.warn(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmHistoryService.batchUpdateAlarmRemark(alarmHistories);
    }

    /**
     * 添加历史告警信息
     *
     * @param alarmHistory 历史告警信息
     * @return 判断信息
     */
    @PostMapping("/insertAlarmHistoryFeign")
    public Result insertAlarmHistoryFeign(@RequestBody AlarmHistory alarmHistory) {
        if (alarmHistory == null) {
            return ResultUtils.warn(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmHistoryService.insertAlarmHistoryFeign(alarmHistory);
    }

    /**
     * 导出历史告警信息
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
        return alarmHistoryExportService.exportAlarmList(exportDto);
    }

    /**
     * 删除历史告警信息
     *
     * @param deviceIds 设施id
     * @return 判断结果
     */
    @PostMapping("/batchDeleteAlarmHistoryFeign")
    public Integer deleteAlarmHistoryFeign(@RequestBody List<String> deviceIds) {
        if (ListUtil.isEmpty(deviceIds)) {
            return AppConstant.LIN;
        }
        return alarmHistoryService.deleteAlarmHistory(deviceIds);
    }

    /**
     * 定时任务添加历史告警信息
     *
     * @param alarmHistoryList 历史告警信息
     * @return 判断信息
     */
    @PostMapping("/insertAlarmHistoryListFeign")
    public void insertAlarmHistoryListFeign(@RequestBody List<AlarmHistory> alarmHistoryList) {
        if (alarmHistoryList != null) {
            alarmHistoryService.insertAlarmHistoryList(alarmHistoryList);
        }
    }

    /**
     * 查询单位信息
     *
     * @param alarmIds 告警id
     * @return 单位信息
     */
    @PostMapping("/queryDepartmentHistory")
    public Result queryDepartmentHistory(@RequestBody List<String> alarmIds) {
        if (ListUtil.isEmpty(alarmIds)) {
            return ResultUtils.warn(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmHistoryService.queryDepartmentHistory(alarmIds);
    }

}
