package com.fiberhome.filink.rfid.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.fiberhome.filink.rfid.bean.fibercore.CoreCoreInfo;
import com.fiberhome.filink.rfid.bean.fibercore.PortCableCoreInfo;
import com.fiberhome.filink.rfid.constant.opticcable.OpticCableSectionConstant;
import com.fiberhome.filink.rfid.dao.fibercore.CoreCoreInfoDao;
import com.fiberhome.filink.rfid.dao.fibercore.PortCableCoreInfoDao;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableSectionInfoDao;
import com.fiberhome.filink.rfid.req.fibercore.InsertCoreCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.InsertPortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.UpdatePortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationCoreCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationCoreCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationPortCableCoreInfoReqForApp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 异步更新光缆段使用状态线程 服务类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-07-04
 */
@Component
@Slf4j
public class UpdateOpticCableSectionStatus {

    @Autowired
    private OpticCableSectionInfoDao opticCableSectionInfoDao;

    @Autowired
    private PortCableCoreInfoDao portCableCoreInfoDao;

    @Autowired
    private CoreCoreInfoDao coreCoreInfoDao;

    /**
     * 根据成端信息更新光缆段使用状态
     *
     * @param insertPortCableCoreInfoReqList 成端信息列表
     * @return void
     */
    public void updateOpticCableSectionStateByPortCableCore(List<InsertPortCableCoreInfoReq> insertPortCableCoreInfoReqList, List<UpdatePortCableCoreInfoReq> updatePortCableCoreInfoReqList) {
        Set<String> portCableSectionIdList = new HashSet<>();
        //获取所有光缆段id
        for (InsertPortCableCoreInfoReq insertPortCableCoreInfoReq : insertPortCableCoreInfoReqList) {
            portCableSectionIdList.add(insertPortCableCoreInfoReq.getOppositeResource());
        }
        for (UpdatePortCableCoreInfoReq updatePortCableCoreInfoReq : updatePortCableCoreInfoReqList) {
            portCableSectionIdList.add(updatePortCableCoreInfoReq.getOppositeResource());
        }
        //更新光缆段使用状态
        this.updateOpticCableSectionState(portCableSectionIdList);
    }


    /**
     * app根据成端信息更新光缆段使用状态
     *
     * @param batchOperationPortCableCoreInfoReqForApp app成端信息列表
     * @return void
     */
    public void updateOpticCableSectionStateByPortCableCoreForApp(BatchOperationPortCableCoreInfoReqForApp batchOperationPortCableCoreInfoReqForApp) {
        Set<String> portCableSectionIdList = new HashSet<>();
        //获取所有光缆段id
        for (OperationPortCableCoreInfoReqForApp operationPortCableCoreInfoReqForApp : batchOperationPortCableCoreInfoReqForApp.getOperationPortCableCoreInfoReqForAppList()) {
            portCableSectionIdList.add(operationPortCableCoreInfoReqForApp.getOppositeResource());
        }
        //更新光缆段使用状态
        this.updateOpticCableSectionState(portCableSectionIdList);
    }


    /**
     * 根据熔纤信息更新光缆段使用状态
     *
     * @param insertCoreCoreInfoReqList 熔纤信息列表
     * @return void
     */
    public void updateOpticCableSectionStateByCoreCore(List<InsertCoreCoreInfoReq> insertCoreCoreInfoReqList) {
        Set<String> portCableSectionIdList = new HashSet<>();

        //获取所有光缆段id
        for (InsertCoreCoreInfoReq insertCoreCoreInfoReq : insertCoreCoreInfoReqList) {
            portCableSectionIdList.add(insertCoreCoreInfoReq.getOppositeResource());
            portCableSectionIdList.add(insertCoreCoreInfoReq.getResource());
        }
        //更新光缆段使用状态
        this.updateOpticCableSectionState(portCableSectionIdList);
    }

    /**
     * app根据熔纤信息更新光缆段使用状态
     *
     * @param batchOperationCoreCoreInfoReqForApp app熔纤信息列表
     * @return void
     */
    public void updateOpticCableSectionStateByCoreCoreForApp(BatchOperationCoreCoreInfoReqForApp batchOperationCoreCoreInfoReqForApp) {
        Set<String> portCableSectionIdList = new HashSet<>();
        //获取所有光缆段id
        for (OperationCoreCoreInfoReqForApp operationCoreCoreInfoReqForApp : batchOperationCoreCoreInfoReqForApp.getOperationCoreCoreInfoReqForAppList()) {
            portCableSectionIdList.add(operationCoreCoreInfoReqForApp.getOppositeResource());
            portCableSectionIdList.add(operationCoreCoreInfoReqForApp.getResource());
        }
        //更新光缆段使用状态
        this.updateOpticCableSectionState(portCableSectionIdList);
    }

    /**
     * 根据熔纤及成端信息更新光缆段使用状态
     *
     * @param portCableSectionIdList 光缆段id列表
     * @return void
     */
    public void updateOpticCableSectionState(Set<String> portCableSectionIdList) {
        //已使用光缆段id
        Set<String> usePortCableSectionIdList = new HashSet<>();
        //获取所有已使用光缆段id
        for (String portCableSectionId : portCableSectionIdList) {
            EntityWrapper<CoreCoreInfo> thisWrapper = new EntityWrapper<>();
            CoreCoreInfo coreCoreInfo = new CoreCoreInfo();
            coreCoreInfo.setResource(portCableSectionId);
            coreCoreInfo.setIsDeleted("0");
            thisWrapper.setEntity(coreCoreInfo);
            //获取所有本端熔纤信息
            List<CoreCoreInfo> thisCoreCoreInfoList = coreCoreInfoDao.selectList(thisWrapper);
            //判断是否包含该光缆段
            for (CoreCoreInfo thisCoreCoreInfo : thisCoreCoreInfoList) {
                if (portCableSectionIdList.contains(thisCoreCoreInfo.getResource())) {
                    usePortCableSectionIdList.add(thisCoreCoreInfo.getResource());
                }
                if (portCableSectionIdList.contains(thisCoreCoreInfo.getOppositeResource())) {
                    usePortCableSectionIdList.add(thisCoreCoreInfo.getOppositeResource());
                }
            }
            //获取所有对端熔纤信息
            EntityWrapper<CoreCoreInfo> oppositeWrapper = new EntityWrapper<>();
            coreCoreInfo = new CoreCoreInfo();
            coreCoreInfo.setOppositeResource(portCableSectionId);
            coreCoreInfo.setIsDeleted("0");
            oppositeWrapper.setEntity(coreCoreInfo);
            //获取所有对端熔纤信息
            List<CoreCoreInfo> oppositeCoreCoreInfoList = coreCoreInfoDao.selectList(oppositeWrapper);
            //判断是否包含该光缆段
            for (CoreCoreInfo oppositeCoreCoreInfo : oppositeCoreCoreInfoList) {
                if (portCableSectionIdList.contains(oppositeCoreCoreInfo.getResource())) {
                    usePortCableSectionIdList.add(oppositeCoreCoreInfo.getResource());
                }
                if (portCableSectionIdList.contains(oppositeCoreCoreInfo.getOppositeResource())) {
                    usePortCableSectionIdList.add(oppositeCoreCoreInfo.getOppositeResource());
                }
            }

            //获取所有成端信息
            EntityWrapper<PortCableCoreInfo> wrapper = new EntityWrapper<>();
            PortCableCoreInfo portCableCoreInfo = new PortCableCoreInfo();
            portCableCoreInfo.setOppositeResource(portCableSectionId);
            portCableCoreInfo.setIsDeleted("0");
            wrapper.setEntity(portCableCoreInfo);
            List<PortCableCoreInfo> portCableCoreInfoList = portCableCoreInfoDao.selectList(wrapper);

            //判断是否包含该光缆段
            for (PortCableCoreInfo usePortCableCoreInfo : portCableCoreInfoList) {
                if (portCableSectionIdList.contains(usePortCableCoreInfo.getOppositeResource())) {
                    usePortCableSectionIdList.add(usePortCableCoreInfo.getOppositeResource());
                }
            }
        }

        //取出未使用id
        portCableSectionIdList.removeAll(usePortCableSectionIdList);

        //更新已使用光缆段
        if (!ObjectUtils.isEmpty(usePortCableSectionIdList)) {
            opticCableSectionInfoDao.updateOpticCableSectionStatusByIds(usePortCableSectionIdList, OpticCableSectionConstant.STATUS_USE);
        }

        //更新未使用光缆段
        if (!ObjectUtils.isEmpty(portCableSectionIdList)) {
            opticCableSectionInfoDao.updateOpticCableSectionStatusByIds(portCableSectionIdList, OpticCableSectionConstant.STATUS_UNUSED);
        }
    }


}
