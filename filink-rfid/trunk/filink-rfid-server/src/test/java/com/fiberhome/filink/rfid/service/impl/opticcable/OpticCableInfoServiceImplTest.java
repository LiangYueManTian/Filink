package com.fiberhome.filink.rfid.service.impl.opticcable;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.mysql.MpQueryHelper;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableSectionInfo;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.opticcable.OpticCableConstant;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableInfoDao;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableSectionInfoDao;
import com.fiberhome.filink.rfid.export.opticcable.OpticCableExport;
import com.fiberhome.filink.rfid.req.opticcable.InsertOpticCableInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.OpticCableInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.OpticCableSectionInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.UpdateOpticCableInfoReq;
import com.fiberhome.filink.rfid.resp.opticcable.OpticCableInfoDetail;
import com.fiberhome.filink.rfid.resp.opticcable.OpticCableInfoResp;
import com.fiberhome.filink.rfid.utils.UtcTimeUtil;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * 光缆信息表 服务实现类测试
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/28
 */
@RunWith(MockitoJUnitRunner.class)
public class OpticCableInfoServiceImplTest {

    /**
     * 被测试类
     */
    @InjectMocks
    private OpticCableInfoServiceImpl opticCableInfoService;
    /**
     * Mock FiberOpticsAndCoreStatisticsDao
     */
    @Mock
    private OpticCableInfoDao opticCableInfoDao;
    /**
     * Mock FiberOpticsAndCoreStatisticsDao
     */
    @Mock
    private OpticCableSectionInfoDao opticCableSectionInfoDao;
    /**
     * Mock SystemLanguageUtil
     */
    @Mock
    private SystemLanguageUtil systemLanguageUtil;
    /**
     * 注入光缆列表导出类
     */
    @Mock
    private OpticCableExport opticCableExport;
    /**
     * Mock LogProcess
     */
    @Mock
    private LogProcess logProcess;
    /**
     * 返回结果封装
     */
    private Result resultResp;
    /**
     * 返回工具类
     */
    private ResultUtils resultUtils = spy(ResultUtils.class);
    /**
     * 查询条件解析封装
     */
    private MpQueryHelper mpQueryHelper = spy(MpQueryHelper.class);
    /**
     * 查询条件解析封装
     */
    private NineteenUUIDUtils nineteenUUIDUtils = spy(NineteenUUIDUtils.class);

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(opticCableInfoService, "exportServerName", "filink-rfid-server");
        ReflectionTestUtils.setField(opticCableInfoService, "maxExportDataSize", 10000);
        ReflectionTestUtils.setField(opticCableInfoService, "cableCoreMaxNum", 1152);
    }

    /**
     * 分页查询光缆列表
     */
    @Test
    public void opticCableListByPage() {
        QueryCondition<OpticCableInfoReq> queryCondition = new QueryCondition<>();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);
        when(opticCableInfoDao.opticCableListTotal(queryCondition)).thenReturn(1);
        List<OpticCableInfoResp> list = new ArrayList<>();
        when(opticCableInfoDao.opticCableListByPage(queryCondition)).thenReturn(list);
        Assert.assertEquals(list, opticCableInfoService.opticCableListByPage(queryCondition).getData());

    }

    @Test
    public void addOpticCable() {
        //入参
        InsertOpticCableInfoReq insertOpticCableInfoReq = new InsertOpticCableInfoReq();
        //参数为空
        Assert.assertEquals(150000, opticCableInfoService.addOpticCable(null).getCode());
        insertOpticCableInfoReq.setCoreNum("1000000");
        //超过最大纤芯数
        Assert.assertEquals(150111, opticCableInfoService.addOpticCable(insertOpticCableInfoReq).getCode());

        insertOpticCableInfoReq.setOpticCableId("xx");
        insertOpticCableInfoReq.setOpticCableName("zz");
        insertOpticCableInfoReq.setCoreNum("100");

        OpticCableInfoDetail opticCableInfoDetail = new OpticCableInfoDetail();
        opticCableInfoDetail.setOpticCableId("XX");
        opticCableInfoDetail.setOpticCableName("zz");
        //名称重复
        when(opticCableInfoDao.queryOpticCableByName(insertOpticCableInfoReq.getOpticCableName())).thenReturn(opticCableInfoDetail);
        Assert.assertEquals(150108, opticCableInfoService.addOpticCable(insertOpticCableInfoReq).getCode());
        //日志
        AddLogBean addLogBean = new AddLogBean();
        addLogBean.setTableName("ww");
        opticCableInfoDetail.setOpticCableId("xx");
        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(addLogBean);
        when(opticCableInfoDao.queryOpticCableByName(insertOpticCableInfoReq.getOpticCableName())).thenReturn(opticCableInfoDetail);

        when(opticCableInfoDao.addOpticCable(insertOpticCableInfoReq)).thenReturn(1);
        Assert.assertEquals(0, opticCableInfoService.addOpticCable(insertOpticCableInfoReq).getCode());
    }

    @Test
    public void queryOpticCableById() {
        String id = "zz";
        when(opticCableInfoDao.queryOpticCableById(id)).thenReturn(null);
        Assert.assertEquals(150109, opticCableInfoService.queryOpticCableById(id).getCode());
        OpticCableInfoDetail opticCableInfoDetail = new OpticCableInfoDetail();
        opticCableInfoDetail.setOpticCableId(id);
        when(opticCableInfoDao.queryOpticCableById(id)).thenReturn(opticCableInfoDetail);
        Assert.assertEquals(0, opticCableInfoService.queryOpticCableById(id).getCode());
    }

    @Test
    public void updateOpticCableById() {

        String id = "zz";
        UpdateOpticCableInfoReq updateOpticCableInfoReq = new UpdateOpticCableInfoReq();
        updateOpticCableInfoReq.setOpticCableId(id);
        updateOpticCableInfoReq.setOpticCableName("ZZ");
        //光缆不存在
        when(opticCableInfoDao.queryOpticCableById(id)).thenReturn(null);
        Assert.assertEquals(150109, opticCableInfoService.updateOpticCableById(updateOpticCableInfoReq).getCode());
        updateOpticCableInfoReq.setOpticCableName("zz");
        OpticCableInfoDetail opticCableInfoDetail = new OpticCableInfoDetail();
        opticCableInfoDetail.setOpticCableId("cc");
        opticCableInfoDetail.setOpticCableName("zz");

        when(opticCableInfoDao.queryOpticCableById(id)).thenReturn(opticCableInfoDetail);
        //名称重复
        when(opticCableInfoDao.queryOpticCableByName(opticCableInfoDetail.getOpticCableName())).thenReturn(opticCableInfoDetail);
        Assert.assertEquals(150108, opticCableInfoService.updateOpticCableById(updateOpticCableInfoReq).getCode());


        opticCableInfoDetail.setOpticCableId(id);
        when(opticCableInfoDao.queryOpticCableById(id)).thenReturn(opticCableInfoDetail);

        //日志
        AddLogBean addLogBean = new AddLogBean();
        addLogBean.setTableName("ww");
        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(addLogBean);

        when(systemLanguageUtil.querySystemLanguage()).thenReturn("CN");
        when(logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE)).thenReturn(new Result());
        //成功
        Assert.assertEquals(0, opticCableInfoService.updateOpticCableById(updateOpticCableInfoReq).getCode());

    }

    @Test
    public void deleteOpticCableById() {
        String id = "zz";
        OpticCableInfoDetail opticCableInfoDetail = new OpticCableInfoDetail();
        when(opticCableInfoDao.queryOpticCableById(id)).thenReturn(opticCableInfoDetail);
        //查询光缆段入参
        OpticCableSectionInfoReq opticCableSectionInfoReq = new OpticCableSectionInfoReq();
        opticCableSectionInfoReq.setBelongOpticCableId(id);
        //查询光缆段返回
        List<OpticCableSectionInfo> opticCableSectionInfoList = new ArrayList<>();
//        when(opticCableSectionInfoDao.queryOpticCableSectionInfoByOpticCableId(opticCableSectionInfoReq)).thenReturn(opticCableSectionInfoList);
        when(opticCableInfoDao.updateOpticCableIsDeletedById(id, OpticCableConstant.IS_DELETED, RequestInfoUtils.getUserId(), UtcTimeUtil.getUtcTime())).thenReturn(1);

        //日志
        AddLogBean addLogBean = new AddLogBean();
        addLogBean.setTableName("ww");
        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(addLogBean);
        when(logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE)).thenReturn(new Result());
        //删除成功
        Assert.assertEquals(0, opticCableInfoService.deleteOpticCableById(id).getCode());
    }

    @Test
    public void checkOpticCableName() {
        String id = "zz";
        Assert.assertEquals(true, opticCableInfoService.checkOpticCableName(id, null));
    }

    @Test
    public void getOpticCableListForApp() {
        when(opticCableInfoDao.getOpticCableListForApp()).thenReturn(new ArrayList<>());
        Assert.assertEquals(0, opticCableInfoService.getOpticCableListForApp().getCode());
    }

    @Test
    public void exportOpticCableList() {
        ExportDto exportDto = new ExportDto();
        //FilinkExportNoDataException
        when(opticCableExport.insertTask(exportDto, "filink-rfid-server",
                I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_LIST))).thenThrow(new FilinkExportNoDataException());
        try {
            Assert.assertEquals(1500102, opticCableInfoService.exportOpticCableList(exportDto).getCode());
        } catch (Exception e) {
        }
    }

    /**
     * exportOpticCableList FilinkExportTaskNumTooBigException
     */
    @Test
    public void exportOpticCableList1() {
        ExportDto exportDto = new ExportDto();
        //FilinkExportTaskNumTooBigException
        when(opticCableExport.insertTask(exportDto, "filink-rfid-server",
                I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_LIST))).thenThrow(new FilinkExportTaskNumTooBigException());
        try {
            Assert.assertEquals(1500103, opticCableInfoService.exportOpticCableList(exportDto).getCode());
        } catch (Exception e) {
        }
    }
    /**
     * exportOpticCableList FilinkExportTaskNumTooBigException
     */
    @Test
    public void exportOpticCableList2() {
        ExportDto exportDto = new ExportDto();
        //FilinkExportTaskNumTooBigException
        when(opticCableExport.insertTask(exportDto, "filink-rfid-server",
                I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_LIST))).thenThrow(new FilinkExportDataTooLargeException("xx"));
        try {
            Assert.assertEquals(150008, opticCableInfoService.exportOpticCableList(exportDto).getCode());
        } catch (Exception e) {
        }
    }
    /**
     * exportOpticCableList Exception
     */
    @Test
    public void exportOpticCableList3() {
        ExportDto exportDto = new ExportDto();
        //Exception
        when(opticCableExport.insertTask(exportDto, "filink-rfid-server",
                I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_LIST))).thenThrow(new RuntimeException());
        try {
            Assert.assertEquals(1500104, opticCableInfoService.exportOpticCableList(exportDto).getCode());
        } catch (Exception e) {
        }
    }

    /**
     * exportOpticCableList Exception
     */
    @Test
    public void exportOpticCableList4() {
        ExportDto exportDto = new ExportDto();
        ExportRequestInfo export = new ExportRequestInfo();
        //Exception
        when(opticCableExport.insertTask(exportDto, "filink-rfid-server",
                I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_LIST))).thenReturn(export);
        when(logProcess.generateAddLogToCallParam("1")).thenReturn(new AddLogBean());
        Assert.assertEquals(0, opticCableInfoService.exportOpticCableList(exportDto).getCode());

    }
}