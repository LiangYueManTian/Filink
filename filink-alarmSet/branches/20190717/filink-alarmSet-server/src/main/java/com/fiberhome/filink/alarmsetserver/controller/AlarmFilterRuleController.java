package com.fiberhome.filink.alarmsetserver.controller;


import com.alibaba.druid.util.StringUtils;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterRule;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterRuleDto;
import com.fiberhome.filink.alarmsetserver.constant.AppConstant;
import com.fiberhome.filink.alarmsetserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmsetserver.service.AlarmFilterRuleService;
import com.fiberhome.filink.alarmsetserver.utils.ListUtil;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
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
 * 告警过滤前端控制器
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-01
 */
@RestController
@RequestMapping("/alarmFilterRule")
public class AlarmFilterRuleController {

    /**
     * 注入Service
     */
    @Autowired
    private AlarmFilterRuleService alarmFilterRuleService;

    /**
     * 设施api
     */
    @Autowired
    private DeviceFeign deviceFeign;

    /**
     * 查询告警过滤列表信息
     *
     * @param queryCondition 封装条件
     * @return 告警过滤列表信息
     */
    @PostMapping("/queryAlarmFilterRuleList")
    public Result queryAlarmFilterRuleList(@RequestBody QueryCondition<AlarmFilterRuleDto> queryCondition) {
        return alarmFilterRuleService.queryAlarmFilterRuleList(queryCondition);
    }

    /**
     * 根据id查询告警过滤信息
     *
     * @param id 过滤id
     * @return 告警过滤信息
     */
    @GetMapping("/queryAlarmFilterRuleById/{id}")
    public Result queryAlarmFilterRuleById(@PathVariable String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultUtils.warn(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmFilterRuleService.queryAlarmFilterRuleById(id);
    }

    /**
     * 新增告警过滤信息
     *
     * @param alarmFilterRule 告警过滤信息
     * @return 判断结果
     */
    @PostMapping("/addAlarmFilterRule")
    public Result addAlarmFilterRule(@RequestBody AlarmFilterRule alarmFilterRule) {
        if (null == alarmFilterRule) {
            return ResultUtils.success(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmFilterRuleService.addAlarmFilterRule(alarmFilterRule);
    }

    /**
     * 删除告警过滤信息
     *
     * @param array 告警过滤id
     * @return 判断结果
     */
    @PostMapping("/batchDeleteAlarmFilterRule")
    public Result batchDeleteAlarmFilterRule(@RequestBody String[] array) {
        if (ListUtil.stringIsEmpty(array)) {
            return ResultUtils.success(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmFilterRuleService.batchDeleteAlarmFilterRule(array);
    }

    /**
     * 修改告警过滤信息
     *
     * @param alarmFilterRule 告警过滤信息
     * @return 判断结果
     */
    @PostMapping("/updateAlarmFilterRule")
    public Result updateAlarmFilterRule(@RequestBody AlarmFilterRule alarmFilterRule) {
        if (alarmFilterRule == null) {
            return ResultUtils.success(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmFilterRuleService.updateAlarmFilterRule(alarmFilterRule);
    }

    /**
     * 修改告警过滤状态
     *
     * @param status  状态
     * @param idArray 用户id
     * @return 判断结果
     */
    @PostMapping("/batchUpdateAlarmFilterRuleStatus")
    public Result batchUpdateAlarmFilterRuleStatus(@RequestParam Integer status, @RequestParam String[] idArray) {
        if (status == null || ListUtil.stringIsEmpty(idArray)) {
            return ResultUtils.success(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmFilterRuleService.batchUpdateAlarmFilterRuleStatus(status, idArray);
    }

    /**
     * 修改告警过滤存库信息
     *
     * @param stored  存库
     * @param idArray 用户id
     * @return 判断结果
     */
    @PostMapping("/batchUpdateAlarmFilterRuleStored")
    public Result batchUpdateAlarmFilterRuleStored(@RequestParam Integer stored, @RequestParam String[] idArray) {
        if (stored == null || ListUtil.stringIsEmpty(idArray)) {
            return ResultUtils.success(LogFunctionCodeConstant.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmFilterRuleService.batchUpdateAlarmFilterRuleStored(stored, idArray);
    }

    /**
     * 查询当前告警信息是否过被过滤
     *
     * @param alarmFilter 过滤条件
     * @return 告警过滤信息
     */
    @PostMapping("/queryAlarmIsIncludedFeign")
    public List<AlarmFilterRule> queryAlarmIsIncludedFeign(@RequestBody List<AlarmFilterCondition> alarmFilter) {
        if (ListUtil.isEmpty(alarmFilter)) {
            return null;
        }
        return alarmFilterRuleService.queryAlarmIsIncludedFeign(alarmFilter);
    }
}
