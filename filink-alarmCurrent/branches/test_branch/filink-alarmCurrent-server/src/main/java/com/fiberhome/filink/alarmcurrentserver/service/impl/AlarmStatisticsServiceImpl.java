package com.fiberhome.filink.alarmcurrentserver.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmAreaRateStatisticsDto;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmDeviceParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmHandleStatisticsDto;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmHomeParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmIncrementalStatistics;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmNameStatisticsDto;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmSourceHomeParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmSourceIncremental;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsByLevelDto;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsGroupInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsTemp;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTime;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrent18n;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmStatisticsTempDao;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmStatisticsService;
import com.fiberhome.filink.alarmcurrentserver.utils.DateUtil;
import com.fiberhome.filink.alarmcurrentserver.utils.ListUtil;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.userapi.bean.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-21
 */
@Slf4j
@Service
public class AlarmStatisticsServiceImpl extends ServiceImpl<AlarmStatisticsTempDao, AlarmStatisticsTemp>
        implements AlarmStatisticsService {

    /**
     * mongodb实现类
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 告警统计dao
     */
    @Autowired
    private AlarmStatisticsTempDao alarmStatisticsTempDao;

    @Autowired
    private AlarmCurrentServiceImpl alarmCurrentService;

    /**
     * 区域告警占比
     *
     * @param queryCondition 封装条件
     * @return 当前告警列表信息
     */
    @Override
    public Result queryAlarmCountByLevel(QueryCondition<AlarmStatisticsParameter> queryCondition) {
        AlarmStatisticsParameter alarmStatisticsParameter = queryCondition.getBizCondition();
        //区域告警查询条件(地区,设施类型，时间)
        Criteria criteria = Criteria.where(AppConstant.AREA_NAME).in(alarmStatisticsParameter.getAreaList())
                .and(AppConstant.ALARM_SOURCE_TYPE_ID).in(alarmStatisticsParameter.getDeviceIds())
                .and(AppConstant.ALARM_BEGIN_TIME).lt(alarmStatisticsParameter.getEndTime()).gt(alarmStatisticsParameter.getBeginTime());
        //区域告警根据地区分组统计
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(AppConstant.AREA_NAME).count().as("groupNum"),
                Aggregation.project().and("_id").as("groupLevel").and("groupNum").as("groupNum").andExclude("_id"));
        //查询当前告警表
        AggregationResults<AlarmStatisticsGroupInfo> results =
                this.mongoTemplate.aggregate(agg, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
        //查询历史告警表
        AggregationResults<AlarmStatisticsGroupInfo> resultsHistory =
                this.mongoTemplate.aggregate(agg, AppConstant.ALARM_HISTORY, AlarmStatisticsGroupInfo.class);
        List<AlarmStatisticsGroupInfo> listHistory = resultsHistory.getMappedResults();
        List<AlarmStatisticsGroupInfo> list = results.getMappedResults();
        //将查询的两个表list合并。有相同的地区则统计数相加。没有添加到list中
        for (int i = 0; i < listHistory.size(); i++) {
            AlarmStatisticsGroupInfo u = listHistory.get(i);
            if (!list.stream().anyMatch(h -> h.getGroupLevel().equals(u.getGroupLevel()))) {
                list = list.stream().collect(Collectors.toList());
                list.add(u);
            } else {
                list.forEach(h -> {
                    if (h.getGroupLevel().equals(u.getGroupLevel())) {
                        h.setGroupNum(u.getGroupNum() + h.getGroupNum());
                    }
                });
            }
        }
        //查询区域告警总数量
        int totalNum = list.stream().mapToInt(u -> Integer.valueOf(u.getGroupNum())).sum();
        //将统计的占比计算后添加到对象,在根据区域将对象添加到map中
        Map<String, AlarmAreaRateStatisticsDto> map = new HashMap<>();
        list.forEach(u -> {
            AlarmAreaRateStatisticsDto dto = new AlarmAreaRateStatisticsDto();
            dto.setAreaAlarmCount(u.getGroupNum());
            dto.setAreaAlarmRate(String.format("%2.2f%%", (float) u.getGroupNum() / totalNum * 100));
            map.put(u.getGroupLevel(), dto);
        });

        //如果有为空的地区补全为0
        if (map.size() < alarmStatisticsParameter.getAreaList().size()) {
            List<String> areaList = alarmStatisticsParameter.getAreaList();
            for (int i = 0; i < areaList.size(); i++) {
                String areaKey = areaList.get(i);
                if (!map.keySet().stream().anyMatch(u -> u.equals(areaKey))) {
                    map.put(areaKey, new AlarmAreaRateStatisticsDto(0, "0.00%"));
                }
            }
        }

        return ResultUtils.success(map);
    }

    /**
     * 告警类型统计
     *
     * @param alarmStatisticsParameter 封装条件
     * @return 当前告警列表信息
     */
    @Override
    public Result queryAlarmByLevelAndArea(AlarmStatisticsParameter alarmStatisticsParameter) {
        //添加要分組的屬性 (告警級別)
        alarmStatisticsParameter.setAlarmStatisticsName(AppConstant.ALARM_FIXED_LEVEL);
        // 查询mongodb告警类型的数据list(当前告警+历史告警)
        List<AlarmStatisticsGroupInfo> list = this.queryMongodbData(alarmStatisticsParameter);
        //将查询出来的数据分组统计(将list转换为map 以区域为key值)
        Map<String, List<AlarmStatisticsGroupInfo>> areaMap =
                list.stream().collect(Collectors.groupingBy(AlarmStatisticsGroupInfo::getGroupArea));
        // 将分组统计的map进行重新转换(主要是values的对象变化)
        // (从Map<String, List<AlarmStatisticsGroupInfo>> 转为  Map<String, AlarmStatisticsByLevelDto>)
        Map<String, AlarmStatisticsByLevelDto> dtoMap = areaMap.entrySet().stream().collect(Collectors.toMap(
                stringListEntry -> stringListEntry.getKey(),
                stringListEntry -> getAlarmStatisticsByLevelDto(stringListEntry.getValue())
        ));
        //如果有为空的地区补全为0
        if (dtoMap.size() != alarmStatisticsParameter.getAreaList().size()) {
            List<String> areaList = alarmStatisticsParameter.getAreaList();
            for (int i = 0; i < areaList.size(); i++) {
                String areaKey = areaList.get(i);
                if (!dtoMap.keySet().stream().anyMatch(u -> u.equals(areaKey))) {
                    dtoMap.put(areaKey, new AlarmStatisticsByLevelDto(0, 0, 0, 0));
                }
            }
        }
        //统计map 总数,方便饼图,柱状图的统计
        List<AlarmStatisticsGroupInfo> infoList = addTotalNum(list);
        dtoMap.put("total", getAlarmStatisticsByLevelDto(infoList));
        System.out.println("结束时间" + new SimpleDateFormat("hh:mm:ss").format(new Date()));
        return ResultUtils.success(dtoMap);
    }


    /**
     * 根据告警处理统计
     *
     * @param alarmStatisticsParameter 封装条件
     * @return 当前告警列表信息
     */
    @Override
    public Result alarmHandleStatistics(AlarmStatisticsParameter alarmStatisticsParameter) {
        //添加要分組的屬性 (告警清除状态)(1,未清除,2,设备清除,3已清除)
        alarmStatisticsParameter.setAlarmStatisticsName("alarm_clean_status");
        //从数据中查询数据,(当前+历史)
        List<AlarmStatisticsGroupInfo> list = this.queryMongodbData(alarmStatisticsParameter);
        //将查询出来的数据分组统计根据地区为key值
        Map<String, List<AlarmStatisticsGroupInfo>> areaMap = list.stream()
                .collect(Collectors.groupingBy(AlarmStatisticsGroupInfo::getGroupArea));
        // 将分组统计的map进行重新转换(主要是values的对象变化)
        // (从 Map<String, List<AlarmStatisticsGroupInfo>> 转为  Map<String, AlarmHandleStatisticsDto>)
        Map<String, AlarmHandleStatisticsDto> dtoMap = areaMap.entrySet().stream().collect(Collectors.toMap(
                stringListEntry -> stringListEntry.getKey(),
                stringListEntry -> getAlarmHandleStatistics(stringListEntry.getValue())
        ));
        //如果有为空的地区补全为0
        if (dtoMap.size() != alarmStatisticsParameter.getAreaList().size()) {
            List<String> areaList = alarmStatisticsParameter.getAreaList();
            for (int i = 0; i < areaList.size(); i++) {
                String areaKey = areaList.get(i);
                if (!dtoMap.keySet().stream().anyMatch(u -> u.equals(areaKey))) {
                    dtoMap.put(areaKey, new AlarmHandleStatisticsDto(0, 0, 0));
                }
            }
        }
        //统计map 总数,方便饼图,柱状图的统计
        List<AlarmStatisticsGroupInfo> infoList = addTotalNum(list);
        dtoMap.put("total", getAlarmHandleStatistics(infoList));
        return ResultUtils.success(dtoMap);
    }

    /**
     * 告警名称统计
     *
     * @param queryCondition 封装条件
     * @return 当前告警列表信息
     */
    @Override
    public Result alarmNameStatistics(QueryCondition<AlarmStatisticsParameter> queryCondition) {
        AlarmStatisticsParameter alarmStatisticsParameter = queryCondition.getBizCondition();
        //添加要分組的屬性 (告警名称)
        alarmStatisticsParameter.setAlarmStatisticsName(AppConstant.ALARM_NAME);
        //从数据中查询数据,(当前+历史)
        List<AlarmStatisticsGroupInfo> list = this.queryMongodbData(alarmStatisticsParameter);
        ////将查询出来的数据分组统计根据地区为key值
        Map<String, List<AlarmStatisticsGroupInfo>> areaMap = list.stream()
                .collect(Collectors.groupingBy(AlarmStatisticsGroupInfo::getGroupArea));
        ////将分组统计的map进行重新转换(主要是values的对象变化)
        Map<String, AlarmNameStatisticsDto> dtoMap = areaMap.entrySet().stream().collect(Collectors.toMap(
                stringListEntry -> stringListEntry.getKey(),
                stringListEntry -> editAlarmNameStatistics(stringListEntry.getValue())
        ));
        ////如果有为空的地区补全为0
        if (dtoMap.size() != alarmStatisticsParameter.getAreaList().size()) {
            List<String> areaList = alarmStatisticsParameter.getAreaList();
            for (int i = 0; i < areaList.size(); i++) {
                String areaKey = areaList.get(i);
                if (!dtoMap.keySet().stream().anyMatch(u -> u.equals(areaKey))) {
                    dtoMap.put(areaKey, new AlarmNameStatisticsDto(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
            }
        }
        //统计map 总数,方便饼图,柱状图的统计
        List<AlarmStatisticsGroupInfo> infoList = addTotalNum(list);
        dtoMap.put("total", editAlarmNameStatistics(infoList));
        return ResultUtils.success(dtoMap);
    }


    /**
     * 告警增量统计
     *
     * @param queryCondition 封装条件
     * @return 判断结果
     */
    @Override
    public Result alarmIncrementalStatistics(QueryCondition<AlarmStatisticsParameter> queryCondition, String timeType) {
        AlarmStatisticsParameter alarmStatisticsParameter = queryCondition.getBizCondition();
        Criteria criteria = new Criteria();
        //增量统计查询的条件 根据地区，设施类型，时间统计
        //如果是admin用户的大屏模式
        if (AppConstant.ADMIN_USER.equals(alarmStatisticsParameter.getAlarmStatisticsName())) {
            criteria = Criteria.where("timeType").is(timeType).and("groupTime")
                    .lte(alarmStatisticsParameter.getEndTime()).gte(alarmStatisticsParameter.getBeginTime());
        } else {
            criteria = Criteria.where("timeType").is(timeType).and("groupArea").in(alarmStatisticsParameter.getAreaList())
                    .and("groupType").in(alarmStatisticsParameter.getDeviceIds())
                    .and("groupTime").lte(alarmStatisticsParameter.getEndTime()).gte(alarmStatisticsParameter.getBeginTime());

        }
        //根据时间分组，统计告警的数量
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("groupTime").sum("groupNum").as("groupNum"),
                Aggregation.project().and("_id").as("groupLevel").and("groupNum").as("groupNum").andExclude("_id"));
        //查询mongodb数据
        List<String> timeList = new ArrayList<>();
        if (AppConstant.DAY.equals(timeType)) {
            timeList = DateUtil.getTimeFingBetween(alarmStatisticsParameter.getBeginTime(), alarmStatisticsParameter.getEndTime());
        } else if (AppConstant.WEEK.equals(timeType)) {
            timeList = DateUtil.getWeekFingBetween();
        } else if (AppConstant.MONTH.equals(timeType)) {
            timeList = DateUtil.getMonthFingBetween();
        }
        List<AlarmStatisticsGroupInfo> list
                = this.mongoTemplate.aggregate(agg, "alarm_incremental_statistics", AlarmStatisticsGroupInfo.class).getMappedResults();
        //返回完整的时间间隔
        List<AlarmStatisticsGroupInfo> listStatistics = new ArrayList<>();
        //将list时间戳格式化
        list.stream().forEach(u -> u.setGroupLevel(new SimpleDateFormat("yyyy-MM-dd").format(Long.valueOf(u.getGroupLevel()))));
        listStatistics.addAll(list);

        timeList.forEach(u -> {
            if (!list.stream().anyMatch(h -> h.getGroupLevel().equals(u))) {
                AlarmStatisticsGroupInfo alarmStatisticsGroupInfo = new AlarmStatisticsGroupInfo();
                alarmStatisticsGroupInfo.setGroupLevel(u);
                alarmStatisticsGroupInfo.setGroupNum(0);
                listStatistics.add(alarmStatisticsGroupInfo);
            }
        });
        //排序
        listStatistics.sort((a, b) -> a.getGroupLevel().compareTo(b.getGroupLevel()));
        return ResultUtils.success(listStatistics);
    }

    /**
     * 统计总数
     *
     * @param list 分组数据
     * @return 分组信息
     */
    private List<AlarmStatisticsGroupInfo> addTotalNum(List<AlarmStatisticsGroupInfo> list) {
        //从mongodb查询的数据进行分组
        Map<String, List<AlarmStatisticsGroupInfo>> totalNum = list.stream()
                .collect(Collectors.groupingBy(AlarmStatisticsGroupInfo::getGroupLevel));
        //将分组后的map 对象的数量相加.返回list
        return totalNum.values().stream().map(u -> {
            AlarmStatisticsGroupInfo info = new AlarmStatisticsGroupInfo();
            info.setGroupLevel(u.get(0).getGroupLevel());
            info.setGroupNum(u.stream().mapToInt(g -> g.getGroupNum()).sum());
            return info;
        }).collect(Collectors.toList());
    }

    /**
     * 查询mongodb数据
     * （历史与当前告警）
     *
     * @param alarmStatisticsParameter 条件
     * @return 判断结果
     */
    private List<AlarmStatisticsGroupInfo> queryMongodbData(AlarmStatisticsParameter alarmStatisticsParameter) {
        //根据设施ID查询
        Criteria criteria = new Criteria();
        if (alarmStatisticsParameter.getAlarmStatisticsName().equals(AlarmCurrent18n.DEVICE_ID)) {
            criteria = Criteria.where(AppConstant.ALARM_SOURCE).in(alarmStatisticsParameter.getDeviceIds())
                    .and(AppConstant.ALARM_BEGIN_TIME).lt(alarmStatisticsParameter.getEndTime())
                    .gt(alarmStatisticsParameter.getBeginTime());
        } else {
            //查询的基本条件(告警类型:设施产生的告警，区域，类型，时间)
            criteria = Criteria.where(AppConstant.AREA_NAME).in(alarmStatisticsParameter.getAreaList())
                    .and(AppConstant.ALARM_SOURCE_TYPE_ID).in(alarmStatisticsParameter.getDeviceIds())
                    .and(AppConstant.ALARM_BEGIN_TIME).lt(alarmStatisticsParameter.getEndTime())
                    .gt(alarmStatisticsParameter.getBeginTime());
            //分组查询根据
        }
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(alarmStatisticsParameter.getAlarmStatisticsName(), AppConstant.AREA_NAME).count().as("groupNum"),
                Aggregation.project().and(alarmStatisticsParameter.getAlarmStatisticsName()).as("groupLevel")
                        .and(AppConstant.AREA_NAME).as("groupArea").and("groupNum").as("groupNum").andExclude("_id"));
        //查询当前表的数据
        AggregationResults<AlarmStatisticsGroupInfo> results =
                this.mongoTemplate.aggregate(agg, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
        //查询历史表的数据
        AggregationResults<AlarmStatisticsGroupInfo> resultsHistory =
                this.mongoTemplate.aggregate(agg, AppConstant.ALARM_HISTORY, AlarmStatisticsGroupInfo.class);
        List<AlarmStatisticsGroupInfo> listHistory = resultsHistory.getMappedResults();
        List<AlarmStatisticsGroupInfo> list = results.getMappedResults();
        //将查询的两个list组合.(有相同的地区,类型的数据要相加.没有则添加到list)
        list = list.stream().collect(Collectors.toList());
        for (int i = 0; i < listHistory.size(); i++) {
            AlarmStatisticsGroupInfo u = listHistory.get(i);
            if (!list.stream().anyMatch(h -> h.getGroupArea().equals(u.getGroupArea())
                    && h.getGroupLevel().equals(u.getGroupLevel()))) {
                list.add(u);
            } else {
                list.forEach(h -> {
                    if (h.getGroupArea().equals(u.getGroupArea()) && h.getGroupLevel().equals(u.getGroupLevel())) {
                        h.setGroupNum(u.getGroupNum() + h.getGroupNum());
                    }
                });
            }
        }
        return list;
    }


    /**
     * 将告警的级别转换为对象（列转行）
     *
     * @param list
     * @return
     */
    protected AlarmStatisticsByLevelDto getAlarmStatisticsByLevelDto(List<AlarmStatisticsGroupInfo> list) {
        // 初始化对象数据(返回的结果不会为null)
        AlarmStatisticsByLevelDto alarmStatistics =
                new AlarmStatisticsByLevelDto(0, 0, 0, 0);
        list.forEach(u -> {
            switch (u.getGroupLevel()) {
                case "1":
                    alarmStatistics.setUrgentAlarmCount(u.getGroupNum());
                    break;
                case "2":
                    alarmStatistics.setMainAlarmCount(u.getGroupNum());
                    break;
                case "3":
                    alarmStatistics.setMinorAlarmCount(u.getGroupNum());
                    break;
                case "4":
                    alarmStatistics.setHintAlarmCount(u.getGroupNum());
                    break;
                default:
            }
        });
        return alarmStatistics;
    }

    /**
     * 将告警处理的list转换对象显示（列转行）
     *
     * @param list
     * @return
     */
    protected AlarmHandleStatisticsDto getAlarmHandleStatistics(List<AlarmStatisticsGroupInfo> list) {
        AlarmHandleStatisticsDto alarmStatistics = new AlarmHandleStatisticsDto(0, 0, 0);
        list.forEach(u -> {
            switch (u.getGroupLevel()) {
                case "1":
                    alarmStatistics.setCleared(u.getGroupNum());
                    break;
                case "2":
                    alarmStatistics.setDeviceCleared(u.getGroupNum());
                    break;
                case "3":
                    alarmStatistics.setNucleared(u.getGroupNum());
                    break;
                default:
            }
        });
        return alarmStatistics;
    }

    /**
     * 将查询告警名称list转换为告警名称统计对象（列转行）
     *
     * @param list
     * @return
     */
    protected AlarmNameStatisticsDto editAlarmNameStatistics(List<AlarmStatisticsGroupInfo> list) {
        AlarmNameStatisticsDto alarmStatistics = new AlarmNameStatisticsDto(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        list.forEach(u -> {
            switch (u.getGroupLevel()) {
                case "撬门":
                    alarmStatistics.setPryDoor(u.getGroupNum());
                    break;
                case "撬锁":
                    alarmStatistics.setPryLock(u.getGroupNum());
                    break;
                case "未关门":
                    alarmStatistics.setNotClosed(u.getGroupNum());
                    break;
                case "高温":
                    alarmStatistics.setHighTemperature(u.getGroupNum());
                    break;
                case "低温":
                    alarmStatistics.setLowTemperature(u.getGroupNum());
                    break;
                case "通信中断":
                    alarmStatistics.setCommunicationInterrupt(u.getGroupNum());
                    break;
                case "水浸":
                    alarmStatistics.setLeach(u.getGroupNum());
                    break;
                case "未关锁":
                    alarmStatistics.setUnLock(u.getGroupNum());
                    break;
                case "倾斜":
                    alarmStatistics.setLean(u.getGroupNum());
                    break;
                case "震动":
                    alarmStatistics.setShake(u.getGroupNum());
                    break;
                case "电量":
                    alarmStatistics.setElectricity(u.getGroupNum());
                    break;
                case "非法开门":
                    alarmStatistics.setViolenceClose(u.getGroupNum());
                    break;
                case "工单超时":
                    alarmStatistics.setOrderOutOfTime(u.getGroupNum());
                    break;
                case "应急开锁告警":
                    alarmStatistics.setEmergencyLock(u.getGroupNum());
                    break;
                case "湿度":
                    alarmStatistics.setHumidity(u.getGroupNum());
                    break;
                case "非法开盖（内盖）":
                    alarmStatistics.setIllegalOpeningInnerCover(u.getGroupNum());
                default:
            }
        });
        return alarmStatistics;
    }

    @Override
    public void queryStatisticsData(AlarmSourceHomeParameter alarmSourceHomeParameter) {
        String type = alarmSourceHomeParameter.getType();
        if (type.equals(AppConstant.DAY)) {
            alarmSourceHomeParameter.setBeginTime(DateUtil.getAdvanceNumberDay(1));
            alarmSourceHomeParameter.setEndTime(DateUtil.getAdvanceNumberEndDay());
            Criteria criteria = Criteria.where(AppConstant.ALARM_BEGIN_TIME)
                    .gte(alarmSourceHomeParameter.getBeginTime()).lte(alarmSourceHomeParameter.getEndTime());
            Aggregation agg = Aggregation.newAggregation(
                    Aggregation.match(criteria),
                    Aggregation.group(AppConstant.ALARM_SOURCE_TYPE_ID, "area_name").count().as("groupNum"),
                    Aggregation.project().and(AppConstant.ALARM_SOURCE_TYPE_ID).as("groupLevel")
                            .and("area_name").as("groupArea").and("groupNum").as("groupNum").andExclude("_id"));
            AggregationResults<AlarmStatisticsGroupInfo> results =
                    this.mongoTemplate.aggregate(agg, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
            AggregationResults<AlarmStatisticsGroupInfo> resultsHistory =
                    this.mongoTemplate.aggregate(agg, AppConstant.ALARM_HISTORY, AlarmStatisticsGroupInfo.class);
            List<AlarmStatisticsGroupInfo> listHistory = resultsHistory.getMappedResults();
            List<AlarmStatisticsGroupInfo> list = results.getMappedResults();
            list = list.stream().collect(Collectors.toList());
            for (int i = 0; i < listHistory.size(); i++) {
                AlarmStatisticsGroupInfo u = listHistory.get(i);
                if (!list.stream().anyMatch(h -> h.getGroupLevel().equals(u.getGroupLevel()))) {
                    list.add(u);
                } else {
                    list.forEach(h -> {
                        if (h.getGroupLevel().equals(u.getGroupLevel())) {
                            h.setGroupNum(u.getGroupNum() + h.getGroupNum());
                        }
                    });
                }
            }
            setMongoTemplate(list, alarmSourceHomeParameter);
        } else if (type.equals(AppConstant.WEEK)) {
            //开始时间为上一周的星期1的凌晨
            alarmSourceHomeParameter.setBeginTime(DateUtil.getAdvanceNumberWeek(1));
            //结束时间为上一周的星期日结束
            alarmSourceHomeParameter.setEndTime(DateUtil.getAdvanceNumberEndWeek());
            List<AlarmStatisticsGroupInfo> alarmStatistics = getAlarmStatistics(alarmSourceHomeParameter);
            setMongoTemplate(alarmStatistics, alarmSourceHomeParameter);
        } else if (type.equals(AppConstant.MONTH)) {
            alarmSourceHomeParameter.setBeginTime(DateUtil.getAdvanceNumberMonth(1));
            alarmSourceHomeParameter.setEndTime(DateUtil.getAdvanceNumberEndMonth());
            List<AlarmStatisticsGroupInfo> alarmStatistics = getAlarmStatistics(alarmSourceHomeParameter);
            setMongoTemplate(alarmStatistics, alarmSourceHomeParameter);
        }
    }

    /**
     * 分组信息
     *
     * @param alarmSourceHomeParameter 条件信息
     * @return 分组信息
     */
    protected List<AlarmStatisticsGroupInfo> getAlarmStatistics(AlarmSourceHomeParameter alarmSourceHomeParameter) {
        Criteria criteria = Criteria.where("groupTime")
                .lte(alarmSourceHomeParameter.getEndTime())
                .gte(alarmSourceHomeParameter.getBeginTime())
                .and("timeType").is(AppConstant.DAY);
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("groupType", "groupArea").sum("groupNum").as("groupNum"),
                Aggregation.project().and("groupType").as("groupLevel")
                        .and("groupArea").as("groupArea").and("groupNum").as("groupNum").andExclude("_id"));
        AggregationResults<AlarmStatisticsGroupInfo> results =
                this.mongoTemplate.aggregate(agg, "alarm_incremental_statistics", AlarmStatisticsGroupInfo.class);
        return results.getMappedResults();
    }

    /**
     * 添加增量信息
     *
     * @param list                     条件信息
     * @param alarmSourceHomeParameter 设施信息
     */
    protected void setMongoTemplate(List<AlarmStatisticsGroupInfo> list, AlarmSourceHomeParameter alarmSourceHomeParameter) {
        List<AlarmIncrementalStatistics> listStatistics = new ArrayList<>();
        list.forEach(u -> {
            AlarmIncrementalStatistics ais = new AlarmIncrementalStatistics();
            ais.setGroupArea(u.getGroupArea());
            ais.setGroupNum(Long.valueOf(u.getGroupNum()));
            ais.setGroupType(u.getGroupLevel());
            ais.setGroupTime(alarmSourceHomeParameter.getBeginTime());
            ais.setTimeType(alarmSourceHomeParameter.getType());
            listStatistics.add(ais);
        });
        this.mongoTemplate.insertAll(listStatistics);
    }

    /**
     * 删除告警增量统计数据
     *
     * @param timeType 模板ids
     * @return 判断结果
     */
    @Override
    public void deleteAlarmIncrementalStatistics(Long deleteTime, String timeType) {
        Query query = new Query(Criteria.where("timeType").is(timeType).and("groupTime").lt(deleteTime));
        mongoTemplate.remove(query, AlarmIncrementalStatistics.class);
    }


    /**
     * 批量删除告警统计模板
     *
     * @param ids 模板ids
     * @return 判断结果
     */
    @Override
    public Integer deleteAlarmStatisticsTemp(String[] ids) {
        return alarmStatisticsTempDao.batchDeleteAlarmStatisticsTemp(ids);
    }

    /**
     * 批量修改告警统计模板信息
     *
     * @param alarmStatisticsTemp 告警名称信息
     * @return 判断结果
     */
    @Override
    public Integer updateAlarmStatisticsTemp(AlarmStatisticsTemp alarmStatisticsTemp) {
        return alarmStatisticsTempDao.batchUpdateAlarmStatisticsTemp(alarmStatisticsTemp);
    }

    /**
     * 批量新增告警统计模板信息
     *
     * @param list 告警名称信息
     * @return 判断结果
     */
    @Override
    public Integer addAlarmStatisticsTemp(List<AlarmStatisticsTemp> list) {
        return alarmStatisticsTempDao.addAlarmStatisticsTemp(list);
    }

    /**
     * 根据ID查询告警统计模板
     *
     * @param id 告警id
     * @return 告警模板信息
     */
    @Override
    public AlarmStatisticsTemp queryAlarmStatisticsTempById(String id) {
        return alarmStatisticsTempDao.queryAlarmStatisticsTempById(id);
    }

    /**
     * 查询告警设施tcp10
     *
     * @param queryCondition 条件信息
     * @return 告警tcp信息
     */
    @Override
    public Result queryAlarmNameGroup(QueryCondition<AlarmStatisticsParameter> queryCondition) {
        AlarmStatisticsParameter alarmStatisticsParameter = queryCondition.getBizCondition();
        Criteria criteria = Criteria.where(AppConstant.AREA_ID).in(alarmStatisticsParameter.getAreaList())
                .and("alarm_code").in(alarmStatisticsParameter.getAlarmCodes())
                .and(AppConstant.ALARM_SOURCE_TYPE_ID).is(alarmStatisticsParameter.getDeviceType())
                .and(AppConstant.ALARM_BEGIN_TIME).lt(alarmStatisticsParameter.getEndTime()).gt(alarmStatisticsParameter.getBeginTime());
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(AppConstant.ALARM_SOURCE).count().as(AppConstant.COUNT),
                Aggregation.project(AppConstant.COUNT).and("alarmSource").previousOperation());
        // 对告警未清除的数据进行分组
        AggregationResults<AlarmDeviceParameter> res =
                mongoTemplate.aggregate(aggregation, AppConstant.ALARM_CURRENT, AlarmDeviceParameter.class);
        AggregationResults<AlarmDeviceParameter> results =
                mongoTemplate.aggregate(aggregation, AppConstant.ALARM_HISTORY, AlarmDeviceParameter.class);
        // 当前告警的分组数据
        List<AlarmDeviceParameter> list = res.getMappedResults();
        // 历史告警的分组数据
        List<AlarmDeviceParameter> historyList = results.getMappedResults();
        list = list.stream().collect(Collectors.toList());
        List<AlarmDeviceParameter> deviceParameterList = forDeviceId(list, historyList);
        // 排序
        List<AlarmDeviceParameter> collect = deviceParameterList.stream().limit(alarmStatisticsParameter.getTopCount())
                .sorted(Comparator.comparing(AlarmDeviceParameter::getCount).reversed()).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    /**
     * 首页设施统计告警名称信息
     *
     * @param alarmHomeParameter 条件信息
     * @return 统计告警信息
     */
    @Override
    public Result queryAlarmNameHomePage(AlarmHomeParameter alarmHomeParameter) {
        this.selectHome(alarmHomeParameter);
        Criteria criteria = Criteria.where(AppConstant.ALARM_SOURCE).is(alarmHomeParameter.getDeviceId())
                .and(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                .and(AppConstant.ALARM_BEGIN_TIME).lt(System.currentTimeMillis()).gt(alarmHomeParameter.getBeginTime());
        AlarmNameStatisticsDto alarmNameStatisticsDto = alarmNameHomePage(criteria);
        return ResultUtils.success(alarmNameStatisticsDto);
    }

    /**
     * 设施告警名称统计
     *
     * @param criteria 条件信息
     * @return 名称统计信息
     */
    private AlarmNameStatisticsDto alarmNameHomePage(Criteria criteria) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(AppConstant.ALARM_NAME).count().as("groupNum"),
                Aggregation.project("groupNum").and("groupLevel").previousOperation());
        AggregationResults<AlarmStatisticsGroupInfo> results =
                this.mongoTemplate.aggregate(aggregation, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
        List<AlarmStatisticsGroupInfo> list = results.getMappedResults();
        return editAlarmNameStatistics(list);
    }

    /**
     * 设施统计告警级别信息
     *
     * @param alarmSourceHomeParameter 条件信息
     * @return 统计告警信息
     */
    @Override
    public Result queryAlarmCurrentSourceLevel(AlarmSourceHomeParameter alarmSourceHomeParameter) {
        Criteria criteria = Criteria.where(AppConstant.ALARM_SOURCE).is(alarmSourceHomeParameter.getDeviceId())
                .and(AppConstant.ALARM_BEGIN_TIME).lt(alarmSourceHomeParameter.getEndTime()).gt(alarmSourceHomeParameter.getBeginTime());
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(AppConstant.ALARM_FIXED_LEVEL).count().as("groupNum"),
                Aggregation.project("groupNum").and("groupLevel").previousOperation());
        AggregationResults<AlarmStatisticsGroupInfo> results =
                this.mongoTemplate.aggregate(aggregation, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
        List<AlarmStatisticsGroupInfo> list = results.getMappedResults();
        AlarmStatisticsByLevelDto alarmStatisticsByLevelDto = getAlarmStatisticsByLevelDto(list);
        return ResultUtils.success(alarmStatisticsByLevelDto);
    }

    /**
     * 设施统计历史告警级别信息
     *
     * @param alarmSourceHomeParameter 条件信息
     * @return 统计告警信息
     */
    @Override
    public Result queryAlarmHistorySourceLevel(AlarmSourceHomeParameter alarmSourceHomeParameter) {
        Criteria criteria = Criteria.where(AppConstant.ALARM_SOURCE).is(alarmSourceHomeParameter.getDeviceId())
                .and(AppConstant.ALARM_BEGIN_TIME).lt(alarmSourceHomeParameter.getEndTime()).gt(alarmSourceHomeParameter.getBeginTime());
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(AppConstant.ALARM_FIXED_LEVEL).count().as("groupNum"),
                Aggregation.project("groupNum").and("groupLevel").previousOperation());
        AggregationResults<AlarmStatisticsGroupInfo> results =
                this.mongoTemplate.aggregate(aggregation, "alarm_history", AlarmStatisticsGroupInfo.class);
        List<AlarmStatisticsGroupInfo> list = results.getMappedResults();
        AlarmStatisticsByLevelDto alarmStatisticsByLevelDto = getAlarmStatisticsByLevelDto(list);
        return ResultUtils.success(alarmStatisticsByLevelDto);
    }

    /**
     * 设施统计当前告警名称信息
     *
     * @param alarmSourceHomeParameter 条件信息
     * @return 统计告警信息
     */
    @Override
    public Result queryAlarmCurrentSourceName(AlarmSourceHomeParameter alarmSourceHomeParameter) {
        Criteria criteria = Criteria.where(AppConstant.ALARM_SOURCE).is(alarmSourceHomeParameter.getDeviceId())
                .and(AppConstant.ALARM_BEGIN_TIME).lt(alarmSourceHomeParameter.getEndTime()).gt(alarmSourceHomeParameter.getBeginTime());
        AlarmNameStatisticsDto alarmNameStatisticsDto = alarmNameHomePage(criteria);
        return ResultUtils.success(alarmNameStatisticsDto);
    }

    /**
     * 设施统计历史告警名称信息
     *
     * @param alarmSourceHomeParameter 条件信息
     * @return 统计告警信息
     */
    @Override
    public Result queryAlarmHistorySourceName(AlarmSourceHomeParameter alarmSourceHomeParameter) {
        Criteria criteria = Criteria.where("alarm_source").is(alarmSourceHomeParameter.getDeviceId())
                .and(AppConstant.ALARM_BEGIN_TIME).lt(alarmSourceHomeParameter.getEndTime()).gt(alarmSourceHomeParameter.getBeginTime());
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(AppConstant.ALARM_NAME).count().as("groupNum"),
                Aggregation.project("groupNum").and("groupLevel").previousOperation());
        AggregationResults<AlarmStatisticsGroupInfo> results =
                this.mongoTemplate.aggregate(aggregation, "alarm_history", AlarmStatisticsGroupInfo.class);
        List<AlarmStatisticsGroupInfo> list = results.getMappedResults();
        AlarmNameStatisticsDto alarmNameStatisticsDto = editAlarmNameStatistics(list);
        return ResultUtils.success(alarmNameStatisticsDto);
    }

    /**
     * 新增告警设施统计信息
     *
     * @param alarmSourceHomeParameter 条件信息
     */
    @Override
    public void querySourceIncremental(AlarmSourceHomeParameter alarmSourceHomeParameter) {
        // 日统计
        if (alarmSourceHomeParameter.getSource() == LogFunctionCodeConstant.ALARM_STATUS_ONE) {
            // 开始时间为前2天
            alarmSourceHomeParameter.setBeginTime(DateUtil.getAdvanceNumberDay(1));
            // 结束时间为前一天
            alarmSourceHomeParameter.setEndTime(DateUtil.getAdvanceNumberEndDay());
            alarmSourceHomeParameter.setType(AppConstant.DAY);
            Criteria criteria = Criteria.where(AppConstant.ALARM_BEGIN_TIME)
                    .lte(alarmSourceHomeParameter.getEndTime()).gte(alarmSourceHomeParameter.getBeginTime());
            Aggregation agg = Aggregation.newAggregation(
                    Aggregation.match(criteria),
                    Aggregation.group(AppConstant.ALARM_SOURCE).count().as(AppConstant.COUNT),
                    Aggregation.project(AppConstant.COUNT).and("alarmSource").previousOperation());
            AggregationResults<AlarmDeviceParameter> results =
                    this.mongoTemplate.aggregate(agg, AppConstant.ALARM_CURRENT, AlarmDeviceParameter.class);
            AggregationResults<AlarmDeviceParameter> resultsHistory =
                    this.mongoTemplate.aggregate(agg, AppConstant.ALARM_HISTORY, AlarmDeviceParameter.class);
            List<AlarmDeviceParameter> listHistory = resultsHistory.getMappedResults();
            List<AlarmDeviceParameter> list = results.getMappedResults();
            list = list.stream().collect(Collectors.toList());
            for (int i = 0; i < listHistory.size(); i++) {
                AlarmDeviceParameter u = listHistory.get(i);
                if (!list.stream().anyMatch(h -> h.getAlarmSource().equals(u.getAlarmSource()))) {
                    list.add(u);
                } else {
                    list.forEach(h -> {
                        if (h.getAlarmSource().equals(u.getAlarmSource())) {
                            h.setCount(u.getCount() + h.getCount());
                        }
                    });
                }
            }
            mongodbInsertAll(list, alarmSourceHomeParameter);
            // 周统计
        } else if (alarmSourceHomeParameter.getSource() == LogFunctionCodeConstant.ALARM_STATUS_TWO) {
            //开始时间为前15周
            alarmSourceHomeParameter.setBeginTime(DateUtil.getAdvanceNumberWeek(1));
            //结束时间为上一周
            alarmSourceHomeParameter.setEndTime(DateUtil.getAdvanceNumberEndWeek());
            alarmSourceHomeParameter.setType(AppConstant.WEEK);
            Criteria criteria = Criteria.where("groupTime")
                    .lte(String.valueOf(alarmSourceHomeParameter.getEndTime()))
                    .gte(String.valueOf(alarmSourceHomeParameter.getBeginTime()))
                    .and("type").is(AppConstant.DAY);
            Aggregation agg = Aggregation.newAggregation(
                    Aggregation.match(criteria),
                    Aggregation.group("deviceId").sum("count").as(AppConstant.COUNT),
                    Aggregation.project(AppConstant.COUNT).and("alarmSource").previousOperation());
            AggregationResults<AlarmDeviceParameter> results =
                    this.mongoTemplate.aggregate(agg, "alarm_source_incremental", AlarmDeviceParameter.class);
            List<AlarmDeviceParameter> list = results.getMappedResults();
            mongodbInsertAll(list, alarmSourceHomeParameter);
        }
    }

    protected void mongodbInsertAll(List<AlarmDeviceParameter> list, AlarmSourceHomeParameter alarmSourceHomeParameter) {
        List<AlarmSourceIncremental> alarmSourceIncrementalList = new ArrayList<>();
        list.forEach(u -> {
            AlarmSourceIncremental alarmSourceIncremental = new AlarmSourceIncremental();
            alarmSourceIncremental.setId(NineteenUUIDUtils.uuid());
            alarmSourceIncremental.setDeviceId(u.getAlarmSource());
            alarmSourceIncremental.setGroupTime(String.valueOf(alarmSourceHomeParameter.getEndTime()));
            alarmSourceIncremental.setCount(u.getCount());
            alarmSourceIncremental.setType(alarmSourceHomeParameter.getType());
            alarmSourceIncrementalList.add(alarmSourceIncremental);
        });
        mongoTemplate.insertAll(alarmSourceIncrementalList);
    }

    /**
     * 设施增量查询信息
     *
     * @param alarmSourceHomeParameter 条件信息
     * @return 设施增量查询信息
     */
    @Override
    public Result queryAlarmSourceIncremental(AlarmSourceHomeParameter alarmSourceHomeParameter) {
        //查询mongodb数据
        List<String> timeList = new ArrayList<>();
        if (AppConstant.DAY.equals(alarmSourceHomeParameter.getType())) {
            timeList = DateUtil.getTimeFingBetween(alarmSourceHomeParameter.getBeginTime(), alarmSourceHomeParameter.getEndTime());
        } else if (AppConstant.WEEK.equals(alarmSourceHomeParameter.getType())) {
            timeList = DateUtil.getTimeFingween(alarmSourceHomeParameter.getBeginTime(), alarmSourceHomeParameter.getEndTime());
        }
        Criteria criteria = Criteria.where("type").is(alarmSourceHomeParameter.getType())
                .and("deviceId").is(alarmSourceHomeParameter.getDeviceId())
                .and("groupTime").lte(String.valueOf(alarmSourceHomeParameter.getEndTime()))
                .gte(String.valueOf(alarmSourceHomeParameter.getBeginTime()));
        Query query = new Query(criteria);
        List<AlarmSourceIncremental> list = this.mongoTemplate.find(query, AlarmSourceIncremental.class);
        //返回完整的时间间隔
        List<AlarmSourceIncremental> listStatistics = new ArrayList<>();
        //将list时间戳格式化
        list.stream().forEach(u -> u.setGroupTime(new SimpleDateFormat("yyyy-MM-dd").format(Long.valueOf(u.getGroupTime()))));
        listStatistics.addAll(list);

        timeList.forEach(u -> {
            if (!list.stream().anyMatch(h -> h.getGroupTime().equals(u))) {
                AlarmSourceIncremental alarmSourceIncremental = new AlarmSourceIncremental();
                alarmSourceIncremental.setGroupTime(u);
                alarmSourceIncremental.setCount(0L);
                listStatistics.add(alarmSourceIncremental);
            }
        });
        return ResultUtils.success(listStatistics);
    }

    /**
     * 大屏告警级别统计信息
     *
     * @return 告警级别统计信息
     */
    @Override
    public Result queryAlarmCurrentLevelGroup() {
        Criteria criteria = new Criteria();
        criteria = selectPermissionsStatus(criteria);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(AppConstant.ALARM_FIXED_LEVEL).count().as("groupNum"),
                Aggregation.project("groupNum").and("groupLevel").previousOperation());
        AggregationResults<AlarmStatisticsGroupInfo> results =
                this.mongoTemplate.aggregate(aggregation, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
        List<AlarmStatisticsGroupInfo> list = results.getMappedResults();
        AlarmStatisticsByLevelDto alarmStatisticsByLevelDto = getAlarmStatisticsByLevelDto(list);
        return ResultUtils.success(alarmStatisticsByLevelDto);
    }

    /**
     * 用户权限
     *
     * @param criteria 条件信息
     * @return 条件信息
     */
    private Criteria selectPermissions(Criteria criteria) {
        User user = alarmCurrentService.getUser();
        if (!user.getId().equals(AppConstant.ONE)) {
            List<String> deviceTypes = alarmCurrentService.getDeviceTypes(user);
            List<String> areaIds = alarmCurrentService.getUserAreaIds(user);
            if (ListUtil.isEmpty(areaIds) || ListUtil.isEmpty(deviceTypes)) {
                criteria = Criteria.where(AppConstant.ALARM_SOURCE_TYPE_ID).in((Object) null).and(AppConstant.AREA_ID).in((Object) null);
            } else {
                criteria = Criteria.where(AppConstant.ALARM_SOURCE_TYPE_ID).in(deviceTypes).and(AppConstant.AREA_ID).in(areaIds);
            }
        }
        return criteria;
    }

    /**
     * 用户权限加未清除数据
     *
     * @param criteria 条件信息
     * @return 条件信息
     */
    private Criteria selectPermissionsStatus(Criteria criteria) {
        User user = alarmCurrentService.getUser();
        if (!user.getId().equals(AppConstant.ONE)) {
            List<String> deviceTypes = alarmCurrentService.getDeviceTypes(user);
            List<String> areaIds = alarmCurrentService.getUserAreaIds(user);
            if (ListUtil.isEmpty(areaIds) || ListUtil.isEmpty(deviceTypes)) {
                criteria = Criteria.where(AppConstant.ALARM_SOURCE_TYPE_ID).in((Object) null).and(AppConstant.AREA_ID).in((Object) null)
                        .and(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE);
            } else {
                criteria = Criteria.where(AppConstant.ALARM_SOURCE_TYPE_ID).in(deviceTypes).and(AppConstant.AREA_ID).in(areaIds)
                        .and(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE);
            }
        } else {
            criteria = Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE);
        }
        return criteria;
    }

    /**
     * 过滤工单超时的用户权限
     *
     * @param criteria 条件信息
     * @return 条件信息
     */
    private Criteria selectPermissionIds(Criteria criteria) {
        User user = alarmCurrentService.getUser();
        if (!user.getId().equals(AppConstant.ONE)) {
            List<String> deviceTypes = alarmCurrentService.getDeviceTypes(user);
            List<String> areaIds = alarmCurrentService.getUserAreaIds(user);
            if (ListUtil.isEmpty(areaIds) || ListUtil.isEmpty(deviceTypes)) {
                criteria = Criteria.where(AppConstant.ALARM_SOURCE_TYPE_ID).in((Object) null).and(AppConstant.AREA_ID).in((Object) null)
                        .and(AlarmCurrent18n.ALARM_TYPE).is(LogFunctionCodeConstant.ALARM_STATUS_ONE);
            } else {
                criteria = Criteria.where(AppConstant.ALARM_SOURCE_TYPE_ID).in(deviceTypes)
                        .and(AppConstant.AREA_ID).in(areaIds)
                        .and(AlarmCurrent18n.ALARM_TYPE).is(LogFunctionCodeConstant.ALARM_STATUS_ONE);
            }
        } else {
            criteria = Criteria.where(AlarmCurrent18n.ALARM_TYPE).is(LogFunctionCodeConstant.ALARM_STATUS_ONE);
        }
        return criteria;
    }

    /**
     * 大屏设施根据日周月topn10
     *
     * @param alarmTime 时间信息
     * @return 告警设施信息
     */
    @Override
    public Result queryScreenDeviceIdGroup(AlarmTime alarmTime) {
        alarmTime.setEndTime(alarmTime.getEndTime() * 1000);
        alarmTime.setStartTime(alarmTime.getStartTime() * 1000);
        User user = alarmCurrentService.getUser();
        Criteria criteria = new Criteria();
        if (!user.getId().equals(AppConstant.ONE)) {
            List<String> deviceTypes = alarmCurrentService.getDeviceTypes(user);
            List<String> areaIds = alarmCurrentService.getUserAreaIds(user);
            if (ListUtil.isEmpty(areaIds) || ListUtil.isEmpty(areaIds)) {
                criteria = Criteria.where(AppConstant.ALARM_SOURCE_TYPE_ID).in((Object) null).and(AppConstant.AREA_ID).in((Object) null)
                        .and(AppConstant.ALARM_BEGIN_TIME).lt(alarmTime.getEndTime())
                        .gt(alarmTime.getStartTime()).and(AlarmCurrent18n.ALARM_TYPE).is(LogFunctionCodeConstant.ALARM_STATUS_ONE);
            } else {
                criteria = Criteria.where(AppConstant.ALARM_SOURCE_TYPE_ID).in(deviceTypes).and(AppConstant.AREA_ID).in(areaIds)
                        .and(AppConstant.ALARM_BEGIN_TIME).lt(alarmTime.getEndTime())
                        .gt(alarmTime.getStartTime()).and(AlarmCurrent18n.ALARM_TYPE).is(LogFunctionCodeConstant.ALARM_STATUS_ONE);
            }
        } else {
            criteria = Criteria.where(AppConstant.ALARM_BEGIN_TIME).lt(alarmTime.getEndTime()).gt(alarmTime.getStartTime())
                    .and(AlarmCurrent18n.ALARM_TYPE).is(LogFunctionCodeConstant.ALARM_STATUS_ONE);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(AppConstant.ALARM_SOURCE).count().as(AppConstant.COUNT),
                Aggregation.project(AppConstant.COUNT).and("alarmSource").previousOperation());
        AggregationResults<AlarmDeviceParameter> res =
                mongoTemplate.aggregate(aggregation, AppConstant.ALARM_CURRENT, AlarmDeviceParameter.class);
        AggregationResults<AlarmDeviceParameter> results =
                mongoTemplate.aggregate(aggregation, AppConstant.ALARM_HISTORY, AlarmDeviceParameter.class);
        // 当前告警的分组数据
        List<AlarmDeviceParameter> list = res.getMappedResults();
        // 历史告警的分组数据
        List<AlarmDeviceParameter> historyList = results.getMappedResults();
        list = list.stream().collect(Collectors.toList());
        List<AlarmDeviceParameter> deviceParameterList = forDeviceId(list, historyList);
        List<AlarmDeviceParameter> collect = deviceParameterList.stream().limit(10)
                .sorted(Comparator.comparing(AlarmDeviceParameter::getCount).reversed()).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    /**
     * 当前告警和历史告警组装信息
     *
     * @param list        当前告警信息
     * @param historyList 历史告警信息
     * @return 告警信息
     */
    protected List<AlarmDeviceParameter> forDeviceId(List<AlarmDeviceParameter> list, List<AlarmDeviceParameter> historyList) {
        for (int i = 0; i < historyList.size(); i++) {
            AlarmDeviceParameter alarmDeviceParameter = historyList.get(i);
            boolean anyMatch = list.stream().anyMatch(deviceParameter ->
                    deviceParameter.getAlarmSource().equals(alarmDeviceParameter.getAlarmSource()));
            if (!anyMatch) {
                list.add(alarmDeviceParameter);
            } else {
                list.forEach(deviceParameter -> {
                    // 判断历史与当前相同的设施，实现相加
                    if (deviceParameter.getAlarmSource().equals(alarmDeviceParameter.getAlarmSource())) {
                        deviceParameter.setCount(alarmDeviceParameter.getCount() + deviceParameter.getCount());
                    }
                });
            }
        }
        return list;
    }

    /**
     * 大屏设施根据tpn10
     *
     * @return 告警设施信息
     */
    @Override
    public Result queryScreenDeviceIdsGroup() {
        Criteria criteria = new Criteria();
        criteria = selectPermissionIds(criteria);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(AppConstant.ALARM_SOURCE).count().as(AppConstant.COUNT),
                Aggregation.project(AppConstant.COUNT).and("alarmSource").previousOperation());
        AggregationResults<AlarmDeviceParameter> res =
                mongoTemplate.aggregate(aggregation, AppConstant.ALARM_CURRENT, AlarmDeviceParameter.class);
        AggregationResults<AlarmDeviceParameter> results =
                mongoTemplate.aggregate(aggregation, AppConstant.ALARM_HISTORY, AlarmDeviceParameter.class);
        // 当前告警的分组数据
        List<AlarmDeviceParameter> list = res.getMappedResults();
        // 历史告警的分组数据
        List<AlarmDeviceParameter> historyList = results.getMappedResults();
        list = list.stream().collect(Collectors.toList());
        List<AlarmDeviceParameter> deviceParameterList = forDeviceId(list, historyList);
        List<AlarmDeviceParameter> collect = deviceParameterList.stream().limit(10)
                .sorted(Comparator.comparing(AlarmDeviceParameter::getCount).reversed()).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    /**
     * app告警名称统计
     *
     * @return 告警统计信息
     */
    @Override
    public Result queryAppAlarmNameGroup() {
        Criteria criteria = new Criteria();
        criteria = selectPermissions(criteria);
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(AppConstant.ALARM_NAME).count().as("groupNum"),
                Aggregation.project("groupNum").and("groupLevel").previousOperation());
        //查询当前表的数据
        AggregationResults<AlarmStatisticsGroupInfo> results =
                this.mongoTemplate.aggregate(agg, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
        List<AlarmStatisticsGroupInfo> list = results.getMappedResults();
        AlarmNameStatisticsDto alarmNameStatisticsDto = editAlarmNameStatistics(list);
        return ResultUtils.success(alarmNameStatisticsDto);
    }

    /**
     * 组装时间数据
     *
     * @param alarmHomeParameter 条件信息
     */
    private void selectHome(AlarmHomeParameter alarmHomeParameter) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        switch (alarmHomeParameter.getTime()) {
            // 一周
            case 1:
                calendar.add(Calendar.WEEK_OF_MONTH, -1);
                alarmHomeParameter.setBeginTime(calendar.getTimeInMillis());
                break;
            // 一月
            case 2:
                calendar.add(Calendar.MONTH, -1);
                alarmHomeParameter.setBeginTime(calendar.getTimeInMillis());
                break;
            // 三月
            case 3:
                calendar.add(Calendar.MONTH, -3);
                alarmHomeParameter.setBeginTime(calendar.getTimeInMillis());
                break;
            default:
        }
    }

}
