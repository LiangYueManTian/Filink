package com.fiberhome.filink.alarmcurrentserver.controller;


import com.fiberhome.filink.alarmcurrentserver.bean.AlarmHomeParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmSourceHomeParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsTemp;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTime;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrentResultCode;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentService;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmStatisticsService;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmStatisticsTemplateService;
import com.fiberhome.filink.alarmcurrentserver.utils.DateUtil;
import com.fiberhome.filink.alarmcurrentserver.utils.ListUtil;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 告警统计前端控制器
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@RestController
@RequestMapping("/alarmStatistics")
public class AlarmStatisticsController {

    @Autowired
    private AlarmStatisticsService alarmStatisticsTempService;

    @Autowired
    private AlarmCurrentService alarmCurrentService;

    @Autowired
    private AlarmStatisticsTemplateService alarmStatisticsTemplateService;


    /**
     * 告警类型统计
     *
     * @param queryCondition 封装条件
     * @return 告警类型统计信息
     */
    @PostMapping("/queryAlarmConutByLevelAndArea")
    public Result queryAlarmByLevelAndArea(@RequestBody QueryCondition<AlarmStatisticsParameter> queryCondition) {
        if (queryCondition == null) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmStatisticsTempService.queryAlarmByLevelAndArea(queryCondition.getBizCondition());
    }

    /**
     * 告警处理统计
     *
     * @param queryCondition 封装条件
     * @return 告警处理统计信息
     */
    @PostMapping("/queryAlarmHandleStatistics")
    public Result queryAlarmHandle(@RequestBody QueryCondition<AlarmStatisticsParameter> queryCondition) {
        if (queryCondition == null) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmStatisticsTempService.alarmHandleStatistics(queryCondition.getBizCondition());
    }

    /**
     * 告警名称统计
     *
     * @param queryCondition 封装条件
     * @return 告警名称统计信息
     */
    @PostMapping("/queryAlarmNameStatistics")
    public Result queryAlarmName(@RequestBody QueryCondition<AlarmStatisticsParameter> queryCondition) {
        if (queryCondition == null) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmStatisticsTempService.alarmNameStatistics(queryCondition);
    }

    /**
     * 区域告警比例统计
     *
     * @param queryCondition 封装条件
     * @return 区域告警比例统计信息
     */
    @PostMapping("/queryAreaAlarmStatistics")
    public Result queryAlarmCountByLevel(@RequestBody QueryCondition<AlarmStatisticsParameter> queryCondition) {
        if (queryCondition == null) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmStatisticsTempService.queryAlarmCountByLevel(queryCondition);
    }

    /**
     * 告警增量统计
     *
     * @param queryCondition 封装条件
     * @return 告警名称统计信息
     */

    @PostMapping("/queryAlarmIncrementalStatistics")
    public Result queryAlarmIncrementalStatistics(@RequestBody QueryCondition<AlarmStatisticsParameter> queryCondition) {
        return alarmStatisticsTempService.alarmIncrementalStatistics(queryCondition, AppConstant.DAY, false);
    }


    /**
     * 告警增量统计(大屏统计)
     *
     * @param timeType 封装条件
     * @return 告警名称统计信息
     */
    @PostMapping("/alarmDateStatistics/{timeType}")
    public Result queryIncrementalStatistics(@PathVariable String timeType) {
        AlarmStatisticsParameter alarmStatisticsParameter = new AlarmStatisticsParameter();
        if (AppConstant.DAY.equalsIgnoreCase(timeType)) {
            //开始时间为前15天
            alarmStatisticsParameter.setBeginTime(DateUtil.getAdvanceNumberDay(15));
            //结束时间为前一天
            alarmStatisticsParameter.setEndTime(DateUtil.getAdvanceNumberEndDay());
        } else if (AppConstant.WEEK.equals(timeType)) {
            //开始时间为前15周
            alarmStatisticsParameter.setBeginTime(DateUtil.getAdvanceNumberWeek(15));
            //结束时间为上一周
            alarmStatisticsParameter.setEndTime(DateUtil.getAdvanceNumberEndWeek());
        } else if (AppConstant.MONTH.equals(timeType)) {
            //开始时间为前12月
            alarmStatisticsParameter.setBeginTime(DateUtil.getAdvanceNumberMonth(12));
            //结束时间为前一天
            alarmStatisticsParameter.setEndTime(DateUtil.getAdvanceNumberEndMonth());
        }
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setBizCondition(alarmStatisticsParameter);
        return alarmStatisticsTempService.alarmIncrementalStatistics(queryCondition, timeType, true);
    }

    /**
     * 根据统计模板id查询告警统计信息
     *
     * @return 当前告警列表信息
     */
    @PostMapping("/queryAlarmStatById/{tempId}")
    public Result queryAlarmStatById(@PathVariable String tempId) {
        AlarmStatisticsTemp alarmStatisticsTemp = alarmStatisticsTemplateService.queryAlarmStatisticsTempId(tempId);
        return ResultUtils.success(alarmStatisticsTemp);
    }

    /**
     * 查询告警统计模板列表信息
     *
     * @return 当前告警列表信息
     */
    @PostMapping("/queryAlarmStatisticsTempList/{pageType}")
    public Result queryAlarmStatisticsTempList(@PathVariable String pageType) {
        return alarmStatisticsTemplateService.queryAlarmStatisticsTempList(pageType);
    }

    /**
     * 新建告警统计模板
     *
     * @param alarmStatisticsTemp 封装条件
     * @return 当前告警列表信息
     */
    @PostMapping("/addAlarmStatisticsTemp")
    public Result addAlarmStatisticsTemp(@RequestBody AlarmStatisticsTemp alarmStatisticsTemp) {
        return alarmStatisticsTemplateService.addAlarmStatisticsTemp(alarmStatisticsTemp);
    }

    /**
     * 修改告警统计模板信息
     *
     * @param alarmStatisticsTemp 封装条件
     * @return 当前告警列表信息
     */
    @PostMapping("/updateAlarmStatisticsTemp")
    public Result updateAlarmStatisticsTemp(@RequestBody AlarmStatisticsTemp alarmStatisticsTemp) {
        return alarmStatisticsTemplateService.updateAlarmStatisticsTemp(alarmStatisticsTemp);
    }


    /**
     * 删除告警统计模板信息
     *
     * @param ids 封装条件
     * @return 当前告警列表信息
     */
    @PostMapping("/deleteManyAlarmStatisticsTemp")
    public Result deleteManyAlarmStatisticsTemp(@RequestBody String[] ids) {
        if (ListUtil.stringIsEmpty(ids)) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmStatisticsTemplateService.deleteManyAlarmStatisticsTemp(ids);
    }

    /**
     * 查询告警top
     *
     * @param queryCondition 条件信息
     * @return 告警tcp信息
     */
    @PostMapping("/queryAlarmNameGroup")
    public Result queryAlarmNameGroup(@RequestBody QueryCondition<AlarmStatisticsParameter> queryCondition) {
        if (queryCondition == null) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmStatisticsTempService.queryAlarmNameGroup(queryCondition);
    }

    /**
     * 首页设施统计告警名称信息
     *
     * @param alarmHomeParameter 条件信息
     * @return 统计告警信息
     */
    @PostMapping("/queryAlarmNameHomePage")
    public Result queryAlarmNameHomePage(@RequestBody AlarmHomeParameter alarmHomeParameter) {
        if (alarmHomeParameter == null) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmStatisticsTempService.queryAlarmNameHomePage(alarmHomeParameter);
    }

    /**
     * 设施统计当前告警级别信息
     *
     * @param alarmSourceHomeParameter 条件信息
     * @return 统计告警信息
     */
    @PostMapping("/queryAlarmCurrentSourceLevel")
    public Result queryAlarmCurrentSourceLevel(@RequestBody AlarmSourceHomeParameter alarmSourceHomeParameter) {
        if (alarmSourceHomeParameter == null) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmStatisticsTempService.queryAlarmCurrentSourceLevel(alarmSourceHomeParameter);
    }

    /**
     * 设施统计历史告警级别信息
     *
     * @param alarmSourceHomeParameter 条件信息
     * @return 统计告警信息
     */
    @PostMapping("/queryAlarmHistorySourceLevel")
    public Result queryAlarmHistorySourceLevel(@RequestBody AlarmSourceHomeParameter alarmSourceHomeParameter) {
        if (alarmSourceHomeParameter == null) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmStatisticsTempService.queryAlarmHistorySourceLevel(alarmSourceHomeParameter);
    }

    /**
     * 设施统计当前告警名称信息
     *
     * @param alarmSourceHomeParameter 条件信息
     * @return 统计告警信息
     */
    @PostMapping("/queryAlarmCurrentSourceName")
    public Result queryAlarmCurrentSourceName(@RequestBody AlarmSourceHomeParameter alarmSourceHomeParameter) {
        if (alarmSourceHomeParameter == null) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmStatisticsTempService.queryAlarmCurrentSourceName(alarmSourceHomeParameter);
    }

    /**
     * 设施统计历史告警名称信息
     *
     * @param alarmSourceHomeParameter 条件信息
     * @return 统计告警信息
     */
    @PostMapping("/queryAlarmHistorySourceName")
    public Result queryAlarmHistorySourceName(@RequestBody AlarmSourceHomeParameter alarmSourceHomeParameter) {
        if (alarmSourceHomeParameter == null) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmStatisticsTempService.queryAlarmHistorySourceName(alarmSourceHomeParameter);
    }

    /**
     * 新增告警设施统计信息
     *
     * @param alarmSourceHomeParameter 条件信息
     */
    @PostMapping("/querySourceIncremental")
    public void querySourceIncremental(@RequestBody AlarmSourceHomeParameter alarmSourceHomeParameter) {
        alarmStatisticsTempService.querySourceIncremental(alarmSourceHomeParameter);
    }

    /**
     * 设施增量查询信息
     *
     * @param alarmSourceHomeParameter 条件信息
     * @return 设施增量查询信息
     */
    @PostMapping("/queryAlarmSourceIncremental")
    public Result queryAlarmSourceIncremental(@RequestBody AlarmSourceHomeParameter alarmSourceHomeParameter) {
        long time = alarmSourceHomeParameter.getEndTime() - alarmSourceHomeParameter.getBeginTime();
        // 是否大于15天
        boolean flag = time > AppConstant.DAY_TO_WEEK_TIME;
        if (flag) {
            alarmSourceHomeParameter.setType(AppConstant.WEEK);
        } else {
            alarmSourceHomeParameter.setType(AppConstant.DAY);
        }
        return alarmStatisticsTempService.queryAlarmSourceIncremental(alarmSourceHomeParameter);
    }

    /**
     * 告警增量增加
     * @param alarmSourceHomeParameter 增量统计的参数
     */
    @PostMapping("/testInsert")
    public void testInsert(@RequestBody AlarmSourceHomeParameter alarmSourceHomeParameter) {
        alarmStatisticsTempService.queryStatisticsData(alarmSourceHomeParameter);
    }

    @PostMapping("/testTask")
    public void tstTak() {
        alarmStatisticsTempService.deleteAlarmIncrementalStatistics(DateUtil.getAdvanceNumberDay(20), AppConstant.DAY);
    }

    /**
     * 大屏告警级别统计信息
     *
     * @return 告警级别统计信息
     */
    @GetMapping("/queryAlarmCurrentLevelGroup")
    public Result queryAlarmCurrentLevelGroup() {
        return alarmStatisticsTempService.queryAlarmCurrentLevelGroup();
    }

    /**
     * 大屏设施根据日周月top
     *
     * @param alarmTime 时间信息
     * @return 告警设施信息
     */
    @PostMapping("/queryScreenDeviceIdGroup")
    public Result queryScreenDeviceIdGroup(@RequestBody AlarmTime alarmTime) {
        if (alarmTime == null) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmStatisticsTempService.queryScreenDeviceIdGroup(alarmTime);
    }

    /**
     * 大屏设施根据top
     *
     * @return 告警设施信息
     */
    @GetMapping("/queryScreenDeviceIdsGroup")
    public Result queryScreenDeviceIdsGroup() {
        return alarmStatisticsTempService.queryScreenDeviceIdsGroup();
    }

    /**
     * app告警名称统计
     *
     * @return 告警统计信息
     */
    @PostMapping("/queryAppAlarmNameGroup")
    public Result queryAppAlarmNameGroup() {
        return alarmStatisticsTempService.queryAppAlarmNameGroup();
    }


    /**
     * 导出告警统计信息
     *
     * @param exportDto 带出信息
     * @return 判断结果
     */
    @PostMapping("/exportAlarmStatisticsList")
    public Result exportAlarmStatisticsList(@RequestBody ExportDto exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmStatisticsTemplateService.exportAlarmStatisticList(exportDto);
    }

}
