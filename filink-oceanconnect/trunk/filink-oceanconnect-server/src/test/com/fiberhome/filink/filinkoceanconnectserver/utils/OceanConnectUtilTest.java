package com.fiberhome.filink.filinkoceanconnectserver.utils;

import com.fiberhome.filink.redis.RedisUtils;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * oceancConnectUtil测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class OceanConnectUtilTest {


    @Mocked
    private RedisUtils redisUtils;

    @Test
    public void clearRedisSerialNum() {
        //正常场景
        String equipmentId = "equipmentId";
        OceanConnectUtil.clearRedisSerialNum(equipmentId);
    }

    @Test
    public void deleteUpgradeDeviceCount() {
        String equipmentId = "equipmentId";
        OceanConnectUtil.deleteUpgradeDeviceCount(equipmentId);
    }

    @Test
    public void deletePicMap() {
        String equipmentId = "equipmentId";
        OceanConnectUtil.deletePicMap(equipmentId);
    }

    @Test
    public void clearDeployCmd() {
        String equipmentId = "equipmentId";
        OceanConnectUtil.clearDeployCmd(equipmentId);
    }

    @Test
    public void deleteControlInfo() {
        String equipmentId = "equipmentId";
        OceanConnectUtil.deleteControlInfo(equipmentId);
    }
}