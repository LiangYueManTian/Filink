package com.fiberhome.filink.onenetserver.stream;


import com.fiberhome.filink.onenetserver.constant.RedisKey;
import com.fiberhome.filink.redis.RedisUtils;
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
     * @param equipmentIdList 主控id集合
     */
    @Async
    public void clearRedisControl(List<String> equipmentIdList) {
        for (String equipmentId : equipmentIdList) {
            RedisUtils.hRemove(RedisKey.CONTROL_INFO, equipmentId);
        }
    }
}
