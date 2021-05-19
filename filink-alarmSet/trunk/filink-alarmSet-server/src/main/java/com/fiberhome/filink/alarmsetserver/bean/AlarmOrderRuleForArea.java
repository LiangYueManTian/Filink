package com.fiberhome.filink.alarmsetserver.bean;

import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * <p>
 * 区域去选单位告警转工单规则查询条件实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-26
 */
@Data
public class AlarmOrderRuleForArea {
    /**
     * 区域
     */
    private String areaId;
    /**
     * 责任单位
     */
    private List<String> deptIdList;

    /**
     * 校验参数
     * @return true参数为空 false参数不为空
     */
    public boolean check() {
        if (StringUtils.isEmpty(areaId)) {
            return true;
        }
        return CollectionUtils.isEmpty(deptIdList);
    }
}
