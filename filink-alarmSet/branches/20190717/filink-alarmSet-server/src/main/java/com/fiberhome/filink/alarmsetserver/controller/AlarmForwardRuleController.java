package com.fiberhome.filink.alarmsetserver.controller;


import com.alibaba.druid.util.StringUtils;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRule;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRuleDto;
import com.fiberhome.filink.alarmsetserver.constant.AppConstant;
import com.fiberhome.filink.alarmsetserver.service.AlarmForwardRuleService;
import com.fiberhome.filink.alarmsetserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmsetserver.utils.ListUtil;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 告警远程通知前端控制器
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-25
 */
@RestController
@RequestMapping("/alarmForwardRule")
public class AlarmForwardRuleController {

    /**
     * 告警远程通知Service
     */
    @Autowired
    private AlarmForwardRuleService alarmForwardRuleService;

    /**
     * 查询告警远程通知列表信息
     *
     * @param queryCondition 告警远程通知dto
     * @return 告警远程通知列表信息
     */
    @PostMapping("/queryAlarmForwardRuleList")
    public Result queryAlarmForwardRuleList(@RequestBody QueryCondition<AlarmForwardRuleDto> queryCondition) {
        return alarmForwardRuleService.queryAlarmForwardRuleList(queryCondition);
    }

    /**
     * 根据id查询远程通知信息
     *
     * @param id 告警id
     * @return 远程通知信息
     */
    @GetMapping("/queryAlarmForwardId/{id}")
    public Result queryAlarmForwardId(@PathVariable String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultUtils.success(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmForwardRuleService.queryAlarmForwardId(id);
    }

    /**
     * 新增告警远程通知信息
     *
     * @param alarmForwardRule 远程通知信息
     * @return 告警远程通知信息
     */
    @PostMapping("/addAlarmForwardRule")
    public Result addAlarmForwardRule(@RequestBody AlarmForwardRule alarmForwardRule) {
        if (alarmForwardRule == null) {
            return ResultUtils.success(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmForwardRuleService.addAlarmForwardRule(alarmForwardRule);
    }

    /**
     * 删除告警远程通知信息
     *
     * @param array 告警远程通知信息
     * @return 判断结果
     */
    @PostMapping("/batchDeleteAlarmForwardRule")
    public Result batchDeleteAlarmForwardRule(@RequestBody String[] array) {
        if (ListUtil.stringIsEmpty(array)) {
            return ResultUtils.success(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmForwardRuleService.deleteAlarmForwardRule(array);
    }

    /**
     * 修改告警远程通知信息
     *
     * @param alarmForwardRule 告警远程通知信息
     * @return 判断结果
     */
    @PostMapping("/updateAlarmForwardRule")
    public Result updateAlarmForwardRule(@RequestBody AlarmForwardRule alarmForwardRule) {
        if (alarmForwardRule == null) {
            return ResultUtils.success(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmForwardRuleService.updateAlarmForwardRule(alarmForwardRule);
    }

    /**
     * 修改告警远程通知状态信息
     *
     * @param status  告警远程通知状态信息
     * @param idArray 用户id
     * @return 判断结果
     */
    @PostMapping("/batchUpdateAlarmForwardRuleStatus")
    public Result batchUpdateAlarmForwardRuleStatus(@RequestParam Integer status, @RequestParam String[] idArray) {
        if (status == null || ListUtil.stringIsEmpty(idArray)) {
            return ResultUtils.success(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmForwardRuleService.batchUpdateAlarmForwardRuleStatus(status, idArray);
    }

    /**
     * 修改告警远程通知推送类型信息
     *
     * @param pushType 推送类型信息
     * @param idArray  用户id
     * @return 判断结果
     */
    @PostMapping("/batchUpdateAlarmForwardRulePushType")
    public Result batchUpdateAlarmForwardRulePushType(@RequestParam Integer pushType, @RequestParam String[] idArray) {
        if (pushType == null || ListUtil.stringIsEmpty(idArray)) {
            return ResultUtils.success(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmForwardRuleService.batchUpdateAlarmForwardRulePushType(pushType, idArray);
    }

    /**
     * 查询当前告警远程通知规则
     *
     * @param alarmForward 远程通知条件
     * @return 告警远程通知信息
     */
    @PostMapping("/queryAlarmForwardRuleFeign")
    public List<AlarmForwardRule> queryAlarmForwardRuleFeign(@RequestBody List<AlarmForwardCondition> alarmForward) {
        if (alarmForward == null) {
            return null;
        }
        return alarmForwardRuleService.queryAlarmForwardRuleFeign(alarmForward);
    }
}
