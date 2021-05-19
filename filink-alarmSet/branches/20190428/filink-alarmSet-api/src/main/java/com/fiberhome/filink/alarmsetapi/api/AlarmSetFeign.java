package com.fiberhome.filink.alarmsetapi.api;

import com.fiberhome.filink.alarmsetapi.bean.AlarmFilterCondition;
import com.fiberhome.filink.alarmsetapi.bean.AlarmFilterRule;
import com.fiberhome.filink.alarmsetapi.bean.AlarmForwardCondition;
import com.fiberhome.filink.alarmsetapi.bean.AlarmForwardRule;
import com.fiberhome.filink.alarmsetapi.bean.AlarmLevel;
import com.fiberhome.filink.alarmsetapi.bean.AlarmName;
import com.fiberhome.filink.alarmsetapi.bean.AlarmOrderCondition;
import com.fiberhome.filink.alarmsetapi.bean.AlarmOrderRule;
import com.fiberhome.filink.alarmsetapi.fallback.AlarmSetFeignFallback;
import com.fiberhome.filink.bean.Result;
import java.util.List;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 告警设置模块feign测试 中转站
 *
 * @author yuanyao@wistronits.com
 * create on 2018/12/16 5:19 PM
 */
@FeignClient(name = "filink-alarmset-server", fallback = AlarmSetFeignFallback.class)
public interface AlarmSetFeign {

    /**
     * 查询当前告警信息是否过被过滤
     *
     * @param alarmFilterConditionList 过滤条件
     * @return 判断结果
     */
    @PostMapping("/alarmFilterRule/queryAlarmIsIncludedFeign")
    AlarmFilterRule queryAlarmIsIncludedFeign(@RequestBody List<AlarmFilterCondition> alarmFilterConditionList);

    /**
     * 查询当前告警远程通知规则
     *
     * @param alarmForwardConditionList 远程通知条件
     * @return 告警远程通知信息
     */
    @PostMapping("/alarmForwardRule/queryAlarmForwardRuleFeign")
    List<AlarmForwardRule> queryAlarmForwardRuleFeign(@RequestBody List<AlarmForwardCondition> alarmForwardConditionList);

    /**
     * 查询当前告警转工单规则
     *
     * @param alarmOrderConditionList 告警转工单条件
     * @return 告警转工单规则信息
     */
    @PostMapping("/alarmOrderRule/queryAlarmOrderRuleFeign")
    AlarmOrderRule queryAlarmOrderRuleFeign(@RequestBody List<AlarmOrderCondition> alarmOrderConditionList);

    /**
     * 根据告警编码查询当前告警设置信息
     *
     * @param alarmCode 告警编码
     * @return 告警设置信息
     */
    @PostMapping("/alarmSet/queryCurrentAlarmSetFeign/{alarmCode}")
    AlarmName queryCurrentAlarmSetFeign(@PathVariable("alarmCode") String alarmCode);

    /**
     * 根据告警名称查询当前告警设置信息
     *
     * @param alarmName 告警名称
     * @return 告警设置信息
     */
    @PostMapping("/alarmSet/queryCurrentAlarmSetByNameFeign/{alarmName}")
    AlarmName queryCurrentAlarmSetByNameFeign(@PathVariable("alarmName") String alarmName);


    /**
     * 根据告警级别编码查询告警级别设置信息
     *
     * @param alarmLevelCode 告警级别编码
     * @return 告警级别信息
     */
    @PostMapping("/alarmSet/queryAlarmLevelSetFeign/{alarmLevelCode}")
    AlarmLevel queryAlarmLevelSetFeign(@PathVariable("alarmLevelCode") String alarmLevelCode);

    /**
     * 查询历史告警设置时间
     *
     * @return 历史告警设置时间
     */
    @PostMapping("/alarmSet/queryAlarmHistorySetFeign")
    Integer queryAlarmHistorySetFeign();

    /**
     * 查询告警过滤信息
     *
     * @return 告警过滤信息
     */
    @PostMapping("/alarmFilterRule/queryAlarmFilterRuleListFeign")
    List<AlarmFilterRule> queryAlarmFilterRuleListFeign();

    /**
     * 删除告警过滤信息
     *
     * @param array 告警过滤id
     * @return 判断结果
     */
    @PostMapping("/alarmFilterRule/batchDeleteAlarmFilterRuleFeign")
    Result batchDeleteAlarmFilterRuleFeign(@RequestBody String[] array);
}
