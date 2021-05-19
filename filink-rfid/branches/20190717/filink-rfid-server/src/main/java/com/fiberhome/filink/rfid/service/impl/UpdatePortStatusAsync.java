package com.fiberhome.filink.rfid.service.impl;

import com.fiberhome.filink.rfid.bean.fibercore.JumpFiberInfo;
import com.fiberhome.filink.rfid.constant.AppConstant;
import com.fiberhome.filink.rfid.dao.fibercore.JumpFiberInfoDao;
import com.fiberhome.filink.rfid.enums.BusTypeEnum;
import com.fiberhome.filink.rfid.req.fibercore.InsertPortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.UpdatePortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationJumpFiberInfoForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationJumpFiberInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.template.PortCableReqDto;
import com.fiberhome.filink.rfid.req.template.PortInfoReqDto;
import com.fiberhome.filink.rfid.resp.fibercore.JumpFiberInfoResp;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 更新端口线程 服务类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-07-03
 */
@Component
@Slf4j
public class UpdatePortStatusAsync {

    /**
     * 注入模板接口
     */
    @Autowired
    TemplateService templateService;

    @Autowired
    private JumpFiberInfoDao jumpFiberInfoDao;

    /**
     * 更新端口使用状态
     *
     * @param jumpFiberInfoList app跳接信息列表
     * @return void
     */
    @SuppressWarnings("all")
    public void updatePortBindingState(List<JumpFiberInfoResp> jumpFiberInfoList) {
        for (JumpFiberInfo jumpFiberInfo : jumpFiberInfoList) {
            //获取本端端口id
            PortInfoReqDto thisPortInfoReqDto = new PortInfoReqDto();
            thisPortInfoReqDto.setDeviceId(jumpFiberInfo.getDeviceId());
            thisPortInfoReqDto.setBoxSide(jumpFiberInfo.getBoxSide());
            thisPortInfoReqDto.setFrameNo(jumpFiberInfo.getFrameNo());
            thisPortInfoReqDto.setDiscSide(jumpFiberInfo.getDiscSide());
            thisPortInfoReqDto.setDiscNo(jumpFiberInfo.getDiscNo());
            thisPortInfoReqDto.setPortNo(jumpFiberInfo.getPortNo());
            String thisPortId = templateService.queryPortIdByPortInfo(thisPortInfoReqDto);

            //获取对端端口id
            PortInfoReqDto oppositePortInfoReqDto = new PortInfoReqDto();
            oppositePortInfoReqDto.setDeviceId(jumpFiberInfo.getOppositeDeviceId());
            oppositePortInfoReqDto.setBoxSide(jumpFiberInfo.getOppositeBoxSide());
            oppositePortInfoReqDto.setFrameNo(jumpFiberInfo.getOppositeFrameNo());
            oppositePortInfoReqDto.setDiscSide(jumpFiberInfo.getOppositeDiscSide());
            oppositePortInfoReqDto.setDiscNo(jumpFiberInfo.getOppositeDiscNo());
            oppositePortInfoReqDto.setPortNo(jumpFiberInfo.getOppositePortNo());
            String oppositePortId = templateService.queryPortIdByPortInfo(oppositePortInfoReqDto);

            //更新本端端口状态
            templateService.updatePortBindingState(thisPortId, AppConstant.BUS_BINDING_DEFAULT_STATE, BusTypeEnum.BUS_TYPE_JUMP.ordinal(), null);
            //更新对端端口状态
            templateService.updatePortBindingState(oppositePortId, AppConstant.BUS_BINDING_DEFAULT_STATE, BusTypeEnum.BUS_TYPE_JUMP.ordinal(), null);
        }
    }

    /**
     * app更新端口使用状态
     *
     * @param batchOperationJumpFiberInfoForApp app跳接信息
     * @return void
     */
    @SuppressWarnings("all")
    public void updatePortBindingStateForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp) {
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()) {
            //获取本端端口id
            PortInfoReqDto thisPortInfoReqDto = new PortInfoReqDto();
            thisPortInfoReqDto.setDeviceId(operationJumpFiberInfoReqForApp.getDeviceId());
            thisPortInfoReqDto.setBoxSide(operationJumpFiberInfoReqForApp.getBoxSide());
            thisPortInfoReqDto.setFrameNo(operationJumpFiberInfoReqForApp.getFrameNo());
            thisPortInfoReqDto.setDiscSide(operationJumpFiberInfoReqForApp.getDiscSide());
            thisPortInfoReqDto.setDiscNo(operationJumpFiberInfoReqForApp.getDiscNo());
            thisPortInfoReqDto.setPortNo(operationJumpFiberInfoReqForApp.getPortNo());
            String thisPortId = templateService.queryPortIdByPortInfo(thisPortInfoReqDto);

            //获取对端端口id
            PortInfoReqDto oppositePortInfoReqDto = new PortInfoReqDto();
            oppositePortInfoReqDto.setDeviceId(operationJumpFiberInfoReqForApp.getOppositeDeviceId());
            oppositePortInfoReqDto.setBoxSide(operationJumpFiberInfoReqForApp.getOppositeBoxSide());
            oppositePortInfoReqDto.setFrameNo(operationJumpFiberInfoReqForApp.getOppositeFrameNo());
            oppositePortInfoReqDto.setDiscSide(operationJumpFiberInfoReqForApp.getOppositeDiscSide());
            oppositePortInfoReqDto.setDiscNo(operationJumpFiberInfoReqForApp.getOppositeDiscNo());
            oppositePortInfoReqDto.setPortNo(operationJumpFiberInfoReqForApp.getOppositePortNo());
            String oppositePortId = templateService.queryPortIdByPortInfo(oppositePortInfoReqDto);

            Integer state = AppConstant.BUS_BINDING_STATE_NO_OPERATOR;
            //新增
            if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationJumpFiberInfoForApp.getUploadType())) {
                state = AppConstant.BUS_BINDING_STATE_NO_OPERATOR;
                //删除
            } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationJumpFiberInfoForApp.getUploadType())) {
                state = AppConstant.BUS_BINDING_DEFAULT_STATE;
            }
            //更新本端端口状态
            templateService.updatePortBindingState(thisPortId, state, BusTypeEnum.BUS_TYPE_JUMP.ordinal(), null);
            //更新对端端口状态
            templateService.updatePortBindingState(oppositePortId, state, BusTypeEnum.BUS_TYPE_JUMP.ordinal(), null);
        }
    }

    /**
     * app更新端口使用状态
     *
     * @param batchOperationJumpFiberInfoForApp app修改跳接信息
     * @return void
     */
    public void updatePortBindingStateToUpdateForApp(BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp) {
        for (OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp : batchOperationJumpFiberInfoForApp.getOperationJumpFiberInfoReqForAppList()) {
            //获取修改前端口id
            List<JumpFiberInfo> jumpFiberInfoList = jumpFiberInfoDao.getJumpFiberInfoByRfidCode(operationJumpFiberInfoReqForApp.getRfidCode());

            //修改前端口id
            PortInfoReqDto oldPortInfoReqDto = new PortInfoReqDto();
            String deviceId = "";
            Integer boxSide = 0;
            String frameNo = "";
            Integer discSide = 0;
            String discNo = "";
            String portNo = "";
            for (JumpFiberInfo jumpFiberInfo : jumpFiberInfoList) {
                if (operationJumpFiberInfoReqForApp.getRfidCode().equals(jumpFiberInfo.getRfidCode())) {
                    deviceId = jumpFiberInfo.getDeviceId();
                    boxSide = jumpFiberInfo.getBoxSide();
                    frameNo = jumpFiberInfo.getFrameNo();
                    discSide = jumpFiberInfo.getDiscSide();
                    discNo = jumpFiberInfo.getDiscNo();
                    portNo = jumpFiberInfo.getPortNo();
                } else {
                    deviceId = jumpFiberInfo.getOppositeDeviceId();
                    boxSide = jumpFiberInfo.getOppositeBoxSide();
                    frameNo = jumpFiberInfo.getOppositeFrameNo();
                    discSide = jumpFiberInfo.getOppositeDiscSide();
                    discNo = jumpFiberInfo.getOppositeDiscNo();
                    portNo = jumpFiberInfo.getOppositePortNo();
                }
            }
            oldPortInfoReqDto.setDeviceId(deviceId);
            oldPortInfoReqDto.setBoxSide(boxSide);
            oldPortInfoReqDto.setFrameNo(frameNo);
            oldPortInfoReqDto.setDiscSide(discSide);
            oldPortInfoReqDto.setDiscNo(discNo);
            oldPortInfoReqDto.setPortNo(portNo);
            String oldPortId = templateService.queryPortIdByPortInfo(oldPortInfoReqDto);

            //获取修改后端口id
            PortInfoReqDto newPortInfoReqDto = new PortInfoReqDto();
            newPortInfoReqDto.setDeviceId(operationJumpFiberInfoReqForApp.getDeviceId());
            newPortInfoReqDto.setBoxSide(operationJumpFiberInfoReqForApp.getBoxSide());
            newPortInfoReqDto.setFrameNo(operationJumpFiberInfoReqForApp.getFrameNo());
            newPortInfoReqDto.setDiscSide(operationJumpFiberInfoReqForApp.getDiscSide());
            newPortInfoReqDto.setDiscNo(operationJumpFiberInfoReqForApp.getDiscNo());
            newPortInfoReqDto.setPortNo(operationJumpFiberInfoReqForApp.getPortNo());
            String newPortId = templateService.queryPortIdByPortInfo(newPortInfoReqDto);

            //修改前端口状态
            templateService.updatePortBindingState(oldPortId, AppConstant.BUS_BINDING_DEFAULT_STATE, BusTypeEnum.BUS_TYPE_JUMP.ordinal(), null);
            //修改后端口状态
            templateService.updatePortBindingState(newPortId, AppConstant.BUS_BINDING_STATE_NO_OPERATOR, BusTypeEnum.BUS_TYPE_JUMP.ordinal(), null);
        }
    }

    /**
     * 更新端口业务状态
     *
     * @param insertPortCableCoreInfoReqList 添加成端信息
     * @param updatePortCableCoreInfoReqList 更新成端信息
     * @return void
     */
    public void updatePortBindingState(List<InsertPortCableCoreInfoReq> insertPortCableCoreInfoReqList, List<UpdatePortCableCoreInfoReq> updatePortCableCoreInfoReqList) {
        //更新已使用端口
        for (InsertPortCableCoreInfoReq insertPortCableCoreInfoReq : insertPortCableCoreInfoReqList) {
            //排除清空数据的特殊处理
            if (!StringUtils.isEmpty(insertPortCableCoreInfoReq.getPortNo()) && !StringUtils.isEmpty(insertPortCableCoreInfoReq.getOppositeCableCoreNo())){
                //更新端口状态
                templateService.updatePortBindingState(insertPortCableCoreInfoReq.getPortId(), AppConstant.BUS_BINDING_STATE_NO_OPERATOR, BusTypeEnum.BUS_TYPE_FORMATION.ordinal(), null);
            }
        }

        //更新未使用端口
        for (UpdatePortCableCoreInfoReq updatePortCableCoreInfoReq : updatePortCableCoreInfoReqList) {
            //更新端口状态
            templateService.updatePortBindingState(updatePortCableCoreInfoReq.getPortId(), updatePortCableCoreInfoReq.getPortStatus(), BusTypeEnum.BUS_TYPE_FORMATION.ordinal(), null);
        }
    }

    /**
     * app更新端口业务状态
     *
     * @param batchOperationPortCableCoreInfoReqForApp app成端信息
     * @return void
     */
    public void updatePortBindingStateForApp(BatchOperationPortCableCoreInfoReqForApp batchOperationPortCableCoreInfoReqForApp) {
        for (OperationPortCableCoreInfoReqForApp operationPortCableCoreInfoReqForApp : batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList()) {
            //获取端口id
            PortInfoReqDto portInfoReqDto = new PortInfoReqDto();
            portInfoReqDto.setDeviceId(operationPortCableCoreInfoReqForApp.getResourceDeviceId());
            portInfoReqDto.setBoxSide(operationPortCableCoreInfoReqForApp.getResourceBoxSide());
            portInfoReqDto.setFrameNo(operationPortCableCoreInfoReqForApp.getResourceFrameNo());
            portInfoReqDto.setDiscSide(operationPortCableCoreInfoReqForApp.getResourceDiscSide());
            portInfoReqDto.setDiscNo(operationPortCableCoreInfoReqForApp.getResourceDiscNo());
            portInfoReqDto.setPortNo(operationPortCableCoreInfoReqForApp.getPortNo());
            String portId = templateService.queryPortIdByPortInfo(portInfoReqDto);

            Integer state = AppConstant.BUS_BINDING_STATE_NO_OPERATOR;
            //新增
            if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())) {
                state = AppConstant.BUS_BINDING_STATE_NO_OPERATOR;
                //删除
            } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())) {
                state = AppConstant.BUS_BINDING_DEFAULT_STATE;
            }
            //更新端口状态
            templateService.updatePortBindingState(portId, state, BusTypeEnum.BUS_TYPE_FORMATION.ordinal(), null);
        }
    }

    /**
     * 更新端口成端状态
     *
     * @param insertPortCableCoreInfoReqList 添加成端信息
     * @param updatePortCableCoreInfoReqList 更新成端信息
     * @return void
     */
    public void batchUpdatePortCableState(List<InsertPortCableCoreInfoReq> insertPortCableCoreInfoReqList, List<UpdatePortCableCoreInfoReq> updatePortCableCoreInfoReqList) {

        //批量更新端口成端状态
        List<PortCableReqDto> portCableReqDtoList = new ArrayList<>();

        //更新已使用端口
        for (InsertPortCableCoreInfoReq insertPortCableCoreInfoReq : insertPortCableCoreInfoReqList) {
            //排除清空数据的特殊处理
            if (!StringUtils.isEmpty(insertPortCableCoreInfoReq.getPortNo()) && !StringUtils.isEmpty(insertPortCableCoreInfoReq.getOppositeCableCoreNo())){
                PortCableReqDto portCableReqDto = new PortCableReqDto();
                portCableReqDto.setPortId(insertPortCableCoreInfoReq.getPortId());
                portCableReqDto.setPortCableState(AppConstant.BUS_BINDING_STATE_NO_OPERATOR);
                portCableReqDtoList.add(portCableReqDto);
            }
        }

        //更新未使用端口
        for (UpdatePortCableCoreInfoReq updatePortCableCoreInfoReq : updatePortCableCoreInfoReqList) {
            PortCableReqDto portCableReqDto = new PortCableReqDto();
            portCableReqDto.setPortId(updatePortCableCoreInfoReq.getPortId());
            portCableReqDto.setPortCableState(updatePortCableCoreInfoReq.getPortStatus());
            portCableReqDtoList.add(portCableReqDto);
        }

        //批量更新端口成端状态
        templateService.batchUpdatePortCableState(portCableReqDtoList);
    }

    /**
     * app更新端口成端状态
     *
     * @param batchOperationPortCableCoreInfoReqForApp app成端信息
     * @return void
     */
    public void batchUpdatePortCableStateForApp(BatchOperationPortCableCoreInfoReqForApp batchOperationPortCableCoreInfoReqForApp) {
        //批量更新端口成端状态
        List<PortCableReqDto> portCableReqDtoList = new ArrayList<>();
        for (OperationPortCableCoreInfoReqForApp operationPortCableCoreInfoReqForApp : batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList()) {
            //获取端口id
            PortInfoReqDto portInfoReqDto = new PortInfoReqDto();
            portInfoReqDto.setDeviceId(operationPortCableCoreInfoReqForApp.getResourceDeviceId());
            portInfoReqDto.setBoxSide(operationPortCableCoreInfoReqForApp.getResourceBoxSide());
            portInfoReqDto.setFrameNo(operationPortCableCoreInfoReqForApp.getResourceFrameNo());
            portInfoReqDto.setDiscSide(operationPortCableCoreInfoReqForApp.getResourceDiscSide());
            portInfoReqDto.setDiscNo(operationPortCableCoreInfoReqForApp.getResourceDiscNo());
            portInfoReqDto.setPortNo(operationPortCableCoreInfoReqForApp.getPortNo());
            String portId = templateService.queryPortIdByPortInfo(portInfoReqDto);

            Integer state = AppConstant.BUS_BINDING_STATE_NO_OPERATOR;
            //新增
            if (AppConstant.OPERATOR_TYPE_SAVE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())) {
                state = AppConstant.BUS_BINDING_STATE_NO_OPERATOR;
                //删除
            } else if (AppConstant.OPERATOR_TYPE_DELETE.equals(batchOperationPortCableCoreInfoReqForApp.getUploadType())) {
                state = AppConstant.BUS_BINDING_DEFAULT_STATE;
            }

            PortCableReqDto portCableReqDto = new PortCableReqDto();
            portCableReqDto.setPortId(portId);
            portCableReqDto.setPortCableState(state);
            portCableReqDtoList.add(portCableReqDto);
        }

        //批量更新端口成端状态
        templateService.batchUpdatePortCableState(portCableReqDtoList);
    }
}
