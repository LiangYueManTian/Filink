package com.fiberhome.filink.commonstation.entity.config;

import lombok.Data;

/**
 * 开锁结果推送
 * @author CongcaiYu
 */
@Data
public class UnlockPushBean {

    /**
     * 标题
     */
    private String title;
    /**
     * 用户token
     */
    private String token;
    /**
     * appKey
     */
    private Long appKey;
    /**
     * 手机id
     */
    private String phoneId;
    /**
     * 推送消息
     */
    private String msg;
    /**
     * 推送keyId
     */
    private String accessKeyId;
    /**
     * 推送密钥
     */
    private String accessKeySecret;
}
