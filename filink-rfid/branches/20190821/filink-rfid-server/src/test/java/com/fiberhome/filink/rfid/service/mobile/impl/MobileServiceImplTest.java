package com.fiberhome.filink.rfid.service.mobile.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.util.DeviceType;
import com.fiberhome.filink.rfid.bean.facility.*;
import com.fiberhome.filink.rfid.bean.template.RealPosition;
import com.fiberhome.filink.rfid.constant.RfIdResultCode;
import com.fiberhome.filink.rfid.dao.template.FacilityDao;
import com.fiberhome.filink.rfid.dao.template.TemplateDao;
import com.fiberhome.filink.rfid.enums.TemplateTypeEnum;
import com.fiberhome.filink.rfid.enums.UploadTypeEnum;
import com.fiberhome.filink.rfid.exception.BizException;
import com.fiberhome.filink.rfid.req.template.PortInfoReqDto;
import com.fiberhome.filink.rfid.req.template.RealReqDto;
import com.fiberhome.filink.rfid.req.template.TemplateReqDto;
import com.fiberhome.filink.rfid.resp.template.RealRspDto;
import com.fiberhome.filink.rfid.resp.template.TemplateRspDto;
import com.fiberhome.filink.rfid.service.rfid.RfidInfoService;
import com.fiberhome.filink.rfid.service.template.impl.TemplateHelper;
import com.fiberhome.filink.rfid.service.template.impl.TemplateServiceImpl;
import com.google.common.collect.Lists;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * Created by Qing on 2019/7/1.
 * MobilService test
 */
@RunWith(JMockit.class)
public class MobileServiceImplTest {

    @InjectMocks
    private MobileServiceImpl mobileService = new MobileServiceImpl();

    /**
     * 坐标重刷
     */
    @Injectable
    private MobileAsync mobileAsync;

    /**
     * Dao 层
     */
    @Injectable
    private TemplateDao templateDao;

    /**
     * facilityDao
     */
    @Injectable
    private FacilityDao facilityDao;

    /**
     * 模板服务类
     */
    @Injectable
    private TemplateServiceImpl templateService;

    /**
     * rfid 关联信息
     */
    @Injectable
    private RfidInfoService rfidInfoService;

    @Injectable
    private TemplateHelper templateHelper;
    /**
     * 设施实体api
     */
    @Injectable
    private DeviceFeign deviceFeign;

    private FacilityUploadDto uploadDto = new FacilityUploadDto();

    private List<BoxInfoBean> boxList = new ArrayList<>();

    private List<BoardInfoBean> boardList = new ArrayList<>();

    private List<PortInfoBean> portList = new ArrayList<>();

    private FrameEntity frameEntity = new FrameEntity();

    private BoardEntity boardEntity = new BoardEntity();

    private List<TemplateRspDto> boxTemplate = new ArrayList<>();

    private List<TemplateRspDto> boxTemplate2 = new ArrayList<>();
    FacilityQueryBean queryBean = new FacilityQueryBean();

    @Before
    public void setUp() {
        queryBean.setQueryType(1);
        queryBean.setDeviceType(DeviceType.Optical_Box.getCode());
        queryBean.setDeviceId("deviceID");

        //反射引用
        ReflectionTestUtils.setField(mobileService, "mysqlBatchNum", 100);
        ReflectionTestUtils.setField(mobileService, "facilityDao", facilityDao);
        ReflectionTestUtils.setField(mobileService, "rfidInfoService", rfidInfoService);
        ReflectionTestUtils.setField(mobileService, "mobileAsync", mobileAsync);
        ReflectionTestUtils.setField(mobileService, "templateService", templateService);
        ReflectionTestUtils.setField(mobileService, "templateHelper", templateHelper);
        ReflectionTestUtils.setField(mobileService, "templateDao", templateDao);
        ReflectionTestUtils.setField(mobileService, "deviceFeign", deviceFeign);

        //标签信息
        BoxInfoBean box = new BoxInfoBean();
        box.setDeviceId("9527");
        box.setLabelState(1);
        box.setLabelType(1);
        box.setProducer("FiberHome");
        boxList.add(box);

        BoardInfoBean board = new BoardInfoBean();
        board.setLabelState(1);
        board.setLabelType(1);
        board.setDeviceId("9527");
        board.setFrameDouble(1);
        board.setFrameNo(123);
        board.setBoardNo(111);
        boardList.add(board);

        PortInfoBean port = new PortInfoBean();
        port.setLabelState(1);
        port.setLabelType(1);
        port.setDeviceId("9527");
        port.setFrameDouble(1);
        port.setFrameNo(123);
        port.setBoardNo(111);
        port.setPortDouble(1);
        port.setPortNo(100);
        port.setPortState(1);
        portList.add(port);
        uploadDto.setBoxList(boxList);
        uploadDto.setBoardList(boardList);
        uploadDto.setPortList(portList);

        //实体信息
        frameEntity.setFrameDouble(1);
        frameEntity.setRealNo(2);
        frameEntity.setMouldNo(2);
        frameEntity.setState(1);

        boardEntity.setPortDouble(1);
        boardEntity.setRealNo(2);
        boardEntity.setMouldNo(2);
        boardEntity.setState(1);

        TemplateRspDto rspDto = new TemplateRspDto();
        rspDto.setCol(1);
        rspDto.setRow(1);
        boxTemplate.add(rspDto);

        TemplateRspDto rspDto1 = new TemplateRspDto();
        rspDto1.setCol(1);
        rspDto1.setRow(1);
        List<TemplateRspDto> list2 = new ArrayList<>();
        TemplateRspDto rspDto2 = new TemplateRspDto();
        rspDto2.setCol(1);
        rspDto2.setRow(1);
        rspDto2.setId("9517");
        rspDto2.setChildTemplateId("9527");
        list2.add(rspDto2);
        List<TemplateRspDto> list3 = new ArrayList<>();
        TemplateRspDto rspDto3 = new TemplateRspDto();
        rspDto3.setCol(1);
        rspDto3.setRow(1);
        rspDto3.setId("9527");
        list3.add(rspDto3);

        rspDto2.setChildTemplateList(list3);
        rspDto1.setChildTemplateList(list2);
        boxTemplate2.add(rspDto1);
    }

    /**
     * 上传设施标签信息
     * 1.增加
     * 2.删除
     * 3.修改
     * 4.上传类型不存在
     * 5.标签已存在
     *
     * @throws Exception 异常
     */
    @Test
    public void uploadFacilityInfo() throws Exception {
        new Expectations() {
            {
                facilityDao.queryBoxLabelByDevId(anyString);
                result = null;

                rfidInfoService.checkRfidCodeListIsExist((Set<String>) any);
                result = false;

                templateDao.queryPortIdByPortInfo((PortInfoReqDto) any);
                result = "portId";
            }
        };

        uploadDto.setUploadType(UploadTypeEnum.INSERT.ordinal());
        Result result = mobileService.uploadFacilityInfo(uploadDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));

        uploadDto.setUploadType(UploadTypeEnum.DELETE.ordinal());
        Result result2 = mobileService.uploadFacilityInfo(uploadDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result2.getCode()));

        new Expectations() {
            {
                facilityDao.queryBoardLabelById(anyString);
                BoardInfoBean boardInfoBean = new BoardInfoBean();
                boardInfoBean.setFrameDouble(1);
                boardInfoBean.setFrameNo(123);
                boardInfoBean.setBoardNo(222);
                result = Collections.singletonList(boardInfoBean);

                facilityDao.queryPortLabelById(anyString);
                PortInfoBean portInfoBean = new PortInfoBean();
                portInfoBean.setFrameDouble(1);
                portInfoBean.setFrameNo(123);
                portInfoBean.setBoardNo(111);
                portInfoBean.setPortDouble(1);
                portInfoBean.setPortNo(666);
                result = Collections.singletonList(portInfoBean);
            }
        };

        uploadDto.setUploadType(UploadTypeEnum.UPDATE.ordinal());
        Result result3 = mobileService.uploadFacilityInfo(uploadDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result3.getCode()));

        uploadDto.setUploadType(null);
        try {
            mobileService.uploadFacilityInfo(uploadDto);
        } catch (BizException e) {
            Assert.assertTrue(RfIdResultCode.UPLOAD_DATA_TYPE_ERROR.equals(e.getCode()));
        }

        uploadDto.setUploadType(4);
        try {
            mobileService.uploadFacilityInfo(uploadDto);
        } catch (BizException e) {
            Assert.assertTrue(RfIdResultCode.UPDATE_TYPE_IS_WRONG.equals(e.getCode()));
        }

        new Expectations() {
            {
                facilityDao.queryBoxLabelByDevId(anyString);
                result = null;

                rfidInfoService.checkRfidCodeListIsExist((Set<String>) any);
                result = true;
            }
        };

        uploadDto.setUploadType(UploadTypeEnum.INSERT.ordinal());
        try {
            mobileService.uploadFacilityInfo(uploadDto);
        } catch (BizException e) {
            Assert.assertTrue(RfIdResultCode.LABEL_IS_EXISTED.equals(e.getCode()));
        }

    }

    /**
     * 条件查询设施标签信息
     *
     * @throws Exception 异常
     *                   1.箱架
     *                   2.盘
     *                   3.端口
     *                   4.指定标签
     */
    @Test
    public void queryFacilityINfo() throws Exception {

        new Expectations() {
            {
                templateDao.queryFacilityInfoByCondition(anyString);
                List<TemplateReqDto> list = new ArrayList<>();
                TemplateReqDto templateReqDto = new TemplateReqDto();
                templateReqDto.setBoxName("BoxName");
                templateReqDto.setTemplateType(1);
                list.add(templateReqDto);
                result = list;

            }
        };
        Result result = mobileService.queryFacilityINfo(queryBean);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }


    @Test
    public void queryFacilityINfo002() throws Exception {

        queryBean.setQueryType(3);
        new Expectations() {
            {
                deviceFeign.queryDeviceNameById(anyString);
                result = "deviceName";
                facilityDao.queryPortLabelByNo(queryBean);
                result = portList;
            }
        };
        Result result3 = mobileService.queryFacilityINfo(queryBean);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result3.getCode()));
    }

    @Test
    public void queryFacilityINfo003() throws Exception {

        queryBean.setRfidLabel("9527");
        new Expectations() {
            {
                deviceFeign.queryDeviceNameById(anyString);
                result = "deviceName";

                facilityDao.queryBoxLabelById(anyString);
                result = boxList;

                facilityDao.queryBoardLabelById(anyString);
                result = boardList;

                facilityDao.queryPortLabelById(anyString);
                result = portList;
            }
        };
        Result result4 = mobileService.queryFacilityINfo(queryBean);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result4.getCode()));
    }

    @Test
    public void queryFacilityINfo001() throws Exception {
        queryBean.setQueryType(2);
        new Expectations() {
            {
                facilityDao.queryBoardLabelByFraNoAndBoaNo(queryBean);
                result = boardList;

                deviceFeign.queryDeviceNameById(anyString);
                result = "deviceName";

            }
        };
        Result result2 = mobileService.queryFacilityINfo(queryBean);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result2.getCode()));
    }

    /**
     * 上传设施实体信息
     * 1.新增 状态为0
     * 2.新增状态部位0
     * 3.部分新增 部分修改
     * 4.修改
     *
     * @throws Exception 异常
     */
    @Test
    public void uploadDeviceInfo() throws Exception {
        DeviceUploadDto uploadDto = new DeviceUploadDto();
        uploadDto.setUploadType(UploadTypeEnum.UPDATE.ordinal());

        List<FrameEntity> frameList = new ArrayList<>();
        List<BoardEntity> boardEntityList = new ArrayList<>();
        boardEntity.setState(1);
        boardEntityList.add(boardEntity);
        frameEntity.setBoardEntity(boardEntityList);
        frameEntity.setState(1);
        frameList.add(frameEntity);
        uploadDto.setFrameEntity(frameList);

        BaseMould boxMould = new BaseMould();
        boxMould.setName("BoxName");
        boxMould.setReversible(2);
        uploadDto.setBoxMould(boxMould);

        BaseMould frameMould = new BaseMould();
        frameMould.setName("BoxName");
        frameMould.setReversible(2);
        frameMould.setPutState(1);
        uploadDto.setFrameMould(frameMould);

        new Expectations() {
            {
                templateService.saveDeviceAndTempRelation((TemplateReqDto) any);
                result = ResultUtils.success();
            }
        };
        //新增状态为0 走addDevice（）方法
        Result result = mobileService.uploadDeviceInfo(uploadDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));

        //新增 但是状态不为0 走addUpdateAfterDevice（）方法
        uploadDto.getFrameEntity().get(0).setState(1);
        uploadDto.getBoxMould().setReversible(1);
        new Expectations() {
            {
//                templateHelper.calcBoxRealPosition(anyBoolean, anyString, (TemplateReqDto) any, anyString);
                List<RealPosition> list = new ArrayList<>();
                RealPosition realPosition = new RealPosition();
                realPosition.setState(1);
                realPosition.setCol(3);
                list.add(realPosition);
                result = list;
            }
        };
        Result result2 = mobileService.uploadDeviceInfo(uploadDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result2.getCode()));

        //部分新增部分修改
        uploadDto.getFrameEntity().get(0).setId("9527");
        new Expectations() {
            {
                templateDao.queryRealPositionByDeviceId(anyString);
                List<RealRspDto> list = new ArrayList<>();
                RealRspDto realRspDto = new RealRspDto();
                realRspDto.setCol(2);
                list.add(realRspDto);
                result = list;
            }
        };

        Result result3 = mobileService.uploadDeviceInfo(uploadDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result3.getCode()));

        //走修改
        uploadDto.getFrameEntity().get(0).getBoardEntity().get(0).setId("9527");
        Result result4 = mobileService.uploadDeviceInfo(uploadDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result4.getCode()));


    }


    /**
     * 验证是否有配业务信息
     */
    @Test
    public void isExistBus() {
        new Expectations() {
            {
                facilityDao.queryBoxLabelByDevId(anyString);
                result = null;
            }
        };
        Boolean existBus = mobileService.isExistBus("");
        Assert.assertTrue(!existBus);

        List<BoxInfoBean> boxInfoBeans = Lists.newArrayList();
        BoxInfoBean bean = new BoxInfoBean();
        bean.setDeviceType("060");
        boxInfoBeans.add(bean);
        new Expectations() {
            {
                facilityDao.queryBoxLabelByDevId(anyString);
                result = boxInfoBeans;
            }
        };
        Boolean existBus1 = mobileService.isExistBus("");
        Assert.assertTrue(existBus1);

    }

    /**
     * 条件查询设施实体信息
     *
     * @throws Exception 异常
     */
    @Test
    public void queryDeviceInfo() throws Exception {
        new Expectations() {
            {
                templateDao.queryFacilityInfoByCondition(anyString);
                List<TemplateReqDto> list = new ArrayList<>();
                TemplateReqDto dto = new TemplateReqDto();
                dto.setRelationId("relationId");
                dto.setDeviceId("deviceId");
                list.add(dto);
                result = list;

                templateDao.queryBoxRealPosition((RealReqDto) any);
                List<RealRspDto> boxRealPosition = new ArrayList<>();
                RealRspDto realRspDto = new RealRspDto();
                realRspDto.setDeviceId("deviceId");
                boxRealPosition.add(realRspDto);
                result = boxRealPosition;

                templateDao.queryFrameReal((List<String>) any);
                List<RealRspDto> frameRealPosition = new ArrayList<>();
                RealRspDto rspDto = new RealRspDto();
                rspDto.setId("9527");
                rspDto.setBusinessNum(1);
                rspDto.setSide(1);
                frameRealPosition.add(rspDto);
                result = frameRealPosition;

                templateDao.queryDiscReal((List<String>) any);
                List<RealRspDto> discRealPositon = new ArrayList<>();
                RealRspDto disDto = new RealRspDto();
                disDto.setBusinessNum(1);
                disDto.setSide(1);
                disDto.setId("9527");
                disDto.setParentId("3333");
                discRealPositon.add(disDto);
                RealRspDto disDto2 = new RealRspDto();
                disDto2.setBusinessNum(1);
                disDto2.setSide(1);
                disDto2.setParentId("9527");
                disDto2.setState(2);
                disDto2.setBusState(0);
                discRealPositon.add(disDto2);
                result = discRealPositon;

                templateDao.queryBoxTemplateById(anyString);
                List<TemplateRspDto> templateRspDtos = new ArrayList<>();
                TemplateRspDto box = new TemplateRspDto();
                box.setTemplateType(TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal());
                box.setId("3396");
                TemplateRspDto frame = new TemplateRspDto();
                frame.setTemplateType(TemplateTypeEnum.TEMPLATE_TYPE_FRAME.ordinal());
                frame.setId("3396");
                TemplateRspDto disc = new TemplateRspDto();
                disc.setTemplateType(TemplateTypeEnum.TEMPLATE_TYPE_DISC.ordinal());
                disc.setId("3396");
                templateRspDtos.add(box);
                templateRspDtos.add(frame);
                templateRspDtos.add(disc);
                result = templateRspDtos;

            }
        };
        DeviceQueryBean queryBean = new DeviceQueryBean();
        queryBean.setFrameDouble(1);
        queryBean.setFrameNo(1);
        queryBean.setBoardNo(1);
        DeviceEntity deviceEntity = mobileService.queryDeviceInfo(queryBean);
        Assert.assertTrue(deviceEntity != null);
    }

    /**
     * 上传设施模板信息
     *
     * @throws Exception 异常
     */
    @Test
    public void uploadDeviceTemplate() throws Exception {
        DeviceEntity deviceEntity = new DeviceEntity();
        //箱架模板
        BaseMould boxMould = new BaseMould();
        boxMould.setCol(1);
        boxMould.setRow(1);
        //框模板
        BaseMould frameMould = new BaseMould();
        frameMould.setCol(1);
        frameMould.setRow(1);
        //盘模板
        BaseMould boardMould = new BaseMould();
        boardMould.setCol(1);
        boardMould.setRow(1);
        deviceEntity.setBoxMould(boxMould);
        deviceEntity.setFrameMould(frameMould);
        deviceEntity.setBoardMould(boardMould);
        new Expectations() {
            {
                templateService.queryAllTemplate((TemplateReqDto) any);
                result = ResultUtils.success(boxTemplate);
            }
        };
        Result result = mobileService.uploadDeviceTemplate(deviceEntity);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));

        new Expectations() {
            {
                templateService.queryAllTemplate((TemplateReqDto) any);
                result = ResultUtils.success(boxTemplate2);
            }
        };
        Result result2 = mobileService.uploadDeviceTemplate(deviceEntity);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result2.getCode()));
    }

    /**
     * 查询所有模板信息
     *
     * @throws Exception 异常
     */
    @Test
    public void queryAllTemplate() throws Exception {
        new Expectations() {
            {
                templateService.queryAllTemplate((TemplateReqDto) any);
                result = ResultUtils.success(boxTemplate2);
            }
        };
        List<DeviceEntity> deviceEntities = mobileService.queryAllTemplate();
        Assert.assertTrue(deviceEntities != null && deviceEntities.size() > 0);
    }

    /**
     * 上传设施业务信息
     *
     * @throws Exception 异常
     */
    @Test
    public void uploadFacilityBusInfo() throws Exception {
        FacilityBusUploadDto busUploadDto = new FacilityBusUploadDto();
        //光交箱
        uploadDto.setUploadType(UploadTypeEnum.INSERT.ordinal());
        BeanUtils.copyProperties(uploadDto, uploadDto);
        busUploadDto.setDeviceType(DeviceType.Optical_Box.getCode());
        busUploadDto.setUploadType(1);
        busUploadDto.setBoxCodeRule(1);
        Result result = mobileService.uploadFacilityBusInfo(busUploadDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 查询设施业务信息
     *
     * @throws Exception 异常
     */
    @Test
    public void queryBusInfoByDeviceId() throws Exception {
        new Expectations() {
            {
                templateDao.queryFacilityInfoByCondition(anyString);
                List<TemplateReqDto> list = new ArrayList<>();
                TemplateReqDto dto = new TemplateReqDto();
                dto.setRelationId("relationId");
                dto.setDeviceId("deviceId");
                list.add(dto);
                result = list;

            }
        };
        Result result = mobileService.queryBusInfoByDeviceId("deviceId", "deviceType");
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 设施标签信息变更（针对标签损坏的场景，信息不变只更改ID）
     *
     * @throws Exception 异常
     */
    @Test
    public void changeFacilityLabel() throws Exception {
        new Expectations() {
            {
                facilityDao.queryBoxLabelById(anyString);
                List<BoxInfoBean> boxInfoBeans = new ArrayList<>();
                BoxInfoBean boxInfoBean = new BoxInfoBean();
                boxInfoBean.setBoxLabel("1025826521");
                boxInfoBeans.add(boxInfoBean);
                result = boxInfoBeans;
            }
        };
        mobileService.changeFacilityLabel("oldLabel", "newLabel", 1);
        new Expectations() {
            {
                facilityDao.queryBoardLabelById(anyString);
                List<BoardInfoBean> boardInfoBeans = new ArrayList<>();
                BoardInfoBean board = new BoardInfoBean();
                board.setBoxLabel("1025826521");
                boardInfoBeans.add(board);
                result = boardInfoBeans;
            }
        };
        mobileService.changeFacilityLabel("oldLabel", "newLabel", 2);
        new Expectations() {
            {
                facilityDao.queryPortLabelById(anyString);
                List<PortInfoBean> portInfoBeans = new ArrayList<>();
                PortInfoBean port = new PortInfoBean();
                port.setBoxLabel("1025826521");
                portInfoBeans.add(port);
                result = portInfoBeans;
            }
        };
        mobileService.changeFacilityLabel("oldLabel", "newLabel", 3);
    }

}