package com.fiberhome.filink.stationserver.util;

import com.fiberhome.filink.redis.RedisUtils;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * stationUtil测试类
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class StationUtilTest {

    private StationUtil stationUtil;

    @Mocked
    private RedisUtils redisUtils;

    @Test
    public void clearRedisSerialNum() {
        //正常场景
        String equipmentId = "equipmentId";
        StationUtil.clearRedisSerialNum(equipmentId);
    }

    @Test
    public void deleteUpgradeDeviceCount() {
        String equipmentId = "equipmentId";
        StationUtil.deleteUpgradeDeviceCount(equipmentId);
    }

    @Test
    public void deletePicMap() {
        String equipmentId = "equipmentId";
        StationUtil.deletePicMap(equipmentId);
    }

    @Test
    public void clearDeployCmd() {
        String equipmentId = "equipmentId";
        StationUtil.clearDeployCmd(equipmentId);
    }

    @Test
    public void deleteControlInfo() {
        String equipmentId = "equipmentId";
        StationUtil.deleteControlInfo(equipmentId);
    }
}