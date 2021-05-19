package com.fiberhome.filink.lockserver.bean;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * 门锁状态 实体类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/4
 */
@Data
public class LockStatusForPda {
    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 设施的门锁信息
     */
    private List<LockForPda> lockList;
}
