package com.fiberhome.filink.alarmcurrentserver.utils;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmQueryTemplate;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrent18n;
import com.fiberhome.filink.alarmcurrentserver.service.impl.AlarmCurrentServiceImpl;
import com.fiberhome.filink.alarmcurrentserver.service.impl.AlarmQueryTemplateServiceImpl;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.api.ExportFeign;
import com.fiberhome.filink.exportapi.utils.ZipUtil;
import com.fiberhome.filink.ossapi.upload.UploadFile;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmCurrentExportTest {

    @Injectable
    private AlarmCurrentServiceImpl alarmCurrentService;

    @Injectable
    private AlarmQueryTemplateServiceImpl alarmQueryTemplateService;

    @Tested
    private AlarmCurrentExport alarmCurrentExport;

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
        filterCondition.setFilterField("alarmSourceTypeId");
        filterCondition.setFilterValue("001");
        filterCondition.setOperator("like");
        filterConditionList.add(filterCondition);
        queryCondition.setFilterConditions(filterConditionList);
        alarmCurrentExport.queryData(queryCondition);
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
        alarmCurrentExport.queryCount(queryCondition);
        QueryCondition condition = new QueryCondition();
        PageCondition pageCondition1 = new PageCondition();
        pageCondition1.setPageNum(1);
        pageCondition1.setPageSize(10);
        condition.setPageCondition(pageCondition1);
        List<FilterCondition> filterConditions = new ArrayList<>();
        FilterCondition condition1 = new FilterCondition();
        condition1.setFilterField(AlarmCurrent18n.TEMPLATE_ID);
        condition1.setFilterValue("001");
        condition1.setOperator("like");
        filterConditions.add(condition1);
        condition.setFilterConditions(filterConditions);
        alarmCurrentExport.queryCount(condition);
    }

    @Test
    public void templateQuery() throws Exception {
        QueryCondition queryCondition = new QueryCondition();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);
        List<FilterCondition> filterConditionList = new ArrayList<>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("alarmSourceTypeId");
        filterCondition.setFilterValue("1");
        filterCondition.setOperator("like");
        filterConditionList.add(filterCondition);
        queryCondition.setFilterConditions(filterConditionList);
        AlarmQueryTemplate alarmQueryTemplate = new AlarmQueryTemplate();
        alarmQueryTemplate.setId("1");
        alarmQueryTemplate.setCreateUser("1");
        new Expectations() {
            {
                alarmQueryTemplateService.queryAlarmTemplateById("1");
                result = ResultUtils.success(alarmQueryTemplate);
            }
        };
        alarmCurrentExport.templateQuery(queryCondition, filterConditionList);
    }

}