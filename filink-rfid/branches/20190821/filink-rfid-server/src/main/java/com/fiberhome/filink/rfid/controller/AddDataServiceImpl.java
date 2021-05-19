package com.fiberhome.filink.rfid.controller;

import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.rfid.dao.fibercore.CoreCoreInfoDao;
import com.fiberhome.filink.rfid.dao.fibercore.JumpFiberInfoDao;
import com.fiberhome.filink.rfid.dao.fibercore.PortCableCoreInfoDao;
import com.fiberhome.filink.rfid.req.fibercore.InsertCoreCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.InsertPortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationJumpFiberInfoReqForApp;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;

/**
 * AddDataServiceImpl
 * todo: 测试代码 造千万脏数据
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/10
 */
@Service
public class AddDataServiceImpl implements AddDataService {


    @Autowired
    private PortCableCoreInfoDao portCableCoreInfoDao;
    @Autowired
    private JumpFiberInfoDao JumpFiberInfoDao;
    @Autowired
    private CoreCoreInfoDao coreCoreInfoDao;

    /**
     * 添加成端 脏数据
     */
    @Override
    public void addData() {
        List<Integer> list = Lists.newArrayList();
        for (int i = 0; i < 8; i++) {
            list.add(i);
        }
        list.parallelStream().forEach(obj -> {
            addDataList();
        });
    }

    private void addDataList() {
        for (int j = 0; j < 1000; j++) {
            List<InsertPortCableCoreInfoReq> insertPortCableCoreInfoReqList = new ArrayList<>();
            String deviceId = NineteenUUIDUtils.uuid();
            String OppositeResource = NineteenUUIDUtils.uuid();
            for (int i = 0; i < 1000; i++) {
                Random random = new Random();
                InsertPortCableCoreInfoReq req = new InsertPortCableCoreInfoReq();
                req.setPortCoreId(NineteenUUIDUtils.uuid());
                req.setResourceDeviceId(deviceId);
                req.setResourceBoxSide(0);
                req.setResourceFrameNo(((Integer) (random.nextInt(8))).toString());
                req.setResourceDiscSide(0);
                req.setResourceDiscNo(((Integer) (random.nextInt(8))).toString());
                req.setPortId(NineteenUUIDUtils.uuid());
                req.setPortNo(((Integer) (random.nextInt(16))).toString());
                req.setOppositeResource(OppositeResource);
                req.setOppositeCableCoreNo(((Integer) i).toString());
                req.setRemark("remark" + i * j);
                req.setIsDeleted("0");
                insertPortCableCoreInfoReqList.add(req);
            }
            portCableCoreInfoDao.savePortCableCoreInfo(insertPortCableCoreInfoReqList);
        }
    }

    /**
     * 添加跳接 脏数据
     */
    @Override
    public void addDataByJump() {
        List<Integer> list = Lists.newArrayList();
        for (int i = 0; i < 9; i++) {
            list.add(i);
        }
        list.parallelStream().forEach(obj -> {
            addDataByJumpList();
        });
    }


    private void addDataByJumpList() {
        for (int j = 0; j < 1000; j++) {
            List<OperationJumpFiberInfoReqForApp> operationJumpFiberInfoReqForApps = new ArrayList<>();
            String deviceId = NineteenUUIDUtils.uuid();
            String OppositeDeviceId = NineteenUUIDUtils.uuid();
            for (int i = 0; i < 1000; i++) {
                Random random = new Random();
                OperationJumpFiberInfoReqForApp req = new OperationJumpFiberInfoReqForApp();
                req.setJumpFiberId(NineteenUUIDUtils.uuid());
                req.setOppositeDeviceId(OppositeDeviceId);
                req.setDeviceId(deviceId);
                req.setBoxSide(0);
                req.setFrameNo(((Integer) (random.nextInt(8))).toString());
                req.setDiscSide(0);
                req.setDiscNo(((Integer) (random.nextInt(8))).toString());
                req.setPortNo(((Integer) i).toString());
                req.setOppositeDeviceId(NineteenUUIDUtils.uuid());
                req.setOppositeBoxSide(0);
                req.setOppositeFrameNo(((Integer) (random.nextInt(8))).toString());
                req.setOppositeDiscSide(0);
                req.setOppositeDiscNo(((Integer) (random.nextInt(8))).toString());
                req.setOppositePortNo(((Integer) i).toString());
                req.setAdapterType(((random.nextInt(2))));
                req.setRemark("remark" + i);
                req.setOppositeRemark("oppositeRemark" + i);
                req.setRfidCode(NineteenUUIDUtils.uuid());
                req.setOppositeRfidCode(NineteenUUIDUtils.uuid());
                req.setInnerDevice("1");
                req.setBranchingUnit("0");
                req.setIsDeleted("0");
                operationJumpFiberInfoReqForApps.add(req);
            }
            JumpFiberInfoDao.addJumpFiberInfo(operationJumpFiberInfoReqForApps);
        }
    }


    /**
     * 添加熔纤
     */
    @Override
    public void addDataByCore() {
        List<Integer> list = Lists.newArrayList();
        for (int i = 0; i < 9; i++) {
            list.add(i);
        }
        list.parallelStream().forEach(obj -> {
            addDataByCoreList();
        });

    }


    private void addDataByCoreList() {
        for (int j = 0; j < 1000; j++) {
            List<InsertCoreCoreInfoReq> insertCoreCoreInfoReqs = new ArrayList<>();
            String deviceId = NineteenUUIDUtils.uuid();
            String resource = NineteenUUIDUtils.uuid();
            String oppositeDeviceId = NineteenUUIDUtils.uuid();
            for (int i = 0; i < 1000; i++) {
                Random random = new Random();
                InsertCoreCoreInfoReq req = new InsertCoreCoreInfoReq();
                req.setCoreCoreId(NineteenUUIDUtils.uuid());
                req.setIntermediateNodeDeviceId(deviceId);
                req.setResource(resource);
                req.setCableCoreNo(((Integer) (random.nextInt(1152))).toString());
                req.setOppositeResource(oppositeDeviceId);
                req.setOppositeCableCoreNo(((Integer) (random.nextInt(1152))).toString());
                req.setRemark("remark" + i);
                req.setIsDeleted("0");
                insertCoreCoreInfoReqs.add(req);
            }
            coreCoreInfoDao.addCoreCoreInfo(insertCoreCoreInfoReqs);
        }
    }

}
