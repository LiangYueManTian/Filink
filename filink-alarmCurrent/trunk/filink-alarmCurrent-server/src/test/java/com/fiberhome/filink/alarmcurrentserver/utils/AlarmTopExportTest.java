package com.fiberhome.filink.alarmcurrentserver.utils;

import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.api.ExportFeign;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.exportapi.utils.ZipUtil;
import com.fiberhome.filink.ossapi.upload.UploadFile;
import java.util.ArrayList;
import java.util.List;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmTopExportTest {

    @Tested
    private AlarmTopExport alarmTopExport;

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
        List<Object> objects = new ArrayList<>();
        Object object = new Object();
        JSONObject json = JSONObject.fromObject(object);
        json.put("deviceName", "deviceName");
        json.put("areaName", "areaName");
        json.put("address", "address");
        json.put("accountabilityUnitName", "accountabilityUnitName");
        json.put("status", "status");
        json.put("ranking", 1);
        objects.add(json);
        new MockUp<ExportApiUtils>() {
            @Mock
            List<Object> getObjList(QueryCondition condition) {
                return objects;
            }
        };
        alarmTopExport.queryData(queryCondition);
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
        List<Object> objectList = new ArrayList<>();
        new MockUp<ExportApiUtils>() {
            @Mock
            Object getObjectList() {
                return objectList;
            }
        };
        alarmTopExport.queryCount(queryCondition);
    }

}