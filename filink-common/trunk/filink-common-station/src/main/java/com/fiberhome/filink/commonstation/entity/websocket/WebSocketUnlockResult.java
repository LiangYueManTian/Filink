package com.fiberhome.filink.commonstation.entity.websocket;

import lombok.Data;

/**
 * <p>
 *   station,oceanConnect,oneNet开锁结果消息推送实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-06-26
 */
@Data
public class WebSocketUnlockResult {
    /**
     * 锁编号
     */
    private String doorNum;
    /**
     * 开锁结果
     */
    private String unlockResult;
}
