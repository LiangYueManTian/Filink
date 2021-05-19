package com.fiberhome.filink.alarmsetserver.controller;


import com.alibaba.druid.util.StringUtils;
import com.fiberhome.filink.alarmsetserver.bean.AlarmColor;
import com.fiberhome.filink.alarmsetserver.bean.AlarmDelay;
import com.fiberhome.filink.alarmsetserver.bean.AlarmLevel;
import com.fiberhome.filink.alarmsetserver.bean.AlarmName;
import com.fiberhome.filink.alarmsetserver.constant.AppConstant;
import com.fiberhome.filink.alarmsetserver.service.AlarmDelayService;
import com.fiberhome.filink.alarmsetserver.service.AlarmLevelService;
import com.fiberhome.filink.alarmsetserver.service.AlarmNameService;
import com.fiberhome.filink.alarmsetserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmsetserver.utils.ListUtil;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 告警设置前端控制器
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@RestController
@RequestMapping("/alarmSet")
public class AlarmSetController {

    /**
     * 告警级别service
     */
    @Autowired
    private AlarmLevelService alarmLevelService;

    /**
     * 告警名称service
     */
    @Autowired
    private AlarmNameService alarmNameService;

    /**
     * 历史告警设置Service
     */
    @Autowired
    private AlarmDelayService alarmDelayService;

    /**
     * 查询告警级别列表信息
     *
     * @return 告警级别列表信息
     */
    @PostMapping("/queryAlarmLevelList")
    public Result queryAlarmLevelList() {
        return alarmLevelService.queryAlarmLevelList();
    }

    /**
     * 查询单个告警级别信息
     *
     * @param alarmId 告警级别
     * @return 告警级别信息
     */
    @PostMapping("/queryAlarmLevelById/{alarmId}")
    public Result queryAlarmLevelById(@PathVariable String alarmId) {
        if (StringUtils.isEmpty(alarmId)) {
            return ResultUtils.success(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmLevelService.queryAlarmLevelById(alarmId);
    }

    /**
     * 根据告警级别编码查询告警级别设置信息
     *
     * @param alarmLevelCode 告警级别编码
     * @return 告警级别
     */
    @PostMapping("/queryAlarmLevelSetFeign/{alarmLevelCode}")
    public AlarmLevel queryAlarmLevelSetFeign(@PathVariable("alarmLevelCode") String alarmLevelCode) {
        return alarmLevelService.queryAlarmLevelSetFeign(alarmLevelCode);
    }

    /**
     * 根据id查询告警名称信息
     *
     * @param array 用户id
     * @return 告警名称信息
     */
    @PostMapping("/queryAlarmCurrentSetById")
    public Result queryAlarmCurrentSetById(@RequestBody String[] array) {
        if (ListUtil.stringIsEmpty(array)) {
            return ResultUtils.success(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmNameService.queryAlarmCurrentSetById(array);
    }

    /**
     * 根据告警编码查询告警名称信息
     *
     * @param alarmCode 告警编码
     * @return 告警名称信息
     */
    @PostMapping("/queryCurrentAlarmSetFeign/{alarmCode}")
    public AlarmName queryCurrentAlarmSetFeign(@PathVariable("alarmCode") String alarmCode) {
        return alarmNameService.queryCurrentAlarmSetFeign(alarmCode);
    }

    /**
     * 根据告警名称查询当前告警设置信息
     *
     * @param alarmName 告警名称
     * @return 告警设置信息
     */
    @PostMapping("/queryCurrentAlarmSetByNameFeign")
    public AlarmName queryCurrentAlarmSetByNameFeign(@RequestParam String alarmName) {
        if (StringUtils.isEmpty(alarmName)) {
            return null;
        }
        return alarmNameService.queryCurrentAlarmSetByNameFeign(alarmName);
    }

    /**
     * 修改告警设置信息
     *
     * @param alarmName 告警设置信息
     * @return 判断结果
     */
    @PostMapping("/updateAlarmCurrentSet")
    public Result updateAlarmCurrentSet(@RequestBody AlarmName alarmName) {
        if (alarmName.getId() == null) {
            return ResultUtils.success(ResultCode.FAIL,
                    I18nUtils.getString(AppConstant.ALARM_NAME_ID_NULL));
        }
        return alarmNameService.updateAlarmCurrentSet(alarmName);
    }

    /**
     * 修改告警级别
     *
     * @param alarmLevel 告警级别
     * @return 修改结果
     */
    @PostMapping("/updateAlarmLevel")
    public Result updateAlarmLevel(@RequestBody AlarmLevel alarmLevel) {
        if (alarmLevel.getPlayCount() == null) {
            alarmLevel.setPlayCount(1);
        }
        // 参数验证
        if (alarmLevel.getPlayCount() < LogFunctionCodeConstant.ALARMLIN || alarmLevel.getPlayCount() > LogFunctionCodeConstant.PLAY_WU) {
            return ResultUtils.success(LogFunctionCodeConstant.PLAY_TIMES_WRONG, I18nUtils.getString(AppConstant.PLAY_TIMES_WRONG));
        }
        if (alarmLevel.getId() == null) {
            return ResultUtils.success(ResultCode.FAIL, I18nUtils.getString(AppConstant.ALARM_LEVEL_ID_NULL));
        }
        return alarmLevelService.updateAlarmLevel(alarmLevel);
    }

    /**
     * 查询历史设置信息
     *
     * @return 历史设置信息
     */
    @PostMapping("/selectAlarmDelay")
    public Result selectAlarmDelay() {
        return alarmDelayService.selectAlarmDelay();
    }

    /**
     * 定时任务查询历史告警设置时间
     *
     * @return 判断结果
     */
    @PostMapping("/queryAlarmHistorySetFeign")
    public Integer queryAlarmHistorySetFeign() {
        return alarmDelayService.selectAlarmDelayTime();
    }

    /**
     * 修改延时入库时间
     *
     * @param alarmDelay 延时入库信息
     * @return 判断结果
     */
    @PostMapping("/updateAlarmDelay")
    public Result updateAlarmDelay(@RequestBody AlarmDelay alarmDelay) {
        if (alarmDelay.getDelay() == null) {
            alarmDelay.setDelay(2);
        }
        if (alarmDelay.getDelay() <= LogFunctionCodeConstant.ALARMLIN || alarmDelay.getDelay() > LogFunctionCodeConstant.ALARMDELAY) {
            return ResultUtils.success(ResultCode.FAIL, I18nUtils.getString(AppConstant.INCORRECT_RANGE_VALUE));
        }
        return alarmDelayService.updateAlarmDelay(alarmDelay);
    }

    /**
     * 查询告警名称信息列表
     *
     * @param queryCondition 条件
     * @return 告警名称信息
     */
    @PostMapping("/queryAlarmNameList")
    public Result queryAlarmNameList(@RequestBody QueryCondition queryCondition) {
        return alarmNameService.queryAlarmNameList(queryCondition);
    }

    /**
     * 查询告警名称信息列表
     *
     * @param queryCondition 封装条件
     * @return 告警名称信息
     */
    @PostMapping("/queryAlarmNamePage")
    public Result queryAlarmNamePage(@RequestBody QueryCondition queryCondition) {
        return alarmNameService.queryAlarmNamePage(queryCondition);
    }

    /**
     * 查询告警级别
     *
     * @return 告警级别信息
     */
    @PostMapping("/queryAlarmLevel")
    public Result queryAlarmLevel() {
        return alarmLevelService.queryAlarmLevel();
    }

    /**
     * 查询颜色信息
     *
     * @return 颜色信息
     */
    @PostMapping("/queryAlarmColor")
    public String queryAlarmColor() {
        String alarmColor = AlarmColor.BLUE + "," + AlarmColor.ORANGE + "," + AlarmColor.PURPLE + ","
                + AlarmColor.RED + "," + AlarmColor.YELLOW;
        return alarmColor;
    }
}
