package com.fiberhome.filink.alarmsetserver.bean;


import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  告警远程通知dto类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-25
 */
@Data
public class AlarmForwardRuleDto extends AlarmForwardRule {

    /**
     * 通知人id
     */
    @TableField(exist = false)
    private Set<String> alarmForwardRuleUserList;

    /**
     * 区域id
     */
    @TableField(exist = false)
    private Set<String> alarmForwardRuleAreaList;

    /**
     * 区域名称
     */
    @TableField(exist = false)
    private List<String> alarmForwardRuleAreaName;

    /**
     * 设施类型
     */
    @TableField(exist = false)
    private List<AlarmForwardRuleDeviceType> alarmForwardRuleDeviceTypeList;

    /**
     * 设施类型id
     */
    private List<String> deviceTypeId;

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
     * 告警级别
     */
    private List<String> alarmLevelId;

    /**
     * 创建时间
     */
    private Long createTimeEnd;

    /**
     * 修改时间
     */
    private Long updateTimeEnd;

    /**
     * 是否启动
     */
    private Integer[] statusArray;

    /**
     * 推送方式
     */
    private Integer[] pushTypeArray;

    /**
     * 告警级别
     */
    @TableField(exist = false)
    private List<AlarmForwardRuleLevel> alarmForwardRuleLevels;
    /**
     * 校验区域和设施类型查询条件
     * @return true任意为空false
     */
    public boolean checkPermission() {
        return CollectionUtils.isEmpty(alarmForwardRuleAreaList) || CollectionUtils.isEmpty(deviceTypeId);
    }
}
