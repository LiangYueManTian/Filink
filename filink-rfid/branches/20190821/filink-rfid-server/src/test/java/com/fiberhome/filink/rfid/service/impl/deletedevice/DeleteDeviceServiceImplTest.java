package com.fiberhome.filink.rfid.service.impl.deletedevice;

import com.fiberhome.filink.rfid.bean.fibercore.PortCableCoreInfo;
import com.fiberhome.filink.rfid.dao.fibercore.CoreCoreInfoDao;
import com.fiberhome.filink.rfid.dao.fibercore.JumpFiberInfoDao;
import com.fiberhome.filink.rfid.dao.fibercore.PortCableCoreInfoDao;
import com.fiberhome.filink.rfid.dao.opticcable.OpticCableSectionInfoDao;
import com.fiberhome.filink.rfid.req.fibercore.PortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.QueryJumpFiberInfoReq;
import com.fiberhome.filink.rfid.resp.fibercore.CoreCoreInfoResp;
import com.fiberhome.filink.rfid.resp.fibercore.JumpFiberInfoResp;
import com.fiberhome.filink.rfid.resp.opticcable.OpticCableSectionInfoResp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * com.fiberhome.filink.rfid.service.impl.rfid
 *
 * @author chaofanrong@wistronits.com
 * @since 2019/7/16
 */
@RunWith(MockitoJUnitRunner.class)
public class DeleteDeviceServiceImplTest {

    @InjectMocks
    private DeleteDeviceServiceImpl deleteDeviceService;

    /**
     * 熔纤信息
     * */
    @Mock
    private CoreCoreInfoDao coreCoreInfoDao;

    /**
     * 成端信息
     * */
    @Mock
    private PortCableCoreInfoDao portCableCoreInfoDao;

    /**
     * 跳接信息
     * */
    @Mock
    private JumpFiberInfoDao jumpFiberInfoDao;

    /**
     * 光缆段信息
     * */
    @Mock
    private OpticCableSectionInfoDao opticCableSectionInfoDao;


    @Test
    public void checkDevice() {
        List<String> deviceIds = new ArrayList<>();
        deviceIds.add("fdsafdasf");
        //熔纤
        List<CoreCoreInfoResp> coreCoreInfoRespList = new ArrayList<>();
        CoreCoreInfoResp coreCoreInfoResp = new CoreCoreInfoResp();
        coreCoreInfoRespList.add(coreCoreInfoResp);
        when(coreCoreInfoDao.queryCoreCoreInfoByDeviceIds(deviceIds)).thenReturn(coreCoreInfoRespList);

        //成端
        List<PortCableCoreInfo> portCableCoreInfoList = new ArrayList<>();
        PortCableCoreInfo portCableCoreInfo = new PortCableCoreInfo();
        portCableCoreInfoList.add(portCableCoreInfo);
        PortCableCoreInfoReq portCableCoreInfoReq = new PortCableCoreInfoReq();
        portCableCoreInfoReq.setDeviceIds(deviceIds);
        when(portCableCoreInfoDao.getPortCableCoreInfo(portCableCoreInfoReq)).thenReturn(portCableCoreInfoList);

        //跳接
        QueryJumpFiberInfoReq queryJumpFiberInfoReq = new QueryJumpFiberInfoReq();
        queryJumpFiberInfoReq.setDeviceIds(deviceIds);
        List<JumpFiberInfoResp> jumpFiberInfoRespList = new ArrayList<>();
        JumpFiberInfoResp jumpFiberInfoResp = new JumpFiberInfoResp();
        jumpFiberInfoRespList.add(jumpFiberInfoResp);
        when(jumpFiberInfoDao.queryJumpFiberInfoByPortInfo(queryJumpFiberInfoReq)).thenReturn(jumpFiberInfoRespList);

        //光缆段
        List<OpticCableSectionInfoResp> opticCableSectionInfoRespList = new ArrayList<>();
        OpticCableSectionInfoResp opticCableSectionInfoResp = new OpticCableSectionInfoResp();
        opticCableSectionInfoRespList.add(opticCableSectionInfoResp);
        when(opticCableSectionInfoDao.opticCableSectionByDevice(deviceIds)).thenReturn(opticCableSectionInfoRespList);

        Assert.assertEquals(true, deleteDeviceService.checkDevice(deviceIds));
    }
}