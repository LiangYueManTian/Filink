package com.fiberhome.filink.alarmcurrentserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTemplateName;

import java.util.List;


/**
 * <p>
 *  告警名称Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-09
 */
public interface AlarmTemplateNameDao extends BaseMapper<AlarmTemplateName> {

    /**
     * 批量新增告警名称信息
     *
     * @param list 告警名称信息
     * @return 判断结果
     */
    Integer addAlarmTemplateName(List<AlarmTemplateName> list);


    /**
     * 批量修改告警名称信息
     *
     * @param list 告警名称信息
     * @return 判断结果
     */
    Integer batchUpdateAlarmTemplateName(AlarmTemplateName alarmTemplateName);


    /**
     * 批量删除关联告警名称id
     *
     * @param array 告警id
     * @return 判断结果
     */
    Integer batchDeleteAlarmTemplateName(String[] array);

    /**
     * 查询告警名称
     *
     * @param id 告警id
     * @return 告警模板信息
     */
    List<AlarmTemplateName> queryAlarmNameById(String id);
}
