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
 * ???????????????
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
     * mongodb?????????
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * ????????????dao
     */
    @Autowired
    private AlarmStatisticsTempDao alarmStatisticsTempDao;

    @Autowired
    private AlarmCurrentServiceImpl alarmCurrentService;

    /**
     * ??????????????????
     *
     * @param queryCondition ????????????
     * @return ????????????????????????
     */
    @Override
    public Result queryAlarmCountByLevel(QueryCondition<AlarmStatisticsParameter> queryCondition) {
        AlarmStatisticsParameter alarmStatisticsParameter = queryCondition.getBizCondition();
        //????????????????????????(??????,?????????????????????)
        Criteria criteria = Criteria.where(AppConstant.AREA_NAME).in(alarmStatisticsParameter.getAreaList())
                .and(AppConstant.ALARM_SOURCE_TYPE_ID).in(alarmStatisticsParameter.getDeviceIds())
                .and(AppConstant.ALARM_BEGIN_TIME).lt(alarmStatisticsParameter.getEndTime()).gt(alarmStatisticsParameter.getBeginTime());
        //????????????????????????????????????
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(AppConstant.AREA_NAME).count().as("groupNum"),
                Aggregation.project().and("_id").as("groupLevel").and("groupNum").as("groupNum").andExclude("_id"));
        //?????????????????????
        AggregationResults<AlarmStatisticsGroupInfo> results =
                this.mongoTemplate.aggregate(agg, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
        //?????????????????????
        AggregationResults<AlarmStatisticsGroupInfo> resultsHistory =
                this.mongoTemplate.aggregate(agg, AppConstant.ALARM_HISTORY, AlarmStatisticsGroupInfo.class);
        List<AlarmStatisticsGroupInfo> listHistory = resultsHistory.getMappedResults();
        List<AlarmStatisticsGroupInfo> list = results.getMappedResults();
        //?????????????????????list???????????????????????????????????????????????????????????????list???
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
        //???????????????????????????
        int totalNum = list.stream().mapToInt(u -> Integer.valueOf(u.getGroupNum())).sum();
        //??????????????????????????????????????????,?????????????????????????????????map???
        Map<String, AlarmAreaRateStatisticsDto> map = new HashMap<>();
        list.forEach(u -> {
            AlarmAreaRateStatisticsDto dto = new AlarmAreaRateStatisticsDto();
            dto.setAreaAlarmCount(u.getGroupNum());
            dto.setAreaAlarmRate(String.format("%2.2f%%", (float) u.getGroupNum() / totalNum * 100));
            map.put(u.getGroupLevel(), dto);
        });

        //?????????????????????????????????0
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
     * ??????????????????
     *
     * @param alarmStatisticsParameter ????????????
     * @return ????????????????????????
     */
    @Override
    public Result queryAlarmByLevelAndArea(AlarmStatisticsParameter alarmStatisticsParameter) {
        //???????????????????????? (????????????)
        alarmStatisticsParameter.setAlarmStatisticsName(AppConstant.ALARM_FIXED_LEVEL);
        // ??????mongodb?????????????????????list(????????????+????????????)
        List<AlarmStatisticsGroupInfo> list = this.queryMongodbData(alarmStatisticsParameter);
        //????????????????????????????????????(???list?????????map ????????????key???)
        Map<String, List<AlarmStatisticsGroupInfo>> areaMap =
                list.stream().collect(Collectors.groupingBy(AlarmStatisticsGroupInfo::getGroupArea));
        // ??????????????????map??????????????????(?????????values???????????????)
        // (???Map<String, List<AlarmStatisticsGroupInfo>> ??????  Map<String, AlarmStatisticsByLevelDto>)
        Map<String, AlarmStatisticsByLevelDto> dtoMap = areaMap.entrySet().stream().collect(Collectors.toMap(
                stringListEntry -> stringListEntry.getKey(),
                stringListEntry -> getAlarmStatisticsByLevelDto(stringListEntry.getValue())
        ));
        //?????????????????????????????????0
        if (dtoMap.size() != alarmStatisticsParameter.getAreaList().size()) {
            List<String> areaList = alarmStatisticsParameter.getAreaList();
            for (int i = 0; i < areaList.size(); i++) {
                String areaKey = areaList.get(i);
                if (!dtoMap.keySet().stream().anyMatch(u -> u.equals(areaKey))) {
                    dtoMap.put(areaKey, new AlarmStatisticsByLevelDto(0, 0, 0, 0));
                }
            }
        }
        //??????map ??????,????????????,??????????????????
        List<AlarmStatisticsGroupInfo> infoList = addTotalNum(list);
        dtoMap.put("total", getAlarmStatisticsByLevelDto(infoList));
        System.out.println("????????????" + new SimpleDateFormat("hh:mm:ss").format(new Date()));
        return ResultUtils.success(dtoMap);
    }


    /**
     * ????????????????????????
     *
     * @param alarmStatisticsParameter ????????????
     * @return ????????????????????????
     */
    @Override
    public Result alarmHandleStatistics(AlarmStatisticsParameter alarmStatisticsParameter) {
        //???????????????????????? (??????????????????)(1,?????????,2,????????????,3?????????)
        alarmStatisticsParameter.setAlarmStatisticsName("alarm_clean_status");
        //????????????????????????,(??????+??????)
        List<AlarmStatisticsGroupInfo> list = this.queryMongodbData(alarmStatisticsParameter);
        //???????????????????????????????????????????????????key???
        Map<String, List<AlarmStatisticsGroupInfo>> areaMap = list.stream()
                .collect(Collectors.groupingBy(AlarmStatisticsGroupInfo::getGroupArea));
        // ??????????????????map??????????????????(?????????values???????????????)
        // (??? Map<String, List<AlarmStatisticsGroupInfo>> ??????  Map<String, AlarmHandleStatisticsDto>)
        Map<String, AlarmHandleStatisticsDto> dtoMap = areaMap.entrySet().stream().collect(Collectors.toMap(
                stringListEntry -> stringListEntry.getKey(),
                stringListEntry -> getAlarmHandleStatistics(stringListEntry.getValue())
        ));
        //?????????????????????????????????0
        if (dtoMap.size() != alarmStatisticsParameter.getAreaList().size()) {
            List<String> areaList = alarmStatisticsParameter.getAreaList();
            for (int i = 0; i < areaList.size(); i++) {
                String areaKey = areaList.get(i);
                if (!dtoMap.keySet().stream().anyMatch(u -> u.equals(areaKey))) {
                    dtoMap.put(areaKey, new AlarmHandleStatisticsDto(0, 0, 0));
                }
            }
        }
        //??????map ??????,????????????,??????????????????
        List<AlarmStatisticsGroupInfo> infoList = addTotalNum(list);
        dtoMap.put("total", getAlarmHandleStatistics(infoList));
        return ResultUtils.success(dtoMap);
    }

    /**
     * ??????????????????
     *
     * @param queryCondition ????????????
     * @return ????????????????????????
     */
    @Override
    public Result alarmNameStatistics(QueryCondition<AlarmStatisticsParameter> queryCondition) {
        AlarmStatisticsParameter alarmStatisticsParameter = queryCondition.getBizCondition();
        //???????????????????????? (????????????)
        alarmStatisticsParameter.setAlarmStatisticsName(AppConstant.ALARM_NAME);
        //????????????????????????,(??????+??????)
        List<AlarmStatisticsGroupInfo> list = this.queryMongodbData(alarmStatisticsParameter);
        ////???????????????????????????????????????????????????key???
        Map<String, List<AlarmStatisticsGroupInfo>> areaMap = list.stream()
                .collect(Collectors.groupingBy(AlarmStatisticsGroupInfo::getGroupArea));
        ////??????????????????map??????????????????(?????????values???????????????)
        Map<String, AlarmNameStatisticsDto> dtoMap = areaMap.entrySet().stream().collect(Collectors.toMap(
                stringListEntry -> stringListEntry.getKey(),
                stringListEntry -> editAlarmNameStatistics(stringListEntry.getValue())
        ));
        ////?????????????????????????????????0
        if (dtoMap.size() != alarmStatisticsParameter.getAreaList().size()) {
            List<String> areaList = alarmStatisticsParameter.getAreaList();
            for (int i = 0; i < areaList.size(); i++) {
                String areaKey = areaList.get(i);
                if (!dtoMap.keySet().stream().anyMatch(u -> u.equals(areaKey))) {
                    dtoMap.put(areaKey, new AlarmNameStatisticsDto(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                }
            }
        }
        //??????map ??????,????????????,??????????????????
        List<AlarmStatisticsGroupInfo> infoList = addTotalNum(list);
        dtoMap.put("total", editAlarmNameStatistics(infoList));
        return ResultUtils.success(dtoMap);
    }


    /**
     * ??????????????????
     *
     * @param queryCondition ????????????
     * @return ????????????
     */
    @Override
    public Result alarmIncrementalStatistics(QueryCondition<AlarmStatisticsParameter> queryCondition, String timeType, boolean flag) {
        AlarmStatisticsParameter alarmStatisticsParameter = queryCondition.getBizCondition();
        Criteria criteria = new Criteria();
        //??????????????????????????? ??????????????????????????????????????????
        //?????????admin?????????????????????
        if (flag) {
            criteria = getIncremental(criteria, alarmStatisticsParameter, timeType);
        } else {
            criteria = Criteria.where("timeType").is(timeType).and("groupArea").in(alarmStatisticsParameter.getAreaList())
                    .and("groupType").in(alarmStatisticsParameter.getDeviceIds())
                    .and("groupTime").lte(alarmStatisticsParameter.getEndTime()).gte(alarmStatisticsParameter.getBeginTime());
        }
        //??????????????????????????????????????????
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("groupTime").sum("groupNum").as("groupNum"),
                Aggregation.project().and("_id").as("groupLevel").and("groupNum").as("groupNum").andExclude("_id"));
        //??????mongodb??????
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
        //???????????????????????????
        List<AlarmStatisticsGroupInfo> listStatistics = new ArrayList<>();
        //???list??????????????????
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
        //??????
        listStatistics.sort((a, b) -> a.getGroupLevel().compareTo(b.getGroupLevel()));
        return ResultUtils.success(listStatistics);
    }

    /**
     * ????????????????????????
     *
     * @param criteria                 mongodb????????????
     * @param alarmStatisticsParameter ??????????????????
     * @param timeType                 ??????????????????
     * @return ??????????????????????????????
     */
    private Criteria getIncremental(Criteria criteria, AlarmStatisticsParameter alarmStatisticsParameter, String timeType) {
        User user = alarmCurrentService.getUser();
        // ?????????????????????admin
        if (!user.getId().equals(AppConstant.ONE)) {
            List<String> deviceTypes = alarmCurrentService.getDeviceTypes(user);
            List<String> areaIds = alarmCurrentService.getUserAreaIds(user);
            if (ListUtil.isEmpty(areaIds) || ListUtil.isEmpty(deviceTypes)) {
                criteria = Criteria.where("groupTime")
                        .lte(alarmStatisticsParameter.getEndTime()).gte(alarmStatisticsParameter.getBeginTime())
                        .and("timeType").is(timeType)
                        .and(AppConstant.ALARM_SOURCE_TYPE_ID).in((Object) null).and(AppConstant.AREA_ID).in((Object) null);
            } else {
                criteria = Criteria.where("groupTime")
                        .lte(alarmStatisticsParameter.getEndTime()).gte(alarmStatisticsParameter.getBeginTime())
                        .and("timeType").is(timeType)
                        .and(AppConstant.ALARM_SOURCE_TYPE_ID).in(deviceTypes).and(AppConstant.AREA_ID).in(areaIds);
            }
        } else {
            criteria = Criteria.where("groupTime")
                    .lte(alarmStatisticsParameter.getEndTime()).gte(alarmStatisticsParameter.getBeginTime())
                    .and("timeType").is(timeType);
        }
        return criteria;
    }

    /**
     * ????????????
     *
     * @param list ????????????
     * @return ????????????
     */
    private List<AlarmStatisticsGroupInfo> addTotalNum(List<AlarmStatisticsGroupInfo> list) {
        //???mongodb???????????????????????????
        Map<String, List<AlarmStatisticsGroupInfo>> totalNum = list.stream()
                .collect(Collectors.groupingBy(AlarmStatisticsGroupInfo::getGroupLevel));
        //???????????????map ?????????????????????.??????list
        return totalNum.values().stream().map(u -> {
            AlarmStatisticsGroupInfo info = new AlarmStatisticsGroupInfo();
            info.setGroupLevel(u.get(0).getGroupLevel());
            info.setGroupNum(u.stream().mapToInt(g -> g.getGroupNum()).sum());
            return info;
        }).collect(Collectors.toList());
    }

    /**
     * ??????mongodb??????
     * ???????????????????????????
     *
     * @param alarmStatisticsParameter ??????
     * @return ????????????
     */
    private List<AlarmStatisticsGroupInfo> queryMongodbData(AlarmStatisticsParameter alarmStatisticsParameter) {
        //????????????ID??????
        Criteria criteria = new Criteria();
        if (alarmStatisticsParameter.getAlarmStatisticsName().equals(AlarmCurrent18n.DEVICE_ID)) {
            criteria = Criteria.where(AppConstant.ALARM_SOURCE).in(alarmStatisticsParameter.getDeviceIds())
                    .and(AppConstant.ALARM_BEGIN_TIME).lt(alarmStatisticsParameter.getEndTime())
                    .gt(alarmStatisticsParameter.getBeginTime());
        } else {
            //?????????????????????(????????????:????????????????????????????????????????????????)
            criteria = Criteria.where(AppConstant.AREA_NAME).in(alarmStatisticsParameter.getAreaList())
                    .and(AppConstant.ALARM_SOURCE_TYPE_ID).in(alarmStatisticsParameter.getDeviceIds())
                    .and(AppConstant.ALARM_BEGIN_TIME).lt(alarmStatisticsParameter.getEndTime())
                    .gt(alarmStatisticsParameter.getBeginTime());
            //??????????????????
        }
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(alarmStatisticsParameter.getAlarmStatisticsName(), AppConstant.AREA_NAME).count().as("groupNum"),
                Aggregation.project().and(alarmStatisticsParameter.getAlarmStatisticsName()).as("groupLevel")
                        .and(AppConstant.AREA_NAME).as("groupArea").and("groupNum").as("groupNum").andExclude("_id"));
        //????????????????????????
        AggregationResults<AlarmStatisticsGroupInfo> results =
                this.mongoTemplate.aggregate(agg, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
        //????????????????????????
        AggregationResults<AlarmStatisticsGroupInfo> resultsHistory =
                this.mongoTemplate.aggregate(agg, AppConstant.ALARM_HISTORY, AlarmStatisticsGroupInfo.class);
        List<AlarmStatisticsGroupInfo> listHistory = resultsHistory.getMappedResults();
        List<AlarmStatisticsGroupInfo> list = results.getMappedResults();
        //??????????????????list??????.(??????????????????,????????????????????????.??????????????????list)
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
     * ????????????????????????????????????????????????
     *
     * @param list
     * @return
     */
    protected AlarmStatisticsByLevelDto getAlarmStatisticsByLevelDto(List<AlarmStatisticsGroupInfo> list) {
        // ?????????????????????(????????????????????????null)
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
     * ??????????????????list?????????????????????????????????
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
     * ?????????????????????list????????????????????????????????????????????????
     *
     * @param list
     * @return
     */
    protected AlarmNameStatisticsDto editAlarmNameStatistics(List<AlarmStatisticsGroupInfo> list) {
        AlarmNameStatisticsDto alarmStatistics = new AlarmNameStatisticsDto(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        list.forEach(u -> {
            switch (u.getGroupLevel()) {
                case "??????":
                    alarmStatistics.setPryDoor(u.getGroupNum());
                    break;
                case "??????":
                    alarmStatistics.setPryLock(u.getGroupNum());
                    break;
                case "?????????":
                    alarmStatistics.setNotClosed(u.getGroupNum());
                    break;
                case "??????":
                    alarmStatistics.setHighTemperature(u.getGroupNum());
                    break;
                case "??????":
                    alarmStatistics.setLowTemperature(u.getGroupNum());
                    break;
                case "????????????":
                    alarmStatistics.setCommunicationInterrupt(u.getGroupNum());
                    break;
                case "??????":
                    alarmStatistics.setLeach(u.getGroupNum());
                    break;
                case "?????????":
                    alarmStatistics.setUnLock(u.getGroupNum());
                    break;
                case "??????":
                    alarmStatistics.setLean(u.getGroupNum());
                    break;
                case "??????":
                    alarmStatistics.setShake(u.getGroupNum());
                    break;
                case "??????":
                    alarmStatistics.setElectricity(u.getGroupNum());
                    break;
                case "????????????":
                    alarmStatistics.setViolenceClose(u.getGroupNum());
                    break;
                case "????????????":
                    alarmStatistics.setOrderOutOfTime(u.getGroupNum());
                    break;
                case "??????????????????":
                    alarmStatistics.setEmergencyLock(u.getGroupNum());
                    break;
                case "??????":
                    alarmStatistics.setHumidity(u.getGroupNum());
                    break;
                case "????????????????????????":
                    alarmStatistics.setIllegalOpeningInnerCover(u.getGroupNum());
                default:
            }
        });
        return alarmStatistics;
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param alarmSourceHomeParameter ????????????
     * @return ??????????????????
     */
    @Override
    public void queryStatisticsData(AlarmSourceHomeParameter alarmSourceHomeParameter) {
        int time = 8 * 60 * 60 * 1000;
        String type = alarmSourceHomeParameter.getType();
        if (type.equals(AppConstant.DAY)) {
            alarmSourceHomeParameter.setBeginTime(DateUtil.getAdvanceNumberDay(1) - time);
            alarmSourceHomeParameter.setEndTime(DateUtil.getAdvanceNumberEndDay() - time);
            log.info(alarmSourceHomeParameter.getBeginTime().toString());
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
            //?????????????????????????????????1?????????
            alarmSourceHomeParameter.setBeginTime(DateUtil.getAdvanceNumberWeek(1) - time);
            //??????????????????????????????????????????
            alarmSourceHomeParameter.setEndTime(DateUtil.getAdvanceNumberEndWeek() - time);
            List<AlarmStatisticsGroupInfo> alarmStatistics = getAlarmStatistics(alarmSourceHomeParameter);
            setMongoTemplate(alarmStatistics, alarmSourceHomeParameter);
        } else if (type.equals(AppConstant.MONTH)) {
            alarmSourceHomeParameter.setBeginTime(DateUtil.getAdvanceNumberMonth(1) - time);
            alarmSourceHomeParameter.setEndTime(DateUtil.getAdvanceNumberEndMonth() - time);
            List<AlarmStatisticsGroupInfo> alarmStatistics = getAlarmStatistics(alarmSourceHomeParameter);
            setMongoTemplate(alarmStatistics, alarmSourceHomeParameter);
        }
    }

    /**
     * ????????????
     *
     * @param alarmSourceHomeParameter ????????????
     * @return ????????????
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
     * ??????????????????
     *
     * @param list                     ????????????
     * @param alarmSourceHomeParameter ????????????
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
     * ??????????????????????????????
     *
     * @param timeType ??????ids
     * @return ????????????
     */
    @Override
    public void deleteAlarmIncrementalStatistics(Long deleteTime, String timeType) {
        Query query = new Query(Criteria.where("timeType").is(timeType).and("groupTime").lt(deleteTime));
        mongoTemplate.remove(query, AlarmIncrementalStatistics.class);
    }


    /**
     * ??????????????????????????????
     *
     * @param ids ??????ids
     * @return ????????????
     */
    @Override
    public Integer deleteAlarmStatisticsTemp(String[] ids) {
        return alarmStatisticsTempDao.batchDeleteAlarmStatisticsTemp(ids);
    }

    /**
     * ????????????????????????????????????
     *
     * @param alarmStatisticsTemp ??????????????????
     * @return ????????????
     */
    @Override
    public Integer updateAlarmStatisticsTemp(AlarmStatisticsTemp alarmStatisticsTemp) {
        return alarmStatisticsTempDao.batchUpdateAlarmStatisticsTemp(alarmStatisticsTemp);
    }

    /**
     * ????????????????????????????????????
     *
     * @param list ??????????????????
     * @return ????????????
     */
    @Override
    public Integer addAlarmStatisticsTemp(List<AlarmStatisticsTemp> list) {
        return alarmStatisticsTempDao.addAlarmStatisticsTemp(list);
    }

    /**
     * ??????ID????????????????????????
     *
     * @param id ??????id
     * @return ??????????????????
     */
    @Override
    public AlarmStatisticsTemp queryAlarmStatisticsTempById(String id) {
        return alarmStatisticsTempDao.queryAlarmStatisticsTempById(id);
    }

    /**
     * ??????????????????tcp10
     *
     * @param queryCondition ????????????
     * @return ??????tcp??????
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
        // ???????????????????????????????????????
        AggregationResults<AlarmDeviceParameter> res =
                mongoTemplate.aggregate(aggregation, AppConstant.ALARM_CURRENT, AlarmDeviceParameter.class);
        AggregationResults<AlarmDeviceParameter> results =
                mongoTemplate.aggregate(aggregation, AppConstant.ALARM_HISTORY, AlarmDeviceParameter.class);
        // ???????????????????????????
        List<AlarmDeviceParameter> list = res.getMappedResults();
        // ???????????????????????????
        List<AlarmDeviceParameter> historyList = results.getMappedResults();
        list = list.stream().collect(Collectors.toList());
        List<AlarmDeviceParameter> deviceParameterList = forDeviceId(list, historyList);
        // ??????
        List<AlarmDeviceParameter> collect = deviceParameterList.stream().limit(alarmStatisticsParameter.getTopCount())
                .sorted(Comparator.comparing(AlarmDeviceParameter::getCount).reversed()).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    /**
     * ????????????????????????????????????
     *
     * @param alarmHomeParameter ????????????
     * @return ??????????????????
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
     * ????????????????????????
     *
     * @param criteria ????????????
     * @return ??????????????????
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
     * ??????????????????????????????
     *
     * @param alarmSourceHomeParameter ????????????
     * @return ??????????????????
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
     * ????????????????????????????????????
     *
     * @param alarmSourceHomeParameter ????????????
     * @return ??????????????????
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
     * ????????????????????????????????????
     *
     * @param alarmSourceHomeParameter ????????????
     * @return ??????????????????
     */
    @Override
    public Result queryAlarmCurrentSourceName(AlarmSourceHomeParameter alarmSourceHomeParameter) {
        Criteria criteria = Criteria.where(AppConstant.ALARM_SOURCE).is(alarmSourceHomeParameter.getDeviceId())
                .and(AppConstant.ALARM_BEGIN_TIME).lt(alarmSourceHomeParameter.getEndTime()).gt(alarmSourceHomeParameter.getBeginTime());
        AlarmNameStatisticsDto alarmNameStatisticsDto = alarmNameHomePage(criteria);
        return ResultUtils.success(alarmNameStatisticsDto);
    }

    /**
     * ????????????????????????????????????
     *
     * @param alarmSourceHomeParameter ????????????
     * @return ??????????????????
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
     * ??????????????????????????????
     *
     * @param alarmSourceHomeParameter ????????????
     */
    @Override
    public void querySourceIncremental(AlarmSourceHomeParameter alarmSourceHomeParameter) {
        int time = 8 * 60 * 60 * 1000;
        // ?????????
        if (alarmSourceHomeParameter.getSource() == LogFunctionCodeConstant.ALARM_STATUS_ONE) {
            // ??????????????????2???
            alarmSourceHomeParameter.setBeginTime(DateUtil.getAdvanceNumberDay(1) - time);
            // ????????????????????????
            alarmSourceHomeParameter.setEndTime(DateUtil.getAdvanceNumberEndDay() - time);
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
            // ?????????
        } else if (alarmSourceHomeParameter.getSource() == LogFunctionCodeConstant.ALARM_STATUS_TWO) {
            //??????????????????15???
            alarmSourceHomeParameter.setBeginTime(DateUtil.getAdvanceNumberWeek(1) - time);
            //????????????????????????
            alarmSourceHomeParameter.setEndTime(DateUtil.getAdvanceNumberEndWeek() - time);
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
            alarmSourceIncremental.setGroupTime(String.valueOf(alarmSourceHomeParameter.getBeginTime()));
            alarmSourceIncremental.setCount(u.getCount());
            alarmSourceIncremental.setType(alarmSourceHomeParameter.getType());
            alarmSourceIncrementalList.add(alarmSourceIncremental);
        });
        mongoTemplate.insertAll(alarmSourceIncrementalList);
    }

    /**
     * ????????????????????????
     *
     * @param alarmSourceHomeParameter ????????????
     * @return ????????????????????????
     */
    @Override
    public Result queryAlarmSourceIncremental(AlarmSourceHomeParameter alarmSourceHomeParameter) {
        //??????mongodb??????
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
        //???????????????????????????
        List<AlarmSourceIncremental> listStatistics = new ArrayList<>();
        //???list??????????????????
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
     * ??????????????????????????????
     *
     * @return ????????????????????????
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
     * ????????????
     *
     * @param criteria ????????????
     * @return ????????????
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
     * ??????????????????????????????
     *
     * @param criteria ????????????
     * @return ????????????
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
     * ?????????????????????????????????
     *
     * @param criteria ????????????
     * @return ????????????
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
     * ???????????????????????????topn10
     *
     * @param alarmTime ????????????
     * @return ??????????????????
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
        // ???????????????????????????
        List<AlarmDeviceParameter> list = res.getMappedResults();
        // ???????????????????????????
        List<AlarmDeviceParameter> historyList = results.getMappedResults();
        list = list.stream().collect(Collectors.toList());
        List<AlarmDeviceParameter> deviceParameterList = forDeviceId(list, historyList);
        List<AlarmDeviceParameter> collect = deviceParameterList.stream().limit(10)
                .sorted(Comparator.comparing(AlarmDeviceParameter::getCount).reversed()).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param list        ??????????????????
     * @param historyList ??????????????????
     * @return ????????????
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
                    // ???????????????????????????????????????????????????
                    if (deviceParameter.getAlarmSource().equals(alarmDeviceParameter.getAlarmSource())) {
                        deviceParameter.setCount(alarmDeviceParameter.getCount() + deviceParameter.getCount());
                    }
                });
            }
        }
        return list;
    }

    /**
     * ??????????????????tpn10
     *
     * @return ??????????????????
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
        // ???????????????????????????
        List<AlarmDeviceParameter> list = res.getMappedResults();
        // ???????????????????????????
        List<AlarmDeviceParameter> historyList = results.getMappedResults();
        list = list.stream().collect(Collectors.toList());
        List<AlarmDeviceParameter> deviceParameterList = forDeviceId(list, historyList);
        List<AlarmDeviceParameter> collect = deviceParameterList.stream().limit(10)
                .sorted(Comparator.comparing(AlarmDeviceParameter::getCount).reversed()).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    /**
     * app??????????????????
     *
     * @return ??????????????????
     */
    @Override
    public Result queryAppAlarmNameGroup() {
        Criteria criteria = new Criteria();
        criteria = selectPermissions(criteria);
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(AppConstant.ALARM_NAME).count().as("groupNum"),
                Aggregation.project("groupNum").and("groupLevel").previousOperation());
        //????????????????????????
        AggregationResults<AlarmStatisticsGroupInfo> results =
                this.mongoTemplate.aggregate(agg, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
        List<AlarmStatisticsGroupInfo> list = results.getMappedResults();
        AlarmNameStatisticsDto alarmNameStatisticsDto = editAlarmNameStatistics(list);
        return ResultUtils.success(alarmNameStatisticsDto);
    }

    /**
     * ??????????????????
     *
     * @param alarmHomeParameter ????????????
     */
    private void selectHome(AlarmHomeParameter alarmHomeParameter) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        switch (alarmHomeParameter.getTime()) {
            // ??????
            case 1:
                calendar.add(Calendar.WEEK_OF_MONTH, -1);
                alarmHomeParameter.setBeginTime(calendar.getTimeInMillis());
                break;
            // ??????
            case 2:
                calendar.add(Calendar.MONTH, -1);
                alarmHomeParameter.setBeginTime(calendar.getTimeInMillis());
                break;
            // ??????
            case 3:
                calendar.add(Calendar.MONTH, -3);
                alarmHomeParameter.setBeginTime(calendar.getTimeInMillis());
                break;
            default:
        }
    }

}
