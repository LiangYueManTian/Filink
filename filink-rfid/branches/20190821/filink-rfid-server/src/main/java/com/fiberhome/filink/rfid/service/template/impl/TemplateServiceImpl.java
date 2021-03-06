package com.fiberhome.filink.rfid.service.template.impl;

import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.rfid.bean.facility.PortInfoBean;
import com.fiberhome.filink.rfid.bean.fibercore.PortCableCoreInfo;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableSectionInfo;
import com.fiberhome.filink.rfid.bean.template.PortCableCoreCondition;
import com.fiberhome.filink.rfid.constant.AppConstant;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCode;
import com.fiberhome.filink.rfid.dao.fibercore.PortCableCoreInfoDao;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableSectionInfoDao;
import com.fiberhome.filink.rfid.dao.template.FacilityDao;
import com.fiberhome.filink.rfid.dao.template.TemplateDao;
import com.fiberhome.filink.rfid.enums.BoardStateEnum;
import com.fiberhome.filink.rfid.enums.BusTypeEnum;
import com.fiberhome.filink.rfid.enums.DeviceAuthEnum;
import com.fiberhome.filink.rfid.enums.TemplateTypeEnum;
import com.fiberhome.filink.rfid.exception.BizException;
import com.fiberhome.filink.rfid.exception.FilinkRfIdException;
import com.fiberhome.filink.rfid.req.fibercore.PortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.template.PortCableReqDto;
import com.fiberhome.filink.rfid.req.template.PortInfoReqDto;
import com.fiberhome.filink.rfid.req.template.RealPortReqDto;
import com.fiberhome.filink.rfid.req.template.RealReqDto;
import com.fiberhome.filink.rfid.req.template.TemplateReqDto;
import com.fiberhome.filink.rfid.resp.fibercore.JumpFiberInfoResp;
import com.fiberhome.filink.rfid.resp.template.PortInfoRspDto;
import com.fiberhome.filink.rfid.resp.template.RealPositionRspDto;
import com.fiberhome.filink.rfid.resp.template.RealRspDto;
import com.fiberhome.filink.rfid.resp.template.TemplateRspDto;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import com.fiberhome.filink.rfid.utils.ResultI18Utils;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ???????????????
 *
 * @author liyj
 * @date 2019/5/28
 */
@Service
@Slf4j
public class TemplateServiceImpl implements TemplateService {

    /**
     * Dao ???
     */
    @Autowired
    private TemplateDao templateDao;

    /**
     * templateHelper ?????????
     */
    @Autowired
    private TemplateHelper templateHelper;
    /**
     * ??????????????????
     */
    @Autowired
    private PortCableCoreInfoDao portCableCoreInfoDao;
    /**
     *
     */
    @Autowired
    private OpticCableSectionInfoDao opticCableSectionInfoDao;
    /**
     * facilityDao
     */
    @Autowired
    private FacilityDao facilityDao;
    /**
     * ??????????????????API
     */
    @Autowired
    private UserFeign userFeign;
    /**
     * ???????????? deviceFeign
     */
    @Autowired
    private DeviceFeign deviceFeign;
    /**
     * ????????????
     */
    @Value("${boxWidth}")
    private Integer boxWidth;

    /**
     * ????????????
     */
    @Value("${boxHeight}")
    private Integer boxHeight;

    /**
     * ????????????
     *
     * @param templateVO templateVo
     * @return Result
     */
    @Override
    public Result createTemplate(TemplateReqDto templateVO) {
        // ????????????
        if (templateVO.getTemplateType() == null || templateVO.getCol() == 0 || templateVO.getRow() == 0 ||
                templateVO.getName() == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.CREATE_TEMPLATE_PARAM_ERROR,
                    RfIdI18nConstant.CREATE_TEMPLATE_PARAM_ERROR);
        }
        // ????????????????????????
        if (templateDao.isExitsTemplateName(templateVO.getName().trim(), templateVO.getTemplateType()) > 0) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.TEMPLATE_NAME_IS_EXITS,
                    RfIdI18nConstant.TEMPLATE_NAME_IS_EXITS);
        }

        if (templateVO.getReversible() == null) {
            templateVO.setReversible(AppConstant.BUS_BINDING_STATE_NO_OPERATOR);
        }
        if (templateVO.getPutState() == null) {
            templateVO.setPutState(AppConstant.BUS_BINDING_DEFAULT_STATE);
        }
        // ?????????????????????
        templateVO.setId(NineteenUUIDUtils.uuid());
        templateDao.saveTemplate(templateVO);
        return ResultI18Utils.convertSuccess(RfIdI18nConstant.CREATE_TEMPLATE_PARAM_SUCCESS);
    }

    /**
     * ?????????????????????  ????????????
     *
     * @param oldString
     * @return ??????????????????
     */
    private String convertTemplateName(String oldString) {

        if (!StringUtils.isEmpty(oldString) && oldString.contains(AppConstant.SEPARATOR_INDEX_DOWN_LINE)) {
            int length = oldString.length();
            String repString = AppConstant.SEPARATOR_INDEX_MYBATIS_SPIT;
            int flag = oldString.indexOf(AppConstant.SEPARATOR_INDEX_DOWN_LINE);
            String newString1 = oldString.substring(0, flag);
            String newString2 = oldString.substring(flag, length);
            oldString = newString1 + repString + newString2;
        } else if (!StringUtils.isEmpty(oldString) && oldString.contains(AppConstant.SEPARATOR_INDEX_ASTERISK)) {
            int length = oldString.length();
            String repString = AppConstant.SEPARATOR_INDEX_MYBATIS_SPIT;
            int flag = oldString.indexOf(AppConstant.SEPARATOR_INDEX_ASTERISK);
            String newString1 = oldString.substring(0, flag);
            String newString2 = oldString.substring(flag, length);
            oldString = newString1 + repString + newString2;
        }
        return oldString;
    }

    /**
     * ?????????????????? ??????????????????
     *
     * @param templateVO ??????
     * @return Result
     */
    @Override
    public Result queryAllTemplate(TemplateReqDto templateVO) {
        if (templateVO == null || templateVO.getTemplateType() == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.QUERY_TEMPLATE_PARAM_ERROR,
                    RfIdI18nConstant.QUERY_TEMPLATE_PARAM_ERROR);
        }
        if (StringUtils.isNotEmpty(templateVO.getName())) {
            templateVO.setName(convertTemplateName(templateVO.getName()));
        }
        //????????????
        TemplateTypeEnum typeEnum = TemplateTypeEnum.getTemplateByIndex(templateVO.getTemplateType());
        if (typeEnum == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.QUERY_TEMPLATE_PARAM_ERROR,
                    RfIdI18nConstant.QUERY_TEMPLATE_PARAM_ERROR);
        }
        //?????????????????????
        List<TemplateRspDto> templateVOS;
        switch (typeEnum) {
            case TEMPLATE_TYPE_BOX:
                templateVOS = templateDao.queryBoxTemplate(templateVO);
                templateVOS = getTemplateData(templateVOS, templateVO);
                break;
            case TEMPLATE_TYPE_FRAME:
                templateVOS = templateDao.queryFrameTemplate(templateVO);
                templateVOS = getTemplateData(templateVOS, templateVO);
                break;
            case TEMPLATE_TYPE_DISC:
                templateVOS = templateDao.queryBoardTemplate(templateVO);
                break;
            default:
                log.error("queryAllTemplate:not find template type -{}" + typeEnum);
                return ResultI18Utils.convertWarnResult(RfIdResultCode.QUERY_TEMPLATE_PARAM_ERROR,
                        RfIdI18nConstant.QUERY_TEMPLATE_PARAM_ERROR);
        }
        return ResultUtils.success(templateVOS);
    }

    /**
     * ???????????????
     *
     * @param realReqDto ?????????Req
     * @return Result
     */
    @Override
    public Result queryRealPosition(RealReqDto realReqDto) {
        //????????????
        if (realReqDto == null || realReqDto.getDeviceId() == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.QUERY_REALPOSITION_PARAM_ERROR,
                    RfIdI18nConstant.QUERY_REALPOSITION_PARAM_ERROR);
        }
        //????????????
        Boolean rfIdDataAuthInfo = getRfIdDataAuthInfo(realReqDto.getDeviceId());
        if (!rfIdDataAuthInfo) {
            log.info("device id{} no permission:" + realReqDto.getDeviceId());
            return ResultUtils.success(Lists.newArrayList());
        }
        List<RealPositionRspDto> realRspDtoList = Lists.newArrayList();
        List<RealPositionRspDto> boxRealPosition = templateDao.queryBoxReal(realReqDto);
        realRspDtoList.addAll(boxRealPosition);
        if (boxRealPosition != null && boxRealPosition.size() != 0) {
            //?????????
            List<String> boxIds = boxRealPosition.stream().map(RealPositionRspDto::getId).collect(Collectors.toList());
            List<RealPositionRspDto> frameRealPosition = templateDao.queryFrameRealP(boxIds);
            realRspDtoList.addAll(frameRealPosition);
            //??????????????????????????? ?????????????????????
            if (frameRealPosition != null && frameRealPosition.size() != 0) {
                List<String> frameIds = frameRealPosition.stream().filter(obj -> obj.getState() == BoardStateEnum.REIGN.ordinal())
                        .map(RealPositionRspDto::getId).collect(Collectors.toList());
                if (frameIds != null && frameIds.size() > 0) {
                    List<RealPositionRspDto> discRealPosition = templateDao.queryDiscRealP(frameIds);
                    realRspDtoList.addAll(discRealPosition);
                    if (discRealPosition != null && discRealPosition.size() != 0) {
                        //?????????????????????????????? ??????????????????????????????
                        List<String> discIds = discRealPosition.stream()
                                .filter(obj -> obj.getState() == BoardStateEnum.REIGN.ordinal())
                                .map(RealPositionRspDto::getId).collect(Collectors.toList());
                        if (discIds != null && discIds.size() != 0) {
                            List<RealPositionRspDto> portRealPosition = templateDao.queryPortRealP(discIds);
                            realRspDtoList.addAll(portRealPosition);
                        }
                    }
                }
            }
        }
        //????????????
        boxRealPosition.stream().forEach(box -> {
            getRealData(box, realRspDtoList);
        });
        return ResultUtils.success(boxRealPosition);
    }

    /**
     * ???????????????????????? id
     *
     * @param frameId ???id
     * @param isFlag  ??????????????????
     * @return Result
     */
    @Override
    public Result queryRealPositionByFrameId(String frameId, boolean isFlag) {
        if (null == frameId) {
            log.info("query real by frameId -- frameId is null");
            return ResultI18Utils.convertWarnResult(RfIdResultCode.QUERY_REALPOSITION_FRAMEID_ERROR,
                    RfIdI18nConstant.QUERY_REALPOSITION_FRAMEID_ERROR);
        }
        // ?????????id ???????????????????????????
        List<RealRspDto> dtoList = Lists.newArrayList();
        List<String> frameIds = Lists.newArrayList();
        frameIds.add(frameId);
        //????????????????????????
        RealRspDto frameReal = templateDao.queryFrameRealPositionById(frameId);
        dtoList.add(frameReal);

        //???????????????x
        Double xApace = frameReal.getAbscissa();
        //???????????????y
        Double yApace = frameReal.getOrdinate();
        Double frameWidth = frameReal.getWidth();
        Double lope = 1.0;
        if (frameWidth < boxWidth) {
            lope = boxWidth / frameWidth;
        }
        if (frameReal.getState() == BoardStateEnum.ABSENT.ordinal()) {
            //???????????????
            calcPosition(frameReal, xApace, yApace, lope);
            return ResultUtils.success(frameReal);
        }

        //?????????????????????
        List<RealRspDto> discReal = templateDao.queryDiscReal(frameIds);
        dtoList.addAll(discReal);
        //????????????????????????
        if (discReal != null && discReal.size() != 0) {
            // ????????????????????? ?????????????????????????????????
            List<String> discIds = discReal.stream().
                    filter(obj -> obj.getState() == BoardStateEnum.REIGN.ordinal())
                    .map(RealRspDto::getId).collect(Collectors.toList());
            if (discIds != null && discIds.size() != 0) {
                List<RealRspDto> portRealPosition = templateDao.queryPortReal(discIds);
                dtoList.addAll(portRealPosition);
            }
        }
        //??????
        getChildData(frameReal, dtoList);

        //???????????????
        calcPosition(frameReal, xApace, yApace, lope);
        Double finalLope = lope;
        frameReal.getChildList().stream().forEach(disc -> {
            //???????????????
            calcPosition(disc, xApace, yApace, finalLope);
            //??????????????????
            if (disc.getChildList() != null) {
                // ???????????????????????????
                disc.getChildList().stream().forEach(port -> {
                    calcPosition(port, xApace, yApace, finalLope);
                });
            }
        });
        return ResultUtils.success(frameReal);
    }

    /**
     * ?????????????????????
     *
     * @param deviceId ??????id
     * @return Result
     */
    @Override
    public Result queryFormationByDeviceId(String deviceId) {
        if (null == deviceId) {
            log.info("query box frame param deviceId is null !!!");
            return ResultI18Utils.convertWarnResult(RfIdResultCode.QUERY_REALPOSITION_DEVICE_ID_ERROR,
                    RfIdI18nConstant.QUERY_REALPOSITION_DEVICE_ID_ERROR);
        }
        RealReqDto realReqDto = new RealReqDto();
        realReqDto.setDeviceId(deviceId);
        List<RealRspDto> boxRealPosition = templateDao.queryBoxRealPosition(realReqDto);
        if (boxRealPosition == null || boxRealPosition.size() == 0) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.DEVICE_IS_NOT_CONFIGURE,
                    RfIdI18nConstant.DEVICE_IS_NOT_CONFIGURE);
        }
        List<RealRspDto> realRspDtoList = Lists.newArrayList();
        realRspDtoList.addAll(boxRealPosition);

        //??????????????????
        List<String> boxIds = boxRealPosition.stream().map(RealRspDto::getId).collect(Collectors.toList());
        List<RealRspDto> frameRealPosition = templateDao.queryFrameReal(boxIds);
        realRspDtoList.addAll(frameRealPosition);

        //??????????????????
        List<String> frameIds = frameRealPosition.stream().map(RealRspDto::getId).collect(Collectors.toList());
        List<RealRspDto> discRealPosition = templateDao.queryDiscReal(frameIds);
        realRspDtoList.addAll(discRealPosition);

        //????????????
        boxRealPosition.stream().forEach(box -> {
            getChildData(box, realRspDtoList);
        });
        return ResultUtils.success(boxRealPosition);
    }

    /**
     * ??????????????????????????????
     *
     * @param reqDto ??????
     * @return Result
     */
    @Override
    public Result saveDeviceAndTempRelation(TemplateReqDto reqDto) {
        //?????????????????????????????????????????????????????????
        if (reqDto == null || reqDto.getBoxCodeRule() == null || reqDto.getBoxTrend() == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.SAVE_TEMPLATE_RELATION_ERROR,
                    RfIdI18nConstant.SAVE_TEMPLATE_RELATION_ERROR);
        }
        if (reqDto.getBoxTemplateId() == null || reqDto.getFrameCodeRule() == null || reqDto.getFrameTrend() == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.SAVE_TEMPLATE_RELATION_ERROR,
                    RfIdI18nConstant.SAVE_TEMPLATE_RELATION_ERROR);
        }
        if (reqDto.getDeviceId() == null || reqDto.getDeviceType() == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.SAVE_TEMPLATE_RELATION_ERROR,
                    RfIdI18nConstant.SAVE_TEMPLATE_RELATION_ERROR);
        }
        if (reqDto.getDiscTrend() == null || reqDto.getDiscCodeRule() == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.SAVE_TEMPLATE_RELATION_ERROR,
                    RfIdI18nConstant.SAVE_TEMPLATE_RELATION_ERROR);
        }
        // ?????? ??????id ??????????????????????????????
        List<TemplateReqDto> templateReqDtos = templateDao.queryFacilityInfoByCondition(reqDto.getDeviceId());
        if (templateReqDtos != null && templateReqDtos.size() > 0) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.SAVE_TEMPLATE_RELATION_ERROR,
                    RfIdI18nConstant.SAVE_TEMPLATE_RELATION_ERROR);
        }

        reqDto.setTemplateType(TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal());
        return templateHelper.calcTemplatePosition(reqDto);

    }


    /**
     * ?????????????????? ????????????id
     *
     * @param portId ??????id
     * @return Result
     */
    @Override
    public Result queryPortInfoByPortId(String portId) {
        if (StringUtils.isEmpty(portId)) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.PORT_ID_IS_NULL, RfIdI18nConstant.PORT_ID_IS_NULL);
        }
        PortCableCoreCondition portInfo = templateDao.getPortRealById(portId);
        // ??????port ??????id  ??????????????? ??????????????????????????????????????????
        PortInfoRspDto infoRspDto = new PortInfoRspDto();
        //?????????
        infoRspDto.setPortNum(templateDao.queryPortNumByPortId(portId));
        infoRspDto.setState(portInfo.getPortState());

        PortCableCoreInfoReq portCondition = new PortCableCoreInfoReq();
        BeanUtils.copyProperties(portInfo, portCondition);
        //??????????????????????????????????????????
        List<PortCableCoreInfo> portCableCoreInfos = portCableCoreInfoDao.getPortCableCoreInfo(portCondition);
        // ??????????????????id ?????? ????????????????????? ?????????get(0)
        if (portCableCoreInfos != null && portCableCoreInfos.size() != 0) {
            PortCableCoreInfo portCableCoreInfo = portCableCoreInfos.get(0);
            infoRspDto.setCableCore(portCableCoreInfo.getOppositeCableCoreNo());

            OpticCableSectionInfo opticCableSectionInfo = new OpticCableSectionInfo();
            opticCableSectionInfo.setOpticCableSectionId(portCableCoreInfo.getOppositeResource());
            opticCableSectionInfo.setIsDeleted("0");
            OpticCableSectionInfo result = opticCableSectionInfoDao.selectOne(opticCableSectionInfo);
            if (result != null) {
                infoRspDto.setOpticCableSectionName(result.getOpticCableSectionName());
            }
        }
        //??????????????????
        PortInfoBean portInfoBean = facilityDao.queryPortLabelInfo(portInfo);
        if (portInfoBean != null) {
            // ??????????????????  ??????????????????????????????
            infoRspDto.setRemark(portInfoBean.getMemo());
            infoRspDto.setAdapterType(portInfoBean.getAdapterType());
            if (portInfoBean.getPortNo() != null) {
                infoRspDto.setPortId(portInfoBean.getPortNo().toString());
            }
            infoRspDto.setRfId(portInfoBean.getPortLabel());
            infoRspDto.setLabelType(portInfoBean.getLabelType());
        }
        return ResultUtils.success(infoRspDto);
    }

    /**
     * ????????????????????????
     *
     * @param list list
     * @return List<JumpFiberInfoResp>
     */
    @Override
    public List<JumpFiberInfoResp> batchQueryPortInfo(List<JumpFiberInfoResp> list) {
        List<JumpFiberInfoResp> respList = Lists.newArrayList();
        if (list != null && list.size() != 0) {
            list.parallelStream().forEach(obj -> {
                PortInfoBean info = templateDao.batchQueryPortInfo(obj);
                if (info == null) {
                    return;
                }
                JumpFiberInfoResp jumpFiberInfoResp = new JumpFiberInfoResp();
                BeanUtils.copyProperties(obj, jumpFiberInfoResp);
                //??????????????????
                jumpFiberInfoResp.setOppositePortStatus(info.getPortState().toString());
                //????????????????????????
                if (info.getLabelState() != null) {
                    jumpFiberInfoResp.setOppositePortRfidStatus(info.getLabelState().toString());
                }
                //???????????????
                jumpFiberInfoResp.setAdapterType(info.getAdapterType());
                //????????????????????????
                jumpFiberInfoResp.setOppositePortRfidCode(info.getPortLabel());
                //????????????????????????
                if (info.getLabelType() != null) {
                    jumpFiberInfoResp.setOppositePortMarkType(info.getLabelType().toString());
                }
                //????????????????????????
                jumpFiberInfoResp.setOppositePortRemark(info.getMemo());
                respList.add(jumpFiberInfoResp);
            });
        }
        return respList;
    }

    /**
     * ????????????id ??? ???????????????
     *
     * @param deviceId ??????id
     * @return Result
     */
    @Override
    public Result queryTemplateTop(String deviceId) {
        if (deviceId == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.PARAMS_ERROR, RfIdI18nConstant.PARAMS_ERROR);
        }
        //?????????????????? ????????????????????????
        List<RealRspDto> frames = templateDao.queryFrameRealPosition(deviceId);
        if (frames != null && frames.size() != 0) {
            List<RealRspDto> discs = templateDao.queryDiscRealPosition(deviceId);
            if (discs != null && discs.size() != 0) {
                frames.stream().forEach(obj -> {
                    List<RealRspDto> child = Lists.newArrayList();
                    discs.stream().forEach(disc -> {
                        if (obj.getId().equals(disc.getParentId())) {
                            child.add(disc);
                        }
                    });
                    obj.setChildList(child);
                });
            }
        }
        return ResultUtils.success(frames);

    }

    /**
     * ??????????????????
     * ???????????????
     *
     * @param reqDto ??????id
     * @return Result
     */
    @Override
    public Result updatePortState(RealPortReqDto reqDto) {
        if (reqDto == null || reqDto.getDiscId() == null || reqDto.getState() == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.PARAMS_ERROR, RfIdI18nConstant.PARAMS_ERROR);
        }
        templateDao.updatePortState(reqDto);
        templateDao.updateDiscState(reqDto);
        return ResultUtils.success();
    }

    /**
     * ??????deviceId ????????? ?????? ???????????????????????????
     *
     * @param deviceId deviceId
     * @return true/false  ????????????????????????/
     */
    @Override
    public Boolean getRfIdDataAuthInfo(String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            throw new BizException(RfIdResultCode.PARAMS_ERROR, RfIdI18nConstant.PARAMS_ERROR);
        }
        String deviceType = deviceFeign.queryDeviceTypeById(deviceId);
        //???????????????????????????????????????
        List<String> deviceTypes = getDeviceTypes(getCurrentUser());
        if (deviceTypes == null || deviceTypes.size() == 0) {
            return false;
        }
        //??????????????????code
        String code = DeviceAuthEnum.getCodeByParent(deviceType);
        return deviceTypes.contains(code);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param portId  ??????id
     * @param state   ?????????????????? ??????/??????
     * @param busType ?????????????????? BusTypeEnum (0/1/2)??????????????????????????????
     */
    @Override
    public void updatePortBindingState(String portId, Integer state, Integer busType, Integer portState) {
        if (StringUtils.isEmpty(portId)) {
            throw new BizException(RfIdResultCode.UPDATE_PORT_BINDING_PORT_IS_NULL,
                    RfIdI18nConstant.UPDATE_PORT_BINDING_PORT_IS_NULL);
        }
        if (state == null) {
            throw new BizException(RfIdResultCode.UPDATE_PORT_BINDING_STATE_IS_NULL,
                    RfIdI18nConstant.UPDATE_PORT_BINDING_STATE_IS_NULL);
        }
        if (busType == null) {
            throw new BizException(RfIdResultCode.UPDATE_PORT_BINDING_BUS_TYPE_IS_NULL,
                    RfIdI18nConstant.UPDATE_PORT_BINDING_BUS_TYPE_IS_NULL);
        }
        //???????????????????????????
        //??????: ?????????????????? ????????????????????????0 ???????????????????????????
        List<RealRspDto> realRspDtos = templateDao.querySameDiscPort(portId);
        //??????????????????
        realRspDtos.stream().filter(obj -> obj.getId().equals(portId)).forEach(obj -> {
            // ???????????????????????? ?????????
            RealPortReqDto portReqDto = new RealPortReqDto();
            portReqDto.setPortId(portId);
            portReqDto.setBusState(state);
            String busState = convertBindState(busType, obj.getBusBindingState(), state);
            portReqDto.setBusBindingState(busState);
            if (BusTypeEnum.BUS_TYPE_PORT.ordinal() == busType) {
                portReqDto.setPortState(portState);
            }
            obj.setBusState(state);
            //??????????????????
            templateDao.updateBusBindingPortState(portReqDto);
        });
        //????????????????????????????????????????????????
        RealPortReqDto discReq = new RealPortReqDto();
        discReq.setDiscId(realRspDtos.get(0).getDiscId());
        List<RealRspDto> collect = realRspDtos.stream().filter(obj -> obj.getBusState()
                .equals(AppConstant.BUS_BINDING_DEFAULT_STATE)).collect(Collectors.toList());
        if (collect == null || collect.size() == AppConstant.BUS_BINDING_DEFAULT_STATE) {
            //??????0 ????????????????????????????????????
            discReq.setBusState(AppConstant.BUS_BINDING_DEFAULT_STATE);
        } else {
            discReq.setBusState(AppConstant.BUS_BINDING_STATE_NO_OPERATOR);
        }
        templateDao.updateBusBindingDiscState(discReq);

    }


    /**
     * ??????????????????????????????id
     *
     * @param portInfoReqDto ??????????????????
     * @return ??????id
     */
    @Override
    public String queryPortIdByPortInfo(PortInfoReqDto portInfoReqDto) {
        return templateDao.queryPortIdByPortInfo(portInfoReqDto);
    }

    /**
     * ????????????
     *
     * @param templateReqDtoList
     * @return
     */
    @Override
    public Result updateTemplate(TemplateReqDto templateReqDtoList) {
        if (templateReqDtoList == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.UPDATE_TEMPLATE_PARAM_IS_NULL,
                    RfIdI18nConstant.UPDATE_TEMPLATE_PARAM_IS_NULL);
        }
        templateDao.updateTemplate(templateReqDtoList);
        return ResultI18Utils.convertSuccess(RfIdI18nConstant.UPDATE_TEMPLATE_SUCCESS);
    }

    /**
     * ??????????????????
     *
     * @param templateReqDto ????????????
     * @return ??????????????????????????????
     */
    @Override
    public Result deleteTemplate(TemplateReqDto templateReqDto) {
        if (templateReqDto == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.DELETE_TEMPLATE_PARAM_IS_NULL,
                    RfIdI18nConstant.DELETE_TEMPLATE_PARAM_IS_NULL);
        }
        Integer templateType = templateReqDto.getTemplateType();
        String templateId = templateReqDto.getId();
        if (StringUtils.isEmpty(templateId)
                || templateType == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.DELETE_TEMPLATE_PARAM_IS_NULL,
                    RfIdI18nConstant.DELETE_TEMPLATE_PARAM_IS_NULL);
        }
        // ????????????????????????
        //????????????????????????????????????
        List<String> templateIds = Lists.newArrayList();
        switch (templateType) {
            case 0:
                templateIds.add(templateId);
                deleteTemplateById(templateIds, templateId);
                break;
            case 1:
                //??????????????? ????????????????????????
                List<TemplateRspDto> templateRspDtos = templateDao.queryTemplateByType(TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal());
                if (templateRspDtos != null && templateRspDtos.size() != 0) {
                    //???????????????????????????
                    templateRspDtos.stream().forEach(obj -> {
                        List<String> collect = Arrays.stream(obj.getChildTemplateId().split(AppConstant.SEPARATOR)).filter(child -> child.equals(templateId))
                                .collect(Collectors.toList());
                        if (collect != null && collect.size() != 0) {
                            templateIds.add(obj.getId());
                        }
                    });
                    if (templateIds != null && templateIds.size() != 0) {
                        return ResultI18Utils.convertWarnResult(RfIdResultCode.TEMPLATE_IS_NOT_DELETE,
                                RfIdI18nConstant.TEMPLATE_IS_NOT_DELETE);
                    }
                    templateDao.deleteTemplateById(templateId);
                } else {
                    templateDao.deleteTemplateById(templateId);
                }
                break;
            case 2:
                // ???????????????
                List<TemplateRspDto> frames = templateDao.queryTemplateByType(TemplateTypeEnum.TEMPLATE_TYPE_FRAME.ordinal());
                if (frames == null) {
                    templateDao.deleteTemplateById(templateId);
                    break;
                }
                if (frames.size() != 0) {
                    // ??????????????? ???????????????
                    frames.forEach(obj -> {
                        List<String> collect = Arrays.stream(obj.getChildTemplateId().split(AppConstant.SEPARATOR)).filter(child -> child.equals(templateId))
                                .collect(Collectors.toList());
                        if (collect != null && collect.size() != 0) {
                            templateIds.add(obj.getId());
                        }
                    });
                    if (templateIds != null && templateIds.size() != 0) {
                        return ResultI18Utils.convertWarnResult(RfIdResultCode.TEMPLATE_IS_NOT_DELETE,
                                RfIdI18nConstant.TEMPLATE_IS_NOT_DELETE);
                    }
                    templateDao.deleteTemplateById(templateId);
                }
                break;
            default:
                log.info("not find templateType {}" + templateType);
                throw new BizException(RfIdResultCode.DELETE_TEMPLATE_PARAM_IS_NULL,
                        RfIdI18nConstant.DELETE_TEMPLATE_PARAM_IS_NULL);
        }
        return ResultI18Utils.convertSuccess(RfIdI18nConstant.DELETE_TEMPLATE_SUCCESS);
    }

    /**
     * ???????????? ?????? ????????????
     *
     * @param portCableReqDtos
     */
    @Override
    public void batchUpdatePortCableState(List<PortCableReqDto> portCableReqDtos) {
        if (portCableReqDtos != null && portCableReqDtos.size() > 0) {
            templateDao.batchUpdatePortCableState(portCableReqDtos);
        }
    }

    /**
     * App ??????????????? ????????????
     *
     * @param deviceId ??????id
     */
    @Override
    public void deleteDeviceEntity(String deviceId) {
        List<RealRspDto> realRspDtos = templateDao.queryRealPositionByDeviceId(deviceId);
        // ????????????
        List<String> collect = realRspDtos.stream().map(RealRspDto::getId).collect(Collectors.toList());
        if (collect != null && collect.size() != 0) {
            templateDao.deletePort(collect);
        }
        //????????????
        templateDao.deleteDeviceEntity(deviceId);
    }

    /**
     * ???????????????
     *
     * @param portId
     * @return
     */
    @Override
    public String queryPortNumByPortId(String portId) {
        return templateDao.queryPortNumByPortId(portId);
    }

    /**
     * ????????????????????????id
     *
     * @param boxTemplateId
     */

    private void deleteTemplateById(List<String> boxTemplateId, String templateId) {
        boolean b = templateDao.queryTemplateExistsById(boxTemplateId) > 0;
        if (b) {
            throw new BizException(RfIdResultCode.TEMPLATE_IS_NOT_DELETE,
                    RfIdI18nConstant.TEMPLATE_IS_NOT_DELETE);
        } else {
            templateDao.deleteTemplateById(templateId);
        }
    }

    /**
     * ??????????????????
     *
     * @param busType      ????????????
     * @param oldBindState ????????????????????? 0-0-0
     * @param state
     * @return
     */
    private String convertBindState(Integer busType, String oldBindState, Integer state) {
        if (StringUtils.isEmpty(oldBindState)) {
            throw new BizException(RfIdResultCode.QUERY_BINDING_INFO_IS_NULL,
                    RfIdI18nConstant.QUERY_BINDING_INFO_IS_NULL);
        }
        StringBuffer sb = new StringBuffer();
        if (BusTypeEnum.BUS_TYPE_FORMATION.ordinal() == busType) {
            //?????? ????????? ????????????????????? - ????????? ???????????????????????????????????????????????????
            int index = oldBindState.indexOf(AppConstant.SEPARATOR_INDEX_UP_LINE);
            String substring = oldBindState.substring(index, oldBindState.length());
            sb.append(state).append(substring);
        } else if (BusTypeEnum.BUS_TYPE_JUMP.ordinal() == busType) {
            //?????? ?????????
            int index = oldBindState.indexOf(AppConstant.SEPARATOR_INDEX_UP_LINE);
            int last = oldBindState.lastIndexOf(AppConstant.SEPARATOR_INDEX_UP_LINE);
            sb.append(oldBindState.subSequence(0, index))
                    .append(AppConstant.SEPARATOR_INDEX_UP_LINE)
                    .append(state)
                    .append(oldBindState.subSequence(last, oldBindState.length()));
        } else if (BusTypeEnum.BUS_TYPE_PORT.ordinal() == busType) {
            //???????????? ?????????
            int last = oldBindState.lastIndexOf(AppConstant.SEPARATOR_INDEX_UP_LINE);
            sb.append(oldBindState.subSequence(0, last))
                    .append(AppConstant.SEPARATOR_INDEX_UP_LINE)
                    .append(state);
        } else {
            log.info("not find busType {} throws Exception" + busType);
            throw new BizException(RfIdResultCode.UPDATE_PORT_BINDING_BUS_TYPE_NOT_FIND,
                    RfIdI18nConstant.UPDATE_PORT_BINDING_BUS_TYPE_NOT_FIND);
        }

        return sb.toString();
    }


    /**
     * ??????????????????????????????
     *
     * @param user
     * @return
     */
    private static List<String> getDeviceTypes(User user) {
        List<String> deviceTypes = new ArrayList<>();
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();
        for (RoleDeviceType roleDeviceType : roleDeviceTypes) {
            deviceTypes.add(roleDeviceType.getDeviceTypeId());
        }
        return deviceTypes;
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    private User getCurrentUser() {
        //????????????????????????
        Object userObj = userFeign.queryCurrentUser(RequestInfoUtils.getUserId(), RequestInfoUtils.getToken());
        //?????????User
        return convertObjectToUser(userObj);
    }

    /**
     * ?????????????????????
     *
     * @param userObj
     * @return
     */
    private User convertObjectToUser(Object userObj) {
        //??????????????????
        if (userObj == null) {
            throw new FilinkRfIdException(RfIdI18nConstant.PARAMS_ERROR);
        }
        User user = JSON.parseObject(JSON.toJSONString(userObj), User.class);
        //??????????????????
        if (user == null) {
            throw new FilinkRfIdException(RfIdI18nConstant.PARAMS_ERROR);
        }
        if (user.getDepartment() == null) {
            user.setDepartment(new Department());
        }
        if (user.getDepartment().getAreaIdList() == null) {
            user.getDepartment().setAreaIdList(new ArrayList<>());
        }
        if (user.getRole() == null) {
            user.setRole(new Role());
        }
        if (user.getRole().getRoleDevicetypeList() == null) {
            user.getRole().setRoleDevicetypeList(new ArrayList<>());
        }
        return user;
    }

    /**
     * ?????????????????????
     * ??????????????????
     *
     * @param realRspDto ????????????
     * @param xApace     ???????????????
     * @param yApace     ???????????????
     * @param lope       ????????????
     */
    private void calcPosition(RealRspDto realRspDto, Double xApace,
                              Double yApace, Double lope) {
        Double abscissa = realRspDto.getAbscissa();
        Double ordinate = realRspDto.getOrdinate();
        Double width = realRspDto.getWidth();
        Double height = realRspDto.getHeight();

        realRspDto.setWidth(width * lope);
        realRspDto.setAbscissa((abscissa - xApace) * lope);
        realRspDto.setHeight(height * lope);
        realRspDto.setOrdinate((ordinate - yApace) * lope);
    }


    /**
     * ???????????????????????????
     *
     * @param parent  ???
     * @param allReal ???????????????
     */
    private RealRspDto getChildData(RealRspDto parent, List<RealRspDto> allReal) {
        for (RealRspDto realRspDto : allReal) {
            if (realRspDto.getParentId().equals(parent.getId())) {
                if (parent.getChildList() == null) {
                    parent.setChildList(new ArrayList<RealRspDto>());
                }
                parent.getChildList().add(getChildData(realRspDto, allReal));
            }
        }
        return parent;
    }

    /**
     * ?????????????????????
     *
     * @param parent  ???
     * @param allReal ????????????
     * @return ???????????????
     */
    private RealPositionRspDto getRealData(RealPositionRspDto parent, List<RealPositionRspDto> allReal) {
        for (RealPositionRspDto realRspDto : allReal) {
            if (realRspDto.getParentId().equals(parent.getId())) {
                if (parent.getChildList() == null) {
                    parent.setChildList(new ArrayList<RealPositionRspDto>());
                }
                parent.getChildList().add(getRealData(realRspDto, allReal));
            }
        }
        return parent;
    }


    /**
     * ???????????????????????????
     *
     * @param templateVOS ???????????????
     * @param templateVO  ????????????
     */
    public List<TemplateRspDto> getTemplateData(List<TemplateRspDto> templateVOS, TemplateReqDto templateVO) {
        // ??????????????????
        if (templateVOS != null && templateVOS.size() != 0) {
            Map<String, TemplateRspDto> allTemplateMap = templateVOS.stream().collect(Collectors.toMap(obj -> obj.getId(), obj -> obj));
            //???????????????????????????
            templateVOS = templateVOS.parallelStream().filter(obj -> StringUtils.isNotEmpty(obj.getChildTemplateId()))
                    .filter(obj -> obj.getTemplateType().equals(templateVO.getTemplateType())).collect(Collectors.toList());
            if (templateVOS == null || templateVOS.size() == 0) {
                return templateVOS;
            }
            //????????????
            templateVOS.stream().forEach(obj -> {
                //???????????????????????????
                //?????????????????? ????????????
                List<TemplateRspDto> childList = getChildList(obj, allTemplateMap);
                obj.setChildTemplateList(childList);
            });
        }
        return templateVOS;
    }

    /**
     * ???????????????????????????
     *
     * @param parent
     * @param allTemplateMap
     * @return
     */
    private List<TemplateRspDto> getChildList(TemplateRspDto parent, Map<String, TemplateRspDto> allTemplateMap) {
        List<TemplateRspDto> list = Lists.newArrayList();
        if (parent != null && StringUtils.isNotEmpty(parent.getChildTemplateId())) {
            //?????????????????????
            List<String> ids = Arrays.asList(parent.getChildTemplateId().split(AppConstant.SEPARATOR));
            // ???????????????
            ids.stream().forEach(id -> {
                TemplateRspDto templateRspDto = allTemplateMap.get(id);
                if (templateRspDto != null) {
                    templateRspDto.setChildTemplateList(getChildList(templateRspDto, allTemplateMap));
                    list.add(templateRspDto);
                }
            });
        }
        return list;
    }

}
