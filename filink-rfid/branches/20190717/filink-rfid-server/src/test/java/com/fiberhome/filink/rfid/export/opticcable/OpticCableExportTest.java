package com.fiberhome.filink.rfid.export.opticcable;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableInfoDao;
import com.fiberhome.filink.rfid.resp.opticcable.OpticCableInfoResp;
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
 * OpticCableExportTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/5
 */
@RunWith(MockitoJUnitRunner.class)
public class OpticCableExportTest {
    @InjectMocks
    private OpticCableExport opticCableExport;
    @Mock
    private OpticCableInfoDao opticCableInfoDao;

    private QueryCondition queryCondition = new QueryCondition();
    @Test
    public void queryData() {
        List<OpticCableInfoResp> resultList = new ArrayList<>();
        when(opticCableInfoDao.opticCableListByPage(queryCondition)).thenReturn(resultList);
        Assert.assertEquals(resultList,opticCableExport.queryData(queryCondition));
    }

    @Test
    public void queryCount() {
        when(opticCableInfoDao.opticCableListTotal(queryCondition)).thenReturn(2);
        Assert.assertTrue(2==opticCableExport.queryCount(queryCondition));
    }
}