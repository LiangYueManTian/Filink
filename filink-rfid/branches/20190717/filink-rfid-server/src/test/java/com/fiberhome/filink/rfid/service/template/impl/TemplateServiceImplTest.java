package com.fiberhome.filink.rfid.service.template.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.rfid.bean.facility.PortInfoBean;
import com.fiberhome.filink.rfid.bean.fibercore.PortCableCoreInfo;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableSectionInfo;
import com.fiberhome.filink.rfid.bean.template.PortCableCoreCondition;
import com.fiberhome.filink.rfid.bean.template.RealPosition;
import com.fiberhome.filink.rfid.constant.RfIdResultCodeConstant;
import com.fiberhome.filink.rfid.dao.fibercore.PortCableCoreInfoDao;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableSectionInfoDao;
import com.fiberhome.filink.rfid.dao.template.FacilityDao;
import com.fiberhome.filink.rfid.dao.template.TemplateDao;
import com.fiberhome.filink.rfid.exception.BizException;
import com.fiberhome.filink.rfid.req.fibercore.PortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.template.PortCableReqDto;
import com.fiberhome.filink.rfid.req.template.PortInfoReqDto;
import com.fiberhome.filink.rfid.req.template.RealPortReqDto;
import com.fiberhome.filink.rfid.req.template.RealReqDto;
import com.fiberhome.filink.rfid.req.template.TemplateReqDto;
import com.fiberhome.filink.rfid.resp.fibercore.JumpFiberInfoResp;
import com.fiberhome.filink.rfid.resp.template.RealRspDto;
import com.fiberhome.filink.rfid.resp.template.TemplateRspDto;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import com.google.common.collect.Lists;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * ???????????????
 *
 * @author liyj
 * @date 2019/7/5
 */
@RunWith(JMockit.class)
public class TemplateServiceImplTest {

    /**
     * templateService
     */
    @Tested
    private TemplateServiceImpl templateService;

    /**
     * templateDao
     */
    @Injectable
    private TemplateDao templateDao;


    /**
     * templateHelper ?????????
     */
    @Injectable
    private TemplateHelper templateHelper;
    /**
     * ??????????????????
     */
    @Injectable
    private PortCableCoreInfoDao portCableCoreInfoDao;
    /**
     * facilityDao
     */
    @Injectable
    private FacilityDao facilityDao;
    /**
     * ??????????????????API
     */
    @Injectable
    private UserFeign userFeign;
    /**
     * ???????????? deviceFeign
     */
    @Injectable
    private DeviceFeign deviceFeign;
    /**
     * ????????????
     */
    @Injectable
    private Integer boxWidth;
    /**
     *
     */
    @Injectable
    private OpticCableSectionInfoDao opticCableSectionInfoDao;
    /**
     * ????????????
     */
    @Injectable
    private Integer boxHeight;
    /**
     * templateDao
     */
    private TemplateReqDto templateReqDto;
    /**
     * templateVos
     */
    private List<TemplateRspDto> templateVOS = Lists.newArrayList();

    /**
     * ??????????????? boxReal
     */
    private List<RealRspDto> boxRealPosition = Lists.newArrayList();
    /**
     * ??????????????? frameReal
     */
    private List<RealRspDto> frameRealPosition = Lists.newArrayList();
    /**
     * ??????????????? discReal
     */
    private List<RealRspDto> discRealPosition = Lists.newArrayList();
    /**
     * ??????????????? portReal
     */
    private List<RealRspDto> portRealPosition = Lists.newArrayList();
    /**
     * ????????????
     */
    private RealRspDto boxs = new RealRspDto();
    /**
     * ????????????
     */
    private RealRspDto frames = new RealRspDto();
    /**
     * ????????????
     */
    private RealRspDto discs = new RealRspDto();
    /**
     * ???????????????
     */
    private RealRspDto port = new RealRspDto();

    /**
     * ???????????????
     *
     * @throws Exception exception
     */
    @Before
    public void setUp() throws Exception {


        TemplateRspDto box = new TemplateRspDto();
        box.setId("boxId");
        box.setCol(1);
        box.setRow(2);
        box.setName("box");
        box.setTemplateType(1);
        box.setPutState(1);
        box.setReversible(1);
        box.setChildTemplateId("frameId,frameId");
        templateVOS.add(box);

        TemplateRspDto frame = new TemplateRspDto();
        frame.setId("frameId");
        frame.setCol(1);
        frame.setRow(2);
        frame.setName("frame");
        frame.setTemplateType(1);
        frame.setPutState(1);
        frame.setReversible(1);
        frame.setChildTemplateId("discId,discId");
        templateVOS.add(frame);

        TemplateRspDto disc = new TemplateRspDto();
        disc.setId("discId");
        disc.setCol(1);
        disc.setRow(2);
        disc.setName("disc");
        disc.setTemplateType(1);
        disc.setPutState(1);
        disc.setReversible(1);
        templateVOS.add(disc);


        boxs.setId("boxId");
        boxs.setParentId("root");
        boxRealPosition.add(boxs);


        frames.setId("frameId");
        frames.setParentId("boxId");
        frames.setWidth(100.00);
        frames.setHeight(200.00);
        frames.setAbscissa(12.00);
        frames.setOrdinate(13.00);
        frameRealPosition.add(frames);


        discs.setState(1);
        discs.setParentId("frameId");
        discs.setId("discId");
        discs.setPutState(1);
        discs.setWidth(100.00);
        discs.setHeight(200.00);
        discs.setAbscissa(12.00);
        discs.setOrdinate(13.00);
        discRealPosition.add(discs);


        port.setId("portId");
        port.setParentId("discId");
        port.setWidth(100.00);
        port.setHeight(200.00);
        port.setAbscissa(12.00);
        port.setOrdinate(13.00);
        portRealPosition.add(port);

    }

    /**
     * ???????????????
     *
     * @return
     */
    @Test
    public void queryPortNumByPortId() throws Exception {
        new Expectations() {
            {
                templateDao.queryPortNumByPortId(anyString);
                result = "1A-2b-A";
            }
        };
        String id = templateService.queryPortNumByPortId("portId");
        Assert.assertTrue(org.apache.commons.lang.StringUtils.isNotEmpty(id));
    }

    /**
     * ????????????
     *
     * @throws Exception
     */
    @Test
    public void createTemplate() throws Exception {
        // ????????????
        Result result = templateService.createTemplate(new TemplateReqDto());
        Assert.assertTrue(RfIdResultCodeConstant.CREATE_TEMPLATE_PARAM_ERROR.equals(result.getCode()));
        new Expectations() {
            {
                templateDao.saveTemplate((TemplateReqDto) any);
            }
        };

        TemplateReqDto reqDto = new TemplateReqDto();
        reqDto.setTemplateType(1);
        reqDto.setCol(1);
        reqDto.setRow(2);
        reqDto.setName("name");

        Result template = templateService.createTemplate(reqDto);
        Assert.assertTrue(template.getCode() == 0);
    }

    /**
     * ??????????????????-box
     *
     * @throws Exception
     */
    @Test
    public void queryAllTemplate001() throws Exception {
        // ????????????
        Result result = templateService.queryAllTemplate(new TemplateReqDto());
        Assert.assertTrue(RfIdResultCodeConstant.QUERY_TEMPLATE_PARAM_ERROR.equals(result.getCode()));

        TemplateReqDto reqDto1 = new TemplateReqDto();
        reqDto1.setTemplateType(4);
        Result result1 = templateService.queryAllTemplate(reqDto1);
        Assert.assertTrue(RfIdResultCodeConstant.QUERY_TEMPLATE_PARAM_ERROR.equals(result1.getCode()));
        // type=0 ???????????????
        new Expectations() {
            {
                templateDao.queryBoxTemplate((TemplateReqDto) any);
                result = templateVOS;
            }
        };
        TemplateReqDto reqDto = new TemplateReqDto();
        reqDto.setTemplateType(0);
        reqDto.setCol(1);
        reqDto.setRow(2);
        reqDto.setName("name_*");

        Result template = templateService.queryAllTemplate(reqDto);
        Assert.assertTrue(template.getCode() == 0);
    }

    /**
     * ??????????????????-frame
     *
     * @throws Exception
     */
    @Test
    public void queryAllTemplate002() throws Exception {

        // type=1 ???????????????
        new Expectations() {
            {
                templateDao.queryFrameTemplate((TemplateReqDto) any);
                result = templateVOS;
            }
        };
        TemplateReqDto reqDto = new TemplateReqDto();
        reqDto.setTemplateType(1);
        reqDto.setCol(1);
        reqDto.setRow(2);
        reqDto.setName("name_");

        Result template = templateService.queryAllTemplate(reqDto);
        Assert.assertTrue(template.getCode() == 0);
    }

    /**
     * ??????????????????-disc
     *
     * @throws Exception
     */
    @Test
    public void queryAllTemplate003() throws Exception {
        // type=2 ???????????????
        new Expectations() {
            {
                templateDao.queryBoardTemplate((TemplateReqDto) any);
                result = templateVOS;
            }
        };
        TemplateReqDto reqDto = new TemplateReqDto();
        reqDto.setTemplateType(2);
        reqDto.setCol(1);
        reqDto.setRow(2);
        reqDto.setName("name*");

        Result template = templateService.queryAllTemplate(reqDto);
        Assert.assertTrue(template.getCode() == 0);
    }

    /**
     * ???????????????
     *
     * @throws Exception
     */
    @Test
    public void queryRealPosition() throws Exception {
        // ??????????????????
        Result result = templateService.queryRealPosition(new RealReqDto());
        Assert.assertTrue(RfIdResultCodeConstant.QUERY_REALPOSITION_PARAM_ERROR.equals(result.getCode()));

        new MockUp<TemplateServiceImpl>() {
            @Mock
            public Boolean getRfIdDataAuthInfo(String deviceId) {
                return false;
            }
        };
        RealReqDto realReqDto = new RealReqDto();
        realReqDto.setDeviceId("deviceId");
        Result result1 = templateService.queryRealPosition(realReqDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result1.getCode()));

    }

    /**
     * ???????????????
     *
     * @throws Exception
     */
    @Test
    public void queryRealPosition001() throws Exception {

        new MockUp<TemplateServiceImpl>() {
            @Mock
            public Boolean getRfIdDataAuthInfo(String deviceId) {
                return true;
            }
        };

        new Expectations() {
            {
                templateDao.queryBoxRealPosition((RealReqDto) any);
                result = boxRealPosition;

                templateDao.queryFrameReal((List<String>) any);
                result = frameRealPosition;

                templateDao.queryDiscReal((List<String>) any);
                result = discRealPosition;

                templateDao.queryPortReal((List<String>) any);
                result = portRealPosition;
            }
        };
        RealReqDto realReqDto = new RealReqDto();
        realReqDto.setDeviceId("deviceId");
        Result result1 = templateService.queryRealPosition(realReqDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result1.getCode()));
    }

    /**
     * ????????????????????????
     *
     * @throws Exception
     */
    @Test
    public void queryRealPositionByFrameId() throws Exception {
        // ??????????????????
        Result result = templateService.queryRealPositionByFrameId(null, false);
        Assert.assertTrue(RfIdResultCodeConstant.QUERY_REALPOSITION_FRAMEID_ERROR.equals(result.getCode()));

        ReflectionTestUtils.setField(templateService, "boxWidth", 123);
        ReflectionTestUtils.setField(templateService, "boxHeight", 123);

        new Expectations() {
            {
                templateDao.queryFrameRealPositionById(anyString);
                result = frames;

                templateDao.queryDiscReal((List<String>) any);
                result = discRealPosition;

                templateDao.queryPortReal((List<String>) any);
                result = portRealPosition;
            }
        };
        Result result1 = templateService.queryRealPositionByFrameId("frameId", false);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result1.getCode()));
    }

    /**
     * ??????????????????
     *
     * @throws Exception
     */
    @Test
    public void queryFormationByDeviceId() throws Exception {
        // ??????????????????
        Result result = templateService.queryFormationByDeviceId(null);
        Assert.assertTrue(RfIdResultCodeConstant.QUERY_REALPOSITION_DEVICE_ID_ERROR.equals(result.getCode()));

        new Expectations() {
            {
                templateDao.queryBoxRealPosition((RealReqDto) any);
                result = null;
            }
        };
        Result result1 = templateService.queryFormationByDeviceId("deviceId");
        Assert.assertTrue(RfIdResultCodeConstant.DEVICE_IS_NOT_CONFIGURE.equals(result1.getCode()));

        new Expectations() {
            {
                templateDao.queryBoxRealPosition((RealReqDto) any);
                result = boxRealPosition;

                templateDao.queryFrameReal((List<String>) any);
                result = frameRealPosition;
            }
        };
        Result result2 = templateService.queryFormationByDeviceId("deviceId");
        Assert.assertTrue(ResultCode.SUCCESS.equals(result2.getCode()));
    }

    /**
     * ??????????????????????????????
     *
     * @throws Exception
     */
    @Test
    public void saveDeviceAndTempRelation() throws Exception {
        // ??????????????????
        Result result = templateService.saveDeviceAndTempRelation(new TemplateReqDto());
        Assert.assertTrue(RfIdResultCodeConstant.SAVE_TEMPLATE_RELATION_ERROR.equals(result.getCode()));

        TemplateReqDto reqDto = new TemplateReqDto();
        reqDto.setBoxCodeRule(1);
        reqDto.setBoxTrend(1);
        Result result1 = templateService.saveDeviceAndTempRelation(reqDto);
        Assert.assertTrue(RfIdResultCodeConstant.SAVE_TEMPLATE_RELATION_ERROR.equals(result1.getCode()));

        reqDto.setBoxTemplateId("boxTemplateId");
        reqDto.setFrameCodeRule(1);
        reqDto.setFrameTrend(1);
        Result result2 = templateService.saveDeviceAndTempRelation(reqDto);
        Assert.assertTrue(RfIdResultCodeConstant.SAVE_TEMPLATE_RELATION_ERROR.equals(result2.getCode()));

        reqDto.setDeviceId("deviceId");
        reqDto.setDeviceType("060");
        Result result3 = templateService.saveDeviceAndTempRelation(reqDto);
        Assert.assertTrue(RfIdResultCodeConstant.SAVE_TEMPLATE_RELATION_ERROR.equals(result3.getCode()));

        reqDto.setDiscTrend(1);
        reqDto.setDiscCodeRule(1);
        Result result4 = templateService.saveDeviceAndTempRelation(reqDto);
        Assert.assertTrue(RfIdResultCodeConstant.SUCCESS.equals(result4.getCode()));
    }

    /**
     * ?????????????????? ????????????id
     *
     * @throws Exception exception
     */
    @Test
    public void queryPortInfoByPortId() throws Exception {
        Result result = templateService.queryPortInfoByPortId(null);
        Assert.assertTrue(RfIdResultCodeConstant.PORT_ID_IS_NULL.equals(result.getCode()));

        RealPosition portReal = new RealPosition();
        PortCableCoreCondition portInfo = new PortCableCoreCondition();
        portInfo.setPortState(1);

        portReal.setState(0);
        portReal.setBusinessNum(1);

        List<PortCableCoreInfo> portCableCoreInfos = Lists.newArrayList();
        PortCableCoreInfo info = new PortCableCoreInfo();
        info.setOppositeCableCoreNo("1");
        info.setOppositeResource("resource");
        portCableCoreInfos.add(info);

        PortInfoBean portInfoBean = new PortInfoBean();
        portInfoBean.setPortNo(1);

        OpticCableSectionInfo op = new OpticCableSectionInfo();
        op.setOpticCableSectionName("id");
        new Expectations() {
            {
                templateDao.getPortRealById(anyString);
                result = portInfo;

                templateDao.queryPortNumByPortId(anyString);
                result = "1A-2-3A";

                portCableCoreInfoDao.getPortCableCoreInfo((PortCableCoreInfoReq) any);
                result = portCableCoreInfos;

                opticCableSectionInfoDao.selectOne((OpticCableSectionInfo) any);
                result = op;

//                facilityDao.queryPortLabelInfo((RealPosition) any);
//                result = portInfoBean;


            }
        };

        Result result4 = templateService.queryPortInfoByPortId("12");
        Assert.assertTrue(RfIdResultCodeConstant.SUCCESS.equals(result4.getCode()));

    }

    /**
     * ????????????????????????
     *
     * @throws Exception
     */
    @Test
    public void batchQueryPortInfo() throws Exception {
        List<JumpFiberInfoResp> list = Lists.newArrayList();
        JumpFiberInfoResp resp = new JumpFiberInfoResp();
        list.add(resp);

        new Expectations() {
            {
                templateDao.batchQueryPortInfo((JumpFiberInfoResp) any);
                result = null;
            }
        };
        List<JumpFiberInfoResp> respList = templateService.batchQueryPortInfo(list);
        Assert.assertEquals(0, respList.size());

        PortInfoBean info = new PortInfoBean();
        info.setPortState(1);
        info.setLabelState(1);
        info.setAdapterType(1);
        info.setPortLabel("1");
        info.setLabelType(1);


        new Expectations() {
            {
                templateDao.batchQueryPortInfo((JumpFiberInfoResp) any);
                result = info;
            }
        };

        List<JumpFiberInfoResp> respList1 = templateService.batchQueryPortInfo(list);
        Assert.assertEquals(1, respList1.size());
    }

    /**
     * ????????????id ??? ???????????????
     */
    @Test
    public void queryTemplateTop() throws Exception {
        Result result = templateService.queryTemplateTop(null);
        Assert.assertTrue(RfIdResultCodeConstant.PARAMS_ERROR.equals(result.getCode()));


        List<RealRspDto> frames = Lists.newArrayList();
        RealRspDto dto = new RealRspDto();
        dto.setId("frameId");
        frames.add(dto);

        List<RealRspDto> discs = Lists.newArrayList();
        RealRspDto dto1 = new RealRspDto();
        dto1.setParentId("frameId");
        discs.add(dto1);

        new Expectations() {
            {
                templateDao.queryFrameRealPosition(anyString);
                result = frames;
                templateDao.queryDiscRealPosition(anyString);
                result = discs;
            }
        };
        Result result1 = templateService.queryTemplateTop("deviceId");
        Assert.assertTrue(ResultCode.SUCCESS.equals(result1.getCode()));
    }

    /**
     * ??????????????????
     * ???????????????
     */
    @Test
    public void updatePortState() throws Exception {
        Result result = templateService.updatePortState(null);
        Assert.assertTrue(RfIdResultCodeConstant.PARAMS_ERROR.equals(result.getCode()));

        RealPortReqDto reqDto = new RealPortReqDto();
        reqDto.setDiscId("discId");
        Result result1 = templateService.updatePortState(reqDto);
        Assert.assertTrue(RfIdResultCodeConstant.PARAMS_ERROR.equals(result1.getCode()));

        reqDto.setState(1);
        new Expectations() {
            {
                templateDao.updatePortState((RealPortReqDto) any);
                templateDao.updateDiscState((RealPortReqDto) any);
            }
        };
        Result result3 = templateService.updatePortState(reqDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result3.getCode()));
    }


    /**
     * ??????deviceId ????????? ?????? ???????????????????????????
     *
     * @throws Exception
     */
    @Test
    public void getRfIdDataAuthInfo() throws Exception {

        try {
            templateService.getRfIdDataAuthInfo(null);
        } catch (BizException e) {
            Assert.assertTrue(e instanceof BizException);
        }
        DeviceInfoDto device = new DeviceInfoDto();
        device.setDeviceType("060");

        List<RoleDeviceType> roleDeviceTypes = Lists.newArrayList();
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setDeviceTypeId("060-2");
        roleDeviceTypes.add(roleDeviceType);

        Role role = new Role();
        role.setRoleDevicetypeList(roleDeviceTypes);

        User user = new User();
        user.setRole(role);

        new Expectations() {
            {
                deviceFeign.getDeviceById(anyString);
                result = device;

                userFeign.queryCurrentUser(anyString, anyString);
                result = user;
            }
        };

        Boolean info = templateService.getRfIdDataAuthInfo("deviceId");
        Assert.assertEquals(true, info);
    }

    /**
     * ???????????????????????????????????????
     *
     * @throws Exception
     */
    @Test
    public void updatePortBindingState() throws Exception {
        try {
            templateService.updatePortBindingState(null, 1, 1, 1);
        } catch (BizException e) {
            Assert.assertEquals(RfIdResultCodeConstant.UPDATE_PORT_BINDING_PORT_IS_NULL, e.getCode());
        }
        try {
            templateService.updatePortBindingState("portId", null, 1, 1);
        } catch (BizException e) {
            Assert.assertEquals(RfIdResultCodeConstant.UPDATE_PORT_BINDING_STATE_IS_NULL, e.getCode());
        }
        try {
            templateService.updatePortBindingState("portId", 1, null, 1);
        } catch (BizException e) {
            Assert.assertEquals(RfIdResultCodeConstant.UPDATE_PORT_BINDING_BUS_TYPE_IS_NULL, e.getCode());
        }

        List<RealRspDto> realRspDtos = Lists.newArrayList();
        RealRspDto realRspDto = new RealRspDto();
        realRspDto.setId("portId");
        realRspDto.setBusBindingState("0-0-0");
        realRspDto.setDiscId("discId");
        realRspDtos.add(realRspDto);

        new Expectations() {
            {
                templateDao.querySameDiscPort(anyString);
                result = realRspDtos;
                templateDao.updateBusBindingPortState((RealPortReqDto) any);
                templateDao.updateBusBindingDiscState((RealPortReqDto) any);
            }
        };
        templateService.updatePortBindingState("portId", 1, 1, 1);
        templateService.updatePortBindingState("portId", 1, 2, 1);
        templateService.updatePortBindingState("portId", 1, 0, 1);
    }

    /**
     * ??????????????????????????????id
     *
     * @throws Exception
     */
    @Test
    public void queryPortIdByPortInfo() throws Exception {
        new Expectations() {
            {
                templateDao.queryPortIdByPortInfo((PortInfoReqDto) any);
                result = "portId";
            }
        };
        String s = templateService.queryPortIdByPortInfo(new PortInfoReqDto());
        Assert.assertEquals("portId", s);
    }

    /**
     * ????????????
     *
     * @throws Exception
     */
    @Test
    public void updateTemplate() throws Exception {
        Result result = templateService.updateTemplate(null);
        Assert.assertTrue(RfIdResultCodeConstant.UPDATE_TEMPLATE_PARAM_IS_NULL.equals(result.getCode()));

        new Expectations() {
            {
                templateDao.updateTemplate((TemplateReqDto) any);
            }
        };
        Result result1 = templateService.updateTemplate(new TemplateReqDto());
        Assert.assertTrue(ResultCode.SUCCESS.equals(result1.getCode()));
    }

    /**
     * ??????????????????
     *
     * @throws Exception
     */
    @Test
    public void deleteTemplate001() throws Exception {
        Result result = templateService.deleteTemplate(null);
        Assert.assertTrue(RfIdResultCodeConstant.DELETE_TEMPLATE_PARAM_IS_NULL.equals(result.getCode()));

        TemplateReqDto reqDto = new TemplateReqDto();
        reqDto.setTemplateType(0);
        Result result1 = templateService.deleteTemplate(reqDto);
        Assert.assertTrue(RfIdResultCodeConstant.DELETE_TEMPLATE_PARAM_IS_NULL.equals(result1.getCode()));

        reqDto.setId("id");

        new Expectations() {
            {
                templateDao.queryTemplateExistsById((List<String>) any);
                result = 1;
            }
        };

        try {
            templateService.deleteTemplate(reqDto);
        } catch (BizException e) {
            Assert.assertEquals(RfIdResultCodeConstant.TEMPLATE_IS_NOT_DELETE, e.getCode());
        }

        new Expectations() {
            {
                templateDao.queryTemplateExistsById((List<String>) any);
                result = 0;
                templateDao.deleteTemplateById(anyString);
            }
        };
        Result result2 = templateService.deleteTemplate(reqDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result2.getCode()));
    }

    /**
     * ?????? ???????????????
     *
     * @throws Exception
     */
    @Test
    public void deleteTemplate002() throws Exception {

        TemplateReqDto reqDto = new TemplateReqDto();
        reqDto.setTemplateType(1);
        reqDto.setId("id");

        List<TemplateRspDto> templateRspDtos = Lists.newArrayList();
        TemplateRspDto dto = new TemplateRspDto();
        dto.setChildTemplateId("id,id");

        templateRspDtos.add(dto);
        new Expectations() {
            {
                templateDao.queryTemplateByType(anyInt);
                result = templateRspDtos;
            }
        };
        Result result = templateService.deleteTemplate(reqDto);
        Assert.assertTrue(RfIdResultCodeConstant.TEMPLATE_IS_NOT_DELETE.equals(result.getCode()));

        new Expectations() {
            {
                templateDao.queryTemplateByType(anyInt);
                result = null;
                templateDao.deleteTemplateById(anyString);
            }
        };
        Result result1 = templateService.deleteTemplate(reqDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result1.getCode()));
    }

    /**
     * ?????? ???????????????
     *
     * @throws Exception
     */
    @Test
    public void deleteTemplate003() throws Exception {

        TemplateReqDto reqDto = new TemplateReqDto();
        reqDto.setTemplateType(2);
        reqDto.setId("id");

        List<TemplateRspDto> templateRspDtos = Lists.newArrayList();
        TemplateRspDto dto = new TemplateRspDto();
        dto.setChildTemplateId("id,id");

        templateRspDtos.add(dto);
        new Expectations() {
            {
                templateDao.queryTemplateByType(anyInt);
                result = templateRspDtos;
            }
        };
        Result result = templateService.deleteTemplate(reqDto);
        Assert.assertTrue(RfIdResultCodeConstant.TEMPLATE_IS_NOT_DELETE.equals(result.getCode()));

        new Expectations() {
            {
                templateDao.queryTemplateByType(anyInt);
                result = null;
                templateDao.deleteTemplateById(anyString);
            }
        };
        Result result1 = templateService.deleteTemplate(reqDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result1.getCode()));
    }

    /**
     * ???????????? ?????? ????????????
     *
     * @throws Exception
     */
    @Test
    public void batchUpdatePortCableState() throws Exception {
        new Expectations() {
            {
                templateDao.batchUpdatePortCableState((List<PortCableReqDto>) any);
            }
        };

        List<PortCableReqDto> portCableReqDtos = Lists.newArrayList();
        PortCableReqDto dto = new PortCableReqDto();
        portCableReqDtos.add(dto);
        templateService.batchUpdatePortCableState(portCableReqDtos);
    }

    /**
     * App ??????????????? ????????????
     *
     * @throws Exception
     */
    @Test
    public void deleteDeviceEntity() throws Exception {
        List<RealRspDto> realRspDtos = Lists.newArrayList();
        RealRspDto dto = new RealRspDto();
        dto.setId("id");
        realRspDtos.add(dto);

        new Expectations() {
            {
                templateDao.queryRealPositionByDeviceId(anyString);
                result = realRspDtos;
                templateDao.deletePort((List<String>) any);
                templateDao.deleteDeviceEntity(anyString);
            }
        };
        templateService.deleteDeviceEntity("deviceId");
    }

    /**
     * ????????????
     *
     * @throws Exception
     */
    @Test
    public void getTemplateData() throws Exception {
        TemplateReqDto reqDto = new TemplateReqDto();
        reqDto.setTemplateType(0);
        reqDto.setCol(1);
        reqDto.setRow(2);
        reqDto.setName("name_*");
        List<TemplateRspDto> templateData = templateService.getTemplateData(templateVOS, reqDto);
        Assert.assertTrue(templateData != null);
    }

}