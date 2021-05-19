package com.fiberhome.filink.rfid.service.mobile.impl;

import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.rfid.bean.facility.BaseMould;
import com.fiberhome.filink.rfid.bean.facility.DeviceUploadDto;
import com.fiberhome.filink.rfid.bean.template.RealPosition;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCodeConstant;
import com.fiberhome.filink.rfid.dao.template.TemplateDao;
import com.fiberhome.filink.rfid.enums.BoardPutStateEnum;
import com.fiberhome.filink.rfid.enums.TemplateCodeRuleEnum;
import com.fiberhome.filink.rfid.enums.TemplateSideEnum;
import com.fiberhome.filink.rfid.enums.TemplateTypeEnum;
import com.fiberhome.filink.rfid.exception.BizException;
import com.fiberhome.filink.rfid.req.template.TemplateReqDto;
import com.fiberhome.filink.rfid.resp.template.RealRspDto;
import com.fiberhome.filink.rfid.service.template.impl.TemplateHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * App 端 异步计算
 *
 * @author liyj
 * @date 2019/6/24
 */
@Component
@Slf4j
public class MobileAsync {


    /**
     * Dao 层
     */
    @Autowired
    private TemplateDao templateDao;

    /**
     * 模板帮助类
     */
    @Autowired
    private TemplateHelper templateHelper;

    /**
     * mysql 批量插入分割数
     */
    @Value("${mysqlBatchNum}")
    private Integer mysqlBatchNum;

    /**
     * 刷原来的坐标
     *
     * @param uploadDto   请求数据
     * @param boxReal     箱的信息
     * @param upFrameList 修改后的框信息
     * @param upDiscList  修改后的盘信息
     */
    @Async
    public void calcOldPosition(DeviceUploadDto uploadDto, List<RealRspDto> boxReal, List<RealRspDto> upFrameList,
                                List<RealRspDto> upDiscList) {
        log.info("App update template Start!");
        // 这里需要将 新增的框和ab 箱关联
        if (boxReal == null || boxReal.size() == 0) {
            log.debug("calcOldPosition is boxReal is null code is line 72");
            throw new BizException(RfIdResultCodeConstant.NOT_QUERY_DATA, RfIdI18nConstant.NOT_QUERY_DATA);
        }
        List<String> boxId = boxReal.stream().map(RealRspDto::getId).collect(Collectors.toList());
        //3 boxReal 获取box 坐标后

        // 获取框的坐标
        List<RealRspDto> frameRealPosition = templateDao.queryFrameReal(boxId);
        if (frameRealPosition == null || frameRealPosition.size() == 0) {
            log.debug("calcOldPosition is query frameRealPosition is null,parme boxId {}" + JSON.toJSONString(boxId));
            throw new BizException(RfIdResultCodeConstant.NOT_QUERY_DATA, RfIdI18nConstant.NOT_QUERY_DATA);
        }

        // 将计算行列的信息丢到 所有框坐标中
        List<String> frameId = frameRealPosition.stream().map(RealRspDto::getId).collect(Collectors.toList());
        // 获取盘的坐标

        List<RealRspDto> discRealPosition = templateDao.queryDiscReal(frameId);
        if (discRealPosition == null || discRealPosition.size() == 0) {
            log.debug("calcOldPosition is query discRealPosition is null,parme frameId {}" + JSON.toJSONString(frameId));
            throw new BizException(RfIdResultCodeConstant.NOT_QUERY_DATA, RfIdI18nConstant.NOT_QUERY_DATA);
        }

        // 获取端口的坐标
        List<String> discId = discRealPosition.stream().map(RealRspDto::getId).collect(Collectors.toList());
        List<RealRspDto> portRealPosition = templateDao.queryPortReal(discId);

        Map<String, RealRspDto> frames = upFrameList.stream().collect(Collectors.toMap(RealRspDto::getId, obj -> obj));
        Map<String, RealRspDto> discs = upDiscList.stream().collect(Collectors.toMap(RealRspDto::getId, obj -> obj));

        // 开始刷坐标 箱的高度和首坐标不会变更 原来的坐标 只做修改
        boxReal.stream().forEach(boxs -> {
            //获取根据boxId 获取下面的框
            frameRealPosition.stream().filter(obj -> obj.getParentId() != boxs.getId()).forEach(frame -> {

                //这里的模板编号变了  frame 和框需要重新 根据模板编号刷colNo 和rowNo
                RealRspDto frameReal = frames.get(frame.getId());
                frame.setRealNo(frameReal.getRealNo());
                frame.setState(frameReal.getState());
                BaseMould boxMould = uploadDto.getBoxMould();
                calcRowNoByModeCode(uploadDto.getBoxTrend(), uploadDto.getBoxCodeRule(), boxMould.getCol(), boxMould.getRow(), frame);

                // 刷框queen 坐标
                templateHelper.appCalcQueenCoordinate(boxs, TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal(),
                        uploadDto.getBoxMould().getCol(), uploadDto.getBoxMould().getRow(), frame);
                //刷盘的坐标
                discRealPosition.stream().filter(obj -> obj.getParentId() != frame.getId()).forEach(disc -> {
                    //这里的模板编号变了
                    RealRspDto discReal = discs.get(disc.getId());
                    disc.setRealNo(discReal.getRealNo());
                    disc.setState(discReal.getState());
                    BaseMould frameMould = uploadDto.getFrameMould();
                    calcRowNoByModeCode(uploadDto.getFrameTrend(), uploadDto.getFrameCodeRule(), frameMould.getCol(), frameMould.getRow(), disc);

                    templateHelper.appCalcQueenCoordinate(frame, TemplateTypeEnum.TEMPLATE_TYPE_FRAME.ordinal(),
                            uploadDto.getFrameMould().getCol(), uploadDto.getFrameMould().getRow(), disc);
                    if (disc.getFrameId() == null) {
                        disc.setCodeRule(uploadDto.getDiscCodeRule());
                        disc.setTrend(uploadDto.getDiscTrend());
                        disc.setDeviceId(uploadDto.getDeviceId());
                        // 这里增加 所属框id
                        disc.setFrameId(frame.getId());
                        // 所属箱id
                        disc.setBoxId(frame.getParentId());
                        disc.setSide(TemplateSideEnum.TEMPLATE_SIDE_STATE_A.ordinal());
                    }
                    //刷端口的坐标
                    if (portRealPosition == null || portRealPosition.size() == 0) {
                        log.debug("calcOldPosition is query portRealPosition is null,parme discId {}" + JSON.toJSONString(discId));
                        return;
                    }
                    portRealPosition.stream().filter(obj -> obj.getParentId() != disc.getId()).forEach(port -> {
                        templateHelper.appCalcQueenCoordinate(disc, TemplateTypeEnum.TEMPLATE_TYPE_DISC.ordinal(),
                                uploadDto.getBoardMould().getCol(), uploadDto.getBoardMould().getRow(), port);
                        if (port.getBoxId() == null) {
                            port.setBoxId(boxs.getBoxId());
                            port.setFrameId(frame.getFrameId());
                            port.setDiscId(disc.getId());
                        }
                    });
                });
            });
        });
        //修改框信息  分批处理 防止数据量过大 超时
        if (frameRealPosition != null && frameRealPosition.size() != 0) {
            List<List<RealPosition>> frameLists = Lists.newArrayList();
            for (int i = 0; i < getCount(frameRealPosition.size()); i++) {
                frameLists.add(frameRealPosition.stream().skip(i * mysqlBatchNum).limit(mysqlBatchNum).collect(Collectors.toList()));
            }
            frameLists.forEach(obj -> {
                templateDao.updateAppFrameInfo(obj);
            });
        }
        //修改盘信息 分批处理 防止数据量过大 超时
        if (discRealPosition != null && discRealPosition.size() != 0) {
            List<List<RealPosition>> lists = Lists.newArrayList();
            for (int i = 0; i < getCount(discRealPosition.size()); i++) {
                lists.add(discRealPosition.stream().skip(i * mysqlBatchNum).limit(mysqlBatchNum).collect(Collectors.toList()));
            }
            lists.forEach(obj -> {
                templateDao.updateAppDiscInfo(obj);
            });
        }
        //修改端口信息  分批处理 防止数据量过大 超时
        if (portRealPosition != null && portRealPosition.size() != 0) {
            List<List<RealPosition>> lists = Lists.newArrayList();
            for (int i = 0; i < getCount(portRealPosition.size()); i++) {
                lists.add(portRealPosition.stream().skip(i * mysqlBatchNum).limit(mysqlBatchNum).collect(Collectors.toList()));
            }
            lists.forEach(obj -> {
                templateDao.updateAppPortInfo(obj);
            });
        }
        log.info("App update template End!");
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
     * 计算新的坐标
     *
     * @param uploadDto    updateDto
     * @param boxReal      箱的坐标 不会改变
     * @param addFrameList 新增的框信息
     * @param addDiscList  新增的盘信息
     */
    @Async
    public void calcNewPosition(DeviceUploadDto uploadDto, List<RealRspDto> boxReal, List<RealRspDto> addFrameList,
                                List<RealRspDto> addDiscList) {
        log.info("add template Date Start!!");
        if (boxReal == null || boxReal.size() == 0) {
            log.debug("calcNewPosition box Real is null");
            throw new BizException(RfIdResultCodeConstant.ADD_DEVICE_ENTITY_PARAM_BOX_DATA_NULL,
                    RfIdI18nConstant.ADD_DEVICE_ENTITY_PARAM_BOX_DATA_NULL);
        }
        //新增框 必定会新增盘信息
        if (addFrameList == null || addFrameList.size() == 0) {
            log.debug("calcNewPosition addFrameList Real is null");
            throw new BizException(RfIdResultCodeConstant.ADD_DEVICE_ENTITY_PARAM_NEW_FRAME_DATA_NULL,
                    RfIdI18nConstant.ADD_DEVICE_ENTITY_PARAM_NEW_FRAME_DATA_NULL);
        }
        if (addDiscList == null || addDiscList.size() == 0) {
            log.debug("calcNewPosition addDiscList Real is null");
            throw new BizException(RfIdResultCodeConstant.ADD_DEVICE_ENTITY_PARAM_NEW_DISC_DATA_NULL,
                    RfIdI18nConstant.ADD_DEVICE_ENTITY_PARAM_NEW_DISC_DATA_NULL);
        }

        String finalABoxId = null;
        String finalBBoxId = null;
        if (boxReal.size() == 2) {
            //双面箱 需要获取箱的id
            for (RealRspDto obj : boxReal) {
                if (obj.getSide() == 0) {
                    finalABoxId = obj.getId();
                } else {
                    finalBBoxId = obj.getId();
                }
            }
        } else {
            //单面箱
            finalABoxId = boxReal.get(0).getId();
        }

        for (RealRspDto obj : addFrameList) {
            if (obj.getSide() == 0) {
                obj.setParentId(finalABoxId);
                obj.setBoxId(finalABoxId);
            } else {
                obj.setParentId(finalBBoxId);
                obj.setBoxId(finalBBoxId);
            }
            //根据模板编号 计算出行列编号
            BaseMould boxMould = uploadDto.getBoxMould();
            calcRowNoByModeCode(uploadDto.getBoxTrend(), uploadDto.getBoxCodeRule(), boxMould.getCol(), boxMould.getRow(), obj);
        }

        //计算盘的坐标
        List<RealPosition> addPortList = Lists.newArrayList();
        addDiscList.forEach(obj -> {
            BaseMould frameMould = uploadDto.getFrameMould();
            calcRowNoByModeCode(uploadDto.getFrameTrend(), uploadDto.getFrameCodeRule(), frameMould.getCol(), frameMould.getRow(), obj);
        });
        TemplateReqDto templateReqDto = new TemplateReqDto();
        BaseMould boxMould = uploadDto.getBoxMould();
        BeanUtils.copyProperties(uploadDto, templateReqDto);
        templateReqDto.setBoxName(boxMould.getName());
        templateReqDto.setBoxTemplateId(boxMould.getId());
        //新增的做计算坐标并插入
        boxReal.forEach(boxs -> {
            //获取根据boxId 获取下面的框
            if (addFrameList != null && addFrameList.size() != 0) {
                addFrameList.stream().filter(obj -> boxs.getId().equals(obj.getParentId())).forEach(frame -> {
                    // 刷框queen 坐标
                    templateHelper.appCalcQueenCoordinate(boxs, TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal(),
                            uploadDto.getBoxMould().getCol(), uploadDto.getBoxMould().getRow(), frame);
                    // 刷盘的坐标
                    addDiscList.stream().filter(obj -> frame.getId().equals(obj.getParentId())).forEach(disc -> {
                        templateHelper.appCalcQueenCoordinate(frame, TemplateTypeEnum.TEMPLATE_TYPE_FRAME.ordinal(),
                                uploadDto.getFrameMould().getCol(), uploadDto.getFrameMould().getRow(), disc);
                        // 所属箱id
                        disc.setBoxId(frame.getParentId());
                        //新增对应的端口信息
                        BaseMould boardMould = uploadDto.getBoardMould();
                        boolean portSideFlag = boardMould.getReversible() == 2;
                        boolean boardPutState = uploadDto.getFrameMould().getPutState() == BoardPutStateEnum.STELLEN.ordinal();
                        List<RealPosition> portPosition = templateHelper.getPortPosition(portSideFlag, boardPutState,
                                disc, boardMould.getCol(), boardMould.getRow());
                        addPortList.addAll(portPosition);
                    });
                });
            }
        });
        //将新增的数据插入到对应的表中
        //新增框信息
        templateDao.saveAppRealPositionFrame(addFrameList);
        //新增盘信息
        templateDao.saveAppRealPositionDisc(addDiscList);
        //新增端口信息
        //这里需要批量处理端口数据
        if (addPortList != null && addPortList.size() != 0) {
            List<List<RealPosition>> lists = Lists.newArrayList();
            for (int i = 0; i < getCount(addPortList.size()); i++) {
                lists.add(addPortList.stream().skip(i * mysqlBatchNum).limit(mysqlBatchNum).collect(Collectors.toList()));
            }
            lists.forEach(obj -> {
                templateDao.saveRealPositionPort(obj);
            });
        }
        log.info("add template Date End!!");
    }

    /**
     * 根据模板编号 反向生成 纵坐标
     *
     * @param trend      走向(0,1 列优/行优)
     * @param codeRule   编号规则 (0,1,2,3 左上/左下/右上/右下)
     * @param col        全部的col
     * @param row        全部的row
     * @param realRspDto 新增的坐标
     */
    private static void calcRowNoByModeCode(Integer trend, Integer codeRule, Integer col, Integer row,
                                            RealRspDto realRspDto) {

        TemplateCodeRuleEnum ruleEnum = TemplateCodeRuleEnum.getTemplateCodeRuleByOrdinal(codeRule);
        //modNum     模板编号
        Integer modNum = realRspDto.getRealNo();
        // 是否列优
        boolean trendFlag = trend == 0;
        assert ruleEnum != null;
        switch (ruleEnum) {
            case TEMPLATE_TREND_LEFT_UP:
                //左上
                if (trendFlag) {
                    //列优 除以行 换行 x+
                    realRspDto.setRowNo(modNum % row == 0 ? row : modNum % row);
                    realRspDto.setColNo(modNum % row == 0 ? modNum / row : modNum / row + 1);
                } else {
                    //行优 除以列 换行 y+
                    realRspDto.setColNo(modNum % col == 0 ? col : modNum % col);
                    realRspDto.setRowNo(modNum % col == 0 ? modNum / col : modNum / col + 1);
                }
                break;
            case TEMPLATE_TREND_LEFT_DOWN:
                //左下
                if (trendFlag) {
                    //列优 除以行 换行 x+
                    int m = modNum % row;
                    int n = modNum / row;
                    if (m == 0) {
                        realRspDto.setColNo(n);
                        realRspDto.setRowNo(1);
                    } else {
                        realRspDto.setColNo(n + 1);
                        realRspDto.setRowNo(row + 1 - m);
                    }
                } else {
                    //行优 除以列 换行 y+
                    realRspDto.setColNo(modNum % col == 0 ? col : modNum % col);
                    realRspDto.setRowNo(modNum % col == 0 ? col - modNum / col : row - modNum / col);
                }
                break;
            case TEMPLATE_TREND_RIGHT_UP:
                //右上
                if (trendFlag) {
                    //列优 除以行 换列 x- 换行 y+
                    int m = modNum % row;
                    int n = modNum / row;
                    realRspDto.setColNo(m == 0 ? col - n + 1 : col - n);
                    realRspDto.setRowNo(m == 0 ? row : m);
                } else {
                    // 行优 除以列 换行 y+ 换列 x-
                    int m = modNum % col;
                    int n = modNum / col;
                    realRspDto.setColNo(m == 0 ? 1 : col + 1 - m);
                    realRspDto.setRowNo(m == 0 ? n : n + 1);
                }
                break;
            case TEMPLATE_TREND_RIGHT_DOWN:
                // 右下
                if (trendFlag) {
                    //列优 除以行 换列 x- 换行 y+
                    int m = modNum % row;
                    int n = modNum / row;
                    realRspDto.setColNo(m == 0 ? col + 1 - n : col - n);
                    realRspDto.setRowNo(m == 0 ? 1 : row + 1 - m);
                } else {
                    // 行优 除以列 换行 y+ 换列 x-
                    int m = modNum % col;
                    int n = modNum / col;
                    realRspDto.setColNo(m == 0 ? 1 : col + 1 - m);
                    realRspDto.setRowNo(m == 0 ? col - n : row - n);
                }
                break;
            default:
                log.info("not find trend by calcRowNoByModeCode!!");
                throw new BizException(RfIdResultCodeConstant.TEMPLATE_CODE_RULE_IS_NULL,
                        RfIdI18nConstant.TEMPLATE_CODE_RULE_IS_NULL);
        }
    }
}
