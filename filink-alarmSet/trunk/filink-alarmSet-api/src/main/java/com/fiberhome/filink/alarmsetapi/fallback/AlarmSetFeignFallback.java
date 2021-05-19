package com.fiberhome.filink.alarmsetapi.fallback;

import com.fiberhome.filink.alarmsetapi.api.AlarmSetFeign;
import com.fiberhome.filink.alarmsetapi.bean.AlarmFilterCondition;
import com.fiberhome.filink.alarmsetapi.bean.AlarmFilterRule;
import com.fiberhome.filink.alarmsetapi.bean.AlarmForwardCondition;
import com.fiberhome.filink.alarmsetapi.bean.AlarmForwardRule;
import com.fiberhome.filink.alarmsetapi.bean.AlarmLevel;
import com.fiberhome.filink.alarmsetapi.bean.AlarmName;
import com.fiberhome.filink.alarmsetapi.bean.AlarmOrderCondition;
import com.fiberhome.filink.alarmsetapi.bean.AlarmOrderRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 告警设置熔断
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/16 5:39 PM
 */
@Slf4j
@Component
public class AlarmSetFeignFallback implements AlarmSetFeign {

    /**
     * 查询当前告警信息是否过被过滤
     *
     * @param alarmFilterConditionList 封装条件
     * @return 判断结果
     */
    @Override
    public List<AlarmFilterRule> queryAlarmIsIncludedFeign(List<AlarmFilterCondition> alarmFilterConditionList) {
        log.warn("queryAlarmIsIncludedFeign failed");
        return null;
    }

    /**
     * 查询当前告警远程通知规则
     *
     * @param alarmForwardConditionList 封装条件
     * @return 告警远程通知信息
     */
    @Override
    public List<AlarmForwardRule> queryAlarmForwardRuleFeign(List<AlarmForwardCondition> alarmForwardConditionList) {
        log.warn("queryAlarmForwardRuleFeign failed");
        return null;
    }


    /**
     * 查询当前告警转工单规则
     *
     * @param alarmOrderConditionList
     * @return 告警转工单规则信息
     */
    @Override
    public AlarmOrderRule queryAlarmOrderRuleFeign(List<AlarmOrderCondition> alarmOrderConditionList) {
        log.warn("queryAlarmOrderRuleFeign failed");
        return null;
    }

    /**
     * 根据告警编码查询当前告警设置信息
     *
     * @param alarmCode 告警编码
     * @return 告警设置信息
     */
    @Override
    public AlarmName queryCurrentAlarmSetFeign(String alarmCode) {
        log.warn("queryCurrentAlarmSetFeign failed");
        return null;
    }

    /**
     * 根据告警名称查询当前告警设置信息
     *
     * @param alarmName 告警名称
     * @return 告警设置信息
     */
    @Override
    public AlarmName queryCurrentAlarmSetByNameFeign(String alarmName) {
        log.warn("queryCurrentAlarmSetByNameFeign failed");
        return null;
    }


    /**
     * 根据告警级别编码查询告警级别设置信息
     *
     * @param alarmLevelCode 告警级别编码
     * @return 告警级别信息
     */
    @Override
    public AlarmLevel queryAlarmLevelSetFeign(String alarmLevelCode) {
        log.warn("queryAlarmLevelSetFeign failed");
        return null;
    }

    /**
     * 查询历史告警设置时间
     *
     * @return 历史告警设置时间
     */
    @Override
    public Integer queryAlarmHistorySetFeign() {
        log.warn("queryAlarmHistorySetFeign failed");
        return null;
    }

}
