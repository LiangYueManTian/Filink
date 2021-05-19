package com.fiberhome.filink.rfid.service.impl.deletedevice;

import com.fiberhome.filink.rfid.bean.fibercore.PortCableCoreInfo;
import com.fiberhome.filink.rfid.constant.AppConstant;
import com.fiberhome.filink.rfid.dao.fibercore.CoreCoreInfoDao;
import com.fiberhome.filink.rfid.dao.fibercore.JumpFiberInfoDao;
import com.fiberhome.filink.rfid.dao.fibercore.PortCableCoreInfoDao;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableSectionInfoDao;
import com.fiberhome.filink.rfid.req.fibercore.PortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.QueryJumpFiberInfoReq;
import com.fiberhome.filink.rfid.resp.fibercore.CoreCoreInfoResp;
import com.fiberhome.filink.rfid.resp.fibercore.JumpFiberInfoResp;
import com.fiberhome.filink.rfid.resp.opticcable.OpticCableSectionInfoResp;
import com.fiberhome.filink.rfid.service.deletedevice.DeleteDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 删除设施 服务类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-06-17
 */
@Service
@Slf4j
public class DeleteDeviceServiceImpl implements DeleteDeviceService {

    /**
     * 熔纤信息
     */
    @Autowired
    private CoreCoreInfoDao coreCoreInfoDao;

    /**
     * 成端信息
     */
    @Autowired
    private PortCableCoreInfoDao portCableCoreInfoDao;

    /**
     * 跳接信息
     */
    @Autowired
    private JumpFiberInfoDao jumpFiberInfoDao;

    /**
     * 光缆段信息
     */
    @Autowired
    private OpticCableSectionInfoDao opticCableSectionInfoDao;


    /**
     * 校验设施能否删除
     *
     * @param deviceIds 设施id列表
     * @return Result
     */
    @Override
    public Boolean checkDevice(List<String> deviceIds) {
        //熔纤
        List<CoreCoreInfoResp> coreCoreInfoRespList = coreCoreInfoDao.queryCoreCoreInfoByDeviceIds(deviceIds);
        //成端
        PortCableCoreInfoReq portCableCoreInfoReq = new PortCableCoreInfoReq();
        portCableCoreInfoReq.setDeviceIds(deviceIds);
        List<PortCableCoreInfo> portCableCoreInfoList = portCableCoreInfoDao.getPortCableCoreInfo(portCableCoreInfoReq);
        //跳接
        QueryJumpFiberInfoReq queryJumpFiberInfoReq = new QueryJumpFiberInfoReq();
        queryJumpFiberInfoReq.setDeviceIds(deviceIds);
        List<JumpFiberInfoResp> jumpFiberInfoRespList = jumpFiberInfoDao.queryJumpFiberInfoByPortInfo(queryJumpFiberInfoReq);
        //光缆段
        List<OpticCableSectionInfoResp> opticCableSectionInfoRespList = opticCableSectionInfoDao.opticCableSectionByDevice(deviceIds);

        //只要有业务数据，不让删除设施
        if (!ObjectUtils.isEmpty(coreCoreInfoRespList) || !ObjectUtils.isEmpty(portCableCoreInfoList) || !ObjectUtils.isEmpty(jumpFiberInfoRespList) || !ObjectUtils.isEmpty(opticCableSectionInfoRespList)) {
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Boolean> checkDevices(List<String> deviceIds) {
        Map<String, Boolean> map = new HashMap<>();
        //熔纤
        List<CoreCoreInfoResp> coreCoreInfoRespList = coreCoreInfoDao.queryCoreCoreInfoByDeviceIds(deviceIds);
        //成端
        PortCableCoreInfoReq portCableCoreInfoReq = new PortCableCoreInfoReq();
        portCableCoreInfoReq.setDeviceIds(deviceIds);
        List<PortCableCoreInfo> portCableCoreInfoList = portCableCoreInfoDao.getPortCableCoreInfo(portCableCoreInfoReq);
        //跳接
        QueryJumpFiberInfoReq queryJumpFiberInfoReq = new QueryJumpFiberInfoReq();
        queryJumpFiberInfoReq.setDeviceIds(deviceIds);
        List<JumpFiberInfoResp> jumpFiberInfoRespList = jumpFiberInfoDao.queryJumpFiberInfoByPortInfo(queryJumpFiberInfoReq);
        //光缆段
        List<OpticCableSectionInfoResp> opticCableSectionInfoRespList = opticCableSectionInfoDao.opticCableSectionByDevice(deviceIds);

        if (ObjectUtils.isEmpty(coreCoreInfoRespList)) {
            map.put(AppConstant.RFID_CORE_KEY, false);
        } else {
            map.put(AppConstant.RFID_CORE_KEY, true);
        }

        if (ObjectUtils.isEmpty(portCableCoreInfoList)) {
            map.put(AppConstant.RFID_PORT_CABLE_KEY, false);
        } else {
            map.put(AppConstant.RFID_PORT_CABLE_KEY, true);
        }

        if (ObjectUtils.isEmpty(jumpFiberInfoRespList)) {
            map.put(AppConstant.RFID_JUMP_KEY, false);
        } else {
            map.put(AppConstant.RFID_JUMP_KEY, true);
        }


        if (ObjectUtils.isEmpty(opticCableSectionInfoRespList)) {
            map.put(AppConstant.RFID_OPTIC_KEY, false);
        } else {
            map.put(AppConstant.RFID_OPTIC_KEY, true);
        }

        return map;
    }
}
