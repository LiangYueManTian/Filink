package com.fiberhome.filink.alarmcurrentserver.controller;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmSourceLevelParameter;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentExportService;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentService;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmDisposeService;
import com.fiberhome.filink.alarmcurrentserver.stream.AlarmStreams;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ColumnInfo;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.server_common.utils.I18nUtils;
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
public class AlarmCurrentControllerTest {
    /**
     * 测试对象
     */
    @Tested
    private AlarmCurrentController alarmCurrentController;

    /**
     * 当前告警Service
     */
    @Injectable
    private AlarmCurrentService alarmCurrentService;

    /**
     * 导出service
     */
    @Injectable
    private AlarmCurrentExportService alarmCurrentExportService;

    /**
     * websocket
     */
    @Injectable
    private AlarmStreams alarmStreams;

    /**
     * 实体类
     */
    private AlarmCurrent alarmCurrent;

    @Injectable
    private AlarmDisposeService alarmDisposeService;

    /**
     * 查询当前告警列表信息
     */
    @Test
    public void queryAlarmCurrentListTest() {
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
        new Expectations() {
            {
//                alarmCurrentService.queryAlarmCurrentList(queryCondition);
                result = ResultUtils.success();
            }
        };
        Result result = alarmCurrentController.queryAlarmCurrentList(queryCondition);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询当前告警信息
     */
    @Test
    public void queryAlarmCurrentByIdsFeignTest() {
        new Expectations() {
            {
                alarmCurrentService.queryAlarmCurrentByIdsFeign((List<String>) any);
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmCurrentController.queryAlarmCurrentByIdsFeign(list);
        alarmCurrentController.queryAlarmCurrentByIdsFeign(null);
    }

    /**
     * 查询单个当前告警的详细信息
     */
    @Test
    public void queryAlarmCurrentInfoByIdTest() {
        new Expectations() {
            {
                alarmCurrentService.queryAlarmCurrentInfoById((String) any);
                result = ResultUtils.success();
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
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "ALARM_ID_NULL";
            }
        };
        Result result = alarmCurrentController.queryAlarmCurrentInfoById("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result result1 = alarmCurrentController.queryAlarmCurrentInfoById(null);
    }

    /**
     * 批量修改当前告警备注信息
     */
    @Test
    public void updateAlarmRemarkTest() {
        List<AlarmCurrent> list = new ArrayList<>();
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setId("1");
        list.add(alarmCurrent);
        new Expectations() {
            {
                alarmCurrentService.batchUpdateAlarmRemark(list);
                result = ResultUtils.success();
            }
        };
        Result result = alarmCurrentController.batchUpdateAlarmRemark(list);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 批量设置当前告警的告警确认状态
     */
    @Test
    public void updateAlarmConfirmStatusTest() {
        List<AlarmCurrent> alarmCurrentList = new ArrayList<>();
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setId("1");
        alarmCurrentList.add(alarmCurrent);
        new Expectations() {
            {
                alarmCurrentService.batchUpdateAlarmConfirmStatus((List<AlarmCurrent>) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmCurrentController.batchUpdateAlarmConfirmStatus(alarmCurrentList);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 批量设置当前告警的告警清除状态
     */
    @Test
    public void updateAlarmCleanStatusTest() {
        List<AlarmCurrent> alarmCurrentList = new ArrayList<>();
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setId("1");
        alarmCurrentList.add(alarmCurrent);
        new Expectations() {
            {
                alarmCurrentService.batchUpdateAlarmCleanStatus((List<AlarmCurrent>) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmCurrentController.batchUpdateAlarmCleanStatus(alarmCurrentList);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询各级别告警总数
     */
    @Test
    public void queryEveryAlarmCountTest() {
        new Expectations() {
            {
                alarmCurrentService.queryEveryAlarmCount(anyString);
                result = ResultUtils.success();
            }
        };
        Result result = alarmCurrentController.queryEveryAlarmCount("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询设备信息是否存在
     */
    @Test
    public void queryAlarmSourceTest() {
        new Expectations() {
            {
                alarmCurrentService.queryAlarmSourceForFeign((List<String>) any);
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmCurrentController.queryAlarmSourceForFeign(list);
    }

    /**
     * 查询区域信息是否存在
     */
    @Test
    public void queryAreaTest() {
        new Expectations() {
            {
                alarmCurrentService.queryAreaForFeign((List<String>) any);
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmCurrentController.queryAreaForFeign(list);
    }

    /**
     * 查询单位id信息
     */
    @Test
    public void queryDepartmentIdTest() {
        new Expectations() {
            {
                alarmCurrentService.queryDepartmentId((List<String>) any);
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        Result result = alarmCurrentController.queryDepartmentId(list);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result resultTwo = new Result();
                resultTwo.setCode(1);
                resultTwo.setMsg("scuuess");
                return resultTwo;
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "INCORRECT_PARAMETER";
            }
        };
        Result resultOne = alarmCurrentController.queryDepartmentId(null);
    }

    /**
     * 设施id查询告警信息
     */
    @Test
    public void queryAlarmDeviceIdTest() {
        new Expectations() {
            {
                alarmCurrentService.queryAlarmDeviceId(anyString);
            }
        };
        Result result = alarmCurrentController.queryAlarmDeviceId("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result resultTwo = new Result();
                resultTwo.setCode(1);
                resultTwo.setMsg("scuuess");
                return resultTwo;
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "INCORRECT_PARAMETER";
            }
        };
        Result resultOne = alarmCurrentController.queryAlarmDeviceId(null);
    }

    /**
     * 查询告警类型
     */
    @Test
    public void queryIsStatusTest() {
        new Expectations() {
            {
                alarmCurrentService.queryIsStatus(anyString);
            }
        };
        Result result = alarmCurrentController.queryIsStatus("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result resultOne = new Result();
                resultOne.setCode(1);
                resultOne.setMsg("success");
                return resultOne;
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "INCORRECT_PARAMETER";
            }
        };
        Result resultOne = alarmCurrentController.queryIsStatus(null);
    }

    /**
     * 导出当前告警信息
     */
    @Test
    public void exportAlarmListTest() {
        new Expectations() {
            {
                alarmCurrentExportService.exportAlarmList((ExportDto) any);
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
        Result result = alarmCurrentController.exportAlarmList(exportDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询告警门信息
     */
    @Test
    public void queryAlarmDoorByIdsFeignTest() {
        new Expectations() {
            {
                alarmCurrentService.queryAlarmDoor((List<String>) any);
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmCurrentController.queryAlarmDoorByIdsFeign(list);
        alarmCurrentController.queryAlarmDoorByIdsFeign(null);
    }

    /**
     * 删除告警信息
     */
    @Test
    public void deleteAlarmsFeignTest() {
        new Expectations() {
            {
                alarmCurrentService.deleteAlarms((List<String>) any);
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        Result result = alarmCurrentController.batchDeleteAlarmsFeign(list);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result resultOne = new Result();
                resultOne.setCode(1);
                resultOne.setMsg("success");
                return resultOne;
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "INCORRECT_PARAMETER";
            }
        };
        Result resultOne = alarmCurrentController.batchDeleteAlarmsFeign(null);
    }

    /**
     * 根据单位id查询告警信息
     */
    @Test
    public void queryAlarmDepartmentFeignTest() {
        new Expectations() {
            {
                alarmCurrentService.queryAlarmDepartmentFeign((List<String>) any);
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmCurrentController.queryAlarmDepartmentFeign(list);
        alarmCurrentController.queryAlarmDepartmentFeign(null);
    }

    /**
     * 告警设施总数信息
     * @throws Exception
     */
    @Test
    public void queryAlarmObjectCountTest() throws Exception {
        new Expectations() {
            {
                alarmCurrentExportService.queryAlarmObjectCount(anyString);
            }
        };
        Result result = alarmCurrentController.queryAlarmObjectCount("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 首页告警设施id更多信息
     * @throws Exception
     */
    @Test
    public void queryAlarmObjectCountHonePage() throws Exception {
        new Expectations() {
            {
                alarmCurrentExportService.queryAlarmObjectCountHonePage((AlarmSourceLevelParameter) any);
            }
        };
        Result result = alarmCurrentController.queryAlarmObjectCountHonePage(new AlarmSourceLevelParameter());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 首页告警级别更多信息
     * @throws Exception
     */
    @Test
    public void queryAlarmDeviceIdHonePage() throws Exception {
        new Expectations() {
            {
                alarmCurrentExportService.queryAlarmDeviceIdHonePage((AlarmSourceLevelParameter) any);
            }
        };
        Result result = alarmCurrentController.queryAlarmDeviceIdHonePage(new AlarmSourceLevelParameter());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 首页告警告警id查询设施类型
     * @throws Exception
     */
    @Test
    public void queryAlarmIdCountHonePage() throws Exception {
        new Expectations() {
            {
                alarmCurrentExportService.queryAlarmIdCountHonePage((AlarmSourceLevelParameter) any);
            }
        };
        Result result = alarmCurrentController.queryAlarmIdCountHonePage(new AlarmSourceLevelParameter());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 首页告警告警id查询告警级别
     * @throws Exception
     */
    @Test
    public void queryAlarmIdHonePage() throws Exception {
        new Expectations() {
            {
                alarmCurrentExportService.queryAlarmIdHonePage((AlarmSourceLevelParameter) any);
            }
        };
        Result result = alarmCurrentController.queryAlarmIdHonePage(new AlarmSourceLevelParameter());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 告警top导出
     * @throws Exception
     */
    @Test
    public void exportDeviceTop() throws Exception {
        new Expectations() {
            {
                alarmCurrentExportService.exportDeviceTop((ExportDto) any);
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
        Result result = alarmCurrentController.exportDeviceTop(exportDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 告警设施id查询c当前告警ode信息
     * @throws Exception
     */
    @Test
    public void queryAlarmDeviceIdCode() throws Exception {
        new Expectations() {
            {
                alarmCurrentService.queryAlarmDeviceIdCode(anyString);
            }
        };
        alarmCurrentController.queryAlarmDeviceIdCode("1");
    }

    /**
     * 查询当前告警列表信息
     * @throws Exception
     */
    @Test
    public void queryAlarmCurrentPage() throws Exception {
        new Expectations() {
            {
                alarmCurrentService.queryAlarmCurrentPage((QueryCondition<AlarmCurrent>) any);
            }
        };
        Result result = alarmCurrentController.queryAlarmCurrentPage(new QueryCondition<AlarmCurrent>());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }


    /**
     * websocket测试
     */
    @Test
    public void websocketTest() {
        alarmCurrentController.websocket();
    }
}