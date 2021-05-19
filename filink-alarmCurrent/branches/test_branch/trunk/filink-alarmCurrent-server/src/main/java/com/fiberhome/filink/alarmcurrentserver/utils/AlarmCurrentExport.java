package com.fiberhome.filink.alarmcurrentserver.utils;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmQueryTemplate;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrent18n;
import com.fiberhome.filink.alarmcurrentserver.service.impl.AlarmCurrentServiceImpl;
import com.fiberhome.filink.alarmcurrentserver.service.impl.AlarmQueryTemplateServiceImpl;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.userapi.bean.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 * 导出方法
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Component
public class AlarmCurrentExport extends AbstractExport {

    /**
     * 当前告警service
     */
    @Autowired
    private AlarmCurrentServiceImpl alarmCurrentService;

    @Autowired
    private AlarmQueryTemplateServiceImpl alarmQueryTemplateService;

    /**
     * 查询导出信息
     *
     * @param queryCondition 条件
     * @return 导出数据
     */
    @Override
    protected List queryData(QueryCondition queryCondition) {
        List<FilterCondition> filterConditions = queryCondition.getFilterConditions();
        boolean flag = filterConditions.stream().anyMatch(u -> u.getFilterField().equals(AlarmCurrent18n.TEMPLATE_ID));
        if (flag) {
            queryCondition = templateQuery(queryCondition, filterConditions);
        }
        User user = alarmCurrentService.getExportUser();
        PageBean pageBean = alarmCurrentService.queryAlarmCurrentList(queryCondition, user, false);
        List<AlarmCurrent> alarmCurrentList = (List<AlarmCurrent>) pageBean.getData();
        return alarmCurrentList;
    }

    protected QueryCondition templateQuery(QueryCondition queryCondition, List<FilterCondition> filterConditions) {
        String templateId = filterConditions.get(0).getFilterValue().toString();
        Result result = alarmQueryTemplateService.queryAlarmTemplateById(templateId);

        AlarmQueryTemplate alarmQueryTemplate = (AlarmQueryTemplate) result.getData();
        if (ObjectUtils.isEmpty(alarmQueryTemplate)) {
            return null;
        }
        // 地址
        filterConditions = new ArrayList<>();
        //获取新的模板数据
        filterConditions = alarmQueryTemplateService.copy1QueryTemplateById(filterConditions, alarmQueryTemplate);
        filterConditions = alarmQueryTemplateService.copy2QueryTemplateById(filterConditions, alarmQueryTemplate);

        //将时间条件放入查询条件
        alarmQueryTemplateService.setAlarmConditionTime(queryCondition, alarmQueryTemplate, filterConditions);
        return queryCondition;
    }

    /**
     * 查询count
     *
     * @param queryCondition
     * @return count信息
     */
    @Override
    protected Integer queryCount(QueryCondition queryCondition) {
        List<FilterCondition> filterConditions = queryCondition.getFilterConditions();
        boolean flag = filterConditions.stream().anyMatch(u -> u.getFilterField().equals(AlarmCurrent18n.TEMPLATE_ID));
        if (flag) {
            queryCondition = templateQuery(queryCondition, filterConditions);
        }
        User user = alarmCurrentService.getExportUser();
        return alarmCurrentService.getCount(queryCondition, user);
    }
}
