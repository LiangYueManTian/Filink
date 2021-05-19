package com.fiberhome.filink.smsapi.bean;

import lombok.Data;

/**
 * ios 相关参数
 *
 * @author yuanyao@wistronits.com
 * create on 2019-03-27 17:36
 */
@Data
public class Ios {

    /**
     * iOS应用图标右上角角标
     */
    private Integer badge = 5;

    /**
     * 开启静默通知
     */
    private Boolean silentNotification = false;

    /**
     * iOS通知声音
     */
    private String music = "default";

    /**
     * iOS10通知副标题的内容
     */
    private String subtitle = "subtitle";

    /**
     * 指定iOS10通知Category
     */
    private String notificationCategory = "iOS10 Notification Category";

    /**
     * 是否允许扩展iOS通知内容
     */
    private Boolean mutableContent = true;

    /**
     * iOS的通知是通过APNs中心来发送的，需要填写对应的环境信息。"DEV" : 表示开发环境 "PRODUCT" : 表示生产环境
     */

    private String apnsEnv = "DEV";

    /**
     * 消息推送时设备不在线（既与移动推送的服务端的长连接通道不通）
     * ，则这条推送会做为通知，通过苹果的APNs通道送达一次。注意：离线消息转通知仅适用于生产环境
     */
    private Boolean remind = true;

    /**
     * iOS消息转通知时使用的iOS通知内容，仅当iOSApnsEnv=PRODUCT && iOSRemind为true时有效
     */
    private String remindBody = "iOSRemindBody";

    /**
     *通知的扩展属性(注意 : 该参数要以json map的格式传入,否则会解析出错)
     */
    private String extParameters = "{\"_ENV_\":\"DEV\",\"k2\":\"v2\"}";


}
