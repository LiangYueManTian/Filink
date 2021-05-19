package com.fiberhome.filink.alarmsetserver.bean;

import com.baomidou.mybatisplus.annotations.TableField;
import java.util.List;
import java.util.Set;
import lombok.Data;

/**
 * <p>
 * 告警过滤DTO实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-01
 */
@Data
public class AlarmFilterRuleDto extends AlarmFilterRule {

    /**
     * 告警名称id集合
     */
    @TableField(exist = false)
    private Set<String> alarmFilterRuleNameList;

    /**
     * 告警名称
     */
    @TableField(exist = false)
    private List<String> alarmFilterRuleNames;

    /**
     * 告警源
     */
    @TableField(exist = false)
    private Set<String> alarmFilterRuleSourceList;

    /**
     * 告警源名称
     */
    @TableField(exist = false)
    private List<String> alarmFilterRuleSourceName;

    /**
     * 排序的字段
     */
    private String sortProperties;

    /**
     * 降序还是升序
     */
    private String sort;

    /**
     * 时间过滤条件
     */
    private String relation;

    /**
     * 起始时间后
     */
    private Long beginTimeEnd;

    /**
     * 结束时间后
     */
    private Long endTimeEnd;

    /**
     * 创建时间后
     */
    private Long createTimeEnd;

    /**
     * 修改时间后
     */
    private Long updateTimeEnd;

    /**
     * 1启用2禁用
     */
    private Integer[] statusArray;

    /**
     * 是否存库(1默认保存2不保存)
     */
    private Integer[] storedArray;
}
