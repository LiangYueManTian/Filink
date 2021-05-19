package com.fiberhome.filink.lockserver.bean;

import lombok.Data;

/**
 * <p>
 * pda锁实体
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/4
 */
@Data
public class LockForPda {
    /**
     * 门名称
     */
    private String doorName;

    /**
     * 电子锁状态 1-开 2-关
     */
    private String lockStatus;

    /**
     * 门状态 1-开 2-关
     */
    private String doorStatus;
}
