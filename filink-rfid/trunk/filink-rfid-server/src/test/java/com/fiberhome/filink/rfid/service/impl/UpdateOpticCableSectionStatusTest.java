package com.fiberhome.filink.rfid.service.impl;

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
import com.google.common.collect.Lists;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author liyj
 * @date 2019/7/25
 */
@RunWith(JMockit.class)
public class UpdateOpticCableSectionStatusTest {
    @Tested
    private UpdateOpticCableSectionStatus UpdateOpticCableSectionStatus;

    @Injectable
    private OpticCableSectionInfoDao opticCableSectionInfoDao;

    @Injectable
    private PortCableCoreInfoDao portCableCoreInfoDao;

    @Injectable
    private CoreCoreInfoDao coreCoreInfoDao;

    private List<InsertPortCableCoreInfoReq> insertPortCableCoreInfoReqList;
    private List<UpdatePortCableCoreInfoReq> updatePortCableCoreInfoReqList;
    private InsertPortCableCoreInfoReq infoReq;
    private UpdatePortCableCoreInfoReq updatePortCableCoreInfoReq;

    /**
     * 初始化数据
     *
     * @throws Exception exception
     */
    @Before
    public void setUp() throws Exception {

        insertPortCableCoreInfoReqList = Lists.newArrayList();
        infoReq = new InsertPortCableCoreInfoReq();
        infoReq.setOppositeResource("1");
        infoReq.setOppositeCableCoreNo("1");
        insertPortCableCoreInfoReqList.add(infoReq);

        updatePortCableCoreInfoReqList = Lists.newArrayList();
        updatePortCableCoreInfoReq = new UpdatePortCableCoreInfoReq();
        updatePortCableCoreInfoReq.setOppositeResource("1");
        updatePortCableCoreInfoReq.setOppositeCableCoreNo("1");
        updatePortCableCoreInfoReqList.add(updatePortCableCoreInfoReq);
    }


    @Test
    public void updateOpticCableSectionStateByPortCableCore() throws Exception {
        UpdateOpticCableSectionStatus.updateOpticCableSectionStateByPortCableCore(insertPortCableCoreInfoReqList, updatePortCableCoreInfoReqList);
    }

    @Test
    public void updateOpticCableSectionStateByPortCableCoreForApp() throws Exception {
        BatchOperationPortCableCoreInfoReqForApp obj = new BatchOperationPortCableCoreInfoReqForApp();
        List<OperationPortCableCoreInfoReqForApp> list = Lists.newArrayList();
        OperationPortCableCoreInfoReqForApp app = new OperationPortCableCoreInfoReqForApp();
        app.setOppositeResource("deviceId");
        list.add(app);
        obj.setOperationPortCableCoreInfoReqForAppList(list);
        UpdateOpticCableSectionStatus.updateOpticCableSectionStateByPortCableCoreForApp(obj);
    }

    @Test
    public void updateOpticCableSectionStateByCoreCore() throws Exception {
        List<InsertCoreCoreInfoReq> insertCoreCoreInfoReqList = Lists.newArrayList();
        InsertCoreCoreInfoReq req = new InsertCoreCoreInfoReq();
        req.setOppositeResource("deviceId");
        req.setResource("resource");
        insertCoreCoreInfoReqList.add(req);
        UpdateOpticCableSectionStatus.updateOpticCableSectionStateByCoreCore(insertCoreCoreInfoReqList);
    }


    @Test
    public void updateOpticCableSectionStateByCoreCoreForApp() throws Exception {
        BatchOperationCoreCoreInfoReqForApp obj = new BatchOperationCoreCoreInfoReqForApp();
        List<OperationCoreCoreInfoReqForApp> list = Lists.newArrayList();
        OperationCoreCoreInfoReqForApp app = new OperationCoreCoreInfoReqForApp();
        app.setOppositeResource("resource");
        app.setResource("resource");
        list.add(app);
        obj.setOperationCoreCoreInfoReqForAppList(list);
        UpdateOpticCableSectionStatus.updateOpticCableSectionStateByCoreCoreForApp(obj);
    }

    @Test
    public void updateOpticCableSectionState() throws Exception {
        Set<String> portCableSectionIdList = new HashSet<>();
        portCableSectionIdList.add("1");
        UpdateOpticCableSectionStatus.updateOpticCableSectionState(portCableSectionIdList);

    }

}