package com.fiberhome.filink.rfid.service.impl;

import com.fiberhome.filink.rfid.bean.fibercore.JumpFiberInfo;
import com.fiberhome.filink.rfid.dao.fibercore.JumpFiberInfoDao;
import com.fiberhome.filink.rfid.req.fibercore.InsertPortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.UpdatePortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationJumpFiberInfoForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationJumpFiberInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.resp.fibercore.JumpFiberInfoResp;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import com.google.common.collect.Lists;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * @author liyj
 * @date 2019/7/25
 */
@RunWith(JMockit.class)
public class UpdatePortStatusAsyncTest {
    @Tested
    private UpdatePortStatusAsync status;

    /**
     * 注入模板接口
     */
    @Injectable
    TemplateService templateService;

    @Injectable
    private JumpFiberInfoDao jumpFiberInfoDao;

    private List<JumpFiberInfoResp> jumpFiberInfoList;
    private JumpFiberInfoResp infoResp = new JumpFiberInfoResp();
    private OperationJumpFiberInfoReqForApp req = new OperationJumpFiberInfoReqForApp();
    private BatchOperationPortCableCoreInfoReqForApp app = new BatchOperationPortCableCoreInfoReqForApp();
    private List<OperationPortCableCoreInfoReqForApp> appList = Lists.newArrayList();
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
        jumpFiberInfoList = Lists.newArrayList();

        infoResp.setDeviceType("060");
        infoResp.setDeviceId("id");
        infoResp.setBoxSide(0);
        infoResp.setFrameNo("1");
        infoResp.setDiscSide(0);
        infoResp.setDiscNo("1");
        infoResp.setPortNo("1");
        infoResp.setOppositeDeviceId("1");
        infoResp.setOppositeBoxSide(0);
        infoResp.setOppositeFrameNo("1");
        infoResp.setOppositeDiscSide(0);
        infoResp.setOppositeDiscNo("1");
        infoResp.setOppositePortNo("1");

        OperationPortCableCoreInfoReqForApp app1 = new OperationPortCableCoreInfoReqForApp();
        app1.setResourceBoxSide(0);
        app1.setResourceFrameNo("1");
        app1.setResourceDiscSide(0);
        app1.setResourceDiscNo("1");
        app1.setPortNo("1");
        app1.setResourceDeviceId("1");
        appList.add(app1);
        app.setOperationPortCableCoreInfoReqForAppList(appList);
        jumpFiberInfoList.add(infoResp);

        req.setDeviceId("id");
        req.setBoxSide(0);
        req.setFrameNo("1");
        req.setDiscSide(0);
        req.setDiscNo("1");
        req.setPortNo("1");
        req.setRfidCode("dede");
        req.setOppositeDeviceId("1");
        req.setOppositeBoxSide(0);
        req.setOppositeFrameNo("1");
        req.setOppositeDiscSide(0);
        req.setOppositeDiscNo("1");
        req.setOppositePortNo("1");
        insertPortCableCoreInfoReqList = Lists.newArrayList();
        infoReq = new InsertPortCableCoreInfoReq();
        infoReq.setOppositeResource("1");
        infoReq.setOppositeCableCoreNo("1");
        infoReq.setPortNo("portNo");
        insertPortCableCoreInfoReqList.add(infoReq);

        updatePortCableCoreInfoReqList = Lists.newArrayList();
        updatePortCableCoreInfoReq = new UpdatePortCableCoreInfoReq();
        updatePortCableCoreInfoReq.setOppositeResource("1");
        updatePortCableCoreInfoReq.setOppositeCableCoreNo("1");
        updatePortCableCoreInfoReqList.add(updatePortCableCoreInfoReq);
    }

    @Test
    public void updatePortBindingState() throws Exception {
        status.updatePortBindingState(jumpFiberInfoList);
    }

    @Test
    public void updatePortBindingStateForApp() throws Exception {
        BatchOperationJumpFiberInfoForApp obj = new BatchOperationJumpFiberInfoForApp();
        List<OperationJumpFiberInfoReqForApp> ob = Lists.newArrayList();
        ob.add(req);
        obj.setOperationJumpFiberInfoReqForAppList(ob);
        status.updatePortBindingStateForApp(obj);
    }

    @Test
    public void updatePortBindingStateToUpdateForApp() throws Exception {
        BatchOperationJumpFiberInfoForApp app = new BatchOperationJumpFiberInfoForApp();
        List<OperationJumpFiberInfoReqForApp> ob = Lists.newArrayList();
        ob.add(req);
        app.setOperationJumpFiberInfoReqForAppList(ob);

        List<JumpFiberInfo> jumpFiberInfoList = Lists.newArrayList();
        JumpFiberInfo jumpFiberInfo = new JumpFiberInfo();
        JumpFiberInfo jumpFiberInfo1 = new JumpFiberInfo();
        jumpFiberInfo.setDeviceId("id");
        jumpFiberInfo.setBoxSide(0);
        jumpFiberInfo.setFrameNo("1");
        jumpFiberInfo.setDiscSide(0);
        jumpFiberInfo.setDiscNo("1");
        jumpFiberInfo.setPortNo("1");
        jumpFiberInfo.setRfidCode("dede");
        jumpFiberInfo1.setOppositeRfidCode("dede1");
        jumpFiberInfo1.setOppositeDeviceId("1");
        jumpFiberInfo1.setOppositeBoxSide(0);
        jumpFiberInfo1.setOppositeFrameNo("1");
        jumpFiberInfo1.setOppositeDiscSide(0);
        jumpFiberInfo1.setOppositeDiscNo("1");
        jumpFiberInfo1.setOppositePortNo("1");
        jumpFiberInfoList.add(jumpFiberInfo);
        jumpFiberInfoList.add(jumpFiberInfo1);
        new Expectations() {
            {
                jumpFiberInfoDao.getJumpFiberInfoByRfidCode(anyString);
                result = jumpFiberInfoList;
            }
        };

        status.updatePortBindingStateToUpdateForApp(app);
    }

    @Test
    public void updatePortBindingState1() throws Exception {
        List<UpdatePortCableCoreInfoReq> up = Lists.newArrayList();
        UpdatePortCableCoreInfoReq req = new UpdatePortCableCoreInfoReq();
        req.setPortId("1");
        req.setPortStatus(0);
        up.add(req);
        List<InsertPortCableCoreInfoReq> insertPortCableCoreInfoReqList = Lists.newArrayList();
        InsertPortCableCoreInfoReq req1 = new InsertPortCableCoreInfoReq();
        req1.setPortNo("1");
        req1.setOppositeCableCoreNo("1");

        insertPortCableCoreInfoReqList.add(req1);
        status.updatePortBindingState(insertPortCableCoreInfoReqList, up);
    }

    @Test
    public void updatePortBindingStateForApp1() throws Exception {
        status.updatePortBindingStateForApp(app);
    }

    @Test
    public void batchUpdatePortCableState() throws Exception {
        status.batchUpdatePortCableState(insertPortCableCoreInfoReqList, updatePortCableCoreInfoReqList);
    }

    @Test
    public void batchUpdatePortCableStateForApp() throws Exception {
        status.batchUpdatePortCableStateForApp(app);
    }

}