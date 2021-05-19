package com.fiberhome.filink.rfid.service.mobile.impl;

import com.fiberhome.filink.bean.FiLinkTimeUtils;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.util.DeviceType;
import com.fiberhome.filink.rfid.bean.facility.*;
import com.fiberhome.filink.rfid.bean.template.RealPosition;
import com.fiberhome.filink.rfid.bean.template.RealPositionCommon;
import com.fiberhome.filink.rfid.bean.template.TemplateVO;
import com.fiberhome.filink.rfid.constant.AppConstant;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCode;
import com.fiberhome.filink.rfid.constant.fibercore.RfIdConstant;
import com.fiberhome.filink.rfid.dao.template.FacilityDao;
import com.fiberhome.filink.rfid.dao.template.TemplateDao;
import com.fiberhome.filink.rfid.enums.BoardPutStateEnum;
import com.fiberhome.filink.rfid.enums.BusTypeEnum;
import com.fiberhome.filink.rfid.enums.PortStateEnum;
import com.fiberhome.filink.rfid.enums.TemplateSideEnum;
import com.fiberhome.filink.rfid.enums.TemplateTypeEnum;
import com.fiberhome.filink.rfid.enums.UploadTypeEnum;
import com.fiberhome.filink.rfid.exception.BizException;
import com.fiberhome.filink.rfid.req.rfid.InsertRfidInfoReq;
import com.fiberhome.filink.rfid.req.template.PortInfoReqDto;
import com.fiberhome.filink.rfid.req.template.RealReqDto;
import com.fiberhome.filink.rfid.req.template.TemplateReqDto;
import com.fiberhome.filink.rfid.resp.template.RealRspDto;
import com.fiberhome.filink.rfid.resp.template.TemplateRspDto;
import com.fiberhome.filink.rfid.service.mobile.MobileService;
import com.fiberhome.filink.rfid.service.rfid.RfidInfoService;
import com.fiberhome.filink.rfid.service.template.impl.TemplateHelper;
import com.fiberhome.filink.rfid.service.template.impl.TemplateServiceImpl;
import com.fiberhome.filink.rfid.utils.ResultI18Utils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.UUIDUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Qing on 2019/6/6.
 * 移动端service实现类
 */
@Slf4j
@Service
public class MobileServiceImpl implements MobileService {

    /**
     * 坐标重刷
     */
    @Autowired
    private MobileAsync mobileAsync;

    /**
     * Dao 层
     */
    @Autowired
    private TemplateDao templateDao;
    /**
     * facilityDao
     */
    @Autowired
    private FacilityDao facilityDao;
    /**
     * 模板服务类
     */
    @Autowired
    private TemplateServiceImpl templateService;
    /**
     * rfid 关联信息
     */
    @Autowired
    private RfidInfoService rfidInfoService;

    /**
     * mysql 批量插入分割数
     */
    @Value("${mysqlBatchNum}")
    private Integer mysqlBatchNum;
    /**
     * 设施实体api
     */
    @Autowired
    private DeviceFeign deviceFeign;

    /**
     * templateHelper
     */
    @Autowired
    private TemplateHelper templateHelper;
    private static final int BOX_LABEL = 1;
    private static final int BOARD_LABEL = 2;
    private static final int PORT_LABEL = 3;

    @Override
    @Transactional
    public Result uploadFacilityInfo(FacilityUploadDto uploadDto) {
        // 上传类型(0新增1删除2修改)
        Integer uploadType = uploadDto.getUploadType();
        if (checkUploadType(uploadType).getCode() != ResultCode.SUCCESS) {
            return checkUploadType(uploadType);
        }
        if (UploadTypeEnum.INSERT.ordinal() == uploadType) {
            //新增
            saveLabelInfo(uploadDto);
        } else if (UploadTypeEnum.DELETE.ordinal() == uploadType) {
            //删除-->解除标签和设施的绑定关系
            deleteLabelInfo(uploadDto);
        } else if (UploadTypeEnum.UPDATE.ordinal() == uploadType) {
            //修改--仅支持单个修改
            updateLabelInfo(uploadDto);
        } else {
            //数据上传类型异常
            throw new BizException(RfIdResultCode.UPDATE_TYPE_IS_WRONG,
                    RfIdI18nConstant.UPDATE_TYPE_IS_WRONG);
        }
        return ResultUtils.success();
    }

    /**
     * 更新标签信息
     *
     * @param uploadDto 标签信息
     */
    private void updateLabelInfo(FacilityUploadDto uploadDto) {
        if (isNotEmpty(uploadDto.getBoxList())) {
            BoxInfoBean boxInfoBean = uploadDto.getBoxList().get(0);
            boxInfoBean.setLastUpdateTime(getUtcTimeStamp());
            //箱架标签信息可以直接修改(所属设施不支持修改)
            facilityDao.updateBoxLabel(boxInfoBean);
        }
        //修改盘
        updateBoardInfo(uploadDto);
        //修改端口
        updatePortInfo(uploadDto);
    }

    /**
     * 修改端口标签
     *
     * @param uploadDto 标签信息
     */
    private void updatePortInfo(FacilityUploadDto uploadDto) {
        if (isNotEmpty(uploadDto.getPortList())) {
            PortInfoBean portInfoBean = uploadDto.getPortList().get(0);
            portInfoBean.setLastUpdateTime(getUtcTimeStamp());
            //修改设备信息需要走删除再新增(涉及到设备的信息 盘 端口 改动的)
            //查询现在数据库内的信息(比较设施的信息是否发生改变)
            List<PortInfoBean> portInfoBeans = facilityDao.queryPortLabelById(portInfoBean.getPortLabel());
            if (isNotEmpty(portInfoBeans)) {
                PortInfoBean existPortLabel = portInfoBeans.get(0);
                if (portInfoBean.getFrameNo().intValue() != existPortLabel.getFrameNo()
                        || portInfoBean.getFrameDouble().intValue() != existPortLabel.getFrameDouble()
                        || portInfoBean.getBoardNo().intValue() != existPortLabel.getBoardNo()
                        || portInfoBean.getPortDouble().intValue() != existPortLabel.getPortDouble()
                        || portInfoBean.getPortNo().intValue() != existPortLabel.getPortNo()) {
                    //查询目标端口状态(对于想要更改的信息所在的端口是否已经绑定了标签)
                    FacilityQueryBean queryBean = new FacilityQueryBean();
                    BeanUtils.copyProperties(portInfoBean, queryBean);
                    List<PortInfoBean> beanList = facilityDao.queryPortLabelByNo(queryBean);
                    if (isNotEmpty(beanList)) {
                        //目标已经有标签
                        throw new BizException(RfIdResultCode.THE_TARGET_IS_ALREADY_BOUND_TO_THE_LABEL,
                                RfIdI18nConstant.THE_TARGET_IS_ALREADY_BOUND_TO_THE_LABEL);
                    } else {
                        //先删除 再新增
                        List<String> labels = uploadDto.getPortList().stream().
                                map(PortInfoBean::getPortLabel).collect(Collectors.toList());
                        facilityDao.deletePortLabelByIds(labels);
                        rfidInfoService.deleteRfidInfo(new LinkedHashSet<>(labels));
                        //删除端口标签 需要告知端口修改状态为空闲
                        uploadDto.getPortList().forEach(port -> {
                            int portState = port.getPortState();
                            //端口的状态为空闲
                            portInfoBean.setPortState(PortStateEnum.FREE.ordinal());
                            askPortToChangeState(port, AppConstant.BUS_BINDING_DEFAULT_STATE);
                            //将端口状态回复,重新贮存
                            port.setPortState(portState);
                        });

                        uploadDto.setUploadType(UploadTypeEnum.INSERT.ordinal());
                        uploadFacilityInfo(uploadDto);

                        //例如:5-->6   之前的端口(5)需要告知将端口修改状态为空闲
                        existPortLabel.setPortState(PortStateEnum.FREE.ordinal());
                        askPortToChangeState(existPortLabel, AppConstant.BUS_BINDING_DEFAULT_STATE);
                    }
                } else {
                    //修改标签的信息,直接修改
                    facilityDao.updatePortLabel(portInfoBean);
                    //这里同步修改端口表中的信息
                    if (portInfoBean.getPortState().intValue() != existPortLabel.getPortState()) {
                        askPortToChangeState(portInfoBean, AppConstant.BUS_BINDING_STATE_NO_OPERATOR);
                    }
                }
            } else {
                //数据库标签已经不存在了
                throw new BizException(RfIdResultCode.CAN_NOT_FIND_INFO_WITH_THIS_LABEL,
                        RfIdI18nConstant.CAN_NOT_FIND_INFO_WITH_THIS_LABEL);

            }
        }
    }

    /**
     * 修改盘标签
     *
     * @param uploadDto 标签信息
     */
    private void updateBoardInfo(FacilityUploadDto uploadDto) {
        if (isNotEmpty(uploadDto.getBoardList())) {
            BoardInfoBean boardInfoBean = uploadDto.getBoardList().get(0);
            boardInfoBean.setLastUpdateTime(getUtcTimeStamp());
            //修改设备信息需要走删除再新增(涉及到设备的信息 盘 端口 改动的)
            //查询现在数据库内的信息(比较设施的信息是否发生改变)
            List<BoardInfoBean> boardInfoBeans = facilityDao.queryBoardLabelById(boardInfoBean.getBoardLabel());
            if (isNotEmpty(boardInfoBeans)) {
                BoardInfoBean existBoardLabel = boardInfoBeans.get(0);
                //设施信息发生改变
                if (boardInfoBean.getFrameNo().intValue() != existBoardLabel.getFrameNo()
                        || boardInfoBean.getFrameDouble().intValue() != existBoardLabel.getFrameDouble()
                        || boardInfoBean.getBoardNo().intValue() != existBoardLabel.getBoardNo()) {
                    //查询目标端口状态(对于想要更改的信息所在的端口是否已经绑定了标签)
                    FacilityQueryBean queryBean = new FacilityQueryBean();
                    BeanUtils.copyProperties(boardInfoBean, queryBean);
                    List<BoardInfoBean> beanList = facilityDao.queryBoardLabelByFraNoAndBoaNo(queryBean);
                    if (isNotEmpty(beanList)) {
                        //目标已经有标签
                        throw new BizException(RfIdResultCode.THE_TARGET_IS_ALREADY_BOUND_TO_THE_LABEL,
                                RfIdI18nConstant.THE_TARGET_IS_ALREADY_BOUND_TO_THE_LABEL);
                    } else {
                        //先删除 再新增
                        List<String> labels = uploadDto.getBoardList().stream().
                                map(BoardInfoBean::getBoardLabel).collect(Collectors.toList());
                        facilityDao.deleteBoardLabelByIds(labels);
                        rfidInfoService.deleteRfidInfo(new LinkedHashSet<>(labels));

                        uploadDto.setUploadType(UploadTypeEnum.INSERT.ordinal());
                        uploadFacilityInfo(uploadDto);
                    }
                } else {
                    //只修改标签的信息,直接修改
                    facilityDao.updateBoardLabel(boardInfoBean);
                }
            } else {
                //数据库标签已经不存在了
                throw new BizException(RfIdResultCode.CAN_NOT_FIND_INFO_WITH_THIS_LABEL,
                        RfIdI18nConstant.CAN_NOT_FIND_INFO_WITH_THIS_LABEL);
            }
        }
    }

    /**
     * 删除标签信息
     *
     * @param uploadDto 标签信息
     */
    private void deleteLabelInfo(FacilityUploadDto uploadDto) {
        //不会真实的删除,走变更接口 用一个虚拟的标签替换掉旧标签---> 解决绑定错误的情况,中转一下
        if (isNotEmpty(uploadDto.getBoxList())) {
            List<String> boxLabels = uploadDto.getBoxList().stream().map(BoxInfoBean::getBoxLabel)
                    .distinct().collect(Collectors.toList());
            //批量更换
            boxLabels.forEach(boxLabel -> {
                changeFacilityLabel(boxLabel, UUIDUtil.getInstance().UUID32(), BOX_LABEL);
            });
        }
        if (isNotEmpty(uploadDto.getBoardList())) {
            List<String> boardLabels = uploadDto.getBoardList().stream().map(BoardInfoBean::getBoardLabel)
                    .distinct().collect(Collectors.toList());
            //批量更换
            boardLabels.forEach(boardLabel -> {
                changeFacilityLabel(boardLabel, UUIDUtil.getInstance().UUID32(), BOARD_LABEL);
            });
        }
        if (isNotEmpty(uploadDto.getPortList())) {
            List<String> portLabels = uploadDto.getPortList().stream().map(PortInfoBean::getPortLabel)
                    .distinct().collect(Collectors.toList());
            //批量更换
            portLabels.forEach(portLabel -> {
                changeFacilityLabel(portLabel, UUIDUtil.getInstance().UUID32(), PORT_LABEL);
            });
        }

    }

    /**
     * 新增标签信息
     *
     * @param uploadDto 标签信息
     */
    private void saveLabelInfo(FacilityUploadDto uploadDto) {
        List<InsertRfidInfoReq> infoReqs = Lists.newArrayList();
        //上传数据
        //上传箱标签之前会上传实体信息,如果标签失败实体信息会产生脏数据,需要回滚
        try {
            uploadBox(uploadDto, infoReqs);
        } catch (RuntimeException e) {
            //手动回滚 删掉实体信息
            templateService.deleteDeviceEntity(uploadDto.getBoxList().get(0).getDeviceId());
            log.warn("INSERT BOX LABEL FAIL, DELETE DEVICE ENTITY");
            throw e;
        }
        uploadBoard(uploadDto, infoReqs);
        uploadPort(uploadDto, infoReqs);
        // 这里需要和智能标签  相关联
        if (infoReqs.size() != 0) {
            rfidInfoService.addRfidInfo(infoReqs);
        }

    }

    /**
     * 上传端口信息
     *
     * @param uploadDto 标签信息
     * @param infoReqs  RFID
     */
    private void uploadPort(FacilityUploadDto uploadDto, List<InsertRfidInfoReq> infoReqs) {
        if (isNotEmpty(uploadDto.getPortList())) {
            List<PortInfoBean> portList = uploadDto.getPortList();
            List<String> checkRepeat = new ArrayList<>();
            //唯一的部分
            List<PortInfoBean> unRepeatPort = new ArrayList<>();
            portList.forEach(obj -> {
                //2019-06-26 增加去重
                String uniValue = obj.getDeviceId() + obj.getFrameNo() + obj.getFrameDouble() + obj.getBoardNo()
                        + obj.getPortNo() + obj.getPortDouble();
                if (checkRepeat.contains(uniValue)) {
                    return;
                } else {
                    //校验是否已经存在
                    FacilityQueryBean queryBean = new FacilityQueryBean();
                    BeanUtils.copyProperties(obj, queryBean);
                    if (!isNotEmpty(facilityDao.queryPortLabelByNo(queryBean))) {
                        //盘 所在设施id+框号+框所属AB面+盘号 唯一
                        checkRepeat.add(uniValue);
                        unRepeatPort.add(obj);
                    } else {
                        //提示用户标签信息已存在
                        throwLabelIsExisted();
                    }
                }
                //未传自动生成
                if (ObjectUtils.isEmpty(obj.getPortLabel())) {
                    obj.setPortLabel(UUIDUtil.getInstance().UUID32());
                }
                //增加主键 不使用label作为主键
                obj.setId(UUIDUtil.getInstance().UUID32());
                //最后修改时间
                obj.setLastUpdateTime(getUtcTimeStamp());
                //端口的boxLabel是否为空
                if (ObjectUtils.isEmpty(obj.getBoxLabel())) {
                    List<BoxInfoBean> boxInfoBeans = facilityDao.queryBoxLabelByDevId(obj.getDeviceId());
                    if (isNotEmpty(boxInfoBeans)) {
                        obj.setBoxLabel(boxInfoBeans.get(0).getBoxLabel());
                    }
                }
                InsertRfidInfoReq infoReq = new InsertRfidInfoReq();
                infoReq.setRfidStatus(getLabelState(obj.getLabelState()));
                infoReq.setRfidType(RfIdConstant.RFID_TYPE_PORT);
                infoReq.setRfidCode(obj.getPortLabel());
                // 这里和烽火同仁 确认 标签类型 置空
                if (obj.getLabelType() != null) {
                    infoReq.setMarkType(obj.getLabelType().toString());
                }
                infoReq.setDeviceId(obj.getDeviceId());
                infoReqs.add(infoReq);
            });
            //新增之前查询标签是否存在
            List<String> labels = unRepeatPort.stream().map(PortInfoBean::getPortLabel).collect(Collectors.toList());
            checkLabelInfoExist(labels);
            facilityDao.batchSavePortLabelInfo(unRepeatPort);
            //新增端口标签 需要告知端口修改状态为占用
            unRepeatPort.forEach(portInfoBean -> {
                if (portInfoBean.getPortState() == null) {
                    portInfoBean.setPortState(PortStateEnum.FREE.ordinal());
                }
                askPortToChangeState(portInfoBean, AppConstant.BUS_BINDING_STATE_NO_OPERATOR);
            });
        }
    }

    /**
     * 修改端口状态
     *
     * @param portInfoBean 端口信息
     */
    private void askPortToChangeState(PortInfoBean portInfoBean, Integer state) {
        PortInfoReqDto portInfoReqDto = new PortInfoReqDto();
        //信息copy
        portInfoReqDto.setDeviceId(portInfoBean.getDeviceId());
        portInfoReqDto.setBoxSide(portInfoBean.getFrameDouble());
        portInfoReqDto.setDiscNo(portInfoBean.getBoardNo().toString());
        portInfoReqDto.setDiscSide(portInfoBean.getPortDouble());
        portInfoReqDto.setFrameNo(portInfoBean.getFrameNo().toString());
        portInfoReqDto.setPortNo(portInfoBean.getPortNo().toString());

        String portId = templateDao.queryPortIdByPortInfo(portInfoReqDto);
        if (!StringUtils.isEmpty(portId)) {
            //端口状态
            templateService.updatePortBindingState(portId, state, BusTypeEnum.BUS_TYPE_PORT.ordinal(),
                    portInfoBean.getPortState());
        } else {
            //为找到对应端口,记录日志
            log.warn("INSERT PORT LABEL WITH NOT FIND PORT ID , NO CHANGE PORT STATE");
        }
    }

    /**
     * 上传盘信息
     *
     * @param uploadDto 标签信息
     * @param infoReqs  RFID
     */
    private void uploadBoard(FacilityUploadDto uploadDto, List<InsertRfidInfoReq> infoReqs) {
        if (isNotEmpty(uploadDto.getBoardList())) {
            List<BoardInfoBean> boardList = uploadDto.getBoardList();
            List<String> checkRepeat = new ArrayList<>();
            //唯一的部分
            List<BoardInfoBean> unRepeatBoard = new ArrayList<>();
            boardList.forEach(obj -> {
                //2019-06-26 增加去重
                String uniValue = obj.getDeviceId() + obj.getFrameNo() + obj.getFrameDouble() + obj.getBoardNo();
                if (checkRepeat.contains(uniValue)) {
                    return;
                } else {
                    //校验是否已经存在
                    FacilityQueryBean queryBean = new FacilityQueryBean();
                    BeanUtils.copyProperties(obj, queryBean);
                    if (!isNotEmpty(facilityDao.queryBoardLabelByFraNoAndBoaNo(queryBean))) {
                        //盘 所在设施id+框号+框所属AB面+盘号 唯一
                        unRepeatBoard.add(obj);
                        checkRepeat.add(uniValue);
                    } else {
                        //提示用户标签信息已存在
                        throwLabelIsExisted();
                    }
                }
                //未传自动生成
                if (ObjectUtils.isEmpty(obj.getBoardLabel())) {
                    obj.setBoardLabel(UUIDUtil.getInstance().UUID32());
                }
                //增加主键 不使用label作为主键
                obj.setId(UUIDUtil.getInstance().UUID32());
                //最后修改时间
                obj.setLastUpdateTime(getUtcTimeStamp());
                //盘的boxLabel是否为空 为空需要去查询
                if (ObjectUtils.isEmpty(obj.getBoxLabel())) {
                    List<BoxInfoBean> boxInfoBeans = facilityDao.queryBoxLabelByDevId(obj.getDeviceId());
                    if (isNotEmpty(boxInfoBeans)) {
                        obj.setBoxLabel(boxInfoBeans.get(0).getBoxLabel());
                    }
                }
                InsertRfidInfoReq infoReq = new InsertRfidInfoReq();
                infoReq.setRfidStatus(getLabelState(obj.getLabelState()));
                infoReq.setRfidType(RfIdConstant.RFID_TYPE_DICS);
                infoReq.setRfidCode(obj.getBoardLabel());
                infoReq.setMarkType(obj.getLabelType().toString());
                infoReq.setDeviceId(obj.getDeviceId());
                infoReqs.add(infoReq);
            });
            //新增之前查询标签是否存在
            List<String> labels = unRepeatBoard.stream().map(BoardInfoBean::getBoardLabel).collect(Collectors.toList());
            checkLabelInfoExist(labels);
            facilityDao.batchSaveBoardLabelInfo(unRepeatBoard);
        }
    }

    /**
     * 上传箱架信息
     *
     * @param uploadDto 标签信息
     * @param infoReqs  RFID
     */
    private void uploadBox(FacilityUploadDto uploadDto, List<InsertRfidInfoReq> infoReqs) {
        if (isNotEmpty(uploadDto.getBoxList())) {
            List<BoxInfoBean> boxList = uploadDto.getBoxList();
            List<String> checkRepeat = new ArrayList<>();
            //唯一的部分
            List<BoxInfoBean> unRepeatBox = new ArrayList<>();
            boxList.forEach(obj -> {
                //2019-06-26 增加去重
                if (uploadDto.getDeviceType() != null) {
                    // 修复web 端没有存设施类型
                    obj.setDeviceType(uploadDto.getDeviceType());
                }
                if (checkRepeat.contains(obj.getDeviceId())) {
                    return;
                } else {
                    //校验是否存在
                    if (!isNotEmpty(facilityDao.queryBoxLabelByDevId(obj.getDeviceId()))) {
                        //框架 对应deviceId唯一
                        checkRepeat.add(obj.getDeviceId());
                        unRepeatBox.add(obj);
                    } else {
                        //提示用户标签信息已存在
                        throwLabelIsExisted();
                    }
                }
                //与烽火张智经理确认 label可以不必填 上传时如果没有 自动生成假的 2019-06-25 16:24:00
                if (ObjectUtils.isEmpty(obj.getBoxLabel())) {
                    obj.setBoxLabel(UUIDUtil.getInstance().UUID32());
                }
                //增加主键 不使用label作为主键
                obj.setId(UUIDUtil.getInstance().UUID32());
                //最后修改时间
                obj.setLastUpdateTime(getUtcTimeStamp());
                InsertRfidInfoReq infoReq = new InsertRfidInfoReq();
                infoReq.setRfidStatus(getLabelState(obj.getLabelState()));
                infoReq.setRfidType(RfIdConstant.RFID_TYPE_BOX);
                infoReq.setRfidCode(obj.getBoxLabel());
                infoReq.setMarkType(obj.getLabelType().toString());
                infoReq.setDeviceId(obj.getDeviceId());
                infoReqs.add(infoReq);
            });
            //新增之前查询标签是否存在
            List<String> labels = unRepeatBox.stream().map(BaseInfoBean::getBoxLabel).collect(Collectors.toList());
            checkLabelInfoExist(labels);
            facilityDao.batchSaveBoxLabelInfo(unRepeatBox);
        }
    }

    /**
     * 转换标签状态
     *
     * @param labelState 标签状态
     * @return 标签状态
     */
    private String getLabelState(Integer labelState) {
        return labelState == null ? "" : labelState.toString();
    }

    /**
     * 集合判空
     *
     * @param list 集合
     * @return 是否为空
     */
    private static boolean isNotEmpty(List list) {
        return list != null && list.size() > 0;
    }

    @Override
    public Result queryFacilityINfo(FacilityQueryBean queryBean) {
        //1.判断标签ID，2.type， 3.判断设施ID 框ID 盘ID 端口ID ，
        //标签ID
        String label = queryBean.getRfidLabel();
        FacilityInfoBean facilityInfoBean = new FacilityInfoBean();
        //设施标签信息
        List<BoxInfoBean> boxInfoBeans = new ArrayList<>();
        List<BoardInfoBean> boardInfoBeans = new ArrayList<>();
        List<PortInfoBean> portInfoBeans = new ArrayList<>();
        if (StringUtils.isEmpty(label)) {
            Integer queryType = queryBean.getQueryType();
            if (!ObjectUtils.isEmpty(queryType)) {
                //设施实体
                //已经与烽火同仁确认，标签ID未传时，设施ID必传 2018-06-14 16:37:00
                switch (queryType) {
                    case BOX_LABEL:
                        //查询设施ID的箱架信息---box_label表 设施id为条件
                        boxInfoBeans = getBoxInfoBeans(queryBean);
                        break;
                    case BOARD_LABEL:
                        // board_label表  deviceId/frameNo/frameDouble和boardNo为条件
                        boardInfoBeans = facilityDao.queryBoardLabelByFraNoAndBoaNo(queryBean);
                        break;
                    case PORT_LABEL:
                        // ---port_label表 deviceId/frameNo/frameDouble/boardNo、portNo/portDouble为查询条件
                        portInfoBeans = facilityDao.queryPortLabelByNo(queryBean);
                        break;
                    default:
                        log.info("not find query type");
                        break;
                }
            }
        } else {
            //直接查询标签对应的信息 查三张表   主键为条件
            boxInfoBeans = facilityDao.queryBoxLabelById(label);
            boardInfoBeans = facilityDao.queryBoardLabelById(label);
            portInfoBeans = facilityDao.queryPortLabelById(label);
        }
        boxInfoBeans.forEach(boxInfoBean -> {
            boxInfoBean.setDeviceName(getDeviceName(boxInfoBean.getDeviceId()));
        });
        boardInfoBeans.forEach(boardInfoBean -> {
            boardInfoBean.setDeviceName(getDeviceName(boardInfoBean.getDeviceId()));
        });
        portInfoBeans.forEach(portInfoBean -> {
            portInfoBean.setDeviceName(getDeviceName(portInfoBean.getDeviceId()));
        });
        facilityInfoBean.setBoxList(boxInfoBeans);
        facilityInfoBean.setBoardList(boardInfoBeans);
        facilityInfoBean.setPortList(portInfoBeans);
        return ResultUtils.success(facilityInfoBean);
    }


    /**
     * 实时获取设施名称
     *
     * @param deviceId 设施id
     * @return
     */
    private String getDeviceName(String deviceId) {
        return deviceFeign.queryDeviceNameById(deviceId);
    }

    /**
     * 查询箱架标签信息
     *
     * @param queryBean 查询条件
     * @return 箱架标签信息
     */
    private List<BoxInfoBean> getBoxInfoBeans(FacilityQueryBean queryBean) {
        List<BoxInfoBean> boxInfoBeans;
        boxInfoBeans = facilityDao.queryBoxLabelByDevId(queryBean.getDeviceId());
        if (queryBean.getDeviceType().equals(DeviceType.Optical_Box.getCode())
                || queryBean.getDeviceType().equals(DeviceType.Distribution_Frame.getCode())) {
            List<TemplateReqDto> templateReqDto = queryDeviceEntity(queryBean.getDeviceId());
            if (isNotEmpty(templateReqDto)) {
                //与烽火同仁确认 设施没有模板信息时 无需提示用户 直接返回空数据即可 2019-06-24 16:31:00
                boxInfoBeans.forEach(boxInfoBean -> {
                    //设置模板信息
                    boxInfoBean.setMouldName(templateReqDto.get(0).getBoxName());
                });
            }
        }
        return boxInfoBeans;
    }

    /**
     * 根据设施ID查询实体信息
     *
     * @param deviceId 设施ID
     * @return 实体信息
     */
    private List<TemplateReqDto> queryDeviceEntity(String deviceId) {
        return templateDao.queryFacilityInfoByCondition(deviceId);
    }

    /**
     * 新增设施
     *
     * @param uploadDto 设施信息
     */
    private Result addDevice(DeviceUploadDto uploadDto) {
        //校验该device 是否已经存在 一个设施只允许建立一套逻辑
        boolean isExists = templateDao.queryDeviceExistsById(uploadDto.getDeviceId()) > 0;
        if (isExists) {
            throw new BizException(RfIdResultCode.DEVICE_IS_EXISTS_BUS,
                    RfIdI18nConstant.DEVICE_IS_EXISTS_BUS);
        }

        //app 端保存设施的关系
        TemplateReqDto dto = new TemplateReqDto();
        BeanUtils.copyProperties(uploadDto, dto);
        dto.setBoxName(uploadDto.getBoxMould().getName());
        dto.setBoxTemplateId(uploadDto.getBoxMould().getId());
        //开始新增 设施和模板的关系 以及生成坐标图
        return templateService.saveDeviceAndTempRelation(dto);
    }

    /**
     * 增加修改后的设施
     *
     * @param uploadDto
     * @param addFrameList
     * @param addDiscList
     */
    private void addUpdateAfterDevice(DeviceUploadDto uploadDto, List<RealRspDto> addFrameList, List<RealRspDto> addDiscList) {
        //完全都是新增 设施 需要去保存关系和计算全部的坐标
        //保存关系表和箱模板的数据
        // 计算出箱的坐标
        List<RealRspDto> boxRealPositionList = Lists.newArrayList();
        BaseMould boxMould = uploadDto.getBoxMould();

        TemplateReqDto templateReqDto = new TemplateReqDto();
        BeanUtils.copyProperties(uploadDto, templateReqDto);
        templateReqDto.setBoxName(boxMould.getName());
        templateReqDto.setBoxTemplateId(boxMould.getId());
        templateReqDto.setTemplateType(TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal());
        // 这里需要计算出所有的模板信息

        boolean boxSideFlag = boxMould.getReversible() == 2;
        String deviceId = uploadDto.getDeviceId();
        //1 保存箱模板时 计算全部的坐标和编号
        List<TemplateRspDto> allTemplate = templateDao.queryBoxTemplateById(templateReqDto.getBoxTemplateId());
        // 树结构的全部数据
        List<TemplateRspDto> templateData = templateService.getTemplateData(allTemplate, templateReqDto);

        RealPositionCommon common = templateHelper.calcWeightAndHeight(templateData);
        List<RealPosition> boxs = templateHelper.calcBoxRealPosition(boxSideFlag, deviceId, templateReqDto, boxMould.getId(), common);
//        数据转换
        boxs.parallelStream().forEach(obj -> {
            RealRspDto realRspDto = new RealRspDto();
            BeanUtils.copyProperties(obj, realRspDto);
            boxRealPositionList.add(realRspDto);
        });
        // 计算坐标并保存
        mobileAsync.calcNewPosition(uploadDto, boxRealPositionList, addFrameList, addDiscList);
    }

    /**
     * 修改设施
     *
     * @param uploadDto 更新设施的时候
     * @return Result 返回Result
     */
    private Result updateDevice(DeviceUploadDto uploadDto) {
        /**
         * 主要逻辑
         * 获取DeviceUploadDto.getFrameEntity  中的数据
         * 根据是否有id 来判断是新增还是修改  分别丢到list 中去
         * 先新增 框盘信息  在根据框盘号 id 修改对应的数据
         * 重新刷queen 坐标
         * 保存和修改对应的数据
         */
        List<FrameEntity> frameEntity = uploadDto.getFrameEntity();
        if (frameEntity == null || frameEntity.size() == 0) {
            return ResultI18Utils.convertWarnResult(RfIdResultCode.PARAMS_ERROR, RfIdI18nConstant.PARAMS_ERROR);
        }
        // 筛选 frameEntity 中的 是否有新增
        List<RealRspDto> upFrameList = Lists.newArrayList();
        List<RealRspDto> addFrameList = Lists.newArrayList();
        List<RealRspDto> upDiscList = Lists.newArrayList();
        List<RealRspDto> addDiscList = Lists.newArrayList();

        // 判断框盘 是否有新增
        frameEntity.forEach(obj -> {
            RealRspDto frame = new RealRspDto();
            frame.setDeviceId(uploadDto.getDeviceId());
            frame.setBusinessNum(obj.getRealNo());
            frame.setRealNo(obj.getMouldNo());
            frame.setState(obj.getState());
            frame.setSide(obj.getFrameDouble());
            if (obj.getId() != null) {
                frame.setId(obj.getId());
                upFrameList.add(frame);
            } else {
                frame.setId(NineteenUUIDUtils.uuid());
                frame.setDeviceType(TemplateTypeEnum.TEMPLATE_TYPE_FRAME.ordinal());
                frame.setBusState(AppConstant.BUS_BINDING_DEFAULT_STATE);
                addFrameList.add(frame);
            }
            if (obj.getBoardEntity() != null && obj.getBoardEntity().size() != 0) {
                obj.getBoardEntity().forEach(board -> {
                    RealRspDto disc = new RealRspDto();
                    disc.setDeviceId(uploadDto.getDeviceId());
                    disc.setBusinessNum(board.getRealNo());
                    disc.setRealNo(board.getMouldNo());
                    disc.setState(board.getState());
                    disc.setSide(TemplateSideEnum.TEMPLATE_SIDE_STATE_A.ordinal());
                    if (board.getId() != null) {
                        disc.setId(board.getId());
                        upDiscList.add(disc);
                    } else {
                        // 存父id
                        disc.setId(NineteenUUIDUtils.uuid());
                        disc.setDeviceType(TemplateTypeEnum.TEMPLATE_TYPE_DISC.ordinal());
                        disc.setParentId(frame.getId());
                        disc.setBusBindingState(AppConstant.BUS_BINDING_DEFAULT);
                        disc.setCodeRule(uploadDto.getDiscCodeRule());
                        disc.setTrend(uploadDto.getDiscTrend());
                        disc.setDeviceId(uploadDto.getDeviceId());
                        // 这里增加 所属框id
                        disc.setFrameId(frame.getId());
                        disc.setPutState(uploadDto.getFrameMould().getPutState());
                        disc.setBusState(AppConstant.BUS_BINDING_DEFAULT_STATE);
                        addDiscList.add(disc);
                    }
                });
            }
        });
        // 判断是否有新增设施
        if (addFrameList.size() == 0 && addDiscList.size() == 0) {
            // 没有新增 只是修改状态
            updateDeviceInfo(upFrameList, upDiscList);
        } else if (upFrameList.size() == 0 && upDiscList.size() == 0) {
            // 都是新增  新增中状态是否是改变的
            // app 筛选是否有状态改变的
            boolean isAddFlag = true;
            if (isNotEmpty(addFrameList)) {
                //true 没有改变
                isAddFlag = addFrameList.stream().filter(obj -> obj.getState() == 0)
                        .collect(Collectors.toList()).size() == 0;
            }
            if (isNotEmpty(addDiscList)) {
                // true  没有改变状态
                isAddFlag = addDiscList.stream().filter(obj -> obj.getState() == 0)
                        .collect(Collectors.toList()).size() == 0 && isAddFlag;
            }

            if (isAddFlag) {
                //没有改变直接调取新增
                addDevice(uploadDto);
            } else {
                //新增后有状态更改的
                addUpdateAfterDevice(uploadDto, addFrameList, addDiscList);
            }

        } else {
            // 有微调 需要去重新刷queen 坐标和 对应的关系  只允许动框
            // 坐标都会变更 这里需要重新刷取坐标
            // 修改关系表  t_relation_device 根据 deviceId 修改对应的 box_template_id
            // 根据关系表中的关系id 可以获取到对应的全部坐标信息 然后重新刷一遍坐标
            // 批量的修改坐标和状态 端口表中的状态不修改 默认还是空闲
            // 这里需要根据模板编号 反向生成一套colNo 和rowNo
            // 在根据上级的父 首坐标 间距 col row 来重新计算坐标

            //1 修改了关系表
            TemplateReqDto reqDto = new TemplateReqDto();
            reqDto.setBoxTemplateId(uploadDto.getBoxMould().getId());
            reqDto.setDeviceId(uploadDto.getDeviceId());
            reqDto.setBoxName(uploadDto.getBoxMould().getName());
            //2 deviceId 获取 box 的坐标 框增加了 设置父id 存到frameList
            List<RealRspDto> boxReal = templateDao.queryRealPositionByDeviceId(uploadDto.getDeviceId());
            if (boxReal == null || boxReal.size() == 0) {
                return ResultI18Utils.convertWarnResult(RfIdResultCode.QUERY_POSITION_INFO_ERROR,
                        RfIdI18nConstant.QUERY_POSITION_INFO_ERROR);
            }
            // 这里重新算取箱的坐标和高度
            //1 保存箱模板时 计算全部的坐标和编号
            TemplateReqDto templateReqDto = new TemplateReqDto();
            BeanUtils.copyProperties(uploadDto, templateReqDto);
            templateReqDto.setBoxName(uploadDto.getBoxMould().getName());
            templateReqDto.setBoxTemplateId(uploadDto.getBoxMould().getId());
            templateReqDto.setTemplateType(TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal());

            List<TemplateRspDto> allTemplate = templateDao.queryBoxTemplateById(templateReqDto.getBoxTemplateId());
            // 树结构的全部数据
            List<TemplateRspDto> templateData = templateService.getTemplateData(allTemplate, templateReqDto);
            RealPositionCommon common = templateHelper.calcWeightAndHeight(templateData);
            boxReal.stream().forEach(obj -> {
                obj.setWidth(common.getBoxWidth());
                obj.setHeight(common.getBoxHeight());
            });

            //刷原来的坐标 异步线程处理
            mobileAsync.calcOldPosition(uploadDto, boxReal, upFrameList, upDiscList);
            // 计算新的坐标
            mobileAsync.calcNewPosition(uploadDto, boxReal, addFrameList, addDiscList);
            //修改关系表
            templateDao.updateRelation(reqDto);
            // 修改箱的高度
            templateDao.updateRealPositionBox(boxReal);
        }
        return ResultUtils.success();
    }


    /**
     * 修改设施状态
     *
     * @param frames 框的信息
     * @param discs  盘的信息
     */
    private void updateDeviceInfo(List<RealRspDto> frames, List<RealRspDto> discs) {
        //这里定义一个常量 批量处理数
        if (frames != null && frames.size() != 0) {
            //判断是否需要分批去更细数据
            List<List<RealRspDto>> frameList = Lists.newArrayList();
            for (int i = 0; i < getCount(frames.size()); i++) {
                frameList.add(frames.stream().skip((i * mysqlBatchNum)).limit(mysqlBatchNum)
                        .collect(Collectors.toList()));
            }
            frameList.forEach(obj -> {
                facilityDao.updateFrameDeviceState(obj);
            });
        }

        if (discs != null && discs.size() != 0) {
            List<List<RealRspDto>> discList = Lists.newArrayList();
            for (int i = 0; i < getCount(discs.size()); i++) {
                discList.add(discs.stream().skip(i * mysqlBatchNum).limit(mysqlBatchNum)
                        .collect(Collectors.toList()));
            }
            discList.forEach(obj -> {
                facilityDao.updateDiscDeviceState(obj);
            });
        }
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
     * 设施实体信息上传
     *
     * @param uploadDto 实体信息
     * @return
     */
    @Override
    @Transactional
    public Result uploadDeviceInfo(DeviceUploadDto uploadDto) {
        // 上传类型(0新增1删除2修改)
        Integer uploadType = uploadDto.getUploadType();
        if (checkUploadType(uploadType).getCode() != ResultCode.SUCCESS) {
            return checkUploadType(uploadType);
        }
        if (UploadTypeEnum.INSERT.ordinal() == uploadType) {
            //新增
        } else if (UploadTypeEnum.DELETE.ordinal() == uploadType) {
            //删除 也是修改对应的状态 暂时没有这个功能
        } else if (UploadTypeEnum.UPDATE.ordinal() == uploadType) {
            //修改 新增  都是走的这个逻辑 根据是否有主键ID 来判断
            return updateDevice(uploadDto);
        }
        return ResultUtils.success();
    }

    /**
     * 请求设施实体信息
     *
     * @param queryBean 查询条件
     * @return 设施信息
     */
    @Override
    public DeviceEntity queryDeviceInfo(DeviceQueryBean queryBean) {
        // 请求设施实体 需要查询的表有 t_relation_device 关系表  t_template 模板表
        // 这里还有根据 关系表查询下面所有的坐标信息 即是设施信息
        DeviceEntity deviceEntity = new DeviceEntity();
        List<TemplateReqDto> dto = templateDao.queryFacilityInfoByCondition(queryBean.getDeviceId());
        if (dto == null || dto.size() == 0) {
            //查不到模板数据 返回空
            return deviceEntity;
        }

        // 将 A/B 面的框都丢进去
        // 开始转换 拼接实体信息
        List<FrameEntity> frameEntityList = Lists.newArrayList();
        dto.forEach(obj -> {
            BeanUtils.copyProperties(obj, deviceEntity);
            // 根据关系表中的realtion
            // 可以查到对应的坐标信息relationId 和
            RealReqDto realReqDto = new RealReqDto();
            realReqDto.setRelationId(obj.getRelationId());
            realReqDto.setDeviceId(obj.getDeviceId());

            // 查出全部的箱信息 通过deviceId 可以取出唯一的一条箱信息 箱分A/B 面
            List<RealRspDto> boxRealPosition = templateDao.queryBoxRealPosition(realReqDto);

            List<String> boxIds = boxRealPosition.stream().map(RealRspDto::getId).collect(Collectors.toList());
            List<RealRspDto> frameRealPosition = templateDao.queryFrameReal(boxIds);
            // 处理数据 进行数据拼接 根据queryBean 信息里条件 进行过滤
            if (queryBean.getFrameNo() != null) {
                //框的信息不为空
                frameRealPosition = frameRealPosition.stream().filter(frame -> frame.getBusinessNum().equals(queryBean.getFrameNo())).collect(Collectors.toList());
            }
            //框分A/B 面
            if (queryBean.getFrameDouble() != null) {
                frameRealPosition = frameRealPosition.stream().filter(frame1 -> frame1.getSide().equals(queryBean.getFrameDouble())).collect(Collectors.toList());
            }
            if (frameRealPosition == null || frameRealPosition.size() == 0) {
                return;
            }
            List<String> frameIds = frameRealPosition.stream().map(RealRspDto::getId).collect(Collectors.toList());
            //查询框下的盘信息
            List<RealRspDto> discRealPositon = templateDao.queryDiscReal(frameIds);
            if (queryBean.getBoardNo() != null) {
                // 这里的盘信息也是一样
                discRealPositon = discRealPositon.stream().filter(
                        obj1 -> obj1.getBusinessNum().equals(queryBean.getBoardNo())).collect(Collectors.toList());
            }
            List<RealRspDto> finalDiscRealPositon = discRealPositon;
            frameRealPosition.forEach(framePosition -> {
                FrameEntity frameEntity = new FrameEntity();
                frameEntity.setFrameDouble(framePosition.getSide());
                frameEntity.setId(framePosition.getId());
                frameEntity.setMouldNo(framePosition.getRealNo());
                frameEntity.setRealNo(framePosition.getBusinessNum());
                frameEntity.setState(framePosition.getState());
                List<BoardEntity> boardEntityList = new ArrayList<>();
                //过滤框下的盘信息
                finalDiscRealPositon.stream().filter(portPosition -> portPosition.getParentId().equals(framePosition.getId()))
                        .forEach(obj2 -> {
                            BoardEntity boardEntity = new BoardEntity();
                            boardEntity.setPortDouble(obj2.getSide());
                            boardEntity.setMouldNo(obj2.getRealNo());
                            boardEntity.setRealNo(obj2.getBusinessNum());
                            boardEntity.setId(obj2.getId());
                            boardEntity.setState(obj2.getState());
                            boardEntity.setBusState(obj2.getBusState());
                            boardEntityList.add(boardEntity);
                        });
                List<BoardEntity> collect = Lists.newArrayList();
                if (boardEntityList != null && boardEntityList.size() != 0) {
                    collect = boardEntityList.stream().filter(board -> board.getBusState().equals(AppConstant.BUS_BINDING_STATE_NO_OPERATOR))
                            .collect(Collectors.toList());
                }
                if (collect == null || collect.size() == 0) {
                    //下属盘没有业务绑定 可以修改
                    frameEntity.setBusState(AppConstant.BUS_BINDING_DEFAULT_STATE);
                } else {
                    frameEntity.setBusState(AppConstant.BUS_BINDING_STATE_NO_OPERATOR);
                }
                frameEntity.setBoardEntity(boardEntityList);
                frameEntityList.add(frameEntity);
            });
        });
        //框实体
        deviceEntity.setFrameEntity(frameEntityList);
        // 模板信息 boxTemplateId 先查出所有的数据
        //查出全部的模板信息 进行分别过滤 拼接 通过箱id 来获取全部的模板数据 这里如果箱有A/B  都是同一个模板
        List<TemplateRspDto> templateRspDtos = templateDao.queryBoxTemplateById(dto.get(0).getBoxTemplateId());
        if (templateRspDtos == null || templateRspDtos.size() == 0) {
            return new DeviceEntity();
        }

        List<TemplateRspDto> boxTemplate = templateRspDtos.stream()
                .filter(obj -> obj.getTemplateType() == TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal()).collect(Collectors.toList());
        List<TemplateRspDto> frameTemplate = templateRspDtos.stream()
                .filter(obj -> obj.getTemplateType() == TemplateTypeEnum.TEMPLATE_TYPE_FRAME.ordinal()).collect(Collectors.toList());
        List<TemplateRspDto> discTemplate = templateRspDtos.stream()
                .filter(obj -> obj.getTemplateType() == TemplateTypeEnum.TEMPLATE_TYPE_DISC.ordinal()).collect(Collectors.toList());
        if (isNotEmpty(boxTemplate)) {
            BaseMould boxMould = new BaseMould();
            BeanUtils.copyProperties(boxTemplate.get(0), boxMould);

            BaseMould frameMould = new BaseMould();
            BeanUtils.copyProperties(frameTemplate.get(0), frameMould);

            BaseMould boardMould = new BaseMould();
            BeanUtils.copyProperties(discTemplate.get(0), boardMould);

            deviceEntity.setBoxMould(boxMould);
            deviceEntity.setFrameMould(frameMould);
            deviceEntity.setBoardMould(boardMould);
        }
        // 通过deviceID 获取deviceName
        deviceEntity.setDeviceName(deviceFeign.queryDeviceNameById(queryBean.getDeviceId()));
        return deviceEntity;
    }


    @Override
    public Result uploadDeviceTemplate(DeviceEntity deviceEntity) {
        //箱架模板
        BaseMould boxMould = deviceEntity.getBoxMould();
        //框模板
        BaseMould frameMould = deviceEntity.getFrameMould();
        //盘模板
        BaseMould boardMould = deviceEntity.getBoardMould();
        TemplateReqDto templateReqDto = new TemplateReqDto();
        templateReqDto.setTemplateType(TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal());
        if (boardMould.getId() != null) {
            templateReqDto.setId(boxMould.getId());
        }
        //查询所有模板信息
        Result allTemplate = templateService.queryAllTemplate(templateReqDto);
        List<TemplateRspDto> resultData = (List<TemplateRspDto>) allTemplate.getData();
        //校验上传是否存在
        for (TemplateRspDto templateRspDto : resultData) {
            //箱结构匹配
            if (boxMould.getCol() == templateRspDto.getCol().intValue()
                    && boxMould.getRow() == templateRspDto.getRow().intValue()) {
                //框模板匹配
                List<TemplateRspDto> frameList = templateRspDto.getChildTemplateList();
                if (frameList != null && frameList.size() > 0) {
                    List<TemplateRspDto> frameCheckList = frameList.stream().filter(frameDto ->
                            frameMould.getCol().intValue() == frameDto.getCol()
                                    && frameMould.getRow().intValue() == frameDto.getRow()).collect(Collectors.toList());
                    if (frameCheckList.size() == frameList.size()) {

                        //盘模板匹配
                        List<TemplateRspDto> boardList = frameCheckList.get(0).getChildTemplateList();
                        List<TemplateRspDto> boardCheckList = boardList.stream().filter(boardDto ->
                                boardMould.getCol().intValue() == boardDto.getCol()
                                        && boardMould.getRow().intValue() == boardDto.getRow()).collect(Collectors.toList());
                        if (boardCheckList.size() == boardList.size()) {
                            //模板增加ID、subName、name信息
                            boardMould.setId(boardCheckList.get(0).getId());
                            boardMould.setName(boardCheckList.get(0).getName());
                            frameMould.setSubname(boardCheckList.get(0).getName());
                            for (TemplateRspDto templateRspDto1 : frameCheckList) {
                                if (templateRspDto1.getChildTemplateId().contains(boardCheckList.get(0).getId())) {
                                    frameMould.setId(templateRspDto1.getId());
                                    frameMould.setName(templateRspDto1.getName());
                                }
                            }
                            boxMould.setId(templateRspDto.getId());
                            boxMould.setName(templateRspDto.getName());
                            boxMould.setSubname(frameCheckList.get(0).getName());
                            //数据存在，直接返回
                            return ResultUtils.success(deviceEntity);
                        }
                    }
                }
            }
        }
        //数据库不存在  保存
        List<TemplateVO> templateVOList = new ArrayList<>();
        TemplateVO boardTemplate = saveTemplate(boardMould, TemplateTypeEnum.TEMPLATE_TYPE_DISC.ordinal(), null);
        TemplateVO frameTemplate = saveTemplate(frameMould, TemplateTypeEnum.TEMPLATE_TYPE_FRAME.ordinal(),
                boardTemplate.getId());
        TemplateVO boxTemplate = saveTemplate(boxMould, TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal(), frameTemplate.getId());
        templateVOList.add(boardTemplate);
        templateVOList.add(frameTemplate);
        templateVOList.add(boxTemplate);
        templateDao.batchSaveTemplate(templateVOList);
        //返回模板信息
        BeanUtils.copyProperties(boxTemplate, boxMould);
        BeanUtils.copyProperties(frameTemplate, frameMould);
        BeanUtils.copyProperties(boardTemplate, boardMould);
        boxMould.setSubname(frameMould.getName());
        frameMould.setSubname(boardMould.getName());
        deviceEntity.setBoardMould(boardMould);
        deviceEntity.setBoxMould(boxMould);
        deviceEntity.setFrameMould(frameMould);
        return ResultUtils.success(deviceEntity);
    }

    @Override
    public List<DeviceEntity> queryAllTemplate() {
        TemplateReqDto templateReqDto = new TemplateReqDto();
        templateReqDto.setTemplateType(TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal());
        //查询所有模板信息
        Result result = templateService.queryAllTemplate(templateReqDto);
        List<TemplateRspDto> resultData = (List<TemplateRspDto>) result.getData();
        List<DeviceEntity> resultList = new ArrayList<>();
        resultData.forEach(rspDto -> {
            //web端使用的结构体与app端使用的不一致，转换
            DeviceEntity deviceEntity = new DeviceEntity();
            BaseMould boardMould = new BaseMould();
            BaseMould frameMould = new BaseMould();
            BaseMould boxMould = getBaseMould(rspDto);
            if (rspDto.getChildTemplateList() != null && rspDto.getChildTemplateList().size() > 0) {
                frameMould = getBaseMould(rspDto.getChildTemplateList().get(0));
                List<TemplateRspDto> boardList = rspDto.getChildTemplateList().get(0).getChildTemplateList();
                if (boardList != null && boardList.size() > 0) {
                    boardMould = getBaseMould(boardList.get(0));
                }
            }
            boxMould.setSubname(frameMould.getName());
            frameMould.setSubname(boardMould.getName());
            deviceEntity.setBoxMould(boxMould);
            deviceEntity.setFrameMould(frameMould);
            deviceEntity.setBoardMould(boardMould);
            resultList.add(deviceEntity);
        });
        return resultList;
    }

    @Override
    @Transactional
    public Result uploadFacilityBusInfo(FacilityBusUploadDto uploadDto) {
        FacilityUploadDto facilityUploadDto = new FacilityUploadDto();
        //检查是否是光交箱 和配线架 ,如果是需要保存相应模板信息
        if (uploadDto.getDeviceType().equals(DeviceType.Optical_Box.getCode())
                || uploadDto.getDeviceType().equals(DeviceType.Distribution_Frame.getCode())) {
            Integer uploadType = uploadDto.getUploadType();
            if (checkUploadType(uploadType).getCode() != ResultCode.SUCCESS) {
                return checkUploadType(uploadType);
            }
            //模板信息只支持新增
            if (UploadTypeEnum.INSERT.ordinal() == uploadType) {
                //判断该设施是否已经绑定了模板信息
                if (!isNotEmpty(queryDeviceEntity(uploadDto.getDeviceId()))) {
                    TemplateReqDto templateReqDto = new TemplateReqDto();
                    BeanUtils.copyProperties(uploadDto, templateReqDto);
                    //保存设施和模板关系
                    Result result = templateService.saveDeviceAndTempRelation(templateReqDto);
                    if (result.getCode() != ResultCode.SUCCESS) {
                        return result;
                    }
                }
            }
        }
        //保存设施标签信息
        BeanUtils.copyProperties(uploadDto, facilityUploadDto);
        Result result1 = uploadFacilityInfo(facilityUploadDto);
        if (result1.getCode() != ResultCode.SUCCESS) {
            return result1;
        }
        return ResultI18Utils.convertSuccess(ResultCode.SUCCESS, RfIdI18nConstant.DEPLOY_BUS_INFO_SUCCESS);
    }

    @Override
    public Result queryBusInfoByDeviceId(String deviceId, String deviceType) {
        //查询设施与模板的关系信息
        TemplateReqDto templateReqDto = new TemplateReqDto();
        List<TemplateReqDto> templateReqDtos = queryDeviceEntity(deviceId);
        if (isNotEmpty(templateReqDtos)) {
            templateReqDto = templateReqDtos.get(0);
        }
        //查询智能标签信息
        FacilityQueryBean queryBean = new FacilityQueryBean();
        queryBean.setDeviceId(deviceId);
        //查询类型--box
        queryBean.setQueryType(1);
        queryBean.setDeviceType(deviceType);
        Result facilityINfo = queryFacilityINfo(queryBean);
        if (facilityINfo.getCode() != ResultCode.SUCCESS) {
            return facilityINfo;
        }
        FacilityInfoBean facilityInfoBean = (FacilityInfoBean) facilityINfo.getData();
        //包装返回结果
        FacilityBusUploadDto result = new FacilityBusUploadDto();
        if (!ObjectUtils.isEmpty(templateReqDto)) {
            BeanUtils.copyProperties(templateReqDto, result);
            result.setBoxName(templateReqDto.getName());
        }
        if (!ObjectUtils.isEmpty(facilityInfoBean)) {
            BeanUtils.copyProperties(facilityInfoBean, result);
        }
        return ResultUtils.success(result);
    }

    @Override
    @Transactional
    public Result changeFacilityLabel(String oldLabel, String newLabel, Integer deviceType) {
        if (!ObjectUtils.isEmpty(deviceType)) {
            //校验新标签是否存在
            List<String> labels = new ArrayList<>();
            labels.add(newLabel);
            checkLabelInfoExist(labels);
            switch (deviceType) {
                case BOX_LABEL:
                    List<BoxInfoBean> boxInfoBeans = facilityDao.queryBoxLabelById(oldLabel);
                    if (isNotEmpty(boxInfoBeans)) {
                        facilityDao.replaceBoxLabel(oldLabel, newLabel);
                        //当箱的标签改变时，盘和端口所在箱标签也需要改变
                        facilityDao.replaceBoardBoxLabel(oldLabel, newLabel);
                        facilityDao.replacePortBoxLabel(oldLabel, newLabel);
                    }
                    break;
                case BOARD_LABEL:
                    List<BoardInfoBean> boardInfoBeans = facilityDao.queryBoardLabelById(oldLabel);
                    if (isNotEmpty(boardInfoBeans)) {
                        facilityDao.replaceBoardLabel(oldLabel, newLabel);
                    }
                    break;
                case PORT_LABEL:
                    List<PortInfoBean> portInfoBeans = facilityDao.queryPortLabelById(oldLabel);
                    if (isNotEmpty(portInfoBeans)) {
                        facilityDao.replacePortLabel(oldLabel, newLabel);
                    }
                    break;
                default:
                    //数据上传类型异常
                    throw new BizException(RfIdResultCode.UPDATE_TYPE_IS_WRONG,
                            RfIdI18nConstant.UPDATE_TYPE_IS_WRONG);
            }
            //将旧标签从rfid中替换掉
            rfidInfoService.changeLabel(newLabel, oldLabel);
        }
        return ResultUtils.success();
    }

    /**
     * 删除device Id
     * 删除设施的时候要回收该设施的所有标签
     *
     * @param deviceId
     * @return Result
     */
    @Override
    @Transactional
    public Result deleteDeviceEntity(String deviceId) {
        facilityDao.recoverBoxLabelByDeviceId(deviceId);
        facilityDao.recoverBoardLabelByDeviceId(deviceId);
        facilityDao.recoverPortLabelByDeviceId(deviceId);
        rfidInfoService.deleteRfidInfoByDeviceId(deviceId);
        // 删除对应的 实景图信息
        templateDao.deleteDeviceEntity(deviceId);
        return ResultUtils.success();
    }

    /**
     * 验证是否有配业务信息
     *
     * @param deviceId
     * @return
     */
    @Override
    public Boolean isExistBus(String deviceId) {
        List<BoxInfoBean> boxInfoBeans = facilityDao.queryBoxLabelByDevId(deviceId);
        if (boxInfoBeans == null || boxInfoBeans.size() == 0) {
            //没有配置业务信息
            return false;
        }
        return true;
    }

    /**
     * 获取模板信息
     *
     * @param rspDto 模板信息
     * @return 模板信息
     */
    private BaseMould getBaseMould(TemplateRspDto rspDto) {
        BaseMould baseMould = new BaseMould();
        baseMould.setId(rspDto.getId());
        baseMould.setName(rspDto.getName());
        baseMould.setRow(rspDto.getRow());
        baseMould.setCol(rspDto.getCol());
        baseMould.setReversible(rspDto.getReversible());
        baseMould.setPutState(rspDto.getPutState());
        return baseMould;
    }

    /**
     * 保存模板信息
     *
     * @param mould           模板
     * @param tempType        模板类型
     * @param childTemplateId 子模板id
     * @return TemplateVO
     */
    private TemplateVO saveTemplate(BaseMould mould, Integer tempType, String childTemplateId) {
        TemplateVO templateVO = new TemplateVO();
        templateVO.setId(UUIDUtil.getInstance().UUID32());
        templateVO.setRow(mould.getRow());
        templateVO.setCol(mould.getCol());
        templateVO.setName(getTemplateName(mould, tempType));
        templateVO.setTemplateType(tempType);
        templateVO.setChildTemplateId(spliceChildTemId(mould, tempType, childTemplateId));
        //单双面数值
        templateVO.setReversible(mould.getReversible());
        //App 端默认就是横着放的
        templateVO.setPutState(mould.getPutState());
//        templateVO.setPutState(BoardPutStateEnum.LAY.ordinal());
        return templateVO;
    }

    /**
     * 获取模板名称
     *
     * @param mould    mould
     * @param tempType 模板类型
     * @return 模板名称
     */
    private String getTemplateName(BaseMould mould, Integer tempType) {
        String templateName = null;
        if (TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal() == tempType) {
            templateName = "BOX-" + mould.getCol() + "-" + mould.getRow() + "-" + System.currentTimeMillis();
        } else if (TemplateTypeEnum.TEMPLATE_TYPE_FRAME.ordinal() == tempType) {
            templateName = "FRAME-" + mould.getCol() + "-" + mould.getRow() + "-" + System.currentTimeMillis();
        } else if (TemplateTypeEnum.TEMPLATE_TYPE_DISC.ordinal() == tempType) {
            templateName = "BOARD-" + mould.getCol() + "-" + mould.getRow() + "-" + System.currentTimeMillis();
        }
        return templateName;
    }

    /**
     * 拼接子ID
     *
     * @param mould           mould
     * @param tempType        模板类别
     * @param childTemplateId 子Id
     * @return 拼接过后ID
     */
    private String spliceChildTemId(BaseMould mould, Integer tempType, String childTemplateId) {
        StringBuilder childTemId = new StringBuilder();
        if (TemplateTypeEnum.TEMPLATE_TYPE_BOX.ordinal() == tempType
                || TemplateTypeEnum.TEMPLATE_TYPE_FRAME.ordinal() == tempType) {
            for (int i = 0; i < mould.getCol() * mould.getRow(); i++) {
                if (i == mould.getCol() * mould.getRow() - 1) {
                    childTemId.append(childTemplateId);
                } else {
                    childTemId.append(childTemplateId).append(",");

                }
            }
        }
        return childTemId.toString();
    }

    /**
     * 校验上传类型
     *
     * @param uploadType 上传类型
     */
    private Result checkUploadType(Integer uploadType) {
        //上传数据类型错误
        return ObjectUtils.isEmpty(uploadType) ? ResultUtils.warn(RfIdResultCode.UPLOAD_DATA_TYPE_ERROR,
                I18nUtils.getSystemString(RfIdI18nConstant.UPLOAD_DATA_TYPE_ERROR)) : ResultUtils.success();
    }

    /**
     * 校验标签信息是否存在
     * 在所有的标签中都不能重复
     *
     * @param labels 标签
     */
    private void checkLabelInfoExist(List<String> labels) {
        //在所有标签中唯一
        if (rfidInfoService.checkRfidCodeListIsExist(new HashSet<>(labels))) {
            //如果数据库找到,提示用户标签已存在
            throwLabelIsExisted();
        }
    }

    /**
     * 提示用户标签已存在
     */
    private void throwLabelIsExisted() {
        //提示用户标签已存在
        throw new BizException(RfIdResultCode.LABEL_IS_EXISTED,
                RfIdI18nConstant.LABEL_IS_EXISTED);
    }

    /**
     * 获取UTC时间戳
     *
     * @return UTC时间戳
     */
    private Long getUtcTimeStamp() {
        return FiLinkTimeUtils.getUtcZeroTimeStamp();
    }
}
