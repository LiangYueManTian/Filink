package com.fiberhome.filink.alarmsetserver.controller;


import com.alibaba.druid.util.StringUtils;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRule;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRuleDto;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRuleForArea;
import com.fiberhome.filink.alarmsetserver.constant.AlarmSetConstant;
import com.fiberhome.filink.alarmsetserver.constant.AlarmSetResultCode;
import com.fiberhome.filink.alarmsetserver.constant.AppConstant;
import com.fiberhome.filink.alarmsetserver.service.AlarmOrderRuleService;
import com.fiberhome.filink.alarmsetserver.utils.ListUtil;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 告警转工单规则前端控制器
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-27
 */
@RestController
@RequestMapping("/alarmOrderRule")
public class AlarmOrderRuleController {

    /**
     * 告警转工单规则Service
     */
    @Autowired
    private AlarmOrderRuleService alarmOrderRuleService;

    /**
     * 查询告警转工单列表信息
     *
     * @param queryCondition 条件
     * @return 告警转工单列表信息
     */
    @PostMapping("/queryAlarmOrderRuleList")
    public Result queryAlarmOrderRuleList(@RequestBody QueryCondition<AlarmOrderRuleDto> queryCondition) {
        return alarmOrderRuleService.queryAlarmOrderRuleList(queryCondition);
    }

    /**
     * 查询告警转工单信息
     *
     * @param id 告警id
     * @return 告警转工单信息
     */
    @GetMapping("/queryAlarmOrderRule/{id}")
    public Result queryAlarmOrderRule(@PathVariable String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultUtils.success(AlarmSetResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmOrderRuleService.queryAlarmOrderRule(id);
    }

    /**
     * 添加告警转工单信息
     *
     * @param alarmOrderRule 告警转工单信息
     * @return 判断结果
     */
    @PostMapping("/addAlarmOrderRule")
    public Result addAlarmOrderRule(@RequestBody AlarmOrderRule alarmOrderRule) {
        if (alarmOrderRule == null) {
            return ResultUtils.success(AlarmSetResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmOrderRuleService.addAlarmOrderRule(alarmOrderRule);
    }

    /**
     * 删除告警转工单信息
     *
     * @param array 告警转工单id
     * @return 判断信息
     */
    @PostMapping("/batchDeleteAlarmOrderRule")
    public Result batchDeleteAlarmOrderRule(@RequestBody String[] array) {
        if (ListUtil.stringIsEmpty(array)) {
            return ResultUtils.success(AlarmSetResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmOrderRuleService.deleteAlarmOrderRule(array);
    }

    /**
     * 修改告警转工单信息
     *
     * @param alarmOrderRule 告警转工单信息
     * @return 判断结果
     */
    @PostMapping("/updateAlarmOrderRule")
    public Result updateAlarmOrderRule(@RequestBody AlarmOrderRule alarmOrderRule) {
        Integer completionTime = alarmOrderRule.getCompletionTime();
        if (completionTime == null) {
            completionTime = 3;
        }
        if (alarmOrderRule == null || completionTime <= AlarmSetConstant.ALARM_MIN
                || completionTime > AlarmSetConstant.ALARM_YEAR) {
            return ResultUtils.success(AlarmSetResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmOrderRuleService.updateAlarmOrderRule(alarmOrderRule);
    }

    /**
     * 修改告警转工单状态信息
     *
     * @param status  告警转工单状态信息
     * @param idArray 用户id
     * @return 判断结果
     */
    @PostMapping("/batchUpdateAlarmOrderRuleStatus")
    public Result batchUpdateAlarmOrderRuleStatus(@RequestParam Integer status, @RequestParam String[] idArray) {
        if (status == null || ListUtil.stringIsEmpty(idArray)) {
            return ResultUtils.success(AlarmSetResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmOrderRuleService.updateAlarmOrderRuleStatus(status, idArray);
    }

    /**
     * 查询当前告警转工单规则
     *
     * @param alarmOrder 告警转工单条件
     * @return 告警转工单规则信息
     */
    @PostMapping("/queryAlarmOrderRuleFeign")
    public AlarmOrderRule queryAlarmOrderRuleFeign(@RequestBody List<AlarmOrderCondition> alarmOrder) {
        if (ListUtil.isEmpty(alarmOrder)) {
            return null;
        }
        return alarmOrderRuleService.queryAlarmOrderRuleFeign(alarmOrder);
    }


    /**
     * 根据部门id集合查询是否存在当前告警转工单规则
     * @param departmentIds 部门id集合
     * @return 是否存在
     */
    @PostMapping("/queryAlarmOrderRuleByDeptIds")
    public Result queryAlarmOrderRuleByDeptIds(@RequestBody List<String> departmentIds) {
        if (ListUtil.isEmpty(departmentIds)) {
            return ResultUtils.success(AlarmSetResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmOrderRuleService.queryAlarmOrderRuleByDeptIds(departmentIds);
    }

    /**
     * 根据区域ID，部门id集合查询是否存在当前告警转工单规则
     * @param alarmOrderRuleForArea 区域ID，部门id集合
     * @return 是否存在
     */
    @PostMapping("/queryAlarmOrderRuleByAreaDeptIds")
    public Result queryAlarmOrderRuleByAreaDeptIds(@RequestBody AlarmOrderRuleForArea alarmOrderRuleForArea) {
        if (alarmOrderRuleForArea == null || alarmOrderRuleForArea.check()) {
            return ResultUtils.success(AlarmSetResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmOrderRuleService.queryAlarmOrderRuleByAreaDeptIds(alarmOrderRuleForArea);
    }

}
