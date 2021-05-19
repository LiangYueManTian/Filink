package com.fiberhome.filink.alarmcurrentserver.utils;

import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.api.ExportFeign;
import com.fiberhome.filink.exportapi.utils.ZipUtil;
import com.fiberhome.filink.ossapi.upload.UploadFile;
import java.util.ArrayList;
import java.util.List;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmStatisticsExportTest {
    @Tested
    private AlarmStatisticsExport alarmStatisticsExport;

    @Injectable
    private ExportFeign exportFeign;

    @Injectable
    private Integer listExcelSize = 1000;

    @Injectable
    private UploadFile uploadFil;

    @Injectable
    private ZipUtil zipUtil;

    @Injectable
    private Integer maxExportDataSize = 1000;

    @Test
    public void queryData() throws Exception {
        QueryCondition queryCondition = new QueryCondition();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);
        List<FilterCondition> filterConditionList = new ArrayList<>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("alarmSource");
        filterCondition.setFilterValue("001");
        filterCondition.setOperator("like");
        filterConditionList.add(filterCondition);
        String code = "code";
        queryCondition.setBizCondition(code);
        queryCondition.setFilterConditions(filterConditionList);
        alarmStatisticsExport.queryData(queryCondition);
    }

    @Test
    public void getPageTypeChange() throws Exception {
        List<Object> objects = new ArrayList<>();
        Object object = new Object();
        JSONObject json = JSONObject.fromObject(object);
        objects.add(json);
        String pageType = AppConstant.ALARM_TYPE_STATISTICS;
        alarmStatisticsExport.getPageTypeChange(objects, pageType);
        String statistics = AppConstant.ALARM_HANDLE_STATISTICS;
        alarmStatisticsExport.getPageTypeChange(objects, statistics);
        String nameStatistics = AppConstant.ALARM_NAME_STATISTICS;
        alarmStatisticsExport.getPageTypeChange(objects, nameStatistics);
        String string = AppConstant.AREA_RATIO_STATISTICS;
        alarmStatisticsExport.getPageTypeChange(objects, string);
    }

    @Test
    public void queryCount() throws Exception {
        QueryCondition queryCondition = new QueryCondition();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);
        List<FilterCondition> filterConditionList = new ArrayList<>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("a");
        filterCondition.setFilterValue("001");
        filterCondition.setOperator("like");
        filterConditionList.add(filterCondition);
        String code = "code";
        queryCondition.setBizCondition(code);
        queryCondition.setFilterConditions(filterConditionList);
        alarmStatisticsExport.queryData(queryCondition);
    }

}