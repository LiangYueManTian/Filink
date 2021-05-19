package com.fiberhome.filink.alarmcurrentserver.controller;


import com.alibaba.druid.util.StringUtils;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmQueryTemplateDto;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmQueryTemplate;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmQueryTemplateService;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrentResultCode;
import com.fiberhome.filink.alarmcurrentserver.utils.ListUtil;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
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
 * 前端控制器
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-04-08
 */
@RestController
@RequestMapping("/alarmQueryTemplate")
public class AlarmQueryTemplateController {

    /**
     * 导出service
     */
    @Autowired
    private AlarmQueryTemplateService alarmQueryTemplateService;

    /**
     * 查询告警模板列表信息
     *
     * @param queryCondition 封装条件
     * @return 告警模板列表信息
     */
    @PostMapping("/queryAlarmTemplateList")
    public Result queryAlarmTemplateList(@RequestBody QueryCondition<AlarmQueryTemplateDto> queryCondition) {
        return alarmQueryTemplateService.queryAlarmTemplateList(queryCondition);
    }

    /**
     * 批量删除告警模板信息
     *
     * @param array 告警id
     * @return 判断结果
     */
    @PostMapping("/batchDeleteAlarmTemplate")
    public Result batchDeleteAlarmTemplate(@RequestBody String[] array) {
        if (ListUtil.stringIsEmpty(array)) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmQueryTemplateService.batchDeleteAlarmTemplate(array);
    }

    /**
     * 查询告警模板信息
     *
     * @param id 告警id
     * @return 告警模板信息
     */
    @PostMapping("/queryAlarmTemplateById/{id}")
    public Result queryAlarmTemplateById(@PathVariable String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmQueryTemplateService.queryAlarmTemplateById(id);
    }

    /**
     * 查询告警模板信息
     *
     * @param id 告警id
     * @param queryCondition 条件
     * @return 告警模板信息
     */
    @PostMapping("/queryAlarmQueryTemplateById/{id}")
    public Result queryAlarmQueryTemplateById(@PathVariable String id, @RequestBody QueryCondition queryCondition) {
        if (StringUtils.isEmpty(id)) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmQueryTemplateService.queryAlarmQueryTemplateById(id, queryCondition);
    }

    /**
     * 修改告警模板信息
     *
     * @param alarmQueryTemplate 告警模板信息
     * @return 判断结果
     */
    @PostMapping("/updateAlarmTemplate")
    public Result updateAlarmTemplate(@RequestBody AlarmQueryTemplate alarmQueryTemplate) {
        if (alarmQueryTemplate == null) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmQueryTemplateService.updateAlarmTemplate(alarmQueryTemplate);
    }

    /**
     * 新增告警模板
     *
     * @param alarmQueryTemplate 告警模板信息
     * @return 判断结果
     */
    @PostMapping("/addAlarmTemplate")
    public Result addAlarmTemplate(@RequestBody AlarmQueryTemplate alarmQueryTemplate) {
        if (alarmQueryTemplate == null) {
            return ResultUtils.warn(AlarmCurrentResultCode.INCORRECT_PARAMETER,
                    I18nUtils.getSystemString(AppConstant.INCORRECT_PARAMETER));
        }
        return alarmQueryTemplateService.addAlarmTemplate(alarmQueryTemplate);
    }
}
