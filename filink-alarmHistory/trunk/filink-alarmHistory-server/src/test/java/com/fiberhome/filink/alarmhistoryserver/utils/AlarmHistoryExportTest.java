package com.fiberhome.filink.alarmhistoryserver.utils;

import com.fiberhome.filink.alarmhistoryserver.service.impl.AlarmHistoryServiceImpl;
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
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmHistoryExportTest {
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
    @Tested
    private AlarmHistoryExport alarmHistoryExport;

    @Injectable
    private AlarmHistoryServiceImpl alarmHistoryService;
    @Test
    public void queryData() throws Exception {
        QueryCondition queryCondition = new QueryCondition();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);
        List<FilterCondition> filterConditionList = new ArrayList<>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("alarmSourceTypeId");
        filterCondition.setFilterValue("001");
        filterCondition.setOperator("like");
        filterConditionList.add(filterCondition);
        queryCondition.setFilterConditions(filterConditionList);
        alarmHistoryExport.queryData(queryCondition);
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
        filterCondition.setFilterField("alarmSourceTypeId");
        filterCondition.setFilterValue("001");
        filterCondition.setOperator("like");
        filterConditionList.add(filterCondition);
        queryCondition.setFilterConditions(filterConditionList);
        alarmHistoryExport.queryCount(queryCondition);
    }

}