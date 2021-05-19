package com.fiberhome.filink.alarmcurrentserver.controller;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmHomeParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmSourceHomeParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsTemp;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTime;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentService;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmStatisticsService;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmStatisticsTemplateService;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ColumnInfo;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmStatisticsControllerTest {

    @Injectable
    private AlarmStatisticsService alarmStatisticsTempService;

    @Injectable
    private AlarmCurrentService alarmCurrentService;

    @Injectable
    private AlarmStatisticsTemplateService alarmStatisticsTemplateService;

    @Tested
    private AlarmStatisticsController alarmStatisticsController;

    /**
     * 告警类型统计
     * @throws Exception
     */
    @Test
    public void queryAlarmByLevelAndArea() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.queryAlarmByLevelAndArea((AlarmStatisticsParameter) any);
            }
        };
        Result result = alarmStatisticsController.queryAlarmByLevelAndArea(new QueryCondition<AlarmStatisticsParameter>());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result resultOne = alarmStatisticsController.queryAlarmByLevelAndArea(null);
    }

    /**
     * 告警处理统计
     * @throws Exception
     */
    @Test
    public void queryAlarmHandle() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.alarmHandleStatistics((AlarmStatisticsParameter) any);
            }
        };
        Result result = alarmStatisticsController.queryAlarmHandle(new QueryCondition<AlarmStatisticsParameter>());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result resultOne = alarmStatisticsController.queryAlarmHandle(null);
    }

    /**
     * 告警名称统计
     * @throws Exception
     */
    @Test
    public void queryAlarmName() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.alarmNameStatistics((QueryCondition<AlarmStatisticsParameter>) any);
            }
        };
        Result result = alarmStatisticsController.queryAlarmName(new QueryCondition<AlarmStatisticsParameter>());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result resultOne = alarmStatisticsController.queryAlarmName(null);
    }

    /**
     * 区域告警比例统计
     * @throws Exception
     */
    @Test
    public void queryAlarmCountByLevel() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.queryAlarmCountByLevel((QueryCondition<AlarmStatisticsParameter>) any);
            }
        };
        Result result = alarmStatisticsController.queryAlarmCountByLevel(new QueryCondition<AlarmStatisticsParameter>());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result count = alarmStatisticsController.queryAlarmCountByLevel(null);
    }

    /**
     * 告警增量统计
     * @throws Exception
     */
    @Test
    public void queryAlarmIncrementalStatistics() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.alarmIncrementalStatistics((QueryCondition<AlarmStatisticsParameter>) any, anyString, false);
            }
        };
        Result result = alarmStatisticsController.queryAlarmIncrementalStatistics(new QueryCondition<AlarmStatisticsParameter>());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 告警增量统计
     * @throws Exception
     */
    @Test
    public void queryIncrementalStatistics() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.alarmIncrementalStatistics((QueryCondition<AlarmStatisticsParameter>) any, anyString, true);
            }
        };
        Result result = alarmStatisticsController.queryIncrementalStatistics("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 根据统计模板id查询告警统计信息
     * @throws Exception
     */
    @Test
    public void queryAlarmStatById() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTemplateService.queryAlarmStatisticsTempId(anyString);
            }
        };
        Result result = alarmStatisticsController.queryAlarmStatById("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询告警统计模板列表信息
     * @throws Exception
     */
    @Test
    public void queryAlarmStatisticsTempList() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTemplateService.queryAlarmStatisticsTempList(anyString);
            }
        };
        Result result = alarmStatisticsController.queryAlarmStatisticsTempList("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void addAlarmStatisticsTemp() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTemplateService.addAlarmStatisticsTemp((AlarmStatisticsTemp) any);
            }
        };
        Result result = alarmStatisticsController.addAlarmStatisticsTemp(new AlarmStatisticsTemp());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 修改告警统计模板信息
     * @throws Exception
     */
    @Test
    public void updateAlarmStatisticsTemp() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTemplateService.updateAlarmStatisticsTemp((AlarmStatisticsTemp) any);
            }
        };
        Result result = alarmStatisticsController.updateAlarmStatisticsTemp(new AlarmStatisticsTemp());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 删除告警统计模板信息
     * @throws Exception
     */
    @Test
    public void deleteManyAlarmStatisticsTemp() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTemplateService.deleteManyAlarmStatisticsTemp((String[]) any);
            }
        };
        String[] strings = new String[]{"1"};
        Result result = alarmStatisticsController.deleteManyAlarmStatisticsTemp(strings);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result resultOne = alarmStatisticsController.deleteManyAlarmStatisticsTemp(null);
    }

    /**
     * 查询告警top
     * @throws Exception
     */
    @Test
    public void queryAlarmNameGroup() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.queryAlarmNameGroup((QueryCondition<AlarmStatisticsParameter>) any);
            }
        };
        Result result = alarmStatisticsController.queryAlarmNameGroup(new QueryCondition<AlarmStatisticsParameter>());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result resultOne = alarmStatisticsController.queryAlarmNameGroup(null);
    }

    /**
     * 首页设施统计告警名称信息
     * @throws Exception
     */
    @Test
    public void queryAlarmNameHomePage() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.queryAlarmNameHomePage((AlarmHomeParameter) any);
            }
        };
        Result result = alarmStatisticsController.queryAlarmNameHomePage(new AlarmHomeParameter());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result resultOne = alarmStatisticsController.queryAlarmNameHomePage(null);
    }

    /**
     * 设施统计告警级别信息
     * @throws Exception
     */
    @Test
    public void queryAlarmCurrentSourceLevel() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.queryAlarmCurrentSourceLevel((AlarmSourceHomeParameter) any);
            }
        };
        Result result = alarmStatisticsController.queryAlarmCurrentSourceLevel(new AlarmSourceHomeParameter());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result resultOne = alarmStatisticsController.queryAlarmCurrentSourceLevel(null);
    }

    /**
     * 设施统计历史告警级别信息
     * @throws Exception
     */
    @Test
    public void queryAlarmHistorySourceLevel() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.queryAlarmHistorySourceLevel((AlarmSourceHomeParameter) any);
            }
        };
        Result result = alarmStatisticsController.queryAlarmHistorySourceLevel(new AlarmSourceHomeParameter());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result resultOne = alarmStatisticsController.queryAlarmHistorySourceLevel(null);
    }

    /**
     * 设施统计当前告警名称信息
     * @throws Exception
     */
    @Test
    public void queryAlarmCurrentSourceName() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.queryAlarmCurrentSourceName((AlarmSourceHomeParameter) any);
            }
        };
        Result result = alarmStatisticsController.queryAlarmCurrentSourceName(new AlarmSourceHomeParameter());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result resultOne = alarmStatisticsController.queryAlarmCurrentSourceName(null);
    }

    /**
     * 设施统计历史告警名称信息
     * @throws Exception
     */
    @Test
    public void queryAlarmHistorySourceName() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.queryAlarmHistorySourceName((AlarmSourceHomeParameter) any);
            }
        };
        Result result = alarmStatisticsController.queryAlarmHistorySourceName(new AlarmSourceHomeParameter());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result resultOne = alarmStatisticsController.queryAlarmHistorySourceName(null);
    }

    /**
     * 新增告警设施统计信息
     * @throws Exception
     */
    @Test
    public void querySourceIncremental() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.querySourceIncremental((AlarmSourceHomeParameter) any);
            }
        };
        alarmStatisticsController.querySourceIncremental(new AlarmSourceHomeParameter());
        alarmStatisticsController.querySourceIncremental(null);
    }

    /**
     * 设施增量查询信息
     * @throws Exception
     */
    @Test
    public void queryAlarmSourceIncremental() throws Exception {
        AlarmSourceHomeParameter alarmSourceHomeParameter = new AlarmSourceHomeParameter();
        alarmSourceHomeParameter.setBeginTime(0L);
        alarmSourceHomeParameter.setEndTime(18000000L);
        new Expectations() {
            {
                alarmStatisticsTempService.queryAlarmSourceIncremental((AlarmSourceHomeParameter) any);
            }
        };
        Result result = alarmStatisticsController.queryAlarmSourceIncremental(alarmSourceHomeParameter);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 删除告警增量统计（日，周，年）
     * @throws Exception
     */
    @Test
    public void tstTak() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.deleteAlarmIncrementalStatistics((Long) any, anyString);
            }
        };
       alarmStatisticsController.tstTak();
    }

    /**
     * 大屏告警级别统计信息
     * @throws Exception
     */
    @Test
    public void queryAlarmCurrentLevelGroup() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.queryAlarmCurrentLevelGroup();
            }
        };
        Result result = alarmStatisticsController.queryAlarmCurrentLevelGroup();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 大屏设施根据日周月top
     * @throws Exception
     */
    @Test
    public void queryScreenDeviceIdGroup() throws Exception {
        AlarmTime alarmTime = new AlarmTime();
        alarmTime.setEndTime(0L);
        new Expectations() {
            {
                alarmStatisticsTempService.queryScreenDeviceIdGroup((AlarmTime) any);
                result = ResultUtils.success(alarmTime);
            }
        };
        Result result = alarmStatisticsController.queryScreenDeviceIdGroup(new AlarmTime());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result resultOne = alarmStatisticsController.queryScreenDeviceIdGroup(null);
    }

    /**
     * 大屏设施根据top
     * @throws Exception
     */
    @Test
    public void queryScreenDeviceIdsGroup() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.queryScreenDeviceIdsGroup();
            }
        };
        Result result = alarmStatisticsController.queryScreenDeviceIdsGroup();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * app告警名称统计
     * @throws Exception
     */
    @Test
    public void queryAppAlarmNameGroup() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempService.queryAppAlarmNameGroup();
            }
        };
        Result result = alarmStatisticsController.queryAppAlarmNameGroup();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 告警统计导出
     * @throws Exception
     */
    @Test
    public void exportAlarmStatisticsList() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTemplateService.exportAlarmStatisticList((ExportDto) any);
            }
        };
        ExportDto exportDto = new ExportDto();
        QueryCondition queryCondition = new QueryCondition();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);
        List<FilterCondition> filterConditionList = new ArrayList<FilterCondition>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("id");
        filterCondition.setFilterValue("1");
        filterCondition.setOperator("like");
        filterConditionList.add(filterCondition);
        queryCondition.setFilterConditions(filterConditionList);
        exportDto.setQueryCondition(queryCondition);
        exportDto.setTaskId("1");
        exportDto.setToken("");
        exportDto.setUserId("1");
        List<ColumnInfo> list = new ArrayList<>();
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setColumnName("1");
        columnInfo.setIsTranslation(1);
        columnInfo.setPropertyName("1");
        list.add(columnInfo);
        exportDto.setColumnInfoList(list);
        exportDto.setExcelType(1);
        exportDto.setFileGeneratedNum(1);
        exportDto.setFileNum(1);
        exportDto.setFilePath("1");
        exportDto.setListName("admin");
        exportDto.setMethodPath("hou");
        Result result = alarmStatisticsController.exportAlarmStatisticsList(exportDto);
    }

    @Test
    public void testInsert() {
        alarmStatisticsController.testInsert(new AlarmSourceHomeParameter());
    }

}