package com.fiberhome.filink.alarmsetserver.bean;

import com.baomidou.mybatisplus.annotations.TableField;
import java.util.List;
import java.util.Set;
import lombok.Data;

/**
 * <p>
 * 告警转工单规则DTO实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-27
 */
@Data
public class AlarmOrderRuleDto extends AlarmOrderRule {

    /**
     * 设施类型
     */
    @TableField(exist = false)
    private List<AlarmOrderRuleDeviceType> alarmOrderRuleDeviceTypeList;

    /**
     * 区域id
     */
    @TableField(exist = false)
    private Set<String> alarmOrderRuleArea;

    /**
     * 区域名称
     */
    @TableField(exist = false)
    private List<String> alarmOrderRuleAreaName;

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
     * 设施名称
     */
    private List<String> deviceTypeId;

    /**
     * 告警名称
     */
    private List<String> alarmName;

    /**
     * 创建时间
     */
    private Long createTimeEnd;

    /**
     * 修改时间
     */
    private Long updateTimeEnd;

    /**
     * 工单类型
     */
    private List<String> orderTypeList;

    /**
     * 1启用2禁用
     */
    private Integer[] statusArray;

    /**
     * 触发条件(1告警发生时触发)
     */
    private Integer[] triggerTypeArray;
}
