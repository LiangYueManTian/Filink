package com.fiberhome.filink.alarmcurrentserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTemplateDevice;

import java.util.List;

/**
 * <p>
 *  告警设施名称Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-09
 */
public interface AlarmTemplateDeviceDao extends BaseMapper<AlarmTemplateDevice> {

    /**
     * 批量新增告警对象信息
     *
     * @param list 告警名称信息
     * @return 判断结果
     */
    Integer addAlarmTemplateDevice(List<AlarmTemplateDevice> list);

    /**
     * 批量修改告警对象信息
     *
     * @param list 告警名称信息
     * @return 判断结果
     */
    Integer batchUpdateAlarmTemplateDevice(AlarmTemplateDevice alarmTemplateDevice);

    /**
     * 批量删除关联告警对象id
     *
     * @param array 告警id
     * @return 判断结果
     */
    Integer batchDeleteAlarmTemplateDevice(String[] array);

    /**
     * 查询告警对象
     *
     * @param id 告警id
     * @return 告警模板信息
     */
    List<AlarmTemplateDevice> queryAlarmDeviceById(String id);

}
