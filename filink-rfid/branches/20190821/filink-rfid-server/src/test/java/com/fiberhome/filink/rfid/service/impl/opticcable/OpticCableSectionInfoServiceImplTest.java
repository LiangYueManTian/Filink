package com.fiberhome.filink.rfid.service.impl.opticcable;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.AreaInfoForeignDto;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.mysql.MpQueryHelper;
import com.fiberhome.filink.rfid.bean.facility.BaseInfoBean;
import com.fiberhome.filink.rfid.bean.fibercore.PortCableCoreInfo;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableSectionInfo;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.dao.fibercore.CoreCoreInfoDao;
import com.fiberhome.filink.rfid.dao.fibercore.PortCableCoreInfoDao;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableInfoDao;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableSectionInfoDao;
import com.fiberhome.filink.rfid.dao.rfid.OpticCableSectionRfidInfoDao;
import com.fiberhome.filink.rfid.dao.template.TemplateDao;
import com.fiberhome.filink.rfid.export.opticcablesection.OpticCableSectionExport;
import com.fiberhome.filink.rfid.req.fibercore.CoreCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.PortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.OpticCableSectionInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.app.OperatorOpticCableSectionInfoReqForApp;
import com.fiberhome.filink.rfid.req.opticcable.app.OpticCableSectionInfoReqForApp;
import com.fiberhome.filink.rfid.req.rfid.app.UploadOpticCableSectionRfidInfoReqApp;
import com.fiberhome.filink.rfid.resp.fibercore.CoreCoreInfoResp;
import com.fiberhome.filink.rfid.resp.opticcable.OpticCableSectionInfoResp;
import com.fiberhome.filink.rfid.resp.opticcable.app.OpticCableSectionInfoRespForApp;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * ?????????????????? ?????????????????????
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/1
 */
@RunWith(MockitoJUnitRunner.class)
public class OpticCableSectionInfoServiceImplTest {
    /**
     * ????????????
     */
    @InjectMocks
    private OpticCableSectionInfoServiceImpl opticCableSectionInfoService;
    /**
     * Mock OpticCableInfoDao
     */
    @Mock
    private OpticCableInfoDao opticCableInfoDao;
    /**
     * Mock OpticCableSectionInfoDao
     */
    @Mock
    private OpticCableSectionInfoDao opticCableSectionInfoDao;
    /**
     * Mock OpticCableSectionInfoDao
     */
    @Mock
    private CoreCoreInfoDao coreCoreInfoDao;
    /**
     * Mock OpticCableSectionInfoDao
     */
    @Mock
    private PortCableCoreInfoDao portCableCoreInfoDao;
    /**
     * Mock OpticCableSectionInfoDao
     */
    @Mock
    private OpticCableSectionRfidInfoDao opticCableSectionRfidInfoDao;
    /**
     * Mock OpticCableSectionInfoDao
     */
    @Mock
    private OpticCableSectionExport opticCableSectionExport;
    /**
     * Mock DeviceFeign
     */
    @Mock
    private DeviceFeign deviceFeign;
    /**
     * Mock AreaFeign
     */
    @Mock
    private AreaFeign areaFeign;
    /**
     * Mock UserFeign
     */
    @Mock
    private UserFeign userFeign;
    /**
     * Mock LogProcess
     */
    @Mock
    private LogProcess logProcess;
    /**
     * Mock LogProcess
     */
    @Mock
    private TemplateDao templateDao;
    /**
     * Mock SystemLanguageUtil
     */
    @Mock
    private SystemLanguageUtil systemLanguageUtil;
    /**
     * ????????????????????????
     */
    private MpQueryHelper mpQueryHelper = spy(MpQueryHelper.class);

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(opticCableSectionInfoService, "exportServerName", "filink-rfid-server");
        ReflectionTestUtils.setField(opticCableSectionInfoService, "maxExportDataSize", 10000);
    }


    /**
     * ???????????????????????????test
     */
    @Test
    public void checkIsPermissionDeviceByDeviceIdList() {
        List<String> deviceIdList = Lists.newArrayList();
        deviceIdList.add("1");
        List<DeviceInfoDto> de = Lists.newArrayList();
        DeviceInfoDto infoDto = new DeviceInfoDto();
        infoDto.setDeviceType("060");

        de.add(infoDto);
        when(deviceFeign.getDeviceByIds(any())).thenReturn(null);
        opticCableSectionInfoService.checkIsPermissionDeviceByDeviceIdList(deviceIdList);
    }

    /**
     * ?????????????????????
     */
    @Test
    public void selectOpticCableSection() {
        QueryCondition<OpticCableSectionInfoReq> queryCondition = new QueryCondition<>();
        OpticCableSectionInfoReq req = new OpticCableSectionInfoReq();
        queryCondition.setBizCondition(req);

        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);
        //?????????????????????
        List<OpticCableSectionInfoResp> list = new ArrayList<>();
        OpticCableSectionInfoResp opticCableSectionInfoResp = new OpticCableSectionInfoResp();
        opticCableSectionInfoResp.setAreaId("area");
        opticCableSectionInfoResp.setStartNode("startNode");
        opticCableSectionInfoResp.setTerminationNode("terminationNode");
        list.add(opticCableSectionInfoResp);

        areaNameByID(opticCableSectionInfoResp);
        deviceNameByID(opticCableSectionInfoResp);
        //????????????
        List<String> userIds = new ArrayList<>();
        userIds.add("1");

        //????????????
        User user = new User();
        user.setId("1");
        Department department = new Department();
        department.setId("1");
        List<String> areaId = new ArrayList<>();
        areaId.add("1");
        department.setAreaIdList(areaId);
        Role role = new Role();
        List<RoleDeviceType> roleDeviceTypeList = new ArrayList<>();
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setDeviceTypeId("1");
        roleDeviceType.setRoleId("1");
        roleDeviceType.setId("1");
        roleDeviceTypeList.add(roleDeviceType);
        role.setId("1");
        role.setRoleDevicetypeList(roleDeviceTypeList);
        user.setDepartment(department);
        user.setRole(role);
        List<User> userList = new ArrayList<>();
        userList.add(user);

        when(userFeign.queryUserByIdList(any(List.class))).thenReturn(userList);
        when(opticCableSectionInfoDao.selectOpticCableSection(queryCondition)).thenReturn(list);
        when(opticCableSectionInfoDao.opticCableSectionByIdTotal(queryCondition)).thenReturn(1);

        Assert.assertEquals(list, opticCableSectionInfoService.selectOpticCableSection(queryCondition).getData());
        opticCableSectionInfoService.getOpticCableSectionInfoReq(queryCondition, "2");
    }

    @Test
    public void opticCableSectionByIdForTopology() {
        OpticCableSectionInfoReq opticCableSectionInfoReq = new OpticCableSectionInfoReq();
        Set<String> setArea = new HashSet<>();
        setArea.add("area");
        opticCableSectionInfoReq.setPermissionAreaIds(setArea);
        opticCableSectionInfoReq.setOpticCableSectionId("zz");
        opticCableSectionInfoReq.setStartNode("startNode");
        opticCableSectionInfoReq.setTerminationNode("terminationNode");
        opticCableSectionInfoReq.setStartNodeDeviceType("startNodeDeviceType");
        opticCableSectionInfoReq.setTerminationNodeDeviceType("terminationNodeDeviceType");


        QueryCondition<OpticCableSectionInfoReq> queryCondition = new QueryCondition<>();
        queryCondition.setBizCondition(opticCableSectionInfoReq);

        getPermissionsInfo();
        //???????????????
        //????????????????????????
        OpticCableSectionInfoResp opticCableSectionInfoResp = new OpticCableSectionInfoResp();
        opticCableSectionInfoResp.setOpticCableSectionId("CC");
        opticCableSectionInfoResp.setStartNode("startNode");
        opticCableSectionInfoResp.setStartNodeDeviceType("030");
        opticCableSectionInfoResp.setTerminationNode("terminationNode");
        opticCableSectionInfoResp.setTerminationNodeDeviceType("030");
        //????????????????????????
        OpticCableSectionInfoResp opticCableSectionInfoResp1 = new OpticCableSectionInfoResp();
        opticCableSectionInfoResp1.setOpticCableSectionId("CC");
        opticCableSectionInfoResp1.setStartNode("startNode");
        opticCableSectionInfoResp1.setStartNodeDeviceType("029");
        opticCableSectionInfoResp1.setTerminationNode("terminationNode");
        opticCableSectionInfoResp1.setTerminationNodeDeviceType("029");


        List<OpticCableSectionInfoResp> opticCableSectionInfoRespList = new ArrayList<>();
        opticCableSectionInfoRespList.add(opticCableSectionInfoResp);
        opticCableSectionInfoRespList.add(opticCableSectionInfoResp1);

        when(opticCableSectionInfoDao.opticCableSectionByIdForTopology(opticCableSectionInfoReq)).thenReturn(opticCableSectionInfoRespList);
        Assert.assertEquals(0, opticCableSectionInfoService.opticCableSectionByIdForTopology(opticCableSectionInfoReq).getCode());

    }

    @Test
    public void selectOpticCableSectionByDeviceId() {
        String deviceId = "deviceId";
        List<String> deviceIds = new ArrayList<>();
        deviceIds.add(deviceId);
        List<OpticCableSectionInfoResp> list = new ArrayList<>();
        when(opticCableSectionInfoDao.opticCableSectionByDevice(deviceIds)).thenReturn(list);
        Assert.assertEquals(0, opticCableSectionInfoService.selectOpticCableSectionByDeviceId(deviceId).getCode());
        OpticCableSectionInfoResp opticCableSectionInfoResp = new OpticCableSectionInfoResp();
        opticCableSectionInfoResp.setOpticCableSectionId("zz");
        list.add(opticCableSectionInfoResp);
        when(opticCableSectionInfoDao.opticCableSectionByDevice(deviceIds)).thenReturn(list);
        Assert.assertEquals(0, opticCableSectionInfoService.selectOpticCableSectionByDeviceId(deviceId).getCode());

    }

    @Test
    public void deleteOpticCableSectionByOpticCableSectionId() {
        String opticCableSectionId = "zz";
        when(opticCableSectionInfoDao.selectOne(any())).thenReturn(null);
        Assert.assertEquals(150211, opticCableSectionInfoService.deleteOpticCableSectionByOpticCableSectionId(opticCableSectionId).getCode());
        OpticCableSectionInfo opticCableSectionInfo = new OpticCableSectionInfo();
        opticCableSectionInfo.setOpticCableSectionId(opticCableSectionId);
        opticCableSectionInfo.setIsDeleted("0");
        when(opticCableSectionInfoDao.selectOne(any())).thenReturn(opticCableSectionInfo);
        when(opticCableSectionInfoDao.selectById(opticCableSectionId)).thenReturn(opticCableSectionInfo);
        when(coreCoreInfoDao.queryCoreCoreInfo(any(CoreCoreInfoReq.class))).thenReturn(null);
        when(portCableCoreInfoDao.getPortCableCoreInfo(any(PortCableCoreInfoReq.class))).thenReturn(null);
        when(opticCableSectionInfoDao.deleteOpticCableSectionByOpticCableSectionId(any(OpticCableSectionInfoReq.class))).thenReturn(1);
        when(opticCableSectionRfidInfoDao.deleteOpticCableSectionRfidInfo(any(UploadOpticCableSectionRfidInfoReqApp.class))).thenReturn(1);
        //??????
        AddLogBean addLogBean = new AddLogBean();
        addLogBean.setTableName("ww");
        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(addLogBean);
        when(systemLanguageUtil.querySystemLanguage()).thenReturn("CN");
        when(logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE)).thenReturn(new Result());


        Assert.assertEquals(0, opticCableSectionInfoService.deleteOpticCableSectionByOpticCableSectionId(opticCableSectionId).getCode());
    }

    @Test
    public void queryOpticCableSectionListForApp() {
        OpticCableSectionInfoReqForApp opticCableSectionInfoReqForApp = new OpticCableSectionInfoReqForApp();
        opticCableSectionInfoReqForApp.setOpticCableSectionId("zz");
        opticCableSectionInfoReqForApp.setAreaId("WH");

        List<OpticCableSectionInfoRespForApp> list = new ArrayList<>();
        OpticCableSectionInfoRespForApp opticCableSectionInfoRespForApp = new OpticCableSectionInfoRespForApp();
        opticCableSectionInfoRespForApp.setOpticCableSectionId("cc");
        opticCableSectionInfoRespForApp.setAreaId("WH");
        list.add(opticCableSectionInfoRespForApp);

        when(opticCableSectionInfoDao.queryOpticCableSectionListForApp(opticCableSectionInfoReqForApp)).thenReturn(list);
        areaNameByID(opticCableSectionInfoReqForApp);
        deviceNameByID(opticCableSectionInfoReqForApp);
        gainDeviceRfidById();
        Assert.assertEquals(0, opticCableSectionInfoService.queryOpticCableSectionListForApp(opticCableSectionInfoReqForApp).getCode());
    }

    @Test
    public void uploadOpticCableSectionInfoForApp() {
        //?????????????????????
        OperatorOpticCableSectionInfoReqForApp operatorOpticCableSectionInfoReqForApp = new OperatorOpticCableSectionInfoReqForApp();
        Assert.assertEquals(150000, opticCableSectionInfoService.uploadOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp).getCode());
        //????????????????????????????????????
        operatorOpticCableSectionInfoReqForApp.setUploadType("0");
        operatorOpticCableSectionInfoReqForApp.setStartNode("zz");
        operatorOpticCableSectionInfoReqForApp.setTerminationNode("zz");
        operatorOpticCableSectionInfoReqForApp.setOpticCableSectionName("xx");
        Assert.assertEquals(150000, opticCableSectionInfoService.uploadOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp).getCode());

        operatorOpticCableSectionInfoReqForApp.setTerminationNode("ZZ");
        operatorOpticCableSectionInfoReqForApp.setCoreNum("50");
        operatorOpticCableSectionInfoReqForApp.setBelongOpticCableId("cc");
        //????????????????????????
        when(opticCableInfoDao.queryOpticCableByCoreNum(operatorOpticCableSectionInfoReqForApp.getBelongOpticCableId())).thenReturn(null);
        Assert.assertEquals(150203, opticCableSectionInfoService.uploadOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp).getCode());

        //??????????????????????????????????????????????????????
        when(opticCableInfoDao.queryOpticCableByCoreNum(operatorOpticCableSectionInfoReqForApp.getBelongOpticCableId())).thenReturn(30);
        Assert.assertEquals(150205, opticCableSectionInfoService.uploadOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp).getCode());
        //?????????????????????????????????
        when(opticCableInfoDao.queryOpticCableByCoreNum(operatorOpticCableSectionInfoReqForApp.getBelongOpticCableId())).thenReturn(100);
        when(deviceFeign.getDeviceByIds(any())).thenReturn(null);
        Assert.assertEquals(150112, opticCableSectionInfoService.uploadOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp).getCode());

        Set<String> deviceIds = new HashSet<>();
        deviceIds.add(operatorOpticCableSectionInfoReqForApp.getStartNode());
        deviceIds.add(operatorOpticCableSectionInfoReqForApp.getTerminationNode());
        String[] deviceIdArray = new String[deviceIds.size()];
        deviceIds.toArray(deviceIdArray);
        List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setDeviceId("cc");
        deviceInfoDtoList.add(deviceInfoDto);
        when(deviceFeign.getDeviceByIds(deviceIdArray)).thenReturn(deviceInfoDtoList);
        Assert.assertEquals(150111, opticCableSectionInfoService.uploadOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp).getCode());

        DeviceInfoDto deviceInfoDto1 = new DeviceInfoDto();
        deviceInfoDto1.setDeviceId("xx");
        deviceInfoDtoList.add(deviceInfoDto1);

        //??????
        AddLogBean addLogBean = new AddLogBean();
        addLogBean.setTableName("ww");
        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(addLogBean);
        when(systemLanguageUtil.querySystemLanguage()).thenReturn("CN");
        when(logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE)).thenReturn(new Result());

        Assert.assertEquals(150202, opticCableSectionInfoService.uploadOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp).getCode());
        when(opticCableSectionInfoDao.queryOpticCableSectionByName("xx", operatorOpticCableSectionInfoReqForApp.getBelongOpticCableId())).thenReturn(null);

        //??????
        when(opticCableSectionInfoDao.addOpticCableSectionInfoForApp(any(OperatorOpticCableSectionInfoReqForApp.class))).thenReturn(1);
        Assert.assertEquals(0, opticCableSectionInfoService.uploadOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp).getCode());
        //??????
        operatorOpticCableSectionInfoReqForApp.setUploadType("2");
        when(opticCableSectionInfoDao.updateOpticCableSectionInfoForApp(any(OperatorOpticCableSectionInfoReqForApp.class))).thenReturn(1);
        Assert.assertEquals(0, opticCableSectionInfoService.uploadOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp).getCode());
        //??????
        operatorOpticCableSectionInfoReqForApp.setUploadType("1");
        when(opticCableSectionInfoDao.deleteOpticCableSectionInfoForApp(any(OperatorOpticCableSectionInfoReqForApp.class))).thenReturn(1);
        Assert.assertEquals(0, opticCableSectionInfoService.uploadOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp).getCode());

        when(opticCableSectionInfoDao.deleteOpticCableSectionInfoForApp(any(OperatorOpticCableSectionInfoReqForApp.class))).thenReturn(0);
        Assert.assertEquals(150202, opticCableSectionInfoService.uploadOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp).getCode());

    }

    @Test
    public void queryDeviceInfoListByOpticCableId() {
        //????????????
        Assert.assertEquals(150000, opticCableSectionInfoService.queryDeviceInfoListByOpticCableId(null).getCode());

        String opticCableId = "zz";
        getPermissionsInfo();
        List<OpticCableSectionInfo> opticCableSectionInfoList = new ArrayList<>();

        //????????????????????????
        OpticCableSectionInfo opticCableSectionInfo = new OpticCableSectionInfo();
        opticCableSectionInfo.setOpticCableSectionId("CC");
        opticCableSectionInfo.setStartNode("startNode");
        opticCableSectionInfo.setStartNodeDeviceType("030");
        opticCableSectionInfo.setTerminationNode("terminationNode");
        opticCableSectionInfo.setTerminationNodeDeviceType("030");
        //????????????????????????
        OpticCableSectionInfo opticCableSectionInfo1 = new OpticCableSectionInfo();
        opticCableSectionInfo1.setOpticCableSectionId("CC");
        opticCableSectionInfo1.setStartNode("startNode");
        opticCableSectionInfo1.setStartNodeDeviceType("029");
        opticCableSectionInfo1.setTerminationNode("terminationNode");
        opticCableSectionInfo1.setTerminationNodeDeviceType("029");
        opticCableSectionInfoList.add(opticCableSectionInfo);
        opticCableSectionInfoList.add(opticCableSectionInfo1);

        when(opticCableSectionInfoDao.queryOpticCableSectionInfoByOpticCableId(any(OpticCableSectionInfoReq.class))).thenReturn(opticCableSectionInfoList);
        List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
        when(deviceFeign.getDeviceByIds(any())).thenReturn(deviceInfoDtoList);
        Assert.assertEquals(0, opticCableSectionInfoService.queryDeviceInfoListByOpticCableId(opticCableId).getCode());
        deviceInfoDtoList.add(new DeviceInfoDto());
        when(deviceFeign.getDeviceByIds(any())).thenReturn(deviceInfoDtoList);
        Assert.assertEquals(0, opticCableSectionInfoService.queryDeviceInfoListByOpticCableId(opticCableId).getCode());


    }

    @Test
    public void exportOpticCableSectionList() {
        ExportDto exportDto = new ExportDto();
        //FilinkExportNoDataException
        when(opticCableSectionExport.insertTask(exportDto, "filink-rfid-server",
                I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_LIST))).thenThrow(new FilinkExportNoDataException());
        try {
            Assert.assertEquals(1500102, opticCableSectionInfoService.exportOpticCableSectionList(exportDto).getCode());
        } catch (Exception e) {
        }

    }

    @Test
    public void exportOpticCableSectionList1() {
        ExportDto exportDto = new ExportDto();
        //FilinkExportDataTooLargeException
        when(opticCableSectionExport.insertTask(exportDto, "filink-rfid-server",
                I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_LIST))).thenThrow(new FilinkExportDataTooLargeException("x"));
        try {
            Assert.assertEquals(150008, opticCableSectionInfoService.exportOpticCableSectionList(exportDto).getCode());
        } catch (Exception e) {
        }
    }

    @Test
    public void exportOpticCableSectionList2() {
        ExportDto exportDto = new ExportDto();
        //FilinkExportTaskNumTooBigException
        when(opticCableSectionExport.insertTask(exportDto, "filink-rfid-server",
                I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_LIST))).thenThrow(new FilinkExportTaskNumTooBigException());
        try {
            Assert.assertEquals(1500103, opticCableSectionInfoService.exportOpticCableSectionList(exportDto).getCode());
        } catch (Exception e) {
        }
    }

    @Test
    public void exportOpticCableSectionList3() {
        ExportDto exportDto = new ExportDto();
        //FilinkExportTaskNumTooBigException
        when(opticCableSectionExport.insertTask(exportDto, "filink-rfid-server",
                I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_LIST))).thenThrow(new RuntimeException());
        try {
            Assert.assertEquals(1500104, opticCableSectionInfoService.exportOpticCableSectionList(exportDto).getCode());
        } catch (Exception e) {
        }
    }

    @Test
    public void exportOpticCableSectionList4() {
        ExportDto exportDto = new ExportDto();
        ExportRequestInfo export = new ExportRequestInfo();
        //FilinkExportTaskNumTooBigException
        when(opticCableSectionExport.insertTask(exportDto, "filink-rfid-server",
                I18nUtils.getSystemString(RfIdI18nConstant.OPTIC_CABLE_SECTION_LIST))).thenReturn(export);
        when(logProcess.generateAddLogToCallParam("1")).thenReturn(new AddLogBean());
        try {
            Assert.assertEquals(0, opticCableSectionInfoService.exportOpticCableSectionList(exportDto).getCode());
        } catch (Exception e) {
        }
    }

    @Test
    public void getPermissionsInfo() {
        List<String> userIds = new ArrayList<>();
        userIds.add("");
        User user = new User();
        //??????????????????????????????
        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("wuhan");
        Department department = new Department();
        department.setAreaIdList(areaIdList);
        user.setDepartment(department);
        //????????????????????????????????????
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setDeviceTypeId("030");
        List<RoleDeviceType> roleDevicetypeList = new ArrayList<>();
        roleDevicetypeList.add(roleDeviceType);
        Role role = new Role();
        role.setRoleDevicetypeList(roleDevicetypeList);
        user.setRole(role);
        List<User> userInfoList = new ArrayList<>();
        userInfoList.add(user);
        when(userFeign.queryUserByIdList(any())).thenReturn(userInfoList);
    }

    @Test
    public void getPermissionsInfoForExport() {
        QueryCondition<OpticCableSectionInfoReq> queryCondition = new QueryCondition<>();
        getPermissionsInfo();
        OpticCableSectionInfoReq opticCableSectionInfoReq = new OpticCableSectionInfoReq();
        opticCableSectionInfoReq.setBelongOpticCableId("x");
        queryCondition.setBizCondition(opticCableSectionInfoReq);
        Assert.assertNotNull(opticCableSectionInfoService.getPermissionsInfoForExport(queryCondition));

    }

    @Test
    public void assemblyOpticCableSectionInfoResp() {
    }

    @Test
    public void coreStatisticsCount() throws Exception {
        String opticCableSectionId = "guanglan1";


        List<CoreCoreInfoResp> coreCoreInfoRespList = new ArrayList<>();
        CoreCoreInfoResp coreCoreInfoResp = new CoreCoreInfoResp();
        coreCoreInfoResp.setCoreCoreId("cc");
        coreCoreInfoResp.setResource("guanglan1");
        coreCoreInfoResp.setCableCoreNo("1-1");
        coreCoreInfoResp.setOppositeResource("guanglan1");
        coreCoreInfoResp.setOppositeCableCoreNo("1-2");
        coreCoreInfoRespList.add(coreCoreInfoResp);

        CoreCoreInfoResp coreCoreInfoResp1 = new CoreCoreInfoResp();
        coreCoreInfoResp1.setCoreCoreId("cc");
        coreCoreInfoResp1.setResource("guanglan1");
        coreCoreInfoResp1.setCableCoreNo("1-2");
        coreCoreInfoResp1.setOppositeResource("guanglan2");
        coreCoreInfoResp1.setOppositeCableCoreNo("1-1");
        coreCoreInfoRespList.add(coreCoreInfoResp1);
        when(coreCoreInfoDao.queryCoreCoreInfoByOpticCableId(opticCableSectionId)).thenReturn(coreCoreInfoRespList);

        List<PortCableCoreInfo> portCableCoreInfoList = new ArrayList<>();
        PortCableCoreInfo portCableCoreInfo = new PortCableCoreInfo();
        portCableCoreInfo.setOppositeResource("guanglan1");
        portCableCoreInfo.setOppositeCableCoreNo("1-1");
        portCableCoreInfoList.add(portCableCoreInfo);
        when(portCableCoreInfoDao.getPortCableCoreInfo(any(PortCableCoreInfoReq.class))).thenReturn(portCableCoreInfoList);

        OpticCableSectionInfo cableSectionInfo = new OpticCableSectionInfo();
        cableSectionInfo.setOpticCableSectionId("guanglan1");
        cableSectionInfo.setUsedCoreNum("2");
        when(opticCableSectionInfoDao.updateOpticCableSectionUsedCoreNum(any(OpticCableSectionInfo.class))).thenReturn(1);
        Future<Integer> future = opticCableSectionInfoService.coreStatisticsCount(opticCableSectionId);
        Assert.assertEquals(java.util.Optional.of(1), java.util.Optional.of(future.get()));
    }

    /**
     * ??????????????????????????????
     *
     * @param opticCableSectionInfoResp opticCableSectionInfoResp
     */
    private <T extends OpticCableSectionInfo> void areaNameByID(T opticCableSectionInfoResp) {

        List<String> areaIdList = new ArrayList<>();
        areaIdList.add(opticCableSectionInfoResp.getAreaId());
        List<AreaInfoForeignDto> areaInfoForeignDtoList = new ArrayList<>(areaIdList.size());
        AreaInfoForeignDto areaInfoForeignDto = new AreaInfoForeignDto();
        areaInfoForeignDto.setAreaId(areaIdList.get(0));
        areaInfoForeignDto.setAreaName("wuhan");
        areaInfoForeignDtoList.add(areaInfoForeignDto);
        when(areaFeign.selectAreaInfoByIds(areaIdList)).thenReturn(areaInfoForeignDtoList);
    }

    /**
     * ??????????????????????????????
     *
     * @param opticCableSectionInfoResp opticCableSectionInfoResp
     */
    private <T extends OpticCableSectionInfo> void deviceNameByID(T opticCableSectionInfoResp) {
        //????????????????????????
        Set<String> deviceIdSet = new HashSet<>();
        deviceIdSet.add(opticCableSectionInfoResp.getStartNode());
        deviceIdSet.add(opticCableSectionInfoResp.getTerminationNode());
        String[] deviceIds = deviceIdSet.toArray(new String[deviceIdSet.size()]);
        List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>(deviceIdSet.size());
        for (int i = 0; i < deviceIdSet.size(); i++) {
            DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
            deviceInfoDto.setDeviceId(deviceIds[i]);
            deviceInfoDto.setDeviceName(deviceIds[i] + "name");
            deviceInfoDtoList.add(deviceInfoDto);
        }
        when(deviceFeign.getDeviceByIds(deviceIds)).thenReturn(deviceInfoDtoList);
    }

    private void gainDeviceRfidById() {
        List<BaseInfoBean> realPositions = new ArrayList<>();
        BaseInfoBean baseInfoBean = new BaseInfoBean();
        baseInfoBean.setDeviceId("cc");
        baseInfoBean.setBoxLabel("zz");
        realPositions.add(baseInfoBean);
        when(templateDao.getDeviceRfidById(any())).thenReturn(realPositions);
    }
}