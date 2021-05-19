package com.fiberhome.filink.lockserver.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * <p>
 * 设施门锁信息 for pda
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/10
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class DoorForPda {
    /**
     * 门编号
     */
    private String doorNum;
    /**
     * 门名称
     */
    private String doorName;
    /**
     * 二维码
     */
    private String qrcode;
    /**
     * 锁具地址/锁编号
     */
    private String lockNum;
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
     * 授权状态 0 – 未授权 1 – 已授权
     */
    private String state;
}
