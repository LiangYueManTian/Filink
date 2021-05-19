package com.fiberhome.filink.stationserver.stream;


import com.fiberhome.filink.filinklockapi.bean.ControlDel;
import com.fiberhome.filink.filinklockapi.constant.OperateState;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.constant.RedisKey;
import com.fiberhome.filink.stationserver.util.StationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 清除redis的主控信息
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class FiLinkClearRedisControlAsync {


    /**
     * 清除redis主控信息
     *
     * @param controlDel 删除主控信息
     */
    @Async
    public void clearRedisControl(ControlDel controlDel) {
        String operate = controlDel.getOperate();
        List<String> hostIdList = controlDel.getHostIdList();
        //更新
        if(OperateState.UPDATE_STATE.equals(operate)){
            update(hostIdList);
        }else {
            //删除
            delete(hostIdList);
        }

    }

    /**
     * 更新操作
     * @param hostIdList 主控id集合
     */
    private void update(List<String> hostIdList){
        for (String equipmentId : hostIdList) {
            RedisUtils.hRemove(RedisKey.CONTROL_INFO, equipmentId);
        }
    }

    /**
     * 删除操作
     * @param hostIdList 主控id集合
     */
    private void delete(List<String> hostIdList){
        for (String equipmentId : hostIdList) {
            //删除缓存中升级、图片、离线信息
            StationUtil.deleteControlInfo(equipmentId);
        }
    }
}
