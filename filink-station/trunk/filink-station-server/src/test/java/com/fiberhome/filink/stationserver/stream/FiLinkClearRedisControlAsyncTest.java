package com.fiberhome.filink.stationserver.stream;

import com.fiberhome.filink.filinklockapi.bean.ControlDel;
import com.fiberhome.filink.filinklockapi.constant.OperateState;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.constant.RedisKey;
import com.fiberhome.filink.stationserver.util.StationUtil;
import mockit.Expectations;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * FiLinkClearRedisControlAsync测试类
 *
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class FiLinkClearRedisControlAsyncTest {

    @Tested
    private FiLinkClearRedisControlAsync fiLinkClearRedisControlAsync;

    private String equipmentId;

    @Before
    public void setUp() {
        equipmentId = "0101CFED4C0400000000";
    }

    @Test
    public void clearRedisControl() {
        //更新操作
        ControlDel update = new ControlDel();
        List<String> updateIdList = new ArrayList<>();
        updateIdList.add(equipmentId);
        update.setOperate(OperateState.UPDATE_STATE);
        update.setHostIdList(updateIdList);
        new Expectations(RedisUtils.class) {
            {
                RedisUtils.hRemove(RedisKey.CONTROL_INFO, equipmentId);
            }
        };
        fiLinkClearRedisControlAsync.clearRedisControl(update);
        //删除操作
        ControlDel delete = new ControlDel();
        List<String> deleteIdList = new ArrayList<>();
        deleteIdList.add(equipmentId);
        delete.setOperate(OperateState.DELETE_STATE);
        delete.setHostIdList(deleteIdList);
        new Expectations(StationUtil.class) {
            {
                StationUtil.deleteControlInfo(equipmentId);
            }
        };
        fiLinkClearRedisControlAsync.clearRedisControl(delete);
    }
}