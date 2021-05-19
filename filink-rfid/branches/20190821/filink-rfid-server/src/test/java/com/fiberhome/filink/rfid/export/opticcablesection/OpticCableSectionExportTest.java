package com.fiberhome.filink.rfid.export.opticcablesection;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableSectionInfoDao;
import com.fiberhome.filink.rfid.resp.opticcable.OpticCableSectionInfoResp;
import com.fiberhome.filink.rfid.service.opticcable.OpticCableSectionInfoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * OpticCableSectionExportTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/5
 */
@RunWith(MockitoJUnitRunner.class)
public class OpticCableSectionExportTest {

    @InjectMocks
    private  OpticCableSectionExport opticCableSectionExport;
    @Mock
    private OpticCableSectionInfoDao opticCableSectionInfoDao;

    @Mock
    private OpticCableSectionInfoService opticCableSectionInfoService;

    private QueryCondition queryCondition= new QueryCondition();
    @Test
    public void queryData() {
        List<OpticCableSectionInfoResp> resultList = new ArrayList<>();
        when(opticCableSectionInfoDao.selectOpticCableSection(queryCondition)).thenReturn(resultList);
        when(opticCableSectionInfoService.assemblyOpticCableSectionInfoResp((resultList))).thenReturn(resultList);
        Assert.assertEquals(resultList,opticCableSectionExport.queryData(queryCondition));

    }

    @Test
    public void queryCount() {
        when(opticCableSectionInfoDao.opticCableSectionByIdTotal(queryCondition)).thenReturn(2);
        Assert.assertTrue(2==opticCableSectionExport.queryCount(queryCondition));
    }
}