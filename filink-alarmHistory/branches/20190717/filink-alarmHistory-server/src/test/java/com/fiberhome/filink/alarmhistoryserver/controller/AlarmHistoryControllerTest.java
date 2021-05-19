package com.fiberhome.filink.alarmhistoryserver.controller;

import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory;
import com.fiberhome.filink.alarmhistoryserver.service.AlarmHistoryExportService;
import com.fiberhome.filink.alarmhistoryserver.service.AlarmHistoryService;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ColumnInfo;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.bean.User;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JMockit.class)
public class AlarmHistoryControllerTest {
    /**
     * controller测试
     */
    @Tested
    private AlarmHistoryController alarmHistoryController;

    /**
     * 历史告警Service
     */
    @Injectable
    private AlarmHistoryService alarmHistoryService;

    /**
     * 导出
     */
    @Injectable
    private AlarmHistoryExportService alarmHistoryExportService;

    /**
     * 查询历史告警列表信息
     */
    @Test
    public void queryAlarmHistoryListTest() {
        new Expectations() {
            {
                alarmHistoryService.queryAlarmHistoryList((QueryCondition<AlarmHistory>) any, (User) any, true);
            }
        };
        Result result = alarmHistoryController.queryAlarmHistoryList(new QueryCondition<>());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询单个历史告警的信息
     */
    @Test
    public void queryAlarmHistoryInfoByIdTest() {
        new Expectations() {
            {
                alarmHistoryService.queryAlarmHistoryInfoById((String) any);
                result = ResultUtils.success();
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "ALARM_ID_NULL";
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result result = new Result();
                result.setCode(1);
                result.setMsg("success");
                return result;
            }
        };
        Result result = alarmHistoryController.queryAlarmHistoryInfoById("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result resultOne = alarmHistoryController.queryAlarmHistoryInfoById(null);
    }

    /**
     * 查询单个历史告警的信息
     */
    @Test
    public void queryAlarmHistoryByIdFeignTest() {
        new Expectations() {
            {
                alarmHistoryService.queryAlarmHistoryById((String) any);
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "ALARM_ID_NULL";
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result result = new Result();
                result.setCode(1);
                result.setMsg("success");
                return result;
            }
        };
        alarmHistoryController.queryAlarmHistoryByIdFeign("1");
        alarmHistoryController.queryAlarmHistoryByIdFeign(null);
    }

    /**
     * 根据设施id查询告警信息
     */
    @Test
    public void queryAlarmHistoryDeviceIdTest() {
        new Expectations() {
            {
                alarmHistoryService.queryAlarmHistoryDeviceId(anyString);
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "INCORRECT_PARAMETER";
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result result = new Result();
                result.setCode(1);
                result.setMsg("success");
                return result;
            }
        };
        Result result = alarmHistoryController.queryAlarmHistoryDeviceId("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result resultOne = alarmHistoryController.queryAlarmHistoryDeviceId(null);
    }

    /**
     * 批量修改历史告警备注信息
     */
    @Test
    public void batchUpdateAlarmRemarkTest() {
        new Expectations() {
            {
                alarmHistoryService.batchUpdateAlarmRemark((List<AlarmHistory>) any);
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "INCORRECT_PARAMETER";
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result result = new Result();
                result.setCode(1);
                result.setMsg("success");
                return result;
            }
        };
        List<AlarmHistory> list = new ArrayList<>();
        AlarmHistory alarmHistory = new AlarmHistory();
        alarmHistory.setId("1");
        list.add(alarmHistory);
        Result result = alarmHistoryController.batchUpdateAlarmRemark(list);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result resultOne = alarmHistoryController.batchUpdateAlarmRemark(null);
    }

    /**
     * 添加历史告警信息
     */
    @Test
    public void insertAlarmHistoryFeignTest() {
        new Expectations() {
            {
                alarmHistoryService.insertAlarmHistoryFeign((AlarmHistory) any);
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "INCORRECT_PARAMETER";
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result result = new Result();
                result.setCode(1);
                result.setMsg("success");
                return result;
            }
        };
        AlarmHistory alarmHistory = new AlarmHistory();
        alarmHistory.setId("1");
        Result result = alarmHistoryController.insertAlarmHistoryFeign(alarmHistory);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result resultOne = alarmHistoryController.insertAlarmHistoryFeign(null);
    }

    /**
     * 导出当前告警信息
     */
    @Test
    public void exportAlarmListTest() {
        new Expectations() {
            {
                alarmHistoryExportService.exportAlarmList((ExportDto) any);
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
        Result result = alarmHistoryController.exportAlarmList(exportDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 删除历史告警信息
     */
   /* @Test
    public void batchDeleteAlarmHistoryFeignTest() {
        new Expectations() {
            {
                alarmHistoryService.deleteAlarmHistory(anyString);
            }
        };
        alarmHistoryController.deleteAlarmHistoryFeign("1");
        alarmHistoryController.deleteAlarmHistoryFeign(null);
    }*/

    /**
     * 定时任务添加历史告警信息
     */
    @Test
    public void insertAlarmHistoryListFeignTest() {
        new Expectations() {
            {
                alarmHistoryService.insertAlarmHistoryList((List<AlarmHistory>) any);
            }
        };
        List<AlarmHistory> list = new ArrayList<>();
        AlarmHistory alarmHistory = new AlarmHistory();
        alarmHistory.setId("1");
        list.add(alarmHistory);
        alarmHistoryController.insertAlarmHistoryListFeign(list);
        alarmHistoryController.insertAlarmHistoryListFeign(null);
    }

    @Test
    public void queryAlarmHistoryByIdsFeign() {
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmHistoryController.queryAlarmHistoryByIdsFeign(list);
        alarmHistoryController.queryAlarmHistoryByIdsFeign(null);
    }

    @Test
    public void deleteAlarmHistoryFeign() {
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmHistoryController.deleteAlarmHistoryFeign(list);
        alarmHistoryController.deleteAlarmHistoryFeign(null);
    }

    @Test
    public void queryDepartmentHistory() {
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmHistoryController.queryDepartmentHistory(list);
        alarmHistoryController.queryDepartmentHistory(null);
    }
}