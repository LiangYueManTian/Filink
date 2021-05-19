package com.fiberhome.filink.rfid.service.template.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.rfid.dao.template.TemplateDao;
import com.fiberhome.filink.rfid.req.template.TemplateReqDto;
import com.fiberhome.filink.rfid.resp.template.RealRspDto;
import com.fiberhome.filink.rfid.resp.template.TemplateRspDto;
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
 * 测试数据
 *
 * @author liyj
 * @date 2019/7/5
 */
@RunWith(JMockit.class)
public class TemplateHelperTest {

    /**
     * templateHelper
     */
    @Tested
    private TemplateHelper templateHelper;

    /**
     * 箱的高度
     */
    @Injectable
    private Integer boxHeight;
    /**
     * 箱的宽度
     */
    @Injectable
    private Integer boxWidth;
    /**
     * box首坐标x
     */
    @Injectable
    private Integer firstCoordinateX;
    /**
     * box首坐标y
     */
    @Injectable
    private Integer firstCoordinateY;

    /**
     * 箱 A/B 面之间的偏移量 由此计算出B面的首坐标
     */
    @Injectable
    private Integer offset;

    /**
     * 箱与框的间距比率
     */
    @Injectable
    private Double boxFrameApace;
    /**
     * 框与盘的间距比率
     */
    @Injectable
    private Double frameDiscApace;

    /**
     * mysql 批量插入分割数
     */
    @Injectable
    private Integer mysqlBatchNum;

    /**
     * TemplateDao
     */
    @Injectable
    private TemplateDao templateDao;
    /**
     * templateService
     */
    @Injectable
    private TemplateServiceImpl templateService;
    /**
     * templateDao
     */
    private TemplateReqDto templateReqDto;
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
        discs.setColNo(1);
        discs.setRowNo(1);
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
        port.setColNo(1);
        port.setRowNo(1);
        portRealPosition.add(port);

    }

    /**
     * 计算模板坐标
     *
     * @throws Exception exception
     */
    @Test
    public void calcTemplatePosition() throws Exception {

        ReflectionTestUtils.setField(templateHelper, "boxWidth", 123);
        ReflectionTestUtils.setField(templateHelper, "boxHeight", 123);
        ReflectionTestUtils.setField(templateHelper, "frameDiscApace", 0.02);
        ReflectionTestUtils.setField(templateHelper, "boxFrameApace", 0.02);
        ReflectionTestUtils.setField(templateHelper, "mysqlBatchNum", 1000);

        ReflectionTestUtils.setField(templateHelper, "firstCoordinateX", 0);
        ReflectionTestUtils.setField(templateHelper, "firstCoordinateY", 0);
        ReflectionTestUtils.setField(templateHelper, "offset", 123);

        new Expectations() {
            {
                templateDao.queryBoxTemplateById(anyString);
                result = templateVOS;

                templateService.getTemplateData((List<TemplateRspDto>) any, (TemplateReqDto) any);
                result = templateList;
            }
        };
        TemplateReqDto reqDto = new TemplateReqDto();
        reqDto.setBoxCodeRule(0);
        reqDto.setBoxTrend(0);

        reqDto.setFrameCodeRule(1);
        reqDto.setFrameTrend(1);

        reqDto.setDiscTrend(2);
        reqDto.setDiscCodeRule(2);

        reqDto.setDeviceId("deviceId");
        reqDto.setBoxTemplateId("boxId");

        Result result = templateHelper.calcTemplatePosition(reqDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }


    /**
     * 计算模板坐标
     *
     * @throws Exception exception
     */
    @Test
    public void calcTemplatePosition001() throws Exception {

        ReflectionTestUtils.setField(templateHelper, "boxWidth", 123);
        ReflectionTestUtils.setField(templateHelper, "boxHeight", 123);
        ReflectionTestUtils.setField(templateHelper, "frameDiscApace", 0.02);
        ReflectionTestUtils.setField(templateHelper, "boxFrameApace", 0.02);
        ReflectionTestUtils.setField(templateHelper, "mysqlBatchNum", 1000);

        ReflectionTestUtils.setField(templateHelper, "firstCoordinateX", 0);
        ReflectionTestUtils.setField(templateHelper, "firstCoordinateY", 0);
        ReflectionTestUtils.setField(templateHelper, "offset", 123);

        new Expectations() {
            {
                templateDao.queryBoxTemplateById(anyString);
                result = templateVOS;

                templateService.getTemplateData((List<TemplateRspDto>) any, (TemplateReqDto) any);
                result = templateList;
            }
        };
        TemplateReqDto reqDto = new TemplateReqDto();
        reqDto.setBoxCodeRule(3);
        reqDto.setBoxTrend(3);

        reqDto.setFrameCodeRule(1);
        reqDto.setFrameTrend(0);

        reqDto.setDiscTrend(2);
        reqDto.setDiscCodeRule(0);

        reqDto.setDeviceId("deviceId");
        reqDto.setBoxTemplateId("boxId");

        Result result = templateHelper.calcTemplatePosition(reqDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 计算模板坐标
     *
     * @throws Exception exception
     */
    @Test
    public void calcTemplatePosition002() throws Exception {

        ReflectionTestUtils.setField(templateHelper, "boxWidth", 123);
        ReflectionTestUtils.setField(templateHelper, "boxHeight", 123);
        ReflectionTestUtils.setField(templateHelper, "frameDiscApace", 0.02);
        ReflectionTestUtils.setField(templateHelper, "boxFrameApace", 0.02);
        ReflectionTestUtils.setField(templateHelper, "mysqlBatchNum", 1000);

        ReflectionTestUtils.setField(templateHelper, "firstCoordinateX", 0);
        ReflectionTestUtils.setField(templateHelper, "firstCoordinateY", 0);
        ReflectionTestUtils.setField(templateHelper, "offset", 123);

        new Expectations() {
            {
                templateDao.queryBoxTemplateById(anyString);
                result = templateVOS;

                templateService.getTemplateData((List<TemplateRspDto>) any, (TemplateReqDto) any);
                result = templateList;
            }
        };
        TemplateReqDto reqDto = new TemplateReqDto();
        reqDto.setBoxCodeRule(2);
        reqDto.setBoxTrend(0);

        reqDto.setFrameCodeRule(2);
        reqDto.setFrameTrend(0);

        reqDto.setDiscTrend(2);
        reqDto.setDiscCodeRule(0);

        reqDto.setDeviceId("deviceId");
        reqDto.setBoxTemplateId("boxId");

        Result result = templateHelper.calcTemplatePosition(reqDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 计算模板坐标
     *
     * @throws Exception exception
     */
    @Test
    public void calcTemplatePosition003() throws Exception {

        ReflectionTestUtils.setField(templateHelper, "boxWidth", 123);
        ReflectionTestUtils.setField(templateHelper, "boxHeight", 123);
        ReflectionTestUtils.setField(templateHelper, "frameDiscApace", 0.02);
        ReflectionTestUtils.setField(templateHelper, "boxFrameApace", 0.02);
        ReflectionTestUtils.setField(templateHelper, "mysqlBatchNum", 1000);

        ReflectionTestUtils.setField(templateHelper, "firstCoordinateX", 0);
        ReflectionTestUtils.setField(templateHelper, "firstCoordinateY", 0);
        ReflectionTestUtils.setField(templateHelper, "offset", 123);

        new Expectations() {
            {
                templateDao.queryBoxTemplateById(anyString);
                result = templateVOS;

                templateService.getTemplateData((List<TemplateRspDto>) any, (TemplateReqDto) any);
                result = templateList;
            }
        };
        TemplateReqDto reqDto = new TemplateReqDto();
        reqDto.setBoxCodeRule(3);
        reqDto.setBoxTrend(0);

        reqDto.setFrameCodeRule(3);
        reqDto.setFrameTrend(0);

        reqDto.setDiscTrend(2);
        reqDto.setDiscCodeRule(1);

        reqDto.setDeviceId("deviceId");
        reqDto.setBoxTemplateId("boxId");

        Result result = templateHelper.calcTemplatePosition(reqDto);
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 计算坐标
     *
     * @throws Exception
     */
    @Test
    public void appCalcQueenCoordinate() throws Exception {
        templateHelper.appCalcQueenCoordinate(discRealPosition.get(0), 2, 1, 1, portRealPosition.get(0));
    }


}