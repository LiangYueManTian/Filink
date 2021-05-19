package com.fiberhome.filink.alarmcurrentserver.service;


import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmQueryTemplate;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmQueryTemplateDto;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;

/**
 * <p>
 *  告警模板服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-04-08
 */
public interface AlarmQueryTemplateService extends IService<AlarmQueryTemplate> {

    /**
     * 查询告警模板列表信息
     *
     * @param queryCondition 封装条件
     * @return 告警模板列表信息
     */
    Result queryAlarmTemplateList(QueryCondition<AlarmQueryTemplateDto> queryCondition);

    /**
     * 批量删除告警模板信息
     *
     * @param array 告警id
     * @return 判断结果
     */
    Result batchDeleteAlarmTemplate(String[] array);

    /**
     * 查询告警模板信息
     *
     * @param id 告警id
     * @return 告警模板信息
     */
    Result queryAlarmTemplateById(String id);

    /**
     * 查询告警模板信息
     *
     * @param id 告警id
     * @param queryCondition 条件
     * @return 告警模板信息
     */
    Result queryAlarmQueryTemplateById(String id, QueryCondition queryCondition);

    /**
     * 修改告警模板信息
     *
     * @param alarmQueryTemplate 告警模板信息
     * @return 判断结果
     */
    Result updateAlarmTemplate(AlarmQueryTemplate alarmQueryTemplate);

    /**
     * 新增告警模板
     *
     * @param alarmQueryTemplate 告警模板信息
     * @return 判断结果
     */
    Result addAlarmTemplate(AlarmQueryTemplate alarmQueryTemplate);

    /**
     * 当前告警
     * @param name
     * @param condition
     * @param string
     * @return
     */
    FilterCondition selectCondition(String name, String condition, Object string);
}
