package com.fiberhome.filink.rfid.export.statistics;

import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.rfid.req.statistics.export.ExportOpticCableInfoStatisticReq;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.when;

/**
 * PortStatisticsExportTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/5
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ExportApiUtils.class})
public class PortStatisticsExportTest {
    /**
     * 被测试类
     */
    @InjectMocks
    private PortStatisticsExport portStatisticsExport;

    private List<Object> objectList = new ArrayList<>();

    private QueryCondition condition = new QueryCondition();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ExportOpticCableInfoStatisticReq exportOpticCableInfoStatisticReq = new ExportOpticCableInfoStatisticReq();
        objectList.add(JSON.toJSON(exportOpticCableInfoStatisticReq));
        PowerMockito.mockStatic(ExportApiUtils.class);
    }

    @Test
    public void queryData() {
        when(ExportApiUtils.getObjList(condition)).thenReturn(objectList);
        List list = portStatisticsExport.queryData(condition);
        Assert.assertTrue(list.size() == 1);
    }

    @Test
    public void queryCount() {
        when(ExportApiUtils.getObjectList()).thenReturn(objectList);
        Integer integer = portStatisticsExport.queryCount(condition);
        Assert.assertTrue(integer == 1);
    }
}