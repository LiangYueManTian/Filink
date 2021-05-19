package com.fiberhome.filink.rfid.service.template.impl;


import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfid.bean.template.RealPosition;
import com.fiberhome.filink.rfid.bean.template.RealPositionCommon;
import com.fiberhome.filink.rfid.bean.template.TemplateVO;
import com.fiberhome.filink.rfid.constant.AppConstant;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCode;
import com.fiberhome.filink.rfid.constant.template.TemplateConstant;
import com.fiberhome.filink.rfid.dao.template.TemplateDao;
import com.fiberhome.filink.rfid.enums.BoardPutStateEnum;
import com.fiberhome.filink.rfid.enums.BoardStateEnum;
import com.fiberhome.filink.rfid.enums.PortStateEnum;
import com.fiberhome.filink.rfid.enums.TemplateCodeRuleEnum;
import com.fiberhome.filink.rfid.enums.TemplateDeviceTypeEnum;
import com.fiberhome.filink.rfid.enums.TemplateSideEnum;
import com.fiberhome.filink.rfid.enums.TemplateTrendEnum;
import com.fiberhome.filink.rfid.enums.TemplateTypeEnum;
import com.fiberhome.filink.rfid.exception.BizException;
import com.fiberhome.filink.rfid.req.template.TemplateReqDto;
import com.fiberhome.filink.rfid.resp.template.TemplateRspDto;
import com.fiberhome.filink.rfid.utils.ResultI18Utils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模板帮助类
 *
 * @author liyj
 * @date 2019/5/23
 */
@Slf4j
@Component
public class TemplateHelper {
    /**
     * 箱的高度
     */
    @Value("${boxHeight}")
    private Integer boxHeight;
    /**
     * 箱的宽度
     */
    @Value("${boxWidth}")
    private Integer boxWidth;
    /**
     * box首坐标x
     */
    @Value("${boxCoordinateX}")
    private Integer firstCoordinateX;
    /**
     * box首坐标y
     */
    @Value("${boxCoordinateY}")
    private Integer firstCoordinateY;

    /**
     * 箱 A/B 面之间的偏移量 由此计算出B面的首坐标
     */
    @Value("${offset}")
    private Integer offset;

    /**
     * 箱与框的间距比率
     */
    @Value("${boxFrameApace}")
    private Double boxFrameApace;
    /**
     * 框与盘的间距比率
     */
    @Value("${frameDiscApace}")
    private Double frameDiscApace;
    /**
     * mysql 批量插入分割数
     */
    @Value("${mysqlBatchNum}")
    private Integer mysqlBatchNum;

    /**
     * TemplateDao
     */
    @Autowired
    private TemplateDao templateDao;
    /**
     * templateService
     */
    @Autowired
    private TemplateServiceImpl templateService;


    /**
     * 计算全部的坐标
     *
     * @param boxRealPositionList 箱坐标
     * @param boxTemplateVO       箱模板
     * @param deviceId            设施id
     * @param templateReqDto      全部模板数据
     * @param templateVOS
     */
    private void calcAllPosition(List<RealPosition> boxRealPositionList, TemplateVO boxTemplateVO,
                                 String deviceId, TemplateReqDto templateReqDto,
                                 List<TemplateRspDto> templateVOS) {
        // 计算框的坐标 和编号规则  这里得出的queen 坐标点即是 首坐标点
        List<RealPosition> frameRealPositionList = Lists.newArrayList();
        //盘的坐标集合
        List<RealPosition> discRealPositionList = Lists.newArrayList();
        //端口的坐标集合
        List<RealPosition> portRealPositionList = Lists.newArrayList();

        List<TemplateRspDto> frameTemplate = Lists.newArrayList();
        templateVOS.stream().forEach(obj -> {
            frameTemplate.addAll(obj.getChildTemplateList());
        });
        List<TemplateRspDto> discTemplate = Lists.newArrayList();
        frameTemplate.stream().forEach(obj -> {
            discTemplate.addAll(obj.getChildTemplateList());
        });
        boxRealPositionList.stream().forEach(box -> {
            //根据箱的A/B 面可以计算出框的关系数
            List<RealPosition> frameRealPosition = getRealPosition(box, TemplateDeviceTypeEnum.DEVICE_TYPE_FRAME,
                    boxTemplateVO.getCol(), boxTemplateVO.getRow(),
                    boxTemplateVO.getCol(), boxTemplateVO.getRow(),
                    TemplateSideEnum.TEMPLATE_SIDE_STATE_A);
            //计算queen 左上 行优 坐标 知道首坐标和间距 高宽(即是上一级已经计算好的)
            calcQueenCoordinate(box, TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal(),
                    boxTemplateVO.getCol(), boxTemplateVO.getRow(), frameRealPosition);
            frameRealPosition.stream().forEach(r -> {
                r.setCodeRule(templateReqDto.getFrameCodeRule());
                r.setTrend(templateReqDto.getFrameTrend());
                r.setDeviceId(deviceId);
                //框默认在位
                r.setState(TemplateConstant.DEFAULT_STATE);
                r.setSide(box.getSide());
                r.setBusState(AppConstant.BUS_BINDING_DEFAULT_STATE);
            });
            frameRealPositionList.addAll(frameRealPosition);
            //=========================================================================================
            // 计算 disc的编号规则和坐标点 根据frameRealPositon 中的首坐标来的 box 分A/B 面
            for (int i = 0; i < frameRealPosition.size(); i++) {
                RealPosition frame = frameRealPosition.get(i);
                TemplateVO frameTemp = frameTemplate.get(i);
                //获取盘的信息  盘的单双面 来自框 框只有A面
                // 盘是摆放方式 竖着放 则 盘的row和col 对调 体现在端口上
                boolean boardPutState = frameTemp.getPutState() == BoardPutStateEnum.STELLEN.ordinal();
                int discCol = frameTemp.getCol();
                int discRow = frameTemp.getRow();

                List<RealPosition> discRealPosition = getRealPosition(frame, TemplateDeviceTypeEnum.DEVICE_TYPE_DISC,
                        frameTemp.getCol(), frameTemp.getRow(),
                        discCol, discRow,
                        TemplateSideEnum.TEMPLATE_SIDE_STATE_A);
                //计算queen
                //横着
                calcQueenCoordinate(frame, TemplateTypeEnum.TEMPLATE_TYPE_FRAME.ordinal(),
                        discCol, discRow, discRealPosition);

                discRealPosition.stream().forEach(r -> {
                    r.setCodeRule(templateReqDto.getDiscCodeRule());
                    r.setTrend(templateReqDto.getDiscTrend());
                    r.setDeviceId(deviceId);
                    // 这里增加 所属框id
                    r.setFrameId(frame.getId());
                    // 所属箱id
                    r.setBoxId(frame.getParentId());
                    r.setPutState(frameTemp.getPutState());
                    r.setState(TemplateConstant.DEFAULT_STATE);
                    r.setBusState(AppConstant.BUS_BINDING_DEFAULT_STATE);
                });
                // 计算端口的坐标 这里需要考虑的是 端口分A/B面  摆放方式:横放/竖放
                for (int m = 0; m < discRealPosition.size(); m++) {
                    RealPosition discReal = discRealPosition.get(m);
                    TemplateVO discTemp = discTemplate.get(m);
                    //盘是否是A/B 面
                    boolean portSideFlag = discTemp.getReversible() == 2;
                    //计算A 面
                    portRealPositionList.addAll(getPortPosition(portSideFlag, boardPutState,
                            discReal, discTemp.getCol(), discTemp.getRow()));
                }
                discRealPositionList.addAll(discRealPosition);
            }
        });
        //保存Frame的坐标
        templateDao.saveRealPositionFrame(frameRealPositionList);
        //保存disc 的坐标
        templateDao.saveRealPositionDisc(discRealPositionList);
        //保存port 的坐标
        if (portRealPositionList.size() != 0) {
            List<List<RealPosition>> port = Lists.newArrayList();
            int count = getCount(portRealPositionList.size());
            for (int i = 0; i < count; i++) {
                port.add(portRealPositionList.stream().skip(i * mysqlBatchNum).limit(mysqlBatchNum)
                        .collect(Collectors.toList()));
            }
            port.stream().forEach(obj -> {
                templateDao.saveRealPositionPort(obj);
            });
        }
    }


    /**
     * 计算queen 坐标
     * 规则 从左上 行优() 根据编号坐标和间距来计算每个坐标
     *
     * @param parents       父
     * @param realPositions 已经计算好编号规则了
     */
    private void calcQueenCoordinate(RealPosition parents, Integer templateType,
                                     Integer col, Integer row,
                                     List<RealPosition> realPositions) {
        if (realPositions != null && realPositions.size() != 0) {
            //根据templateType 计算出间距
            realPositions.stream().forEach(obj -> {
                calcAbscissaByTemplateType(col, templateType, parents.getWidth(), parents.getAbscissa(), obj);
                calcOrdinateByTemplateType(row, templateType, parents.getHeight(), parents.getOrdinate(), obj);
            });
        }
    }

    /**
     * 计算模板的坐标
     *
     * @param templateReqDto templateReqDto
     * @return Result 返回结果
     */
    @Transactional
    public Result calcTemplatePosition(TemplateReqDto templateReqDto) {
        // 不考虑非标准模板 即list 中每个长宽都是一致的
        //1 保存箱模板时 计算全部的坐标和编号
        List<TemplateRspDto> allTemplate = templateDao.queryBoxTemplateById(templateReqDto.getBoxTemplateId());
        // 树结构的全部数据
        List<TemplateRspDto> templateData = templateService.getTemplateData(allTemplate, templateReqDto);
        //计算全部的高 宽
        RealPositionCommon common = calcWeightAndHeight(templateData);
        TemplateRspDto boxTemplateVO = templateData.get(0);
        //箱的单双面
        boolean boxSideFlag = boxTemplateVO.getReversible() == 2;
        String deviceId = templateReqDto.getDeviceId();
        // 开始计算箱的坐标
        List<RealPosition> boxRealPositionList = calcBoxRealPosition(boxSideFlag, deviceId, templateReqDto, boxTemplateVO.getId(), common);
        // 开始计算全部坐标
        calcAllPosition(boxRealPositionList, boxTemplateVO, deviceId, templateReqDto, templateData);
        return ResultI18Utils.convertSuccess(RfIdI18nConstant.SAVE_TEMPLATE_RELATION_SUCCESS);
    }

    /**
     * 计算箱的坐标
     *
     * @param boxSideFlag
     * @param deviceId
     * @param templateReqDto
     * @param boxTemplateId
     * @return
     */
    public List<RealPosition> calcBoxRealPosition(boolean boxSideFlag, String deviceId,
                                                  TemplateReqDto templateReqDto,
                                                  String boxTemplateId, RealPositionCommon common) {
        List<RealPosition> boxRealPositionList = Lists.newArrayList();
        List<TemplateReqDto> templateReqDtos = Lists.newArrayList();
        // 计算出箱的坐标
        // 箱的高宽 就是设定好的
        Double boxHeights = common.getBoxHeight();
        Double boxWidths = common.getBoxWidth();


        //3 计算出每个模板的高宽(盘中高宽 需要和摆放位置有关)
        Double boxFirstQueenX = Double.parseDouble(firstCoordinateX.toString());
        Double boxFirstQueenY = Double.parseDouble(firstCoordinateY.toString());
        //A/B 面的偏移量
        Double templateOffset = Double.parseDouble(offset.toString());

        //默认就是A面
        RealPosition boxRealPositionA = new RealPosition(NineteenUUIDUtils.uuid(), 1, 1, boxFirstQueenX, boxFirstQueenY,
                1, TemplateDeviceTypeEnum.DEVICE_TYPE_BOX.ordinal(), boxHeights, boxWidths,
                templateReqDto.getBoxTrend(), templateReqDto.getBoxCodeRule(), TemplateSideEnum.TEMPLATE_SIDE_STATE_A.ordinal(), boxTemplateId
                , TemplateConstant.TEMPLATE_PARENT, deviceId, TemplateConstant.DEFAULT_STATE, 1);
        boxRealPositionList.add(boxRealPositionA);

        templateReqDto.setRelationId(boxRealPositionA.getId());
        templateReqDto.setId(NineteenUUIDUtils.uuid());
        templateReqDto.setBoxTemplateId(boxTemplateId);
        templateReqDtos.add(templateReqDto);
        if (boxSideFlag) {
            //根据设定好的偏移量去横移 firstX 和firstY
            RealPosition boxRealPositionB = new RealPosition(NineteenUUIDUtils.uuid(), 1, 1, boxFirstQueenX + templateOffset,
                    boxFirstQueenY + templateOffset, 1, TemplateDeviceTypeEnum.DEVICE_TYPE_BOX.ordinal(), boxHeights, boxWidths,
                    templateReqDto.getBoxTrend(), templateReqDto.getBoxCodeRule(), TemplateSideEnum.TEMPLATE_SIDE_STATE_B.ordinal(), boxTemplateId,
                    TemplateConstant.TEMPLATE_PARENT, deviceId, TemplateConstant.DEFAULT_STATE, 1);
            boxRealPositionList.add(boxRealPositionB);
            TemplateReqDto templateReqDto1 = new TemplateReqDto();
            BeanUtils.copyProperties(templateReqDto, templateReqDto1);
            templateReqDto1.setRelationId(boxRealPositionB.getId());
            templateReqDto1.setId(NineteenUUIDUtils.uuid());
            templateReqDtos.add(templateReqDto1);
        }
        //保存箱的关系
        templateDao.saveRelationTemplate(templateReqDtos);
        //保存箱的坐标
        templateDao.saveRealPositionBox(boxRealPositionList);
        return boxRealPositionList;
    }

    /**
     * 计算端口的坐标
     *
     * @param portSideFlag  盘是否A、B 面
     * @param boardPutState 盘的摆放状态
     * @param discReal      上级盘坐标
     * @param col           盘模板的col
     * @param row           盘模板的row
     * @return 一个盘下的所有端口信息
     */
    public List<RealPosition> getPortPosition(boolean portSideFlag, boolean boardPutState,
                                              RealPosition discReal, Integer col, Integer row) {
        List<RealPosition> portReal = Lists.newArrayList();
        int portCol;
        int portRow;
        if (boardPutState) {
            portCol = row;
            portRow = col;
        } else {
            portCol = col;
            portRow = row;
        }
        portReal.addAll(getRealPosition(discReal, TemplateDeviceTypeEnum.DEVICE_TYPE_PORT,
                portCol, portRow,
                portCol, portRow,
                TemplateSideEnum.TEMPLATE_SIDE_STATE_A));
        if (portSideFlag) {
            //双面
            portReal.addAll(getRealPosition(discReal, TemplateDeviceTypeEnum.DEVICE_TYPE_PORT,
                    portCol, portRow,
                    portCol, portRow,
                    TemplateSideEnum.TEMPLATE_SIDE_STATE_B));
        }
        calcQueenCoordinate(discReal, TemplateTypeEnum.TEMPLATE_TYPE_DISC.ordinal(), portCol, portRow, portReal);

        // 端口新建的默认状态为 空闲
        portReal.forEach(obj -> {
            obj.setState(BoardStateEnum.REIGN.ordinal());
            // 这边需要增加 所属箱 框 盘信息
            obj.setBoxId(discReal.getBoxId());
            //横竖保持一致
            obj.setPutState(boardPutState ? BoardPutStateEnum.STELLEN.ordinal() : BoardPutStateEnum.LAY.ordinal());
            obj.setFrameId(discReal.getFrameId());
            obj.setPortState(PortStateEnum.FREE.ordinal());
            obj.setDiscId(discReal.getId());
            obj.setDeviceId(discReal.getDeviceId());
            obj.setDiscNum(discReal.getBusinessNum());
            obj.setBusState(AppConstant.BUS_BINDING_DEFAULT_STATE);
            obj.setBusBindingState(AppConstant.BUS_BINDING_DEFAULT);
            obj.setPortCableState(AppConstant.BUS_BINDING_DEFAULT_STATE);
        });
        return portReal;
    }


    /**
     * 计算App 端的坐标
     *
     * @param parents       父类数据
     * @param templateType  模板类型
     * @param col           col
     * @param row           row
     * @param realPositions 坐标集信息
     */
    public void appCalcQueenCoordinate(RealPosition parents, Integer templateType, Integer col,
                                       Integer row, RealPosition realPositions) {
        if (realPositions != null) {
            //根据templateType 计算出间距
            calcAbscissaByTemplateType(col, templateType, parents.getWidth(), parents.getAbscissa(), realPositions);
            calcOrdinateByTemplateType(row, templateType, parents.getHeight(), parents.getOrdinate(), realPositions);
        }
    }


    /**
     * 计算横坐标
     *
     * @param col          列数量
     * @param firstX       横坐标
     * @param templateType 模板类型
     * @param width        宽度
     * @param realPosition 关系图
     */
    private void calcAbscissaByTemplateType(Integer col, Integer templateType,
                                            Double width, Double firstX, RealPosition realPosition) {
        Double abscissa;
        Double widths = 0.0;
        if (TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal() == templateType) {
            // 箱和框 间距是一样的
            abscissa = width * boxFrameApace;
            widths = (width * (1 - boxFrameApace * 2)) / col;
            //横坐标
            realPosition.setAbscissa(convertDouble(firstX + abscissa + (realPosition.getColNo() - 1) * widths));
        } else if (templateType == TemplateTypeEnum.TEMPLATE_TYPE_FRAME.ordinal()) {
            abscissa = width * frameDiscApace;
            widths = (width * (1 - frameDiscApace * 2)) / col;
            //横坐标
            realPosition.setAbscissa(convertDouble(firstX + abscissa + (realPosition.getColNo() - 1) * widths));
        } else if (templateType == TemplateTypeEnum.TEMPLATE_TYPE_DISC.ordinal()) {
            // 这里的端口
            //间距为0.5 盘下的端口没有间距
            widths = (2 * width) / (3 * col + 1);
            abscissa = 0.5 * widths;
            //横坐标
            realPosition.setAbscissa(convertDouble(firstX + abscissa + 1.5 * (realPosition.getColNo() - 1) * widths));
        }
        realPosition.setWidth(convertDouble(widths));
    }

    /**
     * 根据模板类型来计算间距
     *
     * @param row          行数量
     * @param templateType 模板类型
     * @param height       高度
     * @param firstY       纵坐标x
     * @param realPosition 坐标集
     */
    private void calcOrdinateByTemplateType(Integer row, Integer templateType, Double height,
                                            Double firstY, RealPosition realPosition) {
        // 纵坐标间距
        Double ordinate;
        //纵向每个高度
        Double heights = 0.0;
        if (TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal() == templateType) {
            // 箱和框 间距是一样的
            ordinate = height * boxFrameApace;
            heights = (height * (1 - boxFrameApace * 2)) / row;
            //纵坐标
            realPosition.setOrdinate(convertDouble(firstY + ordinate + (realPosition.getRowNo() - 1) * heights));
        } else if (templateType == TemplateTypeEnum.TEMPLATE_TYPE_FRAME.ordinal()) {
            ordinate = height * frameDiscApace;
            heights = (height * (1 - frameDiscApace * 2)) / row;
            //纵坐标
            realPosition.setOrdinate(convertDouble(firstY + ordinate + (realPosition.getRowNo() - 1) * heights));
        } else if (templateType == TemplateTypeEnum.TEMPLATE_TYPE_DISC.ordinal()) {
            //间距为2 倍
            heights = (2 * height) / (3 * row + 1);
            ordinate = 0.5 * heights;
            realPosition.setOrdinate(convertDouble(firstY + ordinate + 1.4 * (realPosition.getRowNo() - 1) * heights));
        }
        realPosition.setHeight(convertDouble(heights));
    }


    /**
     * 根据模板计算出全部的长度和宽度
     *
     * @param templateData 全部模板数据
     * @return 箱框盘端口的高度和宽度
     */
    public RealPositionCommon calcWeightAndHeight(List<TemplateRspDto> templateData) {
        RealPositionCommon common = new RealPositionCommon();
        // 箱的高宽 就是设定好的
        double height = Double.parseDouble(boxHeight.toString());
        double width = Double.parseDouble(boxWidth.toString());
        if (templateData == null || templateData.size() == 0) {
            common.setBoxHeight(height);
            common.setBoxWidth(width);
            return common;
        }
        // 获取箱-框-盘 模板 不考虑非标准模板
        TemplateRspDto boxTemp = templateData.get(0);
        TemplateRspDto frameTemp = boxTemp.getChildTemplateList().get(0);
        TemplateRspDto discTemp = frameTemp.getChildTemplateList().get(0);

        double portWidth;
        double portHeight;
        double discWidth;
        double discHeight;

        // 端口的宽度
        double frameWidth = convertDouble((width * (1 - boxFrameApace * 2)) / boxTemp.getCol());
        double frameHeight = convertDouble((height * (1 - boxFrameApace * 2)) / boxTemp.getRow());

        // 高度
        boolean boardPutState = frameTemp.getPutState() == BoardPutStateEnum.STELLEN.ordinal();
        if (boardPutState) {
            // 竖着放
            discWidth = convertDouble((frameWidth * (1 - frameDiscApace * 2)) / frameTemp.getRow());
            discHeight = convertDouble((frameHeight * (1 - frameDiscApace * 2)) / frameTemp.getCol());
            portHeight = convertDouble((2 * discHeight) / (3 * discTemp.getCol() + 1));
            portWidth = convertDouble((2 * discWidth) / (3 * discTemp.getRow() + 1));
        } else {
            //横着放
            discWidth = convertDouble((frameWidth * (1 - frameDiscApace * 2)) / frameTemp.getCol());
            discHeight = convertDouble((frameHeight * (1 - frameDiscApace * 2)) / frameTemp.getRow());
            portHeight = convertDouble((2 * discHeight) / (3 * discTemp.getRow() + 1));
            portWidth = convertDouble((2 * discWidth) / (3 * discTemp.getCol() + 1));
        }
        //端口宽间距
        // 高度和宽度比率 尽量保持 1:1

        if (portHeight > portWidth) {
            portHeight = portWidth;
            common.setPortHeight(portHeight);
            common.setPortWidth(portWidth);
        } else {
            portWidth = portHeight;
            common.setPortHeight(portHeight);
            common.setPortWidth(portWidth);
        }

        double dw;
        double dh;
        if (boardPutState) {
            dw = portWidth * (3 * discTemp.getRow() + 1) / 2;
            dh = (portHeight * (3 * discTemp.getCol() + 1)) / 2;
        } else {
            dw = portWidth * (3 * discTemp.getCol() + 1) / 2;
            dh = (portHeight * (3 * discTemp.getRow() + 1)) / 2;
        }
        double fw = (dw * frameTemp.getCol()) / 1 - frameDiscApace * 2;
        double fh = (dh * frameTemp.getRow()) / 1 - frameDiscApace * 2;
        common.setDiscWidth(convertDouble(dw));
        common.setDiscHeight(convertDouble(dh));

        //框的宽度
        common.setFrameWidth(convertDouble(fw));
        common.setFrameHeight(convertDouble(fh));

        // 箱的宽度
        double bw = (fw * boxTemp.getCol()) / 1 - boxFrameApace * 2;
        double bh = (fh * boxTemp.getRow()) / 1 - boxFrameApace * 2;
        if (bw < boxWidth) {
            double lope = boxWidth / bw;
            bw = lope * bw;
            bh = lope * bh;
        }
        common.setBoxWidth(convertDouble(bw));
        common.setBoxHeight(convertDouble(bh));
        //查看设施的时候 放大实景图


        return common;
    }

    /**
     * 获取编号规则 通过父的 col row  trend codeRule
     *
     * @param parentPosition   父的坐标
     * @param deviceType       设施类型(TemplateDeviceTypeEnum)
     * @param templateSideEnum 单双面(TemplateSideEnum)
     * @return 坐标图
     */
    private List<RealPosition> getRealPosition(RealPosition parentPosition,
                                               TemplateDeviceTypeEnum deviceType,
                                               Integer parentCol, Integer parentRow,
                                               Integer col, Integer row,
                                               TemplateSideEnum templateSideEnum) {
        // 根据编号规则获取
        TemplateCodeRuleEnum ruleEnum = TemplateCodeRuleEnum.getTemplateCodeRuleByOrdinal(parentPosition.getCodeRule());
        // 行优还是列优
        boolean trendFlag = parentPosition.getTrend() == TemplateTrendEnum.TEMPLATE_TREND_COL.ordinal();
        List<RealPosition> realPositions;
        switch (ruleEnum) {
            case TEMPLATE_TREND_LEFT_UP:
                //左上
                realPositions = calcRealCodeByLeftUp(trendFlag, parentCol, parentRow, col, row, parentPosition.getId(),
                        templateSideEnum, deviceType);
                break;
            case TEMPLATE_TREND_LEFT_DOWN:
                //左下
                realPositions = calcRealCodeByLeftDown(trendFlag, parentCol, parentRow, col, row, parentPosition.getId(),
                        templateSideEnum, deviceType);
                break;
            case TEMPLATE_TREND_RIGHT_UP:
                //右上
                realPositions = calcRealCodeByRightUp(trendFlag, parentCol, parentRow, col, row, parentPosition.getId(),
                        templateSideEnum, deviceType);
                break;
            case TEMPLATE_TREND_RIGHT_DOWN:
                //右下
                realPositions = calcRealCodeByRightDown(trendFlag, parentCol, parentRow, col, row, parentPosition.getId(),
                        templateSideEnum, deviceType);
                break;
            default:
                log.info("not find template{}" + parentPosition.getCodeRule());
                throw new BizException(RfIdResultCode.TEMPLATE_CODE_RULE_IS_NULL,
                        RfIdI18nConstant.TEMPLATE_CODE_RULE_IS_NULL);
        }
        return realPositions;
    }

    /**
     * 计算编号规则 左上
     * 横+ 纵+
     *
     * @param trendFlag        行优 true /列优 false
     * @param col              行
     * @param row              列
     * @param parentId         父坐标id
     * @param templateSideEnum 单双面
     * @return List<RealPosition> size 和子集size 相同
     */
    private List<RealPosition> calcRealCodeByLeftUp(boolean trendFlag, Integer parentCol, Integer parentRow,
                                                    Integer col, Integer row,
                                                    String parentId,
                                                    TemplateSideEnum templateSideEnum, TemplateDeviceTypeEnum deviceType) {
        List<RealPosition> realPositions = Lists.newArrayList();
        Integer count = 1;
        if (trendFlag) {
            //列优  y 换列重新+ x换列+ 编号规则和走向有关系
            for (int i = 0; i < parentCol; i++) {
                for (int j = 0; j < parentRow; j++) {
                    RealPosition realPosition = new RealPosition();
                    realPosition.setParentId(parentId);
                    realPosition.setId(NineteenUUIDUtils.uuid());
                    Integer num = count++;
                    //业务编号 页面展示用 App 和前端都用这个字段
                    realPosition.setBusinessNum(num);
                    //模板编号 这里需要根据 col 和row
                    realPosition.setRealNo(num);
                    realPosition.setColNo(i + 1);
                    realPosition.setRowNo(j + 1);
                    realPosition.setRow(row);
                    realPosition.setCol(col);
                    realPosition.setSide(templateSideEnum.ordinal());
                    realPosition.setDeviceType(deviceType.ordinal());
                    realPositions.add(realPosition);
                }
            }

        } else {
            //行优 算列(x+ y换行+)
            for (int i = 0; i < parentRow; i++) {
                for (int j = 0; j < parentCol; j++) {
                    RealPosition realPosition = new RealPosition();
                    realPosition.setId(NineteenUUIDUtils.uuid());
                    realPosition.setParentId(parentId);
                    Integer num = count++;
                    realPosition.setBusinessNum(num);
                    //模板编号
                    realPosition.setRealNo(num);
                    realPosition.setColNo(j + 1);
                    realPosition.setRowNo(i + 1);
                    realPosition.setRow(row);
                    realPosition.setCol(col);
                    realPosition.setSide(templateSideEnum.ordinal());
                    realPosition.setDeviceType(deviceType.ordinal());
                    realPositions.add(realPosition);
                }
            }
        }

        return realPositions;
    }


    /**
     * 计算编号规则 左下
     * 横+ 纵 -
     *
     * @param trendFlag        行优 true /列优 false
     * @param col              行
     * @param row              列
     * @param parentId         父坐标id
     * @param templateSideEnum 单双面
     * @return List<RealPosition> size 和子集size 相同
     */
    private List<RealPosition> calcRealCodeByLeftDown(boolean trendFlag,
                                                      Integer parentCol, Integer parentRow,
                                                      Integer col, Integer row, String parentId,
                                                      TemplateSideEnum templateSideEnum, TemplateDeviceTypeEnum deviceType) {
        List<RealPosition> realPositions = Lists.newArrayList();
        Integer count = 1;
        if (trendFlag) {
            //列优   y- x换列+ 编号规则
            for (int i = 0; i < parentCol; i++) {
                for (int j = parentRow; j > 0; j--) {
                    RealPosition realPosition = new RealPosition();
                    realPosition.setParentId(parentId);
                    realPosition.setId(NineteenUUIDUtils.uuid());
                    Integer num = count++;
                    realPosition.setBusinessNum(num);
                    //模板编号
                    realPosition.setRealNo(num);
                    realPosition.setColNo(i + 1);
                    realPosition.setRowNo(j);
                    realPosition.setRow(row);
                    realPosition.setCol(col);
                    realPosition.setSide(templateSideEnum.ordinal());
                    realPosition.setDeviceType(deviceType.ordinal());
                    realPositions.add(realPosition);
                }
            }
        } else {
            //行优 x+ y换行-
            for (int i = parentRow; i > 0; i--) {
                for (int j = 0; j < parentCol; j++) {
                    RealPosition realPosition = new RealPosition();
                    realPosition.setId(NineteenUUIDUtils.uuid());
                    realPosition.setParentId(parentId);
                    Integer num = count++;
                    realPosition.setBusinessNum(num);
                    //模板编号
                    realPosition.setRealNo(num);
                    realPosition.setColNo(j + 1);
                    realPosition.setRowNo(i);
                    realPosition.setRow(row);
                    realPosition.setCol(col);
                    realPosition.setSide(templateSideEnum.ordinal());
                    realPosition.setDeviceType(deviceType.ordinal());
                    realPositions.add(realPosition);
                }
            }
        }

        return realPositions;
    }

    /**
     * 计算编号规则 右上
     * 横- 纵 +
     *
     * @param trendFlag        行优 true /列优 false
     * @param col              行
     * @param row              列
     * @param parentId         父坐标id
     * @param templateSideEnum 单双面
     * @return List<RealPosition> size 和子集size 相同
     */
    private List<RealPosition> calcRealCodeByRightUp(boolean trendFlag,
                                                     Integer parentCol, Integer parentRow,
                                                     Integer col, Integer row, String parentId,
                                                     TemplateSideEnum templateSideEnum, TemplateDeviceTypeEnum deviceType) {
        List<RealPosition> realPositions = Lists.newArrayList();
        Integer count = 1;
        if (trendFlag) {
            //列优 y+ x换列- 编号规则
            for (int i = parentCol; i > 0; i--) {
                for (int j = 0; j < parentRow; j++) {
                    RealPosition realPosition = new RealPosition();
                    realPosition.setParentId(parentId);
                    realPosition.setId(NineteenUUIDUtils.uuid());
                    Integer num = count++;
                    realPosition.setBusinessNum(num);
                    //模板编号
                    realPosition.setRealNo(num);
                    realPosition.setColNo(i);
                    realPosition.setRowNo(j + 1);
                    realPosition.setRow(row);
                    realPosition.setCol(col);
                    realPosition.setSide(templateSideEnum.ordinal());
                    realPosition.setDeviceType(deviceType.ordinal());
                    realPositions.add(realPosition);
                }
            }
        } else {
            //行优 (x- y换行+)
            for (int i = 0; i < parentRow; i++) {
                for (int j = parentCol; j > 0; j--) {
                    RealPosition realPosition = new RealPosition();
                    realPosition.setId(NineteenUUIDUtils.uuid());
                    realPosition.setParentId(parentId);
                    Integer num = count++;
                    realPosition.setBusinessNum(num);
                    //模板编号
                    realPosition.setRealNo(num);
                    realPosition.setColNo(j);
                    realPosition.setRowNo(i + 1);
                    realPosition.setRow(row);
                    realPosition.setCol(col);
                    realPosition.setSide(templateSideEnum.ordinal());
                    realPosition.setDeviceType(deviceType.ordinal());
                    realPositions.add(realPosition);
                }
            }
        }

        return realPositions;
    }

    /**
     * 计算编号规则 右下
     * 横- 纵 -
     *
     * @param trendFlag        行优 true /列优 false
     * @param col              行
     * @param row              列
     * @param parentId         父坐标id
     * @param templateSideEnum 单双面
     * @return List<RealPosition> size 和子集size 相同
     */
    private List<RealPosition> calcRealCodeByRightDown(boolean trendFlag,
                                                       Integer parentCol, Integer parentRow,
                                                       Integer col, Integer row, String parentId,
                                                       TemplateSideEnum templateSideEnum, TemplateDeviceTypeEnum deviceType) {
        List<RealPosition> realPositions = Lists.newArrayList();
        Integer count = 1;
        if (trendFlag) {
            //列优  y- x换列- 编号规则
            for (int i = parentCol; i > 0; i--) {
                for (int j = parentRow; j > 0; j--) {
                    RealPosition realPosition = new RealPosition();
                    realPosition.setParentId(parentId);
                    realPosition.setId(NineteenUUIDUtils.uuid());
                    Integer num = count++;
                    realPosition.setBusinessNum(num);
                    //模板编号
                    realPosition.setRealNo(num);
                    realPosition.setColNo(i);
                    realPosition.setRowNo(j);
                    realPosition.setRow(row);
                    realPosition.setCol(col);
                    realPosition.setSide(templateSideEnum.ordinal());
                    realPosition.setDeviceType(deviceType.ordinal());
                    realPositions.add(realPosition);
                }
            }
        } else {
            //行优 算列(x+ y换行+)
            for (int i = parentRow; i > 0; i--) {
                for (int j = parentCol; j > 0; j--) {
                    RealPosition realPosition = new RealPosition();
                    realPosition.setId(NineteenUUIDUtils.uuid());
                    realPosition.setParentId(parentId);
                    Integer num = count++;
                    realPosition.setBusinessNum(num);
                    //模板编号
                    realPosition.setRealNo(num);
                    realPosition.setColNo(j);
                    realPosition.setRowNo(i);
                    realPosition.setRow(row);
                    realPosition.setCol(col);
                    realPosition.setSide(templateSideEnum.ordinal());
                    realPosition.setDeviceType(deviceType.ordinal());
                    realPositions.add(realPosition);
                }
            }
        }
        return realPositions;
    }

    /**
     * 获取分割数
     *
     * @param listSize listSize
     * @return 分割数
     */
    private int getCount(int listSize) {
        int count = 1;
        if (listSize > mysqlBatchNum) {
            int m = listSize % mysqlBatchNum;
            int i = listSize / mysqlBatchNum;
            count = m == 0 ? i : i + 1;
        }
        return count;
    }

    /**
     * 坐标保存2位小数
     *
     * @param num 编号
     * @return Double
     */
    private Double convertDouble(Double num) {
        if (null == num) {
            return 0.0;
        }
        BigDecimal b = new BigDecimal(num);
        num = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return num;
    }

}
