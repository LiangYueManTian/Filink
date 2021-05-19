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
 * 模板实现类
 *
 * @author liyj
 * @date 2019/5/28
 */
@Service
@Slf4j
public class TemplateServiceImpl implements TemplateService {

    /**
     * Dao 层
     */
    @Autowired
    private TemplateDao templateDao;

    /**
     * templateHelper 帮助类
     */
    @Autowired
    private TemplateHelper templateHelper;
    /**
     * 获取成端信息
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
     * 注入用户服务API
     */
    @Autowired
    private UserFeign userFeign;
    /**
     * 远程调用 deviceFeign
     */
    @Autowired
    private DeviceFeign deviceFeign;
    /**
     * 箱的宽度
     */
    @Value("${boxWidth}")
    private Integer boxWidth;

    /**
     * 箱的高度
     */
    @Value("${boxHeight}")
    private Integer boxHeight;

    /**
     * 新增模版
     *
     * @param templateVO templateVo
     * @return Result
     */
    @Override
    public Result createTemplate(TemplateReqDto templateVO) {
        // 参数校验
        if (templateVO.getTemplateType() == null || templateVO.getCol() == 0 || templateVO.getRow() == 0 ||
                templateVO.getName() == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.CREATE_TEMPLATE_PARAM_ERROR,
                    RfIdI18nConstant.CREATE_TEMPLATE_PARAM_ERROR);
        }
        // 加上名字重复校验
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
        // 保存到数据库中
        templateVO.setId(NineteenUUIDUtils.uuid());
        templateDao.saveTemplate(templateVO);
        return ResultI18Utils.convertSuccess(RfIdI18nConstant.CREATE_TEMPLATE_PARAM_SUCCESS);
    }

    /**
     * 将数据进行转义  特殊字符
     *
     * @param oldString
     * @return 转义后的字符
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
     * 查询全部模板 根据模板类型
     *
     * @param templateVO 模板
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
        //模板类型
        TemplateTypeEnum typeEnum = TemplateTypeEnum.getTemplateByIndex(templateVO.getTemplateType());
        if (typeEnum == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.QUERY_TEMPLATE_PARAM_ERROR,
                    RfIdI18nConstant.QUERY_TEMPLATE_PARAM_ERROR);
        }
        //对应的全部数据
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
     * 查询实景图
     *
     * @param realReqDto 实景图Req
     * @return Result
     */
    @Override
    public Result queryRealPosition(RealReqDto realReqDto) {
        //参数校验
        if (realReqDto == null || realReqDto.getDeviceId() == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.QUERY_REALPOSITION_PARAM_ERROR,
                    RfIdI18nConstant.QUERY_REALPOSITION_PARAM_ERROR);
        }
        //权限校验
        Boolean rfIdDataAuthInfo = getRfIdDataAuthInfo(realReqDto.getDeviceId());
        if (!rfIdDataAuthInfo) {
            log.info("device id{} no permission:" + realReqDto.getDeviceId());
            return ResultUtils.success(Lists.newArrayList());
        }
        List<RealPositionRspDto> realRspDtoList = Lists.newArrayList();
        List<RealPositionRspDto> boxRealPosition = templateDao.queryBoxReal(realReqDto);
        realRspDtoList.addAll(boxRealPosition);
        if (boxRealPosition != null && boxRealPosition.size() != 0) {
            //查询框
            List<String> boxIds = boxRealPosition.stream().map(RealPositionRspDto::getId).collect(Collectors.toList());
            List<RealPositionRspDto> frameRealPosition = templateDao.queryFrameRealP(boxIds);
            realRspDtoList.addAll(frameRealPosition);
            //这里过滤不在位的框 不显示下属信息
            if (frameRealPosition != null && frameRealPosition.size() != 0) {
                List<String> frameIds = frameRealPosition.stream().filter(obj -> obj.getState() == BoardStateEnum.REIGN.ordinal())
                        .map(RealPositionRspDto::getId).collect(Collectors.toList());
                if (frameIds != null && frameIds.size() > 0) {
                    List<RealPositionRspDto> discRealPosition = templateDao.queryDiscRealP(frameIds);
                    realRspDtoList.addAll(discRealPosition);
                    if (discRealPosition != null && discRealPosition.size() != 0) {
                        //这里过滤掉不在位的盘 不显示下属的端口信息
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
        //递归拼树
        boxRealPosition.stream().forEach(box -> {
            getRealData(box, realRspDtoList);
        });
        return ResultUtils.success(boxRealPosition);
    }

    /**
     * 查询实景图根据框 id
     *
     * @param frameId 框id
     * @param isFlag  是否需要放大
     * @return Result
     */
    @Override
    public Result queryRealPositionByFrameId(String frameId, boolean isFlag) {
        if (null == frameId) {
            log.info("query real by frameId -- frameId is null");
            return ResultI18Utils.convertWarnResult(RfIdResultCode.QUERY_REALPOSITION_FRAMEID_ERROR,
                    RfIdI18nConstant.QUERY_REALPOSITION_FRAMEID_ERROR);
        }
        // 根据框id 去查下级的盘和端口
        List<RealRspDto> dtoList = Lists.newArrayList();
        List<String> frameIds = Lists.newArrayList();
        frameIds.add(frameId);
        //查询这条框的信息
        RealRspDto frameReal = templateDao.queryFrameRealPositionById(frameId);
        dtoList.add(frameReal);

        //整体偏移量x
        Double xApace = frameReal.getAbscissa();
        //整体偏移量y
        Double yApace = frameReal.getOrdinate();
        Double frameWidth = frameReal.getWidth();
        Double lope = 1.0;
        if (frameWidth < boxWidth) {
            lope = boxWidth / frameWidth;
        }
        if (frameReal.getState() == BoardStateEnum.ABSENT.ordinal()) {
            //刷框的坐标
            calcPosition(frameReal, xApace, yApace, lope);
            return ResultUtils.success(frameReal);
        }

        //查询盘坐标信息
        List<RealRspDto> discReal = templateDao.queryDiscReal(frameIds);
        dtoList.addAll(discReal);
        //查询端口坐标信息
        if (discReal != null && discReal.size() != 0) {
            // 过滤不在位的盘 下属的端口信息不用管了
            List<String> discIds = discReal.stream().
                    filter(obj -> obj.getState() == BoardStateEnum.REIGN.ordinal())
                    .map(RealRspDto::getId).collect(Collectors.toList());
            if (discIds != null && discIds.size() != 0) {
                List<RealRspDto> portRealPosition = templateDao.queryPortReal(discIds);
                dtoList.addAll(portRealPosition);
            }
        }
        //递归
        getChildData(frameReal, dtoList);

        //刷框的坐标
        calcPosition(frameReal, xApace, yApace, lope);
        Double finalLope = lope;
        frameReal.getChildList().stream().forEach(disc -> {
            //获取盘信息
            calcPosition(disc, xApace, yApace, finalLope);
            //获取端口信息
            if (disc.getChildList() != null) {
                // 获取端口的放大倍数
                disc.getChildList().stream().forEach(port -> {
                    calcPosition(port, xApace, yApace, finalLope);
                });
            }
        });
        return ResultUtils.success(frameReal);
    }

    /**
     * 查询箱框的信息
     *
     * @param deviceId 设施id
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

        //查询框的信息
        List<String> boxIds = boxRealPosition.stream().map(RealRspDto::getId).collect(Collectors.toList());
        List<RealRspDto> frameRealPosition = templateDao.queryFrameReal(boxIds);
        realRspDtoList.addAll(frameRealPosition);

        //查询盘的信息
        List<String> frameIds = frameRealPosition.stream().map(RealRspDto::getId).collect(Collectors.toList());
        List<RealRspDto> discRealPosition = templateDao.queryDiscReal(frameIds);
        realRspDtoList.addAll(discRealPosition);

        //递归拼树
        boxRealPosition.stream().forEach(box -> {
            getChildData(box, realRspDtoList);
        });
        return ResultUtils.success(boxRealPosition);
    }

    /**
     * 保存模板和设施的关系
     *
     * @param reqDto 关系
     * @return Result
     */
    @Override
    public Result saveDeviceAndTempRelation(TemplateReqDto reqDto) {
        //计算模板的坐标和保存坐标图和设施的关系
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
        // 校验 设施id 是否已经存在模板关系
        List<TemplateReqDto> templateReqDtos = templateDao.queryFacilityInfoByCondition(reqDto.getDeviceId());
        if (templateReqDtos != null && templateReqDtos.size() > 0) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.SAVE_TEMPLATE_RELATION_ERROR,
                    RfIdI18nConstant.SAVE_TEMPLATE_RELATION_ERROR);
        }

        reqDto.setTemplateType(TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal());
        return templateHelper.calcTemplatePosition(reqDto);

    }


    /**
     * 查询端口信息 通过端口id
     *
     * @param portId 主键id
     * @return Result
     */
    @Override
    public Result queryPortInfoByPortId(String portId) {
        if (StringUtils.isEmpty(portId)) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.PORT_ID_IS_NULL, RfIdI18nConstant.PORT_ID_IS_NULL);
        }
        PortCableCoreCondition portInfo = templateDao.getPortRealById(portId);
        // 通过port 主键id  获取坐标图 然后获取数据去获取对应的信息
        PortInfoRspDto infoRspDto = new PortInfoRspDto();
        //端口号
        infoRspDto.setPortNum(templateDao.queryPortNumByPortId(portId));
        infoRspDto.setState(portInfo.getPortState());

        PortCableCoreInfoReq portCondition = new PortCableCoreInfoReq();
        BeanUtils.copyProperties(portInfo, portCondition);
        //通过端口号去获取本端端口信息
        List<PortCableCoreInfo> portCableCoreInfos = portCableCoreInfoDao.getPortCableCoreInfo(portCondition);
        // 这里根据端口id 获取 只会有一条数据 故只取get(0)
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
        //智能标签信息
        PortInfoBean portInfoBean = facilityDao.queryPortLabelInfo(portInfo);
        if (portInfoBean != null) {
            // 智能标签信息  从智能标签端口表中取
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
     * 批量获取端口信息
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
                //对端端口状态
                Integer portState = info.getPortState();
                if (portState == null) {
                    //给默认值 空闲
                    portState = 2;
                }
                jumpFiberInfoResp.setOppositePortStatus(portState.toString());
                //对端端口标签状态
                if (info.getLabelState() != null) {
                    jumpFiberInfoResp.setOppositePortRfidStatus(info.getLabelState().toString());
                }
                //适配器类型 保持一致
                Integer adapterType = info.getAdapterType();
                if (adapterType != null) {
                    jumpFiberInfoResp.setAdapterType(adapterType);
                } else {
                    jumpFiberInfoResp.setAdapterType(null);
                }
                //对端智能标签编码
                jumpFiberInfoResp.setOppositePortRfidCode(info.getPortLabel());
                //对端端口标签类型
                if (info.getLabelType() != null) {
                    jumpFiberInfoResp.setOppositePortMarkType(info.getLabelType().toString());
                }
                //对端智能标签备注
                jumpFiberInfoResp.setOppositePortRemark(info.getMemo());
                respList.add(jumpFiberInfoResp);
            });
        }
        return respList;
    }

    /**
     * 通过设施id 和 类型获取值
     *
     * @param deviceId 设施id
     * @return Result
     */
    @Override
    public Result queryTemplateTop(String deviceId) {
        if (deviceId == null) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.PARAMS_ERROR, RfIdI18nConstant.PARAMS_ERROR);
        }
        //查询框的信息 这里拼颗树给前端
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
     * 修改端口状态
     * 盘的上下架
     *
     * @param reqDto 请求id
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
     * 通过deviceId 去获取 设施 是否有智能标签类型
     *
     * @param deviceId deviceId
     * @return true/false  有对应的数据权限/
     */
    @Override
    public Boolean getRfIdDataAuthInfo(String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            throw new BizException(RfIdResultCode.PARAMS_ERROR, RfIdI18nConstant.PARAMS_ERROR);
        }
        String deviceType = deviceFeign.queryDeviceTypeById(deviceId);
        //当前用户可以看到的设施类型
        List<String> deviceTypes = getDeviceTypes(getCurrentUser());
        if (deviceTypes == null || deviceTypes.size() == 0) {
            return false;
        }
        //子集智能标签code
        String code = DeviceAuthEnum.getCodeByParent(deviceType);
        return deviceTypes.contains(code);
    }

    /**
     * 修改端口绑定的业务状态信息
     *
     * @param portId  端口id
     * @param state   修改后的状态 新增/删除
     * @param busType 属于那种业务 BusTypeEnum (0/1/2)成端、跳接、端口标签
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
        //修改端口对应的状态
        //逻辑: 盘的状态修改 盘下的状态有不为0 的则改盘不修改状态
        List<RealRspDto> realRspDtos = templateDao.querySameDiscPort(portId);
        //修改当前状态
        realRspDtos.stream().filter(obj -> obj.getId().equals(portId)).forEach(obj -> {
            // 获取当前端口信息 并修改
            RealPortReqDto portReqDto = new RealPortReqDto();
            portReqDto.setPortId(portId);
            portReqDto.setBusState(state);
            String busState = convertBindState(busType, obj.getBusBindingState(), state);
            portReqDto.setBusBindingState(busState);
            if (BusTypeEnum.BUS_TYPE_PORT.ordinal() == busType) {
                portReqDto.setPortState(portState);
            }
            obj.setBusState(state);
            //修改端口信息
            templateDao.updateBusBindingPortState(portReqDto);
        });
        //看盘下的端口状态是否都是空闲状态
        RealPortReqDto discReq = new RealPortReqDto();
        discReq.setDiscId(realRspDtos.get(0).getDiscId());
        List<RealRspDto> collect = realRspDtos.stream().filter(obj -> obj.getBusState()
                .equals(AppConstant.BUS_BINDING_DEFAULT_STATE)).collect(Collectors.toList());
        if (collect == null || collect.size() == AppConstant.BUS_BINDING_DEFAULT_STATE) {
            //都是0 则允许修改在位不在位状态
            discReq.setBusState(AppConstant.BUS_BINDING_DEFAULT_STATE);
        } else {
            discReq.setBusState(AppConstant.BUS_BINDING_STATE_NO_OPERATOR);
        }
        templateDao.updateBusBindingDiscState(discReq);

    }


    /**
     * 根据端口信息查询端口id
     *
     * @param portInfoReqDto 端口信息请求
     * @return 端口id
     */
    @Override
    public String queryPortIdByPortInfo(PortInfoReqDto portInfoReqDto) {
        return templateDao.queryPortIdByPortInfo(portInfoReqDto);
    }

    /**
     * 修改模板
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
     * 删除模板信息
     *
     * @param templateReqDto 模板信息
     * @return 是否修改或者删除成功
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
        // 校验是否可以删除
        //这里需要根据模板类型来找
        List<String> templateIds = Lists.newArrayList();
        switch (templateType) {
            case 0:
                templateIds.add(templateId);
                deleteTemplateById(templateIds, templateId);
                break;
            case 1:
                //删除框模板 这里需要找到子集
                List<TemplateRspDto> templateRspDtos = templateDao.queryTemplateByType(TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal());
                if (templateRspDtos != null && templateRspDtos.size() != 0) {
                    //看上级有没有被引用
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
                // 删除盘模板
                List<TemplateRspDto> frames = templateDao.queryTemplateByType(TemplateTypeEnum.TEMPLATE_TYPE_FRAME.ordinal());
                if (frames == null) {
                    templateDao.deleteTemplateById(templateId);
                    break;
                }
                if (frames.size() != 0) {
                    // 有绑定框的 就不让删除
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
     * 批量修改 成端 端口状态
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
     * App 新增标签后 失败回滚
     *
     * @param deviceId 设施id
     */
    @Override
    public void deleteDeviceEntity(String deviceId) {
        List<RealRspDto> realRspDtos = templateDao.queryRealPositionByDeviceId(deviceId);
        // 获取信息
        List<String> collect = realRspDtos.stream().map(RealRspDto::getId).collect(Collectors.toList());
        if (collect != null && collect.size() != 0) {
            templateDao.deletePort(collect);
        }
        //删除标签
        templateDao.deleteDeviceEntity(deviceId);
    }

    /**
     * 查询端口号
     *
     * @param portId
     * @return
     */
    @Override
    public String queryPortNumByPortId(String portId) {
        return templateDao.queryPortNumByPortId(portId);
    }

    /**
     * 删除模板通过模板id
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
     * 转换绑定状态
     *
     * @param busType      业务类型
     * @param oldBindState 原来的绑定状态 0-0-0
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
            //成端 第一位 获取第一个出现 - 的下标 然后进行切割后重新拼接成一个字符窜
            int index = oldBindState.indexOf(AppConstant.SEPARATOR_INDEX_UP_LINE);
            String substring = oldBindState.substring(index, oldBindState.length());
            sb.append(state).append(substring);
        } else if (BusTypeEnum.BUS_TYPE_JUMP.ordinal() == busType) {
            //跳接 第二位
            int index = oldBindState.indexOf(AppConstant.SEPARATOR_INDEX_UP_LINE);
            int last = oldBindState.lastIndexOf(AppConstant.SEPARATOR_INDEX_UP_LINE);
            sb.append(oldBindState.subSequence(0, index))
                    .append(AppConstant.SEPARATOR_INDEX_UP_LINE)
                    .append(state)
                    .append(oldBindState.subSequence(last, oldBindState.length()));
        } else if (BusTypeEnum.BUS_TYPE_PORT.ordinal() == busType) {
            //端口标签 第三位
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
     * 获取用户设施类型信息
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
     * 查询当前用户信息
     *
     * @return
     */
    private User getCurrentUser() {
        //查询当前用户对象
        Object userObj = userFeign.queryCurrentUser(RequestInfoUtils.getUserId(), RequestInfoUtils.getToken());
        //转换为User
        return convertObjectToUser(userObj);
    }

    /**
     * 转换为用户对象
     *
     * @param userObj
     * @return
     */
    private User convertObjectToUser(Object userObj) {
        //校验是否有值
        if (userObj == null) {
            throw new FilinkRfIdException(RfIdI18nConstant.PARAMS_ERROR);
        }
        User user = JSON.parseObject(JSON.toJSONString(userObj), User.class);
        //校验用户信息
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
     * 计算放大的坐标
     * 等比放大处理
     *
     * @param realRspDto 坐标实体
     * @param xApace     横向偏移量
     * @param yApace     纵向偏移量
     * @param lope       放大倍数
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
     * 递归获取全部的数据
     *
     * @param parent  父
     * @param allReal 全部的数据
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
     * 获取实景图递归
     *
     * @param parent  父
     * @param allReal 全部数据
     * @return 实景图数据
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
     * 获取全部的模板数据
     *
     * @param templateVOS 全部的数据
     * @param templateVO  查询条件
     */
    public List<TemplateRspDto> getTemplateData(List<TemplateRspDto> templateVOS, TemplateReqDto templateVO) {
        // 处理模板数据
        if (templateVOS != null && templateVOS.size() != 0) {
            Map<String, TemplateRspDto> allTemplateMap = templateVOS.stream().collect(Collectors.toMap(obj -> obj.getId(), obj -> obj));
            //过滤找出全部的父类
            templateVOS = templateVOS.parallelStream().filter(obj -> StringUtils.isNotEmpty(obj.getChildTemplateId()))
                    .filter(obj -> obj.getTemplateType().equals(templateVO.getTemplateType())).collect(Collectors.toList());
            if (templateVOS == null || templateVOS.size() == 0) {
                return templateVOS;
            }
            //箱的数据
            templateVOS.stream().forEach(obj -> {
                //父类去找对应的子集
                //找对应的子集 框的数据
                List<TemplateRspDto> childList = getChildList(obj, allTemplateMap);
                obj.setChildTemplateList(childList);
            });
        }
        return templateVOS;
    }

    /**
     * 获取对应的模板数据
     *
     * @param parent
     * @param allTemplateMap
     * @return
     */
    private List<TemplateRspDto> getChildList(TemplateRspDto parent, Map<String, TemplateRspDto> allTemplateMap) {
        List<TemplateRspDto> list = Lists.newArrayList();
        if (parent != null && StringUtils.isNotEmpty(parent.getChildTemplateId())) {
            //不为空继续寻找
            List<String> ids = Arrays.asList(parent.getChildTemplateId().split(AppConstant.SEPARATOR));
            // 递归找子集
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
