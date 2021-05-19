package com.fiberhome.filink.alarmcurrentserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTemplateArea;

import java.util.List;

/**
 * <p>
 * 告警区域Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-09
 */
public interface AlarmTemplateAreaDao extends BaseMapper<AlarmTemplateArea> {

    /**
     * 批量新增告警区域信息
     *
     * @param list 告警名称信息
     * @return 判断结果
     */
    Integer addAlarmTemplateArea(List<AlarmTemplateArea> list);

    /**
     * 批量修改告警区域信息
     *
     * @param alarmTemplateArea 告警名称信息
     * @return 判断结果
     */
    Integer batchUpdateAlarmTemplateArea(AlarmTemplateArea alarmTemplateArea);


    /**
     * 批量删除关联告警区域id
     *
     * @param array 告警id
     * @return 判断结果
     */
    Integer batchDeleteAlarmTemplateArea(String[] array);

    /**
         * 查询告警区域
     *
     * @param id 告警id
     * @return 告警模板信息
     */
    List<AlarmTemplateArea> queryAlarmAreaById(String id);
}
