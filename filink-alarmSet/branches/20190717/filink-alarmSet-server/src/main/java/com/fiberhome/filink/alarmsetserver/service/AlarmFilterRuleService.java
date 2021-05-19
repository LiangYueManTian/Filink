package com.fiberhome.filink.alarmsetserver.service;


import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterRule;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterRuleDto;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-01
 */
public interface AlarmFilterRuleService extends IService<AlarmFilterRule> {

    /**
     * 查询告警过滤列表信息
     *
     * @param queryCondition 查询条件信息
     * @return 告警过滤列表信息
     */
    Result queryAlarmFilterRuleList(QueryCondition<AlarmFilterRuleDto> queryCondition);

    /**
     * 根据id查询告警过滤信息
     *
     * @param id 过滤id
     * @return 告警过滤信息
     */
    Result queryAlarmFilterRuleById(String id);

    /**
     * 新增告警过滤信息
     *
     * @param alarmFilterRule 告警过滤信息
     * @return 判断结果
     */
    Result addAlarmFilterRule(AlarmFilterRule alarmFilterRule);

    /**
     * 删除告警过滤信息
     *
     * @param array 告警过滤信息
     * @return 判断结果
     */
    Result batchDeleteAlarmFilterRule(String[] array);

    /**
     * 修改告警过滤信息
     *
     * @param alarmFilterRule 告警过滤信息
     * @return 判断结果
     */
    Result updateAlarmFilterRule(AlarmFilterRule alarmFilterRule);

    /**
     * 修改告警过滤状态
     *
     * @param status  状态
     * @param idArray 用户id
     * @return 判断结果
     */
    Result batchUpdateAlarmFilterRuleStatus(Integer status, String[] idArray);

    /**
     * 修改告警过滤存库信息
     *
     * @param stored  存库
     * @param idArray 用户id
     * @return 判断结果
     */
    Result batchUpdateAlarmFilterRuleStored(Integer stored, String[] idArray);

    /**
     * 查询当前告警信息是否过被过滤
     *
     * @param alarmFilter 过滤条件
     * @return 告警过滤信息
     */
    List<AlarmFilterRule> queryAlarmIsIncludedFeign(List<AlarmFilterCondition> alarmFilter);
}
