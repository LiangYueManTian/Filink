package com.fiberhome.filink.rfid.service.impl.rfid;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableSectionInfo;
import com.fiberhome.filink.rfid.bean.rfid.OpticCableSectionRfidInfo;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableSectionInfoDao;
import com.fiberhome.filink.rfid.dao.rfid.OpticCableSectionRfidInfoDao;
import com.fiberhome.filink.rfid.req.opticcable.OpticCableSectionInfoReq;
import com.fiberhome.filink.rfid.req.rfid.DeleteOpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.OpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.UpdateOpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.app.OpticCableSectionRfidInfoReqApp;
import com.fiberhome.filink.rfid.req.rfid.app.UploadOpticCableSectionRfidInfoReqApp;
import com.fiberhome.filink.rfid.resp.rfid.OpticCableSectionRfidInfoResp;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * com.fiberhome.filink.rfid.service.impl.rfid
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/3
 */
@RunWith(MockitoJUnitRunner.class)
public class OpticCableSectionRfidInfoServiceImplTest {

    @InjectMocks
    private OpticCableSectionRfidInfoServiceImpl opticCableSectionRfidInfoService;
    @Mock
    private OpticCableSectionRfidInfoDao opticCableSectionRfidInfoDao;
    @Mock
    private OpticCableSectionInfoDao opticCableSectionInfoDao;
    /**
     * 日志api
     */
    @Mock
    private LogProcess logProcess;
    /**
     * 设施api
     */
    @Mock
    private DeviceFeign deviceFeign;
    /**
     * 远程调用SystemLanguage服务
     */
    @Mock
    private SystemLanguageUtil systemLanguageUtil;


    @Test
    public void opticCableSectionRfidInfoById() {
        QueryCondition<OpticCableSectionRfidInfoReq> queryCondition = new QueryCondition<>();
        when(opticCableSectionRfidInfoDao.opticCableSectionById(queryCondition)).thenReturn(new ArrayList<>());
        Assert.assertEquals(0, opticCableSectionRfidInfoService.opticCableSectionRfidInfoById(queryCondition).getCode());

    }

    @Test
    public void queryOpticCableSectionRfidInfo() {
        OpticCableSectionRfidInfoReqApp queryCondition = new OpticCableSectionRfidInfoReqApp();
        when(opticCableSectionRfidInfoDao.queryOpticCableSectionRfidInfo(queryCondition)).thenReturn(new ArrayList<>());
        Assert.assertEquals(0, opticCableSectionRfidInfoService.queryOpticCableSectionRfidInfo(queryCondition).getCode());
    }

    @Test
    public void uploadOpticCableSectionRfidInfo() {
        UploadOpticCableSectionRfidInfoReqApp uploadOpticCableSectionRfidInfoReqApp = new UploadOpticCableSectionRfidInfoReqApp();
        OpticCableSectionRfidInfo opticCableSectionRfidInfo = new OpticCableSectionRfidInfo();
        opticCableSectionRfidInfo.setOpticStatusId("zz");
        opticCableSectionRfidInfo.setRfidCode("jj");
        List<OpticCableSectionRfidInfo> segmentGISList  = new ArrayList<>();
        segmentGISList.add(opticCableSectionRfidInfo);
        uploadOpticCableSectionRfidInfoReqApp.setSegmentGISList(segmentGISList);

        when(opticCableSectionRfidInfoDao.checkRfidCodeListIsExist(any())).thenReturn(new ArrayList<>());

        uploadOpticCableSectionRfidInfoReqApp.setUploadType("0");
        saveOperatorLog();
        when(opticCableSectionRfidInfoDao.addOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp)).thenReturn(1);
        Assert.assertEquals(0, opticCableSectionRfidInfoService.uploadOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp).getCode());

        uploadOpticCableSectionRfidInfoReqApp.setUploadType("1");
        saveOperatorLog();
        when(opticCableSectionRfidInfoDao.deleteOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp)).thenReturn(1);
        Assert.assertEquals(0, opticCableSectionRfidInfoService.uploadOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp).getCode());

        uploadOpticCableSectionRfidInfoReqApp.setUploadType("2");
        saveOperatorLog();
        when(opticCableSectionRfidInfoDao.updateOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp)).thenReturn(1);
        Assert.assertEquals(0, opticCableSectionRfidInfoService.uploadOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp).getCode());

        opticCableSectionRfidInfo.setOpticStatusId("CC");
        segmentGISList.add(opticCableSectionRfidInfo);
        uploadOpticCableSectionRfidInfoReqApp.setSegmentGISList(segmentGISList);
        when(opticCableSectionRfidInfoDao.updateOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp)).thenReturn(0);
        Assert.assertEquals(150000, opticCableSectionRfidInfoService.uploadOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp).getCode());
        List<String> rfIdCodeList = new ArrayList<>();
        rfIdCodeList.add("cc");
        when(opticCableSectionRfidInfoDao.checkRfidCodeListIsExist(uploadOpticCableSectionRfidInfoReqApp.getSegmentGISList().get(0).getRfidCode())).thenReturn(rfIdCodeList);
        Assert.assertEquals(1500106, opticCableSectionRfidInfoService.uploadOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp).getCode());



    }

    @Test
    public void queryOpticCableSectionRfidInfoByOpticCableSectionId() {
        when(opticCableSectionRfidInfoDao.queryOpticCableSectionRfidInfoByOpticCableSectionId(any())).thenReturn(new ArrayList<>());
        Assert.assertEquals(0, opticCableSectionRfidInfoService.queryOpticCableSectionRfidInfoByOpticCableSectionId("zz").getCode());
    }

    @Test
    public void queryOpticCableSectionRfidInfoByOpticCableId() {
        String opticCableId = "zz";

        OpticCableSectionInfoReq opticCableSectionInfoReq = new OpticCableSectionInfoReq();
        opticCableSectionInfoReq.setBelongOpticCableId(opticCableId);
        //模拟光缆数据为空
        when(opticCableSectionInfoDao.queryOpticCableSectionInfoByOpticCableId(opticCableSectionInfoReq)).thenReturn(null);
        Assert.assertEquals(0, opticCableSectionRfidInfoService.queryOpticCableSectionRfidInfoByOpticCableId("zz").getCode());

        OpticCableSectionInfo opticCableSectionInfo =new OpticCableSectionInfo();
        opticCableSectionInfo.setOpticCableSectionId(opticCableId);
        opticCableSectionInfo.setStartNode("zz");
        opticCableSectionInfo.setTerminationNode("cc");
        List<OpticCableSectionInfo> opticCableSectionInfoList = new ArrayList<>();
        opticCableSectionInfoList.add(opticCableSectionInfo);
        when(opticCableSectionInfoDao.queryOpticCableSectionInfoByOpticCableId(any())).thenReturn(opticCableSectionInfoList);

        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setPositionBase("66,66");
        List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
        deviceInfoDtoList.add(deviceInfoDto);
        when(deviceFeign.getDeviceByIds(any())).thenReturn(deviceInfoDtoList);

        OpticCableSectionRfidInfoResp opticCableSectionRfidInfoResp = new OpticCableSectionRfidInfoResp();
        opticCableSectionRfidInfoResp.setOpticStatusId("xx");
        opticCableSectionRfidInfoResp.setGisSort(1L);
        List<OpticCableSectionRfidInfoResp> opticCableSectionRfIdInfoRespList = new ArrayList<>();
        opticCableSectionRfIdInfoRespList.add(opticCableSectionRfidInfoResp);
        when(opticCableSectionRfidInfoDao.queryOpticCableSectionRfidInfoByOpticCableSectionId(any())).thenReturn(opticCableSectionRfIdInfoRespList);

        Assert.assertEquals(0, opticCableSectionRfidInfoService.queryOpticCableSectionRfidInfoByOpticCableId("CC").getCode());



    }

    @Test
    public void updateOpticCableSectionRfidInfoPositionById() {
        List<UpdateOpticCableSectionRfidInfoReq> updateOpticCableSectionRfidInfoReqList = new ArrayList<>();
        UpdateOpticCableSectionRfidInfoReq updateOpticCableSectionRfidInfoReq = new UpdateOpticCableSectionRfidInfoReq();
        updateOpticCableSectionRfidInfoReq.setOpticStatusId("zz");
        updateOpticCableSectionRfidInfoReqList.add(updateOpticCableSectionRfidInfoReq);
        when(opticCableSectionRfidInfoDao.updateOpticCableSectionRfidInfoPositionById(any())).thenReturn(1);
        Assert.assertEquals(0, opticCableSectionRfidInfoService.updateOpticCableSectionRfidInfoPositionById(updateOpticCableSectionRfidInfoReqList).getCode());
    }

    @Test
    public void deleteOpticCableSectionRfidInfoById() {
        DeleteOpticCableSectionRfidInfoReq deleteOpticCableSectionRfidInfoReq = new DeleteOpticCableSectionRfidInfoReq();
        Assert.assertEquals(150000, opticCableSectionRfidInfoService.deleteOpticCableSectionRfidInfoById(deleteOpticCableSectionRfidInfoReq).getCode());
        Set<String> opticStatusIdList  = new HashSet<>();
        opticStatusIdList.add("zz");
        deleteOpticCableSectionRfidInfoReq.setOpticStatusIdList(opticStatusIdList);
        List<OpticCableSectionRfidInfo> opticCableSectionRfidInfoList = new ArrayList<>();
        when(opticCableSectionRfidInfoDao.selectBatchIds(deleteOpticCableSectionRfidInfoReq.getOpticStatusIdList())).thenReturn(opticCableSectionRfidInfoList);
        Assert.assertEquals(1508103, opticCableSectionRfidInfoService.deleteOpticCableSectionRfidInfoById(deleteOpticCableSectionRfidInfoReq).getCode());


        OpticCableSectionRfidInfo opticCableSectionRfidInfo = new OpticCableSectionRfidInfo();
        opticCableSectionRfidInfo.setRfidCode("xx");
        opticCableSectionRfidInfo.setOpticStatusId("cc");
        List<OpticCableSectionRfidInfo> opticCableSectionRfidInfoList1 = new ArrayList<>();
        opticCableSectionRfidInfoList1.add(opticCableSectionRfidInfo);

        when(opticCableSectionRfidInfoDao.selectBatchIds(any())).thenReturn(opticCableSectionRfidInfoList1);
        when(opticCableSectionRfidInfoDao.deleteBatchIds(any())).thenReturn(1);
        saveOperatorLog();
        Assert.assertEquals(0, opticCableSectionRfidInfoService.deleteOpticCableSectionRfidInfoById(deleteOpticCableSectionRfidInfoReq).getCode());
    }


    public void saveOperatorLog() {
        AddLogBean addLogBean = new AddLogBean();
        addLogBean.setTableName("ww");
        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(addLogBean);
        when(systemLanguageUtil.querySystemLanguage()).thenReturn("CN");
        when(logProcess.addOperateLogBatchInfoToCall(any(), any())).thenReturn(new Result());
    }
}