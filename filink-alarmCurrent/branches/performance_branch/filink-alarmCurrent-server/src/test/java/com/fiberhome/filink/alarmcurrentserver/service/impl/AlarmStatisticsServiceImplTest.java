package com.fiberhome.filink.alarmcurrentserver.service.impl;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmDeviceParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmHomeParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmSourceHomeParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmSourceIncremental;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsGroupInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsTemp;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTime;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmStatisticsTempDao;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;

@RunWith(JMockit.class)
public class AlarmStatisticsServiceImplTest {

    /**
     * mongodb实现类
     */
    @Injectable
    private MongoTemplate mongoTemplate;

    /**
     * 告警统计dao
     */
    @Injectable
    private AlarmStatisticsTempDao alarmStatisticsTempDao;

    @Injectable
    private AlarmCurrentServiceImpl alarmCurrentService;

    @Tested
    private AlarmStatisticsServiceImpl alarmStatisticsService;

    /**
     * 区域告警占比
     *
     * @throws Exception
     */
    @Test
    public void queryAlarmCountByLevel() throws Exception {
        QueryCondition<AlarmStatisticsParameter> alarmQueryTemplateDtoQueryCondition = new QueryCondition<>();
        AlarmStatisticsParameter alarmQueryTemplateDto = new AlarmStatisticsParameter();
        List<String> list = new ArrayList<>();
        list.add("11");
        alarmQueryTemplateDto.setAreaList(list);
        alarmQueryTemplateDto.setBeginTime(0L);
        alarmQueryTemplateDto.setDeviceType("001");
        alarmQueryTemplateDtoQueryCondition.setBizCondition(alarmQueryTemplateDto);
        alarmStatisticsService.queryAlarmCountByLevel(alarmQueryTemplateDtoQueryCondition);
    }

    /**
     * 告警类型统计
     *
     * @throws Exception
     */
    @Test
    public void queryAlarmByLevelAndArea() throws Exception {
        AlarmStatisticsParameter alarmQueryTemplateDto = new AlarmStatisticsParameter();
        List<String> list = new ArrayList<>();
        list.add("11");
        alarmQueryTemplateDto.setAreaList(list);
        alarmQueryTemplateDto.setBeginTime(1L);
        ;
        alarmQueryTemplateDto.setDeviceType("001");
        alarmQueryTemplateDto.setDeviceType("1");
        alarmQueryTemplateDto.setEndTime(3L);
        alarmQueryTemplateDto.setAlarmCodes(list);
        alarmQueryTemplateDto.setTopCount(10);
        alarmStatisticsService.queryAlarmByLevelAndArea(alarmQueryTemplateDto);
    }

    /**
     * 根据告警处理统计
     *
     * @throws Exception
     */
    @Test
    public void alarmHandleStatistics() throws Exception {
        QueryCondition<AlarmStatisticsParameter> queryCondition = new QueryCondition<AlarmStatisticsParameter>();
        AlarmStatisticsParameter alarmQueryTemplateDto = new AlarmStatisticsParameter();
        List<String> list = new ArrayList<>();
        list.add("11");
        alarmQueryTemplateDto.setAreaList(list);
        alarmQueryTemplateDto.setBeginTime(1L);
        ;
        alarmQueryTemplateDto.setDeviceType("001");
        alarmQueryTemplateDto.setDeviceType("1");
        alarmQueryTemplateDto.setEndTime(3L);
        alarmQueryTemplateDto.setAlarmCodes(list);
        alarmQueryTemplateDto.setTopCount(10);
        queryCondition.setBizCondition(alarmQueryTemplateDto);
        alarmStatisticsService.alarmHandleStatistics(alarmQueryTemplateDto);
    }

    @Test
    public void alarmNameStatistics() throws Exception {
        QueryCondition<AlarmStatisticsParameter> queryCondition = new QueryCondition<AlarmStatisticsParameter>();
        AlarmStatisticsParameter alarmQueryTemplateDto = new AlarmStatisticsParameter();
        List<String> list = new ArrayList<>();
        list.add("11");
        alarmQueryTemplateDto.setAreaList(list);
        alarmQueryTemplateDto.setBeginTime(1L);
        ;
        alarmQueryTemplateDto.setDeviceType("001");
        alarmQueryTemplateDto.setDeviceType("1");
        alarmQueryTemplateDto.setEndTime(3L);
        alarmQueryTemplateDto.setAlarmCodes(list);
        alarmQueryTemplateDto.setTopCount(10);
        queryCondition.setBizCondition(alarmQueryTemplateDto);
        alarmStatisticsService.alarmNameStatistics(queryCondition);
    }

    /**
     * 告警增量统计
     *
     * @throws Exception
     */
    @Test
    public void alarmIncrementalStatistics() throws Exception {
        QueryCondition<AlarmStatisticsParameter> queryCondition = new QueryCondition<AlarmStatisticsParameter>();
        AlarmStatisticsParameter alarmQueryTemplateDto = new AlarmStatisticsParameter();
        List<String> list = new ArrayList<>();
        list.add("11");
        alarmQueryTemplateDto.setAreaList(list);
        alarmQueryTemplateDto.setBeginTime(1L);
        ;
        alarmQueryTemplateDto.setDeviceType("001");
        alarmQueryTemplateDto.setDeviceType("1");
        alarmQueryTemplateDto.setEndTime(3L);
        alarmQueryTemplateDto.setAlarmCodes(list);
        alarmQueryTemplateDto.setTopCount(10);
        queryCondition.setBizCondition(alarmQueryTemplateDto);
        alarmStatisticsService.alarmIncrementalStatistics(queryCondition, "DAY", false);
    }

    /**
     * 将告警的级别转换为对象
     *
     * @throws Exception
     */
    @Test
    public void getAlarmStatisticsByLevelDto() throws Exception {
        List<AlarmStatisticsGroupInfo> arrayList = new ArrayList<>();
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfo = new AlarmStatisticsGroupInfo();
        alarmStatisticsGroupInfo.setGroupLevel("1");
        AlarmStatisticsGroupInfo statisticsGroupInfo = new AlarmStatisticsGroupInfo();
        statisticsGroupInfo.setGroupLevel("2");
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfoOne = new AlarmStatisticsGroupInfo();
        alarmStatisticsGroupInfoOne.setGroupLevel("3");
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfoT = new AlarmStatisticsGroupInfo();
        alarmStatisticsGroupInfoT.setGroupLevel("4");
        arrayList.add(alarmStatisticsGroupInfo);
        arrayList.add(statisticsGroupInfo);
        arrayList.add(alarmStatisticsGroupInfoOne);
        arrayList.add(alarmStatisticsGroupInfoT);
        alarmStatisticsService.getAlarmStatisticsByLevelDto(arrayList);
    }

    @Test
    public void queryStatisticsData() throws Exception {
        AlarmSourceHomeParameter alarmSourceHomeParameter = new AlarmSourceHomeParameter();
        alarmSourceHomeParameter.setType("DAY");
        alarmStatisticsService.queryStatisticsData(alarmSourceHomeParameter);
        AlarmSourceHomeParameter sourceHomeParameter = new AlarmSourceHomeParameter();
        sourceHomeParameter.setType("WEEK");
        alarmStatisticsService.queryStatisticsData(sourceHomeParameter);
        AlarmSourceHomeParameter homeParameter = new AlarmSourceHomeParameter();
        homeParameter.setType("MONTH");
        alarmStatisticsService.queryStatisticsData(homeParameter);
    }

    @Test
    public void deleteAlarmIncrementalStatistics() throws Exception {
        alarmStatisticsService.deleteAlarmIncrementalStatistics(0L, "DAY");
    }

    @Test
    public void deleteAlarmStatisticsTemp() throws Exception {
        alarmStatisticsService.deleteAlarmStatisticsTemp(new String[]{});
    }

    @Test
    public void addAlarmStatisticsTemp() throws Exception {
        List<AlarmStatisticsTemp> list = new ArrayList<>();
        alarmStatisticsService.addAlarmStatisticsTemp(list);
    }

    @Test
    public void queryAlarmStatisticsTempById() throws Exception {
        alarmStatisticsService.queryAlarmStatisticsTempById("1");
    }

    @Test
    public void updateAlarmStatisticsTemp() throws Exception {
        alarmStatisticsService.updateAlarmStatisticsTemp(new AlarmStatisticsTemp());
    }

    @Test
    public void queryAlarmNameGroup() throws Exception {
        QueryCondition<AlarmStatisticsParameter> queryCondition = new QueryCondition<AlarmStatisticsParameter>();
        AlarmStatisticsParameter alarmQueryTemplateDto = new AlarmStatisticsParameter();
        List<String> list = new ArrayList<>();
        list.add("11");
        alarmQueryTemplateDto.setAreaList(list);
        alarmQueryTemplateDto.setBeginTime(1L);
        ;
        alarmQueryTemplateDto.setDeviceType("001");
        alarmQueryTemplateDto.setDeviceType("1");
        alarmQueryTemplateDto.setEndTime(3L);
        alarmQueryTemplateDto.setAlarmCodes(list);
        alarmQueryTemplateDto.setTopCount(10);
        queryCondition.setBizCondition(alarmQueryTemplateDto);
        new MockUp<AggregationResults>() {
            @Mock
            List<AlarmDeviceParameter> getMappedResults() {
                List<AlarmDeviceParameter> alarmDeviceParameters = new ArrayList<>();
                AlarmDeviceParameter alarmDeviceParameter = new AlarmDeviceParameter();
                alarmDeviceParameter.setAlarmSource("11");
                alarmDeviceParameter.setCount(0L);
                alarmDeviceParameters.add(alarmDeviceParameter);
                return alarmDeviceParameters;
            }
        };

        alarmStatisticsService.queryAlarmNameGroup(queryCondition);
    }

    @Test
    public void queryAlarmNameHomePage() throws Exception {
        AlarmHomeParameter alarmHomeParameter = new AlarmHomeParameter();
        alarmHomeParameter.setTime(1);
        alarmStatisticsService.queryAlarmNameHomePage(alarmHomeParameter);
        AlarmHomeParameter parameter = new AlarmHomeParameter();
        parameter.setTime(2);
        alarmStatisticsService.queryAlarmNameHomePage(parameter);
        AlarmHomeParameter homeParameter = new AlarmHomeParameter();
        homeParameter.setTime(3);
        alarmStatisticsService.queryAlarmNameHomePage(homeParameter);
    }

    @Test
    public void queryAlarmCurrentSourceLevel() throws Exception {
        alarmStatisticsService.queryAlarmCurrentSourceLevel(new AlarmSourceHomeParameter());
    }

    @Test
    public void queryAlarmHistorySourceLevel() throws Exception {
        alarmStatisticsService.queryAlarmHistorySourceLevel(new AlarmSourceHomeParameter());
    }

    @Test
    public void queryAlarmCurrentSourceName() throws Exception {
        alarmStatisticsService.queryAlarmCurrentSourceName(new AlarmSourceHomeParameter());
    }

    @Test
    public void queryAlarmHistorySourceName() throws Exception {
        alarmStatisticsService.queryAlarmHistorySourceName(new AlarmSourceHomeParameter());
    }

    @Test
    public void querySourceIncremental() throws Exception {
        List<AlarmDeviceParameter> alarmDeviceParameters = new ArrayList<>();
        AlarmDeviceParameter alarmDeviceParameter = new AlarmDeviceParameter();
        alarmDeviceParameter.setCount(0L);
        alarmDeviceParameter.setAlarmSource("gag");
        alarmDeviceParameters.add(alarmDeviceParameter);
        new Expectations() {
            {
                AggregationResults<AlarmDeviceParameter> results =
                        mongoTemplate.aggregate((Aggregation) any, AppConstant.ALARM_CURRENT, AlarmDeviceParameter.class);
                result = results;
            }
        };
        new Expectations() {
            {
                AggregationResults<AlarmDeviceParameter> resultsHistory =
                        mongoTemplate.aggregate((Aggregation) any, AppConstant.ALARM_HISTORY, AlarmDeviceParameter.class);
                result = resultsHistory;
            }
        };
        AlarmSourceHomeParameter alarmSourceHomeParameter = new AlarmSourceHomeParameter();
        alarmSourceHomeParameter.setSource(1);
        alarmStatisticsService.querySourceIncremental(alarmSourceHomeParameter);
        AlarmSourceHomeParameter sourceHomeParameter = new AlarmSourceHomeParameter();
        sourceHomeParameter.setSource(2);
        alarmStatisticsService.querySourceIncremental(sourceHomeParameter);
    }

    @Test
    public void queryAlarmSourceIncremental() throws Exception {
        List<AlarmSourceIncremental> list = new ArrayList<>();
        AlarmSourceIncremental alarmSourceIncremental = new AlarmSourceIncremental();
        alarmSourceIncremental.setId("1");
        alarmSourceIncremental.setGroupTime("111111");
        list.add(alarmSourceIncremental);
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmSourceIncremental.class);
                result = list;
            }
        };
        AlarmSourceHomeParameter alarmSourceHomeParameter = new AlarmSourceHomeParameter();
        alarmSourceHomeParameter.setType("DAY");
        alarmSourceHomeParameter.setEndTime(111111111111111111L);
        alarmSourceHomeParameter.setBeginTime(111111111111111111L);
        alarmStatisticsService.queryAlarmSourceIncremental(alarmSourceHomeParameter);
    }

    @Test
    public void queryAlarmCurrentLevelGroup() throws Exception {
        User userName = new User();
        userName.setId("2");
        Role role = new Role();
        List<RoleDeviceType> list = new ArrayList<>();
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setDeviceTypeId("001");
        role.setRoleDevicetypeList(list);
        list.add(roleDeviceType);
        Department department = new Department();
        List<String> strings = new ArrayList<>();
        strings.add("1");
        department.setAreaIdList(strings);
        userName.setDepartment(department);
        userName.setRole(role);
        List<String> stringList = new ArrayList<>();
        stringList.add(userName.getRole().getRoleDevicetypeList().get(0).getDeviceTypeId());
        new Expectations() {
            {
                alarmCurrentService.getUser();
                result = userName;
            }
        };
        alarmStatisticsService.queryAlarmCurrentLevelGroup();
        new Expectations() {
            {
                alarmCurrentService.getDeviceTypes(userName);
                result = stringList;
            }
        };
        new Expectations() {
            {
                alarmCurrentService.getUserAreaIds(userName);
                result = strings;
            }
        };
        alarmStatisticsService.queryAlarmCurrentLevelGroup();
        User user = new User();
        user.setId("1");
        new Expectations() {
            {
                alarmCurrentService.getUser();
                result = user;
            }
        };
        alarmStatisticsService.queryAlarmCurrentLevelGroup();
    }

    @Test
    public void queryScreenDeviceIdGroup() throws Exception {
        AlarmTime alarmTime = new AlarmTime();
        alarmTime.setStartTime(0L);
        alarmTime.setEndTime(1L);
        User userName = new User();
        userName.setId("2");
        Role role = new Role();
        List<RoleDeviceType> list = new ArrayList<>();
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setDeviceTypeId("001");
        role.setRoleDevicetypeList(list);
        list.add(roleDeviceType);
        Department department = new Department();
        List<String> strings = new ArrayList<>();
        strings.add("1");
        department.setAreaIdList(strings);
        userName.setDepartment(department);
        userName.setRole(role);
        List<String> stringList = new ArrayList<>();
        stringList.add(userName.getRole().getRoleDevicetypeList().get(0).getDeviceTypeId());
        new Expectations() {
            {
                alarmCurrentService.getUser();
                result = userName;
            }
        };
        alarmStatisticsService.queryScreenDeviceIdGroup(alarmTime);
        new Expectations() {
            {
                alarmCurrentService.getDeviceTypes(userName);
                result = stringList;
            }
        };
        new Expectations() {
            {
                alarmCurrentService.getUserAreaIds(userName);
                result = strings;
            }
        };
        alarmStatisticsService.queryScreenDeviceIdGroup(alarmTime);
        User user = new User();
        user.setId("1");
        new Expectations() {
            {
                alarmCurrentService.getUser();
                result = user;
            }
        };
        alarmStatisticsService.queryScreenDeviceIdGroup(alarmTime);
    }

    @Test
    public void queryScreenDeviceIdsGroup() throws Exception {
        AlarmTime alarmTime = new AlarmTime();
        alarmTime.setStartTime(0L);
        alarmTime.setEndTime(1L);
        User userName = new User();
        userName.setId("2");
        Role role = new Role();
        List<RoleDeviceType> list = new ArrayList<>();
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setDeviceTypeId("001");
        role.setRoleDevicetypeList(list);
        list.add(roleDeviceType);
        Department department = new Department();
        List<String> strings = new ArrayList<>();
        strings.add("1");
        department.setAreaIdList(strings);
        userName.setDepartment(department);
        userName.setRole(role);
        List<String> stringList = new ArrayList<>();
        stringList.add(userName.getRole().getRoleDevicetypeList().get(0).getDeviceTypeId());
        new Expectations() {
            {
                alarmCurrentService.getUser();
                result = userName;
            }
        };
        alarmStatisticsService.queryScreenDeviceIdsGroup();
        new Expectations() {
            {
                alarmCurrentService.getDeviceTypes(userName);
                result = stringList;
            }
        };
        new Expectations() {
            {
                alarmCurrentService.getUserAreaIds(userName);
                result = strings;
            }
        };
        alarmStatisticsService.queryScreenDeviceIdsGroup();
        User user = new User();
        user.setId("1");
        new Expectations() {
            {
                alarmCurrentService.getUser();
                result = user;
            }
        };
        alarmStatisticsService.queryScreenDeviceIdsGroup();
    }

    @Test
    public void queryAppAlarmNameGroup() throws Exception {
        AlarmTime alarmTime = new AlarmTime();
        alarmTime.setStartTime(0L);
        alarmTime.setEndTime(1L);
        User userName = new User();
        userName.setId("2");
        Role role = new Role();
        List<RoleDeviceType> list = new ArrayList<>();
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setDeviceTypeId("001");
        role.setRoleDevicetypeList(list);
        list.add(roleDeviceType);
        Department department = new Department();
        List<String> strings = new ArrayList<>();
        strings.add("1");
        department.setAreaIdList(strings);
        userName.setDepartment(department);
        userName.setRole(role);
        List<String> stringList = new ArrayList<>();
        stringList.add(userName.getRole().getRoleDevicetypeList().get(0).getDeviceTypeId());
        new Expectations() {
            {
                alarmCurrentService.getUser();
                result = userName;
            }
        };
        alarmStatisticsService.queryAppAlarmNameGroup();
        new Expectations() {
            {
                alarmCurrentService.getDeviceTypes(userName);
                result = stringList;
            }
        };
        new Expectations() {
            {
                alarmCurrentService.getUserAreaIds(userName);
                result = strings;
            }
        };
        alarmStatisticsService.queryAppAlarmNameGroup();
    }

    @Test
    public void forDeviceId(){
        List<AlarmDeviceParameter> list = new ArrayList<>();
        AlarmDeviceParameter alarmDeviceParameter = new AlarmDeviceParameter();
        alarmDeviceParameter.setAlarmSource("q1");
        alarmDeviceParameter.setCount(1L);
        list.add(alarmDeviceParameter);
        List<AlarmDeviceParameter> historyList = new ArrayList<>();
        historyList.add(alarmDeviceParameter);
        alarmStatisticsService.forDeviceId(list, historyList);
    }

    @Test
    public void mongodbInsertAll() {
        List<AlarmDeviceParameter> list = new ArrayList<>();
        AlarmDeviceParameter alarmDeviceParameter = new AlarmDeviceParameter();
        alarmDeviceParameter.setAlarmSource("q1");
        alarmDeviceParameter.setCount(1L);
        list.add(alarmDeviceParameter);
        AlarmSourceHomeParameter alarmSourceHomeParameter = new AlarmSourceHomeParameter();
        alarmSourceHomeParameter.setEndTime(0L);
        alarmSourceHomeParameter.setType("DAY");
        alarmStatisticsService.mongodbInsertAll(list, alarmSourceHomeParameter);
    }

    @Test
    public void setMongoTemplate() {
        List<AlarmStatisticsGroupInfo> list = new ArrayList<>();
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfo = new AlarmStatisticsGroupInfo();
        alarmStatisticsGroupInfo.setGroupLevel("1");
        alarmStatisticsGroupInfo.setGroupNum(1);
        alarmStatisticsGroupInfo.setGroupArea("qu");
        list.add(alarmStatisticsGroupInfo);
        AlarmSourceHomeParameter alarmSourceHomeParameter = new AlarmSourceHomeParameter();
        alarmSourceHomeParameter.setType("DAY");
        alarmSourceHomeParameter.setEndTime(0L);
        alarmStatisticsService.setMongoTemplate(list, alarmSourceHomeParameter);
    }

    @Test
    public void getAlarmStatistics() {
        AlarmSourceHomeParameter alarmSourceHomeParameter = new AlarmSourceHomeParameter();
        alarmSourceHomeParameter.setType("DAY");
        alarmSourceHomeParameter.setEndTime(0L);
        alarmStatisticsService.getAlarmStatistics(alarmSourceHomeParameter);
    }

    @Test
    public void editAlarmNameStatistics() {
        List<AlarmStatisticsGroupInfo> list = new ArrayList<>();
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfo = new AlarmStatisticsGroupInfo();
        alarmStatisticsGroupInfo.setGroupLevel("撬门");
        list.add(alarmStatisticsGroupInfo);
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfoOnr = new AlarmStatisticsGroupInfo();
        alarmStatisticsGroupInfoOnr.setGroupLevel("撬锁");
        list.add(alarmStatisticsGroupInfoOnr);
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfoTwo = new AlarmStatisticsGroupInfo();
        alarmStatisticsGroupInfoTwo.setGroupLevel("未关门");
        list.add(alarmStatisticsGroupInfoTwo);
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfoThree = new AlarmStatisticsGroupInfo();
        alarmStatisticsGroupInfoThree.setGroupLevel("高温");
        list.add(alarmStatisticsGroupInfoThree);
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfoFour = new AlarmStatisticsGroupInfo();
        alarmStatisticsGroupInfoFour.setGroupLevel("低温");
        list.add(alarmStatisticsGroupInfoFour);
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfoFilter = new AlarmStatisticsGroupInfo();
        alarmStatisticsGroupInfoFilter.setGroupLevel("通信中断");
        list.add(alarmStatisticsGroupInfoFilter);
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfoSix = new AlarmStatisticsGroupInfo();
        alarmStatisticsGroupInfoSix.setGroupLevel("水浸");
        list.add(alarmStatisticsGroupInfoSix);
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfoDen = new AlarmStatisticsGroupInfo();
        alarmStatisticsGroupInfoDen.setGroupLevel("未关锁");
        list.add(alarmStatisticsGroupInfoDen);
        AlarmStatisticsGroupInfo statisticsGroupInfo = new AlarmStatisticsGroupInfo();
        statisticsGroupInfo.setGroupLevel("倾斜");
        list.add(statisticsGroupInfo);
        AlarmStatisticsGroupInfo statisticsGroupInfoOne = new AlarmStatisticsGroupInfo();
        statisticsGroupInfoOne.setGroupLevel("震动");
        list.add(statisticsGroupInfoOne);
        AlarmStatisticsGroupInfo statisticsGroupInfoTwo = new AlarmStatisticsGroupInfo();
        statisticsGroupInfoTwo.setGroupLevel("电量");
        list.add(statisticsGroupInfoTwo);
        AlarmStatisticsGroupInfo statisticsGroupInfoThree = new AlarmStatisticsGroupInfo();
        statisticsGroupInfoThree.setGroupLevel("非法开门");
        list.add(statisticsGroupInfoThree);
        AlarmStatisticsGroupInfo statisticsGroupInfoFour = new AlarmStatisticsGroupInfo();
        statisticsGroupInfoFour.setGroupLevel("工单超时");
        list.add(statisticsGroupInfoFour);
        AlarmStatisticsGroupInfo statisticsGroupInfoFilter = new AlarmStatisticsGroupInfo();
        statisticsGroupInfoFilter.setGroupLevel("应急开锁告警");
        list.add(statisticsGroupInfoFilter);
        AlarmStatisticsGroupInfo statisticsGroupInfoSix = new AlarmStatisticsGroupInfo();
        statisticsGroupInfoSix.setGroupLevel("湿度");
        list.add(statisticsGroupInfoSix);
        AlarmStatisticsGroupInfo statisticsGroupInfoTo = new AlarmStatisticsGroupInfo();
        statisticsGroupInfoTo.setGroupLevel("非法开盖（内盖）");
        list.add(statisticsGroupInfoTo);
        alarmStatisticsService.editAlarmNameStatistics(list);
    }

    @Test
    public void getAlarmHandleStatistics() {
        List<AlarmStatisticsGroupInfo> list = new ArrayList<>();
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfo = new AlarmStatisticsGroupInfo();
        alarmStatisticsGroupInfo.setGroupLevel("1");
        AlarmStatisticsGroupInfo statisticsGroupInfo = new AlarmStatisticsGroupInfo();
        statisticsGroupInfo.setGroupLevel("2");
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfoOne = new AlarmStatisticsGroupInfo();
        alarmStatisticsGroupInfoOne.setGroupLevel("3");
        list.add(alarmStatisticsGroupInfo);
        list.add(statisticsGroupInfo);
        list.add(alarmStatisticsGroupInfoOne);
        alarmStatisticsService.getAlarmHandleStatistics(list);
    }
}