package com.fiberhome.filink.alarmcurrentserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmQueryTemplate;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmQueryTemplateDto;
import java.util.List;

/**
 * <p>
 * 告警模板Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-04-08
 */
public interface AlarmQueryTemplateDao extends BaseMapper<AlarmQueryTemplate> {

    /**
     * 查询告警模板列表信息
     *
     * @param alarmQueryTemplateDto 封装条件
     * @return 告警模板列表信息
     */
    List<AlarmQueryTemplate> queryAlarmTemplateList(AlarmQueryTemplateDto alarmQueryTemplateDto);

    /**
     * 批量删除告警模板信息
     *
     * @param array 告警id
     * @return 判断结果
     */
    Integer batchDeleteAlarmTemplate(String[] array);

    /**
     * 查询告警模板信息
     *
     * @param id 告警id
     * @return 告警模板信息
     */
    AlarmQueryTemplate queryAlarmTemplateById(String id);
}
