package com.fiberhome.filink.fdevice.dto;

import lombok.Data;

/**
 * <p>
 * 设施收藏 PDA  pojo
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/3
 */
@Data
public class DeviceCollectingForPda {
    /**
     * 设施id
     */
    private String deviceId;
    /**
     * 操作状态  0 – 收藏  1 – 取消收藏
     */
    private String optState;
}
