package com.fiberhome.filink.rfid.service.impl.rfid;

import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.dao.rfid.RfidInfoDao;
import com.fiberhome.filink.rfid.req.rfid.InsertRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.QueryRfidInfoReq;
import com.fiberhome.filink.rfid.resp.rfid.RfidInfoResp;
import com.fiberhome.filink.rfid.utils.UtcTimeUtil;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * com.fiberhome.filink.rfid.service.impl.rfid
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/4
 */
@RunWith(MockitoJUnitRunner.class)
public class RfidInfoServiceImplTest {

    @InjectMocks
    private RfidInfoServiceImpl rfidInfoService;
    @Mock
    private RfidInfoDao rfidInfoDao;
    @Mock
    private LogProcess logProcess;
    @Mock
    private SystemLanguageUtil systemLanguageUtil;

    @Mock
    private UtcTimeUtil utcTimeUtil;


    @Test
    public void queryRfidInfo() {
        QueryRfidInfoReq queryRfidInfoReq = new QueryRfidInfoReq();
        List<RfidInfoResp> rfIdInfoRespList = new ArrayList<>();
        when(rfidInfoDao.queryRfidInfo(queryRfidInfoReq)).thenReturn(rfIdInfoRespList);
        Assert.assertEquals(0, rfidInfoService.queryRfidInfo(queryRfidInfoReq).getCode());
    }

    @Test
    public void addRfidInfo() {
        List<InsertRfidInfoReq> insertRfidInfoReqList = new ArrayList<>();
        InsertRfidInfoReq insertRfidInfoReq = new InsertRfidInfoReq();
        insertRfidInfoReq.setRfidId("zz");
        insertRfidInfoReq.setDeviceId("fdsafdsafdsa");
        insertRfidInfoReqList.add(insertRfidInfoReq);
        when(rfidInfoDao.addRfidInfo(insertRfidInfoReqList)).thenReturn(1);
        saveOperatorLog();
        Assert.assertEquals(0, rfidInfoService.addRfidInfo(insertRfidInfoReqList).getCode());
    }

    @Test
    public void deleteRfidInfo() {
        Set<String> rfidCodeList = new HashSet<>();
        rfidCodeList.add("zz");
        when(rfidInfoDao.deleteRfidInfo("zz","1",new Date())).thenReturn(1);
        saveOperatorLog();
        Assert.assertEquals(0, rfidInfoService.deleteRfidInfo(rfidCodeList).getCode());
    }

    @Test
    public void checkRfidCodeListIsExist() {
        Set<String> rfidCodeList = new HashSet<>();
        rfidCodeList.add("zz");
        List<String> rfidCodeRespList = new ArrayList<>();
        rfidCodeRespList.add("cc");
        when(rfidInfoDao.queryRfidInfoByRfidCode(any())).thenReturn(rfidCodeRespList);
        Assert.assertTrue(rfidInfoService.checkRfidCodeListIsExist(rfidCodeList));
        when(rfidInfoDao.queryRfidInfoByRfidCode(any())).thenReturn(null);
        Assert.assertFalse(rfidInfoService.checkRfidCodeListIsExist(rfidCodeList));
    }

    @Test
    public void changeLabel() {
        rfidInfoService.changeLabel("zz", "cc");
    }

    @Test
    public void deleteRfidInfoByDeviceId(){
        when(rfidInfoDao.deleteRfidInfoByDeviceId(any())).thenReturn(0);
        Assert.assertEquals(0,rfidInfoService.deleteRfidInfoByDeviceId("fdsafdsafd"));
    }

    /**
     * 日志
     */
    public void saveOperatorLog() {
        AddLogBean addLogBean = new AddLogBean();
        addLogBean.setTableName("ww");
        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(addLogBean);
        when(systemLanguageUtil.querySystemLanguage()).thenReturn("CN");
        when(logProcess.addOperateLogBatchInfoToCall(any(), any())).thenReturn(new Result());
    }
}