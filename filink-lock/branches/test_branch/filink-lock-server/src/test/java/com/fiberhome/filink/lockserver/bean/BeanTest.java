package com.fiberhome.filink.lockserver.bean;

import com.fiberhome.filink.lockserver.exception.FiLinkControlException;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * <p>
 *  实体类覆盖测试类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/7/1
 */
@RunWith(JMockit.class)
public class BeanTest {

    /**
     * ControlParam 测试
     */
    @Test
    public void testControlParam(){
        ControlParam controlParam = new ControlParam();

        try {
            controlParam.checkUpdateParams();
        }catch (Exception e){
            Assert.assertTrue(e.getClass() ==FiLinkControlException.class);
        }
        controlParam.setHostId("hostId");
        try {
            controlParam.checkParamsForUpdateDeployStatus();
        }catch (Exception e){
            Assert.assertTrue(e.getClass() ==FiLinkControlException.class);
        }
        try {
            controlParam.checkParamsForUpdateDeviceStatus();
        }catch (Exception e){
            Assert.assertTrue(e.getClass() ==FiLinkControlException.class);
        }

        controlParam.setDeployStatus("1");
        controlParam.setDeviceStatus("3");
        controlParam.checkParamsForUpdateDeployStatus();
        controlParam.checkParamsForUpdateDeviceStatus();
        controlParam.checkUpdateParams();

        controlParam.setConfigValue("configValue");
        controlParam.getConfigValue();

        controlParam.setUpdateTime(new Timestamp(System.nanoTime()));
        controlParam.getUpdateTime();

        controlParam.setSyncStatus("1");
        controlParam.getSyncStatus();

    }


    /**
     * ControlParamForControl 测试
     */
    @Test
    public void testControlParamForControl(){
        ControlParamForControl controlParamForControl = new ControlParamForControl();
        controlParamForControl.setActualValue("actualValue");
        controlParamForControl.getActualValue();
        controlParamForControl.setLockList(new ArrayList<>());
        controlParamForControl.getLockList();
        controlParamForControl.setActiveStatus("1");
        controlParamForControl.getActiveStatus();
        controlParamForControl.setCloudPlatform("1");
        controlParamForControl.getCloudPlatform();
        controlParamForControl.setDeployStatus("1");
        controlParamForControl.getDeployStatus();
        controlParamForControl.setDeviceStatus("4");
        controlParamForControl.getDeviceStatus();
        controlParamForControl.setDoors("3");
        controlParamForControl.getDoors();
        controlParamForControl.setHostIp("ip");
        controlParamForControl.getHostIp();
        controlParamForControl.setHostPort("8080");
        controlParamForControl.getHostPort();
        controlParamForControl.setHostType("1");
        controlParamForControl.getHostType();
        controlParamForControl.setHostId("id");
        controlParamForControl.getHostId();
        controlParamForControl.setHostName("name");
        controlParamForControl.getHostName();

        controlParamForControl.getSimCardType();
        controlParamForControl.getSolarCell();
        controlParamForControl.getSimCardType();
        controlParamForControl.getSoftwareVersion();
        controlParamForControl.getHardwareVersion();
        controlParamForControl.getVersionUpdateTime();
        controlParamForControl.getSourceType();
        controlParamForControl.getMacAddr();
        controlParamForControl.getPlatformId();
        controlParamForControl.getImei();
        controlParamForControl.getImsi();
        controlParamForControl.getPlatformId();
        controlParamForControl.getProductId();

    }


    /**
     * DeviceInfoForLock 测试
     */
    @Test
    public void testDeviceInfoForLock(){
        DeviceInfoForLock deviceInfoForLock = new DeviceInfoForLock();
        deviceInfoForLock.setDeviceId("a");
        deviceInfoForLock.getDeviceId();
        deviceInfoForLock.setDeviceName("b");
        deviceInfoForLock.getDeviceName();
        deviceInfoForLock.setDeviceType("1");
        deviceInfoForLock.getDeviceType();
        deviceInfoForLock.setAddress("BJ");
        deviceInfoForLock.getAddress();
        deviceInfoForLock.setControlList(new ArrayList<>());
        deviceInfoForLock.getControlList();
    }

    /**
     * LockForPda 测试
     */
    @Test
    public void testLockForPda(){
        LockForPda lockForPda = new LockForPda();
        lockForPda.setDoorName("name");
        lockForPda.getDoorName();
        lockForPda.setDoorStatus("2");
        lockForPda.getDoorStatus();
        lockForPda.setLockStatus("2");
        lockForPda.getLockStatus();
    }

}
