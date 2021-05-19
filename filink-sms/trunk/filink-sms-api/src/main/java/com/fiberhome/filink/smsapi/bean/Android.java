package com.fiberhome.filink.smsapi.bean;

import lombok.Data;

/**
 * 安卓相关属性
 *
 * @author yuanyao@wistronits.com
 * create on 2019-03-27 17:50
 */
@Data
public class Android {

    /**
     * 通知的提醒方式 "VIBRATE" : 震动 "SOUND" : 声音 "BOTH" : 声音和震动 NONE : 静音
     */
    private String notifyType = "NONE";

    /**
     * 通知栏自定义样式0-100
     */
    private Integer notificationBarType = 1;

    /**
     * 通知栏自定义样式0-100
     */
    private Integer notificationBarPriority = 1;


    /**
     * 点击通知后动作 "APPLICATION" : 打开应用 "ACTIVITY" : 打开AndroidActivity "URL" : 打开URL "NONE" : 无跳转
     */
    private String openType = "NONE";

    /**
     * Android收到推送后打开对应的url,仅当AndroidOpenType="URL"有效
     */
    private String openUrl = "http://www.aliyun.com";

    /**
     * 设定通知打开的activity，仅当AndroidOpenType="Activity"有效
     */
    private String activity = "http://www.aliyun.com";

    /**
     * Android通知音乐
     */
    private String music = "default";

    /**
     * 设置该参数后启动小米托管弹窗功能,
     * 此处指定通知点击后跳转的Activity（托管弹窗的前提条件：1. 集成小米辅助通道；2. StoreOffline参数设为true）
     */
    private String xiaoMiActivity = "com.ali.demo.MiActivity";

    /**
     * Mi title
     */
    private String xiaoMiNotifyTitle = "Mi title";

    /**
     * XiaoMiNotifyBody
     */
    private String xiaoMiNotifyBody = "MiActivity Body";

    /**
     * 设定通知的扩展属性。(注意 : 该参数要以 json map 的格式传入,否则会解析出错)
     */
    private String extParameters = "{\"k1\":\"android\",\"k2\":\"v2\"}";




}
