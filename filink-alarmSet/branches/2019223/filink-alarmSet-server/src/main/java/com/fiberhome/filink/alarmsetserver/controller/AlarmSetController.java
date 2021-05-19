package com.fiberhome.filink.alarmsetserver.controller;


import com.fiberhome.filink.alarmsetserver.bean.AlarmColor;
import com.fiberhome.filink.alarmsetserver.bean.AlarmDelayDto;
import com.fiberhome.filink.alarmsetserver.bean.AlarmLevel;
import com.fiberhome.filink.alarmsetserver.bean.AlarmName;
import com.fiberhome.filink.alarmsetserver.bean.AlarmSetI18;
import com.fiberhome.filink.alarmsetserver.service.AlarmDelayService;
import com.fiberhome.filink.alarmsetserver.service.AlarmLevelService;
import com.fiberhome.filink.alarmsetserver.service.AlarmNameService;
import com.fiberhome.filink.alarmsetserver.utils.AlarmSetCode;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  告警设置前端控制器
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@RestController
@RequestMapping("/alarmSet")
public class AlarmSetController {

    @Autowired
    private AlarmLevelService alarmLevelService;
    @Autowired
    private AlarmNameService alarmNameService;
    @Autowired
    private AlarmDelayService alarmDelayService;

    /**
     * 查询告警级别列表信息
     *
     * @return 告警级别列表信息
     */
    @PostMapping("/queryAlarmLevelList")
    public Result queryAlarmLevelList(){
        return alarmLevelService.queryAlarmLevelList();
    }

    /**
     * 查询单个告警级别信息
     * @param alarmId 告警级别
     * @return 告警级别信息
     */
    @PostMapping("/queryAlarmLevelById/{alarmId}")
    public Result queryAlarmLevelById(@PathVariable String alarmId) {
        return alarmLevelService.queryAlarmLevelById(alarmId);
    }

    /**
     * 查询告警名称信息列表
     *
     * @param queryCondition 封装条件
     * @return 告警名称信息
     */
    @PostMapping("/queryAlarmNameList")
    public Result queryAlarmNameList(@RequestBody QueryCondition queryCondition) {
        return alarmNameService.queryAlarmNameList(queryCondition);
    }

    /**
     * 查询单个告警设置信息
     *
     * @param alarmId 告警id
     * @return 告警设置信息
     */
    @PostMapping("/queryAlarmCurrentSetById/{alarmId}")
    public Result queryAlarmCurrentSetById(@PathVariable String alarmId) {
        return alarmNameService.queryAlarmCurrentSetById(alarmId);
    }

    /**
     * 修改告警设置信息
     *
     * @param alarmName 告警设置信息
     * @return 判断结果
     */
    @PostMapping("/updateAlarmCurrentSet")
    public Result updateAlarmCurrentSet(@RequestBody AlarmName alarmName) {
        if(alarmName.getId() == null){
            return ResultUtils.success(ResultCode.FAIL,
                    I18nUtils.getString(AlarmSetI18.ALARM_NAME_ID_NULL));
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
        // 参数验证
        if (alarmLevel.getPlayCount() == null) {
            alarmLevel.setPlayCount(1);
        }
        if (alarmLevel.getPlayCount() < AlarmSetCode.ALARMLIN || alarmLevel.getPlayCount() > AlarmSetCode.PLAY_WU) {
            return ResultUtils.success(AlarmSetCode.PLAY_TIMES_WRONG, I18nUtils.getString(AlarmSetI18.PLAY_TIMES_WRONG));
        }
        if(alarmLevel.getId() == null){
            return ResultUtils.success(ResultCode.FAIL, I18nUtils.getString(AlarmSetI18.ALARM_LEVEL_ID_NULL));
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
     * 修改延时入库时间
     *
     * @param alarmDelayDo 延时入库信息
     * @return 判断结果
     */
    @PostMapping("/updateAlarmDelay")
    public Result updateAlarmDelay(@RequestBody AlarmDelayDto alarmDelayDo) {
        if (alarmDelayDo.getAlarmDelay().getDelay() < AlarmSetCode.ALARMLIN ||
                alarmDelayDo.getAlarmDelay().getDelay() > AlarmSetCode.ALARMDELAY){
            return ResultUtils.success(ResultCode.FAIL,
                    I18nUtils.getString(AlarmSetI18.INCORRECT_RANGE_VALUE));
        }
        return alarmDelayService.updateAlarmDelay(alarmDelayDo);
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
        String alarmColor = AlarmColor.BLUE+","+AlarmColor.ORANGE+","+AlarmColor.PURPLE+","
                +AlarmColor.RED+","+AlarmColor.YELLOW;
        return alarmColor;
    }
}
