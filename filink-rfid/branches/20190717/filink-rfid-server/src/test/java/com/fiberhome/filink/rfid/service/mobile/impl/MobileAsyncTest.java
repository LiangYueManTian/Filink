package com.fiberhome.filink.rfid.service.mobile.impl;

import com.fiberhome.filink.rfid.bean.facility.BaseMould;
import com.fiberhome.filink.rfid.bean.facility.DeviceUploadDto;
import com.fiberhome.filink.rfid.constant.RfIdResultCodeConstant;
import com.fiberhome.filink.rfid.dao.template.TemplateDao;
import com.fiberhome.filink.rfid.exception.BizException;
import com.fiberhome.filink.rfid.resp.template.RealRspDto;
import com.fiberhome.filink.rfid.resp.template.TemplateRspDto;
import com.fiberhome.filink.rfid.service.template.impl.TemplateHelper;
import com.fiberhome.filink.rfid.service.template.impl.TemplateServiceImpl;
import com.google.common.collect.Lists;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

/**
 * @author liyj
 * @date 2019/7/15
 */
@RunWith(JMockit.class)
public class MobileAsyncTest {

    /**
     * mobileAsync
     */
    @Tested
    private MobileAsync mobileAsync;

    /**
     * Dao 层
     */
    @Injectable
    private TemplateDao templateDao;

    /**
     * 模板帮助类
     */
    @Injectable
    private TemplateHelper templateHelper;
    /**
     * 模板服务类
     */
    @Injectable
    private TemplateServiceImpl templateService;

    /**
     * mysql 批量插入分割数
     */
    @Injectable
    private Integer mysqlBatchNum;

    /**
     * templateVos
     */
    private List<TemplateRspDto> templateVOS = Lists.newArrayList();
    private List<TemplateRspDto> templateList = Lists.newArrayList();

    /**
     * 初始化数据 boxReal
     */
    private List<RealRspDto> boxRealPosition = Lists.newArrayList();
    /**
     * 初始化数据 frameReal
     */
    private List<RealRspDto> frameRealPosition = Lists.newArrayList();
    /**
     * 初始化数据 discReal
     */
    private List<RealRspDto> discRealPosition = Lists.newArrayList();
    /**
     * 初始化数据 portReal
     */
    private List<RealRspDto> portRealPosition = Lists.newArrayList();


    /**
     * 初始化数据 boxReal
     */
    private List<RealRspDto> boxR = Lists.newArrayList();
    /**
     * 初始化数据 frameReal
     */
    private List<RealRspDto> frameR = Lists.newArrayList();
    /**
     * 初始化数据 discReal
     */
    private List<RealRspDto> discR = Lists.newArrayList();
    /**
     * 初始化数据 portReal
     */
    private List<RealRspDto> portR = Lists.newArrayList();

    /**
     * 箱的坐标
     */
    private RealRspDto boxs = new RealRspDto();
    /**
     * 框的坐标
     */
    private RealRspDto frames = new RealRspDto();
    /**
     * 盘的坐标
     */
    private RealRspDto discs = new RealRspDto();
    /**
     * 端口的坐标
     */
    private RealRspDto port = new RealRspDto();


    /**
     * 初始化数据
     *
     * @throws Exception exception
     */
    @Before
    public void setUp() throws Exception {


        List<TemplateRspDto> fc = Lists.newArrayList();
        List<TemplateRspDto> bc = Lists.newArrayList();
        TemplateRspDto disc = new TemplateRspDto();
        disc.setId("discId");
        disc.setCol(1);
        disc.setRow(1);
        disc.setName("disc");
        disc.setTemplateType(2);
        disc.setPutState(1);
        disc.setReversible(1);
        fc.add(disc);
        templateVOS.add(disc);

        TemplateRspDto frame = new TemplateRspDto();
        frame.setId("frameId");
        frame.setCol(1);
        frame.setRow(1);
        frame.setName("frame");
        frame.setTemplateType(1);
        frame.setPutState(1);
        frame.setReversible(1);
        frame.setChildTemplateId("discId,discId");
        frame.setChildTemplateList(fc);
        bc.add(frame);
        templateVOS.add(frame);


        TemplateRspDto box = new TemplateRspDto();
        box.setId("boxId");
        box.setCol(1);
        box.setRow(1);
        box.setName("box");
        box.setTemplateType(0);
        box.setPutState(1);
        box.setReversible(2);
        box.setChildTemplateId("frameId,frameId");
        box.setChildTemplateList(bc);
        templateVOS.add(box);
        templateList.add(box);


        boxs.setId("boxId");
        boxs.setParentId("root");
        boxs.setCodeRule(0);
        boxs.setTrend(0);
        boxRealPosition.add(boxs);
        boxR.add(boxs);


        frames.setId("frameId");
        frames.setParentId("boxId");
        frames.setWidth(100.00);
        frames.setHeight(200.00);
        frames.setAbscissa(12.00);
        frames.setOrdinate(13.00);
        frames.setRealNo(1);
        frames.setState(1);
        frames.setSide(0);
        frames.setRealNo(2);
        frameRealPosition.add(frames);
        frameR.add(frames);

        discs.setState(1);
        discs.setParentId("frameId");
        discs.setId("discId");
        discs.setPutState(1);
        discs.setColNo(1);
        discs.setRowNo(1);
        discs.setWidth(100.00);
        discs.setHeight(200.00);
        discs.setAbscissa(12.00);
        discs.setOrdinate(13.00);
        discs.setRealNo(2);
        discRealPosition.add(discs);
        discR.add(discs);


        port.setId("portId");
        port.setParentId("discId");
        port.setWidth(100.00);
        port.setHeight(200.00);
        port.setAbscissa(12.00);
        port.setOrdinate(13.00);
        port.setColNo(1);
        port.setRowNo(1);
        port.setRealNo(1);
        portRealPosition.add(port);
        portR.add(port);
    }


    /**
     * 计算原来的坐标
     *
     * @throws Exception
     */
    @Test
    public void calcOldPosition001() throws Exception {

        try {
            mobileAsync.calcOldPosition(null, null, null, null);
        } catch (BizException e) {
            Assert.assertTrue(e.getCode().equals(RfIdResultCodeConstant.NOT_QUERY_DATA));
        }


        new Expectations() {
            {
                templateDao.queryFrameReal((List<String>) any);
                result = null;
            }
        };
        try {
            mobileAsync.calcOldPosition(null, boxRealPosition, null, null);
        } catch (BizException e) {
            Assert.assertTrue(e.getCode().equals(RfIdResultCodeConstant.NOT_QUERY_DATA));
        }


        new Expectations() {
            {
                templateDao.queryFrameReal((List<String>) any);
                result = frameRealPosition;

                templateDao.queryDiscReal((List<String>) any);
                result = null;
            }
        };
        try {
            mobileAsync.calcOldPosition(null, boxRealPosition, null, null);
        } catch (BizException e) {
            Assert.assertTrue(e.getCode().equals(RfIdResultCodeConstant.NOT_QUERY_DATA));
        }

        ReflectionTestUtils.setField(mobileAsync, "mysqlBatchNum", 1000);
        new Expectations() {
            {
                templateDao.queryFrameReal((List<String>) any);
                result = frameRealPosition;

                templateDao.queryDiscReal((List<String>) any);
                result = discRealPosition;

                templateDao.queryPortReal((List<String>) any);
                result = portRealPosition;

            }
        };
        DeviceUploadDto uploadDto = new DeviceUploadDto();
        uploadDto.setBoxTemplateId("boxTemplateId");
        uploadDto.setDeviceId("deviceId");
        uploadDto.setBoxCodeRule(0);
        uploadDto.setBoxTrend(1);

        uploadDto.setDiscCodeRule(0);
        uploadDto.setDiscTrend(0);

        uploadDto.setFrameCodeRule(0);
        uploadDto.setFrameTrend(0);

        BaseMould boxMould = new BaseMould();
        boxMould.setCol(1);
        boxMould.setReversible(1);
        boxMould.setName("");
        boxMould.setId("id");
        boxMould.setRow(1);
        boxMould.setPutState(1);

        BaseMould frameMould = new BaseMould();
        frameMould.setCol(1);
        frameMould.setReversible(1);
        frameMould.setName("");
        frameMould.setId("id");
        frameMould.setRow(1);
        frameMould.setPutState(1);


        BaseMould boardMould = new BaseMould();
        boardMould.setCol(1);
        boardMould.setReversible(1);
        boardMould.setName("");
        boardMould.setId("id");
        boardMould.setRow(1);
        boardMould.setPutState(1);

        uploadDto.setBoxMould(boxMould);
        uploadDto.setBoardMould(boardMould);
        uploadDto.setFrameMould(frameMould);

        mobileAsync.calcOldPosition(uploadDto, boxRealPosition, frameR, discR);

    }

    /**
     * 计算原来的坐标
     *
     * @throws Exception
     */
    @Test
    public void calcOldPosition002() throws Exception {

        try {
            mobileAsync.calcOldPosition(null, null, null, null);
        } catch (BizException e) {
            Assert.assertTrue(e.getCode().equals(RfIdResultCodeConstant.NOT_QUERY_DATA));
        }


        new Expectations() {
            {
                templateDao.queryFrameReal((List<String>) any);
                result = null;
            }
        };
        try {
            mobileAsync.calcOldPosition(null, boxRealPosition, null, null);
        } catch (BizException e) {
            Assert.assertTrue(e.getCode().equals(RfIdResultCodeConstant.NOT_QUERY_DATA));
        }


        new Expectations() {
            {
                templateDao.queryFrameReal((List<String>) any);
                result = frameRealPosition;

                templateDao.queryDiscReal((List<String>) any);
                result = null;
            }
        };
        try {
            mobileAsync.calcOldPosition(null, boxRealPosition, null, null);
        } catch (BizException e) {
            Assert.assertTrue(e.getCode().equals(RfIdResultCodeConstant.NOT_QUERY_DATA));
        }

        ReflectionTestUtils.setField(mobileAsync, "mysqlBatchNum", 1000);
        new Expectations() {
            {
                templateDao.queryFrameReal((List<String>) any);
                result = frameRealPosition;

                templateDao.queryDiscReal((List<String>) any);
                result = discRealPosition;

                templateDao.queryPortReal((List<String>) any);
                result = portRealPosition;

            }
        };
        DeviceUploadDto uploadDto = new DeviceUploadDto();
        uploadDto.setBoxTemplateId("boxTemplateId");
        uploadDto.setDeviceId("deviceId");
        uploadDto.setBoxCodeRule(0);
        uploadDto.setBoxTrend(0);

        uploadDto.setDiscCodeRule(1);
        uploadDto.setDiscTrend(1);

        uploadDto.setFrameCodeRule(2);
        uploadDto.setFrameTrend(1);

        BaseMould boxMould = new BaseMould();
        boxMould.setCol(1);
        boxMould.setReversible(1);
        boxMould.setName("");
        boxMould.setId("id");
        boxMould.setRow(1);
        boxMould.setPutState(1);

        BaseMould frameMould = new BaseMould();
        frameMould.setCol(1);
        frameMould.setReversible(1);
        frameMould.setName("");
        frameMould.setId("id");
        frameMould.setRow(1);
        frameMould.setPutState(1);


        BaseMould boardMould = new BaseMould();
        boardMould.setCol(1);
        boardMould.setReversible(1);
        boardMould.setName("");
        boardMould.setId("id");
        boardMould.setRow(1);
        boardMould.setPutState(1);

        uploadDto.setBoxMould(boxMould);
        uploadDto.setBoardMould(boardMould);
        uploadDto.setFrameMould(frameMould);

        mobileAsync.calcOldPosition(uploadDto, boxRealPosition, frameR, discR);

    }

    /**
     * 计算新的坐标
     *
     * @throws Exception
     */
    @Test
    public void calcNewPosition() throws Exception {
        try {
            mobileAsync.calcNewPosition(null, null, null, null);
        } catch (BizException e) {
            Assert.assertTrue(e.getCode().equals(RfIdResultCodeConstant.ADD_DEVICE_ENTITY_PARAM_BOX_DATA_NULL));
        }

        try {
            mobileAsync.calcNewPosition(null, boxRealPosition, null, null);
        } catch (BizException e) {
            Assert.assertTrue(e.getCode().equals(RfIdResultCodeConstant.ADD_DEVICE_ENTITY_PARAM_NEW_FRAME_DATA_NULL));
        }

        try {
            mobileAsync.calcNewPosition(null, boxRealPosition, frameRealPosition, null);
        } catch (BizException e) {
            Assert.assertTrue(e.getCode().equals(RfIdResultCodeConstant.ADD_DEVICE_ENTITY_PARAM_NEW_DISC_DATA_NULL));
        }
        DeviceUploadDto uploadDto = new DeviceUploadDto();
        uploadDto.setBoxTemplateId("boxTemplateId");
        uploadDto.setDeviceId("deviceId");
        uploadDto.setBoxCodeRule(3);
        uploadDto.setBoxTrend(3);

        uploadDto.setDiscCodeRule(3);
        uploadDto.setDiscTrend(1);

        uploadDto.setFrameCodeRule(2);
        uploadDto.setFrameTrend(1);

        BaseMould boxMould = new BaseMould();
        boxMould.setCol(1);
        boxMould.setReversible(1);
        boxMould.setName("");
        boxMould.setId("id");
        boxMould.setRow(1);
        boxMould.setPutState(1);

        BaseMould frameMould = new BaseMould();
        frameMould.setCol(1);
        frameMould.setReversible(1);
        frameMould.setName("");
        frameMould.setId("id");
        frameMould.setRow(1);
        frameMould.setPutState(1);


        BaseMould boardMould = new BaseMould();
        boardMould.setCol(1);
        boardMould.setReversible(1);
        boardMould.setName("");
        boardMould.setId("id");
        boardMould.setRow(1);
        boardMould.setPutState(1);

        uploadDto.setBoxMould(boxMould);
        uploadDto.setBoardMould(boardMould);
        uploadDto.setFrameMould(frameMould);
        mobileAsync.calcNewPosition(uploadDto, boxRealPosition, frameRealPosition, discRealPosition);

    }
    /**
     * 计算新的坐标
     *
     * @throws Exception
     */
    @Test
    public void calcNewPosition001() throws Exception {
        try {
            mobileAsync.calcNewPosition(null, null, null, null);
        } catch (BizException e) {
            Assert.assertTrue(e.getCode().equals(RfIdResultCodeConstant.ADD_DEVICE_ENTITY_PARAM_BOX_DATA_NULL));
        }

        try {
            mobileAsync.calcNewPosition(null, boxRealPosition, null, null);
        } catch (BizException e) {
            Assert.assertTrue(e.getCode().equals(RfIdResultCodeConstant.ADD_DEVICE_ENTITY_PARAM_NEW_FRAME_DATA_NULL));
        }

        try {
            mobileAsync.calcNewPosition(null, boxRealPosition, frameRealPosition, null);
        } catch (BizException e) {
            Assert.assertTrue(e.getCode().equals(RfIdResultCodeConstant.ADD_DEVICE_ENTITY_PARAM_NEW_DISC_DATA_NULL));
        }
        DeviceUploadDto uploadDto = new DeviceUploadDto();
        uploadDto.setBoxTemplateId("boxTemplateId");
        uploadDto.setDeviceId("deviceId");
        uploadDto.setBoxCodeRule(2);
        uploadDto.setBoxTrend(0);

        uploadDto.setDiscCodeRule(1);
        uploadDto.setDiscTrend(1);

        uploadDto.setFrameCodeRule(1);
        uploadDto.setFrameTrend(0);

        BaseMould boxMould = new BaseMould();
        boxMould.setCol(1);
        boxMould.setReversible(1);
        boxMould.setName("");
        boxMould.setId("id");
        boxMould.setRow(1);
        boxMould.setPutState(1);

        BaseMould frameMould = new BaseMould();
        frameMould.setCol(1);
        frameMould.setReversible(1);
        frameMould.setName("");
        frameMould.setId("id");
        frameMould.setRow(1);
        frameMould.setPutState(1);


        BaseMould boardMould = new BaseMould();
        boardMould.setCol(1);
        boardMould.setReversible(1);
        boardMould.setName("");
        boardMould.setId("id");
        boardMould.setRow(1);
        boardMould.setPutState(1);

        uploadDto.setBoxMould(boxMould);
        uploadDto.setBoardMould(boardMould);
        uploadDto.setFrameMould(frameMould);
        mobileAsync.calcNewPosition(uploadDto, boxRealPosition, frameRealPosition, discRealPosition);

    }
}