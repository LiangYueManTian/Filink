package com.fiberhome.filink.lockserver.controller;

import com.fiberhome.filink.lockserver.bean.Lock;
import com.fiberhome.filink.lockserver.bean.OpenLockBean;
import com.fiberhome.filink.lockserver.service.LockService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * LockController测试类
 * @author CongcaiYu
 */
@RunWith(MockitoJUnitRunner.class)
public class LockControllerTest {

    private Lock lock;

    @InjectMocks
    private LockController lockController;

    @Mock
    private LockService lockService;

    @Before
    public void setUp() {
        lock = new Lock();
        lock.setDeviceId("3dffc3413a3f11e9b3520242ac110003");
        lock.setDoorName("door-001");
        lock.setDoorStatus("1");
        lock.setLockNum("1");
        lock.setLockStatus("1");
    }

    @Test
    public void saveLock() {
        lockController.saveLock(lock);
    }

    @Test
    public void batchUpdateLockStatus() {
        List<Lock> locks = new ArrayList<>();
        locks.add(lock);
        lockController.batchUpdateLockStatus(locks);
    }

    @Test
    public void getLockByDeviceId() {
        lockController.getLockByDeviceId("3dffc3413a3f11e9b3520242ac110003");
    }

    @Test
    public void openLock() {
        //参数为空场景
        OpenLockBean openLockBeanNull = new OpenLockBean();
        try {
            lockController.openLock(openLockBeanNull);
        }catch (Exception e){
            e.printStackTrace();
        }

        //构造开锁参数
        OpenLockBean openLockBean = new OpenLockBean();
        openLockBean.setDeviceId("3dffc3413a3f11e9b3520242ac110003");
        List<String> slotNumList = new ArrayList<>();
        slotNumList.add("1");
        slotNumList.add("2");
        openLockBean.setSlotNumList(slotNumList);
        lockController.openLock(openLockBean);
    }
}