package com.fiberhome.filink.alarmcurrentserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTemplateDepartment;

import java.util.List;

/**
 * <p>
 *  告警单位Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-09
 */
public interface AlarmTemplateDepartmentDao extends BaseMapper<AlarmTemplateDepartment> {

    /**
     * 批量新增告警单位信息
     *
     * @param list 告警名称信息
     * @return 判断结果
     */
    Integer addAlarmTemplateDepartment(List<AlarmTemplateDepartment> list);


    /**
     * 批量修改告警单位信息
     *
     * @param alarmTemplateDepartment 告警名称信息
     * @return 判断结果
     */
    Integer batchUpdateAlarmTemplateDepartment(AlarmTemplateDepartment alarmTemplateDepartment);

    /**
     * 批量删除关联告警单位id
     *
     * @param array 告警id
     * @return 判断结果
     */
    Integer batchDeleteAlarmTemplateDepartment(String[] array);

    /**
     * 查询告警单位
     *
     * @param id 告警id
     * @return 告警模板信息
     */
    List<AlarmTemplateDepartment> queryAlarmDepartmentById(String id);
}
