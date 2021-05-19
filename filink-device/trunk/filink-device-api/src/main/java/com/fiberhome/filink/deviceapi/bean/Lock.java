package com.fiberhome.filink.deviceapi.bean;


import lombok.Data;

/**
 * 电子锁实体
 * @Author: zhaoliang
 * @Date: 2019/6/13 9:39
 * @Description: com.fiberhome.filink.deviceapi.bean
 * @version: 1.0
 */
@Data
public class Lock {
    /**
     * 电子锁id
     */
    private String lockId;
    /**
     * 设施id
     */
    private String deviceId;
    /**
     * 电子锁类型
     */
    private String lockType;
    /**
     * 电子锁名称
     */
    private String lockName;
    /**
     * 锁具编号
     */
    private String lockNum;
    /**
     * 电子锁状态
     */
    private String lockStatus;
    /**
     * 门名称
     */
    private String doorName;
    /**
     * 门状态
     */
    private String doorStatus;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 主控id
     */
    private String controlId;

    /**
     * 门编号
     */
    private String doorNum;
}
