package com.fiberhome.filink.lockserver.bean;

import com.fiberhome.filink.lockserver.exception.FiLinkLockException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 电子锁实体
 *
 * @author CongcaiYu
 */
@Data
public class Lock {
    /**
     * 电子锁id  主键
     */
    private String lockId;
    /**
     * 主控id
     */
    private String controlId;
    /**
     * 电子锁名称
     */
    private String lockName;
    /**
     * 锁具地址/锁编号
     */
    private String lockNum;
    /**
     * 电子锁状态 1-开 2-关
     */
    private String lockStatus;
    /**
     * 门名称
     */
    private String doorName;
    /**
     * 门状态 1-开 2-关
     */
    private String doorStatus;
    /**
     * 门编号
     */
    private String doorNum;
    /**
     * 二维码
     */
    private String qrCode;

    /**
     * 行程开关地址
     */
    private String switchNo;
    /**
     * 锁密钥（预留）
     */
    private String lockKey;

    /**
     * 锁唯一ID
     */
    private String lockCode;

    /**
     * 更新时间
     */
    private Long updateTime;
    /**
     * 设施id
     */
    private String deviceId;
    /**
     * 排序字段
     */
    private String rank;
    /**
     * 摄像头
     */
    private String camera;

    /**
     * 检查设施id和门编号
     */
    public void checkDeviceAndDoor() {
        if (this == null
                || StringUtils.isEmpty(this.getDeviceId())
                || StringUtils.isEmpty(this.getDoorNum())) {
            throw new FiLinkLockException("deviceId or doorNum is empty>>>>");
        }
    }

}
