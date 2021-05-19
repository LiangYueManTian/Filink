package com.fiberhome.filink.rfid.export.statistics;

import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.rfid.req.statistics.export.ExportJumpConnectionInCabinetReq;
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
 * JumpConnectionInCabinetExportTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/5
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ExportApiUtils.class})
public class JumpConnectionInCabinetExportTest {
    @InjectMocks
    private JumpConnectionInCabinetExport jumpConnectionInCabinetExport;

    private List<Object> objectList = new ArrayList<>();

    private QueryCondition condition = new QueryCondition();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ExportJumpConnectionInCabinetReq exportJumpConnectionInCabinetReq = new ExportJumpConnectionInCabinetReq();
        objectList.add(JSON.toJSON(exportJumpConnectionInCabinetReq));
        PowerMockito.mockStatic(ExportApiUtils.class);
    }

    @Test
    public void queryData() {
        when(ExportApiUtils.getObjList(condition)).thenReturn(objectList);
        List list = jumpConnectionInCabinetExport.queryData(condition);
        Assert.assertTrue(list.size() == 1);
    }

    @Test
    public void queryCount() {
        when(ExportApiUtils.getObjectList()).thenReturn(objectList);
        Integer integer = jumpConnectionInCabinetExport.queryCount(condition);
        Assert.assertTrue(integer == 1);
    }
}