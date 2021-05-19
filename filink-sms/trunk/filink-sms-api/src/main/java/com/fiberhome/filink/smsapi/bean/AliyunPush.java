package com.fiberhome.filink.smsapi.bean;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * hello  world
 *
 * @author yuanyao@wistronits.com
 * create on 2019-03-27 15:27
 */
@Data
public class AliyunPush extends AliyunMessage {

    /**
     * appKey
     */
    private Long appKey;

    /**
     * 推送目标: DEVICE:按设备推送 ALIAS : 按别名推送 ACCOUNT:按帐号推送  TAG:按标签推送; ALL: 广播推送
     */
    private String target = "ALL";

    /**
     * id集合 对应的值为 设备id1,设备id2. 多个值使用逗号分隔.(帐号与设备有一次最多100个的限制)
     */
    private String ids;

    /**
     * 消息类型 MESSAGE NOTICE
     */
    private String pushType = "NOTICE";

    /**
     * 设备类型
     * 设备类型 ANDROID iOS ALL.
     */
    private String deviceType = "ALL";

    /**
     * 消息的标题
     */
    @NotBlank
    private String title;

    /**
     * 消息的内容
     */
    @NotBlank
    private String body;

    /**
     * ios相关配置
     */
    private Ios ios;

    /**
     * Android相关配置
     */
    private Android android;


}
